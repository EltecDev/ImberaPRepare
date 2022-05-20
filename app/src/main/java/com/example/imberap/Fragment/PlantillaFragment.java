package com.example.imberap.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.example.imberap.utility.GetHexFromRealDataImbera;
import com.example.imberap.utility.GetRealDataFromHexaImbera;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;


public class PlantillaFragment extends Fragment {
    String TAG="PlantillaFragment";
    listener listenermain;

    List<TextView> tvList = new ArrayList<TextView>();
    List<EditText> tvListEditText = new ArrayList<EditText>();

    ScrollView scrollView;
    SwitchMaterial switchFuncionesControl, switchFuncionesVentiladorB0, switchFuncionesVentiladorB1;
    TextView tvActCompresorTiempoMinimoEncendido, tvActCompresorTiempoMinimoApagado, tvActCompresorRetrasoPrimerEncendido, tvActTermostatoTemperaturaDeCorteFrio,
            tvActTermostatoDiferencialFrio, tvActTermostatoLimiteDeTemperaturaBaja, tvActPuertaHabilitar, tvActPuertaCortesCompresorPermitirModosAhorro, tvActPuertaTemperaturaAmbientePermitirModosAhorro,
            tvActPuertaDiferencialModoAhorro1, tvActPuertaDiferencialModoAhorro2, tvActPuertaTiempoPuertaCerradaModoAhorro1, tvActPuertaTiempoPuertaCerradaModoAhorro2, tvActPuertaTiempoParaRegistrarPuertaAbierta,tvActPuertaTiempoParaValidarFallaPuertaAbierta,
            tvDeshieloModoDeDeshielo, tvDeshieloDeshieloPorVentilador, tvDeshieloTiempoMinimoDeDeshielo, tvDeshieloTiempoMaximoDeDeshielo, tvDeshieloTiempoMinimoParaInicioDeDeshielo, tvDeshieloTiempoMaximoParaInicioDeDeshielo,
            tvDeshieloTemperaturaParaFinalizarDeshielo, tvDeshieloTemperaturaDeReinicio, tvDeshieloTemperaturaParaIniciarDeshielo, tvDeshieloTiempoDeGoteoParaCompresor,
            tvDeshieloTiempoDeGoteoParaVentilador, tvVentiladorFuncionesVentiladorB0, tvVentiladorFuncionesVentiladorB1, tvVentiladorRetrasoPrimerEncendido,
            tvVentiladorRetardoParaComenzarCiclo, tvVentiladorTiempoVentiladorEncendido, tvVentiladorTiempoVentiladorApagado, tvVentiladorRetardoEncendidoAlCerrarPuerta, tvProteccionVoltajeProteccionVoltaje,
            tvProteccionVoltajeVoltajeProteccionMinimo120, tvProteccionVoltajeVoltajeProteccionMaximo120minimo220, tvProteccionVoltajeVoltajeProteccionMaximo220, tvProteccionVoltajeTiempoValidacionProteccionVoltaje,
            tvProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt, tvProteccionVoltajeHisteresis, tvLoggerTiempoParaLogger, tvPlantillaVersion,tvactualFirmwareVersion, tvactualPlantilla, tvModeloTrefp;

    EditText etActCompresorTiempoMinimoEncendido, etActCompresorTiempoMinimoApagado, etActCompresorRetrasoPrimerEncendido, etActTermostatoTemperaturaDeCorteFrio,
            etActTermostatoDiferencialFrio, etActTermostatoLimiteDeTemperaturaBaja,  etActPuertaCortesCompresorPermitirModosAhorro, etActPuertaTemperaturaAmbientePermitirModosAhorro,
            etActPuertaDiferencialModoAhorro1, etActPuertaDiferencialModoAhorro2, etActPuertaTiempoPuertaCerradaModoAhorro1, etActPuertaTiempoPuertaCerradaModoAhorro2, etActPuertaTiempoParaRegistrarPuertaAbierta,etActPuertaTiempoParaValidarFallaPuertaAbierta,
             etDeshieloTiempoMinimoDeDeshielo, etDeshieloTiempoMaximoDeDeshielo, etDeshieloTiempoMinimoParaInicioDeDeshielo, etDeshieloTiempoMaximoParaInicioDeDeshielo,
            etDeshieloTemperaturaParaFinalizarDeshielo, etDeshieloTemperaturaDeReinicio, etDeshieloTemperaturaParaIniciarDeshielo, etDeshieloTiempoDeGoteoParaCompresor,
            etDeshieloTiempoDeGoteoParaVentilador, etVentiladorRetrasoPrimerEncendido,
            etVentiladorRetardoParaComenzarCiclo, etVentiladorTiempoVentiladorEncendido, etVentiladorTiempoVentiladorApagado, etVentiladorRetardoEncendidoAlCerrarPuerta,
            etProteccionVoltajeVoltajeProteccionMinimo120, etProteccionVoltajeVoltajeProteccionMaximo120minimo220, etProteccionVoltajeVoltajeProteccionMaximo220, etProteccionVoltajeTiempoValidacionProteccionVoltaje,
            etProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt, etProteccionVoltajeHisteresis, etLoggerTiempoParaLogger, etPlantillaversion,etactualFirmwareVersion, etModeloTrefp;


    Spinner  spinnerModosDeshielo, spinnerFuncionesDeshielo,  spinnerProteccionVoltaje;
    BluetoothServices bluetoothServices;
    BluetoothLeService bluetoothLeService;

    androidx.appcompat.app.AlertDialog progressdialog = null;
    View dialogViewProgressBar;

    List<String> dataListPlantilla = new ArrayList<String>();

    public PlantillaFragment(BluetoothServices bluetoothServices) {
        this.bluetoothServices = bluetoothServices;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plantilla_fragment, container, false);
        init(view);

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


