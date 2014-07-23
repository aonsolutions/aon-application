package com.code.aon.ui.fiscal.controller;

import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.jooq.tools.StringUtils;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.fiscal.config.Model;
import com.code.aon.fiscal.config.ModelConfig;
import com.code.aon.fiscal.config.ModelManager;
import com.code.aon.fiscal.config.ModelManagerParams;
import com.code.aon.ui.util.AonUtil;

public class FiscalMatrix {
	
	private static List<SelectItem> MODELS;
	static {
		MODELS = new LinkedList<SelectItem>();
		MODELS.add(new SelectItem(Model.M111));
		MODELS.add(new SelectItem(Model.M115)); 
		MODELS.add(new SelectItem(Model.M123)); 
		MODELS.add(new SelectItem(Model.M130));
		MODELS.add(new SelectItem(Model.M131));
		MODELS.add(new SelectItem(Model.M303_RG));
		MODELS.add(new SelectItem(Model.M303_RS));
		MODELS.add(new SelectItem(Model.M340));
		MODELS.add(new SelectItem(Model.M347));
		MODELS.add(new SelectItem(Model.M349));
		MODELS.add(new SelectItem(Model.M390));
		MODELS.add(new SelectItem(Model.M390_HF));
		MODELS.add(new SelectItem(Model.M180));
		MODELS.add(new SelectItem(Model.M190));
		MODELS.add(new SelectItem(Model.M200));
	}
	private ModelManagerParams params;
	private List<ModelConfig> list;
	
	public ModelManagerParams getParams() {
		return params;
	}
	public List<SelectItem> getModels() {
		return FiscalMatrix.MODELS;
	}
	public void onSearch(ActionEvent event) {
		int domainId = DomainManager.getCurrentDomain();
		params = new ModelManagerParams(domainId);
		params.setYear(2013);
		onRefresh(event);
	}
	
	public void onRefresh(ActionEvent event) {
		list = new LinkedList<ModelConfig>();
		ModelManager mm = new ModelManager();
		Connection c = null;
		try {
			c = DatabaseUtil.getConnection(AonUtil.getDomainName());
			list = mm.getModelsPanel(c, getParams());
			decorateList(list);
		} catch (Throwable e) {
			AonUtil.addErrorMessage("No se pudo mostrar el informe. ["+ e.getMessage()+"]");
			throw new AbortProcessingException(e);
		} finally {
			DatabaseUtil.closeQuietly(c);
		}
	}
	
	public boolean isDomainNameInOneRow() {
		return (getParams().getModel() == null);
	}
	private void decorateList(List<ModelConfig> toDecorate) {
		Comparator<ModelConfig> comparator = new Comparator<ModelConfig>() {
			
			@Override
			public int compare(ModelConfig mc1, ModelConfig mc2) {
				if (mc1 == null && mc2 == null) return 0;
				if (mc1 == null && mc2 != null) return -1;
				if (mc1 != null && mc2 == null) return 1;
				int ret = (mc1.getDomainName().compareTo(mc2.getDomainName())); 
				if (ret != 0) return ret;
				return (mc1.getModel().getName().compareTo(mc2.getModel().getName()));
			}
		};
		Collections.sort(toDecorate, comparator);
		String domainName = null;
		for (ModelConfig mc : toDecorate) {
			if (!StringUtils.equals(domainName, mc.getDomainName())) {
				domainName = mc.getDomainName();
			} else {
				mc.setDomainName(null);
			}
			
		}
	}

	public List<ModelConfig> getList() {
		return list;
	}
}
