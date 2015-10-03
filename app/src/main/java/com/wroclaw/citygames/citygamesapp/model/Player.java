package com.wroclaw.citygames.citygamesapp.model;

import java.util.HashSet;
import java.util.Set;

public class Player {

	private Long playerId;
	private String email;
	private String password;
	private Set<Team> teams = new HashSet<>(0);


	public Long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(Long playerId) {
		this.playerId = playerId;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public Set<Team> getTeams() {
		return teams;
	}
	public void setTeams(Set<Team> teams) {
		this.teams = teams;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	
}
