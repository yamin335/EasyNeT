package ltd.royalgreen.pacenet.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import ltd.royalgreen.pacenet.CustomAlertDialog
import ltd.royalgreen.pacenet.MainNavigationFragment

import ltd.royalgreen.pacenet.R
import ltd.royalgreen.pacenet.billing.BillPaymentHelper
import ltd.royalgreen.pacenet.billing.RechargeConfirmDialog
import ltd.royalgreen.pacenet.billing.bkash.BkashDataModel
import ltd.royalgreen.pacenet.binding.FragmentDataBindingComponent
import ltd.royalgreen.pacenet.databinding.ProfilePackageChangeFragmentBinding
import ltd.royalgreen.pacenet.pgw.BKashPGWDialog
import ltd.royalgreen.pacenet.pgw.FosterPGWDialog
import ltd.royalgreen.pacenet.util.*
import javax.inject.Inject
import kotlin.math.abs

class PackageChangeFragment : MainNavigationFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: PackageChangeViewModel by viewModels {
        // Get the ViewModel.
        viewModelFactory
    }

    private val args: PackageChangeFragmentArgs by navArgs()

    private var binding by autoCleared<ProfilePackageChangeFragmentBinding>()

    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    private lateinit var bkashPgwDialog: BKashPGWDialog

    private lateinit var fosterPgwDialog: FosterPGWDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.profile_package_change_fragment,
            container,
            false,
            dataBindingComponent
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        viewModel.toastError.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                showErrorToast(requireContext(), it)
                viewModel.toastError.value = null
            }
        })

        viewModel.toastSuccess.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                showSuccessToast(requireContext(), it)
                viewModel.toastSuccess.value = null
            }
        })

        viewModel.popBackStack.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                findNavController().popBackStack()
            }
        })

        binding.cancel.setOnClickListener {
            findNavController().popBackStack()
        }

        if (savedInstanceState == null) {
            val pack = args.ChangingUserPackService
            viewModel.changingPackage= pack
            viewModel.userConnectionId = args.UserConnectionId
            viewModel.consumeData = args.ConsumeData
            viewModel.selectedPackage = ChildPackService(pack.packServiceId, pack.packServiceName,
                pack.packServicePrice, pack.packServiceTypeId, pack.packServiceType,
                pack.parentPackServiceId, pack.parentPackServiceName, pack.isChecked, pack.isParent)
        }

        binding.name = viewModel.selectedPackage?.packServiceName
        binding.price = "${viewModel.selectedPackage?.packServicePrice?.toRounded(2)} BDT"

        viewModel.packServiceList.observe(viewLifecycleOwner, Observer { packList ->
            packList?.let {
                val adapter = PackageListAdapter(it as ArrayList<ChildPackService>, object : PackageListAdapter.OnItemSelectListener {
                    override fun onItemClicked(packService: ChildPackService) {
                        viewModel.selectedPackage = packService
                        binding.name = viewModel.selectedPackage?.packServiceName
                        binding.price = "${viewModel.selectedPackage?.packServicePrice?.toRounded(2)} BDT"

                        binding.save.isEnabled = packService.packServiceId != viewModel.changingPackage?.packServiceId
                    }
                })
                binding.packRecycler.addItemDecoration(RecyclerItemDivider(requireContext(), LinearLayoutManager.VERTICAL, 16))
                binding.packRecycler.adapter = adapter
            }
        })

        binding.save.setOnClickListener {
            calculateAmount()
            val helper = viewModel.packageChangeHelper
            val methods = viewModel.payMethods.value
            val payMethods = methods?.filter { payMethod ->
                payMethod.methodName == "Card" || payMethod.methodName == "Card/BKash"
            }?.map { payMethod ->
                if (payMethod.methodName == "Card") {
                    payMethod.methodName = "${payMethod.methodName}/BKash"
                }
                payMethod
            } as? ArrayList<PayMethod>
            val userPackServiceId = viewModel.changingPackage?.userPackServiceId
            if (helper != null && payMethods != null && userPackServiceId != null) {
                print(" ------ Calculation ------ \n IsUpgrade: ${if (helper.isUpgrade)  "YES" else "NO"} \n Required Amount: ${helper.requiredAmount} \n Actual Payment: ${helper.actualPayAmount} \n Pay Amount: ${helper.payAmount} \n Saved Amount: ${helper.savedAmount} \n Deducted Amount: ${helper.deductedAmount} \n ------ End ------ \n")
                viewModel.billPaymentHelper = BillPaymentHelper(helper.payAmount, helper.deductedAmount, 0, userPackServiceId, true)
                val packageChangeConfirmDialog = CustomAlertDialog(object :
                    CustomAlertDialog.YesCallback {
                    override fun onYes() {
                        viewModel.saveChangedPackage()
                    }
                }, "Are you sure?", "Are you sure to migrate in package: ${viewModel.selectedPackage?.packServiceName}? Which will ${if (viewModel.packageChangeHelper?.isUpgrade == true) "cost ${viewModel.packageChangeHelper?.requiredAmount}" else "save ${viewModel.packageChangeHelper?.savedAmount}"} BDT")

                if (viewModel.consumeData?.isDue == true) {
                    packageChangeConfirmDialog.show(childFragmentManager, "#package_change_dialog")
                } else if (viewModel.consumeData?.isDue == false) {
                    if (helper.isUpgrade) {
                        if (helper.payAmount > 0.0) {
                            val payDialog = PackageChangePaymentDialog(viewModel.selectedPackage?.packServiceName ?: "Unknown", helper.payAmount, payMethods, object : PackageChangePaymentDialog.PayCallback {
                                override fun onPayClicked() {
                                    showRechargeConfirmDialog(helper.payAmount)
                                }
                            })
                            payDialog.show(childFragmentManager, "package_change_pay_dialog")
                        } else {
                            packageChangeConfirmDialog.show(childFragmentManager, "#package_change_dialog")
                        }
                    } else {
                        packageChangeConfirmDialog.show(childFragmentManager, "#package_change_dialog")
                        print("Saved amount: ${helper.savedAmount}")
                    }
                }
            } else {
                showErrorToast(requireContext(), "Not possible at this moment, please try again later!")
            }
        }

        viewModel.bKashToken.observe(viewLifecycleOwner, Observer { bkashDataModel ->
            val billPaymentHelper = viewModel.billPaymentHelper
            if (bkashDataModel != null && billPaymentHelper != null) {
                showBkashPgwDialog(bkashDataModel, billPaymentHelper)
            }
        })

        viewModel.fosterUrl.observe(viewLifecycleOwner, Observer { (paymentProcessUrl, paymentStatusUrl) ->
            if (paymentProcessUrl != null && paymentStatusUrl != null) {
                fosterPgwDialog = FosterPGWDialog (object : FosterPGWDialog.FosterPaymentCallback {
                    override fun onFosterPaymentFinished(fosterResponse: String?) {
                        if (fosterResponse != null) {
                            viewModel.saveChangedPackageByFoster(fosterResponse)
                        } else {
                            fosterPgwDialog.dismiss()
                        }
                    }
                }, paymentProcessUrl, paymentStatusUrl)
                fosterPgwDialog.isCancelable = false
                fosterPgwDialog.show(childFragmentManager, "#foster_payment_dialog")
            }
        })

        viewModel.getUserBalance()
        viewModel.getAllPackService()
        viewModel.getPayMethods()
    }

    private fun showBkashPgwDialog(bkashToken: BkashDataModel, billPaymentHelper: BillPaymentHelper) {
        bkashPgwDialog = BKashPGWDialog(object : BKashPGWDialog.BkashPaymentCallback {
            override fun onBkashPaymentFinished(bkashResponse: String?) {
                if (bkashResponse != null) {
                    viewModel.saveChangedPackageByBkash(bkashResponse)
                } else {
                    bkashPgwDialog.dismiss()
                }
            }
        }, bkashToken.createBkashModel,
            bkashToken.paymentRequest, billPaymentHelper)
        bkashPgwDialog.isCancelable = false
        bkashPgwDialog.show(childFragmentManager, "#bkash_payment_dialog")
    }

    private fun showRechargeConfirmDialog(amount: Double) {
        val rechargeConfirmDialog = RechargeConfirmDialog(object : RechargeConfirmDialog.RechargeConfirmCallback {
            override fun onBKashClicked(amount: Double) {
                val bkashToken = viewModel.bKashToken.value
                val billPaymentHelper = viewModel.billPaymentHelper.returnIfNull {
                    showErrorToast(requireContext(), "Can not possible at this moment, please try again later!")
                    return
                }
                if ( bkashToken != null ) {
                    showBkashPgwDialog(bkashToken, billPaymentHelper)
                } else {
                    viewModel.getBkashToken()
                }
            }

            override fun onFosterClicked(amount: Double) {
                viewModel.getFosterPaymentUrl(amount)
            }
        }, amount)
        rechargeConfirmDialog.isCancelable = false
        rechargeConfirmDialog.show(childFragmentManager, "#recharge_confirm_dialog")
    }

    private fun calculateAmount() {
        val balanceAmount = viewModel.userBalance.value?.balanceAmount ?: return
        val consumeData = viewModel.consumeData ?: return
        val restDays = consumeData.restDays ?: return
        val selectedPackPrice = viewModel.selectedPackage?.packServicePrice ?: return
        val restAmount = consumeData.restAmount ?: return
        val selectedPackUnitPrice = selectedPackPrice / 30
        val newPackPrice = selectedPackUnitPrice * restDays
        val requiredAmount = newPackPrice - restAmount
        val requiredRounded = requiredAmount.toRounded(2)

        when {
            requiredAmount > 0 -> {
                var deductedAmount = 0.0
                if (balanceAmount > 0 && balanceAmount < requiredAmount) {
                    deductedAmount = balanceAmount
                } else if (balanceAmount > requiredAmount) {
                    deductedAmount = requiredAmount
                }

                val deductRounded = abs(deductedAmount).toRounded(2)

                if (consumeData.isDue == true) {
                    val actualPayAmount = requiredAmount - deductRounded
                    val actualRounded = abs(actualPayAmount).toRounded(2)
                    viewModel.packageChangeHelper = PackageChangeHelper(true, requiredRounded, actualRounded, 0.0, 0.0, deductRounded)
                } else {
                    val payAmount = requiredAmount - deductRounded
                    val payRounded = abs(payAmount).toRounded(2)
                    viewModel.packageChangeHelper = PackageChangeHelper(true, requiredRounded, payRounded, payRounded, 0.0, deductRounded)
                }
            }
            requiredAmount < 0 -> {
                val savedAmount = abs(requiredAmount).toRounded(2)
                viewModel.packageChangeHelper = PackageChangeHelper(false, 0.0, 0.0, 0.0, savedAmount, 0.0)
            }
            requiredAmount == 0.0 -> {
                viewModel.packageChangeHelper = PackageChangeHelper(false, 0.0, 0.0, 0.0, 0.0, 0.0)
            }
        }
    }
}
