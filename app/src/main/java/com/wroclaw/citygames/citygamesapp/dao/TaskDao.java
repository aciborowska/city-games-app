package com.wroclaw.citygames.citygamesapp.dao;

import android.graphics.Bitmap;
import android.util.Log;

import com.wroclaw.citygames.citygamesapp.model.Task;
import com.wroclaw.citygames.citygamesapp.model.Tip;
import com.wroclaw.citygames.citygamesapp.util.ImageConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class TaskDao extends Observable {
    private static final String TAG = TaskDao.class.getName();
    private List<Observer> observers = new ArrayList<>();
    private Task instance;

    public  TaskDao(){
        if(instance==null){
            instance = new Task();
            instance.setTaskId((long) -1);
            instance.setCorrectAnswer("?");
        }
    }

    public void setTask(Task task){
        Log.d(TAG,"zmiana zadania, nowe="+task.toString());
        ImageConverter.clean();
        instance = task;
        saveImages();
        this.setChanged();
        this.notifyObservers();
    }

    public Task getTask(){
        return instance;
    }

    private void saveImages(){
        String image = instance.getPicture();
        if(image!=null && !image.isEmpty()){
            Bitmap taskImage = ImageConverter.decode(image);
            String filename = ImageConverter.saveBitmap("task"+instance.getTaskId(),taskImage);
            instance.setPicture(filename);
        }
        List<Tip> tips = new ArrayList<>();
        tips.addAll(instance.getTips());
        for(int i=0;i<tips.size();i++){
            image = tips.get(i).getPicture();
            if(image!=null && !image.isEmpty()){
                Bitmap tipImage = ImageConverter.decode(image);
                String filename = ImageConverter.saveBitmap("tip"+tips.get(i).getTipId(),tipImage);
                tips.get(i).setPicture(filename);
            }
        }

    }
}
