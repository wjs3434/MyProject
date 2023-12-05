package com.example.firebasetest

import android.Manifest
import android.widget.ProgressBar
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import android.widget.Toast



class MainActivity : AppCompatActivity() {
    private var adapter: MyAdapter? = null
    private val recyclerViewItems by lazy { findViewById<RecyclerView>(R.id.recyclerViewItems) }
    private val progressWait by lazy { findViewById<ProgressBar>(R.id.progressWait) }
    private val itemsCollectionRef = Firebase.firestore.collection("sales")

    companion object {
        const val SALE_EDIT_REQUEST_CODE = 123 // 임의의 숫자로 지정
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if (Firebase.auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // recyclerview setup
        recyclerViewItems.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter(this, emptyList())
        recyclerViewItems.adapter = adapter

        adapter?.setOnItemClickListener(object : MyAdapter.OnItemClickListener {
            override fun onItemClick(item: Item) {
                // 판매글의 sellerId와 현재 로그인한 사용자의 UID를 비교
                if (item.sellerID == Firebase.auth.currentUser?.uid) {
                    // 판매글의 sellerID와 현재 로그인한 사용자의 UID가 같으면 SaleEditActivity로 이동
                    val intent = Intent(this@MainActivity, SaleEditActivity::class.java)
                    intent.putExtra("saleId", item.id)
                    intent.putExtra("price", item.price.toString())
                    intent.putExtra("isSold", item.isSold)
                    startActivityForResult(intent, SALE_EDIT_REQUEST_CODE)
                } else {
                    // 다르면 판매 상세정보 보여주기
                    showSaleDetails(item)
                }
            }
        })


        updateList()  // List items on RecyclerView

        findViewById<Button>(R.id.buttonSignOut)?.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.buttonIsSold)?.setOnClickListener{
            checkSold()
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestSinglePermission(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8.0
            createNotificationChannel()
        }
    }

    private fun showSaleDetails(item: Item) {
        val detailIntent = Intent(this@MainActivity, SaleDetailsActivity::class.java).apply {
            putExtra("title", item.title)
            putExtra("price", item.price.toString())
            putExtra("content", item.content)
            putExtra("sellerID", item.sellerID)
            putExtra("sellerEmail", item.sellerEmail)
        }
        startActivity(detailIntent)
    }


    override fun onResume() {
        super.onResume()
        updateList()
    }

    private fun checkSold() {
        progressWait.visibility = View.VISIBLE
        itemsCollectionRef.whereEqualTo("isSold", false).get()
            .addOnSuccessListener {
                progressWait.visibility = View.GONE
                val items = arrayListOf<String>()
                for (doc in it) {
                    items.add("${doc["title"]} - ${doc["price"]}")
                }
                AlertDialog.Builder(this)
                    .setTitle("판매된 것들 !")
                    .setItems(items.toTypedArray(), { dialog, which ->  }).show()
            }
            .addOnFailureListener {
                progressWait.visibility = View.GONE
            }
    }

    private fun requestSinglePermission(permission: String) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED)
            return

        val requestPermLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it == false) { // permission is not granted!
                AlertDialog.Builder(this).apply {
                    setTitle("Warning")
                    setMessage("notification permission required!")
                }.show()
            }
        }

        if (shouldShowRequestPermissionRationale(permission)) {
            // you should explain the reason why this app needs the permission.
            AlertDialog.Builder(this).apply {
                setTitle("Reason")
                setMessage("notification permission required!")
                setPositiveButton("Allow") { _, _ -> requestPermLauncher.launch(permission) }
                setNegativeButton("Deny") { _, _ -> }
            }.show()
        } else {
            // should be called in onCreate()
            requestPermLauncher.launch(permission)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "firebase-messaging", "firebase-messaging channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.description = "This is firebase-messaging channel."
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    private fun updateList() {
        // Firestore 데이터 가져오기
        val db = Firebase.firestore
        val salesCollectionRef = db.collection("sales")

        salesCollectionRef.get().addOnSuccessListener { result ->
            val items = mutableListOf<Item>()
            for (document in result) {
                // Firestore에서 가져온 데이터를 Item 객체로 변환하여 리스트에 추가
                items.add(Item(
                    document.id,
                    document.getString("title") ?: "",
                    document.getString("content") ?: "",
                    document.getLong("price")?.toInt() ?: 0,
                    document.getBoolean("isSold") ?: false,
                    document.getString("sellerID") ?: "",
                    document.getString("sellerEmail") ?: "" // 새로 추가된 sellerEmail 속성
                ))
            }

            // Adapter 업데이트
            adapter?.updateList(items)
        }.addOnFailureListener { exception ->
            Log.w("MainActivity", "Error getting documents: ", exception)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addProduct -> startActivity(
                Intent(this, FirestoreActivity::class.java))
            R.id.messages -> startActivity(
                Intent(this, MessengerActivity::class.java))

        }
        return super.onOptionsItemSelected(item)
    }
}