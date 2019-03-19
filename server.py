from bottle import Bottle, request, run, response, static_file
from interface import Interface

interface = Interface()
app = Bottle()

@app.hook("after_request")
def enable_cors():
    """
    You need to add some headers to each request.
    Don't use the wildcard '*' for Access-Control-Allow-Origin in production.
    """
    response.headers["Access-Control-Allow-Origin"] = "*"
    response.headers["Access-Control-Allow-Methods"] = ("PUT, GET, POST, "
        "DELETE, OPTIONS")
    response.headers["Access-Control-Allow-Headers"] = ("Origin, Accept, "
        "Content-Type, X-Requested-With, X-CSRF-Token")
    return response


@app.route("/")
def index():
    return static_file("index.html", root="Front/")

@app.route("/initialize", method=["GET"])
def initialize():
    return static_file("system.html", root="Front/")

@app.route("/css", method=["GET"])
def get_css():
    return static_file("system.css", root="Front/")

@app.route("/js", method=["GET"])
def get_js():
    return static_file("map.js", root="Front/")

@app.route("/getPath", method=["OPTIONS", "POST"])
def get_path():
    data = request.json
    try:
        to_send = (
            str(data["startLat"])
            + "," + str(data["startLng"])
            + "," + str(data["endLat"])
            + "," + str(data["endLng"])
        )

    except:
        return {
            "totalD": 0,
            "info":[{"lat": 0, "lng": 0, "label": "None"}],
            "sentences": [{"sentence": "No path found"}]
        }

    else:
        result = interface.communicate(to_send)
        if result == "Malformed Data":
            return {
                "totalD": 0,
                "info":[{"lat": 0, "lng": 0, "label": "None"}],
                "sentences": [{"sentence": "No path found"}]
            }
        return result

run(app, host="localhost", port=8080)
