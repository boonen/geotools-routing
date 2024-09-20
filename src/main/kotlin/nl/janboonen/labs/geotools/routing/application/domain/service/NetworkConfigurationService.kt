package nl.janboonen.labs.geotools.routing.application.domain.service

import nl.janboonen.labs.geotools.routing.application.domain.model.RouteSegmentClass
import nl.janboonen.labs.geotools.routing.infrastructure.configuration.NetworkConfigurationProperties
import org.springframework.stereotype.Service

@Service
class NetworkConfigurationService(private val properties: NetworkConfigurationProperties) {

    fun getRouteSegmentClassById(id: String): RouteSegmentClass? {
        return properties.segmentClasses.find { it.id == id }
    }
}