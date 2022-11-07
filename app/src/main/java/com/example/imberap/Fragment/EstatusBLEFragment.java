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
import com.example.imberap.Utility.GetRealDataFromHexaOxxoDisplay;
import com.example.imberap.Utility.GlobalTools;

import java.util.ArrayList;
import java.util.List;
/**
 * 28/09/2022
 * Información importante para logger:
 *
 * La manera de recibir la información por BLE (por partes) necesita saber el firmware que tiene el control para saber que manera se van a leer los datos, actualmente
 * Para logger Firmware 1.02; Modelo 3.3, tiempo son 7 iteraciones, para evento 17 iteraciones
 * Para logger Firmware 1.14; Modelo 3.5, tiempo son 20 iteraciones, para evento 31 iteraciones
 * */
public class EstatusBLEFragment extends Fragment {
    BluetoothServices bluetoothServices;
    BluetoothLeService bluetoothLeService;

    ArrayList<String> listData = new ArrayList<String>() ;
    List<String> realDataList = new ArrayList<String>() ;
    List<String> FinalListData2 = new ArrayList<String>() ;
    List<String> FinalListDataRealState = new ArrayList<String>() ;
    List<String> FinalListDataHandshake = new ArrayList<String>() ;
    List<String> FinalListDataTiempo = new ArrayList<String>() ;
    List<String> FinalListDataEvento = new ArrayList<String>() ;

    //Pantalla de peticion inicial de permisos
    SharedPreferences sp;
    SharedPreferences.Editor esp;
    Context context;

    TextView tvhandshake, tvTime, tvEvent, tvRealState, tvReadParam, tvsubtitulo;
    TextView tvLogg,tvLogg1,tvLogg2,tvLogg3;

    androidx.appcompat.app.AlertDialog progressdialog=null;
    View dialogViewProgressBar;

    public EstatusBLEFragment(){}

