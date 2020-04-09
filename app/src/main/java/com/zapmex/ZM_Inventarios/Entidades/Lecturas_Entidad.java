package com.zapmex.ZM_Inventarios.Entidades;

public class Lecturas_Entidad {

    private int identificador;
    private long codigoBarras;

    public Lecturas_Entidad(String string){

    }

    public Lecturas_Entidad(int identificador, long codigoBarras) {
        this.identificador = identificador;
        this.codigoBarras = codigoBarras;
    }

    public int getIdentificador() {
        return identificador;
    }

    public void setIdentificador(int identificador) {
        this.identificador = identificador;
    }

    public long getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(long codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    @Override
    public String toString(){
        return  "ID= " + identificador + " CB= "+ codigoBarras;
    }

}

