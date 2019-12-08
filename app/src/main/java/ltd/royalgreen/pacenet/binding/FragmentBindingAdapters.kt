package ltd.royalgreen.pacenet.binding

import android.view.View
import androidx.fragment.app.Fragment
import androidx.databinding.BindingAdapter
import ltd.royalgreen.pacenet.network.ApiCallStatus
import javax.inject.Inject

/**
 * Binding adapters that work with a fragment instance.
 */
class FragmentBindingAdapters @Inject constructor(val fragment: Fragment) {
    // Shows and hides progressbar
    @BindingAdapter("showLoader")
    fun showLoader(view: View, apiCallStatus: ApiCallStatus?) {
        view.visibility = if (apiCallStatus?.equals(ApiCallStatus.LOADING) == true) View.VISIBLE else View.GONE
    }

    @BindingAdapter("showIfInvalid")
    fun showIfInvalid(view: View, errorMessage: Boolean?) {
        view.visibility = if (errorMessage == true) View.VISIBLE else View.GONE
    }
//    @BindingAdapter(value = ["imageUrl", "imageRequestListener"], requireAll = false)
//    fun bindImage(imageView: ImageView, url: String?, listener: RequestListener<Drawable?>?) {
//        Glide.with(fragment).load(url).listener(listener).into(imageView)
//    }
}

