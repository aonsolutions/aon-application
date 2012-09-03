package com.code.aon.ui.company.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.ImageUtil;
import com.code.aon.common.util.MimeResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.registry.controller.RegistryAttachController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CompanyImagesController extends RegistryAttachController implements ICompanyConstants {

	private BufferedImage image;
	
	private boolean ratio;
	
	private int width;
	
	private int height;
	
	private int maxWidth;
	
	private int maxHeight;	
	
	public CompanyImagesController() {
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
	
	public void init( byte[] data ) {
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
	
	public void paint(OutputStream out, Object data) throws IOException {
		if (getAonFile() != null && (getAonFile().getSize() > 0) ) {
			out.write(getAonFile().getData());
		}
	}    
	
	public void update(RegistryAttachment attachment ) {
		byte[] data = getAonFile().getData();
		if ( (width != image.getWidth()) || (height != image.getHeight()) ) {
			BufferedImage newImage = ImageUtil.scale(image, width, height);
			String format = (getAonFile().getMimeType() != null) ? getAonFile().getMimeType().getExtension() : null;
			data = ImageUtil.getImage(newImage, format);
		}
		attachment.setData(data);
		MimeType mimeType = CompanyImagesController.getMimeType(getAonFile().getFileName(), data);
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
			criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_ID), ra.getId());
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_DESCRIPTION), imageName);
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