        return view;
    }

    private void init(View v) {
        bluetoothLeService = bluetoothServices.getBluetoothLeService();
        //sharedViewModel = new ViewModelProvider(requireActivity()).get(StatusViewModel.class);
        initCampos(v);
    }

    private void initCampos(View view) {
        scrollView = view.findViewById(R.id.scrollPlantilla);

        tvActCompresorTiempoMinimoEncendido = view.findViewById(R.id.tvActCompresorTiempoMinimoEncendido);
        tvActCompresorTiempoMinimoApagado = view.findViewById(R.id.tvActCompresorTiempoMinimoApagado);
        tvActCompresorRetrasoPrimerEncendido = view.findViewById(R.id.tvActCompresorRetrasoPrimerEncendido);
        tvActTermostatoTemperaturaDeCorteFrio = view.findViewById(R.id.tvActTermostatoTemperaturaDeCorteFrio);
        tvActTermostatoDiferencialFrio = view.findViewById(R.id.tvActTermostatoDiferencialFrio);
        tvActTermostatoLimiteDeTemperaturaBaja = view.findViewById(R.id.tvActTermostatoLimiteDeTemperaturaBaja);
        tvActPuertaHabilitar = view.findViewById(R.id.tvActPuertaHabilitar);
        tvActPuertaCortesCompresorPermitirModosAhorro = view.findViewById(R.id.tvActPuertaCortesCompresorPermitirModosAhorro);
        tvActPuertaTemperaturaAmbientePermitirModosAhorro = view.findViewById(R.id.tvActPuertaTemperaturaAmbientePermitirModosAhorro);
        tvActPuertaDiferencialModoAhorro1 = view.findViewById(R.id.tvActPuertaDiferencialModoAhorro1);
        tvActPuertaDiferencialModoAhorro2 = view.findViewById(R.id.tvActPuertaDiferencialModoAhorro2);
        tvActPuertaTiempoPuertaCerradaModoAhorro1 = view.findViewById(R.id.tvActPuertaTiempoPuertaCerradaModoAhorro1);
        tvActPuertaTiempoPuertaCerradaModoAhorro2 = view.findViewById(R.id.tvActPuertaTiempoPuertaCerradaModoAhorro2);
        tvActPuertaTiempoParaRegistrarPuertaAbierta = view.findViewById(R.id.tvActPuertaTiempoParaRegistrarPuertaAbierta);
        tvActPuertaTiempoParaValidarFallaPuertaAbierta = view.findViewById(R.id.tvActPuertaTiempoParaValidarFallaPuertaAbierta);
        tvDeshieloModoDeDeshielo = view.findViewById(R.id.tvDeshieloModoDeDeshielo);
        tvDeshieloDeshieloPorVentilador = view.findViewById(R.id.tvDeshieloDeshieloPorVentilador);
        tvDeshieloTiempoMinimoDeDeshielo = view.findViewById(R.id.tvDeshieloTiempoMinimoDeDeshielo);
        tvDeshieloTiempoMaximoDeDeshielo = view.findViewById(R.id.tvDeshieloTiempoMaximoDeDeshielo);
        tvDeshieloTiempoMinimoParaInicioDeDeshielo = view.findViewById(R.id.tvDeshieloTiempoMinimoParaInicioDeDeshielo);
        tvDeshieloTiempoMaximoParaInicioDeDeshielo = view.findViewById(R.id.tvDeshieloTiempoMaximoParaInicioDeDeshielo);
        tvDeshieloTemperaturaParaFinalizarDeshielo = view.findViewById(R.id.tvDeshieloTemperaturaParaFinalizarDeshielo);
        tvDeshieloTemperaturaDeReinicio = view.findViewById(R.id.tvDeshieloTemperaturaDeReinicio);
        tvDeshieloTemperaturaParaIniciarDeshielo = view.findViewById(R.id.tvDeshieloTemperaturaParaIniciarDeshielo);
        tvDeshieloTiempoDeGoteoParaCompresor = view.findViewById(R.id.tvDeshieloTiempoDeGoteoParaCompresor);
        tvDeshieloTiempoDeGoteoParaVentilador = view.findViewById(R.id.tvDeshieloTiempoDeGoteoParaVentilador);
        tvVentiladorFuncionesVentiladorB0 = view.findViewById(R.id.tvVentiladorFuncionesVentiladorB0);
        tvVentiladorFuncionesVentiladorB1 = view.findViewById(R.id.tvVentiladorFuncionesVentiladorB1);
        tvVentiladorRetrasoPrimerEncendido = view.findViewById(R.id.tvVentiladorRetrasoPrimerEncendido);
        tvActCompresorTiempoMinimoEncendido = view.findViewById(R.id.tvActCompresorTiempoMinimoEncendido);
        tvVentiladorRetardoParaComenzarCiclo = view.findViewById(R.id.tvVentiladorRetardoParaComenzarCiclo);
        tvVentiladorTiempoVentiladorEncendido = view.findViewById(R.id.tvVentiladorTiempoVentiladorEncendido);
        tvVentiladorTiempoVentiladorApagado = view.findViewById(R.id.tvVentiladorTiempoVentiladorApagado);
        tvVentiladorRetardoEncendidoAlCerrarPuerta = view.findViewById(R.id.tvVentiladorRetardoEncendidoAlCerrarPuerta);

        tvProteccionVoltajeProteccionVoltaje = view.findViewById(R.id.tvProteccionVoltajeProteccionVoltaje);
        tvProteccionVoltajeVoltajeProteccionMinimo120 = view.findViewById(R.id.tvProteccionVoltajeVoltajeProteccionMinimo120);
        tvProteccionVoltajeVoltajeProteccionMaximo120minimo220 = view.findViewById(R.id.tvProteccionVoltajeVoltajeProteccionMaximo120minimo220);
        tvProteccionVoltajeVoltajeProteccionMaximo220 = view.findViewById(R.id.tvProteccionVoltajeVoltajeProteccionMaximo220);
        tvProteccionVoltajeTiempoValidacionProteccionVoltaje = view.findViewById(R.id.tvProteccionVoltajeTiempoValidacionProteccionVoltaje);
        tvProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt = view.findViewById(R.id.tvProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt);
        tvProteccionVoltajeHisteresis = view.findViewById(R.id.tvProteccionVoltajeHisteresis);
        tvLoggerTiempoParaLogger = view.findViewById(R.id.tvLoggerTiempoParaLogger);
        tvPlantillaVersion = view.findViewById(R.id.tvPlantillaVersionTitulo);
        tvactualPlantilla = view.findViewById(R.id.tvactualPlantilla);


        tvList.add(tvActCompresorTiempoMinimoEncendido);
        tvList.add(tvActCompresorTiempoMinimoApagado);
        tvList.add(tvActCompresorRetrasoPrimerEncendido);
        tvList.add(tvActTermostatoTemperaturaDeCorteFrio);
        tvList.add(tvActTermostatoDiferencialFrio);
        tvList.add(tvActTermostatoLimiteDeTemperaturaBaja);
        tvList.add(tvActPuertaHabilitar);
        tvList.add(tvActPuertaCortesCompresorPermitirModosAhorro);
        tvList.add(tvActPuertaTemperaturaAmbientePermitirModosAhorro);
        tvList.add(tvActPuertaDiferencialModoAhorro1);
        tvList.add(tvActPuertaDiferencialModoAhorro2);
        tvList.add(tvActPuertaTiempoPuertaCerradaModoAhorro1);
        tvList.add(tvActPuertaTiempoPuertaCerradaModoAhorro2);
        tvList.add(tvActPuertaTiempoParaRegistrarPuertaAbierta);
        tvList.add(tvActPuertaTiempoParaValidarFallaPuertaAbierta);
        tvList.add(tvDeshieloModoDeDeshielo);
        tvList.add(tvDeshieloDeshieloPorVentilador);
        tvList.add(tvDeshieloTiempoMinimoDeDeshielo);
        tvList.add(tvDeshieloTiempoMaximoDeDeshielo);
        tvList.add(tvDeshieloTiempoMinimoParaInicioDeDeshielo);
        tvList.add(tvDeshieloTiempoMaximoParaInicioDeDeshielo);
        tvList.add(tvDeshieloTemperaturaParaFinalizarDeshielo);
        tvList.add(tvDeshieloTemperaturaDeReinicio);
        tvList.add(tvDeshieloTemperaturaParaIniciarDeshielo);
        tvList.add(tvDeshieloTiempoDeGoteoParaCompresor);
        tvList.add(tvDeshieloTiempoDeGoteoParaVentilador);
        tvList.add(tvVentiladorFuncionesVentiladorB0);
        tvList.add(tvVentiladorFuncionesVentiladorB1);
        tvList.add(tvVentiladorRetrasoPrimerEncendido);
        tvList.add(tvVentiladorRetardoParaComenzarCiclo);
        tvList.add(tvVentiladorTiempoVentiladorEncendido);
        tvList.add(tvVentiladorTiempoVentiladorApagado);
        tvList.add(tvVentiladorRetardoEncendidoAlCerrarPuerta);
        tvList.add(tvProteccionVoltajeProteccionVoltaje);
        tvList.add(tvProteccionVoltajeVoltajeProteccionMinimo120);
        tvList.add(tvProteccionVoltajeVoltajeProteccionMaximo120minimo220);
        tvList.add(tvProteccionVoltajeVoltajeProteccionMaximo220);
        tvList.add(tvProteccionVoltajeTiempoValidacionProteccionVoltaje);
        tvList.add(tvProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt);
        tvList.add(tvProteccionVoltajeHisteresis);
        tvList.add(tvLoggerTiempoParaLogger);
        tvList.add(tvPlantillaVersion);
        tvList.add(tvactualFirmwareVersion);
        tvList.add(tvModeloTrefp);

        etActCompresorTiempoMinimoEncendido = view.findViewById(R.id.etActCompresorTiempoMinimoEncendido);
        etActCompresorTiempoMinimoApagado = view.findViewById(R.id.etActCompresorTiempoMinimoApagado);
        etActCompresorRetrasoPrimerEncendido = view.findViewById(R.id.etActCompresorRetrasoPrimerEncendido);
        etActTermostatoTemperaturaDeCorteFrio = view.findViewById(R.id.etActTermostatoTemperaturaDeCorteFrio);
        etActTermostatoDiferencialFrio = view.findViewById(R.id.etActTermostatoDiferencialFrio);
        etActTermostatoLimiteDeTemperaturaBaja = view.findViewById(R.id.etActTermostatoLimiteDeTemperaturaBaja);
        etActPuertaCortesCompresorPermitirModosAhorro = view.findViewById(R.id.etActPuertaCortesCompresorPermitirModosAhorro);
        etActPuertaTemperaturaAmbientePermitirModosAhorro = view.findViewById(R.id.etActPuertaTemperaturaAmbientePermitirModosAhorro);
        etActPuertaDiferencialModoAhorro1 = view.findViewById(R.id.etActPuertaDiferencialModoAhorro1);
        etActPuertaDiferencialModoAhorro2 = view.findViewById(R.id.etActPuertaDiferencialModoAhorro2);
        etActPuertaTiempoPuertaCerradaModoAhorro1 = view.findViewById(R.id.etActPuertaTiempoPuertaCerradaModoAhorro1);
        etActPuertaTiempoPuertaCerradaModoAhorro2 = view.findViewById(R.id.etActPuertaTiempoPuertaCerradaModoAhorro2);
        etActPuertaTiempoParaRegistrarPuertaAbierta = view.findViewById(R.id.etActPuertaTiempoParaRegistrarPuertaAbierta);
        etActPuertaTiempoParaValidarFallaPuertaAbierta = view.findViewById(R.id.etActPuertaTiempoParaValidarFallaPuertaAbierta);
        etDeshieloTiempoMinimoDeDeshielo = view.findViewById(R.id.etDeshieloTiempoMinimoDeDeshielo);
        etDeshieloTiempoMaximoDeDeshielo = view.findViewById(R.id.etDeshieloTiempoMaximoDeDeshielo);
        etDeshieloTiempoMinimoParaInicioDeDeshielo = view.findViewById(R.id.etDeshieloTiempoMinimoParaInicioDeDeshielo);
        etDeshieloTiempoMaximoParaInicioDeDeshielo = view.findViewById(R.id.etDeshieloTiempoMaximoParaInicioDeDeshielo);
        etDeshieloTemperaturaParaFinalizarDeshielo = view.findViewById(R.id.etDeshieloTemperaturaParaFinalizarDeshielo);
        etDeshieloTemperaturaDeReinicio = view.findViewById(R.id.etDeshieloTemperaturaDeReinicio);
        etDeshieloTemperaturaParaIniciarDeshielo = view.findViewById(R.id.etDeshieloTemperaturaParaIniciarDeshielo);
        etDeshieloTiempoDeGoteoParaCompresor = view.findViewById(R.id.etDeshieloTiempoDeGoteoParaCompresor);
        etDeshieloTiempoDeGoteoParaVentilador = view.findViewById(R.id.etDeshieloTiempoDeGoteoParaVentilador);
        etVentiladorRetrasoPrimerEncendido = view.findViewById(R.id.etVentiladorRetrasoPrimerEncendido);
        etVentiladorRetardoParaComenzarCiclo = view.findViewById(R.id.etVentiladorRetardoParaComenzarCiclo);
        etVentiladorTiempoVentiladorEncendido = view.findViewById(R.id.etVentiladorTiempoVentiladorEncendido);
        etVentiladorTiempoVentiladorApagado = view.findViewById(R.id.etVentiladorTiempoVentiladorApagado);
        etVentiladorRetardoEncendidoAlCerrarPuerta = view.findViewById(R.id.etVentiladorRetardoEncendidoAlCerrarPuerta);
        etProteccionVoltajeVoltajeProteccionMinimo120 = view.findViewById(R.id.etProteccionVoltajeVoltajeProteccionMinimo120);
        etProteccionVoltajeVoltajeProteccionMaximo120minimo220 = view.findViewById(R.id.etProteccionVoltajeVoltajeProteccionMaximo120minimo220);
        etProteccionVoltajeVoltajeProteccionMaximo220 = view.findViewById(R.id.etProteccionVoltajeVoltajeProteccionMaximo220);
        etProteccionVoltajeTiempoValidacionProteccionVoltaje = view.findViewById(R.id.etProteccionVoltajeTiempoValidacionProteccionVoltaje);
        etProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt = view.findViewById(R.id.etProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt);

        etProteccionVoltajeHisteresis = view.findViewById(R.id.etProteccionVoltajeHisteresis);
        etLoggerTiempoParaLogger = view.findViewById(R.id.etLoggerTiempoParaLogger);
        etactualFirmwareVersion = view.findViewById(R.id.etactualFirmwareVersion);
        etModeloTrefp = view.findViewById(R.id.etModelTrefp);
        etPlantillaversion = view.findViewById(R.id.etNewPlantillaversion);


        tvListEditText.add(etActCompresorTiempoMinimoEncendido);
        tvListEditText.add(etActCompresorTiempoMinimoApagado);
        tvListEditText.add(etActCompresorRetrasoPrimerEncendido);
        tvListEditText.add(etActTermostatoTemperaturaDeCorteFrio);
        tvListEditText.add(etActTermostatoDiferencialFrio);
        tvListEditText.add(etActTermostatoLimiteDeTemperaturaBaja);
        tvListEditText.add(etActPuertaCortesCompresorPermitirModosAhorro);
        tvListEditText.add(etActPuertaTemperaturaAmbientePermitirModosAhorro);
        tvListEditText.add(etActPuertaDiferencialModoAhorro1);
        tvListEditText.add(etActPuertaDiferencialModoAhorro2);
        tvListEditText.add(etActPuertaTiempoPuertaCerradaModoAhorro1);
        tvListEditText.add(etActPuertaTiempoPuertaCerradaModoAhorro2);
        tvListEditText.add(etActPuertaTiempoParaRegistrarPuertaAbierta);
        tvListEditText.add(etActPuertaTiempoParaValidarFallaPuertaAbierta);
        tvListEditText.add(etDeshieloTiempoMinimoDeDeshielo);
        tvListEditText.add(etDeshieloTiempoMaximoDeDeshielo);
        tvListEditText.add(etDeshieloTiempoMinimoParaInicioDeDeshielo);
        tvListEditText.add(etDeshieloTiempoMaximoParaInicioDeDeshielo);
        tvListEditText.add(etDeshieloTemperaturaParaFinalizarDeshielo);
        tvListEditText.add(etDeshieloTemperaturaDeReinicio);
        tvListEditText.add(etDeshieloTemperaturaParaIniciarDeshielo);
        tvListEditText.add(etDeshieloTiempoDeGoteoParaCompresor);
        tvListEditText.add(etDeshieloTiempoDeGoteoParaVentilador);
        tvListEditText.add(etVentiladorRetrasoPrimerEncendido);
        tvListEditText.add(etVentiladorRetardoParaComenzarCiclo);
        tvListEditText.add(etVentiladorTiempoVentiladorEncendido);
        tvListEditText.add(etVentiladorTiempoVentiladorApagado);
        tvListEditText.add(etVentiladorRetardoEncendidoAlCerrarPuerta);
        tvListEditText.add(etProteccionVoltajeVoltajeProteccionMinimo120);
        tvListEditText.add(etProteccionVoltajeVoltajeProteccionMaximo120minimo220);
        tvListEditText.add(etProteccionVoltajeVoltajeProteccionMaximo220);
        tvListEditText.add(etProteccionVoltajeTiempoValidacionProteccionVoltaje);
        tvListEditText.add(etProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt);
        tvListEditText.add(etProteccionVoltajeHisteresis);
        tvListEditText.add(etactualFirmwareVersion);
        tvListEditText.add(etModeloTrefp);
        tvListEditText.add(etLoggerTiempoParaLogger);
        tvListEditText.add(etPlantillaversion);

        //Declaraciòn de swiches
        switchFuncionesControl = view.findViewById(R.id.switchFuncionesControl);
        switchFuncionesVentiladorB0 = view.findViewById(R.id.switchFuncionesVentiladorB0);
        switchFuncionesVentiladorB1 = view.findViewById(R.id.switchFuncionesVentiladorB1);


        //declaracion de spinners

        spinnerModosDeshielo = view.findViewById(R.id.spinnerModosdeshielo);
        spinnerFuncionesDeshielo = view.findViewById(R.id.spinnerFuncionesDeshielo);
        spinnerProteccionVoltaje = view.findViewById(R.id.spinnerProteccionVoltaje);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(view.getContext(),
                R.array.modosDeshielo, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModosDeshielo.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(view.getContext(),
                R.array.funcionesDeshielo, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFuncionesDeshielo.setAdapter(adapter3);

        ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(view.getContext(),
                R.array.proteccionVoltaje, android.R.layout.simple_spinner_item);
        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProteccionVoltaje.setAdapter(adapter6);

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
            tvActCompresorTiempoMinimoEncendido.setText(dataListPlantilla.get(30));
            tvActCompresorTiempoMinimoApagado.setText(dataListPlantilla.get(31));
            tvActCompresorRetrasoPrimerEncendido.setText(dataListPlantilla.get(39));

            tvActTermostatoTemperaturaDeCorteFrio.setText(dataListPlantilla.get(4));
            tvActTermostatoDiferencialFrio.setText(dataListPlantilla.get(5));
            tvActTermostatoLimiteDeTemperaturaBaja.setText(dataListPlantilla.get(12));

            tvActPuertaCortesCompresorPermitirModosAhorro.setText(dataListPlantilla.get(27));
            tvActPuertaTemperaturaAmbientePermitirModosAhorro.setText(dataListPlantilla.get(11));
            tvActPuertaDiferencialModoAhorro1.setText(dataListPlantilla.get(9));
            tvActPuertaDiferencialModoAhorro2.setText(dataListPlantilla.get(10));
            tvActPuertaTiempoPuertaCerradaModoAhorro1.setText(dataListPlantilla.get(40));
            tvActPuertaTiempoPuertaCerradaModoAhorro2.setText(dataListPlantilla.get(41));
            tvActPuertaTiempoParaRegistrarPuertaAbierta.setText(dataListPlantilla.get(42));
            tvActPuertaTiempoParaValidarFallaPuertaAbierta.setText(dataListPlantilla.get(21));

            tvDeshieloTiempoMinimoDeDeshielo.setText(dataListPlantilla.get(17));
            tvDeshieloTiempoMaximoDeDeshielo.setText(dataListPlantilla.get(18));
            tvDeshieloTiempoMinimoParaInicioDeDeshielo.setText(dataListPlantilla.get(19));
            tvDeshieloTiempoMaximoParaInicioDeDeshielo.setText(dataListPlantilla.get(20));
            tvDeshieloTemperaturaParaFinalizarDeshielo.setText(dataListPlantilla.get(6));
            tvDeshieloTemperaturaDeReinicio.setText(dataListPlantilla.get(7));
            tvDeshieloTemperaturaParaIniciarDeshielo.setText(dataListPlantilla.get(8));
            tvDeshieloTiempoDeGoteoParaCompresor.setText(dataListPlantilla.get(34));
            tvDeshieloTiempoDeGoteoParaVentilador.setText(dataListPlantilla.get(35));

            tvVentiladorRetrasoPrimerEncendido.setText(dataListPlantilla.get(38));
            tvVentiladorRetardoParaComenzarCiclo.setText(dataListPlantilla.get(36));
            tvVentiladorTiempoVentiladorEncendido.setText(dataListPlantilla.get(32));
            tvVentiladorTiempoVentiladorApagado.setText((dataListPlantilla.get(33)));
            tvVentiladorRetardoEncendidoAlCerrarPuerta.setText(dataListPlantilla.get(37));

            tvProteccionVoltajeVoltajeProteccionMinimo120.setText(dataListPlantilla.get(13));
            tvProteccionVoltajeVoltajeProteccionMaximo120minimo220.setText(dataListPlantilla.get(14));
            tvProteccionVoltajeVoltajeProteccionMaximo220.setText(dataListPlantilla.get(15));
            tvProteccionVoltajeTiempoValidacionProteccionVoltaje.setText(dataListPlantilla.get(16));
            tvProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt.setText(dataListPlantilla.get(29));
            tvProteccionVoltajeHisteresis.setText(dataListPlantilla.get(28));
            tvLoggerTiempoParaLogger.setText(dataListPlantilla.get(43));
            tvactualPlantilla.setText(dataListPlantilla.get(47));


            //mismos datos para los campos editables.
            etActCompresorTiempoMinimoEncendido.setText(dataListPlantilla.get(30));
            etActCompresorTiempoMinimoApagado.setText(dataListPlantilla.get(31));
            etActCompresorRetrasoPrimerEncendido.setText(dataListPlantilla.get(39));

            etActTermostatoTemperaturaDeCorteFrio.setText(dataListPlantilla.get(4));
            etActTermostatoDiferencialFrio.setText(dataListPlantilla.get(5));
            etActTermostatoLimiteDeTemperaturaBaja.setText(dataListPlantilla.get(12));

            etActPuertaCortesCompresorPermitirModosAhorro.setText(dataListPlantilla.get(27));
            etActPuertaTemperaturaAmbientePermitirModosAhorro.setText(dataListPlantilla.get(11));
            etActPuertaDiferencialModoAhorro1.setText(dataListPlantilla.get(9));
            etActPuertaDiferencialModoAhorro2.setText(dataListPlantilla.get(10));
            etActPuertaTiempoPuertaCerradaModoAhorro1.setText(dataListPlantilla.get(40));
            etActPuertaTiempoPuertaCerradaModoAhorro2.setText(dataListPlantilla.get(41));
            etActPuertaTiempoParaRegistrarPuertaAbierta.setText(dataListPlantilla.get(42));
            etActPuertaTiempoParaValidarFallaPuertaAbierta.setText(dataListPlantilla.get(21));

            etDeshieloTiempoMinimoDeDeshielo.setText(dataListPlantilla.get(17));
            etDeshieloTiempoMaximoDeDeshielo.setText(dataListPlantilla.get(18));
            etDeshieloTiempoMinimoParaInicioDeDeshielo.setText(dataListPlantilla.get(19));
            etDeshieloTiempoMaximoParaInicioDeDeshielo.setText(dataListPlantilla.get(20));
            etDeshieloTemperaturaParaFinalizarDeshielo.setText(dataListPlantilla.get(6));
            etDeshieloTemperaturaDeReinicio.setText(dataListPlantilla.get(7));
            etDeshieloTemperaturaParaIniciarDeshielo.setText(dataListPlantilla.get(8));//verificar negativo
            etDeshieloTiempoDeGoteoParaCompresor.setText(dataListPlantilla.get(34));
            etDeshieloTiempoDeGoteoParaVentilador.setText(dataListPlantilla.get(35));

            etVentiladorRetrasoPrimerEncendido.setText(dataListPlantilla.get(38));//verificaer
            etVentiladorRetardoParaComenzarCiclo.setText(dataListPlantilla.get(36));
            etVentiladorTiempoVentiladorEncendido.setText(dataListPlantilla.get(32));
            etVentiladorTiempoVentiladorApagado.setText((dataListPlantilla.get(33)));
            etVentiladorRetardoEncendidoAlCerrarPuerta.setText(dataListPlantilla.get(37));

            etProteccionVoltajeVoltajeProteccionMinimo120.setText(dataListPlantilla.get(13));
            etProteccionVoltajeVoltajeProteccionMaximo120minimo220.setText(dataListPlantilla.get(14));
            etProteccionVoltajeVoltajeProteccionMaximo220.setText(dataListPlantilla.get(15));
            etProteccionVoltajeTiempoValidacionProteccionVoltaje.setText(dataListPlantilla.get(16));
            etProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt.setText(dataListPlantilla.get(29));
            etProteccionVoltajeHisteresis.setText(dataListPlantilla.get(28));

            etLoggerTiempoParaLogger.setText(dataListPlantilla.get(43));
            etPlantillaversion.setText(dataListPlantilla.get(47));
        }

    }

    private void setSwitchesData() {
        if (dataListPlantilla.size() == 1) {
            Toast.makeText(getContext(), "Realizar Hanshake primero", Toast.LENGTH_SHORT).show();
        } else {
            if (dataListPlantilla.get(23).equals("0"))
                switchFuncionesControl.setChecked(false);
            else if (dataListPlantilla.get(23).equals("1"))
                switchFuncionesControl.setChecked(true);

            //cambio de prueba por obsebacion de Natalia
            if (dataListPlantilla.get(25).equals("10")){
                switchFuncionesVentiladorB0.setChecked(false);
                switchFuncionesVentiladorB1.setChecked(true);
            }else if (dataListPlantilla.get(25).equals("11")){
                switchFuncionesVentiladorB0.setChecked(true);
                switchFuncionesVentiladorB1.setChecked(true);
            }else if (dataListPlantilla.get(25).equals("01")){
                switchFuncionesVentiladorB0.setChecked(true);
                switchFuncionesVentiladorB1.setChecked(false);
            }else if (dataListPlantilla.get(25).equals("00")){
                switchFuncionesVentiladorB0.setChecked(false);
                switchFuncionesVentiladorB1.setChecked(false);
            }
        }
    }
    private void setSpinnersData() {
        if (dataListPlantilla.size() == 1) {
            Toast.makeText(getContext(), "Realizar Hanshake primero", Toast.LENGTH_SHORT).show();
        } else {
            if (dataListPlantilla.get(22).equals("00"))
                tvDeshieloModoDeDeshielo.setText("Sin deshielo");
            else if (dataListPlantilla.get(22).equals("10"))
                tvDeshieloModoDeDeshielo.setText("Entrada por tiempo y salida por temperatura de deshielo");
            else if (dataListPlantilla.get(22).equals("11"))
                tvDeshieloModoDeDeshielo.setText("Entrada y salida por temperatura de deshielo");
            else if (dataListPlantilla.get(22).equals("01"))
                tvDeshieloModoDeDeshielo.setText("Entrada por tiempo y salida por temperatura ambiente");

            if (dataListPlantilla.get(23).equals("0"))
                tvActPuertaHabilitar.setText("Puerta cerrada");
            else if (dataListPlantilla.get(23).equals("1"))
                tvActPuertaHabilitar.setText("Puerta abierta");


            if (dataListPlantilla.get(24).equals("0"))
                tvDeshieloDeshieloPorVentilador.setText("Deshielo por resistencia");
            else if (dataListPlantilla.get(24).equals("1"))
                tvDeshieloDeshieloPorVentilador.setText("Deshielo por ventilador");

            if (dataListPlantilla.get(26).equals("0"))
                tvProteccionVoltajeProteccionVoltaje.setText("Sin protección de voltaje");
            else if (dataListPlantilla.get(26).equals("1"))
                tvProteccionVoltajeProteccionVoltaje.setText("Protección a 120 volts");
            else if (dataListPlantilla.get(26).equals("10"))
                tvProteccionVoltajeProteccionVoltaje.setText("Protección a 220 volts");

            if (dataListPlantilla.get(25).equals("10")){
                //tvVentiladorFuncionesVentiladorB0.setText("Ventilador apagado cuando el compresor está apagado");
                //OROGINAL tvVentiladorFuncionesVentiladorB1.setText("Ventilador se enciende al abrir la puerta");
                tvVentiladorFuncionesVentiladorB0.setText("Ventilador apagado cuando el compresor está apagado");
                tvVentiladorFuncionesVentiladorB1.setText("Ventilador se enciende al abrir la puerta");
            }
            else if (dataListPlantilla.get(25).equals("11")){
                tvVentiladorFuncionesVentiladorB0.setText("Ventilador permanece encendido cuando compresor apagado");
                tvVentiladorFuncionesVentiladorB1.setText("Ventilador se enciende al abrir la puerta");
            }
            else if (dataListPlantilla.get(25).equals("01")){
                //tvVentiladorFuncionesVentiladorB0.setText("Ventilador permanece encendido cuando compresor apagado");
                //tvVentiladorFuncionesVentiladorB1.setText("Ventilador se apaga al abrir puerta");ORIGNAL
                tvVentiladorFuncionesVentiladorB0.setText("Ventilador permanece encendido cuando compresor apagado");
                tvVentiladorFuncionesVentiladorB1.setText("Ventilador se apaga al abrir puerta");
            }
            else if (dataListPlantilla.get(25).equals("00")){
                tvVentiladorFuncionesVentiladorB0.setText("Ventilador apagado cuando el compresor está apagado");
                tvVentiladorFuncionesVentiladorB1.setText("Ventilador se apaga al abrir puerta");
            }

            //mismos datos para spinners
            if (dataListPlantilla.get(22).equals("00"))
                spinnerModosDeshielo.setSelection(0);
            else if (dataListPlantilla.get(22).equals("10"))
                spinnerModosDeshielo.setSelection(2);
            else if (dataListPlantilla.get(22).equals("11"))
                spinnerModosDeshielo.setSelection(3);
            else if (dataListPlantilla.get(22).equals("01"))
                spinnerModosDeshielo.setSelection(1);

            if (dataListPlantilla.get(24).equals("0"))
                spinnerFuncionesDeshielo.setSelection(0);
            else if (dataListPlantilla.get(24).equals("1"))
                spinnerFuncionesDeshielo.setSelection(1);

            if (dataListPlantilla.get(26).equals("0"))
                spinnerProteccionVoltaje.setSelection(0);
            else if (dataListPlantilla.get(26).equals("1"))
                spinnerProteccionVoltaje.setSelection(1);
            else if (dataListPlantilla.get(26).equals("10"))
                spinnerProteccionVoltaje.setSelection(2);
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

    private boolean checkEmptydata() {
        int i = 0;
        do {
            if (tvListEditText.get(i).getText().toString().equals("")) {
                tvListEditText.get(i).requestFocus();
                final int j=i;
                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.smoothScrollTo(0, tvListEditText.get(j).getBottom());
                    }
                });
                return false;
            }
            i++;

        } while (i < tvListEditText.size());
        return true;
    }

    private void sortFromDataToHexa() {
        List<EditText> editTextsList = new ArrayList<>(checkCorrectData());
        if (editTextsList.isEmpty()) {
            List<String> arrayListInfo = new ArrayList<>(GetHexFromRealDataImbera.getData(getList()));
            StringBuilder stringBuilder = new StringBuilder();
            for (String dato:arrayListInfo){
                stringBuilder.append(dato.toUpperCase());
            }
            new MyAsyncTaskSendNewPlantilla(stringBuilder).execute();
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
            case "mododeshielo":{
                if (selected==2)
                    return "00000010";
                else if (selected==0)
                    return "00000000";
                else if (selected==3)
                    return "00000011";
                else if (selected==1)
                    return "00000001";
                break;
            }
            case "funcionesControl":{
                if (selected==1)
                    return "00000100";
                else if (selected==0)
                    return "00000000";
                break;
            }
            case "funcionesdeshielo":{
                if (selected==1)
                    return "00000100";
                else if (selected==0)
                    return "00000000";
                break;
            }
            case "funcionesvoltaje":{
                if (selected==1)
                    return "00000001";
                else if (selected==0)
                    return "00000000";
                else if (selected==2)
                    return "00000010";
                break;
            }

        }
        return "";
    }

    public String getOptionSwitch(String option,boolean ischecked){
        Log.d("ischecked",":"+ischecked);
        if ("funcionesControl".equals(option)) {
            if (ischecked)
                return "00000100";
            else
                return "00000000";
        }
        return "";
    }

    public String getOptionSwitchB0B1(boolean selectedB1, boolean selectedB0){
        Log.d("VALOR","B1:"+selectedB1);
        Log.d("VALOR","B0:"+selectedB0);
        if (selectedB0) {
            if (selectedB1)
                return "00000011";
            else
                return "00000001";
        }else{
            if (selectedB1)
                return "00000010";
            else
                return "00000000";
        }
    }

    private void errorEditText(EditText editText,String condition){
        editText.setError(condition);
    }

    private List<EditText> checkCorrectData(){
        List<EditText> editTextsList = new ArrayList<>(getEditTextList());
        //falta agregar función
        List<EditText> editTextsWrongDataList = new ArrayList<>();
        for (int i =0 ; i<editTextsList.size(); i++){
            if (i==0 || i==2 ||  i==3 || i==4 || i==7 || i==8){//-99 a 99
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                if (numf<-99 || numf > 99){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }
            if (i==1 || i==5 || i == 6){//0.0 a 10.0
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                if (numf<-0.0 || numf > 10.0){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }
            if (i==9 || i==12 || i == 13 || i == 14 || i == 15 || i == 16|| i == 17|| i == 33|| i == 34 || i == 10){//00 a 99
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                int num = (int) numf;
                if (num<-0 || num > 99){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }

            if (i==11 ){//00 a 50
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                int num = (int) numf;
                if (num<-0 || num > 50){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }

            if ( i==18 ){//00 a 10
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                int num = (int) numf;
                if (num<-0 || num > 10){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }

            if (i==19){//0.0 a 9.9
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                if (numf<-0.0 || numf > 9.9){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }

            if (i== 20 ||i== 21 ||i== 22 ||i== 23 ||i== 24 ||i== 25 ||i== 26 ||i== 27 ||i== 28 || i== 29 ||i== 30 ||i== 31 ||i== 32 ){//0.0 a 25
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                if(i==23 || i==24){
                    if (numf == 0.0 || numf == 0){
                        editTextsWrongDataList.add(editTextsList.get(i));
                        errorEditText(editTextsList.get(i), "Este campo no puede ser 0");
                    }else if(numf < 0.1 || numf > 25.0 || numf > 25){
                        editTextsWrongDataList.add(editTextsList.get(i));
                        errorEditText(editTextsList.get(i), "Fuera de límites");
                    }

                }else{
                    if (numf<-0.0 || numf > 25.0){
                        errorEditText(editTextsList.get(i), "Fuera de límites");
                        editTextsWrongDataList.add(editTextsList.get(i));
                    }
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
                FinalListData = GetRealDataFromHexaImbera.convert(listData,"Lectura de parámetros de operación");
                dataListPlantilla = GetRealDataFromHexaImbera.GetRealData(FinalListData,"Lectura de parámetros de operación");
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

    class MyAsyncTaskSendNewPlantilla extends AsyncTask<Integer, Integer, String> {
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
                    listenermain.printExcel(getOriginalList(),"imbera");
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


    //utilidad
    public List<String> getOriginalList(){
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
    }
    private List<EditText> getEditTextList(){
        List<EditText> editTextsList = new ArrayList<>();
        editTextsList.add(etActTermostatoTemperaturaDeCorteFrio);
        editTextsList.add(etActTermostatoDiferencialFrio);
        editTextsList.add(etDeshieloTemperaturaParaFinalizarDeshielo);
        editTextsList.add(etDeshieloTemperaturaDeReinicio);
        editTextsList.add(etDeshieloTemperaturaParaIniciarDeshielo);
        editTextsList.add(etActPuertaDiferencialModoAhorro1);
        editTextsList.add(etActPuertaDiferencialModoAhorro2);
        editTextsList.add(etActPuertaTemperaturaAmbientePermitirModosAhorro);
        editTextsList.add(etActTermostatoLimiteDeTemperaturaBaja);
        editTextsList.add(etProteccionVoltajeVoltajeProteccionMinimo120);
        editTextsList.add(etProteccionVoltajeVoltajeProteccionMaximo120minimo220);
        editTextsList.add(etProteccionVoltajeVoltajeProteccionMaximo220);
        editTextsList.add(etProteccionVoltajeTiempoValidacionProteccionVoltaje);
        editTextsList.add(etDeshieloTiempoMinimoDeDeshielo);
        editTextsList.add(etDeshieloTiempoMaximoDeDeshielo);
        editTextsList.add(etDeshieloTiempoMinimoParaInicioDeDeshielo);
        editTextsList.add(etDeshieloTiempoMaximoParaInicioDeDeshielo);
        editTextsList.add(etActPuertaTiempoParaValidarFallaPuertaAbierta);
        editTextsList.add(etActPuertaCortesCompresorPermitirModosAhorro);
        editTextsList.add(etProteccionVoltajeHisteresis);
        editTextsList.add(etProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt);
        editTextsList.add(etActCompresorTiempoMinimoEncendido);
        editTextsList.add(etActCompresorTiempoMinimoApagado);
        editTextsList.add(etVentiladorTiempoVentiladorEncendido);
        editTextsList.add(etVentiladorTiempoVentiladorApagado);
        editTextsList.add(etDeshieloTiempoDeGoteoParaCompresor);
        editTextsList.add(etDeshieloTiempoDeGoteoParaVentilador);
        editTextsList.add(etVentiladorRetardoParaComenzarCiclo);
        editTextsList.add(etVentiladorRetardoEncendidoAlCerrarPuerta);
        editTextsList.add(etVentiladorRetrasoPrimerEncendido);
        editTextsList.add(etActCompresorRetrasoPrimerEncendido);
        editTextsList.add(etActPuertaTiempoPuertaCerradaModoAhorro1);
        editTextsList.add(etActPuertaTiempoPuertaCerradaModoAhorro2);
        editTextsList.add(etActPuertaTiempoParaRegistrarPuertaAbierta);
        editTextsList.add(etLoggerTiempoParaLogger);
        editTextsList.add(etactualFirmwareVersion);
        editTextsList.add(etModeloTrefp);
        editTextsList.add(etPlantillaversion);
        return editTextsList;
    }
    public List<String> getList(){
        List<String> arrayListInfo = new ArrayList<>();
        arrayListInfo.add(etActTermostatoTemperaturaDeCorteFrio.getText().toString());
        arrayListInfo.add(etActTermostatoDiferencialFrio.getText().toString());
        arrayListInfo.add(etDeshieloTemperaturaParaFinalizarDeshielo.getText().toString());
        arrayListInfo.add(etDeshieloTemperaturaDeReinicio.getText().toString());
        arrayListInfo.add(etDeshieloTemperaturaParaIniciarDeshielo.getText().toString());
        arrayListInfo.add(etActPuertaDiferencialModoAhorro1.getText().toString());
        arrayListInfo.add(etActPuertaDiferencialModoAhorro2.getText().toString());
        arrayListInfo.add(etActPuertaTemperaturaAmbientePermitirModosAhorro.getText().toString());
        arrayListInfo.add(etActTermostatoLimiteDeTemperaturaBaja.getText().toString());
        arrayListInfo.add(etProteccionVoltajeVoltajeProteccionMinimo120.getText().toString());
        arrayListInfo.add(etProteccionVoltajeVoltajeProteccionMaximo120minimo220.getText().toString());
        arrayListInfo.add(etProteccionVoltajeVoltajeProteccionMaximo220.getText().toString());
        arrayListInfo.add(etProteccionVoltajeTiempoValidacionProteccionVoltaje.getText().toString());
        arrayListInfo.add(etDeshieloTiempoMinimoDeDeshielo.getText().toString());
        arrayListInfo.add(etDeshieloTiempoMaximoDeDeshielo.getText().toString());
        arrayListInfo.add(etDeshieloTiempoMinimoParaInicioDeDeshielo.getText().toString());
        arrayListInfo.add(etDeshieloTiempoMaximoParaInicioDeDeshielo.getText().toString());
        arrayListInfo.add(etActPuertaTiempoParaValidarFallaPuertaAbierta.getText().toString());
        arrayListInfo.add(getOptionSpinner("mododeshielo",spinnerModosDeshielo.getSelectedItemPosition()));
        arrayListInfo.add(getOptionSwitch("funcionesControl",switchFuncionesControl.isChecked()));
        arrayListInfo.add(getOptionSpinner("funcionesdeshielo",spinnerFuncionesDeshielo.getSelectedItemPosition()));
        arrayListInfo.add(getOptionSwitchB0B1(switchFuncionesVentiladorB1.isChecked(),switchFuncionesVentiladorB0.isChecked()));
        arrayListInfo.add(getOptionSpinner("funcionesvoltaje",spinnerProteccionVoltaje.getSelectedItemPosition()));
        arrayListInfo.add(etActPuertaCortesCompresorPermitirModosAhorro.getText().toString());
        arrayListInfo.add(etProteccionVoltajeHisteresis.getText().toString());
        arrayListInfo.add(etProteccionVoltajeTiempoValidacionParaSalirDeProtDeVolt.getText().toString());
        arrayListInfo.add(etActCompresorTiempoMinimoEncendido.getText().toString());
        arrayListInfo.add(etActCompresorTiempoMinimoApagado.getText().toString());
        arrayListInfo.add(etVentiladorTiempoVentiladorEncendido.getText().toString());
        arrayListInfo.add(etVentiladorTiempoVentiladorApagado.getText().toString());
        arrayListInfo.add(etDeshieloTiempoDeGoteoParaCompresor.getText().toString());
        arrayListInfo.add(etDeshieloTiempoDeGoteoParaVentilador.getText().toString());
        arrayListInfo.add(etVentiladorRetardoParaComenzarCiclo.getText().toString());
        arrayListInfo.add(etVentiladorRetardoEncendidoAlCerrarPuerta.getText().toString());
        arrayListInfo.add(etVentiladorRetrasoPrimerEncendido.getText().toString());
        arrayListInfo.add(etActCompresorRetrasoPrimerEncendido.getText().toString());
        arrayListInfo.add(etActPuertaTiempoPuertaCerradaModoAhorro1.getText().toString());
        arrayListInfo.add(etActPuertaTiempoPuertaCerradaModoAhorro2.getText().toString());
        arrayListInfo.add(etActPuertaTiempoParaRegistrarPuertaAbierta.getText().toString());
        arrayListInfo.add(etLoggerTiempoParaLogger.getText().toString());
        arrayListInfo.add(etactualFirmwareVersion.getText().toString());
        arrayListInfo.add(etModeloTrefp.getText().toString());
        arrayListInfo.add(etPlantillaversion.getText().toString());

        return arrayListInfo;
    }



    public interface listener {
        public void printExcel(List<String> data,String deviceName);

    }
    //Listeners
    public void setListener(listener callback) {
        this.listenermain = callback;
    }

}