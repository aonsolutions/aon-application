package com.code.aon.ui.sales.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.seller.Seller;
import com.code.aon.ui.util.AonUtil;

public class SalesAppParamController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final String MARKET_SALES_CHANNEL_BASE_NAME = "MARKET_AMZ_SALES_CHANNEL_";

	private static final Logger LOGGER = LoggerFactory.getLogger(SalesAppParamController.class.getName());
	
	private ApplicationParameter activeMarketplace;
	
	private boolean amazonEnabled;
	
	private Map<String, String> amazonSalesProductName;
	

	private List<AmazonSalesChannel> amazonSalesChannels;
	
	public List<AmazonSalesChannel> getAmazonSalesChannels() {
		if (amazonSalesChannels == null) {
			amazonSalesChannels = new LinkedList<SalesAppParamController.AmazonSalesChannel>();
		}	
		return amazonSalesChannels;
	}

	public void setAmazonSalesChannels(List<AmazonSalesChannel> amazonSalesChannels) {
		this.amazonSalesChannels = amazonSalesChannels;
	}
	
	public int getAmazonSalesChannelsSize() {
		return amazonSalesChannels.size();
	}
	
	public Map<String, String> getAmazonSalesProductName() {
		return amazonSalesProductName;
	}

	public void setAmazonSalesProductName(Map<String, String> amazonSalesProductName) {
		this.amazonSalesProductName = amazonSalesProductName;
	}

	public boolean isAmazonEnabled() {
		return amazonEnabled;
	}
	
	public void setAmazonEnabled(boolean amazonEnabled) {
		this.amazonEnabled = amazonEnabled;
	}
	
	public boolean isActiveAmazonMarketPlace(){
		return isAmazonEnabled();
	}
	
	public boolean isActiveMarketPlace(){
		if(activeMarketplace==null){
			loadAmazonMarketplaceParams();
		}
		return isActiveAmazonMarketPlace();
	}
	
	public void onInit(ActionEvent event){
		loadAmazonMarketplaceParams();
	}
	
	public void onAddAmazonSalesChannel(ActionEvent event) throws ManagerBeanException {
		this.amazonSalesChannels.add(new AmazonSalesChannel());
	}

	public void onRemoveAmazonSalesChannel(ActionEvent event) throws ManagerBeanException {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));
		this.amazonSalesChannels.remove(index);
		if ( this.amazonSalesChannels.isEmpty() ) {
			this.amazonSalesChannels.add(new AmazonSalesChannel());
		}
	}

	public void accept(ActionEvent event){
		String marketplaces = null;
		if(isAmazonEnabled()){
			marketplaces = "AMAZON";
		}
		if(StringUtils.isNotBlank(marketplaces)){
			activeMarketplace.setValue(marketplaces);
			AppParamUtil.insertParameter(activeMarketplace);
		} else {
			AppParamUtil.removeParameter(AppParam.SALES_ACTIVE_MARKETPLACE);
		}
		
		validateAmazonParams();
		
		try {
			saveAmazonSalesChannels();
			loadAmazonSalesChannels();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}

	}

	public void onLoadDefaultAmazonValues(ActionEvent event) throws ManagerBeanException{
		getAmazonSalesProductName().put("prefix", "az");
		getAmazonSalesProductName().put("suffix", "az");
		
		getAmazonSalesChannels().clear();
		AmazonSalesChannel asc = new AmazonSalesChannel();
		asc.setName("amazon.de");
		getAmazonSalesChannels().add(asc);
		asc = new AmazonSalesChannel();
		asc.setName("amazon.es");
		getAmazonSalesChannels().add(asc);
		asc = new AmazonSalesChannel();
		asc.setName("amazon.fr");
		getAmazonSalesChannels().add(asc);
		asc = new AmazonSalesChannel();
		asc.setName("amazon.it");
		getAmazonSalesChannels().add(asc);
		asc = new AmazonSalesChannel();
		asc.setName("amazon.co.uk");
		getAmazonSalesChannels().add(asc);
	}

	private void loadAmazonMarketplaceParams() {
		activeMarketplace = AppParamUtil.getParameter(AppParam.SALES_ACTIVE_MARKETPLACE);
		if(activeMarketplace==null){
			activeMarketplace = new ApplicationParameter();
			activeMarketplace.setName(AppParam.SALES_ACTIVE_MARKETPLACE.name());
			activeMarketplace.setSystemParameter(false);
		}
		setAmazonEnabled(ArrayUtils.contains(StringUtils.split(activeMarketplace.getValue(), ","), "AMAZON"));
		
		loadAmazonProductName();
		
		try {
			loadAmazonSalesChannels();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
		
	}

	public void validateAmazonParams() {
		String STRING_REGEX = "^[^;]*$";
		Matcher m = null;
		for(String value: amazonSalesProductName.values()){
			m = Pattern.compile(STRING_REGEX).matcher(value);
			if(!m.find()) {
				AonUtil.addErrorMessage("El nombre de producto indicado no es un valor válido.");
				throw new AbortProcessingException("El nombre de producto indicado no es un valor válido.");
			}
		}
		for(AmazonSalesChannel asc: getAmazonSalesChannels()){
			m = Pattern.compile(STRING_REGEX).matcher(asc.getName());
			if(!m.find()) {
				AonUtil.addErrorMessage("El nombre del canal de venta indicado no es un valor válido.");
				throw new AbortProcessingException("El nombre del canal de venta indicado no es un valor válido.");
			}
		}
	}
	
	public void acceptAmazonParams() {
		// prefix and suffix
		StringBuilder builder = new StringBuilder();
		ApplicationParameter amazonSalesProductName = obtainParam(AppParam.SALES_AMAZON_PRODUCT_NAME);
		builder.append("prefix=").append(this.amazonSalesProductName.get("prefix")).append(";");
		builder.append("suffix=").append(this.amazonSalesProductName.get("suffix")).append(";");
		amazonSalesProductName.setValue(builder.toString());
		AppParamUtil.insertParameter(amazonSalesProductName);
	}
		
	private void loadAmazonProductName(){
		String AMAZON_SALES_PRODUCT_PREFIX_REGEX = ".*prefix=([^;]*);.*";
		String AMAZON_SALES_PRODUCT_SUFFIX_REGEX = ".*suffix=([^;]*);.*";
		
		ApplicationParameter _amazonSalesProductName = obtainParam(AppParam.SALES_AMAZON_PRODUCT_NAME);
		amazonSalesProductName = new HashMap<String, String>();
		
		if(_amazonSalesProductName!=null && _amazonSalesProductName.getValue()!=null){
			if(_amazonSalesProductName.getValue().matches(AMAZON_SALES_PRODUCT_PREFIX_REGEX)){
				Matcher m = Pattern.compile(AMAZON_SALES_PRODUCT_PREFIX_REGEX).matcher(_amazonSalesProductName.getValue());
				if(m.find()) {
					amazonSalesProductName.put("prefix", m.group(1));
				}
			}
		} 
		if(_amazonSalesProductName!=null && _amazonSalesProductName.getValue()!=null){
			if(_amazonSalesProductName.getValue().matches(AMAZON_SALES_PRODUCT_SUFFIX_REGEX)){ 
				Matcher m = Pattern.compile(AMAZON_SALES_PRODUCT_SUFFIX_REGEX).matcher(_amazonSalesProductName.getValue());
				if(m.find()) {
					amazonSalesProductName.put("suffix", m.group(1));
				}
			}
		}
	}
	
	private ApplicationParameter obtainParam(AppParam appParam){
		ApplicationParameter param = AppParamUtil.getParameter(appParam);
		if(param==null){
			param = new ApplicationParameter();
			param.setName(appParam.toString());
		}
		return param;
	}
	
	private Seller obtainSeller(String value) throws ManagerBeanException {
		IManagerBean sellerBean = BeanManager.getManagerBean(Seller.class);
		if(NumberUtils.isNumber(value)){
			return (Seller) sellerBean.get(Integer.parseInt(value));
		}
		return (Seller) sellerBean.createNewTo();
	}
	
	private void loadAmazonSalesChannels() throws ManagerBeanException {
		getAmazonSalesChannels().clear();
		
		boolean continueSearch = true;
		int counter = 0;
		ApplicationParameter amazonSalesChannel = null;
		while(continueSearch && counter < 1000){
			amazonSalesChannel = AppParamUtil.getParameter(MARKET_SALES_CHANNEL_BASE_NAME + counter);
			if(amazonSalesChannel==null){
				continueSearch = false;
			} else {
				String[] values = amazonSalesChannel.getValue().split(";");
				AmazonSalesChannel salesChannel = new AmazonSalesChannel();
				salesChannel.setName(values.length>0?values[0]:null);
				salesChannel.setSeller(values.length>1?obtainSeller(values[1]):obtainSeller(null));
				getAmazonSalesChannels().add(salesChannel);
			}
			counter++;
		}
		if(getAmazonSalesChannels()==null || getAmazonSalesChannels().size()==0){
			amazonSalesChannels.add(new AmazonSalesChannel());
		}
	}

	private void saveAmazonSalesChannels() throws ManagerBeanException {
		StringBuilder builder = new StringBuilder();
		ListIterator<AmazonSalesChannel> it = getAmazonSalesChannels().listIterator();
		int counter = -1;
		while(it.hasNext()){
			counter = it.nextIndex();
			ApplicationParameter amazonSalesChannelParam = AppParamUtil.getParameter(MARKET_SALES_CHANNEL_BASE_NAME + counter);
			if(amazonSalesChannelParam==null){
				amazonSalesChannelParam = new ApplicationParameter();
				amazonSalesChannelParam.setName(MARKET_SALES_CHANNEL_BASE_NAME + counter);
			}
			
			AmazonSalesChannel asc = it.next();
			if(StringUtils.isNotBlank(asc.getName().trim()) || (asc.getSeller()!=null && asc.getSeller().getId()!=null)){
				builder.delete(0, builder.length());
				if(StringUtils.isNotBlank(asc.getName().trim())){
					builder.append(asc.getName());
				}
				builder.append(";");
				if(asc.getSeller()!=null && asc.getSeller().getId()!=null){
					builder.append(asc.getSeller().getId());
				}
				amazonSalesChannelParam.setValue(builder.toString());
				AppParamUtil.insertParameter(amazonSalesChannelParam);
			}
		}
		// remove defined remaining sales channels
		ApplicationParameter amazonSalesChannelParam = AppParamUtil.getParameter(MARKET_SALES_CHANNEL_BASE_NAME + ++counter);
		while(amazonSalesChannelParam!=null){
			amazonSalesChannelParam.setValue(null);
			AppParamUtil.insertParameter(amazonSalesChannelParam);
			amazonSalesChannelParam = AppParamUtil.getParameter(MARKET_SALES_CHANNEL_BASE_NAME + (++counter));
		}
	}


	
	// **********************************************************
	
	public static class AmazonSalesChannel implements Serializable {
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private String name;
		private Seller seller;
		
		public AmazonSalesChannel() throws ManagerBeanException{
			seller = (Seller) BeanManager.getManagerBean(Seller.class).createNewTo();
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Seller getSeller() {
			return seller;
		}
		public void setSeller(Seller seller) {
			this.seller = seller;
		}
	}
	
}
