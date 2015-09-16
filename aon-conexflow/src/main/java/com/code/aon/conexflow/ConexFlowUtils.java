package com.code.aon.conexflow;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.code.aon.conexflow.ConexFlow.Query;
import com.code.aon.conexflow.jooq.DBConsults;
import com.code.aon.ui.util.AonUtil;

public class ConexFlowUtils {

	//******************* Card Payment
	
	protected static ConexFlow getConexFlowCardPaymentQuery() {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.SALE_OP);
		query.setEmpresa(DBConsults.getEnterpriseId(AonUtil.getDomainName(), getDomainId()));
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setSoporte("");
		query.setDocumento("");
		query.setFechaCad("");
		query.setImporte("");
		query.setMoneda("");
		query.setPlazos("");
		query.setSecurityCode("");
		query.setRefCliente("");
		query.setInfoAdicionalEntrada("");
		query.setCF_ReplyURL("");
		query.setCF_ReplyURLAuth("");
		query.setFlagAltaToken("");
		query.setFlagTestSaldo("");
		query.setObservaciones("");
		query.setRefTokenCliente("");
		query.setCentroOriginal("");
		query.setTpvOriginal("");
		query.setFechaOriginal("");
		query.setIdOperacionOriginal("");
		query.setAutOriginal("");
		
