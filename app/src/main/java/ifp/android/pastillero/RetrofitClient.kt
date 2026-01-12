package ifp.android.pastillero

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

// objeto que va a conectarse con la api de CIMA (Centro de Investigacion de Medicamentos de la AEMPS, Agencia Española de Medicamentos y Productos Sanitarios)
object RetrofitClient {

    // URL de CIMA
    private const val cimaURL = "https://cima.aemps.es/cima/rest/"

    // conexión con la API de CIMA
    val cimaAPI: CimaInterface by lazy {
        Retrofit.Builder()
            .baseUrl(cimaURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CimaInterface::class.java)
    }
}