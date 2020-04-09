package com.zapmex.ZM_Inventarios;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zapmex.ZM_Inventarios.Utilidad.Utilidades;

public class Consulta_BD extends AppCompatActivity {

    private EditText txtCB;

    ConexionSQLite conn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConexionSQLite conn=new ConexionSQLite(this,"bd_inventario",null,1);
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
}
