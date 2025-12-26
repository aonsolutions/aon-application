package com.esferalia.aon.occam.api.model.fiscal.mod425;

import java.io.Serializable;

public enum Mod4252025DetailKey implements Serializable {
	
	// BASE IMPONIBLE, TIPOS Y CUOTAS
	
 	// Régimen ordinario 
	  C003	(  3,  0.00, true, false)
	 ,C006	(  6,  3.00, true, false)
	 ,C009	(  9,  7.00, true, false)		
	 ,C012	( 12,  9.50, true, false)
	 ,C015	( 15, 15.00, true, false)
	 ,C018	( 18, 20.00, true, false)
	 ,C018B ( 18,  5.00, true, false)
	 
 	// Régimen especial de bienes usados
	 ,C021	( 21,  0.00, true, false)
	 ,C024	( 24,  3.00, true, false)
	 ,C027	( 27,  7.00, true, false)
	 ,C030	( 30,  9.50, true, false)
	 ,C033	( 33, 15.00, true, false)
	 
	// Régimen especial de objetos de arte, antigüedades y objetos de colección
	 ,C036	( 36,  0.00, true, false)
	 ,C039	( 39,  3.00, true, false)
	 ,C042	( 42,  7.00, true, false)
	 ,C045	( 45,  9.50, true, false)
	 ,C048	( 48, 15.00, true, false)
	 
 	// Régimen especial del criterio de caja
	 ,C051	( 51,  0.00, true, false)
	 ,C054	( 54,  3.00, true, false)
	 ,C057	( 57,  7.00, true, false)
	 ,C060	( 60,  9.50, true, false)
	 ,C063	( 63, 15.00, true, false)
	 ,C066	( 66, 20.00, true, false)
	 ,C066B	( 66,  5.00, true, false)
	 
	// Régimen especial de agencias de viaje
	 ,C069  ( 69,  7.00, true, false)
	 
 	// Modificación de bases y rectificación de cuotas impositivas repercutidas
	 ,C071  ( 71,  0.00, true, false)

	// Modificación de bases y cuotas por procedimientos de concurso de acreedores o créditos incobrables
	 ,C073  ( 73,  0.00, true, false)
	 
 	// Total bases I.G.I.C.
	 ,C074  ( 74,  0.00, false, true)
	 
	// Operaciones con inversion del sujeto pasivo
 	 ,C076  ( 76,  0.00, true, false)
	 
	// Cuotas devueltas en Régimen de viajeros
	 ,C078  ( 78,  0.00, true, false)
	 
	// Total cuotas devengadas
	 ,C079  ( 79,  0.00, false, true)
	 
	// DEDUCCIONES 
	 
	 ,C081 ( 81, 0.00, true , false, true)  // IGIC deducible en operaciones interiores corrientes
	 ,C083 ( 83, 0.00, true , false, true)  // IGIC deducible en operaciones interiores con bienes de inversión
	 ,C085 ( 85, 0.00, true , false, true)  // IGIC deducible por importaciones de bienes corrientes
	 ,C087 ( 87, 0.00, true , false, true)  // IGIC deducible por importaciones de bienes de inversión
	 ,C089 ( 89, 0.00, true , false) 		// Rectificación de deducciones
	 ,C090 ( 90, 0.00, false, false, true) 	// Compensación en régimen especial de la agricultura, ganaderia y pesca
	 ,C091 ( 91, 0.00, false, false) 		// Regularización de cuotas soportadas por bienes de inversión
	 ,C092 ( 92, 0.00, false, false) 		// Regularización de cuotas soportadas antes del inicio de la actividad
	 ,C093 ( 93, 0.00, false, false) 		// Regularización por aplicación del porcentaje definitivo de prorrata
	 ,C094 ( 94, 0.00, false, true ) 		// Total cuotas deducibles
	 
	 // RESULTADO DE LAS AUTOLIQUIDACIONES
	 ,C095 ( 95, 0.00, false, true ) // Resultado régimen general
	 
	 // ESTAS CASILLAS SE PONEN AQUI, CON LA UNICA FINALIDAD DE QUE SE CALCULEN AUTOMATICAMENTE CUANDO SE GENERA EL MODELO
	 
