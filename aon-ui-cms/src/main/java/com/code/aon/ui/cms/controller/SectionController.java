package com.code.aon.ui.cms.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.Section;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;

public class SectionController extends BasicController{

	private final static Logger LOGGER = LoggerFactory.getLogger(SectionController.class);
	
	/** A list that contains the selected objects of the model. */
	private ArrayList<ITransferObject> checkList= new ArrayList<ITransferObject>();

	/**
	 * Gets the if the selected row is checked.
	 * 
	 * @return the row checked
	 */
	public boolean getRowChecked() {
		ITransferObject to = (ITransferObject) model.getRowData();
		return checkList.contains( to );
	}
	
	/**
	 * Sets the selected row checked.
	 * 
	 * @param rowChecked the row checked
	 */
	public void setRowChecked(boolean rowChecked) {
		if ( rowChecked ) {
			ITransferObject to = (ITransferObject) model.getRowData();
			if (!checkList.contains( to )) {
				checkList.add( to );
			}
		} else {
			ITransferObject to = (ITransferObject) model.getRowData();
			if (checkList.contains( to )) {
				checkList.remove( to );
			}
		}
	}
	
	/**
	 * Gets the check list.
	 * 
	 * @return the check list
	 */
	protected ArrayList<ITransferObject> getCheckList() {
		return checkList;
	}

	/**
	 * Removes all the selected objects.
	 * 
	 * @param event the event
	 */
	public void onRemoveSelected(ActionEvent event){
		try{
			for (ITransferObject to: checkList) {
				getManagerBean().remove(to);
			}
			onSearch( event );
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public boolean isChecklistEmpty() {
		return (this.checkList.size() <= 0);
	}

	@Override
	public void initializeModel() {
		checkList= new ArrayList<ITransferObject>();
		super.initializeModel();
	}
	
	public void onAccept(ActionEvent event) {
		try {
			Section section = (Section)getTo();
			IManagerBean bean = BeanManager.getManagerBean(Section.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SECTION_DEFAULT_), true);
			List<ITransferObject> list = (List<ITransferObject>)bean.getList(criteria);
			if (list.size() == 0) {
				section.setDefault_(true);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		super.onAccept(event);
	}

	public void defaultChanged(ActionEvent event) throws ManagerBeanException, ExpressionException {
		Section section = (Section) model.getRowData();
		section.setDefault_(true);
		updateDefault(section);
	}
	
	@SuppressWarnings("unchecked")
	private void updateDefault(Section defaultSection) throws ManagerBeanException, ExpressionException {
		IManagerBean bean = BeanManager.getManagerBean(Section.class);
		List<ITransferObject> list = (List<ITransferObject>)model.getWrappedData();
		for (int i = 0; i < list.size(); i++) {
			Section section = (Section)list.get(i);
			if (defaultSection != section)
				section.setDefault_(false);
			bean.update(section);
		}
	}
}
