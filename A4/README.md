# CS 1501 – Algorithm Implementation – Assignment #4

_(Assignment adapted from Dr. John Ramirez’s CS 1501 class.)_

Due: Monday April 18th @ 11:59pm on Gradescope

Late submission deadline: Wednesday April 20th @11:59pm with 10% penalty per late day

## Overview

Purpose: The purpose of this assignment is to make you practice implementing some graph
algorithms and to see how they can be used in a somewhat practical way.

**Feel free to use as much code as you need from Lab 8, Lab 9, and Assignment 4.**

## Details

You are to implement a simple information program for a fictional airline.

.	Your program should be able to handle the following queries as specified in `AirlineInterface.java`:

1. Load from a file (whose name is input by the user) a list of all of the service routes that your airline runs. These routes include the cities served and the various non-stop destinations from each city. Clearly, you will be interpreting these routes as a graph with the cities being the vertices and the non-stop trips being the edges. To keep things simple, assume that all routes are bidirectional, so that you can use an undirected graph (this is not necessarily an incorrect assumption, as airlines most often fly non-stop routes in both directions). Alternatively, you could use a directed graph, with two edges (one for each direction) for each trip. You can think of these as the current active routes, which would be updated periodically in case a route is cancelled or perhaps snow closes an airport somewhere. The routes (edges) should have 2 different weights: one weight based on the **distance** in miles between the cities and the other based on the **price** of a ticket between the cities. There are two example input files in the repository: `a5data1.txt` and `a5data2.txt`. Your program may be tested on other data files.

  ```java
  /**
   * reads the city names and the routes from a file
   * @param fileName the String file name
   * @return true if routes loaded successfully and false otherwise
   */
  public boolean loadRoutes(String fileName);
  ```
2. Save the routes back to a file with the same format that they were read in but containing the possibly modified route information.

  ```java
  /**
  * writes the city names and the routes into a file
  * @param fileName the String file name
  * @return true if routes saved successfully and false otherwise
  */
  public boolean saveRoutes(String fileName);
  ```

3. Return a set of all city names served by the Airline.
```java
/**
 * returns the set of city names in the Airline system
 * @return a (possibly empty) Set<String> of city names
 */
public Set<String> retrieveCityNames();
```

4. Return a set of all non-stop `Route`s out of a given city. Please check `Route.java` for the specification of the `Route` objects.

  ```java
  /**
   * returns the set of direct routes out of a given city
   * @param city the String city name
   * @return a (possibly empty) Set<Route> of Route objects representing the direct routes out
   * of city
   * @throws CityNotFoundException if the city is not found in the Airline
   * system
   */
  public Set<Route> retrieveDirectRoutesFrom(String city) throws CityNotFoundException;
  ```

5.	Allow for each of the three "shortest path" searches below. If multiple paths "tie" for the shortest, you should return any one of them.

    a.	Shortest path based on price from the source to the destination. This option is a bit naïve, since prices are not necessarily additive for hops on a multi-city flight. However, to keep the algorithm fairly simple, you should assume the prices ARE additive. Since distance and price do NOT always correspond, this could be useful to passengers who want to save money.

    ```java
    /**
    * finds cheapest path(s) between two cities
    * @param source the String source city name
    * @param destination the String destination city name
    * @return a (possibly empty) Set<ArrayList<Route>> of cheapest
    * paths. Each path is an ArrayList<Route> of Route objects that includes a
    * Route out of the source and a Route into the destination.
    * @throws CityNotFoundException if any of the two cities are not found in the
    * Airline system
    */
    public Set<ArrayList<Route>> cheapestItinerary(String source, String destination) throws CityNotFoundException;
    ```

    b. Cheapest path from a source city to a destination city through a third (transit) city. In other words, "What is the cheapest path from A to B given that I
    want to stop at C for a while?" Again, to keep the algorithm fairly simple, you should assume the prices ARE additive.
    ```java
    /**
    * finds cheapest path(s) between two cities going through a third city
    * @param source the String source city name
    * @param transit the String transit city name
    * @param destination the String destination city name
    * @return a (possibly empty) Set<ArrayList<Route>> of cheapest
    * paths. Each path is an ArrayList<Route> of city names that includes
    * a Route out of source, into and out of transit, and into destination.
    * @throws CityNotFoundException if any of the three cities are not found in
    * the Airline system
    */
    public Set<ArrayList<Route>> cheapestItinerary(String source, String transit, String destination) throws CityNotFoundException;
    ```
6. Compute a minimum spanning tree for the service routes based on distances. This could be useful for maintenance routes and/or shipping supplies from a central supply location to all of the airports. If the route graph is not connected, this query should identify and show each of the connected subtrees of the graph.

  ```java
  /**
   * finds one Minimum Spanning Tree (MST) for each connected component of
   * the graph
   * @return a (possibly empty) Set<Set<Route>> of MSTs. Each MST is a Set<Route>
   * of Route objects representing the MST edges.
   */
  public Set<Set<Route>> getMSTs();
  ```

