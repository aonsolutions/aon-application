package com.code.aon.groupware.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum DailyTrackingReportType implements IResourceable {

	GRAPHIC ("dailyTrackingGraphic"),
	JOBS_BY_CUSTOMER ("dailyTrackingJobsByCustomer"),
	BY_CUSTOMER ("dailyTrackingByCustomer"),
	BY_USER ("dailyTrackingByUser"),
	BY_JOB_TYPE ("dailyTrackingByJobType"),
	BY_PROJECT ("dailyTrackingByProject"),
	BY_PROJECT_TYPE ("dailyTrackingByProjectType"),
	BY_DATE ("dailyTrackingByDate"),
	GRAPHIC_BY_USER ("dailyTrackingGraphicByUser"),
	GRAPHIC_BY_CUSTOMER ("dailyTrackingGraphicByCustomer"),
	GRAPHIC_BY_PROJECT ("dailyTrackingGraphicByProject"),
	GRAPHIC_BY_JOB_TYPE ("dailyTrackingGraphicByJobType"),
	REPORT ("dailyTrackingExcel");
	
	private static final String BASE_NAME = "com.code.aon.groupware.i18n.messages";
    private static final String MSG_KEY_PREFIX = "daily_tracking_report_";
    private String reportKey;
    
    private DailyTrackingReportType(String reportKey) {
    	this.reportKey = reportKey;
    }
    
    public String getReportKey() {
		return reportKey;
	}

	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}
