import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {

	public static double[] parseInput(String rawInput){
        String[] params = rawInput.split(",");
        double[] parsedOutput = new double[4];
        for (int i=0; i < 4; i++){
            parsedOutput[i] = Double.parseDouble(params[i]);
        }
        return parsedOutput;
    }

	public static void getPath(Graph graph, double[] positions){
		String[] busStops = graph.FindClosestPoints(positions);

		String start = busStops[0];
		String end = busStops[1];

		link2 path = graph.dijkstra(start, end);

		if (path == null || start.equals(end)){
			System.out.println("No path found");
		}
		else{
		System.out.print(path.getDistanceMaxint());
		System.out.print("&&&");


		while (true) {


			ArrayList<String[]> cumPath = new ArrayList<String[]>();

			for(int i = 0 ; i < path.getSize2() ; i++) {
				String[] A = {path.getStartString()[i] , path.getEndString()[i]};
				cumPath.add(A);
			}

			ArrayList<String[]> instructions = new ArrayList<String[]>();
			ArrayList<String[]> stack = new ArrayList<String[]>();

			for(int i = 0 ; i < cumPath.size() ; i++) {

				if(i < 2) {
					stack.add(cumPath.get(i));
				}
				else {
					if(cumPath.get(i)[1].equals(stack.get(stack.size()-1)[1])) {
						stack.add(cumPath.get(i));
					}
					else {
						String[] N = {(stack.get(0)[0]) , (stack.get(stack.size()-1)[0]), (stack.get(stack.size()-1)[1])};

						instructions.add(N);
						String[] changOver = stack.get(stack.size()-1);


						ArrayList<String[]> New = new ArrayList<String[]>();
						New.add(changOver);
						New.add(cumPath.get(i));

						stack = New;
					}
				}
			}

			if(!stack.isEmpty()) {
				String[] N = {(stack.get(0)[0]) , (stack.get(stack.size()-1)[0]), (stack.get(stack.size()-1)[1])};


				instructions.add(N);
			}

			String finall = new String();

			HashMap<String, String[]> nodes =graph.getNodes();

			for (String[] item : instructions) {
				String[] info = nodes.get(item[0]);
				System.out.print(info[2] +','+ info[3] +','+ info[1] +"||");
			}

			if (instructions.size() > 0){
				String last = instructions.get(instructions.size()-1)[1];
				String[] info = nodes.get(last);
				System.out.print(info[2] +','+ info[3] +','+ info[1] +"||");
				System.out.print("&&&");
			}

			for (String[] instruction: instructions) {
			   String busName = graph.getNodes().get(graph.getTrips().get(instruction[2])[2])[1];

			   String startStop = graph.getNodes().get(instruction[0])[1];

			   String endStop = graph.getNodes().get(instruction[1])[1];

			   System.out.print("Pick a "+ busName + " bus from " + startStop + " to " + endStop + "||");

			}
			System.out.println("");
			break;
		}
	}

	}

	public static double [] readInput(){
        String[] positionsStr;
        double positions [] = null;
		Scanner scanner = new Scanner(System.in);

        String node_path = "serialized_graph/nodes.csv";
        String trip_path = "serialized_graph/trips.csv";
        String connection_path = "serialized_graph/connections.csv";

        Graph graph = Graph.makeGraph(node_path, trip_path, connection_path);

		while (true){
			try {
	                String rawInput = scanner.nextLine();
	                double[] params = parseInput(rawInput);
					getPath(graph, params);
	        }

	        catch(Exception e) {
	            System.out.println("Malformed Data");
	        }
		}
    }

    public static void main(String[] args) {
       readInput();
    }

}
