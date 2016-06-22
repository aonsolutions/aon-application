package com.esferalia.aon.file.seres.util.writer.udapa;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import com.code.aon.common.ITransferObject;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.warehouse.Delivery;
import com.esferalia.aon.file.seres.udapa.UdapaDelivery;
import com.esferalia.aon.file.seres.udapa.delivery.data.SEH1B;
import com.esferalia.aon.file.seres.udapa.delivery.data.SEH1C;
import com.esferalia.aon.file.seres.udapa.delivery.data.SEH1D;
import com.esferalia.aon.file.seres.udapa.delivery.data.SEH1G;
import com.esferalia.aon.file.seres.udapa.delivery.data.SEH1L;
import com.esferalia.aon.file.seres.udapa.delivery.data.SEH1P;

public class UdapaDeliveryWriter {
	
	public static final String CHARSET_ENCODING = "ISO-8859-1";
	
	
	public FileOutput createFile(Delivery delivery, List<ITransferObject> list, Date date ) throws FileNotFoundException, UnsupportedEncodingException {
		SEH1C seh1c = createSEH1CRecord( delivery );
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		FileFiller afi = new UdapaDelivery(seh1c, writer);
		FileOutput output = new FileOutput();
		output.setErrors(afi.create());
		output.setContent(outputStream.toString().getBytes(CHARSET_ENCODING));
		return output;
	}
	
	private SEH1C createSEH1CRecord( Delivery delivery ) {
		// TODO Auto-generated method stub
		SEH1C seh1c = new SEH1C();
		
		seh1c.setCabecera(null);
		seh1c.setTipoAvisoDeExpedicion_351_35E_(null);
		seh1c.setNumeroAvisoDeExpedicion(null);
		seh1c.setCodigoEmisor_MS_(null);
		seh1c.setCodigoReceptor_MR_(null);
		seh1c.setFuncionDelMensaje(null);
		seh1c.setFechaDelDocumento_137__102_203_(null);
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
		seh1c.setCodigoTransportista(null);
		seh1c.setNombreTransportista(null);
		seh1c.setMatriculaDelVehiculo(null);


		seh1c.seh1dList = createSEH1DList(delivery);
		seh1c.seh1pList = createSEH1PList(delivery);
		seh1c.seh1lList = createSEH1LList(delivery);
		seh1c.seh1gList = createSEH1GList(delivery);
		seh1c.seh1bList = createSEH1BList(delivery);
		
		return seh1c;
	}

	private List<SEH1D> createSEH1DList(Delivery delivery) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<SEH1P> createSEH1PList(Delivery delivery) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<SEH1L> createSEH1LList(Delivery delivery) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<SEH1G> createSEH1GList(Delivery delivery) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<SEH1B> createSEH1BList(Delivery delivery) {
		// TODO Auto-generated method stub
		return null;
	}

	
	private List<SEH1D> createSEH1DRecord(Delivery delivery) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<SEH1P> createSEH1PRecord(Delivery delivery) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<SEH1L> createSEH1LRecord(Delivery delivery) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<SEH1G> createSEH1GRecord(Delivery delivery) {
		// TODO Auto-generated method stub
		return null;
	}

	private List<SEH1B> createSEH1BRecord(Delivery delivery) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
