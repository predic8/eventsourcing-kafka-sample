package de.predic8.microservices.scm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BackgroundJob {

    @Autowired
    TaskExecutor executor;

    @Autowired
    KafkaListenerRunner runner;

    @PostConstruct
    public void start() {
        System.out.println("Starting KafkaListenerRunner in the background!");
        executor.execute(runner);
    }
}
