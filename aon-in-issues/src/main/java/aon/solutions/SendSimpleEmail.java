package aon.solutions;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;
import software.amazon.awssdk.services.sesv2.SesV2Client;

public class SendSimpleEmail {

	 public static void send(
             String sender,
             String recipient) {
		 
	String subject = "Error en la identificación en el sistema de Aon";	 

     String bodyHTML = "<html>" + "<head><style>a3s {\n"
     		+ "  direction: initial;\n"
     		+ "  font: small/1.5 Arial,Helvetica,sans-serif;\n"
     		+ "}\n"
     		+ ".gt {\n"
     		+ "  font-size: .875rem;\n"
     		+ "}\n"
     		+ ".ii {\n"
     		+ "  direction: ltr;\n"
     		+ "}\n"
     		+ ".hx {\n"
     		+ "  color: #222;\n"
     		+ "}\n"
     		+ ".bkL, .qp {\n"
     		+ "  --gm3-sys-color-background: #fff;\n"
     		+ "  --gm3-sys-color-background-rgb: 255,255,255;\n"
     		+ "  --gm3-sys-color-error: #b3261e;\n"
     		+ "  --gm3-sys-color-error-rgb: 179,38,30;\n"
     		+ "  --gm3-sys-color-error-container: #f9dedc;\n"
     		+ "  --gm3-sys-color-error-container-rgb: 249,222,220;\n"
     		+ "  --gm3-sys-color-inverse-on-surface: #f2f2f2;\n"
     		+ "  --gm3-sys-color-inverse-on-surface-rgb: 242,242,242;\n"
     		+ "  --gm3-sys-color-inverse-primary: #a8c7fa;\n"
     		+ "  --gm3-sys-color-inverse-primary-rgb: 168,199,250;\n"
     		+ "  --gm3-sys-color-inverse-surface: #303030;\n"
     		+ "  --gm3-sys-color-inverse-surface-rgb: 48,48,48;\n"
     		+ "  --gm3-sys-color-on-background: #1f1f1f;\n"
     		+ "  --gm3-sys-color-on-background-rgb: 31,31,31;\n"
     		+ "  --gm3-sys-color-on-error: #fff;\n"
     		+ "  --gm3-sys-color-on-error-rgb: 255,255,255;\n"
     		+ "  --gm3-sys-color-on-error-container: #410e0b;\n"
     		+ "  --gm3-sys-color-on-error-container-rgb: 65,14,11;\n"
     		+ "  --gm3-sys-color-on-primary: #fff;\n"
     		+ "  --gm3-sys-color-on-primary-rgb: 255,255,255;\n"
     		+ "  --gm3-sys-color-on-primary-container: #041e49;\n"
     		+ "  --gm3-sys-color-on-primary-container-rgb: 4,30,73;\n"
     		+ "  --gm3-sys-color-on-primary-fixed: #041e49;\n"
     		+ "  --gm3-sys-color-on-primary-fixed-rgb: 4,30,73;\n"
     		+ "  --gm3-sys-color-on-primary-fixed-variant: #0842a0;\n"
     		+ "  --gm3-sys-color-on-primary-fixed-variant-rgb: 8,66,160;\n"
     		+ "  --gm3-sys-color-on-secondary: #fff;\n"
     		+ "  --gm3-sys-color-on-secondary-rgb: 255,255,255;\n"
     		+ "  --gm3-sys-color-on-secondary-container: #001d35;\n"
     		+ "  --gm3-sys-color-on-secondary-container-rgb: 0,29,53;\n"
     		+ "  --gm3-sys-color-on-secondary-fixed: #001d35;\n"
     		+ "  --gm3-sys-color-on-secondary-fixed-rgb: 0,29,53;\n"
     		+ "  --gm3-sys-color-on-secondary-fixed-variant: #004a77;\n"
     		+ "  --gm3-sys-color-on-secondary-fixed-variant-rgb: 0,74,119;\n"
     		+ "  --gm3-sys-color-on-surface: #1f1f1f;\n"
     		+ "  --gm3-sys-color-on-surface-rgb: 31,31,31;\n"
     		+ "  --gm3-sys-color-on-surface-variant: #444746;\n"
     		+ "  --gm3-sys-color-on-surface-variant-rgb: 68,71,70;\n"
     		+ "  --gm3-sys-color-on-tertiary: #fff;\n"
     		+ "  --gm3-sys-color-on-tertiary-rgb: 255,255,255;\n"
     		+ "  --gm3-sys-color-on-tertiary-container: #072711;\n"
     		+ "  --gm3-sys-color-on-tertiary-container-rgb: 7,39,17;\n"
     		+ "  --gm3-sys-color-on-tertiary-fixed: #072711;\n"
     		+ "  --gm3-sys-color-on-tertiary-fixed-rgb: 7,39,17;\n"
     		+ "  --gm3-sys-color-on-tertiary-fixed-variant: #0f5223;\n"
     		+ "  --gm3-sys-color-on-tertiary-fixed-variant-rgb: 15,82,35;\n"
     		+ "  --gm3-sys-color-outline: #747775;\n"
     		+ "  --gm3-sys-color-outline-rgb: 116,119,117;\n"
     		+ "  --gm3-sys-color-outline-variant: #c4c7c5;\n"
     		+ "  --gm3-sys-color-outline-variant-rgb: 196,199,197;\n"
     		+ "  --gm3-sys-color-primary: #0b57d0;\n"
     		+ "  --gm3-sys-color-primary-rgb: 11,87,208;\n"
     		+ "  --gm3-sys-color-primary-container: #d3e3fd;\n"
     		+ "  --gm3-sys-color-primary-container-rgb: 211,227,253;\n"
     		+ "  --gm3-sys-color-primary-fixed: #d3e3fd;\n"
     		+ "  --gm3-sys-color-primary-fixed-rgb: 211,227,253;\n"
     		+ "  --gm3-sys-color-primary-fixed-dim: #a8c7fa;\n"
     		+ "  --gm3-sys-color-primary-fixed-dim-rgb: 168,199,250;\n"
     		+ "  --gm3-sys-color-scrim: #000;\n"
     		+ "  --gm3-sys-color-scrim-rgb: 0,0,0;\n"
     		+ "  --gm3-sys-color-secondary: #00639b;\n"
     		+ "  --gm3-sys-color-secondary-rgb: 0,99,155;\n"
     		+ "  --gm3-sys-color-secondary-container: #c2e7ff;\n"
     		+ "  --gm3-sys-color-secondary-container-rgb: 194,231,255;\n"
     		+ "  --gm3-sys-color-secondary-fixed: #c2e7ff;\n"
     		+ "  --gm3-sys-color-secondary-fixed-rgb: 194,231,255;\n"
     		+ "  --gm3-sys-color-secondary-fixed-dim: #7fcfff;\n"
     		+ "  --gm3-sys-color-secondary-fixed-dim-rgb: 127,207,255;\n"
     		+ "  --gm3-sys-color-shadow: #000;\n"
     		+ "  --gm3-sys-color-shadow-rgb: 0,0,0;\n"
     		+ "  --gm3-sys-color-surface: #fff;\n"
     		+ "  --gm3-sys-color-surface-rgb: 255,255,255;\n"
     		+ "  --gm3-sys-color-surface-bright: #fff;\n"
     		+ "  --gm3-sys-color-surface-bright-rgb: 255,255,255;\n"
     		+ "  --gm3-sys-color-surface-container: #f0f4f9;\n"
     		+ "  --gm3-sys-color-surface-container-rgb: 240,244,249;\n"
     		+ "  --gm3-sys-color-surface-container-high: #e9eef6;\n"
     		+ "  --gm3-sys-color-surface-container-high-rgb: 233,238,246;\n"
     		+ "  --gm3-sys-color-surface-container-highest: #dde3ea;\n"
     		+ "  --gm3-sys-color-surface-container-highest-rgb: 221,227,234;\n"
     		+ "  --gm3-sys-color-surface-container-low: #f8fafd;\n"
     		+ "  --gm3-sys-color-surface-container-low-rgb: 248,250,253;\n"
     		+ "  --gm3-sys-color-surface-container-lowest: #fff;\n"
     		+ "  --gm3-sys-color-surface-container-lowest-rgb: 255,255,255;\n"
     		+ "  --gm3-sys-color-surface-dim: #d3dbe5;\n"
     		+ "  --gm3-sys-color-surface-dim-rgb: 211,219,229;\n"
     		+ "  --gm3-sys-color-surface-tint: #6991d6;\n"
     		+ "  --gm3-sys-color-surface-tint-rgb: 105,145,214;\n"
     		+ "  --gm3-sys-color-surface-variant: #e1e3e1;\n"
     		+ "  --gm3-sys-color-surface-variant-rgb: 225,227,225;\n"
     		+ "  --gm3-sys-color-tertiary: #146c2e;\n"
     		+ "  --gm3-sys-color-tertiary-rgb: 20,108,46;\n"
     		+ "  --gm3-sys-color-tertiary-container: #c4eed0;\n"
     		+ "  --gm3-sys-color-tertiary-container-rgb: 196,238,208;\n"
     		+ "  --gm3-sys-color-tertiary-fixed: #c4eed0;\n"
     		+ "  --gm3-sys-color-tertiary-fixed-rgb: 196,238,208;\n"
     		+ "  --gm3-sys-color-tertiary-fixed-dim: #6dd58c;\n"
     		+ "  --gm3-sys-color-tertiary-fixed-dim-rgb: 109,213,140;\n"
     		+ "}\n"
     		+ "body {\n"
     		+ "  color: #202124;\n"
     		+ "}\n"
     		+ "body, input, textarea, select {\n"
     		+ "  font-family: \"Google Sans\",Roboto,RobotoDraft,Helvetica,Arial,sans-serif;\n"
     		+ "}\n"
     		+ "body, input, textarea, select, #loading {\n"
     		+ "  font-family: arial,sans-serif;\n"
     		+ "}</style></head>" + "<body>" + "<h1>Ha habido un error</h1>"
                 + "<a> Tu correo no ha podido ser identificado por el sistema. Intentelo de nuevo o contacte con aonsolutions.org para darse de alta. Gracias.</a>" + 
                  "<div dir=\"ltr\"><div dir=\"ltr\"><div dir=\"ltr\"><br></div><div dir=\"ltr\">Un cordial saludo,<br></div><div dir=\"ltr\"><br></div><div dir=\"ltr\"><div><font size=\"4\" color=\"#000000\"><b>JULIO GARCÍA</b></font><font style=\"font-weight:bold\"><font size=\"4\" color=\"#000000\">&nbsp;</font><i style=\"color:rgb(102,102,102)\">·</i></font><font color=\"#666666\">&nbsp;Consultor IT</font></div><div><div><span style=\"color:rgb(102,102,102)\">Director Desarrollo de Negocio</span></div><div><span style=\"color:rgb(102,102,102)\"><br></span></div><div><div style=\"color:rgb(34,34,34)\"><span style=\"color:rgb(102,102,102)\"><img width=\"200\" height=\"34\" src=\"https://ci3.googleusercontent.com/mail-sig/AIorK4xLVOXTMbDq0pVxk7P25WOLVxN1-JGNOdowb6jJrBcSz7KZcDvSVMfTS_S2NUBRMR-_SALXhTPc_DAZ\" class=\"CToWUd\" data-bit=\"iit\"><br></span></div><div style=\"color:rgb(34,34,34)\"><div><font size=\"1\" style=\"color:rgb(102,102,102)\">Atención al&nbsp;</font><font color=\"#666666\" size=\"1\">Cliente | 900 831 205 | aonSolutions.es</font></div><div><font size=\"1\" style=\"color:rgb(102,102,102)\">HORARIO: L</font><span style=\"color:rgb(102,102,102);font-size:x-small\">unes a Jueves de 8 a 15h. Viernes de 8 a 14h..</span></div></div></div></div><div><span style=\"font-size:x-small\"><font color=\"#000000\"><br></font></span></div><div><div><span style=\"font-family:Arial,sans-serif;font-size:9.33333px\"><font color=\"#666666\">Este mensaje está dirigido de manera exclusiva a su destinatario y puede contener información privada y confidencial. No lo reenvíe, copie o distribuya a terceros que no deban conocer su contenido.&nbsp;En caso de haberlo recibido por error,&nbsp;&nbsp;rogamos lo notifique al remitente y proceda a su borrado, así como al de cualquier documento que pudiera adjuntarse.</font></span></div><div><span style=\"font-family:Arial,sans-serif;font-size:9.33333px\"><font color=\"#666666\"><br></font></span></div><div style=\"text-align:center\"><span style=\"color:rgb(0,0,0);font-family:Arial,sans-serif;font-size:x-small\">Si quiere saber más sobre nuestras prácticas relativas a la privacidad, consulte nuestro:</span><br></div><div style=\"text-align:center\"><a href=\"https://aonsolutions.es/politicas-legales/aviso-legal/\" style=\"font-family:Roboto,Arial,Helvetica;font-size:11px;font-weight:700;color:rgb(26,24,27)\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://aonsolutions.es/politicas-legales/aviso-legal/&amp;source=gmail&amp;ust=1715336740086000&amp;usg=AOvVaw0qayja596gnjTHxeSxPaKm\">aviso legal</a><span style=\"color:rgb(26,24,27);font-family:Roboto,Arial,Helvetica;font-size:11px;font-weight:700\">&nbsp;</span><span style=\"color:rgb(26,24,27);font-family:Roboto,Arial,Helvetica;font-size:11px;font-weight:700\">|&nbsp;</span><a href=\"https://aonsolutions.es/politicas-legales/politica-privacidad/\" style=\"font-family:Roboto,Arial,Helvetica;font-size:11px;font-weight:700;color:rgb(26,24,27)\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://aonsolutions.es/politicas-legales/politica-privacidad/&amp;source=gmail&amp;ust=1715336740087000&amp;usg=AOvVaw2WMRdV19ImkkGstXhQa4ku\">política de privacidad<br></a></div></div></div></div></div>" +   		 
                 "</body>" + "</html>";
     
		 
     Destination destination = Destination.builder()
                     .toAddresses(recipient)
                     .build();

     Content content = Content.builder()
                     .data(bodyHTML)
                     .build();

     Content sub = Content.builder()
                     .data(subject)
                     .build();

     Body body = Body.builder()
                     .html(content)
                     .build();

     Message msg = Message.builder()
                     .subject(sub)
                     .body(body)
                     .build();

     EmailContent emailContent = EmailContent.builder()
                     .simple(msg)
                     .build();

     SendEmailRequest emailRequest = SendEmailRequest.builder()
                     .destination(destination)
                     .content(emailContent)
                     .fromEmailAddress(sender)
                     .build();
     
     Region region = Region.EU_WEST_1;    
     SesV2Client client = SesV2Client.builder()
                     .region(region)
                     .build();
     
     try {
             System.out.println("Attempting to send an email through Amazon SES "
                             + "using the AWS SDK for Java...");
             client.sendEmail(emailRequest);
             System.out.println("email was sent");

     } catch (SesV2Exception e) {
             System.err.println(e.awsErrorDetails().errorMessage());
             System.exit(1);
     }
}
}