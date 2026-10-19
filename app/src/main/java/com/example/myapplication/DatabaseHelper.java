package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "school_clinic.db";
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_STUDENTS = "students";
    private static final String TABLE_MEDICAL_RECORDS = "medical_records";
    private static final String TABLE_CLINIC_VISITS = "clinic_visits";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS students (" +
                "student_id TEXT PRIMARY KEY, " +
                "full_name TEXT, " +
                "grade_section TEXT, " +
                "first_name TEXT, " +
                "last_name TEXT NOT NULL DEFAULT '', " +
                "grade TEXT, " +
                "section TEXT, " +
                "gender TEXT, " +
                "age TEXT, " +
                "contact TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS medical_records (" +
                "student_id TEXT PRIMARY KEY, " +
                "allergies TEXT, " +
                "medical_conditions TEXT, " +
                "medications TEXT, " +
                "blood_type TEXT, " +
                "notes TEXT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS clinic_visits (" +
                "visit_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id TEXT, " +
                "visit_date TEXT, " +
                "visit_time TEXT DEFAULT '', " +
                "symptoms TEXT, " +
                "temperature TEXT, " +
                "treatment TEXT, " +
                "notes TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS medical_records (" +
                    "student_id TEXT PRIMARY KEY, " +
                    "allergies TEXT, " +
                    "medical_conditions TEXT, " +
                    "medications TEXT, " +
                    "blood_type TEXT, " +
                    "notes TEXT)");
        }

        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS clinic_visits (" +
                    "visit_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "student_id TEXT, " +
                    "visit_date TEXT, " +
                    "visit_time TEXT DEFAULT '', " +
                    "symptoms TEXT, " +
                    "temperature TEXT, " +
                    "treatment TEXT, " +
                    "notes TEXT)");
        }

        if (oldVersion < 4) {
            if (tableExists(db, TABLE_CLINIC_VISITS)
                    && !columnExists(db, TABLE_CLINIC_VISITS, "visit_time")) {
                db.execSQL("ALTER TABLE clinic_visits " +
                        "ADD COLUMN visit_time TEXT DEFAULT ''");
            }
        }

        if (oldVersion < 5) {
            if (tableExists(db, TABLE_STUDENTS)) {
                addColumnIfMissing(db, "first_name", "TEXT");
                addColumnIfMissing(db, "last_name",
                        "TEXT NOT NULL DEFAULT ''");
                addColumnIfMissing(db, "grade", "TEXT");
                addColumnIfMissing(db, "section", "TEXT");
                addColumnIfMissing(db, "gender", "TEXT");

                if (columnExists(db, TABLE_STUDENTS, "full_name")) {
                    db.execSQL(
                            "UPDATE students SET first_name = " +
                                    "CASE WHEN first_name IS NULL OR first_name = '' " +
                                    "THEN TRIM(full_name) ELSE first_name END " +
                                    "WHERE full_name IS NOT NULL");

                    db.execSQL(
                            "UPDATE students SET last_name = '' " +
                                    "WHERE last_name IS NULL");
                }

                if (columnExists(db, TABLE_STUDENTS, "grade_section")) {
                    db.execSQL(
                            "UPDATE students SET grade = " +
                                    "CASE WHEN grade IS NULL OR grade = '' " +
                                    "THEN grade_section ELSE grade END " +
                                    "WHERE grade_section IS NOT NULL");
                }
            }
        }
    }

    private boolean tableExists(SQLiteDatabase db, String tableName) {
        try (Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master " +
                        "WHERE type='table' AND name=?",
                new String[]{tableName})) {
            return cursor.moveToFirst();
        }
    }

    private boolean columnExists(
            SQLiteDatabase db, String tableName, String columnName) {
        try (Cursor cursor = db.rawQuery(
                "PRAGMA table_info(" + tableName + ")", null)) {
            while (cursor.moveToNext()) {
                if (columnName.equals(cursor.getString(
                        cursor.getColumnIndexOrThrow("name")))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addColumnIfMissing(
            SQLiteDatabase db, String columnName, String definition) {
        if (!columnExists(db, TABLE_STUDENTS, columnName)) {
            db.execSQL("ALTER TABLE students ADD COLUMN " +
                    columnName + " " + definition);
        }
    }

    public boolean addStudent(
            String studentId,
            String firstName,
            String lastName,
            String grade,
            String section,
            String gender,
            String age,
            String contact) {

        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("full_name", (firstName + " " + lastName).trim());
        values.put("grade", grade);
        values.put("section", section);
        values.put("grade_section",
                (grade + " " + section).trim());
        values.put("gender", gender);
        values.put("age", age);
        values.put("contact", contact);

        try {
            long result = getWritableDatabase().insertOrThrow(
                    TABLE_STUDENTS, null, values);
            return result != -1;
        } catch (android.database.sqlite.SQLiteException e) {
            Log.e("StudentRegistration", "Could not register student", e);
            throw e;
        }
    }

    public boolean addStudent(
            String studentId,
            String studentName,
            int age,
            String grade,
            String section) {

        String[] parts = studentName.trim().split("\\s+", 2);
        String firstName = parts[0];
        String lastName = parts.length > 1 ? parts[1] : "";

        return addStudent(
                studentId, firstName, lastName,
                grade, section, "", String.valueOf(age), "");
    }

    public boolean studentExists(String studentId) {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT student_id FROM students WHERE student_id = ?",
                new String[]{studentId})) {
            return cursor.moveToFirst();
        }
    }

    public Cursor getAllStudents() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM students ORDER BY last_name, first_name",
                null);
    }

    public Cursor getMedicalRecord(String studentId) {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM medical_records WHERE student_id = ?",
                new String[]{studentId});
    }

    public boolean saveMedicalRecord(
            String studentId, String allergies, String conditions,
            String medications, String bloodType, String notes) {

        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("allergies", allergies);
        values.put("medical_conditions", conditions);
        values.put("medications", medications);
        values.put("blood_type", bloodType);
        values.put("notes", notes);

        return getWritableDatabase().insertWithOnConflict(
                TABLE_MEDICAL_RECORDS, null, values,
                SQLiteDatabase.CONFLICT_REPLACE) != -1;
    }

    public boolean saveMedicalRecord(
            String studentId, String allergies, String conditions,
            String medications, String notes) {
        return saveMedicalRecord(
                studentId, allergies, conditions, medications, "", notes);
    }

    public boolean addClinicVisit(
            String studentId, String date, String time, String symptoms,
            String temperature, String treatment, String notes) {

        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("visit_date", date);
        values.put("visit_time", time);
        values.put("symptoms", symptoms);
        values.put("temperature", temperature);
        values.put("treatment", treatment);
        values.put("notes", notes);

        return getWritableDatabase().insert(
                TABLE_CLINIC_VISITS, null, values) != -1;
    }

    public Cursor getClinicVisits(String studentId) {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM clinic_visits " +
                        "WHERE student_id = ? ORDER BY visit_id DESC",
                new String[]{studentId});
    }

    public Cursor getAllClinicVisits() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM clinic_visits ORDER BY visit_id DESC",
                null);
    }
}
