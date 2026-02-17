package ifp.android.pastillero

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ifp.android.pastillero.databinding.ActivityMedicamentosRegistradosBinding

class MedicamentosRegistrados : AppCompatActivity() {

    private lateinit var binding: ActivityMedicamentosRegistradosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityMedicamentosRegistradosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val medicamentosDias = cargarMedicamentosDias(this).toMutableList()
        if (medicamentosDias.isEmpty()) {
            medicamentosDias.addAll(
                listOf(
                    "📅 Paracetamol\nCada 2 días",
                    "📅 Ibuprofeno\nCada 1 día"
                )
            )
        }

        val medicamentoHoras = cargarMedicamentosHoras(this).toMutableList()
        if (medicamentoHoras.isEmpty()) {
            Toast.makeText(this, "No hay medicamentos registrados", Toast.LENGTH_SHORT).show()

            medicamentoHoras.addAll(
                listOf(
                    "💊 Aspirina\nCada 8 horas\nPróxima dosis: en 3h 20m",
                    "💊 Paracetamol\nCada 6 horas\nPróxima dosis: en 1h 10m",
                    "💊 Ibuprofeno\nCada 12 horas\nPróxima dosis: en 8h 45m"
                )
            )
        }

        val adapterDias = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            medicamentosDias
        )
        binding.listViewRegistro.adapter = adapterDias

        val adapterHoras = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            medicamentoHoras
        )
        binding.listViewRegistroHoras.adapter = adapterHoras

        // BORRAR MEDICAMENTO HORAS
        binding.listViewRegistroHoras.setOnItemLongClickListener { _, _, position, _ ->

            val item = medicamentoHoras[position]
            val nombre = item.substringAfter("⏰ ").substringBefore("\n")

            AlertDialog.Builder(this)
                .setTitle("Eliminar medicamento")
                .setMessage("¿Quieres eliminar este medicamento?")
                .setPositiveButton("Sí") { _, _ ->

                    cancelarAlarma(nombre)
                    borrarMedicamentoHoras(nombre)

                    medicamentoHoras.removeAt(position)
                    adapterHoras.notifyDataSetChanged()

                    Toast.makeText(this, "Medicamento eliminado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()

            true
        }

        // BORRAR MEDICAMENTO DIAS
        binding.listViewRegistro.setOnItemLongClickListener { _, _, position, _ ->

            val item = medicamentosDias[position]
            val nombre = item.substringAfter("📅 ").substringBefore("\n")

            AlertDialog.Builder(this)
                .setTitle("Eliminar medicamento")
                .setMessage("¿Quieres eliminar este medicamento?")
                .setPositiveButton("Sí") { _, _ ->

                    borrarMedicamentoDias(nombre)

                    medicamentosDias.removeAt(position)
                    adapterDias.notifyDataSetChanged()

                    Toast.makeText(this, "Medicamento eliminado", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancelar", null)
                .show()

            true
        }


        binding.btnRegistrotoMain.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun cargarMedicamentosDias(context: Context): List<String> {
        val prefs = context.getSharedPreferences("meds_dias_prefs", Context.MODE_PRIVATE)
        val lista = mutableListOf<String>()

        for ((nombre, valor) in prefs.all) {
            val partes = valor.toString().split("|")
            val intervalo = partes[0].toInt()

            lista.add("📅 $nombre\nCada $intervalo días")
        }
        return lista
    }

    private fun cargarMedicamentosHoras(context: Context): List<String> {
        val prefs = context.getSharedPreferences("meds_prefs", Context.MODE_PRIVATE)
        val lista = mutableListOf<String>()

        for ((nombre, valor) in prefs.all) {
            val partes = valor.toString().split("|")

            val intervalo = partes[0].toInt()
            val startTime = partes[1].toLong()
            val proximaDosis = calcularProximaDosis(startTime, intervalo)

            lista.add(
                "⏰ $nombre\n" +
                        "Cada $intervalo horas\n" +
                        "Próxima dosis: ${formatearTiempo(proximaDosis)}"
            )
        }
        return lista
    }

    private fun cancelarAlarma(nombre: String) {
        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("Pastilla", nombre)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            nombre.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(AlarmManager::class.java)
        alarmManager?.cancel(pendingIntent)
    }

    private fun borrarMedicamentoHoras(nombre: String) {
        val prefs = getSharedPreferences("meds_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove(nombre).apply()
    }

    private fun borrarMedicamentoDias(nombre: String) {
        val prefs = getSharedPreferences("meds_dias_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove(nombre).apply()
    }


    private fun calcularProximaDosis(startTime: Long, intervaloHoras: Int): Long {
        val intervaloMs = intervaloHoras * 60 * 60 * 1000L
        val ahora = System.currentTimeMillis()
        val ciclos = (ahora - startTime) / intervaloMs
        return startTime + (ciclos + 1) * intervaloMs
    }

    private fun formatearTiempo(timeMillis: Long): String {
        val diff = timeMillis - System.currentTimeMillis()

        if (diff <= 0) return "ahora"

        val horas = diff / (1000 * 60 * 60)
        val minutos = (diff / (1000 * 60)) % 60

        return "en ${horas}h ${minutos}m"
    }
}
