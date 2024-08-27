package nl.janboonen.labs.geotools.routing.common

import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.feature.simple.SimpleFeatureBuilder
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.geotools.graph.build.basic.BasicGraphBuilder
import org.geotools.graph.structure.Graph
import org.geotools.graph.structure.basic.BasicEdge
import org.geotools.graph.structure.basic.BasicNode
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point

class GraphDSL {
    private val builder = BasicGraphBuilder()
    private val nodes = mutableMapOf<Point, BasicNode>()
    private val featureBuilder: SimpleFeatureBuilder = featureBuilder()

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

    fun feature(lineString: LineString, id: String, name: String? = null, defaultTravelTime: Int? = 0): SimpleFeature {
        return featureBuilder.buildFeature(id).apply {
            defaultGeometry = lineString
            attributes.let { attributes ->
                attributes[0] = id
                attributes[1] = name
                attributes[2] = defaultTravelTime
            }
        }
    }

    fun lineString(vararg coordinates: Coordinate): LineString {
        return geometryFactory.createLineString(coordinates)
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

fun featureBuilder(): SimpleFeatureBuilder {
    val featureTypeBuilder = SimpleFeatureTypeBuilder()
    featureTypeBuilder.name = "Network Nodes"
    featureTypeBuilder.defaultGeometry = "geometry"
    featureTypeBuilder.add("geometry", LineString::class.java)
    featureTypeBuilder.add("id", String::class.java)
    featureTypeBuilder.add("name", String::class.java)
    featureTypeBuilder.add("defaultTravelTime", Integer::class.java)
    return SimpleFeatureBuilder(featureTypeBuilder.buildFeatureType())
}