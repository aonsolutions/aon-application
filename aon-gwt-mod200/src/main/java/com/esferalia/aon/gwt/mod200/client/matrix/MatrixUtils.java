package com.esferalia.aon.gwt.mod200.client.matrix;

import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod180;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;

public class MatrixUtils {

	public static void map(IFiscalModel from,FiscalModel to) {
		to.setId(from.getId());
		to.setAdministration(from.getAdministration());
		to.setModel(from.getModel());
		to.setYear(from.getYear());
		to.setPeriod(from.getPeriod());
		to.setReplacement(from.isReplacement());
		to.setDomain(from.getDomain());
		to.setFinance(from.getFinance());
		to.setStatus(from.getStatus());
		to.setComplementary(from.isComplementary());
		to.setDocument(from.getDocument());
		to.setSurname(from.getSurname());
		to.setName(from.getName());
	}
	
	public static void map(IFiscalModel from,Mod190 to) {
		to.setId(from.getId());
		to.setDomain(from.getDomain());
		to.setYear(from.getYear());
		to.setAdministration(from.getAdministration());
		to.setStatus(from.getStatus());
		to.setReplacement(from.isReplacement());
		to.setComplementary(from.isComplementary());
		to.setDocument(from.getDocument());
		to.setName(from.getName());
	}
	
	public static void map(IFiscalModel from,Mod180 to) {
		to.setId(from.getId());
		to.setDomain(from.getDomain());
		to.setYear(from.getYear());
		to.setAdministration(from.getAdministration());
		to.setStatus(from.getStatus());
		to.setReplacement(from.isReplacement());
		to.setComplementary(from.isComplementary());
		to.setDocument(from.getDocument());
		to.setName(from.getName());
	}

	public static void map(IFiscalModel from,Mod184 to) {
		to.setId(from.getId());
		to.setDomain(from.getDomain());
		to.setYear(from.getYear());
		to.setAdministration(from.getAdministration());
		to.setStatus(from.getStatus());
		to.setReplacement(from.isReplacement());
		to.setComplementary(from.isComplementary());
		to.setDocument(from.getDocument());
		to.setName(from.getName());
	}

	public static void map(IFiscalModel from,Mod193 to) {
		to.setId(from.getId());
		to.setDomain(from.getDomain());
		to.setYear(from.getYear());
		to.setAdministration(from.getAdministration());
		to.setStatus(from.getStatus());
		to.setReplacement(from.isReplacement());
		to.setComplementary(from.isComplementary());
		to.setDocument(from.getDocument());
		to.setName(from.getName());
	}

	public static void map(IFiscalModel from,Mod390 to) {
		to.setId(from.getId());
		to.setDomain(from.getDomain());
		to.setYear(from.getYear());
		to.setAdministration(from.getAdministration());
		to.setStatus(from.getStatus());
		to.setReplacement(from.isReplacement());
		to.setComplementary(from.isComplementary());
		to.setDocument(from.getDocument());
		to.setName(from.getName());
	}
	
	public static void map(IFiscalModel from,Mod349 to) {
		to.setId(from.getId());
		to.setDomain(from.getDomain());
		to.setYear(from.getYear());
		to.setAdministration(from.getAdministration());
		to.setStatus(from.getStatus());
		to.setReplacement(from.isReplacement());
		to.setComplementary(from.isComplementary());
		to.setDocument(from.getDocument());
		to.setName(from.getName());
	}

	public static void map(IFiscalModel from,Mod347 to) {
		to.setId(from.getId());
		to.setDomain(from.getDomain());
		to.setYear(from.getYear());
		to.setAdministration(from.getAdministration());
		to.setStatus(from.getStatus());
		to.setReplacement(from.isReplacement());
		to.setComplementary(from.isComplementary());
		to.setDocument(from.getDocument());
		to.setName(from.getName());
	}

}
