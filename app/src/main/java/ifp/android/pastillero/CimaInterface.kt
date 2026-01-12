package ifp.android.pastillero


import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

// interfaz que Retrofit va a usar para conectarse a la API de CIMA con el que va a extraer la informaio
interface CimaInterface {
    @GET("medicamentos")
    suspend fun getMedicamento(@Query("cn") nregistro: String): RespuestaCima
}