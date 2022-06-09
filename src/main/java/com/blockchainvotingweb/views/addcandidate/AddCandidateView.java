package com.blockchainvotingweb.views.addcandidate;

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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import okhttp3.*;
import org.apache.commons.io.IOUtils;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@PageTitle("Add candidate")
@Route(value = "AddCandidate", layout = MainLayout.class)
@RolesAllowed("admin")
@Uses(Icon.class)
public class AddCandidateView extends Div {

    private TextField candidateName = new TextField("Candidate name");

    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    private Label error = new Label();
    private MemoryBuffer memoryBuffer = new MemoryBuffer();
    private Upload singleFileUpload = new Upload(memoryBuffer);

    private Button createCandidate = new Button("Create Candidate");

    public AddCandidateView() {
        addClassName("vote-view");

        add(error);
        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        createCandidate.addClickListener(e -> {
            if (candidateName.isEmpty() || !singleFileUpload.isAttached()) {
                error.getStyle().set("color", "red");
                error.setText("Missing fields");
                return;
            }

            String candidateInformation = candidateName.getValue();
            InputStream inputStream = memoryBuffer.getInputStream();
            String privateKey;
            Map<String, String> postInfo = new HashMap<>();
            try {
                privateKey = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                postInfo.put("name", candidateInformation);
                String signature = PythonInterface.signData(candidateInformation, privateKey);
                postInfo.put("signature", signature);
            } catch (Exception exception) {
                System.out.println("The file reading failed");
                return;
            }

            AddCandidate candidate = null;
            for (String url : Url.urls) {
                String curUrl = url + "/candidate/new";
                try {
                    candidate = post(curUrl, AddCandidate.class, postInfo);
                    System.out.println(candidate.getWallet());
                    break;
                } catch (Exception ex) {
                    System.out.println("Failed to send info to " + curUrl);
                }
            }

            if (candidate == null){
                error.getStyle().set("color", "red");
                error.setText("Candidate could not be created");
            }
        });
    }

    private Component createTitle() {
        return new H3("Cast vote");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(candidateName, singleFileUpload);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        createCandidate.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(createCandidate);
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
