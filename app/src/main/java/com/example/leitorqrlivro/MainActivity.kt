package com.example.leitorqrlivro

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.leitorqrlivro.databinding.ActivityMainBinding
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnLerQR.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.initiateScan()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(result != null) {
                if(result.getContents() == null) {
                    binding.txtInfoLivro.text = "Livro não encontrado"
                } else {
                    var isbn = result.contents.trim()
                    buscarInformacao(isbn)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    fun buscarInformacao(isbn: String) {

        val url = "http://10.0.2.2/dm2_teste_json/?isbn=$isbn"

        Log.e("URL", url)

        var resp = "false"

        CoroutineScope(Dispatchers.IO).launch {

            resp = URL(url).readText()

            withContext(Dispatchers.Main) {
                preencherInfoLivro(resp)
            }
        }
    }

    fun preencherInfoLivro(resp: String) {

        if(resp.equals("false"))
            binding.txtInfoLivro.text = "Livro não encontrado"
        else {
            val livro = JSONObject(resp)
            val infoLivro = "ISBN: ${livro.getString("isbn")} / " +
                    "TÍTULO: ${livro.getString("titulo")} / " +
                    "AUTOR: ${livro.getString("autor")} / " +
                    "USUÁRIO: ${livro.getString("usuario")}"

            binding.txtInfoLivro.text = infoLivro
        }


    }
}