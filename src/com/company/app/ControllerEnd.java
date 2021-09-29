package com.company.app;

import com.company.model.HraciPlocha1;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ControllerEnd
{
    @FXML
    public VBox tabulkaHracu;

    public void nastavitTabulku(String[] barvy)
    {
        int[] tabulka = HraciPlocha1.getInstance().getTabulka();

        Label[] figurkyLabels = new Label[tabulka.length];

        for (int i = 0; i < tabulka.length; i++)
        {
            Label label = new Label((i + 1) + ". " + "Hráč " + (tabulka[i] + 1));

            label.setPrefWidth(300);
            label.setPrefHeight(50);
            label.setStyle("" +
                    "-fx-alignment: center; " +
                    "-fx-font-weight: bold; " +
                    "-fx-text-fill: " + barvy[tabulka[i]] + "; " +
                    "-fx-font-size: 20;"
            );

            figurkyLabels[i] = label;
        }

        tabulkaHracu.getChildren().addAll(figurkyLabels);
    }

    @FXML
    public void doHlavnihoMenu() throws Exception
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/menu1.fxml"));
        Parent root = loader.load();

        ControllerMenu controllerMenu = loader.getController();
        controllerMenu.pripravitMenu();

        Main.getMyStage().setScene(new Scene(root));
    }
}
