package com.vrudenko.telephonyserver.presentation.server

import android.Manifest.permission.READ_CALL_LOG
import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.READ_PHONE_STATE
import android.app.role.RoleManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.vrudenko.telephonyserver.common.extensions.lazyLogger
import com.vrudenko.telephonyserver.databinding.FragmentSetupBinding
import com.vrudenko.telephonyserver.presentation.CallScreeningRoleResultContract
import com.vrudenko.telephonyserver.presentation.common.changeVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SetupFragment : Fragment() {

    private val viewModel by viewModels<SetupViewModel>()

    private val log by lazyLogger()

    private var _binding: FragmentSetupBinding? = null
    private val binding get() = _binding!!

    private val recyclerAdapter: CallLogAdapter = CallLogAdapter()

    private val callScreeningResultLauncher =
        registerForActivityResult(
            CallScreeningRoleResultContract()
        ) {
            log.debug("request role result: {}", it)
            viewModel.handleScreeningRoleRequestResult(it)
        }

    private val permissionsResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { grantedMap ->
            // Handle Permission granted/rejected
            if (grantedMap.values.all { it }) {
                log.debug("granted")
                viewModel.handlePermissionsGranted(true)
            } else {
                log.debug("denied")
                viewModel.handlePermissionsGranted(false)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.callLogRecycler.adapter = recyclerAdapter
        binding.callLogRecycler.layoutManager = LinearLayoutManager(context)
        binding.buttonCallsTracking.setOnClickListener {
            viewModel.handleButtonCallsTrackingClick()
        }
        subscribeViewModelData()
    }

    private fun subscribeViewModelData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            subscribeCallScreenRequests()
        }

        viewModel.trackingButtonTextSource.observe(viewLifecycleOwner) { buttonText ->
            binding.buttonCallsTracking.text = buttonText
        }

        viewModel.permissionsRequestRequiredState.observe(viewLifecycleOwner) { requestRequired ->
            if (requestRequired) requestPermissions()
        }

        viewModel.errorMessageSource.observe(viewLifecycleOwner) { permissionError ->
            binding.errorText.run {
                text = permissionError.text
                changeVisibility(permissionError.show)
            }
        }

        viewModel.connectionTextState.observe(viewLifecycleOwner) { connectionText ->
            binding.connectionInfoText.text = connectionText
        }

        viewModel.serverTextState.observe(viewLifecycleOwner) { serverInfo ->
            binding.serverInformation.text = serverInfo
        }

        viewModel.buttonActiveState.observe(viewLifecycleOwner) { active ->
            binding.buttonCallsTracking.isClickable = active
        }

        viewModel.callLogItemsState.observe(viewLifecycleOwner) { calls ->
            recyclerAdapter.items = calls
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun subscribeCallScreenRequests() {
        viewModel.screeningRoleRequestRequiredState.observe(viewLifecycleOwner) { requestRequired ->
            if (requestRequired) requestCallScreeningRole()
        }
    }

    private fun requestPermissions() {
        fun String.addToListIfNotGranted(permissionList: MutableList<String>) {
            if (PermissionChecker.checkSelfPermission(
                    requireContext(),
                    this
                ) != PERMISSION_GRANTED
            ) {
                permissionList.add(this)
            }
        }

        val permissions = mutableListOf<String>()
        READ_PHONE_STATE.addToListIfNotGranted(permissions)
        READ_CONTACTS.addToListIfNotGranted(permissions)
        READ_CALL_LOG.addToListIfNotGranted(permissions)

        if (permissions.isEmpty()) {
            //everything has been already granted
            viewModel.handlePermissionsGranted(true)
        } else {
            permissionsResultLauncher.launch(permissions.toTypedArray())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestCallScreeningRole() {
        val roleManager = requireContext().getSystemService(AppCompatActivity.ROLE_SERVICE) as RoleManager
        if (roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
            viewModel.handleScreeningRoleRequestResult(true)
        } else {
            callScreeningResultLauncher.launch(null)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}