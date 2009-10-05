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
import javax.faces.model.SelectItem;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ebackoffice.Eccatalogue;
import com.code.aon.ebackoffice.enumeration.CatalogueType;
import com.code.aon.product.Catalogue;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.BasicController;

public class EccatalogueController extends BasicController {
	private static final Logger LOGGER = Logger
			.getLogger(EccatalogueController.class.getName());

	private AonFile image;
	private AonFile icon;
	private List<SelectItem> catalogues;
	private List<SelectItem> catalogueTypes;
	private List<SelectItem> catalogueIcons;
	

	
	public List<SelectItem> getCatalogueTypes() {
		if (catalogueTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot()
					.getLocale();
			catalogueTypes = new LinkedList<SelectItem>();
			for (CatalogueType e : CatalogueType.values()) {
				String name = e.getName(locale);
				SelectItem item = new SelectItem(e, name);
				catalogueTypes.add(item);
			}
		}
		return catalogueTypes;
	}
	
	

	public List<SelectItem> getCatalogues() throws ManagerBeanException {
		catalogues = null;
		if (catalogues == null) {

			refreshCatalogues();
		}

		return catalogues;
	}

	public void setCatalogues(List<SelectItem> catalogues) {
		this.catalogues = catalogues;
	}

	public void refreshCatalogues() throws ManagerBeanException {
		catalogues = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Catalogue.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(IProductAlias.CATALOGUE_ID));
		List<ITransferObject> lista;
		lista = bean.getList(criteria);
		for (ITransferObject rec : lista) {
			Catalogue cat = (Catalogue) rec;
			SelectItem item = new SelectItem(cat, cat.getName());
			catalogues.add(item);
		}
	}

	public AonFile getImage() {
		return image;
	}

	public void setImage(AonFile image) {
		this.image = image;
	}

	public AonFile getIcon() {
		return icon;
	}

	public void setIcon(AonFile icon) {
		this.icon = icon;
	}

	public void uploadImage(UploadEvent event) {
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
			setImage(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void uploadIcon(UploadEvent event) {
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
			setIcon(f);
			
			
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	public void paintImage(OutputStream out, Object data) throws IOException {
		try {
			Integer id = (Integer) data;
			Eccatalogue e = (Eccatalogue) getManagerBean().get(id);
			if (e != null && e.getCatalogueImg() != null) {
				out.write(e.getCatalogueImg());
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}

	}

	public void paintIcon(OutputStream out, Object data) throws IOException {
		try {
			Integer id = (Integer) data;
			Eccatalogue e = (Eccatalogue) getManagerBean().get(id);
			if (e != null && e.getCatalogueIcon() != null) {
				out.write(e.getCatalogueIcon());	
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
	}
	public void paintIconn(OutputStream out, Object data) throws IOException {
		if (icon != null) {
			out.write(icon.getData());
		}
	}

	public void paintImagee(OutputStream out, Object data) throws IOException {
		if (image != null) {
			out.write(image.getData());
		}

	}
	
	

}
