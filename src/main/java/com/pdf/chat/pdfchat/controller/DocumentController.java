package com.pdf.chat.pdfchat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.pdf.chat.pdfchat.model.AskRequest;
import com.pdf.chat.pdfchat.services.DocumentService;
import com.pdf.chat.pdfchat.services.RagService;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private final DocumentService documentService;
    private final RagService ragService;
    
    public DocumentController(DocumentService documentService,
                              RagService ragService) {
        this.documentService = documentService;
        this.ragService = ragService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        try{
           documentService.processDocument(file);
        }
        catch(Exception e){
            return ResponseEntity.status(500).body("Error processing document: " + e.getMessage());
        }
        return ResponseEntity.ok("Document processed successfully");
    }

    @PostMapping("/ask")
    public ResponseEntity<String> ask(@RequestBody AskRequest request) {
        try{
          String answer = ragService.askQuestion(request.getQuestion(),request.getFlag());
          return ResponseEntity.ok(answer);
        }
        catch(Exception e){
            return ResponseEntity.status(500).body("Error processing question: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("service is working");
    }
}