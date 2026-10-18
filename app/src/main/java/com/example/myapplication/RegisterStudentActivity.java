
package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterStudentActivity extends AppCompatActivity {

    private EditText etStudentId;
    private EditText etStudentName;
    private EditText etGradeSection;
    private EditText etAge;
    private EditText etContact;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        databaseHelper = new DatabaseHelper(this);

        etStudentId = findViewById(R.id.etStudentId);
        etStudentName = findViewById(R.id.etStudentName);
        etGradeSection = findViewById(R.id.etGradeSection);
        etAge = findViewById(R.id.etAge);
        etContact = findViewById(R.id.etContact);

        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnBack = findViewById(R.id.btnBack);

        btnRegister.setOnClickListener(v -> registerStudent());
        btnBack.setOnClickListener(v -> finish());
    }

    private void registerStudent() {
        String studentId = etStudentId.getText().toString().trim();
        String studentName = etStudentName.getText().toString().trim();
        String gradeSection = etGradeSection.getText().toString().trim();
        String ageText = etAge.getText().toString().trim();
        String contact = etContact.getText().toString().trim();

        if (studentId.isEmpty() || studentName.isEmpty()
                || gradeSection.isEmpty() || ageText.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in all required fields.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String[] nameParts = studentName.split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        String[] gradeParts = gradeSection.split("\\s+", 2);
        String grade = gradeParts[0];
        String section = gradeParts.length > 1 ? gradeParts[1] : "";

        int age;

        try {
            age = Integer.parseInt(ageText);

            if (age <= 0 || age > 120) {
                Toast.makeText(this,
                        "Please enter a valid age.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Age must be a valid number.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.studentExists(studentId)) {
            Toast.makeText(this,
                    "This student ID is already registered.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = databaseHelper.addStudent(
                studentId,
                firstName,
                lastName,
                grade,
                section,
                "",
                String.valueOf(age),
                contact
        );

        if (success) {
            Toast.makeText(this,
                    "Student registered successfully!",
                    Toast.LENGTH_SHORT).show();

            etStudentId.setText("");
            etStudentName.setText("");
            etGradeSection.setText("");
            etAge.setText("");
            etContact.setText("");
        } else {
            Toast.makeText(this,
                    "Registration failed. Please try again.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
