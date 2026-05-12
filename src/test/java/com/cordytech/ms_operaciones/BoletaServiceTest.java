package com.cordytech.ms_operaciones;

import com.cordytech.ms_operaciones.dto.SimulacionRequest;
import com.cordytech.ms_operaciones.model.Boleta;
import com.cordytech.ms_operaciones.operaciones.CalculoPortuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BoletaServiceTest {

    @Autowired
    private CalculoPortuarioService calculoPortuarioService;

    @Test
    void testSimularCalculo() {
        SimulacionRequest request = new SimulacionRequest();
        request.setCodBuque("B001");
        request.setIdPuerto(1L);
        request.setDiasEstancia(3);
        request.setTipoServicio(Boleta.TipoServicio.BASICO);

        Double resultado = calculoPortuarioService.calcularSimulacion(request);

        assertNotNull(resultado);
        assertTrue(resultado > 0);
    }

    @Test
    void testSimularCalculoCompleto() {
        SimulacionRequest request = new SimulacionRequest();
        request.setCodBuque("B002");
        request.setIdPuerto(1L);
        request.setDiasEstancia(5);
        request.setTipoServicio(Boleta.TipoServicio.COMPLETO);

        Double resultado = calculoPortuarioService.calcularSimulacion(request);

        assertNotNull(resultado);
        assertTrue(resultado > 0);
    }
}
