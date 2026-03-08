package com.pdf.chat.pdfchat.model;
import lombok.Data;

@Data
public class AskRequest {
    private String question;
    private boolean researchOriented;
    public String getQuestion() {
        return question;
    }
    public Boolean getFlag() {
        return researchOriented;
    }
}