package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.CommonGenerator;
import com.code.aon.ui.cms.velocity.FaqGenerator;
import com.code.aon.ui.cms.velocity.GenericGenerator;
import com.code.aon.ui.cms.velocity.HomepageGenerator;
import com.code.aon.ui.cms.velocity.LinkGenerator;
import com.code.aon.ui.cms.velocity.MenuGenerator;
import com.code.aon.ui.form.BasicController;

public class GeneratorController extends BasicController implements Constants {

	private VelocityUtil vu = new VelocityUtil();
	
	public void onGenerate(ActionEvent event) throws ManagerBeanException {

		vu.addMessage("Iniciando proceso de generación", VelocityUtil.INFO);
		vu.addMessage("", VelocityUtil.INFO);
		//Indicamos el directorio del template
		String template_path = ControllerUtil.getCurrentVmTemplatePath();
		vu.addMessage("Buscando plantilla seleccionada '" + ControllerUtil.getCurrentConfig().getTemplate() + "' ...", VelocityUtil.INFO);
		vu.setTemplate_path(template_path);
		vu.initialize();

		//Cargar datos comunes a todas las paginas
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Cargando configuraciones comunes en el contexto  '" + ControllerUtil.getCurrentConfig().getTemplate() + "' ...", VelocityUtil.INFO);
		CommonGenerator.chargeContext(vu);
		
		//Generar index.php de seleccion automatica de idioma
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando página de seleccion de idioma... ", VelocityUtil.INFO);
		CommonGenerator.generateLanguagePage(vu);

		//Generar index.html del idioma seleccionado
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando página de inicio (homepage)... ", VelocityUtil.INFO);
		HomepageGenerator.generate(vu);

		//Generar menus
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando páginas de menú... ", VelocityUtil.INFO);
		MenuGenerator.generate(vu);

		//Generar generic
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando páginas genéricas... ", VelocityUtil.INFO);
		GenericGenerator.generate(vu);
		
		//Generar faq
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando faq... ", VelocityUtil.INFO);
		FaqGenerator.generate(vu);
		
		//Generar link
		vu.addMessage("", VelocityUtil.INFO);
		vu.addMessage("Creando link... ", VelocityUtil.INFO);
		LinkGenerator.generate(vu);
	}


}
