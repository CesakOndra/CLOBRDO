package com.company.model;

import java.util.ArrayList;
import java.util.List;

public class StartovniDomecek {

    private BarvaFigurky barva;

    private List<Figurka> figurkyDoma = new ArrayList<>();

    public List<Figurka> getFigurkyDoma() {
        return figurkyDoma;
    }

    private int pocetFigurek;

    public StartovniDomecek(BarvaFigurky b, int pocFigurek) {
        barva = b;
        pocetFigurek = pocFigurek;

        for(int i = 0; i < pocetFigurek; i++) {
            figurkyDoma.add(new Figurka(barva));
        }
    }

    public Figurka nasaditFigurku() {
        Figurka nasazovanaFigurka = null;
        if (figurkyDoma.size() > 0) {
            nasazovanaFigurka = figurkyDoma.get(figurkyDoma.size() - 1);
            barva.datFigurkuDoHry(figurkyDoma.get(figurkyDoma.size() - 1));
            figurkyDoma.remove(figurkyDoma.size() - 1);
        }
        return nasazovanaFigurka;
    }

    public void vratitFigurku(Figurka vracenaFigurka) {
        if(vracenaFigurka.getBarva() == barva) {
            figurkyDoma.add(vracenaFigurka);
            barva.odebratFigurkuZeHry(vracenaFigurka);
        }
    }

}
