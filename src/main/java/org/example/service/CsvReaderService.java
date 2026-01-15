package org.example.service;

import org.example.model.Movie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvReaderService {

    @Value("${spring.csv.filepath}")
    private String csvFilePath;

    public List<Movie> readMoviesFromCsv() {
        List<Movie> movies = new ArrayList<>();
        String line;
        boolean isFirstLine = true;

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/movielist.csv");
            
            BufferedReader br;
            if (inputStream != null) {
                br = new BufferedReader(new InputStreamReader(inputStream));
            } else {
                br = new BufferedReader(new FileReader(csvFilePath));
            }

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] movieData = line.split(";");
                if (movieData.length >= 5) {
                    Movie movie = new Movie();
                    movie.setYear(Integer.parseInt(movieData[0].trim()));
                    movie.setTitle(movieData[1].trim());
                    movie.setStudios(movieData[2].trim());
                    movie.setProducers(movieData[3].trim());
                    movie.setWinner("yes".equalsIgnoreCase(movieData[4].trim()));
                    movies.add(movie);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao ler arquivo CSV: " + e.getMessage());
        }

        return movies;
    }
}
