package com.code.aon.ui.ebackoffice.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.ebackoffice.enumeration.LoginType;
import com.code.aon.ebackoffice.enumeration.OriginalPrice;
import com.code.aon.ebackoffice.enumeration.PriceType;
import com.code.aon.ebackoffice.enumeration.SkinType;
import com.code.aon.ebackoffice.enumeration.TaxType;
import com.code.aon.ebackoffice.enumeration.WishList;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.BasicController;

public class EcconfigController extends BasicController {

	private static final Logger LOGGER = Logger
			.getLogger(EcconfigController.class.getName());
	private List<SelectItem> skins;
	private List<SelectItem> loginTypes;
	private List<SelectItem> priceTypes;
	private List<SelectItem> taxPriceTypes;
	private List<SelectItem> originalPriceTypes;
	private List<SelectItem> wishListTypes;
	private List<SelectItem> paymethods;
	/** The uploaded file. */
	private AonFile aonFile1;
	private AonFile aonFile2;

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
			for (PriceType e : PriceType.values()) {
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

	public List<SelectItem> getOriginalPriceTypes() {
		if (originalPriceTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			originalPriceTypes = new LinkedList<SelectItem>();
			for (OriginalPrice e : OriginalPrice.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				originalPriceTypes.add(item);

			}
		}

		return originalPriceTypes;
	}

	public List<SelectItem> getWishListTypes() {
		if (wishListTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			wishListTypes = new LinkedList<SelectItem>();
			for (WishList e : WishList.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				wishListTypes.add(item);

			}
		}

		return wishListTypes;
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

	public List<SelectItem> getPaymethods() throws ManagerBeanException {
		if (paymethods == null) {
			refreshPaymethods();
		}
		return paymethods;
	}

	public void setPaymethods(List<SelectItem> paymethods) {
		this.paymethods = paymethods;
	}

	// @Override
	// public void onAccept(ActionEvent event) {
	//	
	//		
	// super.onAccept(event);
	// this.setModel(null);
	// }

	public AonFile getAonFile1() {
		return aonFile1;
	}

	public void setAonFile1(AonFile aonFile1) {
		this.aonFile1 = aonFile1;
	}

	public AonFile getAonFile2() {
		return aonFile2;
	}

	public void setAonFile2(AonFile aonFile2) {
		this.aonFile2 = aonFile2;
	}

	/*
	 * private void writeAttachment(AonFile file,HttpServletResponse response) {
	 * try { String filename = aonFile1.getFileName();
	 * 
	 * response.setHeader("Content-disposition", "attachment;filename=\"" +
	 * filename + "\""); byte[] data = aonFile1.getData();
	 * response.setHeader("Content-Length", String.valueOf(data.length));
	 * ServletOutputStream sos = response.getOutputStream(); sos.write(data);
	 * sos.close(); response.flushBuffer(); } catch (IOException e) {
	 * LOGGER.log(Level.SEVERE, e.getMessage(), e); } }
	 * 
	 * public void downloadImage(ActionEvent event) throws
	 * NumberFormatException, ManagerBeanException { FacesContext context =
	 * FacesContext.getCurrentInstance(); String id =
	 * context.getExternalContext().getRequestParameterMap().get("index");
	 * HttpServletResponse response = (HttpServletResponse)
	 * context.getExternalContext().getResponse(); AonFile file=
	 * (AonFile)getManagerBean().get(Integer.valueOf(id)); writeAttachment(file,
	 * response); context.responseComplete(); }
	 */

	public void footerUploaded(UploadEvent event) {
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
			setAonFile2(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
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
			setAonFile1(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void paintHeader(OutputStream out, Object data) throws IOException {
		// if (getAonFile() != null && (getAonFile().getSize() > 0) ) {
		// out.write(getAonFile().getData());
		// }
		// out.write(((ItemAttachment)getTo()).getData());

		if (((Ecconfig) getTo()).getHeaderImg() != null) {
			this.onSelectFirst(null);
			out.write(((Ecconfig) getTo()).getHeaderImg());
		} else
			out.write(null);

	}

	public void paintFooter(OutputStream out, Object data) throws IOException {
		// if (getAonFile() != null && (getAonFile().getSize() > 0) ) {
		// out.write(getAonFile().getData());
		// }
		// out.write(((ItemAttachment)getTo()).getData());
		if (((Ecconfig) getTo()).getFooterImg() != null) {
			this.onSelectFirst(null);
			out.write(((Ecconfig) getTo()).getFooterImg());
		} else
			out.write(null);

	}

}
