package com.company.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;

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

    public void pripravitMenu()
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
    }

    @FXML
    public void zapnoutHru0() throws Exception
    {
        FXMLLoader loader = new FXMLLoader((Objects.requireNonNull(getClass().getResource("../view/game1.fxml"))));
        Parent root = loader.load();

        int pocetHracu = playerChoiceBox0.getValue();
        int pocetFigurek = figurkyChoiceBox0.getValue();

        ControllerGame controllerGame = loader.getController();
        controllerGame.startHry(pocetHracu, pocetFigurek, velikostHraciPlochy0(), stenyChoiceBox0.getValue(), hranPolicka0.isSelected());

        Main.getMyStage().setScene(new Scene(root));
    }

    //endregion

    //region Hra


    //endregion
}
