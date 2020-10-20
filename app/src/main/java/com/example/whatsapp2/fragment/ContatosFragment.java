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

import com.example.whatsapp2.R;
import com.example.whatsapp2.acitivity.ConversaActivity2;
import com.example.whatsapp2.config.ConfiguracaoFirebase;
import com.example.whatsapp2.helper.Preferencias;
import com.example.whatsapp2.model.Contato;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ContatosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<String> contatos;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerContatos;

    public ContatosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerContatos);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerContatos);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //instânciar
        contatos = new ArrayList<>();




        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contatos, container, false);

        //montar list view e adapter
        listView = (ListView) view.findViewById(R.id.lv_contatos);
        adapter = new ArrayAdapter(
                getActivity(),
                android.R.layout.simple_list_item_1,
                contatos
                );
                listView.setAdapter(adapter);

                //recupera contatos do firebase
                Preferencias preferencias = new Preferencias(getActivity());
                String identificadorUsuarioLogado = preferencias.getIdentificador();
                firebase = ConfiguracaoFirebase.getFirebase()
                          .child("contatos")
                          .child(identificadorUsuarioLogado);

                //Lista para recupera contatos
                valueEventListenerContatos = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //limpar lista
                        contatos.clear();


                        //Lista contatos
                        for (DataSnapshot dados: dataSnapshot.getChildren()){
                            Contato contato = dados.getValue(Contato.class);
                            contatos.add( contato.getNome());

                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {



                    }
                };


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(getActivity(), ConversaActivity2.class);


                        //recupera dados a serem passados
                        String contato = contatos.get(position);


                        //enviando dados para conversa activity
                        intent.putExtra("nome", contato);
                        intent.putExtra("email", contato);

                        startActivity(intent);

                    }
                });


        return view;
    }
}
