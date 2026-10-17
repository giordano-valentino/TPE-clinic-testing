package test;
import modelo.modeloDominio.ambulancia.*;
import modelo.modeloDominio.personas.asociado.Asociado;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AmbulanciaConcurrenciaTest {

    @Test
    void dosAsociadosDebenEsperarYUsarLaAmbulancia()
            throws InterruptedException {

        Ambulancia ambulancia = new Ambulancia();

        Asociado asociadoA = mock(Asociado.class);
        Asociado asociadoB = mock(Asociado.class);

        when(asociadoA.getNombre()).thenReturn("Asociado A");
        when(asociadoB.getNombre()).thenReturn("Asociado B");

        // Simulamos que otro servicio ya está usando la ambulancia.
        ambulancia.getEstadoActual().SolicitudDeTraslado();
        ambulancia.setOcupado(true);

        CountDownLatch esperando = new CountDownLatch(2);
        Semaphore serviciosCompletados = new Semaphore(0);

        // Cada notificación emitida mientras los asociados esperan
        // permite sincronizar el test sin usar sleep().
        ambulancia.addObserver((observable, notificacion) ->
                esperando.countDown()
        );

        Thread hiloA = new Thread(() -> {
            ambulancia.trasladarALaClinica(asociadoA);
            serviciosCompletados.release();
        });

        Thread hiloB = new Thread(() -> {
            ambulancia.atenderDomicilio(asociadoB);
            serviciosCompletados.release();
        });

        hiloA.start();
        hiloB.start();

        try {
            assertTrue(
                esperando.await(2, TimeUnit.SECONDS),
                "Los dos asociados deberían quedar esperando"
            );

            // Liberamos la ambulancia. Solo uno debería conseguirla.
            ambulancia.regresarSinPaciente();

            assertTrue(
                serviciosCompletados.tryAcquire(2, TimeUnit.SECONDS),
                "Un asociado debería conseguir la ambulancia"
            );

            State estado = ambulancia.getEstadoActual();
            assertTrue(
                estado instanceof TrasladandoPacienteClinicaState
                        || estado instanceof AtendiendoEnDomicilioState,
                "El servicio debería cambiar el estado, pero quedó en " + estado.getClass().getSimpleName()
            );

            // Liberamos el servicio del primer asociado.
            ambulancia.regresarSinPaciente();

            assertTrue(
                serviciosCompletados.tryAcquire(2, TimeUnit.SECONDS),
                "El segundo asociado debería conseguir la ambulancia"
            );

        } finally {
            // Evitamos dejar hilos bloqueados si falla una aserción.
            ambulancia.setSimulacionActiva(false);
            ambulancia.regresarSinPaciente();

            hiloA.join(2000);
            hiloB.join(2000);
        }

        assertFalse(hiloA.isAlive(), "El hilo A debería terminar");
        assertFalse(hiloB.isAlive(), "El hilo B debería terminar");
    }
}