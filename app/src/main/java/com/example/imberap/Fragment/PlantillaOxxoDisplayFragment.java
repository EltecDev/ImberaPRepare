package com.example.imberap.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.imberap.BluetoothServices.BluetoothLeService;
import com.example.imberap.BluetoothServices.BluetoothServices;
import com.example.imberap.R;
import com.example.imberap.Utility.GetHexFromRealDataImbera;
import com.example.imberap.Utility.GetHexFromRealDataOxxoDisplay;
import com.example.imberap.Utility.GetRealDataFromHexaImbera;
import com.example.imberap.Utility.GetRealDataFromHexaOxxoDisplay;
import com.example.imberap.Utility.GlobalTools;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class PlantillaOxxoDisplayFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    String TAG="PlantillaOxxoDisplayFragment";
    listener listenermain;
    SharedPreferences sp;
    SharedPreferences.Editor esp;

    List<TextView> tvList = new ArrayList<TextView>();
    List<EditText> ListEditText = new ArrayList<EditText>();
    List<String> ListPlantillasNombres = new ArrayList<String>();
    List<String> ListPlantillas = new ArrayList<String>();
    String plantillaSeleccionadaName;
    String plantillaSeleccionada;

    List<String> FinalListDataRealState = new ArrayList<String>() ;
    List<String> FinalListDataHandshake = new ArrayList<String>() ;
    List<String> FinalListDataPlantilla = new ArrayList<String>() ;

    ScrollView scrollView;
    SwitchMaterial switchOxxoEscalaTemperatura,switchOxxoModoAhorrador,switchOxxoOnOffdeshielo,switchOxxoPullDown,switchOxxoSwitchPuerta,switchOxxoSetAbreviadoParametros;

    TextView tvActOxxoSetpointDiurno, tvActOxxoDiferencialDiurno, tvActOxxoOffsetDiurno,tvActOxxoLimiteInferiorAjusteSetPoint,tvActLimiteSuperiorAjusteSetPoint,tvActOxxoDiferencialTempParaAhorro1,tvActOxxoDiferencialTempParaAhorro2
            ,tvActOxxoTemperaturaEvaporadorTerminarDeshielo,tvActOxxoTemperaturaAmbienteTerminarDeshielo,tvActOxxoTemperaturaPulldown,tvActOxxoAlarmaTemperaturaAlta,tvActOxxoAlarmaTemperaturaBaja,tvActOxxoDiferencialAlarmasTemperatura,tvActOxxoVoltajeProteccionMinimo120,tvOxxoVoltajeProteccionMaximo120minimo220,
            tvOxxoVoltajeProteccionMaximo220,tvOxxoTiempoValidarFallaVoltaje,tvOxxoTiempoEntreDeshielos,tvOxxoTiempoMaximoDeshielo,tvOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta,tvOxxoTiempoValidacionAlarmaPuertaAbierta,
            tvOxxoTiempoPermanenciaModoNocturno,tvOxxoIntensidadFiltroParaSensorAmbienteALaSubida,tvOxxoTiempoAdaptivoDeshielo,tvOxxoHisteresisParaProteccionVoltaje,
            tvOxxoTiempoValidacionSalirFallaVoltaje,tvOxxoTiempoGoteo,tvOxxoTiempoDescansoMinimoCompresor,tvOxxotiempoMaximoCompresorEncendido,tvOxxoTiempoDescansoCompresorCumpleF4,tvOxxoTiempoEncendidoApagadoDeVentilador,tvOxxoTiempoPuertaCerradaEntrarModoAhorro1,tvOxxoTiempoPuertaCerradaEntrarModoAhorro2,tvOxxoDireccionModbus,tvOxxoPassword,tvactualPlantilla;

    TextView tvTemperaturaDeReinicio,tvTemperaturaParaIniciarDeshielo,tvDiferencialDeTemperaturaParaAlarmaDeDeficiencia,tvTiempoBloqueoDisplayDespuesDeshielo,tvTiempoCompresorEncendidoParaMedicionesDeficiencia, tvRetardoParaEncenderSegundoCompresor;

    EditText etActOxxoSetpointDiurno,etActOxxoDiferencialDiurno,etActOxxoOffsetDiurno,etActOxxoLimiteInferiorAjusteSetPoint,etActLimiteSuperiorAjusteSetPoint,etActOxxoDiferencialTempParaAhorro1,etActOxxoDiferencialTempParaAhorro2
            ,etActOxxoTemperaturaEvaporadorTerminarDeshielo,etActOxxoTemperaturaAmbienteTerminarDeshielo,etActOxxoTemperaturaPulldown,etActOxxoAlarmaTemperaturaAlta,etActOxxoalarmaTemperaturaBaja,etActOxxoDiferencialAlarmasTemperatura,etActOxxoVoltajeProteccionMinimo120,etOxxoVoltajeProteccionMaximo120minimo220,
            etOxxoVoltajeProteccionMaximo220,etOxxoTiempoValidarFallaVoltaje,etOxxoTiempoEntreDeshielos,etOxxoTiempoMaximoDeshielo,etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta,etOxxoTiempoValidacionAlarmaPuertaAbierta,
            etOxxoTiempoPermanenciaModoNocturno,etOxxoIntensidadFiltroParaSensorAmbienteALaSubida,etOxxoTiempoAdaptivoDeshielo,etOxxoHisteresisParaProteccionVoltaje,etOxxoTiempoValidacionSalirFallaVoltaje,
            etOxxoTiempoGoteo,etOxxoTiempoDescansoMinimoCompresor,etOxxotiempoMaximoCompresorEncendido,etOxxoTiempoDescansoCompresorCumpleF4,etOxxoTiempoEncendidoApagadoDeVentilador,etOxxoTiempoPuertaCerradaEntrarModoAhorro1,etOxxoTiempoPuertaCerradaEntrarModoAhorro2,etOxxoDireccionModbus,etOxxoPassword,etOxxoNewPlantillaversion,etOxxoactualFirmwareVersion,etOxxoModelTrefp;

    EditText etTemperaturaDeReinicio,etTemperaturaParaIniciarDeshielo,etDiferencialDeTemperaturaParaAlarmaDeDeficiencia,etTiempoBloqueoDisplayDespuesDeshielo,etTiempoCompresorEncendidoParaMedicionesDeficiencia, etRetardoParaEncenderSegundoCompresor;
    TextView tvsubtituloPlantillaOxxo, tvtituloPlantillaOxxo;
    Button btnGetPlantilla, btnDownloadPlantillas;
    Button btnenviarfwOperadores,btnenviarPlantillaOperadores, btngetPlantilla, btnsendPlantilla, btnEnviarFwYPlantilla;

    Spinner spinnerOxxoModosdeshielo ,spinnerOxxoProteccionVoltaje,spinnerPlantillas;
    Spinner spinnerOxxoNumeroSensores, spinnerNivelesTiempoMostrarFallas, spinnerconfigRelevadores;
    BluetoothServices bluetoothServices;
    BluetoothLeService bluetoothLeService;

    androidx.appcompat.app.AlertDialog progressdialog = null;
    View dialogViewProgressBar;

    List<String> dataListPlantilla = new ArrayList<String>();
    Context context;

    public PlantillaOxxoDisplayFragment(){}

    public PlantillaOxxoDisplayFragment(BluetoothServices bluetoothServices, Context context) {
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
        View view = inflater.inflate(R.layout.fragment_plantilla_oxxo_display, container, false);
        init(view);

        view.findViewById(R.id.btnDownloadPlantillas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadPlantillas();
            }
        });
        view.findViewById(R.id.btnSendPlantillaOperador).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPlantillaOperador();
            }
        });
        view.findViewById(R.id.btnSendFwOperador).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFwOperador();
            }
        });
        view.findViewById(R.id.btnSendPlantillaFwTecnico).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataPlantillaFwTecnico();
            }
        });

        view.findViewById(R.id.btnSendPlantilla).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataPlantilla();
            }
        });
        view.findViewById(R.id.btnGetPlantilla).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActualData();
            }
        });
        view.findViewById(R.id.btnSendPlantillaObtenida).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPlantillaActual();
            }
        });
        view.findViewById(R.id.btnSendInfoCrudo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInfoCrudoExcel();
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarUIJerarquia();
    }

    public void actualizarUIJerarquia(){

        /*tvsubtituloPlantillaOxxo.setVisibility(View.GONE);
        spinnerPlantillas.setVisibility(View.GONE);
        btnDownloadPlantillas.setVisibility(View.GONE);
        btnenviarfwOperadores.setVisibility(View.GONE);
        btnenviarPlantillaOperadores.setVisibility(View.GONE);
        btnEnviarFwYPlantilla.setVisibility(View.GONE);*/

        if (!sp.getString("userId","").equals("")){//si no hay usuario logeado entonces no escanear
            switch (sp.getString("userjerarquia","")){
                case "1":{
                    tvtituloPlantillaOxxo.setText("Aquí puedes editar plantillas y mandarlas como parámetros a tu dispositivo IMBERA-OXXO");
                    btnEnviarFwYPlantilla.setVisibility(View.GONE);
                    tvsubtituloPlantillaOxxo.setVisibility(View.VISIBLE);
                    spinnerPlantillas.setVisibility(View.VISIBLE);
                    btnDownloadPlantillas.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    btnenviarfwOperadores.setVisibility(View.GONE);
                    btnGetPlantilla.setVisibility(View.VISIBLE);
                    btnenviarPlantillaOperadores.setVisibility(View.GONE);
                    btnsendPlantilla.setVisibility(View.VISIBLE);
                    break;
                }
                case "2":{

                    break;
                }
                case "3":{

                    break;
                }
                case "4":{
                    tvtituloPlantillaOxxo.setText("Aquí puedes editar plantillas y mandarlas como parámetros a tu dispositivo IMBERA-OXXO");
                    btnEnviarFwYPlantilla.setVisibility(View.GONE);
                    tvsubtituloPlantillaOxxo.setVisibility(View.VISIBLE);
                    spinnerPlantillas.setVisibility(View.VISIBLE);
                    btnDownloadPlantillas.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.VISIBLE);
                    btnGetPlantilla.setVisibility(View.GONE);
                    btnenviarPlantillaOperadores.setVisibility(View.GONE);
                    break;
                }
                case "5":{
                    tvtituloPlantillaOxxo.setText("Usa los botones de abajo para proceder a actualizar el equipo CEO");
                    btnEnviarFwYPlantilla.setVisibility(View.GONE);
                    tvsubtituloPlantillaOxxo.setVisibility(View.GONE);
                    btnenviarfwOperadores.setVisibility(View.VISIBLE);
                    spinnerPlantillas.setVisibility(View.GONE);
                    btnenviarPlantillaOperadores.setVisibility(View.VISIBLE);
                    btnDownloadPlantillas.setVisibility(View.GONE);
                    scrollView.setVisibility(View.GONE);
                    btnGetPlantilla.setVisibility(View.GONE);
                    btnsendPlantilla.setVisibility(View.GONE);
                    break;
                }
                case "6":{
                    tvtituloPlantillaOxxo.setText("Usa los botones de abajo para proceder a actualizar el equipo CEO");
                    btnEnviarFwYPlantilla.setVisibility(View.VISIBLE);
                    tvsubtituloPlantillaOxxo.setVisibility(View.GONE);
                    btnenviarfwOperadores.setVisibility(View.GONE);
                    spinnerPlantillas.setVisibility(View.GONE);
                    btnenviarPlantillaOperadores.setVisibility(View.GONE);
                    btnDownloadPlantillas.setVisibility(View.GONE);
                    scrollView.setVisibility(View.GONE);
                    btnGetPlantilla.setVisibility(View.GONE);
                    btnsendPlantilla.setVisibility(View.GONE);
                    break;
                }
            }

        }
    }

    private void init(View v) {
        bluetoothLeService = bluetoothServices.getBluetoothLeService();
        initCampos(v);
    }

    private void initCampos(View view) {
        scrollView = view.findViewById(R.id.scrollPlantillaOxxo);

        btnDownloadPlantillas= view.findViewById(R.id.btnDownloadPlantillas);
        btnGetPlantilla = view.findViewById(R.id.btnGetPlantilla);
        btnenviarfwOperadores = view.findViewById(R.id.btnSendFwOperador);
        btnsendPlantilla = view.findViewById(R.id.btnSendPlantilla);
        btnenviarPlantillaOperadores = view.findViewById(R.id.btnSendPlantillaOperador);
        btnEnviarFwYPlantilla = view.findViewById(R.id.btnSendPlantillaFwTecnico);

        tvtituloPlantillaOxxo=  view.findViewById(R.id.tvtituloPlantillaOxxo);
        tvsubtituloPlantillaOxxo =  view.findViewById(R.id.tvSubtituloPlantillaOxxo);

        tvActOxxoSetpointDiurno = view.findViewById(R.id.tvActOxxoSetpointDiurno);
        tvActOxxoDiferencialDiurno = view.findViewById(R.id.tvActOxxoDiferencialDiurno);
        tvActOxxoOffsetDiurno = view.findViewById(R.id.tvActOxxoOffsetDiurno);
        tvActOxxoLimiteInferiorAjusteSetPoint = view.findViewById(R.id.tvActOxxoLimiteInferiorAjusteSetPoint);
        tvActLimiteSuperiorAjusteSetPoint = view.findViewById(R.id.tvActLimiteSuperiorAjusteSetPoint);
        tvTemperaturaDeReinicio = view.findViewById(R.id.tvTemperaturaDeReinicio);
        tvTemperaturaParaIniciarDeshielo = view.findViewById(R.id.tvTemperaturaParaIniciarDeshielo);
        tvActOxxoDiferencialTempParaAhorro1 = view.findViewById(R.id.tvActOxxoDiferencialTempParaAhorro1);
        tvActOxxoDiferencialTempParaAhorro2 = view.findViewById(R.id.tvActOxxoDiferencialTempParaAhorro2);
        tvActOxxoTemperaturaEvaporadorTerminarDeshielo = view.findViewById(R.id.tvActOxxoTemperaturaEvaporadorTerminarDeshielo);
        tvActOxxoTemperaturaAmbienteTerminarDeshielo = view.findViewById(R.id.tvActOxxoTemperaturaAmbienteTerminarDeshielo);
        tvActOxxoTemperaturaPulldown = view.findViewById(R.id.tvActOxxoTemperaturaPulldown);
        tvActOxxoAlarmaTemperaturaAlta = view.findViewById(R.id.tvActOxxoAlarmaTemperaturaAlta);
        tvActOxxoAlarmaTemperaturaBaja = view.findViewById(R.id.tvActOxxoAlarmaTemperaturaBaja);
        tvActOxxoDiferencialAlarmasTemperatura = view.findViewById(R.id.tvActOxxoDiferencialParaAlarmasTemperatura);
        tvDiferencialDeTemperaturaParaAlarmaDeDeficiencia = view.findViewById(R.id.tvDiferencialDeTemperaturaParaAlarmaDeDeficiencia);
        tvActOxxoVoltajeProteccionMinimo120 = view.findViewById(R.id.tvActOxxoVoltajeProteccionMinimo120);
        tvOxxoVoltajeProteccionMaximo120minimo220 = view.findViewById(R.id.tvOxxoVoltajeProteccionMaximo120minimo220);
        tvOxxoVoltajeProteccionMaximo220 = view.findViewById(R.id.tvOxxoVoltajeProteccionMaximo220);
        tvOxxoTiempoValidarFallaVoltaje = view.findViewById(R.id.tvOxxoTiempoValidarFallaVoltaje);
        tvOxxoTiempoEntreDeshielos = view.findViewById(R.id.tvOxxoTiempoEntreDeshielos);
        tvOxxoTiempoMaximoDeshielo = view.findViewById(R.id.tvOxxoTiempoMaximoDeshielo);
        tvOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta = view.findViewById(R.id.tvOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta);
        tvOxxoTiempoValidacionAlarmaPuertaAbierta = view.findViewById(R.id.tvOxxoTiempoValidacionAlarmaPuertaAbierta);
        tvOxxoTiempoPermanenciaModoNocturno = view.findViewById(R.id.tvOxxoTiempoPermanenciaModoNocturno);
        tvTiempoBloqueoDisplayDespuesDeshielo = view.findViewById(R.id.tvTiempoBloqueoDisplayDespuesDeshielo);
        tvTiempoCompresorEncendidoParaMedicionesDeficiencia = view.findViewById(R.id.tvTiempoCompresorEncendidoParaMedicionesDeficiencia);
        tvRetardoParaEncenderSegundoCompresor = view.findViewById(R.id.tvRetardoParaEncenderSegundoCompresor);
        tvOxxoIntensidadFiltroParaSensorAmbienteALaSubida = view.findViewById(R.id.tvOxxoIntensidadFiltroParaSensorAmbienteALaSubida);
        tvOxxoTiempoAdaptivoDeshielo = view.findViewById(R.id.tvOxxoTiempoAdaptivoDeshielo);
        tvOxxoHisteresisParaProteccionVoltaje = view.findViewById(R.id.tvOxxoHisteresisParaProteccionVoltaje);
        tvOxxoTiempoValidacionSalirFallaVoltaje = view.findViewById(R.id.tvOxxoTiempoValidacionSalirFallaVoltaje);
        tvOxxoTiempoGoteo = view.findViewById(R.id.tvOxxoTiempoGoteo);
        tvOxxoTiempoDescansoMinimoCompresor = view.findViewById(R.id.tvOxxoTiempoDescansoMinimoCompresor);

        tvOxxotiempoMaximoCompresorEncendido = view.findViewById(R.id.tvOxxotiempoMaximoCompresorEncendido);
        tvOxxoTiempoDescansoCompresorCumpleF4 = view.findViewById(R.id.tvOxxoTiempoDescansoCompresorCumpleF4);
        tvOxxoTiempoEncendidoApagadoDeVentilador = view.findViewById(R.id.tvOxxoTiempoEncendidoApagadoDeVentilador);

        tvOxxoTiempoPuertaCerradaEntrarModoAhorro1 = view.findViewById(R.id.tvOxxoTiempoPuertaCerradaEntrarModoAhorro1);
        tvOxxoTiempoPuertaCerradaEntrarModoAhorro2 = view.findViewById(R.id.tvOxxoTiempoPuertaCerradaEntrarModoAhorro2);
        tvOxxoDireccionModbus = view.findViewById(R.id.tvOxxoDireccionModbus);
        tvOxxoPassword = view.findViewById(R.id.tvOxxoPassword);
        tvactualPlantilla = view.findViewById(R.id.tvactualPlantilla);


        tvList.add(tvActOxxoSetpointDiurno);
        tvList.add(tvActOxxoDiferencialDiurno);
        tvList.add(tvActOxxoOffsetDiurno);
        tvList.add(tvActOxxoLimiteInferiorAjusteSetPoint);
        tvList.add(tvActLimiteSuperiorAjusteSetPoint);
        tvList.add(tvTemperaturaDeReinicio);
        tvList.add(tvTemperaturaParaIniciarDeshielo);
        tvList.add(tvActOxxoDiferencialTempParaAhorro1);
        tvList.add(tvActOxxoDiferencialTempParaAhorro2);
        tvList.add(tvActOxxoTemperaturaEvaporadorTerminarDeshielo);
        tvList.add(tvActOxxoTemperaturaAmbienteTerminarDeshielo);
        tvList.add(tvActOxxoTemperaturaPulldown);
        tvList.add(tvActOxxoAlarmaTemperaturaAlta);
        tvList.add(tvActOxxoAlarmaTemperaturaBaja);
        tvList.add(tvActOxxoDiferencialAlarmasTemperatura);
        tvList.add(tvDiferencialDeTemperaturaParaAlarmaDeDeficiencia);
        tvList.add(tvActOxxoVoltajeProteccionMinimo120);
        tvList.add(tvOxxoVoltajeProteccionMaximo120minimo220);
        tvList.add(tvOxxoVoltajeProteccionMaximo220);
        tvList.add(tvOxxoTiempoValidarFallaVoltaje);
        tvList.add(tvOxxoTiempoEntreDeshielos);
        tvList.add(tvOxxoTiempoMaximoDeshielo);
        tvList.add(tvOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta);
        tvList.add(tvOxxoTiempoValidacionAlarmaPuertaAbierta);
        tvList.add(tvOxxoTiempoPermanenciaModoNocturno);
        tvList.add(tvTiempoBloqueoDisplayDespuesDeshielo);
        tvList.add(tvTiempoCompresorEncendidoParaMedicionesDeficiencia);
        tvList.add(tvRetardoParaEncenderSegundoCompresor);
        tvList.add(tvOxxoIntensidadFiltroParaSensorAmbienteALaSubida);
        tvList.add(tvOxxoTiempoAdaptivoDeshielo);
        tvList.add(tvOxxoHisteresisParaProteccionVoltaje);
        tvList.add(tvOxxoTiempoValidacionSalirFallaVoltaje);
        tvList.add(tvOxxoTiempoGoteo);
        tvList.add(tvOxxoTiempoDescansoMinimoCompresor);
        tvList.add(tvOxxotiempoMaximoCompresorEncendido);
        tvList.add(tvOxxoTiempoDescansoCompresorCumpleF4);
        tvList.add(tvOxxoTiempoEncendidoApagadoDeVentilador);
        tvList.add(tvOxxoTiempoPuertaCerradaEntrarModoAhorro1);
        tvList.add(tvOxxoTiempoPuertaCerradaEntrarModoAhorro2);
        tvList.add(tvOxxoDireccionModbus);
        tvList.add(tvOxxoPassword);
        tvList.add(tvactualPlantilla);

        etActOxxoSetpointDiurno = view.findViewById(R.id.etActOxxoSetpointDiurno);
        etActOxxoDiferencialDiurno = view.findViewById(R.id.etActOxxoDiferencialDiurno);
        etActOxxoOffsetDiurno = view.findViewById(R.id.etActOxxoOffsetDiurno);
        etActOxxoLimiteInferiorAjusteSetPoint = view.findViewById(R.id.etActOxxoLimiteInferiorAjusteSetPoint);
        etActLimiteSuperiorAjusteSetPoint = view.findViewById(R.id.etActLimiteSuperiorAjusteSetPoint);
        etTemperaturaDeReinicio = view.findViewById(R.id.etTemperaturaDeReinicio);
        etTemperaturaParaIniciarDeshielo = view.findViewById(R.id.etTemperaturaParaIniciarDeshielo);
        etActOxxoDiferencialTempParaAhorro1 = view.findViewById(R.id.etActOxxoDiferencialTempParaAhorro1);
        etActOxxoDiferencialTempParaAhorro2 = view.findViewById(R.id.etActOxxoDiferencialTempParaAhorro2);
        etActOxxoTemperaturaEvaporadorTerminarDeshielo = view.findViewById(R.id.etActOxxoTemperaturaEvaporadorTerminarDeshielo);
        etActOxxoTemperaturaAmbienteTerminarDeshielo = view.findViewById(R.id.etActOxxoTemperaturaAmbienteTerminarDeshielo);
        etActOxxoTemperaturaPulldown = view.findViewById(R.id.etActOxxoTemperaturaPulldown);
        etActOxxoAlarmaTemperaturaAlta = view.findViewById(R.id.etActOxxoAlarmaTemperaturaAlta);
        etActOxxoalarmaTemperaturaBaja = view.findViewById(R.id.etActOxxoAlarmaTemperaturaBaja);
        etActOxxoDiferencialAlarmasTemperatura = view.findViewById(R.id.etActOxxoDiferencialParaAlarmasTemperatura);
        etDiferencialDeTemperaturaParaAlarmaDeDeficiencia = view.findViewById(R.id.etDiferencialDeTemperaturaParaAlarmaDeDeficiencia);
        etActOxxoVoltajeProteccionMinimo120 = view.findViewById(R.id.etActOxxoVoltajeProteccionMinimo120);
        etOxxoVoltajeProteccionMaximo120minimo220 = view.findViewById(R.id.etOxxoVoltajeProteccionMaximo120minimo220);
        etOxxoVoltajeProteccionMaximo220 = view.findViewById(R.id.etOxxoVoltajeProteccionMaximo220);
        etOxxoTiempoValidarFallaVoltaje = view.findViewById(R.id.etOxxoTiempoValidarFallaVoltaje);
        etOxxoTiempoEntreDeshielos = view.findViewById(R.id.etOxxoTiempoEntreDeshielos);
        etOxxoTiempoMaximoDeshielo = view.findViewById(R.id.etOxxoTiempoMaximoDeshielo);
        etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta = view.findViewById(R.id.etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta);
        etOxxoTiempoValidacionAlarmaPuertaAbierta = view.findViewById(R.id.etOxxoTiempoValidacionAlarmaPuertaAbierta);
        etOxxoTiempoPermanenciaModoNocturno = view.findViewById(R.id.etOxxoTiempoPermanenciaModoNocturno);
        etTiempoBloqueoDisplayDespuesDeshielo = view.findViewById(R.id.etTiempoBloqueoDisplayDespuesDeshielo);
        etTiempoCompresorEncendidoParaMedicionesDeficiencia= view.findViewById(R.id.etTiempoCompresorEncendidoParaMedicionesDeficiencia);
        etRetardoParaEncenderSegundoCompresor = view.findViewById(R.id.etRetardoParaEncenderSegundoCompresor);
        etOxxoIntensidadFiltroParaSensorAmbienteALaSubida = view.findViewById(R.id.etOxxoIntensidadFiltroParaSensorAmbienteALaSubida);
        etOxxoTiempoAdaptivoDeshielo = view.findViewById(R.id.etOxxoTiempoAdaptivoDeshielo);
        spinnerOxxoProteccionVoltaje = view.findViewById(R.id.spinnerOxxoProteccionVoltaje);
        etOxxoHisteresisParaProteccionVoltaje = view.findViewById(R.id.etOxxoHisteresisParaProteccionVoltaje);
        etOxxoTiempoValidacionSalirFallaVoltaje = view.findViewById(R.id.etOxxoTiempoValidacionSalirFallaVoltaje);
        etOxxoTiempoGoteo = view.findViewById(R.id.etOxxoTiempoGoteo);
        etOxxoTiempoDescansoMinimoCompresor = view.findViewById(R.id.etOxxoTiempoDescansoMinimoCompresor);
        etOxxotiempoMaximoCompresorEncendido = view.findViewById(R.id.etOxxotiempoMaximoCompresorEncendido);
        etOxxoTiempoDescansoCompresorCumpleF4 = view.findViewById(R.id.etOxxoTiempoDescansoCompresorCumpleF4);
        etOxxoTiempoEncendidoApagadoDeVentilador = view.findViewById(R.id.etOxxoTiempoEncendidoApagadoDeVentilador);
        etOxxoTiempoPuertaCerradaEntrarModoAhorro1 = view.findViewById(R.id.etOxxoTiempoPuertaCerradaEntrarModoAhorro1);
        etOxxoTiempoPuertaCerradaEntrarModoAhorro2 = view.findViewById(R.id.etOxxoTiempoPuertaCerradaEntrarModoAhorro2);
        etOxxoDireccionModbus = view.findViewById(R.id.etOxxoDireccionModbus);
        etOxxoPassword = view.findViewById(R.id.etOxxoPassword);
        etOxxoNewPlantillaversion = view.findViewById(R.id.etOxxoNewPlantillaversion);
        etOxxoactualFirmwareVersion = view.findViewById(R.id.etOxxoactualFirmwareVersion);
        etOxxoModelTrefp = view.findViewById(R.id.etOxxoModelTrefp);

        ListEditText.add(etActOxxoSetpointDiurno);
        ListEditText.add(etActOxxoDiferencialDiurno);
        ListEditText.add(etActOxxoOffsetDiurno);
        ListEditText.add(etActOxxoLimiteInferiorAjusteSetPoint);
        ListEditText.add(etActLimiteSuperiorAjusteSetPoint);
        ListEditText.add(etTemperaturaDeReinicio);
        ListEditText.add(etTemperaturaParaIniciarDeshielo);
        ListEditText.add(etActOxxoDiferencialTempParaAhorro1);
        ListEditText.add(etActOxxoDiferencialTempParaAhorro2);
        ListEditText.add(etActOxxoTemperaturaEvaporadorTerminarDeshielo);
        ListEditText.add(etActOxxoTemperaturaAmbienteTerminarDeshielo);
        ListEditText.add(etActOxxoTemperaturaPulldown);
        ListEditText.add(etActOxxoAlarmaTemperaturaAlta);
        ListEditText.add(etActOxxoalarmaTemperaturaBaja);
        ListEditText.add(etActOxxoDiferencialAlarmasTemperatura);
        ListEditText.add(etDiferencialDeTemperaturaParaAlarmaDeDeficiencia);
        ListEditText.add(etActOxxoVoltajeProteccionMinimo120);
        ListEditText.add(etOxxoVoltajeProteccionMaximo120minimo220);
        ListEditText.add(etOxxoVoltajeProteccionMaximo220);
        ListEditText.add(etOxxoTiempoValidarFallaVoltaje);
        ListEditText.add(etOxxoTiempoEntreDeshielos);
        ListEditText.add(etOxxoTiempoMaximoDeshielo);
        ListEditText.add(etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta);
        ListEditText.add(etOxxoTiempoValidacionAlarmaPuertaAbierta);
        ListEditText.add(etOxxoTiempoPermanenciaModoNocturno);
        ListEditText.add(etTiempoBloqueoDisplayDespuesDeshielo);
        ListEditText.add(etTiempoCompresorEncendidoParaMedicionesDeficiencia);
        ListEditText.add(etRetardoParaEncenderSegundoCompresor);
        ListEditText.add(etOxxoIntensidadFiltroParaSensorAmbienteALaSubida);
        ListEditText.add(etOxxoTiempoAdaptivoDeshielo);
        ListEditText.add(etOxxoHisteresisParaProteccionVoltaje);
        ListEditText.add(etOxxoTiempoValidacionSalirFallaVoltaje);
        ListEditText.add(etOxxoTiempoGoteo);
        ListEditText.add(etOxxoTiempoDescansoMinimoCompresor);
        ListEditText.add(etOxxotiempoMaximoCompresorEncendido);
        ListEditText.add(etOxxoTiempoDescansoCompresorCumpleF4);
        ListEditText.add(etOxxoTiempoEncendidoApagadoDeVentilador);
        ListEditText.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro1);
        ListEditText.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro2);
        ListEditText.add(etOxxoDireccionModbus);
        ListEditText.add(etOxxoPassword);
        ListEditText.add(etOxxoNewPlantillaversion);
        ListEditText.add(etOxxoactualFirmwareVersion);
        ListEditText.add(etOxxoModelTrefp);


        //Declaraciòn de swiches
        //switchOxxoModoAhorrador = view.findViewById(R.id.switchOxxoModoAhorrador);
        switchOxxoOnOffdeshielo = view.findViewById(R.id.switchOxxoOnOffdeshielo);
        //switchOxxoPullDown = view.findViewById(R.id.switchOxxoPullDown);
        switchOxxoSwitchPuerta = view.findViewById(R.id.switchOxxoSwitchPuerta);
        //switchOxxoSetAbreviadoParametros = view.findViewById(R.id.switchOxxoSetAbreviadoParametros);
        switchOxxoEscalaTemperatura = view.findViewById(R.id.switchOxxoEscalaTemperatura);

        //declaracion de spinners
        spinnerPlantillas = view.findViewById(R.id.spinnerGetPlantillaRemoto);
        spinnerOxxoModosdeshielo = view.findViewById(R.id.spinnerOxxoModosdeshielo);
        spinnerOxxoProteccionVoltaje = view.findViewById(R.id.spinnerOxxoProteccionVoltaje);
        spinnerOxxoNumeroSensores = view.findViewById(R.id.spinnerOxxoNumeroSensores);
        spinnerNivelesTiempoMostrarFallas = view.findViewById(R.id.spinnerNivelesTiempoMostrarFallas);
        spinnerconfigRelevadores = view.findViewById(R.id.spinnerconfigRelevadores);
        spinnerPlantillas.setOnItemSelectedListener(this);


        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(view.getContext(),
                R.array.modosDeshieloOxxo, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOxxoModosdeshielo.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(view.getContext(),
                R.array.proteccionVoltaje, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOxxoProteccionVoltaje.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(view.getContext(),
                R.array.numeroSensoresTempSistema, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOxxoNumeroSensores.setAdapter(adapter3);

        ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(view.getContext(),
                R.array.nivelesTiempoMostrarFallas, android.R.layout.simple_spinner_item);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNivelesTiempoMostrarFallas.setAdapter(adapter4);

        ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(view.getContext(),
                R.array.configRelevadores, android.R.layout.simple_spinner_item);
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerconfigRelevadores.setAdapter(adapter5);

        List<String> vacio = new ArrayList<String>();
        vacio.add("No hay plantillas descargadas");
        ArrayAdapter<String> adaptervacio = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, vacio);
        adaptervacio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlantillas.setAdapter(adaptervacio);
    }

    private void downloadPlantillas(){
        new MyAsyncTaskGetPlantillas().execute();
    }

    private void sendPlantillaActual(){
        if (!checkEmptydata()){
            Toast.makeText(getContext(), "Campos vacíos, intenta obtener plantilla", Toast.LENGTH_SHORT).show();
        }else{
            listenermain.printExcel(getListToExcel(),"oxxo");
        }

    }

    private void sendInfoCrudoExcel(){
        new MyAsyncTaskGetActualStatusTotalCrudo().execute();
    }

    private void getActualData() {
        bluetoothLeService = bluetoothServices.getBluetoothLeService();
        if (bluetoothServices != null && bluetoothLeService != null)
            getPlantillacommand();
        else
            Toast.makeText(getContext(), "No te has conectados a un BLE", Toast.LENGTH_SHORT).show();
    }

    private void setRealData() {
        if (dataListPlantilla.size() == 1) {
            Toast.makeText(getContext(), "Realizar Hanshake primero", Toast.LENGTH_SHORT).show();
        } else {

            tvActOxxoSetpointDiurno.setText(dataListPlantilla.get(4));
            tvActOxxoDiferencialDiurno.setText(dataListPlantilla.get(5));
            tvActOxxoOffsetDiurno.setText(dataListPlantilla.get(6));
            tvActOxxoLimiteInferiorAjusteSetPoint.setText(dataListPlantilla.get(7));
            tvActLimiteSuperiorAjusteSetPoint.setText(dataListPlantilla.get(8));
            tvTemperaturaDeReinicio.setText(dataListPlantilla.get(9));
            tvTemperaturaParaIniciarDeshielo.setText(dataListPlantilla.get(10));
            tvActOxxoDiferencialTempParaAhorro1.setText(dataListPlantilla.get(11));
            tvActOxxoDiferencialTempParaAhorro2.setText(dataListPlantilla.get(12));
            tvActOxxoTemperaturaEvaporadorTerminarDeshielo.setText(dataListPlantilla.get(13));
            tvActOxxoTemperaturaAmbienteTerminarDeshielo.setText(dataListPlantilla.get(14));
            tvActOxxoTemperaturaPulldown.setText(dataListPlantilla.get(15));
            tvActOxxoAlarmaTemperaturaAlta.setText(dataListPlantilla.get(16));
            tvActOxxoAlarmaTemperaturaBaja.setText(dataListPlantilla.get(17));
            tvActOxxoDiferencialAlarmasTemperatura.setText(dataListPlantilla.get(18));
            tvDiferencialDeTemperaturaParaAlarmaDeDeficiencia.setText(dataListPlantilla.get(19));
            tvActOxxoVoltajeProteccionMinimo120.setText(dataListPlantilla.get(20));
            tvOxxoVoltajeProteccionMaximo120minimo220.setText(dataListPlantilla.get(21));
            tvOxxoVoltajeProteccionMaximo220.setText(dataListPlantilla.get(22));
            tvOxxoTiempoValidarFallaVoltaje.setText(dataListPlantilla.get(23));
            tvOxxoTiempoEntreDeshielos.setText(dataListPlantilla.get(24));
            tvOxxoTiempoMaximoDeshielo.setText(dataListPlantilla.get(25));
            tvOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta.setText(dataListPlantilla.get(26));
            tvOxxoTiempoValidacionAlarmaPuertaAbierta.setText(dataListPlantilla.get(27));
            tvOxxoTiempoPermanenciaModoNocturno.setText(dataListPlantilla.get(28));
            tvTiempoBloqueoDisplayDespuesDeshielo.setText(dataListPlantilla.get(29));
            tvTiempoCompresorEncendidoParaMedicionesDeficiencia.setText(dataListPlantilla.get(30));
            tvRetardoParaEncenderSegundoCompresor.setText(dataListPlantilla.get(31));
            tvOxxoIntensidadFiltroParaSensorAmbienteALaSubida.setText(dataListPlantilla.get(34));
            tvOxxoTiempoAdaptivoDeshielo.setText(dataListPlantilla.get(36));
            tvOxxoHisteresisParaProteccionVoltaje.setText(dataListPlantilla.get(41));
            tvOxxoTiempoValidacionSalirFallaVoltaje.setText(dataListPlantilla.get(42));
            tvOxxoTiempoGoteo.setText(dataListPlantilla.get(43));
            tvOxxoTiempoDescansoMinimoCompresor.setText(dataListPlantilla.get(44));
            tvOxxotiempoMaximoCompresorEncendido.setText(dataListPlantilla.get(45));
            tvOxxoTiempoDescansoCompresorCumpleF4.setText(dataListPlantilla.get(46));
            tvOxxoTiempoEncendidoApagadoDeVentilador.setText(dataListPlantilla.get(47));
            tvOxxoTiempoPuertaCerradaEntrarModoAhorro1.setText(dataListPlantilla.get(48));
            tvOxxoTiempoPuertaCerradaEntrarModoAhorro2.setText(dataListPlantilla.get(49));
            tvOxxoDireccionModbus.setText(dataListPlantilla.get(50));
            tvOxxoPassword.setText(dataListPlantilla.get(51));

            //tvactualPlantilla.setText(dataListPlantilla.get(37));

            //mismos datos para los campos editables.
            etActOxxoSetpointDiurno.setText(dataListPlantilla.get(4));
            etActOxxoDiferencialDiurno.setText(dataListPlantilla.get(5));
            etActOxxoOffsetDiurno.setText(dataListPlantilla.get(6));
            etActOxxoLimiteInferiorAjusteSetPoint.setText(dataListPlantilla.get(7));
            etActLimiteSuperiorAjusteSetPoint.setText(dataListPlantilla.get(8));
            etTemperaturaDeReinicio.setText(dataListPlantilla.get(9));
            etTemperaturaParaIniciarDeshielo.setText(dataListPlantilla.get(10));
            etActOxxoDiferencialTempParaAhorro1.setText(dataListPlantilla.get(11));
            etActOxxoDiferencialTempParaAhorro2.setText(dataListPlantilla.get(12));
            etActOxxoTemperaturaEvaporadorTerminarDeshielo.setText(dataListPlantilla.get(13));
            etActOxxoTemperaturaAmbienteTerminarDeshielo.setText(dataListPlantilla.get(14));
            etActOxxoTemperaturaPulldown.setText(dataListPlantilla.get(15));
            etActOxxoAlarmaTemperaturaAlta.setText(dataListPlantilla.get(16));
            etActOxxoalarmaTemperaturaBaja.setText(dataListPlantilla.get(17));
            etActOxxoDiferencialAlarmasTemperatura.setText(dataListPlantilla.get(18));
            etDiferencialDeTemperaturaParaAlarmaDeDeficiencia.setText(dataListPlantilla.get(19));
            etActOxxoVoltajeProteccionMinimo120.setText(dataListPlantilla.get(20));
            etOxxoVoltajeProteccionMaximo120minimo220.setText(dataListPlantilla.get(21));
            etOxxoVoltajeProteccionMaximo220.setText(dataListPlantilla.get(22));
            etOxxoTiempoValidarFallaVoltaje.setText(dataListPlantilla.get(23));
            etOxxoTiempoEntreDeshielos.setText(dataListPlantilla.get(24));
            etOxxoTiempoMaximoDeshielo.setText(dataListPlantilla.get(25));
            etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta.setText(dataListPlantilla.get(26));
            etOxxoTiempoValidacionAlarmaPuertaAbierta.setText(dataListPlantilla.get(27));
            etOxxoTiempoPermanenciaModoNocturno.setText(dataListPlantilla.get(28));
            etTiempoBloqueoDisplayDespuesDeshielo.setText(dataListPlantilla.get(29));
            etTiempoCompresorEncendidoParaMedicionesDeficiencia.setText(dataListPlantilla.get(30));
            etRetardoParaEncenderSegundoCompresor.setText(dataListPlantilla.get(31));
            etOxxoIntensidadFiltroParaSensorAmbienteALaSubida.setText(dataListPlantilla.get(34));
            etOxxoTiempoAdaptivoDeshielo.setText(dataListPlantilla.get(36));//verificar negativo
            //etOxxoProteccionVoltaje.setText(dataListPlantilla.get(28));
            etOxxoHisteresisParaProteccionVoltaje.setText(dataListPlantilla.get(41));
            etOxxoTiempoValidacionSalirFallaVoltaje.setText(dataListPlantilla.get(42));//verificaer
            etOxxoTiempoGoteo.setText(dataListPlantilla.get(43));
            etOxxoTiempoDescansoMinimoCompresor.setText(dataListPlantilla.get(44));
            etOxxotiempoMaximoCompresorEncendido.setText(dataListPlantilla.get(45));
            etOxxoTiempoDescansoCompresorCumpleF4.setText(dataListPlantilla.get(46));
            etOxxoTiempoEncendidoApagadoDeVentilador.setText(dataListPlantilla.get(47));
            etOxxoTiempoPuertaCerradaEntrarModoAhorro1.setText((dataListPlantilla.get(48)));
            etOxxoTiempoPuertaCerradaEntrarModoAhorro2.setText(dataListPlantilla.get(49));
            etOxxoDireccionModbus.setText(dataListPlantilla.get(50));
            etOxxoPassword.setText(dataListPlantilla.get(51));
            String fw = dataListPlantilla.get(59)+dataListPlantilla.get(60);
            fw = fw.replace(".","");
            fw = fw.substring(0,2)+"."+fw.substring(2);
            etOxxoactualFirmwareVersion.setText(fw);
            etOxxoModelTrefp.setText(dataListPlantilla.get(57));
            etOxxoNewPlantillaversion.setText(dataListPlantilla.get(62));
            //etOxxoactualFirmwareVersion.setText(dataListPlantilla.get(36));
            //etOxxoModelTrefp.setText(dataListPlantilla.get(37));

        }

    }
    private void setRealDataRemoto() {
        if (dataListPlantilla.size() == 1) {
            Toast.makeText(getContext(), "Realizar Hanshake primero", Toast.LENGTH_SHORT).show();
        } else {

            tvActOxxoSetpointDiurno.setText(dataListPlantilla.get(4));
            tvActOxxoDiferencialDiurno.setText(dataListPlantilla.get(5));
            tvActOxxoOffsetDiurno.setText(dataListPlantilla.get(6));
            tvActOxxoLimiteInferiorAjusteSetPoint.setText(dataListPlantilla.get(7));
            tvActLimiteSuperiorAjusteSetPoint.setText(dataListPlantilla.get(8));
            tvTemperaturaDeReinicio.setText(dataListPlantilla.get(9));
            tvTemperaturaParaIniciarDeshielo.setText(dataListPlantilla.get(10));
            tvActOxxoDiferencialTempParaAhorro1.setText(dataListPlantilla.get(11));
            tvActOxxoDiferencialTempParaAhorro2.setText(dataListPlantilla.get(12));
            tvActOxxoTemperaturaEvaporadorTerminarDeshielo.setText(dataListPlantilla.get(13));
            tvActOxxoTemperaturaAmbienteTerminarDeshielo.setText(dataListPlantilla.get(14));
            tvActOxxoTemperaturaPulldown.setText(dataListPlantilla.get(15));
            tvActOxxoAlarmaTemperaturaAlta.setText(dataListPlantilla.get(16));
            tvActOxxoAlarmaTemperaturaBaja.setText(dataListPlantilla.get(17));
            tvActOxxoDiferencialAlarmasTemperatura.setText(dataListPlantilla.get(18));
            tvDiferencialDeTemperaturaParaAlarmaDeDeficiencia.setText(dataListPlantilla.get(19));
            tvActOxxoVoltajeProteccionMinimo120.setText(dataListPlantilla.get(20));
            tvOxxoVoltajeProteccionMaximo120minimo220.setText(dataListPlantilla.get(21));
            tvOxxoVoltajeProteccionMaximo220.setText(dataListPlantilla.get(22));
            tvOxxoTiempoValidarFallaVoltaje.setText(dataListPlantilla.get(23));
            tvOxxoTiempoEntreDeshielos.setText(dataListPlantilla.get(24));
            tvOxxoTiempoMaximoDeshielo.setText(dataListPlantilla.get(25));
            tvOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta.setText(dataListPlantilla.get(26));
            tvOxxoTiempoValidacionAlarmaPuertaAbierta.setText(dataListPlantilla.get(27));
            tvOxxoTiempoPermanenciaModoNocturno.setText(dataListPlantilla.get(28));
            tvTiempoBloqueoDisplayDespuesDeshielo.setText(dataListPlantilla.get(29));
            tvTiempoCompresorEncendidoParaMedicionesDeficiencia.setText(dataListPlantilla.get(30));
            tvRetardoParaEncenderSegundoCompresor.setText(dataListPlantilla.get(31));
            tvOxxoIntensidadFiltroParaSensorAmbienteALaSubida.setText(dataListPlantilla.get(34));
            tvOxxoTiempoAdaptivoDeshielo.setText(dataListPlantilla.get(36));
            tvOxxoHisteresisParaProteccionVoltaje.setText(dataListPlantilla.get(41));
            tvOxxoTiempoValidacionSalirFallaVoltaje.setText(dataListPlantilla.get(42));
            tvOxxoTiempoGoteo.setText(dataListPlantilla.get(43));
            tvOxxoTiempoDescansoMinimoCompresor.setText(dataListPlantilla.get(44));
            tvOxxotiempoMaximoCompresorEncendido.setText(dataListPlantilla.get(45));
            tvOxxoTiempoDescansoCompresorCumpleF4.setText(dataListPlantilla.get(46));
            tvOxxoTiempoEncendidoApagadoDeVentilador.setText(dataListPlantilla.get(47));
            tvOxxoTiempoPuertaCerradaEntrarModoAhorro1.setText(dataListPlantilla.get(48));
            tvOxxoTiempoPuertaCerradaEntrarModoAhorro2.setText(dataListPlantilla.get(49));
            tvOxxoDireccionModbus.setText(dataListPlantilla.get(50));
            tvOxxoPassword.setText(dataListPlantilla.get(51));

            //tvactualPlantilla.setText(dataListPlantilla.get(37));

            //mismos datos para los campos editables.
            etActOxxoSetpointDiurno.setText(dataListPlantilla.get(4));
            etActOxxoDiferencialDiurno.setText(dataListPlantilla.get(5));
            etActOxxoOffsetDiurno.setText(dataListPlantilla.get(6));
            etActOxxoLimiteInferiorAjusteSetPoint.setText(dataListPlantilla.get(7));
            etActLimiteSuperiorAjusteSetPoint.setText(dataListPlantilla.get(8));
            etTemperaturaDeReinicio.setText(dataListPlantilla.get(9));
            etTemperaturaParaIniciarDeshielo.setText(dataListPlantilla.get(10));
            etActOxxoDiferencialTempParaAhorro1.setText(dataListPlantilla.get(11));
            etActOxxoDiferencialTempParaAhorro2.setText(dataListPlantilla.get(12));
            etActOxxoTemperaturaEvaporadorTerminarDeshielo.setText(dataListPlantilla.get(13));
            etActOxxoTemperaturaAmbienteTerminarDeshielo.setText(dataListPlantilla.get(14));
            etActOxxoTemperaturaPulldown.setText(dataListPlantilla.get(15));
            etActOxxoAlarmaTemperaturaAlta.setText(dataListPlantilla.get(16));
            etActOxxoalarmaTemperaturaBaja.setText(dataListPlantilla.get(17));
            etActOxxoDiferencialAlarmasTemperatura.setText(dataListPlantilla.get(18));
            etDiferencialDeTemperaturaParaAlarmaDeDeficiencia.setText(dataListPlantilla.get(19));
            etActOxxoVoltajeProteccionMinimo120.setText(dataListPlantilla.get(20));
            etOxxoVoltajeProteccionMaximo120minimo220.setText(dataListPlantilla.get(21));
            etOxxoVoltajeProteccionMaximo220.setText(dataListPlantilla.get(22));
            etOxxoTiempoValidarFallaVoltaje.setText(dataListPlantilla.get(23));
            etOxxoTiempoEntreDeshielos.setText(dataListPlantilla.get(24));
            etOxxoTiempoMaximoDeshielo.setText(dataListPlantilla.get(25));
            etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta.setText(dataListPlantilla.get(26));
            etOxxoTiempoValidacionAlarmaPuertaAbierta.setText(dataListPlantilla.get(27));
            etOxxoTiempoPermanenciaModoNocturno.setText(dataListPlantilla.get(28));
            etTiempoBloqueoDisplayDespuesDeshielo.setText(dataListPlantilla.get(29));
            etTiempoCompresorEncendidoParaMedicionesDeficiencia.setText(dataListPlantilla.get(30));
            etRetardoParaEncenderSegundoCompresor.setText(dataListPlantilla.get(31));
            etOxxoIntensidadFiltroParaSensorAmbienteALaSubida.setText(dataListPlantilla.get(34));
            etOxxoTiempoAdaptivoDeshielo.setText(dataListPlantilla.get(36));//verificar negativo
            //etOxxoProteccionVoltaje.setText(dataListPlantilla.get(28));
            etOxxoHisteresisParaProteccionVoltaje.setText(dataListPlantilla.get(41));
            etOxxoTiempoValidacionSalirFallaVoltaje.setText(dataListPlantilla.get(42));//verificaer
            etOxxoTiempoGoteo.setText(dataListPlantilla.get(43));
            etOxxoTiempoDescansoMinimoCompresor.setText(dataListPlantilla.get(44));
            etOxxotiempoMaximoCompresorEncendido.setText(dataListPlantilla.get(45));
            etOxxoTiempoDescansoCompresorCumpleF4.setText(dataListPlantilla.get(46));
            etOxxoTiempoEncendidoApagadoDeVentilador.setText(dataListPlantilla.get(47));
            etOxxoTiempoPuertaCerradaEntrarModoAhorro1.setText((dataListPlantilla.get(48)));
            etOxxoTiempoPuertaCerradaEntrarModoAhorro2.setText(dataListPlantilla.get(49));
            etOxxoDireccionModbus.setText(dataListPlantilla.get(50));
            etOxxoPassword.setText(dataListPlantilla.get(51));
            String fw = dataListPlantilla.get(59)+dataListPlantilla.get(60);
            fw = fw.replace(".","");
            fw = fw.substring(0,2)+"."+fw.substring(2);
            etOxxoactualFirmwareVersion.setText(fw);
            etOxxoModelTrefp.setText(dataListPlantilla.get(57));
            etOxxoNewPlantillaversion.setText(dataListPlantilla.get(61));
            //etOxxoactualFirmwareVersion.setText(dataListPlantilla.get(36));
            //etOxxoModelTrefp.setText(dataListPlantilla.get(37));

        }

    }
    private void setSwitchesData() {
        if (dataListPlantilla.size() == 1) {
            Toast.makeText(getContext(), "Realizar Hanshake primero", Toast.LENGTH_SHORT).show();
        } else {
            //Escala de temperatura

            if (dataListPlantilla.get(32).equals("0"))
                switchOxxoEscalaTemperatura.setChecked(false);
            else if (dataListPlantilla.get(32).equals("1"))
                switchOxxoEscalaTemperatura.setChecked(true);

            //banderas de configuracion

            //Entrada en modo ahorrador
            /*
            if (dataListPlantilla.get(27).charAt(7) =='0'){
                switchOxxoModoAhorrador.setChecked(false);
            }else{
                switchOxxoModoAhorrador.setChecked(true);
            }


             */
            //Deshielo al arranque
            if (dataListPlantilla.get(33).charAt(6) =='0'){
                switchOxxoOnOffdeshielo.setChecked(true);
            }else{
                switchOxxoOnOffdeshielo.setChecked(false);
            }

            //Pull down
            /*
            if (dataListPlantilla.get(27).charAt(5) =='0'){
                switchOxxoPullDown.setChecked(true);
            }else{
                switchOxxoPullDown.setChecked(false);
            }


             */
            //Switch de puerta
            if (dataListPlantilla.get(33).charAt(4) =='1'){
                switchOxxoSwitchPuerta.setChecked(false);
            }else{
                switchOxxoSwitchPuerta.setChecked(true);
            }

            //set abreviado de parametros
            /*
            if (dataListPlantilla.get(27).charAt(3) =='0'){
                switchOxxoSetAbreviadoParametros.setChecked(false);
            }else{
                switchOxxoSetAbreviadoParametros.setChecked(true);
            }

             */
        }
    }
    private void setSpinnersData() {
        if (dataListPlantilla.size() == 1) {
            Toast.makeText(getContext(), "Realizar Hanshake primero", Toast.LENGTH_SHORT).show();
        } else {

            if (dataListPlantilla.get(35).equals("0"))
                spinnerOxxoModosdeshielo.setSelection(0);
            else if (dataListPlantilla.get(35).equals("1"))
                spinnerOxxoModosdeshielo.setSelection(1);
            else if (dataListPlantilla.get(35).equals("2"))
                spinnerOxxoModosdeshielo.setSelection(2);


            if (dataListPlantilla.get(37).equals("0"))//C5
                spinnerOxxoProteccionVoltaje.setSelection(0);
            else if (dataListPlantilla.get(37).equals("1"))
                spinnerOxxoProteccionVoltaje.setSelection(1);
            else if (dataListPlantilla.get(37).equals("2"))
                spinnerOxxoProteccionVoltaje.setSelection(2);




            if (dataListPlantilla.get(38).equals("1"))//c7
                spinnerOxxoNumeroSensores.setSelection(0);
            else if (dataListPlantilla.get(38).equals("2"))
                spinnerOxxoNumeroSensores.setSelection(1);
            else if (dataListPlantilla.get(38).equals("3"))
                spinnerOxxoNumeroSensores.setSelection(2);




            if (dataListPlantilla.get(39).equals("1"))
                spinnerNivelesTiempoMostrarFallas.setSelection(1);
            else if (dataListPlantilla.get(39).equals("2"))
                spinnerNivelesTiempoMostrarFallas.setSelection(2);
            else if (dataListPlantilla.get(39).equals("3"))
                spinnerNivelesTiempoMostrarFallas.setSelection(3);
            else if (dataListPlantilla.get(39).equals("4"))
                spinnerNivelesTiempoMostrarFallas.setSelection(4);
            else if (dataListPlantilla.get(39).equals("0"))
                spinnerNivelesTiempoMostrarFallas.setSelection(0);

            if (dataListPlantilla.get(40).equals("1"))
                spinnerconfigRelevadores.setSelection(0);
            else if (dataListPlantilla.get(40).equals("2"))
                spinnerconfigRelevadores.setSelection(1);
            else if (dataListPlantilla.get(40).equals("3"))
                spinnerconfigRelevadores.setSelection(2);
            else if (dataListPlantilla.get(40).equals("4"))
                spinnerconfigRelevadores.setSelection(3);
            else if (dataListPlantilla.get(40).equals("5"))
                spinnerconfigRelevadores.setSelection(4);
            else if (dataListPlantilla.get(40).equals("6"))
                spinnerconfigRelevadores.setSelection(5);
            else if (dataListPlantilla.get(40).equals("7"))
                spinnerconfigRelevadores.setSelection(6);
            else if (dataListPlantilla.get(40).equals("8"))
                spinnerconfigRelevadores.setSelection(7);
            else if (dataListPlantilla.get(40).equals("9"))
                spinnerconfigRelevadores.setSelection(8);
            else if (dataListPlantilla.get(40).equals("10"))
                spinnerconfigRelevadores.setSelection(9);


        }
    }

    private void sendDataPlantilla() {
        bluetoothLeService = bluetoothServices.getBluetoothLeService();

        if (bluetoothServices != null && bluetoothLeService != null){
            if (checkEmptydata()) {
                sortFromDataToHexa();
            }else{
                Toast.makeText(getContext(), "Debes llenar todos los datos", Toast.LENGTH_SHORT).show();
            }
        }else
            Toast.makeText(getContext(), "No te has conectados a un BLE", Toast.LENGTH_SHORT).show();


    }

    private void sendDataPlantillaOperador() {
        bluetoothLeService = bluetoothServices.getBluetoothLeService();
        if (bluetoothServices != null && bluetoothLeService != null){
            //if (checkEmptydata()) {
                sortFromDataToHexaOperador();
            //}else{
            //    Toast.makeText(getContext(), "Debes llenar todos los datos", Toast.LENGTH_SHORT).show();
           // }
        }else
            Toast.makeText(getContext(), "No te has conectados a un BLE", Toast.LENGTH_SHORT).show();
    }

    private boolean checkEmptydata() {
        int i = 0;
        do {
            if (ListEditText.get(i).getText().toString().equals("")) {
                ListEditText.get(i).requestFocus();
                final int j=i;
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.smoothScrollTo(0, ListEditText.get(j).getBottom());
                    }
                });
                return false;
            }
            i++;

        } while (i < ListEditText.size());
        return true;
    }


    private void sendPlantillaOperador(){
        new MyAsyncTaskSendNewPlantillaOperador().execute();
    }

    private void sendFwOperador(){
        bluetoothServices.sendCommand("FirmwareOperador");
    }

    private void sendDataPlantillaFwTecnico(){
        bluetoothServices.sendCommand("FirmwareYPlantillaTecnico");
    }

    private void sortFromDataToHexaOperador() {

        /*List<EditText> editTextsList = new ArrayList<>(checkCorrectData());
        if (editTextsList.isEmpty()) {
            if (sp.getString("modelo","").equals(etOxxoModelTrefp.getText().toString())){
                List<String> arrayListInfo = new ArrayList<>(GetHexFromRealDataOxxoDisplay.getData(getList()));
                StringBuilder stringBuilder = new StringBuilder();
                for (String dato:arrayListInfo){
                    stringBuilder.append(dato.toUpperCase());
                }
                Log.d("","stringbuilder"+stringBuilder.toString());

            }else{
                Toast.makeText(getContext(), "El modelo debe ser el mismo que tiene actualmente el equipo", Toast.LENGTH_SHORT).show();
            }

        } else{
            Toast.makeText(getContext(), "Uno o varios parámetros están fuera de los límites válidos", Toast.LENGTH_SHORT).show();
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.smoothScrollTo(0, editTextsList.get(0).getBottom()-130);
                }
            });
        }*/
    }

    private void sortFromDataToHexa() {
        List<EditText> editTextsList = new ArrayList<>(checkCorrectData());
        if (editTextsList.isEmpty()) {
            if (sp.getString("modelo","").equals(etOxxoModelTrefp.getText().toString())){
                List<String> arrayListInfo = new ArrayList<>(GetHexFromRealDataOxxoDisplay.getData(getList()));
                StringBuilder stringBuilder = new StringBuilder();
                for (String dato:arrayListInfo){
                    stringBuilder.append(dato.toUpperCase());
                }
                Log.d("","stringbuilder"+stringBuilder.toString());
                new MyAsyncTaskSendNewPlantilla(stringBuilder).execute();
            }else{
                Toast.makeText(getContext(), "El modelo debe ser el mismo que tiene actualmente el equipo", Toast.LENGTH_SHORT).show();
            }
        } else{
            Toast.makeText(getContext(), "Uno o varios parámetros están fuera de los límites válidos", Toast.LENGTH_SHORT).show();
            scrollView.post(new Runnable() {
                public void run() {
                    scrollView.smoothScrollTo(0, editTextsList.get(0).getBottom()-130);
                }
            });
        }
    }

    public String getOptionSpinner(String option,int selected){
        //Log.d("VALOR","deshielo:"+selected);
        switch (option){
            case "modosDeshielo":{
                if (selected==2)
                    return "00000010";
                else if (selected==0)
                    return "00000000";
                else if (selected==1)
                    return "00000001";
                break;
            }
            case "proteccionVoltaje":{
                if (selected==1)
                    return "00000001";
                else if (selected==0)
                    return "00000000";
                else if (selected==2)
                    return "00000010";
                break;
            }
            case "numeroSensores":{
                if (selected==1)
                    return "00000010";
                else if (selected==0)
                    return "00000001";
                else if (selected==2)
                    return "00000011";
                break;
            }
            case "nivelesFallas":{
                if (selected==2)
                    return "00000011";
                else if (selected==0)
                    return "00000000";
                else if (selected==1)
                    return "00000001";
                else if (selected==3)
                    return "00000111";
                else if (selected==4)
                    return "00001111";
                break;
            }
            case "configRelevadores":{
                if (selected==0)
                    return "00000001";
                else if (selected==1)
                    return "00000010";
                else if (selected==2)
                    return "00000011";
                else if (selected==3)
                    return "00000100";
                else if (selected==4)
                    return "00000101";
                else if (selected==5)
                    return "00000110";
                else if (selected==6)
                    return "00000111";
                else if (selected==7)
                    return "00001000";
                else if (selected==8)
                    return "00001001";
                else if (selected==9)
                    return "00001010";
                break;
            }
        }
        return "";
    }

    public String getOptionSwitch(String option,boolean ischecked){
        if (ischecked)
            return "00100000";
        else
            return "00000000";
    }

    public String getOptionSwitch( boolean deshielo, boolean switchPuerta){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("000");
        stringBuilder.append("0");
        /*if (set)
            stringBuilder.append("1");
        else
            stringBuilder.append("0");
*/
        if (switchPuerta)
            stringBuilder.append("0");
        else
            stringBuilder.append("1");
        stringBuilder.append("0");
/*
        if (pulldown)
            stringBuilder.append("0");
        else
            stringBuilder.append("1");
*/
        if (deshielo)
            stringBuilder.append("0");
        else
            stringBuilder.append("1");
        stringBuilder.append("0");
/*
        if (modoAhorro)
            stringBuilder.append("1");
        else
            stringBuilder.append("0");
*/
        Log.d("banderas",":"+stringBuilder);
        return stringBuilder.toString();
    }

    private void errorEditText(EditText editText,String condition){
    editText.setError(condition);
    }

    private List<EditText> checkCorrectData(){
    List<EditText> editTextsList = new ArrayList<>(getEditTextList());
    //falta agregar función
    double T0,T1, T6,T7;
    double limiteInf, limiteSup;
    T0 = Float.parseFloat( etActOxxoSetpointDiurno.getText().toString());
    T1 = Float.parseFloat(etActOxxoDiferencialDiurno.getText().toString());
    T6 = Float.parseFloat(etActOxxoLimiteInferiorAjusteSetPoint.getText().toString());
    T7 = Float.parseFloat(etActLimiteSuperiorAjusteSetPoint.getText().toString());

    List<EditText> editTextsWrongDataList = new ArrayList<>();
    for (int i =0 ; i<editTextsList.size(); i++){

        if (i==12){//A6 = *rango de (T0+T1+1.0) a (T7+15.0)
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            limiteInf = T0+T1+1.0;
            limiteSup = T7 + 15.0;
            if (numf<limiteInf || numf > limiteSup){
                errorEditText(editTextsList.get(i), "Fuera de límites (entre "+limiteInf+" y "+limiteSup+")");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }

        if (i==13){//A7 = *rango de (T6-15.0) a (T0-1.0)
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            limiteInf = T6-15.0;
            limiteSup = T0 - 1.0;
            if (numf<limiteInf || numf > limiteSup){
                errorEditText(editTextsList.get(i), "Fuera de límites (entre "+limiteInf+" y "+limiteSup+")");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }

        if (i==0 || i==3 ||  i==4 || i==5 || i==6 || i==9 || i == 10 || i==11){//-99 a 99
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            if (numf<-99 || numf > 99){
                errorEditText(editTextsList.get(i), "Fuera de límites");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }

        if (i==7 || i==8 ){//0.0 a 10.0
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            if (numf<-0.0 || numf > 10.0){
                errorEditText(editTextsList.get(i), "Fuera de límites");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }

        if (  i == 16|| i == 17|| i == 18 || i == 19 || i == 20 || i == 21 ||i == 22 || i == 23 || i == 24|| i==25 || i == 26 || i == 27 || i == 40){//00 a 99
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            int num = (int) numf;
            if (num<-0 || num > 99){
                errorEditText(editTextsList.get(i), "Fuera de límites");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }

        if (i==2 ){//-9.9 a 9.9
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            if (numf<-9.9 || numf > 9.9){
                errorEditText(editTextsList.get(i), "Fuera de límites");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }

        if ( i==1 ){//0.5 a 9.9
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            if (numf<-0.5 || numf > 9.9){
                errorEditText(editTextsList.get(i), "Fuera de límites");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }

        if ( i==14 || i==15 ){//0.5 a 10
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            if (numf<-0.5 || numf > 10){
                errorEditText(editTextsList.get(i), "Fuera de límites");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }

        if (  i==30 || i==36){//0.0 a 9.9
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            if (numf<-0.0 || numf > 9.9){
                errorEditText(editTextsList.get(i), "Fuera de límites");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }


        if (i== 31 ||i== 32 || i==33|| i==34 || i==37 || i==38){//0.0 a 25
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            if (numf<0 || numf > 25){
                errorEditText(editTextsList.get(i), "Fuera de límites");
                editTextsWrongDataList.add(editTextsList.get(i));
            }

        }

        if ( i==35){//0.1 a 25
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            if (numf<0.1 || numf > 25){
                errorEditText(editTextsList.get(i), "Fuera de límites");
                editTextsWrongDataList.add(editTextsList.get(i));
            }

        }

        if (i==39){//0.0 a 255
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            if (numf<0 || numf > 255){
                errorEditText(editTextsList.get(i), "Fuera de límites");
                editTextsWrongDataList.add(editTextsList.get(i));
            }

        }

        if (i==28){//0 a 09
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            int num = (int) numf;

            if (num<0 || num > 9){
                errorEditText(editTextsList.get(i), "Fuera de límites");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }
        if (i==29){//0 a 08
            float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
            int num = (int) numf;

            if (num<0 || num > 7){
                errorEditText(editTextsList.get(i), "Fuera de límites");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }

        //plantilla formato correcto
        if (i==43){
            if (editTextsList.get(i).getText().toString().contains(".")){
                if (editTextsList.get(i).getText().toString().length() >= 3){
                    String n = editTextsList.get(i).getText().toString();
                    int index = n.indexOf(".");

                    if (index > 2){
                        errorEditText(editTextsList.get(i), "La versión no puede superar 99.99");
                        editTextsWrongDataList.add(editTextsList.get(i));
                    }else{
                        if (n.length()-index == 0){
                            errorEditText(editTextsList.get(i), "Verifica el formato del dato Firmware");
                            editTextsWrongDataList.add(editTextsList.get(i));
                        }
                    }
                }else {
                    errorEditText(editTextsList.get(i), "Verifica el formato del dato Plantilla");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }else{
                errorEditText(editTextsList.get(i), "Verifica el formato del dato Plantilla,debe llevar decimal");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }

        //firmware correcto
        if (i==42){
            if (editTextsList.get(i).getText().toString().contains(".")){
                if (editTextsList.get(i).getText().toString().length() >= 3){
                    String n = editTextsList.get(i).getText().toString();
                    int index = n.indexOf(".");

                    if (index > 2){
                        errorEditText(editTextsList.get(i), "La versión no puede superar 99.99");
                        editTextsWrongDataList.add(editTextsList.get(i));
                    }else{
                        if (n.length()-index == 0){
                            errorEditText(editTextsList.get(i), "Verifica el formato del dato Firmware");
                            editTextsWrongDataList.add(editTextsList.get(i));
                        }
                    }
                }else {
                    errorEditText(editTextsList.get(i), "Verifica el formato del dato Firmware");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }else{
                errorEditText(editTextsList.get(i), "Verifica el formato del dato Firmware,debe llevar decimal");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }

        //modelo correcto
        if (i==41){
            if (editTextsList.get(i).getText().toString().contains(".")){
                if (editTextsList.get(i).getText().toString().length() >= 3){
                    String n = editTextsList.get(i).getText().toString();
                    int index = n.indexOf(".");

                    if (index > 2){
                        errorEditText(editTextsList.get(i), "La versión no puede superar 99.99");
                        editTextsWrongDataList.add(editTextsList.get(i));
                    }else{
                        if (n.length()-index == 0){
                            errorEditText(editTextsList.get(i), "Verifica el formato del dato Firmware");
                            editTextsWrongDataList.add(editTextsList.get(i));
                        }
                    }
                }else {
                    errorEditText(editTextsList.get(i), "Verifica el formato del dato Firmware");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }else{
                errorEditText(editTextsList.get(i), "El modelo debe coincidir con el equipo");
                editTextsWrongDataList.add(editTextsList.get(i));
            }
        }
    }

    return editTextsWrongDataList;
    }

    private void getPlantillacommand() {
        new MyAsyncTaskSendPlantillaCommand().execute();

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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i!=0){//no es el inicial
            //Convertir la plantilla a datos reales y mostrarlos en pantalla
            List<String> data = new ArrayList<>();
            plantillaSeleccionada = ListPlantillas.get(i);
            plantillaSeleccionadaName = ListPlantillasNombres.get(i);
            dataListPlantilla.clear();//0106000000010480
            Log.v("selecteditem",":"+plantillaSeleccionada.length());
            Log.v("selecteditem",":"+plantillaSeleccionada);
            data = GetRealDataFromHexaOxxoDisplay.convertPlantillaRemoto("1234567891234567"+plantillaSeleccionada);
            dataListPlantilla = GetRealDataFromHexaOxxoDisplay.GetRealDataPLantillaRemoto(data);
            setRealDataRemoto();
            setSwitchesData();
            setSpinnersData();
            //GetRealDataFromHexaOxxoDisplay.cleanSpace()
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //Remoto-BD
    class MyAsyncTaskGetPlantillas extends AsyncTask<Integer, Integer, String> {
        String exc;

        @Override
        protected String doInBackground(Integer... params) {
            try{
                Thread.sleep(200);
                Connection connection;
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection("jdbc:mysql://electronicaeltec.com:3306/trefp_users","moises","9873216543");

                String sql = "SELECT Nombre_arch,HEX(CAST(parametros AS CHAR)) as F,fecha_creacion FROM archiv_config;";
                Statement statement = connection.createStatement();
                ResultSet result = statement.executeQuery(sql);

                int count = 0;
                ListPlantillas.clear();
                ListPlantillasNombres.clear();
                ListPlantillas.add("00");
                ListPlantillasNombres.add("Selecciona una plantilla...");

                while (result.next()){
                    ListPlantillasNombres.add(result.getString("Nombre_arch"));
                    ListPlantillas.add(result.getString("F"));
                    count++;
                }
                if (count > 0 ){
                    return "exito";
                }else{
                    return "falla";
                }


            }catch(SQLException | ClassNotFoundException ex){
                Log.d("SQLException/ClassNotFoundException",":"+ex);
                exc = ex.toString();
                return "SQLException";
            } catch (IllegalAccessException e) {
                Log.d("IllegalAccessException",":"+e);
                exc = e.toString();
                return "IllegalAccessException";
            } catch (java.lang.InstantiationException e) {
                Log.d("InstantiationException",":"+e);
                exc = e.toString();
                return "InstantiationException";
            } catch (InterruptedException e) {
                Log.d("InterruptedException",":"+e);
                exc = e.toString();
                return "InterruptedException";
            }



        }

        @Override
        protected void onPostExecute(String result) {
            progressdialog.dismiss();
            Log.d("result",":"+result);
            switch (result){
                case "exito":{
                    //Log.d("PLANTILASSSnombre",":::"+ListPlantillas);
                    List<String> vacio = new ArrayList<String>();
                    vacio.add("No hay plantillas descargadas");
                    ArrayAdapter<String> adaptervacio = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, vacio);
                    adaptervacio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPlantillas.setAdapter(adaptervacio);

                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, ListPlantillasNombres);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerPlantillas.setAdapter(adapter1);
                    GlobalTools.showInfoPopup("Éxito","Se encontraron "+ListPlantillas.size()+" plantillas",getContext());
                    break;
                }
                case "falla":{
                    List<String> vacio = new ArrayList<String>();
                    vacio.add("No hay plantillas descargadas");
                    ArrayAdapter<String> adaptervacio = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, vacio);
                    adaptervacio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPlantillas.setAdapter(adaptervacio);

                    GlobalTools.showInfoPopup("Falla al descargar","No se pudo consultar las plantillas en este momento, intenta más tarde,.",getContext());
                    break;
                }
                case "SQLException":{
                    Toast.makeText(context, "Error al conectar con BD", Toast.LENGTH_SHORT).show();
                    break;

                }
                case "IllegalAccessException":{
                    Toast.makeText(context, "Error al conectar con BD", Toast.LENGTH_SHORT).show();
                    break;

                }
                case "InstantiationException":{
                    Toast.makeText(context, "Error al conectar con BD", Toast.LENGTH_SHORT).show();
                    break;

                }
                case "InterruptedException":{
                    Toast.makeText(context, "Error al conectar con BD", Toast.LENGTH_SHORT).show();
                    break;

                }
            }

        }

        @Override
        protected void onPreExecute() {
            createProgressDialog("Buscando plantillas...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }
    //comunicación
    class MyAsyncTaskSendPlantillaCommand extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            bluetoothServices.sendCommand("readParam");

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<String> listData = new ArrayList<String>() ;
            List<String> FinalListData = new ArrayList<String>() ;
            if (bluetoothLeService == null){
                dataListPlantilla.clear();
                Toast.makeText(getContext(), "No te has conectados a un BLE", Toast.LENGTH_SHORT).show();
                return "";
            }else {
                listData = bluetoothLeService.getDataFromBroadcastUpdate();
                FinalListData = GetRealDataFromHexaOxxoDisplay.convert(listData,"Lectura de parámetros de operación");
                dataListPlantilla = GetRealDataFromHexaOxxoDisplay.GetRealData(FinalListData,"Lectura de parámetros de operación");
                return "resp";
            }

        }

        @Override
        protected void onPostExecute(String result) {

            try {
                Thread.sleep(800);
                if (progressdialog != null)progressdialog.dismiss();
                progressdialog=null;
                if (dataListPlantilla.isEmpty()){
                    Toast.makeText(getContext(), "Comando de obtener plantilla fallido, intenta de nuevo o reintenta conectarte a BLE", Toast.LENGTH_LONG).show();
                }else {
                    setRealData();
                    setSwitchesData();
                    setSpinnersData();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onPreExecute() {
            createProgressDialog("Obteniendo plantilla actual...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskSendNewPlantilla extends AsyncTask<Integer, Integer, String>  {
        String comando;
        MyAsyncTaskSendNewPlantilla(StringBuilder sb){
            this.comando = sb.toString();
        }
        @Override
        protected String doInBackground(Integer... params) {
            bluetoothServices.sendCommand("writeRealParam",comando);
            dataListPlantilla.clear();
            try {
                Thread.sleep(800);
                if (bluetoothLeService != null){
                    dataListPlantilla = bluetoothLeService.getDataFromBroadcastUpdate();
                    if (dataListPlantilla.isEmpty()){
                        return "empty";
                    }else {
                        if (dataListPlantilla.get(0).equals("F1 3D ")){
                            return "ok";
                        }else{
                            return "notok";
                        }
                    }
                }else {
                    return "noconnected";
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "exception";
            }


        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(300);
                if (progressdialog != null)progressdialog.dismiss();
                progressdialog=null;
                if (result.equals("empty"))
                    Toast.makeText(getContext(), "Acabas de enviar plantilla, intenta reconectarte a BLE", Toast.LENGTH_SHORT).show();
                if (result.equals("ok")){
                    Toast.makeText(getContext(), "Actualización de plantilla: Correcta", Toast.LENGTH_SHORT).show();
                    //listenermain.printExcel(getListToExcel(),"oxxo");
                }if (result.equals("noconnected")){
                    Toast.makeText(getContext(), "No te has conectados a un BLE", Toast.LENGTH_SHORT).show();
                }if (result.equals("exception")){
                    Toast.makeText(getContext(), "ha ocurrido un error inesperado", Toast.LENGTH_SHORT).show();
                }if (result.equals("notok"))
                    Toast.makeText(getContext(), "Actualización de plantilla: Incorrecta", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            createProgressDialog("Acualizando a nueva plantilla...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskSendNewPlantillaOperador extends AsyncTask<Integer, Integer, String>  {

        MyAsyncTaskSendNewPlantillaOperador(){

        }
        @Override
        protected String doInBackground(Integer... params) {
            String plantilla = "4050AAFFE9000F0000000000000000FFE200320000001EFF6A000000000005000A0000012C012C000000000078000000B4FF9C000A001400000000FFE9000F00B4FF9C665A28280F04280505000A0F000000000000020600000100020F010000000000002A030B1EF00A0A0000000000000A1400DE490000000000400001070012CC";
            bluetoothServices.sendCommand("writeRealParam",plantilla+ GetHexFromRealDataImbera.calculateChacksumString(plantilla));
            dataListPlantilla.clear();
            try {
                Thread.sleep(800);
                if (bluetoothLeService != null){
                    dataListPlantilla = bluetoothLeService.getDataFromBroadcastUpdate();
                    if (dataListPlantilla.isEmpty()){
                        return "empty";
                    }else {
                        if (dataListPlantilla.get(0).equals("F1 3D ")){
                            return "ok";
                        }else{
                            return "notok";
                        }
                    }
                }else {
                    return "noconnected";
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "exception";
            }


        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Thread.sleep(300);
                if (progressdialog != null)progressdialog.dismiss();
                progressdialog=null;
                if (result.equals("empty"))
                    Toast.makeText(getContext(), "Acabas de enviar plantilla, intenta reconectarte a BLE", Toast.LENGTH_SHORT).show();
                if (result.equals("ok")){
                    Toast.makeText(getContext(), "Actualización de plantilla: Correcta", Toast.LENGTH_SHORT).show();
                }if (result.equals("noconnected")){
                    Toast.makeText(getContext(), "No te has conectados a un BLE", Toast.LENGTH_SHORT).show();
                }if (result.equals("exception")){
                    Toast.makeText(getContext(), "ha ocurrido un error inesperado", Toast.LENGTH_SHORT).show();
                }if (result.equals("notok"))
                    Toast.makeText(getContext(), "Actualización de plantilla: Incorrecta", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            createProgressDialog("Acualizando a equipo CEO...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    class MyAsyncTaskGetActualStatusTotalCrudo extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            obtenerInfoTotalCrudo();
            return "resp";
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressdialog != null)progressdialog.dismiss();
            List<String> fin = new ArrayList<String>();
            fin.add(FinalListDataHandshake.get(0));
            fin.add(FinalListDataRealState.get(0));
            fin.add(FinalListDataPlantilla.get(0));

            listenermain.printExcel(fin,"crudototalOxxo");
            progressdialog=null;
        }

        @Override
        protected void onPreExecute() {
            FinalListDataHandshake.clear();
            FinalListDataRealState.clear();
            FinalListDataPlantilla.clear();
            createProgressDialog("Obteniendo estado actual...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private void obtenerInfoTotalCrudo(){
        bluetoothLeService = bluetoothServices.getBluetoothLeService();
        try {
            bluetoothServices.sendCommand("handshake","4021");
            Thread.sleep(250);
            FinalListDataHandshake.add(bluetoothLeService.getDataFromBroadcastUpdateString());

            bluetoothServices.sendCommand("realState","4053");
            Thread.sleep(250);
            FinalListDataRealState.add(bluetoothLeService.getDataFromBroadcastUpdateString());

            bluetoothServices.sendCommand("readParam","4051");
            Thread.sleep(250);
            FinalListDataPlantilla.add(bluetoothLeService.getDataFromBroadcastUpdateString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    //utilidad
    /*public List<String> getOriginalList(){
        List<String> arrayListInfo = new ArrayList<>();
        arrayListInfo.add(etActCompresorTiempoMinimoEncendido.getText().toString());
        arrayListInfo.add(etActCompresorTiempoMinimoApagado.getText().toString());
        arrayListInfo.add(etActCompresorRetrasoPrimerEncendido.getText().toString());

        arrayListInfo.add(etActTermostatoTemperaturaDeCorteFrio.getText().toString());
        arrayListInfo.add(etActTermostatoDiferencialFrio.getText().toString());
        arrayListInfo.add(etActTermostatoLimiteDeTemperaturaBaja.getText().toString());


        if (switchFuncionesControl.isChecked())
            arrayListInfo.add("Puerta habilitada");
        else
            arrayListInfo.add("Puerta deshabilitada");
        arrayListInfo.add(etActPuertaCortesCompresorPermitirModosAhorro.getText().toString());
        arrayListInfo.add(etActPuertaTemperaturaAmbientePermitirModosAhorro.getText().toString());
        arrayListInfo.add(etActPuertaDiferencialModoAhorro1.getText().toString());
        arrayListInfo.add(etActPuertaDiferencialModoAhorro2.getText().toString());
        arrayListInfo.add(etActPuertaTiempoPuertaCerradaModoAhorro1.getText().toString());
        arrayListInfo.add(etActPuertaTiempoPuertaCerradaModoAhorro2.getText().toString());
        arrayListInfo.add(etActPuertaTiempoParaRegistrarPuertaAbierta.getText().toString());
        arrayListInfo.add(etActPuertaTiempoParaValidarFallaPuertaAbierta.getText().toString());

        arrayListInfo.add(spinnerModosDeshielo.getSelectedItem().toString());
        arrayListInfo.add(spinnerFuncionesDeshielo.getSelectedItem().toString());
        arrayListInfo.add(etDeshieloTiempoMinimoDeDeshielo.getText().toString());
        arrayListInfo.add(etDeshieloTiempoMaximoDeDeshielo.getText().toString());
        arrayListInfo.add(etDeshieloTiempoMinimoParaInicioDeDeshielo.getText().toString());
        arrayListInfo.add(etDeshieloTiempoMaximoParaInicioDeDeshielo.getText().toString());
        arrayListInfo.add(etDeshieloTemperaturaParaFinalizarDeshielo.getText().toString());
        arrayListInfo.add(etDeshieloTemperaturaDeReinicio.getText().toString());
        arrayListInfo.add(etDeshieloTemperaturaParaIniciarDeshielo.getText().toString());
        arrayListInfo.add(etDeshieloTiempoDeGoteoParaCompresor.getText().toString());
        arrayListInfo.add(etDeshieloTiempoDeGoteoParaVentilador.getText().toString());

        if (switchFuncionesVentiladorB0.isChecked())
            arrayListInfo.add("Apagar ventilador con compresor apagado: Habilitado");
        else
            arrayListInfo.add("Apagar ventilador con compresor apagado: Deshabilitado");

        if (switchFuncionesVentiladorB0.isChecked())
            arrayListInfo.add("Apagar ventilador con puerta abierta: Habilitado");
        else
            arrayListInfo.add("Apagar ventilador con puerta abierta: Deshabilitado");

        arrayListInfo.add(etVentiladorRetrasoPrimerEncendido.getText().toString());
        arrayListInfo.add(etVentiladorRetardoParaComenzarCiclo.getText().toString());
        arrayListInfo.add(etVentiladorTiempoVentiladorEncendido.getText().toString());
        arrayListInfo.add(etVentiladorTiempoVentiladorApagado.getText().toString());
        arrayListInfo.add(etVentiladorRetardoEncendidoAlCerrarPuerta.getText().toString());

        arrayListInfo.add(spinnerProteccionVoltaje.getSelectedItem().toString());
        arrayListInfo.add(etProteccionVoltajeVoltajeProteccionMinimo120.getText().toString());
        arrayListInfo.add(etProteccionVoltajeVoltajeProteccionMaximo120minimo220.getText().toString());
        arrayListInfo.add(etProteccionVoltajeVoltajeProteccionMaximo220.getText().toString());
        arrayListInfo.add(etProteccionVoltajeTiempoValidacionProteccionVoltaje.getText().toString());
        arrayListInfo.add(etProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt.getText().toString());
        arrayListInfo.add(etProteccionVoltajeHisteresis.getText().toString());
        arrayListInfo.add(etLoggerTiempoParaLogger.getText().toString());
        arrayListInfo.add(etactualFirmwareVersion.getText().toString());
        arrayListInfo.add(etModeloTrefp.getText().toString());
        arrayListInfo.add(etPlantillaversion.getText().toString());
        return arrayListInfo;
    }*/
    private List<EditText> getEditTextList(){
        List<EditText> editTextsList = new ArrayList<>();
        editTextsList.add(etActOxxoSetpointDiurno);
        editTextsList.add(etActOxxoDiferencialDiurno);
        editTextsList.add(etActOxxoOffsetDiurno);
        editTextsList.add(etActOxxoLimiteInferiorAjusteSetPoint);
        editTextsList.add(etActLimiteSuperiorAjusteSetPoint);
        editTextsList.add(etTemperaturaDeReinicio);
        editTextsList.add(etTemperaturaParaIniciarDeshielo);
        editTextsList.add(etActOxxoDiferencialTempParaAhorro1);
        editTextsList.add(etActOxxoDiferencialTempParaAhorro2);
        editTextsList.add(etActOxxoTemperaturaEvaporadorTerminarDeshielo);
        editTextsList.add(etActOxxoTemperaturaAmbienteTerminarDeshielo);
        editTextsList.add(etActOxxoTemperaturaPulldown);
        editTextsList.add(etActOxxoAlarmaTemperaturaAlta);
        editTextsList.add(etActOxxoalarmaTemperaturaBaja);
        editTextsList.add(etActOxxoDiferencialAlarmasTemperatura);
        editTextsList.add(etDiferencialDeTemperaturaParaAlarmaDeDeficiencia);
        editTextsList.add(etActOxxoVoltajeProteccionMinimo120);
        editTextsList.add(etOxxoVoltajeProteccionMaximo120minimo220);
        editTextsList.add(etOxxoVoltajeProteccionMaximo220);
        editTextsList.add(etOxxoTiempoValidarFallaVoltaje);
        editTextsList.add(etOxxoTiempoEntreDeshielos);
        editTextsList.add(etOxxoTiempoMaximoDeshielo);
        editTextsList.add(etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta);
        editTextsList.add(etOxxoTiempoValidacionAlarmaPuertaAbierta);
        editTextsList.add(etOxxoTiempoPermanenciaModoNocturno);
        editTextsList.add(etTiempoBloqueoDisplayDespuesDeshielo);
        editTextsList.add(etTiempoCompresorEncendidoParaMedicionesDeficiencia);
        editTextsList.add(etRetardoParaEncenderSegundoCompresor);
        editTextsList.add(etOxxoIntensidadFiltroParaSensorAmbienteALaSubida);
        editTextsList.add(etOxxoTiempoAdaptivoDeshielo);
        editTextsList.add(etOxxoHisteresisParaProteccionVoltaje);
        editTextsList.add(etOxxoTiempoValidacionSalirFallaVoltaje);
        editTextsList.add(etOxxoTiempoGoteo);
        editTextsList.add(etOxxoTiempoDescansoMinimoCompresor);
        editTextsList.add(etOxxotiempoMaximoCompresorEncendido);
        editTextsList.add(etOxxoTiempoDescansoCompresorCumpleF4);
        editTextsList.add(etOxxoTiempoEncendidoApagadoDeVentilador);
        editTextsList.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro1);
        editTextsList.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro2);
        editTextsList.add(etOxxoDireccionModbus);
        editTextsList.add(etOxxoPassword);
        editTextsList.add(etOxxoNewPlantillaversion);
        editTextsList.add(etOxxoactualFirmwareVersion);
        editTextsList.add(etOxxoModelTrefp);
        return editTextsList;
    }
    public List<String> getList(){
        List<String> arrayListInfo = new ArrayList<>();
        arrayListInfo.add(etActOxxoSetpointDiurno.getText().toString());
        arrayListInfo.add(etActOxxoDiferencialDiurno.getText().toString());
        arrayListInfo.add(etActOxxoOffsetDiurno.getText().toString());
        arrayListInfo.add(etActOxxoLimiteInferiorAjusteSetPoint.getText().toString());
        arrayListInfo.add(etActLimiteSuperiorAjusteSetPoint.getText().toString());
        arrayListInfo.add(etTemperaturaDeReinicio.getText().toString());
        arrayListInfo.add(etTemperaturaParaIniciarDeshielo.getText().toString());
        arrayListInfo.add(etActOxxoDiferencialTempParaAhorro1.getText().toString());
        arrayListInfo.add(etActOxxoDiferencialTempParaAhorro2.getText().toString());
        arrayListInfo.add(etActOxxoTemperaturaEvaporadorTerminarDeshielo.getText().toString());
        arrayListInfo.add(etActOxxoTemperaturaAmbienteTerminarDeshielo.getText().toString());
        arrayListInfo.add(etActOxxoTemperaturaPulldown.getText().toString());
        arrayListInfo.add(etActOxxoAlarmaTemperaturaAlta.getText().toString());
        arrayListInfo.add(etActOxxoalarmaTemperaturaBaja.getText().toString());
        arrayListInfo.add(etActOxxoDiferencialAlarmasTemperatura.getText().toString());
        arrayListInfo.add(etDiferencialDeTemperaturaParaAlarmaDeDeficiencia.getText().toString());
        arrayListInfo.add(etActOxxoVoltajeProteccionMinimo120.getText().toString());
        arrayListInfo.add(etOxxoVoltajeProteccionMaximo120minimo220.getText().toString());
        arrayListInfo.add(etOxxoVoltajeProteccionMaximo220.getText().toString());
        arrayListInfo.add(etOxxoTiempoValidarFallaVoltaje.getText().toString());
        arrayListInfo.add(etOxxoTiempoEntreDeshielos.getText().toString());
        arrayListInfo.add(etOxxoTiempoMaximoDeshielo.getText().toString());
        arrayListInfo.add(etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta.getText().toString());
        arrayListInfo.add(etOxxoTiempoValidacionAlarmaPuertaAbierta.getText().toString());
        arrayListInfo.add(etOxxoTiempoPermanenciaModoNocturno.getText().toString());
        arrayListInfo.add(etTiempoBloqueoDisplayDespuesDeshielo.getText().toString());
        arrayListInfo.add(etTiempoCompresorEncendidoParaMedicionesDeficiencia.getText().toString());
        arrayListInfo.add(etRetardoParaEncenderSegundoCompresor.getText().toString());
        arrayListInfo.add(getOptionSwitch("escalaTemp",switchOxxoEscalaTemperatura.isChecked()));
        arrayListInfo.add(getOptionSwitch( switchOxxoOnOffdeshielo.isChecked(),  switchOxxoSwitchPuerta.isChecked()));
        arrayListInfo.add(etOxxoIntensidadFiltroParaSensorAmbienteALaSubida.getText().toString());
        arrayListInfo.add(getOptionSpinner("modosDeshielo",spinnerOxxoModosdeshielo.getSelectedItemPosition()));
        arrayListInfo.add(etOxxoTiempoAdaptivoDeshielo.getText().toString());
        arrayListInfo.add(getOptionSpinner("proteccionVoltaje",spinnerOxxoProteccionVoltaje.getSelectedItemPosition()));
        arrayListInfo.add(getOptionSpinner("numeroSensores",spinnerOxxoNumeroSensores.getSelectedItemPosition()));
        arrayListInfo.add(getOptionSpinner("nivelesFallas",spinnerNivelesTiempoMostrarFallas.getSelectedItemPosition()));
        arrayListInfo.add(getOptionSpinner("configRelevadores",spinnerconfigRelevadores.getSelectedItemPosition()));
        arrayListInfo.add(etOxxoHisteresisParaProteccionVoltaje.getText().toString());
        arrayListInfo.add(etOxxoTiempoValidacionSalirFallaVoltaje.getText().toString());
        arrayListInfo.add(etOxxoTiempoGoteo.getText().toString());
        arrayListInfo.add(etOxxoTiempoDescansoMinimoCompresor.getText().toString());
        arrayListInfo.add(etOxxotiempoMaximoCompresorEncendido.getText().toString());
        arrayListInfo.add(etOxxoTiempoDescansoCompresorCumpleF4.getText().toString());
        arrayListInfo.add(etOxxoTiempoEncendidoApagadoDeVentilador.getText().toString());
        arrayListInfo.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro1.getText().toString());
        arrayListInfo.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro2.getText().toString());
        arrayListInfo.add(etOxxoDireccionModbus.getText().toString());
        arrayListInfo.add(etOxxoPassword.getText().toString());
        arrayListInfo.add(etOxxoModelTrefp.getText().toString());
        arrayListInfo.add(etOxxoactualFirmwareVersion.getText().toString());
        arrayListInfo.add(etOxxoNewPlantillaversion.getText().toString());


        return arrayListInfo;
    }
    public List<String> getListToExcel(){
        List<String> arrayListInfo = new ArrayList<>();
        arrayListInfo.add(etActOxxoSetpointDiurno.getText().toString());
        arrayListInfo.add(etActOxxoDiferencialDiurno.getText().toString());
        arrayListInfo.add(etActOxxoOffsetDiurno.getText().toString());
        arrayListInfo.add(etActOxxoLimiteInferiorAjusteSetPoint.getText().toString());
        arrayListInfo.add(etActLimiteSuperiorAjusteSetPoint.getText().toString());
        arrayListInfo.add(etTemperaturaDeReinicio.getText().toString());
        arrayListInfo.add(etTemperaturaParaIniciarDeshielo.getText().toString());
        arrayListInfo.add(etActOxxoDiferencialTempParaAhorro1.getText().toString());
        arrayListInfo.add(etActOxxoDiferencialTempParaAhorro2.getText().toString());
        arrayListInfo.add(etActOxxoTemperaturaEvaporadorTerminarDeshielo.getText().toString());
        arrayListInfo.add(etActOxxoTemperaturaAmbienteTerminarDeshielo.getText().toString());
        arrayListInfo.add(etActOxxoTemperaturaPulldown.getText().toString());
        arrayListInfo.add(etActOxxoAlarmaTemperaturaAlta.getText().toString());
        arrayListInfo.add(etActOxxoalarmaTemperaturaBaja.getText().toString());
        arrayListInfo.add(etActOxxoDiferencialAlarmasTemperatura.getText().toString());
        arrayListInfo.add(etDiferencialDeTemperaturaParaAlarmaDeDeficiencia.getText().toString());
        arrayListInfo.add(etActOxxoVoltajeProteccionMinimo120.getText().toString());
        arrayListInfo.add(etOxxoVoltajeProteccionMaximo120minimo220.getText().toString());
        arrayListInfo.add(etOxxoVoltajeProteccionMaximo220.getText().toString());
        arrayListInfo.add(etOxxoTiempoValidarFallaVoltaje.getText().toString());
        arrayListInfo.add(etOxxoTiempoEntreDeshielos.getText().toString());
        arrayListInfo.add(etOxxoTiempoMaximoDeshielo.getText().toString());
        arrayListInfo.add(etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta.getText().toString());
        arrayListInfo.add(etOxxoTiempoValidacionAlarmaPuertaAbierta.getText().toString());
        arrayListInfo.add(etOxxoTiempoPermanenciaModoNocturno.getText().toString());
        arrayListInfo.add(etTiempoBloqueoDisplayDespuesDeshielo.getText().toString());
        arrayListInfo.add(etTiempoCompresorEncendidoParaMedicionesDeficiencia.getText().toString());
        arrayListInfo.add(etRetardoParaEncenderSegundoCompresor.getText().toString());

        if (switchOxxoEscalaTemperatura.isChecked()){
            arrayListInfo.add("Fahrenheit");
        }else
            arrayListInfo.add("Celcius");

        StringBuilder stringBuilder = new StringBuilder();
        /*if (switchOxxoModoAhorrador.isChecked())
            stringBuilder.append("Entrada a Modo Ahorrador: Por botón y puerta - ");
        else
            stringBuilder.append("Entrada a Modo Ahorrador: Sólo por botón - ");
*/
        if (switchOxxoOnOffdeshielo.isChecked())
            stringBuilder.append("Deshielo de arranque: Si - ");
        else
            stringBuilder.append("Deshielo de arranque: No - ");
/*
        if (switchOxxoPullDown.isChecked())
            stringBuilder.append("Pull Down: Habilitado - ");
        else
            stringBuilder.append("Pull Down: Deshabilitado - ");
*/
        if (switchOxxoSwitchPuerta.isChecked())
            stringBuilder.append("Switch de puerta: Normalmente abierto");
        else
            stringBuilder.append("Switch de puerta: Normalmente cerrado");
/*
        if (switchOxxoSetAbreviadoParametros.isChecked())
            stringBuilder.append("Set abreviado de parámetros: Visible");
        else
            stringBuilder.append("Set abreviado de parámetros: No visible");
*/
        arrayListInfo.add(stringBuilder.toString());
        arrayListInfo.add(etOxxoIntensidadFiltroParaSensorAmbienteALaSubida.getText().toString());
        //arrayListIno.add(getOptionSpinner("modosDeshielo",spinnerOxxoModosdeshielo.getSelectedItemPosition()));
        if (spinnerOxxoModosdeshielo.getSelectedItemPosition() == 0)
            arrayListInfo.add("Por ventilador");
        else if (spinnerOxxoModosdeshielo.getSelectedItemPosition() == 1)
            arrayListInfo.add("Por gas caliente");
        else if (spinnerOxxoModosdeshielo.getSelectedItemPosition() == 2)
            arrayListInfo.add("Por resistencia eléctica");

        arrayListInfo.add(etOxxoTiempoAdaptivoDeshielo.getText().toString());

        if (spinnerOxxoProteccionVoltaje.getSelectedItemPosition() == 0)
            arrayListInfo.add("Sin protección de voltaje");
        else if (spinnerOxxoProteccionVoltaje.getSelectedItemPosition() == 1)
            arrayListInfo.add("Protección a 120 Volts");
        else if (spinnerOxxoProteccionVoltaje.getSelectedItemPosition() == 2)
            arrayListInfo.add("Protección a 220 Volts");

        if (spinnerOxxoNumeroSensores.getSelectedItemPosition() == 0)
            arrayListInfo.add("1 sensor");
        else if (spinnerOxxoNumeroSensores.getSelectedItemPosition() == 1)
            arrayListInfo.add("2 sensores");
        else if (spinnerOxxoNumeroSensores.getSelectedItemPosition() == 2)
            arrayListInfo.add("3 sensores");

        if (spinnerNivelesTiempoMostrarFallas.getSelectedItemPosition() == 0)
            arrayListInfo.add("Nivel 0");
        if (spinnerNivelesTiempoMostrarFallas.getSelectedItemPosition() == 1)
            arrayListInfo.add("Nivel 1");
        else if (spinnerNivelesTiempoMostrarFallas.getSelectedItemPosition() == 2)
            arrayListInfo.add("Nivel 2");
        else if (spinnerNivelesTiempoMostrarFallas.getSelectedItemPosition() == 3)
            arrayListInfo.add("Nivel 3");
        else if (spinnerNivelesTiempoMostrarFallas.getSelectedItemPosition() == 4)
            arrayListInfo.add("Nivel 4");

        if (spinnerconfigRelevadores.getSelectedItemPosition() == 0)
            arrayListInfo.add("1 relevador");
        else if (spinnerconfigRelevadores.getSelectedItemPosition() == 1)
            arrayListInfo.add("2 relevadores");
        else if (spinnerconfigRelevadores.getSelectedItemPosition() == 2)
            arrayListInfo.add("3 relevadores");
        if (spinnerconfigRelevadores.getSelectedItemPosition() == 3)
            arrayListInfo.add("4 relevadores");
        else if (spinnerconfigRelevadores.getSelectedItemPosition() == 4)
            arrayListInfo.add("5 relevadores");
        else if (spinnerconfigRelevadores.getSelectedItemPosition() == 5)
            arrayListInfo.add("6 relevadores");
        if (spinnerconfigRelevadores.getSelectedItemPosition() == 6)
            arrayListInfo.add("7 relevadores");
        else if (spinnerconfigRelevadores.getSelectedItemPosition() == 7)
            arrayListInfo.add("8 relevadores");
        else if (spinnerconfigRelevadores.getSelectedItemPosition() == 8)
            arrayListInfo.add("9 relevadores");
        else if (spinnerconfigRelevadores.getSelectedItemPosition() == 9)
            arrayListInfo.add("10 relevadores");


        arrayListInfo.add(etOxxoHisteresisParaProteccionVoltaje.getText().toString());
        arrayListInfo.add(etOxxoTiempoValidacionSalirFallaVoltaje.getText().toString());
        arrayListInfo.add(etOxxoTiempoGoteo.getText().toString());
        arrayListInfo.add(etOxxoTiempoDescansoMinimoCompresor.getText().toString());
        arrayListInfo.add(etOxxotiempoMaximoCompresorEncendido.getText().toString());
        arrayListInfo.add(etOxxoTiempoDescansoCompresorCumpleF4.getText().toString());
        arrayListInfo.add(etOxxoTiempoEncendidoApagadoDeVentilador.getText().toString());
        arrayListInfo.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro1.getText().toString());
        arrayListInfo.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro2.getText().toString());
        arrayListInfo.add(etOxxoDireccionModbus.getText().toString());
        arrayListInfo.add(etOxxoPassword.getText().toString());
        arrayListInfo.add(etOxxoModelTrefp.getText().toString());
        arrayListInfo.add(etOxxoactualFirmwareVersion.getText().toString());
        arrayListInfo.add(etOxxoNewPlantillaversion.getText().toString());


        return arrayListInfo;
    }

    //Listeners
    public void setListener(listener callback) {
        this.listenermain = callback;
    }

    public interface listener {
        public void printExcel(List<String> data,String deviceName);
    }

}
