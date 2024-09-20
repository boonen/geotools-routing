package nl.janboonen.labs.geotools.routing.common

import nl.janboonen.labs.geotools.routing.adapter.`in`.graph.RouteSegmentFeature
import nl.janboonen.labs.geotools.routing.adapter.`in`.graph.RouteSegmentFeatureCollection
import nl.janboonen.labs.geotools.routing.adapter.`in`.graph.createRouteSegmentFeature
import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.feature.DefaultFeatureCollection
import org.geotools.feature.simple.SimpleFeatureBuilder
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point

class EdgeFeatureDSL {
    lateinit var geometry: LineString
    lateinit var id: String
    lateinit var segmentClass: String
    var code: String? = null
    var historicalSpeed: Double? = null

    fun build(): RouteSegmentFeature {
        return createRouteSegmentFeature(id, geometry, segmentClass, code ?: id, "Edge ${id}", historicalSpeed)
    }
}

class LocationFeatureDSL {
    private var featureBuilder: SimpleFeatureBuilder = locationFeatureBuilder()
    lateinit var geometry: Geometry
    lateinit var id: String

    fun build(): SimpleFeature {
        val feature = featureBuilder.buildFeature(id).apply {
            defaultGeometry = geometry
            attributes.let { attributes ->
                attributes[0] = id
            }
        }
        return feature
    }
}

class FeatureCollectionDSL {
    private val features = mutableListOf<SimpleFeature>()

    fun fromFeatures(vararg features: SimpleFeature) {
        this.features.addAll(features)
    }

    fun build(): RouteSegmentFeatureCollection {
        return RouteSegmentFeatureCollection(DefaultFeatureCollection().apply {
            features.forEach { add(it) }
        })
    }
}

fun edgeFeature(init: EdgeFeatureDSL.() -> Unit): SimpleFeature {
    val dsl = EdgeFeatureDSL()
    dsl.init()
    return dsl.build()
}

fun featureCollection(init: FeatureCollectionDSL.() -> Unit): RouteSegmentFeatureCollection {
    val dsl = FeatureCollectionDSL()
    dsl.init()
    return dsl.build()
}

fun locationFeature(init: LocationFeatureDSL.() -> Unit): SimpleFeature {
    val dsl = LocationFeatureDSL()
    dsl.init()
    return dsl.build()
}

fun lineString(vararg coordinates: Coordinate): LineString {
    return geometryFactory.createLineString(coordinates)
}

fun point(coordinate: Coordinate): Geometry {
    return geometryFactory.createPoint(coordinate)
}

fun locationFeatureBuilder(): SimpleFeatureBuilder {
    val featureTypeBuilder = SimpleFeatureTypeBuilder()
    featureTypeBuilder.name = "Location Nodes"
    featureTypeBuilder.defaultGeometry = "geometry"
    featureTypeBuilder.add("geometry", Point::class.java)
    featureTypeBuilder.add("id", String::class.java)
    return SimpleFeatureBuilder(featureTypeBuilder.buildFeatureType())
}