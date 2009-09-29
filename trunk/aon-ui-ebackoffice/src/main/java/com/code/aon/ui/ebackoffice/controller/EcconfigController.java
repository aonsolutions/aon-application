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
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Tariff;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ebackoffice.enumeration.DiscountFormat;
import com.code.aon.ebackoffice.enumeration.LoginType;
import com.code.aon.ebackoffice.enumeration.ShowPrice;
import com.code.aon.ebackoffice.enumeration.SkinType;
import com.code.aon.ebackoffice.enumeration.TaxType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.BasicController;

public class EcconfigController extends BasicController {

	private List<SelectItem> skins;
	private List<SelectItem> loginTypes;
	private List<SelectItem> priceTypes;
	private List<SelectItem> taxPriceTypes;
	private List<SelectItem> discountTypes;
	private List<SelectItem> paymethods;
	private List<SelectItem> tariffs;
	private AonFile headerImage;
	private AonFile leftBanner;
	private AonFile rightBanner;
	private AonFile welcomeBanner;
	private String domain;


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
	
	public void refreshPaymethods() throws ManagerBeanException {
		paymethods = new LinkedList<SelectItem>();
		IManagerBean paymethodBean = BeanManager
				.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(paymethodBean
				.getFieldName(IConfigAlias.PAY_METHOD_ID));
		List<ITransferObject> lista;
		lista = paymethodBean.getList(criteria);
		for (ITransferObject rec : lista) {
			PayMethod method = (PayMethod) rec;
			SelectItem item = new SelectItem(method, method.getName());
			paymethods.add(item);
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

	public List<SelectItem> getPaymethods() throws ManagerBeanException {
		paymethods=null;
		if (paymethods == null) {
			refreshPaymethods();
		}
		return paymethods;
	}

	public void setPaymethods(List<SelectItem> paymethods) {
		this.paymethods = paymethods;
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
		if (getHeaderImage().getData() != null) {
			out.write(getHeaderImage().getData());
		}
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
		if (getLeftBanner().getData() != null) {
			out.write(getLeftBanner().getData());
		}
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
		if (getRightBanner().getData() != null) {
			out.write(getRightBanner().getData());
		}
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
		if (getWelcomeBanner().getData() != null) {
			out.write(getWelcomeBanner().getData());
		}
	}
	
	public void checkValue(ValueChangeEvent event){
		
		System.out.println("dcfdf");
		
	}
	
	public String getDomain(){
//		AuthPrincipal user = UserUtils.getInstance().getPrincipal();
//		String domain = user.getDomain();
//		String domain = UserUtils.getInstance().getPrincipal().getDomain();
//		return "192.168.2.40";
		return "localhost:8080";
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	

}
