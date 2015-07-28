package com.esferalia.aon.gwt.fiscal.server.normalizedMemory;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;

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

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.client.tree.node.D2DepositTreeObject;
import com.esferalia.aon.gwt.fiscal.shared.D2Deposit2014;
import com.esferalia.aon.gwt.fiscal.shared.MemoryFiles;
import com.esferalia.aon.gwt.fiscal.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2Compute;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2MVELContext;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves.Clave;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002013toD2;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002014toD2;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Utils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class NormalizedMemoryServlet extends RemoteServiceServlet implements
		INormalizedMemory {

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
	private static final String D2_FILE_GESTION = "Informe de Gestion";
	private static final String D2_FILE_AUDIT = "Informe de Auditoria";
	private static final String D2_FILE_CONVOC = "Anuncios de Convocatoria";
	private static final String D2_FILE_SICAV = "Certificacion SICAV";

	public Integer initialize() {
		return null;
	}

	public Map<String, String> getSchema(String cif,
			Integer domainId, Boolean textMode) {
		String domain = AonUtil.getDomainName();
		HttpServletRequest request = getThreadLocalRequest();
		// String cif = DBConsults.getCIF(domain, domainId);
		Esquema schema = null;
		if(textMode)
			schema = (Esquema) request.getSession().getAttribute(
						D2_DEPOSIT_SCHEMA + cif);
		if (schema == null) {
			// System.out.println(domainId);
			if (textMode) {
				schema = DBConsults.getDeposit(domain, domainId, cif);
			} else
				schema = DBConsults.getDeposit(domain, domainId);
			request.getSession().setAttribute(D2_DEPOSIT_SCHEMA + cif, schema);
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

	public void updateSchema(String cif, Integer domainId, String key, String value) {
		HttpServletRequest request = getThreadLocalRequest();
		Esquema schema = (Esquema) request.getSession().getAttribute(D2_DEPOSIT_SCHEMA + cif);
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
		
		request.getSession().setAttribute(D2_DEPOSIT_SCHEMA + cif, schema);
		request.getSession().setAttribute(MODIFY_D2_DEPOSIT_SCHEMA + cif, TRUE);
	}

	public Boolean isDigitalDeposit(Integer domainId) {
		String domain = AonUtil.getDomainName();
		return DBConsults.isDigitalDeposit(domain, domainId);
	}

	public Boolean isModify(String cif) {
		HttpServletRequest request = getThreadLocalRequest();
		String modify = (String) request.getSession().getAttribute(
				MODIFY_D2_DEPOSIT_SCHEMA + cif);
		return modify != null && modify.equals(TRUE);
	}

	public void clearSession(String cif) {
		HttpServletRequest request = getThreadLocalRequest();
		request.getSession().removeAttribute(D2_DEPOSIT_SCHEMA + cif);
		request.getSession().removeAttribute(MODIFY_D2_DEPOSIT_SCHEMA + cif);
	}

	public void saveDeposit(String cif, Integer domainId, Boolean textMode) {
		String domain = AonUtil.getDomainName();
		HttpServletRequest request = getThreadLocalRequest();
		Esquema schema = (Esquema) request.getSession().getAttribute(
				D2_DEPOSIT_SCHEMA + cif);

		try {
			byte[] b = Utils.writeXml(schema);
			if (textMode) {
				DBConsults.insertDeposit(domain, b, domainId, cif);
			} else
				DBConsults.insertDeposit(domain, b, domainId);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		request.getSession().removeAttribute(MODIFY_D2_DEPOSIT_SCHEMA + cif);
	}
	
	public void saveDeposit(String cif, Integer domainId, D2Deposit2014 d2Deposit2014, Boolean textMode) {
		String domain = AonUtil.getDomainName();
		HttpServletRequest request = getThreadLocalRequest();
		Esquema schema = DBConsults.getDeposit(domain, domainId);
		schema = D2Deposit2014ToSchema(schema, d2Deposit2014);
		try {
			byte[] b = Utils.writeXml(schema);
			if (textMode) {
				DBConsults.insertDeposit(domain, b, domainId, cif);
			} else
				DBConsults.insertDeposit(domain, b, domainId);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		request.getSession().removeAttribute(MODIFY_D2_DEPOSIT_SCHEMA + cif);
	}

	private Esquema D2Deposit2014ToSchema(Esquema schema, D2Deposit2014 d2Deposit2014) {
		Esquema s = new Esquema();
		s.setCabecera(schema.getCabecera());
		Claves claves = new Claves();

		for(String key : d2Deposit2014.getMapDraft().keySet()){
			if(!key.equals("DepositType")){
				Clave clave = new Clave();
				clave.setCodigo(BigInteger.valueOf(Integer.parseInt(key)));
				clave.setValor(d2Deposit2014.getMapDraft().get(key));
				claves.getClave().add(clave);
			}
		}
		
		s.setClaves(claves);
		
		return s;
	}
	
	public Vector<MemoryTemplate> getDigitalDepositTemplates(Integer domainId) {
		String domain = AonUtil.getDomainName();

		return getDepositText(domain, domainId);

	}

	private Vector<MemoryTemplate> getDepositText(String domain,
			Integer domainId) {
		HttpServletRequest request = getThreadLocalRequest();
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			Result<Record3<Integer, String, byte[]>> data = ctx
					.getDslContext()
					.select(RATTACH.ID, RATTACH.DESCRIPTION, RATTACH.DATA)
					.from(RATTACH)
					.where(RATTACH.DOMAIN.eq(domainId))
					.and(RATTACH.TYPE
							.eq((byte) RegistryAttachmentType.D2_DEPOSIT
									.ordinal())).fetch();

			Vector<MemoryTemplate> v = new Vector<MemoryTemplate>();
			for (Record3<Integer, String, byte[]> record3 : data) {
				MemoryTemplate mt = new MemoryTemplate();
				mt.setId(record3.value1());
				mt.setName(record3.value2());
				if(record3.value3() != null){
					D2DepositTreeObject d2 = new D2DepositTreeObject();
					
					Esquema schema = DBConsults.readXml(record3.value3());
					
					request.getSession().setAttribute(D2_DEPOSIT_SCHEMA + record3.value1(), schema);
					
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
					mt.setD2Deposit2014(d2);
				}
				v.add(mt);
			}

			return v;
		} finally {
			if (ctx != null)
				ctx.close();
		}

	}

	public MemoryTemplate createTextMemory(Integer domainId, String name) {
		String domain = AonUtil.getDomainName();
		MemoryTemplate mt = new MemoryTemplate();

		byte[] data = Utils.CreateXml("", name);
		Integer id = DBConsults.insertDepositText(domain, name, data, domainId);

		mt.setId(id);
		mt.setName(name);

		return mt;

	}
	
	public Map<String, String> updateTexts(MemoryTemplate mt, Integer domainId, String cif, Map<String, String> map) {
		String domain = AonUtil.getDomainName();
		Esquema schema = DBConsults.getDeposit(domain, domainId, mt.getId()
				.toString());
		
		for (Integer i = 0; i < schema.getClaves().getClave().size(); i++) {
			if (schema.getClaves().getClave().get(i).getCodigo().toString()
					.equals(D2DepositKey.MAT19019001.getCode())) { 
				
				map.put(D2DepositKey.MAT19019001.getCode(),  schema.getClaves().getClave().get(i).getValor());
			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT29029001.getCode())) { 
				
				map.put(D2DepositKey.MAT29029001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT39039001.getCode())) { 
				
				map.put(D2DepositKey.MAT39039001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT49049001.getCode())) { 
				
				map.put(D2DepositKey.MAT49049001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT59059001.getCode())) { 
 				
				map.put(D2DepositKey.MAT59059001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT69069001.getCode())) {
				
				map.put(D2DepositKey.MAT69069001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT79079001.getCode())) {
				
				map.put(D2DepositKey.MAT79079001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT89089001.getCode())) { 
				
				map.put(D2DepositKey.MAT89089001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT99099001.getCode())) { 
				
				map.put(D2DepositKey.MAT99099001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT119119001.getCode())) { 
				
				map.put(D2DepositKey.MAT119119001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT129129001.getCode())) { 
				
				map.put(D2DepositKey.MAT129129001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT139139001.getCode())) { 
				
				map.put(D2DepositKey.MAT139139001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} else if (schema.getClaves().getClave().get(i).getCodigo()
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
		String domain = AonUtil.getDomainName();
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			Record1<Integer> data = ctx.getDslContext().select(DOMAIN.PARENT)
					.from(DOMAIN).where(DOMAIN.ID.eq(domainId)).fetchOne();

			return data.value1();

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public String getDomainName(Integer domainId) {
		String domain = AonUtil.getDomainName();
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);

			Record1<String> data = ctx.getDslContext().select(DOMAIN.NAME)
					.from(DOMAIN).where(DOMAIN.ID.eq(domainId)).fetchOne();

			return data.value1();

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public Map<String, String> importAll(String type, String ejercicio, MemoryTemplate mt,
			Integer domainId, String cif, Map<String, String> map) {
		String domainName = getDomainName(domainId);
		if (type.equals("Balance (I.S.)")) {
			if (ejercicio.equals("2013")) {
				Mod2002013 mod2002013 = com.esferalia.aon.occam.api.AON
						.getMod2002013ByYear(domainName, domainId, 2013);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002013toD2.fillBalance(ctx, mod2002013);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if (ejercicio.equals("2014")) {
				Mod2002014 mod2002014 = com.esferalia.aon.occam.api.AON
						.getMod2002014ByYear(domainName, domainId, 2014);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002014toD2.fillBalance(ctx, mod2002014);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			}
		} else if (type.equals("Perdidas y ganancias (I.S.)")) {
			if (ejercicio.equals("2013")) {
				Mod2002013 mod2002013 = com.esferalia.aon.occam.api.AON
						.getMod2002013ByYear(domainName, domainId, 2013);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002013toD2.fillPyg(ctx, mod2002013);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if (ejercicio.equals("2014")) {
				Mod2002014 mod2002014 = com.esferalia.aon.occam.api.AON
						.getMod2002014ByYear(domainName, domainId, 2014);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002014toD2.fillPyg(ctx, mod2002014);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			}
		} else if (type.equals("ECPN (I.S.)")) {
			if (ejercicio.equals("2013")) {
				Mod2002013 mod2002013 = com.esferalia.aon.occam.api.AON
						.getMod2002013ByYear(domainName, domainId, 2013);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002013toD2.fillEcpn(ctx, mod2002013);
				Mod2002013toD2.fillEcpn2(ctx, mod2002013);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			} else if (ejercicio.equals("2014")) {
				Mod2002014 mod2002014 = com.esferalia.aon.occam.api.AON
						.getMod2002014ByYear(domainName, domainId, 2014);
				Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
				Mod2002014toD2.fillEcpn(ctx, mod2002014);
				Mod2002014toD2.fillEcpn2(ctx, mod2002014);

				for (D2DepositHeaderKey key : ctx.keySet()) {
					if(map.containsKey(key.getCode().toString()))
						map.remove(key.getCode().toString());
					map.put(key.getCode(), ctx.get(key)
							.toString());
				}
			}
		} else if (type.equals("Memoria predefinida")) {
			map = updateTexts(mt, domainId, cif, map);
		} else if (type.equals("Memoria (Deposito.xml)")) {
			
			if (ejercicio.equals("2013")) {

			} else if (ejercicio.equals("2014")) {

			}
		}
		return map;

	}

	public void delete(Integer domainId, String document) {
		String domain = AonUtil.getDomainName();
		clearSession(document);
		DBConsults.deleteDeposit(domain, domainId);
	}

	public void deleteFreeText(Integer domainId, Integer rattachId) {
		String domain = AonUtil.getDomainName();
		clearSession(rattachId.toString());
		
		DBConsults.deleteText(domain, domainId, rattachId);
	}
	
	public Map<String, String> createD2Deposit(Integer domainId, Integer id, String name,
			String type) {
		String domainName = AonUtil.getDomainName();
		Enterprise enterprise = AON.getEnterprise(domainName, domainId, id);
		byte[] b = Utils.CreateXml(enterprise, name, type, domainName);
		DBConsults.insertDeposit(domainName, b, domainId);
		return getSchema(enterprise.getDocument(), domainId, false);
	}
	
	public Map<String, String> calculate(Map<String, String> map) {
		D2MVELContext ctx = new D2MVELContext(map, new IAccMiningKeyAccept() {
			@Override
			public boolean acceptKey(Object key) {
				return AonStringUtils.isNotEmpty((String) key);
			}
		});
		ctx.setExpressionMap(D2Compute.COMPUTE_MAP);
		
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
		for (String key : D2Compute.COMPUTE_MAP.keySet()) {
			String expression = D2Compute.COMPUTE_MAP.get(key);
			Object ret = ctx.evaluateExpression(key,expression);
			if (ret instanceof Double) {
				Double calculated = (Double) ret;
				ctx.put("Q"+key, calculated);
				
				if(map.containsKey(key)) map.remove(key);
				map.put(key, calculated.toString());
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
	
	public String getAsHTML(int zoom) {
		//FileItem file = getFile();
		
		//file.getContentType();
		/*IDocument2HtmlConverter converter = 
				getDocument2HtmlConverter(doc);
		try {
			ByteArrayOutputStream os = 
					new ByteArrayOutputStream();
			converter.transform(doc, os, zoom);
			os.flush();
			return os.toString();
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}*/
		return "";
	}
	
	
	public Vector<MemoryFiles> getMemoryFiles(Integer domainId){
		String domain = AonUtil.getDomainName();

		Vector<MemoryFiles> ms = new Vector<MemoryFiles>();
		
		MemoryFiles m = new MemoryFiles();
		Integer id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_MEMORY);
		m.setId(id);m.setBool(id != -1);m.setName(D2_FILE_MEMORY);ms.add(m);
		
		MemoryFiles m2 = new MemoryFiles();
		id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_AUTOCARTERA_MODEL);
		m2.setId(id);m2.setBool(id != -1);m2.setName(D2_FILE_AUTOCARTERA_MODEL);ms.add(m2);
		
		MemoryFiles m3 = new MemoryFiles();
		id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_GESTION);
		m3.setId(id);m3.setBool(id != -1);m3.setName(D2_FILE_GESTION);ms.add(m3);

		MemoryFiles m4 = new MemoryFiles();
		id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_AUDIT);
		m4.setId(id);m4.setBool(id != -1);m4.setName(D2_FILE_AUDIT);ms.add(m4);

		MemoryFiles m5 = new MemoryFiles();
		id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_CONVOC);
		m5.setId(id);m5.setBool(id != -1);m5.setName(D2_FILE_CONVOC);ms.add(m5);

		MemoryFiles m6 = new MemoryFiles();
		id = DBConsults.getMemoryFile(domain, domainId, D2_FILE_SICAV);
		m6.setId(id);m6.setBool(id != -1);m6.setName(D2_FILE_SICAV);ms.add(m6);
		
		return ms;
	}
	
	public void deleteMemoryFile(Integer domainId, Integer id){
		String domain = AonUtil.getDomainName();
		DBConsults.deleteMemoryFile(domain, domainId, id);
		
	}
	
	public MemoryFiles insertMemoryFile(Integer domainId, MemoryFiles mf){
		String domain = AonUtil.getDomainName(); 
		byte[] b = getFile(domainId);
		byte  m = (byte) MimeType.get(getMimeType(domainId)).ordinal();
		if(mf.getBool()){			
			DBConsults.updateMemoryFile(domain, domainId, m, b, mf.getId());
		}
		else {
			Integer id = DBConsults.insertMemoryFile(domain, domainId, m, b, mf.getName());
			mf.setId(id);
		}
		return mf;
	}
}
