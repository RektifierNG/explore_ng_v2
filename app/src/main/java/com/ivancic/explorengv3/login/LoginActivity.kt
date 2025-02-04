package com.ivancic.explorengv3.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ivancic.explorengv3.activities.Menu
import com.ivancic.explorengv3.activities.Profil
import com.ivancic.explorengv3.R
import com.ivancic.explorengv3.activities.Mainou
import com.ivancic.explorengv3.databinding.ActivityLoginBinding
import com.ivancic.explorengv3.databinding.ActivityLoginNewUiBinding
import com.ivancic.explorengv3.models.User
import com.ivancic.explorengv3.models.User4Leaderboard


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginNewUiBinding
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
    private val database2: DatabaseReference =
        FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("UsersLeaderboard")

    var displayList = ArrayList<User4Leaderboard>()
    private var userId:String=""
    var poslovniKorisnik:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginNewUiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if(currentUser != null){

            userId=currentUser.uid
            val intent=Intent(this, Menu::class.java)
            intent.putExtra("user", currentUser)
            startActivity(intent)
        }

//napraviti da se poslovni korisnici registriraju kao pk = 1 - dodati u klasu i onda u bazu
        //neka imaju isti login ali pri register da se registriraju kao jedni ili drugi
        //promijeniti UI da bude ovisno o ovom parametru
        binding.CV.setOnClickListener{
            if (poslovniKorisnik==0) {
                poslovniKorisnik = 1

            }
            else {
                poslovniKorisnik = 0
            }
        }

        binding.btnLogin.setOnClickListener {
            if (binding.email.text.toString().isEmpty()) Toast.makeText(applicationContext, getString(R.string.unesi_ime), Toast.LENGTH_SHORT).show()
            else if (binding.password.text.toString().isEmpty()) Toast.makeText(applicationContext, getString(R.string.unesi_pass), Toast.LENGTH_SHORT).show()
            else if (binding.password.text.toString().length < 6) Toast.makeText(applicationContext, getString(R.string.pass_kratak), Toast.LENGTH_SHORT).show()
            else{
                val str = binding.email.text.toString().filter { !it.isWhitespace() }
                val email ="$str@gmail.com"
                val password=binding.password.text.toString()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(baseContext, getString(R.string.uspjesna_prijava),
                                Toast.LENGTH_SHORT).show()
                            val user = auth.currentUser
                            val intent=Intent(this, Menu::class.java)
                            intent.putExtra("user", user)
                            startActivity(intent)
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, getString(R.string.neuspjesna_prijava),
                                Toast.LENGTH_SHORT).show()
                        }
                    } }
        }


        binding.btnSignup.setOnClickListener {

            if (binding.email.text.toString().isEmpty()) Toast.makeText(applicationContext, getString(R.string.unesi_ime), Toast.LENGTH_SHORT).show()
            else if (binding.password.text.toString().isEmpty()) Toast.makeText(applicationContext, getString(R.string.unesi_pass), Toast.LENGTH_SHORT).show()
            else if (binding.password.text.toString().length < 6) Toast.makeText(applicationContext, getString(R.string.pass_kratak), Toast.LENGTH_SHORT).show()
            else{
                database2.addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        try{
                            val a : List<User4Leaderboard> = snapshot.children.map{ dataSnapshot -> dataSnapshot.getValue(
                                User4Leaderboard::class.java)!! }

                            displayList.addAll(a)

                        }catch (_: Exception){
                        }}

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })


                val str = binding.email.text.toString().filter { !it.isWhitespace() }
                val email ="$str@gmail.com"
                val password=binding.password.text.toString()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(baseContext, getString(R.string.uspjesna_registracija),
                                Toast.LENGTH_SHORT).show()
                            val user = auth.currentUser
                            userId = user!!.uid
                            val currentUserDb = database.child(userId)


                            val name=binding.email.text.toString()
                            val a=ArrayList<Int>()
                            a.add(0)
                            val b=ArrayList<String>()
                            b.add("")

                            val user4Leaderboard = User4Leaderboard(name,userId,0,0.0,0.0,0)
                            displayList.add(user4Leaderboard)
                            database2.setValue(displayList)

                            val userInfo=User(name, userId, 0, "", 0.0, 0, 0.0, 0,0, a, displayList.size-1,b)
                            currentUserDb.child("1").setValue(userInfo)
                            val intent=Intent(this, Profil::class.java)
                            intent.putExtra("user", userId)
                            startActivity(intent)

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, getString(R.string.neuspjesna_registracija),
                                Toast.LENGTH_SHORT).show()
                        }
                    } }
        }
    }

}