package com.esferalia.aon.file.seres.util.writer.udapa;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.product.Item;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.esferalia.aon.file.seres.udapa.UdapaDelivery;
import com.esferalia.aon.file.seres.udapa.delivery.data.SEH1B;
import com.esferalia.aon.file.seres.udapa.delivery.data.SEH1C;
import com.esferalia.aon.file.seres.udapa.delivery.data.SEH1D;
import com.esferalia.aon.file.seres.udapa.delivery.data.SEH1G;
import com.esferalia.aon.file.seres.udapa.delivery.data.SEH1L;
import com.esferalia.aon.file.seres.udapa.delivery.data.SEH1P;

public class UdapaDeliveryWriter {
	
	public static final String CHARSET_ENCODING = "ISO-8859-1";
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	
	public FileOutput createFile(Delivery delivery, String companyEdiCode, String customerEdiCode) throws FileNotFoundException, UnsupportedEncodingException {
		SEH1C seh1c = createSEH1CRecord( delivery, companyEdiCode, customerEdiCode );
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		FileFiller filler = new UdapaDelivery(seh1c, writer);
		FileOutput output = new FileOutput();
		output.setErrors(filler.create());
		output.setContent(outputStream.toString().getBytes(CHARSET_ENCODING));
		return output;
	}
	
	private SEH1C createSEH1CRecord( Delivery delivery, String companyEdiCode, String customerEdiCode ) {
		// TODO Auto-generated method stub
		SEH1C seh1c = new SEH1C();
		
		seh1c.setCabecera("SEH1C");
		seh1c.setTipoAvisoDeExpedicion_351_35E_(SEH1C.V1001T.AVISO_DE_EXPEDICIO_351.getValue());
		seh1c.setNumeroAvisoDeExpedicion(delivery.getReferenceCode());
		seh1c.setCodigoEmisor_MS_(companyEdiCode);
		seh1c.setCodigoReceptor_MR_(customerEdiCode);
		seh1c.setFuncionDelMensaje(SEH1C.V1225F.ORIGINA_9.getValue());
		seh1c.setFechaDelDocumento_137__102_203_(dateFormat.format(delivery.getDate()));
		seh1c.setFechaEsperadaDeEntrega_17__102_203_(null);
		seh1c.setCalificadorFechaEntrega_2_11_PER_358_359__(null);
		seh1c.setFechaDeServicio1(null);
		seh1c.setHoraDeServicio1(null);
		seh1c.setFechaDeServicio2(null);
		seh1c.setHoraDeServicio2(null);
		seh1c.setInformacionAdicional(null);
		seh1c.setNumeroDePedido_ON_(null);
		seh1c.setFechaDePedido_171__102_203_(null);
		seh1c.setNumeroDeAlbaran_DQ_(null);
		seh1c.setFechaDeAlbaran_171__102_203_(null);
		seh1c.setCalificadorReferencia1(null);
		seh1c.setNumeroDeReferencia1(null);
		seh1c.setFechaDeReferencia1_102_203_(null);
		seh1c.setCalificadorReferencia2(null);
		seh1c.setNumeroDeReferencia2(null);
		seh1c.setFechaDeReferencia2_102_203_(null);
		seh1c.setMetodoPagoDeCostesDeTransporte(null);
		seh1c.setCodigoCondicionesDeEntrega(null);
		seh1c.setDescripcionCondicionesDeEntrega(null);
		seh1c.setModoDeTransporte(null);
		seh1c.setCodigoTransportista(delivery.getDriverDocument());
		seh1c.setNombreTransportista(delivery.getDriver());
		seh1c.setMatriculaDelVehiculo(delivery.getNumberPlate());
		
		seh1c.seh1dList = createSEH1DList(delivery);
		seh1c.seh1pList = createSEH1PList(delivery);
		seh1c.seh1lList = createSEH1LList(delivery, companyEdiCode, customerEdiCode);
		seh1c.seh1gList = createSEH1GList(delivery);
		seh1c.seh1bList = createSEH1BList(delivery);
		
		return seh1c;
	}

	// TODO createSEH1DList
	private List<SEH1D> createSEH1DList(Delivery delivery) {
		List<SEH1D> list = new ArrayList<>();
		return list;
	}

	// TODO createSEH1PList
	private List<SEH1P> createSEH1PList(Delivery delivery) {
		List<SEH1P> list = new ArrayList<>();
		return list;
	}

	private List<SEH1L> createSEH1LList(Delivery delivery, String companyEdiCode, String customerEdiCode) {
		List<SEH1L> list = new ArrayList<>();
		delivery.getDetailList().forEach(to -> {
			list.add( createSEH1LRecord((DeliveryDetail)to, companyEdiCode, customerEdiCode) );
		});
		return list;
	}

