package org.tahoe.lafs.ui.base

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.tahoe.lafs.R
import org.tahoe.lafs.databinding.FragmentBaseBinding
import org.tahoe.lafs.extension.hide
import org.tahoe.lafs.extension.show
import org.tahoe.lafs.ui.customview.TahoeToast


abstract class BaseFragment : Fragment() {

    protected val RC_PERMISSION = 10
    protected var permissionGranted = false

    @LayoutRes
    abstract fun getLayoutId(): Int

    private lateinit var binding: FragmentBaseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBaseBinding.inflate(inflater, container, false)
        val rootView = inflater.inflate(R.layout.fragment_base, container, false)
        val contentView = inflater.inflate(getLayoutId(), container, false)
        binding.contentFrame.addView(contentView)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.errorLayout.hide()
        binding.contentFrame.show()
    }

    protected open fun showLoadingScreen() {
        binding.progressBar.show()
        binding.errorLayout.hide()
        // disable touch for screen when loading is shown
        requireActivity().window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    protected open fun showContent() {
        stopLoading()
        binding.errorLayout.hide()
        binding.contentFrame.show()
        // enable the touch when content shown
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    protected fun showError() {
        stopLoading()
        binding.errorLayout.show()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun stopLoading() {
        binding.progressBar.hide()
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    protected fun checkCameraPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionGranted = false
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    RC_PERMISSION
                )
            } else {
                permissionGranted = true
            }
        } else {
            permissionGranted = true
        }
    }

    protected fun checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionGranted = false
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    RC_PERMISSION
                )
            } else {
                permissionGranted = true
            }
        } else {
            permissionGranted = true
        }
    }

    protected fun showToast(title: String, type: Int) {
        TahoeToast.makeText(
            context,
            title,
            TahoeToast.LENGTH_LONG,
            type,
            TahoeToast.POSITION_DEFAULT
        ).show()
    }
}