from flask import Flask, jsonify, request

app = Flask(__name__)


@app.get("/health")
def health():
    return jsonify(status="ok")


@app.route("/echo", methods=["GET", "POST"])
def echo():
    return jsonify(
        method=request.method,
        args=request.args,
        json=request.get_json(silent=True),
        headers=dict(request.headers),
    )


if __name__ == "__main__":
    app.run(debug=True, port=5000)