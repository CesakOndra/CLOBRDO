package com.company.model;

import java.util.HashMap;
import java.util.Map;

public class CilovyDomecek {

    private BarvaFigurky barva;

    private Map<Integer, Figurka> cil;
    public Map<Integer, Figurka> getCil() {return new HashMap<>(cil);}

    private int pocetFigurek;

    public CilovyDomecek(BarvaFigurky b, int pocFigurek) {
        barva = b;
        pocetFigurek = pocFigurek;

        cil = new HashMap<>(pocetFigurek);
    }

    public boolean jeVolno(int kde) {
        return !cil.containsKey(kde);
    }

    public void jitDoCile(Figurka kdo, int kam) {
        cil.put(kam, kdo);
        kdo.setPozice(kam);
        if(cil.size() == pocetFigurek) {
            barva.skoncitHru();
        }
    }

    public void posunVCili(Figurka kdo, int odkud, int kam) {
        cil.put(kam, kdo);
        cil.remove(odkud);
        kdo.setPozice(kam);
    }

}
