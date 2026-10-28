from collections.abc import Iterator
from pathlib import Path

import mutagen

AUDIO_EXTS = {".mp3", ".flac", ".ogg", ".opus", ".m4a", ".wav", ".aac"}


def scan_library(root: Path, limit: int | None = None) -> Iterator[dict]:
    """Walk `root` read-only and yield basic tag info per audio file."""
    root = Path(root)
    if not root.is_dir():
        return
    n = 0
    for path in sorted(root.rglob("*")):
        if path.suffix.lower() not in AUDIO_EXTS or not path.is_file():
            continue
        tags = mutagen.File(path, easy=True)
        yield {
            "path": str(path.relative_to(root)),
            "title": _first(tags, "title"),
            "artist": _first(tags, "artist"),
            "album": _first(tags, "album"),
            "duration": round(tags.info.length, 1) if tags and tags.info else None,
        }
        n += 1
        if limit and n >= limit:
            return


def _first(tags, key: str) -> str | None:
    if not tags:
        return None
    vals = tags.get(key)
    return vals[0] if vals else None
