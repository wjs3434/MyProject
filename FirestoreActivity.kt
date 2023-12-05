package com.example.firebasetest

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreActivity : AppCompatActivity() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("sales")
    private val auth = FirebaseAuth.getInstance()

    private val checkAutoID by lazy { findViewById<CheckBox>(R.id.checkAutoID) }
    private val editID by lazy { findViewById<EditText>(R.id.editID) }
    private val editTitle by lazy { findViewById<EditText>(R.id.editTitle) }
    private val editContent by lazy { findViewById<EditText>(R.id.editContent) }
    private val editPrice by lazy { findViewById<EditText>(R.id.editPrice) }
    private val progressWait by lazy { findViewById<ProgressBar>(R.id.progressWait) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore)

        findViewById<Button>(R.id.buttonAddItem)?.setOnClickListener {
            addItem()
        }

        // 이 부분에서 필요한 UI 요소 초기화 및 이벤트 처리 등을 추가할 수 있어요.
    }

    private fun addItem() {
        val title = editTitle.text.toString()
        val content = editContent.text.toString()
        val price = editPrice.text.toString().toInt()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val sellerID = currentUser.uid
            val sellerEmail = currentUser.email // 현재 로그인한 사용자의 이메일 가져오기

            val salePost = hashMapOf(
                "title" to title,
                "content" to content,
                "price" to price,
                "sellerID" to sellerID,
                "sellerEmail" to sellerEmail,
                "isSold" to true // 초기에는 항상 팔린 상태로 설정

            )

            itemsCollectionRef.add(salePost)
                .addOnSuccessListener {
                    Snackbar.make(editTitle, "Sale post added successfully", Snackbar.LENGTH_SHORT).show()
                    finish() // 판매글 추가 후 현재 액티비티 종료
                }
                .addOnFailureListener {
                    Snackbar.make(editTitle, "Failed to add sale post", Snackbar.LENGTH_SHORT).show()
                }
        } else {
            Snackbar.make(editTitle, "User not logged in", Snackbar.LENGTH_SHORT).show()
        }
    }
    companion object {
        const val TAG = "FirestoreActivity"
    }
}
