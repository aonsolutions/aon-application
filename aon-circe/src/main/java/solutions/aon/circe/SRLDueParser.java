package solutions.aon.circe;

import static com.esferalia.aon.watson.util.AonStringUtils.remove;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SRLDueParser {
	public static void main(String[] args) throws Exception {
		try (FileInputStream dueSRL = new FileInputStream("/home/ndiaz/Descargas/Due-sociedades1.pdf")) {
			parse(dueSRL, new SRLDueParserListener() {
				@Override
				public void onRegistroEntradaYPAE(String registroEntrada, String pae) {
					System.out.println(registroEntrada);
					System.out.println(pae);
					System.out.println(" ");
				}

				@Override
				public void onEmpresaActividad(LocalDate inicioActividad, LocalDate cierreEjercicio) {
					System.out.println(inicioActividad);
					System.out.println(cierreEjercicio);
					System.out.println(" ");
				}

				@Override
				public void onEmpresaDatosJuridicos(String duracionPersonaJuridica, String denominacionSocial,
						float capitalSocial, String acreditaCapitalSocial, String estatutosTipo) {
					System.out.println(duracionPersonaJuridica);
					System.out.println(denominacionSocial);
					System.out.println(capitalSocial);
					System.out.println(acreditaCapitalSocial);
					System.out.println(estatutosTipo);
					System.out.println(" ");
				}

				@Override
				public void onPersonasTrabajadoras(int numPersonasTrabajadoras) {
					System.out.println(numPersonasTrabajadoras);
					System.out.println(" ");
				}

				@Override
				public void onDomicilio(String domicilioSocial, String domicilioFiscal,
						String domicilioNotificaciones) {
					System.out.println(domicilioSocial);
					System.out.println(domicilioFiscal);
					System.out.println(domicilioNotificaciones);
					System.out.println(" ");
				}

				@Override
				public void onNotificacionesTGSS(String telefono, String email) {
					System.out.println(telefono);
					System.out.println(email);
					System.out.println(" ");
				}

				@Override
				public void onMedioNotificacion(String medioNotificacion) {
					System.out.println(medioNotificacion);
					System.out.println(" ");
				}

				@Override
				public void onNotiicacionAEAT(String prefijo, String telefono, String email) {
					System.out.println(prefijo);
					System.out.println(telefono);
					System.out.println(email);
					System.out.println(" ");
				}

				@Override
				public void onComunicaciones(String recibirInformacion) {
					System.out.println(recibirInformacion);
					System.out.println(" ");
				}

				@Override
				public void onSocio(String docIdentidad, String nombre, String apellido, String nacionalidad,
						String sexo, LocalDate fechaNacimineto, String nss, String estadoCivil, String domicilioResidencia,
						String domicilioPersonaAdministradora, String socioTrabajador, String presentadorITP,
						String socioAdministrador, String funcionesDireccionGerencia) {
					System.out.println(docIdentidad);
					System.out.println(nombre);
					System.out.println(apellido);
					System.out.println(nacionalidad);
					System.out.println(sexo);
					System.out.println(fechaNacimineto);
					System.out.println(nss);
					System.out.println(estadoCivil);
					System.out.println(domicilioResidencia);
					System.out.println(domicilioPersonaAdministradora);
					System.out.println(socioTrabajador);
					System.out.println(presentadorITP);
					System.out.println(socioAdministrador);
					System.out.println(funcionesDireccionGerencia);
					System.out.println(" ");

				}

				@Override
				public void onAportacion(float aportacionDineraria, float aportacionNoDineraria, String descripcion) {
					System.out.println(aportacionDineraria);
					System.out.println(aportacionNoDineraria);
					System.out.println(descripcion);
					System.out.println(" ");

				}

				@Override
				public void onCentroActividad(String nombreCentroActividad, float superficieTotal, String domicilio,
						String localPrincipal, String domicilioActividadTGSS) {
					System.out.println(nombreCentroActividad);
					System.out.println(superficieTotal);
					System.out.println(domicilio);
					System.out.println(localPrincipal);
					System.out.println(domicilioActividadTGSS);
					System.out.println(" ");
				}

				@Override
				public void onActividadCNAE(String actividadPrincipal, String otrasActividades) {
					System.out.println(actividadPrincipal);
					System.out.println(otrasActividades);
					System.out.println(" ");
				}

				@Override
				public void onActividadIAE(String actividadPrincipal) {
					System.out.println(actividadPrincipal);
					System.out.println(" ");
				}

				@Override
				public void onLugaresFueraLocal(String epigrafeAE, String tipoActividad, String provincia,
						String municipio, LocalDate fechaInicio) {
					System.out.println(epigrafeAE);
					System.out.println(tipoActividad);
					System.out.println(provincia);
					System.out.println(municipio);
					System.out.println(fechaInicio);
					System.out.println(" ");
				}

				@Override
				public void onDeclaracionCensal(int codigo, String respuesta, LocalDate fecha) {
					System.out.println(codigo);
					System.out.println(respuesta);
					System.out.println(fecha);
					System.out.println(" ");
				}

				@Override
				public void onRepresentante(String docIdentidad, String nombre, String apellidos,
						String domicilioResidencia, String causaRepresentacion, String clave, String tipoRepresentacion,
						String tituloRepresentacion) {
					System.out.println(docIdentidad);
					System.out.println(nombre);
					System.out.println(apellidos);
					System.out.println(domicilioResidencia);
					System.out.println(causaRepresentacion);
					System.out.println(clave);
					System.out.println(tipoRepresentacion);
					System.out.println(tituloRepresentacion);
					System.out.println(" ");
				}

				@Override
				public void onTrabajadorCuentaPropia(String docIdentidad, String nombre, String apellido,
						String nacionalidad, String sexo, LocalDate fechaNacimineto, String nss, String estadoCivil,
						String domicilioResidencia) {
					System.out.println(docIdentidad);
					System.out.println(nombre);
					System.out.println(apellido);
					System.out.println(nacionalidad);
					System.out.println(sexo);
					System.out.println(fechaNacimineto);
					System.out.println(nss);
					System.out.println(estadoCivil);
					System.out.println(domicilioResidencia);
					System.out.println(" ");
				}

				@Override
				public void onAltaPersonaTrabajadora(LocalDate fechaAlta, String funcionesDireccionGerencia) {
					System.out.println(fechaAlta);
					System.out.println(funcionesDireccionGerencia);
					System.out.println(" ");
				}

				@Override
				public void onCentroTrabajo(String centroTrabajo) {
					System.out.println(centroTrabajo);
					System.out.println(" ");
				}

				@Override
				public void onRegimenEncuadramiento(String regimen, String trl, String subgrupo, String grupo) {
					System.out.println(regimen);
					System.out.println(trl);
					System.out.println(subgrupo);
					System.out.println(grupo);
					System.out.println(" ");
				}

				@Override
				public void onCuentaPropia(String cnae, String mutuaIt, String domicilioNotificacion, String altaReta,
						String contingencias, String ceseActividad, LocalDate fechaRealAlta, String opcionCAFP,
						String observacionTGSS) {
					System.out.println(cnae);
					System.out.println(mutuaIt);
					System.out.println(domicilioNotificacion);
					System.out.println(altaReta);
					System.out.println(contingencias);
					System.out.println(ceseActividad);
					System.out.println(fechaRealAlta);
					System.out.println(opcionCAFP);
					System.out.println(observacionTGSS);
					System.out.println(" ");
				}

				@Override
				public void onBaseCotizacion(float baseCotizacion, float rendimientosNetos) {
					System.out.println(baseCotizacion);
					System.out.println(rendimientosNetos);
					System.out.println(" ");
				}

				@Override
				public void onDatosDomiciliacionPago(String cuenta) {
					System.out.println(cuenta);
					System.out.println(" ");
				}

				@Override
				public void onCitaNotarial(String personaCita, String telefono, LocalDateTime fechaHora) {
					System.out.println(personaCita);
					System.out.println(telefono);
					System.out.println(fechaHora);
					System.out.println(" ");
				}

				@Override
				public void onDatosNotaria(String nombre, String apellidos, String direccion, String telefono,
						String fax) {
					System.out.println(nombre);
					System.out.println(apellidos);
					System.out.println(direccion);
					System.out.println(telefono);
					System.out.println(fax);
					System.out.println(" ");
				}
			});
		}
	}

	public static void parse(File file, SRLDueParserListener listener) throws Exception {
		try (PDDocument doc = Loader.loadPDF(file)) {
			parse(doc, listener);
		}
	}

	public static void parse(byte[] data, SRLDueParserListener dueListener) throws Exception {
		try (InputStream is = new ByteArrayInputStream(data)) {
			parse(is, dueListener);
		}
	}

	public static void parse(InputStream is, SRLDueParserListener listener) throws Exception {
		try (PDDocument doc = Loader.loadPDF(is)) {
			parse(doc, listener);
		}
	}

	public static void parse(PDDocument doc, SRLDueParserListener listener) throws Exception {
		AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent()) {
			throw new IOException("You do not have permission to extract text");
		}

		PDFTextStripper stripper = new PDFTextStripper();

		stripper.setSortByPosition(true);

		StringBuilder textBuilder = new StringBuilder();
		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
			// Set the page interval to extract.
			// If we don't, then all pages would be extracted.
			stripper.setStartPage(p);
			stripper.setEndPage(p);

			String text = stripper.getText(doc);
			if (AonStringUtils.isBlank(text))
				continue;

			String[] lines = text.split("\n", -1);
			for (int i = 4; i < lines.length - 3; i++) {
				String line = lines[i];
				textBuilder.append(line);
				textBuilder.append('\n');
			}

//			System.out.println(text);
		}
		parse(textBuilder.toString(), listener);
	}

	public static void parse(String text, SRLDueParserListener listener)
			throws IOException, UnknownPDFException, ParseException {
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			NumberFormat currency = NumberFormat
					.getNumberInstance(new Locale.Builder().setLanguageTag("es").setRegion("ES").build());

			Matcher matcher = find(reader, REGISTRO_ENTRADA_PAE_DATOS);
			String registroEntrada = matcher.group("registro");
			String pae = matcher.group("pae");
			listener.onRegistroEntradaYPAE(registroEntrada, pae);

			matcher = find(reader, INICIO_ACTIVIDAD_CIERRRE_EJERCICIO);
			String inicioActividad = matcher.group("inicioActividad").trim();
			LocalDate fechaInicioActividad = LocalDate.parse(inicioActividad, formatter);
			String cierreEjercicio = matcher.group("cierreEjercicio").trim();
			LocalDate fechaCierreEjercicio = LocalDate.parse(cierreEjercicio + "/" + fechaInicioActividad.getYear(), formatter);
			listener.onEmpresaActividad(fechaInicioActividad, fechaCierreEjercicio);

			matcher = find(reader, DURACION_PERSONA_JURIDICA);
			String duracionPersonaJuridica = matcher.group("duracion");
			matcher = find(reader, DENOMINACION_SOCIAL);
			String denominacionSocial = matcher.group("denominacionSocial");
			matcher = find(reader, CAPITAL_SOCIAL);
			String capitalSocialString = matcher.group("capitalSocial");
			float capitalSocial = currency.parse(capitalSocialString).floatValue();
			String acreditaCapitalSocial = matcher.group("acreditaCapitalSocial");
			matcher = find(reader, ESTATUS_TIPO);
			String estatutosTipo = matcher.group("acogeEstatutosTipo");
			listener.onEmpresaDatosJuridicos(duracionPersonaJuridica, denominacionSocial, capitalSocial,
					acreditaCapitalSocial, estatutosTipo);

			matcher = find(reader, NUM_PERSONAS_TRABAJADORAS);
			String personasTrabajadoras = matcher.group("numPersonasTrabajadoras");
			int numPersonasTrabajadoras = Integer.parseInt(personasTrabajadoras);
			listener.onPersonasTrabajadoras(numPersonasTrabajadoras);

			matcher = find(reader, DOMICILIOS);
			matcher = find(reader, DOMICILIO);
			String domicilioSocialString = matcher.group("domicilio");
			matcher = find(reader, DOMICILIO_CP);
			String cpSocial = matcher.group("cp");
			String municipioSocial = matcher.group("municipio");
			String provinciaSocial = matcher.group("provincia");
			String comunidadSocial = matcher.group("comunidad");
			String paisSocial = matcher.group("pais");
			String domicilioSocial = domicilioSocialString + " " + cpSocial + ", " + municipioSocial + ", " + provinciaSocial
					+ ", " + comunidadSocial + ", " + paisSocial;
			matcher = find(reader, DOMICILIO);
			String domicilioFiscalString = matcher.group("domicilio");
			matcher = find(reader, DOMICILIO_CP);
			String cpFiscal = matcher.group("cp");
			String municipioFiscal = matcher.group("municipio");
			String provinciaFiscal = matcher.group("provincia");
			String comunidadFiscal = matcher.group("comunidad");
			String paisFiscal = matcher.group("pais");
			String domicilioFiscal = domicilioFiscalString + " " + cpFiscal + ", " + municipioFiscal + ", " + provinciaFiscal
					+ ", " + comunidadFiscal + ", " + paisFiscal;
			matcher = find(reader, DOMICILIO);
			String domicilioNotificacionesString = matcher.group("domicilio");
			matcher = find(reader, DOMICILIO_CP);
			String cpNotificaciones = matcher.group("cp");
			String municipioNotificaciones = matcher.group("municipio");
			String provinciaNotificaciones = matcher.group("provincia");
			String comunidadNotificaciones = matcher.group("comunidad");
			String paisNotificaciones = matcher.group("pais");
			String domicilioNotificaciones = domicilioNotificacionesString + " " + cpNotificaciones + ", "
					+ municipioNotificaciones + ", " + provinciaNotificaciones + ", " + comunidadNotificaciones + ", "
					+ paisNotificaciones;
			listener.onDomicilio(domicilioSocial, domicilioFiscal, domicilioNotificaciones);

			matcher = find(reader, TELEFONO_EMAIL);
			String telefonoTGSS = matcher.group("telefono");
			String emailTGSS = matcher.group("mail");
			listener.onNotificacionesTGSS(telefonoTGSS, emailTGSS);

			matcher = find(reader, MEDIO_NOTIFICACION);
			String medioNotificacionTGSS = matcher.group("medioNotificacionDomicilio");
			listener.onMedioNotificacion(medioNotificacionTGSS);

			matcher = find(reader, PREFIJO_TELEFONO_EMAIL);
			String prefijoString = matcher.group("prefijo");
			String prefijo = remove(prefijoString, " ");
			String telefonoAEAT = matcher.group("telefono");
			String emailAEAT = matcher.group("mail");
			listener.onNotiicacionAEAT(prefijo, telefonoAEAT, emailAEAT);
			
			matcher = find(reader, MEDIO_NOTIFICACION);
			String medioNotificacionAEAT = matcher.group("medioNotificacionDomicilio");
			listener.onMedioNotificacion(medioNotificacionAEAT);

			matcher = find(reader, RECIBIR_INFORMACION);
			String recibirInformacion = matcher.group("recibirInformacion");
			listener.onComunicaciones(recibirInformacion);

			while (not(reader, CENTRO_DE_ACTIVIDAD)) {
				matcher = find(reader, SOCIO);
				matcher = find(reader, DOC_NOMBRE_APELLIDO);
				String docIdentidadSocio = matcher.group("doc");
				String nombreSocio = matcher.group("nombre");
				String apellidosSocio = matcher.group("apellidos");
				matcher = find(reader, NACIONALIDAD_SEXO_FECHA_NACIMIENTO);
				String nacionalidadSocio = matcher.group("nacionalidad");
				String sexoSocio = matcher.group("sexo");
				String fechaNacimientoSocioString = matcher.group("fechaNacimiento").trim();
				LocalDate fechaNacimientoSocio = LocalDate.parse(fechaNacimientoSocioString, formatter);
				matcher = find(reader, NSS_ESTADO_CIVIL);
				String nssSocio = matcher.group("nss");
				String estadoCivilSocio = matcher.group("estadoCivil");
				matcher = find(reader, DOMICILIO);
				String domicilioString = matcher.group("domicilio");
				matcher = find(reader, DOMICILIO_CP);
				String cpSocio = matcher.group("cp");
				String municipioSocio = matcher.group("municipio");
				String provinciaSocio = matcher.group("provincia");
				String comunidadSocio = matcher.group("comunidad");
				String paisSocio = matcher.group("pais");
				String domicilioSocio = domicilioString + " " + cpSocio + ", " + municipioSocio + ", " + provinciaSocio + ", "
						+ comunidadSocio + ", " + paisSocio;
				reader.readLine();
				String domicilioPersonaAdministradora = null;
				String socioTrabajador = null;
				String presentador = null;
				String socioAdministrador = null;
				String funcionesDireccionGerencia = null;
				if (not(reader, APORTACION)) {
					matcher = find(reader, DOMICILIO);
					String domicilioAdministradoraString = matcher.group("domicilio");
					matcher = find(reader, DOMICILIO_CP);
					String cpAdministradora = matcher.group("cp");
					String municipioAdministradora = matcher.group("municipio");
					String provinciaAdministradora = matcher.group("provincia");
					String comunidadAdministradora = matcher.group("comunidad");
					String paisAdministradora = matcher.group("pais");
					domicilioPersonaAdministradora = domicilioAdministradoraString + " " + cpAdministradora + ", "
							+ municipioAdministradora + ", " + provinciaAdministradora + ", " + comunidadAdministradora
							+ ", " + paisAdministradora;
					matcher = find(reader, SOCIO_PRESENTADOR);
					socioTrabajador = matcher.group("socioTrabajador");
					presentador = matcher.group("presentador");
					socioAdministrador = matcher.group("socioAdministrador");
					matcher = find(reader, FUNCIONES_DIRECCION_GERENCIA);
					funcionesDireccionGerencia = matcher.group("funcionesDireccionGerencia");
				}
				matcher = find(reader, APORTACION_DESCRIPCION);
				String aportacionDinerariaString = matcher.group("aportacionDineraria");
				float aportacionDineraria = currency.parse(aportacionDinerariaString).floatValue();
				String aportacionNoDinerariaString = matcher.group("aportacionNoDineraria");
				float aportacionNoDineraria = currency.parse(aportacionNoDinerariaString).floatValue();
				String descripcion = matcher.group("descripcion");
				listener.onSocio(docIdentidadSocio, nombreSocio, apellidosSocio, nacionalidadSocio, sexoSocio,
						fechaNacimientoSocio, nssSocio, estadoCivilSocio, domicilioSocio,
						domicilioPersonaAdministradora, socioTrabajador, presentador, socioAdministrador,
						funcionesDireccionGerencia);
				listener.onAportacion(aportacionDineraria, aportacionNoDineraria, descripcion);
				if(find(reader, CONYUGE) != null) {
					matcher = find(reader, DOC_NOMBRE_APELLIDO);
					String docConyuge = matcher.group("doc");
					String nombreConyuge = matcher.group("nombre");
					String apellidosConyuge = matcher.group("apellidos");
					matcher = find(reader, SEXO_TIPO_REGIMEN);
					String sexoConyuge = matcher.group("sexo");
					String tipoRegimen = matcher.group("tipoRegimen");
					listener.onConyuge(docConyuge, nombreConyuge, apellidosConyuge, sexoConyuge, tipoRegimen);
				}

			}

			matcher = find(reader, NOMBRE_CENTRO_ACTIVIDAD);
			String nombreCentroActividad = matcher.group("nombreCentroActividad");
			matcher = find(reader, SUPERFICIE_TOTAL);
			String superficieString = matcher.group("superficie");
			float superficie = currency.parse(superficieString).floatValue();
			matcher = find(reader, DOMICILIO);
			String domicilioCentroActividadString = matcher.group("domicilio");
			matcher = find(reader, DOMICILIO_CP);
			String cpCentroActividad = matcher.group("cp");
			String municipioCentroActividad = matcher.group("municipio");
			String provinciaCentroActividad = matcher.group("provincia");
			String comunidadCentroActividad = matcher.group("comunidad");
			String paisCentroActividad = matcher.group("pais");
			String domicilioCentroActividad = domicilioCentroActividadString + " " + cpCentroActividad + ", "
					+ municipioCentroActividad + ", " + provinciaCentroActividad + ", " + comunidadCentroActividad + ", "
					+ paisCentroActividad;
			matcher = find(reader, LOCAL_PRINCIPAL);
			String localPrincipal = matcher.group("localPrincipal");
			String actividadTGSS = matcher.group("domicilioActvidadTGSS");
			listener.onCentroActividad(nombreCentroActividad, superficie, domicilioCentroActividad, localPrincipal,
					actividadTGSS);

			matcher = find(reader, CNAE);
			matcher = find(reader, ACTIVIDAD_PRINCIPAL);
			String actividadPrincipalCNAE = matcher.group("actividadPrincipal");
			matcher = find(reader, OTRA_ACTIVIDAD);
			String otraActividad = matcher.group("otrasActividades");
			listener.onActividadCNAE(actividadPrincipalCNAE, otraActividad);

			matcher = find(reader, IAE);
			matcher = find(reader, ACTIVIDAD_PRINCIPAL);
			String actividadPrincipalIAE = matcher.group("actividadPrincipal");
			listener.onActividadIAE(actividadPrincipalIAE);

			matcher = find(reader, LUGARES_DE_DESARROLLO);
			matcher = find(reader, EPIGRAFE_AE);
			String epigrafeAE = matcher.group("epigrafeAE");
			matcher = find(reader, TIPO_ACTIVIDAD);
			String tipoActividad = matcher.group("tipoActividad");
			matcher = find(reader, PROVINCIA_MUNICIPIO_FECHA_INICO);
			String provinciaLocal = matcher.group("provincia");
			String municipioLocal = matcher.group("municipio");
			String fechaInicioLocalString = matcher.group("fechaInicio").trim();
			LocalDate fechaInicioLocal = LocalDate.parse(fechaInicioLocalString, formatter);
			listener.onLugaresFueraLocal(epigrafeAE, tipoActividad, provinciaLocal, municipioLocal, fechaInicioLocal);

			while (not(reader, REPRESENTANTE)) {
				matcher = find(reader, DECLARACION_CENSAL_DATOS);
				String codigoString = matcher.group("codigo");
				int codigo = Integer.parseInt(codigoString);
				String respuesta = matcher.group("respuesta");
				String fechaString = matcher.group("fecha");
				if (hasData(fechaString)) {
					LocalDate fechaDC = LocalDate.parse(fechaString, formatter);
					listener.onDeclaracionCensal(codigo, respuesta, fechaDC);
				} else {
					listener.onDeclaracionCensal(codigo, respuesta, null);
				}
			}

			matcher = find(reader, DOC_NOMBRE_APELLIDO);
			String docidentidadRepresentante = matcher.group("doc");
			String nombreRepresentante = matcher.group("nombre");
			String apellidoRepresentante = matcher.group("apellidos");
			matcher = find(reader, DOMICILIO);
			String domicilioRepresentanteString = matcher.group("domicilio");
			matcher = find(reader, DOMICILIO_CP);
			String cpRepresentante = matcher.group("cp");
			String municipioRepresentante = matcher.group("municipio");
			String provinciaRepresentante = matcher.group("provincia");
			String comunidadRepresentante = matcher.group("comunidad");
			String paisRepresentante = matcher.group("pais");
			String domicilioRepresentante = domicilioRepresentanteString + " " + cpRepresentante + ", "
					+ municipioRepresentante + ", " + provinciaRepresentante + ", " + comunidadRepresentante + ", "
					+ paisRepresentante;
			matcher = find(reader, CAUSA_REPRESENTACION);
			String causaRepresentacion = matcher.group("causaRepresentacion");
			String calve = matcher.group("clave");
			matcher = find(reader, TIPO__TITULO_REPRESENTACION);
			String tipoRepresentacion = matcher.group("tipoRepresentacion");
			String titulorepresentacion = matcher.group("tituloRepresentacion");
			listener.onRepresentante(docidentidadRepresentante, nombreRepresentante, apellidoRepresentante,
					domicilioRepresentante, causaRepresentacion, calve, tipoRepresentacion, titulorepresentacion);

			matcher = find(reader, TRABAJADOR_CUENTA_PROPIA);
			matcher = find(reader, DOC_NOMBRE_APELLIDO);
			String docidentidadTrabajadorCuentaPropia = matcher.group("doc");
			String nombreTrabajadorCuentaPropia = matcher.group("nombre");
			String apellidoTrabajadorCuentaPropia = matcher.group("apellidos");
			matcher = find(reader, NACIONALIDAD_SEXO_FECHA_NACIMIENTO);
			String nacionalidadTrabajadorCuentaPropia = matcher.group("nacionalidad");
			String sexoTrabajadorCuentaPropia = matcher.group("sexo");
			String fechaNacimientoTrabajadorCuentaPropiaString = matcher.group("fechaNacimiento").trim();
			LocalDate fechaNacimientoTrabajadorCuentaPropia = LocalDate
					.parse(fechaNacimientoTrabajadorCuentaPropiaString, formatter);
			matcher = find(reader, NSS_ESTADO_CIVIL);
			String nssTrabajadorCuentaPropia = matcher.group("nss");
			String estadoCivilTrabajadorCuentaPropia = matcher.group("estadoCivil");
			matcher = find(reader, DOMICILIO);
			String domicilioTrabajadorCuentaPropiaString = matcher.group("domicilio");
			matcher = find(reader, DOMICILIO_CP);
			String cpTrabajadorCuentaPropia = matcher.group("cp");
			String municipioTrabajadorCuentaPropia = matcher.group("municipio");
			String provinciaTrabajadorCuentaPropia = matcher.group("provincia");
			String comunidadTrabajadorCuentaPropia = matcher.group("comunidad");
			String paisTrabajadorCuentaPropia = matcher.group("pais");
			String domicilioTrabajadorCuentaPropia = domicilioTrabajadorCuentaPropiaString + " " + cpTrabajadorCuentaPropia
					+ ", " + municipioTrabajadorCuentaPropia + ", " + provinciaTrabajadorCuentaPropia + ", "
					+ comunidadTrabajadorCuentaPropia + ", " + paisTrabajadorCuentaPropia;
			listener.onTrabajadorCuentaPropia(docidentidadTrabajadorCuentaPropia, nombreTrabajadorCuentaPropia,
					apellidoTrabajadorCuentaPropia, nacionalidadTrabajadorCuentaPropia, sexoTrabajadorCuentaPropia,
					fechaNacimientoTrabajadorCuentaPropia, nssTrabajadorCuentaPropia, estadoCivilTrabajadorCuentaPropia,
					domicilioTrabajadorCuentaPropia);

			matcher = find(reader, ALTA_PERSONA_TRABAJADORA);
			matcher = find(reader, FECHA_ALTA_DIRECCION_GERENCIA);
			String fechaAltaString = matcher.group("fechaAlta").trim();
			LocalDate fechaAlta = LocalDate.parse(fechaAltaString, formatter);
			String funcionesDireccionGerencia = matcher.group("funcionesDireccionGerencia");
			listener.onAltaPersonaTrabajadora(fechaAlta, funcionesDireccionGerencia);

			matcher = find(reader, CENTRO_TRABAJO);
			matcher = find(reader, DOMICILIO);
			String centroTrabajoString = matcher.group("domicilio");
			matcher = find(reader, DOMICILIO_CP);
			String cpCentroTrabajo = matcher.group("cp");
			String municipioCentroTrabajo = matcher.group("municipio");
			String provinciaCentroTrabajo = matcher.group("provincia");
			String comunidadCentroTrabajo = matcher.group("comunidad");
			String paisCentroTrabajo = matcher.group("pais");
			String centroTrabajo = centroTrabajoString + " " + cpCentroTrabajo + ", " + municipioCentroTrabajo + ", "
					+ provinciaCentroTrabajo + ", " + comunidadCentroTrabajo + ", " + paisCentroTrabajo;
			listener.onCentroTrabajo(centroTrabajo);

			matcher = find(reader, REGIMEN_ENCUADRAMIENTO);
			matcher = find(reader, REGIMEN_SUBGRUPO);
			String regimen = matcher.group("regimen");
			String trl = matcher.group("trl");
			String subgrupo = matcher.group("subgrupo");
			matcher = find(reader, GRUPO);
			String grupo = matcher.group("grupo");
			listener.onRegimenEncuadramiento(regimen, trl, subgrupo, grupo);

			matcher = find(reader, CNAE_DATOS);
			String cnaeFirst = matcher.group("cnae");
			matcher = find(reader, CNAE_SECOND);
			String cnaeSecond = matcher.group("secondCNAE");
			String cnaeDatos = cnaeFirst + " " + cnaeSecond;
			matcher = find(reader, MUTUA_IT);
			String mutuaIt = matcher.group("mutuaIT");
			matcher = find(reader, DOMICILIO);
			String domicilioNotificacionString = matcher.group("domicilio");
			matcher = find(reader, DOMICILIO_CP);
			String cpDomicilioNotificacion = matcher.group("cp");
			String municipioDomicilioNotificacion = matcher.group("municipio");
			String provinciaDomicilioNotificacion = matcher.group("provincia");
			String comunidadDomicilioNotificacion = matcher.group("comunidad");
			String paisDomicilioNotificacion = matcher.group("pais");
			String domicilioNotificacion = domicilioNotificacionString + " " + cpDomicilioNotificacion + ", "
					+ municipioDomicilioNotificacion + ", " + provinciaDomicilioNotificacion + ", "
					+ comunidadDomicilioNotificacion + ", " + paisDomicilioNotificacion;
			matcher = find(reader, TRAMITAR_ALTA_CONTINGENCIAS);
			String altaTramitada = matcher.group("altaReta");
			String contingencias = matcher.group("contingencias");
			String ceseActividad = matcher.group("ceseActividad");
			matcher = find(reader, FECHA_REAL_ALTA);
			String fechaRealAltaString = matcher.group("fechaRealAlta").trim();
			LocalDate fechaRealAlta = LocalDate.parse(fechaRealAltaString, formatter);
			matcher = find(reader, OPCION_CA_FP);
			String opcionCaFp = matcher.group("opcionCAFP");

			matcher = find(reader, BASE_COTIZACION);
			matcher = find(reader, BASE_COTIZACION_RENDIMIENTOS);
			String baseCotizacionString = matcher.group("baseCotizacion");
			float baseCotizacion = currency.parse(baseCotizacionString).floatValue();
			String rendimientoNetoString = matcher.group("rendimientoNeto");
			float rendimientoNeto = currency.parse(rendimientoNetoString).floatValue();
			listener.onBaseCotizacion(baseCotizacion, rendimientoNeto);

			matcher = find(reader, DATOS_DOMICILIACION);
			matcher = find(reader, CUENTA);
			String cuenta = matcher.group("cuenta");
			listener.onDatosDomiciliacionPago(cuenta);

			matcher = find(reader, OBSERVACIONES_TGSS);
			String observacionTGSS = matcher.group("observacionesTGSS");
			listener.onCuentaPropia(cnaeDatos, mutuaIt, domicilioNotificacion, altaTramitada, contingencias,
					ceseActividad, fechaRealAlta, opcionCaFp, observacionTGSS);

			matcher = find(reader, CITA_NOTARIAL);
			matcher = find(reader, PERSONA_CITA_TELEFONO);
			String personaCita = matcher.group("personaCita");
			String telefono = matcher.group("telefono");
			matcher = find(reader, FECHA_HORA);
			String fechaString = matcher.group("fecha").trim();
			String horaString = matcher.group("hora").trim();
			LocalDateTime fechaHora = LocalDateTime.parse(fechaString + " " + horaString, formatterTime);
			listener.onCitaNotarial(personaCita, telefono, fechaHora);

			matcher = find(reader, DATOS_NOTARIA);
			matcher = find(reader, NOMBRE_APELLIDOS);
			String nombreNotaria = matcher.group("nombre");
			String apellidoNotaria = matcher.group("apellido");
			matcher = find(reader, DOMICILIO);
			String domicilioNotariaString = matcher.group("domicilio");
			matcher = find(reader, DOMICILIO_CP);
			String cpDomicilioNotaria = matcher.group("cp");
			String municipioDomicilioNotaria = matcher.group("municipio");
			String provinciaDomicilioNotaria = matcher.group("provincia");
			String comunidadDomicilioNotaria = matcher.group("comunidad");
			String paisDomicilioNotaria = matcher.group("pais");
			matcher = find(reader, TELEFONO_FAX);
			String telefonoNotaria = matcher.group("telefono");
			String faxNotaria = matcher.group("fax");
			String domicilioNotaria = domicilioNotariaString + " " + cpDomicilioNotaria + ", " + municipioDomicilioNotaria
					+ ", " + provinciaDomicilioNotaria + ", " + comunidadDomicilioNotaria + ", " + paisDomicilioNotaria;
			listener.onDatosNotaria(nombreNotaria, apellidoNotaria, domicilioNotaria, telefonoNotaria, faxNotaria);
		}
	}

	private static boolean hasData(String data) {
		return !AonStringUtils.isBlank(data);
	}

	private static Matcher find(BufferedReader reader, Pattern pattern) throws IOException, UnknownPDFException {
		String line;
		while ((line = reader.readLine()) != null) {
			if (AonStringUtils.isBlank(line))
				continue;
			line = AonStringUtils.trim(line);
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				continue;
			}
			return matcher;
		}
		throw new UnknownPDFException(String.format("Pattern: '%s' Not found", pattern.pattern()));
	}

	private static boolean not(BufferedReader reader, Pattern pattern) throws IOException {
		return attempt(reader, pattern).isEmpty();
	}

	private static Optional<Matcher> attempt(BufferedReader reader, Pattern pattern) throws IOException {
		reader.mark(256);
		String line = reader.readLine();
		Matcher matcher = pattern.matcher(line);
		if (matcher.matches())
			return Optional.of(matcher);

		reader.reset();

		return Optional.empty();

	}

	protected static Double parseDouble(String string) {
		return AonNumberUtils.toDouble(AonStringUtils.replace(string, ",", "."));
	}

	protected static Date parseDate(String string) throws ParseException {
		return new SimpleDateFormat("dd-MM-yyyy").parse(string);
	}

	// REGISTRO ENTRADA Y PAE
	protected static final Pattern REGISTRO_ENTRADA_PAE = Pattern.compile("^REGISTRO\\s*ENTRADA\\s*Y\\s*PAE$",
			Pattern.CASE_INSENSITIVE);

	// Registro de Entrada: 202400061360293 PAE: AYUDA-T PYMES
	protected static final Pattern REGISTRO_ENTRADA_PAE_DATOS = Pattern.compile(
			"^REGISTRO\\s*DE\\s*ENTRADA\\s*:\\s*(?<registro>.*)PAE\\s*:\\s*(?<pae>.*)$", Pattern.CASE_INSENSITIVE);

	// EMPRESA
	protected static final Pattern EMPRESA = Pattern.compile("^EMPRESA$", Pattern.CASE_INSENSITIVE);

	// ACTIVIDAD
	protected static final Pattern ACTIVIDAD = Pattern.compile("^ACTIVIDAD$", Pattern.CASE_INSENSITIVE);

	// Inicio de Actividad: 26/04/2024 Cierre de Ejercicio: 31/12
	protected static final Pattern INICIO_ACTIVIDAD_CIERRRE_EJERCICIO = Pattern.compile(
			"^INICIO\\s*DE\\s*ACTIVIDAD\\s*:\\s*(?<inicioActividad>.*)CIERRE\\s*DE\\s*EJERCICIO\\s*:\\s*(?<cierreEjercicio>.*)$",
			Pattern.CASE_INSENSITIVE);

	// DATOS JURÍDICOS
	protected static final Pattern DATOS_JURIDICOS = Pattern.compile("^DATOS\\s*JURIDICOS$", Pattern.CASE_INSENSITIVE);

	// Duración Persona Jurídica: INDEFINIDA
	protected static final Pattern DURACION_PERSONA_JURIDICA = Pattern
			.compile("^DURACI.N\\s*PERSONA\\s*JUR.DICA\\s*:\\s*(?<duracion>.*)$", Pattern.CASE_INSENSITIVE);

	// Denominación Social: LA PLATANERA STUDIO S.L.
	protected static final Pattern DENOMINACION_SOCIAL = Pattern
			.compile("^DENOMINACI.N\\s*SOCIAL\\s*:\\s*(?<denominacionSocial>.*)$", Pattern.CASE_INSENSITIVE);

	// Capital Social: 3000,00 Acredita Capital Social: Sí
	protected static final Pattern CAPITAL_SOCIAL = Pattern.compile(
			"^CAPITAL\\s*SOCIAL\\s*:\\s*(?<capitalSocial>.*)\\s*ACREDITA\\s*CAPITAL\\s*SOCIAL\\s*:\\s*(?<acreditaCapitalSocial>.*)$",
			Pattern.CASE_INSENSITIVE);

	// OBJETO SOCIAL
	protected static final Pattern OBJETO_SOCIAL = Pattern.compile("^OBJETO\\s*SOCAIL$", Pattern.CASE_INSENSITIVE);

	// ¿Se acoge a estatutos tipo?: Sí
	protected static final Pattern ESTATUS_TIPO = Pattern.compile(
			"^\\¿SE\\s*ACOGE\\s*A\\s*ESTATUTOS\\s*TIPO\\?\\s*:\\s*(?<acogeEstatutosTipo>.*)$",
			Pattern.CASE_INSENSITIVE);

	// PERSONAS TRABAJADORAS
	protected static final Pattern PERSONAS_TRABAJADORAS = Pattern.compile("^PERSONAS\\s*TRABAJADORAS$",
			Pattern.CASE_INSENSITIVE);

	// Número de Personas Trabajadoras: 1
	protected static final Pattern NUM_PERSONAS_TRABAJADORAS = Pattern.compile(
			"^N.MERO\\s*DE\\s*PERSONAS\\s*TRABAJADORAS\\s*:\\s*(?<numPersonasTrabajadoras>.*)$",
			Pattern.CASE_INSENSITIVE);

	// DOMICILIOS
	protected static final Pattern DOMICILIOS = Pattern.compile("^DOMICILIOS$", Pattern.CASE_INSENSITIVE);

	// Domicilio Social: PASEO ABEL MATUTES JUAN, 311, 16-20,
	protected static final Pattern DOMICILIO = Pattern
			.compile("^(DIRECCI.N|CENTRO[^:]*|DOMICILIO[^:]*)\\s*:\\s*(?<domicilio>.*)$", Pattern.CASE_INSENSITIVE);

	// 07800, EIVISSA, ILLES BALEARS, ISLAS BALEARES, ESPAÑA,
	protected static final Pattern DOMICILIO_CP = Pattern.compile(
			"^.*(?<cp>\\d{5})\\,\\s*(?<municipio>[^,]+)\\,\\s*(?<provincia>[^,]+)\\,(?:\\s*(?<comunidad>[^,]+)\\,\\s*(?<pais>[^,]+)\\,.*)?$",
			Pattern.CASE_INSENSITIVE);

	// COMUNICACIONES Y NOTIFICACIONES
	protected static final Pattern COMUNICACION_NOTIFICACION = Pattern.compile("^COMUNICACIONES\\s*NOTIFICACI.NES$",
			Pattern.CASE_INSENSITIVE);

	// NOTIFICACIÓN TGSS
	protected static final Pattern NOTIFICACION_TGSS = Pattern.compile("^NOTIFICACI.N\\s*TGSS$",
			Pattern.CASE_INSENSITIVE);

	// Teléfono: 669587726 E-Mail: paulatortosa@icloud.com
	protected static final Pattern TELEFONO_EMAIL = Pattern
			.compile("^TEL.FONO\\s*:\\s*(?<telefono>.*)\\s*E\\-MAIL\\s*:\\s*(?<mail>.*)$", Pattern.CASE_INSENSITIVE);

	// Medio Notificación Domicilio: Correo
	protected static final Pattern MEDIO_NOTIFICACION = Pattern.compile(
			"^MEDIO\\s*NOTIFICACI.N\\s*DOMICILIO\\s*:\\s*(?<medioNotificacionDomicilio>.*)$", Pattern.CASE_INSENSITIVE);

	// NOTIFICACIÓN AEAT
	protected static final Pattern NOTIFICACION_AEAT = Pattern.compile("^NOTIFICACI.N\\s*AEAT$",
			Pattern.CASE_INSENSITIVE);

	// Prefijo: 34 Teléfono: 669587726 E-Mail: paulatortosa@icloud.com
	protected static final Pattern PREFIJO_TELEFONO_EMAIL = Pattern.compile(
			"^PREFIJO\\s*:\\s*(?<prefijo>.*)TEL.FONO\\s*:\\s*(?<telefono>.*)E\\-MAIL\\s*:\\s*(?<mail>.*)$",
			Pattern.CASE_INSENSITIVE);

	// MEDIO NOTIFICACIÓN SOCIEDAD
	protected static final Pattern MEDIO_NOTIFICACION_SOCIEDAD = Pattern.compile("^MEDIO\\s*NOTIFICACI.N\\s*SOCIEDAD$",
			Pattern.CASE_INSENSITIVE);

	// COMUNICACIONES DE LA DIRECCIÓN GENERAL DE INDUSTRIA Y PYME
	protected static final Pattern COMUNICACIONES = Pattern.compile(
			"^COMUNICACIONES\\s*DE\\s*LA\\s*DIRECCI.N\\s*GENERAL\\s*DE\\s*INDUSTRIA\\s*Y\\s*PYME$",
			Pattern.CASE_INSENSITIVE);

	// Deseo recibir información institucional de la DGIPYME.: No
	protected static final Pattern RECIBIR_INFORMACION = Pattern.compile(
			"^DESEO\\s*RECIBIR\\s*INFORMACI.N\\s*INSTITUCIONAL\\s*DE\\s*LA\\s*DGIPYME.*:\\s*(?<recibirInformacion>.*)$",
			Pattern.CASE_INSENSITIVE);

	// SOCIO/A
	protected static final Pattern SOCIO = Pattern.compile("^SOCIO\\/A$", Pattern.CASE_INSENSITIVE);

	// Doc. Identidad: 53652201D Nombre: PAULA Apellidos: TORTOSA GRAS
	protected static final Pattern DOC_NOMBRE_APELLIDO = Pattern.compile(
			"^DOC\\.\\s*IDENTIDAD\\s*:\\s*(?<doc>.*)\\s*NOMBRE\\s*:\\s*(?<nombre>.*)\\s*APELLIDOS\\s*:\\s*(?<apellidos>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Nacionalidad: ESPAÑA Sexo: Varón Fecha de nacimiento: 10/07/1996
	protected static final Pattern NACIONALIDAD_SEXO_FECHA_NACIMIENTO = Pattern.compile(
			"^NACIONALIDAD\\s*:\\s*(?<nacionalidad>.*)SEXO\\s*:\\s*(?<sexo>.*)FECHA\\s*DE\\s*NACIMIENTO\\s*:\\s*(?<fechaNacimiento>.*)$",
			Pattern.CASE_INSENSITIVE);

	// S.S. Nº (NSS/NAF): 08/1249029155 Estado Civil: SOLTERO
	protected static final Pattern NSS_ESTADO_CIVIL = Pattern.compile(
			"^\\s*S.*NSS.*NAF.*:\\s*(?<nss>.*)\\s*ESTADO\\s*CIVIL\\s*:\\s*(?<estadoCivil>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Socio/a Trabajador/a: Sí Presentador ITP/AJD: Sí Socio/a Administrador: Sí
	protected static final Pattern SOCIO_PRESENTADOR = Pattern.compile(
			"^SOCIO.A\\s*TRABAJADOR.A\\s*:\\s*(?<socioTrabajador>.*)\\s*PRESENTADOR\\s*ITP\\/AJD\\s*:\\s*(?<presentador>.*)\\s*SOCIO.A\\s*ADMINISTRADOR\\s*:\\s*(?<socioAdministrador>.*)$",
			Pattern.CASE_INSENSITIVE);

	// ¿Ejerce funciones de Dirección y Gerencia?: Sí
	protected static final Pattern FUNCIONES_DIRECCION_GERENCIA = Pattern.compile(
			"^\\¿EJERCE\\s*FUNCIONES\\s*DE\\s*DIRECCI.N\\s*Y\\s*GERENCIA\\?\\s*:\\s*(?<funcionesDireccionGerencia>.*)$",
			Pattern.CASE_INSENSITIVE);

	// APORTACION
	protected static final Pattern APORTACION = Pattern.compile("^APORTACI.N$", Pattern.CASE_INSENSITIVE);

	// Aportación Dineraria: 0,00 Aportación no Dineraria: 1500,00
	// Descripción:telefono
	protected static final Pattern APORTACION_DESCRIPCION = Pattern.compile(
			"^APORTACI.N\\s*DINERARIA\\s*:\\s*(?<aportacionDineraria>.*)APORTACI.N\\s*NO\\s*DINERARIA\\s*:\\s*(?<aportacionNoDineraria>.*)DESCRIPCI.N\\s*:\\s*(?<descripcion>.*)",
			Pattern.CASE_INSENSITIVE);
	
	// CÓNYUGE
	protected static final Pattern CONYUGE = Pattern.compile("^C.NYUGE$", Pattern.CASE_INSENSITIVE);
	
	// Sexo: Mujer Tipo Régimen Matrimonial: Gananciales
	protected static final Pattern SEXO_TIPO_REGIMEN = Pattern.compile("^\\s*SEXO\\s*:\\s*(?<sexo>.*)\\s*TIPO\\s*R.GIMEN\\s*MATRIMONIAL\\s*:\\s*(?<tipoRegimen>.*)$", Pattern.CASE_INSENSITIVE);

	// CENTRO DE ACTIVIDAD
	protected static final Pattern CENTRO_DE_ACTIVIDAD = Pattern.compile("^CENTRO\\s*DE\\s*ACTIVIDAD$",
			Pattern.CASE_INSENSITIVE);

	// Nombre Centro Actividad: LA PLATANERA STUDIO SL
	protected static final Pattern NOMBRE_CENTRO_ACTIVIDAD = Pattern
			.compile("^NOMBRE\\s*CENTRO\\s*ACTIVIDAD\\s*:\\s*(?<nombreCentroActividad>.*)$", Pattern.CASE_INSENSITIVE);

	// Superficie Total: 1,00
	protected static final Pattern SUPERFICIE_TOTAL = Pattern.compile("^SUPERFICIE\\s*TOTAL\\s*:\\s*(?<superficie>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Local Principal de la Empresa: Sí Domicilio de Actividad TGSS: Sí
	protected static final Pattern LOCAL_PRINCIPAL = Pattern.compile(
			"^LOCAL\\s*PRINCIPAL\\s*DE\\s*LA\\s*EMPRESA\\s*:\\s*(?<localPrincipal>.*)DOMICILIO\\s*DE\\s*ACTIVIDAD\\s*TGSS\\s*:\\s*(?<domicilioActvidadTGSS>.*)$",
			Pattern.CASE_INSENSITIVE);

	// ACTIVIDADES
	protected static final Pattern ACTIVIDADES = Pattern.compile("^ACTIVIDADES$", Pattern.CASE_INSENSITIVE);

	// CLASIFICACIÓN NACIONAL ACTIVIDADES ECONÓMICAS (CNAE)
	protected static final Pattern CNAE = Pattern.compile(
			"^CLASIFICACI.N\\s*NACIONAL\\s*ACTIVIDADES\\s*ECON.MICAS\\s*\\(CNAE\\)$", Pattern.CASE_INSENSITIVE);

	// Actividad Principal: 7311 - Agencias de publicidad
	protected static final Pattern ACTIVIDAD_PRINCIPAL = Pattern.compile(
			"^ACTIVIDAD\\s*PRINCIPAL\\s*:\\s*(?<actividadPrincipal>[\\d\\*]{4,5})\\s*\\-.*$", Pattern.CASE_INSENSITIVE);

	// Otras Actividades: 7021 - Relaciones públicas y comunicación
	protected static final Pattern OTRA_ACTIVIDAD = Pattern
			.compile("^OTRAS\\s*ACTIVIDADES\\s*:\\s*(?<otrasActividades>\\d{4})\\s*\\-.*$", Pattern.CASE_INSENSITIVE);

	// IMPUESTO ACTIVIDAD ECONÓMICA (IAE)
	protected static final Pattern IAE = Pattern.compile("^IMPUESTO\\s*ACTIVIDAD\\s*ECON.MICA\\s*\\(IAE\\)$",
			Pattern.CASE_INSENSITIVE);

	// LUGARES DE DESARROLLO DE LA ACTIVIDAD FUERA DEL LOCAL
	protected static final Pattern LUGARES_DE_DESARROLLO = Pattern.compile(
			"^LUGARES\\s*DE\\s*DESARROLLO\\s*DE\\s*LA\\s*ACTIVIDAD\\s*FUERA\\s*DEL\\s*LOCAL$",
			Pattern.CASE_INSENSITIVE);

	// Epígrafe AE: 1*844 - SERVICIOS PUBLICIDAD, RELACIONES PUBLIC.
	protected static final Pattern EPIGRAFE_AE = Pattern
			.compile("^EP.GRAFE\\s*AE\\s*:\\s*(?<epigrafeAE>[\\d\\*]{5})\\s*\\-.*$", Pattern.CASE_INSENSITIVE);

	// Tipo Actividad: A03 - RESTO DE ACTIVIDADES EMPRESARIALES
	protected static final Pattern TIPO_ACTIVIDAD = Pattern
			.compile("^TIPO\\s*ACTIVIDAD\\s*:\\s*(?<tipoActividad>\\w{3})\\s*\\-.*$", Pattern.CASE_INSENSITIVE);

	// Provincia: ILLES BALEARS Municipio: EIVISSA Fecha Inicio: 26/04/2024
	protected static final Pattern PROVINCIA_MUNICIPIO_FECHA_INICO = Pattern.compile(
			"^PROVINCIA\\s*:\\s*(?<provincia>.*)MUNICIPIO\\s*:\\s*(?<municipio>.*)FECHA\\s*INICIO\\s*:\\s*(?<fechaInicio>.*)$",
			Pattern.CASE_INSENSITIVE);

	// DECLARACIÓN CENSAL
	protected static final Pattern DECLARACION_CENSAL = Pattern.compile("^DECLARACI.N\\s*CENSAL$",
			Pattern.CASE_INSENSITIVE);

	// 502 Comunicación de inicio de actividad. Entregas de bienes o prestaciones de
	// servicios previa o simultánea a la adquisición Sí 01/04/2024
	// 504 Comunicación de inicio de actividad. Entregas de bienes o prestaciones de
	// servicios posterior a adquisición de bienes o No
	protected static final Pattern DECLARACION_CENSAL_DATOS = Pattern.compile(
			"^(?<codigo>\\d{3})\\s*.*(?<respuesta>sí|no)\\s*(?<fecha>.*)$",
			Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ | Pattern.UNICODE_CASE);

	// REPRESENTANTE
	protected static final Pattern REPRESENTANTE = Pattern.compile("^REPRESENTANTE$", Pattern.CASE_INSENSITIVE);

	// Causa de la representación: Legal Clave: Personas Jurídicas residentes o
	// constituidas en España
	protected static final Pattern CAUSA_REPRESENTACION = Pattern.compile(
			"^CAUSA\\s*DE\\s*LA\\s*REPRESENTACI.N\\s*:\\s*(?<causaRepresentacion>.*)CLAVE\\s*:\\s*(?<clave>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Tipo de representación: Individual Título de la representación: Documento
	// público
	protected static final Pattern TIPO__TITULO_REPRESENTACION = Pattern.compile(
			"^TIPO\\s*DE\\s*REPRESENTACI.N\\s*:\\s*(?<tipoRepresentacion>.*)T.TULO\\s*DE\\s*LA\\s*REPRESENTACI.N\\s*:\\s*(?<tituloRepresentacion>.*)$",
			Pattern.CASE_INSENSITIVE);

	// TRABAJADOR/A POR CUENTA PROPIA
	protected static final Pattern TRABAJADOR_CUENTA_PROPIA = Pattern
			.compile("^TRABAJADOR\\/A\\s*POR\\s*CUENTA\\s*PROPIA$", Pattern.CASE_INSENSITIVE);

	// ALTA DE PERSONA TRABAJADORA
	protected static final Pattern ALTA_PERSONA_TRABAJADORA = Pattern.compile("^ALTA\\s*DE\\s*PERSONA\\s*TRABAJADORA$",
			Pattern.CASE_INSENSITIVE);

	// Fecha de Alta: 26/04/2024 ¿Ejerce funciones de Dirección y Gerencia?: Sí
	protected static final Pattern FECHA_ALTA_DIRECCION_GERENCIA = Pattern.compile(
			"^FECHA\\s*DE\\s*ALTA\\s*:\\s*(?<fechaAlta>.*)\\¿EJERCE\\s*FUNCIONES\\s*DE\\s*DIRECCI.N\\s*Y\\s*GERENCIA\\s*\\?\\s*:\\s*(?<funcionesDireccionGerencia>.*)$",
			Pattern.CASE_INSENSITIVE);

	// CENTRO DE TRABAJO
	protected static final Pattern CENTRO_TRABAJO = Pattern.compile("^CENTRO\\s*DE\\s*TRABAJO$",
			Pattern.CASE_INSENSITIVE);

	// RÉGIMEN DE ENCUADRAMIENTO
	protected static final Pattern REGIMEN_ENCUADRAMIENTO = Pattern.compile("^R.GIMEN\\s*DE\\s*ENCUADRAMIENTO$",
			Pattern.CASE_INSENSITIVE);

	// Régimen: RETA TRL: NO Subgrupo: N/A
	protected static final Pattern REGIMEN_SUBGRUPO = Pattern.compile(
			"^R.GIMEN\\s*:\\s*(?<regimen>.*)TRL\\s*:\\s*(?<trl>.*)SUBGRUPO\\s*:\\s*(?<subgrupo>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Grupo: 0521- RÉGIMEN ESPECIAL DE TRABAJADORES POR CUENTA PROPIA O AUTÓNOMOS.
	protected static final Pattern GRUPO = Pattern.compile("^GRUPO\\s*:\\s*(?<grupo>\\d{4}).*$",
			Pattern.CASE_INSENSITIVE);

	// CNAE: 7021 - Relaciones públicas y comunicación
	protected static final Pattern CNAE_DATOS = Pattern.compile("^CNAE\\s*:\\s*(?<cnae>\\d{4}).*$",
			Pattern.CASE_INSENSITIVE);

	// 7311 - Agencias de publicidad
	protected static final Pattern CNAE_SECOND = Pattern.compile("^\\s*(?<secondCNAE>\\d{4}).*$",
			Pattern.CASE_INSENSITIVE);

	// Mutua de IT: FREMAP
	protected static final Pattern MUTUA_IT = Pattern.compile("^\\s*MUTUA\\s*.*:\\s*(?<mutuaIT>.*)$",
			Pattern.CASE_INSENSITIVE);

	// ¿Ha tramitado ya su alta en RETA?: No ¿Contingencias AT / EP?: Sí Acogerse a
	// Cese de Actividad: Sí
	protected static final Pattern TRAMITAR_ALTA_CONTINGENCIAS = Pattern.compile(
			"^\\¿HA\\s*TRAMITADO\\s*.*:\\s*(?<altaReta>.*)\\¿CONTINGENCIAS\\s*AT\\s*\\/\\s*EP\\s*\\?\\s*:\\s*(?<contingencias>.*)ACOGERSE\\s*A\\s*CESE\\s*DE\\s*ACTIVIDAD\\s*:\\s*(?<ceseActividad>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Fecha Real Alta: 26/04/2024
	protected static final Pattern FECHA_REAL_ALTA = Pattern
			.compile("^FECHA\\s*REAL\\s*ALTA\\s*:\\s*(?<fechaRealAlta>.*)$", Pattern.CASE_INSENSITIVE);

	// Opcion CA FP: Sí
	protected static final Pattern OPCION_CA_FP = Pattern.compile("^OPCION\\s*CA\\s*FP\\s*:\\s*(?<opcionCAFP>.*)$",
			Pattern.CASE_INSENSITIVE);

	// BASE COTIZACIÓN
	protected static final Pattern BASE_COTIZACION = Pattern.compile("^BASE\\s*COTIZACI.N$", Pattern.CASE_INSENSITIVE);

	// Base Cotización: 950,98 Rendimientos netos anuales en promedio mensual:
	// 166,70
	protected static final Pattern BASE_COTIZACION_RENDIMIENTOS = Pattern.compile(
			"^BASE\\s*COTIZACI.N\\s*:\\s*(?<baseCotizacion>.*)RENDIMIENTOS\\s*NETOS\\s*ANUALES\\s*EN\\s*PROMEDIO\\s*MENSUAL\\s*:\\s*(?<rendimientoNeto>.*)$",
			Pattern.CASE_INSENSITIVE);

	// DATOS PARA LA DOMICILIACIÓN DE PAGO DE CUOTAS
	protected static final Pattern DATOS_DOMICILIACION = Pattern
			.compile("^DATOS\\s*PARA\\s*LA\\s*DOMICILIACI.N\\s*DE\\s+PAGO\\s*DE\\s*CUOTAS$", Pattern.CASE_INSENSITIVE);

	// Cuenta: 2100 0293 24 0200443673
	protected static final Pattern CUENTA = Pattern.compile("^CUENTA\\s*:\\s*(?<cuenta>.*)$", Pattern.CASE_INSENSITIVE);

	// Observaciones TGSS: ES71
	protected static final Pattern OBSERVACIONES_TGSS = Pattern
			.compile("^OBSERVACIONES\\s*TGSS\\s*:\\s*(?<observacionesTGSS>.*)$", Pattern.CASE_INSENSITIVE);

	// CITA NOTARIAL
	protected static final Pattern CITA_NOTARIAL = Pattern.compile("^CITA\\s*NOTARIAL$", Pattern.CASE_INSENSITIVE);

	// Persona Cita: PAULA TORTOSA GRAS Teléfono: 669587726
	protected static final Pattern PERSONA_CITA_TELEFONO = Pattern.compile(
			"^PERSONA\\s*CITA\\s*:\\s*(?<personaCita>.*)TEL.FONO\\s*:\\s*(?<telefono>.*)$", Pattern.CASE_INSENSITIVE);

	// Fecha: 05/04/2024 Hora: 10:30
	protected static final Pattern FECHA_HORA = Pattern.compile("^FECHA\\s*:\\s*(?<fecha>.*)HORA\\s*:\\s*(?<hora>.*)$",
			Pattern.CASE_INSENSITIVE);

	// DATOS NOTARÍA
	protected static final Pattern DATOS_NOTARIA = Pattern.compile("^DATOS\\s*NOTAR.A$", Pattern.CASE_INSENSITIVE);

	// Nombre: Juan Apellidos: Acero Simón
	protected static final Pattern NOMBRE_APELLIDOS = Pattern
			.compile("^NOMBRE\\s*:\\s*(?<nombre>.*)APELLIDOS\\s*:\\s*(?<apellido>.*)$", Pattern.CASE_INSENSITIVE);

	// Teléfono: 971304142 / Fax: 971390361
	protected static final Pattern TELEFONO_FAX = Pattern.compile(
			"^\\s*TEL.FONO\\s*:\\s*(?<telefono>.*)\\s*\\/\\s*FAX\\s*:\\s*(?<fax>.*)$", Pattern.CASE_INSENSITIVE);

}
