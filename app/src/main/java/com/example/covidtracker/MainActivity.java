package com.example.covidtracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.icu.text.Transliterator;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covidtracker.Api.ApiUtilities;
import com.example.covidtracker.Api.ModelClass;
import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

CountryCodePicker countryCodePicker;
TextView Ttodaytotal,Ttotal,Tactive,Ttodayactive,Trecovered,Ttodayrecovered,Tdeaths,Ttodaydeaths;
String country;
TextView mfilter;
Spinner spinner;
String[] types={"cases","deaths","recovered","active"};
private List<ModelClass> modelClassList;
private List<ModelClass> modelClassList2;


PieChart Ppiechart;
private RecyclerView recyclerView;
com.example.covidtracker.Adapter adapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //getSupportActionBar().hide();

        countryCodePicker=findViewById(R.id.cpp);
        Ttodaytotal=findViewById(R.id.todaytotal);
        Ttotal=findViewById(R.id.totalcase);
        Tactive=findViewById(R.id.activecase);
        Ttodayactive=findViewById(R.id.todayactive);
        Ttodayrecovered=findViewById(R.id.todayrecovered);
        Trecovered=findViewById(R.id.recoveredcase);
        Tdeaths=findViewById(R.id.totaldeath);
        Ttodaydeaths=findViewById(R.id.todaydeath);
        Ppiechart=findViewById(R.id.piechart);
        spinner=findViewById(R.id.spinner);
        mfilter=findViewById(R.id.filter);
        recyclerView=findViewById(R.id.recyclerview);
        modelClassList=new ArrayList<>();
        modelClassList2=new ArrayList<>();


        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, com.hbb20.R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(com.hbb20.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        ApiUtilities.getApiInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {

                    modelClassList2.addAll(response.body());
                    //will work on notifydatasetchanged later
                    adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });


        adapter=new Adapter(getApplicationContext(),modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);


        countryCodePicker.setAutoDetectedCountry(true);
        country=countryCodePicker.getSelectedCountryName();

        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                fetchRequestedData();

            }
        });

        fetchRequestedData();


    }

    private void fetchRequestedData() {
        ApiUtilities.getApiInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                //error on modelclasslist
                modelClassList.addAll( response.body());
                for(int i=0;i<modelClassList.size();i++){

                    if(modelClassList.get(i).getCountry().equals(country)){

                        Tactive.setText((modelClassList.get(i).getActive()));
                        Ttodaydeaths.setText((modelClassList.get(i).getTodayDeaths()));
                        Ttodayrecovered.setText((modelClassList.get(i).getTodayRecovered()));
                        Ttodaytotal.setText((modelClassList.get(i).getTodayCases()));
                        Ttotal.setText((modelClassList.get(i).getCases()));
                        Tdeaths.setText((modelClassList.get(i).getDeaths()));
                        Trecovered.setText((modelClassList.get(i).getRecovered()));

                        int active, total, recovered, deaths;
                        active=Integer.parseInt(modelClassList.get(i).getActive());
                        total=Integer.parseInt(modelClassList.get(i).getCases());
                        recovered=Integer.parseInt(modelClassList.get(i).getRecovered());
                        deaths=Integer.parseInt(modelClassList.get(i).getDeaths());


                        updateGraph(active,total,recovered,deaths);


                    }

                }


            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });




    }

    private void updateGraph(int active, int total, int recovered, int deaths) {
        Ppiechart.clearChart();
        Ppiechart.addPieSlice(new PieModel("active",active, Color.parseColor("#FF4caf50")));
        Ppiechart.addPieSlice(new PieModel("confirmed",total, Color.parseColor("#FFB701")));
        Ppiechart.addPieSlice(new PieModel("recovered",recovered, Color.parseColor("#38ACCD")));
        Ppiechart.addPieSlice(new PieModel("deaths",deaths, Color.parseColor("#F55c47")));
        Ppiechart.startAnimation();



    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String item=types[i];
        mfilter.setText(item);
        adapter.filter(item);


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}