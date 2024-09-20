package nl.janboonen.labs.geotools.routing.adapter.`in`.graph

import nl.janboonen.labs.geotools.routing.common.*
import org.geotools.graph.build.GraphBuilder
import org.geotools.graph.build.basic.BasicGraphBuilder
import org.geotools.graph.build.feature.FeatureGraphGenerator
import org.geotools.graph.build.line.LineStringGraphGenerator
import org.geotools.graph.structure.Edge
import org.geotools.graph.structure.Graph
import org.geotools.graph.structure.basic.BasicEdge
import org.geotools.graph.structure.basic.BasicNode
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import java.util.UUID


class GraphBuilder(
    featureCollection: RouteSegmentFeatureCollection,
    private val graphBuilder: GraphBuilder = BasicGraphBuilder()
) {

    private val referenceGraph: Graph

    init {
        referenceGraph = createGraphFromFeatures(featureCollection)
    }

    private fun createGraphFromFeatures(featureCollection: RouteSegmentFeatureCollection): Graph {
        val graphGenerator = FeatureGraphGenerator(LineStringGraphGenerator())

        require(!featureCollection.isEmpty) { "Feature Collection cannot be empty." }
        featureCollection.forEach(graphGenerator::add)
        return graphGenerator.graph
    }

    fun getGraph(): Graph {
        graphBuilder.importGraph(referenceGraph)
        return graphBuilder.graph
    }

    /**
     * Adds virtual nodes by splitting the edges which lie closest to the given locations.
     */
    fun addVirtualNodes(locations: RouteSegmentFeatureCollection): Pair<Graph, List<Edge>> {
        val insertedEdges = mutableListOf<Edge>()
        var sourceGraph = referenceGraph
        locations.forEach {
            val point: Point = it.defaultGeometry as Point

            // Find the nearest edge (line segment) in the graph to the point
            val nearestEdge = findNearestEdge(point)
            if (nearestEdge != null) {
                // Add the point as a new node in the graph and connect it to the nearest edge
                val lineString = nearestEdge.getGeometry()
                val newLineStrings = lineString?.splitAtCoordinate(point.coordinate)
                if (newLineStrings != null) {
                    sourceGraph = replaceEdgeWithNewSegments(sourceGraph, nearestEdge, point, newLineStrings)
                    insertedEdges.add(nearestEdge)
                }
            }
        }

        return sourceGraph to insertedEdges
    }

    private fun findNearestEdge(point: Point): Edge? {
        var minDistance = Double.MAX_VALUE
        var nearestEdge: Edge? = null

        // Iterate through the edges (lines) of the graph to find the closest one to the point
        for (edge in referenceGraph.edges) {
            val lineString = edge.getGeometry()
            val distance = lineString?.closestCoordinateTo(point)?.distance(point.coordinate)
            if (distance != null && distance < minDistance) {
                minDistance = distance
                nearestEdge = edge
            }
        }

        return nearestEdge
    }

    private fun replaceEdgeWithNewSegments(
        sourceGraph: Graph,
        edgeToSplit: Edge,
        nodeToInsert: Point,
        segmentsToInsert: Pair<LineString, LineString>
    ): Graph {
        val nodeA = edgeToSplit.getNodeA()
        val nodeB = edgeToSplit.getNodeB()
        val originalFeature = edgeToSplit.`object` as RouteSegmentFeature

        graphBuilder.importGraph(sourceGraph)
        graphBuilder.removeEdge(edgeToSplit)

        val newNode = BasicNode().apply { `object` = nodeToInsert }
        val originalName = originalFeature.name
        val originalClass = originalFeature.segmentClass

        val edgeFeatureA = createRouteSegmentFeature(
            id = "${edgeToSplit.id}_1",
            geometry = segmentsToInsert.first,
            segmentClass = originalClass,
            code = UUID.randomUUID().toString(),
            name = "$originalName 1"
        )
        val edgeFeatureB = createRouteSegmentFeature(
            id = "${edgeToSplit.id}_2",
            geometry = segmentsToInsert.second,
            segmentClass = originalClass,
            code = UUID.randomUUID().toString(),
            name = "$originalName 2"
        )
        val newEdgeA = BasicEdge(nodeA, newNode).apply { `object` = edgeFeatureA }
        val newEdgeB = BasicEdge(newNode, nodeB).apply { `object` = edgeFeatureB }
        newNode.add(newEdgeA)
        newNode.add(newEdgeB)

        graphBuilder.addEdge(newEdgeA)
        graphBuilder.addEdge(newEdgeB)
        graphBuilder.addNode(newNode)

        return graphBuilder.graph
    }

}
