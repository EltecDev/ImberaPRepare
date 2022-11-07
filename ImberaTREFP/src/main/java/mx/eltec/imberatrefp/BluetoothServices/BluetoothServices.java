package mx.eltec.imberatrefp.BluetoothServices;

import static android.content.Context.BIND_AUTO_CREATE;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class BluetoothServices {
    SharedPreferences sp;
    SharedPreferences.Editor esp;
    String name, mac,titulo="",command="";
    String[] data;
    List<String> listData = new ArrayList<String>();
    List<String> FinalListData  = new ArrayList<String>();
    List<String> FinalList  = new ArrayList<String>();

    Intent gattServiceIntent;

    AlertDialog progressdialog=null;
    View dialogViewProgressBar;

    BluetoothLeService bluetoothLeService;
    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    Context context;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) binder).getService();
            if (!bluetoothLeService.initialize()) {
                Toast.makeText(context, "Unable to initialize Bluetooth", Toast.LENGTH_SHORT).show();
            }
            bluetoothLeService.connect(mac);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d("ACTION_GATT_SERV_DSORED","CONECTADO");
                connected();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d("ACTION_GATT_SERV_DSORED","DESCONECTADO");
                disconnect();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //Log.d("ACTION_GATT_SERV_Dicovered","TRUE");
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data =intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            }
        }
    };

    public void connected(){
        Log.d("CONNECTED","YO");
    }

    public BluetoothServices(Context context){
        this.context = context;
        data = new String[1000];
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    public void init(){

    }

    public boolean isBluetoothEnabled(){
        return !bluetoothAdapter.isEnabled();
    }

    private boolean isBluetoothAdapterEnabled(){
        return bluetoothAdapter!=null;
    }

    private boolean BLESupport(){
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private BluetoothAdapter getBluetoothAdapter(){
        return bluetoothAdapter;
    }

    public BluetoothLeService getBluetoothLeService(){
        return bluetoothLeService;
    }

    public void connect(String name, String mac){
        //Iniciar servicio de comunicacion
        this.name = name;
        this.mac = mac;
        gattServiceIntent = new Intent(context, BluetoothLeService.class);
        context.bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        context.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    public void disconnect(){
        if (bluetoothLeService!= null){
            bluetoothLeService.disconnect();
            bluetoothLeService=null;
            bluetoothManager = null;
            context.unbindService(mServiceConnection);
            context.unregisterReceiver(mGattUpdateReceiver);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void sendCommand(String titu,String command){
        if (bluetoothLeService == null){
            showInfoPopup("Información","La respuesta obtenida por el control presentó un detalle, intenta de nuevo o vuelve a conectarte al equipo.");
        }else{
            switch (titu){
                case "handshake":{
                    titulo="Handshake";
                    bluetoothLeService.sendComando(command);
                    break;
                }
                case "time":{
                    titulo="tiempo";
                    bluetoothLeService.sendComando(command);
                    break;
                }
                case "event":{
                    titulo="Evento";
                    bluetoothLeService.sendComando(command);
                    break;
                }
                case "realState":{
                    titulo="realStatee";
                    bluetoothLeService.sendComando(command);
                    break;
                }
                case "readParam":{
                    titulo="ReadParam";
                    bluetoothLeService.sendComando(command);
                    break;
                }
                case "writeRealParam":{
                    titulo="Escritura de parámetros de operación";
                    bluetoothLeService.sendComando(command);
                    break;
                }
            }
        }
    }

    public List<String> getFinalList(){
        return FinalList;
    }

    private void showInfoPopup(String tittle, String content){
       /* final AlertDialog alexaDialog;
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
*/
    }

    public void createProgressDialog(String string){
        /*if(progressdialog == null){
            //Crear dialogos de "pantalla de carga" y "popups if"
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//getLayoutInflater();
            dialogViewProgressBar = inflater.inflate(R.layout.show_progress_bar, null, false);
            AlertDialog.Builder adb = new AlertDialog.Builder(context,R.style.Theme_AppCompat_Light_Dialog_Alert_eltc);
            adb.setView(dialogViewProgressBar);
            progressdialog = adb.create();
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView txt = ((TextView) dialogViewProgressBar.findViewById(R.id.txtInfoProgressBar));
            txt.setText(string);

            progressdialog.show();
        }*/

    }

    class MyAsyncTaskSendCommand extends AsyncTask<Integer, Integer, String> {
        String comando;
        public MyAsyncTaskSendCommand(String comando){
            this.comando = comando;
        }
        @Override
        protected String doInBackground(Integer... params) {
            if (bluetoothLeService==null){
                return "nobleconnected";
            }else{
                bluetoothLeService.sendComando(command);
            }
            return "resp";
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(800);
                if (progressdialog != null)progressdialog.dismiss();
                progressdialog=null;
                listData.clear();
                listData = bluetoothLeService.getDataFromBroadcastUpdate();
                FinalListData.clear();
                showInfoPopup("Info:","contenido:"+listData);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

}
