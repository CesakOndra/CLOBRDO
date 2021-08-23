package com.company.app;

import com.company.model.HraciPlocha;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Controller {

    @FXML public Pane menuPane;
    @FXML public Pane gamePane;
    @FXML public Pane endPane;

    //region Menu

    @FXML public RadioButton hranPolicka;
    @FXML public RadioButton autoKonecTahu;
    @FXML public RadioButton malaHraciPlocha;

    @FXML public ChoiceBox<Integer> playerChoiceBox;
    @FXML public ChoiceBox<Integer> figurkyChoiceBox;
    @FXML public ChoiceBox<Integer> stenyChoiceBox;

    @FXML
    public void zapnoutHru() {
        zapnoutPanel(gamePane);
        HraciPlocha.getInstance().zacitHru(
                playerChoiceBox.getValue(),
                stenyChoiceBox.getValue(),
                ((malaHraciPlocha.isSelected()) ? velikostHraciPlochy() : 10),
                figurkyChoiceBox.getValue()
        );
    }

    private int velikostHraciPlochy() {
        int i = 10;

        switch (playerChoiceBox.getValue()) {
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

    //endregion

    //region Game

    @FXML public FlowPane battlefield;

    @FXML public VBox playerList;

    @FXML public Button posunuti;
    public void zapnoutPosunuti(boolean b) {
        posunuti.setDisable(!b);
    }

    @FXML public Button nasazeni;
    public void zapnoutNasazeni(boolean b) {
        nasazeni.setDisable(!b);
    }

    @FXML public Button konec;
    public void nastavitKonec(boolean b) {
        kostka.setDisable(!b);
    }

    @FXML public Button kostka;
    public void zapnoutKostku(boolean b) {
        kostka.setDisable(!b);
        kostka.setOpacity((b) ? 1 : 0.75);
    }
    public void obarvitKostku(String barva) {
        kostka.setStyle("-fx-background-radius: 0; -fx-text-fill: " + barva);
    }

    @FXML public VBox[] hracskePanely;
    public VBox[] getHracskePanely() {return hracskePanely;}

    @FXML public Label[] hracskeFigurky;

    @FXML public Button[] policka;
    public Button[] getPolicka(){return policka;}

    @FXML public Button[][] cilovaPolicka;
    public void initCilovaPolicka() {
        cilovaPolicka = new Button[playerChoiceBox.getValue()][];
    }
    public Button[][] getCilovaPolicka(){return cilovaPolicka;}

    //region Init Metody

    public void nastavitHracskePanely(VBox[] vBoxes) {
        hracskePanely = vBoxes;
    }

    public void nastavitHracskeFigurky(Label[] labels) {
        hracskeFigurky = labels;
    }

    public void nastavitPolicka(Button[] buttons) {
        policka = buttons;
    }

    public void nastavitCilovaPolicka(int koho, Button[] buttons) {
        cilovaPolicka[koho] = buttons;
    }

    //endregion

    public void nastavitPocetFigurek(int koho, int pocetFigurek) {

        StringBuilder figurkyVTextu = new StringBuilder();

        for(int i = 0; i < pocetFigurek; i++) {
            figurkyVTextu.append("O").append((i < pocetFigurek - 1) ? " " : "");
        }

        hracskeFigurky[koho].setText(figurkyVTextu.toString());
    }

    @FXML
    public static void vybratPolicko(int p) {
        HraciPlocha.getInstance().vybratPole(p);
    }

    @FXML
    public static void vybratCilovePolicko(int h, int p) {
        HraciPlocha.getInstance().vybratCilovePole(h, p);
    }

    @FXML
    public void hodKostkou() {
        kostka.setText(String.valueOf(HraciPlocha.getInstance().hodKostkou()));
    }

    @FXML
    public void nasadit() {
        HraciPlocha.getInstance().nasaditFigurku();
    }

    @FXML
    public void posunout() {
        HraciPlocha.getInstance().posunout();
    }

    @FXML
    public void konec() {
        HraciPlocha.getInstance().konecTahu();
    }

    //endregion

    //region End

    @FXML public VBox tabulkaHracu;

    @FXML
    public void doHlavniNabidky() {
        zapnoutPanel(menuPane);
    }

    //endregion

    public void zapnoutPanel(Pane pane) {
        menuPane.setVisible(false);
        gamePane.setVisible(false);
        endPane.setVisible(false);

        pane.setVisible(true);
    }

}
