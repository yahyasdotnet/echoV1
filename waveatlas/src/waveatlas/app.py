from flask import Flask

from waveatlas.config import Config


def create_app(config: type | dict | None = None) -> Flask:
    app = Flask(__name__, template_folder="web/templates", static_folder="web/static")
    app.config.from_object(Config)
    if isinstance(config, dict):
        app.config.update(config)
    elif config is not None:
        app.config.from_object(config)

    from waveatlas.web.routes import bp

    app.register_blueprint(bp)
    return app


def main() -> None:
    """Dev server entry point: `uv run waveatlas`."""
    from dotenv import load_dotenv

    load_dotenv()
    create_app().run(host="0.0.0.0", port=8080, debug=True)
