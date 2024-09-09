package nl.janboonen.labs.geotools.routing.common

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import org.locationtech.jts.linearref.LengthIndexedLine
import org.locationtech.jts.operation.distance.DistanceOp

// Extension function to find the closest node in the LineString to the provided Coordinate
fun LineString.closestCoordinateTo(coordinate: Point): Coordinate? {
    // Use DistanceOp to compute the closest points between D and the LineString AB
    val distanceOp = DistanceOp(this, coordinate)

    // Get the closest point on LineString AB to Point D
    return distanceOp.nearestPoints()[0]
}

fun LineString.splitAtCoordinate(coordinate: Coordinate): Pair<LineString, LineString> {    // Create a LengthIndexedLine to split the LineString at point D
    val indexedLine = LengthIndexedLine(this)
    val indexSplitPoint = indexedLine.indexOf(coordinate)

    val firstLineString = indexedLine.extractLine(0.0, indexSplitPoint) as LineString
    val secondLineString = indexedLine.extractLine(indexSplitPoint, indexedLine.endIndex) as LineString

    return Pair(firstLineString, secondLineString)
}