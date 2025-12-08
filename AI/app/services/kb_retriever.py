# app/services/kb_retriever.py
import os
import pandas as pd
import torch
from sentence_transformers import SentenceTransformer
from app.config import settings

KB_CSV_PATH = settings.KB_CSV_PATH
assert os.path.exists(KB_CSV_PATH), f"KB 파일이 없음: {KB_CSV_PATH}"

kb_df = pd.read_csv(KB_CSV_PATH)

def build_kb_passage(row):
    parts = []
    if pd.notna(row.get("title")):
        parts.append(str(row["title"]))
    if pd.notna(row.get("category")):
        parts.append(f"카테고리: {row['category']}")
    if pd.notna(row.get("risk_pattern")):
        parts.append(f"위험 패턴: {row['risk_pattern']}")
    if pd.notna(row.get("content")):
        parts.append(str(row["content"]))
    if pd.notna(row.get("recommended_action")):
        parts.append(f"대응 방법: {row['recommended_action']}")
    if pd.notna(row.get("legal_basis")):
        parts.append(f"법/지침 근거: {row['legal_basis']}")
    return "\n".join(parts)

kb_df["full_text"] = kb_df.apply(build_kb_passage, axis=1)

KB_EMBED_MODEL_NAME = "intfloat/multilingual-e5-base"
kb_embed_model = SentenceTransformer(KB_EMBED_MODEL_NAME)
kb_embed_model.eval()

kb_passages = ["passage: " + t for t in kb_df["full_text"].tolist()]
kb_embeddings = kb_embed_model.encode(
    kb_passages,
    convert_to_tensor=True,
    normalize_embeddings=True,
)

def search_kb(query: str, top_k: int = 3):
    q_text = "query: " + query
    q_emb = kb_embed_model.encode(
        [q_text],
        convert_to_tensor=True,
        normalize_embeddings=True,
    )

    scores = (kb_embeddings @ q_emb.T).squeeze(1)  # (num_docs,)

    top_k = min(top_k, len(kb_df))
    topk_scores, topk_indices = torch.topk(scores, k=top_k)

    results = []
    for score, idx in zip(topk_scores.tolist(), topk_indices.tolist()):
        row = kb_df.iloc[idx]
        results.append({
            "kb_id": row.get("id", idx),
            "title": row.get("title", ""),
            "category": row.get("category", ""),
            "risk_pattern": row.get("risk_pattern", ""),
            "content": row.get("content", ""),
            "recommended_action": row.get("recommended_action", ""),
            "legal_basis": row.get("legal_basis", ""),
            "similarity": float(score),
        })
    return results
