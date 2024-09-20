package nl.janboonen.labs.geotools.routing.common

import nl.janboonen.labs.geotools.routing.adapter.`in`.graph.RouteSegmentFeature
import nl.janboonen.labs.geotools.routing.adapter.`in`.graph.RouteSegmentFeatureCollection
import nl.janboonen.labs.geotools.routing.adapter.`in`.graph.createRouteSegmentFeature
import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.data.DataUtilities
import org.geotools.feature.FeatureIterator
import org.geotools.geometry.jts.JTSFactoryFinder
import org.geotools.graph.structure.Edge
import org.geotools.graph.structure.Graph
import org.geotools.graph.structure.Node
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.LineString
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("nl.janboonen.labs.geotools.routing.common.GeoToolsExtensions")

val geometryFactory = JTSFactoryFinder.getGeometryFactory()

/**
 * Helper function to iterate over a RouteSegmentFeatureCollection using `forEach{}`
 */
fun RouteSegmentFeatureCollection.forEach(action: (RouteSegmentFeature) -> Unit) {
    val featureIterator: FeatureIterator<SimpleFeature> = this.features()
    featureIterator.use {
        while (featureIterator.hasNext()) {
            val feature = featureIterator.next() as RouteSegmentFeature
            action(feature)
        }
    }
}

fun Graph.getNode(coordinate: Coordinate): Node {
    val point = geometryFactory.createPoint(coordinate)
    return this.nodes.stream()
        .filter({ node -> node.getObject() == point })
        .findFirst()
        .orElse(this.nodes.first())
}

fun simpleNetwork(): RouteSegmentFeatureCollection {
    val feature1 = createRouteSegmentFeature(
        id = "1",
        geometry = geometryFactory.createLineString(
            arrayOf(Coordinate(0.0, 0.0), Coordinate(1.0, 1.0))
        ),
        code = "S1",
        name = "segment 1",
        segmentClass = "1"
    )
    val feature2 = createRouteSegmentFeature(
        id = "2",
        geometry = geometryFactory.createLineString(
            arrayOf(Coordinate(1.0, 1.0), Coordinate(1.0, 3.0))
        ),
        code = "S2",
        name = "segment 2",
        segmentClass = "1"
    )

    val feature3 = createRouteSegmentFeature(
        id = "3",
        geometry = geometryFactory.createLineString(
            arrayOf(Coordinate(1.0, 1.0), Coordinate(2.0, 4.0))
        ),
        code = "S3",
        name = "segment 3",
        segmentClass = "2"
    )
    return RouteSegmentFeatureCollection(DataUtilities.collection(listOf(feature1, feature2, feature3)))
}

/**
 * Gets the underlying Geometry from a Graph Edge (LineString).
 */
fun Edge.getGeometry(): LineString? {
    val feature = this.getObject() as? SimpleFeature
    return feature?.defaultGeometry as? LineString
}