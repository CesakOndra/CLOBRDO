package com.company.model;

public class Figurka {

    private BarvaFigurky barva;
    public BarvaFigurky getBarva() {return barva;}

    private boolean vCili;
    public boolean getVCili() { return vCili; }
    public void setvCili(boolean b) { vCili = b; }

    private int pozice;
    public int getPozice() {return pozice;}
    public void setPozice(int i) {pozice = i;}

    public Figurka(BarvaFigurky b) {
        barva = b;
        vCili = false;
    }

}
