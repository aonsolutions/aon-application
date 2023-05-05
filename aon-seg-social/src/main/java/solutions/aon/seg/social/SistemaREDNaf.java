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
			if (mat.find()) {
				
			}else if(identForm.getInputByName("txt_SDFPRMOVILSMS").getValue().equals("")){
				
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
			
//			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Continuar]")).click();
//
//			
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

//			System.out.println(htmlPage.asXml());
			
//			htmlPage = ((HtmlSubmitInput) htmlPage.querySelector("input[value=Continuar]")).click();

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
			
			// TODO: Check custom erros like :
//			 --------------------------------------------------------------
//			 |								|
//			 | SEGÚN LA INFORMACIÓN EXISTENTE EN NUESTRAS BASES DE DATOS, |
//			 | EL IPF QUE SE ESTÁ TRAMITANDO YA EXISTE. 			|
//			 | DEBE REALIZAR ESTE TRÁMITE A TRAVÉS DE CASIA 		|
//			 |								|
//			 --------------------------------------------------------------
			
			
			
		}
	}
	
	public enum Sexo{
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
		AFGANISTAN("004"),
		ALBANIA ("008"),
		ANTARTIDA("010"),
		ARGELIA("012"),
		SAMOA_AMERICANA("016"),
		ANDORRA("020"),
		ANGOLA("024"),
		ANTIGUA_Y_BARBUDA("28"),
		AZERBAYAN("031"),
		ARGENTINA("032"),
		AUSTRALIA("036"),
		AUSTRIA("040"),
		BAHAMAS("044"),
		BAHREIN("048"),
		BANGLADESH("050"),
		ARMENIA("051"),
		BARBADOS("052"),
		BELGICA("056"),
		BERMUDAS("060"),
		BUTAN("064"),
		BOLIVIA("068"),
		BOSNIA_HERZEGOVINA("070"),
		BOTSWANA("072"),
		BOUVET("074"),
		BRASIL("076"),
		BELICE("084"),
		OC_INDICO_TERRITORIO_BRITANICO("086"),
		SALOMON ("090"),
		ISLAS_VIRGENES_BRITANICAS("092"),
		BRUNEI_DARUSSALAM("096"),
		BULGARIA("100"),
		MYANMAR("104"),
		BURUNDI("108"),
		BIELORRUSIA("112"),
		CAMBOYA("116"),
		CAMERUN("120"),
		CANADA("124"),
		CABO_VERDE("132"),
		CAIMANES_ISLAS("136"),
		REPUBLICA_CENTROAFRICANA("140"),
		SRI_LANKA("144"),
		CHAD("148"),
		CHILE("152"),
		CHINA("156"),
		TAIWAN_PROVINCIA_DE_CHINA("158"),
		CHRISTMAS_ISLAS("162"),
		COCOS_ISLAS("166"),
		COLOMBIA("170"),
		COMORES("174"),
		MAYOTTE("175"),
		CONGO("178"),
		ZAIRE("180"),
		COOK_ISLAS("184"),
		COSTA_RICA("188"),
		CROACIA("191"),
		CUBA("192"),
		CHIPRE("196"),
		REPUBLICA_CHECA("203"),
		BENIN("204"),
		DINAMARCA("208"),
		DOMINICA("212"),
		DOMINICANA_REPUBLICA("214"),
		ECUADOR("218"),
		EL_SALVADOR("222"),
		GUINEA_ECUATORIAL("226"),
		ETIOPIA("231"),
		ERITREA("232"),
		ESTONIA("233"),
		ISLAS_FEROE("234"),
		FALKLAND_O_MALVINAS("238"),
		GEORGIA_DEL_SUR_E_ISLAS_SANDWICH("239"),
		FIDJI("242"),
		FINLANDIA("246"),
		FRANCIA("250"),
		GUAYANA_FRANCESA("254"),
		POLINESIA_FRANCESA("258"),
		TIERRAS_AUSTRALES_FRANCESAS("260"),
		DJIBUTI("262"),
		GABON("266"),
		GEORGIA("268"),
		GAMBIA("270"),
		PALESTINA("275"),
		ALEMANIA("276"),
		GHANA("288"),
		GIBRALTAR("292"),
		KIRIBATI("296"),
		GRECIA("300"),
		GROENLANDIA("304"),
		GRANADA("308"),
		GUADALUPE("312"),
		GUAM("316"),
		GUATEMALA("320"), 
		GUINEA("324"),
		GUAYANA("328"),
		HAITI("332"),
		HEARD_Y_MC_DONALD_ISLAS("334"),
		VATICANO_CIUDAD("336"),
		HONDURAS("340"),
		HONG_KONG("344"),
		HUNGRIA("348"),
		ISLANDIA("352"),
		INDIA("356"),
		INDONESIA("360"),
		IRAN("364"),
		IRAQ("368"),
		IRLANDA("372"),
		ISRAEL("376"),
		ITALIA("380"),
		COSTA_DE_MARFIL("384"),
		JAMAICA("388"),
		JAPON("392"),
		KAZAKSTAN("398"),
		JORDANIA("400"),
		KENIA("404"),
		COREA_NORTE_REPUBLICA_DEMOCRATICA("408"),
		COREA_SUR_REPUBLICA("410"),
		KUWAIT("414"),
		KIRGHIZISTAN("417"),
		LAOS_REPUBLICA_DEMOCRATICA_POPULAR("418"),
		LIBANO("422"),
		LESOTHO("426"),
		LETONIA("428"),
		LIBERIA("430"),
		LIBIA_JAMAHIRIYA_ARABE("434"),
		LIECHTENSTEIN("438"),
		LITUANIA("440"),
		LUXEMBURGO("442"),
		MACAO("446"),
		MADAGASCAR("450"),
		MALAWI("454"),
		MALASIA("458"),
		MALDIVAS_ISLAS("462"),
		MALI("466"),
		MALTA("470"),
		MARTINICA("474"),
		MAURITANIA("478"),
		MAURICIO("480"),
		MEJICO("484"),
		MONACO("492"),
		MONGOLIA("496"),
		REPUBLICA_DE_MOLDAVIA("498"),
		MONTENEGRO("499"),
		MONTSERRAT("500"),
		MARRUECOS("504"),
		MOZAMBIQUE("508"),
		OMAN("512"),
		NAMIBIA("516"),
		NAURU("520"),
		NEPAL("524"),
		PAISES_BAJOS("528"),
		CURAZAO("531"),
		ARUBA("533"),
		SINT_MARTEEN("534"),
		BONAIRE_SAINT_EAUSTATIUS_Y_SABA("535"),
		NUEVA_CALEDONIA_Y_DEPENDENCIAS("540"),
		VANUATU("548"),
		NUEVA_ZELANDA("554"),
		NICARAGUA("558"),
		NIGER("562"),
		NIGERIA("566"),
		NIUE("570"),
		NORFOLK_ISLA("574"),
		NORUEGA("578"),
		ISLAS_MARIANAS_DEL_NORTE("580"),
		ISLAS_MENORES_ALEJADAS_E_E_U_U("581"),
		MICRONESIA("583"),
		ISLAS_MARSHALL("584"),
		PALAU("585"),
		PAKISTAN("586"),
		PANAMA("591"),
		PAPUA_NUEVA_GUINEA("598"),
		PARAGUAY("600"),
		PERU("604"),
		FILIPINAS("608"),
		PITCAIRN("612"),
		POLONIA("616"),
		PORTUGAL("620"),
		GUINEA_BISSAU("624"),
		TIMOR_ORIENTAL("626"),
		PUERTO_RICO("630"),
		QATAR("634"),
		REUNION("638"),
		RUMANIA("642"),
		FEDERACION_DE_RUSIA("643"),
		RUANDA("646"),
		SANTA_HELENA("654"),
		SAN_CRISTOBAL_Y_NIEVES("659"),
		ANGUILA("660"),
		SANTA_LUCIA("662"),
		SAN_PEDRO_Y_MIQUELON("660"),
		SAN_VICENTE_Y_GRANADINAS("670"),
		SAN_MARINO("674"),
		SANTO_TOME_Y_PRINCIPE("678"),
		ARABIA_SAUDI("682"),
		SENEGAL("686"),
		SERBIA("688"),
		SEYCHELLES("690"),
		SIERRA_LEONA("694"),
		SINGAPUR("702"),
		ESLOVAQUIA("703"),
		VIETNAM("704"),
		ESLOVENIA("705"),
		SOMALIA("706"),
		REPUBLICA_DE_SUDAFRICA("710"),
		ZINBABWE("716"),
		ESPAÑA("724"),
		SUDAN_DEL_SUR("728"),
		SUDAN("729"),
		SAHARA_OCCIDENTAL("732"),
		SURINAM("740"),
		SVALBARD_E_ISLA_JUAN_MAYEN("744"),
		SWAZILANDIA("748"),
		SUECIA("752"),
		SUIZA("756"),
		SIRIA_REPUBLICA_ARABE("760"),
		TADJIKISTAN("762"),
		TAILANDIA("764"),
		TOGO("768"),
		TOKELAU("772"),
		TONGA("776"),
		TRINIDAD_Y_TOBAGO("780"),
		EMIRATOS_ARABES_UNIDOS("784"),
		TUNEZ("788"),
		TURQUIA("792"),
		TURKMENISTAN("795"),
		TURKS_Y_CAICOS_ISLAS("796"),
		TUVALU("798"),
		UGANDA("800"),
		UCRANIA("804"),
		MACEDONIA_TERR_ANT_REP_YUGOSLAVA("807"),
		EGIPTO("818"),
		REINO_UNIDO("826"),
		GUERNSEY("831"),
		JERSEY("832"),
		ISLA_DE_MAN("833"),
		REPUBLICA_UNIDA_DE_TANZANIA("834"),
		ESTADOS_UNIDOS("840"),
		VIRGENES_DE_E_E_U_U_ISLAS("850"),
		BURKINA_FASO("854"),
		URUGUAY("858"),
		UZBEKISTAN("860"),
		VENEZUELA("862"),
		WALLIS_Y_FUTUNA_ISLAS("876"),
		SAMOA("882"),
		YEMEN("887"),
		ZAMBIA("894"),
		APATRIDAS("952"),
		PAIS_DESCONOCIDO("953"),
		DESCONOCIDO_SIN_CONVENIO("954"),
		DESCONOCIDO_CON_CONVENIO("955");
		
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
	    
	    	// TODO: Check if any parameter is null , something like  this.name = name != null ? name : "" 
		this.name = name;
		this.firstSurname = firstSurname;
		this.secondSurname = secondSurname;
		this.nationality = nationality;
		this.fatherName = fatherName;
		this.motherName = motherName;
		this.sex = sex;
		this.birthDate = birthDate;
		this.identCode = identCode;
		this.identNumber = identNumber;
	}
	
	//TODO : if null assing empty string, this for all withXXXX methods 
	
	public NafRequest setAuthorized(String authorized) {
	    this.authorized = authorized;
		return this;
	}
	
	public NafRequest withMobilePrefix(String mobilePrefix) {
		this.mobilePrefix = mobilePrefix;
		return this;
	}
	
	public NafRequest withMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
		return this;
	}
	
	public NafRequest withFixedNumber(String fixedNumber) {
		this.fixedNumber = fixedNumber;
		return this;
	}
	
	public NafRequest withStreetType(String streetType) {
		this.streetType = streetType;
		return this;
	}
	
	
	public NafRequest withStreetName(String streetName) {
		this.streetName = streetName;
		return this;
	}
	
	public NafRequest withStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
		return this;
	}
	
	public NafRequest withBis(String bis) {
		this.bis = bis;
		return this;
	}
	
	public NafRequest withBlock(String block) {
		this.block = block;
		return this;
	}
	
	public NafRequest withStair(String stair) {
		this.stair = stair;
		return this;
	}
	
	public NafRequest withFloor(String floor) {
		this.floor = floor;
		return this;
	}
	
	public NafRequest withDoor(String door) {
		this.door = door;
		return this;
	}
	
	public NafRequest withPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}
	
	public NafRequest withLocality(String locality) {
		this.locality = locality;
		return this;
	}
}
	
}
