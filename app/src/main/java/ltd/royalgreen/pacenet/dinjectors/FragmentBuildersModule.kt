package ltd.royalgreen.pacenet.dinjectors

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ltd.royalgreen.pacenet.*
import ltd.royalgreen.pacenet.billing.BillingFragment
import ltd.royalgreen.pacenet.dashboard.DashboardFragment
import ltd.royalgreen.pacenet.login.ForgotPasswordDialog
import ltd.royalgreen.pacenet.login.LoginFragment
import ltd.royalgreen.pacenet.profile.ProfileFragment
import ltd.royalgreen.pacenet.support.SupportFragment

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

//    @ContributesAndroidInjector
//    abstract fun contributeFaqsFragment(): FaqsFragment
//
//    @ContributesAndroidInjector
//    abstract fun contributePrivacyFragment(): PrivacyFragment
//
//    @ContributesAndroidInjector
//    abstract fun contributeContactFragment(): ContactFragment
//
//    @ContributesAndroidInjector
//    abstract fun contributeSignUpDialogFragment(): SignUpDialog

    @ContributesAndroidInjector
    abstract fun contributeDashboardFragment(): DashboardFragment

    @ContributesAndroidInjector
    abstract fun contributeServiceFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributePaymentFragment(): BillingFragment

    @ContributesAndroidInjector
    abstract fun contributeSupportFragment(): SupportFragment

    @ContributesAndroidInjector
    abstract fun contributeForgotPasswordDialog(): ForgotPasswordDialog

//    @ContributesAndroidInjector
//    abstract fun contributePaymentFosterFragment(): PaymentFosterWebViewFragment
}
