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
import com.example.imberap.utility.GetHexFromRealDataOxxo;
import com.example.imberap.utility.GetRealDataFromHexaImbera;
import com.example.imberap.utility.GetRealDataFromHexaOxxo;
import com.google.android.material.switchmaterial.SwitchMaterial;
import java.util.ArrayList;
import java.util.List;

public class PlantillaOxxoFragment extends Fragment {
    String TAG="PlantillaOXXOFragment";
    listener listenermain;

    List<TextView> tvList = new ArrayList<TextView>();
    List<EditText> tvListEditText = new ArrayList<EditText>();

    ScrollView scrollView;
    SwitchMaterial switchOxxoEscalaTemperatura,switchOxxoModoAhorrador,switchOxxoOnOffdeshielo,switchOxxoPullDown,switchOxxoSwitchPuerta,switchOxxoSetAbreviadoParametros;

    TextView tvActOxxoSetpointDiurno, tvActOxxoDiferencialDiurno, tvActOxxoOffsetDiurno,tvActOxxoLimiteInferiorAjusteSetPoint,tvActLimiteSuperiorAjusteSetPoint,tvActOxxoDiferencialTempParaAhorro1,tvActOxxoDiferencialTempParaAhorro2
            ,tvActOxxoTemperaturaEvaporadorTerminarDeshielo,tvActOxxoTemperaturaAmbienteTerminarDeshielo,tvActOxxoTemperaturaPulldown,tvActOxxoVoltajeProteccionMinimo120,tvOxxoVoltajeProteccionMaximo120minimo220,
            tvOxxoVoltajeProteccionMaximo220,tvOxxoTiempoValidarFallaVoltaje,tvOxxoTiempoEntreDeshielos,tvOxxoTiempoMaximoDeshielo,tvOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta,tvOxxoTiempoValidacionAlarmaPuertaAbierta,
            tvOxxoTiempoPermanenciaModoNocturno,tvOxxoIntensidadFiltroParaSensorAmbienteALaSubida,tvOxxoTiempoAdaptivoDeshielo,tvOxxoHisteresisParaProteccionVoltaje,
            tvOxxoTiempoValidacionSalirFallaVoltaje,tvOxxoTiempoGoteo,tvOxxoTiempoDescansoMinimoCompresor,tvOxxoTiempoPuertaCerradaEntrarModoAhorro1,tvOxxoTiempoPuertaCerradaEntrarModoAhorro2,tvOxxoDireccionModbus,tvactualPlantilla;

    EditText etActOxxoSetpointDiurno,etActOxxoDiferencialDiurno,etActOxxoOffsetDiurno,etActOxxoLimiteInferiorAjusteSetPoint,etActLimiteSuperiorAjusteSetPoint,etActOxxoDiferencialTempParaAhorro1,etActOxxoDiferencialTempParaAhorro2
            ,etActOxxoTemperaturaEvaporadorTerminarDeshielo,etActOxxoTemperaturaAmbienteTerminarDeshielo,etActOxxoTemperaturaPulldown,etActOxxoVoltajeProteccionMinimo120,etOxxoVoltajeProteccionMaximo120minimo220,
            etOxxoVoltajeProteccionMaximo220,etOxxoTiempoValidarFallaVoltaje,etOxxoTiempoEntreDeshielos,etOxxoTiempoMaximoDeshielo,etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta,etOxxoTiempoValidacionAlarmaPuertaAbierta,
            etOxxoTiempoPermanenciaModoNocturno,etOxxoIntensidadFiltroParaSensorAmbienteALaSubida,etOxxoTiempoAdaptivoDeshielo,etOxxoHisteresisParaProteccionVoltaje,etOxxoTiempoValidacionSalirFallaVoltaje,
            etOxxoTiempoGoteo,etOxxoTiempoDescansoMinimoCompresor,etOxxoTiempoPuertaCerradaEntrarModoAhorro1,etOxxoTiempoPuertaCerradaEntrarModoAhorro2,etOxxoDireccionModbus,etOxxoNewPlantillaversion,etOxxoactualFirmwareVersion,etOxxoModelTrefp;


    Spinner spinnerOxxoModosdeshielo ,spinnerOxxoProteccionVoltaje;
    BluetoothServices bluetoothServices;
    BluetoothLeService bluetoothLeService;

    androidx.appcompat.app.AlertDialog progressdialog = null;
    View dialogViewProgressBar;

    List<String> dataListPlantilla = new ArrayList<String>();

    public PlantillaOxxoFragment(BluetoothServices bluetoothServices) {
        this.bluetoothServices = bluetoothServices;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plantilla_oxxo_fragment, container, false);
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

