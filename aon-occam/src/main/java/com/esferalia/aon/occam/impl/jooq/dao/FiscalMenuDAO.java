	package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import org.jooq.impl.DSL;
import org.json.JSONArray;

import com.esferalia.aon.jooq.tables.AppParam;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.FiscalMenuItemJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.FiscalModelProperties;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalMenuDAO {
	
	private static final String PARAM_PREFIX = "FS_MODEL_CFG_";
	private static final String PARAM_PREFIX_LIKE = PARAM_PREFIX + "%";
	
	private FiscalMenuDAO() {
		
	}

	public static JSONArray  getDomainsModels(AONContext ctx, int domainId, FiscalMatrixParams params) {
		final JSONArray allModels = new JSONArray();
		Domain domain = null;
		if (params.getScope() == null) {
			domain = DomainDAO.getDomain(ctx, domainId);
		} else {
			domain = DomainDAO.getDomain(ctx, p ->
					p.getIdProperty().eq(domainId)
					.and( p.getScopeProperty().isNull().or(p.getScopeProperty().eq(params.getScope())) ));
		}
		if (domain != null && domain.getId() != null) {
			getDomainModels(ctx, domain, allModels, params);
		}
		return allModels;
	}
	
	private static JSONArray getDomainModels(AONContext ctx, final Domain domain, JSONArray allModels,FiscalMatrixParams params) {
		if (params.isConfiguredVisible()) {
			AppParam admonAppParam = APP_PARAM.as("admonAppParam");
			ctx.getDslContext()
				.select( APP_PARAM.NAME
						,APP_PARAM.VALUE
						,DOMAIN.ID
						,DOMAIN.DESCRIPTION
						,REGISTRY.DOCUMENT
						,REGISTRY.NAME
						,admonAppParam.VALUE
						)
				.from(APP_PARAM)
				.join(DOMAIN).on( APP_PARAM.DOMAIN.equal(DOMAIN.ID))
				.join(COMPANY).on( APP_PARAM.DOMAIN.equal(COMPANY.DOMAIN))
				.join(REGISTRY).on( REGISTRY.ID.equal(COMPANY.REGISTRY))
				.leftOuterJoin(admonAppParam).on( APP_PARAM.DOMAIN.equal(admonAppParam.DOMAIN)
						.and(admonAppParam.NAME.eq(com.esferalia.aon.occam.api.model.type.AppParam.FS_DEFAULT_ADMINISTRATION.toString())))
				.where(APP_PARAM.DOMAIN.equal(domain.getId()).or(DOMAIN.PARENT.equal(domain.getId())))
				.and(APP_PARAM.NAME.like( params.getModel()  == null ? PARAM_PREFIX_LIKE : PARAM_PREFIX + params.getModel().toString() + "%"))
				.and(params.getScope() == null?DSL.trueCondition():DOMAIN.SCOPE.eq( params.getScope()))
				.orderBy(APP_PARAM.NAME)
				.fetch()
				.stream()
				.filter(rec -> AonStringUtils.containsAny(rec.getValue(APP_PARAM.VALUE), "YQM"))
				.map( rec -> new FiscalModel()
						.setDomain(rec.getValue(DOMAIN.ID))
						.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
						.setAdministration( AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)))
						.setModel( fromAppParamName(rec.getValue(APP_PARAM.NAME)) )
						.setDocument(rec.getValue(REGISTRY.DOCUMENT))
						.setName(rec.getValue(REGISTRY.NAME))
						.setStatus( FiscalStatus.MISSING)
						.setPeriod("Y".equals(rec.getValue(APP_PARAM.VALUE))?Period.YEAR:"Q".equals(rec.getValue(APP_PARAM.VALUE))?Period.T1:Period.M01)
				)
				.map( FiscalMenuItemJSON::toJSON )
				.forEach( allModels::put )
				;
		}
		if (params.isMadeModelsVisible()) {
			IFiscalModelTypeVisitor visitor = new IFiscalModelTypeVisitor() {
				@Override public void visitM115() {}	// Resolved in visitM111()
				@Override public void visitM123() {}	// Resolved in visitM111()
				@Override public void visitM130() {}	// Resolved in visitM111()
				@Override public void visitM131() {}	// Resolved in visitM111()
				@Override public void visitM390HF() {}	// Resolved in visitM111()
				@Override public void visitM202() {}	// Resolved in visitM111()
				@Override public void visitM303() {}	// Resolved in visitM111()
				
				@Override 
				public void visitM111() {
					FiscalModelDAO.getMatrixRecords(ctx, domain.getId(), p -> getFilter(p, domain, params))
						.map(fm -> fm.setModel((fm.getModel() == FiscalModelType.M390)?FiscalModelType.M390_HF:fm.getModel())) 
						.filter(fm -> fm.getModel() != FiscalModelType.M390_HF 
							|| (fm.getModel() == FiscalModelType.M390_HF
								&& ( params.getModel() == null 
								  || params.getModel() == FiscalModelType.M390_HF))
								)
						.map( FiscalMenuItemJSON::toJSON )
						.forEach( allModels::put );
				}
/*
				@Override 
				public void visitM115() {
					com.esferalia.aon.occam.impl.jooq.dao.FiscalModelDAO.getMatrixRecords(ctx, domain.getId(), p -> getFilter(p, domain, params))
						.map(record -> {
							FiscalModel fm = com.esferalia.aon.occam.impl.jooq.dao.FiscalModelDAO.map(record);
							if (fm.getModel() == FiscalModelType.M390) {
								fm.setModel(FiscalModelType.M390_HF);
							}
							fm.setDomainName(record.get(DOMAIN.DESCRIPTION));
							return fm;
						})
						.filter(fm -> fm.getModel() != FiscalModelType.M390_HF 
							|| (fm.getModel() == FiscalModelType.M390_HF
								&& ( params.getModel() == null 
								  || params.getModel() == FiscalModelType.M390_HF))
								)
						.map( FiscalMenuItemJSON::toJSON )
						.forEach( allModels::put )
										;
				}
*/
				@Override 
				public void visitM347() {
					if (params.getModel() == null  || FiscalModelType.M347 == params.getModel()) {
						Mod347DAO.getHeaders(ctx, domain.getId(), params.getScope())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM349() {
					if (params.getModel() == null  || FiscalModelType.M349 == params.getModel()) {
						Mod349DAO.getHeaders(ctx, domain.getId(), params.getScope())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM390() {
					if (params.getModel()  == null  || FiscalModelType.M390 == params.getModel()) {
						Mod390DAO.getHeaders(ctx, domain.getId(), params.getScope())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM180() {
					if (params.getModel()  == null  || FiscalModelType.M180 == params.getModel()) {
						Mod180DAO.getHeaders(ctx, domain.getId(), params.getScope())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM184() {
					if (params.getModel()  == null  || FiscalModelType.M184 == params.getModel()) {
						Mod184DAO.getHeaders(ctx, domain.getId(), params.getScope())
						.filter( mod -> mod.getYear()== params.getYear())
						.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
						.map( FiscalMenuItemJSON::toJSON )
						.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM190() {
					if (params.getModel()  == null  || FiscalModelType.M190 == params.getModel()) {
						Mod190DAO.getHeaders(ctx, domain.getId(), params.getScope())
						.filter( mod -> mod.getYear()== params.getYear())
						.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
						.map( FiscalMenuItemJSON::toJSON )
						.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM193() {
					if (params.getModel()  == null  || FiscalModelType.M193.getName().equals(params.getModel())) {
						Mod193DAO.getHeaders(ctx, domain.getId(), params.getScope())
						.filter( mod -> mod.getYear()== params.getYear())
						.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
						.map( FiscalMenuItemJSON::toJSON )
						.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM200() {
					if (params.getModel() == null || FiscalModelType.M200 == params.getModel()) {
						Mod200DAO.getHeaders(ctx, domain.getId(), params.getScope())
						.filter( mod -> mod.getYear()== params.getYear())
						.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
						.map( FiscalMenuItemJSON::toJSON )
						.forEach( allModels::put );
					}
				}
			};
			for (FiscalModelType type : FiscalModelType.values()) {
				type.visit( visitor );
			}
		}
		return allModels;
	}
	
	private static FiscalModelType fromAppParamName( String name ) {
		String model = AonStringUtils.substringAfter(name,PARAM_PREFIX);
		FiscalModelType fmt = FiscalModelType.valueOf(model);
		if (fmt == FiscalModelType.M303_RG || fmt == FiscalModelType.M303_RS) {
			return FiscalModelType.M303;
		}
		return fmt;
	}


	public static Filter getFilter(FiscalModelProperties p, Domain domain, FiscalMatrixParams params) {
		Filter prop = p.getYearProperty().eq(params.getYear());
		if (domain.isParent()) {
			prop = prop.and( p.getParentDomainProperty().eq(domain.getId()));
		} else {
			prop = prop.and( p.getDomainProperty().eq(domain.getId()));
		}
		
		if (params.getAdministration() != null) {
			prop = prop.and( p.getAdministrationProperty().eq(params.getAdministration().value()));
		}
		if (params.getModel() != null) {
			prop = prop.and( p.getModelProperty().eq(params.getModel().getValue()));
		}
		if (params.getScope() != null) {
			prop = prop.and( p.getDomainScopeProperty().eq( params.getScope()));
		}
		return prop;
	}

}
