package com.example.infinitescroling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.infinitescroling.adapters.EditAcademicAdapter;
import com.example.infinitescroling.models.AcademicInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EditProfileActivity extends AppCompatActivity {

    private ListView academicListView;
    private ArrayList<AcademicInfo> academics;
    private EditAcademicAdapter adapter;
    private Spinner editGenderSpinner;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        academicListView = findViewById(R.id.ListView_academics);
        academics = new ArrayList<>();
        //TODO populate with firestorm info instead
        try {
            academics.add(new AcademicInfo("Bachillerato", "Lic. Alfaro ruiz", simpleDateFormat.parse("15/02/2013"), new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        adapter = new EditAcademicAdapter(this, academics);
        academicListView.setAdapter(adapter);

        editGenderSpinner = findViewById(R.id.spinner_genderSelection);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.strArr_genders, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editGenderSpinner.setAdapter(adapter);

        //TODO fill form spaces with the actual information of the user
        //TODO listing academic information and editing it
    }
}
