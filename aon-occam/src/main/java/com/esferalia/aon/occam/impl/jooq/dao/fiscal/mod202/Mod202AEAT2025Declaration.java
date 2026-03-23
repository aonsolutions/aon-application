package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod202;

import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.DEC2;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.bold;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.fontMedium;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.marginTop;
import static com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.paddingLeft;
import static com.esferalia.aon.watson.j2html.TagCreator.div;
import static com.esferalia.aon.watson.j2html.TagCreator.li;
import static com.esferalia.aon.watson.j2html.TagCreator.span;
import static com.esferalia.aon.watson.j2html.TagCreator.ul;

import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.AccountingBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.IModelScript;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Mod202Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.DeclarationInfoUtil.ExplainRowManager;
import com.esferalia.aon.watson.j2html.tags.DomContent;
import com.esferalia.aon.watson.j2html.tags.specialized.UlTag;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

public class Mod202AEAT2025Declaration extends Mod202Declaration {
	
	public static boolean accept(Mod202 mod) {
		return mod.isAEAT() && mod.getYear() >= 2025; 
	}
	
	private enum Mod202KeyDAO  implements IMod202KeyDAO {
		 P00(Mod202Key.P00)
		,P01(Mod202Key.P01)
		// Devengo (2). Fecha de inicio del período impositivo
		,P02(Mod202Key.P02)
		// Devengo (2). C.N.A.E. actividad principal
		,P03(Mod202Key.P03
			,(ctx,mod) -> mod.putDescription(Mod202Key.P03,Mod202DAO.getMainActivityCNAE( ctx, mod ))
			,null
			,null)
		// Contribuyente sometido a normativa de Territorio Foral de Navarra, 
		,X15(Mod202Key.X15)
		// Gipuzkoa 
		,X16(Mod202Key.X16)
		// Bizkaia 
		,X17(Mod202Key.X17)
		// Araba
		,X18(Mod202Key.X18)
		
		// Datos adicionales (3) - Entidad que aplica el régimen de la Ley 49/2002 de 23 de diciembre
		,X01(Mod202Key.X01)
		// Datos adicionales (3) - Entidad que aplica el régimen de la Ley 11/2009 de 26 de octubre
		,X02(Mod202Key.X02)
		// Datos adicionales (3) - Entidad de capital-riesgo que aplica el régimen fiscal especial del art. 50 LIS
		,X19(Mod202Key.X19)
		// Datos adicionales (3) - Entidad que aplica el régimen de las entidades navieras en función del tonelaje
		,X04(Mod202Key.X04)
		// Datos adicionales (3) - Entidad que cumpla los requisitos para la aplicación de los incentivos de empresa de reducida dimensión (art. 101 LIS) y apliquen el tipo de gravamen específico previsto para estas entidades
		,X05(Mod202Key.X05)
		// Datos adicionales (3) - Importe neto de la cifra de negocios de los doce meses anteriores a la fecha de inicio del período impositivo es superior a 6.000.000 euros
		,X06(Mod202Key.X06)
		// Datos adicionales (3) - Cooperativa fiscalmente protegida
		,X13(Mod202Key.X13)
		// Datos adicionales (3) - Marque esta casilla si concurre ALGUNA de las siguientes circunstancias:
		,X11(Mod202Key.X11)
		// Datos adicionales (3) - Entidad con importe neto de la cifra de negocios del período impositivo inmediato anterior inferior a 1 millón de euros
		,X20(Mod202Key.X20)
		// Datos adicionales (3) - Otras entidades con posibilidad de aplicar más de un tipo impositivo.
		,X14(Mod202Key.X14)
		// Datos adicionales (3) - Tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso
		,X08(Mod202Key.X08)
		// Datos adicionales (3) - Importe neto de la cifra de negocios en los doce meses anteriores a la fecha de inicio del período impositivo:
		,X09(Mod202Key.X09)
		
		// Liquidación de modalidad A ó B
		,X00(Mod202Key.X00)
		
		// A) Liquidación. Mod. 40.2 LIS - Base del pago fraccionado [01]
		,C01(Mod202Key.C01
			,(ctx,mod) -> mod.putAmount(Mod202Key.C01, getInitialC01(ctx,mod))
			,"isMethodA()?C01:0.0"
			,(ctx,mod) -> getC01ComputeKeyInfo(ctx,mod) )
		// A) Liquidación. Mod. 40.2 LIS - Resultado de la declaración anterior (complementarias) [02]
		,C02(Mod202Key.C02
			,(ctx,mod) -> mod.putAmount(Mod202Key.C02,
				(mod.isComplementary() && mod.isMethodA())
					?Mod202DAO.getSamePeriodFiscalModels(ctx, mod).mapToDouble(fm -> fm.getDeclarationResult()).sum()
					:0.0)
			,"isMethodA()?C02:0.0"
			, (ctx,mod) -> getC02ComputeKeyInfo( ctx, mod ))
		
