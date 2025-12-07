# app/config.py

from pathlib import Path
from pydantic_settings import BaseSettings, SettingsConfigDict

BASE_DIR = Path(__file__).resolve().parents[1]


class Settings(BaseSettings):
    # 모델/데이터 경로
    KOBERT_MODEL_PATH: str = str(BASE_DIR / "models" / "kobert_binary_v2_final")
    KB_CSV_PATH: str = str(BASE_DIR / "data" / "kb_docs_kr_v2.csv")
    LLM_NAME: str = "Bllossom/llama-3.2-Korean-Bllossom-3B"

    # Pydantic v2 스타일 설정
    model_config = SettingsConfigDict(
        env_file=str(BASE_DIR / ".env"),
        env_file_encoding="utf-8",
    )


settings = Settings()
