package nl.janboonen.labs.geotools.routing.adapters.`in`.geopackage

import nl.janboonen.labs.geotools.routing.adapters.`in`.geopackage.GeopackageReader
import org.assertj.core.api.Assertions.assertThat
import org.geotools.api.data.DataStore
import org.geotools.api.data.DataStoreFinder
import org.geotools.api.data.SimpleFeatureSource
import org.geotools.data.simple.SimpleFeatureCollection
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.MockedStatic
import org.mockito.Mockito.*
import java.io.IOException
import java.nio.file.Path

class GeopackageReaderTest {

    private val filename = mock(Path::class.java)
    private lateinit var dataStoreFinder: MockedStatic<DataStoreFinder>
    private val dataStore = mock(DataStore::class.java)

    @BeforeEach
    fun before() {
        reset(filename, dataStore)
        dataStoreFinder = mockStatic(DataStoreFinder::class.java)
        `when`(filename.toAbsolutePath()).thenReturn(Path.of("~/test.gpkg"))
        `when`(DataStoreFinder.getDataStore(any<Map<String, Object>>())).thenReturn(dataStore)
    }

    @AfterEach
    fun after() {
        dataStoreFinder.close()
    }

    @Test
    fun readFeatures_returnsFeatureCollection_whenFeatureTypeExists() {
        val featureCollection = mock(SimpleFeatureCollection::class.java)
        val featureSource = mock(SimpleFeatureSource::class.java)

        `when`(dataStore.typeNames).thenReturn(arrayOf("existingFeatureType"))
        `when`(dataStore.getFeatureSource("existingFeatureType")).thenReturn(featureSource)
        `when`(featureSource.features).thenReturn(featureCollection)

        val reader = GeopackageReader(filename)
        val result = reader.readFeatures("existingFeatureType")

        assertThat(result).isEqualTo(featureCollection)
    }

    @Test
    fun readFeatures_throwsIllegalArgumentException_whenFeatureTypeDoesNotExist() {
        `when`(dataStore.typeNames).thenReturn(arrayOf("existingFeatureType"))

        val reader = GeopackageReader(filename)

        val exception = assertThrows<IllegalArgumentException> {
            reader.readFeatures("nonExistingFeatureType")
        }

        assertThat(exception.message).isEqualTo("FeatureType 'nonExistingFeatureType' not found in GeoPackage ~/test.gpkg.")
        verify(dataStore).dispose()
    }

    @Test
    fun readFeatures_throwsIllegalArgumentException_whenDataStoreIsNull() {
        `when`(DataStoreFinder.getDataStore(any<Map<String, Object>>())).thenReturn(null)

        val reader = GeopackageReader(filename)

        val exception = assertThrows<IllegalArgumentException> {
            reader.readFeatures("existingFeatureType")
        }

        assertThat(exception.message).contains("FeatureType 'existingFeatureType' not found in GeoPackage ~/test.gpkg.")
    }

    @Test
    fun readFeatures_throwsIllegalArgumentException_whenIOExceptionOccurs() {
        `when`(dataStore.typeNames).thenThrow(IOException::class.java)

        val reader = GeopackageReader(filename)

        val exception = assertThrows<IllegalArgumentException> {
            reader.readFeatures("existingFeatureType")
        }

        assertThat(exception.message).contains("Error reading GeoPackage ~/test.gpkg.")
        verify(dataStore).dispose()
    }
}