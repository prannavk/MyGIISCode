package com.giis.mobileappproto1.di.bindings

import androidx.lifecycle.ViewModel
import com.giis.mobileappproto1.ui.viewmodels.AppsViewModel
import com.giis.mobileappproto1.ui.viewmodels.CalendarViewModel
import com.giis.mobileappproto1.ui.viewmodels.MessagesViewModel
import com.giis.mobileappproto1.ui.viewmodels.OnlyFeedViewModel
import com.giis.mobileappproto1.ui.viewmodels.HomeViewModel
import com.giis.mobileappproto1.ui.viewmodels.MeatBallBottomSheetViewModel
import com.giis.mobileappproto1.ui.viewmodels.NewBottomSheetDialogViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
abstract class ViewModelBindingsClass {

    @Binds
    @IntoMap
    @ViewModelKey(AppsViewModel::class)
    internal abstract fun postAppsViewModel(viewModel: AppsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun postHomeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CalendarViewModel::class)
    internal abstract fun postCalendarViewModel(viewModel: CalendarViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OnlyFeedViewModel::class)
    internal abstract fun postTasksViewModel(viewModel: OnlyFeedViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MessagesViewModel::class)
    internal abstract fun postMessagesViewModel(viewModel: MessagesViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MeatBallBottomSheetViewModel::class)
    internal abstract fun postMeatBallBottomSheetViewModel(viewModel: MeatBallBottomSheetViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NewBottomSheetDialogViewModel::class)
    internal abstract fun postNewBottomSheetDialogViewModel(viewModel: NewBottomSheetDialogViewModel): ViewModel

}