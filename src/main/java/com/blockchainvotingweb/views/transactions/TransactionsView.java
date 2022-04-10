package com.blockchainvotingweb.views.transactions;

import com.blockchainvotingweb.views.MainLayout;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@PageTitle("Transactions")
@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class TransactionsView extends Div implements AfterNavigationObserver {

    private final ObjectMapper objectMapper = new ObjectMapper();
    Grid<Transaction> grid = new Grid<>();
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    String url = "http://localhost:8081/transactions";

    public TransactionsView() {
        addClassName("transactions-view");
        setSizeFull();
        grid.setHeight("100%");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_NO_ROW_BORDERS);
        grid.addComponentColumn(transaction -> createCard(transaction));
        add(grid);
    }


    public <Resp> Resp get(String url, Class<Resp> responseClass) {

        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();

            return handleResponse(response, responseClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <Resp> Resp handleResponse(Response response, Class<Resp> responseClass) {
        if (response.code() == 401) {

        }

        try {
            String responseContent = response.body().string();

            if (500 <= response.code()) {
            }

            if (400 == response.code()) {
                throw new RuntimeException("HTTP Content Format does not match");
            }

            Resp resp = this.objectMapper.readValue(responseContent, responseClass);
            return resp;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private HorizontalLayout createCard(Transaction transaction) {
        HorizontalLayout card = new HorizontalLayout();
        card.addClassName("card");
        card.setSpacing(false);
        card.getThemeList().add("spacing-s");

        VerticalLayout description = new VerticalLayout();
        description.addClassName("description");
        description.setSpacing(false);
        description.setPadding(false);

        HorizontalLayout header = new HorizontalLayout();
        header.addClassName("header");
        header.setSpacing(false);
        header.getThemeList().add("spacing-s");

        Span transactionId = new Span("Hash " + transaction.getTransactionId());
        transactionId.addClassName("name");
        header.add(transactionId);

        Span sender = new Span("Sender " + transaction.getSender());
        sender.addClassName("name");

        Span receiver = new Span("Receiver " + transaction.getRecipient());
        receiver.addClassName("name");

        Span post = new Span(transaction.getValue());
        post.addClassName("post");

        description.add(header, sender,receiver, post);
        card.add(description);
        return card;
    }
    public List<Transaction> getTransactions() {

        System.out.println("Fetching all Comment objects through REST..");


        // do fetch and map result
        List<Transaction> comments = Arrays.asList(get(url, Transaction[].class));

        System.out.println(String.format("...received %d items.", comments.size()));

        return comments;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        // Set some data when this view is displayed.
        List<Transaction> transactions = getTransactions();

        grid.setItems(transactions);
    }

    private static Transaction createTransaction(String image, String name, String date, String post, String likes,
                                                 String comments, String shares) {
        Transaction p = new Transaction();
        p.setTransactionId(name);
        p.setInputsValue(date);
        p.setOutputsValue(post);

        return p;
    }

}
