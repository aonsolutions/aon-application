package com.code.aon.ui.common.role;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Clase que controla los roles habituales de las aplicaciones AON.
 * 
 * @author ecastellano
 * 
 */
public class RoleManager {

	/**
	 * @param role
	 *            The Role
	 * @return TRUE if user has role, false otherwise.
	 */
	public boolean isUserInRole(String role) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.isUserInRole(role);
	}

	/**
	 * @return TRUE if user has IAonRole.USER role, false otherwise.
	 */
	public boolean isUser() {
		return isUserInRole(IAonRole.USER);
	}

	/**
	 * @return TRUE if user has IAonRole.GUEST role, false otherwise.
	 */
	public boolean isGuest() {
		return isUserInRole(IAonRole.GUEST);
	}

	/**
	 * @return TRUE if user has IAonRole.ADMIN role, false otherwise.
	 */
	public boolean isAdmin() {
		return isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.CONFIG role, false otherwise.
	 */
	public boolean isConfig() {
		return isUserInRole(IAonRole.CONFIG) || isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.AUDITOR role, false otherwise.
	 */
	public boolean isAuditor() {
		return isUserInRole(IAonRole.AUDITOR) || isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.CONFIDENTIALITY role, false otherwise.
	 */
	public boolean isConfidentiality() {
		return isUserInRole(IAonRole.CONFIDENTIALITY) || isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.PRODUCT role, false otherwise.
	 */
	public boolean isProductOperator() {
		return isUserInRole(IAonRole.PRODUCT) || isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.COMMERCIAL role, false otherwise.
	 */
	public boolean isCommercialOperator() {
		return isUserInRole(IAonRole.COMMERCIAL) || isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.SALE role, false otherwise.
	 */
	public boolean isSaleOperator() {
		return isUserInRole(IAonRole.SALE) || isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.PURCHASE role, false otherwise.
	 */
	public boolean isPurchaseOperator() {
		return isUserInRole(IAonRole.PURCHASE) || isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.WAREHOUSE role, false otherwise.
	 */
	public boolean isWarehouseOperator() {
		return isUserInRole(IAonRole.WAREHOUSE) || isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.FINANCE role, false otherwise.
	 */
	public boolean isFinanceOperator() {
		return isUserInRole(IAonRole.FINANCE) || isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.ACCOUNTING role, false otherwise.
	 */
	public boolean isAccountingOperator() {
		return isUserInRole(IAonRole.ACCOUNTING) || isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.STATISTICS role, false otherwise.
	 */
	public boolean isStatisticsOperator() {
		return isUserInRole(IAonRole.STATISTICS) || isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.TASK_MOPNITORING role, false otherwise.
	 */
	public boolean isTaskMonitor() {
		return isUserInRole(IAonRole.TASK_MONITORING) || isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.E_SIGNATURE role, false otherwise.
	 */
	public boolean isESignature() {
		return isUserInRole(IAonRole.E_SIGNATURE) || isUserInRole(IAonRole.ADMIN);
	}
	
	/**
	 * Rendered command.
	 * 
	 * @param component the component
	 * @param parent the parent
	 */
	public void renderedCommand( UIComponent component, UIComponent parent ) {
		if ( component.isRendered() ) {
			String id = component.getId();
			if ( !id.contains("search") && !id.contains("back") && !id.contains("Spin") ) {
				component.setRendered(false);
			}
		}
	}	
}
