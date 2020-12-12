package com.example.mytotpapp

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import coil.Coil
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import dev.samstevens.totp.code.HashingAlgorithm
import dev.samstevens.totp.qr.QrData
import dev.samstevens.totp.qr.ZxingPngQrGenerator
import dev.samstevens.totp.secret.DefaultSecretGenerator
import dev.samstevens.totp.secret.SecretGenerator
import io.nayuki.qrcodegen.QrCode
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

  companion object {
    val TAG = javaClass.canonicalName
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_first, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    view.findViewById<Button>(R.id.button_first).setOnClickListener {
      findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    view.findViewById<Button>(R.id.button_create).setOnClickListener {
      generateQrCode(view)
    }
  }

  fun generateQrCode(view: View) {
    val secretGenerator = DefaultSecretGenerator()
    val secret = secretGenerator.generate()
    val data = QrData.Builder()
      .label("my totp App label")
      .secret(secret)
      .issuer("MyTotpApp")
      .algorithm(HashingAlgorithm.SHA512)
      .digits(6)
      .period(30).build()

    val dataUri = data.uri
    var secretSpace = ""
    secret.forEachIndexed { index, c ->
      secretSpace += if ((index != 0) && (index % 4 == 0)) "$c " else c
    }
    //save secret to preferences
    val pref = activity?.getSharedPreferences(getString(R.string.preference_name), Context.MODE_PRIVATE)
    if (pref != null) {
      with(pref.edit()) {
        putString(getString(R.string.otp_key), secret)
        apply()
      }
    }
    val qr = QrCode.encodeText(dataUri, QrCode.Ecc.MEDIUM)
    val qrImage = qr.toSvgString(1)

    val imageLoader = context?.let {
      ImageLoader.Builder(it).componentRegistry {
        add(SvgDecoder(it))
      }.build()
    }
    val fileTemp = File.createTempFile("qrtemp", ".svg")
    val bw = BufferedWriter(FileWriter(fileTemp))
    bw.write(qrImage)
    bw.close()
    val path = fileTemp.absolutePath
    val imgView = view.findViewById<ImageView>(R.id.img)
    imageLoader?.let { imgView.load(File(path), it) }
    view.findViewById<TextView>(R.id.tv_secret).text = "$secretSpace"
    Log.d(TAG, "dataUri: $dataUri")
    Log.d(TAG, "data: $data")
    Log.d(TAG, "secret: $secret")
    Log.d(TAG, "scret: $secretSpace")
  }
}