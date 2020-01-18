package com.randonautica.app

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.randonautica.app.NoiseBasedCamRng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_loading.*

class camRngDialog : DialogFragment() {
    companion object {
        const val REQUEST_PERMISSIONS = 1
    }
    private var entropy = String()

    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.dialog_loading, container)

        //with arrayadapter you have to pass a textview as a resource, and that is simple_list_item_1
        return rootView
    }


    override fun onStart() {
        super.onStart()

        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            setupRngAndViews()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupRngAndViews()
            } else {
                //  finish()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun setupRngAndViews() {
        try {
            val camRng = NoiseBasedCamRng.newInstance(context = requireContext(), numberOfPixelsToUse = 200).apply {
                channel = NoiseBasedCamRng.Channel.GREEN
            }

            val entropy = String()
            val sb = StringBuilder()

            compositeDisposable.add(
                    camRng.getBytes()
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                byteTextView.text = it.toString()
                                var bytes = byteArrayOf()
                                bytes += it.toByte()
                                var st = String()
                                for (b in bytes) {
                                    val st = String.format("%02X", b);
                                    sb.append(st)
                                }
                                Log.d("entropy", ""+sb.length);
                            }
            )

        } catch (t: Throwable) {
            t.printStackTrace()
            Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        NoiseBasedCamRng.reset()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}