package com.esferalia.aon.occam.api.model.fiscal.mod390;

import java.io.Serializable;

public enum Mod3902023DetailKey implements Serializable {
	// IVA devengado
 	// Régimen ordinario
	  C0701	(701,	 0.00,true,false)
	 ,C0002	(  2,	 4.00,true,false)		
	 ,C0703	(703,	 5.00,true,false)
	 ,C0004	(  4,	10.00,true,false)
	 ,C0006	(  6,	21.00,true,false)
 	// Operaciones intragrupo
	 ,C0705	(705,	 0.00,true,false)
	 ,C0501	(501,	 4.00,true,false)
	 ,C0707	(707,	 5.00,true,false)
	 ,C0503	(503,	10.00,true,false)
	 ,C0505	(505,	21.00,true,false)
 	// Régimen especial del criterio de caja
	 ,C0709	(709,	 0.00,true,false)
	 ,C0644	(644,	 4.00,true,false)
	 ,C0711	(711,	 5.00,true,false)
	 ,C0646	(646,	10.00,true,false)
	 ,C0648	(648,	21.00,true,false)
 	// Régimen especial de bienes usados, objetos de arte, antigüedades y objetos de colección
	 ,C0713	(713,	 0.00,true,false)
	 ,C0008	(  8,	 4.00,true,false)
	 ,C0715	(715,	 5.00,true,false)
	 ,C0010	( 10,	10.00,true,false)
	 ,C0012	( 12,	21.00,true,false)
 	// Régimen especial de agencias de viaje
	 ,C0014	( 14,	21.00,true,false)
 	// Adquisiciones intracomunitarias de bienes
	 ,C0717	(717,	 0.00,true,false)
	 ,C0022	( 22,	 4.00,true,false)
	 ,C0719	(719,	 5.00,true,false)
	 ,C0024	( 24,	10.00,true,false)
	 ,C0026	( 26,	21.00,true,false)
 	// Adquisiciones intracomunitarias de servicios
	 ,C0721	(721,	 0.00,true,false)
	 ,C0546	(546,	 4.00,true,false)
	 ,C0723	(723,	 5.00,true,false)
	 ,C0548	(548,	10.00,true,false)
	 ,C0552	(552,	21.00,true,false)
 	// IVA devengado en otros supuestos de inversión del sujeto pasivo
	 ,C0028	( 28,	 0.00,true,false)
 	// Modificación de bases y cuotas
	 ,C0030	( 30,	 0.00,true,false)
 	// Modificación de bases y cuotas de operaciones intragrupo
	 ,C0650	(650,	 0.00,true,false)
 	// Modificación de bases y cuotas por auto de declaración de concurso de acreedores
	 ,C0032	( 32,	 0.00,true,false)
 	// Total bases y cuotas IVA
	 ,C0034	( 34,	 0.00,true,true )
 	// Recargo de equivalencia
	 ,C0664 (664,	 0.00,true,false)
	 ,C0036 ( 36,	 0.50,true,false)
	 ,C0666 (666,	 0.62,true,false)
	 ,C0600 (600,	 1.40,true,false)
	 ,C0602 (602,	 5.20,true,false)
	 ,C0042	( 42,	 1.75,true,false)
	 ,C0044	( 44,	 0.00,true,false)
 	// Modificación recargo equivalencia
	 ,C0046	( 46,	 0.00,true,false)
 	//  Total cuotas IVA y recargo de equivalencia
	 ,C0047	( 47,	 0.00,false,true)

	 
	 // IVA deducible
	 //Operaciones interiores corrientes
	 // IVA deducible en operaciones interiores de bienes y servicios corrientes
	 ,C0191	(191,	 4.00,true ,false,true)
	 ,C0725	(725,	 5.00,true ,false,true)
	 ,C0604	(604,	10.00,true ,false,true)
	 ,C0606	(606,	21.00,true ,false,true)
	 // Total bases imponibles y cuotas deducibles en operaciones interiores de bienes y servicios corrientes
	 ,C0049	( 49,	 0.00,true ,true )
	 
