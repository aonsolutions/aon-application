package com.code.aon.ui.audabridge.controller;



import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class AudabridgeParametersController {
	
	public static final String AUDABRIDGE_PARAMS_BEAN_NAME = "audabridgeParams";
	
	public static final String AUDABRIDGE_PATTERN = "AUDABRIDGE_*";
	public static final String AUDABRIDGE_ACCESS_KEY = "AUDABRIDGE_ACCESS_KEY";
	public static final String AUDABRIDGE_CUSTOMER_ID = "AUDABRIDGE_CUSTOMER_ID";
	public static final String AUDABRIDGE_AUDAPLUS_PATH = "AUDABRIDGE_AUDAPLUS_PATH";
	
	public static final String AUDABRIDGE_PAINT_HOUR_RATE= "AUDABRIDGE_PAINT_HOUR_RATE";
	public static final String AUDABRIDGE_BODYWORK_HOUR_RATE = "AUDABRIDGE_BODYWORK_HOUR_RATE";
	public static final String AUDABRIDGE_LABOUR_HOUR_RATE = "AUDABRIDGE_LABOUR_RATE";

	public static final String AUDABRIDGE_OPERATION_ITEM = "AUDABRIDGE_OPERATION_ITEM";
	public static final String AUDABRIDGE_SPARE_PART_ITEM = "AUDABRIDGE_SPARE_PART_ITEM";
	public static final String AUDABRIDGE_PAINT_ITEM = "AUDABRIDGE_PAINT_ITEM";
	public static final String AUDABRIDGE_DISCOUNT_ITEM = "AUDABRIDGE_DISCOUNT_ITEM";
	public static final String AUDABRIDGE_INTEGRATION_LEVEL = "AUDABRIDGE_INTEGRATION_LEVEL";
	
	
	private Map<String, ApplicationParameter> parameters;
	private IManagerBean managerBean;

	private Item operationItem;
	private Item sparePartItem;
	private Item paintItem;
	private Item discountItem;

	public Map<String, ApplicationParameter> getParameters() {
		try {
			if (parameters == null) {
				loadParameters();
				loadDefaultParameters();
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		return parameters;
	}
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (managerBean == null) {
			managerBean = BeanManager.getManagerBean(ApplicationParameter.class);	
		}
		return managerBean;
	}
	
	private void loadParameters() throws ManagerBeanException {
		parameters = new TreeMap<String, ApplicationParameter>();
		List<ITransferObject> list = getManagerBean().getList(getCriteria());
		for (ITransferObject to: list) {
			ApplicationParameter appParam = (ApplicationParameter) to;
			parameters.put(appParam.getName(), appParam);
		}
	}

	private void loadDefaultParameters() throws ManagerBeanException {
		String[] keys = {
				AUDABRIDGE_ACCESS_KEY
				,AUDABRIDGE_CUSTOMER_ID
				,AUDABRIDGE_AUDAPLUS_PATH
				,AUDABRIDGE_PAINT_HOUR_RATE
				,AUDABRIDGE_BODYWORK_HOUR_RATE
				,AUDABRIDGE_LABOUR_HOUR_RATE
				,AUDABRIDGE_OPERATION_ITEM
				,AUDABRIDGE_SPARE_PART_ITEM
				,AUDABRIDGE_PAINT_ITEM
				,AUDABRIDGE_DISCOUNT_ITEM
				,AUDABRIDGE_INTEGRATION_LEVEL
			};
		
		for (String key : keys) {
			if (!parameters.containsKey(key)) {
				ApplicationParameter p = new ApplicationParameter();
				p.setName(key);
				p.setValue(null);
				p.setSystemParameter(true);
				getManagerBean().insert(p);
				parameters.put(key, p);
			}
		}
	}

	private Criteria getCriteria() throws ManagerBeanException {
		try {
			Criteria criteria = new Criteria();
			String nameAlias = getManagerBean().getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME);
			criteria.addExpression(nameAlias, AUDABRIDGE_PATTERN);
			return criteria;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		} 
	}
	
	public String getAccessKey() {
		return getParameters().get(AUDABRIDGE_ACCESS_KEY).getValue(); 
	}
	public void setAccessKey(String AccessKey) {
		getParameters().get(AUDABRIDGE_ACCESS_KEY).setValue(AccessKey);
	}
	
		
	public String getCustomerId() {
		return getParameters().get(AUDABRIDGE_CUSTOMER_ID).getValue(); 
	}
	public void setCustomerId(String CustomerId) {
		getParameters().get(AUDABRIDGE_CUSTOMER_ID).setValue(CustomerId);
	}
	
	public String getAudaPlusPath() {
		return getParameters().get(AUDABRIDGE_AUDAPLUS_PATH).getValue(); 
	}
	public void setAudaPlusPath(String audaPlusPath) {
		getParameters().get(AUDABRIDGE_AUDAPLUS_PATH).setValue(audaPlusPath);
	}
	
	public Item getOperationItem() {
		try {
			if (operationItem == null) {
				String id = getParameters().get(AUDABRIDGE_OPERATION_ITEM).getValue();
				IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
				if (StringUtils.isNotBlank(id)) {
					operationItem = (Item) itemBean.get(Integer.parseInt(id));
				}
				if (operationItem == null) {
					operationItem = (Item) itemBean.createNewTo();	
				}
			}
		} catch (ManagerBeanException e) {
			operationItem = new Item();
			operationItem.setProduct(new Product());
		} 
		return operationItem;
	}
	
	public void setOperationItem(Item operationItem) {
		this.operationItem = operationItem;
		String id = null;
		if (operationItem != null && operationItem.getId() != null ) {
			id = operationItem.getId().toString(); 
		}
		getParameters().get(AUDABRIDGE_OPERATION_ITEM).setValue(id);
	}
	
	public Item getSparePartItem() {
		try {
			if (sparePartItem == null) {
				String id = getParameters().get(AUDABRIDGE_SPARE_PART_ITEM).getValue();
				IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
				if (StringUtils.isNotBlank(id)) {
					sparePartItem = (Item) itemBean.get(Integer.parseInt(id));
				}
				if (sparePartItem == null) {
					sparePartItem = (Item) itemBean.createNewTo();	
				}
			}
		} catch (ManagerBeanException e) {
			sparePartItem = new Item();
			sparePartItem.setProduct(new Product());
		} 
		return sparePartItem;
	}
	
	public void setSparePartItem(Item sparePartItem) {
		this.sparePartItem = sparePartItem;
		String id = null;
		if (sparePartItem != null && sparePartItem.getId() != null ) {
			id = sparePartItem.getId().toString(); 
		}
		getParameters().get(AUDABRIDGE_SPARE_PART_ITEM).setValue(id);
	}

	public Item getPaintItem() {
		try {
			if (paintItem == null) {
				String id = getParameters().get(AUDABRIDGE_PAINT_ITEM).getValue();
				IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
				if (StringUtils.isNotBlank(id)) {
					paintItem = (Item) itemBean.get(Integer.parseInt(id));
				}
				if (paintItem == null) {
					paintItem = (Item) itemBean.createNewTo();	
				}
			}
		} catch (ManagerBeanException e) {
			paintItem = new Item();
			paintItem.setProduct(new Product());
		} 
		return paintItem;
	}
	
	public void setPaintItem(Item paintItem) {
		this.paintItem = paintItem;
		String id = null;
		if (paintItem != null && paintItem.getId() != null ) {
			id = paintItem.getId().toString(); 
		}
		getParameters().get(AUDABRIDGE_PAINT_ITEM).setValue(id);
	}

	public Item getDiscountItem() {
		try {
			if (discountItem == null) {
				String id = getParameters().get(AUDABRIDGE_DISCOUNT_ITEM).getValue();
				IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
				if (StringUtils.isNotBlank(id)) {
					discountItem = (Item) itemBean.get(Integer.parseInt(id));
				}
				if (discountItem == null) {
					discountItem = (Item) itemBean.createNewTo();	
				}
			}
		} catch (ManagerBeanException e) {
			discountItem = new Item();
			discountItem.setProduct(new Product());
		} 
		return discountItem;
	}
	
	public void setDiscountItem(Item discountItem) {
		this.discountItem = discountItem;
		String id = null;
		if (discountItem != null && discountItem.getId() != null ) {
			id = discountItem.getId().toString(); 
		}
		getParameters().get(AUDABRIDGE_DISCOUNT_ITEM).setValue(id);
	}

	public double getPaintHourRate() {
		String d = getParameters().get(AUDABRIDGE_PAINT_HOUR_RATE).getValue();
		return (StringUtils.isBlank(d))?0.0:Double.parseDouble(d);
	}
	public void setPaintHourRate(double paintHourRate) {
		getParameters().get(AUDABRIDGE_PAINT_HOUR_RATE).setValue(Double.toString(paintHourRate));
	}
	
	public double getBodyworkHourRate() {
		String d = getParameters().get(AUDABRIDGE_BODYWORK_HOUR_RATE).getValue();
		return (StringUtils.isBlank(d))?0.0:Double.parseDouble(d);
	}
	public void setBodyworkHourRate(double bodyworkHourRate) {
		getParameters().get(AUDABRIDGE_BODYWORK_HOUR_RATE).setValue(Double.toString(bodyworkHourRate));
	}
	
	public double getLabourHourRate() {
		String d = getParameters().get(AUDABRIDGE_LABOUR_HOUR_RATE).getValue();
		return (StringUtils.isBlank(d))?0.0:Double.parseDouble(d);
	}
	public void setLabourHourRate(double labourHourRate) {
		getParameters().get(AUDABRIDGE_LABOUR_HOUR_RATE).setValue(Double.toString(labourHourRate));
	}
	
	public int getIntegrationLevel() {
		String d = getParameters().get(AUDABRIDGE_INTEGRATION_LEVEL).getValue();
		return (StringUtils.isBlank(d))?0:Integer.parseInt(d);
	}
	public void setIntegrationLevel(int integrationLevel) {
		getParameters().get(AUDABRIDGE_INTEGRATION_LEVEL).setValue(Integer.toString(integrationLevel));
	}

	public void onLoad(ActionEvent event) {
		try {
			loadParameters();
			loadDefaultParameters();
		} catch (ManagerBeanException e) {
			String msg = "Unable to load defaultParameters";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onAccept(ActionEvent event) throws ManagerBeanException{
		Collection<ApplicationParameter>params = parameters.values();
		for(ApplicationParameter param : params){
			getManagerBean().update(param);
		}
		loadParameters();
		AonUtil.addInfoMessage("Los parámetros se guardaron correctamente.");		
	}
}
