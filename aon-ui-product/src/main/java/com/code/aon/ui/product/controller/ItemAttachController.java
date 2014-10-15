package com.code.aon.ui.product.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.FilenameUtils;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.IAttachmentController;
import com.code.aon.product.ItemAttachment;
import com.code.aon.product.enumeration.AttachmentType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class ItemAttachController extends LinesController implements IAttachmentController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ItemAttachController.class.getName());

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
		if ( this.aonFile != null ) {
			this.aonFile.clean();
		}
		this.aonFile = aonFile;
	}

	public void fileUploaded(UploadEvent event) {
		AonFile f = AttachmentUtil.fileUploaded(event);
		getAttachment().setDescription(FilenameUtils.getName(f.getFileName()));
		setAonFile(f);
	}

	public void downloadAttachment(ActionEvent event) throws NumberFormatException, ManagerBeanException {
		FacesContext context = FacesContext.getCurrentInstance();
		String id = context.getExternalContext().getRequestParameterMap().get("index");
		ItemAttachment attachment = (ItemAttachment) getManagerBean().get(Integer.valueOf(id));
		DownloadUtil.downloadAttachment( attachment );
	}


	/**
	 * Recupera los tipos de adjuntos
	 * 
	 * @return
	 */
	public List<SelectItem> getTypesList() {
		Locale locale = AonUtil.getCurrentLocale();
		List<SelectItem> typesList = new LinkedList<SelectItem>();
		boolean hasThumbnail = hasThumbnail();
		for (AttachmentType type : AttachmentType.values()) {
			if ( (type != AttachmentType.THUMBNAIL) || 
				( (type == AttachmentType.THUMBNAIL) && !hasThumbnail ) ) {
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				typesList.add(item);
			}
		}
		return typesList;
	}
	
	public boolean hasThumbnail() {
		try {
			IManagerBean attachmentBean = BeanManager.getManagerBean(ItemAttachment.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(getCriteria().getExpression());
			criteria.addEqualExpression("ItemAttachment.type",AttachmentType.THUMBNAIL);
			return attachmentBean.getCount(criteria) > 0;
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}
	
}