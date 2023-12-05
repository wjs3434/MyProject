package com.example.firebasetest


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SaleDetailsActivity : AppCompatActivity() {

    private val firestore: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_details)

        val titleTextView = findViewById<TextView>(R.id.titleTextView)
        val priceTextView = findViewById<TextView>(R.id.priceTextView)
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        val sellerTextView = findViewById<TextView>(R.id.sellerTextView)
        val messageButton = findViewById<Button>(R.id.messageButton)

        // MainActivity에서 전달한 데이터 받기
        val saleId = intent.getStringExtra("saleId")
        val title = intent.getStringExtra("title")
        val price = intent.getStringExtra("price")
        val content = intent.getStringExtra("content")
        val sellerID = intent.getStringExtra("sellerID")
        val sellerEmail = intent.getStringExtra("sellerEmail")

        // 받은 데이터를 화면에 표시
        titleTextView.text = title
        priceTextView.text = "가격: $price"
        descriptionTextView.text = "상세내역: $content"
        sellerTextView.text = "판매자ID: $sellerEmail"

        // 현재 로그인한 사용자의 UID 가져오기
        val currentUserEmail = auth.currentUser?.email

        // 현재 로그인한 사용자와 판매글의 판매자가 다를 경우 메시지 버튼 활성화
        if (currentUserEmail != sellerEmail) {
            messageButton.setOnClickListener {
                // 판매글의 정보를 ChatActivity로 전달
                val intent = Intent(this, MessengerActivity::class.java)
                intent.putExtra("sellerEmail", sellerEmail)
                intent.putExtra("currentUserEmail", currentUserEmail)
                intent.putExtra("saleTitle", title)
                startActivity(intent)
            }
        } else {
            // 판매글의 판매자와 현재 로그인한 사용자가 같을 경우 메시지 버튼 비활성화
            messageButton.isEnabled = false
        }
    }
}
