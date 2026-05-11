package net.aonsolutions.aon.gwt.ccaa.server.normalizedMemory;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.json.JSONArray;

import com.code.aon.webservice.payroll.ContractServlet;
import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositPreviousToCurrentConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.DepositType;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.ID2DepositKey;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.CNAE2009ToCNAE2025;
import com.esferalia.aon.occam.api.model.type.CNAE2025;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2Compute;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2PrevioustoD2Current;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves.Clave;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Utils;
import com.esferalia.aon.occam.mod200.api.FISCAL;
import com.esferalia.aon.occam.mod200.api.model.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.mod200.api.model.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.mod200.api.model.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.mod200.api.model.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.mod200.api.model.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.mod200.api.model.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.d2_deposit.Mod2002013toD2;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.d2_deposit.Mod2002014toD2;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.d2_deposit.Mod2002015toD2;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.d2_deposit.Mod2002016toD2;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.d2_deposit.Mod2002017toD2;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.d2_deposit.Mod2002018toD2;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import net.aonsolutions.aon.gwt.ccaa.client.INormalizedMemory;
import net.aonsolutions.aon.gwt.ccaa.shared.MemoryFiles;
import net.aonsolutions.aon.gwt.ccaa.shared.MemoryTemplate;

@WebServlet(name = "D2Deposit", urlPatterns = { "/aon_gwt_aio/ms/gwt_deposit" })
public class NormalizedMemoryServlet extends AonStatelessRemoteServiceServlet implements INormalizedMemory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String D2_FILE_MEMORY = "Memoria";
	private static final String D2_FILE_AUTOCARTERA_MODEL = "Modelo de Autocartera";
	private static final String D2_FILE_GESTION = "Informe de Gesti\u00f3n";
	private static final String D2_FILE_AUDIT = "Informe de Auditor\u00eda";
	private static final String D2_FILE_TITULAR_REAL = "Informe de Titular Real";
	private static final String D2_FILE_NO_FINANCIERA= "Informe sobre Informaci\u00f3n no financiera";
	private static final String D2_FILE_CONVOC = "Anuncios de Convocatoria";
	private static final String D2_FILE_SICAV = "Certificaci\u00f3n SICAV";
	
	
	public static NormalizedMemoryServlet getInstance() {
		return new NormalizedMemoryServlet();
	}
	
	/***** NEW GWT DEPOSIT *****/
	
	public Company getCompany(AonData aonData) {
		return AON.getCompany(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), f -> f.getDomainProperty().eq(aonData.getDomain().getId()));
	}
	
	public Map<String, String> getSchemaTextMode(AonData aonData, Integer id){
		Integer id2 = (id != null) ? id: createSchemaTextMode(aonData, "Plantilla1");
		
		Attach attach = AON.getAttach(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 
				f -> f.getIdProperty().eq(id2), AttachType.REGISTRY);
		
		return getSchema(aonData, attach, -1);
	}
	
	public Integer createSchemaTextMode(AonData aonData, String name) {
		byte[] data = Utils.CreateXml("", name);
		return DBConsults.insertDepositText(aonData.getDomain().getName(), name, data, aonData.getDomain().getId(), aonData.getUser().getLogin());
	}
	
	public void deleteSchemaTextMode(AonData aonData, Integer id) {
		AON.deleteAttach(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), f -> f.getIdProperty().eq(id), AttachType.REGISTRY);
	}
	
	public Map<String, String> getSchema(AonData aonData, Company company, Integer year, Boolean textMode){
		System.out.println("NormalizedMemoryServlet: getSchema (1) - year: " + year);
		Attach attach = AON.getAttach(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), f -> f.getDomainProperty().eq(aonData.getDomain().getId())
				.and(f.getTypeProperty().eq((byte) 17))
				.and(f.getAttachDateProperty().eq(DBConsults.newAttachDate(year)))
			, AttachType.REGISTRY);
		
		if(attach.getId() == null) {
	    	ApplicationParameter ap = AON.getApplicationParameter(aonData.getDomain().getName(), aonData.getDomain().getId(),
	    			aonData.getUser().getLogin(), AppParam.FS_MODEL_CFG_CCAA.getValue());
			String type = DepositType.ABREVIADO.getLabel();
	    	if(ap.getValue() != null) {
	    		type = DepositType.values()[Integer.parseInt(ap.getValue())].getLabel();
	    	} 
			return createD2Deposit(aonData, company.getId(), company.getName(), type, year);	
		} else {
			return getSchema(aonData, attach, year);
		}
	}
	
	public Map<String, String> reset(AonData aonData, Company company, Integer year) {
		delete(aonData, year);
		ApplicationParameter ap = AON.getApplicationParameter(aonData.getDomain().getName(), aonData.getDomain().getId(),
    			aonData.getUser().getLogin(), AppParam.FS_MODEL_CFG_CCAA.getValue());
		String type = DepositType.ABREVIADO.getLabel();
    	if(ap.getValue() != null) {
    		type = DepositType.values()[Integer.parseInt(ap.getValue())].getLabel();
    	} 
		return createD2Deposit(aonData, company.getId(), company.getName(), type, year);	
	}
	
	private Map<String, String> getSchema(AonData aonData, Attach attach, Integer year){
		System.out.println("NormalizedMemoryServlet: getSchema (2) - year: " +  year);
		Map<String, String> map = new HashMap<String, String>();
		if(year>=2017) {
			map.put(D2DepositFooterKey.PR8080827.getCode(), "1");
		}
		try {
			// CREAMOS EL ESQUEMA SEGUN LOS DATOS GUARDADOS EN RATTACH (XML)
			Esquema schema = Utils.readXml(attach.getData());
			// SI NO ES MEMORIA NORMALIZADA, LEEMOS EL DOCUMENTO DE LA MEMORIA
			if(!schema.getCabecera().isMemoriaNormalizada()) {
				Vector<Integer> id = DBConsults.getMemoryFile(aonData.getDomain().getName(), aonData.getDomain().getId(), D2_FILE_MEMORY + year);
				if(id.get(0) == -1) {
					schema.getCabecera().setMemoriaNormalizada(true);		
				}
			}
			
			// RELLENAMOS LA LISTA DE CLAVES
			List<Clave> claves = schema.getClaves().getClave();
			if(schema.getError() != null) {
				map.put("error", schema.getError());
				System.out.println("NormalizedMemoryServlet: getSchema (2) - schema.getError() != null - " + schema.getError());
			}
			
			// TIPO CUESTIONARIO (ABREVIADO, PYME)
			String type = schema.getCabecera().getTipoCuestionario();
			map.put(D2DepositConstants.DEPOSIT_TYPE, type);
			
			// GUARDAR TODAS LAS CLAVES EN EL MAP
			for (Integer i = 0; i < claves.size(); i++) {
				if(!map.containsKey(claves.get(i).getCodigo().toString()))
					map.put(claves.get(i).getCodigo().toString(), claves.get(i).getValor());
			}
			
			// CNAE2025 A PARTIR DE LAS CUENTAS ANUALES DE 2024, CUMPLIMENTARLO SI ESTA VACIO, SOLO SI EQUIVALENTE ES UNICO
			CNAE2025 cnae2025 = null;
			if (year >= 2024 && AonStringUtils.isBlank(map.get(D2DepositHeaderKey.IDA02014.getCode())) && AonStringUtils.isNotBlank(map.get(D2DepositHeaderKey.IDA02001.getCode()))) {
				String cnae2009 = map.get(D2DepositHeaderKey.IDA02001.getCode());
				cnae2025 = getCnae2025fromCnae2009(cnae2009);
				if (cnae2025 != null) {
					// Codigo CNAE2025
					map.put(D2DepositHeaderKey.IDA02014.getCode(), cnae2025.getCodeWithoutPoint());
					// Descripción la del CNAE 2025 (como hace el D2)
					map.put(D2DepositHeaderKey.IDA02009.getCode(), cnae2025.getDescription()); 
				}
			}
			
			// SI EL AÑO NO COINCIDE CON EL QUE CONTIENE EL ESQUEMA, GRABO EL ESQUEMA EN LA BD ?? POR QUE NO VA A COINCIDIR EL AÑO ?? TAL VEZ PROBLEMAS AL GUARDAR LOS DATOS O ALGO ASI ??
			if(year != null && year != -1  && !schema.getCabecera().getEjercicio().equals(BigInteger.valueOf(year))) {
				System.out.println("NormalizedMemoryServlet: getSchema (2) - Año no coincide con el esquema. year="+year + " esquema_ejercicio=" + schema.getCabecera().getEjercicio());
				saveDeposit(aonData, map, year);
			}
			
			// GRABAR CLAVES 8080852 O 8080805 PAGINA PR MEMORIA PYME O ABREVIADA, SEGUN TIPO CUESTIONARIO Y CNAE2025 SI ES NECESARIO
			if(schema.getCabecera().getTipoCuestionario().equalsIgnoreCase("pymes")) {
				String a = map.get(D2DepositFooterKey.PR8080852.getCode());
				//updateSchemaMemory(aonData, "1".equals(a), D2DepositFooterKey.PR8080852.getCode(), year);
				updateSchemaMemoryNew(aonData, "1".equals(a), D2DepositFooterKey.PR8080852.getCode(), year, cnae2025);
			} else {
				String a = map.get(D2DepositFooterKey.PR8080805.getCode());
				//updateSchemaMemory(aonData, "1".equals(a), D2DepositFooterKey.PR8080805.getCode(), year);
				updateSchemaMemoryNew(aonData, "1".equals(a), D2DepositFooterKey.PR8080805.getCode(), year, cnae2025);
			}
			System.out.println("NormalizedMemoryServlet: getSchema (2) - OK");
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("NormalizedMemoryServlet: getSchema (2) - ERROR");
		}
		return map;
	}

	public Map<String, String>  updateType(AonData aonData, Integer year, String type) {
		Esquema schema = getSchema(aonData, year);
		if(!schema.getCabecera().getEjercicio().equals(BigInteger.valueOf(year))) {
			schema.getCabecera().setEjercicio(BigInteger.valueOf(year));
		}
		schema = Utils.changeType(schema, type);
		
		try {
			byte[] b = Utils.writeXml(schema);
			Esquema sch = Utils.readXml(b);
			if(sch.getError() != null ) {
				// TODO 
			} else DBConsults.insertDeposit(aonData.getDomain().getName(), b, aonData.getDomain().getId(), year, aonData.getUser().getLogin());
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		List<Clave> claves = schema.getClaves().getClave();
		Map<String, String> map = new HashMap<>();
		map.put(D2DepositConstants.DEPOSIT_TYPE, type);
		for (Integer i = 0; i < claves.size(); i++) {
			if(!map.containsKey(claves.get(i).getCodigo().toString()))
				map.put(claves.get(i).getCodigo().toString(), claves.get(i).getValor());
		}
		return map;
	}
	
	public void saveDeposit(AonData aonData, Map<String, String> deposit, Integer year) {
		System.out.println("NormalizedMemoryServlet: saveDeposit");
		Esquema schema = getSchema(aonData, year);
		if(!schema.getCabecera().getEjercicio().equals(BigInteger.valueOf(year))) {
			schema.getCabecera().setEjercicio(BigInteger.valueOf(year));
		}
		schema = deposit2Schema(schema, deposit);
		try {
			byte[] b = Utils.writeXml(schema);
			Esquema sch = Utils.readXml(b);
			if(sch.getError() != null) {
				// TODO
				System.err.println("NormalizedMemoryServlet: saveDeposit - ERROR - " + sch.getError());
			} else DBConsults.insertDeposit(aonData.getDomain().getName(), b, aonData.getDomain().getId(), year, aonData.getUser().getLogin());
			System.out.println("NormalizedMemoryServlet: saveDeposit - OK ");
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveDepositTextMode(AonData aonData, Map<String, String> deposit, Integer id) {
		Attach attach = AON.getAttach(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), f -> f.getIdProperty().eq(id), AttachType.REGISTRY);

		Esquema schema = DBConsults.getDeposit(attach);
		schema = deposit2Schema(schema, deposit);
		try {
			byte[] b = Utils.writeXml(schema);
			Esquema sch = Utils.readXml(b);
			if(sch.getError() != null) {
				// TODO
			} else {
				attach.setData(b);
				AON.updateAttach(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), attach);
			}
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private Esquema deposit2Schema(Esquema schema, Map<String, String> deposit) {
		Esquema s = new Esquema();
		s.setCabecera(schema.getCabecera());
		Claves claves = new Claves();

		for(String key : deposit.keySet()){
			if(!key.equals("DepositType")){
				Clave clave = new Clave();
				clave.setCodigo(BigInteger.valueOf(Integer.parseInt(key)));
				clave.setValor(deposit.get(key));
				claves.getClave().add(clave);
			}
		}
		s.setClaves(claves);
		return s;
	}
	
	public Map<String, String> calculate(AonData aonData, Map<String, String> map, Integer year){
		map = calculate(map, year);
		saveDeposit(aonData, map, year);
		return map;
	}
	
	
	public Vector<MemoryTemplate> getTemplates(AonData aonData) {
		Vector<MemoryTemplate> vector = getTemplates(aonData, aonData.getDomain().getId());
		if(vector.size() == 0) {
			Integer id = createSchemaTextMode(aonData, "Plantilla1");
			vector.add(new MemoryTemplate().setId(id).setName("Plantilla1"));
		}
		return getTemplates(aonData, aonData.getDomain().getId());
	}
	
	public Vector<MemoryTemplate> getDepositTemplates(AonData aonData) {
		return getTemplates(aonData, aonData.getDomain().getParentId());
	}
	
	public Vector<MemoryTemplate> getTemplates(AonData aonData, Integer domainId) {
		return AON.getAttachStream(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(),
				f -> f.getDomainProperty().eq(domainId)
				.and(f.getTypeProperty().eq(RegistryAttachmentType.D2_DEPOSIT.value())),
			AttachType.REGISTRY, true).map(r -> new MemoryTemplate()
										.setId(r.getId())
										.setName(r.getDescription())
					)
			.collect(Collectors.toCollection(Vector::new));
	}
	
	/***************************/

	public Boolean isDigitalDeposit(AonData aonData, Integer year) {
		return DBConsults.isDigitalDeposit(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), year);
	}
	
	public String[] getDepositExercises(AonData aonData) {
		return DBConsults.getDepositExercises(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin());
	}
	
	public Map<String, String> updateTexts(AonData aonData, MemoryTemplate mt, Map<String, String> map) {
		Attach attach = AON.getAttach(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), f -> f.getIdProperty().eq(mt.getId()), AttachType.REGISTRY);
		Esquema schema = DBConsults.getDeposit(attach);
		
		for (Integer i = 0; i < schema.getClaves().getClave().size(); i++) {
			if (schema.getClaves().getClave().get(i).getCodigo().toString()	.equals(D2DepositKey.MAT19019001.getCode())) { 
				map.put(D2DepositKey.MAT19019001.getCode(),  schema.getClaves().getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositKey.MAT29029001.getCode())) { 
				map.put(D2DepositKey.MAT29029001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositKey.MAT39039001.getCode())) { 
				map.put(D2DepositKey.MAT39039001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositKey.MAT49049001.getCode())) { 
				map.put(D2DepositKey.MAT49049001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositKey.MAT59059001.getCode())) { 
	 			map.put(D2DepositKey.MAT59059001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositKey.MAT69069001.getCode())) {
				map.put(D2DepositKey.MAT69069001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositKey.MAT79079001.getCode())) {	
				map.put(D2DepositKey.MAT79079001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositKey.MAT89089001.getCode())) { 
				map.put(D2DepositKey.MAT89089001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositKey.MAT99099001.getCode())) { 
				map.put(D2DepositKey.MAT99099001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositKey.MAT119119001.getCode())) { 
				map.put(D2DepositKey.MAT119119001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositKey.MAT129129001.getCode())) { 
				map.put(D2DepositKey.MAT129129001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositKey.MAT139139001.getCode())) { 
				map.put(D2DepositKey.MAT139139001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositKey.MAT149149001.getCode())) { 
				map.put(D2DepositKey.MAT149149001.getCode(), schema.getClaves().getClave().get(i).getValor());
			}
		}
		return map;
	}

	public Date getDate(String str) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		Date date = null;
		try {
			date = formatter.parse(str);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public Map<String, String> importAll(AonData aonData, String type, String ejercicio, MemoryTemplate mt, String cif, Map<String, String> map, Integer year) {
		if ("Balance (I.S.)".equals(type) || "Balance".equals(type)) {
			if("2013".equals(ejercicio)) {
				Mod2002013 mod2002013 = FISCAL.getMod2002013ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2013);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002013toD2.fillBalance(ctx, mod2002013);

				for(D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2014".equals(ejercicio)) {
				Mod2002014 mod2002014 = FISCAL.getMod2002014ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2014);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002014toD2.fillBalance(ctx, mod2002014, year);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2015".equals(ejercicio)){
				Mod2002015 mod2002015 = FISCAL.getMod2002015ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2015);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002015toD2.fillBalance(ctx, mod2002015, year.toString().equals(ejercicio));

				for(D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2016".equals(ejercicio)){
				Mod2002016 mod2002016 = FISCAL.getMod2002016ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2016);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002016toD2.fillBalance(ctx, mod2002016, year.toString().equals(ejercicio));

				for(D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2017".equals(ejercicio)){
				Mod2002017 mod2002017 = FISCAL.getMod2002017ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2017);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002017toD2.fillBalance(ctx, mod2002017, year.toString().equals(ejercicio));

				for(D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2018".equals(ejercicio)){
				Mod2002018 mod2002018 = FISCAL.getMod2002018ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2018);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002018toD2.fillBalance(ctx, mod2002018, year.toString().equals(ejercicio));

				for(D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} 
			
		} else if("Perdidas y ganancias (I.S.)".equals(type) || "Perdidas y Ganancias".equals(type) ) {
			if("2013".equals(ejercicio)) {
				Mod2002013 mod2002013 = FISCAL.getMod2002013ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2013);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002013toD2.fillPyg(ctx, mod2002013);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2014".equals(ejercicio)) {
				Mod2002014 mod2002014 = FISCAL.getMod2002014ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2014);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002014toD2.fillPyg(ctx, mod2002014, year);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2015".equals(ejercicio)){
				Mod2002015 mod2002015 = FISCAL.getMod2002015ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2015);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002015toD2.fillPyg(ctx, mod2002015, year.toString().equals(ejercicio));

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2016".equals(ejercicio)){
				Mod2002016 mod2002016 = FISCAL.getMod2002016ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2016);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002016toD2.fillPyg(ctx, mod2002016, year.toString().equals(ejercicio));

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2017".equals(ejercicio)){
				Mod2002017 mod2002017 = FISCAL.getMod2002017ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2017);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002017toD2.fillPyg(ctx, mod2002017, year.toString().equals(ejercicio));

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2018".equals(ejercicio)){
				Mod2002018 mod2002018 = FISCAL.getMod2002018ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2018);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002018toD2.fillPyg(ctx, mod2002018, year.toString().equals(ejercicio));

				for(D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} 
		} else if("ECPN (I.S.)".equals(type)) {
			if ("2013".equals(ejercicio)) {
				Mod2002013 mod2002013 = FISCAL.getMod2002013ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2013);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002013toD2.fillEcpn(ctx, mod2002013);
				Mod2002013toD2.fillEcpn2(ctx, mod2002013);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2014".equals(ejercicio)) {
				Mod2002014 mod2002014 = FISCAL.getMod2002014ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2014);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002014toD2.fillEcpn(ctx, mod2002014, year);
				Mod2002014toD2.fillEcpn2(ctx, mod2002014, year);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2015".equals(ejercicio)){
				Mod2002015 mod2002015 = FISCAL.getMod2002015ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2015);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002015toD2.fillEcpn(ctx, mod2002015, year.toString().equals(ejercicio));
				Mod2002015toD2.fillEcpn2(ctx, mod2002015, year.toString().equals(ejercicio));

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2016".equals(ejercicio)){
				Mod2002016 mod2002016 = FISCAL.getMod2002016ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2016);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002016toD2.fillEcpn(ctx, mod2002016, year.toString().equals(ejercicio));
				Mod2002016toD2.fillEcpn2(ctx, mod2002016, year.toString().equals(ejercicio));

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2017".equals(ejercicio)){
				Mod2002017 mod2002017 = FISCAL.getMod2002017ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2017);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002017toD2.fillEcpn(ctx, mod2002017, year.toString().equals(ejercicio));
				Mod2002017toD2.fillEcpn2(ctx, mod2002017, year.toString().equals(ejercicio));

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2018".equals(ejercicio)){
				Mod2002018 mod2002018 = FISCAL.getMod2002018ByYear(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), 2018);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002018toD2.fillEcpn(ctx, mod2002018, year.toString().equals(ejercicio));
				Mod2002018toD2.fillEcpn2(ctx, mod2002018, year.toString().equals(ejercicio));
				for(D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} 
		} 
		return map;
	}
	
	private void delete(AonData aonData, Integer year) {
		String domainName = aonData.getDomain().getName();
		Integer domainId = aonData.getDomain().getId();
		
		DBConsults.deleteDeposit(domainName, domainId, year);
		
		DBConsults.deleteMemoryFile(domainName, domainId, D2_FILE_MEMORY);
		DBConsults.deleteMemoryFile(domainName, domainId, D2_FILE_AUTOCARTERA_MODEL);
		DBConsults.deleteMemoryFile(domainName, domainId, D2_FILE_GESTION);
		DBConsults.deleteMemoryFile(domainName, domainId, D2_FILE_AUDIT);
		DBConsults.deleteMemoryFile(domainName, domainId, D2_FILE_CONVOC);
		DBConsults.deleteMemoryFile(domainName, domainId, D2_FILE_SICAV);
	}

	public Map<String, String> calculate(Map<String, String> map, Integer year) {
		AccMiningMVELContext ctx = new AccMiningMVELContext(new IAccMiningKeyAccept() {
			@Override
			public boolean acceptKey(Object key) {
				return AonStringUtils.isNotEmpty((String) key);
			}
		});
		
		for(String key : map.keySet()){
			try {
				if (AonStringUtils.isNotEmpty( map.get(key) )) {
					Double d = Double.parseDouble(map.get(key));
					ctx.put("Q"+key, d);
				}
			} catch (NumberFormatException e) {
				// Ignore value
			}
		}
		ctx.put("PYMES", map.get(D2DepositConstants.DEPOSIT_TYPE).equals("Pymes"));		
		ctx.put("Y2014", year == 2014);		
		
		ctx.setExpressionMap(D2Compute.COMPUTE_MAP_CURRENT);
		map = calculate(map, ctx, D2Compute.COMPUTE_MAP_CURRENT);
		
		ctx.setExpressionMap(D2Compute.COMPUTE_MAP_PREVIOUS);
		map = calculate(map, ctx, D2Compute.COMPUTE_MAP_PREVIOUS);
		
		return map;
	}
	
	private Map<String, String> calculate(Map<String, String> map, AccMiningMVELContext ctx, Map<String, String> computeMap){
		for (String key : computeMap.keySet()) {
			String expression = computeMap.get(key);
			Object ret = ctx.evaluateExpression(key,expression);
			if (ret instanceof Double) {
				Double calculated = (Double) ret;
				ctx.put("Q"+key, calculated);
				map.put(key, Double.toString(AonMathUtils.round(calculated)));
			}
		}
		return map;
	}
	
	public Vector<MemoryFiles> getMemoryFiles(AonData aonData, Integer year){
		Vector<MemoryFiles> ms = new Vector<MemoryFiles>();
		
		MemoryFiles m = new MemoryFiles();
		Vector<Integer> id = DBConsults.getMemoryFile(aonData.getDomain().getName(), aonData.getDomain().getId(), D2_FILE_MEMORY + year);
		m.setId(id.get(0));m.setBool(id.get(0) != -1);m.setName(D2_FILE_MEMORY + year);
		if(id.get(0) != -1){
			MimeType mimeType = MimeType.values()[id.get(1)];
			m.setMimeTypeName(mimeType.getName());
			m.setMimeTypeNumber(id.get(1));
		}
		ms.add(m);
		
		MemoryFiles m2 = new MemoryFiles();
		id = DBConsults.getMemoryFile(aonData.getDomain().getName(), aonData.getDomain().getId(), D2_FILE_AUTOCARTERA_MODEL + year);
		m2.setId(id.get(0));m2.setBool(id.get(0) != -1);m2.setName(D2_FILE_AUTOCARTERA_MODEL + year);
		if(id.get(0) != -1){
			MimeType mimeType = MimeType.values()[id.get(1)];
			m2.setMimeTypeName(mimeType.getName());
			m2.setMimeTypeNumber(id.get(1));
		}
		ms.add(m2);
		
		MemoryFiles m3 = new MemoryFiles();
		id = DBConsults.getMemoryFile(aonData.getDomain().getName(), aonData.getDomain().getId(), D2_FILE_GESTION + year);
		m3.setId(id.get(0));m3.setBool(id.get(0) != -1);m3.setName(D2_FILE_GESTION + year);
		if(id.get(0) != -1){
			MimeType mimeType = MimeType.values()[id.get(1)];
			m3.setMimeTypeName(mimeType.getName());
			m3.setMimeTypeNumber(id.get(1));
		}		
		ms.add(m3);

		MemoryFiles m4 = new MemoryFiles();
		id = DBConsults.getMemoryFile(aonData.getDomain().getName(), aonData.getDomain().getId(), D2_FILE_AUDIT + year);
		m4.setId(id.get(0));m4.setBool(id.get(0) != -1);m4.setName(D2_FILE_AUDIT + year);
		if(id.get(0) != -1){
			MimeType mimeType = MimeType.values()[id.get(1)];
			m4.setMimeTypeName(mimeType.getName());
			m4.setMimeTypeNumber(id.get(1));
		}		
		ms.add(m4);
		
		if(year >= 2017) {
			MemoryFiles m5 = new MemoryFiles();
			id = DBConsults.getMemoryFile(aonData.getDomain().getName(), aonData.getDomain().getId(), D2_FILE_NO_FINANCIERA + year);
			m5.setId(id.get(0));m5.setBool(id.get(0) != -1);m5.setName(D2_FILE_NO_FINANCIERA + year);
			if(id.get(0) != -1){
				MimeType mimeType = MimeType.values()[id.get(1)];
				m5.setMimeTypeName(mimeType.getName());
				m5.setMimeTypeNumber(id.get(1));
			}		
			ms.add(m5);
		} else {
			MemoryFiles m5 = new MemoryFiles();
			id = DBConsults.getMemoryFile(aonData.getDomain().getName(), aonData.getDomain().getId(), D2_FILE_TITULAR_REAL + year);
			m5.setId(id.get(0));m5.setBool(id.get(0) != -1);m5.setName(D2_FILE_TITULAR_REAL + year);
			if(id.get(0) != -1){
				MimeType mimeType = MimeType.values()[id.get(1)];
				m5.setMimeTypeName(mimeType.getName());
				m5.setMimeTypeNumber(id.get(1));
			}		
			ms.add(m5);
		}

		MemoryFiles m6 = new MemoryFiles();
		id = DBConsults.getMemoryFile(aonData.getDomain().getName(), aonData.getDomain().getId(), D2_FILE_CONVOC + year);
		m6.setId(id.get(0));m6.setBool(id.get(0) != -1);m6.setName(D2_FILE_CONVOC + year);
		if(id.get(0) != -1){
			MimeType mimeType = MimeType.values()[id.get(1)];
			m6.setMimeTypeName(mimeType.getName());
			m6.setMimeTypeNumber(id.get(1));
		}		
		ms.add(m6);

		MemoryFiles m7 = new MemoryFiles();
		id = DBConsults.getMemoryFile(aonData.getDomain().getName(), aonData.getDomain().getId(), D2_FILE_SICAV + year);
		m7.setId(id.get(0));m7.setBool(id.get(0) != -1);m7.setName(D2_FILE_SICAV + year);
		if(id.get(0) != -1){
			MimeType mimeType = MimeType.values()[id.get(1)];
			m7.setMimeTypeName(mimeType.getName());
			m7.setMimeTypeNumber(id.get(1));
		}		
		ms.add(m7);
		
		return ms;
	}
	
	public void deleteMemoryFile(AonData aonData, Integer id){
		DBConsults.deleteMemoryFile(aonData.getDomain().getName(), aonData.getDomain().getId(), id);
	}

	public void updateSchemaMemory(AonData aonData, Boolean bool, String key, Integer year){
		Esquema schema = getSchema(aonData, year);
		System.out.println("NormalizedMemoryServlet: updateSchemaMemory - year="+year+" esquema.ejercicio="+schema.getCabecera().getEjercicio());
	
		if(D2DepositFooterKey.PR8080805.getCode().equals(key)
			|| D2DepositFooterKey.PR8080852.getCode().equals(key))
			schema.getCabecera().setMemoriaNormalizada(!bool);
		
		List<Clave> claves = schema.getClaves().getClave();
		Boolean esta = false;
	
		for (Integer i = 0; i < claves.size(); i++) {
			if(schema.getClaves().getClave().get(i).getCodigo().toString().equals(key)){
				if(D2DepositFooterKey.PR8080805.getCode().equals(key)
					|| D2DepositFooterKey.PR8080852.getCode().equals(key))
					schema.getClaves().getClave().get(i).setValor(bool?"0":"1");
				else schema.getClaves().getClave().get(i).setValor(bool?"1":"0");
				esta = true;
			}
		}
		if(!esta) {
			Clave clave = new Clave();
			clave.setCodigo(new BigInteger(key));
			if(D2DepositFooterKey.PR8080805.getCode().equals(key)
				|| D2DepositFooterKey.PR8080852.getCode().equals(key))
				clave.setValor(bool?"0":"1");
			else clave.setValor(bool?"1":"0");
			schema.getClaves().getClave().add(clave);
		}
		
		try {
			// HACE LO MISMO QUE EN SAVEDEPOSIT, GRABA Y LEE EL FICHERO XML, LO PASA A ESQUEMA Y GRABA EL XML
			byte[] b = Utils.writeXml(schema);
			Esquema sch = Utils.readXml(b);
			if(sch.getError() != null) {
				// TODO
				System.err.println("NormalizedMemoryServlet: updateSchemaMemory - sch.getError() != null - " + sch.getError());
			} else DBConsults.insertDeposit(aonData.getDomain().getName(), b, aonData.getDomain().getId(), year, aonData.getUser().getLogin());
			System.out.println("NormalizedMemoryServlet: updateSchemaMemory - OK");
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private void updateSchemaMemoryNew(AonData aonData, Boolean bool, String key, Integer year, CNAE2025 cnae2025){
		Esquema schema = getSchema(aonData, year);
		System.out.println("NormalizedMemoryServlet: updateSchemaMemoryNew - year="+year+" esquema.ejercicio="+schema.getCabecera().getEjercicio());
	
		if(D2DepositFooterKey.PR8080805.getCode().equals(key)
			|| D2DepositFooterKey.PR8080852.getCode().equals(key))
			schema.getCabecera().setMemoriaNormalizada(!bool);
		
		List<Clave> claves = schema.getClaves().getClave();
		Boolean esta = false;
	
		Integer index2009 = null; // Index de la casilla de la descripción del CNAE
		Integer index2014 = null; // Index de la casilla del código del CNAE2025
		for (Integer i = 0; i < claves.size(); i++) {
			if(schema.getClaves().getClave().get(i).getCodigo().toString().equals(key)){
				if(D2DepositFooterKey.PR8080805.getCode().equals(key)
					|| D2DepositFooterKey.PR8080852.getCode().equals(key))
					schema.getClaves().getClave().get(i).setValor(bool?"0":"1");
				else schema.getClaves().getClave().get(i).setValor(bool?"1":"0");
				esta = true;
			}
			// Guardo tambien el index de la descripción del CNAE, por si necesito actualizarlo con la descripción del CNAE2025
			if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositHeaderKey.IDA02009.getCode())) {
				index2009 = i;
			}
			// Guardo tambien el index del codigo del CNAE2025, por si ya existe y es necesario sobreescribirlo más abajo
			if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(D2DepositHeaderKey.IDA02014.getCode())) {
				index2014 = i;
			}
			
		}
		if(!esta) {
			Clave clave = new Clave();
			clave.setCodigo(new BigInteger(key));
			if(D2DepositFooterKey.PR8080805.getCode().equals(key)
				|| D2DepositFooterKey.PR8080852.getCode().equals(key))
				clave.setValor(bool?"0":"1");
			else clave.setValor(bool?"1":"0");
			schema.getClaves().getClave().add(clave);
		}
		
		// CNAE2025 si no es nulo, tambien lo grabamos en el esquema
		if (cnae2025 != null) {
			System.out.println("NormalizedMemoryServlet: updateSchemaMemoryNew - cnae2025 != null - " + cnae2025.getCode() + " " + cnae2025.getDescription());
			// Codigo CNAE2025 
			if (index2014 != null) {
				schema.getClaves().getClave().get(index2014).setValor(cnae2025.getCodeWithoutPoint());
			} else {
				Clave c2014 = new Clave();
				c2014.setCodigo(new BigInteger(D2DepositHeaderKey.IDA02014.getCode()));
				c2014.setValor(cnae2025.getCodeWithoutPoint());
				schema.getClaves().getClave().add(c2014);
			}
			// Descripción la del CNAE 2025 (como hace el D2)
			if (index2009 != null)
				schema.getClaves().getClave().get(index2009).setValor(cnae2025.getDescription());
		}
		
		try {
			byte[] b = Utils.writeXml(schema);
			Esquema sch = Utils.readXml(b);
			if(sch.getError() != null) {
				// TODO
				System.err.println("NormalizedMemoryServlet: updateSchemaMemoryNew - sch.getError() != null - " + sch.getError());
			} else DBConsults.insertDeposit(aonData.getDomain().getName(), b, aonData.getDomain().getId(), year, aonData.getUser().getLogin());
			System.out.println("NormalizedMemoryServlet: updateSchemaMemoryNew - OK");
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}

	private Map<String, String> createD2Deposit(AonData aonData, Integer companyId, String name, String type,Integer year) {
		System.out.println("NormalizedMemoryServlet: createD2Deposit"); 
		Enterprise enterprise = AON.getEnterprise(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), companyId);
		
		// CNAE de la actividad principal, solo si es de longitud 4
		String cnae = null;
		AonConfiguration configuration = AON.getConfiguration(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin());
		if (configuration.getMainActivity() != null) {
			String c = configuration.getMainActivity().getCnaeCode();
			if (c != null && c.length() == 4) {
				cnae = c.substring(0, 2) + "." + c.substring(2);
			}
		}

		// Datos registrales (Tomo, Folio, Nº Hoja). Si hay varios, se coge el último según la fecha de registro
		RecordData recordData = AON.getRecordDataStream(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), f -> f.getRegistryProperty().eq(companyId))
				.sorted(Comparator.comparing(RecordData::getRecordDate,Comparator.nullsFirst(Comparator.naturalOrder())).reversed())
				.findFirst().orElse(null);
		
		Map<D2DepositKey,String> mapFreeText = new HashMap<D2DepositKey, String>();
		Map<D2DepositKey, Double> ctxMem = new LinkedHashMap<D2DepositKey, Double>();
		Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Map<ID2DepositKey, String> ctxText = new LinkedHashMap<>();
		
        // COPIAR DATOS DEL EJERCICIO ANTERIOR
		boolean hasPreviousDeposit = isDigitalDeposit(aonData, year-1);
		if (hasPreviousDeposit) {
			Esquema previousSchema = getSchema(aonData, year-1);
			Map<D2DepositHeaderKey, Double> mapPrevious = getHeaderKeySchema(previousSchema);
			D2PrevioustoD2Current.fillBalance(ctx, mapPrevious);
			D2PrevioustoD2Current.fillPyg(ctx, mapPrevious);
			Map<D2DepositKey, Double> mapPreviousMem = getKeySchema(previousSchema);
			D2PrevioustoD2Current.fill2(ctxMem, mapPreviousMem);
			mapFreeText = getFreeTextKeySchema(previousSchema);			
			if (year >= 2024) {
				Map<ID2DepositKey, String> mapPreviousIde = getMapPreviousIde(previousSchema); // Identificacion
				D2PrevioustoD2Current.fillIde(ctxText, mapPreviousIde);
				Map<ID2DepositKey, String> mapPreviousItr = getMapPreviousItr(previousSchema); // Titular Real
				ctxText.putAll(mapPreviousItr);
				Map<ID2DepositKey, String> mapPreviousPre = getMapPreviousPre(previousSchema); // Presentante que hace la solicitud
				ctxText.putAll(mapPreviousPre);
			}
		}
		
		// DATOS DE LABORAL PARA DETERMINADAS CASILLAS (PERSONAL ASALARIADO)
		putPayrollData(aonData.getDomain(), aonData.getUser().getLogin(), year, hasPreviousDeposit, ctx, ctxMem);
		
		// CREAR EL DEPOSITO (XML) CON TODOS LOS DATOS
		byte[] b = Utils.CreateXml(ctx, ctxText, ctxMem, mapFreeText, enterprise, name, type, aonData.getDomain().getName(), year, cnae, recordData, hasPreviousDeposit);
		
		Integer id = DBConsults.insertDeposit(aonData.getDomain().getName(), b, aonData.getDomain().getId(), year, aonData.getUser().getLogin());
		Attach attach = AON.getAttach(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), f -> f.getIdProperty().eq(id), AttachType.REGISTRY);
		return getSchema(aonData, attach, year);
	}

	private void putPayrollData(Domain domain, String login, Integer year, boolean hasPreviousDeposit, Map<D2DepositHeaderKey, Double> ctx, Map<D2DepositKey, Double> ctxMem) {
		
		// Ejercicio actual
		double[] currentYear = getPayrollData(domain, login, year);
		ctx.put(D2DepositHeaderKey.IDA04001, currentYear[0]);   // Personal asalariado: Número medio de personas FIJO
		ctx.put(D2DepositHeaderKey.IDA04002, currentYear[1]);   // Personal asalariado: Número medio de personas NO FIJO
		ctx.put(D2DepositHeaderKey.IDA04010, currentYear[2]);   // Personal asalariado: Número medio de personas CON DISCAPACIDAD
		ctx.put(D2DepositHeaderKey.IDA04120, currentYear[3]);   // Personal asalariado: Al término del ejercicio FIJO HOMBRES
		ctx.put(D2DepositHeaderKey.IDA04121, currentYear[4]);   // Personal asalariado: Al término del ejercicio FIJO MUJERES
		ctx.put(D2DepositHeaderKey.IDA04122, currentYear[5]);   // Personal asalariado: Al término del ejercicio NO FIJO HOMBRES
		ctx.put(D2DepositHeaderKey.IDA04123, currentYear[6]);   // Personal asalariado: Al término del ejercicio NO FIJO MUJERES
		ctxMem.put(D2DepositKey.MA1398007, currentYear[0] + currentYear[1]); // Memoria Apartado 10 Otra informacion: Número medio de personas empleadas en el curso del ejercicio TOTAL EMPLEO MEDIO
		
		// Datos del ejercicio anterior, solo si no existe deposito del ejercicio anterior, pues si 
		// existe deposito del ejercicio anterior, esos datos se habrán copiado del ejercicio anterior
		if (!hasPreviousDeposit) {
			double[] previousYear = getPayrollData(domain, login, year-1);
			ctx.put(D2DepositHeaderKey.IDA040019, previousYear[0]);   // Personal asalariado: Número medio de personas FIJO
			ctx.put(D2DepositHeaderKey.IDA040029, previousYear[1]);   // Personal asalariado: Número medio de personas NO FIJO
			ctx.put(D2DepositHeaderKey.IDA040109, previousYear[2]);   // Personal asalariado: Número medio de personas CON DISCAPACIDAD
			ctx.put(D2DepositHeaderKey.IDA041209, previousYear[3]);   // Personal asalariado: Al término del ejercicio FIJO HOMBRES
			ctx.put(D2DepositHeaderKey.IDA041219, previousYear[4]);   // Personal asalariado: Al término del ejercicio FIJO MUJERES
			ctx.put(D2DepositHeaderKey.IDA041229, previousYear[5]);   // Personal asalariado: Al término del ejercicio NO FIJO HOMBRES
			ctx.put(D2DepositHeaderKey.IDA041239, previousYear[6]);   // Personal asalariado: Al término del ejercicio NO FIJO MUJERES
			ctxMem.put(D2DepositKey.MA13980079, previousYear[0] + previousYear[1]); // Memoria Apartado 10 Otra informacion: Número medio de personas empleadas en el curso del ejercicio TOTAL EMPLEO MEDIO
		}
		
	}
	
	private double[] getPayrollData(Domain domain, String login, Integer year) {
		
		try {
			JSONArray array = ContractServlet.getContractMediaList(domain, login, year);
			if (!array.isEmpty()) {
				double fixed = 0.0, 
					   unfixed = 0.0, 
					   discap = 0.0, 
					   fixedEndH = 0.0, 
					   fixedEndM = 0.0, 
					   unfixedEndH = 0.0, 
					   unfixedEndM = 0.0;
					
				for (int i = 0; i < array.length(); i++){
					fixed = fixed + array.getJSONObject(i).optDouble("fixed", 0.0);
					unfixed = unfixed + array.getJSONObject(i).optDouble("unfixed", 0.0);
					if (array.getJSONObject(i).getJSONObject("disability").getInt("id") != -1) {
						discap = discap + array.getJSONObject(i).optDouble("fixed", 0.0) + array.getJSONObject(i).optDouble("unfixed", 0.0);
					}
					if (array.getJSONObject(i).getJSONObject("gender").getInt("id") == 1) {
						fixedEndM = fixedEndM + array.getJSONObject(i).optDouble("end_fixed", 0.0);
						unfixedEndM = unfixedEndM + array.getJSONObject(i).optDouble("end_unfixed", 0.0);
					} else {
						fixedEndH = fixedEndH + array.getJSONObject(i).optDouble("end_fixed", 0.0);
						unfixedEndH = unfixedEndH + array.getJSONObject(i).optDouble("end_unfixed", 0.0);
					}
				}
				return new double[] {fixed, unfixed, discap, fixedEndH, fixedEndM, unfixedEndH, unfixedEndM}; 
			}
		} catch (Exception e) {
			// Pase lo que pase, que no afecte al resto de la creación del deposito (se devolverá todo ceros)
			e.printStackTrace();
		}
		return new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		
	}

	public Esquema getSchema(AonData aonData, Integer year){
		Attach attach = AON.getAttach(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), f -> f.getDomainProperty().eq(aonData.getDomain().getId())
				.and(f.getTypeProperty().eq((byte) 17))
				.and(f.getAttachDateProperty().eq(DBConsults.newAttachDate(year)))
			, AttachType.REGISTRY);
		return Utils.readXml(attach.getData());
	}
	
	private Map<ID2DepositKey, String> getMapPreviousIde(Esquema schema) {
		
		// Identificación: Claves que se van a copiar del ejercicio anterior a partir del 2024
		D2DepositHeaderKey[] keys = new D2DepositHeaderKey[] {
 			 // Mujeres y total miembros del órgano de administración
			 D2DepositHeaderKey.IDA04212 
			,D2DepositHeaderKey.IDA04213 
			 // Personal asalariado
			,D2DepositHeaderKey.IDA04001
			,D2DepositHeaderKey.IDA04002
			,D2DepositHeaderKey.IDA04010
			,D2DepositHeaderKey.IDA04120
			,D2DepositHeaderKey.IDA04121
			,D2DepositHeaderKey.IDA04122
			,D2DepositHeaderKey.IDA04123
			 // Presentación de cuentas (fechas inicio y fin)
			,D2DepositHeaderKey.IDA01102
			,D2DepositHeaderKey.IDA01101
		};
		
		Map<ID2DepositKey, String> map = new HashMap<>();
		for (ID2DepositKey key : keys) {
			for (Clave clave : schema.getClaves().getClave()) {
				if (clave.getCodigo().toString().equals(key.getCode()) && clave.getValor() != null) {
					map.put(key,clave.getValor());
				}
			}
		}		
		return map;
		
	}
	 
	private Map<ID2DepositKey, String> getMapPreviousItr(Esquema schema) {
		
		// Identificación del titular real a partir de 2024
		ArrayList<D2DepositHeaderKey> itrKeysList = new ArrayList<>();
		itrKeysList.add(D2DepositHeaderKey.ITR8080828); 
		itrKeysList.add(D2DepositHeaderKey.ITR8080829);
		itrKeysList.addAll(Arrays.asList(D2DepositConstants.ITR_KEYS_4));   // Apartado Ia
		itrKeysList.addAll(Arrays.asList(D2DepositConstants.ITR_KEYS_5));   // Apartado Ib
		itrKeysList.addAll(Arrays.asList(D2DepositConstants.ITR_KEYS_6));   // Apartado II
		itrKeysList.addAll(Arrays.asList(D2DepositConstants.ITR_KEYS_3_A)); // Apartado IIIa
		itrKeysList.addAll(Arrays.asList(D2DepositConstants.ITR_KEYS_3_B)); // Apartado IIIb
		itrKeysList.addAll(Arrays.asList(D2DepositConstants.ITR_KEYS_4_A)); // Apartado IVa
		itrKeysList.addAll(Arrays.asList(D2DepositConstants.ITR_KEYS_4_B)); // Apartado IVb
		
		Map<ID2DepositKey, String> map = new HashMap<>();
		for (ID2DepositKey key : itrKeysList) {
			for (Clave clave : schema.getClaves().getClave()) {
				if (clave.getCodigo().toString().equals(key.getCode()) && clave.getValor() != null) {
					map.put(key,clave.getValor());
				}
			}
		}
		
		return map;
		
	}
	
	private Map<ID2DepositKey, String> getMapPreviousPre(Esquema schema) {
		
		// Identificación del presentante: Claves que se van a copiar del ejercicio anterior a partir del 2024
		D2DepositFooterKey[] keys = new D2DepositFooterKey[] {
			 D2DepositFooterKey.PR8081201 // Nombre y apellidos del presentante que hace la solicitud	   
			,D2DepositFooterKey.PR8081202 // DNI del presentante que hace la solicitud	                  
			,D2DepositFooterKey.PR8081203 // Domicilio del presentante que hace la solicitud	            
			,D2DepositFooterKey.PR8081204 // Ciudad del presentante que hace la solicitud	         
			,D2DepositFooterKey.PR8081205 // Código postal del presentante que hace la solicitud               
			,D2DepositFooterKey.PR8081206 // Provincia del presentante que hace la solicitud            
			,D2DepositFooterKey.PR8081207 // Fax del presentante que hace la solicitud              
			,D2DepositFooterKey.PR8081208 // Teléfono del presentante que hace la solicitud                   
			,D2DepositFooterKey.PR8081209 // Correo electrónico del presentante que hace la solicitud
			,D2DepositFooterKey.PR8081001 // Registro mercantil
		};
		
		Map<ID2DepositKey, String> map = new HashMap<>();
		for (ID2DepositKey key : keys) {
			for (Clave clave : schema.getClaves().getClave()) {
				if (clave.getCodigo().toString().equals(key.getCode()) && clave.getValor() != null) {
					map.put(key,clave.getValor());
				}
			}
		}		
		return map;
		
	}
	
	private Map<D2DepositHeaderKey, Double> getHeaderKeySchema(Esquema schema) {
		Map<D2DepositHeaderKey, Double> map = new HashMap<D2DepositHeaderKey, Double>();
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.BALANCE.length; i++){
			D2DepositHeaderKey headerKey = D2DepositPreviousToCurrentConstants.BALANCE[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(headerKey.getCode())){
					String d = clave.getValor();
					Double value = d != null ? Double.parseDouble(d) : 0.0;
					map.put(headerKey, value);
				}
			}
		}
		
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.PYG.length; i++){
			D2DepositHeaderKey headerKey = D2DepositPreviousToCurrentConstants.PYG[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(headerKey.getCode())){
					String d = clave.getValor();
					Double value = d != null ? Double.parseDouble(d) : 0.0;
					map.put(headerKey, value);
				}
			}
		}
		
		return map;
	}

	private Map<D2DepositKey, Double> getKeySchema(Esquema schema) {
		Map<D2DepositKey, Double> map = new HashMap<D2DepositKey, Double>();
		
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.MEM_AP3.length; i++){
			D2DepositKey headerKey = D2DepositPreviousToCurrentConstants.MEM_AP3[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(headerKey.getCode())){
					String d = clave.getValor();
					Double value = d != null ? Double.parseDouble(d) : 0.0;
					map.put(headerKey, value);
				}
			}
		}
		
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.MEM_AP5.length; i++){
			D2DepositKey headerKey = D2DepositPreviousToCurrentConstants.MEM_AP5[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(headerKey.getCode())){
					String d = clave.getValor();
					Double value = d != null ? Double.parseDouble(d) : 0.0;
					map.put(headerKey, value);
				}
			}
		}
		
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.MEM_AP6.length; i++){
			D2DepositKey headerKey = D2DepositPreviousToCurrentConstants.MEM_AP6[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(headerKey.getCode())){
					String d = clave.getValor();
					Double value = d != null ? Double.parseDouble(d) : 0.0;
					map.put(headerKey, value);
				}
			}
		}
		
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.MEM_AP7.length; i++){
			D2DepositKey headerKey = D2DepositPreviousToCurrentConstants.MEM_AP7[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(headerKey.getCode())){
					String d = clave.getValor();
					Double value = d != null ? Double.parseDouble(d) : 0.0;
					map.put(headerKey, value);
				}
			}
		}
		
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.MEM_AP10.length; i++){
			D2DepositKey headerKey = D2DepositPreviousToCurrentConstants.MEM_AP10[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(headerKey.getCode())){
					String d = clave.getValor();
					Double value = d != null ? Double.parseDouble(d) : 0.0;
					map.put(headerKey, value);
				}
			}
		}
		
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.MEM_AP11.length; i++){
			D2DepositKey headerKey = D2DepositPreviousToCurrentConstants.MEM_AP11[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(headerKey.getCode())){
					String d = clave.getValor();
					Double value = d != null ? Double.parseDouble(d) : 0.0;
					map.put(headerKey, value);
				}
			}
		}
		
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.MEM_AP12.length; i++){
			D2DepositKey headerKey = D2DepositPreviousToCurrentConstants.MEM_AP12[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(headerKey.getCode())){
					String d = clave.getValor();
					Double value = d != null ? Double.parseDouble(d) : 0.0;
					map.put(headerKey, value);
				}
			}
		}
		
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.MEM_AP13.length; i++){
			D2DepositKey headerKey = D2DepositPreviousToCurrentConstants.MEM_AP13[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(headerKey.getCode())){
					String d = clave.getValor();
					Double value = d != null ? Double.parseDouble(d) : 0.0;
					map.put(headerKey, value);
				}
			}
		}
		
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.MEM_AP14.length; i++){
			D2DepositKey headerKey = D2DepositPreviousToCurrentConstants.MEM_AP14[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(headerKey.getCode())){
					String d = clave.getValor();
					Double value = d != null ? Double.parseDouble(d) : 0.0;
					map.put(headerKey, value);
				}
			}
		}
		
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.MEM_AP15.length; i++){
			D2DepositKey headerKey = D2DepositPreviousToCurrentConstants.MEM_AP15[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(headerKey.getCode())){
					String d = clave.getValor();
					Double value = d != null ? Double.parseDouble(d) : 0.0;
					map.put(headerKey, value);
				}
			}
		}
		
		return map;
	}
	
	private Map<D2DepositKey, String> getFreeTextKeySchema(Esquema schema){
		Map<D2DepositKey, String> map = new HashMap<D2DepositKey, String>();
		for(Integer i = 0; i < D2DepositPreviousToCurrentConstants.FREE_TEXT.length; i++){
			D2DepositKey key = D2DepositPreviousToCurrentConstants.FREE_TEXT[i];
			for(Clave clave :schema.getClaves().getClave()){
				if(clave.getCodigo().toString().equals(key.getCode())){
					map.put(key, clave.getValor());
				}
			}
		}
		return map;
	}

	@Override
	public void upload(AonData aonData, String data, String type, Integer year) {
		
		String domainName = aonData.getDomain().getName();
		Integer domainId = aonData.getDomain().getId();
		byte[] fileData = java.util.Base64.getDecoder().decode(data);
		String login = aonData.getUser().getLogin();
		Esquema schema = Utils.readXml(fileData);
		
		// Controlar si se ha leido bien el esquema, si no se ha leído bien o el ejercicio no es correcto, lanzar una excepción, que se capture en el cliente y se muestre un mensaje de error al usuario
		if (schema.getCabecera() == null || schema.getCabecera().getEjercicio() == null || !schema.getCabecera().getEjercicio().equals(BigInteger.valueOf(year))) {
			throw new AonCoreException("El fichero no es correcto.");
		}
		
		// Si todo ha ido bien, se graba el esquema en la base de datos
    	try {
			byte[] b = Utils.writeXml(schema);
			DBConsults.insertDeposit(domainName, b, domainId, year, login);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
    	
	}

	@Override
	public void uploadDocument(AonData aonData, MemoryFiles mf, String data, String type) {
		MimeType m = MimeType.safeValueFromContenType(type);
		byte[] fileData = java.util.Base64.getDecoder().decode(data);
		if(mf.getId() != null && mf.getId() > 0){			
			DBConsults.updateMemoryFile(aonData.getDomain().getName(), aonData.getDomain().getId(),
					m.value(), fileData, mf.getId());
		} else {
			DBConsults.insertMemoryFile(aonData.getDomain().getName(), aonData.getDomain().getId(),
					m.value(), fileData, mf.getName(), aonData.getUser().getLogin());
		}       
	}

	// Equivalencia del codigo CNAE2009 con el codigo CNAE2025, si es única
	// cnae2009 puede venir con punto o sin punto
	private static CNAE2025 getCnae2025fromCnae2009(String cnae2009) {
		
		if (AonStringUtils.isNotBlank(cnae2009)) {
			if (AonStringUtils.containsNone(cnae2009, ".")) {
				cnae2009 = AonStringUtils.left(cnae2009, 2) + "." + AonStringUtils.right(cnae2009, 2);
			}
			
			CNAE2009ToCNAE2025 conv = CNAE2009ToCNAE2025.valueOfCode(cnae2009);
			if (conv != null) {
				String[] cnaes2025 = conv.getCode2025();
				if (cnaes2025.length == 1) {
					CNAE2025 cnae2025 = CNAE2025.valueOfCode(cnaes2025[0]);
					if (cnae2025 != null) {
						return cnae2025; 
					}
				}					
			} 
		}
		return null;

	}
	
	
}
