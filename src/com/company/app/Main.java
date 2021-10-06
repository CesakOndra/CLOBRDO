package com.company.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;

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
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("../view/menu1.fxml"));
        Parent root = loader.load();

        myStage = primaryStage;
        System.out.println(Inet4Address.getLocalHost().getHostAddress());
        System.out.println(InetAddress.getLocalHost().getHostAddress());

        ControllerMenu controllerMenu = loader.getController();
        controllerMenu.pripravitMenu();

        primaryStage.setTitle("CLOBRDO");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
