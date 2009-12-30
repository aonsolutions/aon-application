package com.code.aon.ui.infoweb.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
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
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.infoweb.util.FTPUtil;
import com.code.aon.ui.infoweb.util.ImageUtil;
import com.code.aon.ui.infoweb.util.VelocityUtil;
import com.code.aon.ui.infoweb.velocity.ImageHandler;
import com.code.aon.ui.infoweb.velocity.MenuOptionHandler;
import com.code.aon.ui.infoweb.velocity.VelocityConstants;
import com.code.aon.ui.util.AonUtil;

public class GeneratorController extends BasicController implements VelocityConstants  {

	private VelocityUtil vu = new VelocityUtil();
	
	private boolean generate = false;
	
	private String webPage = "";
	
	public void onGenerate(ActionEvent event) throws ManagerBeanException {
		String dominio = null;
		String template_path = TEMPLATE_PATH;
		String temporal_path = TEMPORAL_PATH + "/" + System.currentTimeMillis() + "";
		String images_temporal_path = temporal_path + "/images";
		String css_temporal_path = temporal_path + "/css";
		try {
			HibernateUtil.setCloseSession(false);
			vu = new VelocityUtil();
			//Añadimos al contexto todo lo necesario para las paginas
			
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
				e.printStackTrace();
			}
			vu.setTemplate(template);
			String current_template_path = TEMPLATE_PATH + "/" + template;

	/*
	 * 		index.vm {
	 * 			#include $content
	 * 			$company - Datos de la empresa
	 * 			$logo - Logo de la empresa.
	 * 		}
	 */
			//Obtenemos datos comunes a todas las paginas company y el logo
			//Indicamos el directorio del template
			vu.setTemplate_path(template_path);
			File f = new File(images_temporal_path);
			f.mkdirs();
			vu.setTemporal_path(temporal_path);

			f = new File(css_temporal_path);
			f.mkdirs();
			
			GregorianCalendar gc = new GregorianCalendar();
			vu.put("currentYear", gc.get(Calendar.YEAR));

			Company company = new Company();
			IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
			List<ITransferObject> companyList = (List<ITransferObject>)companyBean.getList(null);
			if (companyList.size() > 0) {
				company = (Company)companyList.get(0);
			}
			if (company != null && company.getId() >= 0) {
				vu.put("company", company);
				vu.initialize();
	
				IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
				Criteria attachCriteria = new Criteria();
				attachCriteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
				attachCriteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.LOGO);
				List<ITransferObject> attachList = (List<ITransferObject>)attachBean.getList(attachCriteria);
				if (attachList.size() > 0) {
					RegistryAttachment ra = (RegistryAttachment)attachList.get(0);
					String extension = ra.getMimeType()==null?"jpg":ra.getMimeType().getExtension();
					String filename = "logo." + extension;
					if (ImageUtil.copyRegistryBlobToFile(ra, images_temporal_path, filename)) {
						vu.put("logo", filename);
					}
				}
		/*
		 * 		$content = home.vm {
		 *			$description - Descripcion comercial de la empresa (obligatorio - WARNING)
		 *			$slogan - Eslogan de la empresa (opcional)
		 *			$schedule - Horario comercial (opcional)
		 * 		}
		 */		
				IManagerBean webinfoBean = BeanManager.getManagerBean(WebInfo.class);
				Criteria webinfoCriteria = new Criteria();
				webinfoCriteria.addEqualExpression(webinfoBean.getFieldName(IWebInfoAlias.WEB_INFO_COMPANY_ID), company.getId());
				List<ITransferObject> webinfoList = (List<ITransferObject>)webinfoBean.getList(webinfoCriteria);
				if (webinfoList.size() > 0) {
					WebInfo wi = (WebInfo)webinfoList.get(0);
					String description = wi.getCommercialDescription();
					String slogan = wi.getSlogan();
					String schedule = wi.getSchedule();
					if (description != null) vu.put("description", description);
					if (slogan != null) vu.put("slogan", slogan);
					if (schedule != null) vu.put("schedule", schedule);
				}
				webinfoBean = null;

		/*
		 * 		Imagenes de Rattach {
		 *			$images - Todas las imagenes de rattach con sus thumbnails (tn_)
		 * 		}
		 */		

				ArrayList<ImageHandler> all_images = new ArrayList<ImageHandler>();
				attachCriteria = new Criteria();
				attachCriteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
				attachCriteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ADDITIONAL_IMAGE);
				attachList = (List<ITransferObject>)attachBean.getList(attachCriteria);
				for (int i=0; i<attachList.size(); i++) {
					RegistryAttachment ra = (RegistryAttachment)attachList.get(i);
					if ( (ra.getData() != null) && (!StringUtils.isEmpty(ra.getDescription())) ) {
						String filename = ra.getDescription() + "." + ra.getMimeType().getExtension();
						if (!ImageUtil.copyRegistryBlobToFile(ra, images_temporal_path, 200, 200, filename)) {
							AonUtil.addErrorMessage("ERROR: Se produjo un error al intentar copiar la imagen " + filename + "."); 
						}
						ImageHandler ih = new ImageHandler(filename, getPageName(ra.getDescription()), ra.getDescription());
						all_images.add(ih);
					}
				}
				vu.put("all_images", all_images);

