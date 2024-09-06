package dev.fian.dolananpuzzle;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    public static final String SP_BELUM_REKOR = "spBelumRekor";
    public static final String SP_SUDAH_REKOR = "spSudahRekor";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context){
        sp = context.getSharedPreferences(SP_BELUM_REKOR, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public Boolean getSPSudahRekor(){
        return sp.getBoolean(SP_SUDAH_REKOR, false);
    }
}
