package com.perqin.gandear;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author   : perqin
 * Date     : 17-4-5
 */

public class Dungeon {
    private String name;
    private int exp;
    private int money;
    private ArrayList<Round> rounds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public ArrayList<Round> getRounds() {
        return rounds;
    }

    public void setRounds(ArrayList<Round> rounds) {
        this.rounds = rounds;
    }

    public static class Round {
        private String name;
        private HashMap<String, Integer> enemies;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public HashMap<String, Integer> getEnemies() {
            return enemies;
        }

        public void setEnemies(HashMap<String, Integer> enemies) {
            this.enemies = enemies;
        }
    }
}
