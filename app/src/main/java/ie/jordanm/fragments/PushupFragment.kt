package ie.jordanm.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import ie.jordanm.R
import ie.jordanm.main.PushApp
import ie.jordanm.models.PushupModel
import ie.jordanm.utils.*
import kotlinx.android.synthetic.main.fragment_pushups.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.HashMap


class PushupFragment : Fragment(), AnkoLogger {

    lateinit var app: PushApp
    var totalDonated = 0
    lateinit var loader: AlertDialog
    lateinit var eventListener: ValueEventListener
    var numberOneDisplay = 0
    var resetCount = 0

    val datevar = LocalDateTime.now() //gets current date/time
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG) //formats date
    val currentTime = datevar.format(formatter) //passes date to a variable
    val current = currentTime.toString() //collects date as string

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as PushApp
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_pushups, container, false)
        loader = createLoader(activity!!)
        activity?.title = getString(R.string.action_pushup)

        root.setPicker.minValue = 1
        root.setPicker.maxValue = 3000

        root.setPicker.setOnValueChangedListener { _, _, newVal ->
            //Display the newly selected number to paymentAmount
            root.setManualPick.setText("$newVal")
        }
        setButtonListener(root)
        return root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PushupFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    fun setButtonListener(layout: View) {
        layout.finishSet.setOnClickListener {
            val amount = if (layout.setManualPick.text.isNotEmpty())
                layout.setManualPick.text.toString().toInt() else layout.setPicker.value

                writeNewDonation(
                    PushupModel(
                        current = current,
                        amount = amount,
                        numberOneDisplay = numberOneDisplay,
                        email = app.auth.currentUser?.email
                    )
                )
            if (numberOneDisplay > 0) {
                numberOneDisplay = resetCount
                 }
            }
            Toast.makeText(context,"Logged",Toast.LENGTH_SHORT).show()

        layout.pushUp_button.setOnClickListener {
            numberOneDisplay++
            val incrementer = layout.findViewById(R.id.value) as TextView
            incrementer.text = "$numberOneDisplay"
        }
    }


        override fun onResume() {
            super.onResume()
            getTotalDonated(app.auth.currentUser?.uid)
        }

        override fun onPause() {
            super.onPause()
            app.database.child("user-donations")
                .child(app.auth.currentUser!!.uid)
                .removeEventListener(eventListener)
        }

        fun writeNewDonation(donation: PushupModel) {
            // Create new donation at /donations & /donations/$uid
            showLoader(loader, "Adding Donation to Firebase")
            info("Firebase DB Reference : $app.database")
            val uid = app.auth.currentUser!!.uid
            val key = app.database.child("donations").push().key
            if (key == null) {
                info("Firebase Error : Key Empty")
                return
            }
            donation.uid = key
            val donationValues = donation.toMap()

            val childUpdates = HashMap<String, Any>()
            childUpdates["/donations/$key"] = donationValues
            childUpdates["/user-donations/$uid/$key"] = donationValues

            app.database.updateChildren(childUpdates)
            hideLoader(loader)
        }

        fun getTotalDonated(userId: String?) {
            eventListener = object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Donation error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    totalDonated = 0
                    val children = snapshot.children
                    children.forEach {
                        val donation = it.getValue<PushupModel>(PushupModel::class.java)
                        totalDonated += donation!!.amount
                    }
                }
            }

            app.database.child("user-donations").child(userId!!)
                .addValueEventListener(eventListener)
        }
    }
