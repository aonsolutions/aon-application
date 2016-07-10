package com.esferalia.aon.file.seres.util.writer.udapa;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Invoice;
import com.esferalia.aon.file.seres.udapa.UdapaInvoice;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCC;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCD;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCE;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCI;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCL;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCT;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCU;
import com.esferalia.aon.file.seres.udapa.invoice.data.SINCV;

public class UdapaSaleInvoiceWriter {
	
	public static final String CHARSET_ENCODING = "ISO-8859-1";
	
	public FileOutput createFile(Invoice invoice) throws FileNotFoundException, UnsupportedEncodingException {
		SINCC sincc = createSINCCRecord( invoice );
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		FileFiller filler = new UdapaInvoice(sincc, writer);
		FileOutput output = new FileOutput();
		output.setErrors(filler.create());
		output.setContent(outputStream.toString().getBytes(CHARSET_ENCODING));
		return output;
	}


	private SINCC createSINCCRecord(Invoice invoice) {
		// TODO Auto-generated method stub
		SINCC sincc = new SINCC();
		
		sincc.setTipoFactura_325_380_381_383_385_(null);
		sincc.setNumeroDeFactura(null);
		sincc.setCodigoVendedor_aQuienSePide__SU_(null);
		sincc.setCodigoComprador_QuienPide__BY_(null);
		sincc.setFuncionDelMensaje_7_31_5_(null);
		sincc.setFechaFactura(null);
		sincc.setPeriodoDeFacturacion(null);
		sincc.setFormaDePago(null);
		sincc.setCodigoEmisorDeLaFactura_QuienFactura__II_(null);
		sincc.setCodigoReceptorDeLaFactura_aQuienSeFactura_(null);
		sincc.setCodigoReceptorDeLasMercancias_QuienRecibe_(null);
		sincc.setCodigoReceptorDelPago_aQuienSePaga_(null);
		sincc.setCodigoEmisorDelPago_QuienPaga_(null);
		sincc.setRazonDelCargoODelAbono(null);
		sincc.setNumeroDePedido_ON_(null);
		sincc.setNumeroDeAlbaran_DQ_(null);
		sincc.setCalificadorDocumentoRectificado_Sustituido(null);
		sincc.setNumeroDocumentoRectificado_Sustituido(null);
		sincc.setNumeroDeContrato_Acuerdo_CT_(null);
		sincc.setNumeroDeRelacionDeEntregas_REN_(null);
		sincc.setRazonSocialReceptorDeLaFactura(null);
		sincc.setNombre_NumeroDeLaCalleDelReceptorDeLaFactura(null);
		sincc.setPoblacionDelReceptorDeLaFactura(null);
		sincc.setCodigoPostalDelReceptorDeLaFactura(null);
		sincc.setNifDelReceptorDeLaFactura(null);
		sincc.setNombre_NumeroDeLaCalleDelEmisorDeLaFactura(null);
		sincc.setPoblacionDelEmisorDeLaFactura(null);
		sincc.setCodigoPostalDelEmisorDeLaFactura(null);
		sincc.setCodigoDeMoneda(null);
		sincc.setFechaVencimientoUnico(null);
		sincc.setImporteNetoTotalFactura_79_(null);
		sincc.setBaseImponible_125_(null);
		sincc.setImporteBrutoTotalFactura_98_(null);
		sincc.setImporteTotalDeImpuestos_176_(null);
		sincc.setImporteTotalAPagar_139_(null);
		sincc.setSubvencionesVinculadasAlPrecio_80A_(null);
		sincc.setTotalIncrementosDelImporteBruto_259_(null);
		sincc.setTotalMinoracionesDelImporteBruto_260_(null);
		sincc.setIdentificacionAdicionalDeLaParte_API_(null);
		sincc.setReceptorDelDocumento(null);
		sincc.setIdentificacionAdicionalProveedor_API__NAD_SU_(null);


		sincc.sinctList = createSINCTList(invoice);
		sincc.sincvList = createSINCVList(invoice);
		sincc.sincdList = createSINCDList(invoice);
		sincc.sinclList = createSINCLList(invoice); 
		sincc.sincuList = createSINCUList(invoice);
		sincc.sinceList = createSINCEList(invoice);
		sincc.sinciList = createSINCIList(invoice);
		
		return sincc;
	}


	private List<SINCT> createSINCTList(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<SINCV> createSINCVList(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<SINCD> createSINCDList(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<SINCL> createSINCLList(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<SINCU> createSINCUList(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<SINCE> createSINCEList(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<SINCI> createSINCIList(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}

	

	private List<SINCT> createSINCTRecord(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<SINCV> createSINCVRecord(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<SINCD> createSINCDRecord(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<SINCL> createSINCLRecord(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<SINCU> createSINCURecord(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<SINCE> createSINCERecord(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}


	private List<SINCI> createSINCIRecord(Invoice invoice) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
