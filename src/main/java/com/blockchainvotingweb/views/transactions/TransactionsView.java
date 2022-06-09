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
import java.util.ArrayList;
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

    private static List<String> urls = List.of(
            "http://localhost:5000",
            "http://localhost:5001",
            "http://localhost:5002"
    );

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

            System.out.println(responseContent);
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

        Span sender = new Span("Sender " + transaction.getSender());
        sender.addClassName("name");

        Span receiver = new Span("Receiver " + transaction.getReceiver());
        receiver.addClassName("name");

        description.add(header, sender, receiver);
        card.add(description);
        return card;
    }

    public List<Transaction> getTransactions() {

        System.out.println("Fetching all Comment objects through REST..");

        List<Transaction> transactions = Arrays.asList();

        try {
            for (String url : urls) {
                String urlConverted = url + "/transaction/all";
                Transaction[] transactionArray = get(urlConverted, Transaction[].class);
                transactions = Arrays.asList(transactionArray);
            }
        } catch (Exception e) {
            System.out.println("There were a problem to get any server transaction information");
        }

        System.out.println(String.format("...received %d items.", transactions.size()));

        return transactions;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        // Set some data when this view is displayed.
        List<Transaction> transactions = getTransactions();

        grid.setItems(transactions);
    }

}
