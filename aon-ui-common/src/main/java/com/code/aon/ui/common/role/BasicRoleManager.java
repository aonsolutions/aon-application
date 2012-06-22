package com.code.aon.ui.common.role;

import java.util.AbstractMap;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

/**
 * Clase que controla los roles habituales de las aplicaciones AON.
 * 
 * @author ecastellano
 * 
 */
public abstract class BasicRoleManager {
	
	private static final String[] ALLOWED_IDS = new String[] {"Spin", "Scroll", "search", "back", "cancel", "report"};
	
	/** The Constant USER_IN_ROLE. */
	private static final FakeMap USER_IN_ROLE = new FakeMap();
	
	private boolean[] roles;
	
	private boolean admin;	
	
	public BasicRoleManager() {
		this.roles = new boolean[IAonRole.values().length];
		init();	
	}

	/**
	 * @param role
	 *            The Role
	 * @return TRUE if user has role, false otherwise.
	 */
	public abstract boolean isUserInRole(String role);
	
	public void init() {
		for( IAonRole role : IAonRole.values() ) {
			roles[role.ordinal()] = isUserInRole(role.getName());
		}
		this.admin = isUserInRole(IAonRole.ADMIN);
	}

	public void setUserInRole( IAonRole role, boolean value ) {
		this.roles[role.ordinal()] = value;
	}
	
	public void setSysAdmin() {
		this.admin = true;
		setUserInRole(IAonRole.SYS_ADMIN, true);
	}
	
	/**
	 * @param role
	 *            The Role
	 * @return TRUE if user has role, false otherwise.
	 */
	public boolean isUserInRole(IAonRole role) {
		return this.roles[role.ordinal()];
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
		return this.admin;
	}

	/**
	 * @return TRUE if user has IAonRole.CONFIG role, false otherwise.
	 */
	public boolean isConfig() {
		return this.admin || isUserInRole(IAonRole.CONFIG);
	}

	/**
	 * @return TRUE if user has IAonRole.AUDITOR role, false otherwise.
	 */
	public boolean isAuditor() {
		return this.admin || isUserInRole(IAonRole.AUDITOR);
	}

	/**
	 * @return TRUE if user has IAonRole.CONFIDENTIALITY role, false otherwise.
	 */
	public boolean isConfidentiality() {
		return this.admin || isUserInRole(IAonRole.CONFIDENTIALITY);
	}

	/**
	 * @return TRUE if user has IAonRole.PRODUCT role, false otherwise.
	 */
	public boolean isProductOperator() {
		return this.admin || isUserInRole(IAonRole.PRODUCT);
	}

	/**
	 * @return TRUE if user has IAonRole.COMMERCIAL role, false otherwise.
	 */
	public boolean isCommercialOperator() {
		return this.admin || isUserInRole(IAonRole.COMMERCIAL);
	}

	/**
	 * @return TRUE if user has IAonRole.SALE role, false otherwise.
	 */
	public boolean isSaleOperator() {
		return this.admin || isUserInRole(IAonRole.SALE);
	}

	/**
	 * @return TRUE if user has IAonRole.PURCHASE role, false otherwise.
	 */
	public boolean isPurchaseOperator() {
		return this.admin || isUserInRole(IAonRole.PURCHASE);
	}

	/**
	 * @return TRUE if user has IAonRole.WAREHOUSE role, false otherwise.
	 */
	public boolean isWarehouseOperator() {
		return this.admin || isUserInRole(IAonRole.WAREHOUSE);
	}

	/**
	 * @return TRUE if user has IAonRole.FINANCE role, false otherwise.
	 */
	public boolean isFinanceOperator() {
		return this.admin || isUserInRole(IAonRole.FINANCE);
	}

	/**
	 * @return TRUE if user has IAonRole.ACCOUNTING role, false otherwise.
	 */
	public boolean isAccountingOperator() {
		return this.admin || isUserInRole(IAonRole.ACCOUNTING);
	}

	/**
	 * @return TRUE if user has IAonRole.STATISTICS role, false otherwise.
	 */
	public boolean isStatisticsOperator() {
		return this.admin || isUserInRole(IAonRole.STATISTICS);
	}

	/**
	 * @return TRUE if user has IAonRole.TASK_MOPNITORING role, false otherwise.
	 */
	public boolean isTaskMonitor() {
		return this.admin || isUserInRole(IAonRole.TASK_MONITORING);
	}

	/**
	 * @return TRUE if user has IAonRole.E_SIGNATURE role, false otherwise.
	 */
	public boolean isESignature() {
		return this.admin || isUserInRole(IAonRole.E_SIGNATURE);
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
		return this.admin || isUserInRole(IAonRole.DOCUMENT_MANAGER);
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
