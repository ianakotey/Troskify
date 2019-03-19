import subprocess
import re

class Interface():
    def __init__(self):
        self.process = subprocess.Popen(
            ["java", "Main"],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE
        )

    def communicate(self, params):
        params = self._format_params(params)
        b_params = bytes(params, "utf-8")
        self.process.stdout.flush()
        self.process.stdin.write(b_params)
        self.process.stdin.flush()
        output = self.process.stdout.readline()

        return self._format_output(output)

    def _format_params(self, params):
        if params[-1] != "\n":
            params += "\n"
        return params

    def _format_output(self, output):
        output = output.decode("utf-8").strip("\n")
        if output in ["No path found", "Malformed Data"]:
            to_return = {
                "totalD": 0,
                "info":[{"lat": 0, "lng": 0, "label": "None"}],
                "sentences": [{"sentence": "No path found"}]
            }
            return to_return
        distance, stops, instructions = tuple(output.split("&&&"))
        distance = float(distance)
        stops = stops.strip("||").split("||")
        info_set = []
        for stop in stops:
            lat, lon, name = tuple(stop.split(","))
            lat, lon = float(lat), float(lon)
            info_set.append({"lat": lat, "lng": lon, "label": name})

        instructions = instructions.strip("||").split("||")
        sentences_set = []
        for instruction in instructions:
            sentences_set.append({"sentence": instruction})

        to_return = {
            "totalD": distance,
            "info": info_set,
            "sentences": sentences_set
        }
        return to_return

def main():
    interface = Interface()
    while True:
        x = input("Whassop: ")
        interface.communicate(x)


if __name__ == "__main__":
    main()
