package com.wirelessseismic;

import com.google.common.eventbus.EventBus;

import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * Created by igboyd on 3/29/16.
 */
public class WidgetConsumer implements Runnable{

    private BlockingQueue<Widget> widgetBlockingQueue;
    private EventBus eventBus;

    public static void scheduleConsumer(final ScheduledExecutorService producerService,
                                        final BlockingQueue<Widget> widgetBlockingQueue,
                                        final int runTime, final EventBus eventBus){
        final ScheduledFuture<?> forAwhile = producerService.scheduleWithFixedDelay(new WidgetConsumer(widgetBlockingQueue, eventBus),
                0,
                ThreadLocalRandom.current().nextInt(250, 1251),
                TimeUnit.MILLISECONDS);
        producerService.schedule(new StopThreadTask(forAwhile, widgetBlockingQueue, eventBus), runTime, TimeUnit.SECONDS);
    }

    public WidgetConsumer(final BlockingQueue<Widget> widgetBlockingQueue, final EventBus eventBus) {
        this.widgetBlockingQueue = widgetBlockingQueue;
        this.eventBus = eventBus;
    }

    @Override
    public void run() {
        final Widget widget = widgetBlockingQueue.poll();
        if(null != widget){
            eventBus.post(new WidgetStatusReporter(WidgetFunStatus.WidgetThreadStatus.WidgetType.CONSUMER));
        }
    }

    private static class StopThreadTask  implements Runnable {
        private ScheduledFuture<?> future;
        private BlockingQueue<Widget> widgetBlockingQueue;
        private EventBus eventBus;

        public StopThreadTask(ScheduledFuture<?> future, BlockingQueue<Widget> widgetBlockingQueue, EventBus eventBus) {
            this.future = future;
            this.widgetBlockingQueue = widgetBlockingQueue;
            this.eventBus = eventBus;
        }

        @Override
        public void run() {
            if(widgetBlockingQueue.isEmpty()){
                future.cancel(true);
                eventBus.post(new WidgetStatusReporter(WidgetFunStatus.WidgetThreadStatus.WidgetType.CONSUMER, LocalDateTime.now()));
            }
        }
    }
}
