package com.code.aon.ui.webmail.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BUNDLE_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.INVALID_NAME;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.converter.MappedTransferObjectConverter;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.enumeration.MailSource;

public abstract class MailDBController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(MailDBController.class);
	
	private Converter converter;
	
	private MailSource source;
	
	private Integer sourceId;
	
	protected abstract String getDuplicatedMessage( String name );
	
	protected abstract String getSourceAlias() throws ManagerBeanException;
	
	protected abstract String getSourdIdAlias() throws ManagerBeanException;
	
	public MailSource getSource() {
		return source;
	}

	public void setSource(MailSource source) {
		this.source = source;
	}

	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public Converter getConverter() {
		if ( converter == null ) {
			this.converter = new MappedTransferObjectConverter();
		}
		return converter;
	}	

	public void setEnterprise( Integer id ) throws ManagerBeanException {
		this.source = MailSource.ENTERPRISE;
		this.sourceId = id;
		updateCriteria();
	}
	
	private void updateCriteria() throws ManagerBeanException {
		clearCriteria();
		Criteria criteria = getCriteria();
		criteria.addEqualExpression(getSourceAlias(), this.source);
		criteria.addEqualExpression(getSourdIdAlias(), this.sourceId);		
	}
	
	public void idCheck(FacesContext context, UIComponent component, Object value) {
		idCheck( value.toString() );
	}	
	
	private boolean isValidName( String name ) {
		return name.matches("\\p{Alpha}[\\w\\.\\-]*");
	}
	
	private String getInvalidMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, INVALID_NAME, name);
	}
	
	private boolean exists( String name ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getSourceAlias(), this.source);
		criteria.addEqualExpression(getSourdIdAlias(), name);		
		return getManagerBean().getCount(criteria) > 0;
	}
	
	protected abstract String getToName();
	
	private boolean isDuplicated( String name ) throws ManagerBeanException {
		boolean skipCheck = false;
		if (! isNew() ) {
			skipCheck = StringUtils.equals(name, getToName());
		}
		if (! skipCheck ) {
			return exists(name);
		}
		return false;
	}		
	
	private void idCheck( String id ) {
		if (! isValidName(id) ) {
			throw new ValidatorException(new FacesMessage(getInvalidMessage(id)));
		}
		try {
			if ( isDuplicated(id) ) {
				throw new ValidatorException(new FacesMessage(getDuplicatedMessage(id)));			
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}		
	
}
