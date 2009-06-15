package com.code.aon.ui.infoweb.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.infoweb.WebInfo;
import com.code.aon.infoweb.WebInfoPage;
import com.code.aon.infoweb.WebInfoPageDetail;
import com.code.aon.infoweb.WebInfoPageResource;
import com.code.aon.infoweb.WebInfoStyle;
import com.code.aon.infoweb.dao.IWebInfoAlias;
import com.code.aon.infoweb.enumeration.WebInfoFontType;
import com.code.aon.infoweb.enumeration.WebInfoPageType;
import com.code.aon.infoweb.enumeration.WebInfoVariableType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanyImagesController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.infoweb.util.FTPUtil;
import com.code.aon.ui.infoweb.util.ImageUtil;
import com.code.aon.ui.infoweb.util.VelocityUtil;
import com.code.aon.ui.infoweb.velocity.ImageHandler;
import com.code.aon.ui.infoweb.velocity.MenuOptionHandler;
import com.code.aon.ui.infoweb.velocity.VelocityConstants;
import com.code.aon.ui.util.AonUtil;

public class GeneratorController extends BasicController implements VelocityConstants  {

	private static final Logger LOGGER = Logger.getLogger(GeneratorController.class.getName());
	
	private VelocityUtil vu;
	
	private boolean published;
	
	private String webPage;
	
	private int homepage;
	
	private String getTemplate() {
		String template = "default";
		//Obtenemos el template seleccionado
		try {
			IManagerBean apBean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(apBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), TEMPLATE_NAME_PARAM);
			List<ITransferObject> list = apBean.getList(criteria);
			if (list.size() > 0) {
				ApplicationParameter ap = (ApplicationParameter)list.get(0);
				template = ap.getValue();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
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
		try {
			IManagerBean apBean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(apBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), HOMEPAGE_NAME_PARAM);
			List<ITransferObject> list = apBean.getList(criteria);
			if (list.size() > 0) {
				ApplicationParameter ap = (ApplicationParameter)list.get(0);
				homepage = Integer.parseInt(ap.getValue());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return homepage;
	}
	
	private Company getCompany() throws ManagerBeanException {
		IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
		List<ITransferObject> companyList = (List<ITransferObject>)companyBean.getList(null);
		if (! companyList.isEmpty() ) {
			return (Company)companyList.get(0);
		}		
		return null;
	}
	
	public void onGenerate(ActionEvent event) throws ManagerBeanException {
		String domain = null;
		this.published = false;
		this.webPage = null;
		try {
			HibernateUtil.setCloseSession(false);
			vu = new VelocityUtil();
			//Añadimos al contexto todo lo necesario para las paginas
			
			String template = getTemplate();
			LOGGER.fine( "Using template: " + template );
			vu.setTemplate(template);
			//Indicamos el directorio del template
			File templateDirectory = new File(TEMPLATE_PATH);
			vu.setTemplateDirectory(templateDirectory);
			File temporalDirectory = new File( TEMPORAL_PATH, System.currentTimeMillis() + "" );
			vu.setTemporalDirectory(temporalDirectory);
			File currentTemplateDirectory = new File( templateDirectory, template );
			vu.initialize();
			
			File imagesTemporalDirectory = new File( temporalDirectory, IMAGES_PATH );
			imagesTemporalDirectory.mkdirs();
			File cssTemporalDirectory = new File( temporalDirectory, CSS_PATH );
			cssTemporalDirectory.mkdirs();
			
			GregorianCalendar gc = new GregorianCalendar();
			vu.put(CURRENT_YEAR_KEY, gc.get(Calendar.YEAR));

			Company company = getCompany();
			if (company != null) {
				vu.put(COMPANY_KEY, company);
	
				addCompanyLogo( company, imagesTemporalDirectory );
				addWebInfoAttributes( company );
				addCompanyImages( company, imagesTemporalDirectory );

				domain = addContactData( company );
				addAddresses( company );
			}

			this.homepage = getHomepage();
			LOGGER.info( "Homepage: " + homepage );
			
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
			generateCss(temporalDirectory, cssTemporalDirectory);
			
			copyDirectoryToDirectory(new File(currentTemplateDirectory, "js"), temporalDirectory );
			File currentTemplateCssDirectory = new File(currentTemplateDirectory, CSS_PATH);
			copyDirectoryToDirectory(new File(currentTemplateCssDirectory, IMAGES_PATH), cssTemporalDirectory );
			copyDirectoryToDirectory(new File(currentTemplateCssDirectory, CSSIMG_PATH), cssTemporalDirectory );
			copyDirectoryToDirectory(new File(currentTemplateDirectory, IMAGES_PATH), temporalDirectory );

			AonUtil.addInfoMessage("OK: La web ha sido generada." );
			
			publish( domain, temporalDirectory );

		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th );
			AonUtil.addErrorMessage("ERROR: Se ha producido un error durante la generacion de los contenidos.");
		} finally {
			HibernateUtil.setCloseSession(true);
			HibernateUtil.closeSession(HibernateUtil.getSessionFactoryName());
		}
		
	}

	private void copyImageToCss(File temporalDirectory, File cssTemporalDirectory, String value) {
		try {
			File srcFile = new File( new File(temporalDirectory, IMAGES_PATH), value );
			File destFile = new File( new File(cssTemporalDirectory, IMAGES_PATH), value );
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
		}
	}
	
	private void copyDirectoryToDirectory(File srcDir, File destDir) {
		try {
			if ( srcDir.exists() ) {
				LOGGER.info( "Copy directory: " + srcDir + " -> " + destDir );
				FileUtils.copyDirectoryToDirectory(srcDir, destDir );
			} else {
				LOGGER.warning( "Directory doesn't exists: " + srcDir );
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e );
		}		
	}

	private void generateGenericPage(WebInfoPage wip) throws ManagerBeanException {
		LOGGER.fine( "Generating gallery: " + wip );
		IManagerBean wipdBean = BeanManager.getManagerBean(WebInfoPageDetail.class);
		Criteria wipdCriteria = new Criteria();
		wipdCriteria.addEqualExpression(wipdBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_DETAIL_WEB_INFO_PAGE_ID), wip.getId());
		List<ITransferObject> wipdList = (List<ITransferObject>)wipdBean.getList(wipdCriteria);
		WebInfoPageDetail wipd = new WebInfoPageDetail();
		if (wipdList.size() > 0) {
			wipd = (WebInfoPageDetail)wipdList.get(0);
		}
		else {
			AonUtil.addWarningMessage("WARNING: No existe detalle de la pagina " + wip.getName() + "");
			return;
		}

		String template;
		if (wip.getType() == WebInfoPageType.LOCATION) template = LOCATION_TEMPLATE;
		else template = "generic" + wipd.getLayout().ordinal() + ".vm";
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
		wiprCriteria.addEqualExpression(wiprBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE_ID), wip.getId());
		List<ITransferObject> wiprList = (List<ITransferObject>)wiprBean.getList(wiprCriteria);

