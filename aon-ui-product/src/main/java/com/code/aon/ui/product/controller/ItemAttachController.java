package com.code.aon.ui.product.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.ItemAttachment;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.AttachmentType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.LinesController;

public class ItemAttachController extends LinesController {

	private static final Logger LOGGER = Logger
			.getLogger(ItemAttachController.class.getName());

	/** The uploaded file. */
	private AonFile aonFile;

	private AttachmentType type;

	private long maximumSize;

	public ItemAttachController() {
		this.maximumSize = -1;
	}

	public AttachmentType getType() {
		return type;
	}

	public void setType(AttachmentType type) {
		this.type = type;
	}

	public long getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(long maximumSize) {
		this.maximumSize = maximumSize;
	}

	public IAttachment getAttachment() {
		return (IAttachment) getTo();
	}

	/**
	 * Gets the uploaded file.
	 * 
	 * @return the file
	 */
	public AonFile getAonFile() {
		return this.aonFile;
	}

	/**
	 * Sets the file.
	 * 
	 * @param file
	 *            the file
	 */
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	public boolean isUploaded() {
		return (this.aonFile != null)
				&& (!ArrayUtils.isEmpty(this.aonFile.getData()));
	}

	public boolean isMaximumSizeExceeded() {
		return (this.maximumSize != -1)
				&& (this.aonFile.getSize() > this.maximumSize);
	}

	public void fileUploaded(UploadEvent event) {
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
			getAttachment().setDescription(
					FilenameUtils.getName(item.getFileName()));
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}

	private void writeAttachment(ItemAttachment attachment,
			HttpServletResponse response) {
		try {
			String filename = attachment.getDescription();
			if (attachment.getMimeType() != null) {
				response.setContentType(attachment.getMimeType().getName());
			}
			response.setHeader("Content-disposition", "attachment;filename=\""
					+ filename + "\"");
			byte[] data = attachment.getData();
			response.setHeader("Content-Length", String.valueOf(data.length));
			ServletOutputStream sos = response.getOutputStream();
			sos.write(data);
			sos.close();
			response.flushBuffer();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public void downloadAttachment(ActionEvent event)
			throws NumberFormatException, ManagerBeanException {
		FacesContext context = FacesContext.getCurrentInstance();
		String id = context.getExternalContext().getRequestParameterMap().get(
				"index");
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		ItemAttachment attachment = (ItemAttachment) getManagerBean().get(
				Integer.valueOf(id));
		writeAttachment(attachment, response);
		context.responseComplete();
	}

	private List<SelectItem> typesList;
	/**
	 * Recupera los tipos de adjuntos
	 * 
	 * @return
	 */
	public List<SelectItem> getTypesList() {
		if(typesList==null){
		}
		refreshTypeList();
		return typesList;
	}
	
	public void setTypesList(List<SelectItem> typesList) {
		this.typesList = typesList;
	}
	
	public void refreshTypeList(){
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		typesList = new LinkedList<SelectItem>();
		boolean dis=false;
		for (AttachmentType p : AttachmentType.values()) {
			String name = p.getName(locale);
			SelectItem item = new SelectItem(p, name,name,dis);
			if((p.compareTo(AttachmentType.THUMBNAIL)==0 && !hasThumbnail())
					|| p.compareTo(AttachmentType.THUMBNAIL)!=0){
				typesList.add(item);
			}
		}
	}
	
	public boolean hasThumbnail() {
		try {
			IManagerBean attachmentBean = BeanManager.getManagerBean(ItemAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(attachmentBean.getFieldName(IProductAlias.ITEM_ATTACHMENT_TYPE),AttachmentType.THUMBNAIL);
			Iterator<ITransferObject> iter = attachmentBean.getList(criteria).iterator();
			return iter.hasNext();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	//*******************************
	//*******************************
	// FALTA CONTROLAR TAMAÑO DE THUMBNAIL, REDIMENSIONAR LA IMAGEN
	//*******************************
	//*******************************
}