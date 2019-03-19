# Troskify
Small web-based app to recommend public transportation routes within the city of Accra, Ghana

# Prerequisites
* Linux system (**Debian-based OS** preferred)
```
* Java 10
```
* Internet connection - Utilizes the [Google Maps Platform](https://cloud.google.com/maps-platform/) for map rendering
```
* Pyhon 3.x
```
# How it works
The app makes use of the haversine formula to calculate distances between busstops and Dijksra's Shortest Path Algorithm to compute the best trip among all possible permutations, returning the results to the web app that displays the routes using pins on the map.

# How to use
1. Switch to main project directory in a terminal
2. Run "*Python3* **server.py**"
3. Navigate using a browser to [localhost:8080](localhost:8080)
4. Click on 'Begin'
5. Enter a start point and end point (Within the city of Accra *only*)
6. Click on 'Start Trip'
7. To restart, click on 'New Trip'

## Credits
* **Oracking Amenreynolds**
* **Kelvin Ampene**
* **Otitodirichukwu N. Effiong-Akpan**
