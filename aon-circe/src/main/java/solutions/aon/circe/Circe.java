package solutions.aon.circe;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.text.ParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.Page;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableBody;
import org.htmlunit.html.HtmlTableRow;
import org.htmlunit.html.HtmlTextArea;
import org.htmlunit.html.HtmlTextInput;

import solutions.aon.circe.Actividades.ActividadesBuilder;
import solutions.aon.circe.DatosPersonales.DatosPersonalesBuilder;
import solutions.aon.circe.Domicilio.DomicilioBuilder;
import solutions.aon.circe.SeguridadSocial.SeguridadSocialBuilder;
import solutions.aon.circe.exception.SegSocialException;
import solutions.aon.circe.toolkit.HtmlUnitToolkit;

public class Circe {
	public static void main(String[] args) throws Exception {

		String password = "Alma1981";
		FileInputStream certificateIs = new FileInputStream("/home/ndiaz/Descargas/aon.p12");
		String certificateType = KeyStore.getDefaultType();
//		String codCirce = "073701711M";
		String codCirce = "073472648Z";
//		getCirce(certificateIs, password, certificateType, null, null, null);

//		getDatos(certificateIs, password, certificateType, codCirce);

//		imprimirDUEAutonomo(certificateIs, password, certificateType, codCirce);
		
		try (FileOutputStream pdfDUE = new FileOutputStream(
				"/home/ndiaz/eclipse-workspace/aon.parent/aon-circe/src/main/java/DueAutonomo.pdf")) {
			pdfDUE.write(imprimirDUEAutonomo(certificateIs, password, certificateType, codCirce));
		}
	
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

	public static byte[] imprimirDUEAutonomo(InputStream certificateInputStream, String certificatePassword, String certificateType,
			String codigoCirce) throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {
		
		byte[] pdf = null;
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			HtmlPage htmlPage = Circe.accesoAreaPAE(webClient);

			HtmlForm filterId = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("filterId"))
					.orElseThrow();
			filterId.getInputByName("CodCIRCE").setValue(codigoCirce);
			htmlPage.getWebClient().waitForBackgroundJavaScript(5000);
			((HtmlElement) filterId.querySelector("button[type='submit']")).click();
			htmlPage.getWebClient().waitForBackgroundJavaScript(5000);
			List<HtmlAnchor> anchors = htmlPage.getAnchors();
			for (HtmlAnchor anchor : anchors) {
				if (anchor.getHrefAttribute().startsWith("/autonomo/personaldata/Index?")) {
					htmlPage = anchor.click();
				}
			}

			webClient.waitForBackgroundJavaScript(10000);
			
			anchors = htmlPage.getAnchors();
			for (HtmlAnchor anchor : anchors) {
				if (anchor.getHrefAttribute().startsWith("/autonomo/documentos/Index?")) {
					htmlPage = anchor.click();
				}
			}

			webClient.waitForBackgroundJavaScript(10000);
			
			System.out.println(htmlPage.asXml());
			
			DomElement documentos = htmlPage.getElementById("maingrid");
			HtmlTable tablaDocumentos = (HtmlTable) documentos.querySelector("table[class='table']");
			List<HtmlTableBody> bodiesDocumentos = tablaDocumentos.getBodies();
			for (HtmlTableBody htmlTableBody : bodiesDocumentos) {
				List<HtmlTableRow> rows = htmlTableBody.getRows();
				Page page = ((HtmlElement) rows.getLast().getLastChild().getFirstChild()).click();
				pdf = page.getWebResponse().getContentAsStream().readAllBytes();
			}
			
		}
		return pdf;
	}
	
	public static byte[] imprimirDUESociedades(InputStream certificateInputStream, String certificatePassword, String certificateType,
			String codigoCirce) throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {
		
		byte[] pdf = null;
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			HtmlPage htmlPage = Circe.accesoAreaPAE(webClient);

			HtmlForm filterId = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("filterId"))
					.orElseThrow();
			filterId.getInputByName("CodCIRCE").setValue(codigoCirce);
			htmlPage.getWebClient().waitForBackgroundJavaScript(30000);
			((HtmlElement) filterId.querySelector("button[type='submit']")).click();
			htmlPage.getWebClient().waitForBackgroundJavaScript(30000);
			List<HtmlAnchor> anchors = htmlPage.getAnchors();
			for (HtmlAnchor anchor : anchors) {
				if (anchor.getHrefAttribute().startsWith("/sociedades/DatosEmpresa/SRL?")) {
					htmlPage = anchor.click();
				}
			}

			webClient.waitForBackgroundJavaScript(5000);
			anchors = htmlPage.getAnchors();
			for (HtmlAnchor anchor : anchors) {
				if (anchor.getHrefAttribute().startsWith("/Imprimir/Due?area=&")) {
					Page page = anchor.click();
					pdf = page.getWebResponse().getContentAsStream().readAllBytes();

				}
			}
		}
		return pdf;
	}
	
	public static void getDatos(InputStream certificateInputStream, String certificatePassword, String certificateType,
			String codigoCirce) throws FailingHttpStatusCodeException, IOException, SegSocialException,
			InterruptedException {

		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
			HtmlPage htmlPage = Circe.accesoAreaPAE(webClient);

			HtmlForm filterId = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("filterId"))
					.orElseThrow();
			filterId.getInputByName("CodCIRCE").setValue(codigoCirce);
			htmlPage.getWebClient().waitForBackgroundJavaScript(30000);
			((HtmlElement) filterId.querySelector("button[type='submit']")).click();
			htmlPage.getWebClient().waitForBackgroundJavaScript(30000);
			List<HtmlAnchor> anchors = htmlPage.getAnchors();
			for (HtmlAnchor anchor : anchors) {
				if (anchor.getHrefAttribute().startsWith("/autonomo/personaldata/Index?")) {
					htmlPage = anchor.click();
				}
			}

			webClient.waitForBackgroundJavaScript(5000);

