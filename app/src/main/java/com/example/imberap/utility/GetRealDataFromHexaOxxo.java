package com.example.imberap.utility;

import android.util.Log;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GetRealDataFromHexaOxxo {
    static List<String> arrayListInfo = new ArrayList<>();
    final static String TAG = "GetRealDataFromHexa";
    public static List<String> convert(List<String> arrayLists, String action){
        arrayListInfo.clear();
        switch (action){
            case "Handshake":{
                if (!arrayLists.isEmpty()) {
                    StringBuilder s = cleanSpace(arrayLists);
                    //header
                    arrayListInfo.add(s.substring(0, 4));//head
                    arrayListInfo.add(s.substring(4, 28));//Mac

                    //data
                    arrayListInfo.add(s.substring(28, 30));//modelo trefpb
                    arrayListInfo.add(s.substring(30, 34));//version
                    arrayListInfo.add(s.substring(34, 38));//plantilla

                    arrayListInfo.add(s.substring(38, 42));//checklist

                    arrayListInfo.add(s.substring(42));//checksum
                    //checksum
                    //arrayListInfo.add(s.substring(46,54));
                }
                break;
            }
            case "Lectura de parámetros de operación":{
                int i =18;//+2 para salta el AA
                StringBuilder s = cleanSpace(arrayLists);
                Log.d(TAG,"S:"+s);
                //header
                if (!arrayLists.isEmpty()){
                    arrayListInfo.add(s.substring(0, 4));//software version
                    arrayListInfo.add(s.substring(4, 12));//buffer_size
                    arrayListInfo.add(s.substring(12, 14));//data_type
                    arrayListInfo.add(s.substring(14, 16));//data_size
                    do {
                        if (i==30){//saltar posiciones por parámetros que no se están usando
                            i = i+12;
                        }else if (i==50){
                            i = i+20;
                        }else if (i==78){
                            i = i+4;
                        }else if (i==90){
                            i = i+8;
                        }else {
                            arrayListInfo.add(s.substring(i, i+4));
                            i = i + 4;
                        }
                    } while (i < 102);

                    //+48 para saltar posiciones sin usar y el dato 0x66
                    i = 148;
                    do {
                        if (i==164){//saltar posiciones por parámetros que no se están usando
                            i = i+2;
                        }else if (i==168){//saltar posiciones por parámetros que no se están usando
                            i = i+12;
                        }else if (i==192){
                            i = i+20;
                        }else if (i==220){
                            i = i+18;
                        }else if (i==242){
                            i = i+2;
                        }else{
                            arrayListInfo.add(s.substring(i, i+2));
                            i = i + 2;
                        }
                    } while (i < 246);

                    arrayListInfo.add(s.substring(s.length()-14,s.length()-10));//dato final "Plantilla"
                    arrayListInfo.add(s.substring(s.length()-8));//checksum
                }
                return arrayListInfo;
            }
            case "Lectura de datos tipo Tiempo real":{
                if (!arrayLists.isEmpty()){
                    StringBuilder s = cleanSpace(arrayLists);
                    //head
                    arrayListInfo.add(s.substring(0,4));//head
                    arrayListInfo.add(s.substring(4,12));//
                    arrayListInfo.add(s.substring(12,14));//modelo trefpb
                    arrayListInfo.add(s.substring(14,16));//version

                    arrayListInfo.add(s.substring(16,20));//temp1
                    arrayListInfo.add(s.substring(20,24));//temp2
                    arrayListInfo.add(s.substring(24,26));//voltaje
                    arrayListInfo.add(s.substring(26,28));//actuadores
                    arrayListInfo.add(s.substring(28,32));//alarmas
                    //arrayListInfo.add(s.substring(28,32));//plantilla
                    //arrayListInfo.add(s.substring(34));//checksum
                }
                break;
            }
            case "Actualizar a Firmware Original":{
                if (!arrayLists.isEmpty()){
                    StringBuilder s = cleanSpace(arrayLists);
                    //header
                    arrayListInfo.add(s.toString());//head
                }
                break;
            }
            case "Actualizar a Firmware Personalizado":{
                if (!arrayLists.isEmpty()){
                    StringBuilder s = cleanSpace(arrayLists);
                    //header
                    arrayListInfo.add(s.toString());//head
                }
                break;
            }
        }
        return arrayListInfo;
    }

    public static List<String> GetRealData(List<String> data, String action){
        //USO SOLO DE LOS DATOS BUFFER IMPORTANTES PARA MOSTRARLOS EN PANTALLA, LAS POSICIONES RESTANTES (HEADER) SON CORRECTAS
        switch (action){
            case "Handshake":{
                List<String> newData = new ArrayList<>();
                if(data.isEmpty()){
                    //newData.add(getSameData(data.get(0),"trefpversion"));
                    newData.add("nullHandshake");
                }else {
                    //header
                    //newData.add(data.get(0)); head
                    newData.add(hexToAscii(data.get(1)));
                    newData.add(getSameData(data.get(2), action));
                    newData.add(getSameData(data.get(3), "trefpversion"));
                    newData.add(String.valueOf(getDecimalFloat(data.get(4)) )); // decimales con punto
                }
                return newData;
            }
            case "Lectura de parámetros de operación":{
                List<String> newData = new ArrayList<>();
                if(data.isEmpty()){
                    //newData.add(getSameData(data.get(0),"trefpversion"));
                    newData.add("nullHandshake");
                }else {
                    //header
                    Log.d(TAG,"arreglo:"+data+ " size:"+data.size());
                    newData.add(getSameData(data.get(0), "trefpversion"));
                    newData.add(getSameData(data.get(1), action));
                    newData.add(String.valueOf(getDecimal(data.get(2))));
                    newData.add(String.valueOf(getDecimal(data.get(3))));

                    //buffer
                    int i = 4;
                    do {
                        if (i==14 ||i==15 ||i==16 ||i==17 ||i==18 ||i==19 || i==20|| i==21 ||i==22  || i==25|| i ==27 || i==35){
                            newData.add(String.valueOf(getDecimal(data.get(i))));//decimales sin punto
                        }else {
                            if (i==4|| i==6 || i==7 || i==8 || i==12 || i==11 || i==13 ){ //positivos y negativos de -99 a 99
                                float j = getDecimalFloat(data.get(i)); // decimales con punto
                                Log.d(TAG,":"+j);
                                if (j>99.9){
                                    //Extraccion de temperaturas en decimales
                                    newData.add(getNegativeTempfloat("FFFF"+data.get(i)));
                                }else{
                                    newData.add(String.valueOf(getDecimalFloat(data.get(i)) )); // decimales con punto
                                }
                            }else if (i==23){
                                //Extraccion opciones segùn bit usado
                                newData.add(getOptionSwitch(data.get(i),"escalaTemp"));
                            }else if (i==24 ){
                                //Extraccion opciones segùn bit usado
                                newData.add(getOptionSwitch(data.get(i),"banderasConfig"));
                            }else if (i==26 ){
                                //Extraccion opciones segùn bit usado
                                newData.add(getOptionSpinner(data.get(i),"C3"));
                            }else if (i==28 ){
                                //Extraccion opciones segùn bit usado
                                newData.add(getOptionSpinner(data.get(i),"C5"));
                            }else{
                                newData.add(String.valueOf(getDecimalFloat(data.get(i)) )); // decimales con punto
                            }
                        }

                        i++;
                    } while (i < data.size());
                }
                return newData;
            }
            case "Lectura de datos tipo Tiempo real": {
                List<String> newData = new ArrayList<>();
                if(data.isEmpty()){
                    //newData.add(getSameData(data.get(0),"trefpversion"));
                    newData.add("nullHandshake");
                }else {
                    //header
                    /*
                    newData.add(getSameData(data.get(0), "trefpversion"));
                    newData.add(getSameData(data.get(1), action));
                    newData.add(getSameData(data.get(2), action));
                    newData.add(getSameData(data.get(3), action));
                     */
                    //buffer
                    float numf = getDecimalFloat(data.get(4));
                    int num = (int) numf;
                    if (num<99.99){
                        newData.add(String.valueOf(getDecimalFloat(data.get(4)))); //decimales con punto //get temp positivo
                    }else if (num>99.99){
                        newData.add(getNegativeTemp("FFFF"+data.get(4))); //get negativos
                    }else{//Es 0 cero
                        newData.add("0000"); //get negativos
                    }

                    numf = getDecimalFloat(data.get(5));
                    num = (int) numf;
                    if (num<99.99){
                        newData.add(String.valueOf(getDecimalFloat(data.get(5)))); //decimales con punto //get temp positivo
                    }else if (num>99.99){
                        newData.add(getNegativeTemp("FFFF"+data.get(5))); //get negativos
                    }else{//Es 0 cero
                        newData.add("0000"); //get negativos
                    }

                    newData.add(String.valueOf(getDecimal(data.get(6))));//voltage
                    newData.add(getActuador(data.get(7)));
                    newData.add(getAlarma(data.get(8)));

                }
                return newData;
            }

            default:{
                List<String> newData = new ArrayList<>();
                return newData;
            }
        }
    }

    public static StringBuilder cleanSpace(List<String> data){
        StringBuilder s = new StringBuilder();
        for (int i=0; i<data.size();i++){
            String d = data.get(i).replace(" ","");
            d = d.replace(",","");
            s.append(d);
        }
        return s;
    }

    public static String getOptionSwitch(String hexaTemp, String option){
        String optionBinary = HexToBinary(hexaTemp);

        Log.d(TAG,"hexa:"+hexaTemp+" bin:"+optionBinary+" optionS:"+option);
        switch (option){
            case "escalaTemp":{
                if (optionBinary.equals("00100000")){
                    return "1";
                }else if (optionBinary.equals("00000000"))
                    return "0";
                break;
            }
            case "banderasConfig":{
                    return optionBinary;
            }
        }
        return "1.0";
    }

    public static String getOptionSpinner(String hexaTemp, String option){
        String optionBinary = HexToBinary(hexaTemp);
        Log.d(TAG,"hexa:"+hexaTemp+" bin:"+optionBinary+" optionS:"+option);

        switch (option){
            case "C3":{
                if (optionBinary.equals("00000001")){
                    return "1";
                }else if (optionBinary.equals("00000000"))
                    return "0";
                else if (optionBinary.equals("00000010"))
                    return "2";
            }

            case "C5":{
                if (optionBinary.equals("00000010")){
                    return "2";
                }else if (optionBinary.equals("00000000"))
                    return "0";
                else if (optionBinary.equals("00000001"))
                    return "1";
            }
        }



        return "1.0";
    }

    public static String getNegativeTemp(String hexaTemp){
        int parsedResult = (int) Long.parseLong(hexaTemp, 16);
        return String.valueOf(parsedResult/10);
    }

    public static String getNegativeTempfloat(String hexaTemp){
        float parsedResult = (int) Long.parseLong(hexaTemp, 16);
        return String.valueOf(parsedResult/10);
    }

    private static String getActuador(String s){
        String ss = HexToBinary(s);

        StringBuilder stringBuilder = new StringBuilder();
        String c;
        for (int i=0; i<8 ; i++){
            c = ss.substring(i,i+1);
            if (c.equals("1")){
                switch (i)  {
                    case 0:
                        stringBuilder.append("Estado de Lámpara/Aux: ON\n");
                        break;
                    case 6:
                        stringBuilder.append("Estado de deshielo: On\n");
                        break;
                    case 5:
                        stringBuilder.append("Estado de puerta: Abierta\n");
                        break;
                    case 4:
                        stringBuilder.append( "Modo ahorro 1: ON\n");
                        break;
                    case 3:
                        stringBuilder.append("Modo ahorro 2: ON\n");
                        break;
                    case 2:
                        stringBuilder.append("Modo Nocturno: ON\n");
                        break;
                    case 1:
                        stringBuilder.append("Estado Ventilador: ON\n");
                        break;
                    case 7:
                        stringBuilder.append("Estado de compresor: ON");
                        break;
                }
            }else{
                switch (i)  {
                    case 0:
                        stringBuilder.append("Estado de Lámpara/Aux: OFF\n");
                        break;
                    case 6:
                        stringBuilder.append("Estado de deshielo: OFF\n");
                        break;
                    case 5:
                        stringBuilder.append("Estado de puerta: Cerrado\n");
                        break;
                    case 4:
                        stringBuilder.append("Modo ahorro 1: OFF\n");
                        break;
                    case 3:
                        stringBuilder.append("Modo ahorro 2: OFF\n");
                        break;
                    case 2:
                        stringBuilder.append("Modo Nocturno: OFF\n");
                        break;
                    case 1:
                        stringBuilder.append("Estado Ventilador: OFF\n");
                        break;
                    case 7:
                        stringBuilder.append("Estado de compresor: OFF ");
                        break;
                }
            }
        }
        return stringBuilder.toString();
    }
    //PENDIENTE
    private static String getAlarma(String s){

        String ss = HexToBinary(s.substring(0,2));
        String ss2 = HexToBinary(s.substring(2));

        StringBuilder stb = new StringBuilder();
        String c;

        for (int i=0; i<ss2.length() ; i++){
            c = ss2.substring(i,i+1);
            if (c.equals("1")){
                switch (i)  {

                    case 3:
                        stb.append( " - Reservada");
                        break;
                    case 2:
                        stb.append( " - Alarma de compresor exhausto");
                        break;
                    case 1:
                        stb.append( " - Alarma de temperatura alta");
                        break;
                    case 0:
                        stb.append( "Alarma de temperatura baja");
                        break;
                }
            }
        }

        for (int i=0; i<ss.length() ; i++){
            c = ss.substring(i,i+1);
            if (c.equals("1")){
                switch (i)  {
                    case 7:
                        stb.append(" - Falla sensor ambiente en corto");
                        break;
                    case 6:
                        stb.append(" - Falla sensor ambiente en abierto");
                        break;
                    case 5:
                        stb.append(" - Falla sensor evaporador en corto");
                        break;
                    case 4:
                        stb.append( " - Falla sensor evaporador en abierto");
                        break;
                    case 3:
                        stb.append( " - Falla de puerta");
                        break;
                    case 2:
                        stb.append( " - Reservada");
                        break;
                    case 1:
                        stb.append(" - Falla de voltaje bajo");
                        break;
                    case 0:
                        stb.append( " - Falla de voltaje alto");
                        break;

                }
            }
        }


        stb.append(".");
        return stb.toString();
    }

    public static int getDecimal(String hex){
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        int val = 0;
        for (int i = 0; i < hex.length(); i++)
        {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }

        return val;
    }

    public static String HexToBinary(String Hex) {
        String bin =  new BigInteger(Hex, 16).toString(2);
        int inb = Integer.parseInt(bin);
        bin = String.format(Locale.getDefault(),"%08d", inb);
        return bin;
    }

    public static float getDecimalFloat(String hex){
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        float val = 0;
        for (int i = 0; i < hex.length(); i++)
        {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }

        return val/10;
    }

    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hexStr.length(); i+=2) {
            String str = hexStr.substring(i, i+2);
            output.append((char)Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    public static String getSameData(String d,String action){
        float ss;
        switch (action){
            case "Handshake":
                ss = Float.parseFloat(Float.toString(getDecimalFloat(d)));
                return String.valueOf(ss);
            case "trefpversion":
                ss = Float.parseFloat(d);
                return String.valueOf(ss/100);
            default:
                return d;
        }


    }
}
