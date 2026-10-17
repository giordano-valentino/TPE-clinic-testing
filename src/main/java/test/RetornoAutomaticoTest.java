package test;

import modelo.modeloDominio.ambulancia.*;

import modelo.modeloDominio.personas.asociado.Asociado;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RetornoAutomatico tiene un intervalo fijo de 10 segundos (INTERVALO_RETORNO)
 * y no se puede modificar el código original, así que los tiempos de espera
 * de estos tests están calculados sobre ese intervalo. La clase tarda ~40 s.
 */
class RetornoAutomaticoTest {

    private static final long INTERVALO_MS = 10_000;
    private static final long MARGEN_MS = 3_000;

    // CASO 1:
    // El retorno automático se ejecuta al cumplirse el intervalo
    // mientras la simulación está activa.
    @Test
    void debeEjecutarRetornoAlCumplirseElIntervalo() throws Exception {
        Ambulancia ambulancia = new Ambulancia();
        State estado = mock(State.class);
        ambulancia.setState(estado);

        CountDownLatch unRetorno = new CountDownLatch(1);

        doAnswer(invocacion -> {
            unRetorno.countDown();
            return null;
        }).when(estado).RetornoClinica();

        RetornoAutomatico retorno =
                new RetornoAutomatico(ambulancia);

        Thread hilo = new Thread(retorno);

        try {
            hilo.start();

            assertTrue(
                unRetorno.await(INTERVALO_MS + MARGEN_MS, TimeUnit.MILLISECONDS),
                "Debería ejecutar un retorno al cumplirse el intervalo"
            );

            verify(estado, atLeast(1)).RetornoClinica();

        } finally {
            ambulancia.setSimulacionActiva(false);
            hilo.interrupt();
            hilo.join(2000);

            if (hilo.isAlive()) {
                hilo.interrupt();
                hilo.join(1000);
            }
        }

        assertFalse(hilo.isAlive());
    }

    // CASO 2:
    // Si la simulación ya terminó, no debe hacer retornos.
    @Test
    void noDebeEjecutarRetornoSiLaSimulacionYaFinalizo()
            throws Exception {

        Ambulancia ambulancia = new Ambulancia();
        State estado = mock(State.class);
        ambulancia.setState(estado);

        ambulancia.setSimulacionActiva(false);

        Thread hilo = new Thread(
                new RetornoAutomatico(ambulancia));

        hilo.start();
        hilo.join(INTERVALO_MS + MARGEN_MS);

        assertFalse(
            hilo.isAlive(),
            "El hilo debe finalizar"
        );

        verify(estado, never()).RetornoClinica();
    }

    // CASO 3:
    // Una interrupción debe finalizar el hilo limpiamente.
    @Test
    void debeFinalizarCuandoSeInterrumpe() throws Exception {
        Ambulancia ambulancia = new Ambulancia();
        State estado = mock(State.class);
        ambulancia.setState(estado);

        Thread hilo = new Thread(
                new RetornoAutomatico(ambulancia));

        hilo.start();

        try {
            assertTrue(
                esperarEstado(hilo, Thread.State.TIMED_WAITING, 1000),
                "El hilo debería estar esperando el intervalo"
            );

            hilo.interrupt();
            hilo.join(1000);

            assertFalse(
                hilo.isAlive(),
                "El hilo debe finalizar después de la interrupción"
            );

            verify(estado, never()).RetornoClinica();

        } finally {
            ambulancia.setSimulacionActiva(false);

            if (hilo.isAlive()) {
                hilo.interrupt();
                hilo.join(1000);
            }
        }
    }