//			DatosPersonales personales = getDatosPersonales(htmlPage);

			anchors = htmlPage.getAnchors();
			for (HtmlAnchor anchor : anchors) {
				if (anchor.getHrefAttribute().startsWith("/autonomo/actividades/Index?")) {
					htmlPage = anchor.click();
				}
			}

			webClient.waitForBackgroundJavaScript(5000);

//			Actividades actividades = getActividades(htmlPage);

			anchors = htmlPage.getAnchors();
			for (HtmlAnchor anchor : anchors) {
				if (anchor.getHrefAttribute().startsWith("/autonomo/seguridadsocial/Index?")) {
					htmlPage = anchor.click();
				}
			}

			webClient.waitForBackgroundJavaScript(5000);

//			SeguridadSocial seguridadSocial = getSeguridadSocial(htmlPage);

			anchors = htmlPage.getAnchors();
			for (HtmlAnchor anchor : anchors) {
				if (anchor.getHrefAttribute().startsWith("/autonomo/trabajador/Index?")) {
					htmlPage = anchor.click();
				}
			}

			webClient.waitForBackgroundJavaScript(5000);

//			PersonasTrabajadoras personasTrabajadoras = getPersonasTrabajadoras(htmlPage);
		}
	}

	private static Pattern pattern = Pattern.compile(
			"^(?<calle>[a-z\\s]+)\\s(?<portal>\\d+)\\s(?<cp>\\d{5})\\s(?<provincia>[a-z]+)\\s(?<municipio>[a-z\\-]+)$",
			Pattern.CASE_INSENSITIVE);

	private static DatosPersonales getDatosPersonales(HtmlPage htmlPage) throws ParseException {

		DatosPersonalesBuilder builder = new DatosPersonalesBuilder();

		String tipoDocumento = htmlPage.getElementById("select2-Document_Types_SelectedId-container")
				.getAttribute("value");
		builder.tipoDocIdentidad(tipoDocumento);

		String documento = ((HtmlTextInput) htmlPage.getElementById("Document_Value")).getValue();
		builder.documentoIdentidad(documento);

		String fechaNacimiento = ((HtmlTextInput) htmlPage.getElementById("FechaNacimiento")).getValue();
		builder.fechaNacimiento(Utils.FORMATTER.parse(fechaNacimiento));

		String nacionalidad = htmlPage.getElementById("select2-Nacionalidades_SelectedId-container")
				.getAttribute("value");
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
		String domicilioResidencia = htmlPage.getElementById("select2-Domicilios_Residencia_Id-container")
				.getAttribute("value");
		Matcher matcher = pattern.matcher(domicilioResidencia);
		matcher.matches();
		String calleResidencia = matcher.group(1);
		String portalResidencia = matcher.group(2);
		String cpResidencia = matcher.group(3);
		String provinciaResidencia = matcher.group(4);
		String municipioResidencia = matcher.group(5);
		domResBuilder.nombreVia(calleResidencia);
		domResBuilder.portal(portalResidencia);
		domResBuilder.codigoPostal(Integer.parseInt(cpResidencia));
		domResBuilder.provincia(provinciaResidencia);
		domResBuilder.municipio(municipioResidencia);
		Domicilio domResidencia = domResBuilder.build();
		builder.domicilioResidencia(domResidencia);

		DomicilioBuilder domFisBuilder = new DomicilioBuilder();
		String domicilioFiscal = htmlPage.getElementById("select2-Domicilios_Fiscal_Id-container")
				.getAttribute("value");
		matcher = pattern.matcher(domicilioFiscal);
		matcher.matches();
		String calleFiscal = matcher.group(1);
		String portalFiscal = matcher.group(2);
		String cpFisca = matcher.group(3);
		String provinciaFiscal = matcher.group(4);
		String municipioFiscal = matcher.group(5);
		domResBuilder.nombreVia(calleFiscal);
		domResBuilder.portal(portalFiscal);
		domResBuilder.codigoPostal(Integer.parseInt(cpFisca));
		domResBuilder.provincia(provinciaFiscal);
		domResBuilder.municipio(municipioFiscal);
		Domicilio domFiscal = domFisBuilder.build();
		builder.domicilioFiscal(domFiscal);

		DomicilioBuilder domNotBuilder = new DomicilioBuilder();
		String domicilioNotificacion = htmlPage.getElementById("select2-Domicilios_Notificacion_Id-container")
				.getAttribute("value");
		matcher = pattern.matcher(domicilioNotificacion);
		matcher.matches();
		String calleNotificacion = matcher.group(1);
		String portalNotificacion = matcher.group(2);
		String cpNotificacion = matcher.group(3);
		String provinciaNotificacion = matcher.group(4);
		String municipioNotificacion = matcher.group(5);
		domResBuilder.nombreVia(calleNotificacion);
		domResBuilder.portal(portalNotificacion);
		domResBuilder.codigoPostal(Integer.parseInt(cpNotificacion));
		domResBuilder.provincia(provinciaNotificacion);
		domResBuilder.municipio(municipioNotificacion);
		Domicilio domNotificacion = domNotBuilder.build();
		builder.domicilioNotificacion(domNotificacion);

		String telefonoNotificacionTGSS = ((HtmlTextInput) htmlPage
				.getElementById("Notificaciones_NotificacionTGSS_Telefono")).getValue();
		builder.telefonoNotificacionTGSS(Integer.parseInt(telefonoNotificacionTGSS));

		String emailNotificacionTGSS = ((HtmlTextInput) htmlPage
				.getElementById("Notificaciones_NotificacionTGSS_Email")).getValue();
		builder.emailNotificacionTGSS(emailNotificacionTGSS);

		String prefijo = ((HtmlTextInput) htmlPage.getElementById("Notificaciones_NotificacionAEAT_Prefijo"))
				.getValue();
		builder.prefijoPais(prefijo);

		String telefonoNotificacionAEAT = ((HtmlTextInput) htmlPage
				.getElementById("Notificaciones_NotificacionAEAT_Telefono")).getValue();
		builder.telefonoNotificacionTGSS(Integer.parseInt(telefonoNotificacionAEAT));

		String emailNotificacionAEAT = ((HtmlTextInput) htmlPage
				.getElementById("Notificaciones_NotificacionAEAT_Email")).getValue();
		builder.emailNotificacionTGSS(emailNotificacionAEAT);

		String emailNotificacionPYME = ((HtmlTextInput) htmlPage
				.getElementById("Notificaciones_Comunicaciones_Items_1__Medio_Text")).getValue();
		builder.emailNotificacionTGSS(emailNotificacionPYME);

		String telefonoNotificacionPYME = ((HtmlTextInput) htmlPage
				.getElementById("Notificaciones_Comunicaciones_Items_2__Medio_Text")).getValue();
		builder.telefonoNotificacionTGSS(Integer.parseInt(telefonoNotificacionPYME));

		return builder.build();
	}

	private static Actividades getActividades(HtmlPage htmlPage) throws ParseException {

		ActividadesBuilder builder = new ActividadesBuilder();

		String superficieTotal = ((HtmlTextInput) htmlPage.getElementById("CentroActividad_SuperficieTotal"))
				.getValue();
		builder.superficieTotal(Integer.parseInt(superficieTotal));

		String superficieComputable = ((HtmlTextInput) htmlPage.getElementById("CentroActividad_SuperficieComputable"))
				.getValue();
		builder.superficieComputable(Integer.parseInt(superficieComputable));

		String superficieRectificada = ((HtmlTextInput) htmlPage
				.getElementById("CentroActividad_SuperficieRectificada")).getValue();
		builder.superficieRectificada(Integer.parseInt(superficieRectificada));

		String numReferencia = ((HtmlTextInput) htmlPage.getElementById("CentroActividad_NumeroReferencia")).getValue();
		builder.numReferencia(Integer.parseInt(numReferencia));

		DomicilioBuilder domicilioBuilder = new DomicilioBuilder();
		String domicilioActividades = htmlPage.getElementById("select2-CentroActividad_Domicilio_Id-container")
				.getAttribute("value");
		Matcher matcher = pattern.matcher(domicilioActividades);
		matcher.matches();
		String calle = matcher.group(1);
		String portal = matcher.group(2);
		String cp = matcher.group(3);
		String provincia = matcher.group(4);
		String municipio = matcher.group(5);
		domicilioBuilder.nombreVia(calle);
		domicilioBuilder.portal(portal);
		domicilioBuilder.codigoPostal(Integer.parseInt(cp));
		domicilioBuilder.provincia(provincia);
		domicilioBuilder.municipio(municipio);
		Domicilio domicilio = domicilioBuilder.build();
		builder.domicilio(domicilio);

		String nombreComercial = ((HtmlTextInput) htmlPage.getElementById("DatosActividad_NombreComercial")).getValue();
		builder.nombreComercial(nombreComercial);

		String inicioActividad = ((HtmlTextInput) htmlPage.getElementById("DatosActividad_FechaInicioActividad"))
				.getValue();
		builder.inicioActividad(Utils.FORMATTER.parse(inicioActividad));

		String numTrabajadores = ((HtmlTextInput) htmlPage.getElementById("DatosActividad_NumTrabajadores")).getValue();
		builder.numTrabajadores(Integer.parseInt(numTrabajadores));

		DomElement cnae = htmlPage.getElementById("gridCNAETargetId");
		HtmlTable tableCNAE = (HtmlTable) cnae.querySelector("table[class='table']");
		List<HtmlTableBody> bodiesCNAE = tableCNAE.getBodies();
		for (HtmlTableBody htmlTableBody : bodiesCNAE) {
			List<HtmlTableRow> rows = htmlTableBody.getRows();
			for (HtmlTableRow row : rows) {
				String claveCNAE = row.getLastChild().getPreviousSibling().getTextContent();
				builder.claveCNAE(Integer.parseInt(claveCNAE));
			}
		}

		DomElement iae = htmlPage.getElementById("gridIaeTargetId");
		HtmlTable tableIAE = (HtmlTable) iae.querySelector("table[class='table']");
		List<HtmlTableBody> bodiesIAE = tableIAE.getBodies();
		for (HtmlTableBody htmlTableBody : bodiesIAE) {
			List<HtmlTableRow> rows = htmlTableBody.getRows();
			for (HtmlTableRow row : rows) {
				String claveIAE = row.getLastChild().getPreviousSibling().getPreviousSibling().getTextContent();
				builder.claveIAE(Integer.parseInt(claveIAE));
			}
		}

		String realizarComunicacion = ((HtmlCheckBoxInput) htmlPage
				.getElementById("CentroTrabajo_AperturaCentro_ComunicacionApertura")).getValue();
		builder.realizarComunicacion(Boolean.parseBoolean(realizarComunicacion));

		DomElement fueraLocal = htmlPage.getElementById("gridFueraLocalTargetId");
		HtmlTable tableFueraLocal = (HtmlTable) fueraLocal.querySelector("table[class='table']");
		List<HtmlTableBody> bodiesFueraLocal = tableFueraLocal.getBodies();
		for (HtmlTableBody htmlTableBody : bodiesFueraLocal) {
			List<HtmlTableRow> rows = htmlTableBody.getRows();
			for (HtmlTableRow row : rows) {
				String epigrafeFueraLocal = row.getFirstChild().getNextSibling().getNextSibling().getTextContent();
				builder.epigrafeAELugarFueraDelLocal(Integer.parseInt(epigrafeFueraLocal));
			}
		}

		DomElement localAfectado = htmlPage.getElementById("gridFueraLocalTargetId");
		HtmlTable tableLocalAfectado = (HtmlTable) localAfectado.querySelector("table[class='table']");
		List<HtmlTableBody> bodiesLocalAfectado = tableLocalAfectado.getBodies();
		for (HtmlTableBody htmlTableBody : bodiesLocalAfectado) {
			List<HtmlTableRow> rows = htmlTableBody.getRows();
			for (HtmlTableRow row : rows) {
				String epigrafeLocalAfectado = row.getFirstChild().getNextSibling().getNextSibling().getTextContent();
				builder.epigrafeAELocalAfectado(Integer.parseInt(epigrafeLocalAfectado));
			}
		}

		return builder.build();

	}

	private static SeguridadSocial getSeguridadSocial(HtmlPage htmlPage) throws ParseException {

		SeguridadSocialBuilder builder = new SeguridadSocialBuilder();

		String tipoAutonomo = htmlPage.getElementById("select2-TipoAutonomo_SelectedId-container")
				.getAttribute("value");
		builder.tipoAutonomo(tipoAutonomo);

		String nombre = ((HtmlTextInput) htmlPage.getElementById("Persona_Nombre")).getValue();
		builder.nombre(nombre);

		String docIdentidad = ((HtmlTextInput) htmlPage.getElementById("Persona_NumeroDocumento")).getValue();
		builder.docIdentidad(docIdentidad);

		String nss = ((HtmlTextInput) htmlPage.getElementById("Persona_NSSNAF")).getValue();
		builder.nss(nss);

		String solicitudNumAfiliacionSS = ((HtmlCheckBoxInput) htmlPage
				.getElementById("AsignacionNSS_AltaSeguridadSocial")).getValue();
		builder.solicitudNumAfiliacionSS(Boolean.parseBoolean(solicitudNumAfiliacionSS));

		String cnae = htmlPage.getElementById("ListaCNAE").getAttribute("value");
		builder.cnae(cnae);

		String nombreApellidosRepresentante = ((HtmlTextInput) htmlPage.getElementById("Representante_NombreRazon"))
				.getValue();
		builder.nombreApellidosRepresentante(nombreApellidosRepresentante);

		String tipoDocIdentidadRepresentante = htmlPage
				.getElementById("select2-Representante_DocIdentidad_Types_SelectedId-container").getAttribute("value");
		builder.tipoDocIdentidadRepresentante(tipoDocIdentidadRepresentante);

		String docIdentidadRepresentante = ((HtmlTextInput) htmlPage.getElementById("Representante_DocIdentidad_Value"))
				.getValue();
		builder.docIdentidadRepresentante(docIdentidadRepresentante);

		String nssRepresentante = ((HtmlTextInput) htmlPage.getElementById("Representante_NSS")).getValue();
		builder.nssrepresentante(nssRepresentante);

		String regimen = ((HtmlTextInput) htmlPage.getElementById("Regimen_Regimen")).getValue();
		builder.regimen(regimen);

		String grupo = ((HtmlTextArea) htmlPage.getElementById("Regimen_Grupo")).getText();
		builder.grupo(grupo);

		String trl = ((HtmlTextInput) htmlPage.getElementById("Regimen_TRL")).getValue();
		builder.trl(trl);

		String subgrupo = ((HtmlTextInput) htmlPage.getElementById("Regimen_Subgrupo")).getValue();
		builder.subgrupo(subgrupo);

		String integradoColegioProfesional = ((HtmlCheckBoxInput) htmlPage
				.getElementById("IntegradoColegioProfesional")).getValue();
		builder.integradoColegioProfesional(Boolean.parseBoolean(integradoColegioProfesional));

		String colegioProfesional = htmlPage.getElementById("select2-selection select2-selection--single")
				.getAttribute("value");
		builder.colegioProfesional(colegioProfesional);

		String discapacidad = ((HtmlCheckBoxInput) htmlPage.getElementById("Incapacidad_Minusvalido")).getValue();
		builder.discapacidad(Boolean.parseBoolean(discapacidad));

		String tipoDiscapacidad = htmlPage.getElementById("select2-Incapacidad_TipoDiscapacidad_SelectedId-container")
				.getAttribute("value");
		builder.tipoDiscapacidad(tipoDiscapacidad);

		String gradoDiscapacidad = ((HtmlTextInput) htmlPage.getElementById("Incapacidad_GradoIncapacidad")).getValue();
		builder.gradoDiscapacidad(Integer.parseInt(gradoDiscapacidad));

		String fechaEfectoDiscapacidad = ((HtmlTextInput) htmlPage.getElementById("Incapacidad_FechaMinusvalia"))
				.getValue();
		builder.fechaEfectoDiscapacidad(Utils.FORMATTER.parse(fechaEfectoDiscapacidad));

		//
		//
		//

		String baseCotizacion = ((HtmlTextInput) htmlPage.getElementById("BaseCotizacion_Valor")).getValue();
		builder.baseCotizacion(Float.parseFloat(baseCotizacion));

		String rendimientoNetoAnual = ((HtmlTextInput) htmlPage
				.getElementById("BaseCotizacion_RendimientosNetosAnuales")).getValue();
		builder.rendimientoNetoAnual(Float.parseFloat(rendimientoNetoAnual));

		String mutuaIT = htmlPage.getElementById("select2-TipoMutuaIT_SelectedId-container").getAttribute("value");
		builder.mutuaIT(mutuaIT);

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
