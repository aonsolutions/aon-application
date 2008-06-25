package com.code.aon.ui.cvitae.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.cvitae.Curriculum;
import com.code.aon.cvitae.Evaluate;
import com.code.aon.cvitae.EvaluateType;
import com.code.aon.cvitae.dao.ICVitaeAlias;
import com.code.aon.cvitae.enumeration.EvaluationLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class EvaluateController extends BasicController {

	DataModel types;
	Integer score = 0;

	/**
	 * @return the types
	 */
	public DataModel getTypes() {
		return types;
	}

	/**
	 * @param types the types to set
	 */
	public void setTypes(DataModel types) {
		this.types = types;
	}

	/**
	 * @return the score
	 */
	public Integer getScore() {
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getTotalScore() {
		Integer total = 0;
		Iterator iter = ( (List) this.types.getWrappedData() ).iterator();
		while ( iter.hasNext() ) {
			Evaluate e = (Evaluate)  iter.next();
			if ( e.getValue() != EvaluationLevel.NULL ) 
				total += 5;
		}
		return total;
	}

	public void onEvaluation(ActionEvent event) throws ManagerBeanException {
		CurriculumController curriculumController = 
			(CurriculumController)AonUtil.getController( CurriculumController.MANAGER_BEAN_NAME );
		Criteria criteria = new Criteria();
		String field = getManagerBean().getFieldName( ICVitaeAlias.EVALUATE_SUMMARY_CURRICULUM_ID );
		Curriculum cv = (Curriculum) curriculumController.getTo();
		criteria.addEqualExpression( field, cv.getId());
		this.setCriteria(criteria);
		onSearch(event);
		if ( getModel().getRowCount() > 0 ) {
			getModel().setRowIndex(0);
			onSelect(event);
		} else {
			onReset(event);
		}
		this.score = 0;
		loadTypes( cv );
	}

	@SuppressWarnings("unchecked")
	private void loadTypes(Curriculum cv) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean( Evaluate.class );
		Criteria criteria = new Criteria();
		String identifier = bean.getFieldName( ICVitaeAlias.EVALUATE_CURRICULUM_ID );
		criteria.addEqualExpression( identifier, cv.getId() );
		List values = bean.getList( criteria );
		if ( values.size() > 0 ) {
			types = new ListDataModel( values );
			for (Iterator iter = values.iterator(); iter.hasNext();) {
				Evaluate evaluate = (Evaluate) iter.next();
				this.score += evaluate.getValue().ordinal();
			}
		} else {
			List l = new ArrayList();
			bean = BeanManager.getManagerBean( EvaluateType.class );
			for (Iterator iter = bean.getList(null).iterator(); iter.hasNext();) {
				EvaluateType type = (EvaluateType) iter.next();
				Evaluate evaluate = new Evaluate();
				evaluate.setCurriculum( cv );
				evaluate.setEvaluateType( type );
				evaluate.setValue( EvaluationLevel.NULL );
				score += evaluate.getValue().ordinal();
				l.add( evaluate );
			}
			types = new ListDataModel( l );
		}
	}

}
