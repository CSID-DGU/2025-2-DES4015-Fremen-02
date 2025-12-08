# app/services/kobert.py
import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification
from app.config import settings

device = "cuda" if torch.cuda.is_available() else "cpu"

# ===== KoBERT 로딩 =====
KOBERT_MODEL_PATH = settings.KOBERT_MODEL_PATH

kobert_tokenizer = AutoTokenizer.from_pretrained(KOBERT_MODEL_PATH)
kobert_model = AutoModelForSequenceClassification.from_pretrained(KOBERT_MODEL_PATH)
kobert_model.to(device)
kobert_model.eval()

word_emb = kobert_model.get_input_embeddings()

# label2id / id2label 세팅
if hasattr(kobert_model.config, "label2id") and kobert_model.config.label2id:
    label2id = {k.upper(): v for k, v in kobert_model.config.label2id.items()}
else:
    label2id = {"NORMAL": 0, "SMISHING": 1}

id2label = {v: k for k, v in label2id.items()}


def decide_risk_level(prob_smishing: float,
                      low: float = 0.3,
                      high: float = 0.7) -> str:
    if prob_smishing < low:
        return "정상"
    elif prob_smishing < high:
        return "의심"
    else:
        return "위험"


@torch.no_grad()
def predict_smishing(text: str,
                     max_length: int = 128,
                     low: float = 0.3,
                     high: float = 0.7):
    enc = kobert_tokenizer(
        text,
        return_tensors="pt",
        truncation=True,
        padding="max_length",
        max_length=max_length,
    )

    vocab_limit = word_emb.weight.shape[0]
    input_ids = enc["input_ids"].clamp(min=0, max=vocab_limit - 1)
    attention_mask = enc["attention_mask"]
    token_type_ids = torch.zeros_like(input_ids, dtype=torch.long)

    input_ids = input_ids.to(device)
    attention_mask = attention_mask.to(device)
    token_type_ids = token_type_ids.to(device)

    outputs = kobert_model(
        input_ids=input_ids,
        attention_mask=attention_mask,
        token_type_ids=token_type_ids,
    )
    logits = outputs.logits[0]

    probs = torch.softmax(logits, dim=-1).cpu().numpy()
    idx_normal = label2id["NORMAL"]
    idx_smish = label2id["SMISHING"]

    prob_normal = float(probs[idx_normal])
    prob_smishing = float(probs[idx_smish])

    pred_id = int(torch.argmax(logits).item())
    pred_label = id2label[pred_id]

    risk_level = decide_risk_level(prob_smishing, low=low, high=high)

    return {
        "text": text,
        "pred_label": pred_label,
        "prob_normal": prob_normal,
        "prob_smishing": prob_smishing,
        "risk_level": risk_level,
    }
