package com.example.imberap.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.imberap.BluetoothServices.BluetoothLeService;
import com.example.imberap.BluetoothServices.BluetoothServices;
import com.example.imberap.R;

public class OperacionesFragmentOxxo extends Fragment {
    SharedPreferences sp;
    SharedPreferences.Editor esp;
    BluetoothServices bluetoothServices;
    BluetoothLeService bluetoothLeService;
    Context context;

    androidx.appcompat.app.AlertDialog progressdialog=null;

    public OperacionesFragmentOxxo(){}

    public OperacionesFragmentOxxo(BluetoothServices bluetoothServices, Context context) {
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
        init(view);


        view.findViewById(R.id.btnupdateFw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updatefw();
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

        TextView tvtitulo = view.findViewById(R.id.tvtitulo);
        tvtitulo.setText("Aquí se muestran las operaciones actuales posibles con el equipo IMBERA-OXXO");

        return view;
    }

    private void init(View view) {
        //inicialiar los botones de actualización según el modelo
        /*if(sp.getString("modelo","").equals("3.5")){
            view.findViewById(R.id.btnupdateFw).setVisibility(View.GONE);
            view.findViewById(R.id.btnupdateFwOriginal).setVisibility(View.GONE);
        }else if (sp.getString("modelo","").equals("3.3")){
            view.findViewById(R.id.btnupdateFw).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btnupdateFwOriginal).setVisibility(View.GONE);
        }else{
            view.findViewById(R.id.btnupdateFw).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btnupdateFwOriginal).setVisibility(View.GONE);
        }*/

        bluetoothLeService = bluetoothServices.getBluetoothLeService();
    }

    private void updatefw() {
        bluetoothServices.sendCommand("NewFirmwareOxxo");
    }

    private void updatefwOriginal() {
        bluetoothServices.sendCommand("OriginalFirmwareOxxo");
    }



}