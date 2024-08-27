package nl.janboonen.labs.geotools.routing.adapters.`in`.geopackage

import nl.janboonen.labs.geotools.routing.common.SimpleFeatureCollection
import org.geotools.api.data.DataStore
import org.geotools.api.data.DataStoreFinder
import java.io.IOException
import java.nio.file.Path

class GeopackageReader(val filename: Path) {

    fun readFeatures(featureTypeName: String): SimpleFeatureCollection {
        var dataStore: DataStore? = null
        try {
            dataStore = DataStoreFinder.getDataStore(
                mapOf(
                    "dbtype" to "geopkg",
                    "database" to filename.toAbsolutePath().toString(),
                    "read-only" to true
                )
            )

            val typeNames = dataStore?.typeNames
            if (typeNames == null || !typeNames.contains(featureTypeName)) {
                dataStore?.dispose()
                throw IllegalArgumentException("FeatureType '$featureTypeName' not found in GeoPackage ${filename.toAbsolutePath()}.")
            }

            // Access a specific layer by name
            val featureSource = dataStore.getFeatureSource(featureTypeName)
            return featureSource.features
        } catch (e: IOException) {
            dataStore?.dispose()
            throw IllegalArgumentException("Error reading GeoPackage ${filename.toAbsolutePath()}.", e)
        }
    }

}