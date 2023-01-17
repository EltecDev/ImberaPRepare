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

import com.example.imberap.Utility.GetHexFromRealDataRutaFria;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import com.example.imberap.BluetoothServices.BluetoothLeService;
import com.example.imberap.BluetoothServices.BluetoothServices;
import com.example.imberap.R;
import com.example.imberap.Utility.GetHexFromRealDataImbera;
import com.example.imberap.Utility.GetRealDataFromHexRutaFria;


public class PlantillaFragmentRutaFria extends Fragment {
    String TAG="PlantillaFragment";
    //listener listenermain;

    SharedPreferences sp;
    SharedPreferences.Editor esp;

    List<TextView> tvList = new ArrayList<TextView>();
    List<EditText> tvListEditText = new ArrayList<EditText>();

    List<String> FinalListDataRealState = new ArrayList<String>();
    List<String> FinalListDataHandshake = new ArrayList<String>();
    List<String> FinalListDataTiempo = new ArrayList<String>();
    List<String> FinalListDataEvento = new ArrayList<String>();
    List<String> FinalListDataPlantilla = new ArrayList<String>();

    ScrollView scrollView;
    Button  btngetPlantilla, btnsendPlantilla;
    SwitchMaterial switchC0 ,switchC1;

    TextView tvT0,tvT1,tvT2,tvT3,tvT4,tvL6,tvL7,tvL8,tvF3,tvF6,tvF7;

    EditText etT0,etT1,etT2,etT3,etT4,etL6,etL7,etL8,etF3,etF6,etF7,etFW,etModelo, etPlantilla;

    Spinner spinnerC8, spinnerC2;
    TextView tvPlantillatituloTREFP ;
    Button btnGetPlantilla,btnenviarfwOperadores;
    BluetoothServices bluetoothServices;
    BluetoothLeService bluetoothLeService;

    androidx.appcompat.app.AlertDialog progressdialog = null;
    View dialogViewProgressBar;

    List<String> dataListPlantilla = new ArrayList<String>();
    Context context;
    public PlantillaFragmentRutaFria(){}

    public PlantillaFragmentRutaFria(BluetoothServices bluetoothServices, Context context) {
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
        View view = inflater.inflate(R.layout.fragment_plantilla_rutafria, container, false);
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

        /*view.findViewById(R.id.btnSendPlantillaObtenida).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPlantillaActual();
            }
        });*/
        /*view.findViewById(R.id.btnSendInfoCrudo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInfoCrudoExcel();
            }
        });*/


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //actualizarUIJerarquia();
    }

