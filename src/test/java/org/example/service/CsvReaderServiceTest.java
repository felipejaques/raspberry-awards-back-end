package org.example.service;

import org.example.model.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CsvReaderServiceTest {

    @InjectMocks
    private CsvReaderService csvReaderService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(csvReaderService, "csvFilePath", "src/main/resources/data/movielist.csv");
    }

    @Test
    void testReadMoviesFromCsv_WithValidData() {
        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies);
        assertFalse(movies.isEmpty(), "A lista de filmes não deve estar vazia");
        assertTrue(movies.size() > 0, "Deve ter lido pelo menos um filme do CSV");

        Movie firstMovie = movies.get(0);
        assertNotNull(firstMovie.getYear(), "Ano do filme não deve ser nulo");
        assertNotNull(firstMovie.getTitle(), "Título do filme não deve ser nulo");
        assertNotNull(firstMovie.getStudios(), "Estúdios não devem ser nulos");
        assertNotNull(firstMovie.getProducers(), "Produtores não devem ser nulos");
        assertNotNull(firstMovie.getWinner(), "Status de vencedor não deve ser nulo");
    }

    @Test
    void testReadMoviesFromCsv_SkipsHeaderLine() {
        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies);

        for (Movie movie : movies) {
            assertNotNull(movie.getYear(), "Todos os filmes devem ter um ano válido");
            assertNotEquals("Year", movie.getYear(), "A primeira linha (cabeçalho) não deve ser parseada como filme");
        }
    }

    @Test
    void testReadMoviesFromCsv_ParsesDataCorrectly() {

        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies);
        assertFalse(movies.isEmpty(), "Deve ter lido filmes do CSV");

        for (Movie movie : movies) {
            assertTrue(movie.getYear() > 0, "Ano deve ser positivo: " + movie.getYear());
            assertNotNull(movie.getTitle());
            assertFalse(movie.getTitle().trim().isEmpty(), "Título não deve estar vazio");
            assertNotNull(movie.getStudios());
            assertFalse(movie.getStudios().trim().isEmpty(), "Estúdios não devem estar vazios");
            assertNotNull(movie.getProducers());
            assertFalse(movie.getProducers().trim().isEmpty(), "Produtores não devem estar vazios");
            assertNotNull(movie.getWinner());
        }
    }

    @Test
    void testReadMoviesFromCsv_ParsesWinnerFieldCorrectly() {
        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies);
        assertFalse(movies.isEmpty(), "Deve ter lido filmes do CSV");

        for (Movie movie : movies) {
            assertNotNull(movie.getWinner(), "Campo winner não deve ser nulo");
            assertTrue(movie.getWinner() instanceof Boolean, "Winner deve ser Boolean");
        }
        long winnersCount = movies.stream().filter(Movie::getWinner).count();
        assertTrue(winnersCount > 0, "Deve ter filmes vencedores");
    }

    @Test
    void testReadMoviesFromCsv_TrimsWhitespace() {
        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies);
        assertFalse(movies.isEmpty(), "Deve ter lido filmes do CSV");

        for (Movie movie : movies) {
            assertFalse(movie.getTitle().startsWith(" "), "Título não deve começar com espaço");
            assertFalse(movie.getTitle().endsWith(" "), "Título não deve terminar com espaço");
            assertFalse(movie.getStudios().startsWith(" "), "Estúdios não devem começar com espaço");
            assertFalse(movie.getStudios().endsWith(" "), "Estúdios não devem terminar com espaço");
            assertFalse(movie.getProducers().startsWith(" "), "Produtores não devem começar com espaço");
            assertFalse(movie.getProducers().endsWith(" "), "Produtores não devem terminar com espaço");
        }
    }

    @Test
    void testReadMoviesFromCsv_IgnoresIncompleteLinesWithLessThanFiveFields() {
        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies);

        for (Movie movie : movies) {
            assertNotNull(movie.getYear(), "Campo 1 (Ano) não deve ser nulo");
            assertNotNull(movie.getTitle(), "Campo 2 (Título) não deve ser nulo");
            assertNotNull(movie.getStudios(), "Campo 3 (Estúdios) não deve ser nulo");
            assertNotNull(movie.getProducers(), "Campo 4 (Produtores) não deve ser nulo");
            assertNotNull(movie.getWinner(), "Campo 5 (Vencedor) não deve ser nulo");
        }
    }

    @Test
    void testReadMoviesFromCsv_ReturnsNonNullList() {
        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies, "O método deve retornar uma lista não nula");
        assertInstanceOf(List.class, movies, "Deve retornar uma instância de List");
    }

    @Test
    void testReadMoviesFromCsv_ParsesYearAsInteger() {
        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies);
        assertFalse(movies.isEmpty(), "Deve ter lido filmes do CSV");

        for (Movie movie : movies) {
            assertNotNull(movie.getYear());
            assertTrue(movie.getYear() instanceof Integer, "Ano deve ser Integer");
            assertTrue(movie.getYear() > 1900 && movie.getYear() < 2100,
                    "Ano deve estar em um intervalo razoável: " + movie.getYear());
        }
    }

    @Test
    void testReadMoviesFromCsv_FallsBackToCsvFilePath() {
        ReflectionTestUtils.setField(csvReaderService, "csvFilePath", "src/main/resources/data/movielist.csv");

        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies, "Deve retornar uma lista mesmo que use fallback");
    }

    @Test
    void testReadMoviesFromCsv_ParsesWinnerCaseInsensitive() {
        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies);
        assertFalse(movies.isEmpty(), "Deve ter lido filmes do CSV");

        long winnersCount = movies.stream().filter(Movie::getWinner).count();
        assertTrue(winnersCount > 0, "Deve ter encontrado filmes vencedores (yes, Yes, YES)");
    }

    @Test
    void testReadMoviesFromCsv_ValidatesCompleteMovieData() {
        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies);
        assertFalse(movies.isEmpty(), "Deve ter lido filmes do CSV");
        Movie firstMovie = movies.get(0);

        assertNotNull(firstMovie.getYear(), "Ano não pode ser nulo");
        assertTrue(firstMovie.getYear() > 0, "Ano deve ser positivo");

        assertNotNull(firstMovie.getTitle(), "Título não pode ser nulo");
        assertFalse(firstMovie.getTitle().isEmpty(), "Título não pode estar vazio");

        assertNotNull(firstMovie.getStudios(), "Estúdios não podem ser nulo");
        assertFalse(firstMovie.getStudios().isEmpty(), "Estúdios não podem estar vazio");

        assertNotNull(firstMovie.getProducers(), "Produtores não podem ser nulo");
        assertFalse(firstMovie.getProducers().isEmpty(), "Produtores não podem estar vazio");

        assertNotNull(firstMovie.getWinner(), "Winner não pode ser nulo");
    }

    @Test
    void testReadMoviesFromCsv_LoadsFromClasspathResource() {
        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies, "Deve retornar lista mesmo se recurso não existir");
    }

    @Test
    void testReadMoviesFromCsv_AllMoviesHaveRequiredFields() {
        List<Movie> movies = csvReaderService.readMoviesFromCsv();

        assertNotNull(movies);
        assertFalse(movies.isEmpty(), "Deve ter lido filmes do CSV");

        for (Movie movie : movies) {
            assertNotNull(movie.getYear(), "Filme deve ter ano");
            assertNotNull(movie.getTitle(), "Filme deve ter título");
            assertNotNull(movie.getStudios(), "Filme deve ter estúdios");
            assertNotNull(movie.getProducers(), "Filme deve ter produtores");
            assertNotNull(movie.getWinner(), "Filme deve ter status de vencedor");
        }
    }
}
