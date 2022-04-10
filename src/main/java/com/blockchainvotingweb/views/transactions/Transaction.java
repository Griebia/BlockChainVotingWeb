package com.blockchainvotingweb.views.transactions;

public class Transaction {

    private String transactionId;
    private String sender;
    private String recipient;
    private String value;
    private String inputsValue;
    private String outputsValue;

    public Transaction() {
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInputsValue() {
        return inputsValue;
    }

    public void setInputsValue(String inputsValue) {
        this.inputsValue = inputsValue;
    }

    public String getOutputsValue() {
        return outputsValue;
    }

    public void setOutputsValue(String outputsValue) {
        this.outputsValue = outputsValue;
    }
}
