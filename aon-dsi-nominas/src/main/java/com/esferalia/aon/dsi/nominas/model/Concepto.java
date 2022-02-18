package com.esferalia.aon.dsi.nominas.model;

import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Concepto {
	
	private String clave;
	private String nombre;
	private String ss;
	private String irpf;
	private String pag;
	private String enf;
	private String acc;
	private String tipo;
	private String claveCRA;
	private double importe;
	private String cobro;
	private double unidades;
	
	public String getClave() {
		return clave;
	}
	public Concepto setClave(String clave) {
		this.clave = clave;
		return this;
	}
	public String getNombre() {
		return nombre;
	}
	public Concepto setNombre(String nombre) {
		this.nombre = nombre;
		return this;
	}
	public String getSs() {
		return ss;
	}
	public Concepto setSs(String ss) {
		this.ss = ss;
		return this;
	}
	public String getIrpf() {
		return irpf;
	}
	public Concepto setIrpf(String irpf) {
		this.irpf = irpf;
		return this;
	}
	public String getPag() {
		return pag;
	}
	public Concepto setPag(String pag) {
		this.pag = pag;
		return this;
	}
	public String getEnf() {
		return enf;
	}
	public Concepto setEnf(String enf) {
		this.enf = enf;
		return this;
	}
	public String getAcc() {
		return acc;
	}
	public Concepto setAcc(String acc) {
		this.acc = acc;
		return this;
	}
	public String getTipo() {
		return tipo;
	}
	public Concepto setTipo(String tipo) {
		this.tipo = AonStringUtils.trimToEmpty(tipo);
		return this;
	}
	public String getClaveCRA() {
		return claveCRA;
	}
	public Concepto setClaveCRA(String claveCRA) {
		this.claveCRA = claveCRA;
		return this;
	}	
	public double getImporte() {
		return importe;
	}
	public Concepto setImporte(double importe) {
		this.importe = importe;
		return this;
	}
	public String getCobro() {
		return AonStringUtils.trimToEmpty(cobro);
	}
	public Concepto setCobro(String cobro) {
		this.cobro = AonStringUtils.trimToEmpty(cobro);
		return this;
	}
	public double getUnidades() {
		return unidades;
	}
	public Concepto setUnidades(double unidades) {
		this.unidades = unidades;
		return this;
	}	
	
	// Codigo del concepto en AON para payment_concept
	public String getAonCode() {

		String code = "";
		switch (this.clave) {
		case "01":  // Salario base (clave 01 en Omega)
			code = "SALARIO_BASE";
			break;
		case "02":  // Antiguedad (clave 02 en Omega)
			code = "PLUS_ANTIGUEDAD";  // Se pone así para no confundir con la funcion ANTIGÜEDAD de AON
			break;
		case "75":  // Bruto o Liquido (clave 75 en Omega)
			if ("B".equals(this.tipo))
				code = "AJUSTE_BRUTO";
			else code = "AJUSTE_NETO";
			break;
		default:
			code = AonStringUtils.trimToEmpty(this.nombre).toUpperCase().replaceAll("\\W", "_").replaceAll("__", "_"); 
			// Comprobar que no sea una variable del sistema, pues no se puede usar como CODE una 
			// variable del sistema, pues traería problemas si luego se usa el CODE en otro concepto
			if (ContextVariable.isContextVariable(code)) {
				code = "PLUS_"+code;
			}
			break;
		}
		if (code.endsWith("_")) {
			code = code.substring(0, code.length()-1);			
		}			
		return code;	
	}
	
	// Nombre de la variable que se utilizara en la expresion del concepto de AON
	public String getAonVariable() {
		return "IMP_" + getAonCode() + (getCobro().isBlank() || ("75".equals(this.clave) && "F".equals(getCobro())) ? "" : "_" + getCobro()) + (("S".equals(this.ss) || esDescuento()) ? "" : "_NC");
	}
	
	// Expresión que se utilizará en el concepto de AON	 
	public String getAonExpression() {
		String expression = "";
		switch (getCobro()) {
			case "M":  // Mensual
				expression = getAonVariable() + " * DIAS_TRABAJADOS / DIAS_MES";
				break;
			case "D":  // Diario 
				expression = getAonVariable() + " * DIAS_TRABAJADOS";
				break;
			case "F":  // Fijo
				if (this.tipo.equals("B"))
					expression = "BRUTO(" + getAonVariable() + ")";
				else if (this.tipo.equals("L"))
						expression = "NETO(" + getAonVariable() + ")";
				else expression = "FRACCIONAR(" + getAonVariable() + ")"; // Si no ponemos FRACCIONAR, el importe no se trata como fijo y aplica la proporcion si hay absentismo o no es alta o baja a lo largo del mes
				break;
			case "T":  // Proporcional al periodo trabajado (Bruto o Liquido)
				if (this.tipo.equals("B"))
					expression = "BRUTO( " + getAonVariable() + " * DIAS_TRABAJADOS / DIAS_MES )";
				else if (this.tipo.equals("L"))
						expression = "NETO( " + getAonVariable() + " * DIAS_TRABAJADOS / DIAS_MES )";				
				break;
			case "V": // Laborable (Lunes a Viernes)
				expression = getAonVariable() + " * DIAS_LABORABLES";
				break;
			case "S": // Laborable incluyendo Sábados
				expression = getAonVariable() + " * (DIAS_LABORABLES + DIAS_SABADO)";
				break;
			case "H": // Por Horas
				// Si pongo HORAS_TRABAJADAS, esa variable la calcula AON automáticamente, según 
				// los DIAS_TRABAJADOS y no se puede modificar, así que le pongo una variable que 
				// no exista en AON, para que la introduzcan manualmente, como se hace en Omega
				expression = getAonVariable() + " * HORAS_MES";
				break;
			case "P": // Porcentaje sobre el Salario (Sal.Base+Antigüedad)
				if ("02".equals(this.clave))
					expression = getAonVariable() + " * SALARIO_BASE / 100";
				else expression = getAonVariable() + " * (SALARIO_BASE + PLUS_ANTIGUEDAD) / 100"; 
				break;
			case "U": // Importe Unitario
				expression = getAonVariable() + " * UNIDADES_" + getAonCode();
				break;
			default:
				if ("75".equals(this.clave)) {
					if (this.tipo.equals("B"))
						expression = "BRUTO(" + getAonVariable() + ")";
					else expression = "NETO(" + getAonVariable() + ")";					
				}
				else expression = getAonVariable();
				break;
		}
		return expression;
	}
	
	// Expresion que se guarda en IRPF_EXPRESSION del concepto de AON 
	// "_P" si IRPF = "S", en caso contrario cadena vacia
	public String getAonIrpfExpression() {
		return "S".equals(this.irpf) ? "_P" : "";		
	}
	
	// Expresion que se guarda en QUOTE_EXPRESSION del concepto de AON 
	// "_P" si SS = "S", en caso contrario cadena vacia
	public String getAonQuoteExpression() {
		return "S".equals(this.ss) ? "_P" : "";		
	}
	
	// Clave CRA en formato numérico (si está vacia devuelve 1)
	public byte getAonClaveCRA() {
		if (AonStringUtils.isBlank(this.claveCRA)) 
			return 1;
		else return Byte.parseByte(this.claveCRA);
	}
	
	// Descripción del concepto salarial en AON: [clave] + nombre
	public String getAonDescription() {
		return "[" + this.clave + "] " + this.nombre;		
	}
	
	// Devuelve si el concepto lleva una clave de descuentos en Omega (Claves 70,71,80,81,90,91)
	public boolean esDescuento() {
		
		// Claves de conceptos de Omega para los descuentos
		String DESCUENTOS_OMEGA = "70;71;80;81;90;91"; 
		
		return DESCUENTOS_OMEGA.contains(this.clave);
		
	}
	
	@Override
	public String toString() {
		return "Concepto [clave=" + clave + ", nombre=" + nombre + ", ss=" + ss + ", irpf=" + irpf + ", pag=" + pag
				+ ", enf=" + enf + ", acc=" + acc + ", tipo=" + tipo + ", claveCRA=" + claveCRA + ", importe=" + importe
				+ ", cobro=" + cobro + ", unidades=" + unidades + "]";
	}

}
