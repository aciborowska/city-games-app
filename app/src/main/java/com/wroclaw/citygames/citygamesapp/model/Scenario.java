package com.wroclaw.citygames.citygamesapp.model;

import java.util.HashSet;
import java.util.Set;


public class Scenario {
	private Long scenarioId;
	private int usersAmount;
	private String description;
	private int time;
	private float distanceKm;
	private float level;
	private Set<TaskGroup> taskGroups = new HashSet<>(0);
	private Set<Game> games = new HashSet<>(0);
	private boolean ready;

	public Long getScenarioId() {
		return scenarioId;
	}
	public void setScenarioId(Long scenarioId) {
		this.scenarioId = scenarioId;
	}

	public int getUsersAmount() {
		return usersAmount;
	}
	public void setUsersAmount(int usersAmount) {
		this.usersAmount = usersAmount;
	}
	

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}

	public float getDistanceKm() {
		return distanceKm;
	}
	public void setDistanceKm(float distanceKm) {
		this.distanceKm = distanceKm;
	}

	public float getLevel() {
		return level;
	}
	public void setLevel(float level) {
		this.level = level;
	}

	public Set<TaskGroup> getTaskGroups() {
		return taskGroups;
	}
	public void setTaskGroups(Set<TaskGroup> taskGroups) {
		this.taskGroups = taskGroups;
	}

	public boolean getReady(){
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}

	public Set<Game> getGames() {
		return games;
	}
	public void setGames(Set<Game> games) {
		this.games = games;
	}
	
}
