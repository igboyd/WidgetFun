package com.wirelessseismic;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by igboyd on 3/29/16.
 */
public class WidgetFunStatus {

    private int totalWidgetsProduced = 0;
    private int totalWidgetsConsumed = 0;
    private Map<Long, WidgetThreadStatus> widgetThreadStatusMap = new HashMap<>();

    public WidgetFunStatus(final EventBus eventBus) {
        super();
        eventBus.register(this);
    }

    public int getTotalWidgetsProduced() {
        return totalWidgetsProduced;
    }

    public int getTotalWidgetsConsumed() {
        return totalWidgetsConsumed;
    }

    public Map<Long, WidgetThreadStatus> getWidgetThreadStatusMap() {
        return widgetThreadStatusMap;
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleWidgetStatusReporterEvent(final WidgetStatusReporter widgetStatusReporter){
        if(widgetStatusReporter.getWidgetType() == WidgetThreadStatus.WidgetType.PRODUCER){
            incrementTotalWidgetsProduced();
        } else if (widgetStatusReporter.getWidgetType() == WidgetThreadStatus.WidgetType.CONSUMER){
            incrementTotalWidgetsConsumed();
        } else {
            throw new IllegalArgumentException("unknown event");
        }

        if(!widgetThreadStatusMap.containsKey(Thread.currentThread().getId())){
            widgetThreadStatusMap.put(Thread.currentThread().getId(), new WidgetThreadStatus(widgetStatusReporter.getWidgetType()));
        } else {
            final WidgetThreadStatus widgetThreadStatus = widgetThreadStatusMap.get(Thread.currentThread().getId());
            widgetThreadStatus.incrementWidgets();
            widgetThreadStatusMap.replace(Thread.currentThread().getId(), widgetThreadStatus);
        }
    }

    @Subscribe
    @AllowConcurrentEvents
    public void handleShutDownEvent(final WidgetStatusReporter widgetStatusReporter){
        if(widgetStatusReporter.getShutDownTime() != null){
            final WidgetThreadStatus widgetThreadStatus = widgetThreadStatusMap.get(Thread.currentThread().getId());
            if(widgetThreadStatus == null){
                throw new IllegalStateException("we are trying to report a shutdown of a thread that doesn't exist");
            }
            widgetThreadStatus.setShutDownTime(widgetStatusReporter.getShutDownTime());
        }
    }


    private void addWidgetThreadStatus(final long threadId, final WidgetThreadStatus widgetThreadStatus){
        widgetThreadStatusMap.put(threadId, widgetThreadStatus);
    }

    private void incrementTotalWidgetsProduced(){
        totalWidgetsProduced++;
    }

    private void incrementTotalWidgetsConsumed(){
        totalWidgetsConsumed++;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Total Widgets Produced: " + totalWidgetsProduced + "\n"
                + "Total Widgets Consumed " + totalWidgetsConsumed + "\n");
        for(final Long threadId : widgetThreadStatusMap.keySet()){
            sb.append("Widgets for thread " + threadId + "\n" + widgetThreadStatusMap.get(threadId) + "\n");
        }
        return sb.toString();

    }

    public static class WidgetThreadStatus{
        public enum WidgetType {PRODUCER, CONSUMER};


        private WidgetType widgetType;
        private int widgets = 1;
        private LocalDateTime shutDownTime;

        public WidgetThreadStatus(WidgetType widgetType) {
            this.widgetType = widgetType;
        }

        public void incrementWidgets(){
            widgets++;
        }

        public void setShutDownTime(LocalDateTime shutDownTime) {
            this.shutDownTime = shutDownTime;
        }

        @Override
        public String toString() {
            return "\tWidgetThreadStatus{" +
                    "widgetType=" + widgetType +
                    ", widgets=" + widgets +
                    ", shutDownTime=" + shutDownTime +
                    '}';
        }
    }

}
