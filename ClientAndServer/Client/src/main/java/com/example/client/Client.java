package com.example.client;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.paint.LinearGradient;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Consumer;

public class Client extends Application {
    private VBox msgArea = new VBox();
    private TextField input = new TextField();
    private ClientServer clientServer = creatClient();
    private Label top = new Label();
    private BorderPane borderPane = new BorderPane();
    private ScrollPane scrollPane = new ScrollPane();

    public HBox leftMsg(Label text){
        text.setTextFill(Color.WHITE);
        text.setWrapText(true);
        text.setMaxWidth(300);
        Stop[] stops = new Stop[] { new Stop(0, Color.rgb(0, 128,129)), new Stop(1, Color.rgb(0,142,204))};
        text.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops), new CornerRadii(10), new Insets(-5,-5,-5,-10))));
        text.setFont(new Font("Fira Code Light", 18));
        HBox hBox = new HBox(text);
        hBox.setPadding(new Insets(10));
        hBox.setAlignment(Pos.BOTTOM_LEFT);
        return hBox;
    }

    public HBox rightMsg(Label text){
        text.setTextFill(Color.WHITE);
        text.setWrapText(true);
        text.setMaxWidth(300);
        Stop[] stops = new Stop[] { new Stop(0, Color.BLUE), new Stop(1, Color.rgb(88,139,174))};
        text.setBackground(new Background(new BackgroundFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops), new CornerRadii(10), new Insets(-5,-5,-5,-10))));
        text.setFont(new Font("Fira Code Light", 18));
        HBox hBox = new HBox(text);
        hBox.setPadding(new Insets(10));
        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        return hBox;
    }

    public void toSend(){
        if(!input.getText().equals("")) {
            Label text = new Label(input.getText());
            msgArea.getChildren().add(rightMsg(text));


            try {

                clientServer.send(input.getText());
                input.clear();
            } catch (IOException exception) {
                top.setText("Server not connected");
            }
        }
    }
    private Parent content(){
        input.setPrefHeight(35);
        input.setOnAction(e -> toSend());
        msgArea.setAlignment(Pos.BOTTOM_CENTER);

        borderPane.setTop(top);
        borderPane.setCenter(msgArea);
        borderPane.setBottom(input);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.vvalueProperty().bind(msgArea.heightProperty());
        scrollPane.setContent(borderPane);

        borderPane.setStyle("-fx-background-color:rgb(37, 33, 19);");
        scrollPane.setStyle("-fx-background-color:rgb(37, 33, 19);");
        input.setStyle(" -fx-background-color: grey; -fx-font-color: white; -fx-background-radius: 20 ;");

        return scrollPane;
    }

    @Override
    public void init() throws Exception {
        clientServer.startConnection();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(content(), 400, 600);
        primaryStage.setTitle("Chat Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        clientServer.closeConnection();
    }

    private ClientObject creatClient(){
        return new ClientObject("localhost", 5056, data ->{
            Platform.runLater(() ->{
                Label text = new Label(data.toString());
                msgArea.getChildren().add(leftMsg(text));
            });
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}



class ClientObject extends ClientServer{
    private String ip;
    private int port;

    ClientObject(String ip, int port, Consumer<String> onReceivedCallback){
        super(onReceivedCallback);
        this.ip = ip;
        this.port = port;
    }
}