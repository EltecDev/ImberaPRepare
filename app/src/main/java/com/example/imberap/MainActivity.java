package com.example.imberap;

import static android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
import static android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.RestrictionsManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imberap.BluetoothServices.BluetoothLeService;
import com.example.imberap.BluetoothServices.BluetoothServices;
import com.example.imberap.Fragment.ListaBLEFragment;
import com.example.imberap.Fragment.EstatusBLEFragment;
import com.example.imberap.Fragment.LoginFragment;
import com.example.imberap.Fragment.OperacionesFragmentCeoWifi;
import com.example.imberap.Fragment.OperacionesFragmentOxxo;
import com.example.imberap.Fragment.PlantillaCEOWiFiFragment;
import com.example.imberap.Fragment.PlantillaFragment;
import com.example.imberap.Fragment.OperacionesFragment;
import com.example.imberap.Fragment.UsuarioFragment;
import com.example.imberap.Utility.GlobalTools;
import com.example.imberap.Fragment.PlantillaOxxoDisplayFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * TODO:
 * Bug por Natalia pendiente: al apagar el bluetooth cuando ya estás conectado al BLE, debe cambiar el estado a DESCONECTADO
 * Bug por Nat: al escanear muchas veces seguidas dejan de aparecer los equipos
 * Bug por Nat: Al pedir muchos estatus seguidos en un punto marca "No se pudo obtener los datos de estatus"
 * Revisar a fondo el bug que sucede en el "Toast" al enviar comando fallido
 * El problema de los crashes por variables null (en ble sucede bastante cuando el equipo se desconectó automáticamente y se intenta hacer uso de conexión)
 * El control retiene su basura cuando se queda a la mitad del envio de esta (por ejemplo enviar el logger de evento), al pedir otro comando el control se vuelve loco hasta vaciar su basura.
 *
 * Urgente:
 * Se debe manejar la actualizaciòn del firmware mediante el modelo que se lea en la conexiòn (handshake). Isaac porporciona tabla para poder coordinar que modelos pueden actualizar a que equipos
 * El comando es 0x4054: comando para limpiar la memoria, hay que agregarlo en el proceso de envio de firmware.
 * REVISAR LA PETICION DE ESTADO DE TREFPB (PEQUEÑO CON BLE) NO OBTIENE EL ESTADO CORRECTAMENTE CON EQUIPO SIN MODELO Y TAMPOCO HAY REVISIÓN DE COMO RECIBIR CORRECTAMENTE LA INFORMACIÓN
 *
 * No Urgente:
 *agregar el resto de compatibilidad a la interfaz respecto al uso de Operador, en TREFPB debería no poder verse el sroll ni botones
 * */
