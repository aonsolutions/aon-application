package com.esferalia.aon.payroll.irpf;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.xml.sax.SAXException;

import com.aeat.ModeloRetencionesXMLJaxb;
import com.aeat.XMLProgressListener;
import com.aeat.jaxb.AEATRetencionesEntrada2011;
import com.aeat.jaxb.AEATRetencionesError2011;
import com.aeat.jaxb.AEATRetencionesSalida2011;
import com.aeat.jaxb.TipoRetenedorEntrada2011;
import com.aeat.jaxb.TipoRetenedorError2011;
import com.aeat.jaxb.TipoRetenedorSalida2011;
import com.aeat.jaxb.TipoRetenidoError2011;
import com.aeat.jaxb.TipoRetenidoSalida2011;
import com.aeat.modulo.retenciones.ModuloCalculo;
import com.esferalia.aon.salary.expression.ExpressionException;

class AEATIrpfCalculator extends IrpfCalculator {
	
	public static final String TMP_DIR = "com.esferalia.aon.payroll.irpf.tmpdir";

	private static class Break extends Error {
		
	}
	
	private static class FileBreak extends File {
		public FileBreak() {
			super("");
		}

		@Override
		public String getPath() {
			throw new Break();
		}
	}
	
	private static class NoopXMLProgressListner implements XMLProgressListener {
		@Override
		public void avanzarBarraProgreso() {
		}
	}
	
	private static JAXBContext JAXB_CONTEXT = null; 
	
	{
		try {
			JAXB_CONTEXT = JAXBContext.newInstance("com.aeat.jaxb");
		} catch (JAXBException e) {
		}
	}

	private static final File FILE_BREAK = new FileBreak();
	
