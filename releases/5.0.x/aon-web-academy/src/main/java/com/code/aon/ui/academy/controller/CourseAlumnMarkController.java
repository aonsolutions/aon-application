package com.code.aon.ui.academy.controller;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class CourseAlumnMarkController extends LinesController {
	
	private int evaluation = 1;

	/**
	 * @return the evaluation
	 */
	public int getEvaluation() {
		return evaluation;
	}

	/**
	 * @param evaluation the evaluation to set
	 */
	public void setEvaluation(int evaluation) {
		this.evaluation = evaluation;
	}

	public void onEvaluationChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			setEvaluation(((Integer)event.getNewValue()).intValue());
		}
	}

    public void onRefreshAll(ActionEvent event) throws ManagerBeanException {
    	MarkController markController = (MarkController)AonUtil.getController("mark");
		markController.updateCriteria((CourseAlumn)getTo(), getEvaluation());
    	markController.onSearch(event);
    	EvaluationObservationController evaluationObservationController = (EvaluationObservationController)AonUtil.getController("evaluation_observation");
    	evaluationObservationController.updateCriteria((CourseAlumn)getTo(), getEvaluation());
    	evaluationObservationController.onSearch(event);
    }
	
	public String onExecute() throws ReportException, DAOException{
		ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("alumnMarkList");
        manager.setOutputFormat(OutputFormat.PDF);
        String outcome = manager.onExecute();
        return outcome;
	}
}