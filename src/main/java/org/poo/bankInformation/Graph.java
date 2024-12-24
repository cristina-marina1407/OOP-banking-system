package org.poo.bankInformation;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    /* list of list to represent the graph, where each list represents a currency */
    private final List<List<Node>> graph;

    public Graph(final List<Exchange> exchanges) {
        graph = new ArrayList<>();

        /* adds the exchange rates to the graph in both directions */
        for (Exchange exchange : exchanges) {
            addExchange(exchange.getFrom(), exchange.getTo(), exchange.getRate());
            addExchange(exchange.getTo(), exchange.getFrom(), 1.0 / exchange.getRate());
        }
    }

    /**
     *  Adds an exchange rate to the graph
     *  */
    private void addExchange(final String from, final String to, final double rate) {
        int fromIndex = findOrAddCurrency(from);
        int toIndex = findOrAddCurrency(to);

        graph.get(fromIndex).add(new Node(to, rate));
        graph.get(toIndex).add(new Node(from, 1.0 / rate));
    }

    /**
     *  Finds the index of a currency in the graph or adds it if it doesn't exist
     */
    private int findOrAddCurrency(final String currency) {
        for (int i = 0; i < graph.size(); i++) {
            if (graph.get(i).get(0).getTo().equals(currency)) {
                return i;
            }
        }

        graph.add(new ArrayList<>());
        graph.get(graph.size() - 1).add(new Node(currency, 1.0));
        return graph.size() - 1;
    }

    /**
     *  Converts an amount from one currency to another
     */
    public double convert(final String from, final String to, final double amount) {
        /* if the currencies are the same, return the amount */
        if (from.equals(to)) {
            return amount;
        }

        int fromIndex = findIndex(from);
        int toIndex = findIndex(to);

        /* array to keep track of visited nodes */
        boolean[] visited = new boolean[graph.size()];
        /* perform dfs to find the conversion rate */
        return dfs(fromIndex, toIndex, amount, visited);
    }

    /**
     *  Depth first search to find the conversion rate
     */
    private double dfs(final int current, final int target, final double currentRate,
                       final boolean[] visited) {
        if (current == target) {
            return currentRate;
        }

        visited[current] = true;

        for (Node neighbor : graph.get(current)) {
            int neighborIndex = findIndex(neighbor.getTo());
            if (neighborIndex != -1 && !visited[neighborIndex]) {
                double newRate = dfs(neighborIndex, target,
                        currentRate * neighbor.getRate(), visited);
                if (newRate != -1.0) {
                    return newRate;
                }
            }
        }
        return -1.0;
    }

    /**
     *  Finds the index of a currency in the graph
     */
    private int findIndex(final String currency) {
        for (int i = 0; i < graph.size(); i++) {
            if (graph.get(i).get(0).getTo().equals(currency)) {
                return i;
            }
        }
        return -1;
    }
}
