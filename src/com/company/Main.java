package com.company;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Objects;

public class Main extends Application
{

    public static void main(String[] args)
    {
        launch(args);
    }


    private static Stage myStage;

    public static Stage getMyStage()
    {
        return myStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/menu1.fxml"));
        Scene scene = new Scene(loader.load());

        myStage = primaryStage;

        primaryStage.setOnCloseRequest(e ->
        {
            Platform.exit();
            System.exit(0);
        });

        ControllerMenu controllerMenu = loader.getController();
        controllerMenu.pripravitMenu();

        primaryStage.setTitle("CLOBRDO");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
