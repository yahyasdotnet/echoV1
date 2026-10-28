from waveatlas import create_app


def test_health(tmp_path):
    app = create_app({"TESTING": True, "MUSIC_DIR": tmp_path})
    res = app.test_client().get("/api/health")
    assert res.status_code == 200
    assert res.json["status"] == "ok"


def test_scan_empty_library(tmp_path):
    app = create_app({"TESTING": True, "MUSIC_DIR": tmp_path})
    res = app.test_client().get("/api/library/scan")
    assert res.json["count"] == 0
