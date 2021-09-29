package com.company.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BarvaFigurky
{

    private int poradi;

    public int getPoradi()
    {
        return poradi;
    }

    private int startovniPole;

    public int getStartovniPole()
    {
        return startovniPole;
    }

    private int vstupDoCile;

    public int getVstupDoCile()
    {
        return vstupDoCile;
    }

    private Color mojeBarva;

    public Color getMojeBarva()
    {
        return mojeBarva;
    }

    private int mojeBarva0;

    public int getMojeBarva0()
    {
        return mojeBarva0;
    }

    private List<Figurka> figurkyVeHre = new ArrayList<>();

    public List<Figurka> getFigurkyVeHre()
    {
        return figurkyVeHre;
    }

    public void datFigurkuDoHry(Figurka figurka)
    {
        figurkyVeHre.add(figurka);
    }

    public void odebratFigurkuZeHry(Figurka figurka)
    {
        figurkyVeHre.remove(figurka);
    }

    private boolean jsemVeHre;

    public boolean getJsemVeHre()
    {
        return jsemVeHre;
    }

    public BarvaFigurky(int p, int s, int v, int c)
    {
        poradi = p;
        startovniPole = s;
        vstupDoCile = v;
        mojeBarva0 = c;
        jsemVeHre = true;
    }

    public void skoncitHru()
    {
        jsemVeHre = false;
        HraciPlocha1.getInstance().pridatDoTabulky(getPoradi());
    }

}
