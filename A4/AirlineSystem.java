import java.util.*;

import java.io.*;

final public class AirlineSystem implements AirlineInterface {
	private static ArrayList<String> cities;
	private int numOfCities;
	private Digraph G;
	private ArrayList<Route> routes;

	/**
	 * reads the city names and the routes from a file
	 * 
	 * @param fileName the String file name
	 * @return true if routes loaded successfully and false otherwise
	 */
	public boolean loadRoutes(String fileName) {
		try {
			// Open txt file for scanning
			Scanner fileScan = new Scanner(new FileInputStream(fileName));

			// Get the number of cities (first line in txt file).
			numOfCities = fileScan.nextInt();
			cities = new ArrayList<>(numOfCities);
			G = new Digraph(numOfCities); // new graph object

			String cityName;

			// Read city names into set
			for (int i = 0; i < numOfCities; i++) {
				cityName = fileScan.next();
				cities.add(cityName);
			}

			int source;
			int destination;
			int distance;
			double price;

			// Read the direct routes and their wieghts
			while (fileScan.hasNext()) {
				source = fileScan.nextInt() - 1; // get the source
				destination = fileScan.nextInt() - 1; // get the destination
				distance = fileScan.nextInt(); // get the distance
				price = fileScan.nextDouble(); // get the price

				// add routes to the graph
				G.addEdge(new Route(cities.get(source), cities.get(destination), distance, price));
				G.addEdge(new Route(cities.get(destination), cities.get(source), distance, price));
			}

			// close file
			fileScan.close();
		}

		// Return false if no such file is found
		catch (IOException e) {
			return false;
		}

		return true;
	}

