package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import modelo.modeloDominio.ambulancia.Ambulancia;
import modelo.modeloDominio.ambulancia.*;

class TestIntegracionAmbulancia {

    @Test
    void disponibleDebePasarATrasladandoPacienteClinica() {
        Ambulancia ambulancia = new Ambulancia();

        ambulancia.getEstadoActual().SolicitudDeTraslado();

        assertInstanceOf(
            TrasladandoPacienteClinicaState.class,
            ambulancia.getEstadoActual()
        );
    }

    @Test
    void disponibleDebePasarAAtendiendoEnDomicilio() {
        Ambulancia ambulancia = new Ambulancia();

        ambulancia.getEstadoActual()
                  .SolicitudDeAtencionDomicilio();

        assertInstanceOf(
            AtendiendoEnDomicilioState.class,
            ambulancia.getEstadoActual()
        );
    }

    @Test
    void disponibleDebePasarAEnTaller() {
        Ambulancia ambulancia = new Ambulancia();

        ambulancia.getEstadoActual().SolicitudMantenimiento();

        assertInstanceOf(
            EnTallerState.class,
            ambulancia.getEstadoActual()
        );
    }
    
    @Test
    void disponibleDebeSeguirDisponible() {
    	Ambulancia ambulancia = new Ambulancia();

        ambulancia.getEstadoActual().RetornoClinica();
        assertInstanceOf(
                DisponibleState.class,
                ambulancia.getEstadoActual()
            );
    }
    
    @Test
    void atendiendoADomiclioDebeSeguirAtendiendo() {
    	Ambulancia ambulancia = new Ambulancia();
    	
    	ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
    	
    	ambulancia.getEstadoActual().SolicitudDeTraslado();
    	assertInstanceOf(
    			AtendiendoEnDomicilioState.class,
    			ambulancia.getEstadoActual()
    			);
    }
    
    @Test
    void atendiendoADomicilioDebeRechazarMantenimiento() {
    	Ambulancia ambulancia = new Ambulancia();
    	
    	ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
    	
    	ambulancia.getEstadoActual().SolicitudMantenimiento();
    	assertInstanceOf(
    			AtendiendoEnDomicilioState.class,
    			ambulancia.getEstadoActual()
    			);
    }
    
    @Test
    void atendiendoADomicilioDebeRechazarAtencionDomicilio() {
    	Ambulancia ambulancia = new Ambulancia();
    	
    	ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
    	
    	ambulancia.getEstadoActual().SolicitudDeAtencionDomicilio();
    	assertInstanceOf(
    			AtendiendoEnDomicilioState.class,
    			ambulancia.getEstadoActual()
    			);
    }
    
    @Test
    void debePasarDeAtendiendoDomicilioARetornoAClinica() {
    	Ambulancia ambulancia = new Ambulancia();
    	
    	ambulancia.setState(new AtendiendoEnDomicilioState(ambulancia));
    	
    	ambulancia.getEstadoActual().RetornoClinica();
    	assertInstanceOf(
    			RegresandoClinicaSinPacienteState.class,
    			ambulancia.getEstadoActual()
    			);
    }
    
    @Test
    void enTallerDebeNoRetornarALaClinica() {
    	Ambulancia ambulancia = new Ambulancia();
    	
    	ambulancia.setState(new EnTallerState(ambulancia));
    	
    	ambulancia.getEstadoActual().RetornoClinica();
    	assertInstanceOf(
    			EnTallerState.class,
    			ambulancia.getEstadoActual()
    			);
    }
    
    @Test
    void enTallerDebeNoAtenderDomicilio() {
    	Ambulancia ambulancia = new Ambulancia();
    	
    	ambulancia.setState(new EnTallerState(ambulancia));
    	
    	ambulancia.getEstadoActual().SolicitudDeAtencionDomicilio();
    	assertInstanceOf(
    			EnTallerState.class,
    			ambulancia.getEstadoActual()
    			);
    }
    
    @Test
    void enTallerDebeNoTrasladarPaciente() {
    	Ambulancia ambulancia = new Ambulancia();
    	
    	ambulancia.setState(new EnTallerState(ambulancia));
    	
    	ambulancia.getEstadoActual().SolicitudDeTraslado();
    	assertInstanceOf(
    			EnTallerState.class,
    			ambulancia.getEstadoActual()
    			);
    }
    
    @Test
    void enTallerDebeTerminarMantenimiento() {
    	Ambulancia ambulancia = new Ambulancia();
    	
    	ambulancia.setState(new EnTallerState(ambulancia));
    	
    	ambulancia.getEstadoActual().SolicitudMantenimiento();
    	assertInstanceOf(
    			RegresandoDelTallerState.class,
    			ambulancia.getEstadoActual()
    			);
    }
    
