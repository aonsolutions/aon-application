package solutions.aon.circe;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlTextInput;

import solutions.aon.circe.DatosPersonales.DatosPersonalesBuilder;
import solutions.aon.circe.Domicilio.DomicilioBuilder;
import solutions.aon.circe.exception.SegSocialException;
import solutions.aon.circe.toolkit.HtmlUnitToolkit;

public class Circe {
	public static void main(String[] args) throws Exception {

		String password = "Alma1981";
		FileInputStream certificateIs = new FileInputStream("/home/ndiaz/Descargas/aon.p12");
		String certificateType = KeyStore.getDefaultType();
		String codCirce = "073472648Z";

//		getCirce(certificateIs, password, certificateType, null, null, null);

//		getDatos(certificateIs, password, certificateType, codCirce);
		
		Pattern pattern = Pattern.compile("^(?<calle>[a-z\\s]+)\\s(?<portal>\\d+)\\s(?<cp>\\d{5})\\s(?<provincia>[a-z]+)\\s(?<municipio>[a-z\\-]+)$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher("OCHO DE MARZO 4 28523 MADRID RIVAS-VACIAMADRID");

		matcher.matches();
		System.out.println(matcher.group(0));
		System.out.println(matcher.group("calle"));
		System.out.println(matcher.group("portal"));
		System.out.println(matcher.group("cp"));
		System.out.println(matcher.group("provincia"));
		System.out.println(matcher.group("municipio"));

		
	}

	public static void getCirce(InputStream certificateInputStream, String certificatePassword, String certificateType,
			DatosEmpresa datosEmpresa, Domicilio domicilio, String alias)
			throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			HtmlPage htmlPage = Circe.accesoAreaPAE(webClient);

			htmlPage = htmlPage.getAnchorByHref("/Sociedades/DatosEmpresa/SRL").click();

			HtmlForm mainForm = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("mainform"))
					.orElseThrow();

			mainForm.getInputByName("Actividad.Anagrama").type(datosEmpresa.getAnagrama());
			mainForm.getInputByName("Actividad.FechaInicioActividad")
					.type(Utils.FORMATTER.format(datosEmpresa.getInicioActividad()));
			mainForm.getInputByName("Actividad.FechaCierreEjercicio")
					.type(Utils.FORMATTER.format(datosEmpresa.getCierreEjercicio()));
			mainForm.getInputByName("JuridicoSRL.Url").type(datosEmpresa.getPagWebCorporativa());
			HtmlSelect selectedId = mainForm.getSelectByName("JuridicoSRL.Duracion.Periodo.SelectedId");
			selectedId.getOptionByText(datosEmpresa.getDuracionPersonaJuridica());
			mainForm.getInputByName("JuridicoSRL.Duracion.Cantidad").type(datosEmpresa.getCantidad());
			mainForm.getTextAreaByName("JuridicoSRL.DenominacionSocial").type(datosEmpresa.getDenominacionSocial());
			mainForm.getInputByName("JuridicoSRL.CapitalSocial").type(datosEmpresa.getCapitalSocial());
			mainForm.getInputByName("JuridicoSRL.AcreditaCapitalSocial").click();
			mainForm.getInputByName("JuridicoSRL.DenominacionSocialBolsaRMC").click();
			mainForm.getInputByName("JuridicoSRL.EstatutosTipo").click();
			mainForm.getInputByName("JuridicoSRL.NumParticipaciones").type(datosEmpresa.getNumPaticipaciones());
			mainForm.getInputByName("JuridicoSRL.ImporteParticipacion").type(datosEmpresa.getImporteParticipacion());
			mainForm.getInputByName("JuridicoSRL.PaginaWeb").type(datosEmpresa.getPagWeb());
			mainForm.getInputByName("JuridicoSRL.NumTrabajadores").type(datosEmpresa.getNumPersonasTrabajadoras());
			mainForm.getInputByName("DetalleLicencia.EstaSujeto").click();
			mainForm.getInputByName("DetalleLicencia.EstaSolicitada").click();
			mainForm.getInputByName("DetalleLicencia.Expediente").type(datosEmpresa.getNumExpediente());

