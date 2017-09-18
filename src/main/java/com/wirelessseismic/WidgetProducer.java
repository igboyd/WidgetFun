package com.wirelessseismic;

import com.google.common.eventbus.EventBus;

import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**d
 * Created by igboyd on 3/29/16.
 */
public class WidgetProducer implements Runnable {

    private BlockingQueue<Widget> widgetBlockingQueue;
    private EventBus eventBus;

    public static void scheduleProducer(final ScheduledExecutorService producerService,
                                        final BlockingQueue<Widget> widgetBlockingQueue,
                                        final int runTime, final EventBus eventBus){
        final ScheduledFuture<?> forAwhile = producerService.scheduleWithFixedDelay(new WidgetProducer(widgetBlockingQueue, eventBus), 0, 1, TimeUnit.SECONDS);
        producerService.schedule(new StopThreadTask(forAwhile, eventBus), runTime, TimeUnit.SECONDS);
    }

    public WidgetProducer(final BlockingQueue<Widget> widgetBlockingQueue, final EventBus eventBus) {
        this.widgetBlockingQueue = widgetBlockingQueue;
        this.eventBus = eventBus;
    }

    @Override
    public void run() {
        final boolean added = widgetBlockingQueue.add(new Widget());
        if(added){
            eventBus.post(new WidgetStatusReporter(WidgetFunStatus.WidgetThreadStatus.WidgetType.PRODUCER));
        }
    }


    private static class StopThreadTask  implements Runnable {
        private ScheduledFuture<?> future;
        private EventBus eventBus;

        public StopThreadTask(final ScheduledFuture<?> future, final EventBus eventBus) {
            this.future = future;
            this.eventBus = eventBus;
        }

        @Override
        public void run() {
            future.cancel(true);
            eventBus.post(new WidgetStatusReporter(WidgetFunStatus.WidgetThreadStatus.WidgetType.CONSUMER, LocalDateTime.now()));
        }
    }
}
