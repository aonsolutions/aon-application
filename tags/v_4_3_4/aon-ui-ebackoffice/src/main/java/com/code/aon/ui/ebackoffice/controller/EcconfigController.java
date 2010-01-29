package com.code.aon.ui.ebackoffice.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Tariff;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.ebackoffice.enumeration.DiscountFormat;
import com.code.aon.ebackoffice.enumeration.LoginType;
import com.code.aon.ebackoffice.enumeration.ShowPrice;
import com.code.aon.ebackoffice.enumeration.SkinType;
import com.code.aon.ebackoffice.enumeration.TaxType;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;

public class EcconfigController extends BasicController {

	private List<SelectItem> skins;
	private List<SelectItem> loginTypes;
	private List<SelectItem> priceTypes;
	private List<SelectItem> taxPriceTypes;
	private List<SelectItem> discountTypes;
	private List<SelectItem> bankTransfers;
	private List<SelectItem> cashOnDeliverys;
	private List<SelectItem> bankDrafts;
	private List<SelectItem> creditCards;
	private List<SelectItem> paypals;
	private List<SelectItem> tariffs;
	private AonFile headerImage;
	private AonFile leftBanner;
	private AonFile rightBanner;
	private AonFile welcomeBanner;
	private boolean login;
	private boolean showPrice;
	public boolean richTextEnabled;
	private String selectedTab;
	private boolean ecParam;
		
	
	public boolean isEcParam() {
		try {
			ecParam=getEcommerceParam();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ecParam;
	}

	public void setEcParam(boolean ecParam) {
		this.ecParam = ecParam;
	}

	public String getSelectedTab() {
		return selectedTab;
	}
	
	public boolean isPaymethodTab() {
		String s= "tab3";
		if(selectedTab!=null && selectedTab.equals(s))
			return true;		
		else
			return false;
	}


	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
   
	public void setPayMethodTab(ActionEvent e) {
		this.selectedTab = "tab3";
	}
   
	
	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}
	

	public boolean isShowPrice() {
		return showPrice;
	}

