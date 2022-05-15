package com.blockchainvotingweb.views.transactions;

public class Transaction {
    private String sender;
    private String recipient;

    public Transaction() {
    }
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
