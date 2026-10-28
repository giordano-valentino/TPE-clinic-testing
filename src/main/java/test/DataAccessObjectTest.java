package test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.util.Domicilio;
import persistencia.AsociadoDTO;
import persistencia.BaseDeDatos;
import persistencia.DataAccessObject;

/**
 * Suite de pruebas de integración para DataAccessObject (DAO).
 * Organizada según las operaciones CRUD (Alta, Lectura, Modificación, Eliminación),
 * Mapeo de DTOs, y Excepciones SQL / Precondiciones.
 */
public class DataAccessObjectTest {

    private DataAccessObject dao;

    @BeforeEach
    void setUp() {
        // Restablece la base de datos a su estado limpio con 20 registros semilla
        BaseDeDatos.getInstancia().iniciarBD();
        dao = new DataAccessObject();
    }

    // 1. TESTEO DE ALTA DE ASOCIADOS VIA DAO
    @Test
    void testGuardarNuevoAsociadoExitoso() throws SQLException {
        Domicilio domicilio = new Domicilio("Enrique Bochini", 1850, "Mar del Plata");
        Asociado nuevoAsociado = new Asociado("Valentino", "Giordano", "46632600", "2235964847", domicilio);

        // Creo el dao a partir de un asociado y luego deberia poder cargarlo sin problema
        dao.guardar(nuevoAsociado);

        // Verificar que el total de registros en la base pasó de 20 a 21
        ArrayList<AsociadoDTO> lista = dao.cargarBD();
        assertEquals(21, lista.size(), "La cantidad total debe ser 21 luego de guardar un nuevo asociado.");

        // Buscar el DTO persistido en la lista y corroborar sus datos
        AsociadoDTO asociado_cargado = null;
        for (AsociadoDTO dto : lista) {
            if ("46632600".equals(dto.getDni())) {
                asociado_cargado = dto;
                break;
            }
        }

        // Verificamos que todos nuestros datos cargados permanezcan igual y no hayan sufrido cambios
        assertNotNull(asociado_cargado, "El nuevo asociado debe estar presente en la lista recuperada de la base.");
        assertEquals("Valentino", asociado_cargado.getNombre());
        assertEquals("Giordano", asociado_cargado.getApellido());
        assertEquals("Enrique Bochini", asociado_cargado.getCalle());
        assertEquals(1850, asociado_cargado.getNumero());
        assertEquals("Mar del Plata", asociado_cargado.getCiudad());
        assertEquals("2235964847", asociado_cargado.getTelefono());
    }

    // 2. TESTEO DE LEER DATOS VIA DAO
    @Test
    void testCargarBDRecuperaTodosLosRegistrosSemilla() throws SQLException {
        ArrayList<AsociadoDTO> lista = dao.cargarBD();

        assertNotNull(lista, "La lista retornada por cargarBD() no debe ser nula.");
        assertEquals(20, lista.size(), "cargarBD() debe retornar exactamente los 20 registros semilla iniciales.");

        // Verificar datos del primer registro y verificar que no sufrio modificaciones
        AsociadoDTO primero = lista.get(0);
        assertEquals("Juan", primero.getNombre());
        assertEquals("State", primero.getApellido());
        assertEquals("12345678", primero.getDni());
        assertEquals("Buenos Aires", primero.getCalle());
        assertEquals(123, primero.getNumero());
        assertEquals("Mar del Plata", primero.getCiudad());
        assertEquals("2235551234", primero.getTelefono());
    }

    @Test
    // Si ponemos un limite a la cantidad de datos que nos traiga la BD el dao debe traer exactamente esa cantidad de asociados
    void testCargarConLimiteCinco() throws SQLException {
        ArrayList<AsociadoDTO> lista = dao.cargarConLimite(5);

        assertNotNull(lista);
        assertEquals(5, lista.size(), "cargarConLimite(5) debe devolver exactamente 5 elementos.");
    }

    @Test
    // Lo mismo que el caso anterior pero con limite de 1, deberia traernos a Juan State
    void testCargarConLimiteUno() throws SQLException {
        ArrayList<AsociadoDTO> lista = dao.cargarConLimite(1);

        assertNotNull(lista);
        assertEquals(1, lista.size(), "cargarConLimite(1) debe devolver un único elemento.");
        assertEquals("Juan", lista.get(0).getNombre());
    }

    @Test
    void testCargarConLimiteMayorAlTotal() throws SQLException {
        // Pedimos 50 asociados habiendo solo 20 en la base
        ArrayList<AsociadoDTO> lista = dao.cargarConLimite(50);

        assertNotNull(lista);
        assertEquals(20, lista.size(), "Pedir un límite mayor a la cantidad de filas debe retornar el total disponible sin errores.");
    }

    @Test
    void testCargarPorId() throws SQLException {
        // Pedimos cargar el asociado con id = 1 (Juan State)
        AsociadoDTO dto = dao.cargar(1);

        assertNotNull(dto);
        assertEquals("Juan", dto.getNombre());
    }
    
    @Test
    void testCargarPorIdInexistente() throws SQLException{
    	//Si cargamos por un ID inexistente, por ejemplo 999, deberia devolver null ya que no hay ningun resultado que cargar devuelto por la query
    	AsociadoDTO dto = dao.cargar(999);
    	
    	assertNull(dto);
    }
    
