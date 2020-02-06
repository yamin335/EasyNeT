package ltd.royalgreen.pacenet.dinjectors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ltd.royalgreen.pacenet.util.PaceNetViewModelFactory
import ltd.royalgreen.pacenet.MainViewModel
import ltd.royalgreen.pacenet.billing.BillPayHistViewModel
import ltd.royalgreen.pacenet.billing.BillingViewModel
import ltd.royalgreen.pacenet.dashboard.DashboardViewModel
import ltd.royalgreen.pacenet.login.ForgotPassDialogViewModel
import ltd.royalgreen.pacenet.login.LoginViewModel
import ltd.royalgreen.pacenet.profile.ProfileViewModel

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainActivityViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ForgotPassDialogViewModel::class)
    abstract fun bindForgotPassDialogViewModel(forgotPassDialogViewModel: ForgotPassDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun bindDashboardViewModel(dashboardViewModel: DashboardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    abstract fun bindProfileViewModel(profileViewModel: ProfileViewModel): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(BillingViewModel::class)
    abstract fun bindBillingViewModel(billingViewModel: BillingViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BillPayHistViewModel::class)
    abstract fun bindBillPayHistViewModel(billPayHistViewModel: BillPayHistViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(DashboardViewModel::class)
//    abstract fun bindDashboardViewModel(dashboardViewModel: DashboardViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(ServiceFragmentViewModel::class)
//    abstract fun bindServiceFragmentViewModel(serviceFragmentViewModel: ServiceFragmentViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(PaymentFragmentViewModel::class)
//    abstract fun bindPaymentFragmentViewModel(paymentFragmentViewModel: PaymentFragmentViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(SupportFragmentViewModel::class)
//    abstract fun bindSupportFragmentViewModel(supportFragmentViewModel: SupportFragmentViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(AboutFragmentViewModel::class)
//    abstract fun bindAboutFragmentViewModel(aboutFragmentViewModel: AboutFragmentViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: PaceNetViewModelFactory): ViewModelProvider.Factory
}