public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener , ListaBLEFragment.connectListener, PlantillaFragment.listener, UsuarioFragment.MainActivityistener, LoginFragment.listener, OperacionesFragment.listener, PlantillaOxxoDisplayFragment.listener{
    BottomNavigationView bottomNavigationView;
    TextView tvConnectionState, tvfwversion , tvUsuarioActual, tvappversion;
    private BluetoothAdapter mBluetoothAdapter;
    String deviceName, deviceMacAddress;
    LayoutInflater inflater;

    androidx.appcompat.app.AlertDialog progressdialog=null;
    View dialogViewProgressBar;

    //Pantalla de peticion inicial de permisos
    SharedPreferences sp;
    SharedPreferences.Editor esp;

    //Fragments
    LoginFragment loginFragment;
    UsuarioFragment usuarioFragment;
    ListaBLEFragment listaBLEFragment;
    PlantillaFragment plantillaFragment;
    PlantillaCEOWiFiFragment plantillaCEOWiFiFragment;
    PlantillaOxxoDisplayFragment plantillaOxxoDisplayFragment;
    EstatusBLEFragment estatusBLEFragment;
    OperacionesFragment operacionesFragment;
    OperacionesFragmentCeoWifi operacionesFragmentCeoWifi;
    OperacionesFragmentOxxo operacionesFragmentOxxo;
    Fragment active;
    FragmentManager fragmentManager ;
    int position=1;

    //Bluetooth Services
    BluetoothServices bluetoothServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    @Override
    protected void onResume() {
        super.onResume();
        initOnResume();
    }

    private void initOnResume(){
        if (!sp.getString("userId","").equals("")){//si no hay usuario logeado entonces no escanear
            switch (sp.getString("userjerarquia","")){
                case "1":{
                    tvUsuarioActual.setText("Usuario:"+sp.getString("userId","")+"\nJerarquía: Administrador");
                    /*bottomNavigationView.findViewById(R.id.bottom_menu_item4).setVisibility(View.VISIBLE);
                    bottomNavigationView.findViewById(R.id.bottom_menu_item3).setVisibility(View.VISIBLE);*/
                    break;
                }
                case "2":{
                    tvUsuarioActual.setText("Usuario:"+sp.getString("userId","")+"\nJerarquía: Ingeniería");
                    break;
                }
                case "3":{
                    tvUsuarioActual.setText("Usuario:"+sp.getString("userId","")+"\nJerarquía: Laboratorio");
                    break;
                }
                case "4":{
                    tvUsuarioActual.setText("Usuario:"+sp.getString("userId","")+"\nJerarquía: Producción");
                    /*bottomNavigationView.findViewById(R.id.bottom_menu_item4).setVisibility(View.GONE);
                    bottomNavigationView.findViewById(R.id.bottom_menu_item3).setVisibility(View.GONE);*/
                    break;
                }
                case "5":{
                    tvUsuarioActual.setText("Usuario:"+sp.getString("userId","")+"\nJerarquía: Operador");
                    break;
                }
            }
            BluetoothLeService BLE= bluetoothServices.getBluetoothLeService();
            if (BLE==null){
                esp.putBoolean("isconnected",false);
                esp.putString("mac","");
                esp.putString("trefpVersionName","");
                esp.apply();
                disconnectBLE();
                GlobalTools.changeScreenConnectionStatus(tvConnectionState,sp);
            }
        }

    }
    private void init(){
        RestrictionsManager myRestrictionsMgr =(RestrictionsManager) this.getSystemService(Context.RESTRICTIONS_SERVICE);
        Bundle appRestrictions = myRestrictionsMgr.getApplicationRestrictions();


        sp = getSharedPreferences("connection_preferences", Context.MODE_PRIVATE);
        esp = sp.edit();

        //FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        // To log a message to a crash report, use the following syntax:
        //crashlytics.log("E/TAG: my message testt");

        //Vista para controlar el cambio de color en la interfaz
        View backgroundView = findViewById(R.id.welcomeActivityBackgroundLayout);

        // Ajuste visual de la barra de notificaciones para android 6.0+:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = backgroundView.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            backgroundView.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.parseColor("#f4f4f4"));
        }

        // Ajuste visual de la barra de navegación para android 8.0+:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS |
                    SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            getWindow().setNavigationBarColor(Color.parseColor("#f4f4f4"));
        }



        if (!sp.getBoolean("permissionGiven",false))
            askPermission();

        //campos
        tvConnectionState = findViewById(R.id.tvconnectionstate);
        tvUsuarioActual = findViewById(R.id.tvUsuarioActual);
        tvfwversion = findViewById(R.id.tvfwversion);
        tvappversion = findViewById(R.id.tvappVersion);

        tvappversion.setText("Versión: "+BuildConfig.VERSION_NAME);

        //servicios bluetooth
        fragmentManager= getSupportFragmentManager();
        bluetoothServices = new BluetoothServices(this, fragmentManager,tvConnectionState,tvfwversion);


        if (bluetoothServices.BLESupport()){
            if (bluetoothServices.isBluetoothAdapterEnabled()){
                loginFragment = new LoginFragment(bluetoothServices, tvUsuarioActual , this);
                usuarioFragment = new UsuarioFragment(bluetoothServices, tvUsuarioActual , this);
                listaBLEFragment = new ListaBLEFragment(bluetoothServices,tvConnectionState, tvfwversion);
                plantillaFragment = new PlantillaFragment(bluetoothServices,this);
                plantillaCEOWiFiFragment = new PlantillaCEOWiFiFragment(bluetoothServices,this);
                plantillaOxxoDisplayFragment = new PlantillaOxxoDisplayFragment(bluetoothServices,this);
                estatusBLEFragment = new EstatusBLEFragment(bluetoothServices,this);
                operacionesFragment = new OperacionesFragment(bluetoothServices,this);
                operacionesFragmentCeoWifi = new OperacionesFragmentCeoWifi(bluetoothServices,this);
                operacionesFragmentOxxo = new OperacionesFragmentOxxo(bluetoothServices,this);
                active = loginFragment;
            }else {
                Toast.makeText(MainActivity.this, "El dispositivo tiene problemas con el Bluetooth o está apagado", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(MainActivity.this, "El dispositivo no soporta deteccion de BLE", Toast.LENGTH_SHORT).show();
        }


        //Inicializar interfaz MainActivity
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(this);


        listaBLEFragment.setconnectListenerListener(this);
        plantillaFragment.setListener(this);
        usuarioFragment.logoutListener(this);
        loginFragment.loginListener(this);
        //plantillaCEOWiFiFragment.setListener(this);
        plantillaOxxoDisplayFragment.setListener(this);
        operacionesFragment.setOperacionesListener(this);
        //operacionesFragmentceowifi.setOperacionesListener(this);

        fragmentManager.beginTransaction().add(R.id.flFragment,loginFragment, "").hide(loginFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flFragment,usuarioFragment, "").hide(usuarioFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flFragment, estatusBLEFragment, "").hide(estatusBLEFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flFragment, operacionesFragment, "").hide(operacionesFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flFragment, operacionesFragmentCeoWifi, "").hide(operacionesFragmentCeoWifi).commit();
        fragmentManager.beginTransaction().add(R.id.flFragment, operacionesFragmentOxxo, "").hide(operacionesFragmentOxxo).commit();
        fragmentManager.beginTransaction().add(R.id.flFragment, plantillaFragment, "").hide(plantillaFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flFragment, plantillaCEOWiFiFragment, "").hide(plantillaCEOWiFiFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flFragment, plantillaOxxoDisplayFragment, "PlantillaOxxo").hide(plantillaOxxoDisplayFragment).commit();
        fragmentManager.beginTransaction().add(R.id.flFragment,listaBLEFragment, "").hide(listaBLEFragment).commit();

        //si ya hay login exitoso no mostrar pantalla de login al inicio, en su lugar mostrar pantalla de usuario y logout
        if (sp.getString("userId","").equals("")){
            bottomNavigationView.setSelectedItemId(R.id.bottom_menu_item5);
            fragmentManager.beginTransaction().show(loginFragment).commit();
            active= loginFragment;
        }else{
            bottomNavigationView.setSelectedItemId(R.id.bottom_menu_item1);
            fragmentManager.beginTransaction().show(listaBLEFragment).commit();
            active= listaBLEFragment;
        }


    }

    private void askPermission(){

        if (Build.VERSION.SDK_INT >= 31){
            String[] perms = {  Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_CONNECT};
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                esp.putBoolean("permissionGiven",true);
                esp.apply();
            } else {
                requestPermissions(perms,100);
            }
        }else{
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, 1666);
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean         locationEnabled = false;
            try {
                locationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch(Exception ignored) {}
            try {
                locationEnabled |= locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch(Exception ignored) {}
            if(!locationEnabled)
                Log.d("PERMISSION LOCATION ENABLED","CORREWCTO");
            else
                Log.d("PERMISSION LOCATION ENABLED","IJNININCORREWCTO");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == 1 && resultCode == Activity.RESULT_CANCELED) {
            esp.putBoolean("permissionGiven",false);
            esp.apply();
            finish();
            return;
        }
        if (requestCode==100) {
            esp.putBoolean("permissionGiven",true);
            esp.apply();
            Toast.makeText(MainActivity.this, "¡Ahora puedes escanear!", Toast.LENGTH_SHORT).show();
        }else if (requestCode==1666){
            esp.putBoolean("permissionGiven",true);
            esp.apply();
            Toast.makeText(MainActivity.this, "¡Ahora puedes escanear!", Toast.LENGTH_SHORT).show();
        }else{
            esp.putBoolean("permissionGiven",false);
            esp.apply();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_menu_item1:
                if (sp.getString("userId","").equals("")){
                    fragmentManager.beginTransaction().hide(active).show(loginFragment).commit();
                    active= loginFragment;
                    Toast.makeText(this, "Conéctate con un usuario primero", Toast.LENGTH_SHORT).show();
                }else{
                    fragmentManager.beginTransaction().hide(active).show(listaBLEFragment).commit();
                    active=listaBLEFragment;
                }
                return true;
            case R.id.bottom_menu_item2:

                if (sp.getString("userId","").equals("")){
                    fragmentManager.beginTransaction().hide(active).show(loginFragment).commit();
                    active= loginFragment;
                    Toast.makeText(this, "Conéctate con un usuario primero", Toast.LENGTH_SHORT).show();
                }else{
                    /*if (sp.getString("userjerarquia","").equals("4")){//jerarquia 4  = producción
                        showInfoPopup("Jerarquía de usuarios","Tu usuario no puede utilizar esta función, contacta con el administrador del sistema.");
                    }else{*/
                    if (sp.getString("trefpVersionName","").equals("IMBERA-WF")){
                        fragmentManager.beginTransaction().hide(active).show(plantillaCEOWiFiFragment).commit();
                        active=plantillaCEOWiFiFragment;
                    }else if (sp.getString("trefpVersionName","").equals("IMBERA-OXXO")){
                        fragmentManager.beginTransaction().hide(active).show(plantillaOxxoDisplayFragment).commit();
                        active=plantillaOxxoDisplayFragment;
                    }else if (sp.getString("trefpVersionName","").equals("IMBERA-TREFP")){
                        fragmentManager.beginTransaction().hide(active).show(plantillaFragment).commit();
                        active=plantillaFragment;
                    }else if (sp.getString("trefpVersionName","").equals("")){
                            Toast.makeText(this, "Conéctate a un BLE primero", Toast.LENGTH_SHORT).show();
                        }
                    //}

                }
                return true;
            case R.id.bottom_menu_item3:
                if (sp.getString("userId","").equals("")){
                    fragmentManager.beginTransaction().hide(active).show(loginFragment).commit();
                    active= loginFragment;
                    Toast.makeText(this, "Conéctate con un usuario primero", Toast.LENGTH_SHORT).show();
                }else{
                    if (sp.getString("userjerarquia","").equals("4") || sp.getString("userjerarquia","").equals("5")){//jerarquia 4  = producción; 5 = Operador
                        showInfoPopup("Jerarquía de usuarios","Tu usuario no puede utilizar esta función, contacta con el administrador del sistema.");
                    }else{
                        if (sp.getString("trefpVersionName","").equals("")){
                            Toast.makeText(this, "Conéctate a un BLE primero", Toast.LENGTH_SHORT).show();
                        }else{
                            fragmentManager.beginTransaction().hide(active).show(estatusBLEFragment).commit();
                            active= estatusBLEFragment;
                        }
                    }


                }
                return true;
            case R.id.bottom_menu_item4:
                if (sp.getString("userId","").equals("")){
                    Toast.makeText(this, "Conéctate con un usuario primero", Toast.LENGTH_SHORT).show();
                }else{
                    if (sp.getString("userjerarquia","").equals("4") || sp.getString("userjerarquia","").equals("5")){//jerarquia 4  = producción; 5 = Operador
                        showInfoPopup("Jerarquía de usuarios","Tu usuario no puede utilizar esta función, contacta con el administrador del sistema.");
                    }else{
                        if (sp.getString("trefpVersionName","").equals("IMBERA-WF")){
                            fragmentManager.beginTransaction().hide(active).show(operacionesFragmentCeoWifi).commit();
                            active= operacionesFragmentCeoWifi;
                        }else if (sp.getString("trefpVersionName","").equals("IMBERA-OXXO")){
                            fragmentManager.beginTransaction().hide(active).show(operacionesFragmentOxxo).commit();
                            active= operacionesFragmentOxxo;
                        }else if (sp.getString("trefpVersionName","").equals("IMBERA-TREFP")){
                            fragmentManager.beginTransaction().hide(active).show(operacionesFragment).commit();
                            active= operacionesFragment;
                        }else if (sp.getString("trefpVersionName","").equals("")){
                            Toast.makeText(this, "Conéctate a un BLE primero", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            return true;
            case R.id.bottom_menu_item5:
                //todo login, acceso y cuando hay una sesión abierta mostras las demás acciones posibles según su jerarquia
                if (sp.getString("userId","").equals("")){
                    fragmentManager.beginTransaction().hide(active).show(loginFragment).commit();
                    active= loginFragment;
                }else{
                    //todo mostrar las opciones del usuario en cuanto a cuentas
                    fragmentManager.beginTransaction().hide(active).show(usuarioFragment).commit();
                    active= usuarioFragment;
                }

                return true;
        }
        return false;
    }

    @Override
    public void connectBLE(String name,String mac) {
        deviceName = name;
        deviceMacAddress = mac;
        bluetoothServices.connect(name,mac);
    }

    @Override
    public void disconnectBLE() {
        bluetoothServices.disconnect();
    }

    @Override
    public boolean isPermissionEnabled() {
        if (Build.VERSION.SDK_INT >= 31) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
                esp.putBoolean("permissionGiven",true);
                esp.apply();
                return true;
            } else {
                esp.putBoolean("permissionGiven",false);
                esp.apply();
                return false;
            }
        }else{
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                esp.putBoolean("permissionGiven",true);
                esp.apply();
                return true;
            } else {
                esp.putBoolean("permissionGiven",false);
                esp.apply();
                return false;
                //requestPermissions(perms,100);
            }
        }

    }

    @Override
    public void requestPemission() {
        Toast.makeText(this, "Es necesario que acepte los permisos solicitados", Toast.LENGTH_SHORT).show();
        askPermission();
    }

    @Override
    public void logout() {
        esp.putString("userId","");
        esp.putString("userjerarquia","");
        esp.apply();
        tvUsuarioActual.setText("");
        bottomNavigationView.setSelectedItemId(R.id.bottom_menu_item5);
        fragmentManager.beginTransaction().hide(active).show(loginFragment).commit();
        active= loginFragment;
    }

    @Override
    public void login() {
        bottomNavigationView.setSelectedItemId(R.id.bottom_menu_item5);
        fragmentManager.beginTransaction().hide(active).show(usuarioFragment).commit();
        plantillaOxxoDisplayFragment.actualizarUIJerarquia();
        plantillaFragment.actualizarUIJerarquia();
        //fragmentManager.findFragmentByTag("PlantillaOxxo").
        View v  = usuarioFragment.getView();
        TextView tv =v.findViewById(R.id.tvUserName);
        TextView tv2 =v.findViewById(R.id.tvjerarquia);
        tv.setText(sp.getString("userId",""));
        switch (sp.getString("userjerarquia","")){
            case "1":{
                tv2.setText("Administrador");
                //Mostrar menus para este superusuario
                break;
            }
            case "4":{
                //ocultar menus para este usuario
                tv2.setText("Producción");
                break;
            }
            case "5":{
                //ocultar menus para este usuario
                tv2.setText("Operador");
                break;
            }
        }

        active= usuarioFragment;
    }

    /**
     * Empiezan funciones de excel
     * */
    @Override
    public void printExcel(List<String> data, String deviceName) {
        createProgressDialogExcelDoc(data,deviceName);
    }

    @Override
    public void printExcelCrudoExtendido(List<String> data, List<String> evento, List<String> tiempo) {
        createExcelFileCrudototalImberaExtendido(data,evento,tiempo);
    }

    private void showInfoPopup(String tittle, String content){
        final AlertDialog alexaDialog;
        LayoutInflater inflater = getLayoutInflater();//getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.popup_info, null, false);
        AlertDialog.Builder adb = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog_Alert_eltc);
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

    }

    public void createProgressDialogExcelDoc(List<String> data, String name){
        if(progressdialog == null){
            //Crear dialogos de "pantalla de carga" y "popups if"
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.popup_option, null, false);
            AlertDialog.Builder adb = new AlertDialog.Builder(MainActivity.this,R.style.Theme_AppCompat_Light_Dialog_Alert_eltc);
            adb.setView(dialogView);

            progressdialog = adb.create();
            progressdialog.setCanceledOnTouchOutside(false);
            progressdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressdialog.show();

            dialogView.findViewById(R.id.btnSendExcel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressdialog.dismiss();
                    progressdialog = null;

                    if (name.equals("oxxo")){
                        createExcelFileOxxoDisplay(data);
                    }else if (name.equals("imbera"))
                        createExcelFileImberaTREFP(data);
                    else if (name.equals("crudototalImbera"))
                        createExcelFileCrudototalImbera(data);
                    else if (name.equals("crudototalOxxo"))
                        createExcelFileCrudototalOxxo(data);
                }
            });
            dialogView.findViewById(R.id.btndontSendExcel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressdialog.dismiss();
                    progressdialog = null;
                }
            });
        }
    }

    private void createExcelFileCrudototalOxxo(List<String> data){

        String nombreFile= "InfoTotalCrudoOxxo.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        FileOutputStream outputStream = null;

        HSSFWorkbook wb= new HSSFWorkbook();
        HSSFSheet hssfSheet = wb.createSheet("Handshake");

        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue(data.get(0));

        hssfSheet = wb.createSheet("Estado tiempo real");

        hssfRow = hssfSheet.createRow(0);
        hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue(data.get(1));

        hssfSheet = wb.createSheet("Parámetros");

        hssfRow = hssfSheet.createRow(0);
        hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue(data.get(2));

        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(this.getApplicationContext(),"Reporte generado correctamente",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
                setupEmailImberaOxxoCrudo();
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(),"Reporte no generado",Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    private void createExcelFileCrudototalImberaExtendido(List<String> data,List<String> dataEvento,List<String> dataTiempo){
        Log.d("EXCELELES",":"+data.get(0));
        Log.d("EXCELELES",":"+data.get(1));
        Log.d("EXCELELES",":"+data.get(2));

        Log.d("EXCELELES","EENTO:"+dataEvento);
        Log.d("EXCELELES","tiempo:"+dataTiempo);

        String nombreFile= "CrudoTREFPBExt.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        FileOutputStream outputStream = null;

        HSSFWorkbook wb= new HSSFWorkbook();
        HSSFSheet hssfSheet = wb.createSheet("Handshake");

        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue(data.get(0));

        hssfSheet = wb.createSheet("Estado tiempo real");

        hssfRow = hssfSheet.createRow(0);
        hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue(data.get(1));

        hssfSheet = wb.createSheet("Datos tipo tiempo");
        Log.d("GGG",":"+dataTiempo.size());
        /*hssfRow = hssfSheet.createRow(0);
        hssfCell = hssfRow.createCell(0);*/
        for (int i=0; i<dataTiempo.size(); i++){
            hssfCell = hssfRow.createCell(0);
            hssfRow = hssfSheet.createRow(i);
            hssfCell.setCellValue(dataTiempo.get(i));
        }

        hssfSheet = wb.createSheet("Datos tipo evento");
        Log.d("GGG",":"+dataEvento.size());

        hssfCell = hssfRow.createCell(0);
        for (int i=0; i<dataEvento.size(); i++){
            hssfRow = hssfSheet.createRow(i);
            hssfCell.setCellValue(dataEvento.get(i));
        }

        hssfSheet = wb.createSheet("Plantilla");

        hssfRow = hssfSheet.createRow(0);
        hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue(data.get(2));

        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(this.getApplicationContext(),"Reporte generado correctamente",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
                setupEmailImberaTREFPCrudoExtendido();
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(),"Reporte no generado",Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    private void createExcelFileCrudototalImbera(List<String> data){

        String nombreFile= "InfoTotalCrudoTREFPB.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        FileOutputStream outputStream = null;

        HSSFWorkbook wb= new HSSFWorkbook();
        HSSFSheet hssfSheet = wb.createSheet("Handshake");

        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue(data.get(0));

        hssfSheet = wb.createSheet("Estado tiempo real");

        hssfRow = hssfSheet.createRow(0);
        hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue(data.get(1));

        hssfSheet = wb.createSheet("Datos tipo tiempo");

        hssfRow = hssfSheet.createRow(0);
        hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue(data.get(2));

        hssfSheet = wb.createSheet("Datos tipo evento");

        hssfRow = hssfSheet.createRow(0);
        hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue(data.get(3));

        hssfSheet = wb.createSheet("Plantilla");

        hssfRow = hssfSheet.createRow(0);
        hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue(data.get(4));

        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(this.getApplicationContext(),"Reporte generado correctamente",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
                setupEmailImberaTREFPCrudo();
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(),"Reporte no generado",Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    private void createExcelFileImberaTREFP(List<String> data){

        String nombreFile= "PlantillaImberaP.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        FileOutputStream outputStream = null;

        HSSFWorkbook wb= new HSSFWorkbook();
        HSSFSheet hssfSheet = wb.createSheet("Plantilla");

        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue("Parámetro");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue("Cambio");
//inicio
        hssfRow = hssfSheet.createRow(2);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("COMPRESOR");

        hssfRow = hssfSheet.createRow(3);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F2.- Tiempo mínimo de encendido (minutos, por defecto:3.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(0));

        hssfRow = hssfSheet.createRow(4);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F3.- Tiempo mínimo de apagado (minutos, por defecto:1.5)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(1));

        hssfRow = hssfSheet.createRow(5);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("FC.- Retraso de primer encendido (minutos, por defecto:0.4)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(2));
//titulo
        hssfRow = hssfSheet.createRow(6);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("TERMOSTATO");


        hssfRow = hssfSheet.createRow(7);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("T0.- Temperatura de corte frío (°C, por defecto:1.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(3));

        hssfRow = hssfSheet.createRow(8);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("T2.- Diferencial frío , por defecto:2.0");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(4));

        hssfRow = hssfSheet.createRow(9);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("A0.- Límite de temperatura baja , por defecto:-7.0");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(5));
//titulo
        hssfRow = hssfSheet.createRow(10);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("PUERTA");


        hssfRow = hssfSheet.createRow(11);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C1.- Funciones de control");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(6));

        hssfRow = hssfSheet.createRow(12);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C7.- Cortes de compresor para permitir modos de ahorro, por defecto:2");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(7));

        hssfRow = hssfSheet.createRow(13);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("TF.- Temperatura ambiente para permitir modos de ahorro (°C, por defecto:17.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(8));

        hssfRow = hssfSheet.createRow(14);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("TD.- Diferencial de Modo Ahorro 1, por defecto:1.0");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(9));

        hssfRow = hssfSheet.createRow(15);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("TE.- Diferencial de Modo Ahorro 2, por defecto:1.0");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(10));

        hssfRow = hssfSheet.createRow(16);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("FD.- Tiempo de puerta cerrada para Modo Ahorro 1 (horas, por defecto:2.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(11));

        hssfRow = hssfSheet.createRow(17);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("FE.- Tiempo de puerta cerrada para Modo Ahorro 2 (horas, por defecto:1.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(12));

        hssfRow = hssfSheet.createRow(18);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("D2.- Tiempo para registrar puerta abierta (segundos, por defecto: 5)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(13));

        hssfRow = hssfSheet.createRow(19);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("LF.- Tiempo para validar falla de puerta abierta (minutos, por defecto: 3)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(14));
//titulo
        hssfRow = hssfSheet.createRow(20);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("DESHIELO");


        hssfRow = hssfSheet.createRow(21);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C0.- Modo de deshielo , por defecto:2");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(15));

        hssfRow = hssfSheet.createRow(22);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C3.B2.- Funciones de deshielo, por defecto: 4");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(16));

        hssfRow = hssfSheet.createRow(23);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L8.- Tiempo mínimo de deshielo (minutos, por defecto:15)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(17));

        hssfRow = hssfSheet.createRow(24);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L9.- Tiempo máximo de deshielo (minutos, por defecto:30)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(18));

        hssfRow = hssfSheet.createRow(25);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("LA.- Tiempo mínimo para inicio de deshielo (horas, por defecto:12)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(19));

        hssfRow = hssfSheet.createRow(26);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("LB.- Tiempo máximo para inicio de deshielo (horas , por defecto:16)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(20));

        hssfRow = hssfSheet.createRow(27);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("T8.- Temperatura para finalizar deshielo (°C, por defecto:15.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(21));

        hssfRow = hssfSheet.createRow(28);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("T9.- Temperatura de reinicio (°C, por defecto:5.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(22));

        hssfRow = hssfSheet.createRow(29);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("TA.- Temperatura para iniciar deshielo (°C, por defecto:-5.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(23));

        hssfRow = hssfSheet.createRow(30);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F6.- Tiempo de goteo para compresor (minutos, por defecto:10.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(24));

        hssfRow = hssfSheet.createRow(31);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F7.- Tiempo de goteo para ventilador (minutos, por defecto:5.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(25));
//titulo
        hssfRow = hssfSheet.createRow(32);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("VENTILADOR");


        hssfRow = hssfSheet.createRow(33);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C4.B0.- Funciones de ventilador, por defecto:2");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(26));

        hssfRow = hssfSheet.createRow(34);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C4.B1.- Funciones de ventilador, por defecto:2");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(27));

        hssfRow = hssfSheet.createRow(35);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("FB.- Retraso de primer encendido (minutos, por defecto:0.3)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(28));

        hssfRow = hssfSheet.createRow(36);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F9.- Retardo para comenzar ciclo (minutos, por defecto:3.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(29));

        hssfRow = hssfSheet.createRow(37);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F4.- Tiempo de ventilador encendido (minutos, por defecto:3.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(30));

        hssfRow = hssfSheet.createRow(38);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F5.- Tiempo de ventilador apagado (minutos, por defecto:3.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(31));

        hssfRow = hssfSheet.createRow(39);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("FA.- Retardo de encendido del ventilador al cerrar puerta (minutos, por defecto:0.5)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(32));
//titulo
        hssfRow = hssfSheet.createRow(40);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("PROTECCIÓN DE VOLTAJE");


        hssfRow = hssfSheet.createRow(41);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C6.- Protección de voltaje, por defecto:1");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(33));

        hssfRow = hssfSheet.createRow(42);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L0.- Voltaje de protección mínimo para 120 (volts, por defecto:85)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(34));

        hssfRow = hssfSheet.createRow(43);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L1.-Voltaje de protección máximo 120 mínimo 220 (volts, por defecto:40)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(35));

        hssfRow = hssfSheet.createRow(44);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L2.- Voltaje de protección máximo para 220 (volts, por defecto:40)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(36));

        hssfRow = hssfSheet.createRow(45);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L4.- Tiempo de validación de protección de voltaje (segundos, por defecto:15)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(37));

        hssfRow = hssfSheet.createRow(46);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F1.- Tiempo de validación para salir de protección de voltaje (minutos, por defecto:1.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(38));

        hssfRow = hssfSheet.createRow(47);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F0.- Histéresis (volts, por defecto:4.2)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(39));

        hssfRow = hssfSheet.createRow(48);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("D3.- Tiempo para Logger de datos (minutos, por defecto:60)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(40));

        hssfRow = hssfSheet.createRow(49);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("E0 E1.- Versión de Firmware");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(41));

        hssfRow = hssfSheet.createRow(50);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("E2.- Modelo de TREFP");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(42));

        hssfRow = hssfSheet.createRow(51);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("E3.- Versión de la plantilla");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(43));

        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(this.getApplicationContext(),"Reporte generado correctamente",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
                setupEmailImberaTREFP(data);
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(),"Reporte no generado",Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    private void createExcelFileOxxoDisplay(List<String> data){

        String nombreFile= "PlantillaImberaP.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        FileOutputStream outputStream = null;

        HSSFWorkbook wb= new HSSFWorkbook();
        HSSFSheet hssfSheet = wb.createSheet("Plantilla");

        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell hssfCell = hssfRow.createCell(0);

        hssfCell.setCellValue("Parámetro");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue("Cambio");
//inicio
        //hssfRow = hssfSheet.createRow(2);
        //hssfCell = hssfRow.createCell(0);
        //hssfCell.setCellValue("COMPRESOR");

        hssfRow = hssfSheet.createRow(2);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("T0.- Set point diurno (grados, por defecto:-3.5)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(0));

        hssfRow = hssfSheet.createRow(3);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("T1.- Diferencial diurno (grados, por defecto:2.5)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(1));

        hssfRow = hssfSheet.createRow(4);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("T2.- Offset Diurno (grados, por defecto:-0.5)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(2));
//titulo
        //hssfRow = hssfSheet.createRow(6);
        //hssfCell = hssfRow.createCell(0);
        //hssfCell.setCellValue("TERMOSTATO");


        hssfRow = hssfSheet.createRow(5);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("T6.- Límite interior para ajuste de set point (grados , por defecto:-15.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(3));

        hssfRow = hssfSheet.createRow(6);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("T7.- Límite superior para ajuste de set point (grados , por defecto:15.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(4));

        hssfRow = hssfSheet.createRow(7);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("T9.- Temperatura de reinicio de tiempo de interdeshielos (grados , por defecto:3.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(5));

        hssfRow = hssfSheet.createRow(8);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("TA.- Temperatura para iniciar deshielo (grados , por defecto:-10.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(6));

        hssfRow = hssfSheet.createRow(9);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("TD.- Diferencial de temperatura para Ahorro 1 (grados, por defecto:1.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(7));
//titulo
        //hssfRow = hssfSheet.createRow(10);
        //hssfCell = hssfRow.createCell(0);
        //hssfCell.setCellValue("PUERTA");


        hssfRow = hssfSheet.createRow(10);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("TE.- Diferencial de temperatura para Ahorro 2 (grados, por defecto:1.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(8));

        hssfRow = hssfSheet.createRow(11);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("A0.- Temperatura evaporador para terminar deshielo (grados, por defecto:15.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(9));

        hssfRow = hssfSheet.createRow(12);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("A1.- Temperatura ambiente para terminar deshielo (°C, por defecto:30.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(10));

        hssfRow = hssfSheet.createRow(13);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("A4.- Temperatura de pulldown (grados, por defecto:12.0");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(11));

        hssfRow = hssfSheet.createRow(14);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("A6.- Alarma de temperatura Alta (grados, por defecto:12.0");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(12));

        hssfRow = hssfSheet.createRow(15);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("A7.- Alarma de temperatura Baja (grados, por defecto:-12.0");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(13));

        hssfRow = hssfSheet.createRow(16);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("A8.- Diferencial para alarmas de Temperatura (grados, por defecto:1.0");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(14));

        hssfRow = hssfSheet.createRow(17);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("A9.- Diferencial de temperaturas para alarma de deficiencia (grados, por defecto:2.0");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(15));

        hssfRow = hssfSheet.createRow(18);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L0.- Voltaje de protección mínimo para 120  (Volts, por defecto:85");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(16));

        hssfRow = hssfSheet.createRow(19);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L1.- Voltaje de protección máximo 120 mínimo 220 (Volts, por defecto:40)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue("1"+data.get(17));

        hssfRow = hssfSheet.createRow(20);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L2.- Voltaje de protección máximo para 220 (Volts, por defecto:40)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue("2"+data.get(18));

        hssfRow = hssfSheet.createRow(21);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L3.- Tiempo para validar falla de voltaje (segundos, por defecto: 15)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(19));

        hssfRow = hssfSheet.createRow(22);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L4.- Tiempo entre deshielos (horas, por defecto: 4)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(20));
//titulo
        //hssfRow = hssfSheet.createRow(20);
        //hssfCell = hssfRow.createCell(0);
        //hssfCell.setCellValue("DESHIELO");


        hssfRow = hssfSheet.createRow(23);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L5.- Tiempo máximo de deshielo (minutos, por defecto:30)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(21));

        hssfRow = hssfSheet.createRow(24);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L6.- Tiempo de puerta abierta para cancelar control de ventilador por puerta (minutos, por defecto: 99)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(22));

        hssfRow = hssfSheet.createRow(25);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L7.- Tiempo de validación para alarma de puerta abierta (minutos, por defecto:3)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(23));

        hssfRow = hssfSheet.createRow(26);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("L9.- Tiempo de permanencia en modo nocturno (minutos, por defecto:10)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(24));

        hssfRow = hssfSheet.createRow(27);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("LA.- Tiempo de bloqueo de display después de deshielo (minutos, por defecto:15)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(25));

        hssfRow = hssfSheet.createRow(28);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("LB.- Tiempo de compresor encendido para mediciones de deficiencia (minutos, por defecto:5)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(26));

        hssfRow = hssfSheet.createRow(29);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("LC.- Retardo para encender segundo compresor (minutos, por defecto:15)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(27));

        hssfRow = hssfSheet.createRow(30);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C0.- Escala de temperatura (por defecto:Celcius)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(28));

        hssfRow = hssfSheet.createRow(31);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C1.- Banderas de configuración");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(29));

        hssfRow = hssfSheet.createRow(32);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C2.- Intensidad del filtro para el sensor ambiente a la subida (por defecto: 6)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(30));

        hssfRow = hssfSheet.createRow(33);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C3.- Tipo de deshielo (por defecto:Por resistencia eléctrica)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(31));

        hssfRow = hssfSheet.createRow(34);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C4.- Tiempo adaptativo de deshielo (por defecto: 0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(32));

        hssfRow = hssfSheet.createRow(35);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C5.- Protección de voltaje (por defecto:Protección de voltaje a 120 Volts)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(33));

        hssfRow = hssfSheet.createRow(36);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C7.- Número de sensores de temperatura del sistema (por defecto:2)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(34));

        hssfRow = hssfSheet.createRow(37);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C8.- Niveles de tiempo para mostrar falla (por defecto:0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(35));

        hssfRow = hssfSheet.createRow(38);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("C9.- Configuración de relevadores(por defecto:1)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(36));

        hssfRow = hssfSheet.createRow(39);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F0.- Histéresis para la protección de voltaje (Volts, por defecto:4.2)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(37));
//titulo
        //hssfRow = hssfSheet.createRow(32);
        //hssfCell = hssfRow.createCell(0);
        //hssfCell.setCellValue("VENTILADOR");


        hssfRow = hssfSheet.createRow(40);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F1.- Tiempo de validación para salir de falla de voltaje (minutos, por defecto:0.3)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(38));

        hssfRow = hssfSheet.createRow(41);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F2.- Tiempo de goteo (minutos, por defecto:1.1)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(39));

        hssfRow = hssfSheet.createRow(42);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F3.- Tiempo de descanso mínimo del compresor (minutos, por defecto:1.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(40));

        hssfRow = hssfSheet.createRow(43);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F4.- Tiempo máximo de compresor encendido (horas, por defecto:0.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(41));

        hssfRow = hssfSheet.createRow(44);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F5.- Tiempo de descanso compresor cuando se cumple F4 (horas, por defecto:25.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(42));

        hssfRow = hssfSheet.createRow(45);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("F6.- Ciclos de encendido y apagado del ventilador (minutos, por defecto:3.3)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(43));

        hssfRow = hssfSheet.createRow(46);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("FD.- Tiempo de puerta cerrada para entrar a modo Ahorro 1 (horas, por defecto:2.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(44));

        hssfRow = hssfSheet.createRow(47);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("FE.- Tiempo de puerta cerrada para entrar a modo Ahorro 2 (horas, por defecto:1.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(45));

        hssfRow = hssfSheet.createRow(48);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("D0.- Direccion Modbus (por defecto:222)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(46));

        hssfRow = hssfSheet.createRow(49);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("D1.- Password (por defecto:73)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(47));

        hssfRow = hssfSheet.createRow(50);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("D7.- Modelo (por defecto:2)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(48));

        hssfRow = hssfSheet.createRow(51);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("E1 E2.- Versión Firmware (por defecto:1.0)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(49));
//titulo
        //hssfRow = hssfSheet.createRow(40);
        //hssfCell = hssfRow.createCell(0);
        //hssfCell.setCellValue("PROTECCIÓN DE VOLTAJE");


        hssfRow = hssfSheet.createRow(52);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("E3.- Plantilla ( por defecto:10)");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue(data.get(50));



        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(this.getApplicationContext(),"Reporte generado correctamente",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
                setupEmailOxxo(data);
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(),"Reporte no generado",Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    private void setupEmailImberaTREFPCrudoExtendido (){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/excel");
        //intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Información en crudo");
        intent.putExtra(Intent.EXTRA_TEXT,"Envío de archivo HEX enviado desde la aplicación ImberaP\nFecha:"+ Calendar.getInstance().getTime());

        String nombreFile= "CrudoTREFPBExt.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        if (file.exists()){
            Log.v("TEST EMAILExtendido", "Email file_exists!" );
        } else{
            Log.v("TEST EMAIL", "Email file does not exist!" );
        }

        Uri uri = FileProvider.getUriForFile(
                this,
                "mx.eltec.imberap.MainActivity.provider",
                file);

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        this.startActivity(Intent.createChooser(intent, "Send mail..."));
    }

    private void setupEmailImberaTREFPCrudo (){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/excel");
        //intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Información en crudo");
        intent.putExtra(Intent.EXTRA_TEXT,"Envío de archivo HEX enviado desde la aplicación ImberaP\nFecha:"+ Calendar.getInstance().getTime());

        String nombreFile= "InfoTotalCrudoTREFPB.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        if (file.exists()){
            Log.v("TEST EMAIL", "Email file_exists!" );
        } else{
            Log.v("TEST EMAIL", "Email file does not exist!" );
        }

        Uri uri = FileProvider.getUriForFile(
                this,
                "mx.eltec.imberap.MainActivity.provider",
                file);

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        this.startActivity(Intent.createChooser(intent, "Send mail..."));
    }

    private void setupEmailImberaOxxoCrudo (){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/excel");
        //intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Información en crudo");
        intent.putExtra(Intent.EXTRA_TEXT,"Envío de archivo HEX enviado desde la aplicación ImberaP\nFecha:"+ Calendar.getInstance().getTime());

        String nombreFile= "InfoTotalCrudoOxxo.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        if (file.exists()){
            Log.v("TEST EMAIL", "Email file_exists!" );
        } else{
            Log.v("TEST EMAIL", "Email file does not exist!" );
        }

        Uri uri = FileProvider.getUriForFile(
                this,
                "mx.eltec.imberap.MainActivity.provider",
                file);

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        this.startActivity(Intent.createChooser(intent, "Send mail..."));
    }

    private void setupEmailImberaTREFP (List<String> data){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/excel");
        //intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "PLANTILLA DE CONFIGURACIÓN:"+data.get(43));
        intent.putExtra(Intent.EXTRA_TEXT,"Envío de archivo de configuración enviado desde la aplicación ImberaP\nFecha:"+ Calendar.getInstance().getTime());

        String nombreFile= "PlantillaImberaP.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        if (file.exists()){
            Log.v("TEST EMAIL", "Email file_exists!" );
        } else{
            Log.v("TEST EMAIL", "Email file does not exist!" );
        }

        Uri uri = FileProvider.getUriForFile(
                this,
                "mx.eltec.imberap.MainActivity.provider",
                file);

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        this.startActivity(Intent.createChooser(intent, "Send mail..."));
    }

    private void setupEmailOxxo (List<String> data){
        //String[] email = new String[1];
        //email[0] = "luise@eltec.mx";
        //email[0] = "eduard_vis@hotmail.com";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/excel");
        //intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "PLANTILLA DE CONFIGURACIÓN:"+data.get(50));
        intent.putExtra(Intent.EXTRA_TEXT,"Envío de archivo de configuración enviado desde la aplicación ImberaP\nFecha:"+ Calendar.getInstance().getTime());

        String nombreFile= "PlantillaImberaP.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        if (file.exists()){
            Log.v("TEST EMAIL", "Email file_exists!" );
        } else{
            Log.v("TEST EMAIL", "Email file does not exist!" );
        }

        Uri uri = FileProvider.getUriForFile(
                this,
                "mx.eltec.imberap.MainActivity.provider",
                file);

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        this.startActivity(Intent.createChooser(intent, "Send mail..."));
    }

    private void setupEmailEvent (String titulo,String descripcion, String filestring){

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/excel");
        //intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, titulo);
        intent.putExtra(Intent.EXTRA_TEXT,descripcion);


        File file = new File(this.getExternalFilesDir(null),filestring);
        if (file.exists()){
            Log.v("TEST EMAIL", "Email file_exists!" );
        } else{
            Log.v("TEST EMAIL", "Email file does not exist!" );
        }

        Uri uri = FileProvider.getUriForFile(
                this,
                "mx.eltec.imberap.MainActivity.provider",
                file);

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        this.startActivity(Intent.createChooser(intent, "Send mail..."));
    }

    @Override
    public void createExcelTimeData(String name,List<String> data) {
        String nombreFile= "DatosTipoTiempo.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        FileOutputStream outputStream = null;

        HSSFWorkbook wb= new HSSFWorkbook();
        HSSFSheet hssfSheet = wb.createSheet("Datos tipo Tiempo");

        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell hssfCell = hssfRow.createCell(0);

        //se agregan los datos de quien lo genera y la fecha etc
        Date currentTime = Calendar.getInstance().getTime();
        hssfCell.setCellValue("Creador:"+sp.getString("userId",""));
        hssfRow = hssfSheet.createRow(1);
        hssfCell = hssfRow.createCell(0);
        switch (sp.getString("userjerarquia","")){
            case "1":{
                hssfCell.setCellValue("Jerarquía: Administrador");
                break;
            }
            case "4":{
                hssfCell.setCellValue("Jerarquía: Producción");
                break;
            }
        }
        hssfRow = hssfSheet.createRow(2);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("Fecha:"+currentTime);

        //titulos
        hssfRow = hssfSheet.createRow(4);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("TimeStamp1");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue("Temperatura 1");
        hssfCell = hssfRow.createCell(2);
        hssfCell.setCellValue("Temperatura 2");
        hssfCell = hssfRow.createCell(3);
        hssfCell.setCellValue("Voltaje");

        int numeroReg = data.size()/4;
        int numcons=0;

        Log.d("data:","size:"+data.size());
        for(int i=0; i<numeroReg;i++){
            hssfRow = hssfSheet.createRow(i+5);
            for(int j = 0; j<4; j++){
                hssfCell = hssfRow.createCell(j);
                hssfCell.setCellValue(data.get(numcons));
                numcons++;
            }
        }
        Log.d("data:","numcons:"+numcons);

        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(this.getApplicationContext(),"Reporte generado correctamente",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
                setupEmailEvent("Datos tipo Tiempo","Archivo generado en la fecha:"+Calendar.getInstance().getTime(),nombreFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(),"Reporte no generado",Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    @Override
    public void createExcelTimeDataCrudo(String name, List<String> data, List<String> crudo) {
        String nombreFile= "DatosTipoTiempo.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        FileOutputStream outputStream = null;

        HSSFWorkbook wb= new HSSFWorkbook();
        HSSFSheet hssfSheet = wb.createSheet("Datos tipo Tiempo");

        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell hssfCell = hssfRow.createCell(0);

        //se agregan los datos de quien lo genera y la fecha etc
        Date currentTime = Calendar.getInstance().getTime();
        hssfCell.setCellValue("Creador:"+sp.getString("userId",""));
        hssfRow = hssfSheet.createRow(1);
        hssfCell = hssfRow.createCell(0);
        switch (sp.getString("userjerarquia","")){
            case "1":{
                hssfCell.setCellValue("Jerarquía: Administrador");
                break;
            }
            case "4":{
                hssfCell.setCellValue("Jerarquía: Producción");
                break;
            }
        }
        hssfRow = hssfSheet.createRow(2);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("Fecha:"+currentTime);

        //titulos
        hssfRow = hssfSheet.createRow(4);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("TimeStamp1");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue("Temperatura 1");
        hssfCell = hssfRow.createCell(2);
        hssfCell.setCellValue("Temperatura 2");
        hssfCell = hssfRow.createCell(3);
        hssfCell.setCellValue("Voltaje");

        int numeroReg = data.size()/4;
        int numcons=0;
        int numconscrudo=0;

        Log.d("data:","size:"+data.size());
        for(int i=0; i<numeroReg*2;i+=2){
            hssfRow = hssfSheet.createRow(i+5);
            for(int j = 0; j<4; j++){
                hssfCell = hssfRow.createCell(j);
                hssfCell.setCellValue(data.get(numcons));
                numcons++;
            }
            hssfRow = hssfSheet.createRow(i+6);
            for(int j = 0; j<4; j++){
                hssfCell = hssfRow.createCell(j);
                hssfCell.setCellValue(crudo.get(numconscrudo));
                numconscrudo++;
            }
        }
        Log.d("data:","numcons:"+numcons);

        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(this.getApplicationContext(),"Reporte generado correctamente",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
                setupEmailEvent("Datos tipo Tiempo","Archivo generado en la fecha:"+Calendar.getInstance().getTime(),nombreFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(),"Reporte no generado",Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    @Override
    public void createExcelEventData(String name, List<String> data) {
        String nombreFile= "DatosTipoEvento.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        FileOutputStream outputStream = null;

        HSSFWorkbook wb= new HSSFWorkbook();
        HSSFSheet hssfSheet = wb.createSheet("Datos tipo Evento");

        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell hssfCell = hssfRow.createCell(0);

        //se agregan los datos de quien lo genera y la fecha etc
        Date currentTime = Calendar.getInstance().getTime();
        hssfCell.setCellValue("Creador:"+sp.getString("userId",""));
        hssfRow = hssfSheet.createRow(1);
        hssfCell = hssfRow.createCell(0);
        switch (sp.getString("userjerarquia","")){
            case "1":{
                hssfCell.setCellValue("Jerarquía: Administrador");
                break;
            }
            case "4":{
                hssfCell.setCellValue("Jerarquía: Producción");
                break;
            }
        }
        hssfRow = hssfSheet.createRow(2);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("Fecha:"+currentTime);

        //titulos
        hssfRow = hssfSheet.createRow(4);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("TimeStamp START");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue("TimeStamp END");
        hssfCell = hssfRow.createCell(2);
        hssfCell.setCellValue("Tipo de evento");
        hssfCell = hssfRow.createCell(3);
        hssfCell.setCellValue("Temperatura 1I");
        hssfCell = hssfRow.createCell(4);
        hssfCell.setCellValue("Temperatura 2F");
        hssfCell = hssfRow.createCell(5);
        hssfCell.setCellValue("Voltaje");


        int numeroReg = data.size()/6;
        int numcons=0;
        for(int i=0; i<numeroReg;i++){
            hssfRow = hssfSheet.createRow(i+5);
            for(int j = 0; j<6; j++){
                hssfCell = hssfRow.createCell(j);
                hssfCell.setCellValue(data.get(numcons));
                numcons++;
            }
        }

        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(this.getApplicationContext(),"Reporte generado correctamente",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
                setupEmailEvent("Datos tipo Evento","Archivo generado en la fecha:"+Calendar.getInstance().getTime(),nombreFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(),"Reporte no generado",Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

    @Override
    public void createExcelEventDataCrudo(String name, List<String> data, List<String> crudo) {
        String nombreFile= "DatosTipoEvento.xls";
        File file = new File(this.getExternalFilesDir(null),nombreFile);
        FileOutputStream outputStream = null;

        HSSFWorkbook wb= new HSSFWorkbook();
        HSSFSheet hssfSheet = wb.createSheet("Datos tipo Evento");

        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell hssfCell = hssfRow.createCell(0);

        //se agregan los datos de quien lo genera y la fecha etc
        Date currentTime = Calendar.getInstance().getTime();
        hssfCell.setCellValue("Creador:"+sp.getString("userId",""));
        hssfRow = hssfSheet.createRow(1);
        hssfCell = hssfRow.createCell(0);
        switch (sp.getString("userjerarquia","")){
            case "1":{
                hssfCell.setCellValue("Jerarquía: Administrador");
                break;
            }
            case "4":{
                hssfCell.setCellValue("Jerarquía: Producción");
                break;
            }
        }
        hssfRow = hssfSheet.createRow(2);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("Fecha:"+currentTime);

        //titulos
        hssfRow = hssfSheet.createRow(4);
        hssfCell = hssfRow.createCell(0);
        hssfCell.setCellValue("TimeStamp START");
        hssfCell = hssfRow.createCell(1);
        hssfCell.setCellValue("TimeStamp END");
        hssfCell = hssfRow.createCell(2);
        hssfCell.setCellValue("Tipo de evento");
        hssfCell = hssfRow.createCell(3);
        hssfCell.setCellValue("Temperatura 1I");
        hssfCell = hssfRow.createCell(4);
        hssfCell.setCellValue("Temperatura 2F");
        hssfCell = hssfRow.createCell(5);
        hssfCell.setCellValue("Voltaje");


        int numeroReg = data.size()/6;
        int numcons=0;
        int numconscrudo=0;
        for(int i=0; i<numeroReg*2;i+=2){
            hssfRow = hssfSheet.createRow(i+5);
            for(int j = 0; j<6; j++){
                hssfCell = hssfRow.createCell(j);
                hssfCell.setCellValue(data.get(numcons));
                numcons++;
            }
            hssfRow = hssfSheet.createRow(i+6);
            for(int j = 0; j<6; j++){
                hssfCell = hssfRow.createCell(j);
                hssfCell.setCellValue(crudo.get(numconscrudo));
                numconscrudo++;
            }
        }

        try {
            outputStream = new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(this.getApplicationContext(),"Reporte generado correctamente",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
                setupEmailEvent("Datos tipo Evento","Archivo generado en la fecha:"+Calendar.getInstance().getTime(),nombreFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }


        } catch (java.io.IOException e) {
            Toast.makeText(this.getApplicationContext(),"Reporte no generado",Toast.LENGTH_LONG).show();
            e.printStackTrace();

        }
    }

}