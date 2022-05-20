package com.example.imberap.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.TextView;

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


}
