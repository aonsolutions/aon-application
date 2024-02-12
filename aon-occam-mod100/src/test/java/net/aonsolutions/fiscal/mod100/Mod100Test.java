package net.aonsolutions.fiscal.mod100;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import net.aonsolutions.fiscal.mod100.e2023.Declaracion;
import net.aonsolutions.fiscal.mod100.e2023.TipoAsignacionTributaria;
import net.aonsolutions.fiscal.mod100.e2023.TipoDatosAux;
import net.aonsolutions.fiscal.mod100.e2023.TipoDatosEconomicos;
import net.aonsolutions.fiscal.mod100.e2023.TipoDatosIdentificativos;
import net.aonsolutions.fiscal.mod100.e2023.TipoOtraDeclaracion;
import net.aonsolutions.fiscal.mod100.e2023.TipoRdtoTrabajo;
import net.aonsolutions.fiscal.mod100.e2023.TipoRdtoTrabajoRes;
import net.aonsolutions.fiscal.mod100.e2023.TipoRepresentante;
import net.aonsolutions.fiscal.mod100.e2023.TipoResultados;
import net.aonsolutions.fiscal.mod100.e2023.TipoSexo;
import net.aonsolutions.fiscal.mod100.e2023.TipoTomaDatosAmpliada;
import net.aonsolutions.fiscal.mod100.e2023.TipoDatosIdentificativos.Conyuge;
import net.aonsolutions.fiscal.mod100.e2023.TipoDatosIdentificativos.Declarante;
import net.aonsolutions.fiscal.mod100.e2023.TipoDatosIdentificativos.Hijos;

public class Mod100Test {

	private static void save( Declaracion mod ) throws JAXBException, IOException, TransformerException {
		FileOutputStream out = new FileOutputStream( "/home/ecastellano/TRABAJO/FISCAL/MOD100/Mod100-2023.xml" );
		JAXBContext context = JAXBContext.newInstance(Declaracion.class);
		Marshaller um = context.createMarshaller();
		um.setProperty("jaxb.encoding", "ISO-8859-1");
		
		DOMResult domResult = new DOMResult();
		//um.marshal(mod,writer);
		um.marshal(mod, domResult);
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(new DOMSource(domResult.getNode()), new StreamResult(out));		
		
		out.flush();
		out.close();
		System.out.println("DONE!");
	}
	
	private static TipoDatosAux getAux() {
		TipoDatosAux aux = new TipoDatosAux();
		aux.setIdioma("E");
		aux.setVERSION("1.0");
		aux.setNIFEEDD("B01487271");
		return aux;
	}

	private static TipoDatosIdentificativos getDatosIdentificativos() {
		TipoDatosIdentificativos tdi = new TipoDatosIdentificativos();
		tdi.setDeclarante( getDeclarante());
		tdi.setConyuge( getConyuge() );
		tdi.setHijos( getHijos() );
		return tdi;
	}

	private static Declarante getDeclarante() {
		Declarante dec = new Declarante();
		dec.setINDV("1");
		dec.setDPNIFD("44671367P");
		dec.setDPAPENOMD("CASTELLANO HURTADO, EUGENIO");
		dec.setECIVIL("2");
		dec.setDPFNACD("04/06/1974");
		dec.setSEXOD( TipoSexo.H );
		return dec;
	}

	private static Conyuge getConyuge() {
		Conyuge c = new Conyuge();
		c.setDPFNACC( "26/06/1974" );
		c.setDPAPENOMC("GOICOECHEA MANSO, ELENA");
		c.setDPNIFC("44670178S");
		c.setSEXOC( TipoSexo.M ); 
		// TODO Auto-generated method stub
		return c;
	}

	private static Hijos getHijos() {
		Hijos h = new Hijos();
		
		return null;
	}

	private static TipoAsignacionTributaria getAsignacionTributaria() {
		TipoAsignacionTributaria at = new TipoAsignacionTributaria();
		at.setFINESSOCIALES("1"); 
		at.setIGLESIA( "0" );
		return at;
	}

	private static TipoRepresentante getRepresentante() {
		TipoRepresentante rep = new TipoRepresentante();
		rep.setZDNIR("44671367P");
		rep.setZAPEND("CASTELLANO HURTADO, EUGENIO");
		return rep;
	}

	private static TipoOtraDeclaracion getOtraDeclaracion() {
		TipoOtraDeclaracion tod = new TipoOtraDeclaracion();
//		tod.setComplementaria();
//		tod.setIngresosYDevolucionesPrevias();
//		tod.setSolicitudRectificacion( ) ;
		return null;
	}

	public static void main(String[] args) throws JAXBException, IOException, TransformerException {
		Declaracion mod = new Declaracion();
		mod.setModelo( "100" );
		mod.setEjercicio("2023");
		mod.setPeriodo("0A");
		mod.setAux( getAux() );
		mod.setDatosIdentificativos( getDatosIdentificativos() );
		mod.setAsignacionTributaria( getAsignacionTributaria() );
		mod.setRepresentante( getRepresentante() );
		mod.setOtraDeclaracion( getOtraDeclaracion() );
		mod.setDatosEconomicos( getDatosEconomicos() );
		save( mod );
	}

	private static TipoDatosEconomicos getDatosEconomicos() {
		TipoDatosEconomicos ec = new TipoDatosEconomicos();
		TipoResultados res = new TipoResultados();
		TipoRdtoTrabajoRes trtr = new TipoRdtoTrabajoRes();
		trtr.setTPTOTAL( BigDecimal.valueOf(40000.0));
		res.setRdtoTrabajoRes( trtr );
		ec.setCodigoCADeclaracion( "01" );
		ec.setTIPOTRIBUTACION( "1" );
		ec.setResultados(res);
		TipoTomaDatosAmpliada ttda = new TipoTomaDatosAmpliada();
		TipoRdtoTrabajo trtr0 = new TipoRdtoTrabajo();
		trtr0.setTPIC( BigDecimal.valueOf(40000.0));
		ttda.setRdtoTrabajo( trtr0 );
		ttda.setTitular(2);
		ttda.setNif("44671367P");
		ec.getTomaDatosAmpliada().add( ttda );
		return ec;
	}



}
