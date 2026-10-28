package test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import persistencia.AsociadoDTO;
import persistencia.BaseDeDatos;

/**
 * Suite de pruebas unitarias y de integración para la clase BaseDeDatos.
 * Verifica:
 * 
 * 1. Creación automática de la tabla Asociados.
 * 2. Inserción correcta de los datos semilla.
 * 3. Operaciones CRUD directas (ejecutarInsert, ejecutarConsulta, ejecutarActualizacion, ejecutarDelete).
 * 4. Manejo de excepciones de SQL (violaciones de clave única y sintaxis).
 */

public class BaseDeDatosTest {

    private BaseDeDatos bd;

    @BeforeEach
    void setUp() {
        bd = BaseDeDatos.getInstancia();
        bd.iniciarBD();
    }

    
    // 1. CREACIÓN DE TABLA Y ESTRUCTURA
    @Test
    void testCreacionTablaAsociados() throws SQLException {
        Connection conexion = bd.getConexion();
        DatabaseMetaData meta = conexion.getMetaData();

        // Verificar que la tabla 'Asociados' exista en el catálogo o esquema actual
        ResultSet rs = meta.getTables(null, null, "Asociados", new String[] {"TABLE"});
        boolean tablaExiste = rs.next();
        rs.close();

        assertTrue(tablaExiste, "La tabla 'Asociados' debe haber sido creada automáticamente por iniciarBD().");
    }

    // 2. DATOS SEMILLA
    @Test
    void testDatosSemillaCantidadExacta() throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM Asociados;";
        ResultSet rs = bd.ejecutarConsulta(query, -1);

        assertNotNull(rs);
        assertTrue(rs.next(), "Debe haber una fila con el resultado de COUNT.");
        int total = rs.getInt("total");
        rs.close();