        tvActOxxoSetpointDiurno = view.findViewById(R.id.tvActOxxoSetpointDiurno);
        tvActOxxoDiferencialDiurno = view.findViewById(R.id.tvActOxxoDiferencialDiurno);
        tvActOxxoOffsetDiurno = view.findViewById(R.id.tvActOxxoOffsetDiurno);
        tvActOxxoLimiteInferiorAjusteSetPoint = view.findViewById(R.id.tvActOxxoLimiteInferiorAjusteSetPoint);
        tvActLimiteSuperiorAjusteSetPoint = view.findViewById(R.id.tvActLimiteSuperiorAjusteSetPoint);
        tvActOxxoDiferencialTempParaAhorro1 = view.findViewById(R.id.tvActOxxoDiferencialTempParaAhorro1);
        tvActOxxoDiferencialTempParaAhorro2 = view.findViewById(R.id.tvActOxxoDiferencialTempParaAhorro2);
        tvActOxxoTemperaturaEvaporadorTerminarDeshielo = view.findViewById(R.id.tvActOxxoTemperaturaEvaporadorTerminarDeshielo);
        tvActOxxoTemperaturaAmbienteTerminarDeshielo = view.findViewById(R.id.tvActOxxoTemperaturaAmbienteTerminarDeshielo);
        tvActOxxoTemperaturaPulldown = view.findViewById(R.id.tvActOxxoTemperaturaPulldown);
        tvActOxxoVoltajeProteccionMinimo120 = view.findViewById(R.id.tvActOxxoVoltajeProteccionMinimo120);
        tvOxxoVoltajeProteccionMaximo120minimo220 = view.findViewById(R.id.tvOxxoVoltajeProteccionMaximo120minimo220);
        tvOxxoVoltajeProteccionMaximo220 = view.findViewById(R.id.tvOxxoVoltajeProteccionMaximo220);
        tvOxxoTiempoValidarFallaVoltaje = view.findViewById(R.id.tvOxxoTiempoValidarFallaVoltaje);
        tvOxxoTiempoEntreDeshielos = view.findViewById(R.id.tvOxxoTiempoEntreDeshielos);
        tvOxxoTiempoMaximoDeshielo = view.findViewById(R.id.tvOxxoTiempoMaximoDeshielo);
        tvOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta = view.findViewById(R.id.tvOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta);
        tvOxxoTiempoValidacionAlarmaPuertaAbierta = view.findViewById(R.id.tvOxxoTiempoValidacionAlarmaPuertaAbierta);
        tvOxxoTiempoPermanenciaModoNocturno = view.findViewById(R.id.tvOxxoTiempoPermanenciaModoNocturno);
        tvOxxoIntensidadFiltroParaSensorAmbienteALaSubida = view.findViewById(R.id.tvOxxoIntensidadFiltroParaSensorAmbienteALaSubida);
        tvOxxoTiempoAdaptivoDeshielo = view.findViewById(R.id.tvOxxoTiempoAdaptivoDeshielo);
        tvOxxoHisteresisParaProteccionVoltaje = view.findViewById(R.id.tvOxxoHisteresisParaProteccionVoltaje);
        tvOxxoTiempoValidacionSalirFallaVoltaje = view.findViewById(R.id.tvOxxoTiempoValidacionSalirFallaVoltaje);
        tvOxxoTiempoGoteo = view.findViewById(R.id.tvOxxoTiempoGoteo);
        tvOxxoTiempoDescansoMinimoCompresor = view.findViewById(R.id.tvOxxoTiempoDescansoMinimoCompresor);
        tvOxxoTiempoPuertaCerradaEntrarModoAhorro1 = view.findViewById(R.id.tvOxxoTiempoPuertaCerradaEntrarModoAhorro1);
        tvOxxoTiempoPuertaCerradaEntrarModoAhorro2 = view.findViewById(R.id.tvOxxoTiempoPuertaCerradaEntrarModoAhorro2);
        tvOxxoDireccionModbus = view.findViewById(R.id.tvOxxoDireccionModbus);
        tvactualPlantilla = view.findViewById(R.id.tvactualPlantilla);


