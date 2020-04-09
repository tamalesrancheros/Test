package com.zapmex.ZM_Inventarios;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zapmex.ZM_Inventarios.Entidades.Lecturas_Entidad;
import com.zapmex.ZM_Inventarios.Utilidad.Utilidades;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {
    private EditText txtCB;
    private TextView lblMensaje; // conteo

    private ListView lstLista;
    private ArrayList<Lecturas_Entidad> items_arreglo;  //cambia de String a Lecturas_Entidad
    private ArrayAdapter adaptador_items;

    private Button botAgregar;
    int bandera_enter, bandera_limpiar = 0; // BANDERAS DE PASO PARA EL "ENTER"
    public int vcontador = 0;
    public int valor = 0;

    List<Lecturas_Entidad> listaUsuarios = new ArrayList<>();

    Button btnExportar;

    ConexionSQLite conn;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCB = (EditText) findViewById(R.id.txtCodigodeBarras);
        lblMensaje = (TextView) findViewById(R.id.lblMensaje);

        lstLista = (ListView) findViewById(R.id.lstLista);
        botAgregar = (Button) findViewById(R.id.botAgregar);  //BOTON DE AGREGAR - DECLARACION
        btnExportar = findViewById(R.id.btnExportar);

        final ArrayList<Lecturas_Entidad> items_arreglo = new ArrayList<Lecturas_Entidad>(); //array de barcodes //cambia de String a Lecturas_Entidad

        adaptador_items = new ArrayAdapter(MainActivity.this, R.layout.support_simple_spinner_dropdown_item, items_arreglo);
        lstLista.setAdapter(adaptador_items);

        pedirPermisos();
        obtenerUsuarios();

        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                exportarCSV();
            }
        });

        // Metodo para consultar la base / mostrar el contendio ya escaneado
        //mostrar();


        txtCB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bandera_enter=charSequence.toString().indexOf("nn");
                if (bandera_enter>-1 && bandera_limpiar==0){
                    bandera_limpiar=1;

                    txtCB.setText(txtCB.getText().toString().replace("nn", ""));

                    //Consulta_BD si el barcode esta en la base
                    if (consultar())
                    {
                            int valor1 = 0;
                            long valor2 = 0;

                            valor1=registrarBarcode();
                            valor2=Long.parseLong(txtCB.getText().toString().replace("nn", ""));
                            //añade el barcode al tope del list view
                            // items_arreglo.add(0,new Lecturas_Entidad(valor1, valor2));
                            // lstLista.setAdapter(adaptador_items);
                            // adaptador_items.notifyDataSetChanged();

                            vcontador++;
                            lblMensaje.setText(Integer.toString(vcontador)); //se sube el contador de la lista

                            //Toast.makeText(MainActivity.this, "Barcode Agregado", Toast.LENGTH_LONG).show();
                            limpiar();
                    }else{
                        Toast.makeText(MainActivity.this, "NO ENCONTRADO", Toast.LENGTH_LONG).show();

                        //Reproduce un sonido de alerta cuando no encuentra el BC en la BD
                        MediaPlayer mediaplayer = MediaPlayer.create(MainActivity.this, R.raw.alerta);
                        mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                mediaPlayer.reset();
                                mediaPlayer.release();
                            }
                        });
                        mediaplayer.start();

                        // Alerta de BARCODE NO ENCONTRADO
                        AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                        alerta.setMessage("No se encontro el codigo de barras, para continuar presione OK")
                                .setPositiveButton("   OK   ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //
                                    }
                                });
                        AlertDialog titulo = alerta.create();
                        titulo.setTitle("¡ALERTA!");
                        titulo.show();
                        limpiar();
                    }

                    bandera_enter=0;
                    bandera_limpiar=0;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // metodo para borrar un item de la list view
        // Ademas de que borra el BC de la base de acuerdo al ListView Seleccionado
        // OJO EL METODO DE ELIMINACION ES eliminarUsuario (RENOMBRAR)
        lstLista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                valor = i;
                AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                alerta.setMessage("¿Desea borrar este codigo?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Lecturas_Entidad p = items_arreglo.get(valor);

                                //INVOCA Eliminar Usuario.
                                eliminarUsuario(p.getIdentificador());

                                items_arreglo.remove(valor);
                                adaptador_items.notifyDataSetChanged();
                                vcontador--;
                                lblMensaje.setText(Integer.toString(vcontador));

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog titulo = alerta.create();
                titulo.setTitle("Eliminar codigo de barras");
                titulo.show();
                return false;
            }
        });
    }


    //METODO PARA PERMISOS DE ALMACENAMIENTO DENTRO DEL TELEFONO
    private void pedirPermisos() {
        if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }


    private void obtenerUsuarios() {
        listaUsuarios.clear();

        ConexionSQLite admin = new ConexionSQLite(MainActivity.this, "bd_inventario", null, 1);

        SQLiteDatabase db = admin.getWritableDatabase();

        Cursor fila = db.rawQuery("select barcode from invtBarcode", null);

        if(fila != null && fila.getCount() != 0) {
            fila.moveToFirst();
            do{
                listaUsuarios.add(
                        new Lecturas_Entidad(
                                fila.getString(0)));
            } while (fila.moveToNext());
        } else {
            Toast.makeText(this, "NO HAY REGISTROS", Toast.LENGTH_SHORT).show();
        }
        db.close();

    }

    private void exportarCSV() {
        File carpeta = new File(Environment.getExternalStorageDirectory() + "/CSV");
        String archivo = carpeta.toString() + "/" + "Colectora.csv";

        boolean isCreate = false;
        if(!carpeta.exists()){
            isCreate = carpeta.mkdir();
        }
        try{
            FileWriter fileWriter = new FileWriter(archivo);

            ConexionSQLite admin = new ConexionSQLite(MainActivity.this, "bd_inventario", null, 1);
            SQLiteDatabase db = admin.getWritableDatabase();

            Cursor fila = db.rawQuery("select barcode from invtBarcode", null);
            if(fila !=null && fila.getCount() !=0){
                fila.moveToFirst();
                do{
                    fileWriter.append(fila.getString(0));
                    fileWriter.append("\n");
                } while (fila.moveToNext());
            } else {
                Toast.makeText(this, "NO HAY REGISTROS", Toast.LENGTH_SHORT).show();
            }

            db.close();
            fileWriter.close();
            Toast.makeText(this, "Se creo exitosamente el archivo CSV", Toast.LENGTH_SHORT).show();

        } catch(Exception e) {

        }

    }

    //METODO para Registrar BC escaneados
    private int registrarBarcode() {
        ConexionSQLite conn=new ConexionSQLite(this,"bd_inventario",null,1);

        SQLiteDatabase db=conn.getWritableDatabase();

        ContentValues values=new ContentValues();
        values.put(Utilidades.CAMPO_BARCODE,txtCB.getText().toString());
        //values.put(Utilidades.CAMPO_ID,vcontador);

        Long idResultante=db.insert(Utilidades.TABLA_INVTBARCODE,Utilidades.CAMPO_BARCODE,values);

        //consulta de ultimo añadido
        int ultimo_registro= consultarSql();

        db.close();
        return ultimo_registro;
    }

    //METODO para Verificar si el BC esta en la base de la Tienda
    private boolean  consultar() { //PARA CONSULTAR SI EL CB ES PARTE DE LOS CODIGOS DE BARRA DE LA TIENDA
        ConexionSQLite conn=new ConexionSQLite(this,"bd_inventario",null,1);

        SQLiteDatabase db=conn.getReadableDatabase();
        String[] parametros={txtCB.getText().toString()};
        String[] campos={Utilidades.CAMPO_BARCODEFIJO};

        try {
            Cursor cursor =db.query(Utilidades.TABLA_INVTFIJO,campos,Utilidades.CAMPO_BARCODEFIJO+"=?",
                    parametros,null,null,null);
            cursor.moveToFirst();
            if (cursor.getCount()>0){
                return true;
            }
            cursor.close();
            return false;
            //txtCodigodeBarras.setText(cursor.getString(0));

        }catch (Exception e){
            //Aqui añadir el mensaje de error para el barcode
            Toast.makeText(getApplicationContext(),"ERROR EN BARCODE" + " "+ e.getMessage().toString() ,Toast.LENGTH_LONG).show();
            return false;
        }
    }

    //METODO para Eliminar BC de la base de acuerdo al listView
    private void eliminarUsuario(long identificador) {
        ConexionSQLite conn=new ConexionSQLite(this,"bd_inventario",null,1);

        SQLiteDatabase db=conn.getWritableDatabase();
        String[] parametros={String.valueOf(identificador)};

        db.delete(Utilidades.TABLA_INVTBARCODE,Utilidades.CAMPO_ID+"=?",parametros);
        Toast.makeText(getApplicationContext(),"ELIMINADO DE LA BD",Toast.LENGTH_LONG).show();

        limpiar();
        db.close();
    }

    //METODO para Mostrar BC Registrados en la BD y llevarlos al listView
    private int consultarSql() {  //AÑADIDO es para devolver el id del registro que se añadio
        ConexionSQLite conn=new ConexionSQLite(this,"bd_inventario",null,1);

        SQLiteDatabase db=conn.getReadableDatabase();
        String[] parametros={};
        int valor =0;
        try {
            Cursor cursor =db.rawQuery("SELECT max ("+Utilidades.CAMPO_ID+") FROM "+Utilidades.TABLA_INVTBARCODE,parametros);
            cursor.moveToFirst();

            valor= Integer.parseInt(cursor.getString(0));
            cursor.close();
            return valor;
        }
        catch (Exception e){
            //Aqui añadir el mensaje de error para el barcode
            Toast.makeText(getApplicationContext(),"ERROR CONSULTAR SQL" + " "+ e.getMessage().toString() ,Toast.LENGTH_LONG).show();
        }
        return valor;
    }

    //METODO basico para limpiar el editText
    private void limpiar () {

        // detalle por limpiado del edit text?
        // txtCB.setText("");
        txtCB.getText().clear();
    }




    //METODO para mostrar la BD al iniciar la aplicacion

    /*public void mostrar(){

        int valor1 = 0;
        long valor2 = 0;

        ConexionSQLite conn=new ConexionSQLite(this,"bd_inventario",null,1);
        SQLiteDatabase db=conn.getReadableDatabase();

        Cursor cursor=db.rawQuery(
                "SELECT * FROM "+Utilidades.TABLA_INVTBARCODE+" ORDER BY "+Utilidades.CAMPO_ID+" DESC", null);

        //ArrayList<Lecturas_Entidad> alista =  new ArrayList<Lecturas_Entidad>();

        if (cursor.moveToFirst()){
            do {
                valor1 = cursor.getInt(String.format(valor1));
                valor2 = cursor.getLong(1);
                items_arreglo.add(0,new Lecturas_Entidad(valor1, valor2));
               // lblMensaje.setText(Integer.toString(valor1) + " - "  + Long.toString(valor2) );
            }while (cursor.moveToNext());
        }

           /* if (alista.isEmpty()){
            ArrayAdapter<Lecturas_Entidad> aLista = new ArrayAdapter<Lecturas_Entidad>(this, android.R.layout.simple_list_item_1, alista);

        }while (cursor.moveToNext());{
                Lecturas_Entidad oLectura= new Lecturas_Entidad();

                // oLectura.toString();


                lblMensaje.setText(cursor.getString(0));

                // alista.add(0,new Lecturas_Entidad(oLectura.getIdentificador(), oLectura.getCodigoBarras()));
            }

        db.close();
        lstLista.setAdapter(adaptador_items);
        adaptador_items.notifyDataSetChanged();
    }*/
}