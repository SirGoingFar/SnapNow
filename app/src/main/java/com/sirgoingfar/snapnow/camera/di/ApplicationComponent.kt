package com.sirgoingfar.snapnow.camera.di

import com.sirgoingfar.snapnow.camera.ui.fragments.PicturePickerFragment
import com.sirgoingfar.snapnow.sample.di.CameraSampleComponent
import dagger.Component

@Component(modules = [SubComponentsModule::class])
interface ApplicationComponent {

    fun cameraSampleComponent(): CameraSampleComponent.Factory

    fun inject(picturePickerFragment: PicturePickerFragment)

}