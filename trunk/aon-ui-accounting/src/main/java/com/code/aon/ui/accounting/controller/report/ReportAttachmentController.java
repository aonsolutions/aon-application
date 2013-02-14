package com.code.aon.ui.accounting.controller.report;

import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.io.FilenameUtils;
import org.richfaces.event.UploadEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.company.Company;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.IAttachmentController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.DownloadUtil;

public class ReportAttachmentController extends BasicController implements IAttachmentController {

	private AonFile aonFile;
	private long maximumSize;
	private Company company;
	
	public ReportAttachmentController() {
		this.maximumSize = -1;
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
	public AonFile getAonFile() {
		return this.aonFile;
	}
	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	public Company getCompany() {
		try {
			if (company == null) {
				IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
				Iterator<ITransferObject> iter = companyBean.getList(null, 0, 1).iterator();
				if (iter.hasNext()) {
					company = (Company) iter.next();
				}
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("Error al obener los datos de empersa.");
		}
		return company;
	}

	public void fileUploaded(UploadEvent event) {
		AttachmentUtil.fileUploaded(event, this);
		String description = FilenameUtils.getName(getAonFile().getFileName());
		getAttachment().setDescription(description);
	}
	
    public void downloadAttachment( ActionEvent event ) throws NumberFormatException, ManagerBeanException {
        FacesContext context = FacesContext.getCurrentInstance();
        String id = context.getExternalContext().getRequestParameterMap().get("index");
        IAttachment attachment = (IAttachment) getManagerBean().get(Integer.valueOf(id));
        DownloadUtil.downloadAttachment( attachment );    	
    }
	
}