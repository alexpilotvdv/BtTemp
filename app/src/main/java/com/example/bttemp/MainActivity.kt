package com.example.bttemp

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity() {

    private var m_bluetoothAdapter: BluetoothAdapter?=null
    private lateinit var m_pairedDevice: Set<BluetoothDevice>
    private val REQUEST_BT = 1
    companion object{
        val EXTRA_ADDRESS: String ="Devise_address"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        m_bluetoothAdapter= BluetoothAdapter.getDefaultAdapter()
        if(m_bluetoothAdapter==null){
            toast("Устройство не оборудовано блютуз")
            return
        }
        if(!m_bluetoothAdapter!!.isEnabled){
          val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
          startActivityForResult(enableBluetoothIntent,REQUEST_BT)
        }
        val buttonRefresh=findViewById<Button>(R.id.list_refresh)
        buttonRefresh.setOnClickListener{pairedDeviceList()}

    }
    private fun pairedDeviceList(){
     m_pairedDevice=m_bluetoothAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()
        if(!m_pairedDevice.isEmpty()){
            for(device: BluetoothDevice in m_pairedDevice){
                list.add(device)
            }
        } else{
            toast("устройства не найдены")
        }
        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,list)
        val listBtAdapter=findViewById<ListView>(R.id.select_device_list)
        listBtAdapter.adapter=adapter
        //попробовать поменять на без = set...
        listBtAdapter.setOnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address
            intent= Intent(this,ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS,address)
            startActivity(intent)
        }
//        listBtAdapter.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
//            val device: BluetoothDevice = list[position]
//            val address: String = device.address
//            intent= Intent(this,ControlActivity::class.java)
//            intent.putExtra(EXTRA_ADDRESS,address)
//            startActivity(intent)
//
//        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_BT){
            if(resultCode==Activity.RESULT_OK){
                if(m_bluetoothAdapter!!.isEnabled){
                    toast("блютуз включен")
                } else {
                    toast("блютуз выключен")
                }
            }
        } else if(requestCode==Activity.RESULT_CANCELED){
            toast("отмена")
        }
    }
}
