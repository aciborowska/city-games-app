package com.wroclaw.citygames.citygamesapp.dao;

import android.util.Log;

import com.wroclaw.citygames.citygamesapp.model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Aga on 2015-10-31.
 */
public class TaskDao extends Observable {
    private static final String TAG = TaskDao.class.getName();
    private List<Observer> observers = new ArrayList<>();
    private Task instance;

    public  TaskDao(){
        if(instance==null){
            instance = new Task();
            instance.setTaskId(Long.valueOf(-1));
            instance.setCorrectAnswer("?");
        }
    }

    public void setTask(Task task){
        Log.d(TAG,"zmiana zadania, nowe="+task.toString());
        instance = task;
        this.setChanged();
        this.notifyObservers();
    }

    public Task getTask(){
        return instance;
    }
}
