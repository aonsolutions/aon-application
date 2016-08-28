package com.code.aon.ui.finance.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.config.User;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.ILongProcess;
import com.code.aon.ui.finance.file.AEB19Writer;
import com.code.aon.ui.finance.file.AEB32Writer;
import com.code.aon.ui.finance.file.AEB34Writer;
import com.code.aon.ui.finance.file.AEB58Writer;
import com.code.aon.ui.finance.file.SEPA19_14CoreXmlWriter;
import com.code.aon.ui.finance.file.SEPA34_14XmlWriter;
import com.code.aon.ui.finance.file.SEPA58XmlWriter;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Alarm;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.office.Notice;
import com.esferalia.aon.occam.api.model.type.AlarmSource;
import com.esferalia.aon.occam.api.model.type.AlarmStatus;
import com.esferalia.aon.occam.api.model.type.NoticeStatus;
import com.esferalia.aon.occam.api.model.type.NoticeType;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class FBatchCreateDiskProcess implements ILongProcess {

	private static final Logger LOGGER = LoggerFactory.getLogger(FBatchCreateDiskProcess.class.getName());
	
	private FinanceBatch fbatch;
	
	private Date bankDate;
	
	private User user;
	
	private String domainUrl;
	
	private Company company;
	
	private String errorMessage;
	
	private boolean interrupt = false;
	
	public FBatchCreateDiskProcess(FinanceBatch fbatch, Company company, Date bankDate, User user, String domainUrl) {
		this.user = user;
		this.domainUrl = domainUrl;
		this.company = company;
		this.fbatch = fbatch;
		this.bankDate = bankDate;
		this.errorMessage = AonUtil.getMessage(ICommonMessages.FINANCE_BATCH_DISK_ERROR);
	}
	
	@SuppressWarnings("unchecked")
	private List<FinanceBatchDetail> obtainDetailsCollection(FinanceBatch fbatch) {
		String select = "select fbatchDetail " +
    					"from FinanceBatchDetail as fbatchDetail " +
    					"where fbatchDetail.financeBatch.id = " + fbatch.getId() + " " +
    					"order by substring(fbatchDetail.finance.bankAccount, 1, 8), fbatchDetail.finance.registry.id";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
    	Query query = session.createQuery(select);
    	return query.list(); 
	}	
	
	@Override
	public void execute() {
		startProcess();
    	try {
	    	List<FinanceBatchDetail> fbatchDetailCollection = obtainDetailsCollection(fbatch);
	    	FileOutput aebOutput = null;
	    	switch ( fbatch.getFinanceBatchType() ) {
		    	case AEB_19:
		    	case AEB_19_D:
					AEB19Writer aeb19Writer = new AEB19Writer();
					aebOutput = aeb19Writer.createAEB19(company, fbatch, fbatchDetailCollection);
					saveRegistryAttach(aebOutput, "AEB_19_D_"+fbatch.getDescription());
					break;
		    	case AEB_32:
					AEB32Writer aeb32Writer = new AEB32Writer();
					aebOutput = aeb32Writer.createAEB32(company, fbatch, fbatchDetailCollection);
					saveRegistryAttach(aebOutput, "AEB_32_"+fbatch.getDescription());
		    		break;
		    	case AEB_34:
		    	case AEB_34_N:
					AEB34Writer aeb34Writer = new AEB34Writer();
					aebOutput = aeb34Writer.createAEB34(company, fbatch, fbatchDetailCollection);
					saveRegistryAttach(aebOutput, "AEB_34_"+fbatch.getDescription());
					break;
		    	case AEB_58:
		    	case AEB_58_D:
					AEB58Writer aeb58Writer = new AEB58Writer();
					aebOutput = aeb58Writer.createAEB58(company, fbatch, fbatchDetailCollection);
					saveRegistryAttach(aebOutput, "AEB_58_D_"+fbatch.getDescription());
					break;
		    	case SEPA_19_14_CORE_XML:
		    	case SEPA_19_14_COR1_XML:
					SEPA19_14CoreXmlWriter sepa19Writer = new SEPA19_14CoreXmlWriter();
					aebOutput = sepa19Writer.createXml(company, bankDate, fbatch, fbatchDetailCollection);
					saveRegistryAttach(aebOutput, "SEPA_19_14_COR1_XML_"+fbatch.getDescription(), MimeType.MIME_XML);
					break;
		    	case SEPA_34_14_XML:
		    	case SEPA_34_14_N_XML:
					SEPA34_14XmlWriter sepa34Writer = new SEPA34_14XmlWriter();
					aebOutput = sepa34Writer.createXml(company, fbatch, fbatchDetailCollection);
					saveRegistryAttach(aebOutput, "SEPA_34_14_XML_"+fbatch.getDescription(), MimeType.MIME_XML);
		    		break;
		    	case SEPA_58_ANTICIPO_XML:
		    	case SEPA_58_COBRO_XML:
		    		SEPA58XmlWriter sepa58Writer = new SEPA58XmlWriter();
					aebOutput = sepa58Writer.createXml(company,  bankDate, fbatch, fbatchDetailCollection);
					saveRegistryAttach(aebOutput, "SEPA_58_COBRO_XML_"+fbatch.getDescription(), MimeType.MIME_XML);
		    		break;
		    	case NONE:
		    		LOGGER.debug( "None finance batch type");
		    		break;
	    	}
	    	
	        if (aebOutput != null) {
	        	if (aebOutput.getErrors().size() > 0) {
	    			LOGGER.error(errorMessage);
	            }
	        }
		} catch (Throwable e) {
			LOGGER.error(errorMessage);
		}		
	}
	
	private void startProcess() {
		interrupt = false;
		try {
			fbatch.setRattach(0);
			BeanManager.getManagerBean(FinanceBatch.class).update(fbatch);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	public void interrupt() {
		interrupt = true;
	}
	
	private void saveRegistryAttach(FileOutput aebOutput, String name) {
		saveRegistryAttach(aebOutput, name, MimeType.MIME_TXT);
	}
	
	private void saveRegistryAttach(FileOutput aebOutput, String name,
			MimeType mimeType) {
		if(!interrupt){
			Attach attach = new Attach();
			attach.setDomain(new Domain().setId(company.getDomain()));
			attach.setData(aebOutput.getContent());
			attach.setDescription(name.concat(".").concat(mimeType.getExtension()));
			attach.setAttachType(AttachType.REGISTRY);
			attach.setType((short) RegistryAttachmentType.SYSTEM_MESSAGE.ordinal());
			attach.setAttachModule(company.getId());
			attach.setDate(new Date());
			attach.setCreationDate(new Date());
			attach.setCreationUser(null);
			attach.setModificationDate(new Date());
			attach.setModificationUser(null);
			attach.setMimeType(com.esferalia.aon.occam.api.model.type.MimeType
					.getByExtension(mimeType.getExtension()));
			attach.setConfidential(false);
			Integer attachId = AON.insert(AonUtil.getDomainName(), company.getDomain(), user.getLogin(), attach);
			LOGGER.info("## Nuevo Mensaje de Sistema: " + attach.getDescription());
			
			RegistryAttachment ra;
			String downloadURL = null;
			try {
				ra = (RegistryAttachment) BeanManager.getManagerBean(RegistryAttachment.class).get(attachId);
				downloadURL = getDownloadURL(ra);
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
			
			try {
				fbatch.setRattach(attachId);
				fbatch.setFinanceBatchStatus(FinanceBatchStatus.DONE);
				BeanManager.getManagerBean(FinanceBatch.class).update(fbatch);
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage());
			}
			
			Integer noticeId = createNotice(attachId, name, mimeType, downloadURL);
			if(noticeId!=null){
				createAlarm(noticeId, name, mimeType, downloadURL);
			}
		}
	}
	
	private Integer createNotice(Integer attachId, String name, MimeType mimeType, String downloadURL) {
		
		String fileName = name.concat(".").concat(mimeType.getExtension());
		String title = "Fichero generado: " + fileName;
		String body = "El fichero ".concat(fileName)
				.concat(" se ha generado y almacenado correctamente en Documental.");
		if(StringUtils.isNotBlank(downloadURL)){
			body = body.concat(" Se puede descargar accediendo a la siguiente url: ")
					.concat(downloadURL);
		}
		com.esferalia.aon.occam.api.model.security.User user = new com.esferalia.aon.occam.api.model.security.User();
		user.setId(this.user.getId());
		user.setLogin(this.user.getLogin());
		
		Notice notice = new Notice();
		notice.setDomain(company.getDomain());
		notice.setSender(user);
		notice.setRecipient(user);
		notice.setNotice(null);
		notice.setSource(null);	
		notice.setTitle(title);
		notice.setBody(body);
		notice.setType(NoticeType.COMMUNICATION.name());
		notice.setStatus(NoticeStatus.OPEN.name());
		notice.setPriority(Priority.HIGH.name());
		notice.setCompany(company.getFullName());
		notice.setStartDate(new Date());
		notice.setEndDate(new Date());
		
		try {
			return AON.insertNotice(company.getDomain(), AonUtil.getDomainName(), user.getLogin(), notice);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}
	
	private void createAlarm(Integer noticeId, String name, MimeType mimeType, String downloadURL) {
		
		String fileName = name.concat(".").concat(mimeType.getExtension());
		String description = "El fichero ".concat(fileName)
				.concat(" se ha generado y almacenado correctamente en Documental.");
		if(StringUtils.isNotBlank(downloadURL)){
			description = description.concat(" Se puede descargar accediendo a la siguiente url: ")
					.concat(downloadURL);
		}
		
		Alarm alarm = new Alarm();
		alarm.setDomain(company.getDomain());
		alarm.setDescription(description);
		alarm.setAlarmDate(new Date());
		alarm.setStatus(AlarmStatus.PENDING.value());
		alarm.setSource(AlarmSource.NOTICE.value());
		alarm.setSourceId(noticeId);
		alarm.setUserId(user.getId());
		alarm.setPriority(Priority.HIGH.value());
		
		try {
			AON.insertAlarm(company.getDomain(), AonUtil.getDomainName(), user.getLogin(), alarm);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	private String getDownloadURL(RegistryAttachment ra) throws ManagerBeanException, IOException, SQLException, KeyStoreException, GeneralSecurityException {
		String url = null;
		if ( (ra.getDriveId() != null) && (ra.getMD5() == null) ) {
			String domainName = AonUtil.getDomainName();
			Domain domain = new Domain().setName(domainName).setId(ra.getDomain());
			com.esferalia.aon.occam.api.model.security.User user = new com.esferalia.aon.occam.api.model.security.User();
			user.setLogin(user.getLogin());
			DomainGserviceaccount d = DBConsults.getServiceAccount(domain, user);
			Drive drive = DriveUtils.serviceInitialize(d);
			File f = DriveUtils.getFile(drive, domain, user, ra.getDriveId(), ra.getId());
			if ( "OLDRIVE".equals(f.getDescription()) ) {
				drive = DriveUtils.serviceInitializeOld(d);	
			}
			ra.setMD5(f.getMd5Checksum());
		}
		url = domainUrl + ra.getDownloadURL();
		return url;
	}

}
