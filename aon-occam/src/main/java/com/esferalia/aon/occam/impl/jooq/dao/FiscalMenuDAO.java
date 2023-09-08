package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Record;
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
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.FiscalModelDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod180.Mod180DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod184.Mod184DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod190.Mod190DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod193.Mod193DAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod390.Mod390DAO;
import com.esferalia.aon.watson.util.AonEnumUtils;
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
			addConfiguredModels(ctx, domain, allModels,params);
		}
		if (params.isMadeModelsVisible()) {
			IFiscalModelTypeVisitor visitor = new IFiscalModelTypeVisitor() {
				@Override public void visitM115() {	/* Resolved in visitM111() */ }
				@Override public void visitM123() {	/* Resolved in visitM111() */ }
				@Override public void visitM130() {	/* Resolved in visitM111() */ }
				@Override public void visitM131() {	/* Resolved in visitM111() */ }
				@Override public void visitM390HF() {	/* Resolved in visitM111() */ }
				@Override public void visitM202() {	/* Resolved in visitM111() */ }
				@Override public void visitM303() {	/* Resolved in visitM111() */ }
				
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

				@Override 
				public void visitM347() {
					if (params.accept( FiscalModelType.M347 )) {
						Mod347DAO.getHeaders(ctx, domain.getId(), params.getScope())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) 
								|| AonStringUtils.containsIgnoreCase(params.getDeclared(), mod.getName()) )
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM349() {
					if (params.accept( FiscalModelType.M349 )) {
						Mod349DAO.getHeaders(ctx, domain.getId(), params.getScope())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) 
									|| AonStringUtils.containsIgnoreCase(params.getDeclared(), mod.getName()) )
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM390() {
					if (params.accept( FiscalModelType.M390)) {
						Mod390DAO.getHeaders(ctx, domain.getId(), params.getScope())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) 
									|| AonStringUtils.containsIgnoreCase(params.getDeclared(), mod.getName()) )
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM180() {
					if (params.accept( FiscalModelType.M180 )) {
						Mod180DAO.getHeaders(ctx, domain.getId(), params.getScope())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) 
									|| AonStringUtils.containsIgnoreCase(params.getDeclared(), mod.getName()) )
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM184() {
					if (params.accept( FiscalModelType.M184 )) {
						Mod184DAO.getHeaders(ctx, domain.getId(), params.getScope())
						.filter( mod -> mod.getYear()== params.getYear())
						.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
						.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) 
								|| AonStringUtils.containsIgnoreCase(params.getDeclared(), mod.getName()) )
						.map( FiscalMenuItemJSON::toJSON )
						.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM190() {
					if (params.accept( FiscalModelType.M190 )) {
						Mod190DAO.getHeaders(ctx, domain.getId(), params.getScope())
						.filter( mod -> mod.getYear()== params.getYear())
						.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
						.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) 
								|| AonStringUtils.containsIgnoreCase(params.getDeclared(), mod.getName()) )
						.map( FiscalMenuItemJSON::toJSON )
						.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM193() {
					if (params.accept( FiscalModelType.M193 )) {
						Mod193DAO.getHeaders(ctx, domain.getId(), params.getScope())
						.filter( mod -> mod.getYear()== params.getYear())
						.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
						.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) 
								|| AonStringUtils.containsIgnoreCase(params.getDeclared(), mod.getName()) )
						.map( FiscalMenuItemJSON::toJSON )
						.forEach( allModels::put );
					}
				}
				@Override 
				public void visitM200() {
					if (params.accept( FiscalModelType.M200 )) {
						//Mod200DAO.getHeaders(ctx, domain.getId(), params.getScope())
						getHeadersMod200(ctx, domain.getId(), params.getScope())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) 
									|| AonStringUtils.containsIgnoreCase(params.getDeclared(), mod.getName()) )
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
	
	private static void addConfiguredModels(AONContext ctx, final Domain domain, JSONArray allModels,FiscalMatrixParams params) {
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
			.and(DOMAIN.PARENT.isNotNull())
			.and(DOMAIN.ACTIVE.eq((byte) 1))
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
		if (AonStringUtils.isNotBlank(params.getDeclared())) {
			prop = prop.and( p.getNameProperty().like( "%"+params.getDeclared()+"%"));
		}
			
		return prop;
	}
	
	private static Stream<FiscalModel> getHeadersMod200(AONContext ctx, int domain, Integer scope) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL200.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MODEL200)
			.join(DOMAIN).on(FS_MODEL200.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL200.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.and( scope == null ? DSL.trueCondition() : DOMAIN.SCOPE.equal(scope))
			.orderBy(FS_MODEL200.YEAR.desc(),FS_MODEL200.NAME.asc())
			.fetch()
			.stream()
			.map( new Mod200Filler() )
			;
	}
	
	private static class Mod200Filler implements Function<Record,FiscalModel> {

		@Override
		public FiscalModel apply(Record record) {
			return new FiscalModel() 
				.setId(record.getValue(FS_MODEL200.ID))
				.setDomain(record.getValue(FS_MODEL200.DOMAIN))
				.setDomainName(record.getValue(DOMAIN.DESCRIPTION))
				.setYear(record.getValue(FS_MODEL200.YEAR))
				.setAdministration(Administration.safeValueOf(record.getValue(FS_MODEL200.ADMINISTRATION)))
				
				// TODO - Support
				.setStatus( FiscalStatus.PENDING )
				
				// TODO - Support
				.setFinance(null)
				
				.setComplementary( AonEnumUtils.getBoolean( record.getValue(FS_MODEL200.COMPLEMENTARY)))
				.setReplacement( false )
				.setNumber(record.getValue(FS_MODEL200.RECEIPT ))
				.setReplacedNumber(record.getValue(FS_MODEL200.COMPLEMENTARY_RECEIPT))
				.setComments(record.getValue(FS_MODEL200.COMMENTS ))
				.setDocument(record.getValue(FS_MODEL200.DOCUMENT ))
				.setName(record.getValue(FS_MODEL200.NAME))
//				.setResultType(record.getValue(FS_MODEL200.RESULT_TYPE))
//				.setResult(record.getValue(FS_MODEL200.AMOUNT) == null? 0.0 : record.getValue(FS_MODEL200.AMOUNT) )
				.setDeclarationResultType(FiscalModelDeclarationType.safeValueOf(record.getValue(FS_MODEL200.RESULT_TYPE)))
				.setDeclarationResult(record.getValue(FS_MODEL200.AMOUNT) == null? 0.0 : record.getValue(FS_MODEL200.AMOUNT) )

				// TODO - Support
				.setCreationUser(null)
				.setCreationDate(null)
				.setModificationUser(null)
				.setModificationDate(null)
				
				.setModel(FiscalModelType.M200)
				.setPeriod(Period.YEAR)
			;
		}
	}



}
