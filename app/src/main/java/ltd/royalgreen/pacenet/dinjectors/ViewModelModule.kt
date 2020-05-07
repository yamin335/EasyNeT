package ltd.royalgreen.pacenet.dinjectors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ltd.royalgreen.pacenet.billing.bkash.BKashPaymentViewModel
import ltd.royalgreen.pacenet.billing.foster.FosterPaymentViewModel
import ltd.royalgreen.pacenet.util.PaceNetViewModelFactory
import ltd.royalgreen.pacenet.MainViewModel
import ltd.royalgreen.pacenet.billing.PayHistViewModel
import ltd.royalgreen.pacenet.billing.BillingViewModel
import ltd.royalgreen.pacenet.billing.InvoiceDetailViewModel
import ltd.royalgreen.pacenet.billing.InvoiceViewModel
import ltd.royalgreen.pacenet.dashboard.DashboardViewModel
import ltd.royalgreen.pacenet.login.ContactFragmentViewModel
import ltd.royalgreen.pacenet.login.ForgotPassDialogViewModel
import ltd.royalgreen.pacenet.login.LoginViewModel
import ltd.royalgreen.pacenet.profile.PackageAddViewModel
import ltd.royalgreen.pacenet.profile.PackageChangeViewModel
import ltd.royalgreen.pacenet.profile.ProfileViewModel
import ltd.royalgreen.pacenet.support.ConversationDetailViewModel
import ltd.royalgreen.pacenet.support.SupportViewModel
import ltd.royalgreen.pacenet.support.TicketEntryViewModel

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
    @ViewModelKey(PayHistViewModel::class)
    abstract fun bindBillPayHistViewModel(payHistViewModel: PayHistViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InvoiceViewModel::class)
    abstract fun bindRechargeHistViewModel(invoiceViewModel: InvoiceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TicketEntryViewModel::class)
    abstract fun bindTicketEntryViewModel(ticketEntryViewModel: TicketEntryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ConversationDetailViewModel::class)
    abstract fun bindConversationDetailViewModel(conversationDetailViewModel: ConversationDetailViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BKashPaymentViewModel::class)
    abstract fun bindBKashPaymentViewModel(bKashPaymentViewModel: BKashPaymentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FosterPaymentViewModel::class)
    abstract fun bindFosterPaymentViewModel(fosterPaymentViewModel: FosterPaymentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SupportViewModel::class)
    abstract fun bindSupportViewModel(supportViewModel: SupportViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PackageChangeViewModel::class)
    abstract fun bindPackageChangeViewModel(packageChangeViewModel: PackageChangeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PackageAddViewModel::class)
    abstract fun bindPackageAddViewModel(packageAddViewModel: PackageAddViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ContactFragmentViewModel::class)
    abstract fun bindContactFragmentViewModel(contactFragmentViewModel: ContactFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InvoiceDetailViewModel::class)
    abstract fun bindInvoiceDetailViewModel(invoiceDetailViewModel: InvoiceDetailViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: PaceNetViewModelFactory): ViewModelProvider.Factory
}
