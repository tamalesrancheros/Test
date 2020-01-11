package com.example.test2.Utilidad;

public class Utilidades {

    //Constantes campos tabla barcode que se van a tomar en el inventario
    public static final String TABLA_INVTBARCODE="invtBarcode";
    public static final String CAMPO_ID="idInteger";
    public static final String CAMPO_BARCODE="barcode";


    //Constantes campo<s tabla barcode QUE SE ALMACENAN PARA COMPARAR con los de toma
    public static final String TABLA_INVTFIJO="invtFijo";
    public static final String CAMPO_BARCODEFIJO="barcodeFijo";
    public static final String CAMPO_MARCAFIJO="marcaFijo";
    public static final String CAMPO_ESTILOFIJO="estiloFijo";
    public static final String CAMPO_TALLAFIJO="tallaFijo";


    //CREACION DE LAS TABLAS EN EL ARCHIVO
    public static final String CREAR_TABLA_INVTBARCODE="CREATE TABLE "+
            TABLA_INVTBARCODE+" ("+CAMPO_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CAMPO_BARCODE+" INTEGER NOT NULL)";

    public static final String CREAR_TABLA_INVTFIJO="CREATE TABLE "+TABLA_INVTFIJO+
            " ("+CAMPO_BARCODEFIJO+ " INTEGER, "+CAMPO_MARCAFIJO+ " VARCHAR, "+CAMPO_ESTILOFIJO+
            " VARCHAR, "+CAMPO_TALLAFIJO+ " VARCHAR)";
}
