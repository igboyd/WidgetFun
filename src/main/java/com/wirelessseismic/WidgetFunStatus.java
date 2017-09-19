package com.wirelessseismic;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by igboyd on 3/29/16.
 */
public class WidgetFunStatus {

    private AtomicInteger totalWidgetsProduced = new AtomicInteger(0);
    private AtomicInteger totalWidgetsConsumed = new AtomicInteger(0);
    private ConcurrentLinkedQueue<Widget> widgetConcurrentLinkedQueue;
    private Map<WidgetHandler, WidgetThreadStatus> widgetThreadStatusMap = new HashMap<>();

    public WidgetFunStatus() {
        widgetConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Total Widgets Produced: " + totalWidgetsProduced.intValue() + "\n"
                + "Total Widgets Consumed " + totalWidgetsConsumed.intValue() + "\n");
        for(final WidgetHandler widgetHandler : widgetThreadStatusMap.keySet()){
            sb.append(widgetThreadStatusMap.get(widgetHandler).toString()).append("\n");
        }
        return sb.toString();

    }

    public boolean push(Widget widget) {
        final boolean added = widgetConcurrentLinkedQueue.add(widget);
        if(added){
            totalWidgetsProduced.incrementAndGet();
        }
        return added;
    }

    public synchronized void reportWidgetHandled(final WidgetHandler widgetHandler){
        WidgetThreadStatus threadStatus = widgetThreadStatusMap.get(widgetHandler);
        if(threadStatus == null){
            threadStatus = new WidgetThreadStatus(widgetHandler, Thread.currentThread().getId());
            widgetThreadStatusMap.put(widgetHandler, threadStatus);
        }
        threadStatus.incrementWidgets();
    }

    public Widget pop(){
        final Widget widget = widgetConcurrentLinkedQueue.poll();
        if(widget != null){
            totalWidgetsConsumed.incrementAndGet();
        }
        return widget;
    }

    public void reportStopTime(final WidgetHandler widgetHandler){
        widgetThreadStatusMap.get(widgetHandler).setShutDownTime(LocalDateTime.now());
    }

    public boolean hasUnprocessedWidgets() {
        return !widgetConcurrentLinkedQueue.isEmpty();
    }

    public static class WidgetThreadStatus{

        private WidgetHandler handler;
        private AtomicInteger widgets = new AtomicInteger(0);
        private LocalDateTime shutDownTime;
        private long id;

        public WidgetThreadStatus(WidgetHandler handler, long id) {
            this.id = id;
            this.handler = handler;
        }

        public void incrementWidgets(){
            widgets.incrementAndGet();
        }

        public long getId() {
            return id;
        }

        public void setShutDownTime(LocalDateTime shutDownTime) {
            this.shutDownTime = shutDownTime;
        }

        @Override
        public String toString() {
            return "\tWidgetThreadStatus{" +
                    "handler=" + handler +
                    ", threadId=" + id +
                    ", widgetType=" + handler.getType() +
                    ", widgets=" + widgets.intValue() +
                    ", shutDownTime=" + shutDownTime +
                    '}';
        }
    }

}
