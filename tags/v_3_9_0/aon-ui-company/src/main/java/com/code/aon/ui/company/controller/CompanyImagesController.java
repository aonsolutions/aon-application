package com.code.aon.ui.company.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class CompanyImagesController extends LinesController {

	private static final Logger LOGGER = Logger.getLogger(CompanyImagesController.class.getName());
	
	private static final RegistryAttachmentType[] DEFAULT_DISPLAYED_TYPES = new RegistryAttachmentType[] {RegistryAttachmentType.ADDITIONAL_IMAGE};

	private static final int DEFAULT_MAXIMUM_SIZE = 256 * 1024;
	
	private static final int DEFAULT_MAXIMUM_NUMBER = 16;
	
	private AonFile aonFile;
	
	private MimeType mimeType;
	
	private int maximumNumber;
	
	private long maximumSize;
	
	private RegistryAttachmentType[] displayedTypes;
	
	private RegistryAttachmentType attachmentType;
	
	private boolean showAttachemntTypes;
	
	private List<SelectItem> registryAttachmentTypes;
	
	public CompanyImagesController() {
		setMaximumSize(DEFAULT_MAXIMUM_SIZE);
		setDisplayedTypes(DEFAULT_DISPLAYED_TYPES);
		setAttachmentType(RegistryAttachmentType.ADDITIONAL_IMAGE);
		setMaximumNumber(DEFAULT_MAXIMUM_NUMBER);
		setShowAttachemntTypes(false);
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

	public boolean isShowAttachemntTypes() {
		return showAttachemntTypes;
	}

	public void setShowAttachemntTypes(boolean showAttachemntTypes) {
		this.showAttachemntTypes = showAttachemntTypes;
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
					result = MimeType.getByExtension(match.getMimeType());
				}
			} catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th );
			}
		}
		return result;
	}
	
}