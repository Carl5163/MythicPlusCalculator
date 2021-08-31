package com.cwahler.mythicpluscalculator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class Dungeon {

    @Id
    @GeneratedValue
    private Long id;

    private String name = "";
    private int fortLevel = 0;
    private int tyranLevel = 0;
    private double fortScore = 0;
    private double tyranScore = 0;
    private double totalScore = 0;

    protected Dungeon(){}

    public Dungeon(String name, int fortLevel, int tyranLevel, double fortScore, double tyranScore) {
        this.name = name;
        this.fortLevel = fortLevel;
        this.tyranLevel = tyranLevel;
        this.fortScore = fortScore;
        this.tyranScore = tyranScore;
        this.setTotalScore();
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFortLevel() {
        return this.fortLevel;
    }

    public void setFortLevel(int level) {
        this.fortLevel = level;
    }

    public int getTyranLevel() {
        return this.tyranLevel;
    }

    public void setTyranLevel(int tyranLevel) {
        this.tyranLevel = tyranLevel;
    }

    public double getFortScore() {
        return this.fortScore;
    }

    public void setFortScore(double fortScore) {
        this.fortScore = fortScore;
        setTotalScore();
    }

    public double getTyranScore() {
        return this.tyranScore;
    }

    public void setTyranScore(double tyranScore) {
        this.tyranScore = tyranScore;
        setTotalScore();
    }

    public Double getTotalScore() {
        return this.totalScore;
    }

    public void setTotalScore() {
        // this.totalScore = Math.max(fortScore, tyranScore) + 0.5* Math.min(fortScore, tyranScore);
        this.totalScore = fortScore + tyranScore;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.name;
    }
    
    
}
