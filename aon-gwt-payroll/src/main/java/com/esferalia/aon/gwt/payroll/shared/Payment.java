package com.esferalia.aon.gwt.payroll.shared;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.js.payroll.client.Reports;

public class Payment extends Item<Payment.Type> implements Reports.Payment{

	
	Double irpf;
	Double quote;

	String irpfExpression;
	String quoteExpression;

	public static enum Type implements HasDescription {
		//@formatter:off
		CRA_0000,
		CRA_0001,
		CRA_0002,
		CRA_0003,
		CRA_0004,
		CRA_0005,
		CRA_0006,
		CRA_0007,
		CRA_0008,
		CRA_0009,
		CRA_0010,
		CRA_0011,
		CRA_0012,
		CRA_0013,
		CRA_0014,
		CRA_0015,
		CRA_0016,
		CRA_0017,
		CRA_0018,
		CRA_0019,
		CRA_0020,
		CRA_0021,
		CRA_0022,
		CRA_0023,
		CRA_0024,
		CRA_0025,
		CRA_0026,
		CRA_0027,
		CRA_0028,
		CRA_0029,
		CRA_0030,
		CRA_0031,
		CRA_0032,
		CRA_0033,
		CRA_0034,
		CRA_0035,
		CRA_0036,
		CRA_0037,
		CRA_0038,
		CRA_0039,
		CRA_0040,
		CRA_0041,
		CRA_0042,
		CRA_0043,
		CRA_0044,
		CRA_0045,
		CRA_0046,
		CRA_0047,
		CRA_0048,
		CRA_0049,
		CRA_0050,
		CRA_0051,
		CRA_0052,
		CRA_0053,
		CRA_0054,
		CRA_0055,
		CRA_0056,
		CRA_0057,
		CRA_0058,
		CRA_0059,
		CRA_0060,
		CRA_0061;
		//@formatter:on

		public String getDescription() {
			String description = DESCRIPTIONS.get(this);
			return description != null ? StringUtils.leftPad(getCode() , 4, '0') + " " + description : null;
		}
		
		public String getDescription2() {
			String description = DESCRIPTIONS.get(this);
			return description != null ? StringUtils.leftPad(getCode() , 2, '0') + " " + description : null;
		}
		
		public int getCode() {
			return this.ordinal();
		}
		
		public boolean isBBCCIncluded() {
			return BBCC_INCLUDED.getOrDefault(this, true);
		}

		public boolean isBBCCExcluded() {
			return BBCC_EXCLUDED.getOrDefault(this, false);
		}


		@Override
		public String toString() {
			return getDescription();
		}

