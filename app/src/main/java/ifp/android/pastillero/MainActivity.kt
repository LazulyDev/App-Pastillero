package ifp.android.pastillero

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ifp.android.pastillero.databinding.ActivityAboutUsBinding
import ifp.android.pastillero.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // botón para abrir la actividad que permite añadir cuevos medicamentos a la lista
        binding.btnNuevoMed.setOnClickListener {
            val intento = Intent(this, NuevoMed::class.java)
            startActivity(intento)

            Toast.makeText(this, "añadir nuevos medicamentos", Toast.LENGTH_SHORT).show() // TODO: poner String
        }

        // botón about us
        binding.btnAboutUs.setOnClickListener{
            val intento = Intent(this, AboutUs::class.java)
            startActivity(intento)
            Log.i("INICIO_ACTIVIDAD", "Iniciada la actividad About us")
            Toast.makeText(this, "About us", Toast.LENGTH_SHORT).show()
        }
    }
}