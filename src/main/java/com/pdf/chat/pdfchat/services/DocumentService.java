package com.pdf.chat.pdfchat.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class DocumentService {

    private final EmbeddingService embeddingService;
    private final VectorStoreService vectorStoreService;
    
    public DocumentService(EmbeddingService embeddingService,
                           VectorStoreService vectorStoreService) {
        this.embeddingService = embeddingService;
        this.vectorStoreService = vectorStoreService;
    }
    public void processDocument(MultipartFile file) throws Exception {
        String text = extractText(file);
        List<String> chunks = chunkText(text);

        for (String chunk : chunks) {
            List<Double> embedding = embeddingService.generateEmbedding(chunk);
            vectorStoreService.storeVector(chunk, embedding);
        }
    }

    private String extractText(MultipartFile file) throws IOException {
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

    private List<String> chunkText(String text) {
        int chunkSize = 1000;
        int overlap = 200;
        List<String> chunks = new ArrayList<>();
        // overlap is to ensure we don't cut off important context between chunks
        for (int i = 0; i < text.length(); i += chunkSize - overlap) {
            int end = Math.min(text.length(), i + chunkSize);
            chunks.add(text.substring(i, end));
        }
        return chunks;
    }
}