		// A) Liquidación. Mod. 40.2 LIS - A Ingresar [03]
		,C03(Mod202Key.C03
			,null
			,"isMethodA()?((C01*18/100)-C02):0.0"  
			,(ctx,mod) -> getC03ComputeKeyInfo(ctx,mod) )
		
		// B) Liquidación. Mod. 40.3 LIS - Resultado contable después del IS e IC [04]
		,C04(Mod202Key.C04
			,(ctx,mod) -> mod.putAmount(Mod202Key.C04, getInitialC04(ctx,mod))
			,"isMethodB()?C04:0.0"
			,(ctx,mod) -> getC04ComputeKeyInfo(ctx,mod) )
		// B) Liquidación. Mod. 40.3 LIS - Correcciones al resultado contable - por Impuesto sobre Sociedades (IS)- Aumentos [05]
		,C05(Mod202Key.C05
			,null
			,"isMethodB()?C05:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - Correcciones al resultado contable - por Impuesto sobre Sociedades (IS)- Disminuciones [06]
		,C06(Mod202Key.C06
			,null
			,"isMethodB()?C06:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - Correcciones al resultado contable - por Impuesto Complementario (IC) [67]
		,C67(Mod202Key.C67
			,null
			,"isMethodB()?C67:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - 30% gastos amortiz - Disminuciones [37]
		,C37(Mod202Key.C37
			,null
			,"isMethodB()?C37:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - Resto correcciones al resultado contable, excepto comp. - Aumentos [07]
		,C07(Mod202Key.C07
			,null
			,"isMethodB()?C07:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - Resto correcciones al resultado contable, excepto comp. - Disminuciones [08]
		,C08(Mod202Key.C08
			,null
			,"isMethodB()?C08:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - TOTAL. - Aumentos [38]
		,C38(Mod202Key.C38
			,null
			,"isMethodB()?(C05+C67+C07):0.0"
			,(ctx,mod) -> getC38ComputeKeyInfo(ctx,mod) )
		// B) Liquidación. Mod. 40.3 LIS - TOTAL - Disminuciones [39]
		,C39(Mod202Key.C39
			,null
			,"isMethodB()?(C06+C37+C08):0.0"
			,(ctx,mod) -> getC39ComputeKeyInfo(ctx,mod) )
		// B) Liquidación. Mod. 40.3 LIS - Base imponible previa [13]
		,C13(Mod202Key.C13
			,null
			,"isMethodB()?(C04+C38-C39):0.0"
			,(ctx,mod) -> getC13ComputeKeyInfo(ctx,mod) )
		// B) Liquidación. Mod. 40.3 LIS - Remanente reserva de capitalización no aplicada por insuficiencia de base [44]
		,C44(Mod202Key.C44
			,null
			,"isMethodB()?C44:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - Compensación de bases negativas de ejercicios anteriores [14]
		,C14(Mod202Key.C14
			,null
			,"isMethodB()?C14:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - Reserva de nivelación (art. 105 LIS) (solo entidades que cumplan los requisitos para la aplicación de los incentivos de empresa de reducida dimensión (art. 101 LIS) y apliquen el tipo de gravamen específico previsto para estas entidades) - Aumentos [45]
		,C45(Mod202Key.C45
			,null
			,"isMethodB()?C45:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - Reserva de nivelación (art. 105 LIS) (solo entidades que cumplan los requisitos para la aplicación de los incentivos de empresa de reducida dimensión (art. 101 LIS) y apliquen el tipo de gravamen específico previsto para estas entidades) - Disminuciones [46]
		,C46(Mod202Key.C46
			,null
			,"isMethodB()?C46:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - B1 - Caso general (porcentaje único) -  Base pago fraccionado [16]
		,C16(Mod202Key.C16
			,null
			,"isMethodB1()?((C13-C14+C45-C46)>0?(C13-C14+C45-C46):0.0):0.0"
			,(ctx,mod) -> getC16ComputeKeyInfo(ctx,mod) )
		// B) Liquidación. Mod. 40.3 LIS - B1 - Caso general (porcentaje único) -  Porcentaje [17]
		,C17(Mod202Key.C17
			,null
			,"isMethodB1()?computeC17():0.0"
			,(ctx,mod) -> getC17ComputeKeyInfo(ctx,mod) )
		// B) Liquidación. Mod. 40.3 LIS - B1 - Caso general - Dotaciones del art. 11.12 LIS (DF 4ª LIS) [47]
		,C47(Mod202Key.C47
			,null
			,"isMethodB1()?C47:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - B1 - Caso general - Compensación de cuotas negativas ejer. anteriores  (sólo cooperativas) [40]
		,C40(Mod202Key.C40
			,null
			,"isMethodB1()?C40:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - B1 -Reserva de nivelación (105 LIS) convertido en cuotas (solo entidades que cumplan los requisitos para la aplicación de los incentivos de empresa de reducida dimensión (art. 101 LIS) y apliquen el tipo de gravamen específico previsto para estas entidades) - Aumentos [48]
		,C48(Mod202Key.C48
			,null
			,"isMethodB1()?C48:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - B1 -Reserva de nivelación (105 LIS) convertido en cuotas (solo entidades que cumplan los requisitos para la aplicación de los incentivos de empresa de reducida dimensión (art. 101 LIS) y apliquen el tipo de gravamen específico previsto para estas entidades) - Disminuciones [49]
		,C49(Mod202Key.C49
			,null
			,"isMethodB1()?C49:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - B1 - Caso general  -  Resultado previo (clave ([16] x [17]) + [47]-[40]+[48]-[49]) [18]
		,C18(Mod202Key.C18
			,null
			,"isMethodB1()?((C16*C17/100)+C47-C40+C48-C49):0.0"
			,(ctx,mod) -> getC18ComputeKeyInfo(ctx,mod) )
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) - Base del pago fraccionado [19]
		,C19(Mod202Key.C19
			,null
			,"isMethodB2()?((C13-C44-C14+C45)>0?(C13-C44-C14+C45):0.0):0.0"
			,(ctx,mod) -> getC19ComputeKeyInfo(ctx,mod) )
		
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) - Base a tipo 1 [20]
		,C20(Mod202Key.C20
			,null
			,"isMethodB2()?(C20):0.0"
			,(ctx,mod) -> "")
		
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Porcentaje [21]
		,C21(Mod202Key.C21
			,null
			,"isMethodB2()?computePercentage(0):0.0"
			,(ctx,mod) -> getPercentComputeKeyInfo(ctx, mod, Mod202Key.C21, getPercent1(mod)))
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Importe pago fraccionado [22]
		,C22(Mod202Key.C22
			,null
			,"isMethodB2()?(C20*C21/100):0.0"
			,(ctx,mod) -> getAmountComputeKeyInfo(ctx, mod, Mod202Key.C20, Mod202Key.C21, Mod202Key.C22))
		
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) - Base a tipo 2 [23]
		,C23(Mod202Key.C23
			,null
			,"isMethodB2()?(C23):0.0"
			,(ctx,mod) -> "" )
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Porcentaje [24]
		,C24(Mod202Key.C24
			,null
			,"isMethodB2()?computePercentage(1):0.0"
			,(ctx,mod) -> getPercentComputeKeyInfo(ctx, mod, Mod202Key.C24, getPercent2(mod)))
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Importe pago fraccionado [25]
		,C25(Mod202Key.C25
			,null
			,"isMethodB2()?(C23*C24/100):0.0"
			,(ctx,mod) -> getAmountComputeKeyInfo(ctx, mod, Mod202Key.C23, Mod202Key.C24, Mod202Key.C25))
		
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) - Base a tipo 3 [61]
		,C61(Mod202Key.C61
			,null
			,"isMethodB2()?(C61):0.0"
			,(ctx,mod) -> "")
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Porcentaje [62]
		,C62(Mod202Key.C62
			,null
			,"isMethodB2()?computePercentage(2):0.0"
			,(ctx,mod) -> getPercentComputeKeyInfo(ctx, mod, Mod202Key.C62, getPercent3(mod)))
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Importe pago fraccionado [63]
		,C63(Mod202Key.C63
			,null
			,"isMethodB2()?(C61*C62/100):0.0"
			,(ctx,mod) -> getAmountComputeKeyInfo(ctx, mod, Mod202Key.C61, Mod202Key.C62, Mod202Key.C63))
		
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) - Base a tipo 4 [64]
		,C64(Mod202Key.C64
			,null
			,"isMethodB2()?(C64):0.0"
			,(ctx,mod) -> "")
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Porcentaje [65]
		,C65(Mod202Key.C65
			,null
			,"isMethodB2()?computePercentage(3):0.0"
			,(ctx,mod) -> getPercentComputeKeyInfo(ctx, mod, Mod202Key.C65, getPercent4(mod)))
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Importe pago fraccionado [66]
		,C66(Mod202Key.C66
			,null
			,"isMethodB2()?(C64*C65/100):0.0"
			,(ctx,mod) -> getAmountComputeKeyInfo(ctx, mod, Mod202Key.C64, Mod202Key.C65, Mod202Key.C66))
		
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje) -  Dotac. art. 11.12 LIS (solo cooperativas) (DF 4 LIS) [50]
		,C50(Mod202Key.C50
			,null
			,"isMethodB2()?C50:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje). Compensación de cuotas negativas de períodos anteriores (sólo cooperativas) [42]
		,C42(Mod202Key.C42
			,null
			,"isMethodB2()?C42:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje)-Reserva de nivelación (art. 105 LIS) (solo entidades que cumplan los requisitos para la aplicación de los incentivos de empresa de reducida dimensión (art. 101 LIS) y apliquen el tipo de gravamen específico previsto para estas entidades). Aumentos [51]
		,C51(Mod202Key.C51
			,null
			,"isMethodB2()?C51:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje)-Reserva de nivelación (art. 105 LIS) (solo entidades que cumplan los requisitos para la aplicación de los incentivos de empresa de reducida dimensión (art. 101 LIS) y apliquen el tipo de gravamen específico previsto para estas entidades). Disminuciones [52]
		,C52(Mod202Key.C52
			,null
			,"isMethodB2()?C52:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - B2 - Casos específicos (más de un porcentaje)-Resultado previo(claves [22]+[25]+[50]-[42]+[51]-[52]) [26]
		,C26(Mod202Key.C26
			,null
			,"isMethodB2()?(C22+C25+C63+C66+C50-C42+C51-C52):0.0"
			,(ctx,mod) -> getC26ComputeKeyInfo(ctx,mod) )
		// B) Liquidación. Mod. 40.3 LIS - Bonificaciones correspondientes al periodo computado (total) [27]
		,C27(Mod202Key.C27
			,null
			,"isMethodB()?C27:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - Retenciones e ingresos a cuenta practicados sobre ingresos periodo computado [28]
		,C28(Mod202Key.C28
			,null
			,"isMethodB()?C28:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - Volumen operaciones en Territorio Común (%) [29]
		,C29(Mod202Key.C29
			,null
			,"isMethodB()?C29:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - Pagos fraccionados de periodos anteriores en Territorio Común [30]
		,C30(Mod202Key.C30
			,null
			,"isMethodB()?C30:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - Resultado de la declaración anterior (exclusivamente si ésta es complementaria) [31]
		,C31(Mod202Key.C31
			,(ctx,mod) -> mod.putAmount(Mod202Key.C31,
				(mod.isComplementary() && mod.isMethodB())
					?Mod202DAO.getSamePeriodFiscalModels(ctx, mod).mapToDouble(fm -> fm.getDeclarationResult()).sum()
					:0.0)
			,"isMethodB()?C31:0.0"
			, (ctx,mod) -> getC31ComputeKeyInfo( ctx, mod ))
		// B) Liquidación. Mod. 40.3 LIS - Resultado [32]
		,C32(Mod202Key.C32
			,null
			,"computeC32()"
			,(ctx,mod) -> getC32ComputeKeyInfo(ctx,mod) )
		// B) Liquidación. Mod. 40.3 LIS - Mínimo a ingresar (sólo para empresas con CN igual o superior a 10 millones euros) [33]
		,C33(Mod202Key.C33
			,null
			,"isMethodB()?C33:0.0"
			,null)
		// B) Liquidación. Mod. 40.3 LIS - Cantidad a ingresar (mayor de claves [32] y [33] )  [34]
		,C34(Mod202Key.C34
			,null
			,"isMethodB()?(C32>C33?C32:C33):0.0"
			,(ctx,mod) -> getC34ComputeKeyInfo(ctx,mod) )

		// Información adicional  (5). Comunicación de datos adicionales a la declaración
		,A01(Mod202Key.A01)
		// Información adicional  (5). Numero de Referencia de Sociedades (NRS)
		,A02(Mod202Key.A02)
		// Información adicional  (5). Importe excluido por operaciones de quita o espera
		,A03(Mod202Key.A03)
		// Información adicional  (5). Parte integrada en la base imponible por operaciones de quita o espera
		,A04(Mod202Key.A04)
		// Información adicional  (5). Parte integrada en la base imponible a nivel cuota por op. de quita o espera (sólo cooperativas)
		,A05(Mod202Key.A05)
		// Información adicional  (5). Rentas de reversión de deterioros que se integran en la base imponible
		,A06(Mod202Key.A06)
		// Información adicional  (5). Importe correspondiente a la reserva para inversiones en Canarias
		,A07(Mod202Key.A07)
		// Información adicional  (5). Importe correspondiente a la bonificación prevista en el art. 26 de la Ley 19/1994
		,A08(Mod202Key.A08)
		// Información adicional  (5). Importe no computable por aplicación del régimen fiscal de la ZEC
		,A09(Mod202Key.A09)
		// Información adicional  (5). Importe de la minoración correspondiente a las rentas que tengan derecho a la bonificación art. 33 LIS
		,A10(Mod202Key.A10)
		// Información adicional  (5). Importe excluido por operaciones de aumento de capital o fondos propios por compensación de créditos que no se integren en la base imponible por aplicación del art. 17.2 LIS
		,A11(Mod202Key.A11)
		// Información adicional  (5). Importe renta exenta de las entidades que aplican el régimen fiscal especial Cap. XIV del Tit. VII LIS
		,A12(Mod202Key.A12)
		// Información adicional  (5). Importe de la bonificación prevista en el art. 34 LIS
		,A13(Mod202Key.A13)
		;
		
		private Mod202Key key;
		private IValueIntializer initializer;
		private String expression;
		private IValueInfo info;
		
		private Mod202KeyDAO(Mod202Key key) {
			this(key, null, null, null);
		}
		
		private Mod202KeyDAO(Mod202Key key
				, IValueIntializer initializer
				, String expression
				, IValueInfo info) {
			this.key = key;
			this.initializer = initializer;
			this.expression =  expression;
			this.info =  info;
		}

		@Override
		public Mod202Key getKey() {
			return key;
		}
		@Override
		public boolean accept(Mod202 mod) {
			return true;
		}
		@Override
		public void initialize(AONContext ctx,Mod202 mod) {
			if (initializer != null) {
				initializer.initialize(ctx, mod);
			}
		}
		@Override
		public String getExpression() {
			return expression;
		}
		
		@Override
		public String info(AONContext ctx, Mod202 mod) {
			return (this.info != null)?this.info.info(ctx, mod):null;
		}
		
	}

	// **************************************************************
	// **************************************************************
	// **************************************************************
	// **************************************************************
	// **************************************************************
	// **************************************************************
	
	@Override
	IMod202KeyDAO valueOf(String string) {
		return Mod202KeyDAO.valueOf(string);
	}

	@Override
	IMod202KeyDAO[] getKeys() {
		return Mod202KeyDAO.values();
	}

	@Override
	double getResult(Mod202 mod) {
		double x00 = mod.getAmount(Mod202Key.X00);
		if (x00 == 0 ) {
			return mod.getAmount(Mod202Key.C03);
		} 
		return mod.getAmount(Mod202Key.C34);			
	}
	
	@Override
	ComplementaryBeahaviour getComplementaryBehaviour(Mod202 mod) {
		return ComplementaryBeahaviour.REPLACEMENT;
	}

	@Override
	Mod202 initialize(AONContext ctx, Mod202 mod202) {
		mod202.setComplementaryDeclarationAvailable(true);
		mod202.setReplacementDeclarationAvailable(false);
		return super.initializeModel(ctx, mod202);
	}
	
	@Override
	Mod202 uniqueInitialize(AONContext ctx, Mod202 mod202) {
		AonCollectionUtils.stream(getKeys())
			.forEach( k -> k.initialize(ctx, mod202));
		return mod202;
	}

	@Override
	public Mod202Key[] getSamePeriodExplainKeys() {
		return new Mod202Key[] {};
	}
	private static DomContent noMethod(String msg) {
		return ul()
			.with( li( msg ).withStyle( bold ) )
			.with( li( "Resultado : 0.0" ) );
	}
	private static DomContent noMethodA() 	{return noMethod("Tipo C\u00E1lculo no es A) Art\u00EDculo LIS 40.2 LIS");}
	private static DomContent noMethodB() 	{return noMethod("Tipo C\u00E1lculo no es B.1) ni B.2)");}
	private static DomContent noMethodB1() 	{return noMethod("Tipo C\u00E1lculo no es B.1)");}
	private static DomContent noMethodB2() 	{return noMethod("Tipo C\u00E1lculo no es B.2)");}
	
	private static String fmt( double d ) {
		return DEC2.format( d );
	}

	private static String getC01ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C01, new ExplainRowManager() {
				@Override
				public String apply(FiscalModel fm) {
					setSomething(true);
					if (mod.isMethodA()) {
						int year = mod.getYear()- ((mod.getPeriod() == Period.T1)?2:1); 
						return ul()
							.with( li( "Tipo C\u00E1lculo: A) Art\u00EDculo LIS 40.2 LIS") )
							.with( li( "Valor de la casilla [599] del Impuesto de Sociedades del ejercicio "
									+year
									+" : "
									+ fmt( getInitialC01(ctx, mod) ))
									)
							.render();
					}
					return noMethodA().render();
				}
		});	
	}

	private static String getC03ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C03, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodA()) {
					return ul()
						.with( li( "Resultado del 18% de [001] menos [002] ") )
						.with( li(" 18% de " 
								+ fmt( mod.getAmount( Mod202Key.C01 ))
								+ " menos "
								+ fmt( mod.getAmount( Mod202Key.C02 ))
								+ " igual "
								+ fmt( mod.getAmount( Mod202Key.C03 ))))
						.render();
				} 
				return noMethodA().render();
			}
		});	
	}
	
	private static String getC38ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.wrap( mod, fm -> {
				if (mod.isMethodB()) {
					Double c05 = fm.getAmount(Mod202Key.C05);
					Double c67 = fm.getAmount(Mod202Key.C67);
					Double c07 = fm.getAmount(Mod202Key.C07);
					Double c38 = fm.getAmount(Mod202Key.C38);
					return div().withStyle( marginTop )
						.with( span(Mod202Key.C38.getFullDescription())
								.withStyle( bold+fontMedium ) )
						.with( ul()
							.withStyle( paddingLeft )
							.with( li( "Resultado de la f\u00F3rmula [005] + [067] + [007] = [038]") )
							.with( li( fmt(c05)+" - "+fmt(c67)+" + "+fmt(c07)+" = "+fmt(c38)) ));
				} 
				return noMethodB();
		});	
	}

	private static String getC39ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.wrap( mod, fm -> {
			if (mod.isMethodB()) {
				Double c06 = mod.getAmount(Mod202Key.C06);
				Double c37 = mod.getAmount(Mod202Key.C37);
				Double c08 = mod.getAmount(Mod202Key.C08);
				Double c39 = mod.getAmount(Mod202Key.C39);
				return div().withStyle( marginTop )
						.with( span(Mod202Key.C39.getFullDescription())
								.withStyle( bold+fontMedium ) )
						.with( ul()
							.withStyle( paddingLeft )
							.with( li( "Resultado de la f\u00F3rmula [006] + [037] + [008] = [039]") )
							.with( li( fmt(c06)+" + "+fmt(c37)+" + "+fmt(c08)+" = "+fmt(c39)) ))
						;
			} 
			return noMethodB();
		});	
	}
	
	private static String getC02ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return getSamePeriodExplain(ctx, mod, Mod202Key.C02);
	}
	
	private static String getC04ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C04, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				Pair<Date,Date> dates = getAccountingRangePeriod(mod );
				if (mod.isMethodB()) {
					return ul()
						.with( li( "Desde contabilidad.") )
						.with( li( "Saldos acreedor del sumatorio de cuentas que afectan a la confecci\u00F3n del modelo"
								+ " desde el "
								+ DeclarationInfoUtil.FMT.format(dates.getLeft())
								+ " al "
								+ DeclarationInfoUtil.FMT.format(dates.getRight())))
						.render();
				}
				return noMethodB().render();
			}
		});	
	}
	
	@Override
	Stream<AccountingBreakdown> getAccountInfoInfo(AONContext ctx, Mod202 mod, IModelScript<Mod202Key> script,IMod202KeyDAO keyDAO) {
		if (mod.isMethodB() && keyDAO.getKey() == Mod202Key.C04) {
			return getInitialBaseC04(ctx, mod);
		}
		return Stream.empty(); 
	}
	
	private static String getC13ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C13, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB()) {
					return ul()
						.with( li( "Resultado de [004] m\u00E1s [038] menos [039]") )
						.with( li(fmt( mod.getAmount( Mod202Key.C04 ))
								+ " m\u00E1s "
								+ fmt( mod.getAmount( Mod202Key.C38 ))
								+ " menos "
								+ fmt( mod.getAmount( Mod202Key.C39 ))
								+ " igual "
								+ fmt( mod.getAmount( Mod202Key.C13 ))))
						.render();
				}
				return noMethodB().render();
			}
		});
	}

	private static String getC16ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C16, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB1()) {
					double c13 = mod.getAmount( Mod202Key.C13); 
					double c14 = mod.getAmount( Mod202Key.C14);
					double c45 = mod.getAmount( Mod202Key.C45);
					double c46 = mod.getAmount( Mod202Key.C46);
					double c16 = AonMathUtils.round(c13-c14+c45-c46);
					if ( AonMathUtils.isGreatherThanZero(c16)) {
						return ul()
							.with( li( "Resultado de [013] menos [014] m\u00E1s [045] menos [046]") )
							.with( li(fmt( c13 )
									+ " menos "
									+ fmt( c14 )
									+ " m\u00E1s "
									+ fmt( c45 )
									+ " menos "
									+ fmt( c46 )
									+ " igual "
									+ fmt( c16 )))
							.render();
					} else {
						return ul()
							.with( li( "Al ser el resultado de [013] menos [014] m\u00E1s [045] menos [046] menor que cero, el resultado es cero") )
							.with( li(fmt( c13 )
									+ " menos "
									+ fmt( c14 )
									+ " m\u00E1s "
									+ fmt( c45 )
									+ " menos "
									+ fmt( c46 )
									+ " igual "
									+ fmt( c16 )
									+ ". Menor que cero, por lo tanto cero."))
							.render();
					}
				}
				return noMethodB1().render();
			}
		});
	}
	
	private static String getC17ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C17, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB1()) {
					Double x08 = getPercent(mod);
					Double x04 = mod.getAmount(Mod202Key.X04);
					Double x09 = mod.getAmount(Mod202Key.X09);
					double c17 = mod.getAmount(Mod202Key.C17);
					if (AonMathUtils.isNotZero(x04) && mod.getYear() >= 2017  ) {
						return ul()
							.with( li( "Entidad que aplica el r\u00E9gimen de las entidades navieras en funci\u00F3n del tonelaje: " + fmt( c17 )) )
							.render();
					} else if (AonMathUtils.isZero(x09)) {
						return ul()
							.with( li( "Resultado de aplicar 5/7 al tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso (" + fmt(x08) 
									+ "%) es decir, ("+ fmt(x08) + " * 5/7)" 
									+", redondeado a la baja, igual: "+ fmt( c17 )) )
							.render();
					} else {
						return ul()
							.with( li( "Resultado de aplicar 19/20 al tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso (" + fmt(x08) 
									+ "%) es decir, ("+ fmt(x08) + " * 19/20)" 
									+ ", redondeado al alza, igual: "+ fmt( c17 )) )
							.render();
					}
				}
				return noMethodB1().render();
			}
		});
	}
	
	private static String getC18ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C18, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB1()) {
					// (C16*C17/100)+C47-C40+C48-C49"
					Double c16 = mod.getAmount(Mod202Key.C16);
					Double c17 = mod.getAmount(Mod202Key.C17);
					Double c47 = mod.getAmount(Mod202Key.C47);
					Double c40 = mod.getAmount(Mod202Key.C40);
					Double c48 = mod.getAmount(Mod202Key.C48);
					Double c49 = mod.getAmount(Mod202Key.C49);
					Double c18 = mod.getAmount(Mod202Key.C18);
					return ul()
						.with( li( "Resultado de la f\u00F3rmula ([016] * [017] / 100 ) + [047] - [040] + [048] - [049] = [018]") )
						.with( li( "("+fmt(c16) +" * "+fmt(c17)+" / 100 ) + "+fmt(c47)+" - "+fmt(c40)+" + "+fmt(c48)+" - "+fmt(c49)+" = "+fmt(c18)) )
						.render();
				}
				return noMethodB1().render();
			}
		});
	}

	private static String getC19ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C19, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB2()) {
					Double c13 = mod.getAmount(Mod202Key.C13);
					Double c44 = mod.getAmount(Mod202Key.C44);
					Double c14 = mod.getAmount(Mod202Key.C14);
					Double c45 = mod.getAmount(Mod202Key.C45);
					Double temp = AonMathUtils.round(c13-c44-c14+c45);
					Double c19 = mod.getAmount(Mod202Key.C19);
					if (AonMathUtils.isLessThanZero(temp)) {
						return ul()
							.with( li( "Al ser la casilla [013]-[044]-[014]+[045] menor que cero, "
								+ " ("
								+ fmt(temp)
								+") "
								+"el resultado es cero.") )				
							.render();
					} else {
						return ul()
							.with( li( "Resultado de la f\u00F3rmula [013]-[044]-[014]+[045] = [018]") )
							.with( li( fmt(c13)+"-"+fmt(c44)+"-"+fmt(c14)+"+"+fmt(c45)+" = "+fmt(c19)) )
							.render();
					}
				}
				return noMethodB2().render();
			}
		});
	}
	
	private static String getC26ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C26 , new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB2()) {
					double c22 = mod.getAmount(Mod202Key.C22);
					double c25 = mod.getAmount(Mod202Key.C25);
					double c50 = mod.getAmount(Mod202Key.C50);
					double c42 = mod.getAmount(Mod202Key.C42);
					double c51 = mod.getAmount(Mod202Key.C51);
					double c52 = mod.getAmount(Mod202Key.C52);
					double c26 = mod.getAmount(Mod202Key.C26);
					return ul()
						.with( li( "Resultado de la f\u00F3rmula [022] + [025] + [050] - [042] + [051] - [052] ") )
						.with( li( fmt(c22)+" + "+fmt(c25)+" + "+fmt(c50)+" - "+fmt(c42)+" + "+fmt(c51)+" - "+fmt(c52)+" = "+fmt(c26) ) )
						.render();
				}
				return noMethodB2().render();
			}
		});
	}	
	private static String getC31ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return getSamePeriodExplain(ctx, mod, Mod202Key.C31);
	}

	private static String getC32ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C32 , new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB()) {
					double c18 = mod.getAmount(Mod202Key.C18);
					double c26 = mod.getAmount(Mod202Key.C26);
					double c27 = mod.getAmount(Mod202Key.C27);
					double c28 = mod.getAmount(Mod202Key.C28);
					double c29 = mod.getAmount(Mod202Key.C29);
					double c30 = mod.getAmount(Mod202Key.C30);
					double c31 = mod.getAmount(Mod202Key.C31);
					double c32 = mod.getAmount(Mod202Key.C32);
					double rp = mod.isMethodB1()?c18:c26;
					UlTag ul = ul();
					if (mod.isMethodB1()) {
						ul.with( li( "Si la modalidad de c\u00E1lculo es B.1) : Resultado Previo [018] --> "+fmt(c18)) );
					} else {
						ul.with( li( "Si la modalidad de c\u00E1lculo es B.2) : Resultado Previo [026] --> "+fmt(c26)) );
					}
					double c32T= ((rp-c27-c28)*c29/100)-c30-c31;
					ul
						.with( li( "Resultado de la f\u00F3rmula (([Resultado Previo] - [027] - [028] ) * [029] / 100 ) - [030] - [031]") )
						.with( li( "(("+fmt(rp)+" - "+fmt(c27)+" - "+fmt(c28)+" ) * "+fmt(c29)+" / 100 ) - "+fmt(c30)+" - "+fmt(c31)+" = "+fmt(c32T)) );
					if (AonMathUtils.isLessThanZero( c32T )) {
						ul.with( li( "Al ser resultado negativo, cero: "+fmt(c32)) );
					}
					return ul.render();
				}
				return noMethodB2().render();
			}
		});
	}
	
	private static String getC34ComputeKeyInfo(AONContext ctx, Mod202 mod) {
		return DeclarationInfoUtil.getExplain( ctx, mod, Mod202Key.C32 , new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB()) {
					double c34 = mod.getAmount(Mod202Key.C34);
					return ul()
						.with( li("Si la modalidad de c\u00E1lculo es B.1) o B.2) : La cantidad mayor entre [032] y [033]") )
						.with( li( "Resultado: " + fmt(c34)) )
						.render();
				}
				return noMethodB2().render();
			}
		});
	}
	
	private static String getPercentComputeKeyInfo(AONContext ctx, Mod202 mod, Mod202Key percentKey, double taxType) {
		return DeclarationInfoUtil.getExplain( ctx, mod, percentKey , new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB2()) {
					double x09 = mod.getAmount(Mod202Key.X09);
					double percent = mod.getAmount(percentKey);
					if (AonMathUtils.isZero(x09)) {
						return ul()
							.with( li( "Casilla " + percentKey.getBoxFormatted() + " :").withStyle( bold ) )	
							.with( li( "Resultado de aplicar 5/7 al tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso (" + fmt(taxType) 
									+ "%) es decir, ("+ fmt(taxType) + " * 5/7)" 
									+", redondeado a la baja, igual: "+ fmt(percent)) )
							.render();
					} else {
						return ul()
							.with( li( "Casilla " + percentKey.getBoxFormatted() + " :").withStyle( bold ) )
							.with( li( "Resultado de aplicar 19/20 al tipo de gravamen del Impuesto sobre Sociedades del ejercicio en curso (" + fmt(taxType) 
									+ "%) es decir, ("+ fmt(taxType) + " * 19/20)" 
									+ ", redondeado al alza, igual: "+ fmt(percent)) )
							.render();
					}
				}
				return noMethodB2().render();
			}
		});
	}
	
	private static String getAmountComputeKeyInfo(AONContext ctx, Mod202 mod, Mod202Key baseKey, Mod202Key percentKey, Mod202Key amountKey ) {
		return DeclarationInfoUtil.getExplain( ctx, mod, amountKey, new ExplainRowManager() {
			@Override
			public String apply(FiscalModel fm) {
				setSomething(true);
				if (mod.isMethodB2()) {
					double c23 = mod.getAmount(baseKey);
					double c24 = mod.getAmount(percentKey);
					double c25 = mod.getAmount(amountKey);
					return ul()
						.with( li( "Casilla " + amountKey.getBoxFormatted() + " :").withStyle( bold ) )						
						.with( li( "Resultado de la f\u00F3rmula " + baseKey.getBoxFormatted() + " * " + percentKey.getBoxFormatted() + " / 100 ") )
						.with( li( fmt(c23)+" * "+fmt(c24)+" / 100 = "+fmt(c25)) )
						.render();
				}
				return noMethodB2().render();
			}
		});
	}	
	
	public static double getPercent(Mod202 mod) {
		return AonNumberUtils.todouble( mod.getDescription(Mod202Key.X08) );
	}
	public static double getPercent1(Mod202 mod) {
		String[] percent = AonStringUtils.split(AonStringUtils.replace(mod.getDescription(Mod202Key.X08),"N",""), '/');
		if (percent != null && percent.length > 0)
			return AonNumberUtils.todouble(percent[0]);
		else 
			return 0.0;
	}
	public static double getPercent2(Mod202 mod) {
		String[] percent = AonStringUtils.split(AonStringUtils.replace(mod.getDescription(Mod202Key.X08),"N",""), '/');
		if (percent != null && percent.length > 1)
			return AonNumberUtils.todouble(percent[1]);
		else 
			return 0.0;
	}
	public static double getPercent3(Mod202 mod) {
		String[] percent = AonStringUtils.split(AonStringUtils.replace(mod.getDescription(Mod202Key.X08),"N",""), '/');
		if (percent != null && percent.length > 2)
			return AonNumberUtils.todouble(percent[2]);
		else 
			return 0.0;
	}
	public static double getPercent4(Mod202 mod) {
		String[] percent = AonStringUtils.split(AonStringUtils.replace(mod.getDescription(Mod202Key.X08),"N",""), '/');
		if (percent != null && percent.length > 3)
			return AonNumberUtils.todouble(percent[3]);
		else 
			return 0.0;
	}	

}
