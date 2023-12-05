package com.example.firebasetest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasetest.FirestoreActivity.Companion.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth

        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val editName = findViewById<EditText>(R.id.editName)
        val editBirthday = findViewById<EditText>(R.id.editBirthday)

        findViewById<Button>(R.id.buttonSignup)?.setOnClickListener {
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()
            val name = editName.text.toString()
            val birthday = editBirthday.text.toString()

            // Firebase에 계정 생성
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 계정 생성이 성공하면 추가 정보 저장 등의 로직을 수행할 수 있음
                        // 예를 들어, 사용자의 이름과 생년월일을 저장
                        saveAdditionalUserInfo(name, birthday)

                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Account creation failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun saveAdditionalUserInfo(name: String, birthday: String) {
        // 현재 로그인된 사용자 가져오기
        val user = Firebase.auth.currentUser

        // 사용자 프로필 업데이트 요청 생성
        val profileUpdates = userProfileChangeRequest {
            displayName = name // 사용자 이름 설정
        }

        // 사용자 프로필 업데이트 요청 적용
        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 프로필 업데이트 성공 시 추가 정보 저장
                    val userAdditionalInfo = hashMapOf(
                        "birthday" to birthday
                    )

                    // Firebase Firestore에 추가 정보 저장
                    Firebase.firestore.collection("users")
                        .document(user.uid)
                        .set(userAdditionalInfo)
                        .addOnSuccessListener {
                            // 추가 정보 저장 성공
                            Log.d(TAG, "Additional user info saved successfully.")
                        }
                        .addOnFailureListener { e ->
                            // 추가 정보 저장 실패
                            Log.w(TAG, "Error adding additional user info", e)
                        }
                } else {
                    // 프로필 업데이트 실패
                    Log.w(TAG, "Error updating profile", task.exception)
                }
            }
    }
}