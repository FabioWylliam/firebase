package com.example.whatsapp2.acitivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.whatsapp2.Adapter.MensagemAdapter;
import com.example.whatsapp2.R;
import com.example.whatsapp2.config.ConfiguracaoFirebase;
import com.example.whatsapp2.fragment.ContatosFragment;
import com.example.whatsapp2.helper.Base64Custom;
import com.example.whatsapp2.helper.Preferencias;
import com.example.whatsapp2.model.Conversa;
import com.example.whatsapp2.model.Mensagem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ConversaActivity2 extends AppCompatActivity {

private Toolbar toolbar;
private EditText editMensagem;
private ImageButton btMensagem;
private DatabaseReference firebase;
private ListView listView;
private ArrayList<Mensagem> mensagens;
private ArrayAdapter<Mensagem> adapter;
private ValueEventListener valueEventListenerMensagem;

//dados do destinatario
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

 //Dados do remetente
    private  String idUsuarioRemetente;
    private String nomeUsuarioRemetente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa2);

        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar2);
        editMensagem = (EditText) findViewById((R.id.edi_mensagem));
        btMensagem = (ImageButton) findViewById(R.id.bt_enviar);
        listView = (ListView) findViewById(R.id.lv_conversas);

        //dados do usurio logado
        Preferencias preferencias = new Preferencias(ConversaActivity2.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();


        Bundle extra = getIntent().getExtras();

        if (extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);

        }


        // configura Toolbar
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        setSupportActionBar(toolbar);

        //Montar listView e adapter
        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(ConversaActivity2.this, mensagens);
        listView.setAdapter(adapter);

        //rercupera mensagens do fire base
        firebase = ConfiguracaoFirebase.getFirebase()
                    .child("mensagens")
                    .child(idUsuarioRemetente)
                    .child(idUsuarioDestinatario) ;

        //Criar listiner para mensagem
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //limpar mensagem
                mensagens.clear();

                //Recupera mensagens
                for (DataSnapshot dados: dataSnapshot.getChildren()){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener(valueEventListenerMensagem);

        //Enviar mensagem
        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoMensagem = editMensagem.getText().toString();

                if (textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity2.this,"Digite uma senha para enviar", Toast.LENGTH_LONG).show();

                }else{
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setMensagem(textoMensagem);

                    //salvar mensagem para o remetente
                    Boolean retornoMensagemRemetente = salvarMensagem(idUsuarioRemetente,idUsuarioDestinatario,mensagem);
                    if (!retornoMensagemRemetente){
                        Toast.makeText(ConversaActivity2.this,"problema ao salvar mensagem, tente novamente",Toast.LENGTH_LONG).show();

                    }else{
                        //salvar mensagem para o destinatario
                        Boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestinatario,idUsuarioRemetente,mensagem);
                        if(!retornoMensagemDestinatario){
                            Toast.makeText(ConversaActivity2.this,"problema ao enviar mensagens ao destinatario, tente novamente.",Toast.LENGTH_LONG).show();
                        }
                    }

                    //Salvamos conversas para o remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario(idUsuarioDestinatario);
                    conversa.setNome(nomeUsuarioDestinatario);
                    conversa.setMensagem(textoMensagem);
                    Boolean retornoConversaRemetente = salvarConversa(idUsuarioRemetente,idUsuarioDestinatario,conversa);
                    if (!retornoConversaRemetente){
                        Toast.makeText(
                                ConversaActivity2.this,
                                "problema ao salvar conversa, tente novamente",Toast.LENGTH_LONG
                        ).show();

                    }else{
                        //Salvar conversa para o destinatario
                        Conversa conversa1 = new Conversa();
                        conversa.setIdUsuario(idUsuarioRemetente);
                        conversa.setMensagem(textoMensagem);
                        conversa.setNome(nomeUsuarioRemetente);

                        Boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestinatario,idUsuarioRemetente,conversa);
                        if (!retornoConversaDestinatario){
                            Toast.makeText(ConversaActivity2.this,
                                    "problema ao salvar conversa ao destinatario, tente novamente!",Toast.LENGTH_LONG).show();
                        }

                    }

                    editMensagem.setText("");

                }
            }




        });








    }

    private boolean salvarMensagem(String idRemetente, String idDestinatario, Mensagem mensagem){
        try {

            firebase = ConfiguracaoFirebase.getFirebase().child("mensagens");

            firebase.child(idRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue(mensagem);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarConversa(String idRemetente, String iddestnatario, Conversa conversa){

        try {
           firebase = ConfiguracaoFirebase.getFirebase().child("conversas");
           firebase.child(idRemetente)
                   .child(iddestnatario)
                   .setValue(conversa);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }
}