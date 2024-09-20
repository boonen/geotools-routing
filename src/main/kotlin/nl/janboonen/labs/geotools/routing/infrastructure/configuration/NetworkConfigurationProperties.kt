package nl.janboonen.labs.geotools.routing.infrastructure.configuration

import nl.janboonen.labs.geotools.routing.application.domain.model.RouteSegmentClass
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "network")
class NetworkConfigurationProperties {
    lateinit var segmentClasses: List<RouteSegmentClass>
}
