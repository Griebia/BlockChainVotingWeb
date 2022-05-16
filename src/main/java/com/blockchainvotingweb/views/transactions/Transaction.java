package com.blockchainvotingweb.views.transactions;

public class Transaction {
    private String sender;
    private String receiver;

    public Transaction() {
    }
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String recipient) {
        this.receiver = recipient;
    }
}
