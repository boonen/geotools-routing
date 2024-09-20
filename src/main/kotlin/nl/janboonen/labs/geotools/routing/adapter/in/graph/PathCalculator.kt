package nl.janboonen.labs.geotools.routing.adapter.`in`.graph

import nl.janboonen.labs.geotools.routing.application.domain.service.NetworkConfigurationService
import org.geotools.graph.path.DijkstraShortestPathFinder
import org.geotools.graph.path.Path
import org.geotools.graph.structure.Edge
import org.geotools.graph.structure.Graph
import org.geotools.graph.structure.Node
import org.geotools.graph.traverse.standard.DijkstraIterator
import java.time.Duration
import java.time.Instant

class PathCalculator(val networkConfigurationService: NetworkConfigurationService) {

    // Placeholder function to calculate the dynamic cost of an edge
    private fun calculateDynamicCost(edge: Edge, arrivalTime: Instant): Duration {
        val averageDuration = Duration.ofMinutes(15)
        val feature = edge.`object` as RouteSegmentFeature
        val routeSegmentClass = networkConfigurationService.getRouteSegmentClassById(feature.segmentClass)
        return routeSegmentClass?.maximumSpeed?.let {
            val speed = feature.historicalSpeed ?: it
            val distanceInKm = feature.geometry.length * 1000
            val travelTimeInSeconds = distanceInKm / speed
            return Duration.ofSeconds(travelTimeInSeconds.toLong())
        } ?: averageDuration
    }

    fun computeShortestPath(
        graph: Graph,
        startNode: Node,
        endNode: Node,
        departureTime: Instant
    ): Pair<Path, Map<Node, Instant>> {
        // Initialize arrivalTimes with the departureTime of the journey
        val arrivalTimes = graph.nodes.associateWith { departureTime }.toMutableMap()
        val dijkstraIterator = DijkstraIterator.EdgeWeighter { edge: Edge ->
            val arrivalTime = arrivalTimes[edge.nodeA]!!
            val edgePassageDuration = calculateDynamicCost(edge, arrivalTime)
            arrivalTimes[edge.nodeB] = arrivalTime.plus(edgePassageDuration)
            edgePassageDuration.toMillis().toDouble()
        }
        val pathFinder = DijkstraShortestPathFinder(graph, startNode, dijkstraIterator)
        pathFinder.calculate()
        return Pair(pathFinder.getPath(endNode), arrivalTimes)
    }

}