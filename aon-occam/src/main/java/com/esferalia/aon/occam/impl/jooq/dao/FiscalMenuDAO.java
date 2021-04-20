package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.json.FiscalMenuItemJSON;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.FiscalModelProperties;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;

public class FiscalMenuDAO {
	
	public static JSONArray  getDomainsModels(AONContext ctx, int domainId, FiscalMatrixParams params) {
		final JSONArray domainsArray = new JSONArray();
		Domain domain = DomainDAO.getDomain(ctx, domainId);
		if (domain.isParent()) {
			LinkedList<Domain> domains = DomainDAO.getDomainList(ctx, p-> p.getParentProperty().eq( domainId));
			for (Domain d : domains) {
				JSONObject domainJSON = DomainJSON.toJSON(d);
				JSONArray models = getDomainModels(ctx, d.getId(), params);
				if (models != null && !models.isEmpty() ) {
					domainJSON.put(IJsonNames.MODELS, models);
					domainsArray.put(domainJSON);	
				}
			}
		} else {
			JSONObject domainJSON = DomainJSON.toJSON(domain);
			JSONArray models = getDomainModels(ctx, domain.getId(), params);
			if (models != null && !models.isEmpty() ) {
				domainJSON.put(IJsonNames.MODELS, models);
				domainsArray.put(domainJSON);	
			}
		}
		return domainsArray;
	}
	
	private static JSONArray getDomainModels(AONContext ctx, int domainId, FiscalMatrixParams params) {
		final JSONArray arr = new JSONArray();
		IFiscalModelTypeVisitor visitor = new IFiscalModelTypeVisitor() {
			@Override 
			public void visitM111() {
				Mod111DAO.getMod111s(ctx, domainId, p -> getFilter(p, params)).forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM115() {
				Mod115DAO.getMod115s(ctx, domainId, p -> getFilter(p, params)).forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM123() {
				Mod123DAO.getMod123s(ctx, domainId, p -> getFilter(p, params)).forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM130() {
				Mod130DAO.getMod130s(ctx, domainId, p -> getFilter(p, params)).forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM131() {
				Mod131DAO.getMod131s(ctx, domainId, p -> getFilter(p, params)).forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM347() {
				Mod347DAO.getHeaders(ctx, domainId)
					.filter( mod -> mod.getYear()== params.getYear())
					.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
					.forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM349() {
				Mod349DAO.getHeaders(ctx, domainId)
					.filter( mod -> mod.getYear()== params.getYear())
					.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
					.forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM390() {
				Mod390DAO.getHeaders(ctx, domainId)
					.filter( mod -> mod.getYear()== params.getYear())
					.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
					.forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM390HF() {
				Mod390HFDAO.getMod390HFs(ctx, domainId, p -> getFilter(p, params)).forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM180() {
				Mod180DAO.getHeaders(ctx, domainId)
					.filter( mod -> mod.getYear()== params.getYear())
					.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
					.forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM184() {
				Mod184DAO.getHeaders(ctx, domainId)
					.filter( mod -> mod.getYear()== params.getYear())
					.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
					.forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM190() {
				Mod190DAO.getHeaders(ctx, domainId)
					.filter( mod -> mod.getYear()== params.getYear())
					.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
					.forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM193() {
				Mod193DAO.getHeaders(ctx, domainId)
					.filter( mod -> mod.getYear()== params.getYear())
					.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
					.forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM200() {
				Mod200DAO.getMod200s(ctx, domainId)
					.filter( mod -> mod.getYear()== params.getYear())
					.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
					.forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM202() {
				Mod202DAO.getMod202s(ctx, domainId, p -> getFilter(p, params)).forEach(mod -> serialize(mod) );
			}
			@Override 
			public void visitM303() {
				Mod303DAO.getMod303s(ctx, domainId, p -> getFilter(p, params)).forEach(mod -> serialize(mod) );
			}
			
			private void serialize(IFiscalModel model) {
				arr.put(FiscalMenuItemJSON.toJSON(model).toString());
			}
		};
		for (FiscalModelType type : FiscalModelType.values()) {
			type.visit( visitor );
		}
		return arr;
	}

	public static Filter getFilter(FiscalModelProperties p, FiscalMatrixParams params) {
		Filter prop = p.getYearProperty().eq(params.getYear());
		if (params.getAdministration() != null) {
			prop = prop.and( p.getAdministrationProperty().eq(params.getAdministration().getValue()));
		}
		if (params.getModel() != null) {
			prop = prop.and( p.getModelProperty().eq(params.getModel()));
		}
		return prop;
	}

}
