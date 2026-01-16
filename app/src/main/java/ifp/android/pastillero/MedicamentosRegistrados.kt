package ifp.android.pastillero

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import android.content.Context
import android.content.Intent
import ifp.android.pastillero.databinding.ActivityMedicamentosRegistradosBinding

private lateinit var binding: ActivityMedicamentosRegistradosBinding
class MedicamentosRegistrados : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_medicamentos_registrados)
        binding = ActivityMedicamentosRegistradosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listView = findViewById<ListView>(R.id.listViewRegistro)

        val medicamentos = cargarMedicamentos(this)

        if (medicamentos.isEmpty()) {
            Toast.makeText(this, "No hay medicamentos registrados", Toast.LENGTH_SHORT).show()
        }

        val sampleMeds = listOf(
            "Aspirina - cada 8 horas",
            "Paracetamol - cada 6 horas",
            "Ibuprofeno - cada 12 horas"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            sampleMeds
        )

        listView.adapter = adapter

        binding.btnRegistrotoMain.setOnClickListener {
            val intento = Intent(this, MainActivity::class.java)
            startActivity(intento)
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun cargarMedicamentos(context: Context): List<String> {
        val prefs = context.getSharedPreferences("meds_prefs", Context.MODE_PRIVATE)
        val lista = mutableListOf<String>()

        for ((nombre, valor) in prefs.all) {
            val partes = valor.toString().split("|")

            if (partes.size == 2) {
                val intervalo = partes[0]
                val startTime = partes[1].toLong()

                val proximaDosis = calcularProximaDosis(startTime, intervalo.toInt())

                lista.add(
                    "💊 $nombre\n" +
                            "Cada $intervalo horas\n" +
                            "Próxima dosis: ${formatearTiempo(proximaDosis)}"
                )
            }
        }

        return lista
    }

    private fun calcularProximaDosis(startTime: Long, intervaloHoras: Int): Long {
        val intervaloMs = intervaloHoras * 60 * 60 * 1000L
        val ahora = System.currentTimeMillis()

        val ciclos = (ahora - startTime) / intervaloMs
        return startTime + (ciclos + 1) * intervaloMs
    }

    private fun formatearTiempo(timeMillis: Long): String {
        val diff = timeMillis - System.currentTimeMillis()

        val horas = diff / (1000 * 60 * 60)
        val minutos = (diff / (1000 * 60)) % 60

        return if (diff > 0) {
            "en ${horas}h ${minutos}m"
        } else {
            "ahora"
        }
    }
}