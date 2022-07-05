package com.esferalia.aon.in.payroll.aws.lambda;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SESSMTPSender {
	
	

    // Replace sender@example.com with your "From" address.
    // This address must be verified.
    static final String FROM = "no-reply@aon.solutions";	//"sender@example.com";
    static final String FROMNAME = "aonSolutions"; 			//"Sender Name";
	
    // Replace recipient@example.com with a "To" address. If your account 
    // is still in the sandbox, this address must be verified.
    static final String TO1 = "rtrepiana@aonsolutions.es"; //"recipient@example.com";
    static final String TO2 = "jgarcia@aonsolutions.es"; //"recipient@example.com";
    
    //ses-smtp-user.aon.solutions
 
    // Replace smtp_username with your Amazon SES SMTP user name.
    static final String SMTP_USERNAME = "AKIARG5OEKO7NRW5I5EZ"; //"smtp_username";
    
    // Replace smtp_password with your Amazon SES SMTP password.
    static final String SMTP_PASSWORD = "BH+U6/fqQ2gLPH/byNc6WMFfWLjvoKSKXDzPW/P0s4B5"; //"smtp_password";
    
    // The name of the Configuration Set to use for this message.
    // If you comment out or remove this variable, you will also need to
    // comment out or remove the header below.
    static final String CONFIGSET = "ConfigSet";
    
    // Amazon SES SMTP host name. This example uses the US West (Oregon) region.
    // See https://docs.aws.amazon.com/ses/latest/DeveloperGuide/regions.html#region-endpoints
    // for more information.
    static final String HOST = "email-smtp.eu-west-1.amazonaws.com"; //"email-smtp.us-west-2.amazonaws.com";
    
    // The port you will connect to on the Amazon SES SMTP endpoint. 
    static final int PORT = 587;
    
    static final String SUBJECT = "aonSolutions Traspaso Finalizado" ;
    
    static final String BODY = String.join(
    	    System.getProperty("line.separator"),
    	    "<h1>Amazon SES SMTP Email Test</h1>",
    	    "<p>This email was sent with Amazon SES using the ", 
    	    "<a href='https://github.com/javaee/javamail'>Javamail Package</a>",
    	    " for <a href='https://www.java.com'>Java</a>."
    	);

    public static void send(Address to [], Object ...args) throws Exception {

        // Create a Properties object to contain connection configuration information.
    	Properties props = System.getProperties();
    	props.put("mail.transport.protocol", "smtp");
    	props.put("mail.smtp.port", PORT); 
    	props.put("mail.smtp.starttls.enable", "true");
    	props.put("mail.smtp.auth", "true");

        // Create a Session object to represent a mail session with the specified properties. 
    	Session session = Session.getDefaultInstance(props);

        // Create a message with the specified information. 
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(FROM,FROMNAME));
        for ( Address address : to ) {
        	try {
        		msg.addRecipient(Message.RecipientType.TO, address);
        	} catch ( MessagingException  e ) {
        		System.err.println( address.toString() + ":" + e.getMessage());
        	}
        }
        msg.addRecipient(Message.RecipientType.CC, new InternetAddress(TO1));
        msg.addRecipient(Message.RecipientType.CC, new InternetAddress(TO2));
        msg.setSubject(SUBJECT);
        msg.setContent(String.format(BODY_FORMAT, args),"text/html");
        
        
        // Add a configuration set header. Comment or delete the 
        // next line if you are not using a configuration set
        // msg.setHeader("X-SES-CONFIGURATION-SET", CONFIGSET);
            
        // Create a transport.
        Transport transport = session.getTransport();
                    
        // Send the message.
        try
        {
            System.out.println("Sending...");
            
            // Connect to Amazon SES using the SMTP username and password you specified above.
            transport.connect(HOST, SMTP_USERNAME, SMTP_PASSWORD);
        	
            // Send the email.
            transport.sendMessage(msg, msg.getAllRecipients());
            System.out.println("Email sent!");
        }
        catch (Exception ex) {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
        }
        finally
        {
            // Close and terminate the connection.
            transport.close();
        }
    }
    
    private static final String BODY_FORMAT = 
    "<table style=\"margin:0 auto;width:667.0px\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" + 
    "<tbody>\n" + 
    "<tr>\n" + 
    "<td align=\"center\">\n" + 
    "<div>&nbsp;</div>\n" + 
    "<div>\n" + 
    "<table style=\"width:667px\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" + 
    "<tbody>\n" + 
    "<tr>\n" + 
    "<td style=\"height:80px;\">\n" + 
    "<table style=\"width:100%%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" + 
    "<tbody>\n" + 
    "<tr>\n" + 
    "<td width=\"50%%\">\n" + 
    "<div><a href=\"http://www.aonsolutions.es/\" target=\"_blank\" ><img style=\"width: 120\" src=\"https://aonsolutions.es/wp-content/uploads/2020/11/logo-aons-footer.svg\" border=\"0\" alt=\"Aon Solutions\" class=\"CToWUd\"></a></div>\n" + 
    "</td>\n" + 
    "<td style=\"padding-right:10px;color:#36579a;font-family:Arial,Helvetica,sans-serif;font-size:18px;text-align:center\" valign=\"bottom\">\n" + 
    "<div style=\"font-family:Arial,Helvetica,sans-serif\"><strong>\n" + 
    "<div  style=\"color:#888888;font-family:arial,&quot;lucida Grande&quot;,&quot;Trebuchet MS&quot;,sans-serif;font-size:12px;text-align:center\">\n" + 
    "<div  style=\"font-size:1.1em\">Atención al Cliente</div>\n" + 
    "<div  style=\"font-size:1.3em;font-weight:bold\">902 121 009 · 945 121 010</div>\n" + 
    "</div>\n" + 
    "<div  style=\"color:#888888;font-family:arial,&quot;lucida Grande&quot;,&quot;Trebuchet MS&quot;,sans-serif;font-size:12px;text-align:center;font-weight:bold\"><a  style=\"text-decoration-line:none;color:#888888\" title=\"Enviar email\" name=\"m_-8702369465337072269_m_-2506186872717109275_aonContent:mainForm:j_id10806\" href=\"http://aon.esferalia.com/#\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=http://aon.esferalia.com/%%23&amp;source=gmail&amp;ust=1591774234991000&amp;usg=AFQjCNHZLmOfiAOdxCAApSg8Giy92KyK4w\">soporte@aonSolutions.es</a></div>\n" + 
    "</strong></div>\n" + 
    "</td>\n" + 
    "</tr>\n" + 
    "</tbody>\n" + 
    "</table>\n" + 
    "</td>\n" + 
    "</tr>\n" + 
    "<tr>\n" + 
    "<td style=\"height:41px;background-position:0px 0px;text-align:left;background-color:#000000\"><span style=\"color:#ffffff\"><span style=\"font-size:large\">&nbsp;Comunicado C.A.U.&nbsp;</span></span></td>\n" + 
    "</tr>\n" + 
    "</tbody>\n" + 
    "</table>\n" + 
    "</div>\n" + 
    "<table style=\"width:667.0px\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" + 
    "<tbody>\n" + 
    "<tr>\n" + 
    "<td>\n" + 
    "<p style=\"text-align:justify\">&nbsp;</p>\n" + 
    "<p style=\"text-align:justify\">Estimado cliente,</p>\n" + 
    "<p style=\"text-align:justify\">Se han traspasado %d nóminas</p>\n" + 
    "<p style=\"text-align:justify\">El equipo de soporte.</p>\n" + 
    "<p style=\"text-align:justify\">&nbsp;</p>\n" + 
    "</td>\n" + 
    "</tr>\n" + 
    "</tbody>\n" + 
    "</table>\n" + 
    "<div>\n" + 
    "<table style=\"width:667px\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" + 
    "<tbody>\n" + 
    "<tr>\n" + 
    "<td style=\"height:44px;background-position:0px 0px;color:#ffffff;text-align:center;background-color:#000000\"><span style=\"font-size:x-small\"><strong>AON SOLUTIONS, S.L. · CIF: B01487271 · c/ Duque de Wellington, 52 · 01010 Vitoria-Gasteiz · <a style=\"color:#ffffff;text-decoration:none\" href=\"mailto:soporte@aonSolutions.es\" target=\"_blank\">soporte@aonSolutions.es</a></strong></span></td>\n" + 
    "</tr>\n" + 
    "<tr>\n" + 
    "<td style=\"padding-left:10px;padding-right:10px;text-align:justify;padding-top:5px;padding-bottom:5px\"><span style=\"font-family:&quot;Arial&quot;,&quot;sans-serif&quot;;color:gray;font-size:7.5pt\">La información incluida en este mensaje y sus anexos es CONFIDENCIAL y para USO EXCLUSIVO de sus destinatarios. No está permitida su divulgación y/o reproducción sin autorización. Si ha recibido este mensaje y no le incumbe, le rogamos nos los comunique y proceda a su borrado. </span><span style=\"font-family:&quot;Arial&quot;,&quot;sans-serif&quot;;color:gray;font-size:7.5pt\" lang=\"EN-GB\">Gracias.</span></td>\n" + 
    "</tr>\n" + 
    "<tr>\n" + 
    "<td style=\"padding-left:10px;padding-right:10px;font-family:&quot;Arial&quot;,&quot;sans-serif&quot;;color:#459638;font-size:8.5pt;padding-bottom:5px\"><em>Por favor, no imprima este e-mail si no es realmente necesario.</em></td>\n" + 
    "</tr>\n" + 
    "<tr>\n" + 
    "<td style=\"padding-left:10px;padding-right:10px;text-align:justify\"><em><span style=\"font-family:&quot;Arial&quot;,&quot;sans-serif&quot;;font-size:6.5pt;padding-top:5px\">Sus datos de carácter personal han sido recogidos de acuerdo con lo dispuesto en la Ley Orgánica 15/1999 de Protección de Datos de Carácter Personal, y se encuentran almacenados en un fichero propiedad de AON SOLUTIONS,&nbsp;S.L. con domicilio en C/ Duque de Wellington 52&nbsp;de Vitoria-Gasteiz, Alava, cuyo fin es la comunicación con los clientes. De acuerdo con la Ley anterior, tiene derecho a ejercer los derechos de acceso, rectificación y cancelación de los datos enviando una solicitud por escrito. Para ELIMINAR su dirección de correo de nuestra base de datos tiene que escribir un e-mail a <strong><a style=\"color:#36579a;text-decoration:none\" href=\"mailto:info@aonSolutions.es\" target=\"_blank\">LOPD@aonSolutions.es</a></strong> incluyendo razón social y persona de contacto e incluyendo en el asunto BAJA DE LISTA DE CORREO.</span></em></td>\n" + 
    "</tr>\n" + 
    "</tbody>\n" + 
    "</table>\n" + 
    "</div>\n" + 
    "</td>\n" + 
    "</tr>\n" + 
    "</tbody>\n" + 
    "</table>";
}