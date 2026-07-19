package org.butterflygroup.aplikasianggaran;

public class Transaction {
    public static final String TYPE_INCOME = TransactionCategory.PEMASUKAN.getTypeCode();
    public static final String TYPE_EXPENSE = TransactionCategory.PENGELUARAN.getTypeCode();
    public static final String TYPE_EMERGENCY = TransactionCategory.SETOR_DANA_DARURAT.getTypeCode();
    public static final String TYPE_EMERGENCY_WITHDRAW = TransactionCategory.AMBIL_DANA_DARURAT.getTypeCode();

    private final long id;
    private final String date;
    private final String type;
    private final String category;
    private final long amount;
    private final String description;

    public Transaction(long id, String date, String type, String category, long amount, String description) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public long getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public static boolean isSupportedType(String type) {
        return TYPE_INCOME.equals(type)
                || TYPE_EXPENSE.equals(type)
                || TYPE_EMERGENCY.equals(type)
                || TYPE_EMERGENCY_WITHDRAW.equals(type);
    }

    public static String getDisplayLabel(String type) {
        TransactionCategory category = TransactionCategory.fromTypeCode(type);
        if (category != null) {
            return category.getDisplayLabel();
        }
        return type;
    }
}