package com.esferalia.aon.gwt.common.client.metrics;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;

public class StatsEventLogger {

  public static void logEvent(String subSystem,String eventGroup, String type){
	  logEvent(GWT.getModuleName(), subSystem, eventGroup, Duration.currentTimeMillis(), type);
  }

  public static native void logEvent(String moduleName, String subSystem,
	      String eventGroup, double millis, String type) /*-{
	if ( $wnd.__gwtStatsEvent )      
	    $wnd.__gwtStatsEvent({
	      'moduleName' : moduleName,
	      'subSystem' : subSystem,
	      'evtGroup' : eventGroup,
	      'millis' : millis,
	      'type' : type
	    });
  }-*/;
}

