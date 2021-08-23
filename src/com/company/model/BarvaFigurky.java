package com.company.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BarvaFigurky {

    private int poradi;
    public int getPoradi() {return poradi;}

    private int startovniPole;
    public int getStartovniPole() {return startovniPole;}

    private int vstupDoCile;
    public int getVstupDoCile() {return vstupDoCile;}

    private Color mojeBarva;
    public Color getMojeBarva() {return mojeBarva;}

    private List<Figurka> figurkyVeHre = new ArrayList<>();
    public List<Figurka> getFigurkyVeHre() {
        return figurkyVeHre;
    }
    public void datFigurkuDoHry(Figurka figurka) {
        figurkyVeHre.add(figurka);
    }
    public void odebratFigurkuZeHry(Figurka figurka) {
        figurkyVeHre.remove(figurka);
    }

    private boolean jsemVeHre;
    public boolean getJsemVeHre() {return jsemVeHre;}

    public BarvaFigurky(int p, int s, int v, Color c) {
        poradi = p;
        startovniPole = s;
        vstupDoCile = v;
        mojeBarva = c;
        jsemVeHre = true;
    }

    public void skoncitHru() {
        jsemVeHre = false;
        HraciPlocha.getInstance().hracDohral(getPoradi());
    }

}
