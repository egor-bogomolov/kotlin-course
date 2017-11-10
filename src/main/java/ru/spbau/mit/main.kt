package ru.spbau.mit

import java.util.*

fun read(): Graph {
    val reader = Scanner(System.`in`)
    return Graph(reader.nextInt()).apply {
        for (i in 0 until size) {
            addEdge(reader.nextInt(), reader.nextInt())
        }
    }
}

fun markCycle(v: Int, graph: Graph, from: Int? = null): Int? {
    graph.addToVisited(v)
    graph.getEdgesFrom(v)
            .filter { it != from }
            .forEach {
                if (!graph.isVisited(it)) {
                    val stop = markCycle(it, graph, v)
                    if (stop != null) {
                        graph.addToCycle(v)
                        return if (stop == v) null else stop
                    }
                } else {
                    graph.addToCycle(v)
                    return it
                }
            }
    return null
}

fun calcDist(v: Int, graph: Graph, from: Int? = null, distance: Int = 0) {
    graph.setDistanceTo(v, distance)
    graph.getEdgesFrom(v)
            .filter { it != from && !graph.isInCycle(it) }
            .forEach { calcDist(it, graph, v, distance + 1) }
}

fun solve(graph: Graph): MutableList<Int> {
    markCycle(0, graph)
    (0 until graph.size)
            .filter { graph.isInCycle(it) }
            .forEach { calcDist(it, graph) }
    return graph.getDistances()
}

fun main(args: Array<String>) {
    println(solve(read()).joinToString(separator = " "))
}

class Graph(val size: Int) {
    private val edges = MutableList(size) { mutableListOf<Int>() }
    private val cycle = MutableList(size) { false }
    private val visited = MutableList(size) { false }
    private val distances = MutableList(size) { 0 }

    fun addEdge(from: Int, to: Int) {
        edges[from - 1].add(to - 1)
        edges[to - 1].add(from - 1)
    }

    fun addToCycle(v: Int) {
        cycle[v] = true
    }

    fun addToVisited(v: Int) {
        visited[v] = true
    }

    fun isInCycle(v: Int) = cycle[v]

    fun isVisited(v: Int?) = v != null && visited[v]

    fun getEdgesFrom(v: Int) = edges[v]

    fun setDistanceTo(v: Int, distance: Int) {
        distances[v] = distance
    }

    fun getDistances() = distances

}
