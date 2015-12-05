package com.wroclaw.citygames.citygamesapp.model;


import java.util.HashSet;
import java.util.Set;

public class Task {

	private Long taskId;
	private String description;
	private String question;
	private String correctAnswer;
	private String picture;
	private float latitude;
	private float longitude;
	private Set<Tip> tips = new HashSet<>(0);
	private int startTask;

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

	public Set<Tip> getTips() {
		return tips;
	}
	
	public void setTips(Set<Tip> tips) {
		this.tips = tips;
	}

	public int getStartTask() {
		return startTask;
	}

	public void setStartTask(int start_task) {
		this.startTask = start_task;
	}

	@Override
	public String toString() {
		return "Task{" +
				"taskId=" + taskId +
				", description='" + description + '\'' +
				", question='" + question + '\'' +
				", correctAnswer='" + correctAnswer + '\'' +
				", picture='" + picture + '\'' +
				", latitude=" + latitude +
				", longitude=" + longitude +
				", start_task=" + startTask +
				", tips=" + tips +
				'}';
	}

}
