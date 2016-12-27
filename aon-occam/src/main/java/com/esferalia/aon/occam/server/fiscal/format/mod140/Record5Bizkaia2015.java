package com.esferalia.aon.occam.server.fiscal.format.mod140;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod140;
import com.esferalia.aon.occam.api.model.fiscal.Mod140Context;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Record5Bizkaia2015 implements Serializable,IMod140Record {
	 TIPO_DE_REGISTRO (
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append('5');}
			 )
	,MODELO_DOCUMENTO(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append(MODEL);}
			 )
	,EJERCICIO(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( 
					 Integer.toString(AonDateUtils.getYear( invoice.getIssueDate())));}
			 )
	,NIF_DECLARANTE(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append(AonFiscalFileUtils.text(ctx.getDocument(), 9));}
			 )
	,N_ANOTACION(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat('0', 6));}
			 )
	,EPIGRAFE(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonFiscalFileUtils.text(invoice.getEpigraph(), 7));}
			 )
	,IDENTIFICACION_DE_LA_ACTIVIDAD(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append("01");}

			 )
	,TIPO_DE_OPERACION(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( 'A' );}
			 )
	,TIPO_DE_REGISTRO2(
			 (writer,ctx,invoice,account,vatIdx) -> {
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
			 (writer,ctx,invoice,account,vatIdx) -> {
				 if (invoice.getTransaction() == InvoiceTransactionType.OTHER_ISP) {
					 writer.append(  'I' );
				 } else {
					 writer.append( '-' );
				 }
			 }
			 )
	,FACTURA_SIMPLIFICADA(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( ' ' );}
			 )
	,TIPO_DE_DESTINATARIO(
			 (writer,ctx,invoice,account,vatIdx) -> {
				 if (AonStringUtils.isBlank(invoice.getRegistryDocument())) {
					 writer.append( 'A' );
				 } else {
					 writer.append( 'B' );
				 }
			 }
			 )
	,RECTIFICACION_ANOTACION_REGISTRAL(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( ' ' );}
			 )
	,CLAVE_NÚMERO_DE_IDENTIFICACION_EN_EL_PAÍS_DE_RESIDENCIA(
			 (writer,ctx,invoice,account,vatIdx) -> {
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
			 (writer,ctx,invoice,account,vatIdx) -> {
				 if (invoice.getRegistryDocumentCountry() == null || invoice.getRegistryDocumentCountry() == Country.ES) {
					 writer.append(AonFiscalFileUtils.text(invoice.getRegistryDocument(),9));
				 } else {
					 writer.append( AonStringUtils.repeat(' ',9 ));		 
				 }
			 }
			 )
	,NIF_REPRESENTANTE_LEGAL(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat(' ',9 ));}
			 )
	,CODIGO_PAÍS(
			 (writer,ctx,invoice,account,vatIdx) -> {
				if (invoice.getRegistryDocumentCountry() == null) {
					writer.append( Country.ES.getIso2() );					
				} else {
					writer.append( invoice.getRegistryDocumentCountry().getIso2() );
				}
			 }
			 )
	,NÚMERO_DE_IDENTIFICACION_FISCAL_EN_EL_PAÍS_DE_RESIDENCIA(
			 (writer,ctx,invoice,account,vatIdx) -> {
				 if (invoice.getRegistryDocumentCountry()!=null && invoice.getRegistryDocumentCountry() != Country.ES) {
					 writer.append(AonFiscalFileUtils.text(invoice.getRegistryDocument(),20));
				 } else {
					 writer.append( AonStringUtils.repeat(' ',20 ));
				 }
			 }
			 )
	,APELLIDOS_Y_NOMBRE_O_DENOMINACION_SOCIAL_DECLARADO(
			 (writer,ctx,invoice,account,vatIdx) -> {
				 writer.append(AonFiscalFileUtils.text(invoice.getRegistryName(),40));
			 }
			 )
	,IDENTIFICACION_DE_LA_FACTURA(
			 (writer,ctx,invoice,account,vatIdx) -> {
				 writer.append(AonFiscalFileUtils.text(invoice.getDocumentNumber(),40));
			 }
			 )
	,FECHA_DE_EXPEDICION(
			 (writer,ctx,invoice,account,vatIdx) -> {
				 writer.append(AonFiscalFileUtils.date(invoice.getIssueDate()));
			 }
			 )
	,FECHA_DE_LA_OPERACION(
			 (writer,ctx,invoice,account,vatIdx) -> {
				 writer.append(AonFiscalFileUtils.date(invoice.getTaxDate()));
			 }
			 )
	,FECHA_DE_LA_RECEPCION(
			 (writer,ctx,invoice,account,vatIdx) -> {
				 writer.append(AonFiscalFileUtils.date(invoice.getTaxDate()));
			 }
			 )
	 
	,NÚMERO_DE_ANOTACION_DE_OPERACIONES_YA_REGISTRADAS(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat('0', 6));}
			 )
	,FECHA_ANOTACION_DE_OPERACIONES_YA_REGISTRADAS(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat('0', 8));}
			 )
	,N_FACTURA_INICIAL(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat(' ', 20));}
			 )
	,N_FACTURA_FINAL(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat(' ', 20));}
			 )
	,NÚMERO_DE_FACTURAS_ACUMULADAS(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat('0', 8));}
			 )
	,NO_CUENTA_PGC(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append(AonFiscalFileUtils.text(account,3));}
			 )
	,REFERENCIA_DEL_BIEN(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat(' ', 10));}
			 )
	,IMPORTE_GASTO (
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonFiscalFileUtils.signedSpace( invoice.getInvoiceVATs().get(account).get(vatIdx).getBase(), 14 ));}
//			 (writer,ctx,invoice,account,vatIdx) -> {
//				 writer.append( AonFiscalFileUtils.signedSpace(
//					 invoice.getWithholdingData()!=null
//					 	?invoice.getWithholdingData().getBase()
//					 	:0
//					 , 14 ));}
			 )
	,CRITERIO_PAGO(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( 'N' );}
			 )
	,IMPORTE_NO_PAGADO(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonFiscalFileUtils.signedSpace( 0.0, 14 ));}
			 )
	,GASTO_A_COMPUTAR(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonFiscalFileUtils.signedSpace( invoice.getInvoiceVATs().get(account).get(vatIdx).getBase(), 14 ));}
