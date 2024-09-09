package nl.janboonen.labs.geotools.routing.common

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Coordinate

class JtsExtensionsKtTest {

    @Test
    fun `When C is outside LineString AB then split it at the closest point on the line`() {
        // Given
        val lineString = geometryFactory.createLineString(arrayOf(Coordinate(0.0, 0.0), Coordinate(2.0, 2.0),  Coordinate(4.0, 3.0)))
        val point = geometryFactory.createPoint(Coordinate(.5, 1.75))

        // When
        val closestCoordinate = lineString.closestCoordinateTo(point)
        val (firstLineString, secondLineString) = lineString.splitAtCoordinate(closestCoordinate!!)

        // Then
        assertEquals(0.0, firstLineString.startPoint.x)
        assertEquals(0.0, firstLineString.startPoint.y)
        assertEquals(1.125, firstLineString.endPoint.x)
        assertEquals(1.125, firstLineString.endPoint.y)

        assertEquals(1.125, secondLineString.startPoint.x)
        assertEquals(1.125, secondLineString.startPoint.y)
        assertEquals(4.0, secondLineString.endPoint.x)
        assertEquals(3.0, secondLineString.endPoint.y)
    }

}