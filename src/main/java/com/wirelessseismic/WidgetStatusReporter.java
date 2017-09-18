package com.wirelessseismic;

import java.time.LocalDateTime;

/**
 * Created by igboyd on 3/31/16.
 */
public class WidgetStatusReporter {

    private WidgetFunStatus.WidgetThreadStatus.WidgetType widgetType;

    private LocalDateTime shutDownTime;

    public WidgetStatusReporter(WidgetFunStatus.WidgetThreadStatus.WidgetType widgetType, LocalDateTime shutDownTime) {
        this.widgetType = widgetType;
        this.shutDownTime = shutDownTime;
    }

    public WidgetStatusReporter(WidgetFunStatus.WidgetThreadStatus.WidgetType widgetType) {
        this.widgetType = widgetType;
    }

    public WidgetFunStatus.WidgetThreadStatus.WidgetType getWidgetType() {
        return widgetType;
    }

    public LocalDateTime getShutDownTime() {
        return shutDownTime;
    }
}
