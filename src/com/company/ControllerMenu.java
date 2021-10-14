package com.company;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.Objects;

public class ControllerMenu
{


    //region Menu

    @FXML
    public RadioButton hranPolicka0;

    @FXML
    public ChoiceBox<Integer> playerChoiceBox0;
    @FXML
    public ChoiceBox<Integer> figurkyChoiceBox0;
    @FXML
    public ChoiceBox<Integer> stenyChoiceBox0;

    @FXML
    public TextField host;

    @FXML
    public TextField port;

    private int velikostHraciPlochy0()
    {
        int i = 10;

        switch (playerChoiceBox0.getValue())
        {
            case 4:
                i = 8;
                break;
            case 5:
                i = 7;
                break;
            case 6:
                i = 6;
                break;
            case 7:
            case 8:
                i = 5;
                break;
        }

        return i;
    }

    public void pripravitMenu() throws Exception
    {
        // pocet hracu
        for (int i = 2; i <= 8; i++)
        {
            playerChoiceBox0.getItems().add(i);
        }
        playerChoiceBox0.setValue(4);

        // pocet figurek
        for (int i = 1; i <= 4; i++)
        {
            figurkyChoiceBox0.getItems().add(i);
        }
        figurkyChoiceBox0.setValue(4);

        // pocet sten
        for (int i = 2; i <= 10; i++)
        {
            stenyChoiceBox0.getItems().add(i);
        }
        stenyChoiceBox0.setValue(6);

        host.setText(Inet4Address.getLocalHost().getHostAddress());
        port.setText("12358");
    }

    @FXML
    public void zapnoutHru0() throws Exception
    {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/game1.fxml"));
        Scene scene = new Scene(loader.load());

        int pocetHracu = playerChoiceBox0.getValue();
        int pocetFigurek = figurkyChoiceBox0.getValue();

        ControllerGame controllerGame = loader.getController();
        controllerGame.setMultiplayer(false);
        controllerGame.startHry(pocetHracu, pocetFigurek, velikostHraciPlochy0(), stenyChoiceBox0.getValue(), hranPolicka0.isSelected());

        Main.getMyStage().setScene(scene);
    }

    @FXML
    public void vytvoritHru() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/game1.fxml"));
        Scene scene = new Scene(loader.load());

        int pocetHracu = playerChoiceBox0.getValue();
        int pocetFigurek = figurkyChoiceBox0.getValue();

        if (Server.getInstance() != null) Server.getInstance().konec();
        Server.setIsServer(true);
        Server.setServer(new Server(pocetHracu, Integer.parseInt(port.getText())));

        ControllerGame controllerGame = loader.getController();
        controllerGame.setMultiplayer(true);
        controllerGame.startHry(pocetHracu, pocetFigurek, velikostHraciPlochy0(), stenyChoiceBox0.getValue(), hranPolicka0.isSelected());

        Main.getMyStage().setScene(scene);
    }

    @FXML
    public void pripojitSe() throws IOException
    {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/game1.fxml"));
        Scene scene = new Scene(loader.load());

        if (Client.getInstance() != null) Client.getInstance().konec();
        Client.setClient(new Client(host.getText(), Integer.parseInt(port.getText())));

        ControllerGame controllerGame = loader.getController();
        controllerGame.setMultiplayer(true);
        controllerGame.startHry(0, 0, 0, 0, hranPolicka0.isSelected());

        Main.getMyStage().setScene(scene);
    }

    //endregion

    //region Hra


    //endregion
}