		cf.setQuery(query);
		return cf;
	}
	
	public static Query getConexFlowCardPaymentQuery(String empresa, String centro, String tpv, String token, String amount, String cliente) {
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.SALE_OP);
		query.setEmpresa(leftZeros(8, empresa));
		query.setCentro(leftZeros(4, centro));
		query.setTpv(leftZeros(4, tpv));
		query.setFecha(getCurrentDate());
		query.setHora(getCurrentTime());
		query.setSoporte("K");
		query.setDocumento(token);
		query.setFechaCad("");
		query.setImporte(amount);
		query.setMoneda("EUR");
		query.setPlazos("000");
		query.setSecurityCode("");
		query.setRefCliente(leftZeros(20, cliente));
		query.setInfoAdicionalEntrada("");
		query.setCF_ReplyURL("");
		query.setCF_ReplyURLAuth("");
		query.setFlagAltaToken("");
		query.setFlagTestSaldo("");
		query.setObservaciones("");
		query.setRefTokenCliente("");
		query.setCentroOriginal("");
		query.setTpvOriginal("");
		query.setFechaOriginal("");
		query.setIdOperacionOriginal("");
		query.setAutOriginal("");
		
		return query;
	}
	
	protected static List<NameValuePair> getCardPaymentParameters(Query query){
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_STR.getCode(), query.getOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.EMPRESA_STR.getCode(), query.getEmpresa()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_STR.getCode(), query.getCentro()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_STR.getCode(), query.getTpv()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_STR.getCode(), query.getFecha()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_STR.getCode(), query.getHora()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.SOPORTE_STR.getCode(), query.getSoporte()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.DOCUMENTO_STR.getCode(), query.getDocumento()));
		if(!query.getFechaCad().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHACAD_STR.getCode(), query.getFechaCad()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.IMPORTE_STR.getCode(), query.getImporte()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.MONEDA_STR.getCode(), query.getMoneda()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.PLAZOS_STR.getCode(), query.getPlazos()));
		if(!query.getSecurityCode().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.SECURITYCODE_STR.getCode(), query.getSecurityCode()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_CLIENTE_STR.getCode(), query.getRefCliente()));
		if(!query.getInfoAdicionalEntrada().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.INFOADICIONALENTRADA_STR.getCode(), query.getInfoAdicionalEntrada()));
		if(!query.getCF_ReplyURL().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURL_STR.getCode(), query.getCF_ReplyURL()));
		if(!query.getCF_ReplyURLAuth().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURLAUTH_STR.getCode(), query.getCF_ReplyURLAuth()));
		if(!query.getFlagAltaToken().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FLAGALTATOKEN_STR.getCode(), query.getFlagAltaToken()));
		if(!query.getFlagTestSaldo().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FLAGTESTSALDO_STR.getCode(), query.getFlagTestSaldo()));
		if(!query.getObservaciones().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OBSERVACIONES_STR.getCode(), query.getObservaciones()));
		if(!query.getRefTokenCliente().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_TOKEN_CLIENTE_STR.getCode(), query.getRefTokenCliente()));
		if(!query.getCentroOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_ORIGINAL_STR.getCode(), query.getCentroOriginal()));
		if(!query.getTpvOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_ORIGINAL_MAYUS_STR.getCode(), query.getTpvOriginal()));
		if(!query.getFechaOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_ORIGINAL_STR.getCode(), query.getFechaOriginal()));
		if(!query.getIdOperacionOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.ID_OPERACION_ORIGINAL_STR.getCode(), query.getIdOperacionOriginal()));
		if(!query.getAutOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.AUT_ORIGINAL_STR.getCode(), query.getAutOriginal()));
	
		String cadena = query.getOperacion() + query.getEmpresa() + query.getCentro() +
				query.getTpv() + query.getFecha() + query.getHora() +
				query.getSoporte() + query.getDocumento() + query.getFechaCad() +
				query.getImporte() + query.getMoneda() +query.getPlazos() + 
				query.getSecurityCode() + query.getRefCliente() + query.getInfoAdicionalEntrada() +
				query.getCF_ReplyURL() + query.getCF_ReplyURLAuth() + query.getFlagAltaToken() +
				query.getFlagTestSaldo() + query.getObservaciones() + query.getRefTokenCliente() +
				query.getCentroOriginal() + query.getTpvOriginal() + query.getFechaOriginal() +
				query.getIdOperacionOriginal() + query.getAutOriginal();
	
		try {
			urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_MAC_STR.getCode()
					, claveMAC.dameClaveMAC(cadena, ConexFlowEnum.CONEXFLOW_MAC_KEYA.getCode(), ConexFlowEnum.CONEXFLOW_MAC_KEYB.getCode())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return urlParameters;
	}
	
	//******************* Preauthorization Payment
	
	protected static ConexFlow getConexFlowPreauthorizationPaymentQuery() {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.PREAUTHORIZATION_OP);
		query.setEmpresa("");
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setSoporte("");
		query.setDocumento("");
		query.setFechaCad("");
		query.setImporte("");
		query.setMoneda("");
		query.setPlazos("");
		query.setSecurityCode("");
		query.setRefCliente("");
		query.setInfoAdicionalEntrada("");
		query.setCF_ReplyURL("");
		query.setCF_ReplyURLAuth("");
		query.setFlagAltaToken("");
		query.setFlagTestSaldo("");
		query.setObservaciones("");
		query.setRefTokenCliente("");
		query.setCentroOriginal("");
		query.setTpvOriginal("");
		query.setFechaOriginal("");
		query.setIdOperacionOriginal("");
		query.setAutOriginal("");
		
		cf.setQuery(query);
		return cf;
	}
	
	public static Query getConexFlowPreauthorizationPaymentQuery(String empresa, String centro, String tpv, String cliente, String token, Double amount) {
		Integer eur = amount.intValue();
		Double cent = (amount - eur.doubleValue()) * 100;
		
		Integer importe = (eur * 100) + cent.intValue(); 
		
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.PREAUTHORIZATION_OP);
		query.setEmpresa(leftZeros(8, empresa));
		query.setCentro(leftZeros(4, centro));
		query.setTpv(leftZeros(4, tpv));
		query.setFecha(getCurrentDate());
		query.setHora(getCurrentTime());
		query.setSoporte("K");
		query.setDocumento(token);
		query.setFechaCad("");
		query.setImporte(importe.toString());
		query.setMoneda("EUR");
		query.setPlazos("000");
		query.setSecurityCode("");
		query.setRefCliente(leftZeros(20, cliente));
		query.setInfoAdicionalEntrada("");
		query.setCF_ReplyURL("");
		query.setCF_ReplyURLAuth("");
		query.setFlagAltaToken("");
		query.setFlagTestSaldo("");
		query.setObservaciones("");
		query.setRefTokenCliente("");
		query.setCentroOriginal("");
		query.setTpvOriginal("");
		query.setFechaOriginal("");
		query.setIdOperacionOriginal("");
		query.setAutOriginal("");
		
		return query;
	}
	
	protected static List<NameValuePair> getPreauthorizationPaymentParameters(Query query){
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_STR.getCode(), query.getOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.EMPRESA_STR.getCode(), query.getEmpresa()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_STR.getCode(), query.getCentro()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_STR.getCode(), query.getTpv()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_STR.getCode(), query.getFecha()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_STR.getCode(), query.getHora()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.SOPORTE_STR.getCode(), query.getSoporte()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.DOCUMENTO_STR.getCode(), query.getDocumento()));
		if(!query.getFechaCad().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHACAD_STR.getCode(), query.getFechaCad()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.IMPORTE_STR.getCode(), query.getImporte()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.MONEDA_STR.getCode(), query.getMoneda()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.PLAZOS_STR.getCode(), query.getPlazos()));
		if(!query.getSecurityCode().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.SECURITYCODE_STR.getCode(), query.getSecurityCode()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_CLIENTE_STR.getCode(), query.getRefCliente()));
		if(!query.getInfoAdicionalEntrada().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.INFOADICIONALENTRADA_STR.getCode(), query.getInfoAdicionalEntrada()));
		if(!query.getCF_ReplyURL().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURL_STR.getCode(), query.getCF_ReplyURL()));
		if(!query.getCF_ReplyURLAuth().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURLAUTH_STR.getCode(), query.getCF_ReplyURLAuth()));
		if(!query.getFlagAltaToken().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FLAGALTATOKEN_STR.getCode(), query.getFlagAltaToken()));
		if(!query.getFlagTestSaldo().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FLAGTESTSALDO_STR.getCode(), query.getFlagTestSaldo()));
		if(!query.getObservaciones().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OBSERVACIONES_STR.getCode(), query.getObservaciones()));
		if(!query.getRefTokenCliente().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_TOKEN_CLIENTE_STR.getCode(), query.getRefTokenCliente()));
		if(!query.getCentroOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_ORIGINAL_STR.getCode(), query.getCentroOriginal()));
		if(!query.getTpvOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_ORIGINAL_MAYUS_STR.getCode(), query.getTpvOriginal()));
		if(!query.getFechaOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_ORIGINAL_STR.getCode(), query.getFechaOriginal()));
		if(!query.getIdOperacionOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.ID_OPERACION_ORIGINAL_STR.getCode(), query.getIdOperacionOriginal()));
		if(!query.getAutOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.AUT_ORIGINAL_STR.getCode(), query.getAutOriginal()));
	
		String cadena = query.getOperacion() + query.getEmpresa() + query.getCentro() +
				query.getTpv() + query.getFecha() + query.getHora() +
				query.getSoporte() + query.getDocumento() + query.getFechaCad() +
				query.getImporte() + query.getMoneda() +query.getPlazos() + 
				query.getSecurityCode() + query.getRefCliente() + query.getInfoAdicionalEntrada() +
				query.getCF_ReplyURL() + query.getCF_ReplyURLAuth() + query.getFlagAltaToken() +
				query.getFlagTestSaldo() + query.getObservaciones() + query.getRefTokenCliente() +
				query.getCentroOriginal() + query.getTpvOriginal() + query.getFechaOriginal() +
				query.getIdOperacionOriginal() + query.getAutOriginal();
	
		try {
			urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_MAC_STR.getCode()
					, claveMAC.dameClaveMAC(cadena, ConexFlowEnum.CONEXFLOW_MAC_KEYA.getCode(), ConexFlowEnum.CONEXFLOW_MAC_KEYB.getCode())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return urlParameters;
	}
	
	//******************* Continue Card Payment with Authentication Request
	
	//******************* Refund
	
	protected static ConexFlow getConexFlowRefundQuery() {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.REFUND_OP);
		query.setEmpresa("");
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setOperador("");
		query.setSoporte("");
		query.setDocumento("");
		query.setFechaCad("");
		query.setImporte("");
		query.setMoneda("");
		query.setRefCliente("");
		query.setInfoAdicionalEntrada("");
		query.setCF_ReplyURL("");
		query.setObservaciones("");
		query.setCentroOriginal("");
		query.setTpvOriginal("");
		query.setFechaOriginal("");
		query.setIdOperacionOriginal("");
		
		cf.setQuery(query);
		return cf;
	}
	
	protected static Query getConexFlowRefundQuery(String empresa, String centro, String tpv, String token, String amount, String cliente) {
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.REFUND_OP);
		query.setEmpresa(leftZeros(8, empresa));
		query.setCentro(leftZeros(4, centro));
		query.setTpv(leftZeros(4, tpv));
		query.setFecha(getCurrentDate());
		query.setHora(getCurrentTime());
		query.setOperador("");
		query.setSoporte("K");
		query.setDocumento(token);
		query.setFechaCad("");
		query.setImporte(amount);
		query.setMoneda("EUR");
		query.setRefCliente(leftZeros(20, cliente));
		query.setInfoAdicionalEntrada("");
		query.setCF_ReplyURL("");
		query.setObservaciones("");
		query.setCentroOriginal("");
		query.setTpvOriginal("");
		query.setFechaOriginal("");
		query.setIdOperacionOriginal("");
		
		return query;
	}
	
	protected static List<NameValuePair> getRefundParameters(Query query){
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_STR.getCode(), query.getOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.EMPRESA_STR.getCode(), query.getEmpresa()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_STR.getCode(), query.getCentro()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_STR.getCode(), query.getTpv()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_STR.getCode(), query.getFecha()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_STR.getCode(), query.getHora()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERADOR_STR.getCode(), query.getOperador()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.SOPORTE_STR.getCode(), query.getSoporte()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.DOCUMENTO_STR.getCode(), query.getDocumento()));
		if(!query.getFechaCad().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHACAD_STR.getCode(), query.getFechaCad()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.IMPORTE_STR.getCode(), query.getImporte()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.MONEDA_STR.getCode(), query.getMoneda()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_CLIENTE_STR.getCode(), query.getRefCliente()));
		if(!query.getInfoAdicionalEntrada().equals(""))urlParameters.add(new BasicNameValuePair(ConexFlowEnum.INFOADICIONALENTRADA_STR.getCode(), query.getInfoAdicionalEntrada()));
		if(!query.getCF_ReplyURL().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURL_STR.getCode(), query.getCF_ReplyURL()));
		if(!query.getObservaciones().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OBSERVACIONES_STR.getCode(), query.getObservaciones()));
		if(!query.getCentroOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_ORIGINAL_STR.getCode(), query.getCentroOriginal()));
		if(!query.getTpvOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_ORIGINAL_MAYUS_STR.getCode(), query.getTpvOriginal()));
		if(!query.getFechaOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_ORIGINAL_STR.getCode(), query.getFechaOriginal()));
		if(!query.getIdOperacionOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.ID_OPERACION_ORIGINAL_STR.getCode(), query.getIdOperacionOriginal()));
	
		String cadena = query.getOperacion() + query.getEmpresa() + query.getCentro() +
				query.getTpv() + query.getFecha() + query.getHora() + query.getOperador() +
				query.getSoporte() + query.getDocumento() + query.getFechaCad() +
				query.getImporte() + query.getMoneda() + query.getRefCliente() + 
				query.getInfoAdicionalEntrada() + query.getCF_ReplyURL() + query.getObservaciones() + 
				query.getCentroOriginal() + query.getTpvOriginal() + query.getFechaOriginal() +
				query.getIdOperacionOriginal();
	
		try {
			urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_MAC_STR.getCode()
					, claveMAC.dameClaveMAC(cadena, ConexFlowEnum.CONEXFLOW_MAC_KEYA.getCode(), ConexFlowEnum.CONEXFLOW_MAC_KEYB.getCode())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return urlParameters;
	}
	
	//******************* Get Transaction Information
	
	protected static ConexFlow getConexFlowTransactionInfoQuery() {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.TRANSACTION_INFO_OP);
		query.setEmpresa("");
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setOperador("");
		query.setTipoReferencia("");
		query.setFechaOriginal("");
		query.setCentroOriginal("");
		query.setTpvOriginal("");
		query.setRefClienteOriginal("");
		query.setHoraOriginal("");
		query.setIdOperacionOriginal("");
		query.setCF_ReplyURL("");
			
		cf.setQuery(query);
		return cf;
	}
	
	protected static List<NameValuePair> getTransactionInfoParameters(Query query){
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_STR.getCode(), query.getOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.EMPRESA_STR.getCode(), query.getEmpresa()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_STR.getCode(), query.getCentro()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_STR.getCode(), query.getTpv()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_STR.getCode(), query.getFecha()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_STR.getCode(), query.getHora()));
		if(!query.getOperador().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERADOR_STR.getCode(), query.getOperador()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TIPO_REFERENCIA_STR.getCode(), query.getTipoReferencia()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_ORIGINAL_STR.getCode(), query.getFechaOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_ORIGINAL_STR.getCode(), query.getCentroOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_ORIGINAL_STR.getCode(), query.getTpvOriginal()));
		if(!query.getRefClienteOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_CLIENTE_ORIGINAL_STR.getCode(), query.getRefClienteOriginal()));
		if(!query.getHoraOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_ORIGINAL_STR.getCode(), query.getHoraOriginal()));
		if(!query.getIdOperacionOriginal().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.ID_OPERACION_ORIGINAL_STR.getCode(), query.getIdOperacionOriginal()));
		if(!query.getCF_ReplyURL().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURL_STR.getCode(), query.getCF_ReplyURL()));

		
		String cadena = query.getOperacion() + query.getEmpresa() + query.getCentro() +
				query.getTpv() + query.getFecha() + query.getHora() + query.getOperador() +
				query.getTipoReferencia() + query.getFechaOriginal() + query.getCentroOriginal() +
				query.getTpvOriginal() + query.getRefClienteOriginal() + query.getHoraOriginal() +
				query.getIdOperacionOriginal() + query.getCF_ReplyURL();
	
		try {
			urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_MAC_STR.getCode()
					, claveMAC.dameClaveMAC(cadena, ConexFlowEnum.CONEXFLOW_MAC_KEYA.getCode(), ConexFlowEnum.CONEXFLOW_MAC_KEYB.getCode())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return urlParameters;
	}
	
	//******************* Cancelation
	
	protected static ConexFlow getConexFlowCancelationQuery() {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.CANCELATION_OP);
		query.setEmpresa("");
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setOperacionOriginal("");
		query.setImporte("");
		query.setImporteOriginal("");
		query.setAutOriginal("");
		query.setRefCliente("");
		query.setInfoAdicionalEntrada("");
		query.setCF_ReplyURL("");
		query.setObservaciones("");
		query.setCentroOriginal("");
		query.setTpvOriginal("");
		query.setFechaOriginal("");
		query.setIdOperacionOriginal("");
		
		cf.setQuery(query);
		return cf;
	}
	
	public static Query getConexFlowCancelationQuery(String empresa, String centro, String tpv, String cancelOperation, String amount, String amountOriginal, String autorizacion, String cliente, String operacionId, String fechaOriginal) {
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.CANCELATION_OP);
		query.setEmpresa(leftZeros(8, empresa));
		query.setCentro(leftZeros(4, centro));
		query.setTpv(leftZeros(4, tpv));
		query.setFecha(getCurrentDate());
		query.setHora(getCurrentTime());
		query.setOperacionOriginal(cancelOperation);
		query.setImporte(amount);
		query.setImporteOriginal(amountOriginal);
		query.setAutOriginal(autorizacion);
		query.setRefCliente(leftZeros(20, cliente));
		query.setInfoAdicionalEntrada("");
		query.setCF_ReplyURL("");
		query.setObservaciones("");
		query.setCentroOriginal(leftZeros(4, centro));
		query.setTpvOriginal(leftZeros(4, tpv));
		query.setFechaOriginal(fechaOriginal);
		query.setIdOperacionOriginal(operacionId);
		
		return query;
	}
	
	protected static List<NameValuePair> getCancelationParameters(Query query){
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_STR.getCode(), query.getOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.EMPRESA_STR.getCode(), query.getEmpresa()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_STR.getCode(), query.getCentro()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_STR.getCode(), query.getTpv()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_STR.getCode(), query.getFecha()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_STR.getCode(), query.getHora()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_ORIGINAL_STR.getCode(), query.getOperacionOriginal()));
		if(!query.getImporte().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.IMPORTE_STR.getCode(), query.getImporte()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.IMPORTE_ORIGINAL_STR.getCode(), query.getImporteOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.AUT_ORIGINAL_STR.getCode(), query.getAutOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_CLIENTE_STR.getCode(), query.getRefCliente()));
		if(!query.getInfoAdicionalEntrada().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.INFOADICIONALENTRADA_STR.getCode(), query.getInfoAdicionalEntrada()));
		if(!query.getCF_ReplyURL().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURL_STR.getCode(), query.getCF_ReplyURL()));
		if(!query.getObservaciones().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OBSERVACIONES_STR.getCode(), query.getObservaciones()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_ORIGINAL_STR.getCode(), query.getCentroOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_ORIGINAL_MAYUS_STR.getCode(), query.getTpvOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_ORIGINAL_STR.getCode(), query.getFechaOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.ID_OPERACION_ORIGINAL_STR.getCode(), query.getIdOperacionOriginal()));
	
		String cadena = query.getOperacion() + query.getEmpresa() + query.getCentro() +
				query.getTpv() + query.getFecha() + query.getHora() +query.getOperacionOriginal() + 
				query.getImporte() + query.getImporteOriginal() + query.getAutOriginal() + 
				query.getRefCliente() + query.getInfoAdicionalEntrada() + query.getCF_ReplyURL() + 
				query.getObservaciones() + query.getCentroOriginal() + query.getTpvOriginal() + 
				query.getFechaOriginal() + query.getIdOperacionOriginal();
	
		try {
			urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_MAC_STR.getCode()
					, claveMAC.dameClaveMAC(cadena, ConexFlowEnum.CONEXFLOW_MAC_KEYA.getCode(), ConexFlowEnum.CONEXFLOW_MAC_KEYB.getCode())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return urlParameters;
	}
	
	//******************* Confirm Preauthorization
	
	protected static ConexFlow getConexFlowConfirmPreauthorizationQuery() {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
		query.setEmpresa("");
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setDocumento("");
		query.setFechaCad("");
		query.setImporte("");
		query.setImporteOriginal("");
		query.setAutOriginal("");
		query.setMoneda("");
		query.setRefCliente("");
		query.setInfoAdicionalEntrada("");
		query.setCF_ReplyURL("");
		query.setObservaciones("");
		query.setCentroOriginal("");
		query.setTpvOriginal("");
		query.setFechaOriginal("");
		query.setIdOperacionOriginal("");
		
		cf.setQuery(query);
		return cf;
	}
	
	public static Query getConexFlowConfirmPreauthorizationQuery(String empresa, String centro, String tpv, String cliente, String token, Double amount, Integer amountOriginal, String fechaCad, String autorizacion, String fechaOriginal, String operacionId) {
		Integer eur = amount.intValue();
		Double cent = (amount - eur.doubleValue()) * 100;
		Integer importe = (eur * 100) + cent.intValue(); 
		
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.CONFIRM_PREAUTHORIZATION_OP);
		query.setEmpresa(leftZeros(8, empresa));
		query.setCentro(leftZeros(4, centro));
		query.setTpv(leftZeros(4, tpv));
		query.setFecha(getCurrentDate());
		query.setHora(getCurrentTime());
		query.setDocumento(token);
		query.setFechaCad(fechaCad);
		query.setImporte(importe.toString());
		query.setImporteOriginal(amountOriginal != null ? amountOriginal.toString() : importe.toString());
		query.setAutOriginal(autorizacion);
		query.setMoneda("EUR");
		query.setRefCliente(leftZeros(20, cliente));
		query.setInfoAdicionalEntrada("");
		query.setCF_ReplyURL("");
		query.setObservaciones("");
		query.setCentroOriginal(leftZeros(4, centro));
		query.setTpvOriginal(leftZeros(4, tpv));
		query.setFechaOriginal(fechaOriginal);
		query.setIdOperacionOriginal(operacionId);
		
		return query;
	}
	
	protected static List<NameValuePair> getConfirmPreauthorizationParameters(Query query){
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_STR.getCode(), query.getOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.EMPRESA_STR.getCode(), query.getEmpresa()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_STR.getCode(), query.getCentro()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_STR.getCode(), query.getTpv()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_STR.getCode(), query.getFecha()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_STR.getCode(), query.getHora()));
		//urlParameters.add(new BasicNameValuePair(ConexFlowEnum.DOCUMENTO_STR.getCode(), query.getDocumento()));
		//urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHACAD_STR.getCode(), query.getFechaCad()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_ORIGINAL_STR.getCode(), query.getCentroOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_ORIGINAL_MAYUS_STR.getCode(), query.getTpvOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.AUT_ORIGINAL_STR.getCode(), query.getAutOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.IMPORTE_STR.getCode(), query.getImporte()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.IMPORTE_ORIGINAL_STR.getCode(), query.getImporteOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_ORIGINAL_STR.getCode(), query.getFechaOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.ID_OPERACION_ORIGINAL_STR.getCode(), query.getIdOperacionOriginal()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_CLIENTE_STR.getCode(), query.getRefCliente()));
		//if(!query.getInfoAdicionalEntrada().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.INFOADICIONALENTRADA_STR.getCode(), query.getInfoAdicionalEntrada()));
		//if(!query.getCF_ReplyURL().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURL_STR.getCode(), query.getCF_ReplyURL()));

		//String cadena = query.getOperacion() + query.getEmpresa() + query.getCentro() +
			//	query.getTpv() + query.getFecha() + query.getHora() +//query.getDocumento() + 
			//	/*query.getFechaCad() +*/ query.getCentroOriginal() + query.getTpvOriginal() + query.getAutOriginal() +
			//	query.getImporte() + query.getImporteOriginal() + query.getFechaOriginal() +
			//	query.getIdOperacionOriginal() + 
			//	query.getRefCliente();// + 
			//	query.getInfoAdicionalEntrada() + query.getCF_ReplyURL();
		
		
		String cadena2 = query.getOperacion() + query.getEmpresa() + query.getCentro() +
				query.getTpv() + query.getFecha() + query.getHora() +
				query.getCentroOriginal() + query.getTpvOriginal() + query.getFechaOriginal() +
				query.getIdOperacionOriginal() +  
				query.getImporte() + query.getImporteOriginal() + query.getAutOriginal() +
				
				 query.getRefCliente();
	
		try {
			urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_MAC_STR.getCode()
					, claveMAC.dameClaveMAC(cadena2, ConexFlowEnum.CONEXFLOW_MAC_KEYA.getCode(), ConexFlowEnum.CONEXFLOW_MAC_KEYB.getCode())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		//if(!query.getObservaciones().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OBSERVACIONES_STR.getCode(), query.getObservaciones()));
		//if(!query.getMoneda().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.MONEDA_STR.getCode(), query.getMoneda()));
		//urlParameters.add(new BasicNameValuePair(ConexFlowEnum.SOPORTE_STR.getCode(), "K"));
		
		return urlParameters;
	}
	
	//******************* Voucher Redemption
	
	protected static ConexFlow getConexFlowVoucherQuery() {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.REDEMPTION_OP);
		query.setEmpresa("");
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setSoporte("");
		query.setDocumento("");
		query.setImporte("");
		query.setMoneda("");
		query.setValorDocumento("");
		query.setRefCliente("");
		query.setInfoAdicionalEntrada("");
		query.setCF_ReplyURL("");
		
		cf.setQuery(query);
		return cf;
	}
	
	protected static List<NameValuePair> getVoucherParameters(Query query){
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_STR.getCode(), query.getOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.EMPRESA_STR.getCode(), query.getEmpresa()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_STR.getCode(), query.getCentro()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_STR.getCode(), query.getTpv()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_STR.getCode(), query.getFecha()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_STR.getCode(), query.getHora()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.SOPORTE_STR.getCode(), query.getSoporte()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.DOCUMENTO_STR.getCode(), query.getDocumento()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.IMPORTE_STR.getCode(), query.getImporte()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.MONEDA_STR.getCode(), query.getMoneda()));	
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.VALORDOCUMENTO_STR.getCode(), query.getValorDocumento()));	
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_CLIENTE_STR.getCode(), query.getRefCliente()));
		if(!query.getInfoAdicionalEntrada().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.INFOADICIONALENTRADA_STR.getCode(), query.getInfoAdicionalEntrada()));	
		if(!query.getCF_ReplyURL().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURL_STR.getCode(), query.getCF_ReplyURL()));
	
		String cadena = query.getOperacion() + query.getEmpresa() + query.getCentro() +
				query.getTpv() + query.getFecha() + query.getHora() + query.getSoporte() +
				query.getDocumento() + query.getImporte() + query.getMoneda() + query.getValorDocumento() +
				query.getRefCliente() + query.getInfoAdicionalEntrada() + query.getCF_ReplyURL();

		try {
			urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_MAC_STR.getCode()
					, claveMAC.dameClaveMAC(cadena, ConexFlowEnum.CONEXFLOW_MAC_KEYA.getCode(), ConexFlowEnum.CONEXFLOW_MAC_KEYB.getCode())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return urlParameters;
	}
	
	//******************* Issue of an Electronic Reference
	
	protected static ConexFlow getConexFlowIssueQuery() {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.ISSUE_OP);
		query.setEmpresa("");
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setSoporte("");
		query.setDocumento("");
		query.setImporte("");
		query.setMoneda("");
		query.setRefCliente("");
		query.setInfoAdicionalEntrada("");
		query.setCF_ReplyURL("");
		
		cf.setQuery(query);
		return cf;
	}
	
	protected static List<NameValuePair> getIssueParameters(Query query){
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_STR.getCode(), query.getOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.EMPRESA_STR.getCode(), query.getEmpresa()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_STR.getCode(), query.getCentro()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_STR.getCode(), query.getTpv()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_STR.getCode(), query.getFecha()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_STR.getCode(), query.getHora()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.SOPORTE_STR.getCode(), query.getSoporte()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.DOCUMENTO_STR.getCode(), query.getDocumento()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.IMPORTE_STR.getCode(), query.getImporte()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.MONEDA_STR.getCode(), query.getMoneda()));	
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_CLIENTE_STR.getCode(), query.getRefCliente()));
		if(!query.getInfoAdicionalEntrada().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.INFOADICIONALENTRADA_STR.getCode(), query.getInfoAdicionalEntrada()));	
		if(!query.getCF_ReplyURL().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURL_STR.getCode(), query.getCF_ReplyURL()));
	
		String cadena = query.getOperacion() + query.getEmpresa() + query.getCentro() +
				query.getTpv() + query.getFecha() + query.getHora() + query.getSoporte() +
				query.getDocumento() + query.getImporte() + query.getMoneda() + 
				query.getRefCliente() + query.getInfoAdicionalEntrada() + query.getCF_ReplyURL();

		try {
			urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_MAC_STR.getCode()
					, claveMAC.dameClaveMAC(cadena, ConexFlowEnum.CONEXFLOW_MAC_KEYA.getCode(), ConexFlowEnum.CONEXFLOW_MAC_KEYB.getCode())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return urlParameters;
	}
	
	//******************* Response Acknowlegement
	
	protected static ConexFlow getConexFlowACKQuery(String op) {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(op);
		query.setEmpresa("");
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setIdOperacion("");
		query.setRefCliente("");
		query.setACK("");
		query.setCF_ReplyURL("");
		
		cf.setQuery(query);
		return cf;
	}
	
	protected static List<NameValuePair> getACKParameters(Query query){
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_STR.getCode(), query.getOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.EMPRESA_STR.getCode(), query.getEmpresa()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_STR.getCode(), query.getCentro()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_STR.getCode(), query.getTpv()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_STR.getCode(), query.getFecha()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_STR.getCode(), query.getHora()));
		if(!query.getIdOperacion().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.ID_OPERACION_STR.getCode(), query.getIdOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_CLIENTE_STR.getCode(), query.getRefCliente()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.ACK_STR.getCode(), query.getACK()));
		if(!query.getCF_ReplyURL().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURL_STR.getCode(), query.getCF_ReplyURL()));
		
		String cadena = query.getOperacion() + query.getEmpresa() + query.getCentro() +
				query.getTpv() + query.getFecha() + query.getHora() +
				query.getIdOperacion() + query.getRefCliente() + query.getACK() +
				query.getCF_ReplyURL();

		try {
			urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_MAC_STR.getCode()
					, claveMAC.dameClaveMAC(cadena, ConexFlowEnum.CONEXFLOW_MAC_KEYA.getCode(), ConexFlowEnum.CONEXFLOW_MAC_KEYB.getCode())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return urlParameters;
	}
	
	//******************* Create Token
	
	protected static ConexFlow getConexFlowCreateTokenQuery() {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.CREATE_TOKEN_OP);
		query.setEmpresa("");
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setDocumento("");
		query.setFechaCad("");
		query.setRefCliente("");
		query.setCF_ReplyURL("");
		query.setRefTokenCliente("");
		
		cf.setQuery(query);
		return cf;
	}
	
	public static Query getConexFlowCreateTokenQuery(String creditCardNumber, String empresa, String centro, String tpv, String fechaCad, String cliente) {
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.CREATE_TOKEN_OP);
		query.setEmpresa(leftZeros(8, empresa));
		query.setCentro(leftZeros(4, centro));
		query.setTpv(leftZeros(4, tpv));
		query.setFecha(getCurrentDate());
		query.setHora(getCurrentTime());
		query.setDocumento(creditCardNumber);
		query.setFechaCad(fechaCad);
		query.setRefCliente(leftZeros(20, cliente));
		query.setCF_ReplyURL("");
		query.setRefTokenCliente("");
		
		return query;
	}
	
	protected static List<NameValuePair> getCreateTokenParameters(Query query){
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_STR.getCode(), query.getOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.EMPRESA_STR.getCode(), query.getEmpresa()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_STR.getCode(), query.getCentro()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_STR.getCode(), query.getTpv()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_STR.getCode(), query.getFecha()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_STR.getCode(), query.getHora()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.DOCUMENTO_STR.getCode(), query.getDocumento()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHACAD_STR.getCode(), query.getFechaCad()));	
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_CLIENTE_STR.getCode(), query.getRefCliente()));
		if(!query.getCF_ReplyURL().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURL_STR.getCode(), query.getCF_ReplyURL()));
		if(!query.getRefTokenCliente().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_TOKEN_CLIENTE_STR.getCode(), query.getRefTokenCliente()));
		
		String cadena = query.getOperacion() + query.getEmpresa() + query.getCentro() +
				query.getTpv() + query.getFecha() + query.getHora() +
				query.getDocumento() + query.getFechaCad() + query.getRefCliente() +
				query.getCF_ReplyURL();

		try {
			urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_MAC_STR.getCode()
					, claveMAC.dameClaveMAC(cadena, ConexFlowEnum.CONEXFLOW_MAC_KEYA.getCode(), ConexFlowEnum.CONEXFLOW_MAC_KEYB.getCode())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return urlParameters;
	}
	
	//******************* Delete Token
	
	protected static ConexFlow getConexFlowDeleteTokenQuery() {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.DELETE_TOKEN_OP);
		query.setEmpresa("");
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setToken("");
		query.setRefCliente("");
		query.setCF_ReplyURL("");
		query.setRefTokenCliente("");
		
		cf.setQuery(query);
		return cf;
	}
	
	protected static List<NameValuePair> getDeleteTokenParameters(Query query){
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_STR.getCode(), query.getOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.EMPRESA_STR.getCode(), query.getEmpresa()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_STR.getCode(), query.getCentro()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_STR.getCode(), query.getTpv()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_STR.getCode(), query.getFecha()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_STR.getCode(), query.getHora()));
		if(!query.getToken().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TOKEN_STR.getCode(), query.getToken()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_CLIENTE_STR.getCode(), query.getRefCliente()));
		if(!query.getCF_ReplyURL().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURL_STR.getCode(), query.getCF_ReplyURL()));
		if(!query.getRefTokenCliente().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_TOKEN_CLIENTE_STR.getCode(), query.getRefTokenCliente()));


		String cadena = query.getOperacion() + query.getEmpresa() + query.getCentro() +
				query.getTpv() + query.getFecha() + query.getHora() +
				query.getToken() + query.getRefCliente() + query.getCF_ReplyURL() +
				query.getRefTokenCliente();
		try {
			urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_MAC_STR.getCode()
					, claveMAC.dameClaveMAC(cadena, ConexFlowEnum.CONEXFLOW_MAC_KEYA.getCode(), ConexFlowEnum.CONEXFLOW_MAC_KEYB.getCode())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return urlParameters;
	}
	
	//******************* Validate Card Number Check Digit
	
	protected static ConexFlow getConexFlowValidateCardQuery() {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.VALIDATE_CARD_OP);
		query.setEmpresa("");
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setDocumento("");
		query.setRefCliente("");
	
		cf.setQuery(query);
		return cf;
	}
	
	public static Query getConexFlowValidateCardQuery(String creditCardNumber, String empresa, String centro, String tpv, String client) {
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.VALIDATE_CARD_OP);
		query.setEmpresa(leftZeros(8, empresa));
		query.setCentro(leftZeros(4, centro));
		query.setTpv(leftZeros(4, tpv));
		query.setFecha(getCurrentDate());
		query.setHora(getCurrentTime());
		query.setDocumento(creditCardNumber);
		query.setRefCliente(leftZeros(20, client));
		query.setCF_ReplyURL("");
		return query;
	}
	
	protected static List<NameValuePair> getValidateCardParameters(Query query){
		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.OPERACION_STR.getCode(), query.getOperacion()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.EMPRESA_STR.getCode(), query.getEmpresa()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CENTRO_STR.getCode(), query.getCentro()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.TPV_STR.getCode(), query.getTpv()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.FECHA_STR.getCode(), query.getFecha()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.HORA_STR.getCode(), query.getHora()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.DOCUMENTO_STR.getCode(), query.getDocumento()));
		urlParameters.add(new BasicNameValuePair(ConexFlowEnum.REF_CLIENTE_STR.getCode(), query.getRefCliente()));
		if(!query.getCF_ReplyURL().equals("")) urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_REPLYURL_STR.getCode(), query.getCF_ReplyURL()));
		
		String cadena = query.getOperacion() + query.getEmpresa() + query.getCentro() +
				query.getTpv() + query.getFecha() + query.getHora() +
				query.getDocumento() + query.getRefCliente();
		try {
			urlParameters.add(new BasicNameValuePair(ConexFlowEnum.CF_MAC_STR.getCode()
					, claveMAC.dameClaveMAC(cadena, ConexFlowEnum.CONEXFLOW_MAC_KEYA.getCode(), ConexFlowEnum.CONEXFLOW_MAC_KEYB.getCode())));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return urlParameters;
	}
	
	//******************* New Card (Validate Card + Create Token) 
	
	protected static ConexFlow getConexFlowNewCardQuery() {
		ConexFlow cf = new ConexFlow();
		Query query = new Query();
		query.setOperacion(ConexFlowConstant.VALIDATE_CARD_OP);
		query.setEmpresa("");
		query.setCentro("");
		query.setTpv("0001");
		query.setFecha("");
		query.setHora("");
		query.setDocumento("");
		query.setFechaCad("");
		query.setRefCliente("");
		query.setCF_ReplyURL("");
		query.setRefTokenCliente("");
		
		cf.setQuery(query);
		return cf;
	}
	
	//******************* Utils
	
	public static Integer getDomainId(){
		/*DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		return ds.getDomainId();*/
		return 1;
	}	
	
	private static String leftZeros(Integer size, String code){
		Integer length = code.length();
		return generateZeros(size - length) + code;
	}
	
	private static String generateZeros(Integer n){
		String zeros = "";
		for(Integer i = 0; i<n; i++){
			zeros = zeros + "0";
		}
		return zeros;
	}
	
	private static String getCurrentDate(){
		Calendar cal = Calendar.getInstance();
		Integer day = cal.get(Calendar.DATE);
		Integer month = cal.get(Calendar.MONTH) + 1;
		Integer year = cal.get(Calendar.YEAR);
		return leftZeros(2, day.toString()) + leftZeros(2, month.toString()) + year.toString();
	}
	
	private static String getCurrentTime(){
		Calendar cal = Calendar.getInstance();
		Integer hour = cal.get(Calendar.HOUR_OF_DAY);
		Integer minute = cal.get(Calendar.MINUTE);
		Integer second = cal.get(Calendar.SECOND);
		return leftZeros(2, hour.toString()) + leftZeros(2, minute.toString()) + leftZeros(2, second.toString());
	}
}
