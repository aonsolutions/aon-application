package com.code.aon.ui.admin.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.audit.IModule;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class ModuleLinesController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Set<Module> getAvalaibleModules() throws ManagerBeanException {
		Set<Module> modules = new HashSet<Module>();
		modules.addAll( Arrays.asList(Module.values()) );
		for( ITransferObject to : getManagerBean().getList(getCriteria()) ) {
			IModule iModule = (IModule) to;
			modules.remove(iModule.getModule());
		}
		if (! isNevv() ) {
			IModule iModule = (IModule) getTo();
			modules.add(iModule.getModule());
		}
		return modules;
	}
	
	public List<SelectItem> getModules() throws ManagerBeanException {
		List<SelectItem> modules = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for (Module module : getAvalaibleModules()) {
			String name = module.getName(locale);		
			modules.add( new SelectItem(module, name) );
		}
		AonUtil.sortSelectItems(modules);
		return modules;
	}		
	
}