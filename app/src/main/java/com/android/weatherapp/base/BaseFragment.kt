package com.android.weatherapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.android.weatherapp.util.PermissionUtil
import com.android.weatherapp.util.ext.bindingInflate
import com.android.weatherapp.util.ext.popBackStackAllInstances

abstract class BaseFragment<VB : ViewDataBinding>(
    private val layout: Int,
) : Fragment() {

    private var _binding: VB? = null
    val binding: VB? get() = _binding
    var isNavigated = false
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (!isNavigated)
            _binding = container?.bindingInflate<VB>(layout)
        return _binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        initClickListeners()
        initFocusListeners()
        initTextChangeListeners()
    }

    /**
     * initialize the fragment
     */
    abstract fun init()
    open fun initClickListeners() {}
    open fun initFocusListeners() {}
    open fun initTextChangeListeners() {}


    fun navigateWithAction(action: NavDirections) {
        isNavigated = true
        findNavController().navigate(action)
    }

    fun navigate(resId: Int, bundle: Bundle? = null) {
        isNavigated = true
        findNavController().navigate(resId, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!isNavigated)
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                val navController = findNavController()
                if (navController.currentBackStackEntry?.destination?.id != null) {
                    findNavController().popBackStackAllInstances(
                        navController.currentBackStackEntry?.destination?.id!!,
                        true
                    )
                } else {
                    navController.popBackStack()
                    _binding = null
                }
            }

    }


}