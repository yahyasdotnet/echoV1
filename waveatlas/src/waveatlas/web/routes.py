from flask import Blueprint, current_app, jsonify, render_template

from waveatlas.core.scanner import scan_library

bp = Blueprint("main", __name__)


@bp.get("/")
def index():
    return render_template("index.html")


@bp.get("/api/health")
def health():
    return jsonify(status="ok", music_dir=str(current_app.config["MUSIC_DIR"]))


@bp.get("/api/library/scan")
def scan():
    tracks = list(scan_library(current_app.config["MUSIC_DIR"], limit=200))
    return jsonify(count=len(tracks), tracks=tracks)
