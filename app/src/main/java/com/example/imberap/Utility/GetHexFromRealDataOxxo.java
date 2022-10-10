package com.example.imberap.Utility;

import java.util.ArrayList;
import java.util.List;

public class GetHexFromRealDataOxxo {
    public static String TAG = "GetHexFromRealData";
    static List<String> arrayListInfo = new ArrayList<>();

    public static List<String> getData(List<String> dataListPlantilla){
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

                    if (Integer.signum(num)==1){
                        arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto //get temp positivo
                    }else if (Integer.signum(num)==-1){
                        arrayListInfo.add(getNeg((numfinal))); //get negativos
                    }else{//Es 0 cero
                        arrayListInfo.add("0000"); //get negativos
                    }
                    break;
                }
                case 1:{
                    arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto

                    break;
                }
                case 2:{
                    float numf = Float.parseFloat(dataListPlantilla.get(i));
                    float numfinal = Float.parseFloat(dataListPlantilla.get(i));

                    int num;
                    if(numfinal <0 && numfinal >-1.0){
                        num = -1;
                    }else
                        num = (int) numf;

                    if (Integer.signum(num)==1){
                        arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto //get temp positivo
                    }else if (Integer.signum(num)==-1){
                        arrayListInfo.add(getNeg((numfinal))); //get negativos
                    }else{//Es 0 cero
                        arrayListInfo.add("0000"); //get negativos
                    }
                    arrayListInfo.add("000000000000"); //get negativos
                    break;
                }
                case 3:{
                    float numf = Float.parseFloat(dataListPlantilla.get(i));
                    float numfinal = Float.parseFloat(dataListPlantilla.get(i));

                    int num;
                    if(numfinal <0 && numfinal >-1.0){
                        num = -1;
                    }else
                        num = (int) numf;

                    if (Integer.signum(num)==1){
                        arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto //get temp positivo
                    }else if (Integer.signum(num)==-1){
                        arrayListInfo.add(getNeg((numfinal))); //get negativos
                    }else{//Es 0 cero
                        arrayListInfo.add("0000"); //get negativos
                    }
                    break;
                }
                case 4:{

                    float numf = Float.parseFloat(dataListPlantilla.get(i));
                    float numfinal = Float.parseFloat(dataListPlantilla.get(i));

                    int num;
                    if(numfinal <0 && numfinal >-1.0){
                        num = -1;
                    }else
                        num = (int) numf;
                    //Log.d("tag",":"+numfi);

                    if (Integer.signum(num)==1){
                        arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto //get temp positivo
                    }else if (Integer.signum(num)==-1){
                        arrayListInfo.add(getNeg((numfinal))); //get negativos
                    }else{//Es 0 cero
                        arrayListInfo.add("0000"); //get negativos
                    }
                    arrayListInfo.add("00000000000000000000"); //get negativos
                    break;
                }
                case 5:{
                    arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto
                    break;
                }
                case 6:{
                    arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto
                    arrayListInfo.add("0000");
                    break;
                }
                case 7:{
                    float numf = Float.parseFloat(dataListPlantilla.get(i));
                    float numfinal = Float.parseFloat(dataListPlantilla.get(i));

                    int num;
                    if(numfinal <0 && numfinal >-1.0){
                        num = -1;
                    }else
                        num = (int) numf;
                    //Log.d("tag",":"+numfi);

                    if (Integer.signum(num)==1){
                        arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto //get temp positivo
                    }else if (Integer.signum(num)==-1){
                        arrayListInfo.add(getNeg((numfinal))); //get negativos
                    }else{//Es 0 cero
                        arrayListInfo.add("0000"); //get negativos
                    }
                    break;
                }
                case 8:{
                    float numf = Float.parseFloat(dataListPlantilla.get(i));
                    float numfinal = Float.parseFloat(dataListPlantilla.get(i));

                    int num;
                    if(numfinal <0 && numfinal >-1.0){
                        num = -1;
                    }else
                        num = (int) numf;
                    //Log.d("tag",":"+numfi);

                    if (Integer.signum(num)==1){
                        arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto //get temp positivo
                    }else if (Integer.signum(num)==-1){
                        arrayListInfo.add(getNeg((numfinal))); //get negativos
                    }else{//Es 0 cero
                        arrayListInfo.add("0000"); //get negativos
                    }
                    arrayListInfo.add("00000000");
                    break;
                }
                case 9:{

                    float numf = Float.parseFloat(dataListPlantilla.get(i));
                    float numfinal = Float.parseFloat(dataListPlantilla.get(i));

                    int num;
                    if(numfinal <0 && numfinal >-1.0){
                        num = -1;
                    }else
                        num = (int) numf;
                    //Log.d("tag",":"+numfi);

                    if (Integer.signum(num)==1){
                        arrayListInfo.add(convertDecimalToHexa(dataListPlantilla.get(i))); //decimales con punto //get temp positivo
                    }else if (Integer.signum(num)==-1){
                        arrayListInfo.add(getNeg((numfinal))); //get negativos
                    }else{//Es 0 cero
                        arrayListInfo.add("0000"); //get negativos
                    }
                    arrayListInfo.add("00000000000000000000000000000000000000000000");
                    break;
                }
                case 10:{
                    arrayListInfo.add("66");//dato de seguridad
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    break;
                }
                case 11:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    break;
                }
                case 12:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    break;
                }
                case 13:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    break;
                }
                case 14:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    break;
                }
                case 15:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    break;
                }
                case 16:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    break;
                }
                case 17:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    arrayListInfo.add("00");
                    break;
                }
                case 18:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    arrayListInfo.add("000000000000");
                    break;
                }
                case 19:{//C0
                    arrayListInfo.add("0"+decimalToHex(Long.parseLong(dataListPlantilla.get(i))));//swith
                    break;
                }
                case 20:{//C1 banderas de configuracion
                    arrayListInfo.add(decimalToHex(Long.parseLong(dataListPlantilla.get(i))));//switch C1
                    break;
                }
                case 21:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    break;
                }
                case 22:{
                    arrayListInfo.add("0"+decimalToHex(Long.parseLong(dataListPlantilla.get(i))));//spinners
                    break;
                }
                case 23:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    break;
                }
                case 24:{//C5
                    arrayListInfo.add("0"+decimalToHex(Long.parseLong(dataListPlantilla.get(i))));//spinners
                    arrayListInfo.add("00000000000000000000");
                    break;
                }
                case 25:{
                    arrayListInfo.add(convertDecimalToHexa8(dataListPlantilla.get(i))); //decimales con punto
                    break;
                }
                case 26:{
                    arrayListInfo.add(convertDecimalToHexa8(dataListPlantilla.get(i))); //decimales con punto
                    break;
                }
                case 27:{
                    arrayListInfo.add(convertDecimalToHexa8(dataListPlantilla.get(i))); //decimales con punto
                    break;
                }
                case 28:{
                    arrayListInfo.add(convertDecimalToHexa8(dataListPlantilla.get(i))); //decimales con punto
                    arrayListInfo.add("000000000000000000");
                    break;
                }
                case 29:{
                    arrayListInfo.add(convertDecimalToHexa8(dataListPlantilla.get(i))); //decimales con punto

                    break;
                }
                case 30:{
                    arrayListInfo.add(convertDecimalToHexa8(dataListPlantilla.get(i))); //decimales con punto
                    arrayListInfo.add("00");
                    break;
                }
                case 31:{
                    arrayListInfo.add(convertDecimalIntToHexa(dataListPlantilla.get(i))); //decimales sin punto
                    arrayListInfo.add("000000000000");
                    break;
                }
                case 32:{//modelo
                    arrayListInfo.add(convertDecimalToHexa8(dataListPlantilla.get(i))); //decimales con punto
                    arrayListInfo.add("00");
                    break;
                }
                case 33:{
                    /**
                     * Pensar en esto màs adelante cuando se empiece a dividir la app en permisos o en dos apps, pues este valor es candado para no poder agregar plantillas a versiones viejas excepto que sea superusuario
                     * Por ahora:Permitir al usuario que edita la plantilla que edite la version
                     * */
                    String s = dataListPlantilla.get(i).replace(".","");
                    arrayListInfo.add(convertDecimalToHexaFirmwareVersion8(s.substring(0,2)));
                    arrayListInfo.add(convertDecimalToHexaFirmwareVersion8(s.substring(2)));
                    break;
                }
                case 34:{
                    //String s = dataListPlantilla.get(i).replace(".","");
                    arrayListInfo.add("00"+convertDecimalToHexa8(dataListPlantilla.get(i))); //decimales sin punto
                    arrayListInfo.add("CC");
                    arrayListInfo.add(calculateChacksum(arrayListInfo));//CHECKSUM
                    break;
                }



            }

            i++;
        }while (i<dataListPlantilla.size());

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
