package com.code.aon.ui.ecommerce.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.Tariff;
import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.ebackoffice.enumeration.DiscountFormat;
import com.code.aon.ebackoffice.enumeration.LoginType;
import com.code.aon.ebackoffice.enumeration.ShowPrice;
import com.code.aon.ebackoffice.enumeration.SkinType;
import com.code.aon.ebackoffice.enumeration.TaxType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.ecommerce.util.ECommerceUtil;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;



public class ConfigController {

	private List<ITransferObject> configList;
	private Ecconfig activeConfig;
	private Company company;
	private boolean aonEbackoffice;
//	private String PayerID;
	
//	public String getPayerID() {
//		return PayerID;
//	}
//
//	public void setPayerID(String payerID) {
//		PayerID = payerID;
//	}
	
	
	public Ecconfig getActiveConfig() {
		if (activeConfig == null) {
			searchActiveConfig();
		} else if(isAonEbackoffice()==true){
			searchActiveConfig();
		}
		return activeConfig;
	}

	public void setActiveConfig(Ecconfig activeConfig) {
		this.activeConfig = activeConfig;
	} 
	
	public Company getCompany() {
		if(company==null){
			searchCurrentCompany();
		}
		return company;
	}

	public boolean isAonEbackoffice(){
		aonEbackoffice=Boolean.parseBoolean(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(IECommerceConstants.AON_EBACKOFFICE));
		return aonEbackoffice;
	}
	
	public void setAonEbackoffice(boolean aonEbackoffice) {
		this.aonEbackoffice = aonEbackoffice;
	}
	
	public boolean isPaypalReturn() {
		String token = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("token");
		String payerId = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("PayerID");
		if (token != null && payerId != null) {
			PaypalController paypal = (PaypalController) AonUtil
					.getRegisteredBean(IECommerceConstants.PAYPAL_CONTROLLER);
			paypal.setToken(token);
			paypal.setPayerId(payerId);
			paypal.setPayment(true);

			// limpia la url de parametros
			try {
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect(ECommerceUtil.getUrl());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			paypal.confirmPaymentFromPayPal(null);
			((ShopController) AonUtil
					.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
					.setContentView(ViewEnum.PAYMETHOD);
			return true;
		}
		return false;
	}
	
	public void setCompany(Company company) {
		this.company = company;
	}
	
	public String getSkin(){
		return getActiveConfig().getSkin().getName(AonUtil.getCurrentLocale());
	}
	
	private void searchActiveConfig() {
		Iterator<ITransferObject> it = getConfigList().iterator();
		boolean found=false;
		
		while(it.hasNext() && !found){
			Ecconfig econf = (Ecconfig)it.next();
			if(econf.isActive()){
				setActiveConfig(econf);
				found = true;
			}
		}
	}

	private List<ITransferObject> getConfigList() {
		try {
			if (configList == null || configList.size()>=0) {
				IManagerBean bean = BeanManager.getManagerBean(Ecconfig.class);
				configList = bean.getList(null);
				if (configList.size() == 0){
					createDefaultConfig();
				}
			}
			if (configList == null || configList.size()<=0) {
				throw new ManagerBeanException();
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar la configuracion";
			throw new AbortProcessingException(msg, e);
		}
		return configList;
	}
	
	private void createDefaultConfig(){
		try {
			IManagerBean bean = BeanManager.getManagerBean(Ecconfig.class);
			Ecconfig to = new Ecconfig();
			to.setActive(true);
			to.setName("default");
			to.setSkin(SkinType.BASICO1);
			to.setEmail("your@email.com");
			Tariff tariff = new Tariff();
			tariff.setId(1);
			to.setTariff(tariff);
			bean.insert(to);
			configList = bean.getList(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Imposible acceder a la configuracion.");
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void paintHeader(OutputStream out, Object data) throws IOException {
		out.write(activeConfig.getHeaderImg());
	}
	public void paintLeftBanner(OutputStream out, Object data) throws IOException {
		out.write(activeConfig.getLeftBanner());
	}
	public void paintRightBanner(OutputStream out, Object data) throws IOException {
		out.write(activeConfig.getRightBanner());
	}
	public void paintWelcomeBanner(OutputStream out, Object data) throws IOException {
		out.write(activeConfig.getWelcomeBanner());
	}
	
	public void searchCurrentCompany(){
		((CompanyController)FormUtil.getController("company")).onSearch(null);
		((CompanyController)FormUtil.getController("company")).onSelectFirst(null);
		setCompany(((Company)FormUtil.getController("company").getTo()));
	}
	
	public List<SelectItem> getPayMethodList(){
		return ECommerceUtil.payMethodList();
	}

	public boolean isHeaderImage() {
		return (getActiveConfig().getHeaderImg()!=null); 
	}
	public boolean isLeftBanner() {
		return (getActiveConfig().getLeftBanner()!=null); 
	}
	public boolean isRightBanner() {
		return (getActiveConfig().getRightBanner()!=null); 
	}
	public boolean isWelcomeBanner() {
		return (getActiveConfig().getWelcomeBanner()!=null); 
	}
	public boolean isTaxIncluded() {
		return (getActiveConfig().getTaxInPrice() == TaxType.DEFAULT || getActiveConfig().getTaxInPrice() == TaxType.YES); 
	}
	public boolean isShowOriginalPrice() {
		return (getActiveConfig().getPrice() == ShowPrice.DEFAULT || getActiveConfig().getPrice()  == ShowPrice.YES); 
	}
	public boolean isShowDiscount() {
		return (getActiveConfig().getDiscount() != DiscountFormat.NO); 
	}
	public boolean isShowPriceCrossOut() {
		return (getActiveConfig().getDiscount() == DiscountFormat.CROSS_OUT); 
	}
	public boolean isShowDiscountValue() {
		return (getActiveConfig().getDiscount() == DiscountFormat.DISCOUNT); 
	}
	public boolean isShowDiscountPercent() {
		return (getActiveConfig().getDiscount() == DiscountFormat.PERCENT); 
	}
	
	public boolean isNeverLogin() {
		return (getActiveConfig().getShowLogin() == LoginType.NEVER); 
	}
	public boolean isAlwaisLogin() {
		return (getActiveConfig().getShowLogin() == LoginType.ALWAYS); 
	}
	public boolean isOnDemandLogin() {
		return (getActiveConfig().getShowLogin() == LoginType.ON_DEMAND); 
	}
	
	
	
}
