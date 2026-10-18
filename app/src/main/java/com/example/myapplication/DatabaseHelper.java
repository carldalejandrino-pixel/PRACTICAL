package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "school_clinic.db";
    private static final int DATABASE_VERSION = 4;

    private static final String TABLE_STUDENTS = "students";
    private static final String COL_STUDENT_ID = "student_id";
    private static final String COL_FIRST_NAME = "first_name";
    private static final String COL_LAST_NAME = "last_name";
    private static final String COL_GRADE = "grade";
    private static final String COL_SECTION = "section";
    private static final String COL_GENDER = "gender";
    private static final String COL_AGE = "age";
    private static final String COL_CONTACT = "contact";

    private static final String TABLE_MEDICAL = "medical_records";
    private static final String COL_ALLERGIES = "allergies";
    private static final String COL_CONDITIONS = "medical_conditions";
    private static final String COL_MEDICATIONS = "medications";
    private static final String COL_BLOOD_TYPE = "blood_type";
    private static final String COL_MEDICAL_NOTES = "notes";

    private static final String TABLE_VISITS = "clinic_visits";
    private static final String COL_VISIT_ID = "visit_id";
    private static final String COL_VISIT_DATE = "visit_date";
    private static final String COL_VISIT_TIME = "visit_time";
    private static final String COL_SYMPTOMS = "symptoms";
    private static final String COL_TEMPERATURE = "temperature";
    private static final String COL_TREATMENT = "treatment";
    private static final String COL_VISIT_NOTES = "notes";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_STUDENTS + " (" +
                COL_STUDENT_ID + " TEXT PRIMARY KEY, " +
                COL_FIRST_NAME + " TEXT NOT NULL, " +
                COL_LAST_NAME + " TEXT NOT NULL, " +
                COL_GRADE + " TEXT, " +
                COL_SECTION + " TEXT, " +
                COL_GENDER + " TEXT, " +
                COL_AGE + " TEXT, " +
                COL_CONTACT + " TEXT)");

        createMedicalTable(db);
        createVisitsTable(db);
    }

    private void createMedicalTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MEDICAL + " (" +
                COL_STUDENT_ID + " TEXT PRIMARY KEY, " +
                COL_ALLERGIES + " TEXT, " +
                COL_CONDITIONS + " TEXT, " +
                COL_MEDICATIONS + " TEXT, " +
                COL_BLOOD_TYPE + " TEXT, " +
                COL_MEDICAL_NOTES + " TEXT, " +
                "FOREIGN KEY(" + COL_STUDENT_ID + ") REFERENCES " +
                TABLE_STUDENTS + "(" + COL_STUDENT_ID + "))");
    }

    private void createVisitsTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_VISITS + " (" +
                COL_VISIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_STUDENT_ID + " TEXT NOT NULL, " +
                COL_VISIT_DATE + " TEXT NOT NULL, " +
                COL_VISIT_TIME + " TEXT NOT NULL DEFAULT '', " +
                COL_SYMPTOMS + " TEXT, " +
                COL_TEMPERATURE + " TEXT, " +
                COL_TREATMENT + " TEXT, " +
                COL_VISIT_NOTES + " TEXT, " +
                "FOREIGN KEY(" + COL_STUDENT_ID + ") REFERENCES " +
                TABLE_STUDENTS + "(" + COL_STUDENT_ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            createMedicalTable(db);
        }

        if (oldVersion < 3) {
            createVisitsTable(db);
        }

        if (oldVersion < 4) {
            if (tableExists(db, TABLE_VISITS) &&
                    !columnExists(db, TABLE_VISITS, COL_VISIT_TIME)) {
                db.execSQL("ALTER TABLE " + TABLE_VISITS +
                        " ADD COLUMN " + COL_VISIT_TIME +
                        " TEXT NOT NULL DEFAULT ''");
            }
        }
    }

    private boolean tableExists(SQLiteDatabase db, String tableName) {
        try (Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                new String[]{tableName})) {
            return cursor.moveToFirst();
        }
    }

    private boolean columnExists(
            SQLiteDatabase db, String tableName, String columnName) {

        try (Cursor cursor = db.rawQuery(
                "PRAGMA table_info(" + tableName + ")", null)) {

            int nameIndex = cursor.getColumnIndex("name");

            while (cursor.moveToNext()) {
                if (nameIndex >= 0 &&
                        columnName.equals(cursor.getString(nameIndex))) {
                    return true;
                }
            }
        }

        return false;
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

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_FIRST_NAME, firstName);
        values.put(COL_LAST_NAME, lastName);
        values.put(COL_GRADE, grade);
        values.put(COL_SECTION, section);
        values.put(COL_GENDER, gender);
        values.put(COL_AGE, age);
        values.put(COL_CONTACT, contact);

        return db.insert(TABLE_STUDENTS, null, values) != -1;
    }

    // Compatibility method for the five arguments used by your registration screen.
    public boolean addStudent(
            String studentId,
            String studentName,
            int age,
            String grade,
            String section) {

        return addStudent(
                studentId,
                studentName,
                "",
                grade,
                section,
                "",
                String.valueOf(age),
                ""
        );
    }

    public boolean studentExists(String studentId) {
        try (Cursor cursor = getReadableDatabase().query(
                TABLE_STUDENTS,
                new String[]{COL_STUDENT_ID},
                COL_STUDENT_ID + " = ?",
                new String[]{studentId},
                null, null, null)) {

            return cursor.moveToFirst();
        }
    }

    public Cursor getAllStudents() {
        SQLiteDatabase db = getReadableDatabase();

        // Sort by the primary key instead of a possibly missing last_name column.
        return db.query(
                TABLE_STUDENTS,
                null,
                null,
                null,
                null,
                null,
                COL_STUDENT_ID + " ASC"
        );
    }

    public Cursor getMedicalRecord(String studentId) {
        return getReadableDatabase().query(
                TABLE_MEDICAL,
                null,
                COL_STUDENT_ID + " = ?",
                new String[]{studentId},
                null, null, null
        );
    }

    public boolean saveMedicalRecord(
            String studentId,
            String allergies,
            String conditions,
            String medications,
            String bloodType,
            String notes) {

        ContentValues values = new ContentValues();
        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_ALLERGIES, allergies);
        values.put(COL_CONDITIONS, conditions);
        values.put(COL_MEDICATIONS, medications);
        values.put(COL_BLOOD_TYPE, bloodType);
        values.put(COL_MEDICAL_NOTES, notes);

        return getWritableDatabase().insertWithOnConflict(
                TABLE_MEDICAL,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        ) != -1;
    }

    public boolean saveMedicalRecord(
            String studentId,
            String allergies,
            String conditions,
            String medications,
            String notes) {

        return saveMedicalRecord(
                studentId, allergies, conditions, medications, "", notes
        );
    }

    public boolean addClinicVisit(
            String studentId,
            String visitDate,
            String visitTime,
            String symptoms,
            String temperature,
            String treatment,
            String notes) {

        ContentValues values = new ContentValues();
        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_VISIT_DATE, visitDate);
        values.put(COL_VISIT_TIME, visitTime);
        values.put(COL_SYMPTOMS, symptoms);
        values.put(COL_TEMPERATURE, temperature);
        values.put(COL_TREATMENT, treatment);
        values.put(COL_VISIT_NOTES, notes);

        return getWritableDatabase().insert(
                TABLE_VISITS, null, values
        ) != -1;
    }

    public Cursor getClinicVisits(String studentId) {
        return getReadableDatabase().query(
                TABLE_VISITS,
                null,
                COL_STUDENT_ID + " = ?",
                new String[]{studentId},
                null,
                null,
                COL_VISIT_DATE + " DESC, " + COL_VISIT_TIME + " DESC"
        );
    }

    public Cursor getAllClinicVisits() {
        return getReadableDatabase().query(
                TABLE_VISITS,
                null,
                null,
                null,
                null,
                null,
                COL_VISIT_DATE + " DESC, " + COL_VISIT_TIME + " DESC"
        );
    }
}
