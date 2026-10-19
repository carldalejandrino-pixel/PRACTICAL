package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStudentIdentification =
                findViewById(R.id.btnStudentIdentification);
        Button btnRegisterStudent =
                findViewById(R.id.btnRegisterStudent);
        Button btnManageMedicalRecord =
                findViewById(R.id.btnManageMedicalRecord);
        Button btnRecordClinicVisit =
                findViewById(R.id.btnRecordClinicVisit);
        Button btnViewStudentRecord =
                findViewById(R.id.btnViewStudentRecord);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnStudentIdentification.setOnClickListener(v ->
                openScreen(StudentIdentificationActivity.class));

        btnRegisterStudent.setOnClickListener(v ->
                openScreen(RegisterStudentActivity.class));

        btnManageMedicalRecord.setOnClickListener(v ->
                openScreen(ManageMedicalRecordActivity.class));

        btnRecordClinicVisit.setOnClickListener(v ->
                openScreen(RecordClinicVisitActivity.class));

        btnViewStudentRecord.setOnClickListener(v ->
                openScreen(ViewStudentsActivity.class));

        btnLogout.setOnClickListener(v -> finish());
    }

    private void openScreen(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}
