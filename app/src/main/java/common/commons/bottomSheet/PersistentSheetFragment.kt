package common.commons.bottomSheet

import android.os.Bundle
import android.view.View
import android.widget.Toast
import common.commons.databinding.FragmentPersistentSheetBinding
import common.libs.SimpleFragment

class PersistentSheetFragment : SimpleFragment<FragmentPersistentSheetBinding>(FragmentPersistentSheetBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        fBinding.btnFragmentAction.setOnClickListener {
            Toast.makeText(requireContext(), "Action from Persistent Fragment!", Toast.LENGTH_SHORT).show()
        }
    }
}
