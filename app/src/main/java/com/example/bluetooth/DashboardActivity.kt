package com.example.bluetooth

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.example.bluetooth.databinding.ActivityMainBinding
import java.lang.Math.*


class DisplayMessageActivity : AppCompatActivity() {

    var iv: ImageView? = null
    var linear: LinearLayout? = null

    var devList = mutableMapOf<String, Device>()
    private var totalDevices = 0

    class Device(var device: BluetoothDevice?, var num: Int = 0, var imageView: ImageView?, var scale: Int = 1, var connected: Boolean = false ) {
        init {

        }
    }

    lateinit var binding:ActivityMainBinding

    private var pairedDevices: Set<BluetoothDevice>? = null

    var context: Context? = null


    var width: Int = 0
    var height: Int = 0

    var centerx: Int = 0
    var centery: Int = 0
    var truecenterx: Int = 0
    var truecentery: Int = 0
    var offsetX: Double = 0.0
    var offsetY: Double = 0.0
    var oldOffsetX: Double = 0.0
    var oldOffsetY: Double = 0.0

    var oldX: Double = 0.0
    var oldY: Double = 0.0

    var newX: Double = 0.0
    var newY: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard_activity)


        context = this;
        width = (context as DisplayMessageActivity).resources.displayMetrics.widthPixels
        height = (context as DisplayMessageActivity).resources.displayMetrics.heightPixels - 100

        centerx = width/2
        centery = height/2

        truecenterx = width/2
        truecentery = height/2

        val bluetoothManager = applicationContext.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
//        binding.btnBluetoothOn.setOnClickListenter(View.OnClickListener) {
//          val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(intent,1)
//        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        } else {}
        pairedDevices = bluetoothAdapter.bondedDevices
        totalDevices = (pairedDevices as MutableSet<BluetoothDevice>?)?.size ?: 0

        var data:StringBuffer=StringBuffer()
        var counter = 0
        addPhone(width/2, height/2)
        for(device:BluetoothDevice in (pairedDevices as MutableSet<BluetoothDevice>?)!!)
        {
            devList[device.address] = (Device(device, counter, null, 2, false))
            addDevice(device, width/2, height/2, counter++)
            data.append("Device Name=" + device.name+" Device Address=" + device.address)
        }
//        Toast.makeText(applicationContext, data, Toast.LENGTH_SHORT).show()

//        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
//        pairedDevices?.forEach { device ->
//            val deviceName = device.name
//            val deviceHardwareAddress = device.address // MAC address
//        }
        val addButton = findViewById<Button>(R.id.button)
