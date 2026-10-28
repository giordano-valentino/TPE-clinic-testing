package test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import modelo.modeloDominio.ambulancia.*;
import modelo.modeloDominio.personas.asociado.Asociado;
import modelo.modeloDominio.personas.operario.Operario;

class TestAmbulancia {

    @Test
    void debeDelegarTrasladoAlEstadoActual() {
        Ambulancia ambulancia = new Ambulancia();
        State estadoMock = mock(State.class);
        Asociado asociadoMock = mock(Asociado.class);

        ambulancia.setState(estadoMock);

        ambulancia.trasladarALaClinica(asociadoMock);

        verify(estadoMock, times(1)).SolicitudDeTraslado();
        verifyNoMoreInteractions(estadoMock);
        assertTrue(ambulancia.isOcupado());
    }

    @Test
    void debeDelegarRetornoAlEstadoActual() {
        Ambulancia ambulancia = new Ambulancia();
        State estadoMock = mock(State.class);
        RetornoAutomatico retornoMock = mock(RetornoAutomatico.class);

        ambulancia.setState(estadoMock);

        ambulancia.retornoAutomatico(retornoMock);

        verify(estadoMock, times(1)).RetornoClinica();
        verifyNoMoreInteractions(estadoMock); // Asegura que la ambulancia solo toque el método que le corresponde del estado.
    }

    @Test
    void debeDelegarMantenimientoAlEstadoActual() {
        Ambulancia ambulancia = new Ambulancia();
        State estadoMock = mock(State.class);
        Operario operarioMock = mock(Operario.class);

        ambulancia.setState(estadoMock);

        ambulancia.solicitarMantenimiento(operarioMock);

        verify(estadoMock, times(1)).SolicitudMantenimiento();
        verifyNoMoreInteractions(estadoMock);
        assertTrue(ambulancia.isOcupado()); // Verifica que el atributo interno ocupado de la ambulancia haya cambiado a true.
    }

    @Test
    void debeDelegarAtencionDomicilioAlEstadoActual() {
        Ambulancia ambulancia = new Ambulancia();
        State estadoMock = mock(State.class);
        Asociado asociadoMock = mock(Asociado.class);

        ambulancia.setState(estadoMock);

        ambulancia.atenderDomicilio(asociadoMock);

        verify(estadoMock, times(1)).SolicitudDeAtencionDomicilio();
        verifyNoMoreInteractions(estadoMock);
        assertTrue(ambulancia.isOcupado()); // Verifica que el atributo interno ocupado de la ambulancia haya cambiado a true.
    }

    @Test
    void debeDelegarRegresarSinPacienteYLiberarAmbulancia() {
        Ambulancia ambulancia = new Ambulancia();
        State estadoMock = mock(State.class);

        ambulancia.setState(estadoMock);
        ambulancia.setOcupado(true);

        ambulancia.regresarSinPaciente();

        verify(estadoMock, times(1)).RetornoClinica();
        verifyNoMoreInteractions(estadoMock);
        assertFalse(ambulancia.isOcupado()); //Verifica que el atributo interno ocupado de la ambulancia haya cambiado a false.
    }

    @Test
    void debeDelegarVolviendoDelTallerYLiberarAmbulancia() {
        Ambulancia ambulancia = new Ambulancia();
        State estadoMock = mock(State.class);

        ambulancia.setState(estadoMock);
        ambulancia.setOcupado(true);

        ambulancia.volviendoDelTaller();

        verify(estadoMock, times(1)).SolicitudMantenimiento();
        verifyNoMoreInteractions(estadoMock);
        assertFalse(ambulancia.isOcupado());
    }

    /* Simulación de comportamiento ante ausencia de clases concretas:
     * El mock del estado actual simula una transición cambiando el estado
     * de la ambulancia mediante doAnswer, verificando que la ambulancia
     * adopte el nuevo estado sin requerir implementaciones concretas. */
    @Test
    void debePermitirAlEstadoSimularTransicion() {
        Ambulancia ambulancia = new Ambulancia();
        State estadoActualMock = mock(State.class);
        State estadoSiguienteMock = mock(State.class); // Nuevo Mock
        Asociado asociadoMock = mock(Asociado.class);

        doAnswer(invocation -> {
            ambulancia.setState(estadoSiguienteMock);
            return null;
        }).when(estadoActualMock).SolicitudDeTraslado(); // Cuando la ambulancia solicite traslado cambia al nuevo estado Mock

        ambulancia.setState(estadoActualMock);

        ambulancia.trasladarALaClinica(asociadoMock); // Al llamarlo, se dispara la lógica simulada

        assertEquals(estadoSiguienteMock, ambulancia.getEstadoActual());
    }

}
