package org.butterflygroup.aplikasianggaran;

import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    public interface OnTransactionDeleteListener {
        void onDelete(Transaction transaction);
    }

    private final List<Transaction> items = new ArrayList<>();
    private final OnTransactionDeleteListener deleteListener;
    private final NumberFormat currencyFormat;

    public TransactionAdapter(OnTransactionDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        this.currencyFormat.setMaximumFractionDigits(0);
        this.currencyFormat.setMinimumFractionDigits(0);
    }

    public void setItems(List<Transaction> transactions) {
        items.clear();
        if (transactions != null) {
            items.addAll(transactions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = items.get(position);
        holder.numberTextView.setText("#" + (position + 1));
        holder.dateTextView.setText(transaction.getDate());
        holder.typeTextView.setText(Transaction.getDisplayLabel(transaction.getType()));
        holder.typeTextView.setBackgroundTintList(ColorStateList.valueOf(resolveTypeColor(transaction.getType())));
        holder.categoryTextView.setText(transaction.getCategory());
        holder.amountTextView.setText(formatAmount(transaction.getAmount()));
        holder.descriptionTextView.setText(TextUtils.isEmpty(transaction.getDescription()) ? "Tidak ada keterangan" : transaction.getDescription());
        holder.descriptionTextView.setVisibility(TextUtils.isEmpty(transaction.getDescription()) ? View.GONE : View.VISIBLE);
        holder.deleteButton.setOnClickListener(view -> deleteListener.onDelete(transaction));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatAmount(long amount) {
        return currencyFormat.format(amount);
    }

    private int resolveTypeColor(String type) {
        if (Transaction.TYPE_INCOME.equals(type)) {
            return 0xFF166534;
        }
        if (Transaction.TYPE_EXPENSE.equals(type)) {
            return 0xFFB91C1C;
        }
        if (Transaction.TYPE_EMERGENCY.equals(type)) {
            return 0xFFC2410C;
        }
        if (Transaction.TYPE_EMERGENCY_WITHDRAW.equals(type)) {
            return 0xFF1D4ED8;
        }
        return 0xFF0F766E;
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView numberTextView;
        private final TextView dateTextView;
        private final MaterialButton typeTextView;
        private final TextView categoryTextView;
        private final TextView amountTextView;
        private final TextView descriptionTextView;
        private final MaterialButton deleteButton;

        TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            numberTextView = itemView.findViewById(R.id.transactionNumberText);
            dateTextView = itemView.findViewById(R.id.transactionDateText);
            typeTextView = itemView.findViewById(R.id.transactionTypeText);
            categoryTextView = itemView.findViewById(R.id.transactionCategoryText);
            amountTextView = itemView.findViewById(R.id.transactionAmountText);
            descriptionTextView = itemView.findViewById(R.id.transactionDescriptionText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}