package com.wroclaw.citygames.citygamesapp.model;


import java.util.Date;



public class Game {

	
	private Long gameId;
	private Scenario scenario;
	private Date timeStart;
	private Date timeEnd;
	private int points;
	private Team team;

	public Long getGameId() {
		return gameId;
	}
	
	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public Scenario getScenario() {
		return scenario;
	}
	public void setScenario(Scenario scanerioId) {
		this.scenario = scanerioId;
	}

	public Date getTimeStart() {
		return timeStart;
	}
	public void setTimeStart(Date timeStart) {
		this.timeStart = timeStart;
	}

	public Date getTimeEnd() {
		return timeEnd;
	}
	public void setTimeEnd(Date timeEnd) {
		this.timeEnd = timeEnd;
	}

	public int getPoints() {
		return points;
	}
	public void setPoints(int points) {
		this.points = points;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

}
