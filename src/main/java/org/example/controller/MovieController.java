package org.example.controller;

import org.example.dto.ProducerAwardsIntervalDTO;
import org.example.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/producers/awards-intervals")
    public ResponseEntity<ProducerAwardsIntervalDTO> getProducersAwardsIntervals() {
        ProducerAwardsIntervalDTO intervals = movieService.getProducersWithMinMaxAwardsInterval();
        return ResponseEntity.ok(intervals);
    }
}
