package com.code.aon.ui.cvitae.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.cvitae.Curriculum;
import com.code.aon.cvitae.dao.ICVitaeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class KnowledgeController extends BasicController {

	private static final long serialVersionUID = -2159682638961509902L;
	public static final String MANAGER_BEAN_NAME = "Knowledge";

	public void onKnowledges(ActionEvent event) throws ManagerBeanException{
		CurriculumController curriculumController = 
			(CurriculumController) AonUtil.getController( CurriculumController.MANAGER_BEAN_NAME );
		Criteria criteria = getCriteria();
		criteria = new Criteria();
		String field = getManagerBean().getFieldName( ICVitaeAlias.KNOWLEDGE_CURRICULUM_ID );
		criteria.addEqualExpression( field, ((Curriculum)curriculumController.getTo()).getId() );
		this.setCriteria(criteria);
		this.onSearch(event);
		this.onReset(event);
	}
}
