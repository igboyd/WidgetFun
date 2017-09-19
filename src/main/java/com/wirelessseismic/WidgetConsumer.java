package com.wirelessseismic;


import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * Created by igboyd on 3/29/16.
 */
public class WidgetConsumer implements Runnable, WidgetHandler{

    private WidgetFunStatus widgetFunStatus;

    public static WidgetConsumer scheduleConsumer(final ScheduledExecutorService consumerService,
                                        final WidgetFunStatus widgetFunStatus,
                                        final int runTime){
        final WidgetConsumer widgetConsumer = new WidgetConsumer(widgetFunStatus);
        final ScheduledFuture<?> forAwhile = consumerService.scheduleWithFixedDelay(widgetConsumer,
                0,
                ThreadLocalRandom.current().nextInt(250, 1251),
                TimeUnit.MILLISECONDS);
        consumerService.schedule(() -> {
            widgetFunStatus.reportStopTime(widgetConsumer);
        }, runTime, TimeUnit.SECONDS);
        return widgetConsumer;
    }

    public WidgetConsumer(WidgetFunStatus widgetFunStatus) {
        this.widgetFunStatus = widgetFunStatus;
    }

    @Override
    public void run() {
        final Widget widget = widgetFunStatus.pop();
        if(widget != null){
            widgetFunStatus.reportWidgetHandled(this);
        }
    }

    @Override
    public String getType() {
        return "Consumer";
    }
}
