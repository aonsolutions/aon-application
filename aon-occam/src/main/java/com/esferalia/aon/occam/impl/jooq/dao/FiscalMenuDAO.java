package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.FsModel200.FS_MODEL200;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.jooq.Record;
import org.json.JSONArray;

import com.esferalia.aon.jooq.tables.AppParam;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.FiscalMenuItemJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.FiscalModelProperties;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelTypeVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalMatrixParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.security.Scope;
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
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalMenuDAO {
	
	private static final String PARAM_PREFIX = "FS_MODEL_CFG_";
	private static final String PARAM_PREFIX_LIKE = PARAM_PREFIX + "%";
	private static final Logger LOGGER  = Logger.getLogger(FiscalMenuDAO.class.getName());
	
	private FiscalMenuDAO() {
		
	}

	public static JSONArray  getDomainsModels(AONContext ctx, int domainId, FiscalMatrixParams params) {
		final JSONArray allModels = new JSONArray();
		Domain domain = null;
		domain = DomainDAO.getDomain(ctx, domainId);
		if (domain != null && domain.getId() != null) {
			getDomainModels(ctx, domain, allModels, params);
		}
		return allModels;
	}
	
	private static JSONArray getDomainModels(AONContext ctx, final Domain domain, JSONArray allModels,FiscalMatrixParams params) {
		LOGGER.info("FiscalMenuDAO getDomainModels BEGIN");
		LOGGER.info("FiscalMenuDAO getDomainModels AonContext " + ctx.getDomainName() + " " + ctx.getUser());
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
				@Override public void visitM421() {	/* Resolved in visitM111() */ }
								
				@Override 
				public void visitM111() {
					FiscalModelDAO.getMatrixRecords(ctx, domain.getId(), p -> getFilter(p, domain, params))
						.map(fm -> fm.setModel((fm.getModel() == FiscalModelType.M390)?FiscalModelType.M390_HF:fm.getModel()))
						.filter( fm -> fm.getModel() != FiscalModelType.M349 )  // No se coge de fs_model el modelo 349, pues no todos los datos están actualizados, se coge más abajo de su tabla
						.filter( fm -> fm.getModel() != FiscalModelType.M200 )  // Tampoco se coge el modelo 200 de fs_model
						.filter( fm -> fm.getModel() != FiscalModelType.M369 )  // Tampoco se coge el modelo 369 de fs_model
						.filter( fm -> fm.getModel() != FiscalModelType.M390_HF || (fm.getModel() == FiscalModelType.M390_HF && (params.getModel() == null || params.getModel() == FiscalModelType.M390_HF)) )
						.filter( fm -> checkScope(ctx, fm, params.getScope()) )
						.map( FiscalMenuItemJSON::toJSON )
						.forEach( allModels::put );
					LOGGER.info("FiscalMenuDAO getDomainModels visitM111");
				}
				
				@Override 
				public void visitM347() {
					if (params.accept( FiscalModelType.M347 )) {
						Mod347DAO.getHeaders(ctx, domain.getId())						
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getName(), params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getDocument(), params.getDeclared()) )
							.filter( mod -> params.getStatus() == null || mod.getStatus() == params.getStatus())
							.filter( mod -> params.getPeriod() == null || mod.getPeriod() == params.getPeriod())
							.filter( mod -> checkScope(ctx, mod, params.getScope()) )
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
						LOGGER.info("FiscalMenuDAO getDomainModels visitM347");
					}
				}
				@Override 
				public void visitM349() {
					if (params.accept( FiscalModelType.M349 )) {
						Mod349DAO.getHeaders(ctx, domain.getId())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getName(), params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getDocument(), params.getDeclared()) )
							.filter( mod -> params.getStatus() == null || mod.getStatus() == params.getStatus())
							.filter( mod -> params.getPeriod() == null || mod.getPeriod() == params.getPeriod())
							.filter( mod -> checkScope(ctx, mod, params.getScope()) )
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
						LOGGER.info("FiscalMenuDAO getDomainModels visitM349");
					}
				}
				@Override 
				public void visitM390() {
					if (params.accept( FiscalModelType.M390)) {
						Mod390DAO.getHeaders(ctx, domain.getId())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) 
									|| AonStringUtils.containsIgnoreCase(mod.getName(), params.getDeclared())
									|| AonStringUtils.containsIgnoreCase(mod.getFirstSurname(), params.getDeclared())
									|| AonStringUtils.containsIgnoreCase(mod.getSecondSurname(), params.getDeclared())									
									|| AonStringUtils.containsIgnoreCase(mod.getDocument(), params.getDeclared()) )
							.filter( mod -> params.getStatus() == null || mod.getStatus() == params.getStatus())
							.filter( mod -> params.getPeriod() == null || mod.getPeriod() == params.getPeriod())
							.filter( mod -> checkScope(ctx, mod, params.getScope()) )
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
						LOGGER.info("FiscalMenuDAO getDomainModels visitM390");
					}
				}
				@Override 
				public void visitM180() {
					if (params.accept( FiscalModelType.M180 )) {
						Mod180DAO.getHeaders(ctx, domain.getId())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getName(), params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getDocument(), params.getDeclared()) )
							.filter( mod -> params.getStatus() == null || mod.getStatus() == params.getStatus() )
							.filter( mod -> params.getPeriod() == null || mod.getPeriod() == params.getPeriod())
							.filter( mod -> checkScope(ctx, mod, params.getScope()) )
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
						LOGGER.info("FiscalMenuDAO getDomainModels visitM180");
					}
				}
				@Override 
				public void visitM184() {
					if (params.accept( FiscalModelType.M184 )) {
						Mod184DAO.getHeaders(ctx, domain.getId())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getName(), params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getDocument(), params.getDeclared()) )
							.filter( mod -> params.getStatus() == null || mod.getStatus() == params.getStatus() )
							.filter( mod -> params.getPeriod() == null || mod.getPeriod() == params.getPeriod())
							.filter( mod -> checkScope(ctx, mod, params.getScope()) )
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
						LOGGER.info("FiscalMenuDAO getDomainModels visitM184");
					}
				}
				@Override 
				public void visitM190() {
					if (params.accept( FiscalModelType.M190 )) {
						Mod190DAO.getHeaders(ctx, domain.getId())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getName(), params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getDocument(), params.getDeclared()) )
							.filter( mod -> params.getStatus() == null || mod.getStatus() == params.getStatus() )
							.filter( mod -> params.getPeriod() == null || mod.getPeriod() == params.getPeriod())
							.filter( mod -> checkScope(ctx, mod, params.getScope()) )
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
						LOGGER.info("FiscalMenuDAO getDomainModels visitM190");
					}
				}
				@Override 
				public void visitM193() {
					if (params.accept( FiscalModelType.M193 )) {
						Mod193DAO.getHeaders(ctx, domain.getId())
							.filter( mod -> mod.getYear()== params.getYear())
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getName(), params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getDocument(), params.getDeclared()) )
							.filter( mod -> params.getStatus() == null || mod.getStatus() == params.getStatus() )
							.filter( mod -> params.getPeriod() == null || mod.getPeriod() == params.getPeriod())
							.filter( mod -> checkScope(ctx, mod, params.getScope()) )
							.map( FiscalMenuItemJSON::toJSON )
							.forEach( allModels::put );
						LOGGER.info("FiscalMenuDAO getDomainModels visitM193");
					}
				}
				@Override 
				public void visitM200() {
					if (params.accept( FiscalModelType.M200 )) {
						getHeadersMod200(ctx, domain.getId())
							.filter( mod -> mod.getYear() == params.getYear())  
							.filter( mod -> params.getAdministration() == null || mod.getAdministration() == params.getAdministration())							
							.filter( mod -> AonStringUtils.isBlank(params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getName(), params.getDeclared()) || AonStringUtils.containsIgnoreCase(mod.getDocument(), params.getDeclared()) )
							.filter( mod -> params.getStatus() == null || mod.getStatus() == params.getStatus() )
							.filter( mod -> params.getPeriod() == null || mod.getPeriod() == params.getPeriod())
							.filter( mod -> checkScope(ctx, mod, params.getScope()) )
							.map( mod -> FiscalMenuItemJSON.toJSON(mod).put(IJsonNames.IBAN, mod.getIban()) )  // MOD200 AUN NO USA FINANCE						
							.forEach( allModels::put );
						LOGGER.info("FiscalMenuDAO getDomainModels visitM200");
					}
				}
				@Override
				public void visitM369() {
					// EL MODELO 369 AUN NO ESTA EN LA MATRIZ DE MODELOS					
				}
			};
 			for (FiscalModelType type : FiscalModelType.values()) {
				type.visit( visitor );
			}
		}

		LOGGER.info("FiscalMenuDAO getDomainModels allModels size " + allModels.length());
		LOGGER.info("FiscalMenuDAO getDomainModels END");
		return allModels;
	}

	// Comprobar ambito
	private static boolean checkScope(AONContext ctx, IFiscalModel fm, Integer scopeFilter) {

		// No estamos en el entorno, aparecen todos porque segun el ambito el usuario ha podido entrar en esa empresa
		if (ctx.getDomainId() == fm.getDomain())
			return true;
		
		// Estamos en el entorno, el ambito de la empresa debe ser nulo o uno de los disponibles para el usuario
		Domain childDomain = DomainDAO.getDomain(ctx, fm.getDomain());
		if (childDomain != null) {
			if (childDomain.getScope() == null) {
				// Si ambito de la empresa es nulo, aparece si se pide "Sin ámbito" (1) o "Todos" (2)
				return AonNumberUtils.equals(scopeFilter, 1) || AonNumberUtils.equals(scopeFilter, 2); 
			} else if (ctx.getConfig().hasAvailableScopes()) {
				// Si ámbito de la empresa no es nulo, debe ser uno de los ambitos disponibles para el usuario
				for (Scope scope : ctx.getConfig().getAvailableScopes()) {
					if (scope.getId().intValue() == childDomain.getScope().intValue()) {
						return AonNumberUtils.equals(scopeFilter, 0) || AonNumberUtils.equals(scopeFilter, 2); // Aparece si se pide "Mis ámbitos" (0) o "Todos" (2)
					}
				}			 
			}
		}
		return false;
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
//			.and(params.getScope() == null?DSL.trueCondition():DOMAIN.SCOPE.eq( params.getScope()))
			.and(DOMAIN.PARENT.isNotNull())
			.and(DOMAIN.ACTIVE.eq((byte) 1))
			.orderBy(APP_PARAM.NAME)
			.fetch()
			.stream()
			.filter(rec -> AonStringUtils.containsAny(rec.getValue(APP_PARAM.VALUE), "YQM"))
			
			// Modelo 390 solo para AEAT y CANARIAS
			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M390) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M390) && (AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.COMMON_TERRITORY || AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.CANARIAS) ))
			
			// Modelos 130, 131, 200 y 202 solo para AEAT y CANARIAS
			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M130) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M130) && (AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.COMMON_TERRITORY || AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.CANARIAS) ))
			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M131) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M131) && (AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.COMMON_TERRITORY || AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.CANARIAS) ))
			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M200) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M200) && (AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.COMMON_TERRITORY || AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.CANARIAS) ))
			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M202) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M202) && (AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.COMMON_TERRITORY || AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.CANARIAS) ))
			
			// Modelo 130, solo para AEAT, CANARIAS y BIZKAIA
