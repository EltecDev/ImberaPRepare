package com.example.imberap.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.imberap.BluetoothServices.BluetoothLeService;
import com.example.imberap.BluetoothServices.BluetoothServices;
import com.example.imberap.R;

public class OperacionesFragmentCeoWifi extends Fragment {
    SharedPreferences sp;
    SharedPreferences.Editor esp;
    BluetoothServices bluetoothServices;
    BluetoothLeService bluetoothLeService;
    Context context;

    AlertDialog progressdialog=null;

    public OperacionesFragmentCeoWifi(){}

    public OperacionesFragmentCeoWifi(BluetoothServices bluetoothServices, Context context) {
        this.bluetoothServices = bluetoothServices;
        this.context = context;
        sp = context.getSharedPreferences("connection_preferences" , Context.MODE_PRIVATE);
        esp = sp.edit();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_operaciones_oxxo, container, false);
        init();


        view.findViewById(R.id.btnupdateFw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updatefw();
                //Toast.makeText(context, "Función no disponible en esta versión", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.btnupdateFwOriginal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Función no disponible en esta versión", Toast.LENGTH_SHORT).show();
                //updatefwOriginal();
            }
        });
        view.findViewById(R.id.btnconfigurarWifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWifi();
            }
        });
        view.findViewById(R.id.btnVerConfWifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiInfo();
            }
        });
        TextView tvtitulo = view.findViewById(R.id.tvtitulo);
        tvtitulo.setText("Aquí se muestran las operaciones actuales posibles con el equipo IMBERA-OXXO");

        return view;
    }

    private void init() {
        bluetoothLeService = bluetoothServices.getBluetoothLeService();
    }

    private void updatefw() {
        bluetoothServices.sendCommand("NewFirmwareOxxo");
    }

    private void updatefwOriginal() {
        bluetoothServices.sendCommand("OriginalFirmwareOxxo");
    }

    private void updateWifi() {
        createProgressDialogwifi();
    }

    private void WifiInfo() {
        createProgressDialogwifiInfo();
    }

    public void createProgressDialogwifi(){
        if(progressdialog == null){
            //Crear dialogos de "pantalla de carga" y "popups if"
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.popup_wifi, null, false);
            AlertDialog.Builder adb = new AlertDialog.Builder(context,R.style.Theme_AppCompat_Light_Dialog_Alert_eltc);
            adb.setView(dialogView);
            progressdialog = adb.create();
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressdialog.show();

            dialogView.findViewById(R.id.btnSendWifi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressdialog.dismiss();
                    progressdialog = null;
                    EditText etSsid= dialogView.findViewById(R.id.etSSID);
                    EditText etpass= dialogView.findViewById(R.id.etWifiPass);
                    String ssid = etSsid.getText().toString();
                    String pass = etpass.getText().toString();
                    bluetoothServices.sendCommandWifi(ssid,pass);
                }
            });
            dialogView.findViewById(R.id.btndontSendwifi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressdialog.dismiss();
                    progressdialog = null;
                }
            });
        }
    }

    public void createProgressDialogwifiInfo(){
        bluetoothServices.sendCommandWifiInfo();
        /*if(progressdialog == null){
            //Crear dialogos de "pantalla de carga" y "popups if"
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.popup_wifi, null, false);
            AlertDialog.Builder adb = new AlertDialog.Builder(context,R.style.Theme_AppCompat_Light_Dialog_Alert_eltc);
            adb.setView(dialogView);
            progressdialog = adb.create();
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressdialog.show();

            dialogView.findViewById(R.id.btnSendWifi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressdialog.dismiss();
                    progressdialog = null;
                    EditText etSsid= dialogView.findViewById(R.id.etSSID);
                    EditText etpass= dialogView.findViewById(R.id.etWifiPass);
                    String ssid = etSsid.getText().toString();
                    String pass = etpass.getText().toString();
                    bluetoothServices.sendCommandWifiInfo(ssid,pass);
                }
            });
            dialogView.findViewById(R.id.btndontSendwifi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressdialog.dismiss();
                    progressdialog = null;
                }
            });
        }*/
    }

}