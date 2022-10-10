package com.example.imberap.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.imberap.BluetoothServices.BluetoothLeService;
import com.example.imberap.BluetoothServices.BluetoothServices;
import com.example.imberap.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioFragment extends Fragment {

    BluetoothServices bluetoothServices;
    MainActivityistener mainActivityistener;
    //Pantalla de peticion inicial de permisos
    SharedPreferences sp;
    SharedPreferences.Editor esp;
    Context context;


    androidx.appcompat.app.AlertDialog progressdialog=null;
    View dialogViewProgressBar;

    TextView tvUsuario, tvjerarquia;

    public UsuarioFragment(BluetoothServices bluetoothServices, TextView textView, Context context){
        this.bluetoothServices = bluetoothServices;
        this.context = context;
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
        View view =inflater.inflate(R.layout.fragment_userinfo, container, false);
        init(view);

        view.findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        return view;
    }

    private void initCampos(View v){
        tvUsuario = (TextView)v.findViewById(R.id.tvUserName);
        tvjerarquia= (TextView)v.findViewById(R.id.tvjerarquia);
        tvUsuario.setText(sp.getString("userId",""));
        switch (sp.getString("userjerarquia","")){
            case "1":{
                tvjerarquia.setText("Administrador");
                break;
            }
            case "2":{
                tvjerarquia.setText("Ingeniería");
                break;
            }
            case "3":{
                tvjerarquia.setText("Laboratorio");
                break;
            }
            case "4":{
                tvjerarquia.setText("Producción");
                break;
            }
            case "5":{
                tvjerarquia.setText("Operador");
                break;
            }
        }

    }

    public void init(View v){
        initCampos(v);
    }

    private void logout(){
        mainActivityistener.logout();
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

    public void logoutListener(MainActivityistener callback) {
        this.mainActivityistener = callback;
    }

    public interface MainActivityistener {
        public void logout();

    }
}