        tvList.add(tvActOxxoSetpointDiurno);
        tvList.add(tvActOxxoDiferencialDiurno);
        tvList.add(tvActOxxoOffsetDiurno);
        tvList.add(tvActOxxoLimiteInferiorAjusteSetPoint);
        tvList.add(tvActLimiteSuperiorAjusteSetPoint);
        tvList.add(tvActOxxoDiferencialTempParaAhorro1);
        tvList.add(tvActOxxoDiferencialTempParaAhorro2);
        tvList.add(tvActOxxoTemperaturaEvaporadorTerminarDeshielo);
        tvList.add(tvActOxxoTemperaturaAmbienteTerminarDeshielo);
        tvList.add(tvActOxxoTemperaturaPulldown);
        tvList.add(tvActOxxoVoltajeProteccionMinimo120);
        tvList.add(tvOxxoVoltajeProteccionMaximo120minimo220);
        tvList.add(tvOxxoVoltajeProteccionMaximo220);
        tvList.add(tvOxxoTiempoValidarFallaVoltaje);
        tvList.add(tvOxxoTiempoEntreDeshielos);
        tvList.add(tvOxxoTiempoMaximoDeshielo);
        tvList.add(tvOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta);
        tvList.add(tvOxxoTiempoValidacionAlarmaPuertaAbierta);
        tvList.add(tvOxxoTiempoPermanenciaModoNocturno);
        tvList.add(tvOxxoIntensidadFiltroParaSensorAmbienteALaSubida);
        tvList.add(tvOxxoTiempoAdaptivoDeshielo);
        tvList.add(tvOxxoHisteresisParaProteccionVoltaje);
        tvList.add(tvOxxoTiempoValidacionSalirFallaVoltaje);
        tvList.add(tvOxxoTiempoGoteo);
        tvList.add(tvOxxoTiempoDescansoMinimoCompresor);
        tvList.add(tvOxxoTiempoPuertaCerradaEntrarModoAhorro1);
        tvList.add(tvOxxoTiempoPuertaCerradaEntrarModoAhorro2);
        tvList.add(tvOxxoDireccionModbus);
        tvList.add(tvactualPlantilla);

        etActOxxoSetpointDiurno = view.findViewById(R.id.etActOxxoSetpointDiurno);
        etActOxxoDiferencialDiurno = view.findViewById(R.id.etActOxxoDiferencialDiurno);
        etActOxxoOffsetDiurno = view.findViewById(R.id.etActOxxoOffsetDiurno);
        etActOxxoLimiteInferiorAjusteSetPoint = view.findViewById(R.id.etActOxxoLimiteInferiorAjusteSetPoint);
        etActLimiteSuperiorAjusteSetPoint = view.findViewById(R.id.etActLimiteSuperiorAjusteSetPoint);
        etActOxxoDiferencialTempParaAhorro1 = view.findViewById(R.id.etActOxxoDiferencialTempParaAhorro1);
        etActOxxoDiferencialTempParaAhorro2 = view.findViewById(R.id.etActOxxoDiferencialTempParaAhorro2);
        etActOxxoTemperaturaEvaporadorTerminarDeshielo = view.findViewById(R.id.etActOxxoTemperaturaEvaporadorTerminarDeshielo);
        etActOxxoTemperaturaAmbienteTerminarDeshielo = view.findViewById(R.id.etActOxxoTemperaturaAmbienteTerminarDeshielo);
        etActOxxoTemperaturaPulldown = view.findViewById(R.id.etActOxxoTemperaturaPulldown);
        etActOxxoVoltajeProteccionMinimo120 = view.findViewById(R.id.etActOxxoVoltajeProteccionMinimo120);
        etOxxoVoltajeProteccionMaximo120minimo220 = view.findViewById(R.id.etOxxoVoltajeProteccionMaximo120minimo220);
        etOxxoVoltajeProteccionMaximo220 = view.findViewById(R.id.etOxxoVoltajeProteccionMaximo220);
        etOxxoTiempoValidarFallaVoltaje = view.findViewById(R.id.etOxxoTiempoValidarFallaVoltaje);
        etOxxoTiempoEntreDeshielos = view.findViewById(R.id.etOxxoTiempoEntreDeshielos);
        etOxxoTiempoMaximoDeshielo = view.findViewById(R.id.etOxxoTiempoMaximoDeshielo);
        etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta = view.findViewById(R.id.etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta);
        etOxxoTiempoValidacionAlarmaPuertaAbierta = view.findViewById(R.id.etOxxoTiempoValidacionAlarmaPuertaAbierta);
        etOxxoTiempoPermanenciaModoNocturno = view.findViewById(R.id.etOxxoTiempoPermanenciaModoNocturno);
        etOxxoIntensidadFiltroParaSensorAmbienteALaSubida = view.findViewById(R.id.etOxxoIntensidadFiltroParaSensorAmbienteALaSubida);
        etOxxoTiempoAdaptivoDeshielo = view.findViewById(R.id.etOxxoTiempoAdaptivoDeshielo);
        spinnerOxxoProteccionVoltaje = view.findViewById(R.id.spinnerOxxoProteccionVoltaje);
        etOxxoHisteresisParaProteccionVoltaje = view.findViewById(R.id.etOxxoHisteresisParaProteccionVoltaje);
        etOxxoTiempoValidacionSalirFallaVoltaje = view.findViewById(R.id.etOxxoTiempoValidacionSalirFallaVoltaje);
        etOxxoTiempoGoteo = view.findViewById(R.id.etOxxoTiempoGoteo);
        etOxxoTiempoDescansoMinimoCompresor = view.findViewById(R.id.etOxxoTiempoDescansoMinimoCompresor);
        etOxxoTiempoPuertaCerradaEntrarModoAhorro1 = view.findViewById(R.id.etOxxoTiempoPuertaCerradaEntrarModoAhorro1);
        etOxxoTiempoPuertaCerradaEntrarModoAhorro2 = view.findViewById(R.id.etOxxoTiempoPuertaCerradaEntrarModoAhorro2);
        etOxxoDireccionModbus = view.findViewById(R.id.etOxxoDireccionModbus);
        etOxxoNewPlantillaversion = view.findViewById(R.id.etOxxoNewPlantillaversion);
        etOxxoactualFirmwareVersion = view.findViewById(R.id.etOxxoactualFirmwareVersion);
        etOxxoModelTrefp = view.findViewById(R.id.etOxxoModelTrefp);

