import javafx.application.*;
import javafx.beans.value.*;
import javafx.collections.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/** Example of playing all audio files in a given directory. */
public class AudioPlaylist extends Application {
  private static final String MUSIC_DIR = "C:/Users/premendra.kumar/Desktop/songs/Sampoorna Ramayan Katha";
  public static final String TAG_COLUMN_NAME = "Tag";
  public static final String VALUE_COLUMN_NAME = "Value";
  public static final List<String> SUPPORTED_FILE_EXTENSIONS = Arrays.asList(".mp3", ".m4a");
  public static final int FILE_EXTENSION_LEN = 3;

  final Label currentlyPlaying = new Label();
  final ProgressBar progress = new ProgressBar();
  final TableView<Map> metadataTable = new TableView<>();
  private ChangeListener<Duration> progressChangeListener;
  private MapChangeListener<String, Object> metadataChangeListener;

  public static void main(String[] args) throws Exception { launch(args); }

  public void start(final Stage stage) throws Exception {
    stage.setTitle("Simple Audio Player");

    // determine the source directory for the playlist (either the first argument to the program or a default).
    final List<String> params = getParameters().getRaw();
    final File dir = (params.size() > 0)
      ? new File(params.get(0))
      : new File(MUSIC_DIR);
    if (!dir.exists() || !dir.isDirectory()) {
      System.out.println("Cannot find audio source directory: " + dir + " please supply a directory as a command line argument");
      Platform.exit();
      return;
    }

    // create some media players.
    final List<MediaPlayer> players = new ArrayList<>();
    for (String file : dir.list(new FilenameFilter() {
       public boolean accept(File dir, String name) {
        for (String ext: SUPPORTED_FILE_EXTENSIONS) {
          if (name.endsWith(ext)) {
            return true;
          }
        }

        return false;
      }
    })) players.add(createPlayer("file:///" + (dir + "\\" + file).replace("\\", "/").replaceAll(" ", "%20")));
    if (players.isEmpty()) {
      System.out.println("No audio found in " + dir);
      Platform.exit();
      return;
    }    
    
    // create a view to show the mediaplayers.
    final MediaView mediaView = new MediaView(players.get(0));
    final Button skip = new Button("Skip");
    final Button play = new Button("Pause");

    // play each audio file in turn.
    for (int i = 0; i < players.size(); i++) {
      final MediaPlayer player     = players.get(i);
      final MediaPlayer nextPlayer = players.get((i + 1) % players.size());
      player.setOnEndOfMedia(new Runnable() {
         public void run() {
          player.currentTimeProperty().removeListener(progressChangeListener);
          player.getMedia().getMetadata().removeListener(metadataChangeListener);
          player.stop();
          mediaView.setMediaPlayer(nextPlayer);
          nextPlayer.play();
        }
      });
    }
    
    // allow the user to skip a track.
    skip.setOnAction(new EventHandler<ActionEvent>() {
       public void handle(ActionEvent actionEvent) {
        final MediaPlayer curPlayer = mediaView.getMediaPlayer();
        curPlayer.currentTimeProperty().removeListener(progressChangeListener);
        curPlayer.getMedia().getMetadata().removeListener(metadataChangeListener);
        curPlayer.stop();

        MediaPlayer nextPlayer = players.get((players.indexOf(curPlayer) + 1) % players.size());
        mediaView.setMediaPlayer(nextPlayer);
        nextPlayer.play();
      }
    });

    // allow the user to play or pause a track.
    play.setOnAction(new EventHandler<ActionEvent>() {
       public void handle(ActionEvent actionEvent) {
        if ("Pause".equals(play.getText())) {
          mediaView.getMediaPlayer().pause();
          play.setText("Play");
        } else {
          mediaView.getMediaPlayer().play();
          play.setText("Pause");
        }
      }
    });

    // display the name of the currently playing track.
    mediaView.mediaPlayerProperty().addListener(new ChangeListener<MediaPlayer>() {
       public void changed(ObservableValue<? extends MediaPlayer> observableValue, MediaPlayer oldPlayer, MediaPlayer newPlayer) {
        setCurrentlyPlaying(newPlayer);
      }
    });
    
    // start playing the first track.
    mediaView.setMediaPlayer(players.get(0));
    mediaView.getMediaPlayer().play();
    setCurrentlyPlaying(mediaView.getMediaPlayer());

    // silly invisible button used as a template to get the actual preferred size of the Pause button.
    Button invisiblePause = new Button("Pause");
    invisiblePause.setVisible(false);
    play.prefHeightProperty().bind(invisiblePause.heightProperty());
    play.prefWidthProperty().bind(invisiblePause.widthProperty());

    // add a metadataTable for meta data display
    metadataTable.setStyle("-fx-font-size: 13px;");

    TableColumn<Map, String> tagColumn = new TableColumn<>(TAG_COLUMN_NAME);
    tagColumn.setPrefWidth(150);
    TableColumn<Map, Object> valueColumn = new TableColumn<>(VALUE_COLUMN_NAME);
    valueColumn.setPrefWidth(400);

   // tagColumn.setCellValueFactory(new MapValueFactory<>(TAG_COLUMN_NAME));
    valueColumn.setCellValueFactory(new MapValueFactory<>(VALUE_COLUMN_NAME));

    metadataTable.setEditable(true);
    metadataTable.getSelectionModel().setCellSelectionEnabled(true);
    metadataTable.getColumns().setAll(tagColumn, valueColumn);
    valueColumn.setCellFactory(new Callback<TableColumn<Map, Object>, TableCell<Map, Object>>() {
      
      public TableCell<Map, Object> call(TableColumn<Map, Object> column) {
        return new TableCell<Map, Object>() {
          
          protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
              if (item instanceof String) {
                setText((String) item);
                setGraphic(null);
              } else if (item instanceof Integer) {
                setText(Integer.toString((Integer) item));
                setGraphic(null);
              } else if (item instanceof Image) {
                setText(null);
                ImageView imageView = new ImageView((Image) item);
                imageView.setFitWidth(200);
                imageView.setPreserveRatio(true);
                setGraphic(imageView);
              } else {
                setText("N/A");
                setGraphic(null);
              }
            } else {
              setText(null);
              setGraphic(null);
            }
          }
        };
      }
    });

    // layout the scene.
    final StackPane layout = new StackPane();
    layout.setStyle("-fx-background-color: cornsilk; -fx-font-size: 20; -fx-padding: 20; -fx-alignment: center;");

    final HBox progressReport = new HBox(10);
    progressReport.setAlignment(Pos.CENTER);
    progressReport.getChildren().setAll(skip, play, progress, mediaView);

    final VBox content = new VBox(10);
    content.getChildren().setAll(
        currentlyPlaying,
        progressReport,
        metadataTable
    );

    layout.getChildren().addAll(
      invisiblePause,
      content
    );
    progress.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(progress, Priority.ALWAYS);
    VBox.setVgrow(metadataTable, Priority.ALWAYS);

    Scene scene = new Scene(layout, 600, 400);
    stage.setScene(scene);
    stage.show();
  }

  /** sets the currently playing label to the label of the new media player and updates the progress monitor. */
  private void setCurrentlyPlaying(final MediaPlayer newPlayer) {
    newPlayer.seek(Duration.ZERO);

    progress.setProgress(0);
    progressChangeListener = new ChangeListener<Duration>() {
       public void changed(ObservableValue<? extends Duration> observableValue, Duration oldValue, Duration newValue) {
        progress.setProgress(1.0 * newPlayer.getCurrentTime().toMillis() / newPlayer.getTotalDuration().toMillis());
      }
    };
    newPlayer.currentTimeProperty().addListener(progressChangeListener);

    String source = newPlayer.getMedia().getSource();
    source = source.substring(0, source.length() - FILE_EXTENSION_LEN);
    source = source.substring(source.lastIndexOf("/") + 1).replaceAll("%20", " ");
    currentlyPlaying.setText("Now Playing: " + source);

    setMetaDataDisplay(newPlayer.getMedia().getMetadata());
  }

  private void setMetaDataDisplay(final ObservableMap<String, Object> metadata) {
    metadataTable.getItems().setAll(convertMetadataToTableData(metadata));
    metadataChangeListener = new MapChangeListener<String, Object>() {
      
      public void onChanged(Change<? extends String, ?> change) {
        metadataTable.getItems().setAll(convertMetadataToTableData(metadata));
      }
    };
    metadata.addListener(metadataChangeListener);
  }

  private ObservableList<Map> convertMetadataToTableData(ObservableMap<String, Object> metadata) {
    ObservableList<Map> allData = FXCollections.observableArrayList();

    for (String key: metadata.keySet()) {
      Map<String, Object> dataRow = new HashMap<>();

      dataRow.put(TAG_COLUMN_NAME,   key);
      dataRow.put(VALUE_COLUMN_NAME, metadata.get(key));

      allData.add(dataRow);
    }

    return allData;
  }

  /** @return a MediaPlayer for the given source which will report any errors it encounters */
  private MediaPlayer createPlayer(String mediaSource) {
    final Media media = new Media(mediaSource);
    final MediaPlayer player = new MediaPlayer(media);
    player.setOnError(new Runnable() {
       public void run() {
        System.out.println("Media error occurred: " + player.getError());
      }
    });
    return player;
  }
}