	 // IVA deducible en operaciones intragrupo de bienes y servicios corrientes
	 ,C0507	(507,	 4.00,true ,false,true)
	 ,C0727	(727,	 5.00,true ,false,true)
	 ,C0608	(608,	10.00,true ,false,true)
	 ,C0610	(610,	21.00,true ,false,true)
	 // Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes y servicios corrientes
	 ,C0513	(513,	 0.00,true ,true )
	 
	 // Operaciones interiores de bienes de inversión
	 // IVA deducible en operaciones interiores de bienes de inversión	 
	 ,C0197	(197,	 4.00,true ,false,true)
	 ,C0729	(729,	 5.00,true ,false,true)
	 ,C0612	(612,	10.00,true ,false,true)
	 ,C0614	(614,	21.00,true ,false,true)
	 // Total bases imponibles y cuotas deducibles en operaciones interiores de bienes de inversión
	 ,C0051	( 51,	 0.00,true ,true )
	 
	 // IVA deducible en operaciones intragrupo de bienes de inversión
	 ,C0515	(515,	 4.00,true ,false,true)
	 ,C0731	(731,	 5.00,true ,false,true)
	 ,C0616	(616,	10.00,true ,false,true)
	 ,C0618	(618,	21.00,true ,false,true)
	 // Total bases imponibles y cuotas deducibles en operaciones intragrupo de bienes de inversión
	 ,C0521	(521,	 0.00,true ,true )
	 
	 // Importaciones y adquisiciones intracomunitarias de bienes y servicio
	 // IVA deducible en importaciones de bienes corrientes
	 ,C0203	(203,	 4.00,true ,false,true)
	 ,C0733	(733,	 5.00,true ,false,true)
	 ,C0620	(620,	10.00,true ,false,true)
	 ,C0622	(622,   21.00,true ,false,true)
	 // Total bases imponibles y cuotas deducibles en importaciones de bienes corrientes
	 ,C0053	( 53,    0.00,true ,true )
	 
	 // IVA deducible en importaciones de bienes de inversión
	 ,C0209	(209,    4.00,true ,false,true)
	 ,C0735	(735,	 5.00,true ,false,true)
	 ,C0624	(624,   10.00,true ,false,true)
	 ,C0626	(626,   21.00,true ,false,true)
	 // Total bases imponibles y cuotas deducibles en importaciones de bienes de inversión
	 ,C0055	( 55,    0.00,true ,true )
	 
	 // IVA deducible en adquisiciones intracomunitarias de bienes corrientes
	 ,C0215	(215,    4.00,true ,false,true)
	 ,C0737	(737,	 5.00,true ,false,true)
	 ,C0628	(628,   10.00,true ,false,true)
	 ,C0630	(630,   21.00,true ,false,true)
	 // Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes corrientes
	 ,C0057	( 57,    0.00,true ,true )
	 
	 // IVA deducible en adquisiciones intracomunitarias de bienes de inversión
	 ,C0221	(221,    4.00,true ,false,true)
	 ,C0739	(739,	 5.00,true ,false,true)
	 ,C0632	(632,   10.00,true ,false,true)
	 ,C0634	(634,   21.00,true ,false,true)
	 // Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de bienes de inversión
	 ,C0059	( 59,    0.00,true ,true )
	 
	 // IVA deducible en adquisiciones intracomunitarias de servicios
	 ,C0588	(588,    4.00,true ,false,true)
	 ,C0741	(741,	 5.00,true ,false,true)
	 ,C0636	(636,   10.00,true ,false,true)
	 ,C0638	(638,   21.00,true ,false,true)
	 // Total bases imponibles y cuotas deducibles en adquisiciones intracomunitarias de servicios
	 ,C0598	(598,    0.00,true ,true )
	 
	 // Compensación en régimen especial de la agricultura, ganaderia y pesca
	 ,C0061	( 61,    0.00,false ,false,true)
	 
