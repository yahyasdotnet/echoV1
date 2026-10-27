from pydantic import BaseModel, Field

from .artist import Artist
from .source import Source


class Album(BaseModel):
    id: str
    title: str
    artists: list[Artist] = Field(default_factory=list)
    year: int | None = None
    cover_url: str | None = None
    source: Source
