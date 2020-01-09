/*
 * Copyright (c) 2019 Andika Wasisto
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.randonautica.app.Classes

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.randonautica.app.NoiseBasedCamRng

abstract class runCamRng {

        companion object {
            private lateinit var context: Context
            const val REQUEST_PERMISSIONS = 1
            fun setContext(con: Context) {
                context =con
            }
            fun retContext(): Context {
                return context;
            }

            @JvmStatic
            fun onStartR(activity: Activity) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_PERMISSIONS)
                    Log.d("testtttt", "no")

                } else {
                    Log.d("testtttt", "ye")

                   // setup()
                }
            }

            @JvmStatic
            fun setup(): NoiseBasedCamRng {
                var rng = NoiseBasedCamRng.getInstance(context).apply {
                    channel = NoiseBasedCamRng.Channel.RED
                    useMultiplePixels = true
                    movingAverageWindowLength = 30
                    vonNeumannUnbias = true
                    onError = {
                        it.printStackTrace()
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_LONG).show()
                    }
                }

                Toast.makeText(context, "Now on", Toast.LENGTH_LONG).show()
              return rng;
            }


        }
}