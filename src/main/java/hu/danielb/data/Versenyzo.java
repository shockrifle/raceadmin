/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.danielb.data;

/**
 * @author danielb
 */
public class Versenyzo {

    int id;
    int helyezes;
    String nev;
    String nem;
    int rajtszam;
    Korosztaly korosztaly;
    Iskola iskola;
    int kor;

    /**
     * @param id
     * @param helyezes
     * @param nev
     * @param nem
     * @param rajtszam
     * @param korosztaly
     * @param iskola
     * @param kor
     */
    public Versenyzo(int id, int helyezes, String nev, String nem, int rajtszam, Korosztaly korosztaly, Iskola iskola, int kor) {
        this.id = id;
        this.helyezes = helyezes;
        this.nev = nev;
        this.nem = nem;
        this.rajtszam = rajtszam;
        this.korosztaly = korosztaly;
        this.iskola = iskola;
        this.kor = kor;
    }

    public int getId() {
        return id;
    }

    public int getHelyezes() {
        return helyezes;
    }

    public String getNev() {
        return nev;
    }

    public String getNem() {
        return nem;
    }

    public int getRajtszam() {
        return rajtszam;
    }

    public Korosztaly getKorosztaly() {
        return korosztaly;
    }

    public Iskola getIskola() {
        return iskola;
    }

    public int getKor() {
        return kor;
    }
}
