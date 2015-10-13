package com.wroclaw.citygames.citygamesapp.model;


import java.util.HashSet;
import java.util.Set;

public class Task {

	private Long taskId;
	private String description;
	private String question;
	private String correctAnswer;
	private String picture;
	private int correctPoints;
	private int wrongPoints;
	private float latitude;
	private float longitude;
	private TaskGroup taskGroup;
	private Long nextTaskGood;
	private Long nextTaskWrong;
	private Set<Tip> tips = new HashSet<>(0);
	private int start_task;

	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}

	public String getCorrectAnswer() {
		return correctAnswer;
	}
	public void setCorrectAnswer(String correctAnswer) {
		this.correctAnswer = correctAnswer;
	}

	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}

	public int getCorrectPoints() {
		return correctPoints;
	}
	public void setCorrectPoints(int correctPoints) {
		this.correctPoints = correctPoints;
	}

	public int getWrongPoints() {
		return wrongPoints;
	}
	public void setWrongPoints(int wrongPoints) {
		this.wrongPoints = wrongPoints;
	}

	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	public TaskGroup getTaskGroup() {
		return taskGroup;
	}
	public void setTaskGroup(TaskGroup taskGroup) {
		this.taskGroup = taskGroup;
	}

	public Long getNextTaskGood() {
		return nextTaskGood;
	}
	public void setNextTaskGood(Long nextTaskGood) {
		this.nextTaskGood = nextTaskGood;
	}

	public Long getNextTaskWrong() {
		return nextTaskWrong;
	}
	public void setNextTaskWrong(Long nextTaskWrong) {
		this.nextTaskWrong = nextTaskWrong;
	}

	public Set<Tip> getTips() {
		return tips;
	}
	
	public void setTips(Set<Tip> tips) {
		this.tips = tips;
	}

	public int getStart_task() {
		return start_task;
	}

	public void setStart_task(int start_task) {
		this.start_task = start_task;
	}
}
