package org.example.service;

import org.example.dto.ProducerAwardsIntervalDTO;
import org.example.dto.ProducerIntervalDTO;
import org.example.model.Movie;
import org.example.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private List<Movie> winnerMovies;

    @BeforeEach
    void setUp() {
        winnerMovies = new ArrayList<>();
    }

    @Test
    void testGetProducersWithMinMaxAwardsInterval_WithMultipleProducers() {

        Movie movie1 = createMovie(1990, "Producer A", true);
        Movie movie2 = createMovie(1991, "Producer A", true);
        Movie movie3 = createMovie(2002, "Producer B", true);
        Movie movie4 = createMovie(2015, "Producer B", true);
        
        winnerMovies.addAll(Arrays.asList(movie1, movie2, movie3, movie4));
        when(movieRepository.findByWinnerTrue()).thenReturn(winnerMovies);

        ProducerAwardsIntervalDTO result = movieService.getProducersWithMinMaxAwardsInterval();

        assertNotNull(result);
        assertNotNull(result.getMin());
        assertNotNull(result.getMax());
        
        assertEquals(1, result.getMin().size());
        assertEquals(1, result.getMax().size());
        
        ProducerIntervalDTO minInterval = result.getMin().get(0);
        assertEquals("Producer A", minInterval.getProducer());
        assertEquals(1, minInterval.getInterval());
        assertEquals(1990, minInterval.getPreviousWin());
        assertEquals(1991, minInterval.getFollowingWin());
        
        ProducerIntervalDTO maxInterval = result.getMax().get(0);
        assertEquals("Producer B", maxInterval.getProducer());
        assertEquals(13, maxInterval.getInterval());
        assertEquals(2002, maxInterval.getPreviousWin());
        assertEquals(2015, maxInterval.getFollowingWin());
        
        verify(movieRepository, times(1)).findByWinnerTrue();
    }

    @Test
    void testGetProducersWithMinMaxAwardsInterval_WithSingleWin() {
        Movie movie1 = createMovie(1990, "Producer Single", true);
        winnerMovies.add(movie1);
        when(movieRepository.findByWinnerTrue()).thenReturn(winnerMovies);
        ProducerAwardsIntervalDTO result = movieService.getProducersWithMinMaxAwardsInterval();
        assertNotNull(result);
        assertTrue(result.getMin().isEmpty());
        assertTrue(result.getMax().isEmpty());
        
        verify(movieRepository, times(1)).findByWinnerTrue();
    }

    @Test
    void testGetProducersWithMinMaxAwardsInterval_WithNoWinners() {
        when(movieRepository.findByWinnerTrue()).thenReturn(new ArrayList<>());

        ProducerAwardsIntervalDTO result = movieService.getProducersWithMinMaxAwardsInterval();
        assertNotNull(result);
        assertTrue(result.getMin().isEmpty());
        assertTrue(result.getMax().isEmpty());
        
        verify(movieRepository, times(1)).findByWinnerTrue();
    }

    @Test
    void testGetProducersWithMinMaxAwardsInterval_WithMultipleWinsForSameProducer() {
        Movie movie1 = createMovie(1990, "Producer Multi", true);
        Movie movie2 = createMovie(1995, "Producer Multi", true);
        Movie movie3 = createMovie(2000, "Producer Multi", true);
        
        winnerMovies.addAll(Arrays.asList(movie1, movie2, movie3));
        when(movieRepository.findByWinnerTrue()).thenReturn(winnerMovies);

        ProducerAwardsIntervalDTO result = movieService.getProducersWithMinMaxAwardsInterval();

        assertNotNull(result);
        assertNotNull(result.getMin());
        assertNotNull(result.getMax());
        
        assertEquals(2, result.getMin().size());
        assertEquals(2, result.getMax().size());
        
        result.getMin().forEach(interval -> {
            assertEquals("Producer Multi", interval.getProducer());
            assertEquals(5, interval.getInterval());
        });
        
        verify(movieRepository, times(1)).findByWinnerTrue();
    }

    @Test
    void testGetProducersWithMinMaxAwardsInterval_WithAndSeparator() {
        Movie movie1 = createMovie(1990, "Producer A and Producer B", true);
        Movie movie2 = createMovie(1995, "Producer A", true);
        Movie movie3 = createMovie(2000, "Producer B", true);
        
        winnerMovies.addAll(Arrays.asList(movie1, movie2, movie3));
        when(movieRepository.findByWinnerTrue()).thenReturn(winnerMovies);

        ProducerAwardsIntervalDTO result = movieService.getProducersWithMinMaxAwardsInterval();

        assertNotNull(result);
        
        assertEquals(1, result.getMin().size());
        assertEquals(1, result.getMax().size());
        
        ProducerIntervalDTO minInterval = result.getMin().get(0);
        assertEquals("Producer A", minInterval.getProducer());
        assertEquals(5, minInterval.getInterval());
        
        ProducerIntervalDTO maxInterval = result.getMax().get(0);
        assertEquals("Producer B", maxInterval.getProducer());
        assertEquals(10, maxInterval.getInterval());
        
        verify(movieRepository, times(1)).findByWinnerTrue();
    }

    @Test
    void testGetProducersWithMinMaxAwardsInterval_WithCommaSeparator() {
        Movie movie1 = createMovie(1990, "Producer X, Producer Y", true);
        Movie movie2 = createMovie(1992, "Producer X", true);
        Movie movie3 = createMovie(2000, "Producer Y", true);
        
        winnerMovies.addAll(Arrays.asList(movie1, movie2, movie3));
        when(movieRepository.findByWinnerTrue()).thenReturn(winnerMovies);

        ProducerAwardsIntervalDTO result = movieService.getProducersWithMinMaxAwardsInterval();

        assertNotNull(result);
        assertEquals(1, result.getMin().size());
        assertEquals(1, result.getMax().size());
        
        ProducerIntervalDTO minInterval = result.getMin().get(0);
        assertEquals("Producer X", minInterval.getProducer());
        assertEquals(2, minInterval.getInterval());
        
        ProducerIntervalDTO maxInterval = result.getMax().get(0);
        assertEquals("Producer Y", maxInterval.getProducer());
        assertEquals(10, maxInterval.getInterval());
        
        verify(movieRepository, times(1)).findByWinnerTrue();
    }

    @Test
    void testGetProducersWithMinMaxAwardsInterval_WithPortugueseSeparator() {
        Movie movie1 = createMovie(1990, "Producer M e Producer N", true);
        Movie movie2 = createMovie(1993, "Producer M", true);
        Movie movie3 = createMovie(2005, "Producer N", true);
        
        winnerMovies.addAll(Arrays.asList(movie1, movie2, movie3));
        when(movieRepository.findByWinnerTrue()).thenReturn(winnerMovies);

        ProducerAwardsIntervalDTO result = movieService.getProducersWithMinMaxAwardsInterval();

        assertNotNull(result);
        assertEquals(1, result.getMin().size());
        assertEquals(1, result.getMax().size());
        
        ProducerIntervalDTO minInterval = result.getMin().get(0);
        assertEquals("Producer M", minInterval.getProducer());
        assertEquals(3, minInterval.getInterval());
        
        ProducerIntervalDTO maxInterval = result.getMax().get(0);
        assertEquals("Producer N", maxInterval.getProducer());
        assertEquals(15, maxInterval.getInterval());
        
        verify(movieRepository, times(1)).findByWinnerTrue();
    }

    @Test
    void testGetProducersWithMinMaxAwardsInterval_WithSameIntervals() {
        Movie movie1 = createMovie(1990, "Producer Alpha", true);
        Movie movie2 = createMovie(1995, "Producer Alpha", true);
        Movie movie3 = createMovie(2000, "Producer Beta", true);
        Movie movie4 = createMovie(2005, "Producer Beta", true);
        
        winnerMovies.addAll(Arrays.asList(movie1, movie2, movie3, movie4));
        when(movieRepository.findByWinnerTrue()).thenReturn(winnerMovies);

        ProducerAwardsIntervalDTO result = movieService.getProducersWithMinMaxAwardsInterval();

        assertNotNull(result);
        
        assertEquals(2, result.getMin().size());
        assertEquals(2, result.getMax().size());
        
        result.getMin().forEach(interval -> assertEquals(5, interval.getInterval()));
        result.getMax().forEach(interval -> assertEquals(5, interval.getInterval()));
        
        verify(movieRepository, times(1)).findByWinnerTrue();
    }

    @Test
    void testGetProducersWithMinMaxAwardsInterval_WithEmptyProducersString() {
        Movie movie1 = createMovie(1990, "", true);
        Movie movie2 = createMovie(1995, null, true);
        
        winnerMovies.addAll(Arrays.asList(movie1, movie2));
        when(movieRepository.findByWinnerTrue()).thenReturn(winnerMovies);
        ProducerAwardsIntervalDTO result = movieService.getProducersWithMinMaxAwardsInterval();

        assertNotNull(result);
        assertTrue(result.getMin().isEmpty());
        assertTrue(result.getMax().isEmpty());
        
        verify(movieRepository, times(1)).findByWinnerTrue();
    }

    @Test
    void testGetProducersWithMinMaxAwardsInterval_WithMixedSeparators() {
        Movie movie1 = createMovie(1990, "Producer A, Producer B and Producer C", true);
        Movie movie2 = createMovie(1991, "Producer A", true);
        Movie movie3 = createMovie(2000, "Producer B", true);
        Movie movie4 = createMovie(2010, "Producer C", true);
        
        winnerMovies.addAll(Arrays.asList(movie1, movie2, movie3, movie4));
        when(movieRepository.findByWinnerTrue()).thenReturn(winnerMovies);

        ProducerAwardsIntervalDTO result = movieService.getProducersWithMinMaxAwardsInterval();

        assertNotNull(result);
        
        assertEquals(1, result.getMin().size());
        assertEquals(1, result.getMax().size());
        
        ProducerIntervalDTO minInterval = result.getMin().get(0);
        assertEquals("Producer A", minInterval.getProducer());
        assertEquals(1, minInterval.getInterval());
        
        ProducerIntervalDTO maxInterval = result.getMax().get(0);
        assertEquals("Producer C", maxInterval.getProducer());
        assertEquals(20, maxInterval.getInterval());
        
        verify(movieRepository, times(1)).findByWinnerTrue();
    }

    @Test
    void testGetProducersWithMinMaxAwardsInterval_WithUnsortedYears() {
        Movie movie1 = createMovie(2000, "Producer Z", true);
        Movie movie2 = createMovie(1990, "Producer Z", true);
        Movie movie3 = createMovie(1995, "Producer Z", true);
        
        winnerMovies.addAll(Arrays.asList(movie1, movie2, movie3));
        when(movieRepository.findByWinnerTrue()).thenReturn(winnerMovies);

        ProducerAwardsIntervalDTO result = movieService.getProducersWithMinMaxAwardsInterval();

        assertNotNull(result);
        
        assertEquals(2, result.getMin().size());
        assertEquals(2, result.getMax().size());
        
        result.getMin().forEach(interval -> {
            assertEquals("Producer Z", interval.getProducer());
            assertEquals(5, interval.getInterval());
        });
        
        verify(movieRepository, times(1)).findByWinnerTrue();
    }

    @Test
    void testGetProducersWithMinMaxAwardsInterval_WithExtraSpaces() {
        Movie movie1 = createMovie(1990, "  Producer W  ,  Producer V  ", true);
        Movie movie2 = createMovie(1993, "Producer W", true);
        Movie movie3 = createMovie(2001, "Producer V", true);
        
        winnerMovies.addAll(Arrays.asList(movie1, movie2, movie3));
        when(movieRepository.findByWinnerTrue()).thenReturn(winnerMovies);

        ProducerAwardsIntervalDTO result = movieService.getProducersWithMinMaxAwardsInterval();

        assertNotNull(result);
        assertEquals(1, result.getMin().size());
        assertEquals(1, result.getMax().size());
        
        ProducerIntervalDTO minInterval = result.getMin().get(0);
        assertEquals("Producer W", minInterval.getProducer());
        assertEquals(3, minInterval.getInterval());
        
        ProducerIntervalDTO maxInterval = result.getMax().get(0);
        assertEquals("Producer V", maxInterval.getProducer());
        assertEquals(11, maxInterval.getInterval());
        
        verify(movieRepository, times(1)).findByWinnerTrue();
    }

    private Movie createMovie(Integer year, String producers, Boolean winner) {
        return new Movie(year, "Movie Title", "Studio", producers, winner);
    }
}
