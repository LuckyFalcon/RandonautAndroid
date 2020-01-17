//package com.randonautica.app
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import com.randonautica.app.Classes.runCamRng
//import kotlinx.android.synthetic.main.dialog_loading.*
////1
//class camRngFragment : Fragment() {
//    companion object {
//        const val REQUEST_PERMISSIONS = 1
//    }
//
//    private lateinit var rng: CamRng
//
//    //2
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }
//
//    //3
//    override fun onCreateView(inflater: LayoutInflater,
//                              container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.dialog_loading, container, false)
//    }
//    override fun onStart() {
//        super.onStart()
//
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), runCamRng.REQUEST_PERMISSIONS)
//        } else {
//            setup()
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        if (requestCode == runCamRng.REQUEST_PERMISSIONS) {
//            setup()
//        } else {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        }
//    }
//
//    override fun onStop() {
//        super.onStop()
//        rng.close()
//    }
//
//    private fun setup() {
//        rng = NoiseBasedCamRng.getInstance(requireContext()).apply {
//            channel = NoiseBasedCamRng.Channel.RED
//            useMultiplePixels = true
//            movingAverageWindowLength = 30
//            vonNeumannUnbias = true
//            onError = {
//                it.printStackTrace()
//                Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_LONG).show()
//            }
//        }
//        Toast.makeText(requireContext(), "Now on", Toast.LENGTH_LONG).show()
//
//        val nameObserver = Observer<Long> { newName ->
//            // Update the UI, in this case, a TextView.
//            Toast.makeText(requireContext(), "long", Toast.LENGTH_SHORT).show()
//
//            longTextView.text = newName.toString();
//        }
//
//        val booleanObserver = Observer<Boolean> { newName ->
//            // Update the UI, in this case, a TextView.
//            bitTextView.text = newName.toString();
//        }
//
//        val byteObserver = Observer<Byte> { newName ->
//            // Update the UI, in this case, a TextView.
//            Toast.makeText(requireContext(), "byte", Toast.LENGTH_SHORT).show()
//
//            bitTextView.text = newName.toString();
//        }
//
//        val intObserver = Observer<Int> { newName ->
//            // Update the UI, in this case, a TextView.
//            intTextView.text = newName.toString();
//        }
//
//        val floatObserver = Observer<Float> { newName ->
//            // Update the UI, in this case, a TextView.
//            intTextView.text = newName.toString();
//        }
//
//        val doubleObserver = Observer<Double> { newName ->
//            // Update the UI, in this case, a TextView.
//            intTextView.text = newName.toString();
//        }
//
//
//
//      //  rng.getLiveInt().observe(this, nameObserver)
//
//       // rng.getLiveBoolean().observe(this, booleanObserver)
//        rng.getLiveByte().observe(this, byteObserver)
//     //   rng.getLiveInt().observe(this, intObserver)
//     //   rng.getLiveFloat().observe(this, floatObserver)
//     //   rng.getLiveDouble().observe(this, doubleObserver)
//
//
////
////        rng.getLiveByte().observe(this) {
////            byteTextView.text = it.toString()
////        }
////
////        rng.getLiveInt().observe(this) {
////            intTextView.text = it.toString()
////        }
////
//            rng.getLiveLong().observe(this,nameObserver)
////
////        rng.getLiveFloat().observe(this) {
////            floatTextView.text = it.toString()
////        }
////
////        rng.getLiveDouble().observe(this) {
////            doubleTextView.text = it.toString()
////        }
//    }
//}