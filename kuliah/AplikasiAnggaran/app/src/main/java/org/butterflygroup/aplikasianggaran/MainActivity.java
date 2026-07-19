package org.butterflygroup.aplikasianggaran;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "budget_prefs";
    private static final String KEY_ACTIVE_MONTH = "active_month";
    private static final long MIN_EMERGENCY_DEPOSIT = 500_000L;
    private static final long MIN_EMERGENCY_BALANCE = 100_000L;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private final SimpleDateFormat monthValueFormat = new SimpleDateFormat("yyyy-MM", Locale.US);
    private final SimpleDateFormat printableDateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID"));
    private final SimpleDateFormat monthDisplayFormat = new SimpleDateFormat("MMMM yyyy", new Locale("id", "ID"));

    private BudgetDatabaseHelper databaseHelper;
    private SharedPreferences preferences;
    private TransactionAdapter adapter;
    private final List<Transaction> allTransactions = new ArrayList<>();

    private TextView activeMonthText;
    private TextView selectedDateText;
    private TextView openingBalanceValue;
    private TextView incomeValue;
    private TextView expenseValue;
    private TextView emergencyValue;
    private TextView remainingValue;
    private TextView warningText;
    private TextView reportMonthText;
    private TextView reportDateText;
    private TextView emptyStateText;
    private RecyclerView transactionRecyclerView;
    private Spinner transactionCategorySpinner;
    private TextInputEditText amountInput;
    private TextInputEditText descriptionInput;

    private String activeMonth;
    private String selectedDate;

    private final ActivityResultLauncher<String> exportLauncher = registerForActivityResult(
            new ActivityResultContracts.CreateDocument("text/csv"),
            this::handleExportDestination
    );

    private final ActivityResultLauncher<String[]> importLauncher = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            this::handleImportSource
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new BudgetDatabaseHelper(this);
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        bindViews();
        setupTransactionTypeSpinner();
        setupRecyclerView();

        // PENGAMAN: Selalu atur tanggal, bulan, dan tahun sesuai sistem smartphone saat aplikasi dimulai
        Date now = new Date();
        activeMonth = monthValueFormat.format(now);
        selectedDate = dateFormat.format(now);
        
        preferences.edit().putString(KEY_ACTIVE_MONTH, activeMonth).apply();

        updateMonthUi();
        updateDateUi();
        renderData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }

    private void bindViews() {
        activeMonthText = findViewById(R.id.activeMonthText);
        selectedDateText = findViewById(R.id.selectedDateText);
        openingBalanceValue = findViewById(R.id.openingBalanceValue);
        incomeValue = findViewById(R.id.incomeValue);
        expenseValue = findViewById(R.id.expenseValue);
        emergencyValue = findViewById(R.id.emergencyValue);
        remainingValue = findViewById(R.id.remainingValue);
        warningText = findViewById(R.id.warningText);
        reportMonthText = findViewById(R.id.reportMonthText);
        reportDateText = findViewById(R.id.reportDateText);
        emptyStateText = findViewById(R.id.emptyStateText);
        transactionRecyclerView = findViewById(R.id.transactionRecyclerView);
        transactionCategorySpinner = findViewById(R.id.transactionCategorySpinner);
        amountInput = findViewById(R.id.amountInput);
        descriptionInput = findViewById(R.id.descriptionInput);

        MaterialButton chooseMonthButton = findViewById(R.id.chooseMonthButton);
        MaterialButton chooseDateButton = findViewById(R.id.chooseDateButton);
        MaterialButton saveButton = findViewById(R.id.saveButton);
        MaterialButton shareButton = findViewById(R.id.shareButton);
        MaterialButton printButton = findViewById(R.id.printButton);
        MaterialButton exportButton = findViewById(R.id.exportButton);
        MaterialButton importButton = findViewById(R.id.importButton);
        MaterialButton deleteAllButton = findViewById(R.id.deleteAllButton);

        if (chooseMonthButton != null) {
            chooseMonthButton.setOnClickListener(view -> showMonthPicker());
        }
        if (chooseDateButton != null) {
            chooseDateButton.setOnClickListener(view -> showDatePicker());
        }
        
        saveButton.setOnClickListener(view -> saveTransaction());
        shareButton.setOnClickListener(view -> shareReport());
        printButton.setOnClickListener(view -> shareReport());
        exportButton.setOnClickListener(view -> exportCsv());
        importButton.setOnClickListener(view -> importCsv());
        deleteAllButton.setOnClickListener(view -> confirmDeleteAll());
    }

    private void setupTransactionTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
            R.array.transaction_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transactionCategorySpinner.setAdapter(adapter);
        transactionCategorySpinner.setSelection(0);
    }

    private void setupRecyclerView() {
        adapter = new TransactionAdapter(this::confirmDeleteTransaction);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionRecyclerView.setAdapter(adapter);
    }

    private void showMonthPicker() {
        Calendar calendar = Calendar.getInstance();
        try {
            Date current = monthValueFormat.parse(activeMonth);
            if (current != null) calendar.setTime(current);
        } catch (Exception ignored) {}

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);

        final NumberPicker monthPicker = new NumberPicker(this);
        monthPicker.setMinValue(0);
        monthPicker.setMaxValue(11);
        monthPicker.setValue(currentMonth);
        monthPicker.setDisplayedValues(new String[]{
                "Januari", "Februari", "Maret", "April", "Mei", "Juni", 
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        });

        final NumberPicker yearPicker = new NumberPicker(this);
        yearPicker.setMinValue(currentYear - 10);
        yearPicker.setMaxValue(currentYear + 10);
        yearPicker.setValue(currentYear);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(50, 40, 50, 0);
        layout.addView(monthPicker);
        layout.addView(yearPicker);

        new AlertDialog.Builder(this)
                .setTitle(R.string.choose_month)
                .setView(layout)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    activeMonth = String.format(Locale.US, "%04d-%02d", yearPicker.getValue(), monthPicker.getValue() + 1);
                    preferences.edit().putString(KEY_ACTIVE_MONTH, activeMonth).apply();
                    
                    // Reset selectedDate ke awal bulan yang dipilih agar sinkron
                    selectedDate = activeMonth + "-01";
                    
                    updateMonthUi();
                    updateDateUi();
                    renderData();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void showDatePicker() {
        String[] parts = selectedDate.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1;
        int day = Integer.parseInt(parts[2]);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) -> {
                    selectedDate = String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth);
                    
                    // OTOMATIS: Update bulan aktif berdasarkan tanggal yang baru dipilih
                    String newActiveMonth = selectedDate.substring(0, 7);
                    if (!newActiveMonth.equals(activeMonth)) {
                        activeMonth = newActiveMonth;
                        preferences.edit().putString(KEY_ACTIVE_MONTH, activeMonth).apply();
                        updateMonthUi();
                    }
                    
                    updateDateUi();
                    renderData();
                },
                year,
                month,
                day
        );
        dialog.show();
    }

    private void saveTransaction() {
        String selectedCategoryLabel = String.valueOf(transactionCategorySpinner.getSelectedItem());
        TransactionCategory categoryEnum = TransactionCategory.fromDisplayLabel(selectedCategoryLabel);
        String type = categoryEnum != null ? categoryEnum.getTypeCode() : null;
        String category = categoryEnum != null ? categoryEnum.getDisplayLabel() : "";
        String amountText = safeText(amountInput);
        String description = safeText(descriptionInput);

        if (selectedDate == null || selectedDate.isEmpty()) {
            toast(getString(R.string.transaction_date_required));
            return;
        }
        if (type == null) {
            toast(getString(R.string.transaction_type_required));
            return;
        }
        if (category.isEmpty()) {
            toast(getString(R.string.category_required));
            return;
        }
        if (amountText.isEmpty()) {
            toast(getString(R.string.amount_required));
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amountText);
        } catch (NumberFormatException exception) {
            toast(getString(R.string.amount_invalid));
            return;
        }

        if (amount <= 0) {
            toast(getString(R.string.amount_invalid));
            return;
        }

        Summary summary = calculateSummary(activeMonth, allTransactions);
        if (Transaction.TYPE_EMERGENCY.equals(type) && amount > summary.availableBalanceForEmergencyDeposit()) {
            toast(getString(R.string.emergency_deposit_exceeds));
            return;
        }
        if (Transaction.TYPE_EMERGENCY_WITHDRAW.equals(type) && amount > summary.emergencyFund) {
            toast(getString(R.string.emergency_withdraw_exceeds));
            return;
        }

        long insertedId = databaseHelper.insertTransaction(new Transaction(0, selectedDate, type, category, amount, description));
        if (insertedId <= 0) {
            toast(getString(R.string.csv_read_error));
            return;
        }

        clearForm();
        renderData();
    }

    private void confirmDeleteTransaction(Transaction transaction) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage(R.string.confirm_delete_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    databaseHelper.deleteTransaction(transaction.getId());
                    renderData();
                })
                .show();
    }

    private void confirmDeleteAll() {
        if (allTransactions.isEmpty()) {
            toast(getString(R.string.no_data_to_delete));
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_all_title)
                .setMessage(R.string.confirm_delete_all_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    databaseHelper.deleteAllTransactions();
                    renderData();
                })
                .show();
    }

    private void exportCsv() {
        if (allTransactions.isEmpty()) {
            toast(getString(R.string.no_data_to_export));
            return;
        }

        String fileName = getString(R.string.export_file_prefix) + new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date()) + ".csv";
        exportLauncher.launch(fileName);
    }

    private void handleExportDestination(@Nullable Uri uri) {
        if (uri == null) {
            return;
        }

        try (OutputStream outputStream = getContentResolver().openOutputStream(uri);
             BufferedWriter writer = outputStream == null ? null : new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
            if (writer == null) {
                toast(getString(R.string.csv_read_error));
                return;
            }
            writer.write(CsvHelper.toCsv(allTransactions));
            writer.flush();
            toast(getString(R.string.export_success));
        } catch (IOException exception) {
            toast(getString(R.string.csv_read_error));
        }
    }

    private void importCsv() {
        importLauncher.launch(new String[]{"text/*", "text/csv", "application/vnd.ms-excel"});
    }

    private void handleImportSource(@Nullable Uri uri) {
        if (uri == null) {
            return;
        }

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = inputStream == null ? null : new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            if (reader == null) {
                toast(getString(R.string.csv_read_error));
                return;
            }

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }

            List<Transaction> importedTransactions = CsvHelper.parseTransactions(builder.toString());
            new AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_import_title)
                    .setMessage(R.string.confirm_import_message)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        databaseHelper.replaceAllTransactions(importedTransactions);
                        renderData();
                        toast(getString(R.string.import_success));
                    })
                    .show();
        } catch (IOException exception) {
            toast(getString(R.string.csv_read_error));
        } catch (IllegalArgumentException exception) {
            toast(exception.getMessage());
        }
    }

    private void shareReport() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, buildReportText());
        startActivity(Intent.createChooser(intent, getString(R.string.share_chooser)));
    }

    private void renderData() {
        allTransactions.clear();
        allTransactions.addAll(databaseHelper.getAllTransactionsOrdered());

        List<Transaction> activeTransactions = getActiveTransactions();
        adapter.setItems(activeTransactions);
        emptyStateText.setVisibility(activeTransactions.isEmpty() ? View.VISIBLE : View.GONE);

        Summary summary = calculateSummary(activeMonth, allTransactions);
        openingBalanceValue.setText(formatCurrency(summary.openingBalance));
        incomeValue.setText(formatCurrency(summary.incomeTotal));
        expenseValue.setText(formatCurrency(summary.expenseTotal));
        emergencyValue.setText(formatCurrency(summary.emergencyFund));
        remainingValue.setText(formatCurrency(summary.remainingBudget));
        warningText.setText(summary.warningMessage);
        
        updateMonthUi();
        reportMonthText.setText(getString(R.string.report_month_display, formatMonthDisplay(activeMonth)));
        reportDateText.setText(getString(R.string.report_print_date, printableDateFormat.format(new Date())));
    }

    private void updateMonthUi() {
        if (activeMonthText != null) {
            activeMonthText.setText(getString(R.string.active_month_display, formatMonthDisplay(activeMonth)));
        }
    }

    private void updateDateUi() {
        if (selectedDateText != null) {
            selectedDateText.setText(selectedDate);
        }
    }

    private String formatMonthDisplay(String yearMonth) {
        try {
            Date date = monthValueFormat.parse(yearMonth);
            return date != null ? monthDisplayFormat.format(date) : yearMonth;
        } catch (Exception e) {
            return yearMonth;
        }
    }

    private void clearForm() {
        transactionCategorySpinner.setSelection(0);
        amountInput.setText("");
        descriptionInput.setText("");
        updateDateUi();
    }

    private String buildReportText() {
        StringBuilder builder = new StringBuilder();
        Summary summary = calculateSummary(activeMonth, allTransactions);
        builder.append(getString(R.string.app_title)).append('\n');
        builder.append(getString(R.string.report_month_display, formatMonthDisplay(activeMonth))).append('\n');
        builder.append(getString(R.string.report_print_date, printableDateFormat.format(new Date()))).append('\n');
        builder.append('\n');
        builder.append(getString(R.string.opening_balance)).append(": ").append(formatCurrency(summary.openingBalance)).append('\n');
        builder.append(getString(R.string.opening_emergency_fund)).append(": ").append(formatCurrency(summary.openingEmergencyFund)).append('\n');
        builder.append(getString(R.string.income_total)).append(": ").append(formatCurrency(summary.incomeTotal)).append('\n');
        builder.append(getString(R.string.expense_total)).append(": ").append(formatCurrency(summary.expenseTotal)).append('\n');
        builder.append(getString(R.string.emergency_fund)).append(": ").append(formatCurrency(summary.emergencyFund)).append('\n');
        builder.append(getString(R.string.remaining_budget)).append(": ").append(formatCurrency(summary.remainingBudget)).append('\n');
        builder.append(getString(R.string.warning_title)).append(": ").append(summary.warningMessage).append('\n');
        builder.append('\n').append(getString(R.string.table_title)).append('\n');

        List<Transaction> activeTransactions = getActiveTransactions();
        for (int index = 0; index < activeTransactions.size(); index++) {
            Transaction transaction = activeTransactions.get(index);
            builder.append(index + 1).append('.').append(' ')
                    .append(transaction.getDate()).append(' ')
                    .append(Transaction.getDisplayLabel(transaction.getType())).append(' ')
                    .append(transaction.getCategory()).append(' ')
                    .append(formatCurrency(transaction.getAmount())).append(' ')
                    .append(transaction.getDescription().isEmpty() ? getString(R.string.no_description) : transaction.getDescription())
                    .append('\n');
        }
        return builder.toString();
    }

    private List<Transaction> getActiveTransactions() {
        List<Transaction> activeTransactions = new ArrayList<>();
        for (Transaction transaction : allTransactions) {
            if (isDateInActiveMonth(transaction.getDate())) {
                activeTransactions.add(transaction);
            }
        }
        return activeTransactions;
    }

    private Summary calculateSummary(String activeMonthValue, List<Transaction> transactions) {
        String firstDay = getFirstDayOfMonth(activeMonthValue);
        String lastDay = getLastDayOfMonth(activeMonthValue);

        long openingBalance = 0L;
        long incomeTotal = 0L;
        long expenseTotal = 0L;
        long emergencyDepositTotal = 0L;
        long emergencyWithdrawTotal = 0L;

        for (Transaction transaction : transactions) {
            String date = transaction.getDate();
            if (date.compareTo(firstDay) < 0) {
                // Skenario baru: Sisa dana darurat bulan lalu otomatis jadi saldo awal bulan ini
                if (Transaction.TYPE_INCOME.equals(transaction.getType())) {
                    openingBalance += transaction.getAmount();
                } else if (Transaction.TYPE_EXPENSE.equals(transaction.getType())) {
                    openingBalance -= transaction.getAmount();
                } else if (Transaction.TYPE_EMERGENCY.equals(transaction.getType())) {
                    // Setoran tidak mengurangi openingBalance karena dana rolling di saldo utama
                } else if (Transaction.TYPE_EMERGENCY_WITHDRAW.equals(transaction.getType())) {
                    openingBalance -= transaction.getAmount();
                }
            } else if (date.compareTo(firstDay) >= 0 && date.compareTo(lastDay) <= 0) {
                if (Transaction.TYPE_INCOME.equals(transaction.getType())) {
                    incomeTotal += transaction.getAmount();
                } else if (Transaction.TYPE_EXPENSE.equals(transaction.getType())) {
                    expenseTotal += transaction.getAmount();
                } else if (Transaction.TYPE_EMERGENCY.equals(transaction.getType())) {
                    emergencyDepositTotal += transaction.getAmount();
                } else if (Transaction.TYPE_EMERGENCY_WITHDRAW.equals(transaction.getType())) {
                    emergencyWithdrawTotal += transaction.getAmount();
                }
            }
        }

        long emergencyFund = emergencyDepositTotal - emergencyWithdrawTotal;
        long totalExpense = expenseTotal + emergencyDepositTotal;
        long remainingBudget = openingBalance + incomeTotal - totalExpense;
        String warningMessage = buildWarningMessage(emergencyDepositTotal, emergencyFund);
        
        return new Summary(openingBalance, 0, incomeTotal, totalExpense, emergencyFund, remainingBudget, warningMessage);
    }

    private String buildWarningMessage(long emergencyDepositTotal, long emergencyFund) {
        if (emergencyDepositTotal < MIN_EMERGENCY_DEPOSIT) {
            return getString(R.string.warning_deposit_low);
        }
        if (emergencyFund < MIN_EMERGENCY_BALANCE) {
            return getString(R.string.warning_balance_low);
        }
        return getString(R.string.warning_safe);
    }

    private boolean isDateInActiveMonth(String date) {
        return date != null && activeMonth != null && date.startsWith(activeMonth);
    }

    private String getFirstDayOfMonth(String monthValue) {
        return monthValue + "-01";
    }

    private String getLastDayOfMonth(String monthValue) {
        Calendar calendar = Calendar.getInstance();
        String[] parts = monthValue.split("-");
        calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, 1);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return String.format(Locale.US, "%04d-%02d-%02d", Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), lastDay);
    }

    private String safeText(TextInputEditText input) {
        CharSequence text = input.getText();
        return text == null ? "" : text.toString().trim();
    }

    private String formatCurrency(long amount) {
        return currencyFormat.format(amount);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private static class Summary {
        private final long openingBalance;
        private final long openingEmergencyFund;
        private final long incomeTotal;
        private final long expenseTotal;
        private final long emergencyFund;
        private final long remainingBudget;
        private final String warningMessage;

        private Summary(long openingBalance, long openingEmergencyFund, long incomeTotal, long expenseTotal, long emergencyFund, long remainingBudget, String warningMessage) {
            this.openingBalance = openingBalance;
            this.openingEmergencyFund = openingEmergencyFund;
            this.incomeTotal = incomeTotal;
            this.expenseTotal = expenseTotal;
            this.emergencyFund = emergencyFund;
            this.remainingBudget = remainingBudget;
            this.warningMessage = warningMessage;
        }

        private long availableBalanceForEmergencyDeposit() {
            return openingBalance + incomeTotal - expenseTotal;
        }
    }
}
