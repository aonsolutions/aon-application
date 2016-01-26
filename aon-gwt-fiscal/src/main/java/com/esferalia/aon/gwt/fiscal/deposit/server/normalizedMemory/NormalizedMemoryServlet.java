package com.esferalia.aon.gwt.fiscal.deposit.server.normalizedMemory;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
import org.json.JSONException;
import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonRemoteServiceServlet;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.FileInfo;
import com.esferalia.aon.gwt.fiscal.deposit.client.D2DepositTreeObject;
import com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.deposit.shared.D2Deposit2014;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryFiles;
import com.esferalia.aon.gwt.fiscal.deposit.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.accounting.IAccMiningKeyAccept;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2Compute;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2MVELContext;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves.Clave;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002013toD2;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002014toD2;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Utils;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.server.io.ByteArrayOutputStream;
import com.esferalia.aon.watson.util.AonStringUtils;


public class NormalizedMemoryServlet extends AonRemoteServiceServlet implements
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
	private static final String D2_FILE_GESTION = "Informe de Gesti\u00f3n";
	private static final String D2_FILE_AUDIT = "Informe de Auditor\u00eda";
	private static final String D2_FILE_CONVOC = "Anuncios de Convocatoria";
	private static final String D2_FILE_SICAV = "Certificaci\u00f3n SICAV";

	public Integer initialize() {
		return null;
	}

	public Map<String, String> getSchema(String cif,
			Integer domainId, Boolean textMode, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
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
				schema = DBConsults.getDeposit(domain, domainId, year, this.getUserLogin());
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

	public Boolean isDigitalDeposit(Integer domainId, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		return DBConsults.isDigitalDeposit(domain, domainId, year);
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

	public void saveDeposit(String cif, Integer domainId, Boolean textMode, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		Esquema schema = (Esquema) request.getSession().getAttribute(
				D2_DEPOSIT_SCHEMA + cif);

		try {
			byte[] b = Utils.writeXml(schema);
			if (textMode) {
				DBConsults.insertDeposit(domain, b, domainId, cif, this.getUserLogin());
			} else
				DBConsults.insertDeposit(domain, b, domainId, year, this.getUserLogin());
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		request.getSession().removeAttribute(MODIFY_D2_DEPOSIT_SCHEMA + cif);
	}
	
	
	public String[] getDepositExercises(Integer domainId){
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		return DBConsults.getDepositExercises(domain, domainId, getUserLogin());
	}
	
	public void saveDeposit(String cif, Integer domainId, D2Deposit2014 d2Deposit2014, Boolean textMode, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		Esquema schema = DBConsults.getDeposit(domain, domainId, year, this.getUserLogin());
		schema = D2Deposit2014ToSchema(schema, d2Deposit2014);
		try {
			byte[] b = Utils.writeXml(schema);
			if (textMode) {
				DBConsults.insertDeposit(domain, b, domainId, cif, this.getUserLogin());
			} else
				DBConsults.insertDeposit(domain, b, domainId, year, this.getUserLogin());
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
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);

		return getDepositText(domain, domainId);

	}

	private Vector<MemoryTemplate> getDepositText(String domain,
			Integer domainId) {
		HttpServletRequest request = getThreadLocalRequest();
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, getUserLogin());

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
		HttpServletRequest request = getThreadLocalRequest();
		String domain  = AonServletUtils.getRequestDomainName(request);

		MemoryTemplate mt = new MemoryTemplate();

		byte[] data = Utils.CreateXml("", name);
		Integer id = DBConsults.insertDepositText(domain, name, data, domainId, this.getUserLogin());

		mt.setId(id);
		mt.setName(name);

		return mt;

	}
	
	public Map<String, String> updateTexts(MemoryTemplate mt, Integer domainId, String cif, Map<String, String> map) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);

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
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);

		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, getUserLogin());

			Record1<Integer> data = ctx.getDslContext().select(DOMAIN.PARENT)
					.from(DOMAIN).where(DOMAIN.ID.eq(domainId)).fetchOne();

			return data.value1();

		} finally {
			if (ctx != null)
				ctx.close();
		}
	}

	public String getDomainName(Integer domainId) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);

		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId, getUserLogin());

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
			byte[] b = getFile(domainId);

			if (ejercicio.equals("2013")) {

			} else if (ejercicio.equals("2014")) {
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
		}
		return map;

	}

	public void delete(Integer domainId, String document, Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		clearSession(document);
		DBConsults.deleteDeposit(domain, domainId, year);
		
		DBConsults.deleteMemoryFile(domain, domainId, D2_FILE_MEMORY);
		DBConsults.deleteMemoryFile(domain, domainId, D2_FILE_AUTOCARTERA_MODEL);
		DBConsults.deleteMemoryFile(domain, domainId, D2_FILE_GESTION);
		DBConsults.deleteMemoryFile(domain, domainId, D2_FILE_AUDIT);
		DBConsults.deleteMemoryFile(domain, domainId, D2_FILE_CONVOC);
		DBConsults.deleteMemoryFile(domain, domainId, D2_FILE_SICAV);
	}

	public void deleteFreeText(Integer domainId, Integer rattachId) {
		HttpServletRequest request = getThreadLocalRequest();
		String domain = AonServletUtils.getRequestDomainName(request);
		clearSession(rattachId.toString());
		
		DBConsults.deleteText(domain, domainId, rattachId);
	}
	
	public Map<String, String> createD2Deposit(Integer domainId, Integer id, String name,
			String type,Integer year) {
		HttpServletRequest request = getThreadLocalRequest();
		String domainName = AonServletUtils.getRequestDomainName(request);
		Enterprise enterprise = AON.getEnterprise(domainName, domainId, id);
		byte[] b = Utils.CreateXml(enterprise, name, type, domainName, year);
		DBConsults.insertDeposit(domainName, b, domainId, year, this.getUserLogin());
		return getSchema(enterprise.getDocument(), domainId, false, year);
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
		for (Integer i = 0; i < claves.size(); i++) {
			if(schema.getClaves().getClave().get(i).getCodigo().toString().equals(key)){
				if(D2DepositFooterKey.PR8080805.getCode().equals(key))
					schema.getClaves().getClave().get(i).setValor(bool?"0":"1");
				else schema.getClaves().getClave().get(i).setValor(bool?"1":"0");


			}
		}
		
		try {
			byte[] b = Utils.writeXml(schema);
			DBConsults.insertDeposit(domain, b, domainId, year, this.getUserLogin());
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//-------------------- Visualizar Archivo
		private static final int DEFAULT_ZOOM = 130;
		private static final String ZOOM_PARAM = "zoom";
		private static final String PAGE_PARAM = "page";
		
		public String viewer(Integer domainId, MemoryFiles mf){
			HttpServletRequest request = getThreadLocalRequest();
			String domainName = AonServletUtils.getRequestDomainName(request);
			String login = AonServletUtils.getLoggedUser();
			Attach rattach = AON.getAttach(domainName, domainId, login, 
					filter -> filter.getIdProperty().eq(mf.getId())
					, AttachType.REGISTRY);
			rattach.setMd5(AonFileUtils.getMD5Checksum(rattach.getData()));
			FileInfo doc = new FileInfo(rattach);
			if(doc.getDomain() ==  null) doc.setDomain(domainName);
			return getAsHTML(domainName, doc, DEFAULT_ZOOM);
		}
	
		public String getAsHTML(String domainName, FileInfo doc, int zoom) {
			IDocument2HtmlConverter converter = 
					getDocument2HtmlConverter(doc);
			try {
				ByteArrayOutputStream os = 
						new ByteArrayOutputStream();
				converter.transform(domainName, doc, os, zoom);
				os.flush();
				return os.toString();
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
		
		
		private IDocument2HtmlConverter getDocument2HtmlConverter(FileInfo document) {
			
			MimeType mimeType = MimeType.values()[document.getMimetype()];
			return DOC2HTML_CONVERTERS.get(mimeType);
			
		}
		
		
		public static final Map<MimeType, IDocument2HtmlConverter> DOC2HTML_CONVERTERS = 
				new HashMap<MimeType, IDocument2HtmlConverter>(){
			
			private static final long serialVersionUID = -5485478770888354370L;

			{
				put(MimeType.PDF, OpenDocument2HtmlConverter.INSTANCE );
				put(MimeType.MS_WORD, OpenDocument2HtmlConverter.INSTANCE );
				put(MimeType.MS_WORD_2007, OpenDocument2HtmlConverter.INSTANCE );
				put(MimeType.MS_EXCEL, OpenDocument2HtmlConverter.INSTANCE );
				put(MimeType.MS_EXCEL_2007, OpenDocument2HtmlConverter.INSTANCE );
				put(MimeType.MS_POWER_POINT, OpenDocument2HtmlConverter.INSTANCE );
				put(MimeType.MS_POWER_POINT_2007, OpenDocument2HtmlConverter.INSTANCE );
				put(MimeType.HTML, Noop2HtmlConverter.INSTANCE );
				put(MimeType.TXT, Noop2HtmlConverter.INSTANCE );

				put(MimeType.BMP, Image2HtmlConverter.INSTANCE );
				put(MimeType.JPEG, Image2HtmlConverter.INSTANCE );
				put(MimeType.PNG, Image2HtmlConverter.INSTANCE );
				put(MimeType.GIF, Image2HtmlConverter.INSTANCE );
				
				put(MimeType.ZIP, Zip2HtmlConverter.INSTANCE);
			}
		};
		
		
		private static interface IDocument2HtmlConverter {
			void transform(String domainName, FileInfo doc, OutputStream os, int zoom) throws Exception;

		}

		private static class Zip2HtmlConverter implements IDocument2HtmlConverter{
			private static IDocument2HtmlConverter INSTANCE = new Zip2HtmlConverter();
			
			@Override
			public void transform(String domainName, FileInfo doc, OutputStream os, int zoom)
					throws Exception {
				
				PrintStream printStream = new PrintStream(os);
				InputStream in = new ByteArrayInputStream(doc.getData());
				
				ZipInputStream zip = new ZipInputStream(in);
				ZipEntry entry;
				String html="<div class='page' style=' width:150%s; background-color:#FFF;border-radius: 5px 5px 5px 5px;'>"
						+ "<table style='padding-top:10px; padding-bottom:5px;'>";
				while (null != (entry=zip.getNextEntry()) ){
					String icon = entry.isDirectory()?"aon-icon-google-drive-folder":"aon-icon-google-drive-unknown";
					if(!entry.getName().substring(0,entry.getName().length()-1).contains("/")){
						if(entry.isDirectory())
							html = html + "<tr><td style='padding-left:5px;'><button onclick='alert(hola);' class='aon-editDataTable-button "+icon+"' style='padding-left: 20px;'>"+entry.getName()+"</button></td></tr>";
						else{
							html = html + "<tr><td style='padding-left:5px;'><span class='"+icon+"' style='padding-left: 20px;'>"+entry.getName()+"</span></td></tr>";
						}
					}
				}
				html = html + "</table></div>";
				System.out.println(html);
				printStream.printf(html,"%");
			}

		}
		private static class OpenDocument2HtmlConverter implements IDocument2HtmlConverter {
			
			private static  IDocument2HtmlConverter INSTANCE = new OpenDocument2HtmlConverter();

			@Override
			public void transform(String domainName, FileInfo doc, OutputStream os, int zoom) throws Exception {
				PrintStream printStream = new PrintStream(os);
				JSONObject json = new JSONObject();
				json.put("md5", doc.getMd5())
					.put("domainName", doc.getDomain())
					.put("domainId", doc.getDomainId())
					.put("mimetype", doc.getMimetype())
					.put("driveId", doc.getDriveId() != null ? doc.getDriveId() : "null")
					.put("isDrive", doc.getIsDrive())
					.put("fileId", doc.getFileId());
				
				JSONObject jsonResponse = sendPostHttpClient(domainName, json);
				Integer page = jsonResponse.getInt("page");
				for(Integer i = 1; i<= page; i++){
					double width = jsonResponse.getDouble("width"+i) * zoom / 100;
					double height = jsonResponse.getDouble("height"+i) * zoom / 100;
					
					printStream.printf("<div class='page' style='width:%dpx;height:%dpx;'   ><img src='openDocument2Image/%s.png?%s=%d&%s=%d&id=%d'></img> </div>",
							(long)width,
							(long)height,
							doc.getMd5(),
							PAGE_PARAM, i,
							ZOOM_PARAM, zoom,
							doc.getFileId());
				}
				
				/*PDFFile pdfFile = OpenDocument2ImageServlet.getPDFFile(doc);
				
				for (int page = 1; page <= pdfFile.getNumPages(); page++) {
					
					PDFPage pdfPage = pdfFile.getPage(page);

					// get the width and height for the doc at the default zoom
					double width =  pdfPage.getBBox().getWidth() * zoom / 100 ;
					double height = pdfPage.getBBox().getHeight() * zoom / 100 ;
					
					printStream.printf("<div class='page' style='width:%dpx;height:%dpx;'   ><img src='openDocument2Image/%s.png?%s=%d&%s=%d&id=%d'></img> </div>",
							(long)width,
							(long)height,
							doc.getMd5(),
							OpenDocument2ImageServlet.PAGE_PARAM,
							page,
							OpenDocument2ImageServlet.ZOOM_PARAM,
							zoom,
							doc.getFileId());
				}*/		
			}

		}

	private static class Noop2HtmlConverter  extends  Document2HtmlConverter  {
		
		private static  IDocument2HtmlConverter INSTANCE = new Noop2HtmlConverter();
		
		@Override
		void transform(InputStream is, OutputStream os) throws Exception {
			int read ;
			byte buffer [] = new byte [256];
			while ( ( read = is.read(buffer)) == buffer.length ) {
				os.write(buffer, 0, read);
			}
		}
	}
	private static class Image2HtmlConverter implements IDocument2HtmlConverter {
		
		private static  IDocument2HtmlConverter INSTANCE = new Image2HtmlConverter();

		@Override
		public void transform(String domainName, FileInfo doc, OutputStream os, int zoom) throws Exception {
			
			PrintStream printStream = new PrintStream(os);
			
			MimeType mimeType = MimeType.values()[doc.getMimetype()];
			
			printStream.printf("<div class='page'  ><img src='openDocumentConverter/%s.%s?id=%d&domainName=%s&domainId=%d'></img> </div>",
					doc.getMd5(),
					mimeType.getExtension(),
					doc.getFileId(),
					doc.getDomain(),
					doc.getDomainId());
		}
	}
	private abstract static class Document2HtmlConverter implements IDocument2HtmlConverter{
		@Override
		public void transform(String domainName, FileInfo doc, OutputStream os, int zoom) throws Exception {
			Connection conn = null;

			ResultSet rs = null;
			PreparedStatement stmt = null;
			try {
				conn = getConnection();
				stmt = conn.prepareStatement("SELECT data FROM rattach WHERE id = ? ");

				stmt.setInt(1, doc.getFileId());
				
				rs = stmt.executeQuery();
				
				if (!rs.next()) {
					throw new IllegalArgumentException();
				}
				InputStream is = rs.getBinaryStream("data");
				transform(is, os);
			}
			catch (Exception e ) {
				throw new IllegalArgumentException(e);
			}
			finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException e) {
						throw new IllegalArgumentException(e);
					}
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException e) {
						throw new IllegalArgumentException(e);
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						throw new IllegalArgumentException(e);
					}
				}
			}
		}

		abstract void transform(InputStream is, OutputStream os) throws Exception;
		
	}
	
	protected static JSONObject sendPostHttpClient(String domainName, JSONObject json) {
		try{
			String url = "http://"+domainName+"/aon-aio/openDocument2Image/";
			HttpClientBuilder base = HttpClientBuilder.create();
			HttpClient client = base.build();
			HttpPost post = new HttpPost(url);
			List<NameValuePair> urlParameters =  new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("details", json.toString()));
			post.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpResponse response = client.execute(post);
			InputStream is = response.getEntity().getContent();
			String jsonData = convertStreamToString(is);
			return new JSONObject(jsonData);
		} catch (IOException | JSONException e){
			e.printStackTrace();
		}
		return new JSONObject();
	}
	
	private static String convertStreamToString(InputStream is) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
	// -------------------------------------------------------------- ENTERPRISE
	@Override
	public ArrayList<Enterprise> getParentEnterprises(String domainName, int domain,
			String query){
		return AON.getParentEnterprises(domainName, domain, query);		
	}

}
