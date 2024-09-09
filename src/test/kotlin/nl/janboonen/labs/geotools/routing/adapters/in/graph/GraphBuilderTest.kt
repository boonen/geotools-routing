package nl.janboonen.labs.geotools.routing.adapters.`in`.graph

import nl.janboonen.labs.geotools.routing.common.featureCollection
import nl.janboonen.labs.geotools.routing.common.locationFeature
import nl.janboonen.labs.geotools.routing.common.point
import nl.janboonen.labs.geotools.routing.common.simpleNetwork
import org.assertj.core.api.Assertions.assertThat
import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.feature.DefaultFeatureCollection
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.locationtech.jts.geom.Coordinate

class GraphBuilderTest {

    private val graphBuilder = GraphBuilder(simpleNetwork())

    @Test
    fun buildGraph_returnsNonNullGraph_whenValidInput() {
        val graph = graphBuilder.getGraph()

        assertThat(graph).isNotNull
        assertThat(graph.edges.size).isEqualTo(3)
        assertThat(graph.nodes.size).isEqualTo(4)
        assertThat(graph.edges.stream().findFirst().get().`object`).isInstanceOf(SimpleFeature::class.java)
    }

    @Test
    fun buildGraph_throwsIllegalArgumentException_whenFeatureTypeIsEmpty() {
        val exception = assertThrows<IllegalArgumentException> {
            GraphBuilder(DefaultFeatureCollection())
        }

        assertThat(exception.message).contains("Feature Collection cannot be empty.")
    }

    @Test
    fun plotLocationsOnGraph_whenValidInput() {
        val graph = graphBuilder.getGraph()
        val locations = featureCollection {
            fromFeatures(
                locationFeature {
                    id = "1"
                    geometry = point(Coordinate(0.5, 0.5))
                },
                locationFeature {
                    id = "2"
                    geometry = point(Coordinate(1.5, 1.5))
                })
        }
        val (newGraph, edges) = graphBuilder.addVirtualNodes(locations)

        assertThat(newGraph).isNotNull
        assertThat(edges.size).isEqualTo(2)
        assertThat(newGraph.edges.size).isEqualTo(graph.edges.size + 2)
        assertThat(newGraph.nodes.size).isEqualTo(graph.nodes.size + 2)
    }

    @Test
    fun addVirtualNodes_returnsOriginalGraph_whenNoLocationsProvided() {
        val graph = graphBuilder.getGraph()
        val locations = DefaultFeatureCollection()
        val (newGraph, edges) = graphBuilder.addVirtualNodes(locations)

        assertThat(newGraph.edges).isEqualTo(graph.edges)
        assertThat(newGraph.nodes).isEqualTo(graph.nodes)
        assertThat(edges).isEmpty()
    }

}