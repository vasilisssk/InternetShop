package com.game.internetshop.views.base.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.game.internetshop.R
import com.game.internetshop.databinding.FragmentSettingsBinding
import com.game.internetshop.viewmodels.SettingsViewModel
import com.game.internetshop.views.base.registration.RegistrationActivity
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import utils.Utils

class SettingsFragment: Fragment() {
    private val viewModel: SettingsViewModel by viewModel()
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var mapOfSettingsErrors: MutableMap<String, String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapOfSettingsErrors = mutableMapOf<String, String>().apply {
            put("General", getString(R.string.signing_out_general))
        }

        viewModel.uiState.observe(viewLifecycleOwner) {
            state -> updateUi(state)
        }

        binding.btnLogout.setOnClickListener {
            viewModel.onLogOutClicked()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // предотвращаем утечки памяти
        _binding = null
    }

    private fun navigateToRegistration() {
        val intent = Intent(activity, RegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun updateUi(state: SettingsViewModel.SettingsUiState) {
        binding.tvGreetings.text = getString(R.string.greetings) + " " + state.userName
        binding.tvEmailValue.text = state.userEmail
        binding.tvPhoneValue.text = state.userPhone

        if (state.isSigningOutSuccess) {
            viewModel.clearSigningOutResult()
            navigateToRegistration()
        } else {
            state.errorMessage?.let { error ->
                when {
                    error.contains("Failed signing out") -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_sign_out),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    error.contains("Failed getting current user") -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_get_user_id),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                    else -> {
                        Utils.showErrorSnackbar(binding.root, getString(R.string.error_settings_general),
                            Snackbar.LENGTH_LONG)
                        viewModel.clearError()
                    }
                }
            }
        }
    }
}