	private static final XMLProgressListener NOOP_XML_PROGRESS_LISTENER = 
		new NoopXMLProgressListner();
	
	
	@Override
	public void calculate(
			AEATRetencionesEntrada2011 aeatRetencionesEntrada2011, CallbackHandler cb)
			throws IrpfException, ExpressionException {
		
		ModeloRetencionesXMLJaxb modeloRetencionesXMLJaxb = 
			new ModeloRetencionesXMLJaxb();
		modeloRetencionesXMLJaxb.setXMLProgressListener(NOOP_XML_PROGRESS_LISTENER);

		List<TipoRetenedorEntrada2011> retenedor = 
			aeatRetencionesEntrada2011.getRetenedor();
		int numRetenidos = 0;
		for (TipoRetenedorEntrada2011 tipoRetenedorEntrada2011 : retenedor) {
			numRetenidos  += tipoRetenedorEntrada2011.getRetenido().size();
		}
		modeloRetencionesXMLJaxb.setSalidaRetenciones(null);
		modeloRetencionesXMLJaxb.setEntradaRetenciones(null);
		modeloRetencionesXMLJaxb.setSalidaRetenciones(null);
		modeloRetencionesXMLJaxb.setSalidaXMLError(null);
		modeloRetencionesXMLJaxb.setOrdenRetenedorActualError(-1);
		
		modeloRetencionesXMLJaxb.setEntradaRetenciones(aeatRetencionesEntrada2011);
		modeloRetencionesXMLJaxb.setNuneroRetenedorese(retenedor.size());
		modeloRetencionesXMLJaxb.setNumRetenidos(numRetenidos);
		
		try {
			modeloRetencionesXMLJaxb.setJc(JAXB_CONTEXT);
			modeloRetencionesXMLJaxb.setFicheroError(FILE_BREAK);
			modeloRetencionesXMLJaxb.setFicheroSalida(FILE_BREAK);
			modeloRetencionesXMLJaxb.calcularXML();
		} catch ( Break b ){
		} 
		
		AEATRetencionesError2011 aeatRetencionesError2011 = 
			modeloRetencionesXMLJaxb.getSalidaXMLError();
		if ( aeatRetencionesError2011 != null ) {
			for (TipoRetenedorError2011 retenedorError2011 : aeatRetencionesError2011.getRetenedor()) {
				List<TipoRetenidoError2011> retenido = 
					retenedorError2011.getRetenido();
				for (TipoRetenidoError2011 retenidoError2011 : retenido) {
					cb.onError(retenedorError2011, retenidoError2011);
				}
			}
		}
		
		AEATRetencionesSalida2011  aeatRetencionesSalida2011  = 
			modeloRetencionesXMLJaxb.getSalidaRetenciones();
		if ( aeatRetencionesSalida2011 != null ) {
			for (TipoRetenedorSalida2011 retenedorSalida2011 : aeatRetencionesSalida2011.getRetenedor() ) {
				
				List<TipoRetenidoSalida2011> retenido = 
					retenedorSalida2011.getRetenido();
				
				for (TipoRetenidoSalida2011 retenidoSalida2011 : retenido) {
					cb.onSalida(retenedorSalida2011, retenidoSalida2011);
				}
			}
			/*
			try {
				marshal(aeatRetencionesSalida2011, System.out);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
	}

	public void _calculate(
			AEATRetencionesEntrada2011 aeatRetencionesEntrada2011, CallbackHandler cb)
			throws IrpfException, ExpressionException {

		File error = null;
		File salida = null;
		File entrada = null;
		
		try { 
			
			File tmpDir = getTmpDir();
			
			entrada = new File (tmpDir, "AEATEntrada.xml") ; //File.createTempFile("AEATEntrada", ".xml", tmpDir);
			
			marshall(aeatRetencionesEntrada2011, entrada);
			
			error = new File (tmpDir, "AEATError.xml") ; // File.createTempFile("AEATError", ".xml", tmpDir);
			salida = new File (tmpDir, "AEATSalida.xml") ; //File.createTempFile("AEATSalida", ".xml", tmpDir);

			ModuloCalculo.procesarFicheroXml(
					entrada.getAbsolutePath(), 
					error.getAbsolutePath(), 
					null, 
					salida.getAbsolutePath());
			
			if ( error.exists() ) {
				AEATRetencionesError2011 aeatRetencionesError2011  = 
					unmarshal(error);
				List<TipoRetenedorError2011> retenedor = 
					aeatRetencionesError2011.getRetenedor();
				for (TipoRetenedorError2011 retenedorError2011 : retenedor) {
					List<TipoRetenidoError2011> retenido = 
						retenedorError2011.getRetenido();
					for (TipoRetenidoError2011 retenidoError2011 : retenido) {
						cb.onError(retenedorError2011, retenidoError2011);
					}
				}
			}
			if ( salida.exists()  ) {
				AEATRetencionesSalida2011 aeatRetencionesSalida2011  = 
					unmarshal(salida);
				
				List<TipoRetenedorSalida2011> retenedor = 
					aeatRetencionesSalida2011.getRetenedor();

				for (TipoRetenedorSalida2011 retenedorSalida2011 : retenedor ) {
					
					List<TipoRetenidoSalida2011> retenido = 
						retenedorSalida2011.getRetenido();
					
					for (TipoRetenidoSalida2011 retenidoSalida2011 : retenido) {
						cb.onSalida(retenedorSalida2011, retenidoSalida2011);
					}
				}
			}
			
		} catch (SAXException e) {
			throw new IrpfException(e);
		} catch (JAXBException e) {
			throw new IrpfException(e);
		} 
		finally{
		}
	}

	protected static final File getTmpDir() {
		String tmpDir =  System.getProperty(TMP_DIR, System.getProperty("java.io.tmpdir"));
		return new File(tmpDir);
	}

	protected static <T>  void marshall ( T object, File file)
	throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance( "com.aeat.jaxb" );
		Marshaller marshaller = jc.createMarshaller();
		marshaller.marshal(object, file);
	}
	protected static <T>  void marshal ( T object, OutputStream os)
	throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance( "com.aeat.jaxb" );
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(object, os);
	}

	
	protected static <T> T unmarshal( File file )
	throws JAXBException, SAXException {
		JAXBContext jc = JAXBContext.newInstance( "com.aeat.jaxb" );
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		return ( T ) unmarshaller.unmarshal( file );
	}		

	
}