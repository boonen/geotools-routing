package nl.janboonen.labs.geotools.routing.common

import nl.janboonen.labs.geotools.routing.adapter.`in`.graph.RouteSegmentFeatureCollection
import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.feature.DefaultFeatureCollection
import org.geotools.feature.simple.SimpleFeatureBuilder
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point

class EdgeFeatureDSL {
    private var featureBuilder: SimpleFeatureBuilder = edgeFeatureBuilder()
    lateinit var geometry: Geometry
    lateinit var id: String
    var name: String = "defaultName"
    var defaultTravelTime: Int? = null

    fun build(): SimpleFeature {
        val feature = featureBuilder.buildFeature(id).apply {
            defaultGeometry = geometry
            attributes.let { attributes ->
                attributes[0] = id
                attributes[1] = name
                attributes[2] = defaultTravelTime
            }
        }
        return feature
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

fun edgeFeatureBuilder(): SimpleFeatureBuilder {
    val featureTypeBuilder = SimpleFeatureTypeBuilder()
    featureTypeBuilder.name = "Network Nodes"
    featureTypeBuilder.defaultGeometry = "geometry"
    featureTypeBuilder.add("geometry", LineString::class.java)
    featureTypeBuilder.add("id", String::class.java)
    featureTypeBuilder.add("name", String::class.java)
    featureTypeBuilder.add("defaultTravelTime", Integer::class.java)
    return SimpleFeatureBuilder(featureTypeBuilder.buildFeatureType())
}

fun locationFeatureBuilder(): SimpleFeatureBuilder {
    val featureTypeBuilder = SimpleFeatureTypeBuilder()
    featureTypeBuilder.name = "Location Nodes"
    featureTypeBuilder.defaultGeometry = "geometry"
    featureTypeBuilder.add("geometry", Point::class.java)
    featureTypeBuilder.add("id", String::class.java)
    return SimpleFeatureBuilder(featureTypeBuilder.buildFeatureType())
}