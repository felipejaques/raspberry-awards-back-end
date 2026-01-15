package org.example.dto;

import java.util.List;

public class ProducerAwardsIntervalDTO {
    private List<ProducerIntervalDTO> min;
    private List<ProducerIntervalDTO> max;

    public ProducerAwardsIntervalDTO() {
    }

    public ProducerAwardsIntervalDTO(List<ProducerIntervalDTO> min, List<ProducerIntervalDTO> max) {
        this.min = min;
        this.max = max;
    }

    public List<ProducerIntervalDTO> getMin() {
        return min;
    }

    public void setMin(List<ProducerIntervalDTO> min) {
        this.min = min;
    }

    public List<ProducerIntervalDTO> getMax() {
        return max;
    }

    public void setMax(List<ProducerIntervalDTO> max) {
        this.max = max;
    }
}