		//@formatter:off
		static Map<Type, String> DESCRIPTIONS = new HashMap<Type, String>() {
			{
				put(CRA_0000, "-");
				put(CRA_0001, "RETRIBUCION NO INCLUIDA OTROS APARTADOS");
				put(CRA_0002, "HORAS EXTRAORDINARIAS NO ESTRUCTURALES");
				put(CRA_0003, "HORAS EXTR. ESTRUCTURALES O FUERZA MAYOR");
				put(CRA_0004, "PAGAS EXTRAORDINARIAS.PRORRATEO");
				put(CRA_0005, "RETR\u2260PAGA.EXTR.VENCIM.SUP.MES.PRORRATEO");
				put(CRA_0006, "VACACIONES RETRIBUIDAS NO DISFRUTADAS");
				put(CRA_0007, "SALARIOS DE TRAMITACI\u00D3N");
				put(CRA_0008, "RETR.POR ATRASOS NO INCLUIDA OTROS APART");
				put(CRA_0009, "RETRIBUCI\u00D3N POR ATRASOS.CONV.COLECTIVO");
				put(CRA_0010, "RETRIBUCI\u00D3N POR ATRASOS.SENTENCIA JUD.");
				put(CRA_0011, "RETRIBUCI\u00D3N POR ATRASOS.NORMATIVA");
				put(CRA_0012, "RETRIBUCI\u00D3N POR ATRASOS.ACTA CONCILIAC");
				put(CRA_0013, "R.ESPECIE NO INCLUIDA EN OTROS APARTADOS");
				put(CRA_0014, "R.ESP.VIVIENDA.PROP.PAGAD.C/VALOR.CATAST.");
				put(CRA_0015, "R.ESP.VIVIENDA.PROP.PAGAD.PTE.VALOR.CAT.");
				put(CRA_0016, "R.ESP.VIVIENDA.NO PROPIEDAD PAGADOR");
				put(CRA_0017, "R.ESP.VEH\u00CDCULO.ENTREGA AL TRABAJADOR");
				put(CRA_0018, "R.ESP.VEH\u00CDCULO.USO.PROPIEDAD PAGADOR");
				put(CRA_0019, "R.ESP.VEH\u00CDCULO USO.NO PROPIEDAD PAGADOR");
				put(CRA_0020, "R.ESP.VEH\u00CDCULO USO Y POSTERIOR ENTREGA");
				put(CRA_0021, "R.ESP.PR\u00C9STAMO.TIPO INTER\u00C9S < LEGAL");
				put(CRA_0022, "R.ESP. MANUTENCI\u00D3N Y SIMILARES");
				put(CRA_0023, "R.ESP. HOSPEDAJE Y SIMILARES");
				put(CRA_0024, "R.ESP. VIAJES Y SIMILARES");
				put(CRA_0025, "R.ESP.GASTOS DE ESTUDIOS Y MANUTENCI\u00D3N");
				put(CRA_0026, "R.ESP.DERECHOS FUNDADORES DE SOCIEDADES");
				put(CRA_0027, "QUEBRANTO DE MONEDA");
				put(CRA_0028, "DESGASTE \u00DATILES Y HERRAMIENTAS");
				put(CRA_0029, "ADQUISICI\u00D3N Y MANTENIMIENTO ROPA TRABAJO");
				put(CRA_0030, "PERCEPCIONES POR MATRIMONIO");
				put(CRA_0031, "DONACIONES PROMOCIONALES");
				put(CRA_0032, "PLUSES DE TRANSPORTE Y DE DISTANCIA");
				put(CRA_0033, "PLANES PENSIONES Y SIST. ALTERNATIVOS");
				put(CRA_0034, "ACCIONES O PARTICIPACIONES EMPRESA");
				put(CRA_0035, "GASTOS ESTUDIO ACT. CAPACIT. O RECICLAJE");
				put(CRA_0036, "PRODUCTOS.PREC.REB.-CANTIN.COMED.ECONOM.");
				put(CRA_0037, "BIENES DESTINADOS A SERV. SOC. Y CULT.");
				put(CRA_0038, "PRIMAS SEGURO AT O RESPONS. CIVIL TRAB.");
				put(CRA_0039, "PRIMAS SEGURO ENFERMEDAD COM\u00DAN TRABAJ.");
				put(CRA_0040, "PRIMAS SEGURO ENFERMEDAD COM\u00DAN FAMILIAR.");
				put(CRA_0041, "PREST. EDUC. POR CENTR.AUT. A HIJ. TRAB.");
				put(CRA_0042, "GASTOS DE ESTANCIA");
				put(CRA_0043, "GASTOS MANUTENCI\u00D3N PERNOCTA ESPA\u00D1A");
				put(CRA_0044, "GASTOS MANUTENCI\u00D3N PERNOCTA EXTRANJERO");
				put(CRA_0045, "GASTOS MANUTENCI\u00D3N SIN PERNOCTA ESPA\u00D1A");
				put(CRA_0046, "GASTOS MANUTENCI\u00D3N SIN PERNOCTA EXTRANJERO");
				put(CRA_0047, "GASTOS MANUTENCI\u00D3N PERSONAL VUELO ESPA\u00D1A");
				put(CRA_0048, "GASTOS MANUTENCI\u00D3N PERSONAL VUELO EXTR.");
				put(CRA_0049, "GASTOS DE LOCOMOCI\u00D3N TRANSPORTE P\u00DABLICO");
				put(CRA_0050, "GASTOS LOCOMOCI\u00D3N SIN JUSTIFIC. IMPORTE");
				put(CRA_0051, "INDEMNIZACIONES POR FALLECIMIENTO");
				put(CRA_0052, "INDEMNIZACIONES POR TRASLADOS");
				put(CRA_0053, "INDEMNIZACIONES POR SUSPENSIONES");
				put(CRA_0054, "INDEMNIZACIONES POR DESPIDO O CESE");
				put(CRA_0055, "MEJORAS PREST.SS.INCAPACIDAD TEMPORAL");
				put(CRA_0056, "MEJORAS PREST.SS.\u2260INCAPACIDAD TEMPORAL");
				put(CRA_0057, "HORAS COMPLEMENTARIAS PACTADAS");
				put(CRA_0058, "HORAS COMPLEMENTARIAS DE ACEPTACI\u00D3N VOLUNTARIA");
				put(CRA_0059, "VACACIONES NO DISFRUTADAS, RETRIBUIDAS TRAS EL FALLECIMIENTO DEL TRABAJADOR");
				put(CRA_0060, "VACACIONES RETRIBUIDAS NO DISFRUTADAS. COTIZACI\u00D3N DURANTE EL CONTRATO");
				put(CRA_0061, "PLUS DE TRANSPORTE Y DE DISTANCIA. UTILIZACI\u00D3N DE MEDIOS COLECTIVOS APORTADOS POR LA EMPRESA");
			}
		};
		//@formatter:on

