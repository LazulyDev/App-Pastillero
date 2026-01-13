package ifp.android.pastillero

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import ifp.android.pastillero.databinding.ActivityNuevoMedBinding
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.log

private lateinit var binding: ActivityNuevoMedBinding

/* ACCIONES DE LA ACTIVIDAD:
    1. Accede a la cámara para leer un código de barras.
    2. lee el código de barras.
    3. envía el número de registro del medicamento al CIMA (Centro de Investigacion de Medicamentos de la AEMPS, Agencia Española de Medicamentos y Productos Sanitarios)
    4. añade el medicamento a una lista para poder acceder a la información.
    5. muestra la información del medicamento y solicita al usuario el tiempo entre consumiciones.
    6. permite guardar la info de la toma de las dosis en el calendario
* */
class NuevoMed : AppCompatActivity() {

    private var nombreMed: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nuevo_med)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityNuevoMedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // botón para acceder a la cámara.
        binding.btnAbrirAcmara.setOnClickListener {
            accesoCamara()
        }

        binding.btnAnadirMed.setOnClickListener {
            val intervalo = binding.edtIntervalo.text.toString().toInt()
            programarDosis(this, nombreMed, intervalo)
        }
    }

    // funcion para poder usar la cámara para leer un código.
    fun accesoCamara(){

    // ajustes para la cámara para poder leer códigos.
    val configuracionesCamara = GmsBarcodeScannerOptions.Builder().setBarcodeFormats(
        Barcode.FORMAT_ALL_FORMATS
    ).enableAutoZoom().build()

    // lee el código del medicamento y ejecuta la funcion que verifica que el medicamento sea real y apto para el consumo.
    val scanner = GmsBarcodeScanning.getClient(this)
    scanner.startScan()
        .addOnSuccessListener { // extrae el código de barras del medicamento.
                barcode -> val codigoEnRaw: String? = barcode.rawValue
            val codigoRegistro = codigoEnRaw.toString().substring(6, 12)

            Log.d("TAG_FILTRO", "el código de registro es: ${codigoRegistro}")

            // llamada a la función que verifica que el medicamento es un medicamento correcto.
            lifecycleScope.launch {
                val infoMed = infoMedicamento(codigoRegistro)
                binding.tvPruebaBarcode.text = infoMed
            }
            }
            .addOnFailureListener { exception -> Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }

        }

    // función para la verificación de un medicamento.
    suspend fun infoMedicamento (codigoMed: String): String{

        // asegura de que no entren valores nulos o vacíos
        if (codigoMed.isNullOrEmpty()){
            Toast.makeText(this, "no hay código que leer", Toast.LENGTH_SHORT).show()
        }

        // solicita a CIMA la información del medicamento y la returna.
        try {
            val respuestaCIMA = RetrofitClient.cimaAPI.getMedicamento(codigoMed)
            nombreMed = respuestaCIMA.listaMedicamentos[0].nombre.toString()
            return "nombre ${respuestaCIMA.listaMedicamentos[0].nombre} \n fabricante ${respuestaCIMA.listaMedicamentos[0].fabricante} " +
                    "\n Número de Registro ${respuestaCIMA.listaMedicamentos[0].numeroRegistro} " +
                    "\n prescripcion: ${respuestaCIMA.listaMedicamentos[0].prescripcion}"

        } catch (e: Exception){ // gestión de los errores.
            Toast.makeText(this, "Medicamento no encontrado", Toast.LENGTH_SHORT).show()
            Log.e("TAG_FILTRO", "Mensaje de error: ${e.message}")
            return "No es un medicamento"
        }
        return ""
    }

    // funcion para proramar las dosis en el calendario
    fun programarDosis(context: Context, nombreMed: String, intervalo: Int){

        val tomasDiarias = 24/intervalo

        for (i in 0 until tomasDiarias){

            val horaToma = Calendar.getInstance().apply {
                add(Calendar.HOUR_OF_DAY, i * intervalo)
            }

            val intento = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, "toma de ${nombreMed}")
                putExtra(CalendarContract.Events.DESCRIPTION, "Dosis programada de ${nombreMed} cada ${intervalo} horas")
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, horaToma.timeInMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, horaToma.timeInMillis + 10 * 60 * 1000)
                putExtra(CalendarContract.Events.RRULE, "FREQ=DAILY;INTERVAL=1")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intento)
        }
    }
}