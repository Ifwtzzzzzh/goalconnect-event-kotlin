package by.nosarahmanda.goalconnect.Model
import java.text.SimpleDateFormat
import java.util.*


data class EventModel(
    var id: String = "",
    var titleEvent: String = "",
    var categoryEvent: String = "",
    var dateEvent: String = "",
    var placeEvent: String = "",
    var descriptionEvent: String = "",
    var createdAt: String = ""
) {
//    init {
//        // Set the current timestamp when the object is created
//        createdAt = getCurrentFormattedDate()
//    }
//
//    private fun getCurrentFormattedDate(): String {
//        val sdf = SimpleDateFormat("HH:mm:ss, dd:MM:yyyy", Locale.getDefault())
//        return sdf.format(Date())
//    }
}

