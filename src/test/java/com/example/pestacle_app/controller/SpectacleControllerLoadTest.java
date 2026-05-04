package com.example.pestacle_app.controller;

import com.example.pestacle_app.service.SpectacleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
@Tag("load")
class SpectacleControllerLoadTest {

    @Mock
    private SpectacleService spectacleService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SpectacleController controller = new SpectacleController(spectacleService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        when(spectacleService.reserverPlaces(anyLong(), anyInt())).thenReturn(true);
    }

    @Test
    void reserverPlacesShouldHandleConcurrentLoad() throws Exception {
        int totalRequests = 300;
        int threadCount = 20;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<Integer>> futures = new ArrayList<>();

        long startNanos = System.nanoTime();

        for (int i = 0; i < totalRequests; i++) {
            futures.add(executorService.submit(() -> mockMvc.perform(post("/api/spectacles/1/reserver/2"))
                    .andReturn()
                    .getResponse()
                    .getStatus()));
        }

        int successCount = 0;
        for (Future<Integer> future : futures) {
            if (future.get() == 200) {
                successCount++;
            }
        }

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(10, TimeUnit.SECONDS));

        long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);

        assertEquals(totalRequests, successCount, "Certaines requêtes ont échoué sous charge");
        assertTrue(durationMillis < 10000,
                "Le scénario de charge est trop lent: " + durationMillis + " ms");
    }
}
