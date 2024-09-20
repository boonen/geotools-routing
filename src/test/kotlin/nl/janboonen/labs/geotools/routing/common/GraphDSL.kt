package nl.janboonen.labs.geotools.routing.common

import nl.janboonen.labs.geotools.routing.adapter.`in`.graph.RouteSegmentFeatureCollection
import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.graph.build.basic.BasicGraphBuilder
import org.geotools.graph.structure.Graph
import org.geotools.graph.structure.basic.BasicEdge
import org.geotools.graph.structure.basic.BasicNode
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point

class GraphDSL {
    private val builder = BasicGraphBuilder()
    private val nodes = mutableMapOf<Point, BasicNode>()

    fun node(feature: Point): BasicNode {
        return nodes.computeIfAbsent(feature) { BasicNode().apply { setObject(feature) } }
    }

    fun edge(feature: SimpleFeature) {
        val geometry = feature.defaultGeometry as? LineString
            ?: throw IllegalArgumentException("Feature must have a LineString geometry.")
        val startNode = node(geometry.startPoint)
        val endNode = node(geometry.endPoint)
        val edge = BasicEdge(startNode, endNode)
        builder.addEdge(edge)
        builder.addNode(startNode)
        builder.addNode(endNode)
        edge.setObject(feature)
    }

    fun fromFeatureCollection(featureCollection: RouteSegmentFeatureCollection) {
        featureCollection.forEach { edge(it) }
    }

    fun build(): Graph {
        return builder.graph
    }
}

fun graph(init: GraphDSL.() -> Unit): Graph {
    val dsl = GraphDSL()
    dsl.init()
    return dsl.build()
}
