package com.tfg.brais.Service.ComplementaryServices;

import java.security.Principal;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.brais.Service.ControllerServices.UploadService;

@Service
public class TaskDelayerService {

    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    private UploadService uploadService;

    public void delayTask(Runnable task, long delay) {
        executorService.schedule(task, delay, java.util.concurrent.TimeUnit.SECONDS);
    }

    public Runnable createAutoUploadTask(long subjectId, long userId, Principal principal, int answersSize){
        return new Runnable() {
            @Override
            public void run() {
                uploadService.uploadExercise(subjectId, answersSize, new ArrayList<>(answersSize), principal);
            }
        };
    }
}
