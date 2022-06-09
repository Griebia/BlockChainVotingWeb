package com.blockchainvotingweb.views.candidates;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.template.Id;

@JsModule("./views/candidates/image-card.ts")
@Tag("image-card")
public class ImageCard extends LitTemplate {

    @Id
    private Image image;

    @Id
    private Span header;

    @Id
    private Paragraph text;

    @Id
    private Paragraph votes;

    public ImageCard(String title, String text, String votes, String url) {
        this.image.setSrc(url);
        this.image.setAlt(text);
        this.header.setText(title);
        this.text.setText(text);
        this.votes.setText(votes);
    }
}
