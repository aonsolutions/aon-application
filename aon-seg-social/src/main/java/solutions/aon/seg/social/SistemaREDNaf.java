package solutions.aon.seg.social;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlLabel;
import org.htmlunit.html.HtmlOption;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlSubmitInput;

import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.UnknownAuthorizedException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.IpfAlreadyExistsReachedPage;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;

public class SistemaREDNaf {

	public static void main(String[] args) throws Exception {

		String certificate = "/tmp/AyudaTFNMT.p12";
		final String certificatePassword ="123456";
		final String certificateType = "pkcs12";
		
		//Comprueba si hay un campo vacio, en este caso campo NOMBRE PADRE
//		try(InputStream certificateInputStream = new FileInputStream(certificate)) {
//			NafRequest nafRequest = new NafRequest(
//					"Juan Manuel", //NOMBRE
//					"Ortega", //APELLIDO1
//					"Alvarez", //APELLIDO2
//					Nationality.ESPAÑA, //NACIONALIDAD
//					"Juan Manuel", //NOMBRE_PADRE
//					"Milagros",//NOMBRE_MADRE
//					Sexo.VARON,
//					new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
//					IdentCode.DNI, //IDENTIFICADOR DOCUMENTO
//					"45339825V"//NUMERO DE DOCUMENTO
//					);
////			nafRequest.setAuthorized(args[2]);
//			SistemaREDNaf.getNaf(certificateInputStream, certificatePassword, certificateType, nafRequest);
//		}
		
		try(InputStream certificateInputStream = new FileInputStream(certificate)){
			NafRequest nafRequest = new NafRequest(
					"JUAN MANUEL",
					"ORTEGA", 
					"ÁLVAREZ", 
					Nationality.ESPAÑA, 
					"JUAN MANUEL", 
					"MILAGROS",
					Sexo.VARON,
					new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),
					IdentCode.DNI,
					"45339825V");
//			nafRequest.setAuthorized(args[2]);
			SistemaREDNaf.getNaf(certificateInputStream, certificatePassword, certificateType, nafRequest);
		}

		
	}
	
	public static String getDay(Date date) {
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		
		int dia = cal.get(Calendar.DAY_OF_WEEK);
		
		return String.valueOf(dia);
	}
	
	public static String getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		
		int mes = cal.get(Calendar.MONTH)+1;
		
		return String.valueOf(mes);
	}
	
	public static String getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(date);
		
		int anio = cal.get(Calendar.YEAR);
		
		return String.valueOf(anio);
	}
		
