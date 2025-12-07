# app/main.py
from fastapi import FastAPI
from typing import List
from app.models.smishing import SmishingRequest, SmishingResponse, RetrievedDoc
from app.services.pipeline import analyze_sms_full

app = FastAPI(title="Fremen RAG-based Smishing API (Light LLM)")


@app.post("/api/v1/smishing-rag", response_model=SmishingResponse)
def smishing_rag(req: SmishingRequest):
    result = analyze_sms_full(req.text, top_k=2)

    cls = result["classifier"]
    kb_hits = result["kb_hits"]
    explanation = result["explanation"]

    docs_for_response: List[RetrievedDoc] = []
    for i, h in enumerate(kb_hits):
        docs_for_response.append(
            RetrievedDoc(
                id=str(h.get("kb_id", i)),
                title=h.get("title", ""),
                content=h.get("content", h.get("risk_pattern", "")),
                tags=h.get("tags", []) or [],
                score=float(h.get("similarity", h.get("score", 0.0))),
            )
        )

    return SmishingResponse(
        category=cls.get("risk_level", cls.get("pred_label", "")),
        score=float(cls.get("prob_smishing", 0.0)),
        retrieved_docs=docs_for_response,
        explanation=explanation,
    )
