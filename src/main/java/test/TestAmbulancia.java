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

        verify(estadoMock, times(1))
                .SolicitudDeTraslado();
    }

    @Test
    void debeDelegarRetornoAlEstadoActual() {
        Ambulancia ambulancia = new Ambulancia();
        State estadoMock = mock(State.class);

        ambulancia.setState(estadoMock);

        RetornoAutomatico retorno =
                new RetornoAutomatico(ambulancia);

        ambulancia.retornoAutomatico(retorno);

        verify(estadoMock, times(1))
                .RetornoClinica();
    }
    
    @Test
    void debeDelegarMantenimientoAlEstadoActual() {
    	Ambulancia ambulancia = new Ambulancia();
    	State estadoMock = mock(State.class);
    	Operario operarioMock = mock(Operario.class);
    	
    	ambulancia.setState(estadoMock);
    	
    	ambulancia.solicitarMantenimiento(operarioMock);
    	
    	verify(estadoMock, times(1)).SolicitudMantenimiento();
    }
    
    @Test
    void debeDelegarAtencionDomicilioAlEstadoActual() {
    	Ambulancia ambulancia = new Ambulancia();
    	State estadoMock = mock(State.class);
    	Asociado asociadoMock = mock(Asociado.class);

        ambulancia.setState(estadoMock);
        
        ambulancia.atenderDomicilio(asociadoMock);
        
        verify(estadoMock, times(1)).SolicitudDeAtencionDomicilio();
    	
    }
    
    
}