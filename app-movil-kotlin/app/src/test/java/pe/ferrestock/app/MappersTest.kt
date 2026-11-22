package pe.ferrestock.app

import org.junit.Assert.assertEquals
import org.junit.Test
import pe.ferrestock.app.data.mapper.aVista
import pe.ferrestock.app.data.mapper.formatearFecha
import pe.ferrestock.app.data.remote.dto.ProductoDto
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MappersTest {

    @Test
    fun decimalComoTextoSeMapeaCorrectamente() {
        val dtoValido = ProductoDto(
            id = 1,
            codigo = "P001",
            nombre = "Martillo",
            precio = "32.50",
            stock = 10,
            stockMinimo = 2
        )
        val vistaValida = dtoValido.aVista()
        assertEquals(32.5, vistaValida.precio, 0.001)

        val dtoInvalido = ProductoDto(
            id = 2,
            codigo = "P002",
            nombre = "Clavo",
            precio = "abc",
            stock = 100,
            stockMinimo = 20
        )
        val vistaInvalida = dtoInvalido.aVista()
        assertEquals(0.0, vistaInvalida.precio, 0.001)
    }

    @Test
    fun formatearFechaFormateaCorrectamenteHoyYAyer() {
        val ahora = LocalDateTime.now()
        val isoHoy = ahora.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val resultadoHoy = formatearFecha(isoHoy)
        val horaEsperada = ahora.format(DateTimeFormatter.ofPattern("HH:mm"))
        assertEquals("Hoy $horaEsperada", resultadoHoy)

        val ayer = ahora.minusDays(1)
        val isoAyer = ayer.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val resultadoAyer = formatearFecha(isoAyer)
        val horaAyer = ayer.format(DateTimeFormatter.ofPattern("HH:mm"))
        assertEquals("Ayer $horaAyer", resultadoAyer)
    }
}
