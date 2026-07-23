package com.deskflow.desk;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence for {@link Desk}. JpaRepository already gives us findById / findAll /
 * save / deleteById; the methods below are derived queries used by the desk-list
 * and available-desk features.
 */
public interface DeskRepository extends JpaRepository<Desk, Long> {

    List<Desk> findByFloor(int floor);

    List<Desk> findByHasMonitor(boolean hasMonitor);

    List<Desk> findByFloorAndHasMonitor(int floor, boolean hasMonitor);

    List<Desk> findByActiveTrue();
}
