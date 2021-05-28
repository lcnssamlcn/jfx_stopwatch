package com.practice.lcn.fx_stopwatch;

import com.practice.lcn.fx_stopwatch.controller.StopwatchController;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class Main extends Application {
    private static final String TITLE = "Stopwatch";
    private static final int WIDTH = 600;
    private static final int HEIGHT = 300;
    private static final int BTN_WIDTH = 80;
    private static final int BTN_HEIGHT = 80;
    private static final String BG_COLOR = "#E6E6FA";
    private static final String FONT_FAMILY = "Arial";

    private VBox root;
    private HBox display;
    private HBox btnBar;
    private Label labelHhmmss;
    private Label labelSss;
    private ToggleButton btnStartStop;
    private Button btnReset;

    private StopwatchController sc;

    @Override
    public void start(Stage stage) {
        this.root = new VBox(10);
        this.root.setPadding(new Insets(10, 10, 10, 10));

        this.display = new HBox(10);
        this.display.setPadding(new Insets(20, 10, 10, 10));
        this.display.setAlignment(Pos.BASELINE_CENTER);
        this.labelHhmmss = new Label(StopwatchController.INIT_HHMMSS);
        this.labelHhmmss.setFont(new Font(Main.FONT_FAMILY, 108));
        this.labelSss = new Label(StopwatchController.INIT_SSS);
        this.labelSss.setFont(new Font(Main.FONT_FAMILY, 48));
        this.display.getChildren().addAll(
            this.labelHhmmss, this.labelSss
        );
        this.root.getChildren().add(this.display);

        this.sc = new StopwatchController();
        this.sc.setLabelHhmmss(this.labelHhmmss);
        this.sc.setLabelSss(this.labelSss);

        this.btnBar = new HBox(20);
        this.btnBar.setPadding(new Insets(20, 10, 10, 10));
        this.btnBar.setAlignment(Pos.BASELINE_CENTER);
        this.btnStartStop = this.createToggleBtn("/css/btnStartStop.css");
        this.btnStartStop.setOnAction(new EventHandler<ActionEvent>() {
        @Override
            public void handle(ActionEvent event) {
                StopwatchController.STATE state = sc.getState();
                if (state == StopwatchController.STATE.STOP) {
                    sc.start();
                }
                else if (state == StopwatchController.STATE.PAUSE) {
                    sc.resume();
                }
                else if (state == StopwatchController.STATE.RUNNING) {
                    sc.pause();
                }
            }
        });
        this.btnReset = this.createImgBtn("/img/btn_reset.png");
        this.btnReset.setOnAction(new EventHandler<ActionEvent>() {
        @Override
            public void handle(ActionEvent event) {
                StopwatchController.STATE state = sc.getState();
                if (state == StopwatchController.STATE.RUNNING) {
                    btnStartStop.fire();
                    event.consume();
                }
                sc.reset();
            }
        });
        this.btnBar.getChildren().addAll(
            this.btnStartStop, this.btnReset
        );
        this.root.getChildren().add(this.btnBar);

        stage.setTitle(Main.TITLE);
        stage.setResizable(false);
        this.root.setStyle(String.format("-fx-background-color: %s;", Main.BG_COLOR));
        Scene scene = new Scene(this.root, Main.WIDTH, Main.HEIGHT, Color.web(Main.BG_COLOR));
        stage.setScene(scene);

        stage.show();
    }

    private ToggleButton createToggleBtn(String cssPath) {
        ToggleButton btn = new ToggleButton();
        btn.getStylesheets().add(this.getClass().getResource(cssPath).toExternalForm());
        btn.setMinSize(Main.BTN_WIDTH, Main.BTN_HEIGHT);
        btn.setMaxSize(Main.BTN_WIDTH, Main.BTN_HEIGHT);

        return btn;
    }

    private Button createImgBtn(String imgPath) {
        Image img = new Image(imgPath);
        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);

        Button imgBtn = new Button();
        imgBtn.setPrefSize(Main.BTN_WIDTH, Main.BTN_HEIGHT);
        imgBtn.setGraphic(iv);

        return imgBtn;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
