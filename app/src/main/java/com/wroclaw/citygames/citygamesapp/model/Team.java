package com.wroclaw.citygames.citygamesapp.model;

import java.util.List;

public class Team {

    private Long teamId;
    private String password;
    private String name;
    private List<Game> games;
    private List<Player> users;
    private int singular;

    public int getSingular() {
        return singular;
    }

    public void setSingular(int singular) {
        this.singular = singular;
    }

    public List<Player> getUsers() {
        return users;
    }

    public void setUsers(List<Player> users) {
        this.users = users;
    }
    
    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }



    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
