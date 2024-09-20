package nl.janboonen.labs.geotools.routing.adapter.`in`.graph

import org.geotools.feature.DecoratingFeature
import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.api.feature.simple.SimpleFeatureType
import org.geotools.feature.FeatureCollection
import org.geotools.feature.FeatureIterator
import org.geotools.feature.collection.DecoratingFeatureCollection
import org.geotools.feature.simple.SimpleFeatureBuilder
import org.geotools.feature.simple.SimpleFeatureTypeBuilder
import org.locationtech.jts.geom.LineString

class RouteSegmentFeature(delegate: SimpleFeature) : DecoratingFeature(delegate) {

    companion object {
        const val GEOMETRY_ATTRIBUTE: String = "geometry"
        const val SEGMENT_CLASS_ATTRIBUTE = "segmentClass"
        const val CODE_ATTRIBUTE = "code"
        const val NAME_ATTRIBUTE = "name"
        const val HISTORICAL_SPEED_ATTRIBUTE = "historicalSpeed"
    }

    val geometry: LineString
        get() = delegate.getAttribute(GEOMETRY_ATTRIBUTE) as LineString

    val segmentClass: String
        get() = delegate.getAttribute(SEGMENT_CLASS_ATTRIBUTE) as String

    val code: String
        get() = delegate.getAttribute(CODE_ATTRIBUTE) as String

    val name: String
        get() = delegate.getAttribute(NAME_ATTRIBUTE) as String

    val historicalSpeed: Double?
        get() = delegate.getAttribute(HISTORICAL_SPEED_ATTRIBUTE) as? Double

}

class RouteSegmentFeatureCollection(
    delegate: FeatureCollection<SimpleFeatureType, SimpleFeature>
) : DecoratingFeatureCollection<SimpleFeatureType, SimpleFeature>(delegate) {

    override fun features(): FeatureIterator<SimpleFeature> {
        val original = super.features()
        return object : FeatureIterator<SimpleFeature> {
            override fun hasNext(): Boolean = original.hasNext()
            override fun next(): SimpleFeature = RouteSegmentFeature(original.next())
            override fun close() = original.close()
        }
    }
}

private fun createRouteSegmentFeatureType(): SimpleFeatureType {
    val builder = SimpleFeatureTypeBuilder()
    builder.name = "RouteSegment"
    builder.add(RouteSegmentFeature.GEOMETRY_ATTRIBUTE, LineString::class.java)
    builder.add(RouteSegmentFeature.SEGMENT_CLASS_ATTRIBUTE, String::class.java)
    builder.add(RouteSegmentFeature.CODE_ATTRIBUTE, String::class.java)
    builder.add(RouteSegmentFeature.NAME_ATTRIBUTE, String::class.java)
    builder.add(RouteSegmentFeature.HISTORICAL_SPEED_ATTRIBUTE, Double::class.java)

    return builder.buildFeatureType()
}

fun createRouteSegmentFeature(
    id: String,
    geometry: LineString,
    segmentClass: String,
    code: String,
    name: String,
    historicalSpeed: Double? = null
): RouteSegmentFeature {
    val featureBuilder = SimpleFeatureBuilder(createRouteSegmentFeatureType())
    featureBuilder.set("geometry", geometry)
    featureBuilder.set("segmentClass", segmentClass)
    featureBuilder.set("code", code)
    featureBuilder.set("name", name)
    featureBuilder.set("historicalSpeed", historicalSpeed)

    val simpleFeature = featureBuilder.buildFeature(id)
    return RouteSegmentFeature(simpleFeature)
}
