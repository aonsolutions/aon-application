package com.esferalia.aon.gwt.payroll.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.code.aon.ui.common.session.MockHttpServletResponse;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.MultipartConfig;


@MultipartConfig
public class DNIServletTestImpl extends DNIServlet{

	private static final long serialVersionUID = 6424878288942526801L;

	@Test
	public void fillJsonNullTest(){

	MockHttpServletResponse response = new MockHttpServletResponse();
	DNIServlet ds = new DNIServlet();
	AonCoreException e =
	assertThrows(AonCoreException.class,() -> ds.fillJson(response, null)); 
	assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	System.out.println(e.getMessage());

 	}
	
	@Test
	public void fillJsonDniNull() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		DNIServlet ds = new DNIServlet();
			String [] lineas ={"REINO DE ESPANA",
					"ES",
					"DOCUMENTO NACIONAL DEIDENTIDAD",
					"DNI",
					"",
					"APELLIDOS",
					"ORTEGA",
					"ALVAREZ",
					"NONBRE",
					"JUAN MANUEL",
					"SEXO",
					"NACIONALIDAD",
					"NACIMIENTO",
					"M",
					"ESP",
					"06 02 1997",
					"EMISIÓN",
					"VALIDEZ",
					"27 07 2022 27 07 2027",
					"NUM SOPORTE",
					"270722",
					"CCX185073",
					"709248",
					"NATIONAL IDENTITY CARD / DOCUMENTO NACIONAL IDENTIDAD NATIONAL IDENTITY CARD / DOCUMENTO NACIONAL IDENTIDAD"};

			AonCoreException e = assertThrows(AonCoreException.class, () -> ds.fillJson(response, lineas));
			assertEquals(AonError.INVALID_DNI_FORMAT.getMessage(), e.getMessage());
			System.out.println(e.getMessage());
	}

	@Test
	public void fillJsonNationalityNull() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		DNIServlet ds = new DNIServlet();
			String [] lineas ={"REINO DE ESPANA",
					"ES",
					"DOCUMENTO NACIONAL DEIDENTIDAD",
					"DNI",
					"45339825V",
					"APELLIDOS",
					"ORTEGA",
					"ALVAREZ",
					"NONBRE",
					"JUAN MANUEL",
					"SEXO",
					"NACIONALIDAD",
					"NACIMIENTO",
					"M",
					"",
					"06 02 1997",
					"EMISIÓN",
					"VALIDEZ",
					"27 07 2022 27 07 2027",
					"NUM SOPORTE",
					"270722",
					"CCX185073",
					"709248",
					"NATIONAL IDENTITY CARD / DOCUMENTO NACIONAL IDENTIDAD NATIONAL IDENTITY CARD / DOCUMENTO NACIONAL IDENTIDAD"};

			AonCoreException e = assertThrows(AonCoreException.class, () -> ds.fillJson(response, lineas));
			assertEquals(AonError.INVALID_NATIONALITY_FORMAT.getMessage(), e.getMessage());
			System.out.println(e.getMessage());
	}
	
	@Test
	public void fillJsonNameNull() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		DNIServlet ds = new DNIServlet();
			String [] lineas ={"REINO DE ESPANA",
					"ES",
					"DOCUMENTO NACIONAL DEIDENTIDAD",
					"DNI",
					"45339825V",
					"APELLIDOS",
					"ORTEGA",
					"ALVAREZ",
					"NONBRE",
					"",
					"SEXO",
					"NACIONALIDAD",
					"NACIMIENTO",
					"M",
					"ESP",
					"06 02 1997",
					"EMISIÓN",
					"VALIDEZ",
					"27 07 2022 27 07 2027",
					"NUM SOPORTE",
					"270722",
					"CCX185073",
					"709248",
					"NATIONAL IDENTITY CARD / DOCUMENTO NACIONAL IDENTIDAD NATIONAL IDENTITY CARD / DOCUMENTO NACIONAL IDENTIDAD"};

			AonCoreException e = assertThrows(AonCoreException.class, () -> ds.fillJson(response, lineas));
			assertEquals(AonError.INVALID_NAME_FORMAT.getMessage(), e.getMessage());
			System.out.println(e.getMessage());
	}
	
	@Test
	public void fillJsonFirstSurnameNull() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		DNIServlet ds = new DNIServlet();
			String [] lineas ={"REINO DE ESPANA",
					"ES",
					"DOCUMENTO NACIONAL DEIDENTIDAD",
					"DNI",
					"45339825V",
					"APELLIDOS",
					"",
					"ALVAREZ",
					"NONBRE",
					"JUAN MANUEL",
					"SEXO",
					"NACIONALIDAD",
					"NACIMIENTO",
					"M",
					"ESP",
					"06 02 1997",
					"EMISIÓN",
					"VALIDEZ",
					"27 07 2022 27 07 2027",
					"NUM SOPORTE",
					"270722",
					"CCX185073",
					"709248",
					"NATIONAL IDENTITY CARD / DOCUMENTO NACIONAL IDENTIDAD NATIONAL IDENTITY CARD / DOCUMENTO NACIONAL IDENTIDAD"};

			AonCoreException e = assertThrows(AonCoreException.class, () -> ds.fillJson(response, lineas));
			assertEquals(AonError.INVALID_FIRST_SURNAME.getMessage(), e.getMessage());
			System.out.println(e.getMessage());
	}
	
	@Test
	public void fillJsonSecondSurnameNull() {
		MockHttpServletResponse response = new MockHttpServletResponse();
		DNIServlet ds = new DNIServlet();
			String [] lineas ={"REINO DE ESPANA",
					"ES",
					"DOCUMENTO NACIONAL DEIDENTIDAD",
					"DNI",
					"45339825V",
					"APELLIDOS",
					"ORTEGA",
					"",
					"NONBRE",
					"JUAN MANUEL",
					"SEXO",
					"NACIONALIDAD",
					"NACIMIENTO",
					"M",
					"ESP",
					"06 02 1997",
					"EMISIÓN",
					"VALIDEZ",
					"27 07 2022 27 07 2027",
					"NUM SOPORTE",
					"270722",
					"CCX185073",
					"709248",
					"NATIONAL IDENTITY CARD / DOCUMENTO NACIONAL IDENTIDAD NATIONAL IDENTITY CARD / DOCUMENTO NACIONAL IDENTIDAD"};

			AonCoreException e = assertThrows(AonCoreException.class, () -> ds.fillJson(response, lineas));
			assertEquals(AonError.INVALID_SECOND_SURNAME.getMessage(), e.getMessage());
			System.out.println(e.getMessage());
	}
}
