package com.cwahler.mpluscalc;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DungeonRepository extends JpaRepository<Dungeon, Long> {
    List<Dungeon> findByNameStartsWithIgnoreCase(String name);
}
