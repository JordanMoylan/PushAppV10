package ie.jordanm.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import ie.jordanm.R
import ie.jordanm.main.PushApp
import ie.jordanm.models.PushupModel
import ie.jordanm.utils.createLoader
import ie.jordanm.utils.hideLoader
import ie.jordanm.utils.showLoader
import kotlinx.android.synthetic.main.fragment_edit.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class EditFragment : Fragment(), AnkoLogger {

    lateinit var app: PushApp
    lateinit var loader : AlertDialog
    lateinit var root: View
    var editWorkoutActivity: PushupModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as PushApp

        arguments?.let {
            editWorkoutActivity = it.getParcelable("editdonation")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_edit, container, false)
        activity?.title = getString(R.string.action_edit)
        loader = createLoader(activity!!)

        root.editAmount.setText(editWorkoutActivity!!.amount.toString())

        root.editUpdateButton.setOnClickListener {
            showLoader(loader, "Updating Donation on Server...")
            updateDonationData()
            updateDonation(editWorkoutActivity!!.uid, editWorkoutActivity!!)
            updateUserDonation(app.auth.currentUser!!.uid,
                               editWorkoutActivity!!.uid, editWorkoutActivity!!)
        }

        return root
    }

    companion object {
        @JvmStatic
        fun newInstance(donation: PushupModel) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("editdonation",donation)
                }
            }
    }

    fun updateDonationData() {
        editWorkoutActivity!!.amount = root.editAmount.text.toString().toInt()
    }

    fun updateUserDonation(userId: String, uid: String?, donation: PushupModel) {
        app.database.child("user-donations").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(donation)
                        activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.homeFrame, ProgressFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Donation error : ${error.message}")
                    }
                })
    }

    fun updateDonation(uid: String?, donation: PushupModel) {
        app.database.child("donations").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(donation)
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Donation error : ${error.message}")
                    }
                })
    }
}
