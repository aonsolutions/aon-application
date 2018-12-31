package com.code.aon.ui.company.controller;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_IMAGE_DUPLICATED_NAME;
import static com.code.aon.ui.common.ICommonMessages.COMPANY_IMAGE_INVALID_CHARACTER;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.ImageUtil;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.registry.controller.RegistryAttachController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

public class CompanyImagesController extends RegistryAttachController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyImagesController.class.getName());
	
	private boolean ratio;
	
	private Integer width;
	
	private Integer height;
	
	private Integer originalWidth;
	
	private Integer originalHeight;	
	
	public CompanyImagesController() {
		setRatio(true);
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getOriginalWidth() {
		return originalWidth;
	}

	public void setOriginalWidth(Integer originalWidth) {
		this.originalWidth = originalWidth;
	}

	public Integer getOriginalHeight() {
		return originalHeight;
	}

	public void setOriginalHeight(Integer originalHeight) {
		this.originalHeight = originalHeight;
	}

	public boolean isRatio() {
		return ratio;
	}

	public void setRatio(boolean ratio) {
		this.ratio = ratio;
	}

	public boolean isDimensionEditable() {
		return (getOriginalWidth() != null) && (getOriginalHeight() != null);
	}
	
	public void reset() {
		setWidth(null);
		setOriginalWidth(null);
		setHeight(null);
		setOriginalHeight(null);
	}
	
	public void init( byte[] data ) {
		reset();
		if (! ArrayUtils.isEmpty(data) ) {
			try {
				BufferedImage bImage = ImageUtil.getBufferedImage( data );
				if ( bImage != null ) {
					setWidth(bImage.getWidth());
					setOriginalWidth(bImage.getWidth());
					setHeight(bImage.getHeight());
					setOriginalHeight(bImage.getHeight());
				}							
			} catch ( Throwable e ) {
				LOGGER.error(e.getMessage(), e);
			}
		}				
	}
	
	public boolean isImage() {
		if ( (getAonFile() != null) && (getAonFile().getMimeType() != null) ) {
			return StringUtils.startsWith(getAonFile().getMimeType().getName(), "image/");
		}
		return false;
	}
	
	public void paint(OutputStream out, Object data) throws IOException {
		if (getAonFile() != null && (getAonFile().getSize() > 0) ) {
			out.write(getAonFile().getData());
		}
	}    
	
	public void update(RegistryAttachment attachment ) {
		if ( isDimensionEditable() ) {
			if ( (!ObjectUtils.equals(width, getOriginalWidth())) || (!ObjectUtils.equals(height, getOriginalHeight())) ) {
				byte[] data = getAonFile().getData();
				BufferedImage image = ImageUtil.getBufferedImage( data );
				BufferedImage newImage = ImageUtil.scale(image, width, height);
				String format = (getAonFile().getMimeType() != null) ? getAonFile().getMimeType().getExtension() : null;
				data = ImageUtil.getImage(newImage, format);
				attachment.setData(data);
				attachment.setMimeType(getAonFile().resolveMimeType());
			}			
		}		
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
			FacesMessage message = new FacesMessage(AonUtil.getMessage(COMPANY_IMAGE_DUPLICATED_NAME));
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException( message );
		}
		for( int i = 0; i < imageName.length(); i++ ) {
			char c = imageName.charAt(i);
			if (! (Character.isLetter(c) || Character.isDigit(c) || (c == ' ') ) ) {
				String text = AonUtil.getMessage(COMPANY_IMAGE_INVALID_CHARACTER, c);
				FacesMessage message = new FacesMessage(text);
				message.setSeverity(FacesMessage.SEVERITY_ERROR);				
				throw new ValidatorException( message );										
			}
		}
	}	
	
	public void onChangeWidth(ActionEvent event) throws ManagerBeanException {
		if (ratio){
			setHeight( ImageUtil.getProportionalHeight(getOriginalWidth(), getOriginalHeight(), width));
		}
	}

	public void onChangeHeight(ActionEvent event) throws ManagerBeanException {
		if (ratio){
			setWidth( ImageUtil.getProportionalWidth(getOriginalWidth(), getOriginalHeight(), height));
		}
	}	

	public void createContent(OutputStream out, Object idData) throws IOException {
		Integer id = (Integer) idData;
		if ( id != null ) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
				RegistryAttachment ra = (RegistryAttachment) bean.get(id);
				byte[] data = ra.getData();
				if (! ArrayUtils.isEmpty(data) ) {
					out.write(data);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error( e.getMessage(), e );
			}
		}
	}

	public String getDownloadURL() throws ManagerBeanException, IOException, SQLException, KeyStoreException, GeneralSecurityException {
		String url = null;
		RegistryAttachment ra = (RegistryAttachment) getTo();
		if ( (ra.getDriveId() != null) && (ra.getMD5() == null) ) {
			String domainName = AonUtil.getDomainName();
			Domain domain = new Domain().setName(domainName).setId(ra.getDomain());
			User user = new User().setLogin(AonUtil.getRemoteUser() != null ? AonUtil.getRemoteUser() : "");
			
			DomainGserviceaccount d = DBConsults.getServiceAccount(domain, user);
			Drive drive = AonDrive.getInstace().serviceInitialize(d);
			File f = AonDrive.getInstace().getFile(drive, ra.getDriveId());
			ra.setMD5(f.getMd5Checksum());
		}
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		url = ds.getDomainURL() + ra.getDownloadURL();
		return url;
	}
	
	@Override
	protected int getDefaultPageLimit() {
		return AonUtil.getConfigurationController().getPageLimit();
	}	
		
}