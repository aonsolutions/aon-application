package com.code.aon.ui.company.controller;

import java.awt.image.BufferedImage;
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
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.common.util.ImageUtil;
import com.code.aon.common.util.MimeResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class CompanyImagesController extends LinesController implements ICompanyConstants {

	private static final RegistryAttachmentType[] DEFAULT_DISPLAYED_TYPES = new RegistryAttachmentType[] {RegistryAttachmentType.ADDITIONAL_IMAGE};

	private static final int DEFAULT_MAXIMUM_SIZE = 256 * 1024;
	
	private static final int DEFAULT_MAXIMUM_NUMBER = 16;
	
	private AonFile aonFile;
	
	private int maximumNumber;
	
	private long maximumSize;
	
	private RegistryAttachmentType[] displayedTypes;
	
	private RegistryAttachmentType attachmentType;
	
	private List<SelectItem> registryAttachmentTypes;
	
	private BufferedImage image;
	
	private boolean ratio;
	
	private int width;
	
	private int height;
	
	private int maxWidth;
	
	private int maxHeight;	
	
	public CompanyImagesController() {
		setMaximumSize(DEFAULT_MAXIMUM_SIZE);
		setDisplayedTypes(DEFAULT_DISPLAYED_TYPES);
		setAttachmentType(RegistryAttachmentType.ADDITIONAL_IMAGE);
		setMaximumNumber(DEFAULT_MAXIMUM_NUMBER);
		setRatio(true);
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public boolean isRatio() {
		return ratio;
	}

	public void setRatio(boolean ratio) {
		this.ratio = ratio;
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
	
	private void init( byte[] data ) {
		setImage(null);
		if (! ArrayUtils.isEmpty(data) ) {
			BufferedImage bImage = ImageUtil.getBufferedImage( data );
			if ( bImage != null ) {
				setImage(bImage);
				setWidth(bImage.getWidth());
				setMaxWidth(bImage.getWidth());
				setHeight(bImage.getHeight());
				setMaxHeight(bImage.getHeight());
			}			
		}				
	}

	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
		init( aonFile.getData() );
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
			f.setMimeType(getMimeType(f.getFileName(), f.getData()));
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
	
	public void update(RegistryAttachment attachment ) {
		byte[] data = aonFile.getData();
		if ( (width != image.getWidth()) || (height != image.getHeight()) ) {
			BufferedImage newImage = ImageUtil.scale(image, width, height);
			String format = (aonFile.getMimeType() != null) ? aonFile.getMimeType().getExtension() : null;
			data = ImageUtil.getImage(newImage, format);
		}
		attachment.setData(data);
		MimeType mimeType = CompanyImagesController.getMimeType(aonFile.getFileName(), data);
		attachment.setMimeType(mimeType);		
	}
	
	public static MimeType getMimeType(String resource, byte[] data) {
		MimeType mt = MimeResolver.getMimeTypeByExtension(resource);
		if ( mt == null ) {
			mt = MimeResolver.getMimeType(data);
		}
		return mt;
	}
	
	public void imageNameCheck(FacesContext context, UIComponent component, Object value) throws ManagerBeanException {
		String imageName = value.toString();
		IManagerBean bean = getManagerBean();
		Criteria criteria = new Criteria();
		RegistryAttachment ra = (RegistryAttachment) getTo();
		if (ra.getId() != null) {
			criteria.addNotEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_ID), ra.getId());
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
	
	public void onChangeWidth(ActionEvent event) throws ManagerBeanException {
		if (ratio){
			setHeight( ImageUtil.getProportionalHeight(image, width));
		}
	}

	public void onChangeHeight(ActionEvent event) throws ManagerBeanException {
		if (ratio){
			setWidth( ImageUtil.getProportionalWidth(image, height));
		}
	}	
	
}