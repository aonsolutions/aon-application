package com.code.aon.webinfo.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.company.Company;
import com.code.aon.company.WebInfo;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.webinfo.util.FTPUtil;
import com.code.aon.webinfo.util.ImageUtil;
import com.code.aon.webinfo.util.VelocityUtil;
import com.code.aon.webinfo.util.ZipUtil;
import com.code.aon.webinfo.velocity.VelocityConstants;

public class GeneratorController extends BasicController implements VelocityConstants  {

	private VelocityUtil vu = new VelocityUtil();
	
	private boolean generate = false;
	
	private String webPage = "";
	
	public void onGenerate(ActionEvent event) throws ManagerBeanException {

		vu = new VelocityUtil();
		vu.addMessage("Iniciando proceso de generación", VelocityUtil.INFO);
		vu.addMessage("", VelocityUtil.INFO);
		//Añadimos al contexto todo lo necesario para las paginas
/*
 * 		index.vm {
 * 			#include $content
 * 			$logo - Logo de la empresa.
 * 		}
 */
		//Obtenemos datos comunes a todas las paginas company y el logo
		//Indicamos el directorio del template
		String dominio = null;
		String template_path = TEMPLATE_PATH;
		vu.setTemplate_path(template_path);
		String temporal_path = TEMPORAL_PATH + "/" + System.currentTimeMillis() + "";
		File f = new File(temporal_path);
		f.mkdirs();
		vu.setTemporal_path(temporal_path);

		HibernateUtil.setCloseSession(false);
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
				String filename = "logo." + ra.getMimeType().getExtension();
				if (ImageUtil.copyRegistryBlobToFile(ra, temporal_path, filename, null)) {
					vu.put("logo", filename);
				}
			}
	/*
	 * 		$content = home.vm {
	 *			$description - Descripcion comercial de la empresa (obligatorio - WARNING)
	 *			$slogan - Eslogan de la empresa (opcional)
	 *			$schedule - Horario comercial (opcional)
	 *			$images - Imagenes de la empresa (opcional)
	 * 		}
	 */		
			IManagerBean webinfoBean = BeanManager.getManagerBean(WebInfo.class);
			Criteria webinfoCriteria = new Criteria();
			webinfoCriteria.addEqualExpression(webinfoBean.getFieldName(ICompanyAlias.WEB_INFO_COMPANY_ID), company.getId());
			List<ITransferObject> webinfoList = (List<ITransferObject>)webinfoBean.getList(webinfoCriteria);
			if (webinfoList.size() > 0) {
				WebInfo wi = (WebInfo)webinfoList.get(0);
				String description = wi.getCommercialDescription();
				String slogan = wi.getSlogan();
				String schedule = wi.getSchedule();
				String title = wi.getTitle();
				String content = wi.getContent();
				if (description != null) vu.put("description", description);
				if (slogan != null) vu.put("slogan", slogan);
				if (schedule != null) vu.put("schedule", schedule);
				if (title != null && !title.equals("")) vu.put("extra_title", title);
				if (content != null) vu.put("extra_content", content);
			}

			ArrayList<String> images = new ArrayList<String>();
			attachCriteria = new Criteria();
			attachCriteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
			attachCriteria.addEqualExpression(attachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ADDITIONAL_IMAGE);
			attachList = (List<ITransferObject>)attachBean.getList(attachCriteria);
			for (int i=0; i<attachList.size(); i++) {
				RegistryAttachment ra = (RegistryAttachment)attachList.get(i);
				String filename = ra.getDescription() + "." + ra.getMimeType().getExtension();
				if (ImageUtil.copyRegistryBlobToFile(ra, temporal_path, 267, 200, filename, "" + (i + 1))) {
					images.add("foto" + (i + 1) + ".jpg");
				}
			}
			if (images.size() > 0) vu.put("images", images);

	/*
	 * 		$content = contact.vm {
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
						e.printStackTrace();
					}
				}
				else if (m.getMediaType() == MediaType.FIXED_PHONE) {
					vu.put("phone", m.getValue());
				}
			}

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

		//Generar pagina principal
		vu.put("content", "home.vm");
		vu.generate("index.html");
		
		//Generar pagina de contacto
		vu.put("content", "contact.vm");
		vu.generate("contacto.html");
		vu.put("content", "mailok.vm");
		vu.generate("mailok.html");
		vu.put("content", "mailerror.vm");
		vu.generate("mailerror.html");

		//Generar pagina de localizacion
		vu.put("content", "address.vm");
		vu.generate("localizacion.html");

		vu.put("content", "extra.vm");
		vu.generate("extra.html");

		//Descomprimir resources en el directorio temporal
		ZipUtil.uncompressZipFile(vu.getTemplate_path() + "/resources.zip", vu.getTemporal_path(), null);
		
		//Subir por FTP
		if (dominio != null) {
			FTPUtil.uploadFTP(temporal_path, "/" + dominio + "/WEBSITES/www." + dominio + "");
			webPage = "http://www." + dominio + "/";
		}
		
		HibernateUtil.setCloseSession(true);
		generate = true;
	}

	public boolean isGenerate() {
		return generate;
	}

	public String getWebPage() {
		return webPage;
	}


}
