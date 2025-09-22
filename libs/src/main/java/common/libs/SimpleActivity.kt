package common.libs

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import common.libs.extensions.viewBinding

abstract class SimpleActivity<VB : ViewBinding>(bindingInflater: (LayoutInflater) -> VB) :
    AppCompatActivity() {
    protected val binding by viewBinding(bindingInflater)
}