        tvListEditText.add(etActOxxoSetpointDiurno);
        tvListEditText.add(etActOxxoDiferencialDiurno);
        tvListEditText.add(etActOxxoOffsetDiurno);
        tvListEditText.add(etActOxxoLimiteInferiorAjusteSetPoint);
        tvListEditText.add(etActLimiteSuperiorAjusteSetPoint);
        tvListEditText.add(etActOxxoDiferencialTempParaAhorro1);
        tvListEditText.add(etActOxxoDiferencialTempParaAhorro2);
        tvListEditText.add(etActOxxoTemperaturaEvaporadorTerminarDeshielo);
        tvListEditText.add(etActOxxoTemperaturaAmbienteTerminarDeshielo);
        tvListEditText.add(etActOxxoTemperaturaPulldown);
        tvListEditText.add(etActOxxoVoltajeProteccionMinimo120);
        tvListEditText.add(etOxxoVoltajeProteccionMaximo120minimo220);
        tvListEditText.add(etOxxoVoltajeProteccionMaximo220);
        tvListEditText.add(etOxxoTiempoValidarFallaVoltaje);
        tvListEditText.add(etOxxoTiempoEntreDeshielos);
        tvListEditText.add(etOxxoTiempoMaximoDeshielo);
        tvListEditText.add(etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta);
        tvListEditText.add(etOxxoTiempoValidacionAlarmaPuertaAbierta);
        tvListEditText.add(etOxxoTiempoPermanenciaModoNocturno);
        tvListEditText.add(etOxxoIntensidadFiltroParaSensorAmbienteALaSubida);
        tvListEditText.add(etOxxoTiempoAdaptivoDeshielo);
        tvListEditText.add(etOxxoHisteresisParaProteccionVoltaje);
        tvListEditText.add(etOxxoTiempoValidacionSalirFallaVoltaje);
        tvListEditText.add(etOxxoTiempoGoteo);
        tvListEditText.add(etOxxoTiempoDescansoMinimoCompresor);
        tvListEditText.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro1);
        tvListEditText.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro2);
        tvListEditText.add(etOxxoDireccionModbus);
        tvListEditText.add(etOxxoNewPlantillaversion);
        tvListEditText.add(etOxxoactualFirmwareVersion);
        tvListEditText.add(etOxxoModelTrefp);


        //Declaraciòn de swiches
        switchOxxoModoAhorrador = view.findViewById(R.id.switchOxxoModoAhorrador);
        switchOxxoOnOffdeshielo = view.findViewById(R.id.switchOxxoOnOffdeshielo);
        switchOxxoPullDown = view.findViewById(R.id.switchOxxoPullDown);
        switchOxxoSwitchPuerta = view.findViewById(R.id.switchOxxoSwitchPuerta);
        switchOxxoSetAbreviadoParametros = view.findViewById(R.id.switchOxxoSetAbreviadoParametros);
        switchOxxoEscalaTemperatura = view.findViewById(R.id.switchOxxoEscalaTemperatura);

        //declaracion de spinners
        spinnerOxxoModosdeshielo = view.findViewById(R.id.spinnerOxxoModosdeshielo);
        spinnerOxxoProteccionVoltaje = view.findViewById(R.id.spinnerOxxoProteccionVoltaje);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(view.getContext(),
                R.array.modosDeshieloOxxo, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOxxoModosdeshielo.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.proteccionVoltaje, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOxxoProteccionVoltaje.setAdapter(adapter);
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
            tvActOxxoDiferencialTempParaAhorro1.setText(dataListPlantilla.get(9));
            tvActOxxoDiferencialTempParaAhorro2.setText(dataListPlantilla.get(10));
            tvActOxxoTemperaturaEvaporadorTerminarDeshielo.setText(dataListPlantilla.get(11));
            tvActOxxoTemperaturaAmbienteTerminarDeshielo.setText(dataListPlantilla.get(12));
            tvActOxxoTemperaturaPulldown.setText(dataListPlantilla.get(13));
            tvActOxxoVoltajeProteccionMinimo120.setText(dataListPlantilla.get(14));
            tvOxxoVoltajeProteccionMaximo120minimo220.setText(dataListPlantilla.get(15));
            tvOxxoVoltajeProteccionMaximo220.setText(dataListPlantilla.get(16));
            tvOxxoTiempoValidarFallaVoltaje.setText(dataListPlantilla.get(17));
            tvOxxoTiempoEntreDeshielos.setText(dataListPlantilla.get(18));
            tvOxxoTiempoMaximoDeshielo.setText(dataListPlantilla.get(19));
            tvOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta.setText(dataListPlantilla.get(20));
            tvOxxoTiempoValidacionAlarmaPuertaAbierta.setText(dataListPlantilla.get(21));
            tvOxxoTiempoPermanenciaModoNocturno.setText(dataListPlantilla.get(22));
            //tvOxxoEscalaTemperatura.setText(dataListPlantilla.get(23)); REPENSAR debe salir el texto segun la opcion, no
            tvOxxoIntensidadFiltroParaSensorAmbienteALaSubida.setText(dataListPlantilla.get(25));
            //tvOxxoModosDeshielo.setText(dataListPlantilla.get(27));
            tvOxxoTiempoAdaptivoDeshielo.setText(dataListPlantilla.get(27));
            //tvOxxoProteccionVoltaje.setText(dataListPlantilla.get(30));
            tvOxxoHisteresisParaProteccionVoltaje.setText(dataListPlantilla.get(29));
            tvOxxoTiempoValidacionSalirFallaVoltaje.setText(dataListPlantilla.get(30));
            tvOxxoTiempoGoteo.setText(dataListPlantilla.get(31));
            tvOxxoTiempoDescansoMinimoCompresor.setText(dataListPlantilla.get(32));
            tvOxxoTiempoPuertaCerradaEntrarModoAhorro1.setText(dataListPlantilla.get(33));
            tvOxxoTiempoPuertaCerradaEntrarModoAhorro2.setText(dataListPlantilla.get(34));
            tvOxxoDireccionModbus.setText(dataListPlantilla.get(35));
            //tvactualPlantilla.setText(dataListPlantilla.get(37));

            //mismos datos para los campos editables.
            etActOxxoSetpointDiurno.setText(dataListPlantilla.get(4));
            etActOxxoDiferencialDiurno.setText(dataListPlantilla.get(5));
            etActOxxoOffsetDiurno.setText(dataListPlantilla.get(6));
            etActOxxoLimiteInferiorAjusteSetPoint.setText(dataListPlantilla.get(7));
            etActLimiteSuperiorAjusteSetPoint.setText(dataListPlantilla.get(8));
            etActOxxoDiferencialTempParaAhorro1.setText(dataListPlantilla.get(9));
            etActOxxoDiferencialTempParaAhorro2.setText(dataListPlantilla.get(10));
            etActOxxoTemperaturaEvaporadorTerminarDeshielo.setText(dataListPlantilla.get(11));
            etActOxxoTemperaturaAmbienteTerminarDeshielo.setText(dataListPlantilla.get(12));
            etActOxxoTemperaturaPulldown.setText(dataListPlantilla.get(13));
            etActOxxoVoltajeProteccionMinimo120.setText(dataListPlantilla.get(14));
            etOxxoVoltajeProteccionMaximo120minimo220.setText(dataListPlantilla.get(15));
            etOxxoVoltajeProteccionMaximo220.setText(dataListPlantilla.get(16));
            etOxxoTiempoValidarFallaVoltaje.setText(dataListPlantilla.get(17));
            etOxxoTiempoEntreDeshielos.setText(dataListPlantilla.get(18));
            etOxxoTiempoMaximoDeshielo.setText(dataListPlantilla.get(19));
            etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta.setText(dataListPlantilla.get(20));
            etOxxoTiempoValidacionAlarmaPuertaAbierta.setText(dataListPlantilla.get(21));
            etOxxoTiempoPermanenciaModoNocturno.setText(dataListPlantilla.get(22));
            etOxxoIntensidadFiltroParaSensorAmbienteALaSubida.setText(dataListPlantilla.get(25));
            etOxxoTiempoAdaptivoDeshielo.setText(dataListPlantilla.get(27));//verificar negativo
            //etOxxoProteccionVoltaje.setText(dataListPlantilla.get(28));
            etOxxoHisteresisParaProteccionVoltaje.setText(dataListPlantilla.get(29));
            etOxxoTiempoValidacionSalirFallaVoltaje.setText(dataListPlantilla.get(30));//verificaer
            etOxxoTiempoGoteo.setText(dataListPlantilla.get(31));
            etOxxoTiempoDescansoMinimoCompresor.setText(dataListPlantilla.get(32));
            etOxxoTiempoPuertaCerradaEntrarModoAhorro1.setText((dataListPlantilla.get(33)));
            etOxxoTiempoPuertaCerradaEntrarModoAhorro2.setText(dataListPlantilla.get(34));
            etOxxoDireccionModbus.setText(dataListPlantilla.get(35));
            //etOxxoNewPlantillaversion.setText(dataListPlantilla.get(35));
            //etOxxoactualFirmwareVersion.setText(dataListPlantilla.get(36));
            //etOxxoModelTrefp.setText(dataListPlantilla.get(37));

        }

    }

    private void setSwitchesData() {
        if (dataListPlantilla.size() == 1) {
            Toast.makeText(getContext(), "Realizar Hanshake primero", Toast.LENGTH_SHORT).show();
        } else {
            //Escala de temperatura
            Log.d("datalistplantilla",":"+dataListPlantilla);
            if (dataListPlantilla.get(23).equals("0"))
                switchOxxoEscalaTemperatura.setChecked(false);
            else if (dataListPlantilla.get(23).equals("1"))
                switchOxxoEscalaTemperatura.setChecked(true);

            //banderas de configuracion

            //Entrada en modo ahorrador
            if (dataListPlantilla.get(24).charAt(7) =='0'){
                switchOxxoModoAhorrador.setChecked(false);
            }else{
                switchOxxoModoAhorrador.setChecked(true);
            }

            //Deshielo al arranque
            if (dataListPlantilla.get(24).charAt(6) =='0'){
                switchOxxoOnOffdeshielo.setChecked(true);
            }else{
                switchOxxoOnOffdeshielo.setChecked(false);
            }

            //Pull down
            if (dataListPlantilla.get(24).charAt(5) =='0'){
                switchOxxoPullDown.setChecked(true);
            }else{
                switchOxxoPullDown.setChecked(false);
            }

            //Switch de puerta
            if (dataListPlantilla.get(24).charAt(4) =='1'){
                switchOxxoSwitchPuerta.setChecked(false);
            }else{
                switchOxxoSwitchPuerta.setChecked(true);
            }

            //set abreviado de parametros
            if (dataListPlantilla.get(24).charAt(3) =='0'){
                switchOxxoSetAbreviadoParametros.setChecked(false);
            }else{
                switchOxxoSetAbreviadoParametros.setChecked(true);
            }
        }
    }
    private void setSpinnersData() {
        if (dataListPlantilla.size() == 1) {
            Toast.makeText(getContext(), "Realizar Hanshake primero", Toast.LENGTH_SHORT).show();
        } else {


            if (dataListPlantilla.get(26).equals("0"))
                spinnerOxxoModosdeshielo.setSelection(0);
            else if (dataListPlantilla.get(26).equals("1"))
                spinnerOxxoModosdeshielo.setSelection(1);
            else if (dataListPlantilla.get(26).equals("2"))
                spinnerOxxoModosdeshielo.setSelection(2);

            if (dataListPlantilla.get(28).equals("0"))
                spinnerOxxoProteccionVoltaje.setSelection(0);
            else if (dataListPlantilla.get(28).equals("1"))
                spinnerOxxoProteccionVoltaje.setSelection(1);
            else if (dataListPlantilla.get(28).equals("2"))
                spinnerOxxoProteccionVoltaje.setSelection(2);



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
            List<String> arrayListInfo = new ArrayList<>(GetHexFromRealDataOxxo.getData(getList()));
            StringBuilder stringBuilder = new StringBuilder();
            for (String dato:arrayListInfo){
                stringBuilder.append(dato.toUpperCase());
            }
            //Log.d(TAG, stringBuilder.toString());
            Log.d(TAG, ":"+arrayListInfo);
            //new MyAsyncTaskSendNewPlantilla(stringBuilder).execute();
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

        }
        return "";
    }

    public String getOptionSwitch(String option,boolean ischecked){
        if (ischecked)
            return "00100000";
        else
            return "00000000";
    }

    public String getOptionSwitch(boolean modoAhorro, boolean deshielo,boolean pulldown, boolean switchPuerta, boolean set){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("000");
        if (set)
            stringBuilder.append("1");
        else
            stringBuilder.append("0");

        if (switchPuerta)
            stringBuilder.append("0");
        else
            stringBuilder.append("1");

        if (pulldown)
            stringBuilder.append("0");
        else
            stringBuilder.append("1");

        if (deshielo)
            stringBuilder.append("0");
        else
            stringBuilder.append("1");

        if (modoAhorro)
            stringBuilder.append("1");
        else
            stringBuilder.append("0");

        return stringBuilder.toString();
    }

    private void errorEditText(EditText editText,String condition){
        editText.setError(condition);
    }

    private List<EditText> checkCorrectData(){
        List<EditText> editTextsList = new ArrayList<>(getEditTextList());
        //falta agregar función
        List<EditText> editTextsWrongDataList = new ArrayList<>();
        for (int i =0 ; i<editTextsList.size(); i++){
            if (i==0 || i==3 ||  i==9 || i==4 || i==7 || i==8){//-99 a 99
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                if (numf<-99 || numf > 99){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }
            if (i==5 || i==6  ){//0.0 a 10.0
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                if (numf<-0.0 || numf > 10.0){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }
            if ( i == 10 || i == 11 || i == 12|| i == 13|| i == 14|| i == 15 || i == 16 || i == 17 || i == 18){//00 a 99
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

            if ( i==1 || i==21){//0.0 a 9.9
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                if (numf<-0.0 || numf > 9.9){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }


            if (i== 22 ||i== 23 || i==24|| i==25 || i==26){//0.0 a 25
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                if (numf<0 || numf > 25){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }

            }

            if (i==27){//0.0 a 256
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                if (numf<0 || numf > 255){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }

            }

            if (i==19){//0 a 09
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                int num = (int) numf;

                if (num<0 || num > 9){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }
            if (i==20){//0 a 08
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                int num = (int) numf;

                if (num<0 || num > 7){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
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
                FinalListData = GetRealDataFromHexaOxxo.convert(listData,"Lectura de parámetros de operación");
                dataListPlantilla = GetRealDataFromHexaOxxo.GetRealData(FinalListData,"Lectura de parámetros de operación");
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
                    listenermain.printExcel(getListToExcel(),"oxxo");
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
        editTextsList.add(etActOxxoDiferencialTempParaAhorro1);
        editTextsList.add(etActOxxoDiferencialTempParaAhorro2);
        editTextsList.add(etActOxxoTemperaturaEvaporadorTerminarDeshielo);
        editTextsList.add(etActOxxoTemperaturaAmbienteTerminarDeshielo);
        editTextsList.add(etActOxxoTemperaturaPulldown);
        editTextsList.add(etActOxxoVoltajeProteccionMinimo120);
        editTextsList.add(etOxxoVoltajeProteccionMaximo120minimo220);
        editTextsList.add(etOxxoVoltajeProteccionMaximo220);
        editTextsList.add(etOxxoTiempoValidarFallaVoltaje);
        editTextsList.add(etOxxoTiempoEntreDeshielos);
        editTextsList.add(etOxxoTiempoMaximoDeshielo);
        editTextsList.add(etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta);
        editTextsList.add(etOxxoTiempoValidacionAlarmaPuertaAbierta);
        editTextsList.add(etOxxoTiempoPermanenciaModoNocturno);
        editTextsList.add(etOxxoIntensidadFiltroParaSensorAmbienteALaSubida);
        editTextsList.add(etOxxoTiempoAdaptivoDeshielo);
        editTextsList.add(etOxxoHisteresisParaProteccionVoltaje);
        editTextsList.add(etOxxoTiempoValidacionSalirFallaVoltaje);
        editTextsList.add(etOxxoTiempoGoteo);
        editTextsList.add(etOxxoTiempoDescansoMinimoCompresor);
        editTextsList.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro1);
        editTextsList.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro2);
        editTextsList.add(etOxxoDireccionModbus);
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
        arrayListInfo.add(etActOxxoDiferencialTempParaAhorro1.getText().toString());
        arrayListInfo.add(etActOxxoDiferencialTempParaAhorro2.getText().toString());
        arrayListInfo.add(etActOxxoTemperaturaEvaporadorTerminarDeshielo.getText().toString());
        arrayListInfo.add(etActOxxoTemperaturaAmbienteTerminarDeshielo.getText().toString());
        arrayListInfo.add(etActOxxoTemperaturaPulldown.getText().toString());
        arrayListInfo.add(etActOxxoVoltajeProteccionMinimo120.getText().toString());
        arrayListInfo.add(etOxxoVoltajeProteccionMaximo120minimo220.getText().toString());
        arrayListInfo.add(etOxxoVoltajeProteccionMaximo220.getText().toString());
        arrayListInfo.add(etOxxoTiempoValidarFallaVoltaje.getText().toString());
        arrayListInfo.add(etOxxoTiempoEntreDeshielos.getText().toString());
        arrayListInfo.add(etOxxoTiempoMaximoDeshielo.getText().toString());
        arrayListInfo.add(etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta.getText().toString());
        arrayListInfo.add(etOxxoTiempoValidacionAlarmaPuertaAbierta.getText().toString());
        arrayListInfo.add(etOxxoTiempoPermanenciaModoNocturno.getText().toString());
        arrayListInfo.add(getOptionSwitch("escalaTemp",switchOxxoEscalaTemperatura.isChecked()));
        arrayListInfo.add(getOptionSwitch(switchOxxoModoAhorrador.isChecked(), switchOxxoOnOffdeshielo.isChecked(), switchOxxoPullDown.isChecked(), switchOxxoSwitchPuerta.isChecked(), switchOxxoSetAbreviadoParametros.isChecked()));
        arrayListInfo.add(etOxxoIntensidadFiltroParaSensorAmbienteALaSubida.getText().toString());
        arrayListInfo.add(getOptionSpinner("modosDeshielo",spinnerOxxoModosdeshielo.getSelectedItemPosition()));
        arrayListInfo.add(etOxxoTiempoAdaptivoDeshielo.getText().toString());
        arrayListInfo.add(getOptionSpinner("proteccionVoltaje",spinnerOxxoProteccionVoltaje.getSelectedItemPosition()));
        arrayListInfo.add(etOxxoHisteresisParaProteccionVoltaje.getText().toString());
        arrayListInfo.add(etOxxoTiempoValidacionSalirFallaVoltaje.getText().toString());
        arrayListInfo.add(etOxxoTiempoGoteo.getText().toString());
        arrayListInfo.add(etOxxoTiempoDescansoMinimoCompresor.getText().toString());
        arrayListInfo.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro1.getText().toString());
        arrayListInfo.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro2.getText().toString());
        arrayListInfo.add(etOxxoDireccionModbus.getText().toString());
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
        arrayListInfo.add(etActOxxoDiferencialTempParaAhorro1.getText().toString());
        arrayListInfo.add(etActOxxoDiferencialTempParaAhorro2.getText().toString());
        arrayListInfo.add(etActOxxoTemperaturaEvaporadorTerminarDeshielo.getText().toString());
        arrayListInfo.add(etActOxxoTemperaturaAmbienteTerminarDeshielo.getText().toString());
        arrayListInfo.add(etActOxxoTemperaturaPulldown.getText().toString());
        arrayListInfo.add(etActOxxoVoltajeProteccionMinimo120.getText().toString());
        arrayListInfo.add(etOxxoVoltajeProteccionMaximo120minimo220.getText().toString());
        arrayListInfo.add(etOxxoVoltajeProteccionMaximo220.getText().toString());
        arrayListInfo.add(etOxxoTiempoValidarFallaVoltaje.getText().toString());
        arrayListInfo.add(etOxxoTiempoEntreDeshielos.getText().toString());
        arrayListInfo.add(etOxxoTiempoMaximoDeshielo.getText().toString());
        arrayListInfo.add(etOxxoTiempoPuertaAbiertaControlVentiladorPorPuerta.getText().toString());
        arrayListInfo.add(etOxxoTiempoValidacionAlarmaPuertaAbierta.getText().toString());
        arrayListInfo.add(etOxxoTiempoPermanenciaModoNocturno.getText().toString());

        if (switchOxxoEscalaTemperatura.isChecked()){
            arrayListInfo.add("Fahrenheit");
        }else
            arrayListInfo.add("Celcius");

        StringBuilder stringBuilder = new StringBuilder();
        if (switchOxxoModoAhorrador.isChecked())
            stringBuilder.append("Entrada a Modo Ahorrador: Por botón y puerta - ");
        else
            stringBuilder.append("Entrada a Modo Ahorrador: Sólo por botón - ");

        if (switchOxxoOnOffdeshielo.isChecked())
            stringBuilder.append("Deshielo de arranque: Si - ");
        else
            stringBuilder.append("Deshielo de arranque: No - ");

        if (switchOxxoPullDown.isChecked())
            stringBuilder.append("Pull Down: Habilitado - ");
        else
            stringBuilder.append("Pull Down: Deshabilitado - ");

        if (switchOxxoSwitchPuerta.isChecked())
            stringBuilder.append("Switch de puerta: Normalmente abierto - ");
        else
            stringBuilder.append("Switch de puerta: Normalmente cerrado - ");

        if (switchOxxoSetAbreviadoParametros.isChecked())
            stringBuilder.append("Set abreviado de parámetros: Visible");
        else
            stringBuilder.append("Set abreviado de parámetros: No visible");

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
        arrayListInfo.add(getOptionSpinner("proteccionVoltaje",spinnerOxxoProteccionVoltaje.getSelectedItemPosition()));
        if (spinnerOxxoProteccionVoltaje.getSelectedItemPosition() == 0)
            arrayListInfo.add("Sin protección de voltaje");
        else if (spinnerOxxoProteccionVoltaje.getSelectedItemPosition() == 0)
            arrayListInfo.add("Protección a 120 Volts");
        else if (spinnerOxxoProteccionVoltaje.getSelectedItemPosition() == 0)
            arrayListInfo.add("Protección a 220 Volts");
        arrayListInfo.add(etOxxoHisteresisParaProteccionVoltaje.getText().toString());
        arrayListInfo.add(etOxxoTiempoValidacionSalirFallaVoltaje.getText().toString());
        arrayListInfo.add(etOxxoTiempoGoteo.getText().toString());
        arrayListInfo.add(etOxxoTiempoDescansoMinimoCompresor.getText().toString());
        arrayListInfo.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro1.getText().toString());
        arrayListInfo.add(etOxxoTiempoPuertaCerradaEntrarModoAhorro2.getText().toString());
        arrayListInfo.add(etOxxoDireccionModbus.getText().toString());
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
