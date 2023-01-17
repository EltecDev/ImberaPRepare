package com.example.imberap.Fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.imberap.BluetoothServices.BluetoothLeService;
import com.example.imberap.BluetoothServices.BluetoothServices;
import com.example.imberap.Clasesdata.BLEDevices;
import com.example.imberap.R;
import com.example.imberap.adapters.RecyclerViewBLEList;
import com.example.imberap.Utility.GetRealDataFromHexaImbera;
import com.example.imberap.Utility.GlobalTools;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad principal. Muestra una lista de dispositivos BLE TREFP cercanos
 */
public class ListaBLEFragment extends Fragment {

    String name="";

    private BluetoothServices bluetoothServices;
    RecyclerViewBLEList recyclerViewBLEList;
    connectListener connectListener;

    androidx.appcompat.app.AlertDialog progressdialog=null;
    View dialogViewProgressBar, v;
    TextView tvConnectionState, tvfwversion;

    //Pantalla de peticion inicial de permisos
    SharedPreferences sp;
    SharedPreferences.Editor esp;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeService bluetoothLeService;
    private boolean mScanning;
    private Handler mHandler;
    private RecyclerView recyclerView;
    ArrayList<BLEDevices> listaDevices;
    // Se escanea 1 segundo en total
    private static final long SCAN_PERIOD = 1000;

