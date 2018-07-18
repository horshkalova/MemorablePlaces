package com.example.marynahorshkalova.memorableplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public void addPlace(View view) {

        Intent intent = new Intent(this, MapsActivity.class);

        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView placesListView = findViewById(R.id.placesListView);

        ArrayList<String> places = new ArrayList<String>();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, places);

        placesListView.setAdapter(arrayAdapter);
    }


}
