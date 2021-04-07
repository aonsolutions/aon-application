package com.esferalia.aon.gwt.payroll.util;

public class PdfPrintResponses {

	
	public static String pdfPrintError(String msg) {
		return 
		"<html>" + 
			"<head>" + 
				"<link rel='stylesheet' href='http://akrck02.com/bubble/dist/v1.0/master.css' >" + 
			"</head>" + 
			"<script>" +
				"window.onload = () => document.body.style.opacity = '1';" + 
			"</script>" +
			"<body class='box-center box-column' style='height: 100vh; transition 1.5s; opacity: 0;'>" + 
				"<img src='https://www.flaticon.com/svg/vstatic/svg/337/337946.svg?token=exp=1617709473~hmac=ae675c095c1cd91da17b1a08d6d1f1bf' style='max-width:200px'>" +
				"<h1>" + msg + "</h1>" +
			"</body>" + 
		"</html>";
	}
	
	
}
