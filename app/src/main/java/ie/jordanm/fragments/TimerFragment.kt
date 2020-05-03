package ie.jordanm.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ie.jordanm.R
import kotlinx.android.synthetic.main.fragment_timer.*
import kotlinx.android.synthetic.main.fragment_timer.view.*


class TimerFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // calling the timer function when on create
        timer()
    }
//setting a timer every time the timer fragment is opened
    private fun timer () {
        object : CountDownTimer(60000, 1000) { //setting duration
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                text_view_countdown.setText("" + millisUntilFinished / 1000)
            }

            override fun onFinish() {
                text_view_countdown.setText("done!") //after timer message
            }
        }.start() // calling the timer start
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            TimerFragment().apply {
                arguments = Bundle().apply { }
            }
    }
}
