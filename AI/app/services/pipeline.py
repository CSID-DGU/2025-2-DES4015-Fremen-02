# app/services/pipeline.py
from typing import Dict, Any, List
from .kobert import predict_smishing
from .kb_retriever import search_kb
from .llm import build_llm_prompt, generate_llm_explanation2


def analyze_sms_full(text: str, top_k: int = 3) -> Dict[str, Any]:
    cls_result = predict_smishing(text)

    kb_query = (
        f"{text}\n"
        f"[모델 판단]\n"
        f"- 라벨: {cls_result['pred_label']}\n"
        f"- 위험도: {cls_result['risk_level']}"
    )
    kb_hits = search_kb(kb_query, top_k=top_k)

    prompt = build_llm_prompt(text, cls_result, kb_hits)
    explanation = generate_llm_explanation2(prompt)

    return {
        "classifier": cls_result,
        "kb_hits": kb_hits,
        "explanation": explanation,
    }
