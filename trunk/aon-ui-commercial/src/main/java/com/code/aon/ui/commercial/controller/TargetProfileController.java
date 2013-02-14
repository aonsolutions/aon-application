package com.code.aon.ui.commercial.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.commercial.Question;
import com.code.aon.commercial.QuestionValue;
import com.code.aon.commercial.TargetProfile;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;

public class TargetProfileController extends LinesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TargetProfileController.class.getName());
	
	private List<SelectItem> questionValues;
	
	private Integer questionValueId;
	
	public Integer getQuestionValueId() {
		return questionValueId;
	}

	public void setQuestionValueId(Integer questionValueId) {
		this.questionValueId = questionValueId;
	}

	public List<SelectItem> getQuestionValues() {
		return questionValues;
	}
	
	public void resetQuestionValues() {
		this.questionValues = new LinkedList<SelectItem>();
	}
	
	public void refreshQuestionValues( Question question ) {
		try {
			questionValues = CommercialCollectionsController.getQuestionValues(question);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}	

	public Integer getQuestionValueId( TargetProfile tp ) {
		try {		
			IManagerBean bean = BeanManager.getManagerBean(QuestionValue.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.QUESTION_VALUE_QUESTION_ID), tp.getQuestion().getId());
			switch ( tp.getQuestion().getType() ) {
				case BOOLEAN:
				case NUMBER:
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.QUESTION_VALUE_NUMBER), tp.getNumber());
					break;
				case DATE:
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.QUESTION_VALUE_DATE), tp.getDate());
					break;
				case TEXT:
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.QUESTION_VALUE_TEXT), tp.getText());
					break;
			}
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return ((QuestionValue) list.get(0)).getId();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;
	}
	
	public void questionChanged( LookupChangeEvent event ) throws ManagerBeanException {
		Question question = (Question) event.getNewValue();
		if ( event.getNewValue() != null ) {
			refreshQuestionValues(question);
		} else {
			resetQuestionValues();
		}
	}
	
}