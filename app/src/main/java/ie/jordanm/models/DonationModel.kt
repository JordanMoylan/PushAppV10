package ie.jordanm.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@IgnoreExtraProperties
@Parcelize
data class PushupModel(
    var uid: String? = "",
    var current: String = "",
    var amount: Int = 0,
    var numberOneDisplay: Int = 0,
    var message: String = "a message",
    var upvotes: Int = 0,
    var email: String? = "joe@bloggs.com")
                        : Parcelable
{
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "uid" to uid,
            "amount" to amount,
            "current" to current,
            "numberOneDisplay" to numberOneDisplay,
            "message" to message,
            "upvotes" to upvotes,
            "email" to email
        )
    }
}



