package ifp.android.pastillero

import com.google.gson.annotations.SerializedName

// clase de datos que va a guardar la información del medicamento escaneado

// 1. El contenedor (lo que recibe Retrofit)
data class RespuestaCima(
    @SerializedName("resultados") val listaMedicamentos: List<Medicamentos>
)

// 2. El objeto con los datos reales
data class Medicamentos(
    @SerializedName("nregistro") val numeroRegistro: String?,
    @SerializedName("nombre") val nombre: String?,
    @SerializedName("labtitular") val fabricante: String?,
    @SerializedName("cpresc") val prescripcion: String
)