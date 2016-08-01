package com.code.aon.ui.finance.util;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.ILongProcess;
import com.code.aon.ui.finance.controller.FBatchController;
import com.code.aon.ui.finance.file.AEB19Writer;
import com.code.aon.ui.finance.file.AEB32Writer;
import com.code.aon.ui.finance.file.AEB34Writer;
import com.code.aon.ui.finance.file.AEB58Writer;
import com.code.aon.ui.finance.file.SEPA19_14CoreXmlWriter;
import com.code.aon.ui.finance.file.SEPA34_14XmlWriter;
import com.code.aon.ui.finance.file.SEPA58XmlWriter;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;

public class FBatchCreateDiskProcess implements ILongProcess {

	private static final Logger LOGGER = LoggerFactory.getLogger(FBatchCreateDiskProcess.class.getName());
	
	private FBatchController controller;
	
	private Company company;
	
	private String errorMessage;
	
	public FBatchCreateDiskProcess(FBatchController controller) {
		this.controller = controller;
		this.company = controller.getCompany();
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
		FinanceBatch fbatch = (FinanceBatch) controller.getTo();

		controller.setAebOutput(null);
    	controller.setMimeType(MimeType.MIME_TXT);
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
		    		controller.setMimeType(MimeType.MIME_XML);
					SEPA19_14CoreXmlWriter sepa19Writer = new SEPA19_14CoreXmlWriter();
					aebOutput = sepa19Writer.createXml(company, controller.getBankDate(), fbatch, fbatchDetailCollection);
					saveRegistryAttach(aebOutput, "SEPA_19_14_COR1_XML_"+fbatch.getDescription(), MimeType.MIME_XML);
					break;
		    	case SEPA_34_14_XML:
		    	case SEPA_34_14_N_XML:
		    		controller.setMimeType(MimeType.MIME_XML);
					SEPA34_14XmlWriter sepa34Writer = new SEPA34_14XmlWriter();
					aebOutput = sepa34Writer.createXml(company, fbatch, fbatchDetailCollection);
					saveRegistryAttach(aebOutput, "SEPA_34_14_XML_"+fbatch.getDescription(), MimeType.MIME_XML);
		    		break;
		    	case SEPA_58_ANTICIPO_XML:
		    	case SEPA_58_COBRO_XML:
		    		controller.setMimeType(MimeType.MIME_XML);
		    		SEPA58XmlWriter sepa58Writer = new SEPA58XmlWriter();
					aebOutput = sepa58Writer.createXml(company,  controller.getBankDate(), fbatch, fbatchDetailCollection);
					saveRegistryAttach(aebOutput, "SEPA_58_COBRO_XML_"+fbatch.getDescription(), MimeType.MIME_XML);
		    		break;
		    	case NONE:
		    		LOGGER.debug( "None finance batch type");
		    		break;
	    	}
	    	controller.setAebOutput(aebOutput);
	
	        if (aebOutput != null) {
	        	if (aebOutput.getErrors().size() > 0) {
	    			LOGGER.error(errorMessage);
	        	} else {
	                fbatch.setFinanceBatchStatus(FinanceBatchStatus.DONE);
	                controller.getManagerBean().update(fbatch);
	            }
	        }
		} catch (Throwable e) {
			LOGGER.error(errorMessage);
		}		
	}
	
	private void saveRegistryAttach(FileOutput aebOutput, String name) {
		saveRegistryAttach(aebOutput, name, MimeType.MIME_TXT);
	}
	
	private void saveRegistryAttach(FileOutput aebOutput, String name,
			MimeType mimeType) {
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
		AON.insert(AonUtil.getDomainName(), company.getDomain(), "", attach);
		LOGGER.info("## Nuevo Mensaje de Sistema: " + attach.getDescription());
	}

}
