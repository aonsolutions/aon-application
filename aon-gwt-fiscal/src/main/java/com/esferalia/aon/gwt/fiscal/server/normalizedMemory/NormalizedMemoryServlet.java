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

import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.client.tree.node.D2DepositTreeObject;
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
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2MVELContext;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
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
	private static final String MODIFY_D2_DEPOSIT_SCHEMA = "ModifyD2DepositSchema";
	
//	void initFacesContext() {
//		ServletContext context = getServletContext();
//		HttpServletRequest request = getThreadLocalRequest();
//		HttpServletResponse response = getThreadLocalResponse();
//		AonServletUtils.initFacesContext(context, request, response);
//	}
//	void releaseFacesContext() {
//		AonServletUtils.releaseFacesContext();
//	}
//	public Integer initialize() {
//		try {
//			initFacesContext();
//			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
//			return null;
//		} finally {
//			releaseFacesContext();
//		}
//	}
	
	public Integer initialize() {
		return null;
	}

	public Map<String, String> getSchema(String cif,
			Integer domainId, Boolean textMode) {
		String domain = AonUtil.getDomainName();
		HttpServletRequest request = getThreadLocalRequest();
		// String cif = DBConsults.getCIF(domain, domainId);
		Esquema schema = (Esquema) request.getSession().getAttribute(
				D2_DEPOSIT_SCHEMA + cif);
		if (schema == null) {
			// System.out.println(domainId);
			if (textMode) {
				schema = DBConsults.getDeposit(domain, domainId, cif);
			} else
				schema = DBConsults.getDeposit(domain, domainId);
			request.getSession().setAttribute(D2_DEPOSIT_SCHEMA + cif, schema);
		}
		// TODO
		// COMPROBAR SI EL SCHEMA ESTÁ EN LA SESIÓN
		// SI NO ESTA GETDEPOSIT() --> DE DBCONSULTS.
		// DEVOLVER SCHEMA
		
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
		request.getSession().setAttribute(MODIFY_D2_DEPOSIT_SCHEMA + cif, "true");
	}

	public Boolean isDigitalDeposit(Integer domainId) {
		String domain = AonUtil.getDomainName();
		return DBConsults.isDigitalDeposit(domain, domainId);
	}

	public Boolean isModify(String cif) {
		HttpServletRequest request = getThreadLocalRequest();
		String modify = (String) request.getSession().getAttribute(
				MODIFY_D2_DEPOSIT_SCHEMA + cif);
		return modify != null && modify.equals("true");
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
		request.getSession().removeAttribute("ModifyD2DepositSchema" + cif);
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
	
	public Map<String, String> updateTexts(MemoryTemplate mt, Integer domainId, String cif) {
		String domain = AonUtil.getDomainName();
		Esquema schema = DBConsults.getDeposit(domain, domainId, mt.getId()
				.toString());
		Map<String, String> map = new  HashMap<String, String>();
		for (Integer i = 0; i < schema.getClaves().getClave().size(); i++) {
			if (schema.getClaves().getClave().get(i).getCodigo().toString()
					.equals(D2DepositKey.MAT19019001.getCode())) { 
				updateSchema(cif, domainId,D2DepositKey.MAT19019001.getCode() , schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT19019001.getCode(),  schema.getClaves().getClave().get(i).getValor());
			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT29029001.getCode())) { 
				updateSchema(cif, domainId, D2DepositKey.MAT29029001.getCode(), schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT29029001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT39039001.getCode())) { 
				updateSchema(cif, domainId, D2DepositKey.MAT39039001.getCode(), schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT39039001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT49049001.getCode())) { 
				updateSchema(cif, domainId, D2DepositKey.MAT49049001.getCode(), schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT49049001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT59059001.getCode())) { 
 				updateSchema(cif, domainId, D2DepositKey.MAT59059001.getCode(), schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT59059001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT69069001.getCode())) {
				updateSchema(cif, domainId, D2DepositKey.MAT69069001.getCode(), schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT69069001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT79079001.getCode())) {
				updateSchema(cif, domainId,D2DepositKey.MAT79079001.getCode(), schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT79079001.getCode(), schema.getClaves().getClave().get(i).getValor());
			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT89089001.getCode())) { 
				updateSchema(cif, domainId, D2DepositKey.MAT89089001.getCode(), schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT89089001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT99099001.getCode())) { 
				updateSchema(cif, domainId, D2DepositKey.MAT99099001.getCode(), schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT99099001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT119119001.getCode())) { 
				updateSchema(cif, domainId, D2DepositKey.MAT119119001.getCode(), schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT119119001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} 
			else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT129129001.getCode())) { 
				updateSchema(cif, domainId,D2DepositKey.MAT129129001.getCode(), schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT129129001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT139139001.getCode())) { 
				updateSchema(cif, domainId, D2DepositKey.MAT139139001.getCode(), schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT139139001.getCode(), schema.getClaves().getClave().get(i).getValor());

			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals(D2DepositKey.MAT149149001.getCode())) { 
				updateSchema(cif, domainId, D2DepositKey.MAT149149001.getCode(), schema.getClaves()
						.getClave().get(i).getValor());
				map.put(D2DepositKey.MAT149149001.getCode(), schema.getClaves().getClave().get(i).getValor());

			}
		}
		//saveDeposit(cif, domainId, false);
		return map;
	}

	/*public void updateTexts(MemoryTemplate mt, Integer domainId, String cif) {
		String domain = AonUtil.getDomainName();
		Esquema schema = DBConsults.getDeposit(domain, domainId, mt.getId()
				.toString());

		for (Integer i = 0; i < schema.getClaves().getClave().size(); i++) {
			if (schema.getClaves().getClave().get(i).getCodigo().toString()
					.equals("9019001")) {
				updateSchema(cif, domainId, "9019001", schema.getClaves()
						.getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals("9029001")) {
				updateSchema(cif, domainId, "9029001", schema.getClaves()
						.getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals("9039001")) {
				updateSchema(cif, domainId, "9039001", schema.getClaves()
						.getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals("9049001")) {
				updateSchema(cif, domainId, "9049001", schema.getClaves()
						.getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals("9059001")) {
				updateSchema(cif, domainId, "9059001", schema.getClaves()
						.getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals("9069001")) {
				updateSchema(cif, domainId, "9069001", schema.getClaves()
						.getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals("9079001")) {
				updateSchema(cif, domainId, "9079001", schema.getClaves()
						.getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals("9089001")) {
				updateSchema(cif, domainId, "9089001", schema.getClaves()
						.getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals("9099001")) {
				updateSchema(cif, domainId, "9099001", schema.getClaves()
						.getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals("9119001")) {
				updateSchema(cif, domainId, "9119001", schema.getClaves()
						.getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals("9129001")) {
				updateSchema(cif, domainId, "9129001", schema.getClaves()
						.getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals("9139001")) {
				updateSchema(cif, domainId, "9139001", schema.getClaves()
						.getClave().get(i).getValor());
			} else if (schema.getClaves().getClave().get(i).getCodigo()
					.toString().equals("9149001")) {
				updateSchema(cif, domainId, "9149001", schema.getClaves()
						.getClave().get(i).getValor());
			}
		}
		saveDeposit(cif, domainId, false);

	}*/

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

	public void importSocietyValues(String document, Integer domainId) {
		String domainName = getDomainName(domainId);

		// 2014

		Mod2002014 mod2002014 = com.esferalia.aon.occam.api.AON
				.getMod2002014ByYear(domainName, domainId, 2014);
		Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002014toD2.fill(ctx, mod2002014);

		for (D2DepositHeaderKey key : ctx.keySet()) {
			updateSchema(document, domainId, key.getCode(), ctx.get(key)
					.toString());
		}

		// 2013

		/*
		 * Mod2002013 mod2002013 =
		 * com.esferalia.aon.occam.api.AON.getMod2002013ByYear(domainName,
		 * domainId, 2013); ctx = new LinkedHashMap<D2DepositHeaderKey,
		 * Double>(); Mod2002013toD2.fill(ctx, mod2002013);
		 * 
		 * for(D2DepositHeaderKey key : ctx.keySet()){ updateSchema(document,
		 * domainId, key.getCode(), ctx.get(key).toString()); }
		 */
	}

	public Map<String, String> importAll(String type, String ejercicio, MemoryTemplate mt,
			Integer domainId, String cif, Map<String, String> map) {
		String domainName = getDomainName(domainId);
		if (type.equals("Balance")) {
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
					updateSchema(cif, domainId, key.getCode(), ctx.get(key)
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
					updateSchema(cif, domainId, key.getCode(), ctx.get(key)
							.toString());
				}
			}
		} else if (type.equals("Perdidas y ganancias")) {
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
					updateSchema(cif, domainId, key.getCode(), ctx.get(key)
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
					updateSchema(cif, domainId, key.getCode(), ctx.get(key)
							.toString());
				}
			}
		} else if (type.equals("ECPN")) {
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
					updateSchema(cif, domainId, key.getCode(), ctx.get(key)
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
					updateSchema(cif, domainId, key.getCode(), ctx.get(key)
							.toString());
				}
			}
		} else if (type.equals("Memoria predefinida")) {
			updateTexts(mt, domainId, cif);
		} else if (type.equals("Memoria")) {
			
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

	public Map<String, String> createD2Deposit(Integer domainId, Integer id, String name,
			String type) {
		String domainName = AonUtil.getDomainName();
		Enterprise enterprise = AON.getEnterprise(domainName, domainId, id);
		byte[] b = Utils.CreateXml(enterprise, name, type, domainName);
		DBConsults.insertDeposit(domainName, b, domainId);
		return getSchema(enterprise.getDocument(), domainId, false);
	}
	
	
	// TODO Create new class for map
	public static Map<String,String> COMPUTE_MAP = new LinkedHashMap<String,String>();
	static {
		COMPUTE_MAP.put(D2DepositHeaderKey.BA111000.toString(),"(PYMES)?(Q11100+Q11200+Q11300+Q11400+Q11500+Q11600+Q11700):(Q11100+Q11200)");
	}

	private void calculate(Esquema schema) {
		D2MVELContext ctx = new D2MVELContext(schema, new IAccMiningKeyAccept() {
			@Override
			public boolean acceptKey(Object key) {
				return AonStringUtils.isNotEmpty((String) key);
			}
		});
		ctx.setExpressionMap(COMPUTE_MAP);
		List<Clave> claves = schema.getClaves().getClave();
		for (Clave clave : claves) {
			try {
				if (AonStringUtils.isNotEmpty( clave.getValor() )) {
					Double d = Double.parseDouble(clave.getValor());
					ctx.put("Q"+clave.getCodigo().toString(), d);
				}
			} catch (NumberFormatException e) {
				// Ignore value
			}
		}
		ctx.put("PYMES", false);
		for (String key : COMPUTE_MAP.keySet()) {
			String expression = COMPUTE_MAP.get(key);
			Object ret = ctx.evaluateExpression(key,expression);
			if (ret instanceof Double) {
				Double calculated = (Double) ret;
				ctx.put(key, calculated);
				// TODO 
				// populate data
			}
		}
	}
	
}
