package com.wroclaw.citygames.citygamesapp.model;

import java.util.HashSet;
import java.util.Set;

public class Team {

    private Long teamId;
    private String password;
    private String name;
    private Set<Player> users = new HashSet<>(0);
    private Set<Game> games = new HashSet<>(0);

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Set<Player> getUsers() {
        return users;
    }

    public void setUsers(Set<Player> users) {
        this.users = users;
    }

    public void removePlayer(Player player) {
        users.remove(player);
    }

    public Set<Game> getGames() {
        return games;
    }

    public void setGames(Set<Game> games) {
        this.games = games;
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
