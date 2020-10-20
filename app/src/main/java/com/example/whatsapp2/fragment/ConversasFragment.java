package com.example.whatsapp2.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.whatsapp2.Adapter.ConversaAdapter;
import com.example.whatsapp2.R;
import com.example.whatsapp2.acitivity.ConversaActivity2;
import com.example.whatsapp2.config.ConfiguracaoFirebase;
import com.example.whatsapp2.helper.Base64Custom;
import com.example.whatsapp2.helper.Preferencias;
import com.example.whatsapp2.model.Conversa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ConversasFragment extends Fragment {

private ListView listView;
private ArrayAdapter<Conversa> adapter;
private ArrayList<Conversa> conversas;

private DatabaseReference firebase;
private ValueEventListener valuenventlistnerConversas;

    public ConversasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        //Montar listview e adapter
        conversas = new ArrayList<>();
        listView = (ListView) view.findViewById(R.id.lv_conversas);
        adapter = new ConversaAdapter(getActivity(),conversas);
        listView.setAdapter(adapter);

        //recupera dados do usuario
        Preferencias preferencias = new Preferencias(getActivity());
        String idUsuarioLogado = preferencias.getIdentificador();

        //recupera conversa do firebase
        firebase = ConfiguracaoFirebase.getFirebase().child("conversas")
                .child(idUsuarioLogado);
                valuenventlistnerConversas = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        conversas.clear();
                        for (DataSnapshot dados: dataSnapshot.getChildren()){
                            Conversa conversa = dados.getValue(Conversa.class);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                //evento de clique
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Conversa conversa = conversas.get(position);
                Intent intent = new Intent(getActivity(), ConversaActivity2.class);
                intent.putExtra("nome", conversa.getNome());
                String email = Base64Custom.codificarBase64(conversa.getIdUsuario());
                intent.putExtra("email",email);

                startActivity(intent);

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valuenventlistnerConversas);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valuenventlistnerConversas);
    }
}