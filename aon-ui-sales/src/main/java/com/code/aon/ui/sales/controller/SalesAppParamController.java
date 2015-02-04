package com.code.aon.ui.sales.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalesAppParamController.class.getName());
	
	private ApplicationParameter activeMarketplace;
	
	private boolean amazonEnabled;
	
	private Map<String, String> amazonSalesProductName;
	private Map<String, String> salesChannelMap;
	private Map<String, Seller> sellerMap;
	
	
	public Map<String, String> getAmazonSalesProductName() {
		return amazonSalesProductName;
	}

	public void setAmazonSalesProductName(Map<String, String> amazonSalesProductName) {
		this.amazonSalesProductName = amazonSalesProductName;
	}
	
	public Map<String, String> getSalesChannelMap() {
		return salesChannelMap;
	}

	public void setSalesChannelMap(Map<String, String> salesChannelMap) {
		this.salesChannelMap = salesChannelMap;
	}

	public Map<String, Seller> getSellerMap() {
		return sellerMap;
	}

	public void setSellerMap(Map<String, Seller> sellerMap) {
		this.sellerMap = sellerMap;
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
		return isActiveAmazonMarketPlace();
	}
	
	public void onInit(ActionEvent event){
		loadAmazonMarketplaceParams();
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
		
		acceptAmazonParams();
	}

	public void onLoadDefaultAmazonValues(ActionEvent event){
		getAmazonSalesProductName().put("prefix", "az");
		getAmazonSalesProductName().put("suffix", "az");
		getSalesChannelMap().put("DE", "amazon.de");
		getSalesChannelMap().put("ES", "amazon.es");
		getSalesChannelMap().put("FR", "amazon.fr");
		getSalesChannelMap().put("IT", "amazon.it");
		getSalesChannelMap().put("UK", "amazon.co.uk");
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
		
		loadAmazonSalesChannel();
		
		loadAmazonSeller();
		
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
		for(String value: salesChannelMap.values()){
			m = Pattern.compile(STRING_REGEX).matcher(value);
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
		
		// sales channel
		ApplicationParameter amazonSalesChannel = obtainParam(AppParam.SALES_AMAZON_SALES_CHANNEL);
		builder.delete(0, builder.length());
		builder.append("de=").append(salesChannelMap.get("DE")).append(";");
		builder.append("es=").append(salesChannelMap.get("ES")).append(";");
		builder.append("fr=").append(salesChannelMap.get("FR")).append(";");
		builder.append("it=").append(salesChannelMap.get("IT")).append(";");
		builder.append("uk=").append(salesChannelMap.get("UK")).append(";");
		amazonSalesChannel.setValue(builder.toString());
		AppParamUtil.insertParameter(amazonSalesChannel);
		
		// seller
		ApplicationParameter sellerIds = obtainParam(AppParam.SALES_AMAZON_SELLER_IDS);
		builder.delete(0, builder.length());
		builder.append("de=").append(sellerMap.get("DE").getId()).append(";");
		builder.append("es=").append(sellerMap.get("ES").getId()).append(";");
		builder.append("fr=").append(sellerMap.get("FR").getId()).append(";");
		builder.append("it=").append(sellerMap.get("IT").getId()).append(";");
		builder.append("uk=").append(sellerMap.get("UK").getId()).append(";");
		sellerIds.setValue(builder.toString());
		AppParamUtil.insertParameter(sellerIds);
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
	
	private void loadAmazonSalesChannel(){
		String AMAZON_SALES_CHANNEL_REGEX = ".*de=([^;]*);es=([^;]*);fr=([^;]*);it=([^;]*);uk=([^;]*);.*";
		ApplicationParameter _salesChannel = obtainParam(AppParam.SALES_AMAZON_SALES_CHANNEL);
		salesChannelMap = new HashMap<String, String>();
		if(_salesChannel!=null && _salesChannel.getValue()!=null){
			if(_salesChannel.getValue().matches(AMAZON_SALES_CHANNEL_REGEX)){
				Matcher m = Pattern.compile(AMAZON_SALES_CHANNEL_REGEX).matcher(_salesChannel.getValue());
				if(m.find()) {
					salesChannelMap.put("DE", m.group(1));
					salesChannelMap.put("ES", m.group(2));
					salesChannelMap.put("FR", m.group(3));
					salesChannelMap.put("IT", m.group(4));
					salesChannelMap.put("UK", m.group(5));
				}
			}
		}
	}
	
	private void loadAmazonSeller(){
		try {
			String AMAZON_SALES_CHANNEL_REGEX = ".*de=([^;]*);es=([^;]*);fr=([^;]*);it=([^;]*);uk=([^;]*);.*";
			ApplicationParameter _seller = obtainParam(AppParam.SALES_AMAZON_SELLER_IDS);
			sellerMap = new HashMap<String, Seller>();
			String de = null, es = null, fr = null, it = null, uk = null;
			if(_seller!=null && _seller.getValue()!=null){
				if(_seller.getValue().matches(AMAZON_SALES_CHANNEL_REGEX)){
					Matcher m = Pattern.compile(AMAZON_SALES_CHANNEL_REGEX).matcher(_seller.getValue());
					if(m.find()) {
						de = m.group(1);
						es = m.group(2);
						fr = m.group(3);
						it = m.group(4);
						uk = m.group(5);
					}
				}
			}
			sellerMap.put("DE", obtainSeller(de));
			sellerMap.put("ES", obtainSeller(es));
			sellerMap.put("FR", obtainSeller(fr));
			sellerMap.put("IT", obtainSeller(it));
			sellerMap.put("UK", obtainSeller(uk));
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
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
	
}
