package com.wirelessseismic;

import com.google.common.eventbus.EventBus;

import java.util.concurrent.*;

public class WidgetFun {

    private static final int THREAD_POOL = 7;
    private static final int PRODUCER_THREADS = 5;
    private static final int CONSUMER_THREADS = 2;
    private static final int RUN_TIME = 50;

    private BlockingQueue<Widget> widgetBlockingQueue;
    private ScheduledExecutorService producerService;
    private WidgetFunStatus widgetFunStatus;
    private EventBus eventBus;

    public WidgetFun() {
        widgetBlockingQueue = new LinkedBlockingQueue<>();
        producerService = Executors.newScheduledThreadPool(THREAD_POOL);
        eventBus = new EventBus();
        widgetFunStatus = new WidgetFunStatus(eventBus);
    }

    public void startUp(){
        for(int i = 0; i < PRODUCER_THREADS; i++) {
            WidgetProducer.scheduleProducer(producerService, widgetBlockingQueue, RUN_TIME, eventBus);
        }
        for(int i = 0; i < CONSUMER_THREADS; i++) {
            WidgetConsumer.scheduleConsumer(producerService, widgetBlockingQueue, RUN_TIME, eventBus);
        }

        producerService.scheduleWithFixedDelay(new ServiceShutdownTask(), RUN_TIME,
                1, TimeUnit.SECONDS);
    }


    public static void main(String[] args) {
        new WidgetFun().startUp();
    }

    class ServiceShutdownTask implements Runnable {
        @Override
        public void run() {
            if(widgetBlockingQueue.isEmpty()){
                System.out.println(widgetBlockingQueue.size());
                producerService.shutdown();
                System.out.println(widgetFunStatus);
            }
        }
    }
}