			htmlPage = Circe.anadirDomicilio(mainForm, domicilio, alias);
			mainForm = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("mainform")).orElseThrow();
			HtmlSelect domResidenciaSelected = mainForm.getSelectByName("Domicilios.Residencia.Id");
			domResidenciaSelected.getOptionByText(alias);

			htmlPage = Circe.anadirDomicilio(mainForm, domicilio, alias);
			mainForm = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("mainform")).orElseThrow();
			HtmlSelect domFiscalSelected = mainForm.getSelectByName("Domicilios.Fiscal.Id");
			domFiscalSelected.getOptionByText(alias);

			htmlPage = Circe.anadirDomicilio(mainForm, domicilio, alias);
			mainForm = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("mainform")).orElseThrow();
			HtmlSelect domNotificacionSelected = mainForm.getSelectByName("Domicilios.Notificacion.Id");
			domNotificacionSelected.getOptionByText(alias);

			mainForm.getInputByName("Notificaciones.NotificacionTGSS.Telefono").type(datosEmpresa.getTelefono());
			mainForm.getInputByName("Notificaciones.NotificacionTGSS.Email").type(datosEmpresa.getEmail());
			HtmlSelect medioNotificacion = mainForm
					.getSelectByName("Notificaciones.MedioNotificacionDomicilio.SelectedId");
			medioNotificacion.getOptionByText(datosEmpresa.getMedioNotificacion());
			mainForm.getInputByName("Notificaciones.NotificacionAEAT.Prefijo").type(datosEmpresa.getPrefijoPais());
			mainForm.getInputByName("Notificaciones.NotificacionAEAT.Telefono").type(datosEmpresa.getTelefono());
			mainForm.getInputByName("Notificaciones.NotificacionAEAT.Email").type(datosEmpresa.getEmail());
			mainForm.getInputByName("Notificaciones.Comunicaciones.Items[0].Medio.Checked").click();
			mainForm.getInputByName("Notificaciones.Comunicaciones.Items[1].Medio.Checked").click();
			mainForm.getInputByName("Notificaciones.Comunicaciones.Items[1].Medio.Text").type(datosEmpresa.getEmail());
			mainForm.getInputByName("Notificaciones.Comunicaciones.Items[2].Medio.Checked").click();
			mainForm.getInputByName("Notificaciones.Comunicaciones.Items[2].Medio.Text")
					.type(datosEmpresa.getTelefono());
			mainForm.getInputByName("Notificaciones.BoletinInformativo.EnviarBoletin").click();

			mainForm.getButtonByName("Save").click();
		}
	}
	

	public static void getDatos(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String codigoCirce)
			throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException, ParseException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			HtmlPage htmlPage = Circe.accesoAreaPAE(webClient);

			HtmlForm filterId = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("filterId"))
					.orElseThrow();
			filterId.getInputByName("CodCIRCE").setValue(codigoCirce);
			htmlPage.getWebClient().waitForBackgroundJavaScript(30000);
			((HtmlElement) filterId.querySelector("button[type='submit']")).click();
			htmlPage.getWebClient().waitForBackgroundJavaScript(30000);
			htmlPage = ((HtmlElement) htmlPage.querySelector(".table").getLastChild().getFirstChild().getLastChild().getFirstChild()).click();
			System.out.println(htmlPage.asXml());
			DatosPersonales personales = getDatosPersonales (htmlPage);
			//click
			
			
		}
	}
	private static Pattern pattern = Pattern.compile("^(?<calle>[a-z\\s]+)\\s(?<portal>\\d+)\\s(?<cp>\\d{5})\\s(?<provincia>[a-z]+)\\s(?<municipio>[a-z\\-]+)$", Pattern.CASE_INSENSITIVE);
	private static DatosPersonales getDatosPersonales(HtmlPage htmlPage) throws ParseException {
		
		DatosPersonalesBuilder builder = new DatosPersonalesBuilder();
		
		String tipoDocumento = htmlPage.getElementById("select2-Document_Types_SelectedId-container").getAttribute("value");
		builder.tipoDocIdentidad(tipoDocumento);
		
		String documento = ((HtmlTextInput) htmlPage.getElementById("Document_Value")).getValue();
		builder.documentoIdentidad(documento);
		
		String fechaNacimiento = ((HtmlTextInput) htmlPage.getElementById("FechaNacimiento")).getValue();
		builder.fechaNacimiento(Utils.FORMATTER.parse(fechaNacimiento));
		
		String nacionalidad = htmlPage.getElementById("select2-Nacionalidades_SelectedId-container").getAttribute("value");
		builder.nacionalidad(nacionalidad);
		
		String sexo = htmlPage.getElementById("select2-Sexos_SelectedId-container").getAttribute("value");
		builder.sexo(sexo);
		
		String nombre = ((HtmlTextInput) htmlPage.getElementById("Nombre")).getValue();
		builder.nombre(nombre);
		
		String primerApellido = ((HtmlTextInput) htmlPage.getElementById("Apellido1")).getValue();
		builder.primerApellido(primerApellido);
				
		String segundoApellido = ((HtmlTextInput) htmlPage.getElementById("Apellido2")).getValue();
		builder.segundoApellido(segundoApellido);
		
		String dominio = ((HtmlTextInput) htmlPage.getElementById("DireccionInternet")).getValue();
		builder.domino(dominio);
		
		String estadoCivil = htmlPage.getElementById("select2-EstadosCivil_SelectedId-container").getAttribute("value");
		builder.estadoCivil(estadoCivil);
		
		String fechaEstadoCivil = ((HtmlTextInput) htmlPage.getElementById("FechaEstadoCivil")).getValue();
		builder.fechaEstadoCivil(Utils.FORMATTER.parse(fechaEstadoCivil));
		
		DomicilioBuilder domResBuilder = new DomicilioBuilder();
		String domicilioResidencia = htmlPage.getElementById("select2-Domicilios_Residencia_Id-container").getAttribute("value");
		Matcher matcher = pattern.matcher(domicilioResidencia);
		
		Domicilio domResidencia = domResBuilder.build();
		builder.domicilioResidencia(domResidencia);
		
		
		
		
		DomicilioBuilder domFisBuilder = new DomicilioBuilder();
		String domicilioFiscal = htmlPage.getElementById("select2-Domicilios_Fiscal_Id-container").getAttribute("value");
		matcher = pattern.matcher(domicilioFiscal);
		
		Domicilio domFiscal = domFisBuilder.build();
		builder.domicilioFiscal(domFiscal);
		
		
		
		
		DomicilioBuilder domNotBuilder = new DomicilioBuilder();
		String domicilioNotificacion = htmlPage.getElementById("select2-Domicilios_Notificacion_Id-container").getAttribute("value");
		matcher = pattern.matcher(domicilioNotificacion);
		
		Domicilio domNotificacion= domNotBuilder.build();
		builder.domicilioNotificacion(domNotificacion);
		
		
		
		
		String telefonoNotificacionTGSS = ((HtmlTextInput) htmlPage.getElementById("Notificaciones_NotificacionTGSS_Telefono")).getValue();
		builder.telefonoNotificacionTGSS(Integer.parseInt(telefonoNotificacionTGSS));
		
		String emailNotificacionTGSS = ((HtmlTextInput) htmlPage.getElementById("Notificaciones_NotificacionTGSS_Email")).getValue();
		builder.emailNotificacionTGSS(emailNotificacionTGSS);
		
		String prefijo = ((HtmlTextInput) htmlPage.getElementById("Notificaciones_NotificacionAEAT_Prefijo")).getValue();
		builder.prefijoPais(prefijo);
		
		String telefonoNotificacionAEAT = ((HtmlTextInput) htmlPage.getElementById("Notificaciones_NotificacionAEAT_Telefono")).getValue();
		builder.telefonoNotificacionTGSS(Integer.parseInt(telefonoNotificacionAEAT));
		
		String emailNotificacionAEAT = ((HtmlTextInput) htmlPage.getElementById("Notificaciones_NotificacionAEAT_Email")).getValue();
		builder.emailNotificacionTGSS(emailNotificacionAEAT);
		
		String emailNotificacionPYME = ((HtmlTextInput) htmlPage.getElementById("Notificaciones_Comunicaciones_Items_1__Medio_Text")).getValue();
		builder.emailNotificacionTGSS(emailNotificacionPYME);
		
		String telefonoNotificacionPYME = ((HtmlTextInput) htmlPage.getElementById("Notificaciones_Comunicaciones_Items_2__Medio_Text")).getValue();
		builder.telefonoNotificacionTGSS(Integer.parseInt(telefonoNotificacionPYME));

		return builder.build();
	}

	public static HtmlPage anadirDomicilio(HtmlForm mainForm, Domicilio domicilio, String alias)
			throws IOException, InterruptedException {

		HtmlPage htmlPage = ((HtmlElement) mainForm.querySelector("button[data-control='address-button']")).click();
		((HtmlElement) htmlPage.querySelector("button[id='btnNewAddressId']")).click();
		HtmlForm addressForm = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("addressFormId"))
				.orElseThrow();
		addressForm.getInputByName("Alias").type(alias);
		HtmlSelect tipoViaSelected = addressForm.getSelectByName("TipoVia.SelectedId");
		tipoViaSelected.getOptionByText(domicilio.getTipoVia());
		addressForm.getInputByName("NombreVia").type(domicilio.getNombreVia());
		addressForm.getInputByName("KM").type(domicilio.getKm());
		addressForm.getInputByName("Numero").type(domicilio.getNum());
		HtmlSelect calificadorNumSelected = addressForm.getSelectByName("CalificadorNumero.SelectedId");
		calificadorNumSelected.getOptionByText(domicilio.getCalificadorNum());
		addressForm.getInputByName("Bloque").type(domicilio.getBloque());
		addressForm.getInputByName("Portal").type(domicilio.getPortal());
		addressForm.getInputByName("NumeroPiso").type(domicilio.getPiso());
		addressForm.getInputByName("Escalera").type(domicilio.getEscalera());
		addressForm.getInputByName("Puerta").type(domicilio.getPuerta());
		addressForm.getInputByName("ComplementoDomicilio").type(domicilio.getCoplemnetoDomicilio());
		HtmlSelect paisSelected = addressForm.getSelectByName("Pais.SelectedId");
		paisSelected.getOptionByText(domicilio.getPais());
		HtmlSelect provinciaSelected = addressForm.getSelectByName("Provincia.SelectedId");
		provinciaSelected.getOptionByText(domicilio.getProvincia());
		HtmlSelect municipioSelected = addressForm.getSelectByName("Municipio.SelectedId");
		municipioSelected.getOptionByText(domicilio.getMunicipio());
		addressForm.getInputByName("Localidad").type(domicilio.getLocalidad());
		addressForm.getInputByName("CodigoPostal").type(domicilio.getCodigoPostal());
		HtmlSelect indicadorRefCatSelected = addressForm.getSelectByName("TipoReferenciaCatastral.SelectedId");
		indicadorRefCatSelected.getOptionByText(domicilio.getIndicadorReferenciaCatastral());
		addressForm.getInputByName("ReferenciaCatastral").type(domicilio.getReferenciaCataastral());
		htmlPage = ((HtmlElement) addressForm.querySelector("button[id='btnAcceptAddressId']")).click();
		htmlPage = htmlPage.getElementById("btnAcceptChangesId").click();

		return htmlPage;
	}

	public static HtmlPage accesoAreaPAE(WebClient webClient)
			throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {

		HtmlPage htmlPage = webClient.getPage("https://paeelectronico.circe.es");
		HtmlUnitToolkit.checkStatusAndDown(htmlPage);

		HtmlForm sending = HtmlUnitToolkit.wait4(htmlPage, p -> p.getFormByName("sending")).orElseThrow();

		htmlPage = sending.getButtonByName("send").click();
		HtmlUnitToolkit.checkStatusAndDown(htmlPage);

		HtmlSelect lenguageSelect = (HtmlSelect) htmlPage.getElementById("language-select");
		htmlPage = lenguageSelect.getOptionByText("Español").click();

		DomNodeList<DomNode> idpButtons = htmlPage.querySelectorAll(".idp-button");

		for (DomNode idpButton : idpButtons) {
			if (idpButton.getTextContent().contains("Certificado")) {
				htmlPage = ((HtmlButton) idpButton).click();
				break;
			}
		}
		return htmlPage;
	}

}
