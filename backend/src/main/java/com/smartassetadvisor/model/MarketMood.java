package com.smartassetadvisor.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "market_mood")
public class MarketMood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String marketType;
    private String region;
    private String primaryExchanges;

    @Column(name = "local_open")
    private LocalTime localOpen;  

    @Column(name = "local_close")
    private LocalTime localClose;  

    private String currentStatus;
    private String notes;

    public MarketMood() {}

    public MarketMood(String marketType, String region, String primaryExchanges, 
                      LocalTime localOpen, LocalTime localClose, String currentStatus, String notes) {
        this.marketType = marketType;
        this.region = region;
        this.primaryExchanges = primaryExchanges;
        this.localOpen = localOpen;
        this.localClose = localClose;
        this.currentStatus = currentStatus;
        this.notes = notes;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPrimaryExchanges() {
        return primaryExchanges;
    }

    public void setPrimaryExchanges(String primaryExchanges) {
        this.primaryExchanges = primaryExchanges;
    }

    public LocalTime getLocalOpen() {
        return localOpen;
    }

    public void setLocalOpen(LocalTime localOpen) {
        this.localOpen = localOpen;
    }

    public LocalTime getLocalClose() {
        return localClose;
    }

    public void setLocalClose(LocalTime localClose) {
        this.localClose = localClose;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "MarketMood{" +
                "id=" + id +
                ", marketType='" + marketType + '\'' +
                ", region='" + region + '\'' +
                ", primaryExchanges='" + primaryExchanges + '\'' +
                ", localOpen=" + localOpen +
                ", localClose=" + localClose +
                ", currentStatus='" + currentStatus + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
