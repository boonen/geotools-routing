package nl.janboonen.labs.geotools.routing.adapters.`in`.graph

import nl.janboonen.labs.geotools.routing.common.SimpleFeatureCollection
import nl.janboonen.labs.geotools.routing.common.forEach
import org.geotools.graph.build.feature.FeatureGraphGenerator
import org.geotools.graph.build.line.LineStringGraphGenerator
import org.geotools.graph.structure.Graph

class GraphBuilder {
    fun createGraphFromFeatures(featureCollection: SimpleFeatureCollection): Graph {
        val graphGenerator = FeatureGraphGenerator(LineStringGraphGenerator())

        if (featureCollection.isEmpty) {
            throw IllegalArgumentException("Feature Collection cannot be empty.")
        }
        featureCollection.forEach(graphGenerator::add)
        return graphGenerator.graph
    }
}

