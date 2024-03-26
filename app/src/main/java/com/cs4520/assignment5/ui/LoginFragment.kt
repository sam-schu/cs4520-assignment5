package com.cs4520.assignment5.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cs4520.assignment5.R
import com.cs4520.assignment5.databinding.LoginFragmentBinding
import com.cs4520.assignment5.logic.Authenticator

/**
 * The fragment for the login page.
 */
class LoginFragment : Fragment() {
    private lateinit var binding: LoginFragmentBinding
    private lateinit var auth: Authenticator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return LoginFragmentBinding.inflate(inflater).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Authenticator()

        binding.loginButton.setOnClickListener(::loginButtonClicked)
    }

    private fun loginButtonClicked(v: View?) {
        val userField = binding.usernameEntry
        val passField = binding.passwordEntry

        if (auth.authenticate(userField.text.toString(), passField.text.toString())) {
            userField.setText("")
            passField.setText("")
            LoginFragmentDirections.actionLoginFragmentToProductListFragment().let {
                findNavController().navigate(it)
            }
        } else {
            Toast.makeText(context, R.string.invalid_credentials_msg, Toast.LENGTH_LONG).show()
        }
    }
}