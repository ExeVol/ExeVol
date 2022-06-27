package com.example.vaadbaitv3;

public class seker {
    private String nosee;
    private String explain;
    private int baad;
    private int neged;

    @Override
    public String toString() {
        return "seker{" +
                "nosee='" + nosee + '\'' +
                ", explain='" + explain + '\'' +
                ", baad=" + baad +
                ", neged=" + neged +
                '}';
    }
    public seker() {
    }
    public seker(String nosee, String explain, int baad, int neged) {
        this.nosee = nosee;
        this.explain = explain;
        this.baad = baad;
        this.neged = neged;
    }

    public String getNosee() {
        return nosee;
    }

    public void setNosee(String nosee) {
        this.nosee = nosee;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public int getBaad() {
        return baad;
    }

    public void setBaad(int baad) {
        this.baad = baad;
    }

    public int getNeged() {
        return neged;
    }

    public void setNeged(int neged) {
        this.neged = neged;
    }
}
