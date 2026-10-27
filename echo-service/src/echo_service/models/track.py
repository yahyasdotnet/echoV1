from pydantic import BaseModel

from .album import Album
from .artist import Artist
from .source import Source


class Track(BaseModel):
    id: str
    title: str
    artists: list[Artist]
    album: Album | None = None
    duration_ms: int | None = None
    track_number: int | None = None
    disc_number: int | None = None
    source: Source

    file_path: str | None = None
    uri: str | None = None
    preview_url: str | None = None
