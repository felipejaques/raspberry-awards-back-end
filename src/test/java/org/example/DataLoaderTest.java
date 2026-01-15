package org.example;

import org.example.model.Movie;
import org.example.repository.MovieRepository;
import org.example.service.CsvReaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataLoaderTest {

    @Mock
    private CsvReaderService csvReaderService;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private DataLoader dataLoader;

    private List<Movie> testMovies;

    @BeforeEach
    void setUp() {
        testMovies = new ArrayList<>();
        testMovies.add(new Movie(1990, "Dances with Wolves", "Orion", "Kevin Costner", true));
        testMovies.add(new Movie(1991, "The Silence of the Lambs", "Orion", "Scott Ferguson", false));
        testMovies.add(new Movie(1992, "Unforgiven", "Warner Bros.", "Clint Eastwood", true));
    }

    @Test
    void testRun_LoadsMoviesSuccessfully() throws Exception {
        when(csvReaderService.readMoviesFromCsv()).thenReturn(testMovies);

        dataLoader.run();

        verify(csvReaderService, times(1)).readMoviesFromCsv();
        verify(movieRepository, times(1)).saveAll(testMovies);
    }

    @Test
    void testRun_CallsCsvReaderService() throws Exception {
        when(csvReaderService.readMoviesFromCsv()).thenReturn(testMovies);

        dataLoader.run();

        verify(csvReaderService).readMoviesFromCsv();
    }

    @Test
    void testRun_CallsRepositorySaveAll() throws Exception {
        when(csvReaderService.readMoviesFromCsv()).thenReturn(testMovies);

        dataLoader.run();

        verify(movieRepository).saveAll(testMovies);
    }

    @Test
    void testRun_WithEmptyMovieList() throws Exception {
        when(csvReaderService.readMoviesFromCsv()).thenReturn(new ArrayList<>());

        dataLoader.run();

        verify(csvReaderService, times(1)).readMoviesFromCsv();
        verify(movieRepository, times(1)).saveAll(new ArrayList<>());
    }

    @Test
    void testRun_WithSingleMovie() throws Exception {
        List<Movie> singleMovie = Arrays.asList(testMovies.get(0));
        when(csvReaderService.readMoviesFromCsv()).thenReturn(singleMovie);

        dataLoader.run();

        verify(csvReaderService, times(1)).readMoviesFromCsv();
        verify(movieRepository, times(1)).saveAll(singleMovie);
    }

    @Test
    void testRun_SavesCorrectNumberOfMovies() throws Exception {
        when(csvReaderService.readMoviesFromCsv()).thenReturn(testMovies);

        dataLoader.run();

        verify(movieRepository).saveAll(testMovies);
    }

    @Test
    void testRun_CsvReaderIsCalledBeforeSave() throws Exception {
        when(csvReaderService.readMoviesFromCsv()).thenReturn(testMovies);

        dataLoader.run();

        InOrder inOrder = inOrder(csvReaderService, movieRepository);
        inOrder.verify(csvReaderService).readMoviesFromCsv();
        inOrder.verify(movieRepository).saveAll(testMovies);
    }

    @Test
    void testRun_SleepsBeforeLoading() throws Exception {
        when(csvReaderService.readMoviesFromCsv()).thenReturn(testMovies);
        long startTime = System.currentTimeMillis();

        dataLoader.run();

        long elapsedTime = System.currentTimeMillis() - startTime;
        assertTrue(elapsedTime >= 400, "Should sleep for at least 500ms minus some tolerance");
    }

    @Test
    void testRun_RepositoryReceivesAllMovies() throws Exception {
        when(csvReaderService.readMoviesFromCsv()).thenReturn(testMovies);

        dataLoader.run();

        verify(movieRepository).saveAll(testMovies);
    }

    @Test
    void testRun_LoaderIsComponent() {
        assertNotNull(dataLoader);
        assertTrue(dataLoader instanceof DataLoader);
    }

    @Test
    void testRun_ImplementsCommandLineRunner() throws Exception {
        assertTrue(dataLoader instanceof CommandLineRunner);
        
        when(csvReaderService.readMoviesFromCsv()).thenReturn(testMovies);
        
        assertDoesNotThrow(() -> dataLoader.run(new String[]{}));
        
        verify(csvReaderService, times(1)).readMoviesFromCsv();
    }

    @Test
    void testRun_LargeMovieList() throws Exception {
        List<Movie> largeMovieList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeMovieList.add(new Movie(1990 + i, "Movie " + i, "Studio " + i, "Producer " + i, true));
        }
        when(csvReaderService.readMoviesFromCsv()).thenReturn(largeMovieList);

        dataLoader.run();

        verify(movieRepository).saveAll(largeMovieList);
    }
}
