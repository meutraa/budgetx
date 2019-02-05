package host.lost.budgetx

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import org.jetbrains.anko.setContentView

class MainActivity : AppCompatActivity() {

    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(null)

        db = FirebaseFirestore.getInstance()

        val ui = MainActivityUI(db)
        ui.setContentView(this)
    }
}
