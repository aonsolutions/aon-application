package com.code.aon.ui.accounting.controller.report;


import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.accounting.AnnualReport;
import com.code.aon.accounting.AnnualReportDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class AnnualReportDetailController  extends LinesController  {

	private static final String ANNUAL_REPORT_LAUNCHER = "annualReportLauncher";
	
	public void onExecute(ActionEvent event) {
		AnnualReportLauncher launcher =  (AnnualReportLauncher) AonUtil.getRegisteredBean(ANNUAL_REPORT_LAUNCHER);
		launcher.onReset(event);
		launcher.setAnnualReport( (AnnualReport) getMasterController().getTo() );
		launcher.onExecute(event);
	}
	public void onExecutePDF(ActionEvent event) {
		AnnualReportLauncher launcher =  (AnnualReportLauncher) AonUtil.getRegisteredBean(ANNUAL_REPORT_LAUNCHER);
		launcher.onReset(event);
		launcher.setAnnualReport( (AnnualReport) getMasterController().getTo() );
		launcher.onExecutePDF(event);
	}

	public void onMoveUp(ActionEvent event) throws ManagerBeanException {
    	moveMenuOption( (AnnualReportDetail) getSelectedTO(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException {
    	moveMenuOption( (AnnualReportDetail) getSelectedTO(), 1);    	
    }	
	
	@SuppressWarnings("unchecked")
	private void moveMenuOption( AnnualReportDetail annualReportDetail, int movement ) throws ManagerBeanException {
		int oldPosition = annualReportDetail.getSortKey();
		int newPosition = oldPosition + movement;
		annualReportDetail.setSortKey( newPosition );
		getManagerBean().update( annualReportDetail );
    	List<AnnualReportDetail> list = (List<AnnualReportDetail>) getModel().getWrappedData();
    	AnnualReportDetail movedMenuOption = list.get( newPosition );
		movedMenuOption.setSortKey( oldPosition );
		getManagerBean().update( movedMenuOption );
		list.set( newPosition, annualReportDetail );
		list.set( oldPosition, movedMenuOption );
	}
	
	
}



