package com.code.aon.ui.config.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IHeaderObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.config.util.SeriesUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;

public abstract class HeaderObjectController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HeaderObjectController.class);
	
	private boolean numberEditable;
	private int seriesListLength;
	
	public abstract List<SelectItem> getSeriesCodes()  throws ManagerBeanException;

	private IHeaderObject getHeaderObject() {
		return (IHeaderObject) getTo();
	}
	
	public void onSeriesChanged(ValueChangeEvent event)  {
		updateSeries( (String)event.getNewValue() );
	}

	public void updateSeries( String seriesCode ) {
		updateSecurityLevel(seriesCode);
		if ( numberEditable ) {
			int number = obtainMaxNumber(seriesCode);	
			getHeaderObject().setNumber(number);
		}
	}
	
	private void updateSecurityLevel( String seriesCode ) {
		try {
			SecurityLevel securityLevel = SeriesUtil.getSeriesSecurityLevel(seriesCode);
			getHeaderObject().setSecurityLevel(securityLevel);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}		
	}
	
	protected String getTableName() {
		return StringUtils.capitalize(this.getBeanName());
	}

	protected Criteria getSeriesCriteria() {
		return null;
	}
	
	public int obtainMaxNumber(String seriesId) {
    	return SeriesNumberUtil.obtainNumber(seriesId, getTableName(), getSeriesCriteria());
	}
	
	public int getSeriesListLength() {
		return seriesListLength;
	}

	public void setSeriesListLength(int seriesListLength) {
		this.seriesListLength = seriesListLength;
	}
	
	public boolean isNumberEditable() {
		return numberEditable;
	}

	public void setNumberEditable(boolean numberEditable) {
		this.numberEditable = numberEditable;
	}

	public void onNumberEditable(ActionEvent event) {
		IHeaderObject ho = getHeaderObject();
		if ( ho.getNumber() == 0 ) {
			int number = obtainMaxNumber(ho.getSeries());
			ho.setNumber(number);			
		}
	}	

	public void initSeries() {
		getHeaderObject().setSeries(initSeries(true));
	}
	
	public String initSeries(boolean update) {
		String seriesCode = null;
		setSeriesListLength(0);
		setNumberEditable(false);
		try {
			List<SelectItem> list = getSeriesCodes();
			setSeriesListLength(list.size());
			if ( getSeriesListLength() == 1 ) {
				seriesCode = (String) list.get(0).getValue();
				if ( update ) {
					getHeaderObject().setSeries(seriesCode);					
					updateSeries(seriesCode);	
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return seriesCode;
	}
	
	public void updateSeriesNumber() {
		IHeaderObject ho = getHeaderObject();
        if ( StringUtils.isBlank(ho.getSeries()) ) {
        	ho.setSeries(null);
        }
        if(ho.getNumber() == 0) {
        	ho.setNumber(obtainMaxNumber(ho.getSeries()));
		}
		
	}
	
}
