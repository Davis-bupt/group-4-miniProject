package com.deskflow.desk;

import com.deskflow.desk.dto.DeskResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/desks")
public class DeskController {

    private final DeskRepository deskRepository;

    public DeskController(DeskRepository deskRepository) {
        this.deskRepository = deskRepository;
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
}
