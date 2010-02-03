package com.code.aon.ui.company.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class CompanyImagesController extends LinesController implements ICompanyConstants {

	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyImagesController.class.getName());
	
	private static final RegistryAttachmentType[] DEFAULT_DISPLAYED_TYPES = new RegistryAttachmentType[] {RegistryAttachmentType.ADDITIONAL_IMAGE};

	private static final int DEFAULT_MAXIMUM_SIZE = 256 * 1024;
	
	private static final int DEFAULT_MAXIMUM_NUMBER = 16;
	
	private AonFile aonFile;
	
	private MimeType mimeType;
	
	private int maximumNumber;
	
	private long maximumSize;
	
	private RegistryAttachmentType[] displayedTypes;
	
	private RegistryAttachmentType attachmentType;
	
	private List<SelectItem> registryAttachmentTypes;
	
	public CompanyImagesController() {
		setMaximumSize(DEFAULT_MAXIMUM_SIZE);
		setDisplayedTypes(DEFAULT_DISPLAYED_TYPES);
		setAttachmentType(RegistryAttachmentType.ADDITIONAL_IMAGE);
		setMaximumNumber(DEFAULT_MAXIMUM_NUMBER);
	}

	public long getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(long maximumSize) {
		this.maximumSize = maximumSize;
	}
	
	public int getMaximumNumber() {
		return maximumNumber;
	}

	public void setMaximumNumber(int maximumNumber) {
		this.maximumNumber = maximumNumber;
	}

	public AonFile getAonFile() {
		return aonFile;
	}

	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}

	public RegistryAttachmentType[] getDisplayedTypes() {
		return displayedTypes;
	}

	public void setDisplayedTypes(RegistryAttachmentType[] displayedTypes) {
		this.displayedTypes = displayedTypes;
		updateRegistryAttachmentTypes(this.displayedTypes);
	}

	public RegistryAttachmentType getAttachmentType() {
		return attachmentType;
	}

	public void setAttachmentType(RegistryAttachmentType attachmentType) {
		this.attachmentType = attachmentType;
	}

	public List<SelectItem> getRegistryAttachmentTypes() {
		return registryAttachmentTypes;
	}

    private void updateRegistryAttachmentTypes( RegistryAttachmentType[] types ) {
    	Locale locale = AonUtil.getCurrentLocale();
    	registryAttachmentTypes = new LinkedList<SelectItem>();
        for( RegistryAttachmentType status : types ) {
            String name = status.getName(locale); 
            SelectItem item = new SelectItem(status, name);
            registryAttachmentTypes.add( item );
        }
    }	

	public void fileUploaded(UploadEvent event) {
		try {
			UploadItem item = event.getUploadItem();
			AonFile f = new AonFile();
			if ( item.isTempFile() ) {
				FileInputStream in = new FileInputStream(item.getFile());
				byte[] data = IOUtils.toByteArray(in);
				f.setData(data);
			}
			f.setFileName(item.getFileName());
			setAonFile(f);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage());
		}
	}    
	
	public void paint(OutputStream out, Object data) throws IOException {
		if (getAonFile() != null && (getAonFile().getSize() > 0) ) {
			out.write(getAonFile().getData());
		}
	}    
	
	public static void update(RegistryAttachment attachment, AonFile aonFile ) {
		attachment.setData(aonFile.getData());
		MimeType mimeType = CompanyImagesController.getMimeType(aonFile.getFileName(), aonFile.getData());
		attachment.setMimeType(mimeType);		
	}
	
	public static MimeType getMimeType(String resource, byte[] data) {
		MimeType result = null;
		if (! StringUtils.isEmpty(resource) ) {
			String extension = FilenameUtils.getExtension(resource);
			if (! StringUtils.isEmpty(extension) ) {
				result = MimeType.getByExtension(extension);
			}
		}
		if (result == null) {
			try {
				MagicMatch match = Magic.getMagicMatch(data, true);
				if (match != null) {
					result = MimeType.get(match.getMimeType());
				}
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th );
			}
		}
		return result;
	}
	
	public void imageNameCheck(FacesContext context, UIComponent component, Object value) throws ManagerBeanException {
		String imageName = value.toString();
		IManagerBean bean = getManagerBean();
		Criteria criteria = new Criteria();
		RegistryAttachment ra = (RegistryAttachment) getTo();
		if ( ra.getId() != null ) {
			Expression exp = ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_ID), ra.getId());
			criteria.addExpression(exp);
		}
		criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_DESCRIPTION), imageName);
		int count = bean.getCount(criteria);
		if ( count > 0 ) {
			FacesMessage message = new FacesMessage(AonUtil.getMessage(BUNDLE_NAME, COMPANY_IMAGE_DUPLICATED_NAME));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException( message );
		}
		for( int i = 0; i < imageName.length(); i++ ) {
			char c = imageName.charAt(i);
			if (! (Character.isLetter(c) || Character.isDigit(c) || (c == ' ') ) ) {
				String text = AonUtil.getMessage(BUNDLE_NAME, COMPANY_IMAGE_INVALID_CHARACTER, c);
				FacesMessage message = new FacesMessage(text);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);				
				throw new ValidatorException( message );										
			}
		}
	}	
}