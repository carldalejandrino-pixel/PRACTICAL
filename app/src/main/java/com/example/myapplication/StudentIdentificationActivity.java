package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StudentIdentificationActivity extends AppCompatActivity {

    private EditText etStudentId;
    private TextView tvStudentInfo;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_identification);

        databaseHelper = new DatabaseHelper(this);

        etStudentId = findViewById(R.id.etStudentId);
        tvStudentInfo = findViewById(R.id.tvStudentInfo);

        Button btnSearchStudent = findViewById(R.id.btnSearchStudent);
        Button btnBack = findViewById(R.id.btnBack);

        btnSearchStudent.setOnClickListener(v -> searchStudent());
        btnBack.setOnClickListener(v -> finish());
    }

    private void searchStudent() {
        String studentId = etStudentId.getText().toString().trim();

        if (studentId.isEmpty()) {
            etStudentId.setError("Enter a student ID");
            etStudentId.requestFocus();
            return;
        }

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        try (Cursor cursor = db.query(
                "students",
                new String[]{
                        "student_id",
                        "first_name",
                        "last_name",
                        "grade",
                        "section",
                        "gender",
                        "age",
                        "contact"
                },
                "student_id = ?",
                new String[]{studentId},
                null,
                null,
                null
        )) {
            if (cursor.moveToFirst()) {
                String id = cursor.getString(
                        cursor.getColumnIndexOrThrow("student_id"));
                String firstName = cursor.getString(
                        cursor.getColumnIndexOrThrow("first_name"));
                String lastName = cursor.getString(
                        cursor.getColumnIndexOrThrow("last_name"));
                String grade = cursor.getString(
                        cursor.getColumnIndexOrThrow("grade"));
                String section = cursor.getString(
                        cursor.getColumnIndexOrThrow("section"));
                String gender = cursor.getString(
                        cursor.getColumnIndexOrThrow("gender"));
                int age = cursor.getInt(
                        cursor.getColumnIndexOrThrow("age"));
                String contact = cursor.getString(
                        cursor.getColumnIndexOrThrow("contact"));

                String fullName = (firstName + " " + lastName).trim();

                tvStudentInfo.setText(
                        "STUDENT INFORMATION\n\n" +
                                "Student ID: " + id + "\n\n" +
                                "Full Name: " + fullName + "\n\n" +
                                "Grade: " + grade + "\n\n" +
                                "Section: " + section + "\n\n" +
                                "Gender: " + (gender == null || gender.isEmpty()
                                ? "Not provided" : gender) + "\n\n" +
                                "Age: " + age + "\n\n" +
                                "Contact: " + (contact == null || contact.isEmpty()
                                ? "Not provided" : contact)
                );
            } else {
                tvStudentInfo.setText("No student found.");
                Toast.makeText(
                        this,
                        "Student ID not found.",
                        Toast.LENGTH_SHORT
                ).show();
            }
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Error searching for student: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