    // CASO 4:
    // Si la simulación termina durante la espera,
    // no debe realizar otro retorno al despertarse.
    @Test
    void noDebeRetornarDespuesDeFinalizarLaSimulacion()
            throws Exception {

        Ambulancia ambulancia = new Ambulancia();
        State estado = mock(State.class);
        ambulancia.setState(estado);

        CountDownLatch primerRetorno = new CountDownLatch(1);

        doAnswer(invocacion -> {
            primerRetorno.countDown();
            return null;
        }).when(estado).RetornoClinica();

        Thread hilo = new Thread(
                new RetornoAutomatico(ambulancia));

        try {
            hilo.start();

            assertTrue(
                primerRetorno.await(INTERVALO_MS + MARGEN_MS, TimeUnit.MILLISECONDS),
                "Debería producirse el primer retorno"
            );

            // El hilo está en su siguiente ciclo de espera.
            assertTrue(
                esperarEstado(hilo, Thread.State.TIMED_WAITING, 1000)
            );

            ambulancia.setSimulacionActiva(false);

            hilo.join(INTERVALO_MS + MARGEN_MS);

            assertFalse(
                hilo.isAlive(),
                "El hilo debería finalizar"
            );

            verify(estado, times(1)).RetornoClinica();

        } finally {
            ambulancia.setSimulacionActiva(false);

            if (hilo.isAlive()) {
                hilo.interrupt();
                hilo.join(1000);
            }
        }
    }

    // CASO 5:
    // El retorno automático y un asociado operan sobre
    // la misma ambulancia sin ejecutar simultáneamente
    // los métodos protegidos por synchronized.
    @Test
    void retornoYSolicitudDebenEstarSincronizados()
            throws Exception {

        Ambulancia ambulancia = new Ambulancia();
        State estado = mock(State.class);
        Asociado asociado = mock(Asociado.class);

        when(asociado.getNombre()).thenReturn("Asociado test");

        ambulancia.setState(estado);

        AtomicInteger operacionesActivas = new AtomicInteger(0);
        AtomicInteger maximoSimultaneas = new AtomicInteger(0);

        CountDownLatch primerRetorno = new CountDownLatch(1);
        CountDownLatch solicitudTerminada = new CountDownLatch(1);

        org.mockito.stubbing.Answer<Void> operacion = invocacion -> {
            int activas = operacionesActivas.incrementAndGet();

            maximoSimultaneas.accumulateAndGet(
                    activas, Math::max);

            try {
                if (invocacion.getMethod().getName()
                        .equals("RetornoClinica")) {
                    primerRetorno.countDown();
                }

                // Amplía la ventana de posible solapamiento.
                Thread.sleep(20);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            } finally {
                operacionesActivas.decrementAndGet();
            }

            return null;
        };

        doAnswer(operacion).when(estado).RetornoClinica();
        doAnswer(operacion).when(estado).SolicitudDeTraslado();

        Thread hiloRetorno = new Thread(
                new RetornoAutomatico(ambulancia));

        Thread hiloAsociado = new Thread(() -> {
            try {
                ambulancia.trasladarALaClinica(asociado);
            } finally {
                solicitudTerminada.countDown();
            }
        });

        try {
            hiloRetorno.start();

            assertTrue(
                primerRetorno.await(INTERVALO_MS + MARGEN_MS, TimeUnit.MILLISECONDS),
                "El retorno automático debería empezar"
            );

            hiloAsociado.start();

            assertTrue(
                solicitudTerminada.await(2, TimeUnit.SECONDS),
                "La solicitud del asociado debería terminar"
            );

        } finally {
            ambulancia.setSimulacionActiva(false);
            hiloRetorno.interrupt();

            hiloRetorno.join(1000);
            hiloAsociado.join(1000);

            if (hiloRetorno.isAlive()) {
                hiloRetorno.interrupt();
                hiloRetorno.join(1000);
            }

            if (hiloAsociado.isAlive()) {
                hiloAsociado.interrupt();
                hiloAsociado.join(1000);
            }
        }

        assertFalse(hiloRetorno.isAlive());
        assertFalse(hiloAsociado.isAlive());

        assertEquals(
            1,
            maximoSimultaneas.get(),
            "Las operaciones sobre el estado no deberían solaparse"
        );
    }

    // Espera activa acotada: no depende de dormir un tiempo arbitrario.
    private boolean esperarEstado(
            Thread hilo,
            Thread.State estadoEsperado,
            long timeoutMillis) {

        long limite = System.nanoTime()
                + TimeUnit.MILLISECONDS.toNanos(timeoutMillis);

        while (System.nanoTime() < limite) {
            if (hilo.getState() == estadoEsperado) {
                return true;
            }

            if (!hilo.isAlive()) {
                return false;
            }

            Thread.yield();
        }

        return hilo.getState() == estadoEsperado;
    }
}