		static Map<Type, Boolean> BBCC_INCLUDED = new HashMap<Type, Boolean>() {
			{
				put(CRA_0000, false);
				put(CRA_0001, true);
				put(CRA_0002, true);
				put(CRA_0003, true);
				put(CRA_0004, true);
				put(CRA_0005, true);
				put(CRA_0006, true);
				put(CRA_0007, true);
				put(CRA_0008, true);
				put(CRA_0009, true);
				put(CRA_0010, true);
				put(CRA_0011, true);
				put(CRA_0012, true);
				put(CRA_0013, true);
				put(CRA_0014, true);
				put(CRA_0015, true);
				put(CRA_0016, true);
				put(CRA_0017, true);
				put(CRA_0018, true);
				put(CRA_0019, true);
				put(CRA_0020, true);
				put(CRA_0021, true);
				put(CRA_0022, true);
				put(CRA_0023, true);
				put(CRA_0024, true);
				put(CRA_0025, true);
				put(CRA_0026, true);
				put(CRA_0027, true);
				put(CRA_0028, true);
				put(CRA_0029, true);
				put(CRA_0030, true);
				put(CRA_0031, true);
				put(CRA_0032, true);
				put(CRA_0033, true);
				put(CRA_0034, true);
				put(CRA_0035, false);
				put(CRA_0036, true);
				put(CRA_0037, true);
				put(CRA_0038, true);
				put(CRA_0039, true);
				put(CRA_0040, true);
				put(CRA_0041, true);
				put(CRA_0042, true);
				put(CRA_0043, true);
				put(CRA_0044, true);
				put(CRA_0045, true);
				put(CRA_0046, true);
				put(CRA_0047, true);
				put(CRA_0048, true);
				put(CRA_0049, false);
				put(CRA_0050, true);
				put(CRA_0051, true);
				put(CRA_0052, true);
				put(CRA_0053, true);
				put(CRA_0054, true);
				put(CRA_0055, false);
				put(CRA_0056, true);
				put(CRA_0057, true); // CRA_0002
				put(CRA_0058, true); // CRA_0002
				put(CRA_0059, true); // CRA_0006
				put(CRA_0060, true); // CRA_0006
				put(CRA_0061, true); // CRA_0032
			}
		};
		//@formatter:on

