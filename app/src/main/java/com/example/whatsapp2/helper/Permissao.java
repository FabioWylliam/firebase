package com.example.whatsapp2.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static boolean validaPermissoes(int requestCode, Activity activity, String[] permissoes){

        if(Build.VERSION.SDK_INT >=23){

            List<String> listaPermissoes = new ArrayList<String>();

            //Percorre as permissoes passadas, verificando uma a uma
            for (String permissao : permissoes){
                Boolean validaPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if (!validaPermissao) listaPermissoes.add(permissao);

            }
              //caso a lista esteja vazia não é necessario solicitar permissão
            if (listaPermissoes.isEmpty()) return true;

            String [] novasPermissoes = new String [listaPermissoes.size()];
            listaPermissoes.toArray( novasPermissoes);

            //Solicitar permissao
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);
        }

    return true;

    }

}