//			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M130) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M130) && (AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.COMMON_TERRITORY || AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.BIZKAIA || AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.CANARIAS) ))
			
			// Modelo 390HF, solo para ALAVA, BIZKAIA y GIPUZKOA, exclusivamente
			.filter(rec -> AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M390_HF) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M390_HF) && (AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.ALAVA || AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.BIZKAIA || AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == Administration.GIPUZKOA) ))
			
			// Modelos 347, para todas las administraciones
//			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M347) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M347) && AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) != Administration.CANARIAS ))
			
			// Modelo 349, para todas las administraciones, excepto para Canarias
			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M349) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M349) && AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) != Administration.CANARIAS ))
			
			// Resto de Modelos 303, 111, 115, 123, 180, 184, 190, 193, para todas las administraciones
//			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M111) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M111) && AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) != Administration.CANARIAS ))
//			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M115) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M115) && AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) != Administration.CANARIAS ))
//			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M123) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M123) && AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) != Administration.CANARIAS ))
//			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M180) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M180) && AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) != Administration.CANARIAS ))
//			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M184) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M184) && AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) != Administration.CANARIAS ))
//			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M190) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M190) && AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) != Administration.CANARIAS ))
//			.filter(rec -> AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == null || AonStringUtils.notEquals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M193) || (AonStringUtils.equals(rec.getValue(APP_PARAM.NAME), PARAM_PREFIX + FiscalModelType.M193) && AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) != Administration.CANARIAS ))
			
			// Comprobar si se está filtrando por administracion
			.filter(rec -> params.getAdministration() == null || AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)) == params.getAdministration())
			
			.map( rec -> {
					Administration administration = AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE));
					FiscalModelType model = fromAppParamName(rec.getValue(APP_PARAM.NAME), administration);
					// Si administración por defecto es CANARIAS, los modelos de IRPF y Sociedades, se hacen en AEAT
					if ((administration == Administration.CANARIAS) &&  
					    (model == FiscalModelType.M111 || 
					 	 model == FiscalModelType.M115 || 
						 model == FiscalModelType.M123 || 
						 model == FiscalModelType.M180 || 
						 model == FiscalModelType.M184 || 
						 model == FiscalModelType.M190 || 
						 model == FiscalModelType.M193 || 
						 model == FiscalModelType.M130 || 
						 model == FiscalModelType.M131 || 
						 model == FiscalModelType.M200 || 
						 model == FiscalModelType.M202)) {
						administration = Administration.COMMON_TERRITORY;						
					}
					return new FiscalModel()
						.setDomain(rec.getValue(DOMAIN.ID))
						.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
	//					.setAdministration( AppParamDAO.parseDefaultAdministration(rec.getValue(admonAppParam.VALUE)))
	//					.setModel( fromAppParamName(rec.getValue(APP_PARAM.NAME)) )
						.setAdministration(administration)
						.setModel(model)
						.setDocument(rec.getValue(REGISTRY.DOCUMENT))
						.setName(rec.getValue(REGISTRY.NAME))
						.setStatus( FiscalStatus.MISSING)
						.setPeriod("Y".equals(rec.getValue(APP_PARAM.VALUE))?Period.YEAR:"Q".equals(rec.getValue(APP_PARAM.VALUE))?Period.T1:Period.M01);
			})
			.filter( fm -> checkScope(ctx, fm, params.getScope()))
			.map( FiscalMenuItemJSON::toJSON )
			.forEach( allModels::put )
			;
	}

	private static FiscalModelType fromAppParamName( String name, Administration administration ) {
		String model = AonStringUtils.substringAfter(name,PARAM_PREFIX);
		FiscalModelType fmt = FiscalModelType.valueOf(model);
		if (administration == Administration.CANARIAS && fmt == FiscalModelType.M303_RS) {
			return FiscalModelType.M421;
		} else if (fmt == FiscalModelType.M303_RG || fmt == FiscalModelType.M303_RS) {
			return FiscalModelType.M303;
		}
		return fmt;
	}

	private static Filter getFilter(FiscalModelProperties p, Domain domain, FiscalMatrixParams params) {
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
//		if (params.getScope() != null) {
//			prop = prop.and( p.getDomainScopeProperty().eq( params.getScope()));
//		}
		if (AonStringUtils.isNotBlank(params.getDeclared())) {
			prop = prop.and( p.getNameProperty().like("%"+params.getDeclared()+"%").or(p.getSurnameProperty().like("%"+params.getDeclared()+"%")).or(p.getDocumentProperty().like("%"+params.getDeclared()+"%")) );
		}
		if (params.getStatus() != null) {
			prop = prop.and( p.getStatusProperty().eq(params.getStatus().value()) );
		}
		if (params.getPeriod() != null) {
			prop = prop.and( p.getPeriodProperty().eq(params.getPeriod().value()) );
		}
			
		return prop;
	}
	
	private static Stream<FiscalModel> getHeadersMod200(AONContext ctx, int domain) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select(FS_MODEL200.fields())
			.select(DOMAIN.DESCRIPTION)
			.from(FS_MODEL200)
			.join(DOMAIN).on(FS_MODEL200.DOMAIN.equal(DOMAIN.ID))
			.where(FS_MODEL200.DOMAIN.equal(domain).or(DOMAIN.PARENT.equal(domain)))
			.orderBy(FS_MODEL200.YEAR.desc(), FS_MODEL200.NAME.asc(), FS_MODEL200.ID.desc())
			.fetch()
			.stream()
			.map( new Mod200Filler() )
			;
	}	
	
	private static class Mod200Filler implements Function<Record,FiscalModel> {

		@Override
		public FiscalModel apply(Record rec) {
			FiscalModel fm = new FiscalModel();
			fm.setModel(FiscalModelType.M200)
					.setPeriod(Period.YEAR)
					.setId(rec.getValue(FS_MODEL200.ID))
					.setDomain(rec.getValue(FS_MODEL200.DOMAIN))
					.setDomainName(rec.getValue(DOMAIN.DESCRIPTION))
					.setYear(rec.getValue(FS_MODEL200.YEAR))
					.setAdministration(Administration.safeValueOf(rec.getValue(FS_MODEL200.ADMINISTRATION)))
					.setStatus(FiscalStatus.safeValueOf(rec.getValue(FS_MODEL200.STATUS)))
					.setFinance(null)
					.setComplementary( AonEnumUtils.getBoolean( rec.getValue(FS_MODEL200.COMPLEMENTARY)))
					.setReplacement( false )
					.setNumber(rec.getValue(FS_MODEL200.RECEIPT ))
					.setReplacedNumber(rec.getValue(FS_MODEL200.COMPLEMENTARY_RECEIPT))
					.setComments(rec.getValue(FS_MODEL200.COMMENTS ))
					.setDocument(rec.getValue(FS_MODEL200.DOCUMENT ))
					.setName(rec.getValue(FS_MODEL200.NAME))
					.setCreationUser(rec.getValue(FS_MODEL200.CREATION_USER))
					.setCreationDate(rec.getValue(FS_MODEL200.CREATION_DATE))
					.setModificationUser(rec.getValue(FS_MODEL200.MODIFICATION_USER))
					.setModificationDate(rec.getValue(FS_MODEL200.MODIFICATION_DATE))
					.setDeclarationResultType(FiscalModelDeclarationType.safeValueOf(getDeclarationResultTypeMod200(rec.getValue(FS_MODEL200.RESULT_TYPE),rec.getValue(FS_MODEL200.PAY_TYPE),rec.getValue(FS_MODEL200.DEV_TYPE))))
					.setDeclarationResult(rec.getValue(FS_MODEL200.AMOUNT) == null ? 0.0 : (AonStringUtils.equals(rec.getValue(FS_MODEL200.RESULT_TYPE),"D") ? (-1)*rec.getValue(FS_MODEL200.AMOUNT) : rec.getValue(FS_MODEL200.AMOUNT)))
					.setNrc(rec.getValue(FS_MODEL200.NRC))
					.setIban(rec.getValue(FS_MODEL200.IBAN))
				;
			return fm;
			
		}

		private String getDeclarationResultTypeMod200(String resultType, String payType, String devType) {
			if (AonStringUtils.equals(resultType,"N") || AonStringUtils.equals(resultType,"C")) {
				return "N"; // Resultado cero
			} else if (AonStringUtils.equals(resultType,"I")) {
				if (AonStringUtils.equals(payType,"H"))
					return "I";     // Efectivo (obsoleto), se devuelve como ingreso, como no tendrá NRC, no dejará presentarlo 
				else 
					return payType; // Ingreso (I), Domiciliación (U), Anotación CCT (G)
			} else if (AonStringUtils.equals(resultType,"D")) {
				return devType; // Devolución (D), Renuncia (R), Anotación CCT (V)
			}			
			return null;
		}
	}

}
