package com.company.model;

import com.company.app.Main;
import javafx.scene.control.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HraciPlocha {

    //region Singleton

    private static HraciPlocha plocha = null;

    public static HraciPlocha getInstance() {
        if(plocha == null) plocha = new HraciPlocha(); return plocha;
    }

    //endregion

    private Kostka kostka;

    private Map<Integer, Figurka> cesta = new HashMap<>();
    private int delkaCesty;

    private List<BarvaFigurky> hraci = new ArrayList<>();

    private List<Integer> tabulka = new ArrayList<>();

    private BarvaFigurky praveHraje;

    private int pocetHracu;

    private int pocetFigurek;

    private Map<BarvaFigurky, StartovniDomecek> domecky = new HashMap<>();
    private Map<BarvaFigurky, CilovyDomecek> cile = new HashMap<>();

    private Figurka vybranaFigurka;

    public void zacitHru(int pocetH, int stenyNaKostce, int delkaC, int pocetF) {

        pocetHracu = pocetH;
        pocetFigurek = pocetF;
        delkaCesty = pocetHracu * delkaC;

        kostka = new Kostka(stenyNaKostce);

        Main.getController().initCilovaPolicka();
        Main.vytvoritHry(pocetHracu, delkaC, pocetF);

        for (int h = 0; h < pocetH; h++) {

            int start = h * delkaC + 1;
            int cilVstup = h * delkaC;

            BarvaFigurky novyHrac = new BarvaFigurky(h, start, cilVstup,
                    Color.getColor(Main.barvy[h].replace("#", "0x"))
            );

            hraci.add(novyHrac);

            domecky.put(novyHrac, new StartovniDomecek(novyHrac, pocetFigurek));
            cile.put(novyHrac, new CilovyDomecek(novyHrac, pocetFigurek));

            Main.getController().nastavitPocetFigurek(h, pocetFigurek);
        }

        spustitHru();
    }

    private void spustitHru() { // navrh do nastaveni: moznost nahodneho zacinajiciho hrace
        praveHraje = hraci.get(0);
        nastavitKdoHraje(0);

        zacatekTahu();
    }

    private int mujHod;
    private int kolikratHozeno;
    private boolean hozeno;

    public int hodKostkou() {

        if(hozeno) return mujHod;

        mujHod = kostka.hod();
        hozeno = true;
        kolikratHozeno++;

        Main.getController().obarvitKostku(Main.barvy[praveHraje.getPoradi()]);
        Main.getController().zapnoutKostku(false);

        if(domecky.get(praveHraje).getFigurkyDoma().size() == pocetFigurek) {
            if(mujHod != kostka.getPocetStran()) {
                if(kolikratHozeno < 3) {
                    hozeno = false;
                    Main.getController().zapnoutKostku(true);
                }
                else {
                    konecTahu();
                }
            }
            else {
                Main.getController().zapnoutNasazeni(true);
            }
        }
        else if(mujHod == kostka.getPocetStran() && domecky.get(praveHraje).getFigurkyDoma().size() > 0) {
            if(cesta.get(praveHraje.getStartovniPole()) == null || cesta.get(praveHraje.getStartovniPole()).getBarva() != praveHraje) {
                Main.getController().zapnoutNasazeni(true);
            }
            else kontrolaMoznosti();
        }
        else kontrolaMoznosti();

        return mujHod;
    }

    private void posledniFaze() {
        if(mujHod == kostka.getPocetStran()) {
            zacatekTahu();
        }
        else {
            if(Main.getController().autoKonecTahu.isSelected()) konecTahu();
            else Main.getController().nastavitKonec(true);
        }
    }

    public void konecTahu() {

        int hraciCoDohrali = 0;

        for (BarvaFigurky b : hraci) {
            if(!b.getJsemVeHre()) hraciCoDohrali++;
        }

        if(hraciCoDohrali == pocetHracu) {
            konecHry();
            return;
        }

        int ph = praveHraje.getPoradi() + 1;
        praveHraje = hraci.get((ph < hraci.size()) ? ph : 0);

        if(!praveHraje.getJsemVeHre()) {
            konecTahu();
            return;
        }

        zacatekTahu();
    }

    private void zacatekTahu() {
        vybranaFigurka = null;
        hozeno = false;
        kolikratHozeno = 0;

        Main.getController().zapnoutNasazeni(false);
        Main.getController().zapnoutPosunuti(false);
        Main.getController().zapnoutKostku(true);
        nastavitKdoHraje(praveHraje.getPoradi());
    }

    private void konecHry() {

        int[] tabulkaHracu = new int[pocetHracu];

        for (int i = 0; i < pocetHracu; i++) {
            tabulkaHracu[i] = tabulka.get(i);
        }

        Main.ukoncitHru(tabulkaHracu);
    }

    public void hracDohral(int i) {
        tabulka.add(i);
    }

    private int spocitanaCesta(int kam) {
        if(kam < 0) {
            kam += delkaCesty;
        }
        else if(kam > delkaCesty - 1) {
            kam -= delkaCesty;
        }

        return kam;
    }

    private void posunFigurky(Figurka jaka, int kam) {
        int odkud = jaka.getPozice();
        cesta.put(kam, jaka);
        cesta.remove(odkud);
        jaka.setPozice(kam);
    }

    private void vyhodit(Figurka jaka, int kde) {

        domecky.get(jaka.getBarva()).vratitFigurku(jaka);

        Main.getController().nastavitPocetFigurek(jaka.getBarva().getPoradi(), domecky.get(jaka.getBarva()).getFigurkyDoma().size());

        Main.getController().nastavitPocetFigurek(praveHraje.getPoradi(),
                domecky.get(jaka.getBarva()).getFigurkyDoma().size());

        cesta.remove(kde);
    }

    //region Output metody

    public void nastavitKdoHraje(int h) {
        for (int i = 0; i < Main.getController().getHracskePanely().length; i++) {
            Main.getController().getHracskePanely()[i].setStyle(
                    (i == h) ?
                            Main.getController().getHracskePanely()[i].getStyle().replace("-fx-border-width: 3", "-fx-border-width: 10")
                            :
                            Main.getController().getHracskePanely()[i].getStyle().replace("-fx-border-width: 10", "-fx-border-width: 3")
            );
        }
    }

    public void zapnoutPole(int kde, boolean b) {
        Main.getController().getPolicka()[kde].setDefaultButton(b);
    }

    public void zapnoutCilovePole(BarvaFigurky bf, int kde, boolean b) {
        Main.getController().getCilovaPolicka()[bf.getPoradi()][kde].setDefaultButton(b);
    }

    public void vypnoutVsechnaPole() {
        for (Button b : Main.getController().getPolicka()) {
            b.setDefaultButton(false);
        }

        for (int i = 0; i < pocetHracu; i++) {
            for (Button b : Main.getController().getCilovaPolicka()[i]) {
                b.setDefaultButton(false);
            }
        }
    }

    public void nastavitPole(int kde, BarvaFigurky bf, boolean b) {
        if(b) {
            Main.getController().getPolicka()[kde].setText("O");
/*
            double[] doubles = new double[4];
            doubles[0] = Color.getColor(Main.barvy[0].replace("#", "0x")).getRed();
            doubles[1] = Color.getColor(Main.barvy[0].replace("#", "0x")).getGreen();
            doubles[2] = Color.getColor(Main.barvy[0].replace("#", "0x")).getBlue();
            doubles[3] = Color.getColor(Main.barvy[0].replace("#", "0x")).getAlpha();
            javafx.scene.paint.Paint paint = new javafx.scene.paint.Color(doubles[0], doubles[1], doubles[2], doubles[3]);*/
            Main.getController().getPolicka()[kde].setTextFill(Main.getController().getCilovaPolicka()[bf.getPoradi()][0].getTextFill());
        }
        else {
            Main.getController().getPolicka()[kde].setText("");
        }
    }

    public void nastavitCilovePole(int kde, BarvaFigurky bf, boolean b) {
        if(b) {
            Main.getController().getCilovaPolicka()[bf.getPoradi()][kde].setText("O");
        }
        else {
            Main.getController().getCilovaPolicka()[bf.getPoradi()][kde].setText("");
        }
    }

    //endregion

    //region Input metody

    public void vybratPole(int pole) {
        if(!hozeno) return;

        Figurka f = cesta.get(pole);

        if(f == null) {
            if(vybranaFigurka != null) {
                if(pole == spocitanaCesta(vybranaFigurka.getPozice() + mujHod)) {

                    nastavitPole(vybranaFigurka.getPozice(), praveHraje, false);

                    posunFigurky(vybranaFigurka, pole);

                    nastavitPole(pole, praveHraje, true);

                    posledniFaze();

                    vypnoutVsechnaPole();
                }
            }
        }
        else if(f.getBarva().equals(praveHraje)) {
            vybranaFigurka = f;
            Main.getController().zapnoutPosunuti(true);
            ukazatMoznosti();
        }
        else if(vybranaFigurka != null) {
            if(pole == spocitanaCesta(vybranaFigurka.getPozice() + mujHod)) {
                vyhodit(f, pole);

                nastavitPole(vybranaFigurka.getPozice(), praveHraje, false);

                posunFigurky(vybranaFigurka, pole);

                nastavitPole(pole, praveHraje, true);

                posledniFaze();

                vypnoutVsechnaPole();
            }
        }
    }

    public void vybratCilovePole(int poradi, int pole) {

        vypnoutVsechnaPole();

        if(!hozeno) return;

        if(poradi == praveHraje.getPoradi()) {
            if(vybranaFigurka != null && cile.get(praveHraje).jeVolno(pole)) {
                if(vybranaFigurka.getVCili()) {
                    nastavitCilovePole(vybranaFigurka.getPozice(), praveHraje, false);

                    cile.get(praveHraje).posunVCili(vybranaFigurka, pole - mujHod, pole);

                    nastavitCilovePole(vybranaFigurka.getPozice(), praveHraje, true);
                }
                else {
                    int odkud = vybranaFigurka.getPozice();

                    if(spocitanaCesta(odkud + mujHod) - praveHraje.getVstupDoCile() - 1 != pole || spocitanaCesta(praveHraje.getVstupDoCile() - odkud) >= kostka.getPocetStran())
                        return;

                    nastavitPole(odkud, vybranaFigurka.getBarva(), false);

                    cile.get(praveHraje).jitDoCile(vybranaFigurka, pole);
                    vybranaFigurka.setvCili(true);

                    nastavitCilovePole(vybranaFigurka.getPozice(), praveHraje, true);

                    cesta.remove(odkud);
                }

                posledniFaze();
            }
            else if (!cile.get(praveHraje).jeVolno(pole)) {

                vybranaFigurka = cile.get(praveHraje).getCil().get(pole);
                Main.getController().zapnoutPosunuti(true);
                ukazatMoznosti();
            }
        }
    }

    public void nasaditFigurku() {

        vypnoutVsechnaPole();

        if(cesta.get(praveHraje.getStartovniPole()) != null) {
            if(cesta.get(praveHraje.getStartovniPole()).getBarva() != praveHraje) {

                vyhodit(cesta.get(praveHraje.getStartovniPole()), praveHraje.getStartovniPole());

                Figurka nasazenaFig = domecky.get(praveHraje).nasaditFigurku();

                if(nasazenaFig != null) {
                    cesta.put(praveHraje.getStartovniPole(), nasazenaFig);

                    nasazenaFig.setPozice(praveHraje.getStartovniPole());

                    nastavitPole(praveHraje.getStartovniPole(), praveHraje, true);

                    zacatekTahu();
                }
            }
        }
        else {
            Figurka nasazenaFig = domecky.get(praveHraje).nasaditFigurku();

            if(nasazenaFig != null) {
                cesta.put(praveHraje.getStartovniPole(), nasazenaFig);

                nasazenaFig.setPozice(praveHraje.getStartovniPole());

                nastavitPole(praveHraje.getStartovniPole(), praveHraje, true);

                zacatekTahu();
            }
        }

        Main.getController().nastavitPocetFigurek(praveHraje.getPoradi(), domecky.get(praveHraje).getFigurkyDoma().size());

        Main.getController().zapnoutNasazeni(false);
    }

    private void ukazatMoznosti() {

        vypnoutVsechnaPole();

        if(!hozeno || vybranaFigurka.getBarva() != praveHraje) return;

        int kam = vybranaFigurka.getPozice() + mujHod;

        if(!vybranaFigurka.getVCili()) {
            if(spocitanaCesta(praveHraje.getVstupDoCile() - vybranaFigurka.getPozice()) >= mujHod) {
                if(cesta.get(spocitanaCesta(kam)) == null) {

                    zapnoutPole(spocitanaCesta(kam), true);
                }
                else if (cesta.get(spocitanaCesta(kam)).getBarva() != praveHraje){

                    zapnoutPole(spocitanaCesta(kam), true);
                }
            }
            else {
                int cestaDoCile = spocitanaCesta(kam) - 1 - praveHraje.getVstupDoCile();

                if(cestaDoCile < pocetFigurek && cile.get(praveHraje).jeVolno(cestaDoCile)) {

                    zapnoutCilovePole(praveHraje, cestaDoCile, true);
                }
            }
        }
        else if(kam < pocetFigurek) {
            if(cile.get(praveHraje).jeVolno(kam)) {

                zapnoutCilovePole(praveHraje, kam, true);
            }
        }
    }

    public void posunout() {

        if(!hozeno || vybranaFigurka.getBarva() != praveHraje || vybranaFigurka == null) return;

        int kam = vybranaFigurka.getPozice() + mujHod;

        if(!vybranaFigurka.getVCili()) {
            if(spocitanaCesta(praveHraje.getVstupDoCile() - vybranaFigurka.getPozice()) >= mujHod) {
                if(cesta.get(spocitanaCesta(kam)) == null) {

                    nastavitPole(vybranaFigurka.getPozice(), praveHraje, false);

                    posunFigurky(vybranaFigurka, spocitanaCesta(kam));

                    nastavitPole(spocitanaCesta(kam), praveHraje, true);

                    posledniFaze();
                }
                else if (cesta.get(spocitanaCesta(kam)).getBarva() != praveHraje){

                    vyhodit(cesta.get(spocitanaCesta(kam)), spocitanaCesta(kam));

                    nastavitPole(vybranaFigurka.getPozice(), praveHraje, false);

                    posunFigurky(vybranaFigurka, spocitanaCesta(kam));

                    nastavitPole(spocitanaCesta(kam), praveHraje, true);

                    posledniFaze();
                }
            }
            else {
                int cestaDoCile = spocitanaCesta(kam) - 1 - praveHraje.getVstupDoCile();

                if(cestaDoCile < pocetFigurek && cile.get(praveHraje).jeVolno(cestaDoCile)) {

                    int odkud = vybranaFigurka.getPozice();

                    if(spocitanaCesta(odkud + mujHod) - praveHraje.getVstupDoCile() - 1 != cestaDoCile || spocitanaCesta(praveHraje.getVstupDoCile() - odkud) >= kostka.getPocetStran())
                        return;

                    nastavitPole(odkud, vybranaFigurka.getBarva(), false);

                    cile.get(praveHraje).jitDoCile(vybranaFigurka, cestaDoCile);
                    vybranaFigurka.setvCili(true);

                    nastavitCilovePole(vybranaFigurka.getPozice(), praveHraje, true);

                    cesta.remove(odkud);
                }

                posledniFaze();
            }
        }
        else if(kam < pocetFigurek) {
            if(cile.get(praveHraje).jeVolno(kam)) {

                nastavitCilovePole(vybranaFigurka.getPozice(), praveHraje, false);

                cile.get(praveHraje).posunVCili(vybranaFigurka, kam - mujHod, kam);

                nastavitCilovePole(vybranaFigurka.getPozice(), praveHraje, true);

                posledniFaze();
            }
        }

        vypnoutVsechnaPole();
    }

    //endregion

    void kontrolaMoznosti() {

        boolean muzuHrat = false;

        if(domecky.get(praveHraje).getFigurkyDoma().size() != pocetFigurek) {
            for (int i = 0; i < praveHraje.getFigurkyVeHre().size(); i++) {

                Figurka f = praveHraje.getFigurkyVeHre().get(i);

                int kam = f.getPozice() + mujHod;

                if(!f.getVCili()) {
                    if(spocitanaCesta(praveHraje.getVstupDoCile() - f.getPozice()) >= mujHod) {
                        if(cesta.get(spocitanaCesta(kam)) == null) {

                            muzuHrat = true;
                        }
                        else if (cesta.get(spocitanaCesta(kam)).getBarva() != praveHraje){

                            muzuHrat = true;
                        }
                    }
                    else {
                        int cestaDoCile = spocitanaCesta(kam) - 1 - praveHraje.getVstupDoCile();

                        if(cestaDoCile < pocetFigurek && cile.get(praveHraje).jeVolno(cestaDoCile)) {

                            muzuHrat = true;
                        }
                    }
                }
                else if(kam < pocetFigurek) {
                    if(cile.get(praveHraje).jeVolno(kam)) {

                        muzuHrat = true;
                    }
                }
            }
        }

        if (mujHod == kostka.getPocetStran() && domecky.get(praveHraje).getFigurkyDoma().size() > 0) {
            if(cesta.get(praveHraje.getStartovniPole()) != null) {
                if(cesta.get(praveHraje.getStartovniPole()).getBarva() != praveHraje) {
                    muzuHrat = true;
                }
            }
            else {
                muzuHrat = true;
            }
        }


        if(!muzuHrat) konecTahu();
    }
}
