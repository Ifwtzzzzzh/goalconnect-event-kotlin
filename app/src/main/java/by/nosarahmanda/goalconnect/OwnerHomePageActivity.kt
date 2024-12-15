package by.nosarahmanda.goalconnect

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.nosarahmanda.goalconnect.Adapter.EventAdapter
import by.nosarahmanda.goalconnect.Adapter.EventWithoutLatestAdapter
import by.nosarahmanda.goalconnect.Adapter.NewEventAdapter
import by.nosarahmanda.goalconnect.Model.EventModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class OwnerHomePageActivity : AppCompatActivity() {
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var newEventsRecyclerView: RecyclerView
    private lateinit var eventWithoutLatestRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var newEventAdapter: NewEventAdapter
    private lateinit var eventWithoutLatestAdapter: EventWithoutLatestAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var addEventButton: Button
    private var events = mutableListOf<EventModel>()
    private val newEvent = mutableListOf<EventModel>()
    private var allEventWithoutLatest = mutableListOf<EventModel>()
    private var selectedDate = ""
    private var createdAt = getCurrentFormattedDate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_owner_home_page)

        eventsRecyclerView = findViewById(R.id.eventsRecyclerView)
        eventWithoutLatestRecyclerView = findViewById(R.id.eventsWithoutLatestRecyclerView)
        newEventsRecyclerView = findViewById(R.id.newEventsRecyclerView)
        addEventButton = findViewById(R.id.addEventButton)

        firestore = FirebaseFirestore.getInstance()

        eventAdapter = EventAdapter(events, this::showEditDialog, this::deleteEvent)
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventAdapter

        eventWithoutLatestAdapter = EventWithoutLatestAdapter(allEventWithoutLatest, this::showEditDialog, this::deleteEvent)
        eventWithoutLatestRecyclerView.layoutManager = LinearLayoutManager(this)
        eventWithoutLatestRecyclerView.adapter = eventWithoutLatestAdapter

        newEventAdapter = NewEventAdapter(newEvent, this::showEditDialog, this::deleteEvent)
        newEventsRecyclerView.layoutManager = LinearLayoutManager(this)
        newEventsRecyclerView.adapter = newEventAdapter

        addEventButton.setOnClickListener { showAddDialog() }
        refreshRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadEvents() {
        firestore.collection("events")
            .get()
            .addOnSuccessListener { querySnapshot ->
                events.clear()
                for (doc in querySnapshot.documents) {
                    val event = doc.toObject(EventModel::class.java)
                    if (event != null) {
                        event.id = doc.id
                        events.add(event)
                    }
                }

                events.sortByDescending { it.createdAt }
                eventAdapter.notifyDataSetChanged()
                newEventAdapter.notifyDataSetChanged()
                eventWithoutLatestAdapter.notifyDataSetChanged()

                if (events.isNotEmpty()) {
                    val latestEvent = events.first()
                    eventAdapter.updateLatestEvent(latestEvent)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.widget_dialog_add_event, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val dateButton: Button = dialogView.findViewById(R.id.dateButton)
        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                    dateButton.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        val saveButton: Button = dialogView.findViewById(R.id.saveEventButton)
        saveButton.setOnClickListener {
            val title = dialogView.findViewById<EditText>(R.id.titleEditText).text.toString()
            val category = dialogView.findViewById<EditText>(R.id.categoryEditText).text.toString()
            val place = dialogView.findViewById<EditText>(R.id.placeEditText).text.toString()
            val description = dialogView.findViewById<EditText>(R.id.descriptionEditText).text.toString()

            val newEvent = hashMapOf(
                "titleEvent" to title,
                "categoryEvent" to category,
                "dateEvent" to selectedDate,
                "placeEvent" to place,
                "descriptionEvent" to description,
                "createdAt" to createdAt,
            )
            refreshRecyclerView()
            firestore.collection("events").add(newEvent).addOnSuccessListener {
                refreshRecyclerView()
                dialog.dismiss()
            }
            dialog.dismiss()
            refreshRecyclerView()
            Toast.makeText(this, "Event Berhasil Ditambah!", Toast.LENGTH_LONG).show()
        }
        dialog.show()
    }

    @SuppressLint("CutPasteId")
    private fun showEditDialog(event: EventModel) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.widget_dialog_add_event, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<EditText>(R.id.titleEditText).setText(event.titleEvent)
        dialogView.findViewById<EditText>(R.id.categoryEditText).setText(event.categoryEvent)
        dialogView.findViewById<EditText>(R.id.placeEditText).setText(event.placeEvent)
        dialogView.findViewById<EditText>(R.id.descriptionEditText).setText(event.descriptionEvent)

        val dateButton: Button = dialogView.findViewById(R.id.dateButton)
        dateButton.text = event.dateEvent
        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                    dateButton.text = selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        val saveButton: Button = dialogView.findViewById(R.id.saveEventButton)
        saveButton.setOnClickListener {
            val updatedEvent = mapOf(
                "titleEvent" to dialogView.findViewById<EditText>(R.id.titleEditText).text.toString(),
                "categoryEvent" to dialogView.findViewById<EditText>(R.id.categoryEditText).text.toString(),
                "dateEvent" to dialogView.findViewById<Button>(R.id.dateButton).text.toString(),
                "placeEvent" to dialogView.findViewById<EditText>(R.id.placeEditText).text.toString(),
                "descriptionEvent" to dialogView.findViewById<EditText>(R.id.descriptionEditText).text.toString()
            )
            refreshRecyclerView()
            firestore.collection("events").document(event.id).update(updatedEvent).addOnSuccessListener {
                refreshRecyclerView()
                dialog.dismiss()
            }
            dialog.dismiss()
            refreshRecyclerView()
            Toast.makeText(this, "Event Berhasil Diedit!", Toast.LENGTH_LONG).show()
        }
        dialog.show()
    }

    private fun deleteEvent(eventId: String) {
        firestore.collection("events").document(eventId).delete().addOnSuccessListener {
            refreshRecyclerView()
        }
        refreshRecyclerView()
        Toast.makeText(this, "Event Berhasil Dihapus!", Toast.LENGTH_SHORT).show()
    }

    private fun refreshRecyclerView() {
        loadEventsWithoutNewest()
        loadEvents()
        loadNewestEvent()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadNewestEvent() {
        firestore.collection("events")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    val event = querySnapshot.documents[0].toObject(EventModel::class.java)
                    if (event != null) {
                        newEvent.clear()
                        newEvent.add(event)
                        newEventAdapter.notifyDataSetChanged()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load the newest event", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadEventsWithoutNewest() {
        firestore.collection("events")
            .get()
            .addOnSuccessListener { querySnapshot ->
                allEventWithoutLatest.clear()
                val allEvents = querySnapshot.documents.mapNotNull { doc ->
                    val event = doc.toObject(EventModel::class.java)
                    event?.apply { id = doc.id }
                }

                val sortedEvents = allEvents.sortedByDescending { it.createdAt }
                if (sortedEvents.isNotEmpty()) {
                    allEventWithoutLatest.addAll(sortedEvents.drop(1))
                }

                eventAdapter.notifyDataSetChanged()
                eventWithoutLatestAdapter.notifyDataSetChanged()
                newEventAdapter.notifyDataSetChanged()

                if (sortedEvents.isNotEmpty()) {
                    val latestEvent = sortedEvents.first()
                    eventWithoutLatestAdapter.updateLatestEvent(latestEvent)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load events: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun getCurrentFormattedDate(): String {
        val sdf = SimpleDateFormat("HH:mm:ss, dd:MM:yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
}