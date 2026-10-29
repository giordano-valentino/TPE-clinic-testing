package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import persistencia.AsociadoDTO;

class AsociadoDTOTest {

    @Test
    void testConstructorPorDefecto() {
        AsociadoDTO asociado = new AsociadoDTO();

        assertNull(asociado.getNombre());
        assertNull(asociado.getApellido());
        assertNull(asociado.getDni());
        assertNull(asociado.getCiudad());
        assertNull(asociado.getCalle());
        assertEquals(0, asociado.getNumero());
        assertNull(asociado.getTelefono());
    }

    @Test
    void testConstructorParametrizado() {
        AsociadoDTO asociado = new AsociadoDTO(
                "Juan",
                "Perez",
                "12345678",
                "Mar del Plata",
                "Independencia",
                1234,
                "2235555555"
        );

        assertEquals("Juan", asociado.getNombre());
        assertEquals("Perez", asociado.getApellido());
        assertEquals("12345678", asociado.getDni());
        assertEquals("Mar del Plata", asociado.getCiudad());
        assertEquals("Independencia", asociado.getCalle());
        assertEquals(1234, asociado.getNumero());
        assertEquals("2235555555", asociado.getTelefono());
    }

    @Test
    void testSetNombre() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setNombre("Juan");

        assertEquals("Juan", asociado.getNombre());
    }

    @Test
    void testSetApellido() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setApellido("Perez");

        assertEquals("Perez", asociado.getApellido());
    }

    @Test
    void testSetDni() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setDni("12345678");

        assertEquals("12345678", asociado.getDni());
    }

    @Test
    void testSetCiudad() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setCiudad("Mar del Plata");

        assertEquals("Mar del Plata", asociado.getCiudad());
    }

    @Test
    void testSetCalle() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setCalle("Independencia");

        assertEquals("Independencia", asociado.getCalle());
    }

    @Test
    void testSetNumero() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setNumero(1234);

        assertEquals(1234, asociado.getNumero());
    }

    @Test
    void testSetTelefono() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setTelefono("2235555555");

        assertEquals("2235555555", asociado.getTelefono());
    }

    @Test
    void testSetNumeroAceptaUno() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setNumero(1);

        assertEquals(1, asociado.getNumero());
    }
    
    @Test
    void testSetNumeroAceptaCero() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setNumero(0);

        assertEquals(0, asociado.getNumero());
    }
    
    @Test
    void testAssertsHabilitados() {
        assertThrows(AssertionError.class, () -> {
            assert false;
        }, "Los assert estan deshabilitados: ejecutar la JVM con -ea");
    }

    /* String vacio: la validacion solo controla != null, por lo que "" se acepta.
     * Se deja documentado como observacion para el informe */
    @Test
    void testSetNombreAceptaVacio() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setNombre("");

        assertEquals("", asociado.getNombre());
    }

    @Test
    void testSetApellidoAceptaVacio() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setApellido("");

        assertEquals("", asociado.getApellido());
    }

    @Test
    void testSetDniAceptaVacio() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setDni("");

        assertEquals("", asociado.getDni());
    }

    @Test
    void testSetCiudadAceptaVacio() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setCiudad("");

        assertEquals("", asociado.getCiudad());
    }

    @Test
    void testSetCalleAceptaVacio() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setCalle("");

        assertEquals("", asociado.getCalle());
    }

    @Test
    void testSetTelefonoAceptaVacio() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setTelefono("");

        assertEquals("", asociado.getTelefono());
    }
    
    
    /* Un valor válido debe reemplazar al valor cargado previamente */
    @Test
    void testSetNombreReemplazaValorPrevio() {
        AsociadoDTO asociado = new AsociadoDTO();
        asociado.setNombre("Juan");

        asociado.setNombre("Pedro");

        assertEquals("Pedro", asociado.getNombre());
    }

    @Test
    void testSetApellidoReemplazaValorPrevio() {
        AsociadoDTO asociado = new AsociadoDTO();
        asociado.setApellido("Perez");

        asociado.setApellido("Gomez");

        assertEquals("Gomez", asociado.getApellido());
    }

    @Test
    void testSetDniReemplazaValorPrevio() {
        AsociadoDTO asociado = new AsociadoDTO();
        asociado.setDni("12345678");

        asociado.setDni("87654321");

        assertEquals("87654321", asociado.getDni());
    }

    @Test
    void testSetCiudadReemplazaValorPrevio() {
        AsociadoDTO asociado = new AsociadoDTO();
        asociado.setCiudad("Mar del Plata");

        asociado.setCiudad("Balcarce");

        assertEquals("Balcarce", asociado.getCiudad());
    }

    @Test
    void testSetCalleReemplazaValorPrevio() {
        AsociadoDTO asociado = new AsociadoDTO();
        asociado.setCalle("Independencia");

        asociado.setCalle("Colon");

        assertEquals("Colon", asociado.getCalle());
    }

    @Test
    void testSetNumeroReemplazaValorPrevio() {
        AsociadoDTO asociado = new AsociadoDTO();
        asociado.setNumero(1234);

        asociado.setNumero(1000);

        assertEquals(1000, asociado.getNumero());
    }
    
    @Test
    void testSetNumeroCeroReemplazaValorPrevio() {
        AsociadoDTO asociado = new AsociadoDTO();
        asociado.setNumero(1234);

        asociado.setNumero(0);

        assertEquals(0, asociado.getNumero());
    }

    @Test
    void testSetTelefonoReemplazaValorPrevio() {
        AsociadoDTO asociado = new AsociadoDTO();
        asociado.setTelefono("2235555555");

        asociado.setTelefono("2234444444");

        assertEquals("2234444444", asociado.getTelefono());
    }
    
}