//			 (writer,ctx,invoice,account,vatIdx) -> {
//				 writer.append( AonFiscalFileUtils.signedSpace(
//					 invoice.getWithholdingData()!=null
//					 	?invoice.getWithholdingData().getBase()
//					 	:0
//					 , 14 ));}
			 )
	,BASE_IMPONIBLE(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonFiscalFileUtils.signedSpace( invoice.getInvoiceVATs().get(account).get(vatIdx).getBase(), 14 ));}
			 )
	,TIPO_IMPOSITIVO(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonFiscalFileUtils.unsigned( invoice.getInvoiceVATs().get(account).get(vatIdx).getPercentage(), 5 ));}
			 )
	,CUOTA_DEL_IMPUESTO(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonFiscalFileUtils.signedSpace( invoice.getInvoiceVATs().get(account).get(vatIdx).getQuota(), 14 ));}
			 )
	,IMPORTE_TOTAL_DE_LA_FACTURA(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonFiscalFileUtils.signedSpace( invoice.getTotal(), 14 ));}
			 )
	,CUOTA_NO_DEDUCIBLE(
			 (writer,ctx,invoice,account,vatIdx) -> {
				 double quota = invoice.getInvoiceVATs().get(account).get(vatIdx).getQuota();
				 double deductibleQuota = invoice.getInvoiceVATs().get(account).get(vatIdx).getDeductibleQuota();
				 double result = AonMathUtils.round( quota - deductibleQuota);
				 writer.append( AonFiscalFileUtils.signedSpace( result, 14 ));
				 }
			 )
	,CUOTA_DEDUCIBLE(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonFiscalFileUtils.signedSpace( invoice.getInvoiceVATs().get(account).get(vatIdx).getDeductibleQuota(), 14 ));}
			 )
	,CRITERIO_CAJA(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( invoice.isVatAccrualPayment()?'S':'N' );}
			 )
	,IMPORTE_DEVENGADO(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonFiscalFileUtils.signedSpace( 0.0, 14 ));}
			 )
	,FECHA_PAGO(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat('0',8 ));}
			 )
	,MEDIO_PAGO(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( ' ');}
			 )
	,DESCRIPCION_MEDIO(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat(' ',20 ));}
			 )
	,IDENTIFICACION_DE_LA_FACTURA_RECTIFICADA(
			(writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat(' ',20 ));}
			 )
	,FECHA_EXPEDICION_RECTIFICADA(
			(writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat('0',8 ));}
			 )
	,NO_ANOTACION_RECTIFICADA(
			(writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat('0',6 ));}
			 )
	,FILLER(
			(writer,ctx,invoice,account,vatIdx) -> {writer.append( AonStringUtils.repeat(' ',46 ));}
			 )
	,CRLF(
			 (writer,ctx,invoice,account,vatIdx) -> {writer.append( END_LINE);}
			 )
	;
	 
	 private IRecordFiller filler;
	 
	 private Record5Bizkaia2015(IRecordFiller filler) {
		 this.filler = filler;
	 }
	 
	public IRecordFiller getFiller() {
		return filler;
	}

	public static void fill(Writer writer, Mod140Context ctx, Mod140 invoice) throws IOException {
		if (invoice.getInvoiceVATs() == null) {
			invoice.ensureInvoiceVAT(null,0, 0);
		}
		for (String account : invoice.getInvoiceVATs().keySet()) {
			for (int i = 0; i < invoice.getInvoiceVATs().get(account).size(); i++) {
				for (Record5Bizkaia2015 item : Record5Bizkaia2015.values()) {
					item.filler.fill(writer, ctx, invoice, account, i);
				}
			}
		}
	}
	
}
