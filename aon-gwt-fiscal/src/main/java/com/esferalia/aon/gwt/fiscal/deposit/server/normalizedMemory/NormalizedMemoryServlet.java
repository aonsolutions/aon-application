package com.esferalia.aon.gwt.fiscal.deposit.server.normalizedMemory;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.xml.bind.JAXBException;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
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
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2Compute;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2PrevioustoD2Current;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves.Clave;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002013toD2;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002014toD2;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002015toD2;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002016toD2;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002017toD2;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002018toD2;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Utils;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "D2Deposit", urlPatterns = { "/aon_gwt_aio/ms/gwt_deposit" })
public class NormalizedMemoryServlet extends AonRemoteServiceServlet implements INormalizedMemory {

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
		Map<String, String> map = new HashMap<String, String>();
		if(year>=2017) {
			map.put(D2DepositFooterKey.PR8080827.getCode(), "1");
		}
		try {
			Esquema schema = Utils.readXml(attach.getData());		
			
			List<Clave> claves = schema.getClaves().getClave();
			if(schema.getError() != null) {
				map.put("error", schema.getError());
			}
			String type = schema.getCabecera().getTipoCuestionario();
			map.put(D2DepositConstants.DEPOSIT_TYPE, type);
			for (Integer i = 0; i < claves.size(); i++) {
				if(!map.containsKey(claves.get(i).getCodigo().toString()))
					map.put(claves.get(i).getCodigo().toString(), claves.get(i).getValor());
			}
			
			if(year != null && year != -1  && !schema.getCabecera().getEjercicio().equals(BigInteger.valueOf(year))) {
				saveDeposit(aonData, map, year);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			} else DBConsults.insertDeposit(aonData.getDomain().getName(), b, aonData.getDomain().getId(), year, aonData.getUser().getLogin());
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
			byte[] b = Utils.writeXml(schema);
			Esquema sch = Utils.readXml(b);
			if(sch.getError() != null) {
				// TODO
			} else DBConsults.insertDeposit(aonData.getDomain().getName(), b, aonData.getDomain().getId(), year, aonData.getUser().getLogin());
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}

	private Map<String, String> createD2Deposit(AonData aonData, Integer companyId, String name, String type,Integer year) {
		Enterprise enterprise = AON.getEnterprise(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), companyId);
		
		Map<D2DepositKey,String> mapFreeText = new HashMap<D2DepositKey, String>();
		Map<D2DepositKey, Double> ctxMem = new LinkedHashMap<D2DepositKey, Double>();
		Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();

		if(isDigitalDeposit(aonData, year-1)){
			Esquema previousSchema = getSchema(aonData, year-1);
			Map<D2DepositHeaderKey, Double> mapPrevious = getHeaderKeySchema(previousSchema);
			D2PrevioustoD2Current.fillBalance(ctx, mapPrevious);
			D2PrevioustoD2Current.fillPyg(ctx, mapPrevious);
			Map<D2DepositKey, Double> mapPreviousMem = getKeySchema(previousSchema);
			D2PrevioustoD2Current.fill2(ctxMem, mapPreviousMem);
			mapFreeText = getFreeTextKeySchema(previousSchema);
		}
		
		byte[] b = Utils.CreateXml(ctx, ctxMem, mapFreeText, enterprise, name, type, aonData.getDomain().getName(), year);
		
		Integer id = DBConsults.insertDeposit(aonData.getDomain().getName(), b, aonData.getDomain().getId(), year, aonData.getUser().getLogin());
		Attach attach = AON.getAttach(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), f -> f.getIdProperty().eq(id), AttachType.REGISTRY);
		return getSchema(aonData, attach, year);
	}

	public Esquema getSchema(AonData aonData, Integer year){
		Attach attach = AON.getAttach(aonData.getDomain().getName(), aonData.getDomain().getId(), aonData.getUser().getLogin(), f -> f.getDomainProperty().eq(aonData.getDomain().getId())
				.and(f.getTypeProperty().eq((byte) 17))
				.and(f.getAttachDateProperty().eq(DBConsults.newAttachDate(year)))
			, AttachType.REGISTRY);
	
		return Utils.readXml(attach.getData());
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
}
