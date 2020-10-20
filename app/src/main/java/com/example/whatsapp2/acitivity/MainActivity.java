package com.example.whatsapp2.acitivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.whatsapp2.Adapter.TabAdapter;
import com.example.whatsapp2.R;
import com.example.whatsapp2.config.ConfiguracaoFirebase;
import com.example.whatsapp2.helper.Base64Custom;
import com.example.whatsapp2.helper.Preferencias;
import com.example.whatsapp2.helper.SlidingTabLayout;
import com.example.whatsapp2.model.Contato;
import com.example.whatsapp2.model.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button botaoSair;
    private FirebaseAuth autenticacao;
    private Toolbar toolbar;
    private FirebaseAuth usuarioAutenticacao;

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private String identificadorContato;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuarioAutenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("WhatsApp");
        setSupportActionBar( toolbar);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        //Configura sliding Tabs
        slidingTabLayout .setDistributeEvenly(true);

        //adapter
        TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);

        slidingTabLayout.setViewPager(viewPager);
       }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item_sair:
                deslogarUsuario();
                return true;
            case R.id.item_configuracoes:
                return true;
            case R.id.item_adicionar:
                abrirCadastroContato();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void abrirCadastroContato(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);

        //Configurações do dialogo
        alertDialog.setTitle("NOVO CONTATO");
        alertDialog.setMessage("E-MAIL DO USUARIO");
        alertDialog.setCancelable(false);

        final EditText editText = new EditText(MainActivity.this);
        alertDialog.setView(editText);

        //Configura botões
        alertDialog.setPositiveButton("Cadastrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            String emailContato = editText.getText().toString();

            //Validador e-mail
                if (emailContato.isEmpty()){
                    Toast.makeText(MainActivity.this,"Preencha o e-mail",Toast.LENGTH_LONG).show();
                }else{

                    //Verificar se o e-mail já esta cadastrado
                    identificadorContato = Base64Custom.codificarBase64(emailContato);




                    //Recupera a instância do Firebase
                    firebase = ConfiguracaoFirebase.getFirebase().child("usuarios").child(identificadorContato);
                    firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null){

                                //Recupera dados do contato a ser adicionado
                                Usuario usuarioContato = dataSnapshot.getValue(Usuario.class);


                                //Recupera identificador usuario logado (BASE64)
                                Preferencias preferencias = new Preferencias(MainActivity.this);
                                String identificadorUsuarioLogado = preferencias.getIdentificador();

                                firebase = ConfiguracaoFirebase.getFirebase();
                                firebase = firebase.child("contatos")
                                                   .child(identificadorUsuarioLogado)
                                                   .child(identificadorContato);

                                Contato contato = new Contato();
                                contato.setIdentificadorUsuario(identificadorContato);
                                contato.setEmail(usuarioContato.getEmail());
                                contato.setNome(usuarioContato.getNome());



                                firebase.setValue(contato);

                            }else{
                                Toast.makeText(MainActivity.this,"Usuário não possui cadastro.",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.create();
        alertDialog.show();
    }

    public void deslogarUsuario(){
    usuarioAutenticacao.signOut();
    Intent intent = new Intent(MainActivity.this, LoginActivity2.class);
    startActivity(intent);
    finish();
    }
}

