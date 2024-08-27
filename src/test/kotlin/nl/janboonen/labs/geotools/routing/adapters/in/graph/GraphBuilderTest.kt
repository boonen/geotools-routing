package nl.janboonen.labs.geotools.routing.adapters.`in`.graph

import nl.janboonen.labs.geotools.routing.common.simpleNetwork
import org.assertj.core.api.Assertions.assertThat
import org.geotools.api.feature.simple.SimpleFeature
import org.geotools.feature.DefaultFeatureCollection
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GraphBuilderTest {

    val graphBuilder = GraphBuilder()

    @Test
    fun buildGraph_returnsNonNullGraph_whenValidInput() {
        val graph = graphBuilder.createGraphFromFeatures(simpleNetwork())

        assertThat(graph).isNotNull
        assertThat(graph.edges.size).isEqualTo(3)
        assertThat(graph.nodes.size).isEqualTo(4)
        assertThat(graph.edges.stream().findFirst().get().`object`).isInstanceOf(SimpleFeature::class.java)
    }

    @Test
    fun buildGraph_throwsIllegalArgumentException_whenFeatureTypeIsEmpty() {
        val exception = assertThrows<IllegalArgumentException> {
            graphBuilder.createGraphFromFeatures(DefaultFeatureCollection())
        }

        assertThat(exception.message).contains("Feature Collection cannot be empty.")
    }

}