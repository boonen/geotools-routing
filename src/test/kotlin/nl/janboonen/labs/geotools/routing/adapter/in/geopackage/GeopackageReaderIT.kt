package nl.janboonen.labs.geotools.routing.adapter.`in`.geopackage

import nl.janboonen.labs.geotools.routing.common.SimpleFeatureCollection
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import java.nio.file.Paths

class GeopackageReaderIT {

    @Test
    fun readFeatures_returnsFeatureCollection_whenReadingFromTestFile() {
        val filename = Paths.get(ClassPathResource("test_network.gpkg").uri)
        val reader = GeopackageReader(filename)
        val featureTypeName = "segments"

        val result: SimpleFeatureCollection = reader.readFeatures(featureTypeName)

        assertThat(result).isNotNull
        assertThat(result.size()).isEqualTo(7)
    }
}