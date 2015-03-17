package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.Mod140Context;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.impl.jooq.dao.Mod140DAO.AonFiscalFileUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Record5Bizkaia2015 implements Serializable,IMod140Record {
	 TIPO_DE_REGISTRO (
			 (writer,ctx,invoice,vatIdx) -> {writer.append('5');}
			 )
	,MODELO_DOCUMENTO(
			 (writer,ctx,invoice,vatIdx) -> {writer.append(MODEL);}
			 )
	,EJERCICIO(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( 
					 Integer.toString(AonDateUtils.getYear( invoice.getIssueDate())));}
			 )
	,NIF_DECLARANTE(
			 (writer,ctx,invoice,vatIdx) -> {writer.append(AonFiscalFileUtils.text(ctx.getDocument(), 9));}
			 )
	,N_ANOTACION(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat('0', 6));}
			 )
	,EPIGRAFE(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonFiscalFileUtils.text(invoice.getEpigraph(), 7));}
			 )
	,IDENTIFICACION_DE_LA_ACTIVIDAD(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat('0', 2));}
			 )
	,TIPO_DE_OPERACION(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( 'A' );}
			 )
	,TIPO_DE_REGISTRO2(
			 (writer,ctx,invoice,vatIdx) -> {
				 if (invoice.getRectificationType() == RectificationType.NONE) {
					 if (invoice.getInvoiceVATs() != null && invoice.getInvoiceVATs().size() > 1) {
						 writer.append(  'B' );
					 } else {
						 writer.append(  'A' );
					 }
				 } else {
					 writer.append( 'E' );
				 }
				 }
			 )
	,OPERACION_ESPECIAL(
			 (writer,ctx,invoice,vatIdx) -> {
				 if (invoice.getTransaction() == InvoiceTransactionType.OTHER_ISP) {
					 writer.append(  'I' );
				 } else {
					 writer.append( '-' );
				 }
			 }
			 )
	,FACTURA_SIMPLIFICADA(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( ' ' );}
			 )
	,TIPO_DE_DESTINATARIO(
			 (writer,ctx,invoice,vatIdx) -> {
				 if (AonStringUtils.isBlank(invoice.getRegistryDocument())) {
					 writer.append( 'A' );
				 } else {
					 writer.append( 'B' );
				 }
			 }
			 )
	,RECTIFICACION_ANOTACION_REGISTRAL(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( ' ' );}
			 )
	,CLAVE_NÚMERO_DE_IDENTIFICACION_EN_EL_PAÍS_DE_RESIDENCIA(
			 (writer,ctx,invoice,vatIdx) -> {
				 if (invoice.getRegistryDocumentCountry() == null || invoice.getRegistryDocumentCountry() == Country.ES) {
					 if (invoice.getRegistryDocumentType() == DocumentType.CIF 
							 || invoice.getRegistryDocumentType() == DocumentType.NIE
							 || invoice.getRegistryDocumentType() == DocumentType.NIF ) {
						 writer.append( '1' );
					 } else if (invoice.getRegistryDocumentType() == DocumentType.PASSPORT) {
						 writer.append( '3' );
					 } else {
						 writer.append( '6' );
					 }
				 } else if (invoice.getRegistryDocumentCountry().isIntracommunityCountry()) {
					 writer.append( '2' );
				 } else {
					 writer.append( '6' );
				 }
			 }
			 )
	,NIF_DECLARADO(
			 (writer,ctx,invoice,vatIdx) -> {
				 if (invoice.getRegistryDocumentCountry() == null || invoice.getRegistryDocumentCountry() == Country.ES) {
					 writer.append(AonFiscalFileUtils.text(invoice.getRegistryDocument(),9));
				 } else {
					 writer.append( AonStringUtils.repeat(' ',9 ));		 
				 }
			 }
			 )
	,NIF_REPRESENTANTE_LEGAL(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat(' ',9 ));}
			 )
	,CODIGO_PAÍS(
			 (writer,ctx,invoice,vatIdx) -> {
				if (invoice.getRegistryDocumentCountry() == null) {
					writer.append( Country.ES.getIso2() );					
				} else {
					writer.append( invoice.getRegistryDocumentCountry().getIso2() );
				}
			 }
			 )
	,NÚMERO_DE_IDENTIFICACION_FISCAL_EN_EL_PAÍS_DE_RESIDENCIA(
			 (writer,ctx,invoice,vatIdx) -> {
				 if (invoice.getRegistryDocumentCountry()!=null && invoice.getRegistryDocumentCountry() != Country.ES) {
					 writer.append(AonFiscalFileUtils.text(invoice.getRegistryDocument(),20));
				 } else {
					 writer.append( AonStringUtils.repeat(' ',20 ));
				 }
			 }
			 )
	,APELLIDOS_Y_NOMBRE_O_DENOMINACION_SOCIAL_DECLARADO(
			 (writer,ctx,invoice,vatIdx) -> {
				 writer.append(AonFiscalFileUtils.text(invoice.getRegistryName(),40));
			 }
			 )
	,IDENTIFICACION_DE_LA_FACTURA(
			 (writer,ctx,invoice,vatIdx) -> {
				 writer.append(AonFiscalFileUtils.text(invoice.getDocumentNumber(),40));
			 }
			 )
	,FECHA_DE_EXPEDICION(
			 (writer,ctx,invoice,vatIdx) -> {
				 writer.append(AonFiscalFileUtils.date(invoice.getIssueDate()));
			 }
			 )
	,FECHA_DE_LA_OPERACION(
			 (writer,ctx,invoice,vatIdx) -> {
				 writer.append(AonFiscalFileUtils.date(invoice.getTaxDate()));
			 }
			 )
	,FECHA_DE_LA_RECEPCION(
			 (writer,ctx,invoice,vatIdx) -> {
				 writer.append(AonFiscalFileUtils.date(invoice.getTaxDate()));
			 }
			 )
	 
	,NÚMERO_DE_ANOTACION_DE_OPERACIONES_YA_REGISTRADAS(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat('0', 6));}
			 )
	,FECHA_ANOTACION_DE_OPERACIONES_YA_REGISTRADAS(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat('0', 8));}
			 )
	,N_FACTURA_INICIAL(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat(' ', 20));}
			 )
	,N_FACTURA_FINAL(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat(' ', 20));}
			 )
	,NÚMERO_DE_FACTURAS_ACUMULADAS(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat('0', 8));}
			 )
	,NO_CUENTA_PGC(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat(' ', 3));}
			 )
	,REFERENCIA_DEL_BIEN(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat(' ', 10));}
			 )
	,IMPORTE_GASTO (
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonFiscalFileUtils.signed( invoice.getRetentionQuota(), 13 ));}
			 )
	,CRITERIO_PAGO(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( 'N' );}
			 )
	,IMPORTE_NO_PAGADO(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonFiscalFileUtils.signed( 0.0, 13 ));}
			 )
	,GASTO_A_COMPUTAR(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonFiscalFileUtils.signed( invoice.getRetentionQuota(), 13 ));}
			 )
	,BASE_IMPONIBLE(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonFiscalFileUtils.signed( invoice.getInvoiceVATs().get(vatIdx).getBase(), 13 ));}
			 )
	,TIPO_IMPOSITIVO(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonFiscalFileUtils.unsigned( invoice.getInvoiceVATs().get(vatIdx).getPercentage(), 5 ));}
			 )
	,CUOTA_DEL_IMPUESTO(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonFiscalFileUtils.signed( invoice.getInvoiceVATs().get(vatIdx).getQuota(), 13 ));}
			 )
	,IMPORTE_TOTAL_DE_LA_FACTURA(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonFiscalFileUtils.signed( invoice.getTotal(), 13 ));}
			 )
	,CUOTA_NO_DEDUCIBLE(
			 (writer,ctx,invoice,vatIdx) -> {
				 double quota = invoice.getInvoiceVATs().get(vatIdx).getQuota();
				 double deductibleQuota = invoice.getInvoiceVATs().get(vatIdx).getDeductibleQuota();
				 double result = AonMathUtils.round( quota - deductibleQuota);
				 writer.append( AonFiscalFileUtils.signed( result, 13 ));
				 }
			 )
	,CUOTA_DEDUCIBLE(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonFiscalFileUtils.signed( invoice.getInvoiceVATs().get(vatIdx).getDeductibleQuota(), 13 ));}
			 )
	,CRITERIO_CAJA(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( invoice.isVatAccrualPayment()?'X':' ' );}
			 )
	,IMPORTE_DEVENGADO(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonFiscalFileUtils.signed( 0.0, 13 ));}
			 )
	,FECHA_PAGO(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat('0',8 ));}
			 )
	,MEDIO_PAGO(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( ' ');}
			 )
	,DESCRIPCION_MEDIO(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat(' ',20 ));}
			 )
	,IDENTIFICACION_DE_LA_FACTURA_RECTIFICADA(
			(writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat(' ',20 ));}
			 )
	,FECHA_EXPEDICION_RECTIFICADA(
			(writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat('0',8 ));}
			 )
	,NO_ANOTACION_RECTIFICADA(
			(writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat('0',6 ));}
			 )
	,FILLER(
			(writer,ctx,invoice,vatIdx) -> {writer.append( AonStringUtils.repeat(' ',46 ));}
			 )
	,CRLF(
			 (writer,ctx,invoice,vatIdx) -> {writer.append( END_LINE);}
			 )
	;
	 
	 private IRecordFiller filler;
	 
	 private Record5Bizkaia2015(IRecordFiller filler) {
		 this.filler = filler;
	 }
	 
	public IRecordFiller getFiller() {
		return filler;
	}

	public static void fill(Writer writer, Mod140Context ctx,
			Invoice invoice) throws IOException {
		if (invoice.getInvoiceVATs() == null) {
			invoice.ensureInvoiceVAT(0, 0);
		}
		for (int i = 0; i < invoice.getInvoiceVATs().size(); i++) {
			for (Record5Bizkaia2015 item : Record5Bizkaia2015.values()) {
				item.filler.fill(writer, ctx, invoice, i);
			}
		}
	}
}
