package javafx;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;

/**
 * Created by Cardoso on 01-Mar-16.
 */
public class ZoomApp extends Application {

    private ImageView imageView = new ImageView();
    private ScrollPane scrollPane = new ScrollPane();
    final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

    @Override
    public void start(Stage primaryStage) throws Exception {

        zoomProperty.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                imageView.setFitWidth(zoomProperty.get() * 4);
                imageView.setFitHeight(zoomProperty.get() * 3);
            }
        });

        scrollPane.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        });

        Image image = new Image ("file:MandelbrotImage.png");
        imageView.setImage(image);
        imageView.preserveRatioProperty().set(true);
        scrollPane.setContent(imageView);
        primaryStage.setScene(new Scene(scrollPane, 400, 400));
        primaryStage.setTitle("Mandelbrot Set");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}