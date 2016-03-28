package com.esferalia.aon.file.pms.writer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.pms.webpol.WebpolGuests;
import com.esferalia.aon.file.pms.webpol.data.TIPO0;
import com.esferalia.aon.file.pms.webpol.data.TIPO1;
import com.esferalia.aon.file.pms.webpol.data.TIPO2;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationGuest;

public class WebpolGuestsWriter implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat timeFormatter = new SimpleDateFormat("hhmm");
	

	public FileOutput createFile(Hotel[] hotelList, List<ITransferObject> list, Date date ) throws FileNotFoundException, UnsupportedEncodingException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		FileFiller filler;
		if(hotelList.length>1){
			TIPO0 tipo0 = createTIPO0Record(list, date); 
			filler = new WebpolGuests(tipo0, writer);
		} else {
			Hotel hotel = hotelList[0];
			TIPO1 tipo1 = createTIPO1Record(hotel, list, date); 
			filler = new WebpolGuests(tipo1, writer);
		}
		FileOutput output = new FileOutput();
		output.setErrors(filler.create());
		output.setContent(outputStream.toByteArray());
		return output;
	}


	private TIPO0 createTIPO0Record(List<ITransferObject> list, Date dateTime) {
		TIPO0 tipo0 = new TIPO0();
		
		// TODO CodigoAgrupacionHotelera
		tipo0.setCodigoAgrupacionHotelera(null);
		tipo0.setNombreAgrupacion(AonUtil.getDomainName());
		tipo0.setFechaConfeccionFichero(dateFormatter.format(dateTime));
		tipo0.setHoraConfeccionFichero(timeFormatter.format(dateTime));
		tipo0.setTipo1List(new LinkedList<TIPO1>());
		
		TIPO1 tipo1 = null;
		Hotel hotel = null;
		for(ITransferObject to: list){
			ProjectReservationGuest guest = (ProjectReservationGuest)to;
			if(hotel==null || !guest.getProjectReservation().getHotel().getId().equals(hotel.getId())){
				hotel = guest.getProjectReservation().getHotel();
				tipo1 = createTIPO1Record(hotel, dateTime);
				tipo1.setTipo2List(new LinkedList<TIPO2>());
				tipo0.getTipo1List().add(tipo1);
			}
			tipo1.getTipo2List().add(createTIPO2Record(guest));
			
		}
		tipo0.setNumeroRegistrosTipo1(String.valueOf(tipo0.getTipo1List().size()));
		return tipo0;
	}


	private TIPO1 createTIPO1Record(Hotel hotel, List<ITransferObject> list, Date dateTime) {
		TIPO1 tipo1 = createTIPO1Record(hotel, dateTime);
		tipo1.setTipo2List(new LinkedList<TIPO2>());
		list.forEach(to -> {
			tipo1.getTipo2List().add(createTIPO2Record((ProjectReservationGuest)to));
		});
		return tipo1;
	}
	
	private TIPO1 createTIPO1Record(Hotel hotel, Date dateTime) {
		TIPO1 tipo1 = new TIPO1();
		// TODO CodigoEstablecimientoHotelero
		tipo1.setCodigoEstablecimientoHotelero(null);
		tipo1.setNombreEstablecimiento(hotel.getWorkPlace().getDescription());
		tipo1.setFechaConfeccionFichero(dateFormatter.format(dateTime));
		tipo1.setHoraConfeccionFichero(timeFormatter.format(dateTime));
		return tipo1;
	}

		
	private TIPO2 createTIPO2Record(ProjectReservationGuest guest) {
		TIPO2 tipo2 = new TIPO2();
		
		if(guest.getDocument()!=null){
			if(guest.getDocumentType()==DocumentType.NIF || guest.getDocumentType()==DocumentType.CIF){
				tipo2.setNumeroDocumentoEsp(guest.getDocument());
			} else {
				tipo2.setNumeroPasaporteExtranjeros(guest.getDocument());
			}
		}
			
//		D para el DNI
//		P para el  Pasaporte
//		C para el Permiso de Conducir
//		I para   la   carta   o   documento   de identidad
//		N para permiso de residencia español
//		X para  permiso  de  residencia  de  otro Estado    Miembro    de    la    Unión Europea
		
		if(guest.getDocumentType()==null || guest.getDocumentType()==DocumentType.NIF){
			tipo2.setTipoDocumento("D");
		} else if(guest.getDocumentType()==DocumentType.PASSPORT){
			tipo2.setTipoDocumento("P");
//		} else if(guest.getDocumentType()==DocumentType.DRIVING_PERMIT){
//			tipo2.setTipoDocumento("C");
		} else if(guest.getDocumentType()==DocumentType.COMMUNITY_CARD){
			tipo2.setTipoDocumento("I");
		} else if(guest.getDocumentType()==DocumentType.WORK_PERMIT){
			tipo2.setTipoDocumento("N");
//		} else if(guest.getDocumentType()==DocumentType.WORK_PERMIT_IN_UE){
//			tipo2.setTipoDocumento("X");
		}
		
		// TODO FechaExpedicionDocumento
		tipo2.setFechaExpedicionDocumento(null);
		tipo2.setPrimerApellido(guest.getSurname()!=null?guest.getSurname():"");
		tipo2.setSegundoApellido(guest.getSurname2()!=null?guest.getSurname2():"");
		tipo2.setNombre(guest.getName()!=null?guest.getName():"");
		if(guest.getPerson()!=null){
			if(guest.getPerson().getGender()==Gender.MALE){
				tipo2.setSexo("M");
			} else if(guest.getPerson().getGender()==Gender.FEMALE){
				tipo2.setSexo("F");
			}
		}
		tipo2.setFechaNacimiento(guest.getBirthDate()!=null?dateFormatter.format(guest.getBirthDate()):null);
		tipo2.setPaisNacionalidad(guest.getDocumentCountry()!=null?guest.getDocumentCountry().getName(AonUtil.getCurrentLocale()).toUpperCase():"");
		tipo2.setFechaEntrada(guest.getProjectReservation().getStartDate()!=null?dateFormatter.format(guest.getProjectReservation().getStartDate()):"");
		
		return tipo2;
	}
	
}
