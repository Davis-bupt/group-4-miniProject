package com.deskflow.desk;

import com.deskflow.desk.dto.DeskResponse;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/desks")
public class DeskController {

    private final DeskRepository deskRepository;
    private final DeskService deskService;

    public DeskController(DeskRepository deskRepository, DeskService deskService) {
        this.deskRepository = deskRepository;
        this.deskService = deskService;
    }

    @GetMapping
    public List<DeskResponse> listDesks(
        @RequestParam(required = false) Integer floor,
        @RequestParam(required = false) Boolean hasMonitor
    ) {
        List<Desk> desks;
        if (floor != null && hasMonitor != null) {
            desks = deskRepository.findByFloorAndHasMonitor(floor, hasMonitor);
        } else if (floor != null) {
            desks = deskRepository.findByFloor(floor);
        } else if (hasMonitor != null) {
            desks = deskRepository.findByHasMonitor(hasMonitor);
        } else {
            desks = deskRepository.findAll();
        }

        return desks.stream()
            .map(desk -> new DeskResponse(
                desk.getId(),
                desk.getCode(),
                desk.getFloor(),
                desk.isHasMonitor(),
                desk.isActive()
            ))
            .toList();
    }

    /**
     * Creative endpoint C: desks that are active and free on the given date.
     * {@code date} is required (missing -> 400); {@code floor} is an optional filter.
     */
    @GetMapping("/available")
    public List<DeskResponse> availableDesks(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam(required = false) Integer floor
    ) {
        return deskService.findAvailable(date, floor);
    }
}
