package com.company.app;

import com.company.model.HraciPlocha;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {

    private static Controller controller;
    public static Controller getController() {return controller;}

    public static final String[] barvy = new String[]
            {
                    "#f00"   , "#00f"   , // red   , blue
                    "#00d100", "#ebd300", // green , yellow
                    "#fb00ff", "#00E5E5", // pink  , aqua
                    "#f87b05", "#a566ff", // orange, violet
            };


    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/gui.fxml"));
        Parent root = loader.load();

        controller = loader.getController();

        // pocet hracu
        for (int i = 2; i <= 8; i++) {
            controller.playerChoiceBox.getItems().add(i);
        }
        controller.playerChoiceBox.setValue(4);

        // pocet figurek
        for (int i = 1; i <= 4; i++) {
            controller.figurkyChoiceBox.getItems().add(i);
        }
        controller.figurkyChoiceBox.setValue(4);

        // pocet sten
        for (int i = 2; i <= 10; i++) {
            controller.stenyChoiceBox.getItems().add(i);
        }
        controller.stenyChoiceBox.setValue(6);

        controller.zapnoutPanel(controller.menuPane);

        primaryStage.setTitle("CLOBRDO");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }



    public static void vytvoritHry(int pocetHracu, int delkaC, int pocetF) {
        delkaCesty = delkaC;
        pocetFigurek = pocetF;

        nastavitHraciPlochu(pocetHracu);
        nastavitListHracu(pocetHracu);
    }

    // HEJ ONDRO -------------------------------------------------
    /*
    nezapomen pridat nastaveni pro X policek mezi hraci,
    pocet figurek a dalsi jina nastaveni, diky :D
     */
    // -----------------------------------------------------------

    //region Vytvoreni

    static int delkaCesty;
    static int pocetFigurek;

    static void nastavitHraciPlochu(int pocetHracu) {

        Button[] policka = new Button[pocetHracu * delkaCesty];

        //BS = button size, ROW = count of buttons in a row
        double bs = Math.floor(27 * (80.0 / policka.length));

        double row = (controller.battlefield.getPrefWidth() % bs != 0)
                ? Math.floor((controller.battlefield.getPrefWidth() / bs))
                : (controller.battlefield.getPrefWidth() / bs);
        int radius = (controller.hranPolicka.isSelected()) ? 0 : 100;

        int i1 = pocetHracu * pocetFigurek; // pocet cilovych poli
        int i2 = 0; // cislo pro orientaci mezi radky pro cilova policka

        for(int i = 0; i < policka.length; i++) {

            double ii = Math.floor((i + i1) / row); // udava, v kolikate rade jsme

            policka[i] = new Button("");

            double angle = (360.0 / policka.length) * i;

            // sin a cos pro vypocitani xy pozice v okne
            double x = Math.cos(Math.PI / (180 / angle));
            double y = Math.sin(Math.PI / (180 / angle));

            //                                 vynasobeni pro protazeni po celem okne                       reseni radku a poradi tlacitka
            policka[i].setTranslateX(((x + 1) * ((controller.battlefield.getPrefWidth()  - bs) / 2 - 5)) - ((i + i1 - (row * ii)) * bs) + 5);
            policka[i].setTranslateY(((y + 1) * ((controller.battlefield.getPrefHeight() - bs) / 2 - 5)) - (ii * bs)                    + 5);

            policka[i].setRotate(angle);

            // nastaveni cilovych policek
            if(i % delkaCesty == 0) {
                Button[] cilovaPolicka = new Button[pocetFigurek];

                for (int ic = 0; ic < cilovaPolicka.length; ic++) {

                    double ii1 = Math.floor(i2 / row);

                    cilovaPolicka[ic] = new Button("");

                    double centerX = -((i2 - (row * ii1)) * bs) + (controller.battlefield.getPrefWidth()  - bs) / 2;
                    double centerY = -(ii1 * bs)                + (controller.battlefield.getPrefHeight() - bs) / 2;

                    double spacing = ((pocetHracu == 2) ? 0.8 : 1.2);
                    double offset = ((pocetHracu == 2) ? 25 : 10);

                    double dif = bs / ((controller.battlefield.getPrefWidth()  - bs) / 2) * spacing;

                    cilovaPolicka[ic].setTranslateX(centerX + ((x * (1 - (dif * (ic + 1)))) * ((controller.battlefield.getPrefWidth()  - bs) / 2 - offset)));
                    cilovaPolicka[ic].setTranslateY(centerY + ((y * (1 - (dif * (ic + 1)))) * ((controller.battlefield.getPrefHeight() - bs) / 2 - offset)));

                    cilovaPolicka[ic].setRotate(angle);

                    cilovaPolicka[ic].setPrefWidth(bs);
                    cilovaPolicka[ic].setPrefHeight(bs);

                    cilovaPolicka[ic].setScaleX(((pocetHracu == 2) ? 0.7 : 1));
                    cilovaPolicka[ic].setScaleY(((pocetHracu == 2) ? 0.7 : 1));

                    cilovaPolicka[ic].setStyle(
                            "-fx-border-color: " + barvy[i / delkaCesty] + ";-fx-border-radius: " + radius + ";" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: " + radius + ";" +
                                    "-fx-font-size: " + Math.floor(12 * (80.0 / policka.length)) + ";" +
                                    "-fx-text-fill: " + barvy[i / delkaCesty]
                    );

                    cilovaPolicka[ic].setFocusTraversable(false);

                    int hrac = i / delkaCesty;
                    int pole = ic;
                    cilovaPolicka[ic].setOnAction(actionEvent -> {
                        Controller.vybratCilovePolicko(hrac, pole);
                    });

                    i2++;
                }

                controller.battlefield.getChildren().addAll(cilovaPolicka);
                controller.nastavitCilovaPolicka(i / delkaCesty, cilovaPolicka);
            }

            int id = i;
            policka[i].setOnAction(actionEvent -> {
                Controller.vybratPolicko(id);
            });

            policka[i].setPrefWidth(bs);
            policka[i].setPrefHeight(bs);

            policka[i].setStyle(
                    "-fx-font-weight: bold;" +
                            "-fx-background-radius: " + radius + ";" +
                            "-fx-font-size: " + Math.floor(12 * (80.0 / policka.length)) + ";" +
                            (((i - 1) % delkaCesty == 0)?"-fx-border-color: " + barvy[i / delkaCesty] + "; -fx-border-radius: " + radius + "; -fx-border-width: 3; -fx-border-insets: -3":"") + ";" +
                            ((i % delkaCesty == 0)?"-fx-border-color: " + barvy[i / delkaCesty] + "; -fx-border-radius: " + radius + "; -fx-border-width: 2; -fx-border-insets: -2":"")
            );

            policka[i].setFocusTraversable(false);
        }

        controller.battlefield.getChildren().addAll(policka);
        controller.nastavitPolicka(policka);
    }

    static void nastavitListHracu(int pocetHracu) {
        VBox[] domecky = new VBox[pocetHracu];

        Label[] figurkyLabels = new Label[pocetHracu];

        for (int i = 0; i < pocetHracu; i++) {
            domecky[i] = new VBox();
            domecky[i].setPrefWidth(200);
            domecky[i].setPrefHeight(79);

            domecky[i].setStyle(
                    "-fx-border-color: " + barvy[i] + ";" +
                            "-fx-border-width: 3;" +
                            "-fx-border-radius: " + ((controller.hranPolicka.isSelected()) ? 0 : 80) + ";" +
                            "-fx-background-radius: " + ((controller.hranPolicka.isSelected()) ? 0 : 80) + ";" +
                            "-fx-background-color: #fff"
            );

            Label hracLabel = new Label("Hráč " + (i + 1));

            hracLabel.setPrefWidth(200);
            hracLabel.setPrefHeight(30);
            hracLabel.setStyle("-fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: " + barvy[i] + "; -fx-font-size: 20");

            Label figurkyLabel = new Label("");

            figurkyLabel.setPrefWidth(200);
            figurkyLabel.setPrefHeight(49);
            figurkyLabel.setStyle("-fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: " + barvy[i] + "; -fx-font-size: 28");

            figurkyLabels[i] = figurkyLabel;

            domecky[i].getChildren().add(hracLabel);
            domecky[i].getChildren().add(figurkyLabel);
        }

        controller.playerList.getChildren().addAll(domecky);
        controller.nastavitHracskePanely(domecky);
        controller.nastavitHracskeFigurky(figurkyLabels);
    }

    //endregion

    //region Ukonceni

    public static void ukoncitHru(int[] poradi) {
        controller.zapnoutPanel(controller.endPane);

        Label[] hraci = new Label[poradi.length];

        for (int i = 0; i < poradi.length; i++) {
            hraci[i] = new Label((i + 1) + ". HRÁČ " + (poradi[i] + 1));
            hraci[i].setStyle("-fx-font-weight: bold; -fx-font-size: 18; -fx-text-fill: " + barvy[poradi[i]]);
        }

        controller.tabulkaHracu.getChildren().addAll(hraci);
    }

    //endregion
}
