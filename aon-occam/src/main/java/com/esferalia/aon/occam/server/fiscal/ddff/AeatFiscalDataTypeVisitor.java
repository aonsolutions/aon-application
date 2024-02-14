package com.esferalia.aon.occam.server.fiscal.ddff;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.model.ddff.AeatComunidadAutonoma;
import com.esferalia.aon.occam.api.model.ddff.AeatCotizacionAutonomo;
import com.esferalia.aon.occam.api.model.ddff.AeatDatosGenerales;
import com.esferalia.aon.occam.api.model.ddff.AeatDiscapacidad;
import com.esferalia.aon.occam.api.model.ddff.AeatDomicilio;
import com.esferalia.aon.occam.api.model.ddff.AeatEstadoCivil;
import com.esferalia.aon.occam.api.model.ddff.AeatRegCotizacion;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalDataType.AeatFiscalDataTypeContext;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalDataType.IAeatFiscalDataTypeVisitor;
import com.esferalia.aon.occam.api.model.ddff.AeatSexo;
import com.esferalia.aon.occam.api.model.ddff.AeatTitular;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AeatFiscalDataTypeVisitor implements IAeatFiscalDataTypeVisitor {
	private static Logger LOGGER = Logger.getLogger(AeatFiscalDataTypeVisitor.class.getName());
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	
	private String getString(String line, int begin, int count ) {
		return AonStringUtils.substring(line, begin, (begin + count));
	}
	private Double getDouble(String line, int begin, int count ) {
		String i = AonStringUtils.substring(line, begin, (begin + count));
		if (AonStringUtils.isNumeric( i )) {
			return AonMathUtils.round(AonNumberUtils.toDouble( i ) / 100);
		}
		return null; 
	}
	private int getInt(String line, int begin, int count ) {
		String i = AonStringUtils.substring(line, begin, (begin + count));
		if (AonStringUtils.isNumeric( i )) {
			return AonNumberUtils.toint( i );
		}
		return -1; 
	}
	private Date getDate(String line, int begin, int count ) {
		String dateString = getString(line, begin, count);
		if ( AonStringUtils.equals(AonStringUtils.repeat("0",count),dateString)) {
			return null;
		}
		try {
			return dateFormatter.parse( dateString );
		} catch ( ParseException e) {
			LOGGER.log(Level.SEVERE,"Error en fecha de proceso {0}", dateString );
			return null;
		}
	}
	
	@Override 
	public void visitHEADER(AeatFiscalDataTypeContext ctx) {
		// Nothing to do
	}
	
	@Override 
	public void visitDATE(AeatFiscalDataTypeContext ctx) {
		ctx.getFiscalData().setDate( getDate(ctx.getLine(), 51, 8) );
	}
	
	/**
	 * Datos del titular
	 */
	@Override 
	public void visitDG(AeatFiscalDataTypeContext ctx) {
		ctx.getFiscalData().addDatosGenerales( new AeatDatosGenerales()
			.setEstadoCivil( AeatEstadoCivil.getByValue( getString(ctx.getLine(), 8, 1) ))
			.setConyugeNoResidente( "1".equals(getString(ctx.getLine(), 9, 1) ))
			.setConyugeNoResidenteUE( "1".equals(getString(ctx.getLine(), 10, 1) ))
		);
	}
	
	/**
	 * Datos del titular
	 */
	@Override 
	public void visitTI(AeatFiscalDataTypeContext ctx) {
		ctx.getFiscalData().addTitular( new AeatTitular()
			.setNif( getString(ctx.getLine(), 19, 9))
			.setApellidosNombre(getString(ctx.getLine(), 28, 60))
			.setDiscapacidadIRPF( AeatDiscapacidad.getByValue( getString(ctx.getLine(), 88, 1) )) 
			.setDiscapacidad990( AeatDiscapacidad.getByValue( getString(ctx.getLine(), 89, 1) ))
			.setFechaNacimiento(getDate(ctx.getLine(), 90,8))
			.setSexo( AeatSexo.getByValue( getString(ctx.getLine(), 98, 1) ))
			.setFechaFallecimiento(getDate(ctx.getLine(), 100,8))
			.setComunidadAutonoma(AeatComunidadAutonoma.getByValue( getString(ctx.getLine(), 108, 2) ))
			.setIBAN( getString(ctx.getLine(), 110, 34))
			.setSWIFT( getString(ctx.getLine(), 144, 11))
			.setFechaAdquisicionViviendaHabitual( getDate(ctx.getLine(), 155,8))
			.setNumeroPrestamoHipotecario( getString(ctx.getLine(), 163, 20))
			.setPorcentajePrestamo( getDouble(ctx.getLine(), 183, 5))
			.setDeduccionViviendaEjercicioAnterior( "1".equals(getString(ctx.getLine(), 188, 1) ))
			.setIglesiaCatolica( "1".equals(getString(ctx.getLine(), 189, 1) ))
			.setFinesSociales( "1".equals(getString(ctx.getLine(), 190, 1) ))
		);
		
	}
	
	/**
	 * Datos del domicilio
	 */
	@Override 
	public void visitDOM(AeatFiscalDataTypeContext ctx) {
		int domType = getInt(ctx.getLine(), 9, 2);
		if (domType == 20) {
			ctx.getFiscalData().addDomicilio( new AeatDomicilio()
				.setTipoVia( getString(ctx.getLine(), 11, 5))
				.setCodVia( getString(ctx.getLine(), 16, 5))
				.setNombreLargo( getString(ctx.getLine(), 21, 50))
				.setNombreCorto(getString(ctx.getLine(), 71, 25))
				.setNumeracion(getString(ctx.getLine(), 96, 3))
				.setNumero(getString(ctx.getLine(), 99, 5))
				.setCalificadorNumero(getString(ctx.getLine(), 104, 3))
				.setBloque(getString(ctx.getLine(), 107, 3))
				.setPortal(getString(ctx.getLine(), 110, 3))
				.setEscalera(getString(ctx.getLine(), 113,3))
				.setPlanta(getString(ctx.getLine(), 116,3))
				.setPuerta(getString(ctx.getLine(), 119,3))
				.setDatosComplementarios(getString(ctx.getLine(),122,40))	
				.setPoblacion(getString(ctx.getLine(), 162,30))
				.setCodigoPostal(getString(ctx.getLine(), 192,5))	
				.setCodigoMunicipio(getString(ctx.getLine(), 197,5))
				.setMunicipio(getString(ctx.getLine(), 202,30))
				.setCodigoProvincia(getString(ctx.getLine(), 232,2))
				.setProvincia(getString(ctx.getLine(), 234,20))
				.setReferenciaCatastral(getString(ctx.getLine(), 254,20))
				.setFechaModif(getDate(ctx.getLine(), 274,8))
			);
		}
	}
	
	/**
	 * Cotizaciones de autónomos
	 */
	@Override 
	public void visitCT(AeatFiscalDataTypeContext ctx) {
		ctx.getFiscalData().addCotizacionAutonomo( new AeatCotizacionAutonomo()
			.setNumeroAfiliacion(getString(ctx.getLine(), 8, 12 ))
			.setRegCotizacion( AeatRegCotizacion.getByValue( getString(ctx.getLine(), 20, 4) ))
			.setImporte( getDouble(ctx.getLine(), 24, 17 ))
		);
	}


	// ************************************************************************** [PENDIENTE]
	// 
	// *************************************************************************************
	@Override public void visitFA(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitDI(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitRTA(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitRTD(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitRTB(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitRTC(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitAA(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPP(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitCB(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitLT(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitREA(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitREG(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitRI(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitSG(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitCD(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitTV(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitAR(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitAT(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitAG(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitIP(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitSB(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitEAA(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitEAB(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitEAC(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitEAD(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitEAE(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitEAF(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitAMI(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitFIE(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitFIC(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitFID(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitDS(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitTVD(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitDNP(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitDN(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitCC(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPHD(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPHI(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPHM(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPF(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitUR(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitTPU(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitTPR(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitDM(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitDMG(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitDD(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitAD(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitFN(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitFM(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitCOD(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDA(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDB(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDC(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDD(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDE(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDF(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDG(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDH(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDI(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDJ(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDK(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDL(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPDX(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitCLP(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitRTN(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitCNR(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitGPP(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitCTP(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitPUE(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitRUE(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitRPT(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitAY(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitAGS(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitATR(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitIAP(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitDD1(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitDD2(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitDE(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitAS(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
	@Override public void visitEND(AeatFiscalDataTypeContext ctx) {/*Not implemented yet!*/}
}