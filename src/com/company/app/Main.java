package com.company.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/menu1.fxml"));
        Parent root = loader.load();

        myStage = primaryStage;

        ControllerMenu controllerMenu = loader.getController();
        controllerMenu.pripravitMenu();

        primaryStage.setTitle("CLOBRDO");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
