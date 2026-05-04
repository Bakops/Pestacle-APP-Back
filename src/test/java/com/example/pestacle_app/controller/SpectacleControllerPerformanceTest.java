package com.example.pestacle_app.controller;

import com.example.pestacle_app.dto.SpectacleDTO;
import com.example.pestacle_app.model.enums.StatutSpectacle;
import com.example.pestacle_app.service.SpectacleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Tag("perf")
class SpectacleControllerPerformanceTest {

    @Mock
    private SpectacleService spectacleService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SpectacleController controller = new SpectacleController(spectacleService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        when(spectacleService.getSpectacleById(1L)).thenReturn(buildSpectacleDto());
    }

    @Test
    void getSpectacleAverageLatencyShouldStayUnderThreshold() throws Exception {
        int iterations = 250;
        long start = System.nanoTime();

        for (int i = 0; i < iterations; i++) {
            mockMvc.perform(get("/api/spectacles/1"))
                    .andExpect(status().isOk());
        }

        long durationNanos = System.nanoTime() - start;
        double averageLatencyMillis = (durationNanos / 1_000_000.0) / iterations;

        assertTrue(averageLatencyMillis < 150,
                "Latence moyenne trop élevée: " + averageLatencyMillis + " ms");
    }

    private SpectacleDTO buildSpectacleDto() {
        SpectacleDTO dto = new SpectacleDTO();
        dto.setId(1L);
        dto.setTitre("Perf show");
        dto.setDescription("Performance test");
        dto.setDateHeure(LocalDateTime.now().plusDays(2));
        dto.setLieu("Nantes");
        dto.setPrixUnitaire(BigDecimal.valueOf(20));
        dto.setPlacesDisponibles(80);
        dto.setStatut(StatutSpectacle.DISPONIBLE);
        return dto;
    }
}
