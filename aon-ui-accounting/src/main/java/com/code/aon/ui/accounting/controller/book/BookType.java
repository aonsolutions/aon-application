package com.code.aon.ui.accounting.controller.book;

public enum BookType {
    PORTADA 	(AonReportType.COVER	,"Portada"),
    DIARIO 		(AonReportType.JOURNAL 	,"Diario"),
    INV_CUEN	(null					,null),
    BAL_SUMS1	(AonReportType.TRIAL 	,"Balance de Sumas y Saldos 01-03"),
    BAL_SUMS2	(AonReportType.TRIAL 	,"Balance de Sumas y Saldos 01-06"),
    BAL_SUMS3	(AonReportType.TRIAL 	,"Balance de Sumas y Saldos 01-09"),
    BAL_SUMS4	(AonReportType.TRIAL 	,"Balance de Sumas y Saldos 01-12"),
    EXPENSES	(AonReportType.OPERATION,"Libro de compras y gastos"),
    INCOMES		(AonReportType.OPERATION,"Libro de ventas e ingresos"),
    INVENTAR	(AonReportType.OTHER	,"Inventario"),
    BALANCES	(AonReportType.BALANCE	,"Balances predefinidos"),
    MEMORIA		(AonReportType.OTHER	,"Memoria"),
    MAYOR		(AonReportType.LEDGER 	,"Mayor de Cuentas"),
    PER_GAN		(AonReportType.BALANCE 	,"Balance de Pérdidas y Ganancias"),
    IVAR		(AonReportType.VAT 		,"IVA Repercutido"),
    IVAS		(AonReportType.VAT 		,"IVA Soportado"),
    IVAI		(AonReportType.VAT 		,"IVA Inversión"),
    FAC_EMIT	(null					,null),
    FAC_RECI	(null					,null),
    DET_DIA		(null					,null),
    ACCIONES	(null					,null),
    SOCIOS		(null					,null),
    OTROS		(AonReportType.OTHER	,"Otros");
    
    private String description;
    private AonReportType aonReportType;
    
    private BookType(AonReportType aonReportType, String description) {
    	this.aonReportType = aonReportType;
    	this.description = description;
    }

	public String getDescription() {
		return description;
	}

	public AonReportType getAonReportType() {
		return aonReportType;
	}
}
