package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.util.LinkedList;

import org.jooq.conf.ParamType;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.FiscalMenuItemJSON;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.Properties.FiscalModelProperties;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalMenuDAO {
	
	private static final String PARAM_PREFIX = "FS_MODEL_CFG_";
	private static final String PARAM_PREFIX_LIKE = PARAM_PREFIX + "%";

	public static JSONArray  getDomainsModels(AONContext ctx, int domainId, FiscalMatrixParams params) {
		final JSONArray allModels = new JSONArray();
		Domain domain = DomainDAO.getDomain(ctx, domainId);
		if (domain.isParent()) {
			LinkedList<Domain> domains = DomainDAO.getDomainList(ctx, p-> p.getParentProperty().eq( domainId));
			for (Domain d : domains) {
				getDomainModels(ctx, d, allModels, params);
			}
		} else {
			getDomainModels(ctx, domain, allModels, params);
		}
		return allModels;
	}
	
	private static JSONArray getDomainModels(AONContext ctx, final Domain domain, JSONArray allModels,FiscalMatrixParams params) {
		if (params.isMadeModelsVisible()) {
			IFiscalModelTypeVisitor visitor = new IFiscalModelTypeVisitor() {
				@Override 
				public void visitM111() {
					Mod111DAO.getMod111s(ctx, domain.getId(), p -> getFilter(p, params)).forEach(mod -> allModels.put( serialize(mod,domain) ) );
				}
				@Override 
				public void visitM115() {
					Mod115DAO.getMod115s(ctx, domain.getId(), p -> getFilter(p, params)).forEach(mod -> allModels.put( serialize(mod,domain) ) );
				}
				@Override 
				public void visitM123() {
					Mod123DAO.getMod123s(ctx, domain.getId(), p -> getFilter(p, params)).forEach(mod -> allModels.put( serialize(mod,domain) ) );
				}
				@Override 
				public void visitM130() {
					Mod130DAO.getMod130s(ctx, domain.getId(), p -> getFilter(p, params)).forEach(mod -> allModels.put( serialize(mod,domain) ) );
				}
				@Override 
				public void visitM131() {
					Mod131DAO.getMod131s(ctx, domain.getId(), p -> getFilter(p, params)).forEach(mod -> allModels.put( serialize(mod,domain) ) );
				}
				@Override 
				public void visitM347() {
					if (AonStringUtils.isBlank( params.getModel()) || FiscalModelType.M347.getValue().equals(params.getModel())) {
						Mod347DAO.getHeaders(ctx, domain.getId())
						.filter( mod -> mod.getYear()== params.getYear())
						.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
						.forEach(mod -> allModels.put( serialize(mod,domain) ) );
					}
				}
				@Override 
				public void visitM349() {
					if (AonStringUtils.isBlank( params.getModel()) || FiscalModelType.M349.getValue().equals(params.getModel())) {
						Mod349DAO.getHeaders(ctx, domain.getId())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.forEach(mod -> allModels.put( serialize(mod,domain) ) );
					}
				}
				@Override 
				public void visitM390() {
					if (AonStringUtils.isBlank( params.getModel()) || FiscalModelType.M390.getValue().equals(params.getModel())) {
						Mod390DAO.getHeaders(ctx, domain.getId())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.forEach(mod -> allModels.put( serialize(mod,domain) ) );
					}
				}
				@Override 
				public void visitM390HF() {
					Mod390HFDAO.getMod390HFs(ctx, domain.getId(), p -> getFilter(p, params))
						.peek(mod -> mod.setModel( FiscalModelType.M390_HF))
						.forEach(mod -> allModels.put( serialize(mod,domain) ) );
				}
				@Override 
				public void visitM180() {
					if (AonStringUtils.isBlank( params.getModel()) || FiscalModelType.M180.getValue().equals(params.getModel())) {
						Mod180DAO.getHeaders(ctx, domain.getId())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.forEach(mod -> allModels.put( serialize(mod,domain) ) );
					}
				}
				@Override 
				public void visitM184() {
					if (AonStringUtils.isBlank( params.getModel()) || FiscalModelType.M184.getValue().equals(params.getModel())) {
						Mod184DAO.getHeaders(ctx, domain.getId())
						.filter( mod -> mod.getYear()== params.getYear())
						.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
						.forEach(mod -> allModels.put( serialize(mod,domain) ) );
					}
				}
				@Override 
				public void visitM190() {
					if (AonStringUtils.isBlank( params.getModel()) || FiscalModelType.M190.getValue().equals(params.getModel())) {
						Mod190DAO.getHeaders(ctx, domain.getId())
						.filter( mod -> mod.getYear()== params.getYear())
						.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
						.forEach(mod -> allModels.put( serialize(mod,domain) ) );
					}
				}
				@Override 
				public void visitM193() {
					if (AonStringUtils.isBlank( params.getModel()) || FiscalModelType.M193.getValue().equals(params.getModel())) {
						Mod193DAO.getHeaders(ctx, domain.getId())
						.filter( mod -> mod.getYear()== params.getYear())
						.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
						.forEach(mod -> allModels.put( serialize(mod,domain) ) );
					}
				}
				@Override 
				public void visitM200() {
					if (AonStringUtils.isBlank( params.getModel()) || FiscalModelType.M200.getValue().equals(params.getModel())) {
						Mod200DAO.getMod200s(ctx, domain.getId())
						.filter( mod -> mod.getYear()== params.getYear())
						.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
						.forEach(mod -> allModels.put( serialize(mod,domain) ) );
					}
				}
				@Override 
				public void visitM202() {
					Mod202DAO.getMod202s(ctx, domain.getId(), p -> getFilter(p, params)).forEach(mod -> allModels.put( serialize(mod,domain) ) );
				}
				@Override 
				public void visitM303() {
					Mod303DAO.getMod303s(ctx, domain.getId(), p -> getFilter(p, params)).forEach(mod -> allModels.put( serialize(mod,domain) ) );
				}
			};
			for (FiscalModelType type : FiscalModelType.values()) {
				type.visit( visitor );
			}
		}
		if (params.isConfiguredVisible()) {
				FiscalParameters fiscalParams = AppParamDAO.getFiscalParameters(ctx);
				System.out.println(
						
						ctx.getDslContext()
						.select( APP_PARAM.NAME
								,APP_PARAM.VALUE
								,DOMAIN.ID
								,DOMAIN.DESCRIPTION)
						.from(APP_PARAM)
						.join(DOMAIN).on( APP_PARAM.DOMAIN.equal(DOMAIN.ID))
						.where(DOMAIN.PARENT.equal(domain.getId()))
							.and(DOMAIN.SCOPE.isNull().or(SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE)))
							.and(APP_PARAM.NAME.like(PARAM_PREFIX_LIKE))
						.orderBy(APP_PARAM.NAME)
						.getSQL(ParamType.INLINED)
						);
				ctx.getDslContext()
					.select( APP_PARAM.NAME
							,APP_PARAM.VALUE
							,DOMAIN.ID
							,DOMAIN.DESCRIPTION)
					.from(APP_PARAM)
					.join(DOMAIN).on( APP_PARAM.DOMAIN.equal(DOMAIN.ID))
					.where(DOMAIN.ID.equal(domain.getId()))
					.and(APP_PARAM.NAME.like( AonStringUtils.isBlank( params.getModel()) ? PARAM_PREFIX_LIKE : PARAM_PREFIX + FiscalModelType.safeValueOf( params.getModel()).toString() + "%"))
					
					.orderBy(APP_PARAM.NAME)
					.fetch()
					.stream()
					.filter(rec -> AonStringUtils.containsAny(rec.getValue(APP_PARAM.VALUE), "YQM"))
					.map( rec -> new FiscalModel()
							.setDomain(rec.getValue(DOMAIN.ID))
							.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
							.setAdministration(fiscalParams.getAdministration(Administration.UNKNOWN)) 
							.setModel( fromAppParamName(rec.getValue(APP_PARAM.NAME)) )
							.setName(rec.getValue(DOMAIN.DESCRIPTION))
							.setStatus( FiscalStatus.MISSING)
							.setPeriod("Y".equals(rec.getValue(APP_PARAM.VALUE))?Period.YEAR:"Q".equals(rec.getValue(APP_PARAM.VALUE))?Period.T1:Period.M01)
						 )
					.map( mod -> serialize(mod,domain) )
					.peek(json -> System.out.println( json.toString(1)) )
					.forEach(json -> allModels.put( json ) )
					;
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

	private static JSONObject serialize(IFiscalModel model, Domain domain) {
		JSONObject m = FiscalMenuItemJSON.toJSON(model) ;
		m.put(IJsonNames.DOMAIN_NAME,  domain.getDescription());
		return m;
	}

	public static Filter getFilter(FiscalModelProperties p, FiscalMatrixParams params) {
		Filter prop = p.getYearProperty().eq(params.getYear());
		if (params.getAdministration() != null) {
			prop = prop.and( p.getAdministrationProperty().eq(params.getAdministration().getValue()));
		}
		if (params.getModel() != null && AonStringUtils.isNotBlank( params.getModel())) {
			prop = prop.and( p.getModelProperty().eq(params.getModel()));
		}
		return prop;
	}

}
