package com.code.aon.ui.infoweb.controller;

import static com.code.aon.common.enumeration.AppParam.WEBINFO_HOMEPAGE_ID;
import static com.code.aon.common.enumeration.AppParam.WEBINFO_TEMPLATE_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.PUBLISH_PARAMETER;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.BUNDLE_NAME;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.DIRECTORY_CREATION_ERROR;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.DIRECTORY_NOT_FOUND;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.DIRECTORY_NO_READABLE;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.IMAGE_COPY_ERROR;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.NO_PUBLISH_PARAMETERS;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.PAGE_WITHOUT_DETAIL;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.WEB_GENERATED;
import static com.code.aon.ui.infoweb.controller.IInfoWebConstants.WEB_GENERATION_ERROR;
import static com.code.aon.ui.publisher.controller.IPublisherConstants.PUBLISH_ERROR;
import static com.code.aon.ui.publisher.controller.IPublisherConstants.PUBLISH_OK;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.ImageUtil;
import com.code.aon.common.util.MimeResolver;
import com.code.aon.company.Company;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.infoweb.WebInfo;
import com.code.aon.infoweb.WebInfoPage;
import com.code.aon.infoweb.WebInfoPageDetail;
import com.code.aon.infoweb.WebInfoPageResource;
import com.code.aon.infoweb.WebInfoStyle;
import com.code.aon.infoweb.enumeration.WebInfoFontType;
import com.code.aon.infoweb.enumeration.WebInfoPageType;
import com.code.aon.infoweb.enumeration.WebInfoVariableType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanyImagesController;
import com.code.aon.ui.config.PublishProperties;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.PublishParameterController;
import com.code.aon.ui.config.util.FTPUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.infoweb.util.PathUtil;
import com.code.aon.ui.infoweb.util.VelocityUtil;
import com.code.aon.ui.infoweb.velocity.ImageHandler;
import com.code.aon.ui.infoweb.velocity.MenuOptionHandler;
import com.code.aon.ui.infoweb.velocity.VelocityConstants;
import com.code.aon.ui.publisher.controller.IPublisherConstants;
import com.code.aon.ui.publisher.util.ImageUtilEx;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class GeneratorController extends BasicController implements VelocityConstants  {

	private static final String WEB_INFO_PAGE_RESOURCE_RATTACH_DESCRIPTION = "WebInfoPageResource.rattach.description";

	private static final String WEB_INFO_PAGE_RESOURCE_RATTACH_DATA = "WebInfoPageResource.rattach.data";

	private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorController.class.getName());
	
	private static final int MAX_PAGE_COUNT = 8;
	
	private static final FileFilter WEB_INFO_FILTER = new WebinfoFilter();
	
	private VelocityUtil vu;
	
	private boolean generated;
	
	private boolean published;
	
	private int homepage;
	
	private PublishProperties publishProperties;
	
	private LogPanelController log = LogPanelController.getInstance();
	
	private String getTemplate() {
		String template = DEFAULT_TEMPLATE;
		//Obtenemos el template seleccionado
		ApplicationParameter ap = AppParamUtil.getParameter(WEBINFO_TEMPLATE_NAME);
		if ( ap != null ) {
			template = ap.getValue();				
		}
		return template;
	}
	
	/**
	 * Obtenemos si esta definida la pagina homepage seleccionada
	 * 
	 * @return the homepage
	 */
	private int getHomepage() {
		int homepage = 0;
		ApplicationParameter ap = AppParamUtil.getParameter(WEBINFO_HOMEPAGE_ID);
		if ( ap != null ) {
			homepage = Integer.parseInt(ap.getValue());				
		}
		return homepage;
	}
	
	private Company getCompany() throws ManagerBeanException {
		IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
		List<ITransferObject> companyList = companyBean.getList(null);
		if (! companyList.isEmpty() ) {
			return (Company)companyList.get(0);
		}		
		return null;
	}
	
	public void onInit(ActionEvent event) {
		PublishParameterController ppc = (PublishParameterController) AonUtil.getRegisteredBean(PUBLISH_PARAMETER);
		this.publishProperties = ppc.getPublishProperties();
		this.generated = false;
		this.published = false;		
	}	
	
	public boolean isReadableDirectory( File directory ) {
		if (!directory.exists()) {
			log.error(AonUtil.getMessage(BUNDLE_NAME, DIRECTORY_NOT_FOUND, directory));
			return false;
		}
		if (!directory.canRead()) {
			log.error(AonUtil.getMessage(BUNDLE_NAME, DIRECTORY_NO_READABLE, directory));
			return false;
		}		
		return true;
	}
	
	public void onGenerate(ActionEvent event) throws ManagerBeanException {
		this.generated = false;
		this.published = false;
		try {
			if ( this.publishProperties.isEmpty() ) {
				log.error(AonUtil.getMessage(BUNDLE_NAME, NO_PUBLISH_PARAMETERS));
				return;
			}
			
			HibernateUtil.setCloseSession(false);
			vu = new VelocityUtil();
			//Añadimos al contexto todo lo necesario para las paginas
			
			String template = getTemplate();
			LOGGER.info( "Using template: {}", template );
			vu.setTemplate(template);
			//Indicamos el directorio del template
			File templateDirectory = PathUtil.getTemplatePath(template);
			if (! isReadableDirectory(templateDirectory) ) {
				return;
			}
			LOGGER.info( "Template directory: {}", templateDirectory );
			File previewDirectory = PathUtil.getPreviewPath();
			if (! previewDirectory.exists() ) {
				if (! previewDirectory.mkdirs() ) {
					log.error(AonUtil.getMessage(BUNDLE_NAME, DIRECTORY_CREATION_ERROR, previewDirectory));
					return;	
				}
			}
			LOGGER.info( "Preview directory: {}", previewDirectory );
			FileUtils.cleanDirectory(previewDirectory);
			vu.setTemplateDirectory(templateDirectory);
			vu.setOutputDirectory(previewDirectory);
			vu.initialize();
			
			File imagesPreviewDirectory = new File( previewDirectory, IMAGES_PATH );
			imagesPreviewDirectory.mkdirs();
			File cssPreviewDirectory = new File( previewDirectory, CSS_PATH );
			cssPreviewDirectory.mkdirs();
			
			GregorianCalendar gc = new GregorianCalendar();
			vu.put(CURRENT_YEAR_KEY, gc.get(Calendar.YEAR));

			Company company = getCompany();
			if (company != null) {
				vu.put(COMPANY_KEY, company);
	
				addCompanyLogo( company, imagesPreviewDirectory );
				addWebInfoAttributes( company );
				addCompanyImages( company, imagesPreviewDirectory );

				addContactData( company );
				addAddresses( company );
			}

			this.homepage = getHomepage();
			LOGGER.info( "Homepage: {}", homepage );
			
			generateMenu();

			//Generar pagina principal
			vu.put(CONTENT_KEY, HOME_TEMPLATE);
			if ( isGenerateDefaultPage() ) {
				vu.generate(INDEX_HTML);
			}

			//Generar pagina de error 404
			vu.generate(ERROR_TEMPLATE,ERROR_HTML);

			//Generar pagina de error 404
			vu.put(CONTENT_KEY, MAIL_TEMPLATE);
			vu.generate(MAIL_PHP);

			generatePages();
			generateCss(previewDirectory, cssPreviewDirectory);
			
			copyDirectoryToDirectory(new File(templateDirectory, "js"), previewDirectory );
			File currentTemplateCssDirectory = new File(templateDirectory, CSS_PATH);
			copyDirectoryToDirectory(new File(currentTemplateCssDirectory, IMAGES_PATH), cssPreviewDirectory );
			copyDirectoryToDirectory(new File(currentTemplateCssDirectory, CSSIMG_PATH), cssPreviewDirectory );
			copyDirectoryToDirectory(new File(templateDirectory, IMAGES_PATH), previewDirectory );

			log.info(AonUtil.getMessage(BUNDLE_NAME, WEB_GENERATED));
			
			this.generated = upload(this.publishProperties.getPreviewPath());

		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th );
			log.error(AonUtil.getMessage(BUNDLE_NAME, WEB_GENERATION_ERROR));
		} finally {
			HibernateUtil.setCloseSession(true);
			HibernateUtil.closeSession(HibernateUtil.getSessionFactoryName());
		}		
	}
	
	public void onPublish(ActionEvent event) throws ManagerBeanException {
		this.published = upload(this.publishProperties.getPublishPath());
	}	
	
	private boolean upload( String destination ) {
		boolean published = false;
		FTPUtil ftp = new FTPUtil(log);
		try {
			File source = PathUtil.getPreviewPath();
			ftp.connect(publishProperties.getFtpProperties());
			if ( ftp.isConnected() ) {
				ftp.synchronize(source, destination);
				published = true;
			}
		} catch (Throwable th) {
			LOGGER.error(th.getMessage(), th );
			log.error( AonUtil.getMessage(IPublisherConstants.BUNDLE_NAME, PUBLISH_ERROR) );
		} finally {
			ftp.close();
		}
		if ( published ) {
			log.info( AonUtil.getMessage(IPublisherConstants.BUNDLE_NAME, PUBLISH_OK) );
		}		
		log.finish();
		return published;
	}

	private void copyImageToCss(File temporalDirectory, File cssTemporalDirectory, String value) {
		try {
			File srcFile = new File( new File(temporalDirectory, IMAGES_PATH), value );
			File destFile = new File( new File(cssTemporalDirectory, IMAGES_PATH), value );
			if ( srcFile.exists() ) {
				FileUtils.copyFile(srcFile, destFile);	
			} else {
				LOGGER.warn( "File doesn't exists: {}", srcFile );
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e );
		}
	}
	
	private void copyDirectoryToDirectory(File srcDir, File destDir) {
		try {
			if ( srcDir.exists() ) {
				LOGGER.info( "Copy directory: {} -> {}", srcDir, destDir );
				File realDestDir = new File(destDir, srcDir.getName());
				FileUtils.copyDirectory(srcDir, realDestDir, WEB_INFO_FILTER );
			} else {
				LOGGER.warn( "Directory doesn't exists: {}", srcDir );
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e );
		}		
	}

	private void generateGenericPage(WebInfoPage wip) throws ManagerBeanException {
		LOGGER.debug( "Generating gallery: {}", wip );
		IManagerBean wipdBean = BeanManager.getManagerBean(WebInfoPageDetail.class);
		Criteria wipdCriteria = new Criteria();
		wipdCriteria.addEqualExpression(wipdBean.getFieldName(IEntityAlias.WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE_ID), wip.getId());
		List<ITransferObject> wipdList = wipdBean.getList(wipdCriteria);
		WebInfoPageDetail wipd = new WebInfoPageDetail();
		if (wipdList.size() > 0) {
			wipd = (WebInfoPageDetail)wipdList.get(0);
		} else {			
			log.warn(AonUtil.getMessage(BUNDLE_NAME, PAGE_WITHOUT_DETAIL, wip.getName()));
			return;
		}

		String template = LOCATION_TEMPLATE;
		if (wip.getType() != WebInfoPageType.LOCATION) {
			template = "generic" + wipd.getLayout().ordinal() + ".vm";
		}
		vu.put(TITLE_KEY, wipd.getTitle());
		vu.put(TEXT_KEY, wipd.getContent());
		if (wip.getType() == WebInfoPageType.LOCATION) {
			//Primero miramos si el extra tiene |
			String extra = wipd.getExtra();
			if (extra.indexOf("|") >= 0) {
				int num = 0;
				StringTokenizer st = new StringTokenizer(extra, "|");
				while (st.hasMoreTokens()) {
					String coords = st.nextToken();
					if (num == 0) vu.put(COORDS_KEY, coords);
					else vu.put(COORDS_KEY+num, coords);
					num++;
				}
			} else {
				vu.put(COORDS_KEY, wipd.getExtra());
			}
		}
		
		//Ahora las imagenes
		ArrayList<ImageHandler> images = new ArrayList<ImageHandler>();
		IManagerBean wiprBean = BeanManager.getManagerBean(WebInfoPageResource.class);
		Criteria wiprCriteria = new Criteria();
		wiprCriteria.addEqualExpression(wiprBean.getFieldName(IEntityAlias.WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE_ID), wip.getId());
		wiprCriteria.addNotNullExpression(WEB_INFO_PAGE_RESOURCE_RATTACH_DATA);
		List<ITransferObject> wiprList = wiprBean.getList(wiprCriteria);

		for (int i = 0; i < wiprList.size(); i++) {
			WebInfoPageResource wipr = (WebInfoPageResource)wiprList.get(i);
			ImageHandler ih = new ImageHandler(getImageName(wipr.getRattach()), getImagePageLink(wipr.getRattach().getDescription()), wipr.getContent());
			images.add(ih);
		}
		vu.put(IMAGES_KEY, images);
		generatePage(template, wip);
		vu.remove(TITLE_KEY);
		vu.remove(TEXT_KEY);
		if (wip.getType() == WebInfoPageType.LOCATION) vu.remove(COORDS_KEY);
		vu.remove(IMAGES_KEY);
	}

	private void generateGalleryPage(WebInfoPage wip) throws ManagerBeanException {
		LOGGER.debug( "Generating gallery: {}", wip);
		ArrayList<ImageHandler> images = new ArrayList<ImageHandler>();
		IManagerBean wiprBean = BeanManager.getManagerBean(WebInfoPageResource.class);
		Criteria wiprCriteria = new Criteria();
		wiprCriteria.addEqualExpression(wiprBean.getFieldName(IEntityAlias.WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE_ID), wip.getId());
		wiprCriteria.addNotNullExpression(WEB_INFO_PAGE_RESOURCE_RATTACH_DESCRIPTION);
		wiprCriteria.addNotNullExpression(WEB_INFO_PAGE_RESOURCE_RATTACH_DATA);
		List<ITransferObject> wiprList = wiprBean.getList(wiprCriteria);
		String previous_link = "";
		String next_link = "";
		boolean primera = true;
		boolean ultima = false;
		for (int i = 0; i < wiprList.size(); i++) {
			if ((i+1) == wiprList.size()) {
				ultima = true;
				next_link = "";
			} else {
				WebInfoPageResource wiprn = (WebInfoPageResource)wiprList.get(i+1);
				next_link = getImagePageLink(wiprn.getRattach().getDescription());
			}
			WebInfoPageResource wipr = (WebInfoPageResource)wiprList.get(i);
			//Por cada imagen se hace tambien una pagina
			ImageHandler ih = new ImageHandler(getImageName(wipr.getRattach()), getImagePageLink(wipr.getRattach().getDescription()), wipr.getContent());
			images.add(ih);
			
			vu.put(IMAGE_KEY, ih);
			if (!primera) vu.put(PREVIOUS_KEY, previous_link);
			if (!ultima) vu.put(NEXT_KEY, next_link);
			vu.put(RETURN_KEY, getPageName(wip.getName()));
			generateImagePage(IMAGE_VIEW_TEMPLATE, wipr.getRattach().getDescription());
			vu.remove(IMAGE_KEY);
			if (!primera) vu.remove(PREVIOUS_KEY);
			if (!ultima) vu.remove(NEXT_KEY);
			vu.remove(RETURN_KEY);
			primera = false;
			previous_link = getImagePageLink(wipr.getRattach().getDescription());
		}
		vu.put(GALLERY_KEY, images);
		
		generatePage(GALLERY_TEMPLATE, wip);
		if (wip.getType() == WebInfoPageType.LOCATION) {
			vu.remove(COORDS_KEY);
		}
		vu.remove(GALLERY_KEY);
	}

	private void generateImagePage(String template, String name) {
		vu.put(PAGENAME_KEY, name);
		vu.put(CONTENT_KEY, template);
		vu.generate( getImagePageName(name) );
	}
	
	private void generatePage(String template, WebInfoPage wip) {
		String name = wip.getName();
		vu.put(PAGENAME_KEY, name);
		vu.put(CONTENT_KEY, template);
		String fileName = (wip.getId() != homepage) ? getPageName(name) : INDEX_HTML;
		vu.generate( fileName );
	}

	private String getPageName(String name) {
		String page = name + ".html";
		page = page.replaceAll(" ", "_");
		page = page.replaceAll("ñ", "n").replaceAll("á", "a").replaceAll("é", "e").replaceAll("í", "i").replaceAll("ó", "o").replaceAll("ú", "u");
		page = page.replaceAll("Ñ", "N").replaceAll("Á", "A").replaceAll("É", "E").replaceAll("Í", "I").replaceAll("Ó", "O").replaceAll("Ú", "U");
		return page;
	}

	private String getPageLink(String name) {
		return StringEscapeUtils.escapeHtml(getPageName(name));
	}
	
	private String getImagePageName(String name) {
		return IMAGE_PAGE_PREFFIX + getPageName(name);
	}

	private String getImagePageLink(String name) {
		return StringEscapeUtils.escapeHtml(getImagePageName(name));
	}
	
    public WebInfoVariableType getVariableType(String text) {
	    WebInfoVariableType wvt[] = WebInfoVariableType.values();
		for (int i = 0;i<wvt.length;i++) {
			if (text.startsWith(wvt[i].getPrefix())) {
				return wvt[i];
			}
		}
		return null;
    }

	public String getFontType(WebInfoStyle style) {
		if ( NumberUtils.isNumber(style.getValue()) ) {
			try {
				int value = Integer.parseInt(style.getValue());
				if ( value == WebInfoFontType.ARIAL.ordinal()) {
					return WebInfoFontType.ARIAL.getValue();
				}
				if (value == WebInfoFontType.TIMES.ordinal()) {
					return WebInfoFontType.TIMES.getValue();
				}
				if (value == WebInfoFontType.TREBUCHET.ordinal()) {
					return WebInfoFontType.TREBUCHET.getValue();
				}
				if (value == WebInfoFontType.VERDANA.ordinal()) {
					return WebInfoFontType.VERDANA.getValue();
				}
			} catch (NumberFormatException n) {
				LOGGER.error(n.getMessage(), n );
			}
		}
		return "Verdana";
	}

	public String getImage(WebInfoStyle style) {
		String filename = "blank.jpg";
		if ( NumberUtils.isNumber(style.getValue()) ) {
			try {
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				Criteria attachCriteria = new Criteria();
				attachCriteria.addEqualExpression(attachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_ID), Integer.parseInt(style.getValue()));
				List<ITransferObject> attachList = attachBean.getList(attachCriteria);
				if (attachList.size() > 0) {
					RegistryAttachment ra = (RegistryAttachment)attachList.get(0);
					filename = getImageName(ra);
				}
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th );
			}
		}
		return filename;
	}

	public boolean isGenerated() {
		return generated;
	}

	public boolean isPublished() {
		return published;
	}

	public String getWebPage() {
		return this.publishProperties.getPublishURL();
	}
	
	public String getPreviewPage() {
		return this.publishProperties.getPreviewURL();
	}
	
	public static String getImageName( RegistryAttachment ra ) {
		return getImageName(ra, null);
	}

	public static String getImageName( RegistryAttachment ra, String name ) {
		MimeType mimeType = ra.getMimeType();
		if ( mimeType == null ) {
			mimeType = CompanyImagesController.getMimeType(ra.getDescription(), ra.getData());
			if ( mimeType == null ) {
				mimeType = MimeType.MIME_JPEG;
			}
		}
		String preffix = (name != null ) ? name : ra.getDescription();
		if ( StringUtils.isEmpty(preffix) ) {
			preffix = "webInfoImage_" + ra.getId(); 
		}
		return preffix + "." + mimeType.getExtension();
	}

	private void addCompanyLogo( Company company, File imagesDirectory ) throws ManagerBeanException {
		LOGGER.info( "Writing company logo" );
		IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria attachCriteria = new Criteria();
		attachCriteria.addEqualExpression(attachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
		attachCriteria.addEqualExpression(attachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.LOGO);
		List<ITransferObject> attachList = attachBean.getList(attachCriteria);
		if (attachList.size() > 0) {
			RegistryAttachment ra = (RegistryAttachment) attachList.get(0);
			String filename = getImageName(ra, LOGO_KEY);
			File path = new File( imagesDirectory, filename );
			try {
				FileUtils.writeByteArrayToFile(path, ra.getData());
				vu.put(LOGO_KEY, filename);
			} catch (IOException e) {
				LOGGER.error( "Error writing company logo " + path, e );
			}
		}		
	}
	
	private void addWebInfoAttributes( Company company ) throws ManagerBeanException {
		/*
		 * 		$content = home.vm {
		 *			$description - Descripcion comercial de la empresa (obligatorio - WARNING)
		 *			$slogan - Eslogan de la empresa (opcional)
		 *			$schedule - Horario comercial (opcional)
		 * 		}
		 */		
		LOGGER.info( "Adding WebInfo attributes to the context" );
		IManagerBean webinfoBean = BeanManager.getManagerBean(WebInfo.class);
		Criteria webinfoCriteria = new Criteria();
		webinfoCriteria.addEqualExpression(webinfoBean.getFieldName(IEntityAlias.WEB_INFO_COMPANY_ID), company.getId());
		List<ITransferObject> webinfoList = webinfoBean.getList(webinfoCriteria);
		if (webinfoList.size() > 0) {
			WebInfo wi = (WebInfo)webinfoList.get(0);
			String description = wi.getCommercialDescription();
			if (! StringUtils.isBlank(description) ) {
				vu.put(DESCRIPTION_KEY, description);
			}
			String slogan = wi.getSlogan();
			if (! StringUtils.isBlank(slogan) ) {
				vu.put(SLOGAN_KEY, slogan);
			}
			String schedule = wi.getSchedule();
			if (! StringUtils.isBlank(schedule) ) {
				vu.put(SCHEDULE_KEY, schedule);
			}
		}		
	}

	private void addCompanyImages( Company company, File imagesDirectory ) throws ManagerBeanException {
		LOGGER.info( "Writing company images" );
		IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		List<ImageHandler> all_images = new LinkedList<ImageHandler>();
		Criteria attachCriteria = new Criteria();
		attachCriteria.addEqualExpression(attachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
		attachCriteria.addEqualExpression(attachBean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ADDITIONAL_IMAGE);
		List<ITransferObject> attachList = attachBean.getList(attachCriteria);
		for (int i=0; i<attachList.size(); i++) {
			RegistryAttachment ra = (RegistryAttachment)attachList.get(i);
			if ( (ra.getData() != null) && (!StringUtils.isEmpty(ra.getDescription())) ) {
				String filename = getImageName(ra);
				File path = new File(imagesDirectory, filename);
				if (!copyRegistryBlobToFile(ra, 200, 200, path)) {
					log.error(AonUtil.getMessage(BUNDLE_NAME, IMAGE_COPY_ERROR, filename)); 
				}
				ImageHandler ih = new ImageHandler(filename, getImagePageLink(ra.getDescription()), ra.getDescription());
				all_images.add(ih);
			} else {
				LOGGER.warn( "Invalid image: {}", ra );
			}
		}
		vu.put(ALL_IMAGES_KEY, all_images);	
	}
	
	private void addContactData( Company company ) {
		/*
		 * 		Sacar datos de contacto {
		 * 			$email - Email para el formulario de envio, si no existe no hay opcion de menu. 
		 * 		}
		 */
		LOGGER.info( "Adding Contact attributes to the context" );
		Iterator<RegistryMedia> mediaList = company.getMedias().iterator();
		while (mediaList.hasNext()) {
			RegistryMedia m = (RegistryMedia)mediaList.next();
			switch (m.getMediaType()) {
				case EMAIL:
					vu.put(EMAIL_KEY, m.getValue());
					break;
				case FIXED_PHONE:
					vu.put(PHONE_KEY, m.getValue());
					break;
				case FAX:
					vu.put(FAX_KEY, m.getValue());
					break;
			}
		}		
	}
	
	private void addAddresses( Company company ) throws ManagerBeanException {
		/*
		 * 		$content = address.vm {
		 *			$company - Datos de empresa
		 *			$addresses - Direcciones de la empresa (Google Maps)
		 * 		}
		 */	
		LOGGER.info( "Adding addresses to the context" );
		RegistryAddress defaultAddress = company.getDefaultAddress();
		ArrayList<RegistryAddress> addresses = new ArrayList<RegistryAddress>();
		Iterator<RegistryAddress> addressList = company.getAddresses().iterator();
		while (addressList.hasNext()) {
			RegistryAddress a = (RegistryAddress)addressList.next();
			if (defaultAddress != null && a.getId() == defaultAddress.getId()) {
				//Nothing
			} else {
				addresses.add(a);
			}
		}
		if (addresses.size() > 0) {
			vu.put(ADDRESSES_KEY, addresses);
		}
		if (defaultAddress != null) {
			LOGGER.debug( "Default address: {}", defaultAddress );
			vu.put(ADDRESS_KEY, defaultAddress);		
		}
	}

	private void generateCss( File temporalDirectory, File cssTemporalDirectory) throws ManagerBeanException {
		//Parseamos los estilos
		LOGGER.info( "Writting the css" );
		IManagerBean wisBean = BeanManager.getManagerBean(WebInfoStyle.class);
		List<ITransferObject> wisList = wisBean.getList(null);
		for (int i=0;i < wisList.size();i++) {
			WebInfoStyle wis = (WebInfoStyle)wisList.get(i);
			String name = wis.getVariable(); 
			WebInfoVariableType wivt = getVariableType(name);
			String value = wis.getValue();
			if (wivt == WebInfoVariableType.FONT) {
				value = getFontType(wis);
			} else if (wivt == WebInfoVariableType.IMAGE) {
				value = getImage(wis);
				copyImageToCss(temporalDirectory, cssTemporalDirectory, value);
			} else if (wivt == WebInfoVariableType.BORDER || wivt == WebInfoVariableType.SIZE) {
				value = value + "px";
			}
			vu.put(name, value);
			vu.put(name.toLowerCase(), value);
		}
		vu.generateCSS();		
	}
	
	private void generatePages() throws ManagerBeanException {
		//Generar paginas segun menu.
		LOGGER.info( "Generating the pages" );
		IManagerBean wipBean = BeanManager.getManagerBean(WebInfoPage.class);
		Criteria wipCriteria = new Criteria();
		wipCriteria.addEqualExpression(wipBean.getFieldName(IEntityAlias.WEB_INFO_PAGE_ACTIVE), true);
		wipCriteria.addOrder(wipBean.getFieldName(IEntityAlias.WEB_INFO_PAGE_POSITION));
		List<ITransferObject> wipList = wipBean.getList(wipCriteria, 0, MAX_PAGE_COUNT);
		for (int i=0;i < wipList.size();i++) {
			WebInfoPage wip = (WebInfoPage)wipList.get(i);
			switch (wip.getType()) {
				case CONTACT:
					generatePage(CONTACT_TEMPLATE, wip );
					break;
				case GENERIC:
					generateGenericPage(wip);
					break;
				case LOCATION:
					generateGenericPage(wip);
					break;
				case GALLERY:
					generateGalleryPage(wip);
					break;
			}
		}		
	}
	
	private MenuOptionHandler getMenuOptionHandler( WebInfoPage wip ) {
		String label = wip.getName();
		String link = getPageLink(label);
		return new MenuOptionHandler(label, link);		
	}

	/**
	 * Sacamos el menu de las paginas y cada una de las pagina.
	 * @throws ManagerBeanException 
	 */
	private void generateMenu() throws ManagerBeanException {
		LOGGER.info( "Generating the menu for the context" );
		ArrayList<MenuOptionHandler> menu = new ArrayList<MenuOptionHandler>();
		IManagerBean wimBean = BeanManager.getManagerBean(WebInfoPage.class);
		MenuOptionHandler indexMenu = new MenuOptionHandler(INDEX_TITLE, INDEX_HTML);
		if (! isGenerateDefaultPage() ) {			
			WebInfoPage wip = (WebInfoPage) wimBean.get(homepage);
			if ( wip != null ) {
				indexMenu.setLabel(wip.getName());
			}
		}
		menu.add(indexMenu);
		Criteria wimCriteria = new Criteria();
		wimCriteria.addEqualExpression(wimBean.getFieldName(IEntityAlias.WEB_INFO_PAGE_ACTIVE), true);
		wimCriteria.addOrder(wimBean.getFieldName(IEntityAlias.WEB_INFO_PAGE_POSITION));
		List<ITransferObject> wimList = wimBean.getList(wimCriteria, 0, MAX_PAGE_COUNT);
		for (int i=0;i < wimList.size();i++) {
			WebInfoPage wip = (WebInfoPage)wimList.get(i);
			int id = wip.getId();
			if (homepage != id) {
				menu.add( getMenuOptionHandler(wip) );
			}
		}
		vu.put(MENU_KEY, menu);		
	}
	
	private boolean isGenerateDefaultPage() {
		return this.homepage == 0;
	}
	
	private MimeType getMimeType( RegistryAttachment ra ) {
		MimeType type = ra.getMimeType();
		if ( type == null ) {
			return MimeResolver.getMimeType(ra.getData());
		}
		return type;
	}
	
	private boolean copyRegistryBlobToFile(RegistryAttachment ra, int maxWidth, int maxHeight, File file) {
		try {
			FileUtils.writeByteArrayToFile(file, ra.getData());
			MimeType type = getMimeType(ra);
			BufferedImage image = ImageUtilEx.getBufferedImage(ra.getData(), type);
			Dimension d = ImageUtil.getResizeDimension(image, maxWidth, maxHeight);
			File outputFile = new File(file.getParentFile(), "tn_" + file.getName());		
			BufferedImage newImage = ImageUtil.scale(image, d.width, d.height);
			return ImageUtilEx.writeBufferedImage(newImage, type, outputFile);
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}
	
	private static class WebinfoFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			if ( pathname.isDirectory() ) {
				String name = pathname.getName();
				if ( name.equalsIgnoreCase("CVS") || name.equalsIgnoreCase(".svn") ) {
					return false;
				}
			}
			return true;
		}
		
	}
	
}