public static void getNaf(final InputStream certificateInputStream, final String certificatePassword, final String certificateType, NafRequest nafRequest) throws SegSocialException, FailingHttpStatusCodeException, IOException, InterruptedException {
		
		String regMovil ="^\\d{9}$";
		Pattern pat = Pattern.compile(regMovil);
		Matcher mat;
		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
			
			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=APR01&E=I&AP=AFIR");
//			System.out.println(htmlPage.asXml());

			DomElement authorizedElement = htmlPage.getElementById("Sub0501210055_1_0" );
		    if (authorizedElement == null) {
			throw new UnknownAuthorizedException("La autorizacion seleccionada no es correcta, por favor intentelo de nuevo"); 
		    }
			for (int i = 1; authorizedElement != null ; i++) {
			    String authorizedText = authorizedElement.getTextContent();
			    if (authorizedText != null && authorizedText.startsWith(nafRequest.authorized)) {
				htmlPage = authorizedElement.dblClick();
				break;
			    }
			    authorizedElement = htmlPage.getElementById("Sub0501210055_1_" + i);
			    if (authorizedElement == null) {
				throw new UnknownAuthorizedException("La autorizacion seleccionada no es correcta, por favor intentelo de nuevo"); 
			    }
			}
			
			HtmlForm identForm = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("jacadaform")).orElseThrow();
			
			identForm.getInputByName("txt_SDFAOAPPFA1PRNOMBRE").setValue(nafRequest.name);
			identForm.getInputByName("txt_SDFAOAPPFA1PRAPELLIDO1").setValue(nafRequest.firstSurname);
			identForm.getInputByName("txt_SDFAOAPPFA1PRAPELLIDO2").setValue(nafRequest.secondSurname);
			
			identForm.getInputByName("txt_SDFPRNACIONALIDAD_ayuda").setValue(nafRequest.nationality != null ? nafRequest.nationality.value : "");
			
			identForm.getInputByName("txt_SDFPRPADRE").setValue(nafRequest.fatherName);
			identForm.getInputByName("txt_SDFPRMADRE").setValue(nafRequest.motherName);
			
			HtmlSelect sexSelect = htmlPage.getHtmlElementById("ListaSexo_ayuda");
			HtmlOption sexOpt = sexSelect.getOptionByValue(nafRequest.sex != null ? nafRequest.sex.value: "");
			sexSelect.setSelectedAttribute(sexOpt, true);
			
			String day = nafRequest.birthDate != null ? SistemaREDNaf.getDay(nafRequest.birthDate) : "";
			identForm.getInputByName("txt_SDFPRDIANA").setValue(day);
			String month = nafRequest.birthDate != null ? SistemaREDNaf.getMonth(nafRequest.birthDate) : "";
			identForm.getInputByName("txt_SDFPRMESNA").setValue(month);
			String year = nafRequest.birthDate != null ? SistemaREDNaf.getYear(nafRequest.birthDate) : "";
			identForm.getInputByName("txt_SDFPRAONA").setValue(year);
			identForm.getInputByName("txt_SDFPRTIPOPF_ayuda").setValue(nafRequest.identCode != null ? nafRequest.identCode.value: "");
			identForm.getInputByName("txt_SDFPRCODPF").setValue(nafRequest.identNumber);
		
			//-------------------- Optionals ----------------------
			identForm.getInputByName("txt_SDFPRPREFSMS_ayuda").setValue(nafRequest.mobilePrefix);
			identForm.getInputByName("txt_SDFPRMOVILSMS").setValue(nafRequest.mobileNumber);
			mat = pat.matcher(nafRequest.mobileNumber);
			if (mat.find() || identForm.getInputByName("txt_SDFPRMOVILSMS").getValue().equals("") ) {
								
			}else {
				throw new InvalidDataException("El numero de telefono movil no es correcto, intentelo otra vez");
			}
			
			
			identForm.getInputByName("txt_SDFPRTIPOVIA_ayuda").setValue(nafRequest.streetType);
			identForm.getInputByName("txt_SDFPRNOMVIA").setValue(nafRequest.streetName);
			identForm.getInputByName("txt_SDFPRNUMERO").setValue(nafRequest.streetNumber);
			identForm.getInputByName("txt_SDFPRBIS").setValue(nafRequest.bis);
			identForm.getInputByName("txt_SDFPRBLOQUE").setValue(nafRequest.block);
			identForm.getInputByName("txt_SDFPRESCALERA").setValue(nafRequest.stair);
			identForm.getInputByName("txt_SDFPRPISO").setValue(nafRequest.floor);
			identForm.getInputByName("txt_SDFPRPUERTA").setValue(nafRequest.door);
			identForm.getInputByName("txt_SDFPRTELEFONO9").setValue(nafRequest.fixedNumber);
			identForm.getInputByName("txt_SDFPRCODPOS_ayuda").setValue(nafRequest.postalCode);
			identForm.getInputByName("txt_SDFPRLOCRES_ayuda").setValue("");
			identForm.getInputByName("txt_SDFPRLOCRES_ayuda").setValue(nafRequest.locality);
			
			HtmlSelect registAddressNot = htmlPage.getHtmlElementById("ListaSiNo");
			HtmlOption yes = registAddressNot.getOptionByValue("SI");
			HtmlOption no = registAddressNot.getOptionByValue("NO");
			registAddressNot.setSelectedAttribute(no, true);
			
			HtmlSelect registEmail =(HtmlSelect) htmlPage.getElementById("ListaSiNo001");
			registEmail.setSelectedAttribute(yes, true);

			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Continuar]")).click();
			
			HtmlElement targetElement = (HtmlElement) htmlPage.getElementById("Frame");
			if (targetElement != null) {
				throw new IpfAlreadyExistsReachedPage(" --------------------------------------------------------------\n"
						+ "			 |								|\n"
						+ "			 | SEGÚN LA INFORMACIÓN EXISTENTE EN NUESTRAS BASES DE DATOS, |\n"
						+ "			 | EL IPF QUE SE ESTÁ TRAMITANDO YA EXISTE. 			|\n"
						+ "			 | DEBE REALIZAR ESTE TRÁMITE A TRAVÉS DE CASIA 		|\n"
						+ "			 |								|\n"
						+ "			 --------------------------------------------------------------");
			}

			try {
			    HtmlUnitToolkit.manageStatusCode(htmlPage);
			} catch ( UnfilledMandatory unfilledMandatory) {

			    String unfilledId = htmlPage.getFocusedElement().getId();
			    System.out.println(unfilledId);
			    if (unfilledId.equals("SDFPRCODPF")  && htmlPage.getElementById(unfilledId).getTextContent() !=null) {
			    	unfilledId = "SDFPRTIPOPF_ayuda";
				}
			    System.out.println(unfilledId);

			    List<HtmlLabel> labels = identForm.getByXPath("//label[@for=\""+unfilledId+"\"]");
			    if ( labels.isEmpty()) {
				throw unfilledMandatory;
			    }
			    
			    String msg = labels.stream().map(HtmlLabel::getTextContent).collect(Collectors.joining("" , "El campo '", "' es obligatorio." ));
			    throw new UnfilledMandatory(msg);
		
			}			
		}
	}
	
	public enum Sexo{
		EMPTY(""),
		VARON("Varón"),
		MUJER("Mujer");
		
		private String value;
		
		private Sexo(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	public enum IdentCode{
		EMPTY(""),
		DNI("1"),
		NIE("6");

		private String value;
		
		private IdentCode(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
	//NO ENTRAN PAISES QUE NO SEAN DE EUROPA
	public enum Nationality{
		EMPTY(""),
		AUSTRIA("040"),
		BELGICA("056"),
		BULGARIA("100"),
		CROACIA("191"),
		CHIPRE("196"),
		REPUBLICA_CHECA("203"),
		BENIN("204"),
		DINAMARCA("208"),
		ESTONIA("233"),
		FINLANDIA("246"),
		FRANCIA("250"),
		ALEMANIA("276"),
		GRECIA("300"),
		HUNGRIA("348"),
		ISLANDIA("352"),
		IRLANDA("372"),
		ITALIA("380"),
		LETONIA("428"),
		LIECHTENSTEIN("438"),
		LITUANIA("440"),
		LUXEMBURGO("442"),
		MALTA("470"),
		PAISES_BAJOS("528"),
		NORUEGA("578"),
		POLONIA("616"),
		PORTUGAL("620"),
		RUMANIA("642"),
		ESLOVAQUIA("703"),
		ESLOVENIA("705"),
		ESPAÑA("724"),
		SUECIA("752"),
		SUIZA("756");
		
		private String value;
		
		private Nationality(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
	
	//METODO DE PRUEBA PARA ALMACENAR LA PAGINA RESULTADO
	public static void savePageToFile(HtmlPage htmlPage, String fileName) throws IOException {
	    try (OutputStream outputStream = new FileOutputStream("/tmp/"+fileName)) {
	        outputStream.write(htmlPage.asNormalizedText().getBytes());
	    }
	}
	
	public static class NafRequest {
		
    String authorized = "";
	String name = "";
	String firstSurname = "";
	String secondSurname= "" ;
	Nationality nationality  ;
	String fatherName = "";
	String motherName = "";
	Sexo sex;
	Date birthDate ;
	IdentCode identCode ;
	String identNumber = "";
	String mobilePrefix = "";
	String mobileNumber = "";
	String streetType = "";
	String streetName = "";
	String streetNumber = "" ;
	String bis = "";
	String block = "";
	String stair = "";
	String floor = "";
	String door = "";
	String fixedNumber = "";
	String postalCode = "";
	String locality = "";

	
	public NafRequest(
		String name, 
		String firstSurname, 
		String secondSurname,
		Nationality nationality, 
		String fatherName, 
		String motherName,
		Sexo sex,
		Date birthDate, 
		IdentCode identCode, 
		String identNumber) {
	    
		this.name = name !=null ? name :"";
		this.firstSurname = firstSurname != null ? firstSurname : "";
		this.secondSurname = secondSurname != null ? secondSurname : "";
		this.nationality = nationality != null ? nationality : Nationality.EMPTY;
		this.fatherName = fatherName != null ? fatherName : "";
		this.motherName = motherName != null ? motherName : "";
		this.sex = sex != null ? sex : Sexo.EMPTY;
		this.birthDate = birthDate != null ? birthDate : new Date();
		this.identCode = identCode != null ? identCode : IdentCode.EMPTY;
		this.identNumber = identNumber != null ? identNumber : "";
	}
		
	public NafRequest setAuthorized(String authorized) {
	    this.authorized = authorized;
		return this;
	}
	
	public NafRequest withMobilePrefix(String mobilePrefix) {
		this.mobilePrefix = mobilePrefix;
		if (mobilePrefix == null) {
			this.mobilePrefix = "";
		}
		return this;
	}
	
	public NafRequest withMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
		if (mobileNumber == null) {
			this.mobileNumber = "";
		}
		return this;
	}
	
	public NafRequest withFixedNumber(String fixedNumber) {
		this.fixedNumber = fixedNumber;
		if (fixedNumber == null) {
			this.fixedNumber = "";
		}
		return this;
	}
	
	public NafRequest withStreetType(String streetType) {
		this.streetType = streetType;
		if (streetType == null) {
			this.streetType = "";
		}
		return this;
	}
	
	
	public NafRequest withStreetName(String streetName) {
		this.streetName = streetName;
		if (streetName == null) {
			this.streetName = "";
		}
		return this;
	}
	
	public NafRequest withStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
		if (streetNumber == null) {
			this.streetNumber = "";
		}
		return this;
	}
	
	public NafRequest withBis(String bis) {
		this.bis = bis;
		if (bis == null) {
			this.bis = "";
		}
		return this;
	}
	
	public NafRequest withBlock(String block) {
		this.block = block;
		if (block == null) {
			this.block = "";
		}
		return this;
	}
	
	public NafRequest withStair(String stair) {
		this.stair = stair;
		if (stair == null) {
			this.stair = "";
		}
		return this;
	}
	
	public NafRequest withFloor(String floor) {
		this.floor = floor;
		if (floor == null) {
			this.floor = "";
		}
		return this;
	}
	
	public NafRequest withDoor(String door) {
		this.door = door;
		if (door == null) {
			this.door = "";
		}
		return this;
	}
	
	public NafRequest withPostalCode(String postalCode) {
		this.postalCode = postalCode;
		if (postalCode == null) {
			this.postalCode = "";
		}
		return this;
	}
	
	public NafRequest withLocality(String locality) {
		this.locality = locality;
		if (locality == null) {
			this.locality = "";
		}
		return this;
	}
}
	
}
