package com.example.imberap.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.imberap.BluetoothServices.BluetoothLeService;
import com.example.imberap.BluetoothServices.BluetoothServices;
import com.example.imberap.R;
import com.example.imberap.utility.GetRealDataFromHexaImbera;


public class OperacionesFragment extends Fragment {
    SharedPreferences sp;
    SharedPreferences.Editor esp;
    BluetoothServices bluetoothServices;
    BluetoothLeService bluetoothLeService;
    Context context;

    public OperacionesFragment(BluetoothServices bluetoothServices, Context context) {
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
        View view = inflater.inflate(R.layout.fragment_operaciones, container, false);
        init();

        Button btn1 = view.findViewById(R.id.btnupdateFwOriginal);
        //btn1.setText("Actualización a Firmware:"+ GetRealDataFromHexaImbera.getSameData(sp.getString("NewFirmware","").substring(256,260),"trefpversion"));
        Button btn2 = view.findViewById(R.id.btnupdateFw);
        //btn2.setText("Actualización a Firmware Original:"+ GetRealDataFromHexaImbera.getSameData(sp.getString("NewFirmware","").substring(256,260),"trefpversion"));


        view.findViewById(R.id.btnhandshake).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hanshake();
            }
        });
        view.findViewById(R.id.btnreadTimeData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeData();
            }
        });
        view.findViewById(R.id.btnreadEventData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventData();
            }
        });

        view.findViewById(R.id.btnreadRealTimeData).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realTimeState();
            }
        });
        view.findViewById(R.id.btnupdateFw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAGGGG","::");
                updatefw();
            }
        });
        view.findViewById(R.id.btnupdateFwOriginal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatefwOriginal();
            }
        });

        return view;
    }

    private void init() {
        bluetoothLeService = bluetoothServices.getBluetoothLeService();
    }

    private void hanshake() {
        bluetoothServices.sendCommand("handshake");
    }

    private void timeData() {
        bluetoothServices.sendCommand("time");
    }

    private void eventData() {
        bluetoothServices.sendCommand("event");
    }

    private void realTimeState() {
        bluetoothServices.sendCommand("realState");
    }

    private void updatefw() {
        bluetoothServices.sendCommand("NewFirmware");
    }

    private void updatefwOriginal() {
        bluetoothServices.sendCommand("OriginalFirmware");
    }




}