package javafx;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.CanvasBuilder;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * Created by Cardoso on 29-Feb-16.
 */
public class MandelbrotWithZoom extends Application {
    // Size of the canvas for the Mandelbrot set
    private static final int CANVAS_WIDTH = 400;
    private static final int CANVAS_HEIGHT = 400;
    // Left and right border
    private static final int X_OFFSET = 25;
    // Top and Bottom border
    private static final int Y_OFFSET = 20;

    @Override
    public void start(Stage primaryStage) throws Exception {
        final Pane fractalRootPane = new Pane();
        Canvas canvas = CanvasBuilder
                .create()
                .height(CANVAS_HEIGHT)
                .width(CANVAS_WIDTH)
                .layoutX(X_OFFSET)
                .layoutY(Y_OFFSET)
                .build();

        double MANDELBROT_RE_MIN = -2;
        double MANDELBROT_RE_MAX = 1;
        double MANDELBROT_IM_MIN = -1.2;
        double MANDELBROT_IM_MAX = 1.2;
        paintSet(canvas.getGraphicsContext2D(),
                MANDELBROT_RE_MIN,
                MANDELBROT_RE_MAX,
                MANDELBROT_IM_MIN,
                MANDELBROT_IM_MAX);

        fractalRootPane.getChildren().add(canvas);
        Scene scene = new Scene(fractalRootPane, CANVAS_WIDTH + 2 * X_OFFSET, CANVAS_HEIGHT + 2 * Y_OFFSET);
        scene.setFill(Color.BLACK);

        //zoom
        final ImageView imageView = new ImageView();
        ScrollPane scrollPane = new ScrollPane();
        final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

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

        //saving image
        int width = (int) scene.getWidth();
        int height = (int) scene.getHeight();
        WritableImage imageOut = new WritableImage(height, width);
        canvas.snapshot(null, imageOut);
        File file = new File("MandelbrotImage.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(imageOut, null), "png", file);
        } catch (Exception s) {
        }

        Image MandelbrotImage = new Image ("file:MandelbrotImage.png");
        imageView.setImage(MandelbrotImage);
        imageView.preserveRatioProperty().set(true);
        scrollPane.setContent(imageView);
        primaryStage.setScene(new Scene(scrollPane, 400, 400));
        primaryStage.setTitle("Mandelbrot Set");
        primaryStage.show();
    }

    private void paintSet(GraphicsContext ctx, double reMin, double reMax, double imMin, double imMax) {
        double precision = Math.max((reMax - reMin) / CANVAS_WIDTH, (imMax - imMin) / CANVAS_HEIGHT);
        int convergenceSteps = 50;
        for (double c = reMin, xR = 0; xR < CANVAS_WIDTH; c = c + precision, xR++) {
            for (double ci = imMin, yR = 0; yR < CANVAS_HEIGHT; ci = ci + precision, yR++) {
                double convergenceValue = checkConvergence(ci, c, convergenceSteps);
                double t1 = convergenceValue / convergenceSteps;
                //playing with colour
                double c1 = Math.min(400 * 2 * t1, 100);
                double c2 = Math.max(255 * (2 * t1 - 1), 0);
                double c3 = Math.min(300 * 2 * t1, 500);
                double c4 = Math.max(255 * (2 * t1 - 1), 0);

                if (convergenceValue != convergenceSteps) {
                    ctx.setFill(Color.color(c2 / 255.0, c1 / 255.0, c1 / 255.0));
                } else {
                    // Convergence Color
                    //ctx.setFill(Color.GREEN);
                    ctx.setFill(Color.color(c4 / 500.0, c4 / 400.0, c3 / 500.0));

                }
                ctx.fillRect(xR, yR, 1, 1);
            }
        }
    }

    /**
     * Checks the convergence of a coordinate (c, ci) The convergence factor
     * determines the color of the point.
     */

    private int checkConvergence(double ci, double c, int convergenceSteps) {
        double z = 0;
        double zi = 0;
        for (int i = 0; i < convergenceSteps; i++) {
            double ziT = 2 * (z * zi);
            double zT = z * z - (zi * zi);
            z = zT + c;
            zi = ziT + ci;

            if (z * z + zi * zi >= 4.0) {
                return i;
            }
        }
        return convergenceSteps;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}




