package test;

import modelo.modeloDominio.ambulancia.*;
import modelo.modeloDominio.personas.operario.Operario;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MantenimientoConcurrenciaTest {

    @Test
    void operarioDebeEsperarYLuegoLlevarAmbulanciaAlTaller()
            throws InterruptedException {

        Ambulancia ambulancia = new Ambulancia();
        Operario operario = mock(Operario.class);

        when(operario.getNombre()).thenReturn("Operario 1");

        ambulancia.getEstadoActual().SolicitudDeTraslado();
        ambulancia.setOcupado(true);

        CountDownLatch esperando = new CountDownLatch(1);
        CountDownLatch solicitudTerminada = new CountDownLatch(1);

        ambulancia.addObserver((observable, notificacion) ->
                esperando.countDown()
        );

        Thread hiloOperario = new Thread(() -> {
            try {
                ambulancia.solicitarMantenimiento(operario);
            } finally {
                solicitudTerminada.countDown();
            }
        });

        hiloOperario.start();

        try {
            assertTrue(
                esperando.await(2, TimeUnit.SECONDS),
                "El operario debería esperar mientras la ambulancia está ocupada"
            );

            ambulancia.regresarSinPaciente();

            assertTrue(
                solicitudTerminada.await(2, TimeUnit.SECONDS),
                "El operario debería conseguir la ambulancia al liberarse"
            );

            assertInstanceOf(
                EnTallerState.class,
                ambulancia.getEstadoActual()
            );

            assertTrue(
                ambulancia.isOcupado(),
                "La ambulancia debería estar ocupada durante el mantenimiento"
            );

            ambulancia.volviendoDelTaller();
            ambulancia.regresarSinPaciente();

            assertInstanceOf(
                DisponibleState.class,
                ambulancia.getEstadoActual()
            );

            assertFalse(
                ambulancia.isOcupado(),
                "La ambulancia debería quedar libre al salir del taller"
            );

        } finally {
            ambulancia.setSimulacionActiva(false);
            ambulancia.regresarSinPaciente();
            hiloOperario.join(2000);
        }

        assertFalse(
            hiloOperario.isAlive(),
            "El hilo del operario debería terminar"
        );
    }
}