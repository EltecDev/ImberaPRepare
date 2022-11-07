package mx.eltec.imberatrefp.Utility;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.TextView;
import java.util.Locale;
public class GlobalTools {

    public static void changeScreenConnectionStatus(TextView tv, SharedPreferences sp){
        if (sp.getBoolean("isconnected",false)){
            tv.setText("Conectado a:"+sp.getString("mac",""));
            tv.setTextColor(Color.parseColor("#00a135"));
        }else{
            tv.setText("Desconectado");
            tv.setTextColor(Color.BLACK);
        }
    }


    public static String checkChecksumImberaTREFPB(String data){
        //if (data.length()!=50){//la comunicación es erronea, es posible que no tenga fw instalado
            //return "notFirmware";
        //}else{
            String checksumData = data.substring(data.length()-8);
            int checksumTotal=0;
            String c="";
            for (int p=0; p<data.length()-9 ; p+=2) {
                checksumTotal = checksumTotal + GetRealDataFromHexaOxxoDisplay.getDecimal(data.substring(p, p + 2));
            }

            c = Integer.toHexString(checksumTotal);
            String finalchecksum = "";
            if (c.length() == 1)
                finalchecksum = "0000000" + c;
            else if (c.length() == 2)
                finalchecksum = "000000" + c;
            else if (c.length() == 3)
                finalchecksum = "00000" + c;
            else if (c.length() == 4)
                finalchecksum = "0000" + c;
            else if (c.length() == 5)
                finalchecksum = "000" + c;
            else if (c.length() == 6)
                finalchecksum = "00" + c;
            else if (c.length() == 7)
                finalchecksum = "0" + c;
            else
                finalchecksum = c;
            finalchecksum = finalchecksum.toUpperCase(Locale.ROOT);

            if(checksumData.equals(finalchecksum)){
                return "ok";
            }else{
                return "notok";
            }
        //}
    }

    public static String checkChecksum(String data){
        String checksumData = data.substring(data.length()-8);
        int checksumTotal=0;
        String c="";
        for (int p=0; p<data.length()-9 ; p+=2) {
            checksumTotal = checksumTotal + GetRealDataFromHexaOxxoDisplay.getDecimal(data.substring(p, p + 2));
        }
        c = Integer.toHexString(checksumTotal);
        String finalchecksum = "";
        if (c.length() == 1)
            finalchecksum = "0000000" + c;
        else if (c.length() == 2)
            finalchecksum = "000000" + c;
        else if (c.length() == 3)
            finalchecksum = "00000" + c;
        else if (c.length() == 4)
            finalchecksum = "0000" + c;
        else if (c.length() == 5)
            finalchecksum = "000" + c;
        else if (c.length() == 6)
            finalchecksum = "00" + c;
        else if (c.length() == 7)
            finalchecksum = "0" + c;
        else
            finalchecksum = c;
        finalchecksum = finalchecksum.toUpperCase(Locale.ROOT);
        if(checksumData.equals(finalchecksum)){
            return "ok";
        }else{
            return "notok";
        }
    }


}
