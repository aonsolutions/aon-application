package com.code.aon.customer;

public interface IEdiSupport {

	
	public String ACTIVE = "EDI_ACTIVE";
	public String SERES_AUTO_COMMIT_DELIVERY = "SERES_AUTO_COMMIT_DELIVERY";
	
	public String CABECERA = "EDI_CABECERA";
	public String PEDIDOS = "EDI_PEDIDOS";
	public String PTO_ENTREGA = "EDI_PTO_ENTREGA";
	public String FACTURA = "EDI_FACTURA";
	public String FINANCIERA = "EDI_FINANCIERA";
	public String ALBARANES = "EDI_ALBARANES";
	public String MEDIDA = "EDI_MEDIDA";
	public String MEDIDA_FACTURA = "EDI_MEDIDA_FACTURA";
	
	public String[] EDI_VALUES = {
			CABECERA, 
			PEDIDOS,
			PTO_ENTREGA,
			FACTURA,
			FINANCIERA,
			ALBARANES,
			MEDIDA,
			MEDIDA_FACTURA
	};
	
	public String EDI_CODES_PATTERN = CABECERA + "=([^;]*);" 
			+ PEDIDOS + "=([^;]*);"
			+ PTO_ENTREGA + "=([^;]*);"
			+ FACTURA + "=([^;]*);"
			+ FINANCIERA + "=([^;]*);"
			+ ALBARANES + "=([^;]*);";

	public String EDI_PACKING_PATTERN = MEDIDA + "=([^;]*);"
			+ "(?:" + MEDIDA_FACTURA + "=([^;]*);)?"
			;

}
