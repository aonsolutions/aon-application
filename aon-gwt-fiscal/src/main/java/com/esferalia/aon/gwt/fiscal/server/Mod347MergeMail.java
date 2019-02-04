package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Mod347Key;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Merge Mail Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model347MergeMail" })
public class Mod347MergeMail extends HttpServlet {

	private static final long serialVersionUID = 1812242250894272353L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod347"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Mod347 mod347 = FISCAL.getMod347(domainName, domainId, user,id);

// ----

			String s = mod347.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}
			String fileName = "Mod347" + "_" + mod347.getYear() + "_" + sb.toString();			
			resp.setContentType(MimeType.CSV.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.CSV.getExtension()+ "\";");
			OutputStreamWriter writer = new OutputStreamWriter(resp.getOutputStream(),"ISO-8859-1"); 
			
			writer.append("id");
			writer.append(CSV_DELIMITER);
			writer.append("tipo");
			writer.append(CSV_DELIMITER);
			writer.append("documento");
			writer.append(CSV_DELIMITER);
			writer.append("razon_social");
			writer.append(CSV_DELIMITER);
			writer.append("importe_trimestre_1");
			writer.append(CSV_DELIMITER);
			writer.append("importe_trimestre_2");
			writer.append(CSV_DELIMITER);
			writer.append("importe_trimestre_3");
			writer.append(CSV_DELIMITER);
			writer.append("importe_trimestre_4");
			writer.append(CSV_DELIMITER);
			writer.append("importe_anual");
			writer.append(CSV_DELIMITER);
			writer.append("email");
			writer.append(CSV_DELIMITER);
			writer.append("direccion_tipo");
			writer.append(CSV_DELIMITER);
			writer.append("direccion");
			writer.append(CSV_DELIMITER);
			writer.append("direccion_numero");
			writer.append(CSV_DELIMITER);
			writer.append("direccion_complemento1");
			writer.append(CSV_DELIMITER);
			writer.append("direccion_complemento2");
			writer.append(CSV_DELIMITER);
			writer.append("direccion_codigo_postal");
			writer.append(CSV_DELIMITER);
			writer.append("direccion_ciudad");
			writer.append(CSV_DELIMITER);
			writer.append("direccion_provincia");
			writer.append(CSV_DELIMITER);
			
			
			writer.append(System.lineSeparator());
			String tipo = "";
			for (Mod347Declared declared : mod347.getDeclared()) {
				Integer registryId = null;
				if (Mod347Key.B == declared.getType()) {
					Customer customer = AON.getCustomer(domainName, domainId, user, p-> p.getDomainProperty().eq(domainId).and(p.getDocumentProperty().eq(declared.getDocument())));
					registryId = (customer != null && customer.getRegistry() != null )?customer.getRegistry().getId():null;
					tipo = "Cliente";
				} 
				if (Mod347Key.A == declared.getType()) {
					Supplier supplier = AON.getSupplier(domainName, domainId, user, p-> p.getDomainProperty().eq(domainId).and(p.getDocumentProperty().eq(declared.getDocument())))
							.orElse(new Supplier());
					registryId = (supplier != null)?supplier.getId():null;
					tipo = "Proveedor";
					if (registryId == null) {
						Creditor creditor = AON.getBasicCreditors(domainName, domainId, user, p-> p.getDomainProperty().eq(domainId).and(p.getDocumentProperty().eq(declared.getDocument())))
								.findFirst().orElse(new Creditor());
						registryId = (creditor != null)?creditor.getId():null;
						tipo = "Acreedor";
					}
				}
				final Integer registry = registryId;
				RAddress address = null;
				RegistryMedia media = null;
				if (registry != null) {
					address = AON.getRAddress(domainName, domainId, user, p -> p.getRegistryProperty().eq(registry).and(p.getTypeProperty().eq( (byte) 0 )));
					LinkedList<RegistryMedia> medias = AON.getRMediaList(domainName, domainId, user, p -> p.getRegistryProperty().eq(registry).and(p.getMediaProperty().eq( (byte) 4 )));
					if (medias != null && medias.size() > 0 ) {
						for (int i = 0 ; i< medias.size(); i++) {
							media = medias.get(i);
							if (AonStringUtils.isNotBlank( media.getValue())) break;
						}
					}
				}
				if (address == null) address = new RAddress();
				if (media == null) media = new RegistryMedia();
				
				writer.append( AonNumberUtils.toString( registryId ));
				writer.append(CSV_DELIMITER);
				write(writer,tipo);
				writer.append(CSV_DELIMITER);
				write(writer,declared.getDocument());
				writer.append(CSV_DELIMITER);
				write(writer,declared.getName());
				writer.append(CSV_DELIMITER);
				writer.append( AonNumberUtils.toString( declared.getFirstQuarterAmount() ));
				writer.append(CSV_DELIMITER);
				writer.append( AonNumberUtils.toString( declared.getSecondQuarterAmount() ));
				writer.append(CSV_DELIMITER);
				writer.append( AonNumberUtils.toString( declared.getThirdQuarterAmount() ));
				writer.append(CSV_DELIMITER);
				writer.append( AonNumberUtils.toString( declared.getFourthQuarterAmount() ));
				writer.append(CSV_DELIMITER);
				writer.append( AonNumberUtils.toString( declared.getAmount() ));
				writer.append(CSV_DELIMITER);
				write(writer,AonStringUtils.defaultString(media.getValue()));
				writer.append(CSV_DELIMITER);
				write(writer,AonStringUtils.defaultString(address.getStreet_type()));
				writer.append(CSV_DELIMITER);
				write(writer,AonStringUtils.defaultString(address.getAddress()));
				writer.append(CSV_DELIMITER);
				write(writer,AonStringUtils.defaultString(address.getNumber()));
				writer.append(CSV_DELIMITER);
				write(writer,AonStringUtils.defaultString(address.getAddress2()));
				writer.append(CSV_DELIMITER);
				write(writer,AonStringUtils.defaultString(address.getAddress3()));
				writer.append(CSV_DELIMITER);
				write(writer,AonStringUtils.defaultString(address.getZip()));
				writer.append(CSV_DELIMITER);
				write(writer,AonStringUtils.defaultString(address.getCity()));
				writer.append(CSV_DELIMITER);
				write(writer,AonStringUtils.defaultString(address.getGeozoneName()));
				writer.append(System.lineSeparator());
			}			
			writer.flush();
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
    private static final char CSV_DELIMITER = ',';
    private static final char CSV_QUOTE = '"';
    private static final String CSV_QUOTE_STR = String.valueOf(CSV_QUOTE);
    private static final char[] CSV_SEARCH_CHARS = new char[] {CSV_DELIMITER, CSV_QUOTE, '\r', '\n'};

    private void write(final Writer out, final CharSequence input) throws IOException {

        if (AonStringUtils.containsNone(input.toString(), CSV_SEARCH_CHARS)) {
            out.write(input.toString());
        } else {
            out.write(CSV_QUOTE);
            out.write(AonStringUtils.replace(input.toString(), CSV_QUOTE_STR, CSV_QUOTE_STR + CSV_QUOTE_STR));
            out.write(CSV_QUOTE);
        }
    }
}


