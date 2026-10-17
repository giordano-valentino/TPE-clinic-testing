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
    void testSetNombreNoAceptaNull() {
        AsociadoDTO asociado = new AsociadoDTO();

        assertThrows(AssertionError.class, () -> asociado.setNombre(null));
    }

    @Test
    void testSetApellidoNoAceptaNull() {
        AsociadoDTO asociado = new AsociadoDTO();

        assertThrows(AssertionError.class, () -> asociado.setApellido(null));
    }

    @Test
    void testSetDniNoAceptaNull() {
        AsociadoDTO asociado = new AsociadoDTO();

        assertThrows(AssertionError.class, () -> asociado.setDni(null));
    }

    @Test
    void testSetCiudadNoAceptaNull() {
        AsociadoDTO asociado = new AsociadoDTO();

        assertThrows(AssertionError.class, () -> asociado.setCiudad(null));
    }

    @Test
    void testSetCalleNoAceptaNull() {
        AsociadoDTO asociado = new AsociadoDTO();

        assertThrows(AssertionError.class, () -> asociado.setCalle(null));
    }
    /*
    @Test
    void testSetNumeroNoAceptaCero() {
        AsociadoDTO asociado = new AsociadoDTO();

        assertThrows(AssertionError.class, () -> asociado.setNumero(0));
    }
    Reemplazo por el de abajo, el codigo original rechaza el 0, cuando lo tendria que aceptar */
    @Test
    void testSetNumeroAceptaCero() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setNumero(0);

        assertEquals(0, asociado.getNumero());
    }

    @Test
    void testSetNumeroNoAceptaNegativos() {
        AsociadoDTO asociado = new AsociadoDTO();

        assertThrows(AssertionError.class, () -> asociado.setNumero(-1));
    }

    @Test
    void testSetTelefonoNoAceptaNull() {
        AsociadoDTO asociado = new AsociadoDTO();

        assertThrows(AssertionError.class, () -> asociado.setTelefono(null));
    }
    
    /* Gian: se prueba que el constructor no acepte ningun parametro en null*/
    
    @Test
    void testConstructorNoAceptaNombreNull() {
        assertThrows(AssertionError.class, () -> new AsociadoDTO(
                null, "Perez", "12345678", "Mar del Plata", "Independencia", 1234, "2235555555"));
    }

    @Test
    void testConstructorNoAceptaApellidoNull() {
        assertThrows(AssertionError.class, () -> new AsociadoDTO(
                "Juan", null, "12345678", "Mar del Plata", "Independencia", 1234, "2235555555"));
    }

    @Test
    void testConstructorNoAceptaDniNull() {
        assertThrows(AssertionError.class, () -> new AsociadoDTO(
                "Juan", "Perez", null, "Mar del Plata", "Independencia", 1234, "2235555555"));
    }

    @Test
    void testConstructorNoAceptaCiudadNull() {
        assertThrows(AssertionError.class, () -> new AsociadoDTO(
                "Juan", "Perez", "12345678", null, "Independencia", 1234, "2235555555"));
    }

    @Test
    void testConstructorNoAceptaCalleNull() {
        assertThrows(AssertionError.class, () -> new AsociadoDTO(
                "Juan", "Perez", "12345678", "Mar del Plata", null, 1234, "2235555555"));
    }

    @Test
    void testConstructorNoAceptaNumeroNegativo() {
        assertThrows(AssertionError.class, () -> new AsociadoDTO(
                "Juan", "Perez", "12345678", "Mar del Plata", "Independencia", -1, "2235555555"));
    }

    @Test
    void testConstructorNoAceptaTelefonoNull() {
        assertThrows(AssertionError.class, () -> new AsociadoDTO(
                "Juan", "Perez", "12345678", "Mar del Plata", "Independencia", 1234, null));
    }
    /* fin Gian: se prueba que el constructor no acepte ningun parametro en null*/
    
    /* Gian: prueba de valores extremos en el numero. El 1 quizas tiene sentido,
     * los demas no se si vale la pena*/
    @Test
    void testSetNumeroAceptaUno() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setNumero(1);

        assertEquals(1, asociado.getNumero());
    }

    /*  Preguntar en la practica
    @Test
    void testSetNumeroAceptaMaximo() {
        AsociadoDTO asociado = new AsociadoDTO();

        asociado.setNumero(Integer.MAX_VALUE);

        assertEquals(Integer.MAX_VALUE, asociado.getNumero());
    }

    @Test
    void testSetNumeroNoAceptaMinimo() {
        AsociadoDTO asociado = new AsociadoDTO();

        assertThrows(AssertionError.class, () -> asociado.setNumero(Integer.MIN_VALUE));
    }
    */
    /* fin Gian: prueba de valores extremos en el numero. */
    
}
