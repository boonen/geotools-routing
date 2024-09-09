package nl.janboonen.labs.geotools.routing.domain

import nl.janboonen.labs.geotools.routing.common.toTimeslot
import java.math.BigInteger
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime

class Network {
}

class RegulatedPassagePoint(private val id: String, private val facilitySchedule: FacilitySchedule) {

    fun isWithinSchedule(timeWindow: TimeWindow): Boolean {
        val day = timeWindow.start.dayOfWeek
        val startSlot = timeWindow.start.toTimeslot()
        val endSlot = timeWindow.start.plus(timeWindow.duration).toTimeslot()
        val bitmask = facilitySchedule.daySchedule[day] ?: return false

        for (i in startSlot until endSlot) {
            if (!bitmask.testBit(i)) {
                return false
            }
        }
        return true
    }

}

data class TimeWindow(val start: LocalDateTime, val duration: Duration)

data class FacilitySchedule(
    val daySchedule: Map<DayOfWeek, BigInteger>
    // Can be expanded with closure schedules
)

data class RouteSectionClass(
    val id: String, val name: String,
    val allowedWidth: Double? = null,
    val allowedHeight: Double? = null,
    val allowedDraught: Double? = null,
    val maximumSpeed: Double? = null
)
