package com.wroclaw.citygames.citygamesapp.dao;

import android.graphics.Bitmap;
import android.util.Log;

import com.wroclaw.citygames.citygamesapp.Globals;
import com.wroclaw.citygames.citygamesapp.model.Task;
import com.wroclaw.citygames.citygamesapp.model.Tip;
import com.wroclaw.citygames.citygamesapp.util.ImageConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class TaskDao extends Observable {
    private static final String TAG = TaskDao.class.getName();

    private Task currentTask;
    private Map<String,String> choiceTasks = new HashMap<>();

    public TaskDao() {
        if (currentTask == null) {
            currentTask = new Task();
            currentTask.setTaskId((long) -1);
            currentTask.setCorrectAnswer("?");
        }
    }

    public void setTask(Task task) {
        Log.d(TAG, "zmiana zadania, nowe=" + task.toString());
        ImageConverter.cleanPhotoDir();
        currentTask = task;
        saveImages();
        if(currentTask.getTaskId()== Globals.CHOICE_TASK) choiceTask();
        this.setChanged();
        this.notifyObservers();
    }

    public Task getTask() {
        return currentTask;
    }

    private void saveImages() {
        String image = currentTask.getPicture();
        if (image != null && !image.isEmpty()) {
            Bitmap taskImage = ImageConverter.decode(image);
            String filename = ImageConverter.saveBitmap("task" + currentTask.getTaskId(), taskImage);
            currentTask.setPicture(filename);
        }
        List<Tip> tips = new ArrayList<>();
        tips.addAll(currentTask.getTips());
        for (int i = 0; i < tips.size(); i++) {
            image = tips.get(i).getPicture();
            if (image != null && !image.isEmpty()) {
                Bitmap tipImage = ImageConverter.decode(image);
                String filename = ImageConverter.saveBitmap("tip" + tips.get(i).getTipId(), tipImage);
                tips.get(i).setPicture(filename);
            }
        }
    }

    public String getIdForTask(String taskName){
        if(choiceTasks != null){
            return choiceTasks.get(taskName);
        }
        Log.e(TAG,"choice task == null");
        return null;
    }

    private void choiceTask(){
        choiceTasks.clear();
        String namesAndIds[] = currentTask.getQuestion().split("#");
        String names[] = namesAndIds[0].split(";");
        String ids[] = namesAndIds[1].split(";");

        for(int i=0;i<names.length;i++){
            choiceTasks.put(names[i],ids[i]);
            Log.d(TAG,"choice :"+names[i]+", "+ids[i]);
        }
    }
}
