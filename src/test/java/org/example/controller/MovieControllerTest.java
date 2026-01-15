package org.example.controller;

import org.example.dto.ProducerAwardsIntervalDTO;
import org.example.dto.ProducerIntervalDTO;
import org.example.service.MovieService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieController movieController;

    @Test
    void testGetProducersAwardsIntervals_Success() {
        List<ProducerIntervalDTO> minIntervals = new ArrayList<>();
        minIntervals.add(new ProducerIntervalDTO("Producer A", 1, 1990, 1991));

        List<ProducerIntervalDTO> maxIntervals = new ArrayList<>();
        maxIntervals.add(new ProducerIntervalDTO("Producer B", 10, 1990, 2000));

        ProducerAwardsIntervalDTO responseDto = new ProducerAwardsIntervalDTO(minIntervals, maxIntervals);

        when(movieService.getProducersWithMinMaxAwardsInterval()).thenReturn(responseDto);

        ResponseEntity<ProducerAwardsIntervalDTO> response = movieController.getProducersAwardsIntervals();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getMin().size());
        assertEquals(1, response.getBody().getMax().size());
        assertEquals("Producer A", response.getBody().getMin().get(0).getProducer());
        assertEquals(1, response.getBody().getMin().get(0).getInterval());
        assertEquals("Producer B", response.getBody().getMax().get(0).getProducer());
        assertEquals(10, response.getBody().getMax().get(0).getInterval());
    }

    @Test
    void testGetProducersAwardsIntervals_WithEmptyResults() {
        ProducerAwardsIntervalDTO responseDto = new ProducerAwardsIntervalDTO(new ArrayList<>(), new ArrayList<>());

        when(movieService.getProducersWithMinMaxAwardsInterval()).thenReturn(responseDto);

        ResponseEntity<ProducerAwardsIntervalDTO> response = movieController.getProducersAwardsIntervals();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMin().isEmpty());
        assertTrue(response.getBody().getMax().isEmpty());
    }

    @Test
    void testGetProducersAwardsIntervals_WithMultipleResults() {
        List<ProducerIntervalDTO> minIntervals = new ArrayList<>();
        minIntervals.add(new ProducerIntervalDTO("Producer A", 1, 1990, 1991));
        minIntervals.add(new ProducerIntervalDTO("Producer B", 1, 2000, 2001));

        List<ProducerIntervalDTO> maxIntervals = new ArrayList<>();
        maxIntervals.add(new ProducerIntervalDTO("Producer C", 15, 1985, 2000));
        maxIntervals.add(new ProducerIntervalDTO("Producer D", 15, 1990, 2005));

        ProducerAwardsIntervalDTO responseDto = new ProducerAwardsIntervalDTO(minIntervals, maxIntervals);

        when(movieService.getProducersWithMinMaxAwardsInterval()).thenReturn(responseDto);

        ResponseEntity<ProducerAwardsIntervalDTO> response = movieController.getProducersAwardsIntervals();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getMin().size());
        assertEquals(2, response.getBody().getMax().size());
        assertEquals(1, response.getBody().getMin().get(0).getInterval());
        assertEquals(1, response.getBody().getMin().get(1).getInterval());
        assertEquals(15, response.getBody().getMax().get(0).getInterval());
        assertEquals(15, response.getBody().getMax().get(1).getInterval());
    }

    @Test
    void testGetProducersAwardsIntervals_ReturnsOkStatus() {
        ProducerAwardsIntervalDTO responseDto = new ProducerAwardsIntervalDTO(new ArrayList<>(), new ArrayList<>());

        when(movieService.getProducersWithMinMaxAwardsInterval()).thenReturn(responseDto);

        ResponseEntity<ProducerAwardsIntervalDTO> response = movieController.getProducersAwardsIntervals();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testGetProducersAwardsIntervals_ResponseContainsMinAndMaxFields() {
        List<ProducerIntervalDTO> minIntervals = new ArrayList<>();
        minIntervals.add(new ProducerIntervalDTO("Producer X", 5, 1995, 2000));

        List<ProducerIntervalDTO> maxIntervals = new ArrayList<>();
        maxIntervals.add(new ProducerIntervalDTO("Producer Y", 20, 1980, 2000));

        ProducerAwardsIntervalDTO responseDto = new ProducerAwardsIntervalDTO(minIntervals, maxIntervals);

        when(movieService.getProducersWithMinMaxAwardsInterval()).thenReturn(responseDto);

        ResponseEntity<ProducerAwardsIntervalDTO> response = movieController.getProducersAwardsIntervals();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getMin());
        assertNotNull(response.getBody().getMax());
    }

    @Test
    void testGetProducersAwardsIntervals_VerifyAllFields() {
        List<ProducerIntervalDTO> minIntervals = new ArrayList<>();
        minIntervals.add(new ProducerIntervalDTO("Test Producer", 3, 2010, 2013));

        ProducerAwardsIntervalDTO responseDto = new ProducerAwardsIntervalDTO(minIntervals, new ArrayList<>());

        when(movieService.getProducersWithMinMaxAwardsInterval()).thenReturn(responseDto);

        ResponseEntity<ProducerAwardsIntervalDTO> response = movieController.getProducersAwardsIntervals();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getMin().isEmpty());
        
        ProducerIntervalDTO producer = response.getBody().getMin().get(0);
        assertEquals("Test Producer", producer.getProducer());
        assertEquals(3, producer.getInterval());
        assertEquals(2010, producer.getPreviousWin());
        assertEquals(2013, producer.getFollowingWin());
    }

    @Test
    void testGetProducersAwardsIntervals_ContentTypeJson() {
        ProducerAwardsIntervalDTO responseDto = new ProducerAwardsIntervalDTO(new ArrayList<>(), new ArrayList<>());

        when(movieService.getProducersWithMinMaxAwardsInterval()).thenReturn(responseDto);

        ResponseEntity<ProducerAwardsIntervalDTO> response = movieController.getProducersAwardsIntervals();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
