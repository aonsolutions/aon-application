package com.code.aon.conexflow;


public enum ConexFlowEnum {

	OPERACION_STR("Operacion")
	,EMPRESA_STR("Empresa")
	,CENTRO_STR("Centro")
	,TPV_STR("Tpv")
	,TPV_MAYUS_STR("TPV")
	,FECHA_STR("Fecha")
	,HORA_STR("Hora")
	,SOPORTE_STR("Soporte")
	,DOCUMENTO_STR("Documento")
	,FECHACAD_STR("FechaCad")
	,IMPORTE_STR("Importe")
	,MONEDA_STR("Moneda")
	,PLAZOS_STR("Plazos")
	,SECURITYCODE_STR("SecurityCode")
	,REF_CLIENTE_STR("Ref_Cliente")
	,TIPO_REFERENCIA_STR("Tipo_Referencia")
	,FECHA_ORIGINAL_STR("Fecha_Original")
	,REF_CLIENTE_ORIGINAL_STR("Ref_Cliente_Original")
	,HORA_ORIGINAL_STR("Hora_Original")
	,CENTRO_ORIGINAL_STR("Centro_Original")
	,TPV_ORIGINAL_STR("Tpv_Original")
	,TPV_ORIGINAL_MAYUS_STR("TPV_Original")
	,ID_OPERACION_ORIGINAL_STR("ID_Operacion_Original")
	,IMPORTE_ORIGINAL_STR("Importe_Original")
	,AUT_ORIGINAL_STR("Aut_Original")
	,OPERACION_ORIGINAL_STR("Operacion_Original")
	,INFOADICIONALENTRADA_STR("InfoAdicionalEntrada")
	,ACK_STR("ACK")
	,CF_MAC_STR("CF_MAC")
	,VALORDOCUMENTO_STR("ValorDocumento")
	,CF_REPLYURL_STR("CF_ReplyURL")
	,OPERADOR_STR("Operador")
	,CF_REPLYURLAUTH_STR("CF_ReplyURLAuth")
	,FLAGALTATOKEN_STR("FlagAltaToken")
	,FLAGUPDATETOKEN_STR("FlagUpdateToken")
	,FLAGTESTSALDO_STR("FlagTestSaldo")
	,OBSERVACIONES_STR("Observaciones")
	,ID_OPERACION_STR("ID_Operacion")
	,RESULTADO_STR("Resultado")
	,DES_RESULTADO_STR("Des_Resultado")
	,DES_TIPO_DOC_STR("Des_Tipo_Doc")
	,DES_CA_STR("Des_CA")
	,AUTORIZACION_STR("Autorizacion")
	,COMERCIO_STR("Comercio")
	,TERMINAL_STR("Terminal")
	,REFERENCIA_STR("Referencia")
	,INFOADICIONALSALIDA_STR("InfoAdicionalSalida")
	,TICKET_STR("Ticket")
	,VOUCHER_STR("Voucher")
	,CF_DOCUMENTNUMBER_STR("CF_DocumentNumber")
	,CF_EXPDATETIME_STR("CF_ExpDateTime")
	,TIPOAUTENTICACION_STR("TipoAutenticacion")
	,TIPOAUTORIZACION_STR("TipoAutorizacion")
	,CF_AUTHURL_STR("CF_AuthURL")
	,TOKEN_STR("Token")
	,CF_BANKID_STR("CF_BankID")
	,CF_PAN_STR("CF_PAN")
	,OPERACIONORIGINAL_STR("OperacionOriginal")
	,REF_TOKEN_CLIENTE_STR("Ref_Token_Cliente")
	
	/********** DATOS PLAYASOL **********/
	
	,CONEXFLOW_SERVER("https://integraciones.conexflow.es/CF_WEB_PLAYASOL/WebPlugin/xtn.asp")
	,CONEXFLOW_ACK_SERVER("https://integraciones.conexflow.es/CF_WEB_PLAYASOL/WebPlugin/ack.asp")
	,CONEXFLOW_USERID("0000028302830283")
	
	/************************************/
	
	/********** CLAVES DE CIFRADO MAC **********/
	
	,CONEXFLOW_MAC_KEYA("193D59719B1B2662")
	,CONEXFLOW_MAC_KEYB("A8DB8A2EF1223B4E")
	
	/*******************************************/
	,SALE_OP("V")
	,PREAUTHORIZATION_OP("P")
	,REFUND_OP("D")
	,CANCELATION_OP("A")
	,CONFIRM_PREAUTHORIZATION_OP("C")
	,REDEMPTION_OP("R")
	,ISSUE_OP("E")
	,CREATE_TOKEN_OP("T")
	,DELETE_TOKEN_OP("B")
	,VALIDATE_CARD_OP("N")
	,TRANSACTION_INFO_OP("S")
	
	/********** PLAYASOL CODES **********/
	
	,PLAYASOL_EMPRESA("283")
	,PLAYASOL_CENTRO("283")
	,PLAYASOL_TPV("283")
	
	/************************************/
	;
	
	private String code;

	private ConexFlowEnum(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}
}