    // Callback del escaneo BLE.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Filtro de dispositivos TREFP
                    if (device.getName()==null){
                        //listaDevices.add(new BLEDevices("","", null));

                    }else{
                        if (device.getName().equals("IMBERA_RUTA_FRIA") || device.getName().contains("IMBERA-TREFP") || device.getName().contains("IMBERA-OXXO") || device.getName().contains("OXXO-CMO")|| device.getName().contains("IMBERA-WF")){
                            if (listaDevices.isEmpty()){
                                listaDevices.add(new BLEDevices(device.getName(),device.getAddress(), null));
                            }else {
                                listaDevices.add(new BLEDevices(device.getName(),device.getAddress(), null));
                            }
                        }

                    }
                }
            });
        }
    };

    private void sortListBLE(){
        if (listaDevices.size()>1){
            for (int i=0; i<listaDevices.size(); i++){
                String mac = listaDevices.get(i).getMac();
                for (int j=i+1; j<listaDevices.size(); j++){
                    String mac2 = listaDevices.get(j).getMac();
                    if (mac.equals(mac2)){
                        listaDevices.remove(j);
                        j--;
                    }
                }
            }
        }


    }

    public ListaBLEFragment(){}

    public ListaBLEFragment(BluetoothServices bluetoothServices,TextView tv, TextView tvv){
        this.tvConnectionState = tv;
        this.tvfwversion = tvv;
        this.bluetoothServices = bluetoothServices;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_list_ble_devices, container, false);
        init(view);
        view.findViewById(R.id.btnEscanear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothServices.isBluetoothEnabled()){
                    Toast.makeText(getContext(), "Bluetooth desactivado", Toast.LENGTH_SHORT).show();
                }else{
                    if (connectListener.isPermissionEnabled()){
                        new MyAsyncTaskScanBLEdevices().execute();
                    }else{
                        connectListener.requestPemission();
                    }
                }
            }
        });
        view.findViewById(R.id.btnDesconect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalTools.changeScreenConnectionStatus(tvConnectionState,sp);
                new MyAsyncTaskDesconnectBLE().execute();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        if (!sp.getString("userId","").equals("")){//si no hay usuario logeado entonces no escanear
            if (connectListener.isPermissionEnabled()){
                if (sp.getBoolean("permissionGiven",false)){
                    if(!sp.getBoolean("isconnected",false)){
                        new MyAsyncTaskScanBLEdevices().execute();
                    }
                }
            }else{
                connectListener.requestPemission();
            }
        }

        super.onStart();
    }

    private void init(View view){
        //Inicializar variables
        sp = getContext().getSharedPreferences("connection_preferences", Context.MODE_PRIVATE);
        esp = sp.edit();

        mHandler = new Handler();
        mBluetoothAdapter = bluetoothServices.getBluetoothAdapter();
        listaDevices = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rvbleDevices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewBLEList = new RecyclerViewBLEList(listaDevices);
        recyclerView.setAdapter(recyclerViewBLEList);
    }

    //Metodo que inicia el escaneo
    private void scanLeDevice(final boolean enable) {
        //Reiniciar Lista de ble
        listaDevices.clear();
        //Si bluetooth activo entonces...proceder escaner
        if (enable) {
            // Se detiene el escaneo luego de 1 segundo(s)
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    recyclerViewBLEList = null;
                    //sortListDevices();//elimina los dispositivos repetidos del escaneo por filtro de MAC
                    sortListBLE();
                    recyclerViewBLEList = new RecyclerViewBLEList(listaDevices);
                    recyclerView.setAdapter(recyclerViewBLEList);
                    recyclerViewBLEList.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (sp.getBoolean("isconnected",false)){
                                Toast.makeText(getContext(), "Ya estás conectado a un BLE", Toast.LENGTH_SHORT).show();
                            }else{
                                new MyAsyncTaskConnectBLE(v).execute();
                            }
                        }
                    });
                }
            }, SCAN_PERIOD);
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);

        }
    }

    class MyAsyncTaskConnectBLE extends AsyncTask<Integer, Integer, String> {
        View v;
        public MyAsyncTaskConnectBLE(View v) {
            this.v=v;
        }

        @Override
        protected String doInBackground(Integer... params) {
            name = listaDevices.get(recyclerView.getChildAdapterPosition(v)).getNombre();
            connectListener.connectBLE(listaDevices.get(recyclerView.getChildAdapterPosition(v)).getNombre(),listaDevices.get(recyclerView.getChildAdapterPosition(v)).getMac());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "resp";
        }

        @Override
        protected void onPostExecute(String result) {
                if (progressdialog != null){
                    progressdialog.dismiss();
                    progressdialog=null;
                }
                new MyAsyncTaskGetHandshake().execute();
                //esp.putBoolean("isconnected",true);
                //esp.apply();
        }

        @Override
        protected void onPreExecute() {
            createProgressDialog("Conectando con el dispositivo...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskDesconnectBLE extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(400);
                if (bluetoothServices.getBluetoothLeService()!=null){
                    connectListener.disconnectBLE();
                    return "resp";
                }else
                    return "noconexion";
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "resp";
            }


        }

        @Override
        protected void onPostExecute(String result) {
            if (progressdialog != null)progressdialog.dismiss();
            progressdialog=null;
            if (result.equals("noconexion")){
                Toast.makeText(getContext(), "No estás conectado a ningún BLE", Toast.LENGTH_SHORT).show();
                esp.putBoolean("isconnected",false);
                esp.apply();
            }else{
                //Toast.makeText(getContext(), "Te has desconectado de BLE", Toast.LENGTH_SHORT).show();
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvfwversion.setText("");
                        tvConnectionState.setText("Desconectado");
                        tvConnectionState.setTextColor(Color.BLACK);
                        esp.putString("trefpVersionName","");
                        esp.apply();
                    }
                });

                esp.putBoolean("isconnected",false);
                esp.apply();
            }
        }

        @Override
        protected void onPreExecute() {
            createProgressDialog("Desconectando del dispositivo...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskScanBLEdevices extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {

            scanLeDevice(true);
            return "resp";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(600);
                if (progressdialog != null)progressdialog.dismiss();
                progressdialog=null;


            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onPreExecute() {
            createProgressDialog("Buscando dispositivos...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskGetHandshake extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            if (sp.getBoolean("isconnected",false)){
                bluetoothLeService = bluetoothServices.getBluetoothLeService();
                if (bluetoothLeService.sendFirstComando("4021")){
                    Log.d("","dataChecksum total:7");
                    return "ok";
                }else
                    Log.d("","dataChecksum total:8");
                    return "not";
            }else
                return "noconnected";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (progressdialog != null)progressdialog.dismiss();
            progressdialog=null;

            if (result.equals("noconnected")) {
                Toast.makeText(getContext(), "Problemas de conexión, reconecta a tu BLE", Toast.LENGTH_SHORT).show();
            }else {
                if (result.equals("ok")){
                    List<String> listData = new ArrayList<String>();
                    List<String> FinalListData = new ArrayList<String>();
                    listData = bluetoothLeService.getDataFromBroadcastUpdate();

                    if (!listData.isEmpty()){
                        Log.d("listdataHandshake",":"+listData);
                        /*if(name.equals("IMBERA-TREFP")){
                            String isChecksumOk = GlobalTools.checkChecksumImberaTREFPB(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                            if (isChecksumOk.equals("ok")){
                                FinalListData = GetRealDataFromHexaImbera.convert(listData, "Handshake","","");
                                listData = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Handshake","","");
                                tvfwversion.setText("Modelo TREFPB:" + listData.get(1)
                                        + "\nVersión:" + listData.get(2)
                                        + "\nPlantilla:" + listData.get(3));
                                esp.putString("modelo",listData.get(1));
                                esp.putString("numversion",listData.get(2));
                                esp.putString("plantillaVersion",listData.get(3));
                                esp.putString("trefpVersionName",name);
                                esp.apply();
                            }else if (isChecksumOk.equals("notFirmware")){
                                //Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                                GlobalTools.showInfoPopup("Información del equipo","Tu control BLE no está respondiendo como se esperaba, intenta de nuevo o contacta a personal autorizado. (NFW)",getContext());
                                esp.putString("trefpVersionName","");
                                esp.putString("numversion","");
                                esp.putString("plantillaVersion","");
                                esp.apply();
                                desconectarBLE();
                            }else if (isChecksumOk.equals("notok")){
                                GlobalTools.showInfoPopup("Información del equipo","Tu control BLE no está respondiendo como se esperaba, intenta de nuevo o contacta a personal autorizado. (CHKSM)",getContext());
                                esp.putString("trefpVersionName","");
                                esp.putString("numversion","");
                                esp.putString("plantillaVersion","");
                                esp.apply();
                                desconectarBLE();
                            }
                        }else if (name.equals("IMBERA-OXXO")){
                            String isChecksumOk = GlobalTools.checkChecksum(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                            if (isChecksumOk.equals("ok")){
                                FinalListData = GetRealDataFromHexaImbera.convert(listData, "Handshake","","");
                                listData = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Handshake","","");
                                tvfwversion.setText("Modelo TREFPB:" + listData.get(1)
                                        + "\nVersión:" + listData.get(2)
                                        + "\nPlantilla:" + listData.get(3));
                                esp.putString("modelo",listData.get(1));
                                esp.putString("numversion",listData.get(2));
                                esp.putString("plantillaVersion",listData.get(3));
                                esp.putString("trefpVersionName",name);
                                esp.apply();
                            }else if (isChecksumOk.equals("notFirmware")){
                                //Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                                GlobalTools.showInfoPopup("Información del equipo","Tu control BLE no está respondiendo como se esperaba, intenta de nuevo o contacta a personal autorizado. (NFW)",getContext());
                                esp.putString("trefpVersionName","");
                                esp.putString("numversion","");
                                esp.putString("plantillaVersion","");
                                esp.apply();
                                desconectarBLE();
                            }else if (isChecksumOk.equals("notok")){
                                GlobalTools.showInfoPopup("Información del equipo","Tu control BLE no está respondiendo como se esperaba, intenta de nuevo o contacta a personal autorizado. (CHKSM)",getContext());
                                esp.putString("trefpVersionName","");
                                esp.putString("numversion","");
                                esp.putString("plantillaVersion","");
                                esp.apply();
                                desconectarBLE();
                            }
                        }else if (name.equals("IMBERA-WF")){*/
                            String isChecksumOk = GlobalTools.checkChecksum(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                            if (isChecksumOk.equals("ok")){
                                FinalListData = GetRealDataFromHexaImbera.convert(listData, "Handshake","","");
                                listData = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Handshake","","");
                                tvfwversion.setText("Modelo:" + listData.get(1)
                                        + "\nVersión FW:" + listData.get(2)
                                        + "\nPlantilla:" + listData.get(3));
                                esp.putString("modelo",listData.get(1));
                                esp.putString("numversion",listData.get(2));
                                esp.putString("plantillaVersion",listData.get(3));
                                esp.putString("trefpVersionName",name);
                                esp.apply();
                            }else if (isChecksumOk.equals("notFirmware")){
                                //Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                                GlobalTools.showInfoPopup("Información del equipo","Tu control BLE no está respondiendo como se esperaba, intenta de nuevo o contacta a personal autorizado. (NFW)",getContext());
                                esp.putString("trefpVersionName","");
                                esp.putString("numversion","");
                                esp.putString("plantillaVersion","");
                                esp.apply();
                                desconectarBLE();
                            }else if (isChecksumOk.equals("notok")){
                                GlobalTools.showInfoPopup("Información del equipo","Tu control BLE no está respondiendo como se esperaba, intenta de nuevo o contacta a personal autorizado. (CHKSM)",getContext());
                                esp.putString("trefpVersionName","");
                                esp.putString("numversion","");
                                esp.putString("plantillaVersion","");
                                esp.apply();
                                desconectarBLE();
                            }
                        //}


                    }else{
                        Toast.makeText(getContext(), "No se pudo obtener primera comunicación", Toast.LENGTH_SHORT).show();
                        esp.putString("trefpVersionName","");
                        esp.putString("numversion","");
                        esp.putString("plantillaVersion","");
                        esp.apply();
                        desconectarBLE();
                    }

                }else{
                    Toast.makeText(getContext(), "Fallo al conectar a un BLE", Toast.LENGTH_SHORT).show();
                    desconectarBLE();
                }

            }

        }

        @Override
        protected void onPreExecute() {
            createProgressDialog("Obteniendo primera comunicación...");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
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

    public void desconectarBLE(){
        connectListener.disconnectBLE();
        GlobalTools.changeScreenConnectionStatus(tvConnectionState,sp);
    }


    //Metodos MainActivity
    public interface connectListener {
        public void connectBLE(String name,String mac);
        public void disconnectBLE();
        public boolean isPermissionEnabled();
        public void requestPemission();
    }

    //Listeners
    public void setconnectListenerListener(connectListener callback) {
        this.connectListener = callback;
    }


}