		/*
		 * 		Sacar datos de contacto {
		 * 			$email - Email para el formulario de envio, si no existe no hay opcion de menu. 
		 * 		}
		 */
				Iterator<RegistryMedia> mediaList = company.getMedias().iterator();
				while (mediaList.hasNext()) {
					RegistryMedia m = (RegistryMedia)mediaList.next();
					if (m.getMediaType() == MediaType.EMAIL) {
						vu.put("email", m.getValue());
					}
					else if (m.getMediaType() == MediaType.WEB) {
						try {
							URL web = new URL(m.getValue());
							dominio = web.getHost();
							if (dominio.indexOf(".") != dominio.lastIndexOf(".")) {
								dominio = dominio.substring(dominio.indexOf(".") + 1);
							}
						} catch (MalformedURLException e) {
							AonUtil.addWarningMessage("WARNING: La url de la pagina web no tiene el formato correcto (http://www.midominio.com).");
							e.printStackTrace();
						}
					}
					else if (m.getMediaType() == MediaType.FIXED_PHONE) {
						vu.put("phone", m.getValue());
					}
					else if (m.getMediaType() == MediaType.FAX) {
						vu.put("fax", m.getValue());
					}
				}
				companyBean = null;
				attachBean = null;
		/*
		 * 		$content = address.vm {
		 *			$company - Datos de empresa
		 *			$addresses - Direcciones de la empresa (Google Maps)
		 * 		}
		 */
				RegistryAddress defaultAddress = company.getDefaultAddress();
				ArrayList<RegistryAddress> addresses = new ArrayList<RegistryAddress>();
				Iterator<RegistryAddress> addressList = company.getAddresses().iterator();
				while (addressList.hasNext()) {
					RegistryAddress a = (RegistryAddress)addressList.next();
					if (defaultAddress != null && a.getId() == defaultAddress.getId()) {
						//Nothing
					}
					else {
						addresses.add(a);
					}
				}
				if (addresses.size() > 0) vu.put("addresses", addresses);
				if (defaultAddress != null) vu.put("address", defaultAddress);
			}

			int homepage = 0;
			/* 
			 * Obtenemos si esta definida la pagina homepage seleccionada 
			 */
			try {
				IManagerBean apBean = BeanManager.getManagerBean(ApplicationParameter.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(apBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), HOMEPAGE_NAME_PARAM);
				List<ITransferObject> list = apBean.getList(criteria);
				if (list.size() > 0) {
					ApplicationParameter ap = (ApplicationParameter)list.get(0);
					homepage = Integer.parseInt(ap.getValue());
				}
			} catch (ManagerBeanException e) {}
			
			/* 
			 * Sacamos el menu de las paginas y cada una de las pagina.
			 */
			ArrayList<MenuOptionHandler> menu = new ArrayList<MenuOptionHandler>();
			MenuOptionHandler moh = new MenuOptionHandler("Inicio", "index.html");
			menu.add(moh);
			IManagerBean wimBean = BeanManager.getManagerBean(WebInfoPage.class);
			Criteria wimCriteria = new Criteria();
			wimCriteria.addEqualExpression(wimBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_ACTIVE), true);
			wimCriteria.addOrder(wimBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_POSITION));
			List<ITransferObject> wimList = (List<ITransferObject>)wimBean.getList(wimCriteria);
			for (int i=0;i < wimList.size();i++) {
				WebInfoPage wip = (WebInfoPage)wimList.get(i);
				int id = wip.getId();
				if (homepage != id) {
					String label = wip.getName();
					String link = wip.getName() + ".html";
					link = link.replaceAll(" ", "_");
					link = link.replaceAll("ñ", "n").replaceAll("á", "a").replaceAll("é", "e").replaceAll("í", "i").replaceAll("ó", "o").replaceAll("ú", "u");
					link = link.replaceAll("Ñ", "N").replaceAll("Á", "A").replaceAll("É", "E").replaceAll("Í", "I").replaceAll("Ó", "O").replaceAll("Ú", "U");
					moh = new MenuOptionHandler(label, link);
					menu.add(moh);
				}
			}
			vu.put("menu", menu);

			//Generar pagina principal
			vu.put("content", "home.vm");
			vu.generate("index.html");

			//Generar pagina de error 404
			vu.generate("404error.vm","404error.html");

			//Generar pagina de error 404
			vu.put("content", "mail.vm");
			vu.generate("mail.php");

			//Generar paginas segun menu.
			IManagerBean wipBean = BeanManager.getManagerBean(WebInfoPage.class);
			Criteria wipCriteria = new Criteria();
			wipCriteria.addEqualExpression(wipBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_ACTIVE), true);
			List<ITransferObject> wipList = (List<ITransferObject>)wipBean.getList(wipCriteria);
			for (int i=0;i < wipList.size();i++) {
				WebInfoPage wip = (WebInfoPage)wipList.get(i);
				int id = wip.getId();
				String pagename = wip.getName();
				boolean isIndex = false;
				if (homepage == id) {
					pagename = "index";
					isIndex = true;
				}
				if (wip.getType() == WebInfoPageType.CONTACT) generatePage("contact.vm", pagename );
				else if (wip.getType() == WebInfoPageType.GENERIC) generateGenericPage(wip, isIndex);
				else if (wip.getType() == WebInfoPageType.LOCATION) generateGenericPage(wip, isIndex);
				else if (wip.getType() == WebInfoPageType.GALLERY) generateGalleryPage(wip, isIndex);
			}

			//Parseamos los estilos
			IManagerBean wisBean = BeanManager.getManagerBean(WebInfoStyle.class);
			List<ITransferObject> wisList = (List<ITransferObject>)wisBean.getList(null);
			for (int i=0;i < wisList.size();i++) {
				WebInfoStyle wis = (WebInfoStyle)wisList.get(i);
				String name = wis.getVariable(); 
				WebInfoVariableType wivt = getVariableType(name);
				String value = wis.getValue();

				if (wivt == WebInfoVariableType.FONT) {
					value = getFontType(wis);
				}
				else {
					if (wivt == WebInfoVariableType.IMAGE) {
						value = getImage(wis);
						copyImageToCss(temporal_path, value);
					}
					else {
						if (wivt == WebInfoVariableType.BORDER || wivt == WebInfoVariableType.SIZE) {
							value = value + "px";
						}
					}
				}
				vu.put(name, value);
				vu.put(name.toLowerCase(), value);
			}
			vu.generateCSS();
			
			vu.copyDir(current_template_path + "/js", temporal_path + "/");
			vu.copyDir(current_template_path + "/css/images", temporal_path + "/css");
			vu.copyDir(current_template_path + "/css/img", temporal_path + "/css");
			vu.copyDir(current_template_path + "/images", temporal_path + "/");
			
			//Subimos por FTP
			try {
				if (dominio != null) {
					FTPUtil.uploadFTP(temporal_path, "/" + dominio + "/WEBSITES/www." + dominio + "/", "192.168.3.47");
					webPage = "http://www." + dominio + "/";
				}
				generate = true;
				AonUtil.addInfoMessage("OK: La web ha sido generada." );
			}
			catch (Exception e) {
				e.printStackTrace();
				AonUtil.addErrorMessage("ERROR: Se ha producido un error durante la publicacion de la pagina.");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			AonUtil.addErrorMessage("ERROR: Se ha producido un error durante la generacion de los contenidos.");
		} 
		finally {
			HibernateUtil.setCloseSession(true);
			HibernateUtil.closeSession();
		}
		
	}

	private void copyImageToCss(String temporal_path, String value) {
		try {
			vu.copyFile(temporal_path + "/images/"+value+"", temporal_path + "/css/images/"+value+"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void generateGenericPage(WebInfoPage wip, boolean isIndex) throws ManagerBeanException {
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
		if (wip.getType() == WebInfoPageType.LOCATION) template = "location.vm";
		else template = "generic" + wipd.getLayout().ordinal() + ".vm";
		vu.put("title", wipd.getTitle());
		vu.put("text", wipd.getContent());
		if (wip.getType() == WebInfoPageType.LOCATION) {
			//Primero miramos si el extra tiene |
			String extra = wipd.getExtra();
			if (extra.indexOf("|") >= 0) {
				int num = 0;
				StringTokenizer st = new StringTokenizer(extra, "|");
				while (st.hasMoreTokens()) {
					String coords = st.nextToken();
					if (num == 0) vu.put("coords", coords);
					else vu.put("coords"+num, coords);
					num++;
				}
			}
			else vu.put("coords", wipd.getExtra());
		}
		
		//Ahora las imagenes
		ArrayList<ImageHandler> images = new ArrayList<ImageHandler>();
		IManagerBean wiprBean = BeanManager.getManagerBean(WebInfoPageResource.class);
		Criteria wiprCriteria = new Criteria();
		wiprCriteria.addEqualExpression(wiprBean.getFieldName(IWebInfoAlias.WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE_ID), wip.getId());
		List<ITransferObject> wiprList = (List<ITransferObject>)wiprBean.getList(wiprCriteria);

		for (int i = 0; i < wiprList.size(); i++) {
			WebInfoPageResource wipr = (WebInfoPageResource)wiprList.get(i);
			ImageHandler ih = new ImageHandler(wipr.getRattach().getDescription() + "." + wipr.getRattach().getMimeType().getExtension(), getPageName(wipr.getRattach().getDescription()), wipr.getContent());
			images.add(ih);
		}
		vu.put("images", images);
		String pagename = wip.getName();
		if (isIndex) pagename = "index";
		generatePage(template, pagename);
		vu.remove("title");
		vu.remove("text");
		if (wip.getType() == WebInfoPageType.LOCATION) vu.remove("coords");
		vu.remove("images");
	}

	private void generateGalleryPage(WebInfoPage wip, boolean isIndex) throws ManagerBeanException {
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
			}
			else {
				WebInfoPageResource wiprn = (WebInfoPageResource)wiprList.get(i+1);
				next_link = getPageName(wiprn.getRattach().getDescription());
			}
			WebInfoPageResource wipr = (WebInfoPageResource)wiprList.get(i);
			//Por cada imagen se hace tambien una pagina
			ImageHandler ih = new ImageHandler(wipr.getRattach().getDescription() + "." + wipr.getRattach().getMimeType().getExtension(), getPageName(wipr.getRattach().getDescription()), wipr.getContent());
			images.add(ih);
			
			vu.put("image", ih);
			if (!primera) vu.put("previous", previous_link);
			if (!ultima) vu.put("next", next_link);
			vu.put("return", getPageName(wip.getName()));
			generatePage("imageview.vm", wipr.getRattach().getDescription());
			vu.remove("image");
			if (!primera) vu.remove("previous");
			if (!ultima) vu.remove("next");
			vu.remove("return");
			primera = false;
			previous_link = getPageName(wipr.getRattach().getDescription());
		}
		vu.put("gallery", images);
		
		String pagename = wip.getName();
		if (isIndex) pagename = "index";
		generatePage("gallery.vm", pagename);
		if (wip.getType() == WebInfoPageType.LOCATION) vu.remove("coords");
		vu.remove("gallery");
	}

	private void generatePage(String template, String name) {
		vu.put("pagename", name);
		String page = getPageName(name);
		vu.put("content", template);
		vu.generate(page);
	}

	private String getPageName(String name) {
		String page = name + ".html";
		page = page.replaceAll(" ", "_");
		page = page.replaceAll("ñ", "n").replaceAll("á", "a").replaceAll("é", "e").replaceAll("í", "i").replaceAll("ó", "o").replaceAll("ú", "u");
		page = page.replaceAll("Ñ", "N").replaceAll("Á", "A").replaceAll("É", "E").replaceAll("Í", "I").replaceAll("Ó", "O").replaceAll("Ú", "U");
		return page;
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
		try {
			if (Integer.parseInt(style.getValue()) == WebInfoFontType.ARIAL.ordinal()) {
				return WebInfoFontType.ARIAL.getValue();
			}
			if (Integer.parseInt(style.getValue()) == WebInfoFontType.TIMES.ordinal()) {
				return WebInfoFontType.TIMES.getValue();
			}
			if (Integer.parseInt(style.getValue()) == WebInfoFontType.TREBUCHET.ordinal()) {
				return WebInfoFontType.TREBUCHET.getValue();
			}
			if (Integer.parseInt(style.getValue()) == WebInfoFontType.VERDANA.ordinal()) {
				return WebInfoFontType.VERDANA.getValue();
			}

		} catch (NumberFormatException n) {
			n.printStackTrace();
		}
		return "Verdana";
	}

	public String getImage(WebInfoStyle style) {
		String filename = "blank.jpg";
		try {
			IManagerBean attachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Criteria attachCriteria = new Criteria();
			attachCriteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_ID), Integer.parseInt(style.getValue()));
			List<ITransferObject> attachList = (List<ITransferObject>)attachBean.getList(attachCriteria);
			if (attachList.size() > 0) {
				RegistryAttachment ra = (RegistryAttachment)attachList.get(0);
				filename = ra.getDescription() + "." + ra.getMimeType().getExtension();
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} catch (NumberFormatException n) {
		}
		return filename;
	}

	public boolean isGenerate() {
		return generate;
	}

	public String getWebPage() {
		return webPage;
	}


}
