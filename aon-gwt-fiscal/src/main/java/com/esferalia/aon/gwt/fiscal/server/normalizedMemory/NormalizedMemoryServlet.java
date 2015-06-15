package com.esferalia.aon.gwt.fiscal.server.normalizedMemory;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.fiscal.client.normalizedMemory.INormalizedMemory;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema.Claves.Clave;
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
	
	public Map<String, String> getSchema(String part, Integer domainId){
		String domain = AonUtil.getDomainName();
		HttpServletRequest request = getThreadLocalRequest();
		String cif = DBConsults.getCIF(domain, domainId);
		Esquema schema = (Esquema) request.getSession().getAttribute("d2DepositSchema"+cif);
		if(schema == null){
			//System.out.println(domainId);
			schema = DBConsults.getDeposit(domain, domainId);
			request.getSession().putValue("d2DepositSchema"+cif, schema);
		}
		//TODO
			// COMPROBAR SI EL SCHEMA ESTÁ EN LA SESIÓN
			// SI NO ESTA GETDEPOSIT() --> DE  DBCONSULTS.
			// DEVOLVER SCHEMA
		D2DepositKey[] keyList = null;
		switch (part) {
		case "IDA": keyList = D2DepositConstants.IDA_ABREVIATE_KEYS;break;
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
		
		default:
			break;
		}
		List<Clave> claves = schema.getClaves().getClave();
		Map<String, String> map=  new HashMap<String, String>(); 
		for(Integer i = 0; i < claves.size(); i++){
			for(Integer j = 0; j < keyList.length; j++){
				if(claves.get(i).getCodigo().toString().equals(keyList[j].getCode())){
					map.put(keyList[j].getName(), claves.get(i).getValor());
				}
			}
		}
		return map;
	}
	
	public void updateSchema(Integer domainId, String key, String value) {
		String domain = AonUtil.getDomainName();
		HttpServletRequest request = getThreadLocalRequest();
		String cif = DBConsults.getCIF(domain, domainId);
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
	
	public void saveDeposit(String cif, Integer domainId){
		String domain = AonUtil.getDomainName();
		HttpServletRequest request = getThreadLocalRequest();
		Esquema schema = (Esquema) request.getSession().getAttribute("d2DepositSchema"+cif);
		
		try {
			byte[] b = Utils.writeXml(schema);
			DBConsults.insertDeposit(domain, b, domainId);
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		request.getSession().removeAttribute("ModifyD2DepositSchema"+cif);
	}
	
}
