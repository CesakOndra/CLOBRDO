package com.company.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HraciPlocha1
{
//region Singleton

    private static HraciPlocha1 plocha = null;

    public static HraciPlocha1 getInstance()
    {
        if (plocha == null) plocha = new HraciPlocha1();
        return plocha;
    }

    public void novaHraciPlocha()
    {
        plocha = new HraciPlocha1();
    }

    //endregion

    // Hraci kostka
    private Kostka kostka;

    public Kostka getKostka()
    {
        return kostka;
    }

    // List policek
    private Map<Integer, Figurka> cesta = new HashMap<>();

    public Map<Integer, Figurka> getCesta()
    {
        return cesta;
    }

    // Pocet vsech policek
    private int delkaCesty;

    // List hracu
    private List<BarvaFigurky> hraci = new ArrayList<>();

    public List<BarvaFigurky> getHraci()
    {
        return hraci;
    }

    // Tabulka sestupne jdoucich hraci, kteri dohrali
    private List<Integer> tabulka = new ArrayList<>();

    public int[] getTabulka()
    {
        int[] tab = new int[tabulka.size()];
        for (int i = 0; i < tab.length; i++)
        {
            tab[i] = tabulka.get(i);
        }
        return tab;
    }

    // Barva prave hrajiciho hrace
    private BarvaFigurky praveHraje;

    public BarvaFigurky getPraveHraje()
    {
        return praveHraje;
    }

    // Pocet hracu ve hre
    private int pocetHracu;

    public int getPocetHracu()
    {
        return pocetHracu;
    }

    // Pocet figurek, ktere ma kazdy z hracu
    private int pocetFigurek;

    public int getPocetFigurek()
    {
        return pocetFigurek;
    }

    // Domecky odkud se nasazuji figurky
    private Map<BarvaFigurky, StartovniDomecek> domecky = new HashMap<>();

    public Map<BarvaFigurky, StartovniDomecek> getDomecky()
    {
        return domecky;
    }

    // Policka se snazi hraci dojit se svymi figurkami
    private Map<BarvaFigurky, CilovyDomecek> cile = new HashMap<>();

    public Map<BarvaFigurky, CilovyDomecek> getCile()
    {
        return cile;
    }

    // Prave vybrana figurka (se stejnou barvou)
    private Figurka vybranaFigurka;

    public Figurka getVybranaFigurka()
    {
        return vybranaFigurka;
    }

    public void setVybranaFigurka(Figurka vybranaFigurka)
    {
        this.vybranaFigurka = vybranaFigurka;
    }

    // Hodnota na kostce
    private int mujHod;

    public int getMujHod()
    {
        return mujHod;
    }

    // Kolikrat bylo hozeno za tento tah
    private int hozeno;

    public int getHozeno()
    {
        return hozeno;
    }

    public void pripravitHru(int pocetH, int stenyNaKostce, int delkaC, int pocetF)
    {
        pocetHracu = pocetH;
        pocetFigurek = pocetF;
        delkaCesty = pocetHracu * delkaC;

        kostka = new Kostka(stenyNaKostce);

        for (int h = 0; h < pocetH; h++)
        {
            int start = h * delkaC + 1;
            int cilVstup = h * delkaC;

            BarvaFigurky novyHrac = new BarvaFigurky(h, start, cilVstup, h);

            hraci.add(novyHrac);

            domecky.put(novyHrac, new StartovniDomecek(novyHrac, pocetFigurek));
            cile.put(novyHrac, new CilovyDomecek(novyHrac, pocetFigurek));
        }
    }

    public void zacitHru()
    {
        // navrh do nastaveni: moznost nahodneho zacinajiciho hrace
        int zacinajiciHrac = 0;
        praveHraje = hraci.get(zacinajiciHrac);
    }

    public void hodKostkou()
    {
        mujHod = kostka.hod();
        hozeno++;
    }

    public boolean hratZnovu()
    {
        return mujHod == kostka.getPocetStran();
    }

    public int spocitanaCesta(int kam)
    {
        if (kam < 0)
        {
            kam += delkaCesty;
        }
        else if (kam > delkaCesty - 1)
        {
            kam -= delkaCesty;
        }

        return kam;
    }

    public void zacatekTahu()
    {
        vybranaFigurka = null;
        hozeno = 0;
    }

    public void pridatDoTabulky(int h)
    {
        tabulka.add(h);
    }

    public void posunoutFigurku(Figurka jaka, int kam)
    {
        int odkud = jaka.getPozice();
        cesta.put(kam, jaka);
        cesta.remove(odkud);
        jaka.setPozice(kam);
    }

    public void posunoutFigurkuVCili(Figurka kdo, int kam)
    {
        cile.get(praveHraje).posunVCili(vybranaFigurka, kam - mujHod, kam);
    }

    public void posunoutFigurkuDoCile(Figurka kdo, int odkud, int kam)
    {
        cile.get(praveHraje).jitDoCile(kdo, kam);
        cesta.remove(odkud);
    }

    public void nasaditFigurku(BarvaFigurky bf)
    {
        Figurka nasazenaF = domecky.get(bf).nasaditFigurku();
        cesta.put(bf.getStartovniPole(), nasazenaF);
        nasazenaF.setPozice(bf.getStartovniPole());
    }

    public void vyhodit(Figurka kdo)
    {
        int kde = kdo.getPozice();
        domecky.get(kdo.getBarva()).vratitFigurku(kdo);

        cesta.remove(kde);
    }

    public int kdoDohral()
    {
        int hraciKteriDohrali = 0;

        for (BarvaFigurky b : hraci)
        {
            if (!b.getJsemVeHre()) hraciKteriDohrali++;
        }

        return hraciKteriDohrali;
    }

    public void hrajeDalsi()
    {
        int ph = praveHraje.getPoradi() + 1;
        praveHraje = hraci.get((ph < hraci.size()) ? ph : 0);
        if (!praveHraje.getJsemVeHre())
        {
            hrajeDalsi();
        }
    }
}
