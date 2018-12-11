package com.media.dmitry68.callrecorder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import com.media.dmitry68.callrecorder.permissions.PermissionManager
import com.media.dmitry68.callrecorder.preferences.ManagerPref
import com.media.dmitry68.callrecorder.preferences.SettingsActivity
import com.media.dmitry68.callrecorder.service.ServiceManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MVPView{

    private val TAG = "LOG"
    private val permissionManager = PermissionManager(this)
    private lateinit var serviceManager: ServiceManager
    lateinit var presenter: MainPresenter
    private lateinit var managerPref: ManagerPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "Create MainActivity")
        serviceManager = ServiceManager(applicationContext)
        managerPref = ManagerPref(this)
        presenter = MainPresenter(this, serviceManager, permissionManager, managerPref)
        switchService.setOnCheckedChangeListener(SwitchModeListener())

        presenter.setUp()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "Resume MainActivity")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "Pause MainActivity")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Destroy MainActivity")
    }

    //TODO: think about move this fun to PermissionManager
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when(requestCode){
            PermissionManager.REQUEST_CODE_ASK_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                with(perms){
                    put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED)
                    put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED)
                    put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED)
                }
                for (i in 0 until permissions.size)
                    perms[permissions[i]] = grantResults[i]
                if (perms[Manifest.permission.READ_PHONE_STATE] == PackageManager.PERMISSION_GRANTED
                    && perms[Manifest.permission.WRITE_EXTERNAL_STORAGE] == PackageManager.PERMISSION_GRANTED
                    && perms[Manifest.permission.RECORD_AUDIO] == PackageManager.PERMISSION_GRANTED) {
                    presenter.onCheckPermission(true)
                    presenter.setSwitchCompatState(true)
                } else {
                    Toast.makeText(this, R.string.message_problem_with_permission, Toast.LENGTH_LONG)
                        .show()
                    presenter.onCheckPermission(false)
                    presenter.setSwitchCompatState(false)
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, REQUEST_SETTINGS)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showSwitchMode(b: Boolean) {
        switchService.visibility = if (b) View.VISIBLE else View.GONE
    }

    override fun setSwitchMode(b: Boolean){
        switchService.isChecked = b
        Log.d(TAG, "switch to $b")
    }

    companion object {
        const val REQUEST_SETTINGS = 100
    }

    inner class SwitchModeListener: CompoundButton.OnCheckedChangeListener{
        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            presenter.switchCompatChange(isChecked)
        }
    }


}
