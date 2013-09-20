package com.esferalia.aon.ui.payroll.controller.contract;

import static com.code.aon.ui.common.ICommonMessages.NOT_MAIL_ACCOUNTS;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.controller.AttachmentController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.SecurityInfo;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractAttachmentType;
import com.esferalia.aon.ui.payroll.utils.PayrollEmailUtil;
import com.esferalia.aon.ui.payroll.utils.PdfUtils;

public class ContractAttachController extends AttachmentController {
	
	private ArrayList<IAttachment> checks = new ArrayList<IAttachment>();
	
	private ContractAttachmentType type;
		
	private boolean show;
	
	public ContractAttachController() {
		this.show = true;
	}

	public ContractAttachmentType getType() {
		return type;
	}
	
	public void setType(ContractAttachmentType type) {
		this.type = type;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}
	
	public int getCheckedCount(){
		return checks.size();
	}
	
	public void rowSelected(ActionEvent event) throws ManagerBeanException{
		IAttachment to = (IAttachment) getModel().getRowData();
		if (checks.contains(to)) {
			checks.remove(to);
		} else {
			checks.add(to);
		}
	}
	
	public void rowSelected(ValueChangeEvent event) throws ManagerBeanException{
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	public boolean getRowChecked() throws ManagerBeanException{
		IAttachment to = (IAttachment) getModel().getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) throws ManagerBeanException{
		if (rowChecked) {
			IAttachment to = (IAttachment) getModel().getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			IAttachment to = (IAttachment) getModel().getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public void checkAll(ActionEvent event) throws ManagerBeanException{
		for (ITransferObject ito : this.getWrappedList()) {
			ContractAttachment o = (ContractAttachment) ito;
			if (!checks.contains(o) && o.isPdfType()) {
				checks.add( o );
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearChecks();
	}
	public void clearChecks() {
		checks = new ArrayList<IAttachment>();
	}
	
	public void onDownloadSelected(ActionEvent event){
		byte[] data = PdfUtils.mergePdf(checks);
		InputStream in = new ByteArrayInputStream(data);
		long size = ArrayUtils.getLength(data);
		DownloadUtil.downloadAttachment("Contract-documents", MimeType.MIME_PDF, in, size);
	}
	
	public void onSendSelectedByEmail(ActionEvent event) throws ManagerBeanException, IOException {
		sendSelectedByEmail(null, true);
	}
	private void sendSelectedByEmail(SecurityInfo securyInfo, boolean facturae) throws ManagerBeanException, IOException {
		PayrollEmailUtil emailController = new PayrollEmailUtil();
		
		MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
		if (mailConfig.getMailAccountCount() > 0) {
			MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
			messageController.initNewMessage();
			emailController.initMessageController(messageController, (Contract)this.getMasterController().getTo(), checks, facturae);
			messageController.setShowNewMessageWindow(true);
			messageController.setSecurityInfo(securyInfo);
		} else {
			AonUtil.addErrorMessageFromBundle(NOT_MAIL_ACCOUNTS);
		}
	}
	
}
