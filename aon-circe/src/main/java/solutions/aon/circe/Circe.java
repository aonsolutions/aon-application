package solutions.aon.circe;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.KeyStore;

import javax.swing.plaf.synth.SynthOptionPaneUI;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.WebRequest;
import org.htmlunit.WebResponse;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.util.WebClientUtils;
import org.htmlunit.util.WebConnectionWrapper;

import solutions.aon.circe.exception.SegSocialException;
import solutions.aon.circe.toolkit.HtmlUnitToolkit;

public class Circe {
	public static void main(String[] args) throws Exception {

		String password = "Alma1981";
		FileInputStream certificateIs = new FileInputStream("/home/ndiaz/Descargas/aon.p12");
		String certificateType = KeyStore.getDefaultType();
		String codCirce = "073472648Z";

//		getCirce(certificateIs, password, certificateType, null, null, null);
		
		getDatosCirce(certificateIs, password, certificateType, codCirce);
	}

	public static void getCirce(InputStream certificateInputStream, String certificatePassword, String certificateType,
			DatosEmpresa datosEmpresa, Domicilio domicilio, String alias)
			throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {

		HtmlPage htmlPage = Circe.accesoAreaPAE(certificateInputStream, certificatePassword, certificateType);

		htmlPage = htmlPage.getAnchorByHref("/Sociedades/DatosEmpresa/SRL").click();

		HtmlForm mainForm = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("mainform")).orElseThrow();

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
		HtmlSelect medioNotificacion = mainForm.getSelectByName("Notificaciones.MedioNotificacionDomicilio.SelectedId");
		medioNotificacion.getOptionByText(datosEmpresa.getMedioNotificacion());
		mainForm.getInputByName("Notificaciones.NotificacionAEAT.Prefijo").type(datosEmpresa.getPrefijoPais());
		mainForm.getInputByName("Notificaciones.NotificacionAEAT.Telefono").type(datosEmpresa.getTelefono());
		mainForm.getInputByName("Notificaciones.NotificacionAEAT.Email").type(datosEmpresa.getEmail());
		mainForm.getInputByName("Notificaciones.Comunicaciones.Items[0].Medio.Checked").click();
		mainForm.getInputByName("Notificaciones.Comunicaciones.Items[1].Medio.Checked").click();
		mainForm.getInputByName("Notificaciones.Comunicaciones.Items[1].Medio.Text").type(datosEmpresa.getEmail());
		mainForm.getInputByName("Notificaciones.Comunicaciones.Items[2].Medio.Checked").click();
		mainForm.getInputByName("Notificaciones.Comunicaciones.Items[2].Medio.Text").type(datosEmpresa.getTelefono());
		mainForm.getInputByName("Notificaciones.BoletinInformativo.EnviarBoletin").click();

		mainForm.getButtonByName("Save").click();
	}

	public static void getDatosCirce(InputStream certificateInputStream, String certificatePassword,
			String certificateType, String codigoCirce)
			throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {

		HtmlPage htmlPage = Circe.accesoAreaPAE(certificateInputStream, certificatePassword, certificateType);
		
		htmlPage.getWebClient().setJavaScriptTimeout(30000);
		htmlPage.getWebClient().getOptions().setCssEnabled(true);
		htmlPage.getWebClient().getOptions().setDownloadImages(true);
		htmlPage.getWebClient().getOptions().setJavaScriptEnabled(true);
		
		new WebConnectionWrapper(htmlPage.getWebClient()) {
			@Override
			public WebResponse getResponse(WebRequest request) throws IOException {
				System.out.println(request.getUrl());
				System.out.println(request.getHttpMethod());
				System.out.println(request.getRequestBody());
				WebResponse response =  super.getResponse(request);
				System.out.println(response.getContentAsString());
				return response;
			}
		};
		HtmlForm filterId = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("filterId")).orElseThrow();
//		filterId.getInputByName("CodCIRCE").setValue(codigoCirce);
		htmlPage.getWebClient().waitForBackgroundJavaScript(30000);
		((HtmlElement) filterId.querySelector("button[type='submit']")).click();
		
//		HtmlAnchor htmlAnchor = HtmlUnitToolkit.wait4(htmlPage, p -> p.getAnchorByHref("/autonomo/personaldata/Index?_id=HH1xpbbt5eo=")).orElseThrow();
//		htmlPage = htmlAnchor.click();
//		System.out.println(htmlPage.asXml());
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

	public static HtmlPage accesoAreaPAE(InputStream certificateInputStream, String certificatePassword,
			String certificateType)
			throws FailingHttpStatusCodeException, IOException, SegSocialException, InterruptedException {
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {

			WebClientUtils.attachVisualDebugger(webClient);

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

}
