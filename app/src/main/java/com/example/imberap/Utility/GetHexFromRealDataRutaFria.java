package com.example.imberap.Utility;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GetHexFromRealDataRutaFria {
    public static String TAG = "GetHexFromRealDataOxxoDisplay";
    static List<String> arrayListInfo = new ArrayList<>();

    public static List<String> getData(List<String> dataListPlantilla, String plantillaOriginal){
        //espacios a modificar en la cadena:
        int T4espacio=16;
        int L6espacio=142;
        int L8espacio=146;
        int C0espacio=162;
        int C2espacio=166;
        int C8espacio=178;
        int F3espacio=200;
        int F6espacio=206;
        int F7espacio=208;

        Log.d(TAG, ";:"+dataListPlantilla);

        plantillaOriginal = plantillaOriginal.substring(18);
        Log.d(TAG, ";Plantillariginal:"+plantillaOriginal);
        arrayListInfo.clear();
        //revision para hacer estos datos dinamicos
        arrayListInfo.add("4050");//comando de inicio de modificación de parámetros
        arrayListInfo.add("AA");//buffer_size
        int i=0;
        do{
            switch (i){
                case 0:{
                    float numf = Float.parseFloat(dataListPlantilla.get(i));
                    float numfinal = Float.parseFloat(dataListPlantilla.get(i));
                    int num;
                    if(numfinal <0 && numfinal >-1.0){
                        num = -1;
                    }else
                        num = (int) numf;

                    if (Integer.signum(num)==1 || numfinal>0){
                        arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto //get temp positivo
                    }else if (Integer.signum(num)==-1){
                        arrayListInfo.add(getNeg((numfinal))); //get negativos
                    }else{//Es 0 cero
                        arrayListInfo.add("0000"); //get negativos
                    }
                    break;
                }//T0
                case 1:{
                    arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto
                    break;
                }//T1
                case 2:{
                    float numf = Float.parseFloat(dataListPlantilla.get(i));
                    float numfinal = Float.parseFloat(dataListPlantilla.get(i));
                    int num;
                    if(numfinal <0 && numfinal >-1.0){
                        num = -1;
                    }else
                        num = (int) numf;
                    if (Integer.signum(num)==1|| numfinal>0){
                        arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto //get temp positivo
                    }else if (Integer.signum(num)==-1){
                        arrayListInfo.add(getNeg((numfinal))); //get negativos
                    }else{//Es 0 cero
                        arrayListInfo.add("0000"); //get negativos
                    }
                    break;
                }//T2
                case 3:{
                    float numf = Float.parseFloat(dataListPlantilla.get(i));
                    float numfinal = Float.parseFloat(dataListPlantilla.get(i));
                    int num;
                    if(numfinal <0 && numfinal >-1.0){
                        num = -1;
                    }else
                        num = (int) numf;
                    if (Integer.signum(num)==1|| numfinal>0){
                        arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto //get temp positivo
                    }else if (Integer.signum(num)==-1){
                        arrayListInfo.add(getNeg((numfinal))); //get negativos
                    }else{//Es 0 cero
                        arrayListInfo.add("0000"); //get negativos
                    }
                    break;
                }//T3
                case 4:{
                    arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto
                    arrayListInfo.add(plantillaOriginal.substring(T4espacio+4, L6espacio));
                    break;
                }//T4
                case 5:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    break;
                }//L6
                case 6:{

                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    break;
                }//L7
                case 7:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    arrayListInfo.add(plantillaOriginal.substring(L8espacio+2, C0espacio));
                    break;
                }//L8
                case 8:{
                    if (decimalToHex(Long.parseLong(dataListPlantilla.get(i))).length() ==1 ){
                        arrayListInfo.add("0"+decimalToHex(Long.parseLong(dataListPlantilla.get(i))));//swith
                    }else
                        arrayListInfo.add(decimalToHex(Long.parseLong(dataListPlantilla.get(i))));//swith
                    break;
                }//C0
                case 9:{
                    if (decimalToHex(Long.parseLong(dataListPlantilla.get(i))).length() ==1 ){
                        arrayListInfo.add("0"+decimalToHex(Long.parseLong(dataListPlantilla.get(i))));//swith
                    }else
                        arrayListInfo.add(decimalToHex(Long.parseLong(dataListPlantilla.get(i))));//swith
                    break;
                }//C1
                case 10:{
                    if (decimalToHex(Long.parseLong(dataListPlantilla.get(i))).length() ==1 ){
                        arrayListInfo.add("0"+decimalToHex(Long.parseLong(dataListPlantilla.get(i))));//swith
                    }else
                        arrayListInfo.add(decimalToHex(Long.parseLong(dataListPlantilla.get(i))));//swith

                    arrayListInfo.add(plantillaOriginal.substring(C2espacio+2, C8espacio));
                    break;
                }//C2
                case 11:{
                    if (decimalToHex(Long.parseLong(dataListPlantilla.get(i))).length() ==1 ){
                        arrayListInfo.add("0"+decimalToHex(Long.parseLong(dataListPlantilla.get(i))));//swith
                    }else
                        arrayListInfo.add(decimalToHex(Long.parseLong(dataListPlantilla.get(i))));//swith

                    arrayListInfo.add(plantillaOriginal.substring(C8espacio+2, F3espacio));
                    break;
                }//C8
                case 12:{
                    arrayListInfo.add(convertDecimalToHexa8(dataListPlantilla.get(i))); //decimales con punto
                    arrayListInfo.add(plantillaOriginal.substring(F3espacio+2, F6espacio));
                    break;
                }//F3
                case 13:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    break;
                }//F6
                case 14:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    arrayListInfo.add(plantillaOriginal.substring(F7espacio+2, 240));//hasta el modelo
                    break;
                }//F7
                case 15:{//modelo
                    arrayListInfo.add(convertDecimalToHexa8(dataListPlantilla.get(i))); //decimales con punto
                    arrayListInfo.add("00");
                    break;
                }
                case 16:{
                    /**
                     * Pensar en esto màs adelante cuando se empiece a dividir la app en permisos o en dos apps, pues este valor es candado para no poder agregar plantillas a versiones viejas excepto que sea superusuario
                     * Por ahora:Permitir al usuario que edita la plantilla que edite la version
                     * */
                    String s = dataListPlantilla.get(i).replace(".","");
                    String s2, s3,s4;

                    if (s.length() != 4){
                        int index = dataListPlantilla.get(i).indexOf(".");
                        if (index ==1 ){
                            s2 = "0"+dataListPlantilla.get(i);
                        }else{
                            s2 = dataListPlantilla.get(i);
                        }
                        s4 =dataListPlantilla.get(i).substring(index+1);
                        if (s4.length() == 1 ){//el numero es menor a dos dígitos
                            s3 = dataListPlantilla.get(i)+"0";
                        }else{
                            s3 = dataListPlantilla.get(i);
                        }
                        /*Log.d("TEST FIRM","s:"+s);
                        Log.d("TEST FIRM","s2:"+s2);
                        Log.d("TEST FIRM","s3:"+s3);

                        Log.d("TEST FIRM","s22;"+s2.substring(0,2));
                        Log.d("TEST FIRM","s32:"+s3.substring(2));

                        Log.d("TEST FIRM","FINAL:"+s2.substring(0,2)+s3.substring(2));*/
                        arrayListInfo.add(convertDecimalToHexaFirmwareVersion8(s2.substring(0,2)));
                        arrayListInfo.add(convertDecimalToHexaFirmwareVersion8(s3.substring(2)));

                    }else{
                        arrayListInfo.add(convertDecimalToHexaFirmwareVersion8(s.substring(0,2)));
                        arrayListInfo.add(convertDecimalToHexaFirmwareVersion8(s.substring(2)));
                    }



                    break;
                }
                case 17:{
                    //String s = dataListPlantilla.get(i).replace(".","");
                    arrayListInfo.add("00"+convertDecimalToHexa8(dataListPlantilla.get(i))); //decimales sin punto
                    arrayListInfo.add("CC");
                    arrayListInfo.add(calculateChacksum(arrayListInfo));//CHECKSUM
                    break;
                }
            }
            i++;
        }while (i<dataListPlantilla.size());
        Log.d(TAG,"DATOS A ENVIAR:"+arrayListInfo);
        return arrayListInfo;
    }


    public static String calculateChacksum(List<String> data) {
        int checksum = 0;
        String s;
        StringBuilder stringBuilder = new StringBuilder();
        for (String dato : data) {
            stringBuilder.append(dato.toUpperCase());
        }
        s = stringBuilder.toString().trim();
        for (int h = 0; h < s.length(); h += 2) {
            if (h + 2 >= s.length()) {
                checksum = checksum + getDecimal(s.substring(h));
            } else
                checksum = checksum + getDecimal(s.substring(h, h + 2));
        }
        String c = Integer.toHexString(checksum);
        if (c.length() == 1)
            c = "0000000" + c;
        else if (c.length() == 2)
            c = "000000" + c;
        else if (c.length() == 3)
            c = "00000" + c;
        else if (c.length() == 4)
            c = "0000" + c;
        else if (c.length() == 5)
            c = "000" + c;
        else if (c.length() == 6)
            c = "00" + c;
        else if (c.length() == 7)
            c = "0" + c;
        return c;
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

    public static String convertDecimalToHexa(String s){
        float f = Float.parseFloat(s);
        f = f*10;
        int nm = (int) f;
        String ss="";
        String c = Integer.toHexString(nm);
        if (c.length() == 1)
            ss = "000"+c;
        else if (c.length() == 2)
            ss = "00"+c;
        else if (c.length() == 3)
            ss = "0"+c;
        else
            ss = c;
        return ss;
    }

    public static String convertDecimalToHexaFirmwareVersion8(String s){
        float f = Float.parseFloat(s);

        int nm = (int) f;
        String ss="";
        String c = Integer.toHexString(nm);

        if (c.length() == 1)
            ss = "0"+c;
        else
            ss=c;
        return ss;
    }
    public static String convertDecimalToHexaModelo8(String s){
        float f = Float.parseFloat(s);
        int nm = (int) f;
        String ss="";
        String c = Integer.toHexString(nm);

        if (c.length() == 1)
            ss = "0"+c;
        else
            ss=c;
        return ss;
    }

    public static String convertDecimalToHexa8(String s){
        float f = Float.parseFloat(s);
        f = f*10;
        int nm = (int) f;
        String ss="";
        String c = Integer.toHexString(nm);

        if (c.length() == 1)
            ss = "0"+c;
        else
            ss=c;
        return ss;
    }

    public static String convertDecimalIntToHexa(String s){
        int f = Integer.parseInt(s);
        //f = f*10;
        //int nm = (int) f;
        //String ss = Integer.toString(f, 16);
        String ss = Integer.toHexString(f);
        if (ss.length()==1){
            ss = "0"+ss;
        }
        return ss;
    }

    public static int binaryToDecimal(long binary){
        int decimalNumber = 0, i = 0;
        while (binary > 0) {
            decimalNumber
                    += Math.pow(2, i++) * (binary % 10);
            binary /= 10;
        }
        return decimalNumber;
    }
    public static String decimalToHex(long binary){
        int decimalNumber = binaryToDecimal(binary);
        String hexNumber
                = Integer.toHexString(decimalNumber);
        hexNumber = hexNumber.toUpperCase();
        return hexNumber;
    }

    public static String getNeg(float val){
        val = val *10;
        int numfi = (int) val;

        String hex = Integer.toHexString(numfi);
        return hex.substring(4);
    }
}
