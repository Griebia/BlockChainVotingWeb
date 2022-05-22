package com.blockchainvotingweb.views.getvote;

import com.blockchainvotingweb.data.entity.Url;
import com.blockchainvotingweb.views.MainLayout;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import okhttp3.*;
import org.vaadin.olli.FileDownloadWrapper;

import javax.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@PageTitle("Get Vote")
@Route(value = "GetVote", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class GetVoteView extends Div {

    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    private HorizontalLayout buttonLayout;
    private Button getVote = new Button("Get vote");
    private Button saveKey = new Button("Download");
    private Text wallet = new Text("Wallet info");

    public GetVoteView() {
        addClassName("vote-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        getVote.addClickListener(e -> {
            for (String url : Url.urls) {
                String curUrl = url + "/voter/new";
                try {
                    VoteInfo voteInfo = get(curUrl, VoteInfo.class);
                    FileDownloadWrapper buttonWrapper = new FileDownloadWrapper(
                            new StreamResource("vote.pen", () -> new ByteArrayInputStream(voteInfo.getPrivate_key().getBytes())));

                    saveKey.setDisableOnClick(false);
                    buttonWrapper.wrapComponent(saveKey);
                    buttonLayout.add(buttonWrapper);

                    wallet.setText(voteInfo.getWallet());
                    break;
                } catch (Exception ex) {
                    System.out.println("Failed to send info to " + curUrl);
                }
            }
        });
    }

    private Component createTitle() {
        return new H3("Get vote");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(wallet);
        return formLayout;
    }

    private Component createButtonLayout() {
        buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        getVote.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveKey.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(getVote, saveKey);
        saveKey.setDisableOnClick(false);

        return buttonLayout;
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

}
