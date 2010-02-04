package com.code.aon.ui.registry.controller.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;

public class RegistryPayMethodSearchListener extends RegistrySearchListener {

	private static final String RPAY_METHOD_SUFFIX = "RPayMethod";

	private static final String RBANK_SUFFIX = "Rbank";

	private static final Logger LOGGER = LoggerFactory.getLogger(RegistryPayMethodSearchListener.class);
	
	private static final PayMethod EMPTY_PAYMETHOD = new PayMethod();
	
	private List<PayMethod> payMethods;
	
	private LinesController registryPayMethod;
	
	private LinesController registryBank;
	
	public List<PayMethod> getPayMethods() {
		if (payMethods == null) {
			payMethods = new LinkedList<PayMethod>();
			payMethods.add(EMPTY_PAYMETHOD);
		}
		return payMethods;
	}

	public void setPayMethods(List<PayMethod> payMethods) {
		this.payMethods = payMethods;
	}
	
	public int getPayMethodsSize() {
		return this.payMethods.size();
	}	

	public List<Integer> getPayMethodsIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for(PayMethod payMethod : getPayMethods()) {
			if ((payMethod != null) && (payMethod.getId() != null)) {
				ids.add(payMethod.getId());
			}
		}
		return ids;
	}
	
	public PayMethod getEmptyPayMethod() {
		return EMPTY_PAYMETHOD;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setPayMethods(new LinkedList<PayMethod>());
		getPayMethods().add(EMPTY_PAYMETHOD);
		super.init();
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		String payMethod = resolveAlias("payMethods_payment_id");
		addEnumToCriteria(criteria, payMethod, getPayMethodsIds().toArray());
		super.completeCriteria();
	}
	
	public void onAddPayMethod(ActionEvent event) {
		getPayMethods().add(EMPTY_PAYMETHOD);
	}
	
	public void onRemovePayMethod(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		getPayMethods().remove(index);
		if (getPayMethods().isEmpty()) {
			getPayMethods().add(EMPTY_PAYMETHOD);
		}
	}

	@Override
	protected void initControllers( String name ) {
		super.initControllers(name);
		this.registryPayMethod = (LinesController) AonUtil.getRegisteredBean(name + RPAY_METHOD_SUFFIX);
		if ( registryPayMethod == null ) {
			LOGGER.error( "Registry Pay Method Managed Bean not found for {}", name );
		}			
		this.registryBank = (LinesController) AonUtil.getRegisteredBean(name + RBANK_SUFFIX);
		if ( registryBank == null ) {
			LOGGER.error( "Registry Bank Managed Bean not found for {}", name );
		}					
	}

	@Override
	protected void updateListeners( IControllerListener controllerListener ) throws ControllerListenerException {
		super.updateListeners(controllerListener);
		RegistryPayMethodLookupListener listener = (RegistryPayMethodLookupListener) controllerListener;
		try {
			if ( registryBank != null ) {
				listener.updateRegistryBank(registryBank);
			}
			if ( registryPayMethod != null ) {
				listener.updateRegistryPayMethod(registryPayMethod);	
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		try {		
			RegistryPayMethodLookupListener listener = (RegistryPayMethodLookupListener) getLookupListener(event);
			listener.checkRegistryBank();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}	
	
}