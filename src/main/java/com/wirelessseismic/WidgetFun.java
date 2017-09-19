package com.wirelessseismic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.*;

public class WidgetFun {

    private static final int PRODUCER_THREADS = 5;
    private static final int CONSUMER_THREADS = 2;
    private static final int RUN_TIME = 50;

    private ScheduledExecutorService producerService;
    private ScheduledExecutorService consumerService;
    private WidgetFunStatus widgetFunStatus;

    public WidgetFun() {
        producerService = Executors.newScheduledThreadPool(PRODUCER_THREADS);
        consumerService = Executors.newScheduledThreadPool(CONSUMER_THREADS);
        widgetFunStatus = new WidgetFunStatus();
    }

    public void startUp(){
        for(int i = 0; i < PRODUCER_THREADS; i++) {
            WidgetProducer.scheduleProducer(producerService, widgetFunStatus, RUN_TIME);
        }
        producerService.scheduleWithFixedDelay(new ProducerShutdownTask(), RUN_TIME,
                1, TimeUnit.SECONDS);

        for(int i = 0; i < CONSUMER_THREADS; i++) {
            WidgetConsumer.scheduleConsumer(consumerService, widgetFunStatus, RUN_TIME);
        }
        consumerService.scheduleWithFixedDelay(new ConsumerShutdownTask(), RUN_TIME,
                1, TimeUnit.SECONDS);




    }


    public static void main(String[] args) {
        new WidgetFun().startUp();
    }

    class ProducerShutdownTask implements Runnable {
        @Override
        public void run() {
            producerService.shutdown();
            System.out.println(widgetFunStatus);
        }
    }

    class ConsumerShutdownTask implements Runnable {
        @Override
        public void run() {
            while(widgetFunStatus.hasUnprocessedWidgets()){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            consumerService.shutdown();
            System.out.println(widgetFunStatus);
            try {
                final Path path = Paths.get("widgetFun.log");
                if(!Files.exists(path)){
                    Files.createFile(path);
                }
                Files.write(path, widgetFunStatus.toString().getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

