package com.code.aon.ui.config.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.impl.jooq.dao.ScopeDAO;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class ScopeController extends BasicController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(ScopeController.class);
	
	private boolean skipParentScopes;
	private List<SelectItem> scopes;
	private List<com.esferalia.aon.occam.api.model.security.Scope> usedScopesInDomain;
	private List<SelectItem> usedScopes;
	private boolean showReassignWindow;
	private Scope oldScope; 
	private Scope newScope;
	private boolean removeOldScope;
	
	public boolean isSkipParentScopes() {
		return skipParentScopes;
	}
	public void setSkipParentScopes(boolean skipParentScopes) {
		this.skipParentScopes = skipParentScopes;
	}
	
	public boolean isShowReassignWindow() {
		return showReassignWindow;
	}
	public void setShowReassignWindow(boolean showReassignWindow) {
		this.showReassignWindow = showReassignWindow;
	}
	public Scope getOldScope() {
		return oldScope;
	}
	public void setOldScope(Scope oldScope) {
		this.oldScope = oldScope;
	}
	public Scope getNewScope() {
		return newScope;
	}
	public void setNewScope(Scope newScope) {
		this.newScope = newScope;
	}
	public String getDeleteLabel() {
		if (getOldScope() != null) {
			return "Borrar el scope " + getOldScope().getDescription();
		}
		setRemoveOldScope(false);	
		return "";
	}
	                 
	public boolean isRemovableOldScope() {
		if (getOldScope() != null) {
			Occam occam = getOccam();
			return AonNumberUtils.equals(occam.getDomain(),getOldScope().getDomain()); 
		}
		return false;
	}
	
	public boolean isRemoveOldScope() {
		return removeOldScope;
	}
	public void setRemoveOldScope(boolean removeOldScope) {
		this.removeOldScope = removeOldScope;
	}
	public void onScopeOldChanged(ActionEvent event) {
		System.out.println( oldScope == null ? "NULL" : oldScope.getDescription());
	}
	
	@Override
	public void initializeModel() {
		try {
			UserUtils.getInstance().addForceHeredityDomainCondition(getCriteria(), getFieldName(IEntityAlias.SCOPE_DOMAIN) );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}		
		super.initializeModel();
	}
	
	public boolean isAyudaT() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		return ds.getDomainNameURL().contains("ayudat");
	}

	public void onReassignShow(ActionEvent event) {
		setOldScope( null );
		setNewScope( null );
		setRemoveOldScope( true );
		fillScopes();
		fillUsedScopes();
	}
	
	private Occam getOccam() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		return new Occam()
			.setDomainName(ds.getCurrentDomainName())
			.setDomain(ds.getDomainId())
			.setUser(ds.getCurrentUser());
		
	}
	
	public void onReassign(ActionEvent event) {
		try {
			if (getOldScope() == null || getNewScope() == null) {
				String message = "Debe seleccionar los \u00E1mbitos origen y destino.";
				AonUtil.addErrorMessage(message);
			} else if (getOldScope().getId().equals(getNewScope().getId())) {
				String message = "El \u00E1mbito origen y destino no pueden ser iguales.";
				AonUtil.addErrorMessage(message);
			} else {
				Occam occam = getOccam();
				if (isRemoveOldScope() && isRemovableOldScope()) {
					AON.reassignAndDeleteScope( occam, occam.getDomain(), getOldScope().getId(), getNewScope().getId() );
					onSearch(event);
				} else {
					AON.reassignScope( occam, occam.getDomain(), getOldScope().getId(), getNewScope().getId() );
				}
				this.usedScopesInDomain = null;
			}
		} catch (Exception e) {
			String msg = "No se han podido reasignar los registros del \u00E1mbito. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public boolean isUsed() {
		try {
			if (getModel() != null && getModel().getRowData() != null) {
				Scope scope = (Scope) getModel().getRowData();
				return AonCollectionUtils.stream(getUsedScopesInDomain())
					.anyMatch( s -> AonNumberUtils.equals( scope.getId(), s.getId()));
			}
		} catch (ManagerBeanException e) {
			// Nothing.
		}
		return true; /// Se desconoce. Se devuelve lo menos malo.
		
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		Scope scope = (Scope) getTo();
		Integer scopeId = scope.getId();
		Occam occam = getOccam();
		if (AON.canScopeBeDeleted( occam, occam.getDomain(), scopeId)) {
			super.onRemove(event);
		} else {
			String message = "El \u00E1mbito no puede ser borrado, tiene dependencias en otras entidades.";
			AonUtil.addErrorMessage(message);
		};
	}
	
	public List<SelectItem> getScopes() {
		return scopes;
	}
	private void fillScopes() {
		Occam occam = getOccam();
		scopes = new LinkedList<>();
		try {
			IManagerBean scopeBean = BeanManager.getManagerBean(Scope.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(scopeBean.getFieldName(IEntityAlias.SCOPE_DESCRIPTION));
			for (ITransferObject ito : scopeBean.getList(criteria)) {
				Scope scope = (Scope)ito;
				if (AonNumberUtils.equals(scope.getDomain(), occam.getDomain())) {
					SelectItem item = new SelectItem(scope, scope.getDescription());
					scopes.add(item);
				}
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			AonUtil.addErrorMessage(e.getMessage());
			scopes = new LinkedList<>();
		}
	}
	
	public List<com.esferalia.aon.occam.api.model.security.Scope> getUsedScopesInDomain() {
		if ( usedScopesInDomain == null) {
			Occam occam = getOccam();
			try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
				usedScopesInDomain = ScopeDAO.getUsedScopesInDomain(ctx, ctx.getDomainId())
					.collect( Collectors.toCollection(LinkedList::new));
			}		
		}
		return usedScopesInDomain;
	}
	public List<SelectItem> getUsedScopes() {
		return usedScopes;		
	}
	private void fillUsedScopes() {
		Occam occam = getOccam();
		usedScopes = AonCollectionUtils.stream(getUsedScopesInDomain())
			.map( scope -> {
				Scope s = new Scope();
				s.setId( scope.getId() );
				s.setDomain( scope.getDomain() );
				s.setDescription( scope.getDescription() );
				if ( AonNumberUtils.notEquals( s.getDomain(), occam.getDomain())) {
					s.setDescription( "(*) " + scope.getDescription() );	
				}
				return s;
			})
			.map( scope -> new SelectItem(scope,scope.getDescription()))
			.collect( Collectors.toCollection(LinkedList::new));
	}
	
}

