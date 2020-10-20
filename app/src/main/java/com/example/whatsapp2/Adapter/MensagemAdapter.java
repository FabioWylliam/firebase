package com.example.whatsapp2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.whatsapp2.R;
import com.example.whatsapp2.helper.Preferencias;
import com.example.whatsapp2.model.Mensagem;

import java.util.ArrayList;
import java.util.List;

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

        private Context context;
        private ArrayList<Mensagem> mensagens;


    public MensagemAdapter(@NonNull Context c,  @NonNull ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        //verificar se a lista esta preenchida
        if (mensagens != null){


        //Recupera dados usuario remetente
            Preferencias preferencias = new Preferencias(context);
            String idUsuarioRemetente = preferencias.getIdentificador();

        //iniciar o objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        //Recupera a mensagem
        Mensagem mensagem = mensagens.get(position);

        //Montar view a parti do xml
            if (idUsuarioRemetente.equals(mensagem.getIdUsuario())){
                view = inflater.inflate(R.layout.item_mensagem_direita,parent,false);
            }else{
                view = inflater.inflate(R.layout.item_mensagem_esquerda,parent,false);
            }


        //Recupera elemento para exibição
            TextView textoMensagem = (TextView) view.findViewById(R.id.tv_mensagem);
            textoMensagem.setText(mensagem.getMensagem());

        }
        return view;
    }
}
