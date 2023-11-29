package com.esferalia.aon.gwt.fiscal.server.fiscal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Matrix Send AEAT", urlPatterns = { "/aon_gwt_fiscal/ms/MatrixSendAEAT" })
public class MatrixSendAEAT extends HttpServlet {

	private static final long serialVersionUID = 6152551748473230508L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			AEATParams aeatParams = ModelAdmonUtils.getAEATParams(req);
			
			send(resp, aeatParams);
			
//			Occam occam = new Occam()
//					.setDomainName(aeatParams.getDomainName())
//					.setDomain(aeatParams.getDomainId())
//					.setUser(aeatParams.getUser());
//			
//			Mod303 mod303 = MODEL303.get(occam, ModelAdmonUtils.getFiscalModelId(aeatParams));
//			if (mod303 == null) {
//				throw new AonCoreException("[INT] Modelo no encontrado");
//			}
//			ModelAdmonUtils.send(resp, aeatParams, mod303);
			
		} catch (Exception e) {
			ModelAdmonUtils.giveExceptionBack(resp,e.getMessage());
		}
	}

	// FALTA
	private void send(HttpServletResponse resp, AEATParams aeatParams) throws IOException {
		
		// PRUEBA 
		resp.setHeader(AonHttpUtils.CONTENT_TYPE, MimeType.HTML.getName());
		resp.setHeader(AonHttpUtils.CONTENT_ENCODING, StandardCharsets.UTF_8.displayName());				
		AonIOUtils.write( "PRESENTACION MULTIPLE DE MODELOS DESDE LA MATRIZ " + aeatParams.getSelected() , resp.getOutputStream() );
		resp.flushBuffer();
		// ------
		
		// DENTRO DE AEATPARAMS ESTARA EL ARRAY CON LOS MODELOS SELECCIONADOS MXXX_ID
		// RECORRER EL ARRAY, PARA CADA ELEMENTO EXTRAER EL MODELO Y EL ID
		// SEGUN EL MODELO LEER EL MODELO CON EL ID LLAMANDO AL GET CORRESPONDIENTE
		// UNA VEZ QUE TENEMOS EL MODELO, LLAMAR A SEND CON EL MODELO O A TGVIONLINE SI ES INFORMATIVA (180, 190, ...)
		// CONTROLAR EN SEND COMO SE ARMAN LAS RESPUESTAS PUES EN ESTE CASO SE DEVOLVERA UNA PAGINA CON TODOS LOS 
		//   ERRORES DE TODAS LAS DECLARACIONES, POR ESO ANTES DE CADA DECLARACION HAY QUE PONER EL DNI Y EL 
		//   NOMBRE DE LA DECLARACION
		// A LA VUELTA SE MOSTRARA LA WEB CON LOS RESULTADOS Y SE REFRESCARA LA PANTALLA
		
	}
	
}