        assertEquals(20, total, "La semilla debe contener exactamente 20 registros iniciales.");
    }

    @Test
    void testDatosSemillaContenidoPrimerRegistro() throws SQLException {
        String query = "SELECT * FROM Asociados WHERE id = ? ;";
        ResultSet rs = bd.ejecutarConsulta(query, 1);

        assertNotNull(rs);
        assertTrue(rs.next(), "El registro semilla con id = 1 debe existir.");
        assertEquals("Juan", rs.getString("nombre"));
        assertEquals("State", rs.getString("apellido"));
        assertEquals("12345678", rs.getString("DNI"));
        assertEquals("Buenos Aires", rs.getString("calle"));
        assertEquals(123, rs.getInt("numero"));
        assertEquals("Mar del Plata", rs.getString("ciudad"));
        assertEquals("2235551234", rs.getString("telefono"));
        rs.close();
    }

    // 3. OPERACIONES CRUD


    @Test
    void testCRUD_Create_ejecutarInsert() throws SQLException {
        AsociadoDTO nuevo = new AsociadoDTO("Lionel", "Messi", "10101010", "Rosario", "Lavalle", 1010, "3415555555");
        String insertQuery = "INSERT INTO Asociados (nombre, apellido, DNI, calle, numero, ciudad, telefono) VALUES (?, ?, ?, ?, ?, ?, ?);";

        bd.ejecutarInsert(insertQuery, nuevo);

        // Verificar que ahora hay 21 registros
        ResultSet rsTotal = bd.ejecutarConsulta("SELECT COUNT(*) AS total FROM Asociados;", -1);
        assertTrue(rsTotal.next());
        assertEquals(21, rsTotal.getInt("total"), "El número de registros debe ser 21 luego de insertar.");
        rsTotal.close();

        // Verificar los datos insertados
        ResultSet rsNuevo = bd.ejecutarConsulta("SELECT * FROM Asociados WHERE DNI = '10101010';", -1);
        assertTrue(rsNuevo.next(), "El nuevo asociado debe poder consultarse en la base de datos.");
        assertEquals("Lionel", rsNuevo.getString("nombre"));
        assertEquals("Messi", rsNuevo.getString("apellido"));
        assertEquals("Lavalle", rsNuevo.getString("calle"));
        assertEquals(1010, rsNuevo.getInt("numero"));
        rsNuevo.close();
    }

    @Test
    void testCRUD_Read_ejecutarConsultaSinParametro() throws SQLException {
        String query = "SELECT * FROM Asociados;";
        ResultSet rs = bd.ejecutarConsulta(query, -1);

        assertNotNull(rs);
        int contador = 0;
        while (rs.next()) {
            contador++;
        }
        rs.close();

        assertEquals(20, contador, "La consulta general sin parámetros debe iterar los 20 registros iniciales.");
    }

    @Test
    void testCRUD_Read_ejecutarConsultaConParametroId() throws SQLException {
        String query = "SELECT * FROM Asociados WHERE id = ? ;";
        ResultSet rs = bd.ejecutarConsulta(query, 2);

        assertNotNull(rs);
        assertTrue(rs.next(), "Debe existir un registro con id = 2.");
        assertEquals("Maria", rs.getString("nombre"));
        assertEquals("Template", rs.getString("apellido"));
        assertEquals("87654321", rs.getString("DNI"));
        rs.close();
    }

    @Test
    void testCRUD_Read_idInexistenteRetornaVacio() throws SQLException {
        String query = "SELECT * FROM Asociados WHERE id = ? ;";
        ResultSet rs = bd.ejecutarConsulta(query, 9999);

        assertNotNull(rs);
        assertFalse(rs.next(), "La consulta por un ID inexistente no debe retornar ninguna fila.");
        rs.close();
    }

    @Test
    void testCRUD_Update_ejecutarActualizacion() throws SQLException {
        // En BaseDeDatos.ejecutarActualizacion el id se encuentra fijado a 1 (stmt.setInt(8, 1))
        AsociadoDTO datosActualizados = new AsociadoDTO("JuanActualizado", "StateActualizado", "12345678", "Mar del Plata", "San Juan", 500, "2234441111");
        String updateQuery = "UPDATE Asociados SET nombre = ?, apellido = ?, DNI = ?, calle = ?, numero = ?, ciudad = ?, telefono = ? WHERE id = ? ;";

        bd.ejecutarActualizacion(updateQuery, datosActualizados);

        // Comprobar que los cambios impactaron en el registro id = 1
        ResultSet rs = bd.ejecutarConsulta("SELECT * FROM Asociados WHERE id = ? ;", 1);
        assertTrue(rs.next(), "El registro id = 1 debe existir.");
        assertEquals("JuanActualizado", rs.getString("nombre"));
        assertEquals("StateActualizado", rs.getString("apellido"));
        assertEquals("San Juan", rs.getString("calle"));
        assertEquals(500, rs.getInt("numero"));
        assertEquals("2234441111", rs.getString("telefono"));
        rs.close();
    }

    @Test
    void testCRUD_Update_asociadoInexistenteNoModificaRegistros() throws SQLException {
        // Intentamos actualizar filtrando por un DNI inexistente
        AsociadoDTO fantasma = new AsociadoDTO("Inexistente", "Fantasma", "99999999", "Mar del Plata", "Calle", 100, "111");
        String updateQuery = "UPDATE Asociados SET nombre = ?, apellido = ?, DNI = ?, calle = ?, numero = ?, ciudad = ?, telefono = ? WHERE DNI = '00000000' AND id = ? ;";

        bd.ejecutarActualizacion(updateQuery, fantasma);

        // Verificar que Juan State (id 1) sigue intacto
        ResultSet rsJuan = bd.ejecutarConsulta("SELECT * FROM Asociados WHERE id = 1;", -1);
        assertTrue(rsJuan.next());
        assertEquals("Juan", rsJuan.getString("nombre"), "Ningún asociado existente debió ser modificado.");
        rsJuan.close();
    }

    @Test
    void testCRUD_Delete_ejecutarDelete() throws SQLException {
        String dniABorrar = "12345678"; // DNI de Juan State
        String deleteQuery = "DELETE FROM Asociados WHERE DNI = ? ;";

        bd.ejecutarDelete(deleteQuery, dniABorrar);

        // Verificar que el total baja a 19
        ResultSet rsTotal = bd.ejecutarConsulta("SELECT COUNT(*) AS total FROM Asociados;", -1);
        assertTrue(rsTotal.next());
        assertEquals(19, rsTotal.getInt("total"), "Luego de borrar, el total debe ser 19.");
        rsTotal.close();

        // Verificar que el registro ya no se encuentra
        ResultSet rsBorrado = bd.ejecutarConsulta("SELECT * FROM Asociados WHERE DNI = '12345678';", -1);
        assertFalse(rsBorrado.next(), "El registro eliminado no debe encontrarse en la base.");
        rsBorrado.close();
    }

    @Test
    void testCRUD_Delete_dniInexistenteNoAlteraRegistros() throws SQLException {
        String deleteQuery = "DELETE FROM Asociados WHERE DNI = ? ;";

        // Intentar borrar un DNI inexistente
        bd.ejecutarDelete(deleteQuery, "99999999");

        // Verificar que el total sigue siendo 20
        ResultSet rsTotal = bd.ejecutarConsulta("SELECT COUNT(*) AS total FROM Asociados;", -1);
        assertTrue(rsTotal.next());
        assertEquals(20, rsTotal.getInt("total"), "El total de registros debe mantenerse en 20.");
        rsTotal.close();
    }

    // 4. EXCEPCIONES DE SQL
    @Test
    void testExcepcionPorDniDuplicado() {
        // El DNI 12345678 ya existe en la semilla
        AsociadoDTO duplicado = new AsociadoDTO("Otro", "Usuario", "12345678", "Mar del Plata", "Belgrano", 100, "2230000000");
        String insertQuery = "INSERT INTO Asociados (nombre, apellido, DNI, calle, numero, ciudad, telefono) VALUES (?, ?, ?, ?, ?, ?, ?);";

        assertThrows(SQLException.class, () -> {
            bd.ejecutarInsert(insertQuery, duplicado);
        }, "Debe lanzar SQLException por violar la restricción UNIQUE del campo DNI.");
    }

    @Test
    void testExcepcionPorConsultaSqlInvalida() {
        String consultaInvalida = "SELECT * FROM TablaQueNoExiste;";

        assertThrows(SQLException.class, () -> {
            bd.ejecutarConsulta(consultaInvalida, -1);
        }, "Debe lanzar SQLException al ejecutar una consulta SQL con errores o tablas inexistentes.");
    }
}
