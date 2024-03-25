package solutions.aon.circe;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.security.KeyStore;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;

import solutions.aon.circe.exception.SegSocialException;
import solutions.aon.circe.toolkit.HtmlUnitToolkit;

public class Circe {
	public static void main(String[] args) throws Exception {
		
		String password = "Alma1981";
		FileInputStream certificateIs = new FileInputStream("/home/ndiaz/Descargas/aon.p12");
		String certificateType = KeyStore.getDefaultType();
		
//		getCirce(certificateIs, password, certificateType, null, null);
	}
	
	public static void getCirce( InputStream certificateInputStream, String certificatePassword, String certificateType, DatosEmpresa datosEmpresa, Domicilio domicilio, String alias) throws FailingHttpStatusCodeException, MalformedURLException, IOException, SegSocialException, InterruptedException {
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword,
				certificateType)) {
						
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
		
		htmlPage = htmlPage.getAnchorByHref("/Sociedades/DatosEmpresa/SRL").click();
		
		HtmlForm mainForm = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("mainform")).orElseThrow();
		
		mainForm.getInputByName("Actividad.Anagrama").type(datosEmpresa.getAnagrama());
		mainForm.getInputByName("Actividad.FechaInicioActividad").type(Utils.FORMATTER.format(datosEmpresa.getInicioActividad()));
		mainForm.getInputByName("Actividad.FechaCierreEjercicio").type(Utils.FORMATTER.format(datosEmpresa.getCierreEjercicio()));
		mainForm.getInputByName("JuridicoSRL.Url").type(datosEmpresa.getPagWebCorporativa());
		HtmlSelect 	selectedId = mainForm.getSelectByName("JuridicoSRL.Duracion.Periodo.SelectedId");
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
	    
	    //Añadir un nuevo domicilio
	    
	    htmlPage = ((HtmlElement) mainForm.querySelector("button[data-control='address-button']")).click();
	    ((HtmlElement) htmlPage.querySelector("button[id='btnNewAddressId']")).click();
		HtmlForm addressForm = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("addressFormId")).orElseThrow();
		addressForm.getInputByName("Alias").type(alias);
		HtmlSelect 	tipoViaSelected = addressForm.getSelectByName("TipoVia.SelectedId");
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
	    
		mainForm = (HtmlForm) HtmlUnitToolkit.wait4(htmlPage, p -> p.getElementById("mainform")).orElseThrow();
		HtmlSelect 	domResidenciaSelected = mainForm.getSelectByName("Domicilios.Residencia.Id");
		domResidenciaSelected.getOptionByText(datosEmpresa.getDomicilioSocial().toString());
	    
	    

		mainForm.getInputByName("Notificaciones.NotificacionTGSS.Telefono").type(datosEmpresa.getTelefono());
		mainForm.getInputByName("Notificaciones.NotificacionTGSS.Email").type(datosEmpresa.getEmail());
		HtmlSelect 	medioNotificacion = mainForm.getSelectByName("Notificaciones.MedioNotificacionDomicilio.SelectedId");
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






		
		
//		System.out.println(htmlPage.asXml());
		}

	}
	
	public static void anadirDomicilio(Domicilio domicilio, String alias) {

	}
	
}
		