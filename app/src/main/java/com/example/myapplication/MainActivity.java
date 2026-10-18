package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnStudentIdentification;
    private Button btnRegisterStudent;
    private Button btnManageMedicalRecord;
    private Button btnRecordClinicVisit;
    private Button btnViewStudentRecord;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStudentIdentification =
                findViewById(R.id.btnStudentIdentification);
        btnRegisterStudent =
                findViewById(R.id.btnRegisterStudent);
        btnManageMedicalRecord =
                findViewById(R.id.btnManageMedicalRecord);
        btnRecordClinicVisit =
                findViewById(R.id.btnRecordClinicVisit);
        btnViewStudentRecord =
                findViewById(R.id.btnViewStudentRecord);
        btnLogout = findViewById(R.id.btnLogout);

        btnStudentIdentification.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        StudentIdentificationActivity.class)));

        btnRegisterStudent.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        RegisterStudentActivity.class)));

        btnManageMedicalRecord.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        ManageMedicalRecordActivity.class)));

        btnRecordClinicVisit.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        RecordClinicVisitActivity.class)));

        btnViewStudentRecord.setOnClickListener(v ->
                startActivity(new Intent(
                        MainActivity.this,
                        ViewStudentsActivity.class)));

        btnLogout.setOnClickListener(v -> finish());
    }
}
