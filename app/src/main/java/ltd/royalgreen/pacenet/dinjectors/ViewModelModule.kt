package ltd.royalgreen.pacenet.dinjectors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ltd.royalgreen.pacenet.util.PaceNetViewModelFactory
import ltd.royalgreen.pacenet.MainActivityViewModel
import ltd.royalgreen.pacenet.login.LoginViewModel

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    abstract fun bindMainActivityViewModel(mainActivityViewModel: MainActivityViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(FaqsFragmentViewModel::class)
//    abstract fun bindFaqsFragmentViewModel(faqsFragmentViewModel: FaqsFragmentViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(PrivacyFragmentViewModel::class)
//    abstract fun bindPrivacyFragmentViewModel(privacyFragmentViewModel: PrivacyFragmentViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(ContactFragmentViewModel::class)
//    abstract fun bindContactFragmentViewModel(contactFragmentViewModel: ContactFragmentViewModel): ViewModel
//
//    @Binds
//    @IntoMap
//    @ViewModelKey(LoginViewModel::class)
//    abstract fun bindLoginViewModel(loginViewModel: LoginViewModel): ViewModel
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
