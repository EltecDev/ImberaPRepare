package mx.eltec.imberatrefp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mx.eltec.imberatrefp.BluetoothServices.BluetoothLeService;
import mx.eltec.imberatrefp.BluetoothServices.BluetoothServices;
import mx.eltec.imberatrefp.Utility.GetRealDataFromHexaImbera;
import mx.eltec.imberatrefp.Utility.GetRealDataFromHexaOxxoDisplay;
import mx.eltec.imberatrefp.Utility.GlobalTools;

/**
 * TODO:
 *
 * */
public class ConexionTrefp {

    androidx.appcompat.app.AlertDialog progressdialog=null;
    View dialogViewProgressBar;

    String deviceMacAddress;
    ListenerTREFP listenerTREFP;

    String name = "IMBERA-TREFP";

    //Bluetooth Services
    BluetoothLeService bluetoothLeService;

    ArrayList<String> listData = new ArrayList<String>() ;
    ArrayList<String> FinalListDataEvento = new ArrayList<String>() ;
    ArrayList<String> FinalListDataTiempo = new ArrayList<String>() ;



    public BluetoothServices bluetoothServices;

    public ConexionTrefp(Context context){
        bluetoothServices = new BluetoothServices(context);
    }

    public void connect(String deviceMacAddress){
        new MyAsyncTaskConnectBLETrefp(deviceMacAddress).execute();
    }

    public void getTREFPBLEHandshake(){
        new MyAsyncTaskGetHandshakeTREFP().execute();
    }
    public void getTREFPBLETiempo(){
        new MyAsyncTaskGetTiempoTrefpb().execute();
    }
    public void getTREFPBLEEvento(){
        new MyAsyncTaskGetEventoTrefpb().execute();
    }


    public void getOXXOHandshake(){
        new MyAsyncTaskGetHandshakeCEO().execute();
    }

    public void getTREFPBLERealTimeStatus(){
        new MyAsyncTaskGetActualStatusTREFP().execute();
    }

    public void getCEORealTimeStatus(){
        new MyAsyncTaskGetActualStatusCEO().execute();
    }

    public void isConnectionAlive(){
        if (bluetoothLeService==null){
            listenerTREFP.isConnectionAlive("Desconectado");
        }else{
            listenerTREFP.isConnectionAlive("Conectado");
        }
        //new MyAsyncTaskGetHandshakeTREFPCheckConnection().execute();
    }

    public void desconectar(){
        bluetoothServices.disconnect();
        bluetoothLeService=null;
    }

    class MyAsyncTaskConnectBLETrefp extends AsyncTask<Integer, Integer, String> {
        String mac="";
        public MyAsyncTaskConnectBLETrefp(String  mac) {
            this.mac =mac;
        }

