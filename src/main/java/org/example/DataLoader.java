package org.example;

import org.example.model.Movie;
import org.example.repository.MovieRepository;
import org.example.service.CsvReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(10)
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CsvReaderService csvReaderService;

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            Thread.sleep(500);
            
            List<Movie> movies = csvReaderService.readMoviesFromCsv();
            movieRepository.saveAll(movies);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