	// TODO createSEH1GList
	private List<SEH1G> createSEH1GList(Delivery delivery) {
		List<SEH1G> list = new ArrayList<>();
		return list;
	}

	// TODO createSEH1BList
	private List<SEH1B> createSEH1BList(Delivery delivery) {
		List<SEH1B> list = new ArrayList<>();
		return list;
	}

	
	private SEH1D createSEH1DRecord(DeliveryDetail detail) {
		// TODO Auto-generated method stub
		SEH1D record = new SEH1D();
		record.setDirecciones(null);
		record.setTipoAvisoDeExpedicion_351_35E_(null);
		record.setNumeroAvisoDeExpedicion(null);
		record.setCodigoEmisor_MS_(null);
		record.setCodigoReceptor_MR_(null);
		record.setCalificadorInterlocutor(null);
		record.setCodigoInterlocutor(null);
		record.setAgenciaResponsableListaDeCodigos(null);
		record.setNombre1(null);
		record.setNombre2(null);
		record.setNombre3(null);
		record.setNombre4(null);
		record.setNombre5(null);
		record.setDireccion1_Calle_Numero_(null);
		record.setDireccion2_Calle_Numero_(null);
		record.setDireccion3_Calle_Numero_(null);
		record.setDireccion4_Calle_Numero_(null);
		record.setPoblacion(null);
		record.setProvincia(null);
		record.setCodigoPostal(null);
		record.setCodigoPais(null);
		record.setCalificadorReferencia(null);
		record.setNumeroDeReferencia(null);
		record.setFuncionDeContacto(null);
		record.setCodigoDepartamentoOEmpleado(null);
		record.setNombreDepartamentoOEmpleado(null);
		record.setCalificadorReferencia2(null);
		record.setNumeroDeReferencia2(null);
		return record;
	}

	private SEH1P createSEH1PRecord(DeliveryDetail detail) {
		// TODO Auto-generated method stub
		SEH1P record = new SEH1P();
		record.setEmbalajes(null);
		record.setTipoAvisoDeExpedicion_351_35E_(null);
		record.setNumeroAvisoDeExpedicion(null);
		record.setCodigoEmisor_MS_(null);
		record.setCodigoReceptor_MR_(null);
		record.setNumeroDeJerarquiaDeEmbalaje(null);
		record.setNumeroDeSub_jerarquiaDeEmbalaje(null);
		record.setContadorDeEmbalaje(null);
		record.setNumeroDeBultosOEmbalajes(null);
		record.setCalificadorCodificacionDelEmbalaje(null);
		record.setAcuerdosYCondicionesDeEmbalajes(null);
		record.setTipoDeEmbalajeCodificado(null);
		record.setTipoDeEmbalajeDescripcion(null);
		record.setResp_PagoTransporteDeEmbalajeRetornable(null);
		record.setPesoTotalNeto1_AAC_(null);
		record.setPesoTotalNeto2(null);
		record.setCod_SignificacionDeLaMedidaPesoTotalNeto(null);
		record.setCodigoUnidadDeMedidaPesoTotalNeto(null);
		record.setPesoTotalBruto1_AAD_(null);
		record.setPesoTotalBruto2(null);
		record.setCod_SignificacionDeLaMedidaPesoTotalBruto(null);
		record.setCodigoUnidadDeMedidaPesoTotalBruto(null);
		record.setAltura1_HT_(null);
		record.setAltura2(null);
		record.setCodigoSignificacionDeLaMedidaAltura_3_4_(null);
		record.setCodigoUnidadDeMedidaAltura(null);
		record.setAncho1_WD_(null);
		record.setAncho2(null);
		record.setCodigoSignificacionDeLaMedidaAncho_3_4_(null);
		record.setCodigoUnidadDeMedidaAncho(null);
		record.setLongitud1_AAC_(null);
		record.setLongitud2_AAC_(null);
		record.setCodigoSignificacionDeLaMedidaLongitud_3_4_(null);
		record.setCodigoUnidadDeMedidaLongitud(null);
		record.setUnidadesDeMaterialConsignado(null);
		record.setCalificadorManipulacion(null);
		record.setInstruccionesDeManipulacion(null);
		record.setMarcaOEtiquetaDeEmbarque1(null);
		record.setMarcaOEtiquetaDeEmbarque2(null);
		record.setMarcaOEtiquetaDeEmbarque3(null);
		record.setMarcaOEtiquetaDeEmbarque4(null);
		record.setNumero1DeSeriadoORangoInferiorDelEmbalaje(null);
		record.setNumero1DeSeriadoORangoSuperiorDelEmbalaje(null);
		record.setNumero2DeSeriadoORangoInferiorDelEmbalaje(null);
		record.setNumero2DeSeriadoORangoSuperiorDelEmbalaje(null);
		record.setNumero3DeSeriadoORangoInferiorDelEmbalaje(null);
		record.setNumero3DeSeriadoORangoSuperiorDelEmbalaje(null);
		return record;
	}

