package com.company.model;

import java.util.Random;

public class Kostka {

    private int pocetStran;
    public int getPocetStran() { return pocetStran; }

    public Kostka(int ps) {
        pocetStran = ps;
    }

    public int hod() {
        return new Random().nextInt(pocetStran) + 1;
    }

}
