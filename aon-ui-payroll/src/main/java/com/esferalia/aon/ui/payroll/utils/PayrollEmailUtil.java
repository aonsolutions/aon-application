package com.esferalia.aon.ui.payroll.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.payroll.Contract;

public class PayrollEmailUtil extends CompanyEmailUtil {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void initMessageController( MessageController messageController, Contract contract, List<IAttachment> attachList, boolean facturae ) throws ManagerBeanException, IOException{
		String[] emails = getEmails(contract.getWorkPlace().getEnterprise().getRegistry());
		initMessageController(messageController, emails, "");
		messageController.setSubject( getEmailSubject(contract) );
		for(IAttachment attach: attachList){
			messageController.addAttachment( getAttachFile(attach) );
		}
	}	
	
	public String getEmailSubject( Contract contract ) {
		return "Nuevo contrato laboral ("+contract.getPerson().getFullName()+")";
	}
	
	public AonFile getAttachFile( IAttachment attach ) throws IOException {
		String fileName = attach.getDescription();
		File file = File.createTempFile( fileName, ".pdf" );
		FileUtils.writeByteArrayToFile(file, attach.getData());
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( fileName + ".pdf" );
		aonFile.setMimeType(MimeType.MIME_PDF);
		return aonFile;
	}
	
}
