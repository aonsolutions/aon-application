package com.code.aon.ui.common.role;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;

/**
 * Clase que controla los roles habituales de las aplicaciones AON.
 * 
 * @author ecastellano
 * 
 */
public abstract class BasicRoleManager implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String[] ALLOWED_IDS = new String[] {"Spin", "Scroll", "search", "back", "cancel", "report"};
	
	/** The Constant USER_IN_ROLE. */
	private static final FakeMap USER_IN_ROLE = new FakeMap();
	
	private boolean[] roles;
	
	private Boolean admin;	
	
	private boolean[] getRoles() {
		if (this.roles == null) {
			init();
		}
		return this.roles;
	}
	
	/**
	 * @param role
	 *            The Role
	 * @return TRUE if user has role, false otherwise.
	 */
	public abstract boolean isUserInRole(String role);
	
	public void init() {
		this.roles = new boolean[IAonRole.values().length];
		for( IAonRole role : IAonRole.values() ) {
			roles[role.ordinal()] = isUserInRole(role.getName());
		}
		this.admin = isUserInRole(IAonRole.ADMIN);
	}

	public void setUserInRole( IAonRole role, boolean value ) {
		getRoles()[role.ordinal()] = value;
	}
	
	public void setSysAdmin() {
		this.admin = Boolean.TRUE;
		setUserInRole(IAonRole.SYS_ADMIN, true);
	}
	
	/**
	 * @param role
	 *            The Role
	 * @return TRUE if user has role, false otherwise.
	 */
	public boolean isUserInRole(IAonRole role) {
		return getRoles()[role.ordinal()];
	}

	/**
	 * @return TRUE if user has IAonRole.GUEST role, false otherwise.
	 */
	public boolean isGuest() {
		return (!isAdmin()) && isUserInRole(IAonRole.GUEST);
	}

	/**
	 * @return TRUE if user has IAonRole.ADMIN role, false otherwise.
	 */
	public boolean isAdmin() {
		if ( this.admin == null ) {
			init();
		}
		return this.admin;
	}

	/**
	 * @return TRUE if user has IAonRole.CONFIG role, false otherwise.
	 */
	public boolean isConfig() {
		return isAdmin() || isUserInRole(IAonRole.CONFIG);
	}

	/**
	 * @return TRUE if user has IAonRole.AUDITOR role, false otherwise.
	 */
	public boolean isAuditor() {
		return isAdmin() || isUserInRole(IAonRole.AUDITOR);
	}

	/**
	 * @return TRUE if user has IAonRole.CONFIDENTIALITY role, false otherwise.
	 */
	public boolean isConfidentiality() {
		return isAdmin() || isUserInRole(IAonRole.CONFIDENTIALITY);
	}

	/**
	 * @return TRUE if user has IAonRole.PRODUCT role, false otherwise.
	 */
	public boolean isProductOperator() {
		return isAdmin() || isUserInRole(IAonRole.PRODUCT);
	}

	/**
	 * @return TRUE if user has IAonRole.COMMERCIAL role, false otherwise.
	 */
	public boolean isCommercialOperator() {
		return isAdmin() || isUserInRole(IAonRole.COMMERCIAL);
	}

	/**
	 * @return TRUE if user has IAonRole.SALE role, false otherwise.
	 */
	public boolean isSaleOperator() {
		return isAdmin() || isUserInRole(IAonRole.SALE);
	}

	/**
	 * @return TRUE if user has IAonRole.PURCHASE role, false otherwise.
	 */
	public boolean isPurchaseOperator() {
		return isAdmin() || isUserInRole(IAonRole.PURCHASE);
	}

	/**
	 * @return TRUE if user has IAonRole.WAREHOUSE role, false otherwise.
	 */
	public boolean isWarehouseOperator() {
		return isAdmin() || isUserInRole(IAonRole.WAREHOUSE);
	}

	/**
	 * @return TRUE if user has IAonRole.FINANCE role, false otherwise.
	 */
	public boolean isFinanceOperator() {
		return isAdmin() || isUserInRole(IAonRole.FINANCE);
	}

	/**
	 * @return TRUE if user has IAonRole.ACCOUNTING role, false otherwise.
	 */
	public boolean isAccountingOperator() {
		return isAdmin() || isUserInRole(IAonRole.ACCOUNTING);
	}

	/**
	 * @return TRUE if user has IAonRole.STATISTICS role, false otherwise.
	 */
	public boolean isStatisticsOperator() {
		return isAdmin() || isUserInRole(IAonRole.STATISTICS);
	}

	/**
	 * @return TRUE if user has IAonRole.TASK_MOPNITORING role, false otherwise.
	 */
	public boolean isTaskMonitor() {
		return isAdmin() || isUserInRole(IAonRole.TASK_MONITORING);
	}

	/**
	 * @return TRUE if user has IAonRole.E_SIGNATURE role, false otherwise.
	 */
	public boolean isESignature() {
		return isAdmin() || isUserInRole(IAonRole.E_SIGNATURE);
	}
	
	/**
	 * @return TRUE if user has IAonRole.SUPER_USER role, false otherwise.
	 */
	public boolean isSysAdmin() {
		return isUserInRole(IAonRole.SYS_ADMIN);
	}
	
	/**
	 * @return TRUE if user has IAonRole.TGC role, false otherwise.
	 */
	public boolean isTgc() {
		return isUserInRole(IAonRole.TGC);
	}

	/**
	 * @return TRUE if user has IAonRole.DOCUMENT role, false otherwise.
	 */
	public boolean isDocument() {
		return isDocumentManager() || isUserInRole(IAonRole.DOCUMENT);
	}
	
	/**
	 * @return TRUE if user has IAonRole.DOCUMENT_MANANGER role, false otherwise.
	 */
	public boolean isDocumentManager() {
		return isAdmin() || isUserInRole(IAonRole.DOCUMENT_MANAGER);
	}
	
	/**
	 * @return TRUE if user has IAonRole.DOCUMENT role, false otherwise.
	 */
	public boolean isCallCenter() {
		return isCallCenterManager() || isUserInRole(IAonRole.CALL_CENTER);
	}
	
	/**
	 * @return TRUE if user has IAonRole.DOCUMENT_MANANGER role, false otherwise.
	 */
	public boolean isCallCenterManager() {
		return isAdmin() || isUserInRole(IAonRole.CALL_CENTER_MANAGER);
	}

	/**
	 * @return TRUE if user has IAonRole.PAYROLL role, false otherwise.
	 */
	public boolean isPayroll() {
		return isAdmin() || isUserInRole(IAonRole.PAYROLL);
	}

	/**
	 * @return TRUE if user has IAonRole.FISCAL role, false otherwise.
	 */
	public boolean isFiscal() {
		return isAdmin() || isUserInRole(IAonRole.FISCAL);
	}
	
	/**
	 * @return TRUE if user has IAonRole.ACCOUNTING_MANAGER role, false otherwise.
	 */
	public boolean isAccountingManager() {
		return isAdmin() || isUserInRole(IAonRole.ACCOUNTING_MANAGER);
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
			for( String allowedId : ALLOWED_IDS ) {
				if ( id.contains(allowedId) ) {
					return;
				}
			}
			component.setRendered(false);
		}
	}	

	/**
	 * Do nothing.
	 * 
	 * @param component the component
	 * @param parent the parent
	 */
	public void doNothing( UIComponent component, UIComponent parent ) {
	}

	/**
	 * Gets the user in role.
	 *
	 * @return the user in role
	 */
	public FakeMap getUserInRole() {
		return USER_IN_ROLE;
	}
	
	/**
	 * The Class FakeMap.
	 */
	private static class FakeMap extends AbstractMap<String,Boolean> {
		
		@Override
		public Set<java.util.Map.Entry<String, Boolean>> entrySet() {
			return null;
		}
		
		@Override
		public Boolean get(Object key) {
			if ( key != null ) {
				String value = key.toString();
				String[] roles = StringUtils.split(value, ", " );
				FacesContext ctx = FacesContext.getCurrentInstance();
				for( String role : roles ) {
					if ( ctx.getExternalContext().isUserInRole(role) ) {
						return Boolean.TRUE;
					}
				}				
			}
			return Boolean.FALSE;
		}

		
	}
	
}
