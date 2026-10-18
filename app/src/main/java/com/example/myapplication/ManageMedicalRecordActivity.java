package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ManageMedicalRecordActivity extends AppCompatActivity {

    private EditText etStudentId;
    private EditText etAllergies;
    private EditText etConditions;
    private EditText etMedications;
    private EditText etMedicalNotes;

    private Button btnLoadMedicalRecord;
    private Button btnSaveMedicalRecord;
    private Button btnMedicalBack;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_medical_record);

        dbHelper = new DatabaseHelper(this);

        etStudentId = findViewById(R.id.etMedicalStudentId);
        etAllergies = findViewById(R.id.etAllergies);
        etConditions = findViewById(R.id.etConditions);
        etMedications = findViewById(R.id.etMedications);
        etMedicalNotes = findViewById(R.id.etMedicalNotes);

        btnLoadMedicalRecord = findViewById(R.id.btnLoadMedicalRecord);
        btnSaveMedicalRecord = findViewById(R.id.btnSaveMedicalRecord);
        btnMedicalBack = findViewById(R.id.btnMedicalBack);

        btnLoadMedicalRecord.setOnClickListener(v -> loadMedicalRecord());
        btnSaveMedicalRecord.setOnClickListener(v -> saveMedicalRecord());
        btnMedicalBack.setOnClickListener(v -> finish());
    }

    private void loadMedicalRecord() {
        String studentId = etStudentId.getText().toString().trim();

        if (studentId.isEmpty()) {
            etStudentId.setError("Enter a student ID");
            etStudentId.requestFocus();
            return;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try (Cursor studentCursor = db.query(
                "students",
                new String[]{"student_id"},
                "student_id = ?",
                new String[]{studentId},
                null,
                null,
                null
        )) {
            if (!studentCursor.moveToFirst()) {
                Toast.makeText(this, "Student not found.", Toast.LENGTH_SHORT).show();
                clearMedicalFields();
                return;
            }
        }

        try (Cursor cursor = db.query(
                "medical_records",
                new String[]{
                        "allergies",
                        "medical_conditions",
                        "medications",
                        "notes"
                },
                "student_id = ?",
                new String[]{studentId},
                null,
                null,
                null
        )) {
            if (cursor.moveToFirst()) {
                etAllergies.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow("allergies")));
                etConditions.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow("medical_conditions")));
                etMedications.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow("medications")));
                etMedicalNotes.setText(cursor.getString(
                        cursor.getColumnIndexOrThrow("notes")));

                Toast.makeText(this, "Medical record loaded.", Toast.LENGTH_SHORT).show();
            } else {
                clearMedicalFields();
                Toast.makeText(
                        this,
                        "No medical record found. You can create one.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Could not load medical record: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void saveMedicalRecord() {
        String studentId = etStudentId.getText().toString().trim();

        if (studentId.isEmpty()) {
            etStudentId.setError("Enter a student ID");
            etStudentId.requestFocus();
            return;
        }

        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            try (Cursor cursor = db.query(
                    "students",
                    new String[]{"student_id"},
                    "student_id = ?",
                    new String[]{studentId},
                    null,
                    null,
                    null
            )) {
                if (!cursor.moveToFirst()) {
                    Toast.makeText(this, "Student not found.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            boolean saved = dbHelper.saveMedicalRecord(
                    studentId,
                    etAllergies.getText().toString().trim(),
                    etConditions.getText().toString().trim(),
                    etMedications.getText().toString().trim(),
                    etMedicalNotes.getText().toString().trim()
            );

            if (saved) {
                Toast.makeText(this, "Medical record saved.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Could not save medical record.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Error saving medical record: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private void clearMedicalFields() {
        etAllergies.setText("");
        etConditions.setText("");
        etMedications.setText("");
        etMedicalNotes.setText("");
    }
}