	public void setShowPrice(boolean showPrice) {
		this.showPrice = showPrice;
	}

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}

	public List<SelectItem> getSkins() {
		if (skins == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			skins = new LinkedList<SelectItem>();
			for (SkinType e : SkinType.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				skins.add(item);

			}
		}

		return skins;
	}

	public List<SelectItem> getLoginTypes() {
		if (loginTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			loginTypes = new LinkedList<SelectItem>();
			for (LoginType e : LoginType.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				loginTypes.add(item);

			}
		}

		return loginTypes;
	}

	public List<SelectItem> getPriceTypes() {
		if (priceTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			priceTypes = new LinkedList<SelectItem>();
			for (ShowPrice e : ShowPrice.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				priceTypes.add(item);

			}
		}

		return priceTypes;
	}

	public List<SelectItem> getTaxPriceTypes() {
		if (taxPriceTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			taxPriceTypes = new LinkedList<SelectItem>();
			for (TaxType e : TaxType.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				taxPriceTypes.add(item);

			}
		}

		return taxPriceTypes;
	}

	public List<SelectItem> getDiscountTypes() {
		if (discountTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			discountTypes = new LinkedList<SelectItem>();
			for (DiscountFormat e : DiscountFormat.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				discountTypes.add(item);

			}
		}

		return discountTypes;
	}
	
	public void refreshBankTranfers() throws ManagerBeanException {
		bankTransfers = new LinkedList<SelectItem>();
		IManagerBean paymethodBean = BeanManager
				.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(paymethodBean.getFieldName(IConfigAlias.PAY_METHOD_TYPE),PayMethodType.BANK_TRANSFER);
		criteria.addOrder(paymethodBean
				.getFieldName(IConfigAlias.PAY_METHOD_ID));
		List<ITransferObject> lista;
		lista = paymethodBean.getList(criteria);
		for (ITransferObject rec : lista) {
			PayMethod method = (PayMethod) rec;
			SelectItem item = new SelectItem(method, method.getName());
			bankTransfers.add(item);
		}
	}
	
	public void refreshPaypals() throws ManagerBeanException {
		paypals = new LinkedList<SelectItem>();
		IManagerBean paymethodBean = BeanManager
				.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(paymethodBean.getFieldName(IConfigAlias.PAY_METHOD_TYPE),PayMethodType.CREDIT_CARD);
		criteria.addOrder(paymethodBean
				.getFieldName(IConfigAlias.PAY_METHOD_ID));
		List<ITransferObject> lista;
		lista = paymethodBean.getList(criteria);
		for (ITransferObject rec : lista) {
			PayMethod method = (PayMethod) rec;
			SelectItem item = new SelectItem(method, method.getName());
			paypals.add(item);
		}
	}
	
	public void refreshCreditCards() throws ManagerBeanException {
		creditCards = new LinkedList<SelectItem>();
		IManagerBean paymethodBean = BeanManager
				.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(paymethodBean.getFieldName(IConfigAlias.PAY_METHOD_TYPE),PayMethodType.CREDIT_CARD);
		criteria.addOrder(paymethodBean
				.getFieldName(IConfigAlias.PAY_METHOD_ID));
		List<ITransferObject> lista;
		lista = paymethodBean.getList(criteria);
		for (ITransferObject rec : lista) {
			PayMethod method = (PayMethod) rec;
			SelectItem item = new SelectItem(method, method.getName());
			creditCards.add(item);
		}
	}
	
	public void refreshCashOnDeliverys() throws ManagerBeanException {
		cashOnDeliverys = new LinkedList<SelectItem>();
		IManagerBean paymethodBean = BeanManager
				.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(paymethodBean.getFieldName(IConfigAlias.PAY_METHOD_TYPE),PayMethodType.CASH_BASIS);
		criteria.addOrder(paymethodBean
				.getFieldName(IConfigAlias.PAY_METHOD_ID));
		List<ITransferObject> lista;
		lista = paymethodBean.getList(criteria);
		for (ITransferObject rec : lista) {
			PayMethod method = (PayMethod) rec;
			SelectItem item = new SelectItem(method, method.getName());
			cashOnDeliverys.add(item);
		}
	}
	
	public void refreshBankDrafts() throws ManagerBeanException {
		bankDrafts = new LinkedList<SelectItem>();
		IManagerBean paymethodBean = BeanManager
				.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(paymethodBean.getFieldName(IConfigAlias.PAY_METHOD_TYPE),PayMethodType.NEGOTIABLE_DOCUMENT);
		criteria.addOrder(paymethodBean
				.getFieldName(IConfigAlias.PAY_METHOD_ID));
		List<ITransferObject> lista;
		lista = paymethodBean.getList(criteria);
		for (ITransferObject rec : lista) {
			PayMethod method = (PayMethod) rec;
			SelectItem item = new SelectItem(method, method.getName());
			bankDrafts.add(item);
		}
	}
	
	

	public void refreshTariffs() throws ManagerBeanException {
		tariffs = new LinkedList<SelectItem>();
		IManagerBean tariffBean = BeanManager
				.getManagerBean(Tariff.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(tariffBean
				.getFieldName(IConfigAlias.TARIFF_ID));
		List<ITransferObject> lista;
		lista = tariffBean.getList(criteria);
		for (ITransferObject rec : lista) {
			Tariff tar = (Tariff) rec;
			SelectItem item = new SelectItem(tar, tar.getName());
			tariffs.add(item);
		}
	}

	
	
	public List<SelectItem> getBankTransfers() throws ManagerBeanException {
		bankTransfers=null;
		if (bankTransfers == null) {
			refreshBankTranfers();
		}
		return bankTransfers;
	}
	
	public void setBankTransfers(List<SelectItem> bankTransfers) {
		this.bankTransfers = bankTransfers;
	}

	public List<SelectItem> getCashOnDeliverys() throws ManagerBeanException {
		cashOnDeliverys=null;
		if (cashOnDeliverys == null) {
			refreshCashOnDeliverys();
		}
		return cashOnDeliverys;
	}

	public void setCashOnDeliverys(List<SelectItem> cashOnDeliverys) {
		this.cashOnDeliverys = cashOnDeliverys;
	}

	public List<SelectItem> getBankDrafts() throws ManagerBeanException {
		bankDrafts=null;
		if (bankDrafts == null) {
			refreshBankDrafts();
		}
		return bankDrafts;
	}

	public void setBankDrafts(List<SelectItem> bankDrafts) {
		this.bankDrafts = bankDrafts;
	}

	public List<SelectItem> getCreditCards() throws ManagerBeanException {
		creditCards=null;
		if (creditCards == null) {
			refreshCreditCards();
		}
		return creditCards;
	}

	public void setCreditCards(List<SelectItem> creditCards) {
		this.creditCards = creditCards;
	}

	public List<SelectItem> getPaypals() throws ManagerBeanException {
		paypals=null;
		if (paypals == null) {
			refreshPaypals();
		}
		return paypals;
	}

	public void setPaypals(List<SelectItem> paypals) {
		this.paypals = paypals;
	}

	
	public List<SelectItem> getTariffs() throws ManagerBeanException {
		tariffs=null;
		if (tariffs == null) {
			refreshTariffs();
		}
		return tariffs;
	}

	public void setTariffs(List<SelectItem> tariffs) {
		this.tariffs = tariffs;
	}

	public AonFile getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(AonFile aonFile1) {
		this.headerImage = aonFile1;
	}

	public AonFile getLeftBanner() {
		return leftBanner;
	}

	public void setLeftBanner(AonFile leftBanner) {
		this.leftBanner = leftBanner;
	}

	public AonFile getRightBanner() {
		return rightBanner;
	}

	public void setRightBanner(AonFile rightBanner) {
		this.rightBanner = rightBanner;
	}

	public AonFile getWelcomeBanner() {
		return welcomeBanner;
	}

	public void setWelcomeBanner(AonFile welcomeBanner) {
		this.welcomeBanner = welcomeBanner;
	}

	public void headerUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName(item.getFileName());
			setHeaderImage(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void paintHeader(OutputStream out, Object data) throws IOException {
		if (getHeaderImage()!=null) {
			out.write(getHeaderImage().getData());
		}
	}
	
	public void deleteHeader(ActionEvent e)   {
		setHeaderImage(null);
	}
		
	
	public void leftBannerUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName(item.getFileName());
			setLeftBanner(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void paintLeftBanner(OutputStream out, Object data) throws IOException {
		if (getLeftBanner() != null) {
			out.write(getLeftBanner().getData());
		}
	}
	
	public void deleteLeftBanner(ActionEvent e)   {
		setLeftBanner(null);
	}
	
	public void rightBannerUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName(item.getFileName());
			setRightBanner(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void paintRightBanner(OutputStream out, Object data) throws IOException {
		if (getRightBanner() != null) {
			out.write(getRightBanner().getData());
		}
	}
	
	public void deleteRightBanner(ActionEvent e)   {
		setRightBanner(null);
	}
	
	public void welcomeBannerUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			File file = item.getFile();
			if (file != null) {
				FileInputStream in = new FileInputStream(file);
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName(item.getFileName());
			setWelcomeBanner(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void paintWelcomeBanner(OutputStream out, Object data) throws IOException {
		if (getWelcomeBanner() != null) {
			out.write(getWelcomeBanner().getData());
		}
	}
	
	public void deleteWelcomeBanner(ActionEvent e)   {
		setWelcomeBanner(null);
	}
		
	public void getLoginState(ActionEvent e){	
		
		setLogin(((Ecconfig)this.getTo()).getShowLogin()==LoginType.NEVER);		
		if(this.login==true){
			((Ecconfig)this.getTo()).setCommerce(false);
		}
	}
	
	public void getShowPriceState(ActionEvent e){	
		
		setShowPrice(((Ecconfig)this.getTo()).getPrice()==ShowPrice.NO);		
		if(this.showPrice==true){
			((Ecconfig)this.getTo()).setDiscount(DiscountFormat.NO);
			((Ecconfig)this.getTo()).setTaxInPrice(TaxType.NO);
		}
	}
	
	
	@Override
	public void onSelectFirst(ActionEvent event) {
		// TODO Auto-generated method stub
		super.onSelectFirst(event);
		setShowPrice(((Ecconfig)this.getTo()).getPrice()==ShowPrice.NO);
		setLogin(((Ecconfig)this.getTo()).getShowLogin()==LoginType.NEVER);
	}
	
	
	public String getDomain(){
		AuthPrincipal user = UserUtils.getInstance().getPrincipal();
		return user.getDomain();
	}
	
	public String getUrl(){
		StringBuffer url = new StringBuffer( "http://aon." );
		url.append( getDomain() );
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		if ( request.getRemotePort() != 80 ) {
			url.append( ":" ).append( String.valueOf(request.getLocalPort()) );
		}
		url.append( "/aon-ecommerce" );
		url.append( "?aonEbackoffice=true" );
		return url.toString();	
	}
	
	public boolean getEcommerceParam() throws ManagerBeanException {
		IManagerBean param= BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(param.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME),"EC_SALES_ALLOWED");
		return Boolean.parseBoolean(((ApplicationParameter)param.getList(criteria).get(0)).getValue());
		
	}
}