7. Given a dollar amount entered by the user, return all trips which start at a given city and whose cost is less than or equal to that amount. In this case, a trip can contain an arbitrary number of hops (but it should not repeat any cities – i.e., it cannot contain a cycle). This feature would be useful for the airline to print out localized weekly "super saver" fare advertisements or to help travelers who are flexible in their destinations but not flexible in their overall costs. Be careful to implement this option as efficiently as possible, since it has the possibility of having an exponential run-time (especially for long paths). Consider a recursive / backtracking / pruning approach.

  ```java
  /**
   * finds all itineraries starting out of a source city and within a given
   * price
   * @param city the String city name
   * @param budget the double budget amount in dollars
   * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
   * less than or equal to the budget. Each path is an ArrayList<Route> of Route
   * objects starting with a Route object out of the source city.
   */
  public Set<ArrayList<Route>> tripsWithin(String city, double budget)
    throws CityNotFoundException;
  ```

8. Given a dollar amount entered by the user, return all trips whose cost is less than or equal to that amount. In this case, a trip can contain an arbitrary number of hops (but it should not repeat any cities – i.e., it cannot contain a cycle). This feature would be useful for the airline to print out weekly "super saver" fare advertisements or to help travelers who are flexible in their destinations but not flexible in their overall costs. Be careful to implement this option as efficiently as possible, since it has the possibility of having an exponential run-time (especially for long paths). Consider a recursive / backtracking / pruning approach.
    ```java
    /**
     * finds all itineraries within a given price regardless of the
     * starting city
     * @param  budget the double budget amount in dollars
     * @return a (possibly empty) Set<ArrayList<Route>> of paths with a total cost
     * less than or equal to the budget. Each path is an ArrayList<Route> of Route
     * objects.
     */
    public Set<ArrayList<Route>> tripsWithin(double budget);
    ```
9. Remove a route from the schedule. This may affect the searches and algorithms indicated above.

  ```java
  /**
   * delete a given non-stop route from the Airline's schedule. Both directions
   * of the route have to be deleted.
   * @param  source the String source city name
   * @param  destination the String destination city name
   * @return true if the route is deleted successfully and false if no route
   * existed between the two cities
   * @throws CityNotFoundException if any of the two cities are not found in the
   * Airline system
   */
  public boolean deleteRoute(String source, String destination)
    throws CityNotFoundException;
  ```

10. Allow vertices to be removed from your graph (handling corresponding edges
correctly).
  ```java
  /**
   * delete a given city and all non-stop routes out of and into the city from
   * the Airline schedule.
   * @param  city  the String city name
   * @throws CityNotFoundException if the city is not found in the Airline system
   */
  public void deleteCity(String city) throws CityNotFoundException;
  ```

.	You must encapsulate the functionality of your airline in a **single, cohesive class** named `AirlineSystem.java`, which has to `implements` the `AirlineInterface`. You must represent the graph using **adjacency lists**. The cities should minimally have a string for a name and any other information you want to add. The edges will have multiple weights (distance, price). **Again, you may use the code from Lab 8, Lab 9, and Assignment 4**.

.	You must use the algorithms and implementations discussed in class for your queries. For example, to obtain the MST you must use either **Prim’s or Kruskal’s algorithm**, for the shortest distance paths you must use **Dijkstra’s algorithm**, and to obtain the shortest-hops path you must use **breadth-first search**.

. The test program `AirlineTest.java` has a menu-driven loop that asks the user for many choices. Please use this program to test your code.

.	Below is an example input file, visual graph, and response to some of the queries listed above. The index numbers for the vertices are based on the order that the cities appear in the file (note that the indexing starts at 1). Please also check `sample-output.txt` for more examples.

![](docs/a5-1.png)
![](docs/a5-2.png)

## Extra Credit (10 points)

For each of the three "shortest path" searches listed above, if multiple paths "tie" for the shortest, you should return **all** of them.

## Submission Requirements

You must submit to Gradescope at least the following file:
1.	`AirlineSystem.java`

The idea from your submission is that the autograder can compile and run your programs from the command line WITHOUT ANY additional files or changes, so be sure to test it thoroughly before submitting it. If the autograder cannot compile or run your submitted code it will be graded as if the program does not work.

Note: If you use an IDE such as NetBeans, Eclipse, or IntelliJ, to develop your programs, make sure they will compile and run on the command-line before submitting – this may require some modifications to your program (such as removing some package information).

## Rubrics

Item|Points
----|------|
(**Mandatory**) `loadRoutes`|	8
`saveRoutes`|	10
(**Mandatory**) `retrieveCityNames`|	8
(**Mandatory**) `retrieveDirectRoutesFrom`|	8
`cheapestItinerary`|	10
`cheapestItinerary` with transit city |	8
`getMSTs`|	10
`tripsWithin` with a city and a budget|	10
`tripsWithin` with budget only|	8
`deleteRoute`|	10
`deleteCity`|	10
Extra Credit|	10 points
# Airline-System-App
