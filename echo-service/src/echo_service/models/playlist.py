from pydantic import BaseModel, Field

from .source import Source
from .track import Track


class Playlist(BaseModel):
    id: str
    name: str
    description: str | None = None
    tracks: list[Track] = Field(default_factory=list)
    source: Source
