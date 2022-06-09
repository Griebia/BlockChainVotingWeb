package com.blockchainvotingweb.views.candidates;

public class Candidate {
    private String name;
    private String wallet_address;
    private int votes;

    public String getWallet_address() {
        return wallet_address;
    }

    public void setWallet_address(String wallet) {
        this.wallet_address = wallet;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
