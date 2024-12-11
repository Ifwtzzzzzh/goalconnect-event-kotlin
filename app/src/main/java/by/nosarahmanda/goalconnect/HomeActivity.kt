package by.nosarahmanda.goalconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.BottomNavigation
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var badgeDrawable: BadgeDrawable

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupGoogleSignIn()
        findViewById<Button>(R.id.btn_log_out).setOnClickListener {
            signOut()
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.navbar_home)
        badgeDrawable.setNumber(2)
        badgeDrawable.setVisible(true)
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // Use your Web Client ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        auth = FirebaseAuth.getInstance()
    }
    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Sign Out Successful", Toast.LENGTH_SHORT).show()
                goToSignInScreen()
            } else {
                Toast.makeText(this, "Sign Out Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun revokeAccess() {
        auth.signOut()
        googleSignInClient.revokeAccess().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Access Revoked", Toast.LENGTH_SHORT).show()
                goToSignInScreen() // Replace with your navigation logic
            } else {
                Toast.makeText(this, "Revoke Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun goToSignInScreen() {
        val intent = Intent(this, MainActivity::class.java) // Replace with your sign-in activity
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

}