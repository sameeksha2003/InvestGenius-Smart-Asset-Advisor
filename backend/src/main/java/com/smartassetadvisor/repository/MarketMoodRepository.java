package com.smartassetadvisor.repository;

import com.smartassetadvisor.model.MarketMood;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketMoodRepository extends JpaRepository<MarketMood, Long> {
}
