# 📄 Research Paper RAG Chat

## 🚀 About the Project

This project implements a **Retrieval Augmented Generation (RAG)** system that allows users to upload research papers (PDF) or any other pdf and ask questions based on their content.

The system extracts text from uploaded documents, converts the text into **vector embeddings**, stores them in a **vector database**, and retrieves the most relevant sections when a user asks a question. The retrieved context is then sent to an **LLM (Gemini)** to generate an accurate answer.

This approach enables **context-aware question answering** over large documents like research papers.

---

## 🏗 High Level Architecture
```
              +------------------+
            |   User Request   |
            |  (Upload / Ask)  |
            +--------+---------+
                     |
                     v
           +-------------------+
           |   Spring Boot API |
           +-------------------+
            |               |
            |               |
            v               v
   +----------------+   +----------------+
   | Document       |   | Question       |
   | Processing     |   | Processing     |
   +--------+-------+   +--------+-------+
            |                    |
            v                    v
    Extract Text            Create Query
     from PDF                Embedding
            |                    |
            v                    v
     Chunk Document        Vector Search
            |                    |
            v                    v
    Generate Embeddings      Qdrant DB
            |
            v
       Store in
       Qdrant DB
```
---       

## 🛠 Tech Stack

- **Java 17**
- **Spring Boot**
- **Apache PDFBox** – PDF text extraction
- **Qdrant** – Vector database
- **Gemini API** – LLM for answer generation
- **REST APIs**
- **Docker** (for running Qdrant)

---

# 📡 API Endpoints

Base URL
http://localhost:8080

---

# 1️⃣ Upload Document

Uploads a research paper and stores its embeddings in the vector database.

### Endpoint

POST /api/upload

### Request

Multipart form-data

Example curl:

```bash
curl -X POST "http://localhost:8080/api/upload" \
-F "file=@paper.pdf"
```

# 2️⃣ Ask Question

Ask a question related to the uploaded research papers.

This endpoint supports two modes:

| Flag    | Mode          | Description                                                                           |
| ------- | ------------- | ------------------------------------------------------------------------------------- |
| `true`  | Research Mode | Uses a research-focused prompt to generate detailed answers from the document context |
| `false` | General Mode  | Uses a general assistant prompt                                                       |

---

## Endpoint

POST /api/ask

---

## Request Body

```json
{
  "question": "What is the main contribution of the paper?",
  "flag": true
}
```

Example curl:

```bash
curl -X POST http://localhost:8080/api/ask \
-H "Content-Type: application/json" \
-d '{
  "question": "What is the main contribution of the paper?",
  "flag": true
}'
```

# How to Run

## Clone project

git clone

## Start vector DB

docker run -p 6333:6333 qdrant/qdrant

## Open Dashboard

http://localhost:6333/dashboard

## Create Vector collection

```bash
curl -X PUT "http://localhost:6333/collections/research_papers" \
-H "Content-Type: application/json" \
-d '{
"vectors": {
"size": 768,
"distance": "Cosine"
}
}'
```

size should be based on the embedding model you use.

## Run application

mvn spring-boot:run

# RAG Pipeline Summary

```

PDF
 ↓
Text Extraction
 ↓
Chunking
 ↓
Embeddings
 ↓
Qdrant Vector DB
 ↓
Query Embedding
 ↓
Vector Search
 ↓
LLM (Gemini)
 ↓
Final Answer

```
