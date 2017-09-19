package com.wirelessseismic;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**d
 * Created by igboyd on 3/29/16.
 */
public class WidgetProducer implements Runnable, WidgetHandler {

    private WidgetFunStatus widgetFunStatus;

    public static WidgetProducer scheduleProducer(final ScheduledExecutorService producerService,
                                        final WidgetFunStatus widgetFunStatus,
                                        final int runTime){
        final WidgetProducer widgetProducer = new WidgetProducer(widgetFunStatus);
        producerService.scheduleWithFixedDelay(widgetProducer, 0, 1, TimeUnit.SECONDS);
        producerService.schedule(() -> {
            widgetFunStatus.reportStopTime(widgetProducer);
        }, runTime, TimeUnit.SECONDS);
        return widgetProducer;
    }

    private WidgetProducer(WidgetFunStatus widgetFunStatus) {
        this.widgetFunStatus = widgetFunStatus;
    }

    public void run() {
        if(widgetFunStatus.push(new Widget())){
            widgetFunStatus.reportWidgetHandled(this);
        }
    }

    @Override
    public String getType() {
        return "Producer";
    }
}
