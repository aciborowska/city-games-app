package com.wroclaw.citygames.citygamesapp.model;

import java.util.HashSet;
import java.util.Set;

public class TaskGroup {
	
	private Long taskGroupId;
	private String description;
	private Scenario scenarioId;
	private Set<Task> tasks = new HashSet<>(0);

	public Long getTaskGroupId() {
		return taskGroupId;
	}
	public void setTaskGroupId(Long taskGroupId) {
		this.taskGroupId = taskGroupId;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Scenario getScenarioId() {
		return scenarioId;
	}
	public void setScenarioId(Scenario scenario) {
		this.scenarioId = scenario;
	}

	public Set<Task> getTasks() {
		return tasks;
	}
	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}

	
}
