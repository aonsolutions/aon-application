package com.esferalia.aon.occam.api.model.ddff;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum AeatFiscalDataType implements Serializable {
	 DDFF2022(0 ,"Registro de cabecera") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitHEADER(ctx);}
	}
	,DATE	(1 ,"Fecha de proceso") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDATE(ctx);}
		
		@Override
		boolean accept(String code, int recordType) {
			return recordType == 1;
		}
	}
	,DOM	(2 ,"Datos del domicilio") {
		@Override  public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDOM(ctx);}
	}
	,FA		(2 ,"Familia numerosa") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitFA(ctx);}
	}
	,DI		(2 ,"Discapacidad") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDI(ctx);}
	}
	,RTA	(2 ,"Rendimientos del trabajo modelo 190") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitRTA(ctx);}
	}
	,RTD	(2 ,"Rentas exentas modelo 190") {public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitRTD(ctx);}
	}
	,RTB	(2 ,"Premios y ganancias patrimoniales modelo 190") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitRTB(ctx);}
	}
	,RTC	(2 ,"Actividades Económicas modelo 190") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitRTC(ctx);}
	}
	,AA		(2 ,"Rendimientos del trabajo, Actividades Económicas, Premios, Ganancias Patrimoniales e Imputaciones de Renta, con ejercicio de devengo anterior a 2022") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitAA(ctx);}
	}
	,PP		(2 ,"Planes de Pensiones") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPP(ctx);}
	}
	,CB		(2 ,"Rendimientos de cuentas bancarias") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitCB(ctx);}
	}
	,LT		(2 ,"Rendimientos de Letras del Tesoro") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitLT(ctx);}
	}
	,REA	(2 ,"Rendimientos explicitos de capital mobiliario, base imponible del ahorro") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitREA(ctx);}
	}
	,REG	(2 ,"Rendimientos explicitos de capital mobiliario, base imponible general") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitREG(ctx);}
	}
	,RI		(2 ,"Rendimientos implicitos de capital mobiliario") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitRI(ctx);}
	}
	,SG		(2 ,"Rendimientos de operaciones de seguros") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitSG(ctx);}
	}
	,CD		(2 ,"Capitales diferidos de seguros de vida a cuyo rendimiento le sea aplicable la DT4") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitCD(ctx);}
	}
	,TV		(2 ,"Ventas de activos financieros y otros valores mobiliarios") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitTV(ctx);}
	}
	,AR		(2 ,"Arrendamientos de locales") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitAR(ctx);}
	}
	,AT		(2 ,"Arrendamientos de pisos turísticos") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitAT(ctx);}
	}
	,CT		(2 ,"Cotizaciones de autónomos") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitCT(ctx);}
	}
	,AG		(2 ,"Subvenciones o indemnizaciones a agricultores, ganaderos o forestales") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitAG(ctx);}
	}
	,IP		(2 ,"Otras subvenciones y ayudas por las administraciones públicas") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitIP(ctx);}
	}
	,SB		(2 ,"Subvenciones recogidas en la base de datos nacional") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitSB(ctx);}
	}
	,EAA	(2 ,"Atribución de rendimientos de capital mobiliario") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitEAA(ctx);}
	}
	,EAB	(2 ,"Atribución de rendimientos de capital inmobiliario") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitEAB(ctx);}
	}
	,EAC	(2 ,"Atribución de rendimientos de actividades económicas") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitEAC(ctx);}
	}
	,EAD	(2 ,"Atribución de ganancias y pérdidas patrimoniales") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitEAD(ctx);}
	}
	,EAE	(2 ,"Atribución de retenciones e ingresos a cuenta") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitEAE(ctx);}
	}
	,EAF	(2 ,"Atribución de deducciones") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitEAF(ctx);}
	}
	,AMI	(2 ,"Amortización de Inmuebles declarada por el Contribuyente") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitAMI(ctx);}
	}
	,FIE	(2 ,"Ventas de fondos de inversión con retención") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitFIE(ctx);}
	}
	,FIC	(2 ,"Ventas de fondos de inversión cotizados, sin retención") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitFIC(ctx);}
	}
	,FID	(2 ,"Valor de transmisión sobre el que se aplicó la disposición transitoria 9ª "
			  + "de la ley de IRPF en 2015 y/o 2016 y/o 2017 y/o 2018 y/o 2019 y/o 2020") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitFID(ctx);}
	}
	,DS		(2 ,"Ventas derechos de suscripcion con retención") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDS(ctx);}
	}
	,TVD	(2 ,"Ventas derechos de suscripcion sin retención") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitTVD(ctx);}
	}
	,DNP	(2 ,"Aportaciones a patrimonios protegidos de personas con discapacidad") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDNP(ctx);}
	}
	,DN		(2 ,"Donaciones") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDN(ctx);}
	}
	,CC		(2 ,"Deducciones autonómicas") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitCC(ctx);}
	}
	,PHD	(2 ,"Préstamos hipotecarios, deducción vivienda habitual") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPHD(ctx);}
	}
	,PHI	(2 ,"Préstamos hipotecarios, intereses abonados indebidamente") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPHI(ctx);}
	}
	,PHM	(2 ,"Préstamos hipotecarios, minoración del principal por la entidad financiera") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPHM(ctx);}
	}
	,PF		(2 ,"Pagos fraccionados") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPF(ctx);}
	}
	,UR		(2 ,"Información catastral de inmuebles") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitUR(ctx);}
	}
	,TPU	(2 ,"Transmisiones patrimoniales urbanas") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitTPU(ctx);}
	}
	,TPR	(2 ,"Transmisiones patrimoniales rústicas") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitTPR(ctx);}
	}
	,DM		(2 ,"Deducción por maternidad") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDM(ctx);}
	}
	,DMG	(2 ,"Deducción por maternidad. Gastos de guardería") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDMG(ctx);}
	}
	,DD		(2 ,"Deducción por descendientes con discapacidad a cargo") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDD(ctx);}
	}
	,AD		(2 ,"Deducción por ascendientes con discapacidad a cargo") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitAD(ctx);}
	}
	,FN		(2 ,"Deducción por familia numerosa") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitFN(ctx);}
	}
	,FM		(2 ,"Deducción por ascendientes separado legalmente o sin vínculo matrimonial") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitFM(ctx);}
	}
	,COD	(2 ,"Deducción por cónyuge no separado legalmente con discapacidad") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitCOD(ctx);}
	}
	,PDA	(2 ,"Importes pendientes de deducir en los ejercicios anteriores por intereses de "
			  + "los capitales invertidos en la adquisición o mejora del inmueble y gastos de "
			  + "reparación y conservación del mismo.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDA(ctx);}
	}
	,PDB	(2 ,"Saldos netos negativos de las ganancias y pérdidas patrimoniales pendientes "
			  + "de compensar en los ejercicios anteriores correspondientes a la base imponible general.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDB(ctx);}
	}
	,PDC	(2 ,"Saldos netos negativos de las ganancias y pérdidas patrimoniales pendientes de "
			  + "compensar en los ejercicios anteriores correspondientes a la base imponible del ahorro.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDC(ctx);}
	}
	,PDD	(2 ,"Rendimientos de capital mobiliario negativos pendientes de compensar en los "
			  + "ejercicios anteriores correspondientes a la base imponible del ahorro.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDD(ctx);}
	}
	,PDE	(2 ,"Exceso no reducido de las aportaciones y contribuciones a sistemas de "
			  + "previsión social (Régimen General) pendientes de reducir de ejercicios anteriores.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDE(ctx);}
	}
	,PDF	(2 ,"Exceso no reducido derivado de contribuciones empresariales a sistemas de "
			  + "previsión social (Régimen General) pendientes de reducir de ejercicios anteriores.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDF(ctx);}
	}
	,PDG	(2 ,"Exceso no reducido derivado de las aportaciones y contribuciones a sistemas "
			  + "de previsión social realizadas por la propia persona con discapcidad pendiente "
			  + "reducir de ejercicios anteriores.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDG(ctx);}
	}
	,PDH	(2 ,"Exceso no reducido derivado de las aportaciones y contribuciones a sistemas de "
			  + "previsión social realizadas por parientes o tutores a favor de personas con "
			  + "discapcidad pendiente reducir de ejercicios anteriores.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDH(ctx);}
	}
	,PDI	(2 ,"Exceso no reducido derivado de las aportaciones a patrimonios protegidos de "
			  + "personas con discapcidad pendiente reducir de ejercicios anteriores.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDI(ctx);}
	}
	,PDJ	(2 ,"Exceso no reducido derivado de las aportaciones a la mutualidad de de previsión "
			  + "social de deportistas profesionales pendientes reducir de ejercicios anteriores.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDJ(ctx);}
	}
	,PDK	(2 ,"Bases liquidables generales negativas pendientes de compensar de ejercicios anteriores.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDK(ctx);}
	}
	,PDL	(2 ,"Ganancias Patrimoniales Imputadas por Cuartas Partes") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDL(ctx);}
	}
	,PDX	(2 ,"Deducción por obras de mejora de la Eficiencia Energética de Viviendas. Pendientes de deducir") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPDX(ctx);}
	}
	,CLP	(2 ,"Deducciones de Castilla y León no aplicadas en en los periodos 2019, 2020 y 2021") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitCLP(ctx);}
	}
	,RTN	(2 ,"Rendimientos del trabajo procedentes del modelo 296.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitRTN(ctx);}
	}
	,CNR	(2 ,"Rendimientos de cuentas bancarias procedentes del modelo 291.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitCNR(ctx);}
	}
	,GPP	(2 ,"Ganancias y pérdidas patrimoniales con precio aplazado pendientes de imputación") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitGPP(ctx);}
	}
	,CTP	(2 ,"Cotizaciones y marcas de parado o pensionista.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitCTP(ctx);}
	}
	,PUE	(2 ,"Pensiones informadas por Estados de la Unión Europea") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitPUE(ctx);}
	}
	,RUE	(2 ,"Rendimientos del trabajo y retrib. de Consejeros informadas por Estados de la Unión Europea") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitRUE(ctx);}
	}
	,RPT	(2 ,"Rendimientos del trabajo, pensiones, retrib. de Consejeros y retribuciones "
			  + "profesionales informadas por terceros Estados") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitRPT(ctx);}
	}
	,AY		(2 ,"Ayuda a personas físicas de bajo nivel de ingresos y patrimonio") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitAY(ctx);}
	}
	,AGS	(2 ,"Ayudas al Sector de la Industria del Gas Intensiva") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitAGS(ctx);}
	}
	,ATR	(2 ,"Ayudas al Sector del Transporte por Carretera") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitATR(ctx);}
	}
	,IAP	(2 ,"Intereses abonados por Administraciones Públicas") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitIAP(ctx);}
	}
	,DD1	(2 ,"Ampliación de la deducción por Maternidad e incremento de la misma por "
			  + "gastos de guardería en 2020.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDD1(ctx);}
	}
	,DD2	(2 ,"Ampliación de la deducción por Maternidad e incremento de la misma por "
			  + "gastos de guardería en 2021.") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDD2(ctx);}
	}
	,DG		(3 ,"Datos generales") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDG(ctx);}
	}
	,TI		(3 ,"Datos del titular") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitTI(ctx);}
	}
	,DE		(3 ,"Descendientes") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitDE(ctx);}
	}
	,AS		(3 ,"Ascendientes") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitAS(ctx);}
	}
	,END	(2 ,"Registro de cierre") {
		@Override public void visit(IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {visitor.visitEND(ctx);}
		@Override
		boolean accept(String code, int recordType) {
			return recordType == 9;
		}
	}
	;
	private int recordType;
	private String desc;
	private String pattern;
	
	private AeatFiscalDataType(int recordType, String desc) {
		this.recordType = recordType;
		this.desc = desc;
		String code = this.toString();
		int remaining = (8 - AonStringUtils.length(code) - 1);
		this.pattern = code + (remaining>0?"([0-9]{"+ remaining +"}[0-9\\s])":"");
	}
	
	public int getRecordType() {
		return recordType;
	}

	public String getDesc() {
		return desc;
	}
	
	boolean accept(String code, int recordType) {
		return (this.recordType == recordType
			&& AonStringUtils.isNotBlank(code)
			&& code.matches( this.pattern ));
	}

	public static void parse( IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx) {
		String rt = AonStringUtils.substring(ctx.getLine(), 0 ,1);
		if (AonStringUtils.isNumeric( rt ) ) {
			final String code = AonStringUtils.substring(ctx.getLine(), 1 ,9);
			final  int recordType = AonNumberUtils.toint(rt);
			AonCollectionUtils
				.stream(AeatFiscalDataType.values())
				.filter( t -> t.accept(code,recordType))
				.forEach(t -> t.visit(visitor,ctx));
		}
	}

	public abstract void visit( IAeatFiscalDataTypeVisitor visitor,AeatFiscalDataTypeContext ctx);
	
	public static class AeatFiscalDataTypeContext {
		private final AeatFiscalData fiscalData;
		private  String line;
		
		public AeatFiscalDataTypeContext(AeatFiscalData fiscalData) {
			this.fiscalData = fiscalData;
		}
		
		public AeatFiscalData getFiscalData() {
			return fiscalData;
		}
		
		public String getLine() {
			return line;
		}
		public AeatFiscalDataTypeContext setLine(String line) {
			this.line = line;
			return this;
		}
		
	}
	
	public interface IAeatFiscalDataTypeVisitor {
		void visitHEADER(AeatFiscalDataTypeContext ctx);
		void visitDATE(AeatFiscalDataTypeContext ctx);
		void visitDOM(AeatFiscalDataTypeContext ctx);
		void visitFA(AeatFiscalDataTypeContext ctx);
		void visitDI(AeatFiscalDataTypeContext ctx);
		void visitRTA(AeatFiscalDataTypeContext ctx);
		void visitRTD(AeatFiscalDataTypeContext ctx);
		void visitRTB(AeatFiscalDataTypeContext ctx);
		void visitRTC(AeatFiscalDataTypeContext ctx);
		void visitAA(AeatFiscalDataTypeContext ctx);
		void visitPP(AeatFiscalDataTypeContext ctx);
		void visitCB(AeatFiscalDataTypeContext ctx);
		void visitLT(AeatFiscalDataTypeContext ctx);
		void visitREA(AeatFiscalDataTypeContext ctx);
		void visitREG(AeatFiscalDataTypeContext ctx);
		void visitRI(AeatFiscalDataTypeContext ctx);
		void visitSG(AeatFiscalDataTypeContext ctx);
		void visitCD(AeatFiscalDataTypeContext ctx);
		void visitTV(AeatFiscalDataTypeContext ctx);
		void visitAR(AeatFiscalDataTypeContext ctx);
		void visitAT(AeatFiscalDataTypeContext ctx);
		void visitCT(AeatFiscalDataTypeContext ctx);
		void visitAG(AeatFiscalDataTypeContext ctx);
		void visitIP(AeatFiscalDataTypeContext ctx);
		void visitSB(AeatFiscalDataTypeContext ctx);
		void visitEAA(AeatFiscalDataTypeContext ctx);
		void visitEAB(AeatFiscalDataTypeContext ctx);
		void visitEAC(AeatFiscalDataTypeContext ctx);
		void visitEAD(AeatFiscalDataTypeContext ctx);
		void visitEAE(AeatFiscalDataTypeContext ctx);
		void visitEAF(AeatFiscalDataTypeContext ctx);
		void visitAMI(AeatFiscalDataTypeContext ctx);
		void visitFIE(AeatFiscalDataTypeContext ctx);
		void visitFIC(AeatFiscalDataTypeContext ctx);
		void visitFID(AeatFiscalDataTypeContext ctx);
		void visitDS(AeatFiscalDataTypeContext ctx);
		void visitTVD(AeatFiscalDataTypeContext ctx);
		void visitDNP(AeatFiscalDataTypeContext ctx);
		void visitDN(AeatFiscalDataTypeContext ctx);
		void visitCC(AeatFiscalDataTypeContext ctx);
		void visitPHD(AeatFiscalDataTypeContext ctx);
		void visitPHI(AeatFiscalDataTypeContext ctx);
		void visitPHM(AeatFiscalDataTypeContext ctx);
		void visitPF(AeatFiscalDataTypeContext ctx);
		void visitUR(AeatFiscalDataTypeContext ctx);
		void visitTPU(AeatFiscalDataTypeContext ctx);
		void visitTPR(AeatFiscalDataTypeContext ctx);
		void visitDM(AeatFiscalDataTypeContext ctx);
		void visitDMG(AeatFiscalDataTypeContext ctx);
		void visitDD(AeatFiscalDataTypeContext ctx);
		void visitAD(AeatFiscalDataTypeContext ctx);
		void visitFN(AeatFiscalDataTypeContext ctx);
		void visitFM(AeatFiscalDataTypeContext ctx);
		void visitCOD(AeatFiscalDataTypeContext ctx);
		void visitPDA(AeatFiscalDataTypeContext ctx);
		void visitPDB(AeatFiscalDataTypeContext ctx);
		void visitPDC(AeatFiscalDataTypeContext ctx);
		void visitPDD(AeatFiscalDataTypeContext ctx);
		void visitPDE(AeatFiscalDataTypeContext ctx);
		void visitPDF(AeatFiscalDataTypeContext ctx);
		void visitPDG(AeatFiscalDataTypeContext ctx);
		void visitPDH(AeatFiscalDataTypeContext ctx);
		void visitPDI(AeatFiscalDataTypeContext ctx);
		void visitPDJ(AeatFiscalDataTypeContext ctx);
		void visitPDK(AeatFiscalDataTypeContext ctx);
		void visitPDL(AeatFiscalDataTypeContext ctx);
		void visitPDX(AeatFiscalDataTypeContext ctx);
		void visitCLP(AeatFiscalDataTypeContext ctx);
		void visitRTN(AeatFiscalDataTypeContext ctx);
		void visitCNR(AeatFiscalDataTypeContext ctx);
		void visitGPP(AeatFiscalDataTypeContext ctx);
		void visitCTP(AeatFiscalDataTypeContext ctx);
		void visitPUE(AeatFiscalDataTypeContext ctx);
		void visitRUE(AeatFiscalDataTypeContext ctx);
		void visitRPT(AeatFiscalDataTypeContext ctx);
		void visitAY(AeatFiscalDataTypeContext ctx);
		void visitAGS(AeatFiscalDataTypeContext ctx);
		void visitATR(AeatFiscalDataTypeContext ctx);
		void visitIAP(AeatFiscalDataTypeContext ctx);
		void visitDD1(AeatFiscalDataTypeContext ctx);
		void visitDD2(AeatFiscalDataTypeContext ctx);
		void visitDG(AeatFiscalDataTypeContext ctx);
		void visitTI(AeatFiscalDataTypeContext ctx);
		void visitDE(AeatFiscalDataTypeContext ctx);
		void visitAS(AeatFiscalDataTypeContext ctx);
		void visitEND(AeatFiscalDataTypeContext ctx);
	}
}
