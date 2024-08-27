package nl.janboonen.labs.geotools.routing.common

import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.api.feature.simple.SimpleFeatureType
import org.geotools.data.DataUtilities
import org.geotools.feature.FeatureCollection
import org.geotools.feature.FeatureIterator
import org.geotools.feature.simple.SimpleFeatureBuilder
import org.geotools.geometry.jts.JTSFactoryFinder
import org.geotools.graph.structure.Graph
import org.geotools.graph.structure.Node
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Point
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("nl.janboonen.labs.geotools.routing.common.GeoToolsExtensions")

val featureBuilder: SimpleFeatureBuilder = SimpleFeatureBuilder(
    DataUtilities.createType(
        "segments",
        // srid is -1 to skip loading the EPSG database
        "geom:LineString:srid=-1,id:String,name:String,class:String"
    )
)
val geometryFactory = JTSFactoryFinder.getGeometryFactory()

/**
 * Alias for FeatureCollection<SimpleFeatureType, SimpleFeature>
 */
typealias SimpleFeatureCollection = FeatureCollection<SimpleFeatureType, SimpleFeature>

/**
 * Helper function to iterate over a FeatureCollection using `forEach{}`
 */
fun FeatureCollection<SimpleFeatureType, SimpleFeature>.forEach(action: (SimpleFeature) -> Unit) {
    val featureIterator: FeatureIterator<SimpleFeature> = this.features()
    featureIterator.use {
        while (featureIterator.hasNext()) {
            val feature = featureIterator.next()
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

fun <T> SimpleFeature.getAttribute(name: String, type: Class<T>): T {
    val attribute = this.getAttribute(name)
    if (attribute != null) {
        try {
            return type.cast(attribute)
        } catch (e: ClassCastException) {
            logger.warn("Attribute $name is not of type $type")
        }
    }
    return type.cast(null)
}

fun simpleNetwork(): FeatureCollection<SimpleFeatureType, SimpleFeature> {
    val feature1 = featureBuilder.buildFeature("1")
    feature1.defaultGeometry = geometryFactory.createLineString(
        arrayOf(Coordinate(0.0, 0.0), Coordinate(1.0, 1.0))
    )
    feature1.setAttribute("id", "1")
    feature1.setAttribute("name", "segment 1")
    feature1.setAttribute("class", "1")
    val feature2 = featureBuilder.buildFeature("2")
    feature2.defaultGeometry = geometryFactory.createLineString(
        arrayOf(Coordinate(1.0, 1.0), Coordinate(1.0, 3.0))
    )
    feature2.setAttribute("id", "2")
    feature2.setAttribute("name", "segment 2")
    feature2.setAttribute("class", "1")
    val feature3 = featureBuilder.buildFeature("3")
    feature3.defaultGeometry = geometryFactory.createLineString(
        arrayOf(Coordinate(1.0, 1.0), Coordinate(2.0, 4.0))
    )
    feature3.setAttribute("id", "3")
    feature3.setAttribute("name", "segment 3")
    feature3.setAttribute("class", "2")
    return DataUtilities.collection(listOf(feature1, feature2, feature3))
}
