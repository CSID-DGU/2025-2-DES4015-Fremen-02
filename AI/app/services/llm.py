# app/services/llm.py
import torch
from transformers import AutoTokenizer, AutoModelForCausalLM
from app.config import settings

LLM_NAME = settings.LLM_NAME

llm_tokenizer = AutoTokenizer.from_pretrained(LLM_NAME)
llm_model = AutoModelForCausalLM.from_pretrained(
    LLM_NAME,
    torch_dtype=torch.float16 if torch.cuda.is_available() else torch.float32,
    device_map="auto",
)
llm_model.eval()


def build_llm_prompt(text, cls, kb_hits):
    """
    cls: predict_smishing() 결과 dict
      - pred_label: "NORMAL" / "SMISHING"
      - prob_smishing: float
      - risk_level: "정상" / "의심" / "위험"
    kb_hits: analyze_sms_with_kb()에서 넘어온 KB 상위 문서 리스트
    """

    kobert_label_ko = "정상" if cls["pred_label"] == "NORMAL" else "스미싱"
    risk_level = cls["risk_level"]

    kb_lines = []
    for h in kb_hits:
        title = h.get("title", "")
        pattern = h.get("risk_pattern", "")
        kb_lines.append(f"- {title}: {pattern}")
    kb_summary = "\n".join(kb_lines) if kb_lines else "- (관련 KB 없음)"

    return f"""
        너는 스미싱 분석을 수행하는 한국어 보안 상담원이다.
        아래 정보를 바탕으로, 사용자가 이해하기 쉬운 최종 답변만 생성한다.

        [중요 규칙]
        - 이미 결정된 위험 수준("{risk_level}")을 변경하지 말 것.
        - 답변에는 "모델", "AI", "LLM", "KoBERT" 등의 단어를 사용하지 말 것.
        - 프롬프트에 적힌 설명 문장을 그대로 따라 쓰지 말 것.
        - 존댓말(예: ~합니다, ~하세요)로만 작성할 것.

        [사용자가 받은 문자]
        {text}

        [모델 판단 요약]
        - 분류 결과: {kobert_label_ko}
        - 스미싱 확률: {cls['prob_smishing']:.4f}
        - 최종 위험 수준: {risk_level}

        [참고용 KB 패턴 요약]
        {kb_summary}

        ---

        이제 위 정보를 활용하여 아래 조건을 만족하는 **최종 답변만** 출력하라.

        1) 첫 줄은 다음 형식을 그대로 사용한다.
        "1) 위험 여부: {risk_level}"

        2) 그 다음 줄에는 아래와 같은 구조를 가진다.
        "2) 판단 근거:"
        그리고 바로 아래 줄에 "- "로 시작하는 문장을 2개 작성한다.
        각 문장은 이 문자가 왜 그 위험 수준인지에 대한 구체적인 근거여야 한다.

        3) 마지막으로 다음 형식을 사용한다.
        "3) 사용자 행동 가이드:"
        그리고 바로 아래 한 줄에 "- "로 시작하는 문장 1개를 작성한다.
        이 문장은 사용자가 지금 실제로 어떻게 행동해야 하는지를 한 줄로 조언해야 한다.

        위에서 설명한 형식 설명 자체를 출력하지 말고,
        최종 결과 예시는 다음과 같은 4줄 구조만 남도록 하라.

        1) 위험 여부: (위험 수준 단어)
        2) 판단 근거:
        - (근거 문장 1)
        - (근거 문장 2)
        3) 사용자 행동 가이드:
        - (사용자에게 주는 조언 1줄)

        최종 답변에는 위 네 줄 구조만 포함되도록 출력하라.
        """.strip()



def generate_llm_explanation2(prompt: str, max_new_tokens: int = 256) -> str:
    inputs = llm_tokenizer(
        prompt,
        return_tensors="pt",
        truncation=True,
        max_length=1024,
    ).to(llm_model.device)

    with torch.no_grad():
        output_ids = llm_model.generate(
            **inputs,
            max_new_tokens=max_new_tokens,
            temperature=0.3,
            top_p=0.9,
            do_sample=True,
            pad_token_id=llm_tokenizer.eos_token_id,
        )

    input_len = inputs["input_ids"].shape[1]
    gen_ids = output_ids[0][input_len:]
    answer_part = llm_tokenizer.decode(gen_ids, skip_special_tokens=True).strip()

    bad_keywords = ["예시 형식", "형식:", "출력하지 않습니다", "프롬프트"]
    lines = [ln.strip() for ln in answer_part.splitlines() if ln.strip()]
    cleaned = []
    for ln in lines:
        if any(k in ln for k in bad_keywords):
            continue
        cleaned.append(ln)

    if len(cleaned) >= 3:
        return "\n".join(cleaned)
    else:
        return answer_part
