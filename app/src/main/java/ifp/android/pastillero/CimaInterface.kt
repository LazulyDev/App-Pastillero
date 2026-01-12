package ifp.android.pastillero

import retrofit2.http.GET
import retrofit2.http.Query

interface CimaInterface {
    @GET("Medicamentos")
    suspend fun getMedicamento(@Query("nregistro") nregistro: String): Medicamentos
}