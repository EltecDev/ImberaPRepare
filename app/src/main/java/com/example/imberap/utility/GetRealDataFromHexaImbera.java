package com.example.imberap.utility;

import android.util.Log;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GetRealDataFromHexaImbera {
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

                //header
                if (!arrayLists.isEmpty()){
                    arrayListInfo.add(s.substring(0, 4));//software version
                    arrayListInfo.add(s.substring(4, 12));//buffer_size
                    arrayListInfo.add(s.substring(12, 14));//data_type
                    arrayListInfo.add(s.substring(14, 16));//data_size
                    do {
                        if (i==22){//saltar posiciones por parámetros que no se están usando
                            i = i+4;
                        }else if (i==30){//saltar posiciones por parámetros que no se están usando
                            i = i+20;
                        }else if (i==62){
                            i = i+8;
                        }else{
                            arrayListInfo.add(s.substring(i, i+4));
                            i = i + 4;
                        }
                    } while (i < 86);
                    //+2 para saltar 66

                    i = 148;
                    do {
                        if (i==154){//saltar posiciones por parámetros que no se están usando
                            i = i+2;
                        }else if (i==158){//saltar posiciones por parámetros que no se están usando
                            i = i+6;
                        }else if (i==172){
                            i = i+6;
                        }else if (i==184){
                            i = i+2;
                        }else if (i==190){
                            i = i+2;
                        }else if (i==196){
                            i = i+16;
                        }else if (i==228){
                            i = i+2;
                        }else if (i==242){
                            i = i+6;
                        }else if (i==252){
                            i = i+8;
                        }else{
                            arrayListInfo.add(s.substring(i, i+2));
                            i = i + 2;
                        }
                    } while (i < s.length()-14);

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
            case "Lectura de datos tipo Tiempo":{
                if (!arrayLists.isEmpty()){
                    StringBuilder s = cleanSpace(arrayLists);
                    //header
                    arrayListInfo.add(s.substring(0,4));//head
                    arrayListInfo.add(s.substring(4,12));//
                    arrayListInfo.add(s.substring(12,14));//modelo trefpb
                    arrayListInfo.add(s.substring(14,16));//version
                    //data
                    for(int i=16; i<s.length(); i+=18){
                        if (i+18>s.length()){
                            arrayListInfo.add(s.substring(i));//checksum
                            break;
                        }else
                            arrayListInfo.add(s.substring(i,i+18));
                    }

                }
                break;
            }
            case "Lectura de datos tipo Evento":{
                if (!arrayLists.isEmpty()){
                    StringBuilder s = cleanSpace(arrayLists);
                    //header
                    arrayListInfo.add(s.substring(0,4));//head
                    arrayListInfo.add(s.substring(4,12));//
                    arrayListInfo.add(s.substring(12,14));//modelo trefpb
                    arrayListInfo.add(s.substring(14,16));//version

                    //data
                    //data
                    for(int i=16; i<s.length(); i+=28){
                        if (i+28>s.length()){
                            arrayListInfo.add(s.substring(i));//checksum
                            break;
                        }else
                            arrayListInfo.add(s.substring(i,i+28));
                    }

                    /*
                    arrayListInfo.add(s.substring(16,24));//timestampSTART
                    arrayListInfo.add(s.substring(24,32));//timestampEND
                    arrayListInfo.add(s.substring(32,34));//eventType
                    arrayListInfo.add(s.substring(34,38));//TEMP1
                    arrayListInfo.add(s.substring(38,42));//TEMP2
                    arrayListInfo.add(s.substring(42,44));//VOLTAGE
                    arrayListInfo.add(s.substring(44,52));//checksum


                     */
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
                    //newData.add(data.get(0));
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
                    Log.d("PRUEBA",":"+data.get(22));
                    newData.add(getSameData(data.get(0), "trefpversion"));
                    newData.add(getSameData(data.get(1), action));
                    newData.add(String.valueOf(getDecimal(data.get(2))));
                    newData.add(String.valueOf(getDecimal(data.get(3))));

                    //buffer
                    int i = 4;
                    do {
                        if (i==42||i==17||i==18||i==19||i==20|| i==21||i==13||i==14||i==15||i==16||i==43 || i == 27){
                            newData.add(String.valueOf(getDecimal(data.get(i))));//decimales sin punto
                        }else {
                            if (i==4|| i==6 || i==7 || i==8 || i==12 || i==11){
                                //comprobar si es popsitivo
                                float j = getDecimalFloat(data.get(i)); // decimales con punto
                                if (j>99.9){
                                    //Extraccion de temperaturas en decimales
                                    newData.add(getNegativeTemp("FFFF"+data.get(i)));
                                }else{
                                    newData.add(String.valueOf(getDecimalFloat(data.get(i)) )); // decimales con punto
                                }
                            }else if (i==22 ){
                                //Extraccion opciones segùn bit usado
                                newData.add(getOptionSpinner(data.get(i),"mododeshielo"));
                            }else if (i==23 ){
                                //Extraccion opciones segùn bit usado
                                newData.add(getOptionSpinner(data.get(i),"funcionesControl"));
                            }else if (i==24 ){
                                //Extraccion opciones segùn bit usado
                                newData.add(getOptionSpinner(data.get(i),"funcionesdeshielo"));
                            }else if (i==25 ){
                                //Extraccion opciones segùn bit usado
                                newData.add(getOptionSpinner(data.get(i),"funcionesventilador"));
                            }else if (i==26 ){
                                //Extraccion opciones segùn bit usado
                                newData.add(getOptionSpinner(data.get(i),"funcionesvoltaje"));
                            }else{
                                newData.add(String.valueOf(getDecimalFloat(data.get(i)) )); // decimales con punto
                            }
                        }

                        i++;
                    } while (i < data.size());
                    Log.d("Pureba2",":"+newData);
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
                    /*newData.add(getSameData(data.get(0), "trefpversion"));
                    newData.add(getSameData(data.get(1), action));
                    newData.add(getSameData(data.get(2), action));
                    newData.add(getSameData(data.get(3), action));

                     */
                    //buffer
                    float numf = getDecimalFloat(data.get(4));
                    //numf = getDecimalFloat("FFCE");
                    int num = (int) numf;
                    if (num<99.99){
                        newData.add(String.valueOf(getDecimalFloat(data.get(4)))); //decimales con punto //get temp positivo
                    }else if (num>99.99){
                        //newData.add(getNegativeTemp("FFFF"+data.get(i).substring(22,26))); //get negativos
                        newData.add(getNegativeTemp("FFFF"+data.get(4))); //get negativos
                    }else{//Es 0 cero
                        newData.add("0000"); //get negativos
                    }

                    numf = getDecimalFloat(data.get(5));
                    //numf = getDecimalFloat("FFCE");
                    num = (int) numf;
                    if (num<99.99){
                        newData.add(String.valueOf(getDecimalFloat(data.get(5)))); //decimales con punto //get temp positivo
                    }else if (num>99.99){
                        //newData.add(getNegativeTemp("FFFF"+data.get(i).substring(22,26))); //get negativos
                        newData.add(getNegativeTemp("FFFF"+data.get(5))); //get negativos
                    }else{//Es 0 cero
                        newData.add("0000"); //get negativos
                    }

                    //newData.add(String.valueOf(getDecimalFloat(data.get(4)) ));//temp2
                    //newData.add(String.valueOf(getDecimalFloat(data.get(5)) ));//temp1
                    newData.add(String.valueOf(getDecimal(data.get(6))));//voltage
                    newData.add(getActuador(data.get(7)));
                    newData.add(getAlarma(data.get(8)));
                    //newData.add(getSameData(data.get(9), "trefpversion")); // decimales con punto
                    //newData.add(hexToAscii(data.get(9)));
                }
                return newData;
            }
            case "Lectura de datos tipo Tiempo":{
                List<String> newData = new ArrayList<>();
                //header
                if(data.isEmpty()){
                    //newData.add(getSameData(data.get(0),"trefpversion"));
                    newData.add("nullHandshake");
                }else{
                    //header
                    /*newData.add(getSameData(data.get(0),"trefpversion"));
                    newData.add(getSameData(data.get(1),action));
                    newData.add(getSameData(data.get(2),action));
                    newData.add(getSameData(data.get(3),action));
                     */
                    //buffer
                    Date date;
                    int i=4;
                    do {
                        if (i+1 >= data.size()){
                            i=data.size();//no interesa el checksum
                        }else {
                            //date = new java.util.Date(Long.parseLong(String.valueOf(getDecimal(data.get(i).substring(0,8)))));
                            Instant instant = Instant.ofEpochSecond(getDecimal(data.get(i).substring(0,8)));
                            date = Date.from(instant);
                            newData.add(date.toString()); //decimales sin punto
                            //decision de temperaturas positivas y negativas
                            float numf = getDecimalFloat(data.get(i).substring(8,12));
                            int num = (int) numf;
                            if (num<99.99){
                                newData.add(String.valueOf(getDecimalFloat(data.get(i).substring(8,12)))); //decimales con punto //get temp positivo
                            }else if (num>99.99){
                                newData.add(getNegativeTemp("FFFF"+data.get(i).substring(8,12))); //get negativos
                            }else{//Es 0 cero
                                newData.add("0000"); //get negativos
                            }

                            numf = getDecimalFloat(data.get(i).substring(12,16));
                            num = (int) numf;
                            if (num<99.99){
                                newData.add(String.valueOf(getDecimalFloat(data.get(i).substring(12,16)))); //decimales con punto //get temp positivo
                            }else if (num>99.99){
                                //newData.add(getNegativeTemp("FFFF"+data.get(i).substring(22,26))); //get negativos
                                newData.add(getNegativeTemp("FFFF"+data.get(i).substring(12,16))); //get negativos
                            }else{//Es 0 cero
                                newData.add("0000"); //get negativos
                            }
                            //newData.add(String.valueOf(getDecimalFloat(data.get(i).substring(8,12)) ));
                            //newData.add(String.valueOf(getDecimalFloat(data.get(i).substring(12,16)) ));

                            newData.add(String.valueOf(getDecimal(data.get(i).substring(16)))); //decimales sin punto
                            i++;
                        }
                    }while (i < data.size());
                }
                return newData;
            }
            case "Lectura de datos tipo Evento":{
                List<String> newData = new ArrayList<>();
                //header
                if(data.isEmpty()){
                    //newData.add(getSameData(data.get(0),"trefpversion"));
                    newData.add("nullHandshake");
                }else{
                    /*newData.add(getSameData(data.get(0),"trefpversion"));
                    newData.add(getSameData(data.get(1),action));
                    newData.add(getSameData(data.get(2),action));
                    newData.add(getSameData(data.get(3),action));

                     */
                    //buffer
                    Date date;
                    int i=4;
                    do {
                        if (i+1 >= data.size()){
                            break;//i=data.size();//no interesa el checksum
                        }else {
                            //Date
                            Instant instant = Instant.ofEpochSecond(getDecimal(data.get(i).substring(0,8)));
                            date = Date.from(instant);
                            newData.add(date.toString());

                            instant = Instant.ofEpochSecond(getDecimal(data.get(i).substring(8,16)));
                            date = Date.from(instant);
                            newData.add(date.toString());

                            newData.add(getEventType((data.get(i).substring(16,18))));//evento type

                            float numf = getDecimalFloat(data.get(i).substring(18,22));
                            int num = (int) numf;
                            if (num<99.99){
                                newData.add(String.valueOf(getDecimalFloat(data.get(i).substring(18,22)))); //decimales con punto //get temp positivo
                            }else if (num>99.99){
                                newData.add(getNegativeTemp("FFFF"+data.get(i).substring(18,22))); //get negativos
                            }else{//Es 0 cero
                                newData.add("0000"); //get negativos
                            }

                            numf = getDecimalFloat(data.get(i).substring(22,26));
                            //numf = getDecimalFloat("FFCE");
                            num = (int) numf;
                            if (num<99.99){
                                newData.add(String.valueOf(getDecimalFloat(data.get(i).substring(22,26)))); //decimales con punto //get temp positivo
                            }else if (num>99.99){
                                //newData.add(getNegativeTemp("FFFF"+data.get(i).substring(22,26))); //get negativos
                                newData.add(getNegativeTemp("FFFF"+data.get(i).substring(22,26))); //get negativos
                            }else{//Es 0 cero
                                newData.add("0000"); //get negativos
                            }

                            //newData.add(String.valueOf(getDecimalFloat(data.get(i).substring(18,22)) ));
                            //newData.add(String.valueOf(getDecimalFloat(data.get(i).substring(22,26)) ));
                            newData.add(String.valueOf(getDecimal(data.get(i).substring(26)))); //decimales sin punto,voltaje

                            i++;
                        }
                    }while (i < data.size());
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

    /**PENDIENTE CAMBIO DE SPINNER A SWITCH EN VARIAS OPCIONES*/
    public static String getOptionSpinner(String hexaTemp, String option){
        String optionBinary = HexToBinary(hexaTemp);

        /*
        C4B1:apagado con puerta abierta=0 switch no activo

        ventilador se apaga con compresor apagado
        0 switch apagado 1 encendido

         */
        switch (option){
            case "mododeshielo":{

                if (optionBinary.equals("00000010")){
                    return "10";
                }else if (optionBinary.equals("00000000"))
                    return "00";
                else if (optionBinary.equals("00000011"))
                    return "11";
                else if (optionBinary.equals("00000001"))
                    return "01";
                break;
            }
            case "funcionesControl":{
                if (optionBinary.equals("00000100")){
                    return "1";
                }else if (optionBinary.equals("00000000"))
                    return "0";
                break;
            }
            case "funcionesdeshielo":{
                if (optionBinary.equals("00000100")){
                    return "1";
                }else if (optionBinary.equals("00000000"))
                    return "0";
                break;
            }
            case "funcionesventilador":{
                switch (optionBinary) {
                    case "00000010":
                        return "10";
                    case "00000011":
                        return "11";
                    case "00000001":
                        return "01";
                    case "00000000":
                        return "00";
                }
                break;
            }
            case "funcionesvoltaje":{
                if (optionBinary.equals("00000001")){
                    return "1";
                }else if (optionBinary.equals("00000000"))
                    return "0";
                else if (optionBinary.equals("00000010"))
                    return "10";
                break;
            }

        }
        return "1.0";
    }

    public static String getNegativeTemp(String hexaTemp){
        int parsedResult = (int) Long.parseLong(hexaTemp, 16);
        return String.valueOf(parsedResult/10);
    }

    private static String getEventType(String s){
        String ss = HexToBinary(s);
        StringBuilder stringBuilder = new StringBuilder();
        String c;
        for (int i=0; i<5 ; i++){
            c = ss.substring(i,i+1);
            if (c.equals("1")){
                switch (i)  {
                    case 4:
                        stringBuilder.append("Modo ahorro 1: ON\n");
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
                    case 0:
                        stringBuilder.append("Estado de compresor: ON\n");
                        break;
                }
            }else{
                switch (i)  {
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
                    case 0:
                        stringBuilder.append("Estado de compresor: OFF\n ");
                        break;
                }
            }
        }
        return stringBuilder.toString();
    }

    private static String getActuador(String s){
        String ss = HexToBinary(s);
        StringBuilder stringBuilder = new StringBuilder();
        String c;
        for (int i=0; i<5 ; i++){
            c = ss.substring(i,i+1);
            if (c.equals("1")){
                switch (i)  {
                    case 4:
                        stringBuilder.append("Modo ahorro 2: ON");
                        break;
                    case 3:
                        stringBuilder.append("Modo ahorro 1: ON\n");
                        break;
                    case 2:
                        stringBuilder.append("Estado de puerta: ON\n");
                        break;
                    case 1:
                        stringBuilder.append("Estado de deshielo: ON\n");
                        break;
                    case 0:
                        stringBuilder.append("\nEstado de compresor: ON\n");
                        break;
                }
            }else{
                switch (i)  {
                    case 4:
                        stringBuilder.append("Modo ahorro 2: OFF");
                        break;
                    case 3:
                        stringBuilder.append("Modo ahorro 1: OFF\n");
                        break;
                    case 2:
                        stringBuilder.append("Estado de puerta: OFF\n");
                        break;
                    case 1:
                        stringBuilder.append("Estado de deshielo: OFF\n");
                        break;
                    case 0:
                        stringBuilder.append("\nEstado de compresor: OFF\n ");
                        break;
                }
            }
        }
        return stringBuilder.toString();
    }

    private static String getAlarma(String s){
        String ss = HexToBinary(s);

        StringBuilder stb = new StringBuilder();
        String c;
        for (int i=0; i<8 ; i++){
             c = ss.substring(i,i+1);
            if (c.equals("1")){
                switch (i)  {
                    case 7:
                        stb.append("Falla sensor ambiente en corto\n");
                        break;
                    case 6:
                        stb.append("Falla sensor ambiente en abierto\n");
                        break;
                    case 5:
                        stb.append("Falla sensor evaporador en corto\n");
                        break;
                    case 4:
                        stb.append("Falla sensor evaporador en abierto\n");
                        break;
                    case 3:
                        stb.append("Falla de puerta\n");
                        break;
                    case 2:
                        stb.append("Reservada\n");
                        break;
                    case 1:
                        stb.append("Falla de voltaje bajo\n");
                        break;
                    case 0:
                        stb.append( "\nFalla de voltaje alto\n");
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