    @Test
    void RegresandoTallerNoDebePasarATrasladandoPacienteClinica() {
        Ambulancia ambulancia = new Ambulancia();

        ambulancia.setState(new RegresandoDelTallerState(ambulancia));
        
        ambulancia.getEstadoActual().SolicitudDeTraslado();

        assertInstanceOf(
            RegresandoDelTallerState.class,
            ambulancia.getEstadoActual()
        );
    }
    
    @Test
    void RegresandoTallerNoDebePasarAAtencionADomicilio() {
        Ambulancia ambulancia = new Ambulancia();

        ambulancia.setState(new RegresandoDelTallerState(ambulancia));
        
        ambulancia.getEstadoActual().SolicitudDeAtencionDomicilio();

        assertInstanceOf(
            RegresandoDelTallerState.class,
            ambulancia.getEstadoActual()
        );
    }

    @Test
    void RegresandoTallerNoDebePasarAMantenimiento() {
        Ambulancia ambulancia = new Ambulancia();

        ambulancia.setState(new RegresandoDelTallerState(ambulancia));
        
        ambulancia.getEstadoActual().SolicitudMantenimiento();

        assertInstanceOf(
            RegresandoDelTallerState.class,
            ambulancia.getEstadoActual()
        );
    }

    @Test
    void RegresandoTallerDebePasarADisponible() {
        Ambulancia ambulancia = new Ambulancia();

        ambulancia.setState(new RegresandoDelTallerState(ambulancia));
        
        ambulancia.getEstadoActual().RetornoClinica();

        assertInstanceOf(
            DisponibleState.class,
            ambulancia.getEstadoActual()
        );
    }
    
    @Test
    void regresandoAClinicaDebePasarADisponible() {
    	Ambulancia ambulancia = new Ambulancia();

        ambulancia.setState(new RegresandoClinicaSinPacienteState(ambulancia));
        
        ambulancia.getEstadoActual().RetornoClinica();
        
        assertInstanceOf(
                DisponibleState.class,
                ambulancia.getEstadoActual()
            );
    }
    
    @Test
    void regresandoAClinicaNoDebePasarAMantenimiento() {
    	Ambulancia ambulancia = new Ambulancia();

        ambulancia.setState(new RegresandoClinicaSinPacienteState(ambulancia));
        
        ambulancia.getEstadoActual().SolicitudMantenimiento();
        
        assertInstanceOf(
        		RegresandoClinicaSinPacienteState.class,
                ambulancia.getEstadoActual()
            );
    }
    
    @Test
    void regresandoAClinicaDebePasarATrasladandoPaciente() {
    	Ambulancia ambulancia = new Ambulancia();

        ambulancia.setState(new RegresandoClinicaSinPacienteState(ambulancia));
        
        ambulancia.getEstadoActual().SolicitudDeTraslado();
        
        assertInstanceOf(
        		TrasladandoPacienteClinicaState.class,
                ambulancia.getEstadoActual()
            );
    }
    
    @Test
    void regresandoAClinicaDebePasarAAtendiendoDomicilio() {
    	Ambulancia ambulancia = new Ambulancia();

        ambulancia.setState(new RegresandoClinicaSinPacienteState(ambulancia));
        
        ambulancia.getEstadoActual().SolicitudDeAtencionDomicilio();
        
        assertInstanceOf(
        		AtendiendoEnDomicilioState.class,
                ambulancia.getEstadoActual()
            );
    }

    @Test
    void traslandoPacienteDebeSeguirTrasladando() {
    	Ambulancia ambulancia = new Ambulancia();

        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));
        
        ambulancia.getEstadoActual().SolicitudDeTraslado();
        
        assertInstanceOf(
        		TrasladandoPacienteClinicaState.class,
                ambulancia.getEstadoActual()
            );
    }
    
    @Test
    void traslandoPacienteNoDebeAtenderDomicilio() {
    	Ambulancia ambulancia = new Ambulancia();

        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));
        
        ambulancia.getEstadoActual().SolicitudDeAtencionDomicilio();
        
        assertInstanceOf(
        		TrasladandoPacienteClinicaState.class,
                ambulancia.getEstadoActual()
            );
    }
    
    @Test
    void traslandoPacienteDebePasarADisponible() {
    	Ambulancia ambulancia = new Ambulancia();

        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));
        
        ambulancia.getEstadoActual().RetornoClinica();
        
        assertInstanceOf(
        		DisponibleState.class,
                ambulancia.getEstadoActual()
            );
    }
    
    @Test
    void traslandoPacienteNoDebeIrAMantenimiento() {
    	Ambulancia ambulancia = new Ambulancia();

        ambulancia.setState(new TrasladandoPacienteClinicaState(ambulancia));
        
        ambulancia.getEstadoActual().SolicitudDeTraslado();
        
        assertInstanceOf(
        		TrasladandoPacienteClinicaState.class,
                ambulancia.getEstadoActual()
            );
    }
}
