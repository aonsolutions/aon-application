package com.esferalia.aon.ingenet.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TestIngenetDeliveryServlet  {

	private static String getValue(){
		String xml = "";
		
//		String filePath = "//tmp//delivery_example.xml";
//		try (
//			BufferedReader xml_br = new BufferedReader(new FileReader(filePath))) {
//			String sCurrentLine;
//			while ((sCurrentLine = xml_br.readLine()) != null) {
//				xml += sCurrentLine;
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" 
				+ "<ALBARANES>"
				+ "	<DATOS_ALBARANES>"
				+ "		<NUMERO>1</NUMERO>"
				+ "		<DATOS_CLIENTE><DATOS_REGISTRO>"
				+ "			<DATOS_DOCUMENTO><TIPO_DOCUMENTO>1</TIPO_DOCUMENTO><DOCUMENTO>A46103834</DOCUMENTO></DATOS_DOCUMENTO>"
				+ "		</DATOS_REGISTRO></DATOS_CLIENTE>"
				+ "		<DATOS_DIRECCION_ENTREGA>"
				+ "			<DIRECCION>PADULETA</DIRECCION><DIRECCION2></DIRECCION2><DIRECCION3></DIRECCION3>"
				+ "			<CIUDAD>VITORIA-GASTEIZ</CIUDAD><CODIGO_POSTAL>01015</CODIGO_POSTAL>"
				+ "		</DATOS_DIRECCION_ENTREGA>"
				+ "		<FECHA_EMISION>20170112</FECHA_EMISION>"
				+ "		<COMENTARIOS></COMENTARIOS>"
				+ "		<DATOS_CENTRO_TRABAJO>"
				+ "			<DESCRIPCION>PADULETA,1 </DESCRIPCION>"
				+ "			<DATOS_DIRECCION>"
				+ "				<DIRECCION>PADULETA</DIRECCION><DIRECCION2></DIRECCION2><DIRECCION3></DIRECCION3>"
				+ "				<CIUDAD>VITORIA-GASTEIZ</CIUDAD><CODIGO_POSTAL>01015</CODIGO_POSTAL>"
				+ "			</DATOS_DIRECCION>"
				+ "		</DATOS_CENTRO_TRABAJO>"
				+ "		<LINEAS_ALBARAN>"
				+ "			<DATOS_LINEA_ALBARAN>"
				+ "				<LINEA>1</LINEA>"
				+ "				<PRODUCTO>"
				+ "					<CODIGO>08MLV082</CODIGO>"
				+ "					<NOMBRE>69022 PATATA MIRALOBUENO GRANEL 8KG. </NOMBRE>"
				+ "					<DESCRIPCION></DESCRIPCION>"
				+ "					<NUMERO_LOTE_SERIE>69022-001</NUMERO_LOTE_SERIE>"
				+ "					<FECHA_LOTE_SERIE>20140112</FECHA_LOTE_SERIE>"
				+ "					<CODIGO_BARRAS>38424273000537</CODIGO_BARRAS>"
				+ "					<REFERENCIA_CLIENTE>08480000690227</REFERENCIA_CLIENTE>"
				+ "				</PRODUCTO>"
				+ "				<DESCRIPCION>PATATA MIRALOBUENO GRANEL 8KG. </DESCRIPCION>"
				+ "				<CANTIDAD>840.000</CANTIDAD>"
				+ "				<COMPOSICION_PRODUCTO_ELABORADO>"
				+ "					<DATOS_COMPOSICION_PRODUCTO>"
				+ "						<PRODUCTO>"
				+ "							<CODIGO>08MLV082</CODIGO>"
				+ "							<NOMBRE>69022 PATATA MIRALOBUENO GRANEL 8KG. </NOMBRE>"
				+ "							<DESCRIPCION></DESCRIPCION>"
				+ "							<NUMERO_LOTE_SERIE>001</NUMERO_LOTE_SERIE>"
				+ "							<FECHA_LOTE_SERIE>20140112</FECHA_LOTE_SERIE>"
				+ "							<CODIGO_BARRAS>38424273000537</CODIGO_BARRAS>"
				+ "							<REFERENCIA_CLIENTE>08480000690227</REFERENCIA_CLIENTE>"
				+ "						</PRODUCTO>"
				+ "						<CANTIDAD>540.000</CANTIDAD>"
				+ "					</DATOS_COMPOSICION_PRODUCTO>"
				+ "					<DATOS_COMPOSICION_PRODUCTO>"
				+ "						<PRODUCTO>"
				+ "							<CODIGO>08MLV082</CODIGO>"
				+ "							<NOMBRE>69022 PATATA MIRALOBUENO GRANEL 8KG. </NOMBRE>"
				+ "							<DESCRIPCION></DESCRIPCION>"
				+ "							<NUMERO_LOTE_SERIE>002</NUMERO_LOTE_SERIE>"
				+ "							<FECHA_LOTE_SERIE>20140112</FECHA_LOTE_SERIE>"
				+ "							<CODIGO_BARRAS>38424273000537</CODIGO_BARRAS>"
				+ "							<REFERENCIA_CLIENTE>08480000690227</REFERENCIA_CLIENTE>"
				+ "						</PRODUCTO>"
				+ "						<CANTIDAD>300.000</CANTIDAD>"
				+ "					</DATOS_COMPOSICION_PRODUCTO>"
				+ "				</COMPOSICION_PRODUCTO_ELABORADO>"
				+ "				<DATOS_ELABORACION_ORIGEN>"
				+ "					<SERIE>PV17</SERIE>"
				+ "					<NUMERO>1</NUMERO>"
				+ "				</DATOS_ELABORACION_ORIGEN>"
				+ "				<ENVASE>N</ENVASE>"
				+ "			</DATOS_LINEA_ALBARAN>"
				+ "			<DATOS_LINEA_ALBARAN>"
				+ "				<LINEA>2</LINEA>"
				+ "				<PRODUCTO>"
				+ "					<CODIGO>111475</CODIGO>"
				+ "					<NOMBRE>CAJA LABRADOR ART 101901</NOMBRE>"
				+ "				</PRODUCTO>"
				+ "				<DESCRIPCION></DESCRIPCION>"
				+ "				<CANTIDAD>10</CANTIDAD>"
				+ "				<ENVASE>S</ENVASE>"
				+ "			</DATOS_LINEA_ALBARAN>"
				+ "		</LINEAS_ALBARAN>"
				+ "		<DATOS_HOJA_RUTA>"
				+ "			<FECHA_EMISION>20140112</FECHA_EMISION>"
				+ "			<DATOS_AGENCIA_TRANSPORTE>"
				+ "				<DATOS_REGISTRO>"
				+ "					<DATOS_DOCUMENTO>"
				+ "						<PAIS_DOCUMENTO><CODIGO>724</CODIGO><DESCRIPCION>ESPANA</DESCRIPCION></PAIS_DOCUMENTO>"
				+ "						<TIPO_DOCUMENTO>1</TIPO_DOCUMENTO>"
				+ "						<DOCUMENTO>B01496413</DOCUMENTO>"
				+ "					</DATOS_DOCUMENTO>"
				+ "					<NOMBRE>VITOTRANS, S.L.</NOMBRE>"
				+ "				</DATOS_REGISTRO>"
				+ "			</DATOS_AGENCIA_TRANSPORTE>"
				+ "			<REFERNCIA_AGENCIA_TRANSPORTE>00000000000000000</REFERNCIA_AGENCIA_TRANSPORTE>"
				+ "			<NUMERO_MATRICULA>0000XXX</NUMERO_MATRICULA>"
				+ "			<NOMBRE_CONDUCTOR>Nico Rosberg</NOMBRE_CONDUCTOR>"
				+ "			<DOCUMENTO_CONDUCTOR>00000000A</DOCUMENTO_CONDUCTOR>"
				+ "		</DATOS_HOJA_RUTA>"
				+ "	</DATOS_ALBARANES>"
//					<DATOS_ALBARANES>
//						<NUMERO>2</NUMERO>
//						<DATOS_CLIENTE>
//							<DATOS_REGISTRO>
//								<DATOS_DOCUMENTO>
//									<PAIS_DOCUMENTO>
//										<CODIGO>724</CODIGO>
//										<DESCRIPCION>ESPANA</DESCRIPCION>
//									</PAIS_DOCUMENTO>
//									<TIPO_DOCUMENTO>1</TIPO_DOCUMENTO>
//									<DOCUMENTO>A46103834</DOCUMENTO>
//								</DATOS_DOCUMENTO>
//								<NOMBRE>MERCADONA, SA</NOMBRE>
//								<ALIAS></ALIAS>
//								<NACIONALIDAD>
//									<CODIGO>724</CODIGO>
//									<DESCRIPCION>ESPANA</DESCRIPCION>
//								</NACIONALIDAD>
//								<TELEFONO_FIJO>976696990</TELEFONO_FIJO>
//							</DATOS_REGISTRO>
//						</DATOS_CLIENTE>
//						<FECHA_EMISION>20170112</FECHA_EMISION>
//						<COMENTARIOS></COMENTARIOS>
//						<DATOS_CENTRO_TRABAJO>
//							<DESCRIPCION>PADULETA,1 </DESCRIPCION>
//							<DATOS_DIRECCION>
//								<DIRECCION>PADULETA</DIRECCION>
//								<DIRECCION2></DIRECCION2>
//								<DIRECCION3></DIRECCION3>
//								<CIUDAD>VITORIA-GASTEIZ</CIUDAD>
//								<CODIGO_POSTAL>01015</CODIGO_POSTAL>
//							</DATOS_DIRECCION>
//						</DATOS_CENTRO_TRABAJO>
//						<LINEAS_ALBARAN>
//							<DATOS_LINEA_ALBARAN>
//								<LINEA>2</LINEA>
//								<PRODUCTO>
//									<CODIGO>08MLV022</CODIGO>
//									<NOMBRE>69098 PATATA MIRALOBUENO ROJA 2KG</NOMBRE>
//									<DESCRIPCION></DESCRIPCION>
//									<NUMERO_LOTE_SERIE>69098-001</NUMERO_LOTE_SERIE>
//									<FECHA_LOTE_SERIE>20140112</FECHA_LOTE_SERIE>
//									<CODIGO_BARRAS>28424273000561</CODIGO_BARRAS>
//									<REFERENCIA_CLIENTE>08437007877137</REFERENCIA_CLIENTE>
//								</PRODUCTO>
//								<DESCRIPCION>PATATA MIRALOBUENO GRANEL 8KG. </DESCRIPCION>
//								<CANTIDAD>152.000</CANTIDAD>
//								<COMPOSICION_PRODUCTO_ELABORADO>
//									<DATOS_COMPOSICION_PRODUCTO>
//										<PRODUCTO>
//											<CODIGO>08MLV022</CODIGO>
//											<NOMBRE>69098 PATATA MIRALOBUENO ROJA 2KG</NOMBRE>
//											<DESCRIPCION></DESCRIPCION>
//											<NUMERO_LOTE_SERIE>001</NUMERO_LOTE_SERIE>
//											<FECHA_LOTE_SERIE>20140112</FECHA_LOTE_SERIE>
//											<CODIGO_BARRAS>28424273000561</CODIGO_BARRAS>
//											<REFERENCIA_CLIENTE>08437007877137</REFERENCIA_CLIENTE>
//										</PRODUCTO>
//										<CANTIDAD>100.000</CANTIDAD>
//									</DATOS_COMPOSICION_PRODUCTO>
//									<DATOS_COMPOSICION_PRODUCTO>
//										<PRODUCTO>
//											<CODIGO>08MLV022</CODIGO>
//											<NOMBRE>69098 PATATA MIRALOBUENO ROJA 2KG</NOMBRE>
//											<DESCRIPCION></DESCRIPCION>
//											<NUMERO_LOTE_SERIE>002</NUMERO_LOTE_SERIE>
//											<FECHA_LOTE_SERIE>20140112</FECHA_LOTE_SERIE>
//											<CODIGO_BARRAS>28424273000561</CODIGO_BARRAS>
//											<REFERENCIA_CLIENTE>08437007877137</REFERENCIA_CLIENTE>
//										</PRODUCTO>
//										<CANTIDAD>52.000</CANTIDAD>
//									</DATOS_COMPOSICION_PRODUCTO>
//								</COMPOSICION_PRODUCTO_ELABORADO>
//								<DATOS_ELABORACION_ORIGEN>
//									<SERIE>PV17</SERIE>
//									<NUMERO>2</NUMERO>
//								</DATOS_ELABORACION_ORIGEN>
//								<ENVASE>N</ENVASE>
//							</DATOS_LINEA_ALBARAN>
//							<DATOS_LINEA_ALBARAN>
//								<LINEA>2</LINEA>
//								<PRODUCTO>
//									<CODIGO>117699</CODIGO>
//									<NOMBRE>CAJA CARTON GRANEL UDAPA 12KG BLANCA 388*234*248</NOMBRE>
//								</PRODUCTO>
//								<DESCRIPCION></DESCRIPCION>
//								<CANTIDAD>10</CANTIDAD>
//								<ENVASE>S</ENVASE>
//							</DATOS_LINEA_ALBARAN>
//						</LINEAS_ALBARAN>
//						<DATOS_HOJA_RUTA>
//							<FECHA_EMISION>20140112</FECHA_EMISION>
//							<DATOS_AGENCIA_TRANSPORTE>
//								<DATOS_REGISTRO>
//									<DATOS_DOCUMENTO>
//										<PAIS_DOCUMENTO>
//											<CODIGO>724</CODIGO>
//											<DESCRIPCION>ESPANA</DESCRIPCION>
//										</PAIS_DOCUMENTO>
//										<TIPO_DOCUMENTO>1</TIPO_DOCUMENTO>
//										<DOCUMENTO>B01496413</DOCUMENTO>
//									</DATOS_DOCUMENTO>
//									<NOMBRE>VITOTRANS, S.L.</NOMBRE>
//								</DATOS_REGISTRO>
//							</DATOS_AGENCIA_TRANSPORTE>
//							<REFERNCIA_AGENCIA_TRANSPORTE>00000000000000000
//							</REFERNCIA_AGENCIA_TRANSPORTE>
//							<NUMERO_MATRICULA>0000XXX</NUMERO_MATRICULA>
//							<NOMBRE_CONDUCTOR>Nico Rosberg</NOMBRE_CONDUCTOR>
//							<DOCUMENTO_CONDUCTOR>00000000A</DOCUMENTO_CONDUCTOR>
//						</DATOS_HOJA_RUTA>
//					</DATOS_ALBARANES>
				+ "</ALBARANES>"
				;
		
		return xml;
	}

	public static void main(String[] args) throws Exception {
		String path = "http://";
		path += "udapa.esferalia.net";
		path += ":8080";
		path += "/aon-aio";
		path += "/ingenet/delivery";

		String user = "ingenet";
		String passwd = "1ng3n3t";
		
		String xml = getValue();
		
        StringBuilder postData = new StringBuilder();
        postData.append('&');
        postData.append(URLEncoder.encode(AbstractIngenetServlet.PARAM_USERNAME, "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(user, "UTF-8"));
        postData.append('&');
        postData.append(URLEncoder.encode(AbstractIngenetServlet.PARAM_PASSWORD, "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(passwd, "UTF-8"));
        postData.append('&');
        postData.append(URLEncoder.encode(AbstractIngenetServlet.PARAM_VALUE, "UTF-8"));
        postData.append('=');
        postData.append(URLEncoder.encode(xml, "UTF-8"));
        
        byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8.name());

        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.connect();
        conn.getOutputStream().write(postDataBytes);
        

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8.name()));
        StringBuffer sb = new StringBuffer();
        for(String in; (in = br.readLine()) != null;) {
            sb.append(in + "\n");
        }
        br.close();
	}
		
}
