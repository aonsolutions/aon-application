package com.esferalia.aon.in.payroll.tgss.cra;

import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.esferalia.aon.watson.util.AonStringUtils;

public class CRAParser {
	
	public static class CtaCot {
		
	    protected String regimen;
	    protected String provincia;
	    protected String numero;
	    
	    public String getRegimen() {
			return regimen;
		}
	    
	    public String getProvincia() {
			return provincia;
		}
	    
	    
	    public String getNumero() {
			return numero;
		}
	}
	
	public static class Periodo {

	    protected String mes;
	    protected String anho;
	    
	    public String getMes() {
			return mes;
		}
	    
	    public String getAnho() {
			return anho;
		}
	}
	
	public static class ConceptoRetributivo {

	    protected String codigo;
	    protected String indicadorExcluidoIncluido;
	    protected String importe;
	    protected String indicadorTipoActuacion;

	    public String getCodigo() {
			return codigo;
		}
	    
	    public String getImporte() {
			return importe;
		}
	    
	    public String getIndicadorTipoActuacion() {
			return indicadorTipoActuacion;
		}

	    public String getIndicadorExcluidoIncluido() {
			return indicadorExcluidoIncluido;
		}
	  
	}
	
	public static class Trabajador {
		private String naf; 
		
		private Stack<ConceptoRetributivo> conceptosRetributivos = new Stack<ConceptoRetributivo>();
		
		public String getNaf() {
			return naf;
		}
		

		public Stack<ConceptoRetributivo> getConceptosRetributivos() {
			return conceptosRetributivos;
		}
		
		void pushConceptoRetributivo(ConceptoRetributivo conceptoRetributivo){
			conceptosRetributivos.push(conceptoRetributivo);
		}
		
	}
	
	public static class Liquidacion {
		private CtaCot ccc;
		private CtaCot cccConcertado;
		private Periodo periodoLiquidacion;	
		
		private Stack<Trabajador> trabajadores = new Stack<Trabajador>();
		
		public CtaCot getCcc() {
			return ccc;
		}
		
		public CtaCot getCccConcertado() {
			return cccConcertado;
		}
		
		public Periodo getPeriodoLiquidacion() {
			return periodoLiquidacion;
		}
		
		public Stack<Trabajador> getTrabajadores() {
			return trabajadores;
		}
		
		void pushTrabajador(Trabajador trabajador) {
			trabajadores.push(trabajador);
		}
		
		Trabajador peekTrabajador() {
			return trabajadores.peek();
		}
	}
	
	public static class CRA {
		
		private Stack<Liquidacion> liquidaciones = new Stack<Liquidacion>();  
		
		public Stack<Liquidacion> getLiquidaciones() {
			return liquidaciones;
		}
		
		private Liquidacion peekLiquidacion() {
			return liquidaciones.peek();
		}
		
		private void pushLiquidacion(Liquidacion liquidacion) {
			liquidaciones.push(liquidacion);
		}		
	}
	
	
	public static CRA parse(Reader reader) throws CRAException {
		return parse(new LineNumberReader(reader));
		
	}
	
	public static CRA parse(LineNumberReader reader) throws CRAException {
		CRA cra = new CRA();
		
		reader.lines()
		.filter(AonStringUtils::isNotBlank)
		.forEach(l -> parseLine(cra, l));
		
		return cra;
	}
	
	// ------------------------------------------------------------------------

	private static void parseLine(CRA cra, String line) throws CRAException {
		String head = AonStringUtils.substring(line, 0, 3);
		switch (head) {
		case "ETI": 
			parseETI(cra, line);
			break;
		case "DDE":
			parseDDE(cra, line);
			break;
		case "TRB":
			parseTRB(cra, line);
			break;
		case "CRE":
			parseCRE(cra, line);
			break;
		default:
			throw new CRAException();
		}
		
	}
	
	
	private static void parseETI(CRA cra, String line) {
		
	}

	private static void parseDDE(CRA cra, String line) {
		
		Liquidacion liquidacion = new Liquidacion();
		
		liquidacion.ccc = new CtaCot();
		liquidacion.ccc.regimen = AonStringUtils.substring(line, 3, 7);
		liquidacion.ccc.provincia = AonStringUtils.substring(line, 7, 9 );
		liquidacion.ccc.numero = AonStringUtils.substring(line, 9, 18);
		
		liquidacion.periodoLiquidacion = new Periodo();
		liquidacion.periodoLiquidacion.anho = AonStringUtils.substring(line, 18, 22);
		liquidacion.periodoLiquidacion.mes = AonStringUtils.substring(line, 22, 24);
		
		liquidacion.cccConcertado = new CtaCot();
		liquidacion.cccConcertado.regimen = AonStringUtils.substring(line, 24, 28);
		liquidacion.cccConcertado.provincia = AonStringUtils.substring(line, 28, 30 );
		liquidacion.cccConcertado.numero = AonStringUtils.substring(line, 30, 39);
		
		cra.pushLiquidacion(liquidacion);

		//System.out.printf("%s%s%s\r\n", ctaCot.regimen, ctaCot.provincia, ctaCot.numero);
		
	}
 
	private static void parseTRB(CRA cra, String line) {
		Trabajador trabajador = new Trabajador();
		trabajador.naf = AonStringUtils.substring(line, 3, 15);
		
		cra.peekLiquidacion().pushTrabajador(trabajador);
		
	}

	private static void parseCRE(CRA cra, String line) {
		ConceptoRetributivo conceptoRetributivo = new ConceptoRetributivo();
		conceptoRetributivo.codigo =  AonStringUtils.substring(line, 3, 7);
		conceptoRetributivo.indicadorExcluidoIncluido =  AonStringUtils.substring(line, 7, 8);
		conceptoRetributivo.importe =  AonStringUtils.substring(line, 8, 17);
		conceptoRetributivo.indicadorTipoActuacion =  AonStringUtils.substring(line, 17, 18);
		
		cra.peekLiquidacion().peekTrabajador().pushConceptoRetributivo(conceptoRetributivo);
	}
	

}
