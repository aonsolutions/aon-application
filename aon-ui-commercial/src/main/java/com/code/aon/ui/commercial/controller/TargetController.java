package com.code.aon.ui.commercial.controller;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.QuestionType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.sales.bridge.util.SalesBridgeUtil;
import com.code.aon.ui.commercial.ICommercialMessages;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.RegistryController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TargetController extends RegistryController implements ICommercialConstants {

	private Set<Integer> checks = new HashSet<Integer>();
	
	private IControllerListener questionListener;
	
	private ResourceBundle bundle;

	public TargetController() {
		setBundleName(ICommercialMessages.BUNDLE_KEY);
	}
	
	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundleName(String bundleName) {
		this.bundle = AonUtil.getResourceBundle(bundleName);
	}	
		
	public String getAliasPreffix() {
		return getPojoShortName();
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		checkNone(event);
	}
	
	public Set<Integer> getCheckedTargets() {
		return checks;
	}
	
	protected Integer getId( Object o ) {
		return ((Target) o).getId();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator<ITransferObject> iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			checks.add( getId(iter.next()) );
		}
	}

	public void checkNone(ActionEvent event) {
		this.checks.clear();
	}

	public boolean getRowChecked() {
		Integer id = getId( model.getRowData() );
		return checks.contains(id);
	}

	public void setRowChecked(boolean rowChecked) {
		Integer id = getId(model.getRowData());		
		if (rowChecked) {
			if (!checks.contains(id)) {
				checks.add(id);
			}
		} else {
			if (checks.contains(id)) {
				checks.remove(id);
			}
		}
	}
	
	public String getListReportKey() {
		return ICommercialConstants.TARGET_LIST;
	}

	public String getListDetailReportKey() {
		return ICommercialConstants.TARGET_LIST_DETAIL;
	}

	public String getListDetailExcelReportKey() {
		return ICommercialConstants.TARGET_LIST_DETAIL_EXCEL;
	}
	
	public void onLoadCustomer(ActionEvent event) throws ManagerBeanException {
		BasicController customerController = (BasicController)AonUtil.getRegisteredBean(CUSTOMER_CONTROLLER_NAME);
		customerController.onLoad(event, ((Target)getTo()).getId(), NAVIGATION_TARGET_FORM, TARGET_CONTROLLER_NAME + ".refresh");
	}

	public void onCreateCustomer(ActionEvent event) throws ManagerBeanException {
		SalesBridgeUtil salesUtil = new SalesBridgeUtil();
		Customer customer = salesUtil.createCustomer((Target)getTo());
		((Target)getTo()).setCustomer(customer.getId()!=null);
	}

	public IControllerListener getQuestionListener() {
		if ( questionListener == null ) {
			questionListener = new ControllerAdapter() {
				@Override
				public void beforeModelInitialized(ControllerEvent event)
						throws ControllerListenerException {
					IController controller = event.getController();
					try {
						String alias = controller.getFieldName(IEntityAlias.QUESTION_TYPE);
						controller.getCriteria().addNotEqualExpression(alias, QuestionType.INFO);
					} catch (ManagerBeanException e) {
						throw new ControllerListenerException(e);
					} 
				}		
			};
		}
		return questionListener;
	}

	public void setQuestionListener(IControllerListener questionListener) {
		this.questionListener = questionListener;
	}
	
}