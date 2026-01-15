package org.example.repository;

import org.example.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    private Movie winnerMovie;
    private Movie loserMovie;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();

        winnerMovie = new Movie(1990, "Dances with Wolves", "Orion Pictures", "Kevin Costner, Jim Wilson", true);
        loserMovie = new Movie(1992, "Unforgiven", "Warner Bros.", "Clint Eastwood", false);

        movieRepository.save(winnerMovie);
        movieRepository.save(loserMovie);
    }

    @Test
    void testFindByWinnerTrue_ReturnWinners() {
        List<Movie> winners = movieRepository.findByWinnerTrue();

        assertNotNull(winners);
        assertFalse(winners.isEmpty());
        assertEquals(1, winners.size());
        assertTrue(winners.get(0).getWinner());
        assertEquals("Dances with Wolves", winners.get(0).getTitle());
    }

    @Test
    void testFindByWinnerTrue_WithNoWinners() {
        movieRepository.deleteAll();
        Movie movie = new Movie(1995, "Test Movie", "Studio", "Producer", false);
        movieRepository.save(movie);

        List<Movie> winners = movieRepository.findByWinnerTrue();

        assertNotNull(winners);
        assertTrue(winners.isEmpty());
    }

    @Test
    void testFindByWinnerTrue_WithMultipleWinners() {
        Movie winner2 = new Movie(1994, "Forrest Gump", "Paramount", "Wendy Finerman, Gary Goetzman", true);
        Movie winner3 = new Movie(1997, "Titanic", "20th Century Fox", "James Cameron, James Horner", true);

        movieRepository.save(winner2);
        movieRepository.save(winner3);

        List<Movie> winners = movieRepository.findByWinnerTrue();

        assertNotNull(winners);
        assertEquals(3, winners.size());
        for (Movie winner : winners) {
            assertTrue(winner.getWinner());
        }
    }

    @Test
    void testFindAllWinnersOrderedByProducerAndYear_ReturnsWinners() {
        List<Movie> winners = movieRepository.findAllWinnersOrderedByProducerAndYear();

        assertNotNull(winners);
        assertFalse(winners.isEmpty());
        assertEquals(1, winners.size());
        assertTrue(winners.get(0).getWinner());
    }

    @Test
    void testFindAllWinnersOrderedByProducerAndYear_WithMultipleWinners() {
        Movie winner2 = new Movie(1994, "Forrest Gump", "Paramount", "Wendy Finerman", true);
        Movie winner3 = new Movie(1990, "Another Winner", "Studio", "Kevin Costner", true);

        movieRepository.save(winner2);
        movieRepository.save(winner3);

        List<Movie> winners = movieRepository.findAllWinnersOrderedByProducerAndYear();

        assertNotNull(winners);
        assertEquals(3, winners.size());
        for (Movie winner : winners) {
            assertTrue(winner.getWinner());
        }
    }

    @Test
    void testFindAllWinnersOrderedByProducerAndYear_WithNoWinners() {
        movieRepository.deleteAll();
        Movie movie = new Movie(1990, "Test", "Studio", "Producer", false);
        movieRepository.save(movie);

        List<Movie> winners = movieRepository.findAllWinnersOrderedByProducerAndYear();

        assertNotNull(winners);
        assertTrue(winners.isEmpty());
    }

    @Test
    void testSaveMovie() {
        Movie newMovie = new Movie(2000, "Gladiator", "DreamWorks", "Ridley Scott", true);

        Movie savedMovie = movieRepository.save(newMovie);

        assertNotNull(savedMovie.getId());
        assertEquals("Gladiator", savedMovie.getTitle());
        assertEquals(2000, savedMovie.getYear());
    }

    @Test
    void testFindById() {
        Movie savedMovie = movieRepository.save(new Movie(2005, "Crash", "Lions Gate Films", "Paul Haggis", true));

        Optional<Movie> foundMovie = movieRepository.findById(savedMovie.getId());

        assertTrue(foundMovie.isPresent());
        assertEquals("Crash", foundMovie.get().getTitle());
        assertEquals(2005, foundMovie.get().getYear());
    }

    @Test
    void testFindById_NotFound() {
        Optional<Movie> foundMovie = movieRepository.findById(999L);

        assertFalse(foundMovie.isPresent());
    }

    @Test
    void testUpdateMovie() {
        Movie savedMovie = movieRepository.save(new Movie(1995, "Braveheart", "Icon Productions", "Mel Gibson", true));
        
        savedMovie.setTitle("Braveheart - Updated");
        movieRepository.save(savedMovie);

        Optional<Movie> updatedMovie = movieRepository.findById(savedMovie.getId());

        assertTrue(updatedMovie.isPresent());
        assertEquals("Braveheart - Updated", updatedMovie.get().getTitle());
    }

    @Test
    void testDeleteMovie() {
        Movie savedMovie = movieRepository.save(new Movie(2010, "The King's Speech", "The Weinstein Company", "Geoffrey Rush", true));
        Long movieId = savedMovie.getId();

        movieRepository.delete(savedMovie);

        Optional<Movie> deletedMovie = movieRepository.findById(movieId);
        assertFalse(deletedMovie.isPresent());
    }

    @Test
    void testFindAll() {
        Movie winner2 = new Movie(1994, "Forrest Gump", "Paramount", "Wendy Finerman", true);
        movieRepository.save(winner2);

        List<Movie> allMovies = movieRepository.findAll();

        assertNotNull(allMovies);
        assertEquals(3, allMovies.size());
    }

    @Test
    void testMovieFieldsArePersisted() {
        Movie movie = new Movie(1988, "Rain Man", "United Artists", "Mark Johnson", true);
        movie.setYear(1988);
        movie.setTitle("Rain Man");
        movie.setStudios("United Artists");
        movie.setProducers("Mark Johnson");
        movie.setWinner(true);

        Movie savedMovie = movieRepository.save(movie);

        Optional<Movie> retrievedMovie = movieRepository.findById(savedMovie.getId());

        assertTrue(retrievedMovie.isPresent());
        Movie m = retrievedMovie.get();
        assertEquals(1988, m.getYear());
        assertEquals("Rain Man", m.getTitle());
        assertEquals("United Artists", m.getStudios());
        assertEquals("Mark Johnson", m.getProducers());
        assertTrue(m.getWinner());
    }

    @Test
    void testFindByWinnerTrue_IgnoresNonWinners() {
        movieRepository.deleteAll();

        Movie winner1 = new Movie(1990, "Winner 1", "Studio", "Producer", true);
        Movie loser1 = new Movie(1991, "Loser 1", "Studio", "Producer", false);
        Movie winner2 = new Movie(1992, "Winner 2", "Studio", "Producer", true);
        Movie loser2 = new Movie(1993, "Loser 2", "Studio", "Producer", false);

        movieRepository.save(winner1);
        movieRepository.save(loser1);
        movieRepository.save(winner2);
        movieRepository.save(loser2);

        List<Movie> winners = movieRepository.findByWinnerTrue();

        assertEquals(2, winners.size());
        for (Movie winner : winners) {
            assertTrue(winner.getWinner());
        }
    }

    @Test
    void testFindByWinnerFalse() {
        movieRepository.deleteAll();

        Movie loser1 = new Movie(1991, "Loser 1", "Studio", "Producer", false);
        Movie winner1 = new Movie(1990, "Winner 1", "Studio", "Producer", true);
        Movie loser2 = new Movie(1993, "Loser 2", "Studio", "Producer", false);

        movieRepository.save(loser1);
        movieRepository.save(winner1);
        movieRepository.save(loser2);

        List<Movie> losers = movieRepository.findByWinnerTrue();

        assertEquals(1, losers.size());
        assertTrue(losers.get(0).getWinner());
    }

    @Test
    void testDeleteAll() {
        movieRepository.deleteAll();

        List<Movie> movies = movieRepository.findAll();

        assertTrue(movies.isEmpty());
    }

    @Test
    void testCountWinners() {
        movieRepository.deleteAll();

        movieRepository.save(new Movie(1990, "Movie 1", "Studio", "Producer", true));
        movieRepository.save(new Movie(1991, "Movie 2", "Studio", "Producer", false));
        movieRepository.save(new Movie(1992, "Movie 3", "Studio", "Producer", true));

        List<Movie> winners = movieRepository.findByWinnerTrue();

        assertEquals(2, winners.size());
    }

    @Test
    void testFindAllWinnersOrderedByProducerAndYear_OrderingCorrect() {
        movieRepository.deleteAll();

        movieRepository.save(new Movie(2000, "Movie Z", "Studio", "Producer C", true));
        movieRepository.save(new Movie(1990, "Movie B", "Studio", "Producer A", true));
        movieRepository.save(new Movie(1995, "Movie A", "Studio", "Producer B", true));
        movieRepository.save(new Movie(1988, "Movie Y", "Studio", "Producer A", true));

        List<Movie> winners = movieRepository.findAllWinnersOrderedByProducerAndYear();

        assertNotNull(winners);
        assertEquals(4, winners.size());
        
        for (Movie winner : winners) {
            assertTrue(winner.getWinner());
        }
    }
}
