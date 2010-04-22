package com.code.aon.ui.academy.controller;

import com.code.aon.academy.CourseAlumn;
import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class EvaluationObservationController extends BasicController {
	

	public void updateCriteria(CourseAlumn courseAlumn, int evaluation) throws ManagerBeanException{
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(this.getFieldName(IAcademyAlias.EVALUATION_OBSERVATION_ALUMN_ID),courseAlumn==null?new Integer(-1):courseAlumn.getId());
		criteria.addEqualExpression(this.getFieldName(IAcademyAlias.EVALUATION_OBSERVATION_EVALUATION),new Integer(evaluation));
		criteria.addOrder(this.getFieldName(IAcademyAlias.EVALUATION_OBSERVATION_EVALUATION));
		criteria.addOrder(this.getFieldName(IAcademyAlias.EVALUATION_OBSERVATION_ALUMN_ID));
		this.setCriteria(criteria);
	}
	
	public String onExecute() throws ReportException, DAOException{
		ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("courseEvaluationList");
        manager.setOutputFormat(OutputFormat.PDF);
        String outcome = manager.onExecute();
        return outcome;
	}


}
