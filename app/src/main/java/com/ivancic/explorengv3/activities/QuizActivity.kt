package com.ivancic.explorengv3.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ivancic.explorengv3.R
import com.ivancic.explorengv3.databinding.ActivityQuizBinding
import com.ivancic.explorengv3.models.GlideApp
import com.ivancic.explorengv3.models.Question

class QuizActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var userId:String=""
    var displayList = ArrayList<Question>()
    private var noOfQuiz:Int=0
    private var quizPoints:Int=0
    private var totalPoints:Double=0.0
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Questions")
    private lateinit var binding: ActivityQuizBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null) userId = auth.currentUser!!.uid

        noOfQuiz=intent.getIntExtra("noQuizzes",0)
        quizPoints=intent.getIntExtra("quizPoints",0)
        totalPoints=intent.getDoubleExtra("totalPoints",0.0)

        database.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {
                try{
                    val a : List<Question> = snapshot.children.map{ dataSnapshot -> dataSnapshot.getValue(
                        Question::class.java)!! }
                    displayList.addAll(a)

                }catch (e : Exception){
                }

                triggerQuestion()

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



    }

    private fun triggerQuestion(n:Int=0,currentQuizPoints:Int=0) {

        val question =(0 until displayList.size).random()
        var isItTrue=false
        binding.question.animate().translationYBy(-250f).setDuration(400).withEndAction { binding.question.animate().translationYBy(250f).setDuration(400) }
        binding.btnOptionA.animate().translationXBy(-250f).setDuration(600).withEndAction {binding.btnOptionA.animate().translationXBy(250f).setDuration(600)}
        binding.btnOptionB.animate().translationXBy(250f).setDuration(600).withEndAction {binding.btnOptionB.animate().translationXBy(-250f).setDuration(600)}
        binding.btnOptionC.animate().translationXBy(-350f).setDuration(400).withEndAction {binding.btnOptionC.animate().translationXBy(350f).setDuration(400)}
        binding.btnOptionD.animate().translationXBy(350f).setDuration(400).withEndAction {binding.btnOptionD.animate().translationXBy(-350f).setDuration(400)}

        binding.textquestion.text=displayList[question].question
        binding.btnOptionA.text=displayList[question].optionA
        binding.btnOptionB.text=displayList[question].optionB
        binding.btnOptionC.text=displayList[question].optionC
        binding.btnOptionD.text=displayList[question].optionD


        binding.btnOptionA.background = ResourcesCompat.getDrawable(resources, R.drawable.ripple, null)
        binding.btnOptionB.background = ResourcesCompat.getDrawable(resources, R.drawable.ripple, null)
        binding.btnOptionC.background = ResourcesCompat.getDrawable(resources, R.drawable.ripple, null)
        binding.btnOptionD.background = ResourcesCompat.getDrawable(resources, R.drawable.ripple, null)

        binding.btnNext.visibility= View.GONE

        binding.btnOptionA.setOnClickListener {
            if(displayList[question].answer==1) {
                binding.btnOptionA.background = ResourcesCompat.getDrawable(resources, R.drawable.answertrue, null)
                isItTrue=true}
            else binding.btnOptionA.background = ResourcesCompat.getDrawable(resources, R.drawable.answerfalse, null)
            if(displayList[question].answer==2) binding.btnOptionB.background = ResourcesCompat.getDrawable(resources, R.drawable.answertrue, null)

            if(displayList[question].answer==3) binding.btnOptionC.background = ResourcesCompat.getDrawable(
                resources, R.drawable.answertrue, null)

            if(displayList[question].answer==4) binding.btnOptionD.background = ResourcesCompat.getDrawable(
                resources, R.drawable.answertrue, null)

            binding.btnNext.visibility= View.VISIBLE
            }

        binding.btnOptionB.setOnClickListener {
            if(displayList[question].answer==1) binding.btnOptionA.background = ResourcesCompat.getDrawable(
                resources, R.drawable.answertrue, null)
            if(displayList[question].answer==2)  {
                binding.btnOptionB.background = ResourcesCompat.getDrawable(resources, R.drawable.answertrue, null)
                isItTrue=true}
            else binding.btnOptionB.background = ResourcesCompat.getDrawable(resources, R.drawable.answerfalse, null)
            if(displayList[question].answer==3) binding.btnOptionC.background = ResourcesCompat.getDrawable(
                resources, R.drawable.answertrue, null)
            if(displayList[question].answer==4) binding.btnOptionD.background = ResourcesCompat.getDrawable(
                resources, R.drawable.answertrue, null)
            binding.btnNext.visibility= View.VISIBLE
        }

        binding.btnOptionC.setOnClickListener {
            if(displayList[question].answer==1) binding.btnOptionA.background = ResourcesCompat.getDrawable(
                resources, R.drawable.answertrue, null)
            if(displayList[question].answer==2) binding.btnOptionB.background = ResourcesCompat.getDrawable(
                resources, R.drawable.answertrue, null)
            if(displayList[question].answer==3)  {
                binding.btnOptionC.background = ResourcesCompat.getDrawable(resources, R.drawable.answertrue, null)
                isItTrue=true}
            else binding.btnOptionC.background = ResourcesCompat.getDrawable(resources, R.drawable.answerfalse, null)
            if(displayList[question].answer==4) binding.btnOptionD.background = ResourcesCompat.getDrawable(
                resources, R.drawable.answertrue, null)
            binding.btnNext.visibility= View.VISIBLE
        }

        binding.btnOptionD.setOnClickListener {
            if(displayList[question].answer==1) binding.btnOptionA.background = ResourcesCompat.getDrawable(
                resources, R.drawable.answertrue, null)
            if(displayList[question].answer==2) binding.btnOptionB.background = ResourcesCompat.getDrawable(
                resources, R.drawable.answertrue, null)
            if(displayList[question].answer==3) binding.btnOptionC.background = ResourcesCompat.getDrawable(
                resources, R.drawable.answertrue, null)
            if(displayList[question].answer==4)  {
                binding.btnOptionD.background = ResourcesCompat.getDrawable(resources, R.drawable.answertrue, null)
                isItTrue=true}
            else binding.btnOptionD.background = ResourcesCompat.getDrawable(resources, R.drawable.answerfalse, null)
            binding.btnNext.visibility= View.VISIBLE
        }
        if(n==5) binding.btnNext.text=getString(R.string.finish)
        binding.btnNext.setOnClickListener {
            if(n==5) {
                 val userDB: DatabaseReference =
                    FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users")
                val database2: DatabaseReference =
                    FirebaseDatabase.getInstance("https://explore-ng-default-rtdb.europe-west1.firebasedatabase.app/").getReference("UsersLeaderboard")
                userDB.child(userId).child("/1/numberOfQuizzes").setValue(noOfQuiz+1)
                userDB.child(userId).child("/1/totalQuizPoint").setValue(quizPoints+currentQuizPoints)
                val a:Double=totalPoints+(currentQuizPoints).toDouble()/5
                userDB.child(userId).child("/1/totalPoints").setValue(a)
                userDB.child(userId).child("/1/avgPointsQuiz").setValue((quizPoints+currentQuizPoints).toDouble()/(noOfQuiz+1))
                database2.child(Menu.currUser.userNo.toString()).child("totalQuizPoint").setValue(quizPoints+currentQuizPoints)
                database2.child(Menu.currUser.userNo.toString()).child("totalPoints").setValue(a)
                finish()
            }
            triggerQuestion(n+1,when(isItTrue){true->currentQuizPoints+1 false->currentQuizPoints}) }

        if(displayList[question].type==2){
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("question/${displayList[question].imageOfMarker}.jpg")
            GlideApp.with(this@QuizActivity).load(sRef).into(binding.imagequestion)
        }}


}