		//@formatter:off
		static Map<Type, Boolean> BBCC_EXCLUDED = new HashMap<Type, Boolean>() {
			{
				put(CRA_0000, true);
				put(CRA_0001, false);
				put(CRA_0002, false);
				put(CRA_0003, false);
				put(CRA_0004, false);
				put(CRA_0005, false);
				put(CRA_0006, false);
				put(CRA_0007, false);
				put(CRA_0008, false);
				put(CRA_0009, false);
				put(CRA_0010, false);
				put(CRA_0011, false);
				put(CRA_0012, false);
				put(CRA_0013, false);
				put(CRA_0014, true);
				put(CRA_0015, true);
				put(CRA_0016, false);
				put(CRA_0017, false);
				put(CRA_0018, false);
				put(CRA_0019, true);
				put(CRA_0020, true);
				put(CRA_0021, false);
				put(CRA_0022, false);
				put(CRA_0023, false);
				put(CRA_0024, false);
				put(CRA_0025, false);
				put(CRA_0026, false);
				put(CRA_0027, false);
				put(CRA_0028, false);
				put(CRA_0029, true);
				put(CRA_0030, false);
				put(CRA_0031, false);
				put(CRA_0032, false);
				put(CRA_0033, false);
				put(CRA_0034, false);
				put(CRA_0035, true);
				put(CRA_0036, false);
				put(CRA_0037, false);
				put(CRA_0038, false);
				put(CRA_0039, false);
				put(CRA_0040, false);
				put(CRA_0041, false);
				put(CRA_0042, true);
				put(CRA_0043, true);
				put(CRA_0044, true);
				put(CRA_0045, true);
				put(CRA_0046, true);
				put(CRA_0047, true);
				put(CRA_0048, true);
				put(CRA_0049, true);
				put(CRA_0050, true);
				put(CRA_0051, true);
				put(CRA_0052, true);
				put(CRA_0053, true);
				put(CRA_0054, true);
				put(CRA_0055, true);
				put(CRA_0056, false);
				put(CRA_0057, false); // CRA_0002
				put(CRA_0058, false); // CRA_0002
				put(CRA_0059, false); // CRA_0006
				put(CRA_0060, false); // CRA_0006
				put(CRA_0061, false); // CRA_0032
			}
		};
		//@formatter:on

		public static Type DEFAULT = CRA_0001;


		public static Type getByCode(int code) {
			for (Type type : values())
				if ( type.getCode() == code )
					return type;
			return null;
		}
	}
	
	public Double getIrpf() {
		return irpf;
	}
	
	public void setIrpf(Double irpf) {
		this.irpf = irpf;
	}

	public String getIrpfExpression() {
		return irpfExpression;
	}

	public void setIrpfExpression(String irpfExpression) {
		this.irpfExpression = irpfExpression;
	}
	
	public Double getQuote() {
		return quote;
	}
	
	public void setQuote(Double quote) {
		this.quote = quote;
	}

	public String getQuoteExpression() {
		return quoteExpression;
	}

	public void setQuoteExpression(String quoteExpression) {
		this.quoteExpression = quoteExpression;
	}

	@Override
	public int getCode() {
		return null == type ? 1 : this.type.getCode();
	}

	@Override
	public String getCodeDescription() {
		return null == type ? "01 RETRIBUCION NO INCLUIDA OTROS APARTADOS" : this.type.getDescription2();
	}
	
	@Override
	public Long getStartDateTime() {
		return startDate.getTime();
	}
	
	@Override
	public Long getEndDateTime() {
		return endDate.getTime();
	}

	@Override
	public String toString() {
		return "Payment [irpf=" + irpf + ", quote=" + quote + ", irpfExpression=" + irpfExpression
				+ ", quoteExpression=" + quoteExpression + ", type=" + type + ", id=" + id + ", month=" + month
				+ ", scope=" + scope + ", startDate=" + startDate + ", endDate=" + endDate + ", amount=" + amount
				+ ", name=" + name + ", expression=" + expression + ", description=" + description + ", salaryType="
				+ salaryType + ", dbAmount=" + dbAmount + ", conceptId=" + conceptId + ", descriptionTemplate="
				+ descriptionTemplate + ", domain=" + domain + ", defined=" + Arrays.toString(defined) + "]";
	}
	

	
}