    public void actualizarUIJerarquia(){
        if (!sp.getString("userId","").equals("")){//si no hay usuario logeado entonces no escanear
            switch (sp.getString("userjerarquia","")){
                case "1":{
                    tvPlantillatituloTREFP.setText("Aquí puedes editar plantillas y mandarlas como parámetros a tu dispositivo IMBERA-OXXO");
                    scrollView.setVisibility(View.VISIBLE);
                    btnenviarfwOperadores.setVisibility(View.GONE);
                    btnGetPlantilla.setVisibility(View.VISIBLE);
                    //btnenviarPlantillaOperadores.setVisibility(View.GONE);
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
                    tvPlantillatituloTREFP.setText("Aquí puedes editar plantillas y mandarlas como parámetros a tu dispositivo IMBERA-OXXO");
                    scrollView.setVisibility(View.GONE);
                    btnGetPlantilla.setVisibility(View.GONE);
                    //btnenviarPlantillaOperadores.setVisibility(View.GONE);
                    break;
                }
                case "5":{
                    tvPlantillatituloTREFP.setText("Usa los botones de abajo para proceder a actualizar el equipo TREFPB");
                    btnenviarfwOperadores.setVisibility(View.VISIBLE);
                    //btnenviarPlantillaOperadores.setVisibility(View.VISIBLE);
                    scrollView.setVisibility(View.GONE);
                    btnGetPlantilla.setVisibility(View.GONE);
                    btnsendPlantilla.setVisibility(View.GONE);
                    break;
                }
                case "6":{
                    tvPlantillatituloTREFP.setText("Usa los botones de abajo para proceder a actualizar el equipo TREFPB");
                    btnenviarfwOperadores.setVisibility(View.VISIBLE);
                    //btnenviarPlantillaOperadores.setVisibility(View.VISIBLE);
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
        //sharedViewModel = new ViewModelProvider(requireActivity()).get(StatusViewModel.class);
        initCampos(v);
    }

    private void initCampos(View view) {
        scrollView = view.findViewById(R.id.scrollPlantilla);

        btnGetPlantilla = view.findViewById(R.id.btnGetPlantilla);
        btnsendPlantilla = view.findViewById(R.id.btnSendPlantilla);

        tvT0 = view.findViewById(R.id.tvT0);
        tvT1 = view.findViewById(R.id.tvT1);
        tvT2 = view.findViewById(R.id.tvT2);
        tvT3 = view.findViewById(R.id.tvT3);
        tvT4 = view.findViewById(R.id.tvT4);
        tvL6 = view.findViewById(R.id.tvL6);
        tvL7 = view.findViewById(R.id.tvL7);
        tvL8 = view.findViewById(R.id.tvL8);
        tvF3 = view.findViewById(R.id.tvF3);
        tvF6 = view.findViewById(R.id.tvF6);
        tvF7 = view.findViewById(R.id.tvF7);


        tvList.add(tvT0);
        tvList.add(tvT1);
        tvList.add(tvT2);
        tvList.add(tvT3);
        tvList.add(tvT4);
        tvList.add(tvL6);
        tvList.add(tvL7);
        tvList.add(tvL8);
        tvList.add(tvF3);
        tvList.add(tvF6);
        tvList.add(tvF7);


        etT0 = view.findViewById(R.id.etT0);
        etT1 = view.findViewById(R.id.etT1);
        etT2 = view.findViewById(R.id.etT2);
        etT3 = view.findViewById(R.id.etT3);
        etT4 = view.findViewById(R.id.etT4);
        etL6 = view.findViewById(R.id.etL6);
        etL7 = view.findViewById(R.id.etL7);
        etL8 = view.findViewById(R.id.etL8);
        etF3 = view.findViewById(R.id.etF3);
        etF6 = view.findViewById(R.id.etF6);
        etF7 = view.findViewById(R.id.etF7);
        etFW = view.findViewById(R.id.etFW);
        etModelo = view.findViewById(R.id.etModelo);
        etPlantilla = view.findViewById(R.id.etPlantilla);

        tvListEditText.add(etT0);
        tvListEditText.add(etT1);
        tvListEditText.add(etT2);
        tvListEditText.add(etT3);
        tvListEditText.add(etT4);
        tvListEditText.add(etL6);
        tvListEditText.add(etL7);
        tvListEditText.add(etL8);
        tvListEditText.add(etF3);
        tvListEditText.add(etF6);
        tvListEditText.add(etF7);
        tvListEditText.add(etFW);
        tvListEditText.add(etModelo);
        tvListEditText.add(etPlantilla);


        //Declaraciòn de swiches
        switchC0 = view.findViewById(R.id.switchC0);
        switchC1 = view.findViewById(R.id.switchC1);

        //declaracion de spinners
        spinnerC8 = view.findViewById(R.id.spinnerC8);
        spinnerC2 = view.findViewById(R.id.spinnerC2);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(view.getContext(),
                R.array.stringArrayC8, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerC8.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(view.getContext(),
                R.array.stringArrayC2, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerC2.setAdapter(adapter3);

    }

    public void getHexDataFromFields(){
        /*float numf = Float.parseFloat(etTCOffsetDeSensor.getText().toString());
        int num = (int) numf;

        if (numf>0){
            hexTC = convertDecimalToHexa(etTCOffsetDeSensor.getText().toString()).toUpperCase(); //decimales con punto //get temp positivo
        }else if (numf<0){
            hexTC = getNeg(Integer.parseInt(String.valueOf(num))); //get negativos
        }else if(numf==0.0){//Es 0 cero
            hexTC = "0000"; //get negativos
        }

        numf = Float.parseFloat(etA6AlarmaTemperaturaAlta.getText().toString());
        num = (int) numf;
        if (Integer.signum(num)==1){
            hexA6 = convertDecimalToHexa(etA6AlarmaTemperaturaAlta.getText().toString()).toUpperCase(); //decimales con punto //get temp positivo
        }else if (Integer.signum(num)==-1){
            hexA6 = getNeg(Integer.parseInt(String.valueOf(num))); //get negativos
        }else{//Es 0 cero
            hexA6 = "0000"; //get negativos
        }

        numf = Float.parseFloat(etA7AlarmaTemperaturaBaja.getText().toString());
        num = (int) numf;
        if (Integer.signum(num)==1){
            hexA7 = convertDecimalToHexa(etA7AlarmaTemperaturaBaja.getText().toString()).toUpperCase(); //decimales con punto //get temp positivo
        }else if (Integer.signum(num)==-1){
            hexA7 = getNeg(Integer.parseInt(String.valueOf(num))); //get negativos
        }else{//Es 0 cero
            hexA7 = "0000"; //get negativos
        }

        numf = Float.parseFloat(etA8DiferencialTemperatura.getText().toString());
        num = (int) numf;
        if (Integer.signum(num)==1){
            hexA8 = convertDecimalToHexa(etA8DiferencialTemperatura.getText().toString()).toUpperCase(); //decimales con punto //get temp positivo
        }else if (Integer.signum(num)==-1){
            hexA8 = getNeg(Integer.parseInt(String.valueOf(num))); //get negativos
        }else{//Es 0 cero
            hexA8 = "0000"; //get negativos
        }

        hexLC = GetHexFromRealDataImbera.convertDecimalIntToHexa(etLCTiempoRetardoInicial.getText().toString());
        hexFC = GetHexFromRealDataOxxoDisplay.convertDecimalToHexa8(etFCTiemposilencioAlarma.getText().toString());
        Log.d("asdasdasd",":"+hexLC);
        Log.d("asdasdasdasdd",":"+hexFC);*/
    }
    public void createCommandtoSend(){
        getHexDataFromFields();

        /*nuevaPlantillaEnviar = null;
        nuevaPlantillaEnviar = new StringBuilder();
        nuevaPlantillaEnviar.append("4050AA");
        nuevaPlantillaEnviar.append(plantillaDefault.substring(0,espacioTC));

        nuevaPlantillaEnviar.append(hexTC);
        nuevaPlantillaEnviar.append(plantillaDefault.substring(espacioTC+4,espacioA6));

        nuevaPlantillaEnviar.append(hexA6);
        nuevaPlantillaEnviar.append(hexA7);
        nuevaPlantillaEnviar.append(hexA8);

        nuevaPlantillaEnviar.append(plantillaDefault.substring(espacioA8+4,espacioLC));
        nuevaPlantillaEnviar.append(hexLC);

        nuevaPlantillaEnviar.append(plantillaDefault.substring(espacioLC+2,espacioC1));
        nuevaPlantillaEnviar.append("00");
        nuevaPlantillaEnviar.append(plantillaDefault.substring(espacioC1+2, espacioC1+10));
        nuevaPlantillaEnviar.append("01");

        nuevaPlantillaEnviar.append(plantillaDefault.substring(espacioC1+12,espacioFC));
        nuevaPlantillaEnviar.append(hexFC);

        //sin rienicio
        nuevaPlantillaEnviar.append(plantillaDefault.substring(espacioFC+2,plantillaDefault.length()-8));

        //se obtiene el CKSM
        String check = GetHexFromRealDataImbera.calculateChacksumString(nuevaPlantillaEnviar.toString());
        nuevaPlantillaEnviar.append(check);
        Log.d(TAG,"Plantilla A ENVIAR:"+nuevaPlantillaEnviar);
        new MyAsyncTaskSendNewPlantilla(nuevaPlantillaEnviar).execute();*/
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
            tvT0.setText(dataListPlantilla.get(4));
            tvT1.setText(dataListPlantilla.get(5));
            tvT2.setText(dataListPlantilla.get(6));
            tvT3.setText(dataListPlantilla.get(7));
            tvT4.setText(dataListPlantilla.get(8));
            tvL6.setText(dataListPlantilla.get(9));
            tvL7.setText(dataListPlantilla.get(10));
            tvL8.setText(dataListPlantilla.get(11));
            tvF3.setText(dataListPlantilla.get(16));
            tvF6.setText(dataListPlantilla.get(17));
            tvF7.setText(dataListPlantilla.get(18));

            etT0.setText(dataListPlantilla.get(4));
            etT1.setText(dataListPlantilla.get(5));
            etT2.setText(dataListPlantilla.get(6));
            etT3.setText(dataListPlantilla.get(7));
            etT4.setText(dataListPlantilla.get(8));
            etL6.setText(dataListPlantilla.get(9));
            etL7.setText(dataListPlantilla.get(10));
            etL8.setText(dataListPlantilla.get(11));
            etF3.setText(dataListPlantilla.get(16));
            etF6.setText(dataListPlantilla.get(17));
            etF7.setText(dataListPlantilla.get(18));

            etModelo.setText(dataListPlantilla.get(19));
            String fw = dataListPlantilla.get(20)+dataListPlantilla.get(21);
            fw = fw.replace(".","");
            fw = fw.substring(0,2)+"."+fw.substring(2);
            etFW.setText(fw);
            etPlantilla.setText(dataListPlantilla.get(22));
        }

    }
    private void setSwitchesData() {
        if (dataListPlantilla.size() == 1) {
            Toast.makeText(getContext(), "Realizar Hanshake primero", Toast.LENGTH_SHORT).show();
        } else {
            if (dataListPlantilla.get(12).equals("0"))
                switchC0.setChecked(false);
            else if (dataListPlantilla.get(12).equals("1"))
                switchC0.setChecked(true);

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
            /*if (dataListPlantilla.get(33).charAt(6) =='0'){
                switchC1.setChecked(true);
            }else{
                switchC1.setChecked(false);
            }*/

            //Pull down
            /*
            if (dataListPlantilla.get(27).charAt(5) =='0'){
                switchOxxoPullDown.setChecked(true);
            }else{
                switchOxxoPullDown.setChecked(false);
            }


             */
            //Switch de puerta
            if (dataListPlantilla.get(13).charAt(4) =='1'){
                switchC1.setChecked(false);
            }else{
                switchC1.setChecked(true);
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
            if (dataListPlantilla.get(14).equals("1"))
                spinnerC2.setSelection(0);
            else if (dataListPlantilla.get(14).equals("2"))
                spinnerC2.setSelection(1);
            else if (dataListPlantilla.get(14).equals("3"))
                spinnerC2.setSelection(2);
            else if (dataListPlantilla.get(14).equals("4"))
                spinnerC2.setSelection(3);


            if (dataListPlantilla.get(15).equals("0"))
                spinnerC8.setSelection(0);
            else if (dataListPlantilla.get(15).equals("1"))
                spinnerC8.setSelection(1);
            if (dataListPlantilla.get(15).equals("2"))
                spinnerC8.setSelection(2);
            else if (dataListPlantilla.get(15).equals("3"))
                spinnerC8.setSelection(3);
            if (dataListPlantilla.get(15).equals("4"))
                spinnerC8.setSelection(4);
            else if (dataListPlantilla.get(15).equals("5"))
                spinnerC8.setSelection(5);
            if (dataListPlantilla.get(15).equals("6"))
                spinnerC8.setSelection(6);
            else if (dataListPlantilla.get(15).equals("7"))
                spinnerC8.setSelection(7);
            if (dataListPlantilla.get(15).equals("8"))
                spinnerC8.setSelection(8);
            else if (dataListPlantilla.get(15).equals("9"))
                spinnerC8.setSelection(9);



        }
    }

    private void sendDataPlantilla() {
        bluetoothLeService = bluetoothServices.getBluetoothLeService();

        if (bluetoothServices != null && bluetoothLeService != null){
            //if (checkEmptydata()) {
                sortFromDataToHexa();
           // }else{
                //Toast.makeText(getContext(), "Debes llenar todos los datos", Toast.LENGTH_SHORT).show();
            //}
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
            List<String> arrayListInfo = new ArrayList<>(GetHexFromRealDataRutaFria.getData(getList(),sp.getString("plantilla","")));
            StringBuilder stringBuilder = new StringBuilder();
            for (String dato:arrayListInfo){
                stringBuilder.append(dato.toUpperCase());
            }
            Log.d("a<sd",":"+stringBuilder.toString());

            new MyAsyncTaskSendNewPlantilla(stringBuilder).execute();//se envian los datos al TREFP
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
        switch (option){
            case "C2":{
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
            case "C8":{
                if (selected==1)
                    return "00000011";
                else if (selected==0)
                    return "00000001";
                else if (selected==2)
                    return "00000111";
                else if (selected==3)
                    return "00001111";
                break;
            }

        }
        return "";
    }

    public String getOptionSwitch(String option,boolean ischecked){
        if ("switchpuerta".equals(option)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("000");
            stringBuilder.append("0");
            /*if (set)
                stringBuilder.append("1");
            else
                stringBuilder.append("0");
    */
            if (ischecked)
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
            /*if (deshielo)
                stringBuilder.append("0");
            else
                stringBuilder.append("1");
                */
            stringBuilder.append("0");
            /*
                    if (modoAhorro)
                        stringBuilder.append("1");
                    else
                        stringBuilder.append("0");
            */
            stringBuilder.append("0");
            Log.d("banderas",":"+stringBuilder);
            return stringBuilder.toString();
        }

        if ("escalaTemp".equals(option)){
            if (ischecked)
                return "00100000";
            else
                return "00000000";
        }

        return "";
    }



    private void errorEditText(EditText editText,String condition){
        editText.setError(condition);
    }

    private List<EditText> checkCorrectData(){
        List<EditText> editTextsList = new ArrayList<>(getEditTextList());
        //falta agregar función
        List<EditText> editTextsWrongDataList = new ArrayList<>();
        float numA0 = Float.parseFloat(editTextsList.get(8).getText().toString());
        float numT0 = Float.parseFloat(editTextsList.get(0).getText().toString());
        if(numA0 > numT0){
            errorEditText(editTextsList.get(8), "A0 siempre debe ser menor a T0");
            editTextsWrongDataList.add(editTextsList.get(8));
        }
        for (int i =0 ; i<editTextsList.size(); i++){
            if (i==0 || i==2 ||  i==3 ){//-99 a 99
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                if (numf<-99 || numf > 99){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }
            if (i==1|| i==4){//0.5 a 9.9
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                if (numf<-0.5 || numf > 9.9){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }

            if (i==5 || i == 6){//00 a 99
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                int num = (int) numf;
                if (num<-0 || num > 99){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }

            if ( i==7 || i==9 || i==10 ){//00 a 250
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                int num = (int) numf;
                if (num<-0 || num > 250){
                    errorEditText(editTextsList.get(i), "Fuera de límites");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }

            if (i== 8  ){//0.0 a 25
                float numf = Float.parseFloat(editTextsList.get(i).getText().toString());
                /*if(i==23 || i==24){
                    if (numf == 0.0 || numf == 0){
                        editTextsWrongDataList.add(editTextsList.get(i));
                        errorEditText(editTextsList.get(i), "Este campo no puede ser 0");
                    }else if(numf < 0.1 || numf > 25.0 || numf > 25){
                        editTextsWrongDataList.add(editTextsList.get(i));
                        errorEditText(editTextsList.get(i), "Fuera de límites");
                    }

                }else{*/
                    if (numf<-0.0 || numf > 25.0){
                        errorEditText(editTextsList.get(i), "Fuera de límites");
                        editTextsWrongDataList.add(editTextsList.get(i));
                    }
                //}
            }


            //firmware correcto
            if (i==12){
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
            if (i==11){

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
                        errorEditText(editTextsList.get(i), "El modelo debe coincidir con el del equipo.");
                        editTextsWrongDataList.add(editTextsList.get(i));
                    }
                }else{
                    errorEditText(editTextsList.get(i), "Verifica el formato del dato Firmware,debe llevar decimal");
                    editTextsWrongDataList.add(editTextsList.get(i));
                }
            }

            //plantilla formato correcto
            if (i==13){

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
                StringBuilder s = GetRealDataFromHexRutaFria.cleanSpace(listData);
                esp.putString("plantilla",s.toString());
                esp.apply();
                FinalListData = GetRealDataFromHexRutaFria.convert(listData,"Lectura de parámetros de operación",sp.getString("numversion",""), sp.getString("modelo",""));
                dataListPlantilla = GetRealDataFromHexRutaFria.GetRealData(FinalListData,"Lectura de parámetros de operación",sp.getString("numversion",""), sp.getString("modelo",""));
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
                    //listenermain.printExcel(getOriginalList(),"imbera");
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
            createProgressDialog("Actualizando a nueva plantilla...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    /*class MyAsyncTaskGetActualStatusTotalCrudo extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            obtenerInfoTotalCrudo();
            return "resp";
        }

        @Override
        protected void onPostExecute(String result) {
            if (progressdialog != null)progressdialog.dismiss();
            if (sp.getString("modelo","").equals("3.3") && sp.getString("numversion","").equals("1.02")){
                List<String> fin = new ArrayList<String>();
                fin.add(FinalListDataHandshake.get(0));
                fin.add(FinalListDataRealState.get(0));
                fin.add(FinalListDataPlantilla.get(0));

                listenermain.printExcelCrudoExtendido(fin,FinalListDataEvento,FinalListDataTiempo);
                progressdialog=null;
            }else if (sp.getString("modelo","").equals("3.5") && sp.getString("numversion","").equals("1.04")){
                List<String> fin = new ArrayList<String>();
                Log.d("LOGG1",":"+FinalListDataHandshake.get(0));
                Log.d("LOGG2",":"+FinalListDataRealState.get(0));
                Log.d("LOGG3",":"+FinalListDataPlantilla.get(0));
                fin.add(FinalListDataHandshake.get(0));
                fin.add(FinalListDataRealState.get(0));
                fin.add(FinalListDataPlantilla.get(0));

                listenermain.printExcelCrudoExtendido(fin,FinalListDataEvento,FinalListDataTiempo);
                progressdialog=null;
            }else{
                List<String> fin = new ArrayList<String>();
                fin.add(FinalListDataHandshake.get(0));
                fin.add(FinalListDataRealState.get(0));
                fin.add(FinalListDataTiempo.get(0));
                fin.add(FinalListDataEvento.get(0));
                fin.add(FinalListDataPlantilla.get(0));

                listenermain.printExcel(fin,"crudototalImbera");
                progressdialog=null;
            }


        }

        @Override
        protected void onPreExecute() {
            FinalListDataHandshake.clear();
            FinalListDataTiempo.clear();
            FinalListDataEvento.clear();
            FinalListDataRealState.clear();
            FinalListDataPlantilla.clear();
            createProgressDialog("Obteniendo estado actual...");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }*/

    private void obtenerInfoTotalCrudo(){
        List<String> FinalListData = new ArrayList<String>() ;
        List<String> listData = new ArrayList<String>() ;
        List<String> FinalListTest = new ArrayList<String>() ;
        String isChecksumOk;

        FinalListDataRealState.clear();
        bluetoothLeService = bluetoothServices.getBluetoothLeService();
        try {
            bluetoothServices.sendCommand("handshake","4021");
            Thread.sleep(250);
            FinalListDataHandshake.add(bluetoothLeService.getDataFromBroadcastUpdateString());

            if (sp.getString("modelo","").equals("3.3") && sp.getString("numversion","").equals("1.02")){
                FinalListData.clear();
                listData.clear();
                bluetoothServices.sendCommand("time","4060");
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(0));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(1));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(2));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(3));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(4));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(5));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(6));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(7));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(8));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(9));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(7));


                FinalListTest.clear();
                FinalListData.clear();
                FinalListDataEvento.clear();
                bluetoothServices.sendCommand("event","4061");
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(0));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(1));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(2));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(3));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(4));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(5));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(6));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(7));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(8));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(9));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(10));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(11));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(12));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(13));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(14));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(15));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(16));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(17));


            }else if ((sp.getString("modelo","").equals("3.5") && sp.getString("numversion","").equals("1.04")) ||
                    (sp.getString("modelo","").equals("3.5") && sp.getString("numversion","").equals("1.04"))){
                FinalListData.clear();
                listData.clear();
                bluetoothServices.sendCommand("time","4060");
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(0));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(1));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(2));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(3));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(4));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(5));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(6));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(7));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(8));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(9));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(10));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(11));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(12));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(13));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(14));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(15));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(16));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(17));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(18));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(19));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(20));
                Thread.sleep(700);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataTiempo.get(21));

                FinalListTest.clear();
                FinalListData.clear();
                FinalListDataEvento.clear();
                bluetoothServices.sendCommand("event","4061");
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(0));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(1));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(2));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(3));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(4));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(5));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(6));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(7));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(8));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(9));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(10));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(11));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(12));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(13));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(14));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(15));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(16));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(17));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(18));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(19));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(20));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(21));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(22));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(23));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(24));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(25));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(26));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(27));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(28));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(29));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(30));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(31));
                Thread.sleep(700);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());
                Log.d("islistttt",":"+FinalListDataEvento.get(32));



            }else{
                //if modelo actual es menor a 3.3 en adelante, entonces mostrar de forma de nuevo logger
                listData.clear();
                FinalListDataTiempo.clear();
                bluetoothLeService = bluetoothServices.getBluetoothLeService();
                bluetoothServices.sendCommand("time","4060");
                Thread.sleep(3050);
                FinalListDataTiempo.add(bluetoothLeService.getDataFromBroadcastUpdateString());


                listData.clear();
                FinalListData.clear();
                FinalListDataEvento.clear();
                bluetoothLeService = bluetoothServices.getBluetoothLeService();
                bluetoothServices.sendCommand("event","4061");
                Thread.sleep(3000);
                FinalListDataEvento.add(bluetoothLeService.getDataFromBroadcastUpdateString());

            }

            bluetoothServices.sendCommand("realState","4053");
            Thread.sleep(250);
            FinalListDataRealState.add(bluetoothLeService.getDataFromBroadcastUpdateString());

            bluetoothServices.sendCommand("readParam","4051");
            Thread.sleep(350);
            FinalListDataPlantilla.add(bluetoothLeService.getDataFromBroadcastUpdateString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updatefwOperador() {//firmware dado para únicamente grabar, dado desde Eltec (o alto cargo en TREFP PC)
        bluetoothServices.sendCommand("newfwTrefpResetMemory");
    }
    //utilidad
    /*public List<String> getOriginalListToExcel(){
        List<String> arrayListInfo = new ArrayList<>();
        arrayListInfo.add(tvT0.getText().toString());
        arrayListInfo.add(tvT1.getText().toString());
        arrayListInfo.add(tvT2.getText().toString());
        arrayListInfo.add(tvT3.getText().toString());
        arrayListInfo.add(tvT4.getText().toString());
        arrayListInfo.add(tvL6.getText().toString());
        arrayListInfo.add(tvL7.getText().toString());
        arrayListInfo.add(tvL8.getText().toString());

        if (switchC0.isChecked())
            arrayListInfo.add("Celcius");
        else
            arrayListInfo.add("Faranjeit");

        if (switchC1.isChecked())
            arrayListInfo.add("Puerta habilitada");
        else
            arrayListInfo.add("Puerta deshabilitada");

        if (switchC0.isChecked())
            arrayListInfo.add("Apagar ventilador con compresor apagado: Habilitado");
        else
            arrayListInfo.add("Apagar ventilador con compresor apagado: Deshabilitado");




        arrayListInfo.add(etFW.getText().toString());
        arrayListInfo.add(etModelo.getText().toString());
        arrayListInfo.add(etPlantilla.getText().toString());
        return arrayListInfo;
    }*/
    private List<EditText> getEditTextList(){
        List<EditText> editTextsList = new ArrayList<>();
        editTextsList.add(etT0);
        editTextsList.add(etT1);
        editTextsList.add(etT2);
        editTextsList.add(etT3);
        editTextsList.add(etT4);
        editTextsList.add(etL6);
        editTextsList.add(etL7);
        editTextsList.add(etL8);
        editTextsList.add(etF3);
        editTextsList.add(etF6);
        editTextsList.add(etF7);
        editTextsList.add(etModelo);
        editTextsList.add(etFW);
        editTextsList.add(etPlantilla);
        return editTextsList;
    }
    public List<String> getList(){
        List<String> arrayListInfo = new ArrayList<>();
        arrayListInfo.add(etT0.getText().toString());
        arrayListInfo.add(etT1.getText().toString());
        arrayListInfo.add(etT2.getText().toString());
        arrayListInfo.add(etT3.getText().toString());
        arrayListInfo.add(etT4.getText().toString());
        arrayListInfo.add(etL6.getText().toString());
        arrayListInfo.add(etL7.getText().toString());
        arrayListInfo.add(etL8.getText().toString());

        arrayListInfo.add(getOptionSwitch("escalaTemp",switchC0.isChecked()));
        arrayListInfo.add(getOptionSwitch("switchpuerta",switchC1.isChecked()));
        arrayListInfo.add(getOptionSpinner("C2",spinnerC2.getSelectedItemPosition()));
        arrayListInfo.add(getOptionSpinner("C8",spinnerC8.getSelectedItemPosition()));

        arrayListInfo.add(etF3.getText().toString());
        arrayListInfo.add(etF6.getText().toString());
        arrayListInfo.add(etF7.getText().toString());
        arrayListInfo.add(etModelo.getText().toString());
        arrayListInfo.add(etFW.getText().toString());
        arrayListInfo.add(etPlantilla.getText().toString());
        return arrayListInfo;
    }



    /*public interface listener {
        public void printExcel(List<String> data,String deviceName);
        public void printExcelCrudoExtendido(List<String> data,List<String> evento,List<String> tiempo);

    }
    //Listeners
    public void setListener(listener callback) {
        this.listenermain = callback;
    }*/

}