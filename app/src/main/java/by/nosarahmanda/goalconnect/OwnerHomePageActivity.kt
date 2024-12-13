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
import by.nosarahmanda.goalconnect.Model.EventModel
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class OwnerHomePageActivity : AppCompatActivity() {
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private lateinit var firestore: FirebaseFirestore
    private lateinit var addEventButton: Button
    private val events = mutableListOf<EventModel>()
    private var selectedDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_owner_home_page)

        eventsRecyclerView = findViewById(R.id.eventsRecyclerView)
        addEventButton = findViewById(R.id.addEventButton)

        firestore = FirebaseFirestore.getInstance()

        eventAdapter = EventAdapter(events, this::showEditDialog, this::deleteEvent)
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventAdapter

        addEventButton.setOnClickListener { showAddDialog() }
        loadEvents()
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
                eventAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
            Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val dateButton: Button = dialogView.findViewById(R.id.dateButton)
        dateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Update the `selectedDate` and button text
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
            val place = dialogView.findViewById<EditText>(R.id.placeEditText).text.toString()
            val description = dialogView.findViewById<EditText>(R.id.descriptionEditText).text.toString()

            val newEvent = hashMapOf(
                "titleEvent" to title,
                "dateEvent" to selectedDate,
                "placeEvent" to place,
                "descriptionEvent" to description
            )

            firestore.collection("events").add(newEvent).addOnSuccessListener {
                loadEvents()
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
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<EditText>(R.id.titleEditText).setText(event.titleEvent)
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
                "dateEvent" to dialogView.findViewById<Button>(R.id.dateButton).text.toString(),
                "placeEvent" to dialogView.findViewById<EditText>(R.id.placeEditText).text.toString(),
                "descriptionEvent" to dialogView.findViewById<EditText>(R.id.descriptionEditText).text.toString()
            )

            firestore.collection("events").document(event.id).update(updatedEvent).addOnSuccessListener {
                loadEvents()
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
            loadEvents()
        }
        refreshRecyclerView()
        Toast.makeText(this, "Event Berhasil Dihapus!", Toast.LENGTH_LONG).show()
    }

    private fun refreshRecyclerView() {
        loadEvents()
    }
}