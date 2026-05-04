package com.example.pestacle_app.controller;

import com.example.pestacle_app.dto.SpectacleDTO;
import com.example.pestacle_app.service.SpectacleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpectacleController.class)
@ActiveProfiles("test")
class SpectacleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpectacleService spectacleService;

    @Test
    void getSpectacle_retourneLeSpectacleDemande() throws Exception {
        SpectacleDTO dto = new SpectacleDTO();
        dto.setId(1L);
        dto.setTitre("Concert test");

        when(spectacleService.getSpectacleById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/spectacles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titre").value("Concert test"));
    }

    @Test
    void createSpectacle_refuseUnPayloadInvalide() throws Exception {
        SpectacleDTO dto = new SpectacleDTO();
        dto.setDescription("Description");
        dto.setDateHeure(LocalDateTime.now().plusDays(1));
        dto.setLieu("Paris");
        dto.setPrixUnitaire(BigDecimal.TEN);
        dto.setPlacesDisponibles(10);

        mockMvc.perform(post("/api/spectacles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
