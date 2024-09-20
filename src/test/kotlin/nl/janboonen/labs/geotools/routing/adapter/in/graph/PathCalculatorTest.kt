package nl.janboonen.labs.geotools.routing.adapter.`in`.graph

import nl.janboonen.labs.geotools.routing.application.domain.model.RouteSegmentClass
import nl.janboonen.labs.geotools.routing.application.domain.service.NetworkConfigurationService
import nl.janboonen.labs.geotools.routing.common.*
import nl.janboonen.labs.geotools.routing.infrastructure.configuration.NetworkConfigurationProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.locationtech.jts.geom.Coordinate
import java.time.Instant
import java.time.temporal.ChronoUnit

class PathCalculatorTest {

    private val networkConfigurationProperties = NetworkConfigurationProperties()
    private val pathCalculator: PathCalculator
    private val fixedInstant = Instant.ofEpochMilli(0)

    init {
        networkConfigurationProperties.segmentClasses =
            listOf(
                RouteSegmentClass("C", "Class A", maximumSpeed = 30.0),
                RouteSegmentClass("D", "Class B ", maximumSpeed = 30.0)
            )
        pathCalculator = PathCalculator(NetworkConfigurationService(networkConfigurationProperties))
    }

    @Test
    fun `find shortest path with fixed cost (duration)`() {
        val graph = graph {
            fromFeatureCollection(featureCollection {
                fromFeatures(
                    edgeFeature {
                        geometry = lineString(Coordinate(0.0, 0.0), Coordinate(1.0, 1.0))
                        id = "1"
                        segmentClass = "A"
                    },
                    edgeFeature {
                        geometry = lineString(Coordinate(1.0, 1.0), Coordinate(2.0, 2.0))
                        id = "2"
                        segmentClass = "B"
                    })
            })
        }

        assertThat(graph).isNotNull
        val endNode = graph.getNode(Coordinate(2.0, 2.0))
        val result = pathCalculator.computeShortestPath(
            graph,
            graph.getNode(Coordinate(0.0, 0.0)),
            endNode,
            fixedInstant
        )
        assertNotNull(result)
        assertThat(result.second[endNode]).isEqualTo(fixedInstant.plus(30, ChronoUnit.MINUTES))
    }

}