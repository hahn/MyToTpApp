package com.example.mytotpapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.DefaultCodeVerifier
import dev.samstevens.totp.time.SystemTimeProvider

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_second, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    view.findViewById<Button>(R.id.button_second).setOnClickListener {
      findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
    }

    view.findViewById<Button>(R.id.btncheck).setOnClickListener {
      val string = view.findViewById<EditText>(R.id.edt).text
      val isVerified = checkTotp(string.toString())
      view.findViewById<TextView>(R.id.textview_second).text =  when (isVerified) {
        true -> "Verifikasi berhasil!"
        false -> "Verifikasi gagal!"
      }
    }
  }

  private fun checkTotp(token: String): Boolean {
    val pref = activity?.getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE)
    if (pref != null) {
      val secret = pref.getString(getString(R.string.otp_key),"")
      val timeProvider = SystemTimeProvider()
      val codeGenerator = DefaultCodeGenerator()
      val verifier = DefaultCodeVerifier(codeGenerator, timeProvider)
      return verifier.isValidCode(secret, token)
    }
    return false
  }
}