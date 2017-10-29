package ru.spbau.mit

import org.junit.Before
import kotlin.test.assertEquals
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TestSource {

    private val size = 7
    private val correctDistances = listOf(1, 2, 0, 0, 3, 1, 1)
    private val correctVisited = listOf(false, true, true, true, false, false, true)
    private val correctCycle = listOf(false, false, true, true, false, true, true)
    private var graph: Graph = Graph(size)

    @Before
    fun setup() {
        graph = Graph(size)
        graph.addEdge(1, 2)
        graph.addEdge(2, 3)
        graph.addEdge(3, 4)
        graph.addEdge(4, 6)
        graph.addEdge(6, 7)
        graph.addEdge(3, 7)
        graph.addEdge(7, 5)
    }

    @Test
    fun testGraphDistances() {
        for (v in 0 until size) {
            graph.setDistanceTo(v, correctDistances[v])
        }
        assertEquals(correctDistances, graph.getDistances())
    }

    @Test
    fun testGraphVisited() {
        for (v in 0 until size) {
            assertFalse { graph.isVisited(v) }
            if (correctVisited[v]) {
                graph.addToVisited(v)
                assertTrue { graph.isVisited(v) }
            }
        }
    }

    @Test
    fun testGraphCycle() {
        for (v in 0 until size) {
            assertFalse { graph.isInCycle(v) }
            if (correctCycle[v]) {
                graph.addToCycle(v)
                assertTrue { graph.isInCycle(v) }
            }
        }
    }

    @Test
    fun testGraphEdges() {
        assertEquals(listOf(1), graph.getEdgesFrom(0))
        assertEquals(listOf(0, 2), graph.getEdgesFrom(1))
        assertEquals(listOf(1, 3, 6), graph.getEdgesFrom(2))
        assertEquals(listOf(2, 5), graph.getEdgesFrom(3))
        assertEquals(listOf(6), graph.getEdgesFrom(4))
        assertEquals(listOf(3, 6), graph.getEdgesFrom(5))
        assertEquals(listOf(5, 2, 4), graph.getEdgesFrom(6))
    }

    @Test
    fun testMarkCycle() {
        markCycle(0, graph)
        for (v in 0 until size) {
            println(v)
            assertEquals(correctCycle[v], graph.isInCycle(v))
        }
    }

    @Test
    fun testCalcDist() {
        markCycle(0, graph)
        calcDist(2, graph)
        assertEquals(2, graph.getDistances()[0])
        assertEquals(1, graph.getDistances()[1])
        assertEquals(0, graph.getDistances()[2])
        calcDist(6, graph)
        assertEquals(1, graph.getDistances()[4])
        assertEquals(0, graph.getDistances()[6])
    }
}
