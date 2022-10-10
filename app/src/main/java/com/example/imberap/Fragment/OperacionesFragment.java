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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.imberap.BluetoothServices.BluetoothLeService;
import com.example.imberap.BluetoothServices.BluetoothServices;
import com.example.imberap.R;
import com.example.imberap.Utility.GetRealDataFromHexaImbera;
import com.example.imberap.Utility.GlobalTools;

import java.util.ArrayList;
import java.util.List;


public class OperacionesFragment extends Fragment {
    SharedPreferences sp;
    SharedPreferences.Editor esp;
    BluetoothServices bluetoothServices;
    BluetoothLeService bluetoothLeService;
    Context context;
    listener listener;

    List<String> FinalListDataEvento = new ArrayList<String>() ;
    List<String> FinalListDataTiempo = new ArrayList<String>() ;

    androidx.appcompat.app.AlertDialog progressdialog = null;
    View dialogViewProgressBar;

    public OperacionesFragment(){}

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
        if (sp.getString("trefpVersionName","").equals("IMBERA-OXXO")){
            view.findViewById(R.id.btnreadEventData).setVisibility(View.GONE);
            view.findViewById(R.id.btnreadTimeData).setVisibility(View.GONE);
        }else if(sp.getString("trefpVersionName","").equals("IMBERA-TREFP")){
            view.findViewById(R.id.btnreadEventData).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btnreadTimeData).setVisibility(View.VISIBLE);
        }
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
                updatefw();
            }
        });
        view.findViewById(R.id.btnupdateFwOriginal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatefwOriginal();
            }
        });

        TextView tvtitulo = view.findViewById(R.id.tvtitulo);
        tvtitulo.setText("Aquí se muestran las operaciones actuales posibles con el equipo IMBERA-TREFPB");
        return view;
    }

    private void init() {


    }

    private void hanshake() {
        if (!sp.getString("trefpVersionName","").equals("")){
            bluetoothServices.sendCommand("handshake");
        }else{
            Toast.makeText(context, "Debes estar conectado a un BLE", Toast.LENGTH_SHORT).show();
        }
    }

    private void timeData() {
        if (!sp.getString("trefpVersionName","").equals("")){
            new MyAsyncTaskGetActualDatosTipoTiempo().execute();
        }else{
            Toast.makeText(context, "Debes estar conectado a un BLE", Toast.LENGTH_SHORT).show();
        }

    }

    private void eventData() {
        if (!sp.getString("trefpVersionName","").equals("")){
            new MyAsyncTaskGetActualDatosTipoEvento().execute();
        }else{
            Toast.makeText(context, "Debes estar conectado a un BLE", Toast.LENGTH_SHORT).show();
        }
    }

    private void realTimeState() {
        if (!sp.getString("trefpVersionName","").equals("")){
            bluetoothServices.sendCommand("realState");
        }else{
            Toast.makeText(context, "Debes estar conectado a un BLE", Toast.LENGTH_SHORT).show();
        }

    }

    private void updatefw() {
        if (!sp.getString("trefpVersionName","").equals("")){
            bluetoothServices.sendCommand("NewFirmware");
        }else{
            Toast.makeText(context, "Debes estar conectado a un BLE", Toast.LENGTH_SHORT).show();
        }

    }
    /*private void updatefw() {
        if (!sp.getString("trefpVersionName","").equals("")){
            bluetoothServices.sendCommand("newfwTrefpResetMemory");
        }else{
            Toast.makeText(context, "Debes estar conectado a un BLE", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void updatefwOriginal() {
        if (!sp.getString("trefpVersionName","").equals("")){
            bluetoothServices.sendCommand("OriginalFirmware");
        }else{
            Toast.makeText(context, "Debes estar conectado a un BLE", Toast.LENGTH_SHORT).show();
        }
    }

    private void getTiemInfo(){
        List<String> FinalListData = new ArrayList<String>() ;
        List<String> listData = new ArrayList<String>() ;
        List<String> FinalListTest = new ArrayList<String>() ;
        String isChecksumOk;
        try {
            bluetoothLeService = bluetoothServices.getBluetoothLeService();
            if (sp.getString("modelo","").equals("3.3") && sp.getString("numversion","").equals("1.02")){
                FinalListData.clear();
                listData.clear();
                bluetoothServices.sendCommand("time","4060");
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(0));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(1));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(2));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(3));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(4));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(5));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(6));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(7));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(8));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(9));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(7));


                for (int i=0; i<listData.size(); i++){//comprobación de la obtención de datos sin lista vacía
                    if (listData.get(i).length() !=0){
                        FinalListTest.add(listData.get(i));
                    }
                }

                isChecksumOk = GlobalTools.checkChecksumImberaTREFPBList(GetRealDataFromHexaImbera.cleanSpaceList(FinalListTest));
                Log.d("istiempoChecksum",":"+isChecksumOk);
                if (isChecksumOk.equals("ok")){
                    if (FinalListTest.get(0).length() == 0){
                        FinalListDataTiempo.clear();
                    }else{
                        FinalListData = GetRealDataFromHexaImbera.convert(FinalListTest, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                        FinalListDataTiempo = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                    }
                }else{

                }


            }else if (sp.getString("modelo","").equals("3.5") && sp.getString("numversion","").equals("1.04")){
                FinalListData.clear();
                listData.clear();
                bluetoothServices.sendCommand("time","4060");
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(0));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(1));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(2));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(3));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(4));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(5));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(6));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(7));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(8));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(9));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(10));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(11));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(12));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(13));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(14));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(15));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(16));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(17));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(18));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(19));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(20));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(21));



                for (int i=0; i<listData.size(); i++){//comprobación de la obtención de datos sin lista vacía
                    if (listData.get(i).length() !=0){
                        FinalListTest.add(listData.get(i));
                    }
                }

                isChecksumOk = GlobalTools.checkChecksumImberaTREFPBList(GetRealDataFromHexaImbera.cleanSpaceList(FinalListTest));
                Log.d("istiempoChecksum",":"+isChecksumOk);
                if (isChecksumOk.equals("ok")){
                    if (FinalListTest.get(0).length() == 0){
                        FinalListDataTiempo.clear();
                    }else{
                        FinalListData = GetRealDataFromHexaImbera.convert(FinalListTest, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                        FinalListDataTiempo = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                    }
                }else{
                }
            }else{
                //if modelo actual es menor a 3.3 en adelante, entonces mostrar de forma de nuevo logger
                listData.clear();
                FinalListDataTiempo.clear();
                bluetoothLeService = bluetoothServices.getBluetoothLeService();
                bluetoothServices.sendCommand("time","4060");
                Thread.sleep(3550);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                isChecksumOk = GlobalTools.checkChecksumImberaTREFPBList(GetRealDataFromHexaImbera.cleanSpaceList(listData));
                Log.d("istiempoChecksum",":"+isChecksumOk);
                if (listData.get(0).length() == 0){
                    FinalListDataTiempo.clear();
                }else{
                    FinalListData = GetRealDataFromHexaImbera.convert(listData, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                    FinalListDataTiempo = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getEventoInfo(){
        List<String> FinalListData = new ArrayList<String>() ;
        List<String> listData = new ArrayList<String>() ;
        List<String> FinalListTest = new ArrayList<String>() ;
        String isChecksumOk;
        try {
            bluetoothLeService = bluetoothServices.getBluetoothLeService();
            //bluetoothServices.sendCommand("event","4061");
            if (sp.getString("modelo","").equals("3.3") && sp.getString("numversion","").equals("1.02")){
                FinalListTest.clear();
                FinalListData.clear();
                listData.clear();
                bluetoothServices.sendCommand("event","4061");
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(0));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(1));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(2));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(3));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(4));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(5));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(6));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(7));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(8));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(9));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(10));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(11));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(12));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(13));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(14));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(15));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(16));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(17));
                for (int i=0; i<listData.size(); i++){//comprobación de la obtención de datos sin lista vacía
                    if (listData.get(i).length() !=0){
                        FinalListTest.add(listData.get(i));
                    }
                }
                isChecksumOk = GlobalTools.checkChecksumImberaTREFPBList(GetRealDataFromHexaImbera.cleanSpaceList(FinalListTest));
                Log.d("isevento",":"+isChecksumOk);
                if (isChecksumOk.equals("ok")){

                    if (FinalListTest.get(0).length() == 0){
                        FinalListDataEvento.clear();
                    }else{
                        FinalListData = GetRealDataFromHexaImbera.convert(FinalListTest, "Lectura de datos tipo Evento",sp.getString("numversion",""), sp.getString("modelo",""));
                        FinalListDataEvento = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Evento",sp.getString("numversion",""), sp.getString("modelo",""));
                    }
                }else{

                }
            }else if (sp.getString("modelo","").equals("3.5") && sp.getString("numversion","").equals("1.04")){
                FinalListTest.clear();
                FinalListData.clear();
                listData.clear();
                bluetoothServices.sendCommand("event","4061");
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(0));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(1));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(2));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(3));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(4));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(5));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(6));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(7));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(8));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(9));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(10));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(11));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(12));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(13));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(14));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(15));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(16));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(17));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(18));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(19));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(20));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(21));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(22));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(23));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(24));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(25));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(26));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(27));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(28));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(29));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+listData.get(30));
                Thread.sleep(700);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());

                for (int i=0; i<listData.size(); i++){//comprobación de la obtención de datos sin lista vacía
                    if (listData.get(i).length() !=0){
                        FinalListTest.add(listData.get(i));
                    }
                }
                isChecksumOk = GlobalTools.checkChecksumImberaTREFPBList(GetRealDataFromHexaImbera.cleanSpaceList(FinalListTest));
                Log.d("isevento",":"+isChecksumOk);
                if (isChecksumOk.equals("ok")){
                    if (FinalListTest.get(0).length() == 0){
                        FinalListDataEvento.clear();
                    }else{
                        FinalListData = GetRealDataFromHexaImbera.convert(FinalListTest, "Lectura de datos tipo Evento",sp.getString("numversion",""), sp.getString("modelo",""));
                        FinalListDataEvento = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Evento",sp.getString("numversion",""), sp.getString("modelo",""));
                    }
                }else{
                }
            }else {
                //if modelo actual es menor a 3.3 en adelante, entonces mostrar de forma de nuevo logger
                listData.clear();
                FinalListData.clear();
                FinalListDataEvento.clear();
                bluetoothLeService = bluetoothServices.getBluetoothLeService();
                bluetoothServices.sendCommand("event","4061");
                Thread.sleep(1000);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                isChecksumOk = GlobalTools.checkChecksumImberaTREFPBList(GetRealDataFromHexaImbera.cleanSpaceList(listData));
                Log.d("istiempoChecksum",":"+isChecksumOk);
                if (listData.get(0).length() == 0){
                    FinalListDataEvento.clear();
                }else{
                    FinalListData = GetRealDataFromHexaImbera.convert(listData, "Lectura de datos tipo Evento",sp.getString("numversion",""), sp.getString("modelo",""));
                    FinalListDataEvento = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Evento",sp.getString("numversion",""), sp.getString("modelo",""));
                    Log.d("Datos tipo tiempo REPORTE",":"+FinalListDataEvento);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class MyAsyncTaskGetActualDatosTipoEvento extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            getEventoInfo();
            return "resp";
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressdialog != null)progressdialog.dismiss();
            listener.createExcelEventData("",FinalListDataEvento);
            progressdialog=null;
        }

        @Override
        protected void onPreExecute() {
            FinalListDataEvento.clear();
            createProgressDialog("Obteniendo estado actual...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskGetActualDatosTipoTiempo extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            getTiemInfo();
            return "resp";
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressdialog != null)progressdialog.dismiss();
            listener.createExcelTimeData("",FinalListDataTiempo);
            progressdialog=null;
        }

        @Override
        protected void onPreExecute() {
            FinalListDataTiempo.clear();
            createProgressDialog("Obteniendo estado actual...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    //Metodos MainActivity
    public interface listener {
        public void createExcelTimeData(String name, List<String> data);
        public void createExcelTimeDataCrudo(String name, List<String> data, List<String> crudo);
        public void createExcelEventData(String name, List<String> data);
        public void createExcelEventDataCrudo(String name, List<String> data,List<String> crudo);
    }

    public void createProgressDialog(String string){
        if(progressdialog == null){
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);//getLayoutInflater();
            dialogViewProgressBar = inflater.inflate(R.layout.show_progress_bar, null, false);
            androidx.appcompat.app.AlertDialog.Builder adb = new androidx.appcompat.app.AlertDialog.Builder(getContext(),R.style.Theme_AppCompat_Light_Dialog_Alert_eltc);
            adb.setView(dialogViewProgressBar);
            progressdialog = adb.create();
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            TextView txt = ((TextView) dialogViewProgressBar.findViewById(R.id.txtInfoProgressBar));
            txt.setText(string);
            progressdialog.show();
        }

    }

    //Listeners
    public void setOperacionesListener(listener callback) {
        this.listener = callback;
    }

}