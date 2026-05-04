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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class SpectacleControllerUnitTest {

    @Mock
    private SpectacleService spectacleService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SpectacleController controller = new SpectacleController(spectacleService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getSpectacleShouldReturn200AndPayload() throws Exception {
        SpectacleDTO dto = buildSpectacleDto();
        when(spectacleService.getSpectacleById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/spectacles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Concert de test"))
                .andExpect(jsonPath("$.lieu").value("Paris"));
    }

    @Test
    void getAllSpectaclesShouldReturnPagedPayload() throws Exception {
        Page<SpectacleDTO> page = new PageImpl<>(List.of(buildSpectacleDto()), PageRequest.of(0, 20), 1);
        when(spectacleService.getAllSpectacles(any())).thenReturn(page);

        mockMvc.perform(get("/api/spectacles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titre").value("Concert de test"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void reserverPlacesShouldReturnTrue() throws Exception {
        when(spectacleService.reserverPlaces(anyLong(), anyInt())).thenReturn(true);

        mockMvc.perform(post("/api/spectacles/1/reserver/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    private SpectacleDTO buildSpectacleDto() {
        SpectacleDTO dto = new SpectacleDTO();
        dto.setId(1L);
        dto.setTitre("Concert de test");
        dto.setDescription("Description test");
        dto.setDateHeure(LocalDateTime.now().plusDays(1));
        dto.setLieu("Paris");
        dto.setPrixUnitaire(BigDecimal.valueOf(35));
        dto.setPlacesDisponibles(120);
        dto.setStatut(StatutSpectacle.DISPONIBLE);
        return dto;
    }
}
