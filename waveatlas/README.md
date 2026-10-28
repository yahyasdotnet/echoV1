# WaveAtlas

> A self-hosted taste engine for your music. Explore your library as a map, paint a playlist as an energy curve, and let the app learn what you like, all locally, with no accounts and no data leaving your server.

**Status:** early development / pre-alpha. Everything below describes the target design; see the [Roadmap](#roadmap) for what exists today.

*(WaveAtlas is a placeholder name. Rename freely.)*

---

## Why

Most music discovery tools ask you to type a prompt or just feed you a stream. Most library managers are great at organizing files and terrible at helping you find what to listen to. WaveAtlas sits between them:

- **For uncertain people:** point at things instead of describing them. Pick artists from a bubble field, drag a mood dot, answer a few A/B sound tests.
- **For lazy people:** hit one button and get a solid starter library or playlist, kept fresh over time.
- **For everyone:** see your whole collection as a zoomable map and build playlists by lassoing regions or drawing a curve.

## Features

### Explore
- **Embedding map:** your library projected to 2D (UMAP) from audio embeddings, rendered with WebGL so tens of thousands of tracks stay smooth. Click to play, lasso to select.
- **Linked lenses:** map, timeline (eras), genre sunburst, mood plane, and taste radar. Selecting in one highlights in the others.
- **Hover previews:** a short snippet from the interesting part of the track, not the intro.

### Build
- **Energy-curve painter:** draw an arc (calm, build, peak, comedown) and the app fills it with matching tracks.
- **Journey mode:** pick a start and end song and get a path through the map between them.
- **Include and exclude regions:** paint areas you want more of, and areas you never want.
- **Comfort to adventure slider:** controls how far from your taste cluster recommendations are sampled.
- **Export:** `.m3u` playlists, with Navidrome / Jellyfin / Plex integration planned.

### Understand
- **Taste fingerprint:** a generated poster of your library's shape.
- **Explainable picks:** every recommendation shows why (path on the map, shared tags, similar artists you own).
- **Library health:** completeness per artist and album, genre balance, duplicates, suspected fake lossless, match-confidence review queue.

### Discover (optional)
- Suggestions from MusicBrainz relationships and ListenBrainz data.
- A wishlist you can hand off to tools like Lidarr.
- Optional, user-configured acquisition plugins (see [Legal](#legal-and-responsible-use)).

## How it works

```
 local files ─► scan ─► identify (MusicBrainz / AcoustID) ─► analyze (Essentia) ─► embed ─► index
                                                                                      │
                                           taste model ◄── skips, likes, lassos ◄─────┤
                                                │                                     │
                                                └──────────────► web UI ◄─────────────┘
```

| Layer | Tech |
|---|---|
| Web / API | Flask + gunicorn |
| Frontend | Svelte or React, deck.gl (WebGL), D3, wavesurfer.js |
| Jobs | Celery or RQ + Redis |
| Storage | PostgreSQL + pgvector |
| Audio analysis | Essentia (TensorFlow models), librosa, CLAP / MusiCNN embeddings |
| Metadata | MusicBrainz (API or self-hosted mirror), AcoustID / Chromaprint |
| Optional acquisition | slskd (REST API) and other plugins |

Design principles:

- **Pipeline separate from UI:** a core library (scan, identify, analyze, embed, index) with the Flask app as one client.
- **Idempotent, resumable jobs:** every track has a state; a restart never redoes finished work.
- **Versioned embeddings:** model name and version stored per vector so models can be swapped later.
- **Content-hashed tracks:** moving or renaming files doesn't lose history or taste data.
- **Non-destructive:** your files are never modified or moved without explicit opt-in.

## Quick start

Requirements: Docker and Docker Compose, a music folder, and roughly 4 GB RAM free for analysis. x86-64 is the primary target; ARM support is best-effort.

```bash
git clone https://github.com/<you>/waveatlas.git
cd waveatlas
cp .env.example .env     # set MUSIC_DIR, PUID, PGID, and a UI password
docker compose up -d
```

Then open <http://localhost:8080>.

**Try it without your library:** start in demo mode with a bundled set of CC-licensed tracks.

```bash
docker compose --profile demo up -d
```

### Services

```yaml
services:
  web:      # Flask API + static frontend
  worker:   # scanning, Essentia analysis, UMAP
  redis:    # job queue and cache
  db:       # Postgres + pgvector
  # opt-in profiles
  slskd:        # optional acquisition plugin, internal network only
  gluetun:      # optional VPN container for slskd
  musicbrainz:  # optional local MusicBrainz mirror (heavy)
  gpu-worker:   # optional GPU analysis worker
```

### Configuration

| Variable | Description | Default |
|---|---|---|
| `MUSIC_DIR` | Path to your library (mounted read-only) | required |
| `INCOMING_DIR` | Writable folder for new acquisitions | `./incoming` |
| `PUID` / `PGID` | User and group IDs for file access | `1000` |
| `UI_PASSWORD` | Basic auth for the web UI (or configure OIDC) | required |
| `MB_USER_AGENT` | User-Agent sent to MusicBrainz | required |
| `ANALYSIS_WORKERS` | Parallel analysis processes | `2` |

### Notes for self-hosters

- **Permissions:** set `PUID` / `PGID` to match the owner of your music files.
- **Network shares:** inotify doesn't work on NFS/SMB, so use scheduled rescans instead of file watching.
- **First run:** pretrained models download into a persistent volume, not the image.
- **Analysis time:** expect a first scan of a large library to take hours. Progress is shown live in the UI and jobs are resumable.
- **MusicBrainz rate limit:** the public API allows about 1 request per second. Responses are cached; for big libraries use the optional mirror.
- **Reverse proxy:** works behind Traefik, Caddy, or Nginx Proxy Manager. Don't expose the slskd container.

## Roadmap

- [ ] **Phase 0, prove the core:** scan, analyze, UMAP map of a small library, click to play
- [ ] **Phase 1, delightful:** lasso/exclude, energy-curve painter, M3U export, demo mode
- [ ] **Phase 2, smart:** taste model, comfort-to-adventure slider, explanations
- [ ] **Phase 3, discover:** MusicBrainz/ListenBrainz suggestions, wishlist, Lidarr handoff
- [ ] **Phase 4, grow:** plugin system, optional acquisition modules, release polish

## Project layout (planned)

```
waveatlas/
├── core/          # pipeline library: scan, identify, analyze, embed, index
├── web/           # Flask app and API
├── frontend/      # JS frontend
├── worker/        # job definitions
├── plugins/       # sources, metadata providers, export targets
├── docker/        # Dockerfiles and compose profiles
└── docs/
```

## Legal and responsible use

WaveAtlas works fully with your own local files. Acquisition integrations (such as slskd) are **optional, disabled by default, and configured by you**. You are responsible for complying with the laws of your country and the terms of any network or service you connect to. Please rate-limit automated searches and respect the communities you use.

## Contributing

Issues and pull requests are welcome, especially for audio analysis quality, metadata matching, and UI. Please open an issue to discuss larger changes first.

## Acknowledgements

[MusicBrainz](https://musicbrainz.org), [ListenBrainz](https://listenbrainz.org), [Essentia](https://essentia.upf.edu), [slskd](https://github.com/slskd/slskd), [Navidrome](https://www.navidrome.org), [Lidarr](https://lidarr.audio), [deck.gl](https://deck.gl), [wavesurfer.js](https://wavesurfer.xyz).

## License

TBD (AGPL-3.0 to protect against closed forks, or MIT for maximum adoption).
