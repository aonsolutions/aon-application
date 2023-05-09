package solutions.aon.seg.social;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlPage;
import org.junit.Assert;
import org.junit.Test;

import solutions.aon.seg.social.SistemaREDNaf.IdentCode;
import solutions.aon.seg.social.SistemaREDNaf.NafRequest;
import solutions.aon.seg.social.SistemaREDNaf.Nationality;
import solutions.aon.seg.social.SistemaREDNaf.Sexo;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.UnknownAuthorizedException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.IpfAlreadyExistsReachedPage;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;


public class TestSistemaREDNaf {
		
	@Test
	public void testGetNafEmptyName() throws Exception{
		NafRequest nft = new NafRequest(
				"", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);
		try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
			SistemaREDNaf.getNaf(certificateInputStream,
					"123456",
					"pkcs12",
					nft
					);		
			Assert.fail();
		}catch(UnfilledMandatory uf) {
			System.out.println(uf.getMessage());
			Assert.assertEquals("El campo 'Nombre' es obligatorio.", uf.getMessage());
		}
	}
//	
	@Test 
	public void testGetNafEmptyFirstSurname() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);
		try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
			SistemaREDNaf.getNaf(certificateInputStream,
					"123456",
					"pkcs12",
					nft
					);			
			Assert.fail();
		}catch(UnfilledMandatory uf) {
			System.out.println(uf.getMessage());
			Assert.assertEquals("El campo 'Primer Apellido' es obligatorio.", uf.getMessage());
		}
	}
	
	@Test 
	public void testGetNafEmptySecondSurname() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);

		try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
			SistemaREDNaf.getNaf(certificateInputStream,
					"123456",
					"pkcs12",
					nft
					);			
			Assert.fail();
		}catch(UnfilledMandatory uf) {
			System.out.println(uf.getMessage());
			Assert.assertEquals("El campo 'Segundo Apellido' es obligatorio.", uf.getMessage());
		}
	}
	
	@Test 
	public void testGetNafEmptyNationality() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.EMPTY,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);

		try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
			SistemaREDNaf.getNaf(certificateInputStream,
					"123456",
					"pkcs12",
					nft
					);			
			Assert.fail();
		}catch(UnfilledMandatory uf) {
			System.out.println(uf.getMessage());
			Assert.assertEquals("El campo 'Nacionalidad' es obligatorio.", uf.getMessage());
		}
	}
	
	@Test 
	public void testGetNafEmptyFatherName() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);

		try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
			SistemaREDNaf.getNaf(certificateInputStream,
					"123456",
					"pkcs12",
					nft
					);			
			Assert.fail();
		}catch(UnfilledMandatory uf) {
			System.out.println(uf.getMessage());
			Assert.assertEquals("El campo 'N.Padre' es obligatorio.", uf.getMessage());
		}
	}
	
	@Test 
	public void testGetNafEmptyMotherName() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);

		try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
			SistemaREDNaf.getNaf(certificateInputStream,
					"123456",
					"pkcs12",
					nft
					);			
			Assert.fail();
		}catch(UnfilledMandatory uf) {
			System.out.println(uf.getMessage());
			Assert.assertEquals("El campo 'N.Madre' es obligatorio.", uf.getMessage());
		}
	}
	
	
	@Test 
	public void testGetNafEmptySexOption() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.EMPTY,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);

		try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
			SistemaREDNaf.getNaf(certificateInputStream,
					"123456",
					"pkcs12",
					nft
					);			
			Assert.fail();
		}catch(UnfilledMandatory uf) {
			System.out.println(uf.getMessage());
			Assert.assertEquals("El campo 'Sexo' es obligatorio.", uf.getMessage());
		}
	}
	
	
	@Test 
	public void testGetNafEmptyBirthDate() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("00/00/0000"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);

		try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
			SistemaREDNaf.getNaf(certificateInputStream,
					"123456",
					"pkcs12",
					nft
					);			
			Assert.fail();
		}catch(UnfilledMandatory uf) {
			System.out.println(uf.getMessage());
			Assert.assertEquals("El campo 'Fecha Nacimiento' es obligatorio.", uf.getMessage());
		}
	}
	
	
	@Test 
	public void testGetNafEmptyIdentCode() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.EMPTY,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);

		try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
			SistemaREDNaf.getNaf(certificateInputStream,
					"123456",
					"pkcs12",
					nft
					);			
			Assert.fail();
		}catch(UnfilledMandatory uf) {
			System.out.println(uf.getMessage());
			Assert.assertEquals("El campo 'Ident. Persona Física' es obligatorio.", uf.getMessage());
		}
	}
	
	
	@Test 
	public void testGetNafEmptyIdentNumber() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				""//NUMERO DE DOCUMENTO
				);

		try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
			SistemaREDNaf.getNaf(certificateInputStream,
					"123456",
					"pkcs12",
					nft
					);			
			Assert.fail();
		}catch(UnfilledMandatory uf) {
			System.out.println(uf.getMessage());
			Assert.assertEquals("El campo 'Ident. Persona Física' es obligatorio.", uf.getMessage());
		}
	}
	
		//OBLIGATORY DATA IN SistemaREDNaf
	
	
	@Test
	public void testGetNafWrongPhoneNumber() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);
				nft.mobileNumber = "aaa";
				
				try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
					SistemaREDNaf.getNaf(certificateInputStream,
							"123456",
							"pkcs12",
							nft
							);			
					Assert.fail();
				}catch(InvalidDataException uf) {
					System.out.println(uf.getMessage());
					Assert.assertEquals("El numero de telefono movil no es correcto, intentelo otra vez", uf.getMessage());
				}
	}
	
	@Test
	public void testGetNafWrongStreetType() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);
				nft.streetType = "aaa";
				
				try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
					SistemaREDNaf.getNaf(certificateInputStream,
							"123456",
							"pkcs12",
							nft
							);			
					Assert.fail();
				}catch(InvalidDataException uf) {
					System.out.println(uf.getMessage());
					Assert.assertEquals("El tipo de via introducido no es correcto", uf.getMessage());
				}
	}
	
	
	
	@Test(expected = UnknownAuthorizedException.class)
	public void testGetNafWrongAuthorizedElement() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);

				final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12");
				try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, "123456", "pkcs12")){
			
				HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=APR01&E=I&AP=AFIR");
//			System.out.println(htmlPage.asXml());

				DomElement authorizedElement = htmlPage.getElementById("Sub0501210055_1_" );
				if (authorizedElement == null) {
					throw new UnknownAuthorizedException("La autorizacion seleccionada no es correcta, por favor intentelo de nuevo"); 
				}
				for (int i = 1; authorizedElement != null ; i++) {
			    String authorizedText = authorizedElement.getTextContent();
			    if (authorizedText != null && authorizedText.startsWith(nft.authorized)) {
				htmlPage = authorizedElement.dblClick();
				break;
			    }
			    authorizedElement = htmlPage.getElementById("Sub0501210055_1_" + i);
			    if (authorizedElement == null) {
				throw new UnknownAuthorizedException("La autorizacion seleccionada no es correcta, por favor intentelo de nuevo"); 
			    }
			}
		}
		
	}
	
