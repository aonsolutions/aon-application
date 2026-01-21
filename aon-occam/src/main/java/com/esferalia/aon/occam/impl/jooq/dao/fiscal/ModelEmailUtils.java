package com.esferalia.aon.occam.impl.jooq.dao.fiscal;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelUtils;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.AuthDAO;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.SecurityDAO;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.aws.ses.SES;
import solutions.aon.aws.ses.SESMessage;

public class ModelEmailUtils {
	
	private static final DecimalFormat FMT = new DecimalFormat("#,##0.00");	

	private ModelEmailUtils() {
		
	}
	
	// ENVIO EMAIL NOTIFICACION A LA DIRECCION EMAIL DE LA EMPRESA (SE SUPONE QUE SE USA POR LA ASESORIA PARA ENVIAR NOTIFICACION A LA EMPRESA CLIENTE)
	public static void sendEmailCliente(Occam occam, IFiscalModel model, byte[] draftModelPdf) {
		
		try {
			System.out.println("Envio email a cliente. Modelo fiscal: " + model.getModel() + " " + model.getYear() + " " + model.getPeriod());

//			String from = "fiscal." + parentName + "@aon.solutions"; // SUPONGO QUE SERA EL DOMINIO PADRE, NO SERIA MEJOR SIMPLEMENTE notificaciones o fiscal.notificaciones@aon.solutions
			String fromName = getParentCompanyName(occam);    // Nombre de la empresa del dominio padre
			String from = "fiscal.notificaciones@aon.solutions"; // POR AHORA SE DEJA CON UNA CUENTA FICTICIA GENERICA
			String to = getEnterpriseEmail(occam);  // email de la empresa cliente
			String cc = "";                         
			String reply = getUserEmail(occam);     // email del usuario
			String bcc = reply;                     // En BCC tambien se pone el email del usuario
			String subject = "Nuevo modelo para confirmar en el portal"; // Asunto del mensaje de correo
			String parentName = getParentName(occam);
			String url = getLogoUrl(parentName); // Logo de la empresa del dominio padre: "https://" + parentName + "/aonDocuments/company.logo"
			String body = "";
					if (AonStringUtils.isNotBlank(url)) {
						body = body +
							"<div style=\"margin: 20px;\">" + 
							  "<img src=\"" + url + "\" width=\"350\" height=\"150\">" +
							"</div>"; 
					}
			body = body +
					"<div>" +
						"<p>Estimado cliente:</p>" +
						"<p>Tiene un nuevo modelo para confirmar en el portal:</p>" +
						"<p>Modelo: " + model.getModelFullName() + "<br>" +
						"Declarante: " + model.getDocument() + " " + model.getFullName() + "<br>" +
						"Resultado: " + FMT.format(model.getDeclarationResult()) + "<br>" +
						"Tipo Declaración: " + model.getDeclarationResultType().getDescription() + "</p>" +
						"<p>Un saludo</p>" +
						"<p><h5>Este mensaje está dirigido de manera exclusiva a su destinatario y puede contener información privada y/o confidencial. No lo reenvíe, copie o distribuya a terceros que no deban conocer su contenido. En caso de haberlo recibido por error, rogamos lo notifique al remitente y proceda a su borrado, así como el de cualquier documento que pudiera adjuntarse.</h5></p>"+
					"</div>";
				
			sendEmail(fromName, from, to, cc, bcc, reply, subject, body, model, draftModelPdf);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage());
		}
	
	}
	
	// ENVIAR EMAIL DE NOTIFICACION AL ASESOR (SE ENVIA CUANDO DESDE EL PORTAL SE ACEPTA O RECHAZA EL MODELO, POR LA EMPRESA CLIENTE)
	public static void sendEmailAsesor(Occam occam, FiscalModel model, String user, String reasonReject) {
		
		try {
			System.out.println("Envio email a asesor. Modelo fiscal: " + model.getModel() + " " + model.getYear() + " " + model.getPeriod() + " " + model.getCreationUser() + " " + model.getModificationUser() + ". User: " + user);

			String userName = getParentUserName(occam, user);
			String fromName = getCompanyName(occam);    // Nombre de la empresa del dominio
			String from = "fiscal.notificaciones@aon.solutions"; // POR AHORA SE DEJA CON UNA CUENTA FICTICIA GENERICA
			String to = getParentUserEmail(occam, user);         // email del último usuario que modificó el modelo, antes de marcarlo como aceptado o rechazado por el cliente
			String cc = getParentUserEmail(occam, model.getCreationUser());  // email del usuario que creo el modelo
			if (AonStringUtils.equals(to, cc)) {
				cc = ""; // Si cc y to son iguales, no se pone cc
			}
			String bcc = "";
 			String reply = getUserEmail(occam);             // email del usuario logeado
			String accepted = model.getStatus() == FiscalStatus.CUSTOMER_REJECTED ? "<font color=\"#660000\"><b>RECHAZADO</b></font>" : "<font color=\"#38761d\">ACEPTADO</font>";
			String modelStatus = ""; 
			String subjectStatus = "";
			if (model.getStatus() == FiscalStatus.CUSTOMER_REJECTED) {
				modelStatus = "<font color=\"#660000\"><b>" + model.getStatus().getName().toUpperCase() + "</b></font>";
				subjectStatus = "RECHAZADO";
			}  else if (model.getStatus() == FiscalStatus.SENT) {
				modelStatus = "<font color=\"#38761d\">ACEPTADO Y PRESENTADO</font>";
				subjectStatus = "ACEPTADO y PRESENTADO";
			} else if (model.getStatus() == FiscalStatus.FINISHED) {
				modelStatus = "<font color=\"#38761d\">ACEPTADO</font> y <font color=\"#b45f06\"><b>PENDIENTE DE PRESENTACIÓN</b></font>";
				subjectStatus = "ACEPTADO y PENDIENTE DE PRESENTACION";
			}
			String subject = "Nuevo modelo " + subjectStatus + " por su cliente."; // Asunto del mensaje de correo 
			String url = getLogoUrl(occam.getDomainName()); // Logo de la empresa del dominio: "https://" + domainName + "/aonDocuments/company.logo" 
			
			String reasonRejectLine = "";
			if (model.getStatus() == FiscalStatus.CUSTOMER_REJECTED && AonStringUtils.isNotBlank(reasonReject)) {
				reasonRejectLine = "Motivo del Rechazo: " + reasonReject + "<br>"; 
			}
			
			String body = "";
					if (AonStringUtils.isNotBlank(url)) {
						body = body +
							"<div style=\"margin: 20px;\">" + 
							  "<img src=\"" + url + "\" width=\"350\" height=\"150\">" +
							"</div>"; 
					}
			body = body +
					"<div>" +
						"<p>Estimado " + userName + ":</p>" +  
						"<p>Tiene un nuevo modelo " + accepted + " por su cliente:</p>" + // 
						"<p>Modelo: " + model.getModelFullName() + "<br>" +
						"Declarante: " + model.getDocument() + " " + model.getFullName() + "<br>" +
						"Resultado: " + FMT.format(model.getDeclarationResult()) + "<br>" +
						"Tipo Declaración: " + model.getDeclarationResultType().getDescription() + "<br>" + 
						"Estado: " + modelStatus + "<br>" +
						reasonRejectLine +					
						"</p>" +
						"<p>Un saludo</p>" +
						"<p><h5>Este mensaje está dirigido de manera exclusiva a su destinatario y puede contener información privada y/o confidencial. No lo reenvíe, copie o distribuya a terceros que no deban conocer su contenido. En caso de haberlo recibido por error, rogamos lo notifique al remitente y proceda a su borrado, así como el de cualquier documento que pudiera adjuntarse.</h5></p>"+
					"</div>";
				
			sendEmail(fromName, from, to, cc, bcc, reply, subject, body, model, null);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage());
		}
	
	}	

	// ENVIO EMAIL. SE LE PUEDE PASAR EL BORRADOR DEL MODELO EN PDF PARA QUE SE ENVIE COMO ADJUNTO EN EL MENSAJE DE CORREO
	private static void sendEmail(String fromName, String from, String to, String cc, String bcc, String reply, String subject, String body, IFiscalModel model, byte[] draftModelPdf) {
		
		try {
			// Direcciones email de origen, destino y reply deben estar cumplimentadas
			if (AonStringUtils.isNotBlank(from) && AonStringUtils.isNotBlank(to) && AonStringUtils.isNotBlank(reply)) {
				SESMessage msg = new SESMessage()
					.setAlias(fromName)
					.setFrom(from) // Si no se indica from, se enviará desde "no-reply@aon.solutions"
					.setTo(to)
					.setCc(cc)
					.setBcc(bcc) 
					.setReplyTo(reply)					
					.setSubject(subject) 
					.setBody(body) 
				;

				// Adjuntar PDF con el borrador del modelo, si se ha podido obtener
				File parent = null;
				File file = null;
				if (draftModelPdf != null) {
					String fileName = getFileName(model); 
					Path parentPath = Files.createTempDirectory(model.getDocument());
					parent = parentPath.toFile();
					file = new File(parent, fileName);
					AonFileUtils.writeByteArrayToFile(file, draftModelPdf); // data es el documento PDF con el borrador (solo AEAT), que se adjunta al correo
					msg.setFile(file);
				}
				
				// Enviar mensaje
				SES.sendEmail(msg);
				
				if (file != null) {
					// Una vez que el mensaje ha sido enviado, intentar borrar el directorio y fichero temporal
					if (!file.delete()) 
						System.out.println("No se ha podido borrar el fichero " + file.getPath());
					if (!parent.delete())  
						System.out.println("No se ha podido borrar el directorio temporal " + parent.getPath());
				}
				
			} else {
				System.out.println("ERROR: Dirección email (from, to o reply) no cumplimentada");
				throw new AonCoreException("Falta indicar dirección email (origen, destino o respuesta).");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AonCoreException(e.getMessage());
		}

	}

	// Obtener la dirección email de la empresa
	private static String getEnterpriseEmail(Occam occam) {
		
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return ctx.getDslContext().transactionResult(
				configuration -> {
					Company company = CompanyDAO.getCompany(ctx, ctx.getDomainId());
					Enterprise enterprise = CompanyDAO.getEnterprise(ctx, company.getId());
					return enterprise.getEmail(); // Email de la empresa
				}
			);
		}

	}
	
	// Obtener la dirección email del usuario
	private static String getUserEmail(Occam occam) {
		
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return ctx.getDslContext().transactionResult(
					configuration -> {
						User user = ctx.getConfig().getUser();
						MailAccount mailAccount = SecurityDAO.getMailAccount(ctx, f -> f.getUserIdProperty().eq(user.getId()).and(f.getDomainProperty().eq(user.getDomain().getId())));
						String email = mailAccount.getEmail();
						if (AonStringUtils.isBlank(email)) {
							email = getAuthEmail(ctx, user);
						}						
						return email;
					}
			);
		}

	}

	// Obtener la dirección email del usuario del dominio padre
	private static String getParentUserEmail(Occam occam, String parentUser) {
		
		try (CloseableAONContext ctx = AONContext.getAONContext(occam.getDomainName(), occam.getDomain(), parentUser)) {
			return ctx.getDslContext().transactionResult(
					configuration -> {
						User user = ctx.getConfig().getUser();
						MailAccount mailAccount = SecurityDAO.getMailAccount(ctx, f -> f.getUserIdProperty().eq(user.getId()).and(f.getDomainProperty().eq(user.getDomain().getId())));
						String email = mailAccount.getEmail();
						if (AonStringUtils.isBlank(email)) {
							email = getAuthEmail(ctx, user);
						}
						return email;
					}
			);
		}

	}
	
	private static String getAuthEmail(CloseableAONContext ctx, User user) {
		String email = null;
		if (user.getAuth() != null) {
			email = user.getAuth().getEmail();
			if (AonStringUtils.isBlank(email)) {
				Auth auth = AuthDAO.getAuth(ctx, user.getAuth().getAuth());
				email = auth.getEmail();
				return email;
			}
		}
		return email;
	}
	
	// Obtener el nombre del usuario del dominio padre
	private static String getParentUserName(Occam occam, String parentUser) {
		
		try (CloseableAONContext ctx = AONContext.getAONContext(occam.getDomainName(), occam.getDomain(), parentUser)) {
			return ctx.getDslContext().transactionResult(
					configuration -> {
						User user = ctx.getConfig().getUser();						
						return user.getName();
					}
			);
		}

	}
	
	// Obtener el nombre de la empresa del dominio padre o del propio dominio, si no tiene padre
	private static String getParentCompanyName(Occam occam) {
		
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return ctx.getDslContext().transactionResult(
				configuration -> {
					Integer domainId = DomainDAO.getParentDomain(ctx);
					
					if (domainId == null) {
						domainId = ctx.getDomainId();
					}
					
					Domain domain = DomainDAO.getDomain(ctx, domainId);
					Company company = CompanyDAO.getCompany(ctx, domain.getId());
					
					return company.getName();
				}
			);
		}

	}
	
	// Obtener el nombre de la empresa del dominio 
	private static String getCompanyName(Occam occam) {
		
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return ctx.getDslContext().transactionResult(
				configuration -> {
					Domain domain = DomainDAO.getDomain(ctx, ctx.getDomainId());
					Company company = CompanyDAO.getCompany(ctx, domain.getId());
					return company.getName();
				}
			);
		}

	}
	
	// Obtener el nombre del dominio padre o del propio dominio, si no tiene padre
	private static String getParentName(Occam occam) {
		
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return ctx.getDslContext().transactionResult(
				configuration -> {
					Integer domainId = DomainDAO.getParentDomain(ctx);
					
					if (domainId == null) {
						domainId = ctx.getDomainId();
					}
					
					Domain domain = DomainDAO.getDomain(ctx, domainId);
					return domain.getName();
				}
			);
		}

	}
	
	// Obtener nombre del fichero que se adjunta al mensaje de correo
	private static String getFileName(IFiscalModel fs) {
		String document = AonStringUtils.trimToEmpty( fs.getDocument());
		document = document.replaceAll("[^a-zA-Z0-9.-]", "_");
		return  "Mod" + FiscalModelUtils.getModelName(fs) 
				+ "_" + fs.getYear() 
				+ "_" + ( fs.getModel() == FiscalModelType.M202 ? AonFiscalFileUtils.getMod202Period((Mod202) fs) : fs.getPeriod().getName()) 
				+ AonStringUtils.prependIfMissing(document, "_") + ".pdf";
	}
	
	public static String getLogoUrl(String companyName) {
		//String logo = "https://aon.solutions/assets/aon-logo.png"; // Esta seria la imagen de AON, si la queremos devolver por defecto
		String logoUrl = "";
		try {
			String logo = "https://" + companyName + "/aonDocuments/company.logo";
			URI uri = URI.create(logo);
		    URL url = uri.toURL();
	        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
	        int statusCode = connection.getResponseCode();
	        if(200 == statusCode) {
	        	logoUrl = logo;
	        }
            connection.disconnect();
		} catch (Exception e) {
			// do nothing
		}
		return logoUrl;
		
	}


}