	private SEH1L createSEH1LRecord(DeliveryDetail detail, String companyEdiCode, String customerEdiCode) {
		Item item = detail.getItem();
		String tipoAvisoDeExpedicion = SEH1C.V1001T.AVISO_DE_EXPEDICIO_351.getValue();
		SEH1L record = new SEH1L();
		record.setLineas("SEH1L");
		record.setTipoAvisoDeExpedicion_351_35E_(tipoAvisoDeExpedicion);
		record.setNumeroAvisoDeExpedicion("");
		record.setCodigoEmisor_MS_(companyEdiCode);
		record.setCodigoReceptor_MR_(customerEdiCode);
		record.setNumeroDeJerarquiaDeEmbalaje(null);
		record.setNumeroDeSub_jerarquiaDeEmbalaje(null);
		record.setNumeroDeLineaArticulo(detail.getLine());
		record.setCodigoDeArticuloEAN_13ODUN_14(item.getBarcode());
		record.setDescripcionDelArticulo(item.getProduct().getName());
		record.setTipoArticuloEAN_CU_DU_(null);
		record.setCodigoInternoArticuloParaElProveedor_SA_(item.getProduct().getCode());
		record.setVariablePromocional_PV_(null);
		record.setCodigoDUN_14_ADU_(null);
		record.setCodigoACU(null);
		record.setNumeroDeLote_NB_(item.getSerialNumber());
		record.setNumeroDeArticuloDelComprador_1__IN_(null);
		record.setCantidadDeEnvio_12_(detail.getQuantity());
		record.setCalificadorUnidadDeMedida(null);
		record.setUnidadesDeConsumoEnUnidadDeExpedicion(null);
		record.setFechaDeExpiracion_36__102_203_(null);
		record.setCalificadorReferencia1(null);
		record.setNumeroReferencia1(null);
		record.setFechaReferencia1_102_203_(null);
		record.setCalificadorReferencia2(null);
		record.setNumeroReferencia2(null);
		record.setFechaReferencia2_102_203_(null);
		record.setCalificadorReferencia3(null);
		record.setNumeroReferencia3(null);
		record.setFechaReferencia3_102_203_(null);
		record.setUnidadesEnAgrupacionSuperior_45E_(null);
		record.setCodigoEANAdicional(null);
		record.setCantidadSinCargo_192_(null);
		record.setCalificadorDeCantidad(null);
		record.setOtrasCantidades(null);
		record.setUnidadDeMedidaCantidad(null);
		return record;
	}

	private SEH1G createSEH1GRecord(DeliveryDetail detail) {
		// TODO Auto-generated method stub
		SEH1G record = new SEH1G();
		record.setDesgloseCantidadLineas(null);
		record.setTipoAvisoDeExpedicion_351_35E_(null);
		record.setNumeroAvisoDeExpedicion(null);
		record.setCodigoEmisor_MS_(null);
		record.setCodigoReceptor_MR_(null);
		record.setNumeroDeJerarquiaDeEmbalaje(null);
		record.setNumeroDeSub_jerarquiaDeEmbalaje(null);
		record.setNumeroDeLineaArticulo(null);
		record.setContadorDesgloseLineas(null);
		record.setCodigoLugarDeEntrega(null);
		record.setTipoCodigo(null);
		record.setFechaDeEntrega_17_(null);
		record.setCantidadDividida_11_(null);
		record.setPesoEnvioDivididoEnKGM(null);
		return record;
	}

	private SEH1B createSEH1BRecord(DeliveryDetail detail) {
		// TODO Auto-generated method stub
		SEH1B record = new SEH1B();
		record.setInformacionDeLotes(null);
		record.setTipoAvisoDeExpedicion_351_35E_(null);
		record.setNumeroAvisoDeExpedicion(null);
		record.setCodigoEmisor_MS_(null);
		record.setCodigoReceptor_MR_(null);
		record.setNumeroDeJerarquiaDeEmbalaje(null);
		record.setNumeroDeSub_jerarquiaDeEmbalaje(null);
		record.setNumeroDeLineaArticulo(null);
		record.setContadorInformacionDeLotes(null);
		record.setCodigoInstrucciones(null);
		record.setFechaDeCaducidad_36__102_203_(null);
		record.setFechaRecepcionDeMercancias_50__102_203_(null);
		record.setMejorAntesDeFecha_361__102_203_(null);
		record.setCalificadorCantidad_11_12_(null);
		record.setCantidad(null);
		record.setCalificadorDelNumeroDeIdentidad(null);
		record.setNumeroDeIdentidad(null);
		record.setFechaDeEnvasadoOEmpaquetado(null);
		return record;
	}
	
	
}
