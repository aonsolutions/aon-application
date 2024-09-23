package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;

import jakarta.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediService;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.io.DataUrl;
import com.esferalia.aon.watson.server.io.DataUrlSerializer;
import com.esferalia.aon.watson.server.io.IDataUrlSerializer;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;

@WebServlet(name = "TEDI Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/Tedi" })
public class TediServiceImpl extends AonStatelessRemoteServiceServlet implements TediService {

	private static final long serialVersionUID = -2121272613749054639L;

	@Override
	public TediResult parseInvoice(Occam occam, String fileName, String content) throws AonCoreException {
		try {
			IDataUrlSerializer serializer = new DataUrlSerializer();
			DataUrl unserialized = serializer.unserialize(content);
			ByteArrayInputStream input = new ByteArrayInputStream(unserialized.getData());
			String extension = AonStringUtils.substringAfterLast(fileName, ".");
			TediResult result = TEDI.parse(new TediContext().setOccam(occam), input, MimeType.getByExtension(extension));
			result.getAccountingInvoice()
				.setTediParsed(true);
			return result;
		} catch ( TediException t) {
			t.printStackTrace();
			throw new AonCoreException(t);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new AonCoreException(e);
		}
	}
	
	@Override
	public TediResult validateInvoice(Occam occam, TediResult result ) throws AonCoreException {
		try {
			return TEDI.validateInvoice(new TediContext().setOccam(occam), result);
		} catch ( TediException t) {
			t.printStackTrace();
			throw new AonCoreException(t);
		} 	
	}
	
}