	 // Cuotas deducibles en virtud de resolución administrativa o sentencia firmes con tipos no vigentes
	 ,C0661	(661,    0.00,true ,false)
	 // Rectificación de deducciones
	 ,C0062	( 62,    0.00,true ,false)
	 // Rectificación de deducciones por operaciones intragrupo
	 ,C0652	(652,    0.00,true ,false)
	 // Regularización de bienes de inversión
	 ,C0063	( 63,    0.00,false,false)
	 // Regularización por aplicación porcentaje definitivo de prorrata
	 ,C0522	(522,    0.00,false,false)
	 // Suma de deducciones
	 ,C0064	( 64,    0.00,false,true )
	 
	 // Resultado régimen general
	 ,C0065	( 65,    0.00,false,true )
	 
	 
	 // Operaciones en régimen general
	 ,C0099	 ( 99,   0.00,true ,false)
	 // Operaciones a las que habiéndoles sido aplicado el régimen especial del criterio de caja hubieran resultado devengadas conforme a la regla general de devengo contenida en el art. 75 LIVA
	 ,C0653	 (653,   0.00,true ,false)
	 // Entregas intracomunitarias de bienes y servicios
	 ,C0103	 (103,   0.00,true ,false)
	 // Exportaciones y otras operaciones exentas con derecho a deducción
	 ,C0104	 (104,   0.00,true ,false)
	 // Operaciones exentas sin derecho a deducción
	 ,C0105	 (105,   0.00,true ,false)
	 // Operaciones no sujetas por reglas de localización (excepto las incluidas en la casilla 126)
	 ,C0110	 (110,   0.00,true ,false)
	 // Operaciones sujetas con inversión del sujeto pasivo
	 ,C0125	 (125,   0.00,true ,false)
	 // Operaciones no sujetas por reglas de localización acogidas a los regímenes especiales de ventanilla única
	 ,C0126	 (126,   0.00,true ,false)
	 // Operaciones sujetas y acogidas a los regímenes especiales de ventanilla única
	 ,C0127	 (127,   0.00,true ,false)
	 // Operaciones intragrupo valoradas conforme a lo dispuesto en los arts. 78 y 79 LIVA
	 ,C0128	 (128,   0.00,true ,false)
	 // Operaciones en régimen simplificado
	 ,C0100	 (100,   0.00,true ,false)
	 // Operaciones en régimen especial de la agricultura, ganadería y pesca
	 ,C0101	 (101,   0.00,true ,false)
	 // Operaciones realizadas por sujetos pasivos acogidos al régimen especial del recargo de equivalencia
	 ,C0102	 (102,   0.00,true ,false)
	 // Operaciones en Régimen especial de bienes usados, objetos de arte, antigüedades y objetos de colección
	 ,C0227	 (227,   0.00,true ,false)
	 // Operaciones en régimen especial de Agencias de Viajes
	 ,C0228	 (228,   0.00,true ,false)
	 // Entregas de bienes inmuebles, operaciones financieras y relativas al oro de inversión no habituales
	 ,C0106	 (106,   0.00,true ,false)
	 // Entregas de bienes de inversión
	 ,C0107	 (107,   0.00,true ,false)
	 // Total volumen de operaciones 
	 ,C0108	 (108,   0.00,true ,false)
	 
	 // Regimen especial de criterio de caja.
	 ,C0654	 (654,   0.00,true ,false)
	 ,C0656	 (656,   0.00,true ,false)
	 ;
	 
	private int box;
	private Double percent;
	private boolean taxableBaseAvailable;
	private boolean readonly;
	private boolean prorrata;
	
	private Mod3902023DetailKey(int box, Double percent, boolean taxableBaseAvailable, boolean readonly) {
		this(box, percent, taxableBaseAvailable, readonly, false);	
	}
	
	private Mod3902023DetailKey(int box, Double percent, boolean taxableBaseAvailable, boolean readonly, boolean prorrata) {
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
	public boolean isSurcharge() {
		return (this == C0036 || this == C0600
			|| this == C0602 || this == C0042
			|| this == C0044 || this == C0102);
	}
	public boolean isProrrataEnabled() {
		return prorrata;
	}
}
