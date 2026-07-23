package com.deskflow.desk;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeskRepository extends JpaRepository<Desk, Long> {

    List<Desk> findByFloor(Integer floor);

    List<Desk> findByHasMonitor(Boolean hasMonitor);

    List<Desk> findByFloorAndHasMonitor(Integer floor, Boolean hasMonitor);
}
