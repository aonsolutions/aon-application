package com.code.aon.ui.config.event;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class SeriesListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(SeriesListener.class.getName());
	
	private boolean showFilterPanel;
	
	private boolean[] statuses;
	
	private Scope scope;
	
	public SeriesListener() {
		reset();
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		((Series)event.getController().getTo()).setSecurityLevel(SecurityLevel.OFFICIAL);
	}
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		updateCriteria();
	}

	public boolean isShowFilterPanel() {
		return showFilterPanel;
	}

	public void setShowFilterPanel(boolean showFilterPanel) {
		this.showFilterPanel = showFilterPanel;
	}	
	
	public boolean[] getStatuses() {
		return statuses;
	}

	public void setStatuses(boolean[] statuses) {
		this.statuses = statuses;
	}
	
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	private void reset() {
		setStatuses(new boolean[]{true});
		try {
			setScope((Scope)BeanManager.getManagerBean(Scope.class).createNewTo());
		} catch ( ManagerBeanException e ) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public void onClear( ActionEvent event ) {
		reset();
		onFilter(event);
		setShowFilterPanel(true);
	}
	
	private void updateCriteria() {
		BasicController controller = (BasicController) getController();
		try {		
			Criteria criteria = controller.getCriteria();
			if ( ArrayUtils.getLength(statuses) == 1 ) {
				String alias = controller.getFieldName(IEntityAlias.SERIES_ACTIVE);
				criteria.addEqualExpression(alias, statuses[0]);
			}			
			if ((getScope() != null) && (getScope().getId() != null)) {
				criteria.addEqualExpression("Series.scope<id", getScope().getId());
			}					
		} catch ( ManagerBeanException e ) {
			LOGGER.error( e.getMessage(), e );
		}
	}	
	
	public void onFilter( ActionEvent event ) {
		try {
			if (getController().getTo() != null) {
				getController().onCancel(null);
			}
			getController().clearCriteria();
			getController().initializeModel();
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
		setShowFilterPanel(false);
	}
	
}