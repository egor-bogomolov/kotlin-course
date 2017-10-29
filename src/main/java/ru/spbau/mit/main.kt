package ru.spbau.mit

import java.util.*

fun read(): Graph {
    val reader = Scanner(System.`in`)
    val graph = Graph(reader.nextInt())
    for (i in 0 until graph.size) {
        graph.addEdge(reader.nextInt(), reader.nextInt())
    }
    return graph
}

fun markCycle(v: Int, graph: Graph, from: Int? = null): Int? {
    graph.addToVisited(v)
    for (to in graph.getEdgesFrom(v)) {
        if (to != from && !graph.isVisited(to)) {
            val stop = markCycle(to, graph, v)
            if (stop != null) {
                graph.addToCycle(v)
                return if (stop == v) null else stop
            }
        } else if (to != from) {
            graph.addToCycle(v)
            return to
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
    private val edges = mutableListOf<MutableList<Int>>()
    private val cycle = mutableListOf<Boolean>()
    private val visited = mutableListOf<Boolean>()
    private val distances = mutableListOf<Int>()

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

    init {
        for (i in 0 until size) {
            edges.add(mutableListOf())
            cycle.add(false)
            visited.add(false)
            distances.add(0)
        }
    }
}