		for (int i = 0; i < wiprList.size(); i++) {
			WebInfoPageResource wipr = (WebInfoPageResource)wiprList.get(i);
			ImageHandler ih = new ImageHandler(getImageName(wipr.getRattach()), getImagePageName(wipr.getRattach().getDescription()), wipr.getContent());
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
		LOGGER.fine( "Generating gallery: " + wip);
		ArrayList<ImageHandler> images = new ArrayList<ImageHandler>();
		IManagerBean wiprBean = BeanManager.getManagerBean(WebInfoPageResource.class);
		Criteria wiprCriteria = new Criteria();
		wiprCriteria.addEqualExpression(wiprBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE_ID), wip.getId());
		wiprCriteria.addNotNullExpression("WebInfoPageResource.rattach.description");
		wiprCriteria.addNotNullExpression("WebInfoPageResource.rattach.data");
		List<ITransferObject> wiprList = (List<ITransferObject>)wiprBean.getList(wiprCriteria);
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
				next_link = getImagePageName(wiprn.getRattach().getDescription());
			}
			WebInfoPageResource wipr = (WebInfoPageResource)wiprList.get(i);
			//Por cada imagen se hace tambien una pagina
			ImageHandler ih = new ImageHandler(getImageName(wipr.getRattach()), getImagePageName(wipr.getRattach().getDescription()), wipr.getContent());
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
			previous_link = getImagePageName(wipr.getRattach().getDescription());
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

	private String getImagePageName(String name) {
		return IMAGE_PAGE_PREFFIX + getPageName(name);
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
				LOGGER.log(Level.SEVERE, n.getMessage(), n );
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
				attachCriteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_ID), Integer.parseInt(style.getValue()));
				List<ITransferObject> attachList = (List<ITransferObject>)attachBean.getList(attachCriteria);
				if (attachList.size() > 0) {
					RegistryAttachment ra = (RegistryAttachment)attachList.get(0);
					filename = getImageName(ra);
				}
			} catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th );
			}
		}
		return filename;
	}

	public boolean isPublished() {
		return published;
	}

	public String getWebPage() {
		return webPage;
	}

	private String getDomain( String value ) {
		String domain = null;
		URL web = null;
		try {
			web = new URL(value);
		} catch (MalformedURLException e) {
			if (! value.startsWith("http://") ) {
				try {
					web = new URL("http://" +value);
				} catch (MalformedURLException e1) {
					LOGGER.log(Level.WARNING, e1.getMessage(), e1 );
				}
			}
		}
		if ( web != null ) {
			domain = web.getHost();
			if (domain.indexOf(".") != domain.lastIndexOf(".")) {
				domain = domain.substring(domain.indexOf(".") + 1);
			}
		} else {
			AonUtil.addWarningMessage("WARNING: La url de la pagina web no tiene el formato correcto (http://www.midominio.com).");			
		}
		return domain;
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
		attachCriteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
		attachCriteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.LOGO);
		List<ITransferObject> attachList = (List<ITransferObject>)attachBean.getList(attachCriteria);
		if (attachList.size() > 0) {
			RegistryAttachment ra = (RegistryAttachment) attachList.get(0);
			String filename = getImageName(ra, LOGO_KEY);
			if (ImageUtil.copyRegistryBlobToFile(ra, imagesDirectory, filename)) {
				vu.put(LOGO_KEY, filename);
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
		webinfoCriteria.addEqualExpression(webinfoBean.getFieldName(IWebInfoAlias.WEB_INFO_COMPANY_ID), company.getId());
		List<ITransferObject> webinfoList = (List<ITransferObject>)webinfoBean.getList(webinfoCriteria);
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
		attachCriteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
		attachCriteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ADDITIONAL_IMAGE);
		List<ITransferObject> attachList = (List<ITransferObject>)attachBean.getList(attachCriteria);
		for (int i=0; i<attachList.size(); i++) {
			RegistryAttachment ra = (RegistryAttachment)attachList.get(i);
			if ( (ra.getData() != null) && (!StringUtils.isEmpty(ra.getDescription())) ) {
				String filename = getImageName(ra);
				if (!ImageUtil.copyRegistryBlobToFile(ra, imagesDirectory, 200, 200, filename)) {
					AonUtil.addErrorMessage("ERROR: Se produjo un error al intentar copiar la imagen " + filename); 
				}
				ImageHandler ih = new ImageHandler(filename, getImagePageName(ra.getDescription()), ra.getDescription());
				all_images.add(ih);
			} else {
				LOGGER.warning( "Invalid image: " + ra );
			}
		}
		vu.put(ALL_IMAGES_KEY, all_images);	
	}
	
	private String addContactData( Company company ) {
		/*
		 * 		Sacar datos de contacto {
		 * 			$email - Email para el formulario de envio, si no existe no hay opcion de menu. 
		 * 		}
		 */
		LOGGER.info( "Adding Contact attributes to the context" );
		String domain = null;
		Iterator<RegistryMedia> mediaList = company.getMedias().iterator();
		while (mediaList.hasNext()) {
			RegistryMedia m = (RegistryMedia)mediaList.next();
			switch (m.getMediaType()) {
				case WEB:
					domain = getDomain(m.getValue());
					LOGGER.info( "Domain: " + domain + " from " + m);
					break;
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
		return domain;
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
			LOGGER.fine( "Default address: " + defaultAddress );
			vu.put(ADDRESS_KEY, defaultAddress);		
		}
	}

	private void generateCss( File temporalDirectory, File cssTemporalDirectory) throws ManagerBeanException {
		//Parseamos los estilos
		LOGGER.info( "Writting the css" );
		IManagerBean wisBean = BeanManager.getManagerBean(WebInfoStyle.class);
		List<ITransferObject> wisList = (List<ITransferObject>)wisBean.getList(null);
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
		wipCriteria.addEqualExpression(wipBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_ACTIVE), true);
		List<ITransferObject> wipList = (List<ITransferObject>)wipBean.getList(wipCriteria);
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
		String link = getPageName(label);
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
		wimCriteria.addEqualExpression(wimBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_ACTIVE), true);
		wimCriteria.addOrder(wimBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_POSITION));
		List<ITransferObject> wimList = (List<ITransferObject>)wimBean.getList(wimCriteria);
		for (int i=0;i < wimList.size();i++) {
			WebInfoPage wip = (WebInfoPage)wimList.get(i);
			int id = wip.getId();
			if (homepage != id) {
				menu.add( getMenuOptionHandler(wip) );
			}
		}
		vu.put(MENU_KEY, menu);		
	}

	private void publish( String domain, File temporalDirectory ) {
		//Subimos por FTP
		try {
			if (domain != null) {
				webPage = "http://www." + domain + "/";					
				FTPUtil.uploadFTP(temporalDirectory, "/" + domain + "/WEBSITES/www." + domain);
				this.published = true;
			}
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th );
			AonUtil.addErrorMessage("ERROR: Se ha producido un error durante la publicacion de la pagina.");
		}		
	}
	
	private boolean isGenerateDefaultPage() {
		return this.homepage == 0;
	}
	
}
