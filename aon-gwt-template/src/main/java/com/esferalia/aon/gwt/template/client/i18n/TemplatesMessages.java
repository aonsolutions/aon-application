package com.esferalia.aon.gwt.template.client.i18n;

import com.google.gwt.i18n.client.Messages;

public interface TemplatesMessages extends Messages {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF ! --> \u0021 ¡ --> \u00A1
	
	@DefaultMessage("Producto")
	String product();
	
	@DefaultMessage("Cantidad")
	String quantity();
	
	@DefaultMessage("Detalle 1")
	String detail1();
  	
 	@DefaultMessage("Detalle 2")
	String detail2();
  	
 	@DefaultMessage("Detalle 3")
	String detail3(); 	

 	@DefaultMessage("Nombre")
	String name();
 	
 	@DefaultMessage("N\u00FAmero Serie")
	String serialNumber();

 	@DefaultMessage("Formato")
	String format();

 	@DefaultMessage("Unidades")
	String units();
 	
 	@DefaultMessage("Formato Unidades")
	String unitsFormat();
  	
 	@DefaultMessage("Medida")
	String measurement();
	
 	@DefaultMessage("Formato Medida")
	String measurementFormat();

 	@DefaultMessage("C\u00f3digo")
	String code();
 	
 	@DefaultMessage("Precio Coste")
	String priceCost();
 	
 	@DefaultMessage("Precio Venta Base")
	String priceSaleBase();
 	
 	@DefaultMessage("Categor\u00eda")
	String category();
 	
 	@DefaultMessage("Marca")
	String brand();
 	
 	@DefaultMessage("Etiqueta")
	String tag();
 	
 	@DefaultMessage("Tipo")
	String type();
 	
 	@DefaultMessage("IVA")
	String vat();
 	
 	@DefaultMessage("IRPF")
	String irpf();
 	
 	@DefaultMessage("Inventariable")
	String inventoriable();
 	
 	@DefaultMessage("Producto Compuesto")
	String composedProduct();
 	
 	@DefaultMessage("Precio Composici\u00f3n")
	String compositionPrice();
 	
 	@DefaultMessage("Estado")
	String status();
 	
 	@DefaultMessage("C\u00f3digo de Barras")
	String barcode();

 	@DefaultMessage("Descripci\u00f3n")
	String description();

 	@DefaultMessage("Serializable")
	String serializable();

 	@DefaultMessage("Loteable")
	String lotable();

 	@DefaultMessage("Envasado")
	String packaged();
 	
 	@DefaultMessage("Cliente")
	String customer();
 	
 	@DefaultMessage("Precio")
	String price();
 	
 	@DefaultMessage("Descuento")
	String discount();
 	
 	@DefaultMessage("Fecha Inicio")
	String startDate();
 	
 	@DefaultMessage("Fecha Fin")
	String endDate();
 	
 	@DefaultMessage("Fecha Facturaci\u00f3n")
	String billingDate();
 	
 	@DefaultMessage("Periodo")
	String period();
 	
 	@DefaultMessage("Comercial")
	String comercial();
 	
 	@DefaultMessage("Centro de Trabajo")
	String workplace();
 	
 	@DefaultMessage("Grupo Facturaci\u00f3n")
	String billingGroup();
 	
 	@DefaultMessage("Confidencial")
	String confidential();
 	
 	@DefaultMessage("L\u00EDnea")
	String line();

 	@DefaultMessage("Expendiente")
	String record();
 	
 	@DefaultMessage("Inicial")
	String initial();
 	
 	@DefaultMessage("Compras")
	String purchases();
 	
 	@DefaultMessage("Ventas")
	String sales();
 	
 	@DefaultMessage("Final")
	String finale();
 	
 	@DefaultMessage("Traspaso")
	String transfer();

 	@DefaultMessage("Valor Consumo")
	String consumptionValue();
 	
 	@DefaultMessage("Consumo")
	String consumption();
 	
 	@DefaultMessage("Departamento")
	String department();

 	@DefaultMessage("Inventario")
	String inventory();

 	@DefaultMessage("Coste")
	String cost();
 	
 	@DefaultMessage("Total")
	String total();

 	@DefaultMessage("Recuento")
	String count();

 	@DefaultMessage("Stock")
	String stock();

 	@DefaultMessage("Cuota")
	String fee();
 	
 	@DefaultMessage("Inventorio Cerrado")
	String closedInventory();
 	
 	@DefaultMessage("Inventorio Valorado")
	String valuedInventory();
 	
 	@DefaultMessage("Columna {0}")
	String column(Integer num);
 	
 	@DefaultMessage("Texto Libre")
	String freeText();

 	@DefaultMessage("Columnas Obligatorias:")
	String requiredColumns();

 	@DefaultMessage("Almac\u00e9n Destino")
	String targetWarehouse();
 	
 	@DefaultMessage("Almac\u00e9n Origen")
	String sourceWarehouse();
 	
 	@DefaultMessage("Serie")
	String serie();
 	
 	@DefaultMessage("Comentarios")
	String comments();
 	
 	@DefaultMessage("Archivo")
	String file();
 	
 	@DefaultMessage("Plantilla")
	String template();
 	
 	@DefaultMessage("Almac\u00e9n")
	String warehouse();
 	
 	@DefaultMessage("Ignorar clientes inactivos")
	String ignoreInactiveCustomer();
 	
 	@DefaultMessage("Compra-Venta")
	String purchaseSale();
 	
 	@DefaultMessage("Compra")
	String purchase();
 	
 	@DefaultMessage("Venta")
	String sale();
 	
 	@DefaultMessage("A\u00f1adir informaci\u00f3n del envasado")
 	String addPackagedInfo();
 
 	@DefaultMessage("Incluir \u00FAnicamente productos con cantidad distinta de cero")
 	String includeZeroQuantity();
 	
 	@DefaultMessage("Se ha importado correctamente")
 	String importOk();
 	
 	@DefaultMessage("Est\u00e1s seguro de eliminar la plantilla {0}")
 	String deleteTemplateMessage(String name);
 	
 	@DefaultMessage("Est\u00e1s seguro de eliminar la etiqueta {0}")
 	String deleteTagMessage(String name);
 	
 	@DefaultMessage("Cat\u00E1logo")
 	String catalogue();
 	
 	@DefaultMessage("Vendedor")
 	String seller();
 	
 	@DefaultMessage("Ecommerce")
 	String ecommerce();
 	
	@DefaultMessage("Asignar Cuotas")
 	String assignFees();
	
	@DefaultMessage("*Faltan columnas por a\u00f1adir")
	String error1();

	@DefaultMessage("*Es necesario seleccionar una plantilla.")
	String error2();
	
	@DefaultMessage("*Faltan datos por a\u00f1adir")
 	String error3();

	@DefaultMessage("Acciones")
 	String actions();
	
	@DefaultMessage("No hay ning\u00fan archivo.")
 	String notFiles();
	
}
