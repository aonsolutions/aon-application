package com.esferalia.aon.gwt.fiscal.deposit.server.normalizedMemory;

import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2Deposit;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositPreviousToCurrentConstants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016;
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
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Utils;
import com.esferalia.aon.occam.server.accounting.AccMiningMVELContext;
import com.esferalia.aon.watson.util.AonStringUtils;


public class NormalizedMemoryServlet extends AonRemoteServiceServlet implements INormalizedMemory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String D2_DEPOSIT_SCHEMA = "d2DepositSchema";
	private static final String D2_DEPOSIT_FILE = "D2DepositFile";
	private static final String D2_DEPOSIT_MIMETYPE = "D2DepositMimeType";
	private static final String MODIFY_D2_DEPOSIT_SCHEMA = "ModifyD2DepositSchema";
	private static final String TRUE = "true";
	
	private static final String D2_FILE_MEMORY = "Memoria";
	private static final String D2_FILE_AUTOCARTERA_MODEL = "Modelo de Autocartera";
	private static final String D2_FILE_GESTION = "Informe de Gesti\u00f3n";
	private static final String D2_FILE_AUDIT = "Informe de Auditor\u00eda";
	private static final String D2_FILE_CONVOC = "Anuncios de Convocatoria";
	private static final String D2_FILE_SICAV = "Certificaci\u00f3n SICAV";

	public String getLoggedUser() {
		return AonServletUtils.getLoggedUser();
	}
	
	public Map<String, String> getSchema(String cif,
			Integer domainId, Boolean textMode, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		// String cif = DBConsults.getCIF(domain, domainId);
		Esquema schema = null;
		if(textMode)
			schema = (Esquema) request.getSession().getAttribute(
						D2_DEPOSIT_SCHEMA + cif + year);
		if (schema == null) {
			// System.out.println(domainId);
			if (textMode) {
				schema = DBConsults.getDeposit(domain, domainId, cif);
			} else
				schema = DBConsults.getDeposit(domain, domainId, year, this.getUserLogin());
			request.getSession().setAttribute(D2_DEPOSIT_SCHEMA + cif + year, schema);
		}
		
		List<Clave> claves = schema.getClaves().getClave();
		Map<String, String> map = new HashMap<String, String>();
		String type = schema.getCabecera().getTipoCuestionario();
		map.put(D2DepositConstants.DEPOSIT_TYPE, type);
		for (Integer i = 0; i < claves.size(); i++) {
			if(!map.containsKey(claves.get(i).getCodigo().toString()))
				map.put(claves.get(i).getCodigo().toString(), claves.get(i).getValor());
		}
		return map;
	}

	public void updateSchema(String cif, Integer domainId, String key, String value, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		Esquema schema = (Esquema) request.getSession().getAttribute(D2_DEPOSIT_SCHEMA + cif +year);
		Boolean isNew = true;
		for (Integer i = 0; i < schema.getClaves().getClave().size(); i++) {
			if (schema.getClaves().getClave().get(i).getCodigo().toString().equals(key)) {
				schema.getClaves().getClave().get(i).setValor(value);
				isNew = false;
			}
		}
		if (isNew) {
			Clave c = new Clave();
			c.setCodigo(BigInteger.valueOf(Integer.parseInt(key)));
			c.setValor(value);
			schema.getClaves().getClave().add(c);
		}
		// ------
		// calculate(schema);
		// ------
		
		request.getSession().setAttribute(D2_DEPOSIT_SCHEMA + cif + year, schema);
		request.getSession().setAttribute(MODIFY_D2_DEPOSIT_SCHEMA + cif + year, TRUE);
	}

	public Boolean isDigitalDeposit(Integer domainId, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		return DBConsults.isDigitalDeposit(domain, domainId, year);
	}

	public Boolean isModify(String cif, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String modify = (String) request.getSession().getAttribute(
				MODIFY_D2_DEPOSIT_SCHEMA + cif + year);
		return modify != null && modify.equals(TRUE);
	}

	public void clearSession(String cif, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		request.getSession().removeAttribute(D2_DEPOSIT_SCHEMA + cif + year);
		request.getSession().removeAttribute(MODIFY_D2_DEPOSIT_SCHEMA + cif + year);
	}

	public void saveDeposit(String cif, Integer domainId, Boolean textMode, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		Esquema schema = (Esquema) request.getSession().getAttribute(
				D2_DEPOSIT_SCHEMA + cif + year);

		try {
			byte[] b = Utils.writeXml(schema);
			if (textMode) {
				DBConsults.insertDeposit(domain, b, domainId, cif, this.getUserLogin());
			} else
				DBConsults.insertDeposit(domain, b, domainId, year, this.getUserLogin());
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		request.getSession().removeAttribute(MODIFY_D2_DEPOSIT_SCHEMA + cif + year);
	}
	
	
	public String[] getDepositExercises(Integer domainId){
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		return DBConsults.getDepositExercises(domain, domainId, getUserLogin());
	}
	
	public void saveDeposit(String cif, Integer domainId, D2Deposit d2Deposit2014, Boolean textMode, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		Esquema schema = DBConsults.getDeposit(domain, domainId, year, this.getUserLogin());
		schema = D2DepositToSchema(schema, d2Deposit2014);
		try {
			byte[] b = Utils.writeXml(schema);
			if (textMode) {
				DBConsults.insertDeposit(domain, b, domainId, cif, this.getUserLogin());
			} else
				DBConsults.insertDeposit(domain, b, domainId, year, this.getUserLogin());
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		request.getSession().removeAttribute(MODIFY_D2_DEPOSIT_SCHEMA + cif + year);
	}
	
	private Esquema D2DepositToSchema(Esquema schema, D2Deposit d2Deposit) {
		Esquema s = new Esquema();
		s.setCabecera(schema.getCabecera());
		Claves claves = new Claves();

		for(String key : d2Deposit.getMapDraft().keySet()){
			if(!key.equals("DepositType")){
				Clave clave = new Clave();
				clave.setCodigo(BigInteger.valueOf(Integer.parseInt(key)));
				clave.setValor(d2Deposit.getMapDraft().get(key));
				claves.getClave().add(clave);
			}
		}
		
		s.setClaves(claves);
		
		return s;
	}
	
	public Vector<MemoryTemplate> getDigitalDepositTemplates(Integer domainId, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);

		return getDepositText(domain, domainId, year);

	}

	private Vector<MemoryTemplate> getDepositText(String domain, Integer domainId, Integer year) {
		return AON.getAttachStream(domain, domainId, getUserLogin(),
				f -> f.getDomainProperty().eq(domainId)
				.and(f.getTypeProperty().eq(RegistryAttachmentType.D2_DEPOSIT.value())),
			AttachType.REGISTRY, true).map(r -> new MemoryTemplate()
										.setId(r.getId())
										.setName(r.getDescription())
										.setD2Deposit(getD2DepositTreeObject(r.getId(), year, r.getData())))
			.collect(Collectors.toCollection(Vector::new));
	}
	
	private D2Deposit getD2DepositTreeObject(Integer id, Integer year, byte[] data){
		if(data != null){
			HttpServletRequest request = getThreadLocalRequest();
			D2Deposit d2 = new D2Deposit();
			
			Esquema schema = DBConsults.readXml(data);
			
			request.getSession().setAttribute(D2_DEPOSIT_SCHEMA + id + year, schema);
			
			List<Clave> claves = schema.getClaves().getClave();
			Map<String, String> map = new HashMap<String, String>();
			String type = schema.getCabecera().getTipoCuestionario();
			map.put(D2DepositConstants.DEPOSIT_TYPE, type);
			for (Integer i = 0; i < claves.size(); i++) {
				if(!map.containsKey(claves.get(i).getCodigo().toString()))
					map.put(claves.get(i).getCodigo().toString(), claves.get(i).getValor());
			}
			d2.setMap(map);
			d2.setMapDraft(map);
			return d2;
		}
		return null;
	}

	public MemoryTemplate createTextMemory(Integer domainId, String name, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain  = AonServletUtils.getRequestDomainName(request);

		byte[] data = Utils.CreateXml("", name);
		Integer id = DBConsults.insertDepositText(domain, name, data, domainId, this.getUserLogin());

		return new MemoryTemplate().setId(id).setName(name)
				.setD2Deposit(getD2DepositTreeObject(id, year, data));
	}
	
	public Map<String, String> updateTexts(MemoryTemplate mt, HashMap<D2DepositKey, Boolean> freeTextMap, Integer domainId, String cif, Map<String, String> map) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);

		Esquema schema = DBConsults.getDeposit(domain, domainId, mt.getId()
				.toString());
		
		for (Integer i = 0; i < schema.getClaves().getClave().size(); i++) {
			if (freeTextMap.get(D2DepositKey.MAT19019001) && schema.getClaves().getClave().get(i).getCodigo().toString()
					.equals(D2DepositKey.MAT19019001.getCode())) { 
				
				map.put(D2DepositKey.MAT19019001.getCode(),  schema.getClaves().getClave().get(i).getValor());
			} 
			else if (freeTextMap.get(D2DepositKey.MAT29029001) && schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT29029001.getCode())) { 
				
				map.put(D2DepositKey.MAT29029001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (freeTextMap.get(D2DepositKey.MAT39039001) && schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT39039001.getCode())) { 
				
				map.put(D2DepositKey.MAT39039001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (freeTextMap.get(D2DepositKey.MAT49049001) && schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT49049001.getCode())) { 
				
				map.put(D2DepositKey.MAT49049001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (freeTextMap.get(D2DepositKey.MAT59059001) && schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT59059001.getCode())) { 
 				
				map.put(D2DepositKey.MAT59059001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} 
			else if (freeTextMap.get(D2DepositKey.MAT69069001) && schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT69069001.getCode())) {
				
				map.put(D2DepositKey.MAT69069001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} 
			else if (freeTextMap.get(D2DepositKey.MAT79079001) && schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT79079001.getCode())) {
				
				map.put(D2DepositKey.MAT79079001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} 
			else if (freeTextMap.get(D2DepositKey.MAT89089001) && schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT89089001.getCode())) { 
				
				map.put(D2DepositKey.MAT89089001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (freeTextMap.get(D2DepositKey.MAT99099001) && schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT99099001.getCode())) { 
				
				map.put(D2DepositKey.MAT99099001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (freeTextMap.get(D2DepositKey.MAT119119001) && schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT119119001.getCode())) { 
				
				map.put(D2DepositKey.MAT119119001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (freeTextMap.get(D2DepositKey.MAT129129001) && schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT129129001.getCode())) { 
				
				map.put(D2DepositKey.MAT129129001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} else if (freeTextMap.get(D2DepositKey.MAT139139001) && schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT139139001.getCode())) { 
				
				map.put(D2DepositKey.MAT139139001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} else if (freeTextMap.get(D2DepositKey.MAT149149001) && schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT149149001.getCode())) { 
				
				map.put(D2DepositKey.MAT149149001.getCode(), schema.getClaves().getClave().get(i).getValor());

			}
		}
		return map;
	}

	public String getDateStr(Date date) {
		return "";
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

	public Integer getParentDomain(Integer domainId) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		return AON.getDomain(domain, domainId, getUserLogin()).getParentId();
	}

	public String getDomainName(Integer domainId) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		return AON.getDomain(domain, domainId, getUserLogin()).getName();
	}

	public Map<String, String> importAll(String type, String ejercicio, MemoryTemplate mt,
			Integer domainId, String cif, Map<String, String> map, Integer year) {
		String domainName = getDomainName(domainId);
		if ("Balance (I.S.)".equals(type)) {
			if("2013".equals(ejercicio)) {
				Mod2002013 mod2002013 = FISCAL.getMod2002013ByYear(domainName, domainId, getUserLogin(), 2013);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002013toD2.fillBalance(ctx, mod2002013);

				for(D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2014".equals(ejercicio)) {
				Mod2002014 mod2002014 = FISCAL.getMod2002014ByYear(domainName, domainId, getUserLogin(), 2014);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002014toD2.fillBalance(ctx, mod2002014, year);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2015".equals(ejercicio)){
				Mod2002015 mod2002015 = FISCAL.getMod2002015ByYear(domainName, domainId, getUserLogin(), 2015);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002015toD2.fillBalance(ctx, mod2002015, year.toString().equals(ejercicio));

				for(D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2016".equals(ejercicio)){
				Mod2002016 mod2002016 = FISCAL.getMod2002016ByYear(domainName, domainId, getUserLogin(), 2016);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002016toD2.fillBalance(ctx, mod2002016);

				for(D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			}
		} else if("Perdidas y ganancias (I.S.)".equals(type)) {
			if("2013".equals(ejercicio)) {
				Mod2002013 mod2002013 = FISCAL.getMod2002013ByYear(domainName, domainId, getUserLogin(), 2013);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002013toD2.fillPyg(ctx, mod2002013);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2014".equals(ejercicio)) {
				Mod2002014 mod2002014 = FISCAL.getMod2002014ByYear(domainName, domainId, getUserLogin(), 2014);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002014toD2.fillPyg(ctx, mod2002014, year);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2015".equals(ejercicio)){
				Mod2002015 mod2002015 = FISCAL.getMod2002015ByYear(domainName, domainId, getUserLogin(), 2015);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002015toD2.fillPyg(ctx, mod2002015, year.toString().equals(ejercicio));

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if("2016".equals(ejercicio)){
				Mod2002016 mod2002016 = FISCAL.getMod2002016ByYear(domainName, domainId, getUserLogin(), 2016);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002016toD2.fillPyg(ctx, mod2002016);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			}
		} else if("ECPN (I.S.)".equals(type)) {
			if ("2013".equals(ejercicio)) {
				Mod2002013 mod2002013 = FISCAL.getMod2002013ByYear(domainName, domainId, getUserLogin(), 2013);
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
				Mod2002014 mod2002014 = FISCAL.getMod2002014ByYear(domainName, domainId, getUserLogin(), 2014);
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
				Mod2002015 mod2002015 = FISCAL.getMod2002015ByYear(domainName, domainId, getUserLogin(), 2015);
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
				Mod2002016 mod2002016 = FISCAL.getMod2002016ByYear(domainName, domainId, getUserLogin(), 2016);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002016toD2.fillEcpn(ctx, mod2002016);
				Mod2002016toD2.fillEcpn2(ctx, mod2002016);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			}
		} else if ("Memoria (Deposito.xml)".equals(type)) {
			byte[] b = getFile(domainId);
			try {
				Esquema schema = Utils.readXml(b);
				if(schema.getCabecera().getCIF().equals(cif)){
					List<Clave> claves = schema.getClaves().getClave();
					map = new HashMap<String, String>();
					String typeSch = schema.getCabecera().getTipoCuestionario();
					map.put(D2DepositConstants.DEPOSIT_TYPE, typeSch);
					for (Integer i = 0; i < claves.size(); i++) {
						if(!map.containsKey(claves.get(i).getCodigo().toString()))
							map.put(claves.get(i).getCodigo().toString(), claves.get(i).getValor());
					}
				}
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		return map;

	}

	public void delete(Integer domainId, String document, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		clearSession(document, year);
		DBConsults.deleteDeposit(domain, domainId, year);
		
		DBConsults.deleteMemoryFile(domain, domainId, D2_FILE_MEMORY);
		DBConsults.deleteMemoryFile(domain, domainId, D2_FILE_AUTOCARTERA_MODEL);
		DBConsults.deleteMemoryFile(domain, domainId, D2_FILE_GESTION);
		DBConsults.deleteMemoryFile(domain, domainId, D2_FILE_AUDIT);
		DBConsults.deleteMemoryFile(domain, domainId, D2_FILE_CONVOC);
		DBConsults.deleteMemoryFile(domain, domainId, D2_FILE_SICAV);
	}

	public void deleteFreeText(Integer domainId, Integer rattachId, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		clearSession(rattachId.toString(), year);
		
		DBConsults.deleteText(domain, domainId, rattachId);
	}
	
	public Map<String, String> createD2Deposit(Integer domainId, Integer id, String name,
			String type,Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domainName = AonServletUtils.getRequestDomainName(request);
		Enterprise enterprise = AON.getEnterprise(domainName, domainId, this.getUserLogin(), id);
		
		Map<D2DepositKey,String> mapFreeText = new HashMap<D2DepositKey, String>();
		Map<D2DepositKey, Double> ctxMem = new LinkedHashMap<D2DepositKey, Double>();
		Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		
		if(isDigitalDeposit(domainId, year-1)){
			Esquema previousSchema = getSchema(domainId, year-1);
			Map<D2DepositHeaderKey, Double> mapPrevious = getHeaderKeySchema(previousSchema);
			D2PrevioustoD2Current.fillBalance(ctx, mapPrevious);
			D2PrevioustoD2Current.fillPyg(ctx, mapPrevious);
			Map<D2DepositKey, Double> mapPreviousMem = getKeySchema(previousSchema);
			D2PrevioustoD2Current.fill2(ctxMem, mapPreviousMem);
			mapFreeText = getFreeTextKeySchema(previousSchema);
		}
		
		byte[] b = Utils.CreateXml(ctx, ctxMem, mapFreeText, enterprise, name, type, domainName, year);
		
		DBConsults.insertDeposit(domainName, b, domainId, year, this.getUserLogin());
		return getSchema(enterprise.getDocument(), domainId, false, year);
	}
	
	public Map<String, String> calculate(Map<String, String> map) {
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
		
		ctx.setExpressionMap(D2Compute.COMPUTE_MAP_CURRENT);
		map = calculate(map, ctx, D2Compute.COMPUTE_MAP_CURRENT);
		
		ctx.setExpressionMap(D2Compute.COMPUTE_MAP_PREVIOUS);
		map = calculate(map, ctx, D2Compute.COMPUTE_MAP_PREVIOUS);

		return map;
	}
	
	public Map<String, String> calculate(Map<String, String> map, AccMiningMVELContext ctx, Map<String, String> computeMap){
		for (String key : computeMap.keySet()) {
			String expression = computeMap.get(key);
			Object ret = ctx.evaluateExpression(key,expression);
			if (ret instanceof Double) {
				Double calculated = (Double) ret;
				ctx.put("Q"+key, calculated);
				
				if(map.containsKey(key)) map.remove(key);
				map.put(key, round(calculated, 2).toString());
			}
		}
		return map;
	}
	
	private byte[] getFile(Integer domainId) {
		HttpServletRequest request = getThreadLocalRequest();
		return (byte[]) request.getSession().getAttribute(D2_DEPOSIT_FILE+domainId);
	}
	
	private String getMimeType(Integer domainId) {
		HttpServletRequest request = getThreadLocalRequest();
		return (String) request.getSession().getAttribute(D2_DEPOSIT_MIMETYPE+domainId);
	}
	
	public Vector<MemoryFiles> getMemoryFiles(Integer domainId){
		HttpServletRequest request = getThreadLocalRequest();
		String domain  = AonServletUtils.getRequestDomainName(request);

		Vector<MemoryFiles> ms = new Vector<MemoryFiles>();
		
		MemoryFiles m = new MemoryFiles();
		Vector<Integer> id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_MEMORY);
		m.setId(id.get(0));m.setBool(id.get(0) != -1);m.setName(D2_FILE_MEMORY);
		if(id.get(0) != -1){
			MimeType mimeType = MimeType.values()[id.get(1)];
			m.setMimeTypeName(mimeType.getName());
			m.setMimeTypeNumber(id.get(1));
		}
		ms.add(m);
		
		MemoryFiles m2 = new MemoryFiles();
		id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_AUTOCARTERA_MODEL);
		m2.setId(id.get(0));m2.setBool(id.get(0) != -1);m2.setName(D2_FILE_AUTOCARTERA_MODEL);
		if(id.get(0) != -1){
			MimeType mimeType = MimeType.values()[id.get(1)];
			m2.setMimeTypeName(mimeType.getName());
			m2.setMimeTypeNumber(id.get(1));
		}
		ms.add(m2);
		
		MemoryFiles m3 = new MemoryFiles();
		id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_GESTION);
		m3.setId(id.get(0));m3.setBool(id.get(0) != -1);m3.setName(D2_FILE_GESTION);
		if(id.get(0) != -1){
			MimeType mimeType = MimeType.values()[id.get(1)];
			m3.setMimeTypeName(mimeType.getName());
			m3.setMimeTypeNumber(id.get(1));
		}		
		ms.add(m3);

		MemoryFiles m4 = new MemoryFiles();
		id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_AUDIT);
		m4.setId(id.get(0));m4.setBool(id.get(0) != -1);m4.setName(D2_FILE_AUDIT);
		if(id.get(0) != -1){
			MimeType mimeType = MimeType.values()[id.get(1)];
			m4.setMimeTypeName(mimeType.getName());
			m4.setMimeTypeNumber(id.get(1));
		}		
		ms.add(m4);

		MemoryFiles m5 = new MemoryFiles();
		id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_CONVOC);
		m5.setId(id.get(0));m5.setBool(id.get(0) != -1);m5.setName(D2_FILE_CONVOC);
		if(id.get(0) != -1){
			MimeType mimeType = MimeType.values()[id.get(1)];
			m5.setMimeTypeName(mimeType.getName());
			m5.setMimeTypeNumber(id.get(1));
		}		
		ms.add(m5);

		MemoryFiles m6 = new MemoryFiles();
		id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_SICAV);
		m6.setId(id.get(0));m6.setBool(id.get(0) != -1);m6.setName(D2_FILE_SICAV);
		if(id.get(0) != -1){
			MimeType mimeType = MimeType.values()[id.get(1)];
			m6.setMimeTypeName(mimeType.getName());
			m6.setMimeTypeNumber(id.get(1));
		}		
		ms.add(m6);
		
		return ms;
	}
	
	public void deleteMemoryFile(Integer domainId, Integer id){
		HttpServletRequest request = getThreadLocalRequest();
		String domain  = AonServletUtils.getRequestDomainName(request);
		DBConsults.deleteMemoryFile(domain, domainId, id);
		
	}
	
	public void deleteMemoryFile(Integer domainId, String name){
		HttpServletRequest request = getThreadLocalRequest();
		String domain  = AonServletUtils.getRequestDomainName(request);
		DBConsults.deleteMemoryFile(domain, domainId, name);
		
	}
	
	public MemoryFiles insertMemoryFile(Integer domainId, MemoryFiles mf){
		HttpServletRequest request = getThreadLocalRequest();
		String domain  = AonServletUtils.getRequestDomainName(request);
		byte[] b = getFile(domainId);
		byte  m = (byte) MimeType.get(getMimeType(domainId)).ordinal();
		if(mf.getBool()){			
			DBConsults.updateMemoryFile(domain, domainId, m, b, mf.getId());
		}
		else {
			Integer id = DBConsults.insertMemoryFile(domain, domainId, m, b, mf.getName(), this.getUserLogin());
			mf.setId(id);
		}
		return mf;
	}
	
	public void updateSchemaMemory(Boolean bool, Integer domainId, String key, Integer year){
		HttpServletRequest request = getThreadLocalRequest();
		String domain  = AonServletUtils.getRequestDomainName(request);
		Esquema schema = DBConsults.getDeposit(domain, domainId, year, this.getUserLogin());
		if(D2DepositFooterKey.PR8080805.getCode().equals(key))
			schema.getCabecera().setMemoriaNormalizada(!bool);
		
		List<Clave> claves = schema.getClaves().getClave();
		Boolean esta = false;
		for (Integer i = 0; i < claves.size(); i++) {
			if(schema.getClaves().getClave().get(i).getCodigo().toString().equals(key)){
				if(D2DepositFooterKey.PR8080805.getCode().equals(key))
					schema.getClaves().getClave().get(i).setValor(bool?"0":"1");
				else schema.getClaves().getClave().get(i).setValor(bool?"1":"0");
				esta = true;
			}
		}
		if(!esta) {
			Clave clave = new Clave();
			clave.setCodigo(new BigInteger(key));
			if(D2DepositFooterKey.PR8080805.getCode().equals(key))
				clave.setValor(bool?"0":"1");
			else clave.setValor(bool?"1":"0");
			schema.getClaves().getClave().add(clave);
		}
		
		try {
			byte[] b = Utils.writeXml(schema);
			DBConsults.insertDeposit(domain, b, domainId, year, this.getUserLogin());
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------- ENTERPRISE
	@Override
	public LinkedList<Enterprise> getParentEnterprises(String domainName, int domain,
			String query){
		return AON.getParentEnterprises(domainName, domain, this.getUserLogin(), query);		
	}

	public Esquema getSchema(Integer domainId, Integer year){
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		return DBConsults.getDeposit(domain, domainId, year, "");
	}
	
	public Map<D2DepositHeaderKey, Double> getHeaderKeySchema(Esquema schema) {
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

	public Map<D2DepositKey, String> getFreeTextKeySchema(Esquema schema){
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
 	
	public Map<D2DepositKey, Double> getKeySchema(Esquema schema) {
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
	
	
	public static Double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
}
