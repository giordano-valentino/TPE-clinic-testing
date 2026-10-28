package test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import persistencia.BaseDeDatos;

/**
 * Suite de pruebas dedicada al patrón Singleton y ciclo de vida de la conexión
 * de la clase BaseDeDatos.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseDeDatosSingletonTest {

    @Test
    @Order(1)
    void testPatronSingletonRetornaMismaInstancia() {
        BaseDeDatos instancia1 = BaseDeDatos.getInstancia();
        BaseDeDatos instancia2 = BaseDeDatos.getInstancia();

        assertNotNull(instancia1, "La instancia de BaseDeDatos no debe ser nula.");
        assertSame(instancia1, instancia2, "El método getInstancia() debe devolver siempre la misma instancia en memoria.");
    }

    @Test
    @Order(2)
    void testConexionActivaYValida() throws SQLException {
        BaseDeDatos bd = BaseDeDatos.getInstancia();
        Connection conexion = bd.getConexion();

        assertNotNull(conexion, "La conexión a la base de datos no debe ser nula.");
        assertFalse(conexion.isClosed(), "La conexión debe encontrarse abierta tras instanciar la clase.");
        assertTrue(conexion.isValid(2), "La conexión debe ser válida y comunicarse con MySQL.");
    }

    @Test
    @Order(3)
    void testSiCierroLaConexionPuedoVolverAIniciarla() throws SQLException {
        BaseDeDatos bd = BaseDeDatos.getInstancia();
        bd.cerrarConexion();

        BaseDeDatos bd_2 = BaseDeDatos.getInstancia();

        Connection conexion = bd_2.getConexion();
        assertNotNull(conexion, "La conexion con la base de datos no debe ser nula");
        assertFalse(conexion.isClosed(), "La conexión debe encontrarse abierta tras instanciar la clase.");
    }
}
