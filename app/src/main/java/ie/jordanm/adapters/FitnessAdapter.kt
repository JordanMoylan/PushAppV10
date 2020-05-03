package ie.jordanm.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.jordanm.R
import ie.jordanm.models.PushupModel
import kotlinx.android.synthetic.main.card_pushups.view.*

interface FitnessListener {
    fun onDonationClick(donation: PushupModel)
}

class FitnessAdapter constructor(var pushups: ArrayList<PushupModel>,
                                  private val listener: FitnessListener)
    : RecyclerView.Adapter<FitnessAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.card_pushups,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val pushup = pushups[holder.adapterPosition]
        holder.bind(pushup,listener)
    }

    override fun getItemCount(): Int = pushups.size

    fun removeAt(position: Int) {
        pushups.removeAt(position)
        notifyItemRemoved(position)
    }

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(donation: PushupModel, listener: FitnessListener) {
            itemView.tag = donation
            itemView.setCounter.text = donation.amount.toString()
            itemView.dateDisplay.text = donation.current
            itemView.repCounter.text=donation.numberOneDisplay.toString()
            itemView.setOnClickListener { listener.onDonationClick(donation) }
        }
    }
}
