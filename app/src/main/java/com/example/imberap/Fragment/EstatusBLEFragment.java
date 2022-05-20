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
import com.example.imberap.utility.GetRealDataFromHexaImbera;
import com.example.imberap.utility.GetRealDataFromHexaOxxo;
import com.example.imberap.utility.GetRealDataFromHexaOxxoDisplay;

import java.util.ArrayList;
import java.util.List;

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

        view.findViewById(R.id.btnGetCurrentdata).setOnClickListener(new View.OnClickListener() {
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

    class MyAsyncTaskGetActualStatus extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            convertInfo();
            return "resp";
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressdialog != null)progressdialog.dismiss();
            progressdialog=null;

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

                tvhandshake.setText("MAC:" + FinalListDataHandshake.get(0)
                        + "\nModelo TREFPB:" + FinalListDataHandshake.get(1)
                        + "\nVersión:" + FinalListDataHandshake.get(2)
                        + "\nPlantilla:" + FinalListDataHandshake.get(3));



                StringBuilder stringb = new StringBuilder();
                int j=1;
                for (int i=0;i<FinalListDataTiempo.size(); i+=4){
                    if (i+3>FinalListDataTiempo.size()){
                        break;
                    }   else {
                        stringb.append("\nIteración "+j+
                                "\nTimeStamp:" + FinalListDataTiempo.get(i)
                                +"\nTemperatura 1:" + FinalListDataTiempo.get(i+1) + "C°"
                                + "\nTemperatura 2:" + FinalListDataTiempo.get(i+2) + "C°"
                                + "\nVoltaje:" + FinalListDataTiempo.get(i+3));
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
                                +"\nTipo de evento:" + FinalListDataEvento.get(i+2)
                                +"\nTemperatura 1I:" + FinalListDataEvento.get(i+3) + " C°"
                                + "\nTemperatura 2F:" + FinalListDataEvento.get(i+4) + " C°"
                                + "\nVoltaje:" + FinalListDataEvento.get(i+5));
                        j++;
                    }
                }
                tvEvent.setText(stringa.toString());

                tvRealState.setText("\nTemperatura 1:" + FinalListDataRealState.get(0) + " C°"
                        + "\nTemperatura 2:" + FinalListDataRealState.get(1) + " C°"
                        + "\nVoltage:" + FinalListDataRealState.get(2)
                        + "\nActuadores:" + FinalListDataRealState.get(3)
                        + "\nAlarmas:" + FinalListDataRealState.get(4)
                );
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
                            + "\nPlantilla:" + FinalListDataHandshake.get(3));
                }else{
                    tvhandshake.setText("No se pudo obtener información Handshake de tu Trefp, por favor intenta reconectarte");
                }
                if (!FinalListDataRealState.isEmpty()){
                    tvRealState.setText("\nTemperatura 1:" + FinalListDataRealState.get(0) + " C°"
                                    + "\nTemperatura 2:" + FinalListDataRealState.get(1) + " C°"
                                    + "\nVoltage:" + FinalListDataRealState.get(2)
                                    + "\nActuadores:" + FinalListDataRealState.get(3)
                            + "\nAlarmas:" + FinalListDataRealState.get(4)
                    );
                }else
                    tvRealState.setText("No se pudo obtener información Estado en tiempo real de tu Trefp, por favor intenta reconectarte");

            }


        }

        @Override
        protected void onPreExecute() {
            tvhandshake.setText("");
            tvRealState.setText("");
            tvEvent.setText("");
            tvTime.setText("");
            createProgressDialog("Obteniendo estado actual...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private void convertInfo(){
        List<String> FinalListData = new ArrayList<String>() ;
        listData.clear();
        FinalListDataRealState.clear();
        try {
            if (sp.getString("trefpVersionName","").equals("IMBERA-TREFP")){
                bluetoothServices.sendCommand("handshake","4021");
                Thread.sleep(150);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                if (listData.get(0).length() == 0){
                    FinalListDataHandshake.clear();
                }else{
                    FinalListData = GetRealDataFromHexaImbera.convert(listData, "Handshake");
                    FinalListDataHandshake = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Handshake");
                }

                FinalListData.clear();
                FinalListData2.clear();
                listData.clear();
                bluetoothServices.sendCommand("time","4060");
                Thread.sleep(150);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                if (listData.get(0).length() == 0){
                    FinalListDataTiempo.clear();
                }else{
                    FinalListData = GetRealDataFromHexaImbera.convert(listData, "Lectura de datos tipo Tiempo");
                    FinalListDataTiempo = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Tiempo");
                }

                FinalListData.clear();
                FinalListData2.clear();
                listData.clear();
                bluetoothServices.sendCommand("event","4061");
                Thread.sleep(150);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                if (listData.get(0).length() == 0){
                    FinalListDataEvento.clear();
                }else{
                    FinalListData = GetRealDataFromHexaImbera.convert(listData, "Lectura de datos tipo Evento");
                    FinalListDataEvento = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Evento");
                }

                FinalListData.clear();
                FinalListData2.clear();
                listData.clear();
                bluetoothServices.sendCommand("realState","4053");
                Thread.sleep(150);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                if (listData.get(0).length() == 0){
                    FinalListDataRealState.clear();
                }else{
                    FinalListData = GetRealDataFromHexaImbera.convert(listData, "Lectura de datos tipo Tiempo real");
                    FinalListDataRealState = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Tiempo real");
                }

            }else if( sp.getString("trefpVersionName","").equals("IMBERA-OXXO")){
                bluetoothServices.sendCommand("handshake","4021");
                Thread.sleep(150);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());

                if (listData.get(0).length() == 0){
                    FinalListDataHandshake.clear();
                }else{
                    FinalListData = GetRealDataFromHexaOxxoDisplay.convert(listData, "Handshake");
                    FinalListDataHandshake = GetRealDataFromHexaOxxoDisplay.GetRealData(FinalListData, "Handshake");
                }

                FinalListData.clear();
                FinalListData2.clear();
                listData.clear();


                bluetoothServices.sendCommand("realState","4053");
                Thread.sleep(150);
                listData.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                if (listData.get(0).length() == 0){
                    FinalListDataRealState.clear();
                }else{
                    FinalListData = GetRealDataFromHexaOxxoDisplay.convert(listData, "Lectura de datos tipo Tiempo real");
                    FinalListDataRealState = GetRealDataFromHexaOxxoDisplay.GetRealData(FinalListData, "Lectura de datos tipo Tiempo real");
                }



            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Log.d("handhand",":"+FinalListDataRealState);
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
