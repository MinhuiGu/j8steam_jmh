package com.ms.tech;

import java.util.Calendar;
import java.util.Comparator;

public class Transaction {

    public Transaction(String toAccountName, String fromAccoundName, Double amount, Calendar dateTime, String group) {
        this.toAccountName = toAccountName;
        this.fromAccoundName = fromAccoundName;
        this.amount = amount;
        this.dateTime = dateTime;
        this.group = group;
    }

    private String fromAccoundName;
    private String toAccountName;
    private Double amount;
    private Calendar dateTime;

    public String getGroup() {
        return group;
    }

    private String group;

    public String getFromAccoundName() {
        return fromAccoundName;
    }

    public String getToAccountName() {
        return toAccountName;
    }

    public Double getAmount() {
        return amount;
    }

    public Calendar getDateTime() {
        return dateTime;
    }

    static final Comparator<Transaction> fromNameComparator = new Comparator<Transaction>() {

        @Override
        public int compare(Transaction t1, Transaction t2) {
            return t1.getFromAccoundName().toUpperCase().compareTo(t2.getFromAccoundName().toUpperCase());
        }
    };

    static final Comparator<Transaction> amountComparator = new Comparator<Transaction>() {

        @Override
        public int compare(Transaction t1, Transaction t2) {
            return t1.getAmount().compareTo(t2.getAmount());
        }
    };
}