    public EstatusBLEFragment(BluetoothServices bluetoothServices, Context context){
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
        View view =inflater.inflate(R.layout.fragment_estatus, container, false);
        init(view);

        view.findViewById(R.id.btnGetCurrentLogger).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLogger();
            }
        });
        view.findViewById(R.id.btnsendStatus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStatus();
            }
        });

        return view;
    }

    private void initCampos(View v){
        tvhandshake = v.findViewById(R.id.tvhandshake);
        tvTime = v.findViewById(R.id.tvdatostipoTiempo);
        tvEvent = v.findViewById(R.id.tvdatostipoEvento);
        tvRealState = v.findViewById(R.id.tvdatosEstadoTiempoReal);
        tvsubtitulo = v.findViewById(R.id.tvsubtitulo);

        tvLogg = v.findViewById(R.id.loggerView);
        tvLogg1 = v.findViewById(R.id.loggerView1);
        tvLogg2 = v.findViewById(R.id.loggerView2);
        tvLogg3 = v.findViewById(R.id.loggerView3);

    }
    private void init(View v){
        initCampos(v);
        /*if (bluetoothLeService!=null)new MyAsyncTaskGetActualStatus().execute();
        else Toast.makeText(getContext(), "No estás conectado a BLE", Toast.LENGTH_SHORT).show();*/
    }
    private void requestStatus(){
        bluetoothLeService = bluetoothServices.getBluetoothLeService();
        if (bluetoothLeService!=null)
            new MyAsyncTaskGetActualStatus().execute();
        else
            Toast.makeText(getContext(), "No estás conectado a BLE", Toast.LENGTH_SHORT).show();
    }
    private void requestLogger(){
        bluetoothLeService = bluetoothServices.getBluetoothLeService();
        if (bluetoothLeService!=null)
            new MyAsyncTaskGetActualLogger().execute();
        else
            Toast.makeText(getContext(), "No estás conectado a BLE", Toast.LENGTH_SHORT).show();
    }

    class MyAsyncTaskGetActualStatus extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            if (convertInfoEstatus().equals("true")){
                return "true";
            }else{
                return "false";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            if (progressdialog != null)progressdialog.dismiss();
            progressdialog=null;

            if (result.equals("true")){
                if (sp.getString("trefpVersionName","").equals("IMBERA-TREFP")){
                    tvsubtitulo.setText("Aquí se muestra el estado actual de tu dispositivo IMBERA-TREFPB");
                    if (sp.getString("numversion","").equals("1.04")){
                        tvhandshake.setVisibility(View.VISIBLE);
                        tvLogg.setVisibility(View.VISIBLE);
                        tvLogg1.setVisibility(View.VISIBLE);
                        tvLogg2.setVisibility(View.VISIBLE);
                        tvLogg3.setVisibility(View.VISIBLE);
                        tvEvent.setVisibility(View.VISIBLE);
                        tvTime.setVisibility(View.VISIBLE);
                        tvRealState.setVisibility(View.VISIBLE);

                        tvhandshake.setText("MAC:" + FinalListDataHandshake.get(0)
                                + "\nModelo TREFPB:" + FinalListDataHandshake.get(1)
                                + "\nVersión:" + FinalListDataHandshake.get(2)
                                + "\nPlantilla:" + FinalListDataHandshake.get(3));

                        tvRealState.setText("\nTemperatura 1:" + FinalListDataRealState.get(0) + " °C"
                                + "\nTemperatura 2:" + FinalListDataRealState.get(1) + " °C"
                                + "\nTemperatura 3:" + FinalListDataRealState.get(2) + " °C"
                                + "\nTemperatura Display:" + FinalListDataRealState.get(3) + " °\n"
                                + "\nVoltaje:" + FinalListDataRealState.get(4)+"\n"
                                + "\nActuadores:" + FinalListDataRealState.get(5)
                                + "\nAlarmas:" + FinalListDataRealState.get(6)+ "\n"
                        );
                    }else{
                        tvhandshake.setVisibility(View.VISIBLE);
                        tvLogg.setVisibility(View.VISIBLE);
                        tvLogg1.setVisibility(View.VISIBLE);
                        tvLogg2.setVisibility(View.VISIBLE);
                        tvLogg3.setVisibility(View.VISIBLE);
                        tvEvent.setVisibility(View.VISIBLE);
                        tvTime.setVisibility(View.VISIBLE);
                        tvRealState.setVisibility(View.VISIBLE);

                        tvhandshake.setText("MAC:" + FinalListDataHandshake.get(0)
                                + "\nModelo TREFPB:" + FinalListDataHandshake.get(1)
                                + "\nVersión:" + FinalListDataHandshake.get(2)
                                + "\nPlantilla:" + FinalListDataHandshake.get(3));


                        tvRealState.setText("\nTemperatura 1:" + FinalListDataRealState.get(0) + " °C"
                                + "\nTemperatura 2:" + FinalListDataRealState.get(1) + " °C"
                                + "\nVoltaje:" + FinalListDataRealState.get(2)
                                + "\nActuadores:" + FinalListDataRealState.get(3)
                                + "\nAlarmas:" + FinalListDataRealState.get(4)+ "\n"
                        );
                    }
                }else if (sp.getString("trefpVersionName","").contains("IMBERA-OXXO")){
                    tvsubtitulo.setText("Aquí se muestra el estado actual de tu dispositivo IMBERA-OXXO");

                    //OXXO no tiene LOGGER
                    tvhandshake.setVisibility(View.VISIBLE);
                    tvLogg.setVisibility(View.GONE);
                    tvLogg1.setVisibility(View.GONE);
                    tvLogg2.setVisibility(View.GONE);
                    tvLogg3.setVisibility(View.GONE);
                    tvEvent.setVisibility(View.GONE);
                    tvTime.setVisibility(View.GONE);
                    tvRealState.setVisibility(View.VISIBLE);
                    if (!FinalListDataHandshake.isEmpty()){
                        tvhandshake.setText("MAC:" + FinalListDataHandshake.get(0)
                                + "\nModelo TREFPB:" + FinalListDataHandshake.get(1)
                                + "\nVersión:" + FinalListDataHandshake.get(2)
                                + "\nPlantilla:" + FinalListDataHandshake.get(3)+ "\n");
                    }else{
                        tvhandshake.setText("No se pudo obtener información Handshake de tu Trefp, por favor intenta reconectarte");
                    }
                    if (!FinalListDataRealState.isEmpty()){
                        if (FinalListDataRealState.get(0).equals("8")){
                            tvRealState.setText("\nTemperatura 1:" + FinalListDataRealState.get(1) + " °C"
                                    + "\nTemperatura 2:" + FinalListDataRealState.get(2) + " °C"
                                    + "\nVoltaje:" + FinalListDataRealState.get(3)
                                    + "\nActuadores:" + FinalListDataRealState.get(4)
                                    + "\nAlarmas:" + FinalListDataRealState.get(5)+ "\n"
                            );
                        }else{
                            tvRealState.setText("\nTemperatura 1:" + FinalListDataRealState.get(1) + " °C"
                                    + "\nTemperatura 2:" + FinalListDataRealState.get(2) + " °C"
                                    + "\nTemperatura 3:" + FinalListDataRealState.get(3) + " °C"
                                    + "\nVoltaje:" + FinalListDataRealState.get(4)
                                    + "\nActuadores:" + FinalListDataRealState.get(5)
                                    + "\nAlarmas:" + FinalListDataRealState.get(6)+ "\n"
                            );
                        }

                    }else
                        tvRealState.setText("No se pudo obtener información Estado en tiempo real de tu Trefp, por favor intenta reconectarte");

                }
            }else if(result.equals("false")){
                GlobalTools.showInfoPopup("Información","La comunicación no fue exitosa, reintenta o reconecta con el equipo TREFP",getContext());
            }



        }

        @Override
        protected void onPreExecute() {
            tvhandshake.setText("");
            tvRealState.setText("");
            createProgressDialog("Obteniendo estado actual...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskGetActualLogger extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            if (convertInfoLogger().equals("true")){
                return "true";
            }else{
                return "false";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            if (progressdialog != null)progressdialog.dismiss();
            progressdialog=null;

            if (result.equals("true")){
                if (sp.getString("trefpVersionName","").equals("IMBERA-TREFP")){
                    tvsubtitulo.setText("Aquí se muestra el estado actual de tu dispositivo IMBERA-TREFPB");

                    tvhandshake.setVisibility(View.VISIBLE);
                    tvLogg.setVisibility(View.VISIBLE);
                    tvLogg1.setVisibility(View.VISIBLE);
                    tvLogg2.setVisibility(View.VISIBLE);
                    tvLogg3.setVisibility(View.VISIBLE);
                    tvEvent.setVisibility(View.VISIBLE);
                    tvTime.setVisibility(View.VISIBLE);
                    tvRealState.setVisibility(View.VISIBLE);

                    StringBuilder stringb = new StringBuilder();
                    int j=1;
                    for (int i=0;i<FinalListDataTiempo.size(); i+=4){
                        if (i+3>FinalListDataTiempo.size()){
                            break;
                        }   else {
                            stringb.append("\nIteración "+j+
                                    "\nTimeStamp:" + FinalListDataTiempo.get(i)
                                    +"\nTemperatura 1:" + FinalListDataTiempo.get(i+1) + " °C"
                                    + "\nTemperatura 2:" + FinalListDataTiempo.get(i+2) + " °C"
                                    + "\nVoltaje:" + FinalListDataTiempo.get(i+3)+ "\n");
                            j++;
                        }
                    }
                    tvTime.setText(stringb.toString());

                    StringBuilder stringa = new StringBuilder();
                    j=1;
                    for (int i=0;i<FinalListDataEvento.size(); i+=6){
                        if (i+5>FinalListDataEvento.size()){
                            break;
                        }   else {
                            stringa.append("\nIteración "+j+
                                    "\nTimeStamp START:" + FinalListDataEvento.get(i)
                                    +"\nTimeStamp END:" + FinalListDataEvento.get(i+1)
                                    +"\nTipo de evento:\n" + FinalListDataEvento.get(i+2)
                                    +"\nTemperatura 1I:" + FinalListDataEvento.get(i+3) + " °C"
                                    + "\nTemperatura 2F:" + FinalListDataEvento.get(i+4) + " °C"
                                    + "\nVoltaje:" + FinalListDataEvento.get(i+5)+ "\n");
                            j++;
                        }
                    }
                    tvEvent.setText(stringa.toString());

                }else if (sp.getString("trefpVersionName","").contains("IMBERA-OXXO")){
                    tvsubtitulo.setText("Aquí se muestra el estado actual de tu dispositivo IMBERA-OXXO");

                    //OXXO no tiene LOGGER
                    tvhandshake.setVisibility(View.VISIBLE);
                    tvLogg.setVisibility(View.GONE);
                    tvLogg1.setVisibility(View.GONE);
                    tvLogg2.setVisibility(View.GONE);
                    tvLogg3.setVisibility(View.GONE);
                    tvEvent.setVisibility(View.GONE);
                    tvTime.setVisibility(View.GONE);
                    tvRealState.setVisibility(View.VISIBLE);
                    if (!FinalListDataHandshake.isEmpty()){
                        tvhandshake.setText("MAC:" + FinalListDataHandshake.get(0)
                                + "\nModelo TREFPB:" + FinalListDataHandshake.get(1)
                                + "\nVersión:" + FinalListDataHandshake.get(2)
                                + "\nPlantilla:" + FinalListDataHandshake.get(3)+ "\n");
                    }else{
                        tvhandshake.setText("No se pudo obtener información Handshake de tu Trefp, por favor intenta reconectarte");
                    }
                    if (!FinalListDataRealState.isEmpty()){
                        if (FinalListDataRealState.get(0).equals("8")){
                            tvRealState.setText("\nTemperatura 1:" + FinalListDataRealState.get(1) + " °C"
                                    + "\nTemperatura 2:" + FinalListDataRealState.get(2) + " °C"
                                    + "\nVoltaje:" + FinalListDataRealState.get(3)
                                    + "\nActuadores:" + FinalListDataRealState.get(4)
                                    + "\nAlarmas:" + FinalListDataRealState.get(5)+ "\n"
                            );
                        }else{
                            tvRealState.setText("\nTemperatura 1:" + FinalListDataRealState.get(1) + " °C"
                                    + "\nTemperatura 2:" + FinalListDataRealState.get(2) + " °C"
                                    + "\nTemperatura 3:" + FinalListDataRealState.get(3) + " °C"
                                    + "\nVoltaje:" + FinalListDataRealState.get(4)
                                    + "\nActuadores:" + FinalListDataRealState.get(5)
                                    + "\nAlarmas:" + FinalListDataRealState.get(6)+ "\n"
                            );
                        }

                    }else
                        tvRealState.setText("No se pudo obtener información Estado en tiempo real de tu Trefp, por favor intenta reconectarte");

                }
            }else if(result.equals("false")){
                GlobalTools.showInfoPopup("Información","La comunicación no fue exitosa, reintenta o reconecta con el equipo TREFP",getContext());
            }



        }

        @Override
        protected void onPreExecute() {
            tvEvent.setText("");
            tvTime.setText("");
            createProgressDialog("Obteniendo logger...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private String convertInfoEstatus(){
        List<String> FinalListData = new ArrayList<String>() ;
        List<String> FinalListTest = new ArrayList<String>() ;
        String isChecksumOk;
        listData.clear();
        FinalListDataRealState.clear();
        try {
            if (sp.getString("trefpVersionName","").equals("IMBERA-TREFP") || sp.getString("trefpVersionName","").equals("OXXO-CMO")){
                bluetoothServices.sendCommand("handshake","4021");
                Thread.sleep(450);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                if(GetRealDataFromHexaImbera.cleanSpace(listData).toString().length() == 0){
                    return "false";
                }else{
                    isChecksumOk = GlobalTools.checkChecksumImberaTREFPB(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                    Log.d("ishandshake",":"+isChecksumOk);
                    if (isChecksumOk.equals("ok")){
                        if (listData.get(0).length() == 0){
                            FinalListDataHandshake.clear();
                            return "false";
                        }else{
                            FinalListData = GetRealDataFromHexaImbera.convert(listData, "Handshake",sp.getString("numversion",""), sp.getString("modelo",""));
                            FinalListDataHandshake = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Handshake",sp.getString("numversion",""),sp.getString("modelo",""));
                        }
                    }else{
                        return "false";
                    }

                    //if modelo actual es igual a 3.3 en adelante, entonces mostrar de forma de nuevo logger
                    Log.d("LOGGG2",":"+sp.getString("numversion",""));
                    Log.d("LOGGG2",":"+sp.getString("modelo",""));

                    FinalListData.clear();
                    FinalListData2.clear();
                    listData.clear();
                    bluetoothServices.sendCommand("realState","4053");
                    Thread.sleep(450);
                    listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                    Log.d("isrealtimeLISADATA:",":"+listData);
                    isChecksumOk = GlobalTools.checkChecksum(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                    Log.d("isrealtime",":"+isChecksumOk);
                    if (isChecksumOk.equals("ok")){
                        if (listData.get(0).length() == 0){
                            FinalListDataRealState.clear();
                            return "false";
                        }else{
                            FinalListData = GetRealDataFromHexaImbera.convert(listData, "Lectura de datos tipo Tiempo real",sp.getString("numversion",""), sp.getString("modelo",""));
                            FinalListDataRealState = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Tiempo real",sp.getString("numversion",""), sp.getString("modelo",""));
                        }
                    }else{
                        return "false";
                    }

                    /*if (sp.getString("modelo","").equals("3.3") && sp.getString("numversion","").equals("1.02")){

                    }else if (sp.getString("modelo","").equals("3.5") && sp.getString("numversion","").equals("1.04")){

                    }else{

                    }*/


                }


            }else if( sp.getString("trefpVersionName","").equals("IMBERA-OXXO")){
                bluetoothServices.sendCommand("handshake","4021");
                Thread.sleep(250);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                if(GetRealDataFromHexaImbera.cleanSpace(listData).toString().length() == 0){
                    return "false";
                }else{
                    isChecksumOk = GlobalTools.checkChecksum(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                    Log.d("ishandshake",":"+isChecksumOk);
                    if (isChecksumOk.equals("ok")){
                        if (listData.get(0).length() == 0){
                            FinalListDataHandshake.clear();
                            return "false";
                        }else{
                            FinalListData = GetRealDataFromHexaOxxoDisplay.convert(listData, "Handshake");
                            FinalListDataHandshake = GetRealDataFromHexaOxxoDisplay.GetRealData(FinalListData, "Handshake");
                        }
                    }else{
                        return "false";
                    }

                    isChecksumOk = GlobalTools.checkChecksum(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                    Log.d("isresltatus",":"+isChecksumOk);
                    if (isChecksumOk.equals("ok")){
                        FinalListData.clear();
                        FinalListData2.clear();
                        listData.clear();
                        bluetoothServices.sendCommand("realState","4053");
                        Thread.sleep(250);
                        listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                        if (listData.get(0).length() == 0){
                            FinalListDataRealState.clear();
                            return "false";
                        }else{
                            FinalListData = GetRealDataFromHexaOxxoDisplay.convert(listData, "Lectura de datos tipo Tiempo real");
                            FinalListDataRealState = GetRealDataFromHexaOxxoDisplay.GetRealData(FinalListData, "Lectura de datos tipo Tiempo real");
                        }
                    }else{
                        return "false";
                    }

                }
                //return "true";


            }

        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        return "true";

    }

    private String convertInfoLogger(){
        List<String> FinalListData = new ArrayList<String>() ;
        List<String> FinalListTest = new ArrayList<String>() ;
        String isChecksumOk;
        listData.clear();
        FinalListDataRealState.clear();
        try {
            if (sp.getString("trefpVersionName","").equals("IMBERA-TREFP") || sp.getString("trefpVersionName","").equals("OXXO-CMO")){

                    //if modelo actual es igual a 3.3 en adelante, entonces mostrar de forma de nuevo logger
                    Log.d("LOGGG2",":"+Double.parseDouble(sp.getString("numversion","")));
                    Log.d("LOGGG2",":"+Double.parseDouble(sp.getString("modelo","")));
                    if (sp.getString("modelo","").equals("3.3") && sp.getString("numversion","").equals("1.02")){
                        FinalListData.clear();
                        FinalListData2.clear();
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


                        for (int i=0; i<listData.size(); i++){//comprobación de la obtención de datos sin lista vacía
                            if (listData.get(i).length() !=0){
                                FinalListTest.add(listData.get(i));
                            }
                        }

                        if (FinalListTest.get(0).length() == 0){
                            FinalListDataTiempo.clear();
                            return "false";
                        }else{
                            FinalListData = GetRealDataFromHexaImbera.convert(FinalListTest, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                            FinalListDataTiempo = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                        }
                        /*isChecksumOk = GlobalTools.checkChecksumImberaTREFPBList(GetRealDataFromHexaImbera.cleanSpaceList(FinalListTest));
                        Log.d("istiempoChecksum",":"+isChecksumOk);
                        if (isChecksumOk.equals("ok")){
                            if (FinalListTest.get(0).length() == 0){
                                FinalListDataTiempo.clear();
                                return "false";
                            }else{
                                FinalListData = GetRealDataFromHexaImbera.convert(FinalListTest, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                                FinalListDataTiempo = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                            }
                        }else{
                            return "false";
                        }*/

                        FinalListTest.clear();
                        FinalListData.clear();
                        FinalListData2.clear();
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
                        if (FinalListTest.get(0).length() == 0){
                            FinalListDataEvento.clear();
                            return "false";
                        }else{
                            FinalListData = GetRealDataFromHexaImbera.convert(FinalListTest, "Lectura de datos tipo Evento",sp.getString("numversion",""), sp.getString("modelo",""));
                            FinalListDataEvento = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Evento",sp.getString("numversion",""),sp.getString("modelo",""));
                        }

                        /*
                        isChecksumOk = GlobalTools.checkChecksumImberaTREFPBList(GetRealDataFromHexaImbera.cleanSpaceList(FinalListTest));
                        Log.d("isevento",":"+isChecksumOk);
                        if (isChecksumOk.equals("ok")){

                            if (FinalListTest.get(0).length() == 0){
                                FinalListDataEvento.clear();
                                return "false";
                            }else{
                                FinalListData = GetRealDataFromHexaImbera.convert(FinalListTest, "Lectura de datos tipo Evento",sp.getString("numversion",""), sp.getString("modelo",""));
                                FinalListDataEvento = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Evento",sp.getString("numversion",""),sp.getString("modelo",""));
                            }
                        }else{
                            return "false";
                        }*/
                    }else if (sp.getString("modelo","").equals("3.5") && sp.getString("numversion","").equals("1.04")){
                        FinalListData.clear();
                        FinalListData2.clear();
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

                        if (FinalListTest.get(0).length() == 0){
                            FinalListDataTiempo.clear();
                            return "false";
                        }else{
                            FinalListData = GetRealDataFromHexaImbera.convert(FinalListTest, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                            FinalListDataTiempo = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                        }
                        /*isChecksumOk = GlobalTools.checkChecksumImberaTREFPBList(GetRealDataFromHexaImbera.cleanSpaceList(FinalListTest));
                        Log.d("istiempoChecksum",":"+isChecksumOk);
                        if (isChecksumOk.equals("ok")){
                            if (FinalListTest.get(0).length() == 0){
                                FinalListDataTiempo.clear();
                                return "false";
                            }else{
                                FinalListData = GetRealDataFromHexaImbera.convert(FinalListTest, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                                FinalListDataTiempo = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Tiempo",sp.getString("numversion",""), sp.getString("modelo",""));
                            }
                        }else{
                            return "false";
                        }*/

                        FinalListTest.clear();
                        FinalListData.clear();
                        FinalListData2.clear();
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
                        if (FinalListTest.get(0).length() == 0){
                            FinalListDataEvento.clear();
                            return "false";
                        }else{
                            FinalListData = GetRealDataFromHexaImbera.convert(FinalListTest, "Lectura de datos tipo Evento",sp.getString("numversion",""), sp.getString("modelo",""));
                            FinalListDataEvento = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Evento",sp.getString("numversion",""), sp.getString("modelo",""));
                        }
                        /*isChecksumOk = GlobalTools.checkChecksumImberaTREFPBList(GetRealDataFromHexaImbera.cleanSpaceList(FinalListTest));
                        Log.d("isevento",":"+isChecksumOk);
                        if (isChecksumOk.equals("ok")){

                            if (FinalListTest.get(0).length() == 0){
                                FinalListDataEvento.clear();
                                return "false";
                            }else{
                                FinalListData = GetRealDataFromHexaImbera.convert(FinalListTest, "Lectura de datos tipo Evento",sp.getString("numversion",""), sp.getString("modelo",""));
                                FinalListDataEvento = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Evento",sp.getString("numversion",""), sp.getString("modelo",""));
                            }
                        }else{
                            return "false";
                        }*/
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
                            //StringBuilder s = GetRealDataFromHexaImbera.cleanSpace(listData);
                            //listData.clear();
                            //listData = GetRealDataFromHexaImbera.divideCrudoTiempo(s.toString());
                            //Log.d("Datos tipo tiempo REPORTE crudo",":"+listData);
                            //Log.d("Datos tipo tiempo REPORTE",":"+FinalListDataTiempo);
                            //listener.createExcelTimeData("",FinalListDataTiempo);
                            //listener.createExcelTimeDataCrudo("",FinalListDataTiempo,listData);
                        }

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
                            //StringBuilder s = GetRealDataFromHexaImbera.cleanSpace(listData);
                            //listData.clear();
                            //listData = GetRealDataFromHexaImbera.divideCrudoEvento(s.toString());
                            Log.d("Datos tipo tiempo REPORTE",":"+FinalListDataEvento);
                            //listener.createExcelEventData("",FinalListDataEvento);
                            //listener.createExcelEventDataCrudo("",FinalListDataTiempo,listData);
                        }
                    }

                //}


            }else if( sp.getString("trefpVersionName","").equals("IMBERA-OXXO")){
                bluetoothServices.sendCommand("handshake","4021");
                Thread.sleep(250);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                if(GetRealDataFromHexaImbera.cleanSpace(listData).toString().length() == 0){
                    return "false";
                }else{
                    isChecksumOk = GlobalTools.checkChecksum(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                    Log.d("ishandshake",":"+isChecksumOk);
                    if (isChecksumOk.equals("ok")){
                        if (listData.get(0).length() == 0){
                            FinalListDataHandshake.clear();
                            return "false";
                        }else{
                            FinalListData = GetRealDataFromHexaOxxoDisplay.convert(listData, "Handshake");
                            FinalListDataHandshake = GetRealDataFromHexaOxxoDisplay.GetRealData(FinalListData, "Handshake");
                        }
                    }else{
                        return "false";
                    }

                    isChecksumOk = GlobalTools.checkChecksum(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                    Log.d("isresltatus",":"+isChecksumOk);
                    if (isChecksumOk.equals("ok")){
                        FinalListData.clear();
                        FinalListData2.clear();
                        listData.clear();
                        bluetoothServices.sendCommand("realState","4053");
                        Thread.sleep(250);
                        listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                        if (listData.get(0).length() == 0){
                            FinalListDataRealState.clear();
                            return "false";
                        }else{
                            FinalListData = GetRealDataFromHexaOxxoDisplay.convert(listData, "Lectura de datos tipo Tiempo real");
                            FinalListDataRealState = GetRealDataFromHexaOxxoDisplay.GetRealData(FinalListData, "Lectura de datos tipo Tiempo real");
                        }
                    }else{
                        return "false";
                    }

                }
                //return "true";


            }

        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        return "true";

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
}
