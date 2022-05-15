package com.blockchainvotingweb.views.vote;

import com.blockchainvotingweb.data.entity.SamplePerson;
import com.blockchainvotingweb.data.entity.Url;
import com.blockchainvotingweb.data.service.SamplePersonService;
import com.blockchainvotingweb.data.tools.PrivateKeyReader;
import com.blockchainvotingweb.views.MainLayout;
import com.blockchainvotingweb.views.transactions.Transaction;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import javax.annotation.security.PermitAll;
import javax.crypto.Cipher;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@PageTitle("Vote")
@Route(value = "Vote", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class VoteView extends Div {

    private TextField voter = new TextField("Voters wallet");
    private TextField candidate = new TextField("Candidate wallet");
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    private MemoryBuffer memoryBuffer = new MemoryBuffer();
    private Upload singleFileUpload = new Upload(memoryBuffer);

    private Button vote = new Button("Save");

    public VoteView() {
        addClassName("vote-view");

        add(createTitle());
        add(createFormLayout());
        add(createButtonLayout());

        vote.addClickListener(e -> {
            if (!candidate.isEmpty() && !singleFileUpload.isAttached()) {
                return;
            }

            String votersWallet = voter.getValue();
            String candidateWallet = candidate.getValue();
            InputStream inputStream = memoryBuffer.getInputStream();
            String privateKey;
            Map<String, String> postInfo = new HashMap<>();
            try {
                privateKey = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
                PrivateKey privKey = new PrivateKeyReader(privateKey).getPrivateKey();
                Signature sig = Signature.getInstance("SHA256withRSA");
                sig.initSign(privKey);

                postInfo.put("sender", votersWallet);
                postInfo.put("receiver", candidateWallet);

                String jsonString = new ObjectMapper().configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true).writeValueAsString(postInfo);
                String data = DigestUtils.sha256Hex(jsonString);
                System.out.println(data);
                sig.update(data.getBytes(StandardCharsets.UTF_8));

                Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, privKey);
                byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

                byte[] signatureBytes = sig.sign();
                String signatureString = hex(cipherText);
                System.out.println(signatureString);
                postInfo.put("signature", signatureString);
            } catch (Exception exception) {
                System.out.println("The file reading failed");
                return;
            }


            for (String url : Url.urls) {
                String curUrl = url + "/transaction/new";
                try {
                    post(curUrl, Object.class, postInfo);
                } catch (Exception ex) {
                    System.out.println("Failed to send info to " + curUrl);
                }
            }
        });
    }

    public static String hex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte aByte : bytes) {
            result.append(String.format("%02x", aByte));
            // upper case
            // result.append(String.format("%02X", aByte));
        }
        return result.toString();
    }

    private Component createTitle() {
        return new H3("Cast vote");
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(voter, candidate, singleFileUpload);
        return formLayout;
    }

    private Component createButtonLayout() {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName("button-layout");
        vote.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(vote);
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