//        val deviceButton = findViewById<ImageView>(R.id.imageView2)

        addButton.setOnClickListener {
//            Log.d("test","hi")
        }

        val layout = findViewById<ConstraintLayout>(R.id.ConstraintView1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Log.d("testy","bro")
            layout.setOnCapturedPointerListener { view, motionEvent ->
                // Get the coordinates required by your app
                val horizontalOffset: Float = motionEvent.x
                layout.x += horizontalOffset
                // Use the coordinates to update your view and return true if the event was
                // successfully processed
                true
            }
        }

        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        this.registerReceiver(broadcastReceiver, filter)

        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val mProfileListener: ServiceListener = object : ServiceListener {

            @SuppressLint("MissingPermission")
            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                Log.d("test", "1")
                if (profile == BluetoothProfile.A2DP) {
                    Log.d("test", "2")
                    var deviceConnected = false
                    val btA2dp = proxy as BluetoothA2dp
                    val a2dpConnectedDevices = btA2dp.connectedDevices
                    if (a2dpConnectedDevices.size != 0) {
                        Log.d("test", "3")
                        var count = 0
                        for (device in a2dpConnectedDevices) {

                            Log.d("test", "4")

                            devList[device.address]?.connected  = true
                            devList[device?.address]?.imageView?.clearColorFilter()
                        }
                    }
                    if (!deviceConnected) {

                    }
                    mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP, btA2dp)
                }
            }

            override fun onServiceDisconnected(profile: Int) {
                // TODO
            }
        }
        mBluetoothAdapter.getProfileProxy(context, mProfileListener, BluetoothProfile.A2DP)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        var device: BluetoothDevice? = null

        @SuppressLint("MissingPermission")
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            val action = intent.action
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            if (BluetoothDevice.ACTION_FOUND == action) {
                Log.d("test", "AHHH")
                Toast.makeText(
                    baseContext,
                    (device?.name ?: "Device") + " found an action",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {

                devList[device?.address]?.imageView?.clearColorFilter()
                devList[device?.address]?.connected  = true
                Toast.makeText(
                    baseContext,
                    (device?.name ?: "Device") + " is now Connected",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                val matrix = ColorMatrix()
                matrix.setSaturation(0f)
                devList[device?.address]?.imageView?.setColorFilter(ColorMatrixColorFilter(matrix))
                devList[device?.address]?.connected  = false
                Toast.makeText(
                    baseContext,
                    (device?.name ?: "Device") + " is disconnected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun addPhone(x: Int, y: Int) {
        val imageView = ImageView(this)
        imageView.layoutParams = LinearLayout.LayoutParams(350, 350) // value is in pixels
        imageView.x = (x - 350/2).toFloat()
        imageView.y = (y - 350/2).toFloat()
        val imgResId = R.drawable.p1
        var resId = imgResId
        imageView.setImageResource(imgResId)

//            binding.changeImage.setOnClickListener {
//                resId =
//                    if (resId == R.drawable.ic_launcher_background) R.mipmap.ic_launcher else R.drawable.ic_launcher_background
//                imageView.setImageResource(resId)
//            }

        // Add ImageView to LinearLayout
        val layout = findViewById<ConstraintLayout>(R.id.ConstraintView1)
        layout.addView(imageView)

        imageView.setOnClickListener {

            val path = Path().apply {
                moveTo(layout.x, layout.y);
                lineTo((0).toFloat(), (0).toFloat())
                offset((0).toFloat(), (0).toFloat())
            }

            val move = ObjectAnimator.ofFloat(layout, View.X, View.Y, path).apply {
                duration = 1000
//                start()
            }

            val scaleX = ObjectAnimator.ofFloat(layout, "scaleX", 1f).apply {
                duration = 1000
//                start()
            }
            val scaleY = ObjectAnimator.ofFloat(layout, "scaleY", 1f).apply {
                duration = 1000
//                start()
            }
//            val scaleDownY = ObjectAnimator.ofFloat(layout, "scaleY", 0.5f)

            val animation = AnimatorSet()
            animation.play(move).with(scaleX).with(scaleY)
            animation.start()
        }

        devList["-1"] = (Device(null, -1, imageView, 2))
    }

    fun addDevice(device: BluetoothDevice, cx: Int = 0, cy: Int = 0, num: Int = 0) {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        val imageView = ImageView(this)
        imageView.layoutParams = LinearLayout.LayoutParams(250, 250) // value is in pixels
        imageView.x = (centerx - 250/2).toFloat()
        imageView.y = (centery - 250/2).toFloat()
        val imgResId = R.drawable.p1
        var resId = imgResId
        imageView.setImageResource(imgResId)
//        device.connectGatt()
        if (devList[device.address]?.connected == false) {
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            imageView.setColorFilter(ColorMatrixColorFilter(matrix))
        }
//            binding.changeImage.setOnClickListener {
//                resId =
//                    if (resId == R.drawable.ic_launcher_background) R.mipmap.ic_launcher else R.drawable.ic_launcher_background
//                imageView.setImageResource(resId)
//            }

        // Add ImageView to LinearLayout
        val layout = findViewById<ConstraintLayout>(R.id.ConstraintView1)
        layout.addView(imageView)

//        Log.e("test", "addDevice: " + cx)
//        Log.d("test", "" + device)
        val radius = 400
        val deg = (360 / totalDevices) * num

        val tempY = radius * sin(toRadians(deg.toDouble()))
        val tempX = radius * cos(toRadians(deg.toDouble()))

        val myX = (centerx-250/2) + tempX
        val myY = (centery-250/2) + tempY

//        val myX = tempX
//        val myY = tempY


        val path = Path().apply {
//            arcTo(0f, 0f, 1000f, 1000f, 270f, -180f, true)

//            rLineTo(100f, 100f)
            lineTo(tempX.toFloat(), tempY.toFloat())
            offset((centerx-250/2).toFloat(), (centery-250/2).toFloat())
        }

        val animator = ObjectAnimator.ofFloat(imageView, View.X, View.Y, path).apply {
            duration = 1000
            start()
        }

        imageView.setOnClickListener {

            val scale = 1.5f
            Toast.makeText(applicationContext, device.name, Toast.LENGTH_SHORT).show()

            val path = Path().apply {
                moveTo(layout.x, layout.y);
                lineTo((-tempX * scale).toFloat(), (-tempY * scale).toFloat())
                offset((0).toFloat(), (0).toFloat())
            }

            val move = ObjectAnimator.ofFloat(layout, View.X, View.Y, path).apply {
                duration = 1000
//                start()
            }

            val scaleX = ObjectAnimator.ofFloat(layout, "scaleX", scale).apply {
                duration = 1000
//                start()
            }
            val scaleY = ObjectAnimator.ofFloat(layout, "scaleY", scale).apply {
                duration = 1000
//                start()
            }
//            val scaleDownY = ObjectAnimator.ofFloat(layout, "scaleY", 0.5f)

            val animation = AnimatorSet()
            animation.play(move).with(scaleX).with(scaleY)
            animation.start()
        }

        devList[device.address]?.imageView = imageView

    }



}