    // 3. TESTEO DE MODIFICAR DATOS VIA DAO
    @Test
    void testDemostrarInconsistenciaEnActualizar() throws SQLException {
        // Datos correspondientes a Maria Template (id = 2) modificados
        Domicilio nuevoDom = new Domicilio("Calle Modificada", 456, "Mar del Plata");
        Asociado mariaEditada = new Asociado("MariaModificada", "TemplateModificada", "87654321", "2238888888", nuevoDom);

        dao.actualizar(mariaEditada);

        // Debido que encontramos en el codigo una linea con en stmt.setInt(8, 1), el registro modificado fue el id = 1 (Juan State)
        ArrayList<AsociadoDTO> lista = dao.cargarBD();
        AsociadoDTO registro1 = lista.get(0); // Fila 1 en la base

        assertEquals("MariaModificada", registro1.getNombre(),
                "Error Demostrado: la actualización impactó sobre el id=1 a causa del hardcodeo en BaseDeDatos.");
    }

    // 4. TESTEO DE ELIMINAR ASOCIADOS VIA DAO
    @Test
    // A traves del DAO deberiamos poder borrar a un asociado
    void testEliminarAsociadoExistente() throws SQLException {
        String dniABorrar = "12345678"; // DNI de Juan State

        dao.eliminar(dniABorrar);

        // Verificar que el tamaño de la base bajó a 19
        ArrayList<AsociadoDTO> lista = dao.cargarBD();
        assertEquals(19, lista.size(), "Luego de eliminar a un asociado, la lista debe contener 19 registros.");

        // Verificar que el asociado borrado ya no se encuentra en la colección
        boolean encontrado = false;
        for (AsociadoDTO dto : lista) {
            if (dniABorrar.equals(dto.getDni())) {
                encontrado = true;
                break;
            }
        }
        assertFalse(encontrado, "El asociado eliminado no debe figurar en la base de datos.");
    }

    @Test
    // Si queremos eliminar un DNI que no existe no deberia ocurrir nada
    void testEliminarDniInexistenteNoAlteraRegistros() throws SQLException {
        // Intentar eliminar un DNI que no existe
        dao.eliminar("99999999");

        // Comprobar que la base mantiene intactos los 20 registros
        ArrayList<AsociadoDTO> lista = dao.cargarBD();
        assertEquals(20, lista.size(), "Eliminar un DNI inexistente no debe alterar la cantidad de registros.");
    }
    
    @Test 
    // Si quisieramos eliminar y pasamos como parametro null, no deberia haber modificacion alguna ni se deberia realizar la query.
    void testEliminarConDNINull() {
    	AssertionError error = assertThrows(AssertionError.class, () -> {
            dao.eliminar(null);
        });
    	
    	// Deberiamos obtener un error de que el DNI no puede ser nulo
    	assertEquals("El DNI no puede ser nulo", error.getMessage());
    }
    
    // 5. TESTEO DE MAPEAR UN DAO A UN DTO
    @Test
    void testCrearDTOAlineaAtributosDeDominio() {
        Domicilio dom = new Domicilio("Alberti", 500, "Mar del Plata");
        Asociado asociado = new Asociado("Santiago", "Yanosky", "35111222", "2235559988", dom);

        AsociadoDTO dto = dao.CrearDTO(asociado);

        assertNotNull(dto);
        assertEquals("Santiago", dto.getNombre());
        assertEquals("Yanosky", dto.getApellido());
        assertEquals("35111222", dto.getDni());
        assertEquals("Alberti", dto.getCalle());
        assertEquals(500, dto.getNumero());
        assertEquals("Mar del Plata", dto.getCiudad());
        assertEquals("2235559988", dto.getTelefono());
    }


    @Test
    void testNoSeGuardaIdEnCrearDTO() {
        int idEsperado = 99;
        AsociadoDTO dto = dao.CrearDTO(idEsperado, "Ana", "Gomez", "22334455", "Colon", 500, "Mar del Plata", "2234445566");

        assertNotNull(dto);
        assertEquals("Ana", dto.getNombre());
        assertEquals("22334455", dto.getDni());
        // El id se pierde por falta de soporte en AsociadoDTO y falta de asignación en CrearDTO
    }

    // 6. MANEJO EXCEPCIONES SQL Y VALIDACIONES DE INTEGRIDAD
    @Test
    void testGuardarDniDuplicadoLanzaSQLException() throws SQLException {
        Domicilio domicilio = new Domicilio("Dorrego", 3100, "Mar del Plata");
        Asociado duplicado = new Asociado("Agustin", "Proia", "35521599", "2231234567", domicilio);

        // Guardamos por primera vez al asociado
        dao.guardar(duplicado);

        // Intentar guardarlo por segunda vez con el mismo DNI debe disparar SQLException
        assertThrows(SQLException.class, () -> {
            dao.guardar(duplicado);
        }, "Guardar un asociado con DNI repetido debe lanzar SQLException por restricción UNIQUE.");
    }

    @Test
    void testPrecondicionesLanzanAssertionError() {
        // guardar con asociado nulo
        assertThrows(AssertionError.class, () -> dao.guardar(null));

        // actualizar con asociado nulo
        assertThrows(AssertionError.class, () -> dao.actualizar(null));

        // cargarConLimite con limite <= 0
        assertThrows(AssertionError.class, () -> dao.cargarConLimite(0));
        assertThrows(AssertionError.class, () -> dao.cargarConLimite(-5));

        // cargar con id <= 0
        assertThrows(AssertionError.class, () -> dao.cargar(0));
        assertThrows(AssertionError.class, () -> dao.cargar(-1));

        // eliminar con DNI vacio
        assertThrows(AssertionError.class, () -> dao.eliminar(""));
    }
    
    @Test
    public void testGuardarAsociadoConCamposVaciosDeberiaRechazarse() {
        Asociado asociadoVacio = new Asociado("", "", "", "", new Domicilio("", 1, ""));

        assertThrows(
            AssertionError.class,
            () -> dao.guardar(asociadoVacio),
            "No debería permitirse guardar en la base de datos un Asociado con nombre, apellido y DNI vacíos"
        );
    }
}
