package ifp.android.pastillero

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import ifp.android.pastillero.databinding.ActivityNuevoMedBinding

private lateinit var binding: ActivityNuevoMedBinding
class NuevoMed : AppCompatActivity() {
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

        // botón para acceder a la cámara
        binding.btnAbrirAcmara.setOnClickListener {
            // TODO: funcion para acceder a la cámara
        }
    }

    // funcion para poder usar la cámara para leer un código
    fun accesoCamara(){

        // ajustes para la cámara para poder leer códigos
        val configuracionesCamara = GmsBarcodeScannerOptions.Builder().setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
        ).enableAutoZoom().build()

        val scanner = GmsBarcodeScanning.getClient(this)
        scanner.startScan()
            .addOnSuccessListener {
                barcode -> val codigoEnRaw: String? = barcode.rawValue
            }
            .addOnFailureListener { exception -> Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }

    }

    // función para la verificación de un medicamentoç
    fun esMedicamentoReal (codigoMed: String): Boolean{
        if (codigoMed == null || codigoMed.length <= 0){
            Toast.makeText(this, "no hay código que leer", Toast.LENGTH_SHORT).show()
        } else {
            
        }

        return false
    }
}