	/**
	 * writes the city names and the routes into a file
	 * 
	 * @param fileName the String file name
	 * @return true if routes saved successfully and false otherwise
	 */
	public boolean saveRoutes(String fileName) {
		// create a File object to write to the file.
		try {
			FileWriter writer = new FileWriter(fileName);

			for (String city : cities) {
				writer.write(city);
			}

			for (Route route : routes) {
				writer.write(route.toString());
			}

			writer.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * returns the set of city names in the Airline system
	 * 
	 * @return a (possibly empty) Set<String> of city names
	 */
	public Set<String> retrieveCityNames() {
		if (cities.isEmpty())
			return null;

		Set<String> cityNames = new LinkedHashSet<String>();

		// Return the city names in order
		for (String city : cities)
			cityNames.add(city);

		// Return the set of city names
		return cityNames;
	}

	/**
	 * returns the set of direct routes out of a given city
	 * 
	 * @param city the String city name
	 * @return a (possibly empty) Set<Route> of Route objects representing the
	 *         direct routes out of city
	 * @throws CityNotFoundException if the city is not found in the Airline
	 *                               system
	 */
	public Set<Route> retrieveDirectRoutesFrom(String city)
			throws CityNotFoundException {
		if (!cities.contains(city))
			throw new CityNotFoundException(city);

		// Get the index of city from HashSet
		int i = cities.indexOf(city);

		// Get the index of the city
		// Create an empty set of direct routes
		Set<Route> directRoutes = new HashSet<Route>();

		// For each route r of city i
		// add r to set of direct routes
		for (Route r : G.adj(i))
			directRoutes.add(r);

		// Return the set of direct routes
		return directRoutes;
	}

	/**
	 * finds cheapest path(s) between two cities
	 * 
	 * @param source      the String source city name
	 * @param destination the String destination city name
	 * @return a (possibly empty) Set<ArrayList<Route>> of cheapest
	 *         paths. Each path is an ArrayList<Route> of Route objects that
	 *         includes a
	 *         Route out of the source and a Route into the destination.
	 * @throws CityNotFoundException if any of the two cities are not found in the
	 *                               Airline system
	 */
	public Set<ArrayList<Route>> cheapestItinerary(String source,
			String destination) throws CityNotFoundException {
		if (source.equals(destination))
			return null;

		int s = cities.indexOf(source);
		int d = cities.indexOf(destination);

		// check if any of the cities are not found in Airline system.
		if ((!cities.contains(source) || (!cities.contains(destination))))
			throw new CityNotFoundException(source);

		Set<ArrayList<Route>> shortestDistanceSet = new LinkedHashSet<ArrayList<Route>>();

		G.dijkstras(s);
		if (G.marked[d]) {
			ArrayList<Route> shortestDistPath = new ArrayList<Route>();
			Stack<Integer> path = new Stack<>();

			for (int x = d; x != s; x = G.edgeTo[x]) {
				path.push(x);

				if (G.edgeTo[x] == s) {
					path.push(s);
					break;
				}
			}

			int prevVertex = s;

			while (!path.isEmpty()) {
				int v = path.pop();

				Iterable<Route> children = G.adj(prevVertex);

				for (Route r : children) {
					int to = cities.indexOf(r.destination);
					if (to == v) {
						shortestDistPath.add(r);
						prevVertex = to;
						break;
					}
				}
			}
			shortestDistanceSet.add(shortestDistPath);
		}
		return shortestDistanceSet;

	}

	/**
	 * finds cheapest path(s) between two cities going through a third city
	 * 
	 * @param source      the String source city name
	 * @param transit     the String transit city name
	 * @param destination the String destination city name
	 * @return a (possibly empty) Set<ArrayList<Route>> of cheapest
	 *         paths. Each path is an ArrayList<Route> of city names that includes
	 *         a Route out of source, into and out of transit, and into destination.
	 * @throws CityNotFoundException if any of the three cities are not found in
	 *                               the Airline system
	 */
	public Set<ArrayList<Route>> cheapestItinerary(String source,
			String transit, String destination) throws CityNotFoundException {
		// check if any of the cities are not found in Airline system.
		if ((!cities.contains(source) || (!cities.contains(destination))) || (!cities.contains(transit)))
			throw new CityNotFoundException(source);

		int s = cities.indexOf(source);
		int d = cities.indexOf(destination);
		int t = cities.indexOf(transit);

		Set<ArrayList<Route>> cheapestPathSet = new LinkedHashSet<ArrayList<Route>>();

		G.dijkstras(s);
		if (G.marked[t]) {
			G.dijkstras(t); // check if shortest distance from source to destination exists.
			if (!G.marked[d])
				return cheapestPathSet;

			ArrayList<Route> cheapestPath = new ArrayList<Route>();
			Stack<Integer> path = new Stack<>();
			// G.dijkstras(t);

			for (int x = d; x != s; x = G.edgeTo[x]) {
				path.push(x);

				if (G.edgeTo[x] == t) // check if transit has been reached.
				{
					path.push(t); // push the transit vertex onto stack if so.
					G.dijkstras(s); // call dijkstras once more to find path from transit to source.
					x = G.edgeTo[t];
					path.push(x);
					x = t;
				}
			}

			int prevVertex = s;
			while (!path.isEmpty()) {
				int v = path.pop();

				Iterable<Route> children = G.adj(prevVertex);

				for (Route route : children) {
					int to = cities.indexOf(route.destination);
					if (to == v) {
						cheapestPath.add(route);
						prevVertex = to;
						break;
					}
				}
			}

			cheapestPathSet.add(cheapestPath);
		}

		return cheapestPathSet;
	}

	/**
	 * finds one Minimum Spanning Tree (MST) for each connected component of
	 * the graph
	 * 
	 * @return a (possibly empty) Set<Set<Route>> of MSTs. Each MST is a Set<Route>
	 *         of Route objects representing the MST edges.
	 */

	public Set<Set<Route>> getMSTs() {
		Set<Set<Route>> mstSet = new HashSet<Set<Route>>();
		mstSet.add(G.kruskals());

		return mstSet;
	}

	/**
	 * finds all itineraries starting out of a source city and within a given
	 * price
	 * 
	 * @param city   the String city name
	 * @param budget the double budget amount in dollars
	 * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
	 *         less than or equal to the budget. Each path is an ArrayList<Route> of
	 *         Route
	 *         objects starting with a Route object out of the source city.
	 */
	public Set<ArrayList<Route>> tripsWithin(String city, double budget)
			throws CityNotFoundException {
		if (!cities.contains(city))
			throw new CityNotFoundException(city);

		int source = cities.indexOf(city);

		Set<ArrayList<Route>> citiesInTrip = new HashSet<>(); // the set of cities included in such trip.
		boolean[] verticiesVisited = new boolean[numOfCities]; // an array to track verticies visited.
		this.routes = new ArrayList<>(); // list of routes within budget.

		double[] costTo = new double[numOfCities];
		int[] edgeTo = new int[numOfCities];

		for (int i = 0; i < edgeTo.length; i++) {
			edgeTo[i] = -1;
			costTo[i] = 0.0;
			verticiesVisited[i] = false;
		}

		int current;
		verticiesVisited[source] = true; // mark the source vertex as visited.

		Queue<Integer> queue = new LinkedList<>(); // queue to perform a bfs traversal from source vertex.
		queue.add(source); // sdd the source vertex to the queue.

		while (!queue.isEmpty()) // iterate until each path within the budget
		{
			current = queue.poll(); // get the next vertex from the queue.

			for (Route r : G.adj(current)) // check if each neighbor is visited or within budget.
			{
				double costToNeighbor = costTo[current] + r.price; // calculate the cost to the neighbor.
				int neighbor = cities.indexOf(r.destination); // index of the neighbor vertex.

				if ((neighbor == source) || (neighbor == edgeTo[current])) // skip if neighbor is the source vertex.
					continue;

				// check if neighbor is within budget.
				if (costToNeighbor <= budget) {
					ArrayList<Route> path = new ArrayList<Route>();

					if (!verticiesVisited[neighbor]) // check if neighbor has been visited; process otherwise.
					{
						queue.add(neighbor); // add neighbor to the queue.
						edgeTo[neighbor] = current; // create path from neighbor to source.
						costTo[neighbor] = costToNeighbor; // initialize cost to neighbor from source.
						verticiesVisited[neighbor] = true; // mark neighbor as visited.
						path.add(r);
					}

					// get the path from current to source.
					for (int child = current; edgeTo[child] != -1; child = edgeTo[child]) {
						int parent = edgeTo[child]; // get the parent of the vertex in the path.
						for (Route r1 : G.adj(parent)) {
							int vertex = cities.indexOf(r1.destination);
							if (vertex == child)
								path.add(0, r1); // add to the beginning of the path.

							break;
						}
					}

					citiesInTrip.add(new ArrayList<>(path)); // add the path to the list of valid paths.
				}
			}
		}

		return citiesInTrip;
	}

	/**
	 * finds all itineraries within a given price regardless of the
	 * starting city
	 * 
	 * @param budget the double budget amount in dollars
	 * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
	 *         less than or equal to the budget. Each path is an ArrayList<Route> of
	 *         Route
	 *         objects.
	 */
	public Set<ArrayList<Route>> tripsWithin(double budget) {
		Set<ArrayList<Route>> tripSet = new HashSet<ArrayList<Route>>();
		Set<ArrayList<Route>> tripList;

		for (String city : cities) {
			try {
				tripList = tripsWithin(city, budget);

				for (ArrayList<Route> trip : tripList)
					tripSet.add(trip);
			} catch (CityNotFoundException e) {
				System.out.println(e + " No such city.");
			}
		}

		return tripSet;
	}

	/**
	 * delete a given non-stop route from the Airline's schedule. Both directions
	 * of the route have to be deleted.
	 * 
	 * @param source      the String source city name
	 * @param destination the String destination city name
	 * @return true if the route is deleted successfully and false if no route
	 *         existed between the two cities
	 * @throws CityNotFoundException if any of the two cities are not found in the
	 *                               Airline system
	 */
	public boolean deleteRoute(String source, String destination)
			throws CityNotFoundException {

		// check if any of the cities are not found in Airline system.
		if ((!cities.contains(source) || (!cities.contains(destination)))) {
			throw new CityNotFoundException(source);
		}

		int s = cities.indexOf(source);
		int d = cities.indexOf(destination);

		return G.deleteEdge(s, d);
	}

	/**
	 * delete a given city and all non-stop routes out of and into the city from
	 * the Airline schedule.
	 * 
	 * @param city the String city name
	 * @throws CityNotFoundException if the city is not found in the Airline system
	 */
	public void deleteCity(String city) throws CityNotFoundException {
		// check if any of the cities are not found in Airline system.
		if (!retrieveCityNames().contains(city)) {
			throw new CityNotFoundException(city);
		}

		int cityToDelete = cities.indexOf(city);// get the index of the city to delete.

		// loop through every route for each city.
		// delete any route with a corresponding destination to
		// the city to delete.
		for (int i = 0; i < numOfCities; i++) {
			if (i == cityToDelete)
				continue;

			for (Route route : G.adj(i)) {
				int d = cities.indexOf(route.destination);
				if (d == cityToDelete) {
					G.deleteEdge(i, cityToDelete);
					break;
				}
			}
		}

		G.deleteVertex(cityToDelete);
		cities.remove(cityToDelete);
		numOfCities--;
	}

	private class Digraph {
		private static final int INFINITY = Integer.MAX_VALUE;
		private int v; // number of vertices.
		private int e; // number of edges.
		private ArrayList<LinkedList<Route>> adj; // adjacency list.
		private int[] edgeTo; // edgeTo[i] = previous edge on shortest s-d path.
		private double[] costTo; // cost[i] = cost to vertex i from edgeTo[i].
		boolean[] marked; // array to track vertices that have been visited.
		private int[] bestEdge; // array of best edges to vertices forming an MST.

		// contructor for Digraph object with v vertices
		public Digraph(int v) {
			if (v < 0)
				throw new RuntimeException("Number of vertices must be nonnegative");
			this.v = v;
			this.e = 0;

			ArrayList<LinkedList<Route>> temp = new ArrayList<LinkedList<Route>>();
			adj = temp;

			// create linked list of routes for each vertex v
			for (int i = 0; i < v; i++)
				adj.add(new LinkedList<Route>());
		}

		/**
		 * Add the route r to this directed graph.
		 */
		public void addEdge(Route route) {
			int from = cities.indexOf(route.source);
			adj.get(from).add(route);
			e++;
		}

		// function to delete an edge given a source and destination.
		public boolean deleteEdge(int source, int destination) {
			boolean success = false;

			// Traverse the graph until the right route is located.
			for (Route route : adj(source)) {
				int d = cities.indexOf(route.destination);
				if (d == destination) // delete the route if the destinations correspond.
				{
					adj.get(source).remove(route); // remove the route from the graph departing from "source".
					success = true; // initialize a successful removal.
					e--;
					break;
				}
			}

			// Traverse the graph until the right route is located.
			for (Route route : adj(destination)) {
				int s = cities.indexOf(route.destination);
				if (s == source) // delete the route if the destinations correspond.
				{
					adj.get(destination).remove(route); // remove the route from the graph departing from "destination".
					e--;
					return success; // return the success status.
				}
			}

			return success;
		}

		public void deleteVertex(int vertexToDelete) {
			adj.remove(vertexToDelete);
			v--;
		}

		public Iterable<Route> adj(int i) {
			return adj.get(i);
		}

		// Dijkstra's algorithm to help perform BEST-First-Search.
		public void dijkstras(int source) {
			marked = new boolean[this.v]; // marked array of vertices.
			costTo = new double[this.v]; // cost array array of vetices.

			edgeTo = new int[this.v]; // parent array of vertices.
			for (int i = 0; i < v; i++) {
				costTo[i] = INFINITY;
				marked[i] = false;
			}

			costTo[source] = 0.0;
			marked[source] = true;
			int nMarked = 1;
			int current = source;

			while (nMarked < this.v) {
				for (Route r : adj(current)) {
					int d = cities.indexOf(r.destination);
					if ((costTo[current] + r.price) < costTo[d]) {
						// :update edgeTo and costTo
						costTo[d] = costTo[current] + r.price;
						edgeTo[d] = current;
					}
				}

				// Find the vertex with minimim path distance
				// This can be done more effiently using a priority queue!
				double min = INFINITY;
				current = -1;

				for (int i = 0; i < costTo.length; i++) {
					if (marked[i])
						continue;

					if (costTo[i] < min) {
						min = costTo[i];
						current = i;
					}
				}

				if (current == -1)
					break;

				marked[current] = true;
				nMarked++;
			}
		}

		public Set<Route> kruskals() {
			marked = new boolean[this.v]; // marked array of vertices.
			bestEdge = new int[this.v]; // array of best edges for vertices.

			Set<Route> MST = new HashSet<Route>(); // Set holding MST(s).
			UF UF = new UF(this.v); // create a union-find data structure.

			edgeTo = new int[this.v]; // parent array of vertices.
			for (int i = 0; i < this.v; i++) {
				bestEdge[i] = INFINITY;
				marked[i] = false;
			}

			int nMarked = 0; // Fix: initialize nMarked to 0
			int current = 0;
			edgeTo[current] = current;
			marked[current] = true;
			nMarked++;

			Queue<Route> pq = new PriorityQueue<Route>(); // create priority queue.

			while (nMarked < this.v) { // Fix: update loop condition
				// add all edges of the current vertex that have not yet been added
				for (Route r : adj(current)) {
					int d = cities.indexOf(r.destination);
					if (!marked[d]) {
						pq.add(r); // add the edge to the priority queue if not
					}
				}
				marked[current] = true;
				nMarked++;

				if (pq.isEmpty()) {
					// If all edges are processed and priority queue is empty, break out of the loop
					break;
				}

				// Fix: Update current vertex to the next unmarked vertex
				do {
					current++;
					if (current == this.v) {
						current = 0;
					}
				} while (marked[current]);

				// Fix: If all vertices are marked, break out of the loop
				if (nMarked == this.v) {
					break;
				}
			}

			// unify all vertices to a component
			// starting with vertices connecting the min edges.
			while (UF.count() > 1) {
				if (pq.isEmpty()) {
					// return MST if all edges are processed
					return MST;
				}

				Route minEdge = pq.poll(); // retrieve min edge.
				int p = cities.indexOf(minEdge.source); // get index of source.
				int q = cities.indexOf(minEdge.destination); // get index of destination.

				if (!UF.isConnected(p, q)) // if the two components are not connected:
				{
					MST.add(minEdge); // add that edge to MST
					UF.unify(p, q); // unify both components into one.
				}
			}

			return MST;
		}
	}

	/*
	 * private inner class to represent union-find data structure.
	 */
	class UF {
		private int count;
		private int[] id;
		private int[] size;

		public UF(int n) {
			if (n <= 0)
				throw new IllegalArgumentException("Component size must be greater than zero.");

			count = n;
			id = new int[n];
			size = new int[n];

			// intialize each vertex to have itself as its ID.
			for (int i = 0; i < id.length; i++) {
				id[i] = i;
				size[i] = 1; // each tree has initial size of 1.
			}
		}

		// unify vertices p and q into one component.
		public void unify(int p, int q) {
			// return if two vertices are already coonnected
			// to the same component.

			int pID = find(p), qID = find(q);
			if (pID == qID)
				return;

			if (size[pID] < size[qID]) // check if first component is smaller.
			{
				id[pID] = qID; // update ID of smaller component.
				size[qID] += size[pID]; // add the size of both components.
			} else // check if first component is larger and update.
			{
				id[qID] = pID;
				size[pID] += size[qID];
			}

			count--; // decrement the count after merging two components.
		}

		// find the component of vertex p.
		// returns the ID of p.
		public int find(int p) {
			if (p == id[p]) // locate the root of the component.
				return p;

			// copress path leading back to root
			// using "path compression" to achieve amortized constant time
			return id[p] = find(id[p]);
		}

		// check if two vertices are connected to the same component.
		public boolean isConnected(int p, int q) {
			return find(p) == find(q);
		}

		// return the size of the component that 'p' belongs to.
		public int componentSize(int p) {
			return size[find(p)];
		}

		// return the number of components.
		public int count() {
			return count;
		}
	}
}
