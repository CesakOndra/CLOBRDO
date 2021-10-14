package com.company;

import com.company.model.BarvaFigurky;
import com.company.model.Figurka;
import com.company.model.HraciPlocha1;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ControllerGame
{
    private boolean multiplayer;

    public void setMultiplayer(boolean multiplayer)
    {
        this.multiplayer = multiplayer;
    }

    public final String[] barvy = new String[]
            {
                    "#f00", "#00f",       // red   , blue
                    "#00d100", "#ffdd00", // green , yellow
                    "#fb00ff", "#00E5E5", // pink  , aqua
                    "#f87b05", "#a566ff", // orange, violet
            };

    @FXML
    public FlowPane battlefield;

    @FXML
    public VBox playerList;

    @FXML
    public Button start;

    @FXML
    public Button nasazeni;

    @FXML
    public Button konec;

    @FXML
    public Button kostka;

    @FXML
    public VBox[] hracskePanely;

    @FXML
    public Label[] hracskeFigurky;

    @FXML
    public Button[] policka;

    @FXML
    public Button[][] cilovaPolicka;


    HraciPlocha1 plocha = HraciPlocha1.getInstance();

    private static boolean hranPolicka;

    public void startHry(int pocetHracu, int pocetFigurek, int velikostPlochy, int pocetSten, boolean hp) throws IOException
    {
        plocha.novaHraciPlocha();

        hranPolicka = hp;

        if (multiplayer)
        {
            if (Server.isServer()) // Server
            {
                plocha.pripravitHru(
                        pocetHracu,
                        pocetSten,
                        velikostPlochy,
                        pocetFigurek
                );

                Server.getInstance().zprava(new String[]{
                        String.valueOf(pocetHracu),
                        String.valueOf(pocetSten),
                        String.valueOf(velikostPlochy),
                        String.valueOf(pocetFigurek)
                });

                vytvoritHru(pocetHracu, velikostPlochy, pocetFigurek);

                for (int i = 0; i < pocetHracu; i++)
                {
                    nastavitPocetFigurek0(i, pocetFigurek);
                }
            }
            else // Client
            {
                String[] strings = Client.getInstance().read();

                plocha.pripravitHru(
                        Integer.parseInt(strings[0]),
                        Integer.parseInt(strings[1]),
                        Integer.parseInt(strings[2]),
                        Integer.parseInt(strings[3])
                );

                vytvoritHru(Integer.parseInt(strings[0]), Integer.parseInt(strings[2]), Integer.parseInt(strings[3]));

                for (int i = 0; i < Integer.parseInt(strings[0]); i++)
                {
                    nastavitPocetFigurek0(i, Integer.parseInt(strings[3]));
                }

            }

            plocha.zacitHru();
            zacatekTahu();

            if (Server.isServer()) realtimeServer();
            else realtimeClient();
        }
        else // Singleplayer
        {
            plocha.pripravitHru(
                    pocetHracu,
                    pocetSten,
                    velikostPlochy,
                    pocetFigurek
            );

            vytvoritHru(pocetHracu, velikostPlochy, pocetFigurek);

            for (int i = 0; i < pocetHracu; i++)
            {
                nastavitPocetFigurek0(i, pocetFigurek);
            }

            plocha.zacitHru();
            zacatekTahu();
        }

        start.setDisable(true);
        start.setVisible(false);
    }

    int delkaCesty;
    int pocetFigurek;

    public void vytvoritHru(int pocetHracu, int delkaC, int pocetF)
    {
        delkaCesty = delkaC;
        pocetFigurek = pocetF;

        nastavitHraciPlochu(pocetHracu);
        nastavitListHracu(pocetHracu);
    }

    void nastavitHraciPlochu(int pocetHracu)
    {
        //policka = new Button[pocetHracu * delkaCesty];
        cilovaPolicka = new Button[pocetHracu][];

        Button[] policka = new Button[pocetHracu * delkaCesty];

        double d = 50.0;

        //BS = button size, ROW = count of buttons in a row
        double bs = Math.floor(27 * (d / policka.length)); // 27

        double row = (battlefield.getPrefWidth() % bs != 0)
                ? Math.floor((battlefield.getPrefWidth() / bs))
                : (battlefield.getPrefWidth() / bs);
        int radius = (hranPolicka) ? 0 : 100;

        int i1 = pocetHracu * pocetFigurek; // pocet cilovych poli
        int i2 = 0; // cislo pro orientaci mezi radky pro cilova policka

        for (int i = 0; i < policka.length; i++)
        {
            double ii = Math.floor((i + i1) / row); // udava, v kolikate rade jsme

            policka[i] = new Button("");

            double angle = (360.0 / policka.length) * i;

            // sin a cos pro vypocitani xy pozice v okne
            double x = Math.cos(Math.PI / (180 / angle));
            double y = Math.sin(Math.PI / (180 / angle));

            //                                 vynasobeni pro protazeni po celem okne                       reseni radku a poradi tlacitka
            policka[i].setTranslateX(((x + 1) * ((battlefield.getPrefWidth() - bs) / 2 - 5)) - ((i + i1 - (row * ii)) * bs) + 5);
            policka[i].setTranslateY(((y + 1) * ((battlefield.getPrefHeight() - bs) / 2 - 5)) - (ii * bs) + 5);

            policka[i].setRotate(angle);

            // nastaveni cilovych policek
            double font = Math.floor(12 * (d / policka.length));

            if (i % delkaCesty == 0)
            {
                cilovaPolicka[i / delkaCesty] = new Button[pocetFigurek];

                Button[] cilovaPolicka = new Button[pocetFigurek];

                for (int ic = 0; ic < cilovaPolicka.length; ic++)
                {

                    double ii1 = Math.floor(i2 / row);

                    cilovaPolicka[ic] = new Button("");

                    double centerX = -((i2 - (row * ii1)) * bs) + (battlefield.getPrefWidth() - bs) / 2;
                    double centerY = -(ii1 * bs) + (battlefield.getPrefHeight() - bs) / 2;

                    double spacing = ((pocetHracu == 2) ? 0.8 : 1.2);
                    double offset = ((pocetHracu == 2) ? 25 : 10);

                    double dif = bs / ((battlefield.getPrefWidth() - bs) / 2) * spacing;

                    cilovaPolicka[ic].setTranslateX(centerX + ((x * (1 - (dif * (ic + 1)))) * ((battlefield.getPrefWidth() - bs) / 2 - offset)));
                    cilovaPolicka[ic].setTranslateY(centerY + ((y * (1 - (dif * (ic + 1)))) * ((battlefield.getPrefHeight() - bs) / 2 - offset)));

                    cilovaPolicka[ic].setRotate(angle);

                    cilovaPolicka[ic].setPrefWidth(bs);
                    cilovaPolicka[ic].setPrefHeight(bs);

                    cilovaPolicka[ic].setScaleX(((pocetHracu == 2) ? 0.7 : 1));
                    cilovaPolicka[ic].setScaleY(((pocetHracu == 2) ? 0.7 : 1));

                    cilovaPolicka[ic].setStyle(
                            "-fx-border-color: " + barvy[i / delkaCesty] + ";-fx-border-radius: " + radius + ";" +
                                    "-fx-font-weight: bold;" +
                                    "-fx-background-radius: " + radius + ";" +
                                    "-fx-font-size: " + font + ";" +
                                    "-fx-text-fill: " + barvy[i / delkaCesty]
                    );

                    cilovaPolicka[ic].setFocusTraversable(false);

                    int hrac = i / delkaCesty;
                    int pole = ic;
                    cilovaPolicka[ic].setOnAction(actionEvent ->
                    {
                        try
                        {
                            vybratCilovePolicko(hrac, pole);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    });

                    i2++;
                }

                battlefield.getChildren().addAll(cilovaPolicka);
                this.cilovaPolicka[i / delkaCesty] = cilovaPolicka;
            }

            int id = i;
            policka[i].setOnAction(actionEvent ->
            {
                try
                {
                    vybratPolicko(id);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            });

            policka[i].setPrefWidth(bs);
            policka[i].setPrefHeight(bs);

            policka[i].setStyle(
                    "-fx-font-weight: bold;" +
                            "-fx-background-radius: " + radius + ";" +
                            "-fx-font-size: " + font + ";" +
                            (((i - 1) % delkaCesty == 0) ? "-fx-border-color: " + barvy[i / delkaCesty] + "; -fx-border-radius: " + radius + "; -fx-border-width: 3; -fx-border-insets: -3" : "") + ";" +
                            ((i % delkaCesty == 0) ? "-fx-border-color: " + barvy[i / delkaCesty] + "; -fx-border-radius: " + radius + "; -fx-border-width: 2; -fx-border-insets: -2" : "")
            );

            policka[i].setFocusTraversable(false);
        }

        battlefield.getChildren().addAll(policka);
        this.policka = policka;
    }

    void nastavitListHracu(int pocetHracu)
    {

        VBox[] domecky = new VBox[pocetHracu];

        Label[] figurkyLabels = new Label[pocetHracu];

        for (int i = 0; i < pocetHracu; i++)
        {
            domecky[i] = new VBox();
            domecky[i].setPrefWidth(150); // 200
            domecky[i].setPrefHeight(50); // 79

            domecky[i].setStyle(
                    "-fx-border-color: " + barvy[i] + ";" +
                            "-fx-border-width: 1;" +
                            "-fx-border-radius: " + ((hranPolicka) ? 0 : 80) + ";" +
                            "-fx-background-radius: " + ((hranPolicka) ? 0 : 80) + ";" +
                            "-fx-background-color: #fff"
            );

            Label hracLabel = new Label("Hráč " + (i + 1));

            hracLabel.setPrefWidth(200);
            hracLabel.setPrefHeight(20);
            hracLabel.setStyle("-fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: " + barvy[i] + "; -fx-font-size: 13");

            Label figurkyLabel = new Label("");

            figurkyLabel.setPrefWidth(200);
            figurkyLabel.setPrefHeight(30);
            figurkyLabel.setStyle("-fx-alignment: center; -fx-font-weight: bold; -fx-text-fill: " + barvy[i] + "; -fx-font-size: 16");

            figurkyLabels[i] = figurkyLabel;

            domecky[i].getChildren().add(hracLabel);
            domecky[i].getChildren().add(figurkyLabel);
        }

        playerList.getChildren().addAll(domecky);
        hracskePanely = domecky;
        hracskeFigurky = figurkyLabels;
    }

    private void nastavitKdoHraje(int h)
    {
        for (int i = 0; i < hracskePanely.length; i++)
        {
            hracskePanely[i].setStyle(
                    (i == h) ?
                            hracskePanely[i].getStyle().replace("-fx-border-width: 1", "-fx-border-width: 5")
                            :
                            hracskePanely[i].getStyle().replace("-fx-border-width: 5", "-fx-border-width: 1")
            );
        }
    }

    private void nastavitPocetFigurek0(int koho, int pocetFigurek)
    {
        StringBuilder figurkyVTextu = new StringBuilder();

        for (int i = 0; i < pocetFigurek; i++)
        {
            figurkyVTextu.append("O").append((i < pocetFigurek - 1) ? " " : "");
        }

        hracskeFigurky[koho].setText(figurkyVTextu.toString());
    }

    public void zapnoutKostku(boolean b)
    {
        kostka.setDisable(!b);
        kostka.setOpacity((b) ? 1 : 0.5);
    }

    private void obarvitKostku(String barva)
    {
        kostka.setStyle("-fx-background-radius: 0; -fx-text-fill: " + barva);
    }

    private void nastavitPole(int kde, BarvaFigurky bf, boolean b)
    {
        if (b)
        {
            policka[kde].setText("O");
            policka[kde].setTextFill(cilovaPolicka[bf.getPoradi()][0].getTextFill());
        }
        else
        {
            policka[kde].setText("");
        }
    }

    private void nastavitCilovePole(int kde, BarvaFigurky bf, boolean b)
    {
        if (b)
        {
            cilovaPolicka[bf.getPoradi()][kde].setText("O");
        }
        else
        {
            cilovaPolicka[bf.getPoradi()][kde].setText("");
        }
    }

    private void zapnoutPole(int kde)
    {
        policka[kde].setDefaultButton(true);
    }

    private void zapnoutCilovePole(int kde, BarvaFigurky bf)
    {
        cilovaPolicka[bf.getPoradi()][kde].setDefaultButton(true);
    }

    private void nastavitKonec()
    {
        konec.setDisable(false);
        konec.setDefaultButton(true);
    }

    private void zapnoutNasazeni(boolean b)
    {
        nasazeni.setDisable(!b);
        nasazeni.setDefaultButton(b);
    }

    private void vypnoutVsechnaPole()
    {
        for (Button button : policka)
        {
            button.setDefaultButton(false);
        }

        for (int i = 0; i < plocha.getPocetHracu(); i++)
        {
            for (Button button : cilovaPolicka[i])
            {
                button.setDefaultButton(false);
            }
        }
    }

    private void ukazatMoznosti()
    {
        vypnoutVsechnaPole();

        Figurka vybranaF = plocha.getVybranaFigurka();

        if (plocha.getHozeno() == 0 || vybranaF.getBarva() != plocha.getPraveHraje())
        {
            return;
        }

        int kam = vybranaF.getPozice() + plocha.getMujHod();

        if (!vybranaF.getVCili())
        {
            if (plocha.spocitanaCesta(plocha.getPraveHraje().getVstupDoCile() - vybranaF.getPozice()) >= plocha.getMujHod())
            {
                if (plocha.getCesta().get(plocha.spocitanaCesta(kam)) == null)
                {
                    zapnoutPole(plocha.spocitanaCesta(kam));
                }
                else if (plocha.getCesta().get(plocha.spocitanaCesta(kam)).getBarva() != plocha.getPraveHraje())
                {
                    zapnoutPole(plocha.spocitanaCesta(kam));
                }
            }
            else
            {
                int cestaDoCile = plocha.spocitanaCesta(kam) - 1 - plocha.getPraveHraje().getVstupDoCile();

                if (cestaDoCile < plocha.getPocetFigurek() && plocha.getCile().get(plocha.getPraveHraje()).jeVolno(cestaDoCile))
                {
                    zapnoutCilovePole(cestaDoCile, plocha.getPraveHraje());
                }
            }
        }
        else if (kam < plocha.getPocetFigurek())
        {
            if (plocha.getCile().get(plocha.getPraveHraje()).jeVolno(kam))
            {
                zapnoutCilovePole(kam, plocha.getPraveHraje());
            }
        }
    }

    private void kontrolaMoznosti() throws Exception
    {
        boolean muzuHrat = false;

        if (plocha.getDomecky().get(plocha.getPraveHraje()).getFigurkyDoma().size() != pocetFigurek)
        {
            for (int i = 0; i < plocha.getPraveHraje().getFigurkyVeHre().size(); i++)
            {
                Figurka f = plocha.getPraveHraje().getFigurkyVeHre().get(i);

                int kam = f.getPozice() + plocha.getMujHod();

                if (!f.getVCili())
                {
                    if (plocha.spocitanaCesta(plocha.getPraveHraje().getVstupDoCile() - f.getPozice()) >= plocha.getMujHod())
                    {
                        if (plocha.getCesta().get(plocha.spocitanaCesta(kam)) == null)
                        {
                            muzuHrat = true;
                        }
                        else if (plocha.getCesta().get(plocha.spocitanaCesta(kam)).getBarva() != plocha.getPraveHraje())
                        {
                            muzuHrat = true;
                        }
                    }
                    else
                    {
                        int cestaDoCile = plocha.spocitanaCesta(kam) - 1 - plocha.getPraveHraje().getVstupDoCile();

                        if (cestaDoCile < pocetFigurek && plocha.getCile().get(plocha.getPraveHraje()).jeVolno(cestaDoCile))
                        {
                            muzuHrat = true;
                        }
                    }
                }
                else if (kam < pocetFigurek)
                {
                    if (plocha.getCile().get(plocha.getPraveHraje()).jeVolno(kam))
                    {
                        muzuHrat = true;
                    }
                }
            }
        }

        if (plocha.getMujHod() == plocha.getKostka().getPocetStran() && plocha.getDomecky().get(plocha.getPraveHraje()).getFigurkyDoma().size() > 0)
        {
            if (plocha.getCesta().get(plocha.getPraveHraje().getStartovniPole()) != null)
            {
                if (plocha.getCesta().get(plocha.getPraveHraje().getStartovniPole()).getBarva() != plocha.getPraveHraje())
                {
                    muzuHrat = true;
                }
            }
            else
            {
                muzuHrat = true;
            }
        }

        if (!muzuHrat) konecTahu();
    }

    private void zacatekTahu()
    {
        plocha.zacatekTahu();
        zapnoutNasazeni(false);
        nastavitKdoHraje(plocha.getPraveHraje().getPoradi());

        if (multiplayer)
        {
            if (Server.isServer())
            {
                zapnoutKostku(plocha.getPraveHraje().getPoradi() == 0);
            }
            else
            {
                zapnoutKostku(plocha.getPraveHraje().getPoradi() == Client.getInstance().getPoradi());
            }
        }
        else
        {
            zapnoutKostku(true);
        }
    }

    private void posledniFaze() throws Exception
    {
        if (plocha.hratZnovu())
        {
            if (multiplayer)
            {
                if (Server.isServer())
                {
                    zacatekTahu();
                    Server.getInstance().zprava(new String[]{"zacatekTahu"});
                }
                else Client.getInstance().zprava(new String[]{"zacatekTahu"});
            }
            else
            {
                zacatekTahu();
            }
        }
        else
        {
            konecTahu();
        }
    }

    private void konecTahu() throws Exception
    {
        if (plocha.kdoDohral() == plocha.getPocetHracu())
        {
            if (multiplayer)
            {
                if (Server.isServer())
                {
                    Server.getInstance().zprava(new String[]{"konecHry"});
                    konecHry();
                }
                else
                {
                    Client.getInstance().zprava(new String[]{"konecHry"});
                }
            }
            else
            {
                konecHry();
            }
        }
        else
        {
            if (plocha.kdoDohral() > 0)
            {
                if (multiplayer)
                {
                    if (Server.isServer()) nastavitKonec();
                    else Client.getInstance().zprava(new String[]{"nastavitKonec"});
                }
                else
                {
                    nastavitKonec();
                }
            }

            if (multiplayer)
            {
                if (Server.isServer())
                {
                    plocha.hrajeDalsi();
                    Server.getInstance().zprava(new String[]{"hrajeDalsi"});
                    zacatekTahu();
                }
                else Client.getInstance().zprava(new String[]{"hrajeDalsi"});
            }
            else
            {
                plocha.hrajeDalsi();
                zacatekTahu();
            }
        }
    }

    @FXML
    public void konecHry() throws Exception
    {
        if (multiplayer)
        {
            if (Server.isServer())
            {
                timer.cancel();
                Server.getInstance().konec();
            }
            else
            {
                timer.cancel();
                Client.getInstance().konec();
            }
        }

        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/end.fxml")));
        Parent root = loader.load();

        ControllerEnd controllerEnd = loader.getController();
        controllerEnd.nastavitTabulku(barvy);

        Main.getMyStage().setScene(new Scene(root));
    }

    @FXML
    public void hodKostkou() throws Exception
    {
        plocha.hodKostkou();
        kostka.setText(String.valueOf(plocha.getMujHod()));
        obarvitKostku(barvy[plocha.getPraveHraje().getPoradi()]);
        zapnoutKostku(false);

        if (multiplayer)
        {
            if (Server.isServer())
            {
                Server.getInstance().zprava(new String[]{"hodKostkou", String.valueOf(plocha.getMujHod())});
            }
            else
            {
                Client.getInstance().zprava(new String[]{"hodKostkou", String.valueOf(plocha.getMujHod())});
            }
        }

        if (plocha.getDomecky().get(plocha.getPraveHraje()).getFigurkyDoma().size() == plocha.getPocetFigurek())
        {
            if (plocha.getMujHod() != plocha.getKostka().getPocetStran())
            {
                if (plocha.getHozeno() < 3)
                {
                    zapnoutKostku(true);
                }
                else
                {
                    konecTahu();
                }
            }
            else
            {
                zapnoutNasazeni(true);
            }
        }
        else if (plocha.getMujHod() == plocha.getKostka().getPocetStran() && plocha.getDomecky().get(plocha.getPraveHraje()).getFigurkyDoma().size() > 0)
        {
            if (plocha.getCesta().get(plocha.getPraveHraje().getStartovniPole()) == null || plocha.getCesta().get(plocha.getPraveHraje().getStartovniPole()).getBarva() != plocha.getPraveHraje())
            {
                zapnoutNasazeni(true);
            }
            else
            {
                kontrolaMoznosti();
            }
        }
        else
        {
            kontrolaMoznosti();
        }
    }

    @FXML
    public void nasadit()
    {
        vypnoutVsechnaPole();

        if (plocha.getCile().get(plocha.getPraveHraje()).getCil().size() == pocetFigurek)
        {
            return;
        }

        Figurka f = plocha.getCesta().get(plocha.getPraveHraje().getStartovniPole());

        if (f != null && f.getBarva() == plocha.getPraveHraje())
        {
            return;
        }
        else if (f != null && f.getBarva() != plocha.getPraveHraje())
        {
            plocha.vyhodit(plocha.getCesta().get(plocha.getPraveHraje().getStartovniPole()));
            nastavitPocetFigurek0(f.getBarva().getPoradi(), plocha.getDomecky().get(f.getBarva()).getFigurkyDoma().size());

            if (multiplayer)
            {
                if (Server.isServer())
                    Server.getInstance().zprava(new String[]{"vyhodit", String.valueOf(f.getBarva().getPoradi()), String.valueOf(plocha.getPraveHraje().getStartovniPole())});
                else
                    Client.getInstance().zprava(new String[]{"vyhodit", String.valueOf(f.getBarva().getPoradi()), String.valueOf(plocha.getPraveHraje().getStartovniPole())});
            }
        }

        plocha.nasaditFigurku(plocha.getPraveHraje());

        nastavitPole(plocha.getPraveHraje().getStartovniPole(), plocha.getPraveHraje(), true);

        nastavitPocetFigurek0(plocha.getPraveHraje().getPoradi(), plocha.getDomecky().get(plocha.getPraveHraje()).getFigurkyDoma().size());

        if (multiplayer)
        {
            if (Server.isServer())
                Server.getInstance().zprava(new String[]{"nasadit"});
            else
                Client.getInstance().zprava(new String[]{"nasadit"});
        }

        zacatekTahu();
    }

    @FXML
    public void vybratPolicko(int pole) throws Exception
    {
        if (plocha.getHozeno() == 0) return;

        Figurka f = plocha.getCesta().get(pole);
        Figurka vybranaF = plocha.getVybranaFigurka();

        if (f != null && f.getBarva().equals(plocha.getPraveHraje()))
        {
            vybranaF = f;
            plocha.setVybranaFigurka(vybranaF);
            ukazatMoznosti();
        }
        else if (vybranaF != null)
        {
            if (pole == plocha.spocitanaCesta(vybranaF.getPozice() + plocha.getMujHod()))
            {
                if (f != null && !f.getBarva().equals(plocha.getPraveHraje()))
                {
                    int puvodPozice = f.getPozice();
                    plocha.vyhodit(f);
                    nastavitPocetFigurek0(f.getBarva().getPoradi(), plocha.getDomecky().get(f.getBarva()).getFigurkyDoma().size());

                    if (multiplayer)
                    {
                        if (Server.isServer())
                            Server.getInstance().zprava(new String[]{"vyhodit", String.valueOf(f.getBarva().getPoradi()), String.valueOf(puvodPozice)});
                        else
                            Client.getInstance().zprava(new String[]{"vyhodit", String.valueOf(f.getBarva().getPoradi()), String.valueOf(puvodPozice)});
                    }
                }

                int puvodPozice = vybranaF.getPozice();

                nastavitPole(vybranaF.getPozice(), plocha.getPraveHraje(), false);

                plocha.posunoutFigurku(vybranaF, pole);

                nastavitPole(pole, plocha.getPraveHraje(), true);

                if (multiplayer)
                {
                    if (Server.isServer())
                        Server.getInstance().zprava(new String[]{
                                "posunout",
                                String.valueOf(plocha.getPraveHraje().getPoradi()),
                                String.valueOf(plocha.getPraveHraje().getFigurkyVeHre().indexOf(vybranaF)),
                                String.valueOf(puvodPozice),
                                String.valueOf(pole)
                        });
                    else
                        Client.getInstance().zprava(new String[]{
                                "posunout",
                                String.valueOf(plocha.getPraveHraje().getPoradi()),
                                String.valueOf(plocha.getPraveHraje().getFigurkyVeHre().indexOf(vybranaF)),
                                String.valueOf(puvodPozice),
                                String.valueOf(pole)
                        });
                }

                vypnoutVsechnaPole();

                posledniFaze();
            }
        }
    }

    @FXML
    public void vybratCilovePolicko(int hrac, int pole) throws Exception
    {
        if (plocha.getHozeno() == 0) return;

        Figurka vybranaF = plocha.getVybranaFigurka();

        if (plocha.getPraveHraje().getPoradi() == hrac)
        {
            if (vybranaF != null && plocha.getCile().get(plocha.getPraveHraje()).jeVolno(pole))
            {
                if (vybranaF.getVCili())
                {
                    int puvodPozice = vybranaF.getPozice();

                    nastavitCilovePole(vybranaF.getPozice(), plocha.getPraveHraje(), false);

                    plocha.posunoutFigurkuVCili(vybranaF, pole);

                    if (multiplayer)
                    {
                        if (Server.isServer())
                        {
                            Server.getInstance().zprava(new String[]{
                                    "vCili",
                                    String.valueOf(plocha.getPraveHraje().getPoradi()),
                                    String.valueOf(plocha.getPraveHraje().getFigurkyVeHre().indexOf(vybranaF)),
                                    String.valueOf(puvodPozice),
                                    String.valueOf(pole)
                            });
                        }
                        else
                        {
                            Client.getInstance().zprava(new String[]{
                                    "vCili",
                                    String.valueOf(plocha.getPraveHraje().getPoradi()),
                                    String.valueOf(plocha.getPraveHraje().getFigurkyVeHre().indexOf(vybranaF)),
                                    String.valueOf(puvodPozice),
                                    String.valueOf(pole)
                            });
                        }
                    }
                }
                else
                {
                    int odkud = vybranaF.getPozice();

                    if (plocha.spocitanaCesta(odkud + plocha.getMujHod()) - plocha.getPraveHraje().getVstupDoCile() - 1 != pole
                            || plocha.spocitanaCesta(plocha.getPraveHraje().getVstupDoCile() - odkud) >= plocha.getKostka().getPocetStran())
                    {
                        return;
                    }

                    nastavitPole(odkud, vybranaF.getBarva(), false);

                    plocha.posunoutFigurkuDoCile(vybranaF, odkud, pole);
                    vybranaF.setvCili(true);

                    if (multiplayer)
                    {
                        if (Server.isServer())
                        {
                            Server.getInstance().zprava(new String[]{
                                    "doCile",
                                    String.valueOf(plocha.getPraveHraje().getPoradi()),
                                    String.valueOf(plocha.getPraveHraje().getFigurkyVeHre().indexOf(vybranaF)),
                                    String.valueOf(odkud),
                                    String.valueOf(pole)
                            });
                        }
                        else
                        {
                            Client.getInstance().zprava(new String[]{
                                    "doCile",
                                    String.valueOf(plocha.getPraveHraje().getPoradi()),
                                    String.valueOf(plocha.getPraveHraje().getFigurkyVeHre().indexOf(vybranaF)),
                                    String.valueOf(odkud),
                                    String.valueOf(pole)
                            });
                        }
                    }
                }

                nastavitCilovePole(pole, plocha.getPraveHraje(), true);

                vypnoutVsechnaPole();

                if (plocha.getCile().get(plocha.getPraveHraje()).getCil().size() == pocetFigurek)
                {
                    konecTahu();
                }
                else
                {
                    posledniFaze();
                }
            }
            else if (!plocha.getCile().get(plocha.getPraveHraje()).jeVolno(pole))
            {
                vybranaF = plocha.getCile().get(plocha.getPraveHraje()).getCil().get(pole);
                plocha.setVybranaFigurka(vybranaF);
                ukazatMoznosti();
            }
        }
    }

    private void realtimeServer()
    {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < plocha.getPocetHracu() - 1; i++)
                {
                    try
                    {
                        if (i + 1 != plocha.getPraveHraje().getPoradi()) continue;

                        String[] strings = Server.getInstance().read(i);
                        System.out.println(Arrays.toString(strings));
                        aktualizovat(strings);
                        Server.getInstance().zprava(strings);
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }, 100, 250);
    }

    private Timer timer;

    private void realtimeClient()
    {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    String[] strings = Client.getInstance().read();
                    aktualizovat(strings);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }, 100, 250);
    }

    private void aktualizovat(String[] strings)
    {
        switch (strings[0])
        {
            case "hodKostkou":
            {
                Platform.runLater(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        kostka.setText(strings[1]);
                    }
                });
                obarvitKostku(barvy[plocha.getPraveHraje().getPoradi()]);
            }
            break;
            case "kdoHraje":
            {
                Platform.runLater(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        nastavitKdoHraje(Integer.parseInt(strings[1]));
                    }
                });
            }
            break;
            case "hrajeDalsi":
            {
                plocha.hrajeDalsi();
                zacatekTahu();
            }
            break;
            case "zacatekTahu":
            {
                zacatekTahu();
            }
            break;
            case "nasadit":
            {
                if (Server.isServer())
                {
                    if (plocha.getPraveHraje().getPoradi() == 0) break;
                }
                else if (plocha.getPraveHraje().getPoradi() == Client.getInstance().getPoradi()) break;

                Platform.runLater(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        plocha.nasaditFigurku(plocha.getPraveHraje());

                        nastavitPole(plocha.getPraveHraje().getStartovniPole(), plocha.getPraveHraje(), true);

                        nastavitPocetFigurek0(plocha.getPraveHraje().getPoradi(), plocha.getDomecky().get(plocha.getPraveHraje()).getFigurkyDoma().size());
                    }
                });
            }
            break;
            case "vyhodit":
            {
                if (Server.isServer())
                {
                    if (plocha.getPraveHraje().getPoradi() == 0) break;
                }
                else if (plocha.getPraveHraje().getPoradi() == Client.getInstance().getPoradi()) break;

                int poradi = Integer.parseInt(strings[1]);

                plocha.vyhodit(plocha.getCesta().get(Integer.parseInt(strings[2])));

                Platform.runLater(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        nastavitPocetFigurek0(poradi, plocha.getDomecky().get(plocha.getHraci().get(poradi)).getFigurkyDoma().size());
                    }
                });
            }
            break;
            case "posunout":
            {
                if (Server.isServer())
                {
                    if (plocha.getPraveHraje().getPoradi() == 0) break;
                }
                else if (plocha.getPraveHraje().getPoradi() == Client.getInstance().getPoradi()) break;

                int puvodniPole = Integer.parseInt(strings[3]);
                int pole = Integer.parseInt(strings[4]);
                Figurka vybranaF = plocha.getHraci().get(Integer.parseInt(strings[1])).getFigurkyVeHre().get(Integer.parseInt(strings[2]));

                plocha.posunoutFigurku(vybranaF, pole);

                Platform.runLater(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        nastavitPole(puvodniPole, vybranaF.getBarva(), false);

                        nastavitPole(pole, plocha.getHraci().get(Integer.parseInt(strings[1])), true);
                    }
                });
            }
            break;
            case "vCili":
            {
                if (Server.isServer())
                {
                    if (plocha.getPraveHraje().getPoradi() == 0) break;
                }
                else if (plocha.getPraveHraje().getPoradi() == Client.getInstance().getPoradi()) break;

                int puvodniPole = Integer.parseInt(strings[3]);
                int pole = Integer.parseInt(strings[4]);
                Figurka vybranaF = plocha.getHraci().get(Integer.parseInt(strings[1])).getFigurkyVeHre().get(Integer.parseInt(strings[2]));

                plocha.posunoutFigurkuVCili(vybranaF, pole);

                Platform.runLater(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        nastavitCilovePole(puvodniPole, plocha.getHraci().get(Integer.parseInt(strings[1])), false);

                        nastavitCilovePole(pole, plocha.getHraci().get(Integer.parseInt(strings[1])), true);
                    }
                });
            }
            break;
            case "doCile":
            {
                if (Server.isServer())
                {
                    if (plocha.getPraveHraje().getPoradi() == 0) break;
                }
                else if (plocha.getPraveHraje().getPoradi() == Client.getInstance().getPoradi()) break;

                int puvodniPole = Integer.parseInt(strings[3]);
                int pole = Integer.parseInt(strings[4]);
                Figurka vybranaF = plocha.getHraci().get(Integer.parseInt(strings[1])).getFigurkyVeHre().get(Integer.parseInt(strings[2]));

                plocha.posunoutFigurkuDoCile(vybranaF, puvodniPole, pole);
                vybranaF.setvCili(true);

                Platform.runLater(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        nastavitPole(puvodniPole, vybranaF.getBarva(), false);

                        nastavitCilovePole(pole, plocha.getHraci().get(Integer.parseInt(strings[1])), true);
                    }
                });
            }
            break;
            case "nastavitKonec":
            {
                if (!Server.isServer()) break;
                nastavitKonec();
            }
            break;
            case "konecHry":
            {
                Platform.runLater(new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            konecHry();
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
            break;
        }
    }
}
