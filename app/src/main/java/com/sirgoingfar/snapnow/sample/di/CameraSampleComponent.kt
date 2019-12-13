package com.sirgoingfar.snapnow.sample.di

import dagger.Subcomponent

@Subcomponent
interface CameraSampleComponent {

    @Subcomponent.Factory
    interface Factory{
        fun create():CameraSampleComponent
    }


}