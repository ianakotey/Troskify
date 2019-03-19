import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Graph {

	private HashMap<String , ArrayList<link>> connections ;
	private HashMap<String , String[]> nodes ;
	private HashMap<String, String[]> trips;
	private float switchWeight = 3*10^3;
	private float maxsize =  Float.POSITIVE_INFINITY;

	public HashMap<String, ArrayList<link>> getConnections() {
		return connections;
	}



	public HashMap<String, String[]> getNodes() {
		return nodes;
	}


	public HashMap<String, String[]> getTrips() {
		return trips;
	}



	public float getSwitchWeight() {
		return switchWeight;
	}



	public float getMaxsize() {
		return maxsize;
	}


	Graph(HashMap<String , String[]> nodes , HashMap<String , String[]> trips, HashMap<String, ArrayList<link>> connections ) {
		this.connections = connections;
		this.nodes = nodes;
		this.trips = trips;
	}

	public float CalcCumWeight(float cumWeight , String sTripId , float distance , String tripId )  {
		float weight = cumWeight + distance;
		if(sTripId != tripId) {
			weight += this.switchWeight;
		}
		return weight;
	}

	public double haverSine(double lat1, double lon1, double lat2, double lon2) {
		//calculates the shortest distance given map coordinates
		final int R = 6371; // Radius of the earth

		double latDistance = toRad(lat2-lat1);
		double lonDistance = toRad(lon2-lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
				Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
				Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double distance = R * c;

		return distance;

	}

	private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		}
		else {
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;
			if (unit == "K") {
				dist = dist * 1.609344;
			} else if (unit == "N") {
				dist = dist * 0.8684;
			}
			return (dist);
		}
	}

	private static double toRad(double value) {
		return value * Math.PI / 180;
	}

	//takes double of the start(longitude and latitude) and float of end(longitude and latitude) find the closest point to the end
	public String[] FindClosestPoints(double[] positions) {

		double Startlat1 = positions[0];
		double Startlon1 = positions[1];
		double Endlat1 = positions[2];
		double Endlon1 = positions[3];

		double lat2;
		double lon2;

		double minDistanceStart = 0;
		double minDistanceEnd = 0;
		double StartDistance = 0;
		double EndDistance = 0;

		boolean firstRun = true;
		String startBusStop = null;
		String endBusStop = null;

		String key;
		String[] value;


		for (Map.Entry<String , String[]> node : this.nodes.entrySet()) {

			key = node.getKey();
			value = node.getValue();


			 lat2 = Double.parseDouble(value[2]);
			 lon2 = Double.parseDouble(value[3]);

			StartDistance = this.distance(Startlat1, Startlon1, lat2, lon2, "K");
			if (firstRun || StartDistance < minDistanceStart ) {
				// System.out.println(minDistanceStart);
				startBusStop = key;
				minDistanceStart = StartDistance;
			}

			EndDistance = this.haverSine(Endlat1, Endlon1, lat2, lon2);
			if (firstRun || EndDistance < minDistanceEnd ) {
				endBusStop = key;
				minDistanceEnd = EndDistance;
				// System.out.println(minDistanceEnd);
				firstRun = false;
			}


		}
		String[] busStops = {startBusStop, endBusStop};
		return busStops;
	}



	public link2 dijkstra(String source , String destination) {

		link2 start = new link2("", this.maxsize , this.maxsize , "Trippier");


		HashMap<String , link2> tracer = new HashMap<String, link2>();
		HashMap<String , Boolean> asPivot = new HashMap<String, Boolean>();

		for (Map.Entry<String , String[]> nodes : this.nodes.entrySet()) {

			tracer.put(nodes.getKey() ,start );


			asPivot.put(nodes.getKey() , Boolean.FALSE);

		}



		link2 update = new link2(source , 0 , 0 , "Trippier");

		tracer.replace(source , update);



		String pivot = source;

		String sTripId = "Trippier";


		while (true) {
			if( pivot == null || pivot.equals(destination)) {
				break;
			}

			for(link connection: this.connections.get(pivot)) {
				String adjNode = connection.id;

				float distance = connection.weight;
				String tripId = connection.carId;


				float cumWeight = this.CalcCumWeight(tracer.get(pivot).getWeightMaxint() , sTripId , distance , tripId );



				if(cumWeight < tracer.get(adjNode).getWeightMaxint()) {
					ArrayList<String> cumPath = new ArrayList<String>();


					for(String s : tracer.get(pivot).getStartString()) {
						cumPath.add(s);


					}
					cumPath.add(adjNode);


					float cumDistance = tracer.get(pivot).getDistanceMaxint() + distance;

					ArrayList<String> cumTrip = new ArrayList<String>();
					for(String s : tracer.get(pivot).getEndString()) {
						cumTrip.add(s);

					}
					cumTrip.add(tripId);

					link2 add = new link2(cumPath , cumWeight , cumDistance , cumTrip);
					tracer.put(adjNode ,add );

				}

			}


			asPivot.put(pivot , Boolean.TRUE);
			float minWeight  = this.maxsize;
			pivot = null;


			for (Map.Entry<String,link2> entry : tracer.entrySet()) {
				String  key = entry.getKey();
				link2 value = entry.getValue();

				float weight = value.getWeightMaxint();

				if(weight < minWeight && !asPivot.get(key)) {
					minWeight = weight;
					pivot = key;

					sTripId = value.getEndString()[value.getSize2()-1];
				}
			}



		}


		if(pivot != null) {
			return tracer.get(pivot);
		}
		else return null;

	}

	public static Graph makeGraph(String nodePath , String tripPath , String connectionPath) {

		HashMap<String , ArrayList<link>> connections = new HashMap<String, ArrayList<link>>();
		HashMap<String , String[]> nodes = new HashMap<String, String[]>();
		HashMap<String, String[]> trips = new HashMap<String, String[]>();


		String line = "";
		String cvsSplitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(nodePath))) {

			while ((line = br.readLine()) != null) {

				String[] row = line.split(cvsSplitBy);

				nodes.put(row[0] , row);


			}

		} catch (IOException e) {
			e.printStackTrace();
		}


		String line2 = "";
		try (BufferedReader br = new BufferedReader(new FileReader(tripPath))) {

			while ((line2 = br.readLine()) != null) {

				String[] row = line2.split(cvsSplitBy);
				trips.put(row[0] , row);
			}



		} catch (IOException e) {
			e.printStackTrace();
		}


		String line3 = "";
		try (BufferedReader br = new BufferedReader(new FileReader(connectionPath))) {

			while ((line3 = br.readLine()) != null) {

				String[] row = line3.split(cvsSplitBy);
				String sType = row[0];
				String tripId = row[1];
				String fromStop = row[2];
				String toStop = row[3];
				String weight = row[4];
				int one  = Integer.parseInt(sType);

				//                System.out.print(sType);
				//                System.out.print(", ");
				//                System.out.print(fromStop);
				//                System.out.print(", ");
				//                System.out.println(toStop);

				if(one != 1 ) {

					link N  = new link(toStop , Float.parseFloat(weight) , tripId);

					if(connections.containsKey(fromStop)) {
						connections.get(fromStop).add(N);
					}

					else {
						ArrayList<link> NList = new ArrayList<link>();
						NList.add(N);
						connections.put(fromStop, NList);
					}

					link N2 = new link();

					if(!connections.containsKey(toStop)) {
						connections.put(toStop ,new ArrayList<link>());
					}

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Graph(nodes , trips , connections);
	}

}

class link {
	public String id;
	public float weight;
	public String carId;

	link(String id , float weight , String carId) {
		this.id = id;
		this.weight = weight;
		this.carId = carId;
	}

	link() {
	}

	@Override
	public String toString() {
		return "link{" +
				"id='" + id + '\'' +
				", weight=" + weight +
				", carId='" + carId + '\'' +
				'}';
	}
}


class link2 {


	private int size1 = 0;
	private int size2 = 0;

	private ArrayList<String> startString = new ArrayList<String>();
	private float weightMaxint;
	private float distanceMaxint;
	private ArrayList<String> end = new ArrayList<String>();

	link2(String s  , float Wmaxint ,float Dmaxint , String end) {
		this.startString.add(s);
		this.end.add(end);
		this.weightMaxint = Wmaxint;
		this.distanceMaxint = Dmaxint;
	}

	link2(ArrayList<String> s , float Wmaxint , float Dmaxint , ArrayList<String> s2) {
		this.startString = s;
		this.end = s2;
		this.weightMaxint = Wmaxint;
		this.distanceMaxint = Dmaxint;

	}

	public float getWeightMaxint() {
		return weightMaxint;
	}


	public float getDistanceMaxint() {
		return distanceMaxint;
	}

	public String[] getStartString() {
		this.size1 = 0;

		String[] giveBack = new String[this.startString.size()];


		for(String s: this.startString) {
			giveBack[size1] = s;
			size1++;
		}
		return giveBack;
	}

	public String[] getEndString() {
		this.size2 = 0;

		String[] giveBack = new String[this.end.size()];


		for(String s: this.end) {
			giveBack[size2] = s;
			size2++;
		}

		return giveBack;
	}


	@Override
	public String toString() {
		return "link2{" +
				"size1=" + size1 +
				", size2=" + size2 +
				", startString=" + startString +
				", weightMaxint=" + weightMaxint +
				", distanceMaxint=" + distanceMaxint +
				", end=" + end +
				'}';
	}

	public int getSize1() {
		return size1;
	}

	public int getSize2() {
		return size2;
	}



}
