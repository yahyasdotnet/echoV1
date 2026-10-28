import os
from pathlib import Path


class Config:
    SECRET_KEY = os.environ.get("SECRET_KEY", "dev-only-change-me")
    MUSIC_DIR = Path(os.environ.get("MUSIC_DIR", "./music"))
    DATA_DIR = Path(os.environ.get("DATA_DIR", "./data"))
    MB_USER_AGENT = os.environ.get("MB_USER_AGENT", "WaveAtlas/0.1 (set-your-contact)")
