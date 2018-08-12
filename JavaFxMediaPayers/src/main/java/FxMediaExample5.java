import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FxMediaExample5 extends Application
{
	// Create the Area for Logging
	private TextArea messageArea = new TextArea();

	public static void main(String[] args)
	{
		Application.launch(args);
	}

	@Override
	public void start(Stage stage)
	{
		// Create the Sliders
		final Slider cycleSlider = new Slider(1, 5, 1);
		cycleSlider.setMajorTickUnit(1);
		cycleSlider.setShowTickLabels(true);

		final Slider volumeSlider = new Slider(0.0, 1.0, 0.5);
		volumeSlider.setMajorTickUnit(0.1);
		volumeSlider.setShowTickLabels(true);

		final Slider rateSlider = new Slider(0, 8, 4);
		rateSlider.setMajorTickUnit(1);
		rateSlider.setShowTickLabels(true);

		final Slider balanceSlider = new Slider(-1.0, 1.0, 0.0);
		balanceSlider.setMajorTickUnit(0.2);
		balanceSlider.setShowTickLabels(true);

		// Locate the media content in the CLASSPATH
		URL mediaUrl=null;
		try {
			mediaUrl = Paths
					.get("song/Yeh Jo Halki Halki Khumariya Full Song Son Of Sardaar  Ajay Devgn Sonakshi Sinha.mp3")
					.toUri().toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
				//getClass().getResource("Test.mp4");
		
		String mediaStringUrl = mediaUrl.toExternalForm();

		// Create a Media
		final Media media = new Media(mediaStringUrl);

		// Create a Media Player
		final MediaPlayer player = new MediaPlayer(media);
		// Automatically begin the playback
		player.setAutoPlay(true);

		// Create a 400X300 MediaView
		final MediaView mediaView = new MediaView(player);
		mediaView.setFitWidth(400);
		mediaView.setFitHeight(300);
		mediaView.setSmooth(true);

		// Create the DropShadow effect
		DropShadow dropshadow = new DropShadow();
		dropshadow.setOffsetY(5.0);
		dropshadow.setOffsetX(5.0);
		dropshadow.setColor(Color.WHITE);

		mediaView.setEffect(dropshadow);

		// Create the Buttons
		Button playButton = new Button("Play");
		Button stopButton = new Button("Stop");

		// Create the Event Handlers for the Button
		playButton.setOnAction(new EventHandler <ActionEvent>()
		{
            public void handle(ActionEvent event)
            {
            	if (player.getStatus() == Status.PLAYING)
            	{
            		player.stop();
            		player.play();
            	}
            	else
            	{
            		player.play();
            	}
            }
        });

		stopButton.setOnAction(new EventHandler <ActionEvent>()
		{
            public void handle(ActionEvent event)
            {
            	player.stop();
            }
        });

		// Create the Listener for the Sliders
		cycleSlider.valueProperty().addListener(new InvalidationListener()
		{
			public void invalidated(Observable ov)
			{
				if (cycleSlider.isValueChanging())
				{
					messageArea.appendText("\nCycle Count changed to: " + (int)cycleSlider.getValue());
					player.setCycleCount((int)cycleSlider.getValue());
				}
			}
		});

		volumeSlider.valueProperty().addListener(new InvalidationListener()
		{
			public void invalidated(Observable ov)
			{
				if (volumeSlider.isValueChanging())
				{
					messageArea.appendText("\nVolume changed to: " + volumeSlider.getValue());
					player.setVolume(volumeSlider.getValue());
				}
			}
		});

		rateSlider.valueProperty().addListener(new InvalidationListener()
		{
			public void invalidated(Observable ov)
			{
				if (rateSlider.isValueChanging())
				{
					messageArea.appendText("\nRate changed to: " + rateSlider.getValue());
					player.setRate(rateSlider.getValue());
				}
			}
		});

		balanceSlider.valueProperty().addListener(new InvalidationListener()
		{
			public void invalidated(Observable ov)
			{
				if (balanceSlider.isValueChanging())
				{
					messageArea.appendText("\nBalance changed to: " + balanceSlider.getValue());
					player.setVolume(balanceSlider.getValue());
				}
			}
		});

		// Add Handlers for End and Repeat
		player.setOnEndOfMedia(new Runnable()
		{
            public void run()
            {
            	messageArea.appendText("\nEnd of media !");
            }
		});

		player.setOnRepeat(new Runnable()
		{
            public void run()
            {
            	messageArea.appendText("\nRepeating media !");
            }
		});

		// Create the GridPane
		GridPane sliderPane = new GridPane();
		// Set horizontal and vertical Spacing
		sliderPane.setHgap(5);
		sliderPane.setVgap(10);

		// Add the details to the GridPane
		sliderPane.addRow(0, new Label("CycleCount:"), cycleSlider);
		sliderPane.addRow(1, new Label("Volume:"), volumeSlider);
		sliderPane.addRow(2, new Label("Rate:"), rateSlider);
		sliderPane.addRow(3, new Label("Balance:"), balanceSlider);

		// Create the HBox
		HBox controlBox = new HBox(5, playButton, stopButton);

		// Create the VBox
		VBox root = new VBox(5,mediaView,controlBox,sliderPane,messageArea);

		// Set the Style-properties of the HBox
		root.setStyle("-fx-padding: 10;" +
				"-fx-border-style: solid inside;" +
				"-fx-border-width: 2;" +
				"-fx-border-insets: 5;" +
				"-fx-border-radius: 5;" +
				"-fx-border-color: blue;");

		// Create the Scene
		Scene scene = new Scene(root);
		// Add the scene to the Stage
		stage.setScene(scene);
		// Set the title of the Stage
		stage.setTitle("A Media Properties Example");
		// Display the Stage
		stage.show();
	}
}