        @Override
        protected String doInBackground(Integer... params) {
            bluetoothServices.connect("TREFP-IMBERA",mac);
            try {
                Thread.sleep(2000);
                bluetoothLeService = bluetoothServices.getBluetoothLeService();
                if (bluetoothLeService == null){
                    return "notConnected";
                }else{
                    return "connected";
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "noConnected";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("connected")){
                Log.d("CONNECTED","OK");
            }else{
                listenerTREFP.onError("Error al intentar conectar con:"+mac);
            }

        }

        @Override
        protected void onPreExecute() {
            //createProgressDialog("Conectando con el dispositivo...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskGetHandshakeCEO extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            bluetoothLeService = bluetoothServices.getBluetoothLeService();
            if (bluetoothLeService.sendFirstComando("4021")){
                Log.d("","dataChecksum total:7");
                return "ok";
            }else
                Log.d("","dataChecksum total:8");
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

            List<String> listData = new ArrayList<String>();
            List<String> FinalListData = new ArrayList<String>();
            listData = bluetoothLeService.getDataFromBroadcastUpdate();

            if (result.equals("noconnected")) {
                listData.add("noconnected");
            }else {
                if (result.equals("ok")){
                    if (!listData.isEmpty()){
                        Log.d("listdata",":"+listData);
                            String isChecksumOk = GlobalTools.checkChecksum(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                            if (isChecksumOk.equals("ok")){
                                FinalListData = GetRealDataFromHexaImbera.convert(listData, "Handshake");
                                listData = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Handshake");
                                listenerTREFP.getInfo(listData);
                            }else if (isChecksumOk.equals("notFirmware")){
                                listData.add("notFirmware");
                                listenerTREFP.getInfo(listData);
                            }else if (isChecksumOk.equals("notok")){
                                listData.add("notok");
                                listenerTREFP.getInfo(listData);
                            }
                    }else{
                        listData.add("No se pudo obtener los datos, ¿estás conectado?");
                        listenerTREFP.getInfo(listData);
                    }
                }else{
                    listData.add("Fallo al conectar a un BLE");
                    listenerTREFP.getInfo(listData);
                }

            }

        }

        @Override
        protected void onPreExecute() {
            //createProgressDialog("Obteniendo primera comunicación...");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskGetHandshakeTREFP extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            bluetoothLeService = bluetoothServices.getBluetoothLeService();
            if (bluetoothLeService.sendFirstComando("4021")){
                Log.d("","dataChecksum total:7");
                return "ok";
            }else
                Log.d("","dataChecksum total:8");
            return "not";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<String> listData = new ArrayList<String>();
            List<String> FinalListData = new ArrayList<String>();
            listData = bluetoothLeService.getDataFromBroadcastUpdate();

            if (result.equals("noconnected")) {
                listData.add("noconnected");
                listenerTREFP.getInfo(listData);
            }else {

                if (result.equals("ok")){
                    if (!listData.isEmpty()){

                        String isChecksumOk = GlobalTools.checkChecksumImberaTREFPB(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                        if (isChecksumOk.equals("ok")){
                            //FinalListData = GetRealDataFromHexaImbera.convert(listData, "Handshake");
                            //listData = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Handshake");
                            listenerTREFP.getInfo(listData);
                        }else if (isChecksumOk.equals("notFirmware")){
                            listData.add("noFirmware");
                            listenerTREFP.getInfo(listData);
                        }else if (isChecksumOk.equals("notok")){
                            listData.add("notOkChecksum");
                            listenerTREFP.getInfo(listData);
                        }
                    }else{
                        listData.add("No se pudo obtener información, ¿estás conectado?");
                        listenerTREFP.getInfo(listData);
                    }
                }else{
                    listData.add("Fallo al conectar a un BLE");
                    listenerTREFP.getInfo(listData);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            //createProgressDialog("Obteniendo primera comunicación...");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskGetTiempoTrefpb extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            convertInfoLoggerTiempo();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            listenerTREFP.getInfo(FinalListDataTiempo);
        }

        @Override
        protected void onPreExecute() {
            //createProgressDialog("Obteniendo primera comunicación...");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskGetEventoTrefpb extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            convertInfoLoggerEvento();
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            listenerTREFP.getInfo(FinalListDataEvento);

        }

        @Override
        protected void onPreExecute() {
            //createProgressDialog("Obteniendo primera comunicación...");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }



    class MyAsyncTaskGetHandshakeTREFPCheckConnection extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            bluetoothLeService = bluetoothServices.getBluetoothLeService();
            if (bluetoothLeService == null){
                return "not";
            }else{
                return "ok";
            }
            /*if (bluetoothLeService.sendFirstComando("4021")){
                Log.d("","dataChecksum total:7");
                return "ok";
            }else
                Log.d("","dataChecksum total:8");
            return "not";*/
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (result.equals("ok")){
                listenerTREFP.isConnectionAlive("Conectado");
            }else{
                listenerTREFP.isConnectionAlive("Desconectado");
            }
            /*byte[] listData;
            listData = bluetoothLeService.getDataFromBroadcastUpdateByte();
            listenerTREFP.getInfoBytes(listData);*/
            /*if (result.equals("noconnected")) {
                listData.add("noconnected");
                listenerTREFP.getInfo(listData);
            }else {*/

                /*if (result.equals("ok")){
                    if (!listData.isEmpty()){

                        String isChecksumOk = GlobalTools.checkChecksumImberaTREFPB(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                        if (isChecksumOk.equals("ok")){
                            //FinalListData = GetRealDataFromHexaImbera.convert(listData, "Handshake");
                            //listData = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Handshake");
                            listenerTREFP.getInfo(listData);
                        }else if (isChecksumOk.equals("notFirmware")){
                            listData.add("noFirmware");
                            listenerTREFP.getInfo(listData);
                        }else if (isChecksumOk.equals("notok")){
                            listData.add("notOkChecksum");
                            listenerTREFP.getInfo(listData);
                        }
                    }else{
                        //listData.add("No se pudo obtener información, ¿estás conectado?");
                        listenerTREFP.getInfo(listData);
                    }
                }else{
                    //listData.add("Fallo al conectar a un BLE");
                    listenerTREFP.getInfo(listData);
                }*/
            //}
        }

        @Override
        protected void onPreExecute() {
            //createProgressDialog("Obteniendo primera comunicación...");
        }
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskGetActualStatusCEO extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            bluetoothLeService = bluetoothServices.getBluetoothLeService();
            if (bluetoothLeService.sendFirstComando("4053")){
                Log.d("","dataChecksum total:7");
                return "ok";
            }else
                Log.d("","dataChecksum total:8");
            return "not";

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<String> listData = new ArrayList<String>();
            List<String> FinalListData = new ArrayList<String>();
            listData = bluetoothLeService.getDataFromBroadcastUpdate();

            if (result.equals("noconnected")) {
                listData.add("Problemas de conexión, reconecta a tu BLE");
                listenerTREFP.getInfo(listData);
            }else {
                if (result.equals("ok")){
                    if (!listData.isEmpty()){
                        Log.d("listdata",":"+listData);
                        String isChecksumOk = GlobalTools.checkChecksum(GetRealDataFromHexaOxxoDisplay.cleanSpace(listData).toString());
                        if (isChecksumOk.equals("ok")){
                            FinalListData = GetRealDataFromHexaOxxoDisplay.convert(listData, "Lectura de datos tipo Tiempo real");
                            listData = GetRealDataFromHexaOxxoDisplay.GetRealData(FinalListData, "Lectura de datos tipo Tiempo real");
                            listenerTREFP.getInfo(listData);
                        }else if (isChecksumOk.equals("notFirmware")){
                            listenerTREFP.getInfo(listData);
                        }else if (isChecksumOk.equals("notok")){
                            listenerTREFP.getInfo(listData);

                        }
                    }else{
                        listData.add("Fallo al conectar a un BLE ¿Estás conectado?");
                        listenerTREFP.getInfo(listData);

                    }

                }else{
                    listData.add("Fallo al conectar a un BLE");
                    listenerTREFP.getInfo(listData);

                }
            }

        }

        @Override
        protected void onPreExecute() {
            Log.d("ConexiónTREFP",":Iniciando conexión");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskGetActualStatusTREFP extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            bluetoothLeService = bluetoothServices.getBluetoothLeService();
            if (bluetoothLeService.sendFirstComando("4053")){
                Log.d("","dataChecksum total:7");
                return "ok";
            }else
                Log.d("","dataChecksum total:8");
            return "not";

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<String> listData = new ArrayList<String>();
            List<String> FinalListData = new ArrayList<String>();
            listData = bluetoothLeService.getDataFromBroadcastUpdate();
            if (result.equals("noconnected")) {
                listData.add("Problemas de conexiòn, reconecta a tu BLE");
                listenerTREFP.getInfo(listData);
            }else {
                if (result.equals("ok")){
                    if (!listData.isEmpty()){
                        String isChecksumOk = GlobalTools.checkChecksumImberaTREFPB(GetRealDataFromHexaImbera.cleanSpace(listData).toString());
                        if (isChecksumOk.equals("ok")){
                            FinalListData = GetRealDataFromHexaImbera.convert(listData, "Lectura de datos tipo Tiempo real");
                            listData = GetRealDataFromHexaImbera.GetRealData(FinalListData, "Lectura de datos tipo Tiempo real");
                            listenerTREFP.getInfo(listData);
                        }else if (isChecksumOk.equals("notFirmware")){
                            listData.add("notFirmware");
                            listenerTREFP.getInfo(listData);
                        }else if (isChecksumOk.equals("notok")){
                            listData.add("notok");
                            listenerTREFP.getInfo(listData);
                        }
                    }else{
                        listData.add("No se pudo obtener información, ¿estás conectado?");
                        listenerTREFP.getInfo(listData);
                    }
                }else{
                    listData.add("Fallo al conectar a un BLE");
                    listenerTREFP.getInfo(listData);
                }
            }
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    public interface ListenerTREFP{
        public void onError(String error);
        public void isConnectionAlive(String resp);
        public void getInfo(List<String> data);
        public void getInfoBytes(byte[] data);
    }

    private String convertInfoLoggerTiempo(){
        List<String> FinalListData = new ArrayList<String>() ;
        List<String> FinalListTest = new ArrayList<String>() ;
        String isChecksumOk;
        listData.clear();
        try {
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
                    FinalListDataTiempo.add(listData.get(i));
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        return "true";

    }

    private String convertInfoLoggerEvento(){
        List<String> FinalListData = new ArrayList<String>() ;
        List<String> FinalListTest = new ArrayList<String>() ;
        String isChecksumOk;
        listData.clear();
        try {
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
                    FinalListDataEvento.add(listData.get(i));
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        return "true";

    }

    public void ConexionTREFPListener(ListenerTREFP listenerTREFP) {
        this.listenerTREFP = listenerTREFP;
    }
}