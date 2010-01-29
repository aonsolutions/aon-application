package com.code.aon.ui.registry.controller;

import java.io.Serializable;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.NoteType;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class RegistryNoteLinesController extends LinesController {
	
	private static final Logger LOGGER = Logger.getLogger(RegistryNoteLinesController.class.getName());

	private Date fromDate;
	
	private Date toDate;
	
	private NoteType noteType;

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public NoteType getNoteType() {
		return noteType;
	}

	public void setNoteType(NoteType noteType) {
		this.noteType = noteType;
	}
	
	public void onCustomSearch(ActionEvent event){
		try {
			Criteria customCriteria = new Criteria();
			customCriteria = createCustomCriteria(customCriteria);
			this.setCriteria(customCriteria);
			this.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error customizing search", e);
			AonUtil.addErrorMessage("Error customizing search");
			throw new AbortProcessingException(e);
		}
	}

	private Criteria createCustomCriteria(Criteria customCriteria) throws ManagerBeanException {
		if(getFromDate() != null){
			customCriteria.addGreaterThanOrEqualExpression(this.getFieldName(IRegistryAlias.REGISTRY_NOTE_NOTE_DATE), getFromDate());
		}
		if(getToDate() != null){
			customCriteria.addLessThanOrEqualExpression(this.getFieldName(IRegistryAlias.REGISTRY_NOTE_NOTE_DATE), getToDate());
		}
		if(getNoteType() != null){
			customCriteria.addEqualExpression(this.getFieldName(IRegistryAlias.REGISTRY_NOTE_NOTETYPE), getNoteType());
		}
		customCriteria.addExpression(ExpressionUtilities.getNotEqualExpression(this.getFieldName(IRegistryAlias.REGISTRY_NOTE_NOTETYPE), NoteType.OBSERVATION));
		IController controller = getMasterController();
		Serializable pk = controller.getManagerBean().getId( controller.getTo());
		customCriteria.addEqualExpression(this.getFieldName(IRegistryAlias.REGISTRY_NOTE_REGISTRY_ID), pk );
		return customCriteria;
	}
}