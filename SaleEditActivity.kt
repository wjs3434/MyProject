package com.example.firebasetest

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SaleEditActivity : AppCompatActivity() {

    private val db = Firebase.firestore
    private val itemsCollectionRef = db.collection("sales")

    private lateinit var saleId: String
    private lateinit var originalPrice: String
    private var originalIsSold: Boolean = false

    private val editPrice by lazy { findViewById<EditText>(R.id.editPrice) }
    private val toggleIsSold by lazy { findViewById<ToggleButton>(R.id.toggleIsSold) }
    private val buttonUpdatePrice by lazy { findViewById<Button>(R.id.buttonUpdatePrice) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_edit)

        saleId = intent.getStringExtra("saleId") ?: ""
        originalPrice = intent.getStringExtra("price") ?: ""
        originalIsSold = intent.getBooleanExtra("isSold", false)

        editPrice.setText(originalPrice)
        toggleIsSold.isChecked = originalIsSold

        buttonUpdatePrice.setOnClickListener {
            updatePrice()
        }
    }

    private fun updatePrice() {
        val newPrice = editPrice.text.toString().toInt()
        val newIsSold = toggleIsSold.isChecked

        itemsCollectionRef.document(saleId)
            .update(mapOf(
                "price" to newPrice,
                "isSold" to newIsSold
            ))
            .addOnSuccessListener {
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener {
                setResult(RESULT_CANCELED)
                finish()
            }
    }
}