	 // Operaciones específicas
	 ,C120 (120, 0.00, false, false) // Operaciones en régimen general
//	 ,C121 (121, 0.00, false, false) // Operaciones a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el artículo 18 Ley 20/1991	 
	 ,C122 (122, 0.00, false, false) // Exportaciones definitivas y operaciones asimiladas a la exportación
//	 ,C123 (123, 0.00, false, false) // Operaciones relativas a áreas exentas
//	 ,C124 (124, 0.00, false, false) // Operaciones interiores exentas por el artículo 25 de la Ley 19/1994 realizadas por el sujeto pasivo
//	 ,C125 (125, 0.00, false, false) // Otras operaciones exentas con derecho a deducción
	 ,C126 (126, 0.00, false, false) // Operaciones exentas sin derecho a deducción
//	 ,C127 (127, 0.00, false, false) // Operaciones en régimen simplificado
	 ,C128 (128, 0.00, false, false) // Operaciones no sujetas por reglas de localización o con inversión del sujeto pasivo
//	 ,C129 (129, 0.00, false, false) // Operaciones en régimen especial de la agricultura, ganadería y pesca
//	 ,C130 (130, 0.00, false, false) // Operaciones en regímenes especiales de bienes usados, objetos de arte, antigüedades o colección
//	 ,C131 (131, 0.00, false, false) // Operaciones en régimen especial de agencias de viajes
//	 ,C132 (132, 0.00, false, false) // Entregas de bienes inmuebles y operaciones financieras no habituales
//	 ,C133 (133, 0.00, false, false) // Entregas de bienes de inversión para el transmitente
//	 ,C134 (134, 0.00, false, true ) // Total volumen de operaciones
//	 ,C135 (135, 0.00, false, false) // Importaciones de bienes de inversión exentos por el artículo 25 de la Ley 19/1994
//	 ,C136 (136, 0.00, false, false) // Cuotas de I.G.I.C. soportado no deducible
//	 ,C137 (137, 0.00, false, false) // Otras operaciones no sujetas con derecho a deducción (artículo 29.4.1ªg) Ley 20/1991)
	 
	 // Exclusivamente para aquellos sujetos pasivos acogidos al régimen especial de criterio de caja y para aquellos que sean destinatarios de operaciones afectadas por el mismo
//	 ,C139 (139, 0.00, true , false) //	Importes de las entregas de bienes y prestaciones de servicios a las que Base Cuota habiéndoles aplicado el régimen especial de criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991 138
//	 ,C141 (141, 0.00, true , false) //	Importes de las adquisiciones de bienes y servicios a las que sea de aplicación o afecte el régimen especial del criterio de caja conforme a la regla general de devengo contenida en el art. 18 de la Ley 20/1991
	
	 // Declaración informativa del volumen de operaciones en el régimen especial del pequeño empresario o profesional (exclusivamente a cumplimentar por los sujetos pasivos acogidos al REPEP)
//	 ,C142 (142, 0.00, false, false) //	Importe de operaciones habituales u ocasionales sujetas al IGIC exentas por Régimen especial del pequeño empresario o profesional
//	 ,C143 (143, 0.00, false, false) //	Importe de operaciones sujetas al IGIC exentas por Régimen especial del comerciante minorista
//	 ,C144 (144, 0.00, false, false) //	Importe de entregas de bienes y prestaciones de servicios no sujetas al IGIC imputables a la sede de la actividad económica situada en Canarias
//	 ,C145 (145, 0.00, false, false) //	Importe de entregas de bienes y prestaciones de servicios no sujetas al IGIC imputables a otras sedes o establecimientos situados fuera de Canarias
//	 ,C146 (146, 0.00, false, false) //	Importe en el supuesto de transmisión de la totalidad o parte del patrimonio empresarial o profesional
//	 ,C147 (147, 0.00, false, true ) //	Total volumen de operaciones en el REPEP
	 ;
	 
	private int box;
	private Double percent;
	private boolean taxableBaseAvailable;
	private boolean readonly;
	private boolean prorrata;
	
	private Mod4252025DetailKey(int box, Double percent, boolean taxableBaseAvailable, boolean readonly) {
		this(box, percent, taxableBaseAvailable, readonly, false);	
	}
	
	private Mod4252025DetailKey(int box, Double percent, boolean taxableBaseAvailable, boolean readonly, boolean prorrata) {
		this.percent = percent;
		this.box = box;
		this.taxableBaseAvailable = taxableBaseAvailable;
		this.readonly = readonly;
		this.prorrata = prorrata;
	}
	
	public Double getPercent() {
		return percent;
	}

	public int getBox() {
		return box;
	}
	
	public boolean hasTaxableBaseAvailable(){
		return taxableBaseAvailable;
	}
	
	public boolean isReadonly() {
		return readonly;
	}
	
	public boolean isProrrataEnabled() {
		return prorrata;
	}
	
}