//	(expected = InvalidCertificateException.class)
	@Test
	public void testGetNafWrongCertificateData() throws Exception{
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);

		try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
			SistemaREDNaf.getNaf(certificateInputStream,
					"123456",
					"",
					nft
					);	
			Assert.fail();
		}catch(InvalidCertificateException e) {
			fail("Datos de certificados incorrectos");
		}
		

	}
	
	
	@Test
	public void testGetNafIpfAlreadyExists() throws Exception {
		NafRequest nft = new NafRequest(
				"JUAN MANUEL", //NOMBRE
				"ORTEGA", //PRIMER APELLIDO
				"ÁLVAREZ",// SEGUNDO APELLIDO
				Nationality.ESPAÑA,//NACIONALIDAD
				"JUAN MANUEL",// NOMBRE PADRE
				"MILAGROS",//NOMBRE MADRE
				Sexo.VARON,//SEXO
				new SimpleDateFormat("dd/mm/yyyy").parse("06/02/1997"),//FECHA DE NACIMIENTO
				IdentCode.DNI,// IDENTIFICADOR DOCUMENTO
				"45339825V"//NUMERO DE DOCUMENTO
				);

		try(final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("AyudaTFNMT.p12")){
			SistemaREDNaf.getNaf(certificateInputStream,
					"123456",
					"pkcs12",
					nft
					);	
			Assert.fail();
		}catch(IpfAlreadyExistsReachedPage e) {
			System.out.println(e.getMessage());
		}
	}
	
//	catch(InvalidCertificateException uf) {
//		System.out.println(uf.getMessage() + "Fallo certificacion");
//		Assert.assertEquals("Fallo en la autenticacion del certificado", uf.getMessage());
//	}
	
}
