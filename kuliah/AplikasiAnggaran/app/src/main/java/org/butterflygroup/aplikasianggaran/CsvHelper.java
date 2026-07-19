package org.butterflygroup.aplikasianggaran;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class CsvHelper {
    private static final String HEADER = "id,date,type,category,amount,description";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private CsvHelper() {
    }

    public static String toCsv(List<Transaction> transactions) {
        StringBuilder builder = new StringBuilder();
        builder.append(HEADER).append('\n');
        if (transactions != null) {
            for (Transaction transaction : transactions) {
                builder.append(transaction.getId()).append(',')
                        .append(escape(transaction.getDate())).append(',')
                        .append(escape(transaction.getType())).append(',')
                        .append(escape(transaction.getCategory())).append(',')
                        .append(transaction.getAmount()).append(',')
                        .append(escape(transaction.getDescription())).append('\n');
            }
        }
        return builder.toString();
    }

    public static List<Transaction> parseTransactions(String csvContent) {
        if (csvContent == null || csvContent.trim().isEmpty()) {
            throw new IllegalArgumentException("File CSV kosong.");
        }

        String normalized = csvContent.replace("\r\n", "\n").replace('\r', '\n');
        String[] rawLines = normalized.split("\n");
        List<String> lines = new ArrayList<>();
        for (String rawLine : rawLines) {
            if (!rawLine.trim().isEmpty()) {
                lines.add(rawLine);
            }
        }

        if (lines.isEmpty()) {
            throw new IllegalArgumentException("File CSV kosong.");
        }

        String header = stripBom(lines.get(0)).trim();
        if (!HEADER.equalsIgnoreCase(header)) {
            throw new IllegalArgumentException("Header CSV tidak sesuai format yang diharapkan.");
        }

        List<Transaction> transactions = new ArrayList<>();
        for (int index = 1; index < lines.size(); index++) {
            List<String> columns = parseLine(lines.get(index));
            if (columns.size() != 6) {
                throw new IllegalArgumentException("Baris CSV tidak valid.");
            }

            long id = parseLong(columns.get(0));
            String date = columns.get(1).trim();
            String type = columns.get(2).trim();
            String category = columns.get(3).trim();
            long amount = parseLong(columns.get(4));
            String description = columns.get(5).trim();

            if (!isValidDate(date) || !Transaction.isSupportedType(type) || category.isEmpty() || amount <= 0) {
                throw new IllegalArgumentException("Baris CSV tidak valid.");
            }

            transactions.add(new Transaction(id, date, type, category, amount, description));
        }

        Collections.sort(transactions, Comparator.comparing(Transaction::getDate).thenComparingLong(Transaction::getId));
        return transactions;
    }

    public static boolean isValidDate(String dateValue) {
        if (dateValue == null || dateValue.trim().isEmpty()) {
            return false;
        }

        DATE_FORMAT.setLenient(false);
        try {
            Date parsed = DATE_FORMAT.parse(dateValue.trim());
            return parsed != null && DATE_FORMAT.format(parsed).equals(dateValue.trim());
        } catch (ParseException exception) {
            return false;
        }
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }

        boolean shouldQuote = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")
                || value.startsWith(" ") || value.endsWith(" ");
        String escaped = value.replace("\"", "\"\"");
        return shouldQuote ? "\"" + escaped + "\"" : escaped;
    }

    private static List<String> parseLine(String line) {
        List<String> columns = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '"') {
                if (inQuotes && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    current.append('"');
                    index++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (character == ',' && !inQuotes) {
                columns.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }

        columns.add(current.toString());
        return columns;
    }

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Baris CSV tidak valid.");
        }
    }

    private static String stripBom(String value) {
        return value != null && value.startsWith("\uFEFF") ? value.substring(1) : value;
    }
}