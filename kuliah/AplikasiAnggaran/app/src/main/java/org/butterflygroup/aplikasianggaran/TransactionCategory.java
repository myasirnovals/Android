package org.butterflygroup.aplikasianggaran;

public enum TransactionCategory {
    PEMASUKAN("income", "Pemasukan"),
    PENGELUARAN("expense", "Pengeluaran"),
    SETOR_DANA_DARURAT("emergency", "Setor Dana Darurat"),
    AMBIL_DANA_DARURAT("emergency_withdraw", "Ambil Dana Darurat");

    private final String typeCode;
    private final String displayLabel;

    TransactionCategory(String typeCode, String displayLabel) {
        this.typeCode = typeCode;
        this.displayLabel = displayLabel;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public static TransactionCategory fromDisplayLabel(String displayLabel) {
        for (TransactionCategory category : values()) {
            if (category.displayLabel.equals(displayLabel)) {
                return category;
            }
        }
        return null;
    }

    public static TransactionCategory fromTypeCode(String typeCode) {
        for (TransactionCategory category : values()) {
            if (category.typeCode.equals(typeCode)) {
                return category;
            }
        }
        return null;
    }
}