package com.sirgoingfar.snapnow.sample.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sirgoingfar.snapnow.R
import com.sirgoingfar.snapnow.camera.di.DaggerApplicationComponent
import com.sirgoingfar.snapnow.sample.di.CameraSampleComponent
import com.sirgoingfar.snapnow.sample.ui.fragments.PicturePickerTestFragment

class PicturePickerActivity : AppCompatActivity() {

    lateinit var cameraSampleComponent: CameraSampleComponent

    override fun onCreate(savedInstanceState: Bundle?) {

        cameraSampleComponent = DaggerApplicationComponent.create()
            .cameraSampleComponent().create()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_picture_picker)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PicturePickerTestFragment.newInstance())
            .commit()
    }

}
