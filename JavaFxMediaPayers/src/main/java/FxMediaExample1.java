import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;

public class FxMediaExample1 extends Application
{
	private AudioClip audioClip;

	public static void main(String[] args)
	{
		Application.launch(args);
	}

	@Override
	public void init()
	{
		// Create an AudioClip, which loads the audio data synchronously
		URL resource=null;
		try {
			resource = Paths
					.get("song/Yeh Jo Halki Halki Khumariya Full Song Son Of Sardaar  Ajay Devgn Sonakshi Sinha.mp3")
					.toUri().toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//getClass().getResource("C:/Users/premendra.kumar/Desktop/songs/Yeh Jo Halki Halki Khumariya Full Song Son Of Sardaar  Ajay Devgn Sonakshi Sinha.mp3");
		audioClip = new AudioClip(resource.toExternalForm());
	}

	@Override
	public void start(Stage stage)
	{
		// Create the Buttons
		Button playButton = new Button("Play");
		Button stopButton = new Button("Stop");

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

		final Slider panSlider = new Slider(-1.0, 1.0, 0.0);
		panSlider.setMajorTickUnit(0.2);
		panSlider.setShowTickLabels(true);

		final Slider prioritySlider = new Slider(0.0, 10.0, 0.0);
		prioritySlider.setMajorTickUnit(1);
		prioritySlider.setShowTickLabels(true);

		// Create the Event Handlers for the Button
		playButton.setOnAction(new EventHandler <ActionEvent>()
		{
            public void handle(ActionEvent event)
            {
            	audioClip.play();
            }
        });

		stopButton.setOnAction(new EventHandler <ActionEvent>()
		{
            public void handle(ActionEvent event)
            {
            	audioClip.stop();
            }
        });

		// Bind the Properties
		audioClip.cycleCountProperty().bind(cycleSlider.valueProperty());
		audioClip.volumeProperty().bind(volumeSlider.valueProperty());
		audioClip.rateProperty().bind(rateSlider.valueProperty());
		audioClip.balanceProperty().bind(balanceSlider.valueProperty());
		audioClip.panProperty().bind(panSlider.valueProperty());
		audioClip.priorityProperty().bind(prioritySlider.valueProperty());

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
		sliderPane.addRow(4, new Label("Pan:"), panSlider);
		sliderPane.addRow(5, new Label("Priority:"), prioritySlider);

		// Create the HBox
		HBox buttonBox = new HBox(5, playButton, stopButton);

		VBox root = new VBox(5,sliderPane, buttonBox);
		// Set the Sie of the VBox
		root.setPrefWidth(300);
		root.setPrefHeight(350);

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
		stage.setTitle("An AucioClip Example");
		// Display the Stage
		stage.show();
	}
}
