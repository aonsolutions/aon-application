package com.esferalia.aon.gwt.fiscal.server.normalizedMemory;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;

import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.gwt.fiscal.shared.MemoryTemplate;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves.Clave;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Mod2002014toD2;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Utils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class NormalizedMemoryServlet extends RemoteServiceServlet implements INormalizedMemory{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	void initFacesContext() {
		ServletContext context = getServletContext();
		HttpServletRequest request = getThreadLocalRequest();
		HttpServletResponse response = getThreadLocalResponse();
		AonServletUtils.initFacesContext(context, request, response);
	}

	void releaseFacesContext() {
		AonServletUtils.releaseFacesContext();
	}
	
	public Integer initialize(){
		try{
			initFacesContext();
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			return ds.getDomainId();
		}finally{
			releaseFacesContext();
		}
	}
	
	public Map<String, String> getSchema(String cif,String part, Integer domainId, Boolean textMode){
		String domain = AonUtil.getDomainName();
		HttpServletRequest request = getThreadLocalRequest();
	//String cif = DBConsults.getCIF(domain, domainId);
		Esquema schema = (Esquema) request.getSession().getAttribute("d2DepositSchema"+cif);
		if(schema == null){
			//System.out.println(domainId);
			if(textMode){
				schema = DBConsults.getDeposit(domain,domainId, cif);
			}
			else schema = DBConsults.getDeposit(domain, domainId);
			request.getSession().putValue("d2DepositSchema"+cif, schema);
		}
		//TODO
			// COMPROBAR SI EL SCHEMA ESTÁ EN LA SESIÓN
			// SI NO ESTA GETDEPOSIT() --> DE  DBCONSULTS.
			// DEVOLVER SCHEMA
		D2DepositKey[] keyList = null;
		D2DepositHeaderKey[] keyListHeader = null;
		D2DepositFooterKey[] keyListFooter = null;
		
		switch (part) {
		case "IDA": keyListHeader = D2DepositConstants.IDA_ABREVIATE_KEYS;break;
		case "BA": keyListHeader = D2DepositConstants.BA_ABREVIATE_KEYS;break;
		case "PA": keyListHeader = D2DepositConstants.PA_ABREVIATE_KEYS;break;
		case "PNA": keyListHeader = D2DepositConstants.PNA_ABREVIATE_KEYS;break;
		case "IMA": keyListHeader = D2DepositConstants.IMA_ABREVIATE_KEYS;break;
		case "MAT1": keyList = D2DepositConstants.MAT1_ABREVIATE_KEYS;break;
		case "MAT2": keyList = D2DepositConstants.MAT2_ABREVIATE_KEYS;break;
		case "MAT3": keyList = D2DepositConstants.MAT3_ABREVIATE_KEYS;break;
		case "MA3": keyList = D2DepositConstants.MA3_ABREVIATE_KEYS;break;
		case "MAT4": keyList = D2DepositConstants.MAT4_ABREVIATE_KEYS;break;
		case "MAT5": keyList = D2DepositConstants.MAT5_ABREVIATE_KEYS;break;
		case "MA5": keyList = D2DepositConstants.MA5_ABREVIATE_KEYS;break;
		case "MAT6": keyList = D2DepositConstants.MAT6_ABREVIATE_KEYS;break;
		case "MA6": keyList = D2DepositConstants.MA6_ABREVIATE_KEYS;break;
		case "MAT7": keyList = D2DepositConstants.MAT7_ABREVIATE_KEYS;break;
		case "MA7":keyList = D2DepositConstants.MA7_ABREVIATE_KEYS;break;
		case "MAT8": keyList = D2DepositConstants.MAT8_ABREVIATE_KEYS;break;
		case "MAT9": keyList = D2DepositConstants.MAT9_ABREVIATE_KEYS;break;
		case "MA10": keyList = D2DepositConstants.MA10_ABREVIATE_KEYS;break;
		case "MAT11": keyList = D2DepositConstants.MAT11_ABREVIATE_KEYS;break;
		case "MA11": keyList = D2DepositConstants.MA11_ABREVIATE_KEYS;break;
		case "MAT12": keyList = D2DepositConstants.MAT12_ABREVIATE_KEYS;break;
		case "MA12": keyList = D2DepositConstants.MA12_ABREVIATE_KEYS; break;
		case "MAT13": keyList = D2DepositConstants.MAT13_ABREVIATE_KEYS;break;
		case "MA13": keyList = D2DepositConstants.MA13_ABREVIATE_KEYS;break;
		case "MAT14": keyList = D2DepositConstants.MAT14_ABREVIATE_KEYS;break;
		case "MA14": keyList = D2DepositConstants.MA14_ABREVIATE_KEYS;
		case "MA15": keyList = D2DepositConstants.MA15_ABREVIATE_KEYS;break;
		case "A": keyListFooter = D2DepositConstants.A_ABREVIATE_KEYS;break;
		case "PR": keyListFooter = D2DepositConstants.PR_ABREVIATE_KEYS;
					keyListHeader = D2DepositConstants.PR_DATOS_EXTRA;
					break;
		case "H": keyListFooter = D2DepositConstants.H_ABREVIATE_KEYS; break;
		
		default:
			break;
		}
		List<Clave> claves = schema.getClaves().getClave();
		Map<String, String> map=  new HashMap<String, String>(); 
		for(Integer i = 0; i < claves.size(); i++){
			if(keyList != null)
				for(Integer j = 0; j < keyList.length; j++){
					if(claves.get(i).getCodigo().toString().equals(keyList[j].getCode())){
						map.put(keyList[j].getName(), claves.get(i).getValor());
					}	
				}
			if(keyListHeader != null)
				for(Integer j = 0; j < keyListHeader.length; j++){
					if(claves.get(i).getCodigo().toString().equals(keyListHeader[j].getCode())){
						map.put(keyListHeader[j].getName(), claves.get(i).getValor());
					}	
				}
			if(keyListFooter != null)
				for(Integer j = 0; j < keyListFooter.length; j++){
					if(claves.get(i).getCodigo().toString().equals(keyListFooter[j].getCode())){
						map.put(keyListFooter[j].getName(), claves.get(i).getValor());
					}	
				}

				
		}
		return map;
	}
	
	public void updateSchema(String cif,Integer domainId, String key, String value) {
		String domain = AonUtil.getDomainName();
		HttpServletRequest request = getThreadLocalRequest();
		//String cif = DBConsults.getCIF(domain, domainId);
		Esquema schema = (Esquema) request.getSession().getAttribute("d2DepositSchema"+cif);
		Boolean bool = true;
		for(Integer i = 0; i< schema.getClaves().getClave().size(); i++){
			if(schema.getClaves().getClave().get(i).getCodigo().toString().equals(key)){
				schema.getClaves().getClave().get(i).setValor(value);
				bool = false;
			}
		}
		if(bool){
			Clave c = new Clave();
			c.setCodigo(BigInteger.valueOf(Integer.parseInt(key)));
			c.setValor(value);
			schema.getClaves().getClave().add(c);
		}
		request.getSession().putValue("d2DepositSchema"+cif, schema);
		request.getSession().putValue("ModifyD2DepositSchema"+cif,"true");
	}
	
	public Boolean isDigitalDeposit(Integer domainId){
		String domain = AonUtil.getDomainName();
		return DBConsults.isDigitalDeposit(domain, domainId);
	}
	
	public Boolean isModify(String cif){
		HttpServletRequest request = getThreadLocalRequest();
		String modify = (String) request.getSession().getAttribute("ModifyD2DepositSchema"+cif);
		return modify != null && modify.equals("true");
	}
	
	public void clearSession(String cif){
		HttpServletRequest request = getThreadLocalRequest();
		request.getSession().removeAttribute("d2DepositSchema"+cif);
		request.getSession().removeAttribute("ModifyD2DepositSchema"+cif);
	}
	
	public void saveDeposit(String cif, Integer domainId, Boolean textMode){
		String domain = AonUtil.getDomainName();
		HttpServletRequest request = getThreadLocalRequest();
		Esquema schema = (Esquema) request.getSession().getAttribute("d2DepositSchema"+cif);
		
		try {
			byte[] b = Utils.writeXml(schema);
			if(textMode){
				DBConsults.insertDeposit(domain, b, domainId, cif);
			}
			else DBConsults.insertDeposit(domain, b, domainId);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		request.getSession().removeAttribute("ModifyD2DepositSchema"+cif);
	}

	
	public Vector<MemoryTemplate> getDigitalDepositTemplates(Integer domainId){
		String domain = AonUtil.getDomainName();

		
		return  getDepositText(domain, domainId);

	}
	
	private Vector<MemoryTemplate> getDepositText(String domain,Integer domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
		
		Result<Record2<Integer, String>> data = ctx.getDslContext().select(RATTACH.ID, RATTACH.DESCRIPTION)
		 			.from(RATTACH)
					.where(RATTACH.DOMAIN.eq(domainId))
					.and(RATTACH.TYPE.eq((byte)RegistryAttachmentType.D2_DEPOSIT.ordinal())).fetch();
		
		Vector<MemoryTemplate> v = new Vector<MemoryTemplate>();
		for (Record2<Integer, String> record2 : data) {
			MemoryTemplate mt = new MemoryTemplate();
			mt.setId(record2.value1());
			mt.setName(record2.value2());
			v.add(mt);
		}
		
		return v;
		}finally {
			if (ctx != null) ctx.close();
		}
		
	}
	
	public MemoryTemplate createTextMemory(Integer domainId, String name){
		String domain = AonUtil.getDomainName();
		MemoryTemplate mt = new MemoryTemplate();
		
		byte[] data = Utils.CreateXml("", name );
		Integer id = DBConsults.insertDepositText(domain, name, data, domainId);
		
		mt.setId(id);
		mt.setName(name);

		return mt;
		
	}


	public void updateTexts(MemoryTemplate mt, Integer domainId, String cif) {
		String domain = AonUtil.getDomainName();
		HttpServletRequest request = getThreadLocalRequest();
		Esquema schema = DBConsults.getDeposit(domain, domainId, mt.getId().toString());

		for(Integer i = 0; i< schema.getClaves().getClave().size(); i++){
			if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9019001")){
				updateSchema(cif, domainId, "9019001", schema.getClaves().getClave().get(i).getValor());
			}
			else if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9029001")){
				updateSchema(cif, domainId, "9029001", schema.getClaves().getClave().get(i).getValor());
			}
			else if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9039001")){
				updateSchema(cif, domainId, "9039001", schema.getClaves().getClave().get(i).getValor());
			}
			else if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9049001")){
				updateSchema(cif, domainId, "9049001", schema.getClaves().getClave().get(i).getValor());
			}
			else if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9059001")){
				updateSchema(cif, domainId, "9059001", schema.getClaves().getClave().get(i).getValor());
			}
			else if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9069001")){
				updateSchema(cif, domainId, "9069001", schema.getClaves().getClave().get(i).getValor());
			}
			else if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9079001")){
				updateSchema(cif, domainId, "9079001", schema.getClaves().getClave().get(i).getValor());
			}
			else if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9089001")){
				updateSchema(cif, domainId, "9089001", schema.getClaves().getClave().get(i).getValor());
			}
			else if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9099001")){
				updateSchema(cif, domainId, "9099001", schema.getClaves().getClave().get(i).getValor());
			}
			else if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9119001")){
				updateSchema(cif, domainId, "9119001", schema.getClaves().getClave().get(i).getValor());
			}
			else if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9129001")){
				updateSchema(cif, domainId, "9129001", schema.getClaves().getClave().get(i).getValor());
			}
			else if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9139001")){
				updateSchema(cif, domainId, "9139001", schema.getClaves().getClave().get(i).getValor());
			}
			else if(schema.getClaves().getClave().get(i).getCodigo().toString().equals("9149001")){
				updateSchema(cif, domainId, "9149001", schema.getClaves().getClave().get(i).getValor());
			}
		}
		saveDeposit(cif, domainId, false);
		
	}
	
	public String getDateStr(Date date){
		return "";
	}
	
	public Date getDate(String str){
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		Date date = null;
		try {
        	date = formatter.parse(str);
        }catch (ParseException e) {
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
						.from(DOMAIN)
						.where(DOMAIN.ID.eq(domainId))
						.fetchOne();
			
			return data.value1();
			
				
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}
	
	public String getDomainName(Integer domainId){
		String domain = AonUtil.getDomainName();
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain, domainId);
			
			Record1<String> data = ctx.getDslContext().select(DOMAIN.NAME)
						.from(DOMAIN)
						.where(DOMAIN.ID.eq(domainId))
						.fetchOne();
			
			return data.value1();
			
				
			
		}finally {
			if (ctx != null) ctx.close();
		}
	}

	public void importSocietyValues(String document, Integer domainId){
		String domainName = getDomainName(domainId);
		
		// 2014
		
		Mod2002014 mod2002014 = com.esferalia.aon.occam.api.AON.getMod2002014ByYear(domainName, domainId, 2014);
		Map<D2DepositHeaderKey, Double> ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002014toD2.fill(ctx, mod2002014);
		
		for(D2DepositHeaderKey key : ctx.keySet()){
			updateSchema(document, domainId, key.getCode(), ctx.get(key).toString());
		}
		
		// 2013

		/*Mod2002013 mod2002013 = com.esferalia.aon.occam.api.AON.getMod2002013ByYear(domainName, domainId, 2013);
		ctx = new LinkedHashMap<D2DepositHeaderKey, Double>();
		Mod2002013toD2.fill(ctx, mod2002013);
				
		for(D2DepositHeaderKey key : ctx.keySet()){
			updateSchema(document, domainId, key.getCode(), ctx.get(key).toString());
		}*/
	}
	
	public void createD2Deposit(Integer domainId, Integer id, String name){
		String domainName = AonUtil.getDomainName();
		Enterprise enterprise = AON.getEnterprise(domainName, domainId, id);
		byte[] b = Utils.CreateXml(enterprise, name);
		Integer depositId = DBConsults.insertDeposit(domainName, b, domainId);
	}
}
