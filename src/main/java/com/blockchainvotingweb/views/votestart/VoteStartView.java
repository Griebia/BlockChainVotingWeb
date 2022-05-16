package com.blockchainvotingweb.views.votestart;

import com.blockchainvotingweb.data.entity.Url;
import com.blockchainvotingweb.data.tools.PythonInterface;
import com.blockchainvotingweb.views.MainLayout;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import okhttp3.*;
import org.apache.commons.io.IOUtils;

import javax.annotation.security.PermitAll;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@PageTitle("Vote Start")
@Route(value = "VoteStart", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class VoteStartView extends Div {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    private MemoryBuffer memoryBuffer = new MemoryBuffer();
    private Upload singleFileUpload = new Upload(memoryBuffer);

    private Button voteStart = new Button("Start vote");

    public VoteStartView() {
        addClassName("vote-start-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        voteStart.addClickListener(e -> {
            if (!singleFileUpload.isAttached()) {
                return;
            }
            InputStream inputStream = memoryBuffer.getInputStream();
            String privateKey;
            Map<String, String> postInfo = new HashMap<>();
            try {
                privateKey = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                postInfo.put("data", "StartVote");
                String signature = PythonInterface.signData(postInfo.get("data"), privateKey);

                postInfo.put("signature", signature);
            } catch (Exception exception) {
                System.out.println("The file reading failed");
                return;
            }


            for (String url : Url.urls) {
                String curUrl = url + "/startvote";
                try {
                    post(curUrl, Object.class, postInfo);
                } catch (Exception ex) {
                    System.out.println("Failed to send info to " + curUrl);
                }
            }
        });
    }

    private Component createTitle() {
        return new H3("Start vote");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(singleFileUpload);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        voteStart.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(voteStart);
        return buttonLayout;
    }


    public <Resp> Resp post(String url, Class<Resp> responseClass, Map<String, String> postValues) {

        try {
            String json = new ObjectMapper().writeValueAsString(postValues);
            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"), json);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
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
