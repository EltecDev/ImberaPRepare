package com.example.imberap.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.imberap.BluetoothServices.BluetoothLeService;
import com.example.imberap.BluetoothServices.BluetoothServices;
import com.example.imberap.R;


import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class LoginFragment extends Fragment {

    BluetoothServices bluetoothServices;
    listener listener;
    //Pantalla de peticion inicial de permisos
    SharedPreferences sp;
    SharedPreferences.Editor esp;
    Context context;

    TextView tvUsuarioActual;


    androidx.appcompat.app.AlertDialog progressdialog=null;
    View dialogViewProgressBar;

    EditText etUsuario, etpassword;

    public LoginFragment(BluetoothServices bluetoothServices, TextView textView, Context context){
        this.bluetoothServices = bluetoothServices;
        this.context = context;
        this.tvUsuarioActual = textView;
        this.sp = context.getSharedPreferences("connection_preferences",Context.MODE_PRIVATE);
        this.esp = sp.edit();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_login_layout, container, false);
        init(view);

        view.findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etUsuario.getText().toString().equals("") || etpassword.getText().toString().equals("")){
                    Toast.makeText(context, "Debes llenar los campos para continuar", Toast.LENGTH_SHORT).show();
                }else{
                    new MyAsyncTaskLogin().execute();
                }

            }
        });

        return view;
    }

    private void initCampos(View v){
        etUsuario = (EditText)v.findViewById(R.id.etLoginUsuario);
        etpassword = (EditText)v.findViewById(R.id.etLoginPassword);
    }

    public void init(View v){
        initCampos(v);
    }

    class MyAsyncTaskLogin extends AsyncTask<Integer, Integer, String> {
        String exc;

        @Override
        protected String doInBackground(Integer... params) {
            try{

                Thread.sleep(200);
                Connection connection;
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection("jdbc:mysql://electronicaeltec.com:3306/trefp_users","moises","9873216543");

                String user,pass,jerarquia="";
                user = etUsuario.getText().toString();
                pass = etpassword.getText().toString();

                String sql = "SELECT Jerarquia FROM cuentasusers WHERE idCuentasUsers='"+user+"' AND Passw='"+pass+"';";

                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql);

                int count = 0;

                while (result.next()){
                    esp.putString("userjerarquia",result.getString("Jerarquia"));
                    jerarquia = result.getString("Jerarquia");
                    count++;
                }

                if (count > 0 ){
                    //TODO SOLAMENTE SE ACEPTAN POR AHORA DOS USUARIOS, SUPERUSUARIO Y PRODUCCIÓN
                    if (jerarquia.equals("1") || jerarquia.equals("4") || jerarquia.equals("5") || jerarquia.equals("6") ) {
                        esp.putString("userId", user);
                        esp.apply();
                        return "exito";
                    }else
                        return "noJerarquia";

                }else{
                    return "falla";
                }
            }catch(SQLException | ClassNotFoundException ex){
                Log.d("SQLException/ClassNotFoundException",":"+ex);
                exc = ex.toString();
                return "SQLException";
            } catch (IllegalAccessException e) {
                Log.d("IllegalAccessException",":"+e);
                exc = e.toString();
                return "IllegalAccessException";
            } catch (java.lang.InstantiationException e) {
                Log.d("InstantiationException",":"+e);
                exc = e.toString();
                return "InstantiationException";
            } catch (InterruptedException e) {
                Log.d("InterruptedException",":"+e);
                exc = e.toString();
                return "InterruptedException";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressdialog.dismiss();
            Log.d("result",":"+result);
            switch (result){
                case "exito":{

                    switch (sp.getString("userjerarquia","")){
                        case "1":{
                            tvUsuarioActual.setText("Usuario:"+sp.getString("userId","")+"\nJerarquía: Administrador");
                            break;
                        }
                        case "2":{
                            tvUsuarioActual.setText("Usuario:"+sp.getString("userId","")+"\nJerarquía: Ingeniería");
                            break;
                        }
                        case "3":{
                            tvUsuarioActual.setText("Usuario:"+sp.getString("userId","")+"\nJerarquía: Laboratorio");
                            break;
                        }
                        case "4":{
                            tvUsuarioActual.setText("Usuario:"+sp.getString("userId","")+"\nJerarquía: Producción");
                            break;
                        }
                        case "5":{
                            tvUsuarioActual.setText("Usuario:"+sp.getString("userId","")+"\nJerarquía: Operador");
                            break;
                        }
                        case "6":{
                            tvUsuarioActual.setText("Usuario:"+sp.getString("userId","")+"\nJerarquía: Técnico");
                            break;
                        }
                    }
                    etUsuario.setText("");
                    etpassword.setText("");
                    listener.login();
                    break;

                }
                case "noJerarquia":{
                    showInfoPopup("Jerarquía de usuarios","No puedes usar este usuario para conectarte con Imbera T. Config, solicita apoyo al administrador del sistema.");
                    //Toast.makeText(context, "El usuario o la contraseña no son correctas", Toast.LENGTH_SHORT).show();
                    break;

                }
                case "falla":{
                    showInfoPopup("Error de autenticación","El usuario o la contraseña no son correctas.");
                    break;

                }
                case "SQLException":{
                    Toast.makeText(context, "Error al conectar con BD", Toast.LENGTH_SHORT).show();
                    break;

                }
                case "IllegalAccessException":{
                    Toast.makeText(context, "Error al conectar con BD", Toast.LENGTH_SHORT).show();
                    break;

                }
                case "InstantiationException":{
                    Toast.makeText(context, "Error al conectar con BD", Toast.LENGTH_SHORT).show();
                    break;

                }
                case "InterruptedException":{
                    Toast.makeText(context, "Error al conectar con BD", Toast.LENGTH_SHORT).show();
                    break;

                }
            }

        }

        @Override
        protected void onPreExecute() {
            createProgressDialog("Buscando usuario...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private void showInfoPopup(String tittle, String content){
        final AlertDialog alexaDialog;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_info, null, false);
        AlertDialog.Builder adb = new AlertDialog.Builder(context,R.style.Theme_AppCompat_Light_Dialog_Alert_eltc);
        adb.setView(dialogView);

        TextView tv1 = (TextView) dialogView.findViewById(R.id.tvTituloData);
        tv1.setText(tittle);
        TextView tv2 = (TextView) dialogView.findViewById(R.id.tvsubtitulo);
        tv2.setText(content);

        alexaDialog = adb.create();
        alexaDialog.setCanceledOnTouchOutside(false);
        alexaDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alexaDialog.show();
        dialogView.findViewById(R.id.welcomeAlexaButtonLater).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alexaDialog.dismiss();
            }
        });

    }

    public void createProgressDialog(String string){
        if(progressdialog == null){
            //Crear dialogos de "pantalla de carga" y "popups if"
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);//getLayoutInflater();
            dialogViewProgressBar = inflater.inflate(R.layout.show_progress_bar, null, false);
            androidx.appcompat.app.AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(getContext(),R.style.Theme_AppCompat_Light_Dialog_Alert_eltc);
            adb.setView(dialogViewProgressBar);
            progressdialog = adb.create();
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView txt = ((TextView) dialogViewProgressBar.findViewById(R.id.txtInfoProgressBar));
            txt.setText(string);
        }
        progressdialog.show();
    }

    public void loginListener(listener callback) {
        this.listener = callback;
    }

    public interface listener {
        public void login();

    }
}
