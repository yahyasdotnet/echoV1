import spotipy
from dotenv import load_dotenv
from spotipy.oauth2 import SpotifyClientCredentials


def create_spotify_client() -> spotipy.Spotify:
    load_dotenv()
    return spotipy.Spotify(auth_manager=SpotifyClientCredentials())


def main() -> None:
    spotify = create_spotify_client()
    results = spotify.search(q="daft punk", type="track", limit=5)

    for item in results["tracks"]["items"]:
        print(item["name"], "-", item["artists"][0]["name"])


if __name__ == "__main__":
    main()
