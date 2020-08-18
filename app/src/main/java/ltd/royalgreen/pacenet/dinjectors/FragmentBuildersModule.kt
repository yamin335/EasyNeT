package ltd.royalgreen.pacenet.dinjectors

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ltd.royalgreen.pacenet.*
import ltd.royalgreen.pacenet.billing.PayHistFragment
import ltd.royalgreen.pacenet.billing.BillingFragment
import ltd.royalgreen.pacenet.billing.InvoiceDetailDialog
import ltd.royalgreen.pacenet.billing.InvoiceFragment
import ltd.royalgreen.pacenet.billing.bkash.BKashPaymentWebDialog
import ltd.royalgreen.pacenet.billing.foster.FosterPaymentWebDialog
import ltd.royalgreen.pacenet.dashboard.DashboardFragment
import ltd.royalgreen.pacenet.login.ForgotPasswordDialog
import ltd.royalgreen.pacenet.login.LoginFragment
import ltd.royalgreen.pacenet.pgw.BKashPGWDialog
import ltd.royalgreen.pacenet.pgw.FosterPGWDialog
import ltd.royalgreen.pacenet.profile.PackageChangeFragment
import ltd.royalgreen.pacenet.profile.ProfileFragment
import ltd.royalgreen.pacenet.support.SupportFragment
import ltd.royalgreen.pacenet.support.SupportTicketConversationFragment
import ltd.royalgreen.pacenet.support.TicketEntryFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeSupportTicketConversation(): SupportTicketConversationFragment

    @ContributesAndroidInjector
    abstract fun contributeFosterPaymentWebDialog(): FosterPaymentWebDialog

    @ContributesAndroidInjector
    abstract fun contributeBKashPaymentWebDialog(): BKashPaymentWebDialog

    @ContributesAndroidInjector
    abstract fun contributeDashboardFragment(): DashboardFragment

    @ContributesAndroidInjector
    abstract fun contributeServiceFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributePackageChangeFragment(): PackageChangeFragment

    @ContributesAndroidInjector
    abstract fun contributePaymentFragment(): BillingFragment

    @ContributesAndroidInjector
    abstract fun contributeSupportFragment(): SupportFragment

    @ContributesAndroidInjector
    abstract fun contributeForgotPasswordDialog(): ForgotPasswordDialog

    @ContributesAndroidInjector
    abstract fun contributePayHistFragment(): PayHistFragment

    @ContributesAndroidInjector
    abstract fun contributeRechargeHistFragment(): InvoiceFragment

    @ContributesAndroidInjector
    abstract fun contributeTicketEntryFragment(): TicketEntryFragment

    @ContributesAndroidInjector
    abstract fun contributePrivacyPolicyFragment(): PrivacyPolicyFragment

    @ContributesAndroidInjector
    abstract fun contributePrivacyContactFragment(): ContactFragment

    @ContributesAndroidInjector
    abstract fun contributeBKashPGWDialog(): BKashPGWDialog

    @ContributesAndroidInjector
    abstract fun contributeFosterPGWDialog(): FosterPGWDialog

    @ContributesAndroidInjector
    abstract fun contributeInvoiceDetailDialog(): InvoiceDetailDialog
}
