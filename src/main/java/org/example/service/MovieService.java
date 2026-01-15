package org.example.service;

import org.example.dto.ProducerAwardsIntervalDTO;
import org.example.dto.ProducerIntervalDTO;
import org.example.model.Movie;
import org.example.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public ProducerAwardsIntervalDTO getProducersWithMinMaxAwardsInterval() {
        List<Movie> winners = movieRepository.findByWinnerTrue();
        
        Map<String, List<Integer>> producerWins = new HashMap<>();
        
        for (Movie movie : winners) {
            String[] producers = parseProducers(movie.getProducers());
            for (String producer : producers) {
                producerWins.computeIfAbsent(producer, k -> new ArrayList<>()).add(movie.getYear());
            }
        }
        
        List<ProducerIntervalDTO> allIntervals = new ArrayList<>();
        
        for (Map.Entry<String, List<Integer>> entry : producerWins.entrySet()) {
            String producer = entry.getKey();
            List<Integer> years = entry.getValue();
            
            if (years.size() > 1) {
                Collections.sort(years);
                for (int i = 1; i < years.size(); i++) {
                    int interval = years.get(i) - years.get(i - 1);
                    allIntervals.add(new ProducerIntervalDTO(
                        producer,
                        interval,
                        years.get(i - 1),
                        years.get(i)
                    ));
                }
            }
        }
        
        if (allIntervals.isEmpty()) {
            return new ProducerAwardsIntervalDTO(new ArrayList<>(), new ArrayList<>());
        }
        
        int minInterval = allIntervals.stream()
            .mapToInt(ProducerIntervalDTO::getInterval)
            .min()
            .orElse(0);
        
        int maxInterval = allIntervals.stream()
            .mapToInt(ProducerIntervalDTO::getInterval)
            .max()
            .orElse(0);
        
        List<ProducerIntervalDTO> minIntervals = allIntervals.stream()
            .filter(p -> p.getInterval() == minInterval)
            .collect(Collectors.toList());
        
        List<ProducerIntervalDTO> maxIntervals = allIntervals.stream()
            .filter(p -> p.getInterval() == maxInterval)
            .collect(Collectors.toList());
        
        return new ProducerAwardsIntervalDTO(minIntervals, maxIntervals);
    }
    
    /**
     * Analisa a string de produtores e retorna um array com os nomes individuais.
     * Trata separadores como ", ", " and ", " e ".
     */
    private String[] parseProducers(String producersString) {
        if (producersString == null || producersString.trim().isEmpty()) {
            return new String[0];
        }
        
        String normalized = producersString
            .replaceAll(" and ", ",")
            .replaceAll(" e ", ",");
        
        return Arrays.stream(normalized.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toArray(String[]::new);
    }
}
