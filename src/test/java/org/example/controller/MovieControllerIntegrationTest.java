package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ProducerAwardsIntervalDTO;
import org.example.dto.ProducerIntervalDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetProducersAwardsIntervals_ValidatesCompleteDataFromCsvFile() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/producers/awards-intervals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.min").isArray())
                .andExpect(jsonPath("$.max").isArray())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ProducerAwardsIntervalDTO response = objectMapper.readValue(jsonResponse, ProducerAwardsIntervalDTO.class);

        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getMin(), "Min intervals should not be null");
        assertNotNull(response.getMax(), "Max intervals should not be null");

        validateMinIntervalProducer(response.getMin());

        validateMaxIntervalProducer(response.getMax());
    }

    private void validateMinIntervalProducer(List<ProducerIntervalDTO> minIntervals) {
        assertFalse(minIntervals.isEmpty(), 
            "Should have at least one producer with minimum interval");
        
        assertEquals(1, minIntervals.size(), 
            "Should have exactly 1 producer with minimum interval based on current CSV data");

        ProducerIntervalDTO minProducer = minIntervals.get(0);
        
        assertEquals("Joel Silver", minProducer.getProducer(), 
            "Producer with minimum interval should be 'Joel Silver' based on current CSV data");
        assertEquals(1, minProducer.getInterval(), 
            "Minimum interval should be 1 year based on current CSV data");
        assertEquals(1990, minProducer.getPreviousWin(), 
            "Joel Silver's previous win should be in 1990 based on current CSV data");
        assertEquals(1991, minProducer.getFollowingWin(), 
            "Joel Silver's following win should be in 1991 based on current CSV data");
    }

    private void validateMaxIntervalProducer(List<ProducerIntervalDTO> maxIntervals) {
        assertFalse(maxIntervals.isEmpty(), 
            "Should have at least one producer with maximum interval");
        
        assertEquals(1, maxIntervals.size(), 
            "Should have exactly 1 producer with maximum interval based on current CSV data");

        ProducerIntervalDTO maxProducer = maxIntervals.get(0);
        
        assertEquals("Matthew Vaughn", maxProducer.getProducer(), 
            "Producer with maximum interval should be 'Matthew Vaughn' based on current CSV data");
        assertEquals(13, maxProducer.getInterval(), 
            "Maximum interval should be 13 years based on current CSV data");
        assertEquals(2002, maxProducer.getPreviousWin(), 
            "Matthew Vaughn's previous win should be in 2002 based on current CSV data");
        assertEquals(2015, maxProducer.getFollowingWin(), 
            "Matthew Vaughn's following win should be in 2015 based on current CSV data");
    }

    @Test
    void testGetProducersAwardsIntervals_ReturnsOkStatus() throws Exception {
        mockMvc.perform(get("/api/producers/awards-intervals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProducersAwardsIntervals_ReturnsJsonContentType() throws Exception {
        mockMvc.perform(get("/api/producers/awards-intervals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetProducersAwardsIntervals_ReturnsCorrectStructure() throws Exception {
        mockMvc.perform(get("/api/producers/awards-intervals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min").exists())
                .andExpect(jsonPath("$.max").exists())
                .andExpect(jsonPath("$.min").isArray())
                .andExpect(jsonPath("$.max").isArray());
    }

    @Test
    void testGetProducersAwardsIntervals_IntervalObjectsHaveAllFields() throws Exception {
        mockMvc.perform(get("/api/producers/awards-intervals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.min[0].producer").exists())
                .andExpect(jsonPath("$.min[0].interval").exists())
                .andExpect(jsonPath("$.min[0].previousWin").exists())
                .andExpect(jsonPath("$.min[0].followingWin").exists())
                .andExpect(jsonPath("$.max[0].producer").exists())
                .andExpect(jsonPath("$.max[0].interval").exists())
                .andExpect(jsonPath("$.max[0].previousWin").exists())
                .andExpect(jsonPath("$.max[0].followingWin").exists());
    }

    @Test
    void testGetProducersAwardsIntervals_ValidatesExactValuesWithJsonPath() throws Exception {
        mockMvc.perform(get("/api/producers/awards-intervals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Validar produtor com menor intervalo
                .andExpect(jsonPath("$.min[0].producer").value("Joel Silver"))
                .andExpect(jsonPath("$.min[0].interval").value(1))
                .andExpect(jsonPath("$.min[0].previousWin").value(1990))
                .andExpect(jsonPath("$.min[0].followingWin").value(1991))
                // Validar produtor com maior intervalo
                .andExpect(jsonPath("$.max[0].producer").value("Matthew Vaughn"))
                .andExpect(jsonPath("$.max[0].interval").value(13))
                .andExpect(jsonPath("$.max[0].previousWin").value(2002))
                .andExpect(jsonPath("$.max[0].followingWin").value(2015));
    }

    @Test
    void testGetProducersAwardsIntervals_ValidatesIntervalCalculation() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/producers/awards-intervals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ProducerAwardsIntervalDTO response = objectMapper.readValue(jsonResponse, ProducerAwardsIntervalDTO.class);

        for (ProducerIntervalDTO producer : response.getMin()) {
            int calculatedInterval = producer.getFollowingWin() - producer.getPreviousWin();
            assertEquals(producer.getInterval(), calculatedInterval,
                String.format("Interval calculation error for producer '%s': expected %d but following-previous = %d",
                    producer.getProducer(), producer.getInterval(), calculatedInterval));
        }

        for (ProducerIntervalDTO producer : response.getMax()) {
            int calculatedInterval = producer.getFollowingWin() - producer.getPreviousWin();
            assertEquals(producer.getInterval(), calculatedInterval,
                String.format("Interval calculation error for producer '%s': expected %d but following-previous = %d",
                    producer.getProducer(), producer.getInterval(), calculatedInterval));
        }
    }


    @Test
    void testGetProducersAwardsIntervals_ValidatesYearOrder() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/producers/awards-intervals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ProducerAwardsIntervalDTO response = objectMapper.readValue(jsonResponse, ProducerAwardsIntervalDTO.class);

        for (ProducerIntervalDTO producer : response.getMin()) {
            assertTrue(producer.getPreviousWin() < producer.getFollowingWin(),
                String.format("Previous win year (%d) should be less than following win year (%d) for producer '%s'",
                    producer.getPreviousWin(), producer.getFollowingWin(), producer.getProducer()));
        }

        for (ProducerIntervalDTO producer : response.getMax()) {
            assertTrue(producer.getPreviousWin() < producer.getFollowingWin(),
                String.format("Previous win year (%d) should be less than following win year (%d) for producer '%s'",
                    producer.getPreviousWin(), producer.getFollowingWin(), producer.getProducer()));
        }
    }

    @Test
    void testGetProducersAwardsIntervals_ValidatesCountOfProducers() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/producers/awards-intervals")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ProducerAwardsIntervalDTO response = objectMapper.readValue(jsonResponse, ProducerAwardsIntervalDTO.class);

        assertEquals(1, response.getMin().size(),
            "Expected exactly 1 producer with minimum interval based on current CSV data");

        assertEquals(1, response.getMax().size(),
            "Expected exactly 1 producer with maximum interval based on current CSV data");
    }
}
