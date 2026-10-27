from pydantic import BaseModel

from .source import Source


class Artist(BaseModel):
    id: str
    name: str
    source: Source
