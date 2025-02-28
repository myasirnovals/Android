package com.techwithbob.todoapp;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class TaskEditor extends AppCompatActivity {
    private EditText titleEdit, descriptionEdit;
    private TextView dueDateText;
    private CheckBox priorityCheck;
    private RadioGroup statusGroup;
    private RadioButton activeRadio, doneRadio, delayedRadio;
    private Button saveButton, deleteButton;
    private Database database;
    private String mode;
    private String currentDate;
    private int taskId;
    private LocalDate selectedDueDate;
    private DateTimeFormatter dateFormatter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_editor);

        // Initialize views
        initializeViews();

        // Initialize database
        database = new Database(this);
        dateFormatter = DateTimeFormatter.ofPattern("E dd, LLL yyyy", Locale.UK);

        // Get intent extras
        mode = getIntent().getStringExtra("mode");
        currentDate = getIntent().getStringExtra("date");

        // Setup due date picker
        setupDueDatePicker();

        // Setup based on mode
        if (mode.equals("edit")) {
            taskId = getIntent().getIntExtra("taskId", -1);
            loadTaskData();
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        // Setup button listeners
        setupButtonListeners();
    }

    private void initializeViews() {
        titleEdit = findViewById(R.id.title);
        descriptionEdit = findViewById(R.id.description);
        dueDateText = findViewById(R.id.due_date);
        priorityCheck = findViewById(R.id.priority);
        statusGroup = findViewById(R.id.status);
        activeRadio = findViewById(R.id.active);
        doneRadio = findViewById(R.id.done);
        delayedRadio = findViewById(R.id.delayed);
        saveButton = findViewById(R.id.submit);
        deleteButton = findViewById(R.id.delete);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupDueDatePicker() {
        dueDateText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    TaskEditor.this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDueDate = LocalDate.of(year, month + 1, dayOfMonth);
                        dueDateText.setText(selectedDueDate.format(dateFormatter));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadTaskData() {
        Task task = database.readTask(currentDate, taskId);
        if (task != null) {
            titleEdit.setText(task.getTitle());
            descriptionEdit.setText(task.getDescription());
            priorityCheck.setChecked(task.isPriority());
            selectedDueDate = task.getDueDate();
            dueDateText.setText(task.getDueDateToString());

            switch (task.getStatus()) {
                case "Active":
                    activeRadio.setChecked(true);
                    break;
                case "Done":
                    doneRadio.setChecked(true);
                    break;
                case "Delayed":
                    delayedRadio.setChecked(true);
                    break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupButtonListeners() {
        saveButton.setOnClickListener(v -> saveTask());

        deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(TaskEditor.this)
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        database.deleteTask(currentDate, taskId);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveTask() {
        if (!validateInput()) {
            return;
        }

        Task task = new Task();
        if (mode.equals("edit")) {
            task.setID(taskId);
        }

        task.setTitle(titleEdit.getText().toString().trim());
        task.setDescription(descriptionEdit.getText().toString().trim());
        task.setPriority(priorityCheck.isChecked());
        task.setDueDate(selectedDueDate);

        int selectedId = statusGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.active) {
            task.setStatus("Active");
        } else if (selectedId == R.id.done) {
            task.setStatus("Done");
        } else if (selectedId == R.id.delayed) {
            task.setStatus("Delayed");
        }

        if (mode.equals("add")) {
            database.insertTask(currentDate, task);
        } else {
            database.updateTask(currentDate, task);
        }

        setResult(RESULT_OK);
        finish();
    }


    private boolean validateInput() {
        if (titleEdit.getText().toString().trim().isEmpty()) {
            titleEdit.setError("Title cannot be empty");
            return false;
        }
        if (selectedDueDate == null) {
            dueDateText.setError("Due date must be selected");
            return false;
        }
        if (statusGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a status", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void showSaveConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Save Task")
                .setMessage("Are you sure you want to save this task?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    saveTask();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
