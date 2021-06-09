package com.esferalia.aon.gwt.payroll.server.pdf;

import static com.esferalia.aon.gwt.payroll.tools.Console.Separator.ARROW_REVERSE;
import static com.esferalia.aon.gwt.payroll.tools.Console.Separator.EQUAL;
import static com.esferalia.aon.gwt.payroll.tools.Console.Status.GET;
import static com.esferalia.aon.gwt.payroll.tools.Console.Status.RUN;
import static com.esferalia.aon.gwt.payroll.tools.TestTools.assertWithLog;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.formatDate;
import static com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats.toLatinNumber;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.croppedString;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.getContent;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit.getLines;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.StringToolkit.joinCharacterList;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.AT_EP_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.AT_EP_PERCENT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.CGC_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.CGC_BASE;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.CGC_PERCENT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.CGP_BASE;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.EMPLOYEE_CATEGORY;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.EMPLOYEE_DOCUMENT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.EMPLOYEE_GROUP;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.EMPLOYEE_NAME;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.EMPLOYEE_SENIORITY;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.EMPLOYEE_SS_NUMBER;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.ENTERPRISE_ADDRESS;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.ENTERPRISE_CCC;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.ENTERPRISE_DOCUMENT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.ENTERPRISE_NAME;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.EXTRA_PRORRATION;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.EXTR_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.EXTR_BASE;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.EXTR_PERCENT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.FOGASA_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.FOGASA_PERCENT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.FP_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.FP_PERCENT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.IRPF_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.NEXTR_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.NEXTR_BASE;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.NEXTR_PERCENT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.REMUNERATION;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.TOTAL_COSTS;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.UNEMPLOYMENT_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.UNEMPLOYMENT_PERCENT;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.tools.Console;
import com.esferalia.aon.gwt.payroll.tools.Console.Status;
import com.esferalia.aon.gwt.payroll.util.DraftPayrollBuilder;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFonts;
import com.esferalia.aon.in.payroll.pdf.api.setting.PdfFormats;
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

public class DefaultPayrollIntegrationTest {

	
	Console console;
	
	@Rule
	public TestName testName = new TestName();
	
	@Before
	public void prepare() {
		console = new Console();
		console.start(testName.getMethodName());
	}
	
	@Test
	public void dataIntegrationTest() {
		
		SalaryDraft draft = new SalaryDraft();
		
		/** Filling general data **/
		draft.setStartDate(new Date());
		draft.setEndDate(new Date());
		draft.setTimeUnits(30);
		draft.setIrpfBase(800d);
		draft.setInkindIrpfBase(345d);
		draft.setProrationBase(500d);
		draft.setRemuneration(1455.9);
		draft.setCgcBase(1350.55);
		draft.setCgpBase(1234.1);
		draft.sethExtraBase(279.3);
		draft.setNonHExtraBase(129.3);
		draft.setTotalEnterprise(767.23);
		
		/** Filling employee data **/
		draft.setEmployeeName("ANTONIO DE LA VEGA AMATISTO RODIGUEZ PEREZ LÓPEZ");
		draft.setEmployeeAddress("Avenida de los cerezos 25 Ático A, escalera B1, Alcántara, Toledo");
		draft.setEmployeeDocument("50392654R");
		draft.setEmployeeSeniorityDate(new Date());
		draft.setEmployeeQuoteGroup("01");
		draft.setEmployeeAgreementCategory("JUNIOR SOFTWARE ENGINEER / ANALISTA / JEFE DE COORDINACION SEÑOR DEL COSO Y TAL");
		draft.setEmployeeSS("182624935460231");
		
		/** Filling etreprise data **/
		draft.setEnterpriseName("MICROSOFT AI LABS LTD WESTERN-EUROPE.S.L.");
		draft.setEnterpriseCCC("16485960263702345");
		draft.setEnterpriseAddress("Plaza de los alfajores azules, 22 Bajo A, Alameda de riotes, Extremadura");
		draft.setEnterpriseDocument("192735471938G");
		
		/** Payments **/
		Payment horasExtrasEstruct = new Payment();
		horasExtrasEstruct.setType(Type.CRA_0002);
		horasExtrasEstruct.setDescription("Horas extras estructurales");
		horasExtrasEstruct.setAmount(125.55);
		horasExtrasEstruct.setQuote(125.55);
		
		Payment horasExtrasNoEstruct = new Payment();	
		horasExtrasNoEstruct.setType(Type.CRA_0003);
		horasExtrasNoEstruct.setDescription("Horas extras no estructurales");
		horasExtrasNoEstruct.setAmount(99.24);
		horasExtrasNoEstruct.setQuote(99.24);
		
		draft.addPayment(horasExtrasEstruct);
		draft.addPayment(horasExtrasNoEstruct);
		
		/** Deduction **/
		Deduction cgcDeduction = new Deduction();
		cgcDeduction.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.COMMON_CONTINGENCY);
		cgcDeduction.setAmount(64.20);
		
		Deduction unemploymentDeduction = new Deduction();
		unemploymentDeduction.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.UNEMPLOYMENT);
		unemploymentDeduction.setAmount(17.53);
		
		draft.addDeduction(cgcDeduction);
		draft.addDeduction(unemploymentDeduction);
		
		/** Costs **/
		Deduction cgcCost = new Deduction();
		cgcCost.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.COMMON_CONTINGENCY);
		cgcCost.setAmount(24.20);
		
		Deduction imsCost = new Deduction();
		imsCost.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.OTHER);
		imsCost.setName("IMS_E");
		imsCost.setAmount(12.99);

		Deduction itCost = new Deduction();
		itCost.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.OTHER);
		itCost.setName("IT_E");
		itCost.setAmount(12.99);

		Deduction unemploymentCost = new Deduction();
		unemploymentCost.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.UNEMPLOYMENT);
		unemploymentCost.setName("DESMPL_E");
		unemploymentCost.setAmount(7.12);
		
		Deduction fpCost = new Deduction();
		fpCost.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.JOB_TRAINING);
		fpCost.setName("FP_E");
		fpCost.setAmount(6.8);
		
		Deduction fogasaCost = new Deduction();
		fogasaCost.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.FOGASA);
		fogasaCost.setName("FOGASA");
		fogasaCost.setAmount(3.8);
		
		Deduction extrCost = new Deduction();
		extrCost.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.STRUCTURAL_OVERTIME);
		extrCost.setName("EXTR_E");
		extrCost.setAmount(31.49);
		
		Deduction nextrCost = new Deduction();
		nextrCost.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.NON_STRUCTURAL_OVERTIME);
		nextrCost.setName("NEXTR_E");
		nextrCost.setAmount(47.49);
		
		draft.addCost(cgcCost);
		draft.addCost(imsCost);
		draft.addCost(itCost);
		draft.addCost(unemploymentCost);
		draft.addCost(fpCost);
		draft.addCost(fogasaCost);
		draft.addCost(extrCost);
		draft.addCost(nextrCost);
		
		Double varDesmplPercent = 5d;
		Double varCgcPercent = 12.41;
		
		Double varCgcE = 12.41;
		Double varCgpE = 11.22;
		
		Double varTarifaIms = 17d;
		Double varTarifaIt =  23.71d;
		
		Double varPorcentajeDesmplE = 4.96;
		Double varPorcentajeFpE = 4.56;
		Double varPorcentajeFogasa = 2.34;
		Double varPorcentajeExtrE = 16.65;
		Double varPorcentajeNextrE = 8.51;
		Double varPorcentajeCgcE = 17d;
		
		/** Context data **/
		draft.addVariable("PORCENTAJE_DESMPL", varDesmplPercent, new Date(), new Date());
		draft.addVariable("PORCENTAJE_CGC", varCgcPercent, new Date(), new Date());
		
		/** Cost context data **/
		draft.addVariable("BASE_CGC_E", varCgcE, new Date(), new Date());
		draft.addVariable("BASE_CGP_E", varCgpE, new Date(), new Date());
		
		draft.addVariable("TARIFA_IMS", varTarifaIms, new Date(), new Date());
		draft.addVariable("TARIFA_IT", varTarifaIt, new Date(), new Date());
		
		draft.addVariable("PORCENTAJE_DESMPL_E", varPorcentajeDesmplE, new Date(), new Date());
		draft.addVariable("PORCENTAJE_FP_E", varPorcentajeFpE, new Date(), new Date());
		draft.addVariable("PORCENTAJE_FOGASA", varPorcentajeFogasa, new Date(), new Date());
		draft.addVariable("PORCENTAJE_CGC_E", varPorcentajeCgcE, new Date(), new Date());
		draft.addVariable("PORCENTAJE_EXTR_E", varPorcentajeExtrE, new Date(), new Date());
		draft.addVariable("PORCENTAJE_NEXTR_E", varPorcentajeNextrE, new Date(), new Date());
	
		ByteOutputStream pdf = new ByteOutputStream();
		DraftPayrollBuilder.printPayrollDraft(pdf, "", draft);		
		
		try {
			PDDocument document = PDDocument.load(pdf.getBytes());
			PDPageTree pages = document.getPages();
			
			/** Employee data **/
			String empName = "";
			String empNSS = "";
			String empDocument = "";
			String empCategory = "";
			String empGroup = "";
			String empSeniority = "";
			
			/** Enterprise data **/
			String entName = "";
			String entCCC = "";
			String entDocument = "";
			String entAddress = "";
				
			/** General data **/
			int totalDays = -1;
			
			
			/** Costs **/
			String remuneration = "";
			String prorration = "";
			
			String cgcBase = "";
			String cgcPercent = "";
			String cgcAmount = "";
			
			String cgpBase = "";
			
			String atEpPercent = "";
			String atEpAmount = "";
			
			String unemploymentPercent = "";
			String unemploymentAmount = "";
			
			String fpPercent = "";
			String fpAmount = "";
			
			String fogasaPercent = "";
			String fogasaAmount = "";
			
			String extrBase = "";
			String extrPercent = "";
			String extrAmount = "";
			
			String nextrBase = "";
			String nextrPercent = "";
			String nextrAmount = "";
			
			String irpfTotal = "";
			String irpfAmount = "";
			String inkindIrpfAmount = "";
			
			String totalCosts = "";
			
			/** Getting data markers **/
			Optional<PDMarkedContent> empNameMark = getContent(document, EMPLOYEE_NAME);
			Optional<PDMarkedContent> empDocumentMark = getContent(document, EMPLOYEE_DOCUMENT);
			Optional<PDMarkedContent> empGroupMark = getContent(document, EMPLOYEE_GROUP);
			Optional<PDMarkedContent> empCategoryMark = getContent(document, EMPLOYEE_CATEGORY);
			Optional<PDMarkedContent> empSsMark = getContent(document, EMPLOYEE_SS_NUMBER);
			Optional<PDMarkedContent> empSeniorityMark = getContent(document, EMPLOYEE_SENIORITY);				
			
			if(empNameMark.isPresent())
				empName = joinCharacterList(empNameMark.get().getContents());		
			
			if(empDocumentMark.isPresent())
				empDocument = joinCharacterList(empDocumentMark.get().getContents());

			if(empGroupMark.isPresent())
				empGroup = joinCharacterList(empGroupMark.get().getContents());

			if(empCategoryMark.isPresent())
				empCategory = joinCharacterList(empCategoryMark.get().getContents());	
			
			if(empSsMark.isPresent())
				empNSS = joinCharacterList(empSsMark.get().getContents());	
			
			if(empSeniorityMark.isPresent())
				empSeniority = joinCharacterList(empSeniorityMark.get().getContents());	
						
			empDocument = empDocument.replace("NIF:", "").trim();
			empGroup = empGroup.replace("G.Cotizaci\u00F3n:", "").trim();
			empCategory = empCategory.replace("G.Profesional:", "").trim();
			empSeniority = empSeniority.replace("Fecha de antig\u00FCedad:", "").trim();
			empNSS = empNSS.replace("NSS:", "").trim();
			
			console.jump();
			console.log(Status.RUN,"EMPLOYEE DATA");
			console.line();
			
			console.log(GET, "NAME", empName, ARROW_REVERSE);
			console.log(GET, "DOCUMENT", empDocument, ARROW_REVERSE);
			console.log(GET, "GROUP", empGroup, ARROW_REVERSE);
			console.log(GET, "CATEGORY", empCategory, ARROW_REVERSE);
			console.log(GET, "SENIORITY", empSeniority, ARROW_REVERSE);
			console.log(GET, "SS", empNSS, ARROW_REVERSE);						
			
			Optional<PDMarkedContent> entNameMark = getContent(document, ENTERPRISE_NAME);
			Optional<PDMarkedContent> entCccMark = getContent(document, ENTERPRISE_CCC);
			Optional<PDMarkedContent> entDocumentMark = getContent(document, ENTERPRISE_DOCUMENT);
			Optional<PDMarkedContent> entAddressMark = getContent(document, ENTERPRISE_ADDRESS);
			
			if(entNameMark.isPresent())
				entName = joinCharacterList(entNameMark.get().getContents());	
			
			if(entDocumentMark.isPresent())
				entDocument = joinCharacterList(entDocumentMark.get().getContents());	
			
			if(entCccMark.isPresent())
				entCCC = joinCharacterList(entCccMark.get().getContents());
			
			if(entAddressMark.isPresent())
				entAddress = joinCharacterList(entAddressMark.get().getContents());
			
			entDocument = entDocument.replace("NIF:", "").trim();	
			entCCC = entCCC.replace("CCC:", "").trim();
			
			console.jump();
			console.log(RUN,"ENTERPRISE DATA");
			console.line();
			
			console.log(GET, "NAME", entName, ARROW_REVERSE);
			console.log(GET, "DOCUMENT", entDocument, ARROW_REVERSE);
			console.log(GET, "CCC", entCCC, ARROW_REVERSE);
			
			Optional<PDMarkedContent> remunerationMark = getContent(document, REMUNERATION);			
			Optional<PDMarkedContent> prorrationMark = getContent(document, EXTRA_PRORRATION);		
			
			Optional<PDMarkedContent> cgcBaseMark = getContent(document, CGC_BASE);			
			Optional<PDMarkedContent> cgcPercentMark = getContent(document, CGC_PERCENT);			
			Optional<PDMarkedContent> cgcAmountMark = getContent(document, CGC_AMOUNT);	
			
			Optional<PDMarkedContent> cgpBaseMark = getContent(document, CGP_BASE);
			
			Optional<PDMarkedContent> atEpPercentMark = getContent(document, AT_EP_PERCENT);			
			Optional<PDMarkedContent> atEpAmountMark = getContent(document, AT_EP_AMOUNT);			

			Optional<PDMarkedContent> unemploymentPercentMark = getContent(document, UNEMPLOYMENT_PERCENT);			
			Optional<PDMarkedContent> unemploymentAmountMark = getContent(document, UNEMPLOYMENT_AMOUNT);			

			Optional<PDMarkedContent> fpPercentMark = getContent(document, FP_PERCENT);
			Optional<PDMarkedContent> fpAmountMark = getContent(document, FP_AMOUNT);			
			
			Optional<PDMarkedContent> fogasaPercentMark = getContent(document, FOGASA_PERCENT);		
			Optional<PDMarkedContent> fogasaAmountMark = getContent(document, FOGASA_AMOUNT);
			
			Optional<PDMarkedContent> extrBaseMark = getContent(document, EXTR_BASE);		
			Optional<PDMarkedContent> extrPercentMark = getContent(document, EXTR_PERCENT);		
			Optional<PDMarkedContent> extrAmountMark = getContent(document, EXTR_AMOUNT);		
			
			Optional<PDMarkedContent> nextrBaseMark = getContent(document, NEXTR_BASE);		
			Optional<PDMarkedContent> nextrPercentMark = getContent(document, NEXTR_PERCENT);		
			Optional<PDMarkedContent> nextrAmountMark = getContent(document, NEXTR_AMOUNT);		
			
			Optional<PDMarkedContent> irpfAmountMark = getContent(document, IRPF_AMOUNT);		
			Optional<PDMarkedContent> totalCostsMark = getContent(document, TOTAL_COSTS);		
			
			if(remunerationMark.isPresent())
				remuneration = joinCharacterList(remunerationMark.get().getContents());	
			
			if(prorrationMark.isPresent())
				prorration = joinCharacterList(prorrationMark.get().getContents());	
			
			if(cgcBaseMark.isPresent())
				cgcBase = joinCharacterList(cgcBaseMark.get().getContents());
			
			if(cgcPercentMark.isPresent())
				cgcPercent = joinCharacterList(cgcPercentMark.get().getContents());
			
			if(cgcAmountMark.isPresent())
				cgcAmount = joinCharacterList(cgcAmountMark.get().getContents());
			
			if(cgpBaseMark.isPresent())
				cgpBase = joinCharacterList(cgpBaseMark.get().getContents());
			
			if(atEpPercentMark.isPresent())
				atEpPercent = joinCharacterList(atEpPercentMark.get().getContents());
			
			if(atEpAmountMark.isPresent())
				atEpAmount = joinCharacterList(atEpAmountMark.get().getContents());
			
			if(unemploymentPercentMark.isPresent())
				unemploymentPercent = joinCharacterList(unemploymentPercentMark.get().getContents());
			
			if(unemploymentAmountMark.isPresent())
				unemploymentAmount = joinCharacterList(unemploymentAmountMark.get().getContents());
			
			if(fpPercentMark.isPresent())
				fpPercent = joinCharacterList(fpPercentMark.get().getContents());
			
			if(fpAmountMark.isPresent())
				fpAmount = joinCharacterList(fpAmountMark.get().getContents());
			
			if(fogasaPercentMark.isPresent())
				fogasaPercent = joinCharacterList(fogasaPercentMark.get().getContents());
			
			if(fogasaAmountMark.isPresent())
				fogasaAmount = joinCharacterList(fogasaAmountMark.get().getContents());
			
			if(extrBaseMark.isPresent())
				extrBase = joinCharacterList(extrBaseMark.get().getContents());
			
			if(extrPercentMark.isPresent())
				extrPercent = joinCharacterList(extrPercentMark.get().getContents());
			
			if(extrAmountMark.isPresent())
				extrAmount = joinCharacterList(extrAmountMark.get().getContents());
			
			if(nextrBaseMark.isPresent())
				nextrBase = joinCharacterList(nextrBaseMark.get().getContents());
			
			if(nextrPercentMark.isPresent())
				nextrPercent = joinCharacterList(nextrPercentMark.get().getContents());
			
			if(nextrAmountMark.isPresent())
				nextrAmount = joinCharacterList(nextrAmountMark.get().getContents());
			
			if(irpfAmountMark.isPresent())
				irpfTotal = joinCharacterList(irpfAmountMark.get().getContents());
			
			if(totalCostsMark.isPresent())
				totalCosts = joinCharacterList(totalCostsMark.get().getContents());
			
			remuneration = removeSpecialChars(remuneration);			
			prorration = removeSpecialChars(prorration);			
			
			cgcBase = removeSpecialChars(cgcBase);			
			cgcPercent = removeSpecialChars(cgcPercent);
			cgcAmount = removeSpecialChars(cgcAmount);			
			
			cgpBase = removeSpecialChars(cgpBase);
			
			atEpPercent = removeSpecialChars(atEpPercent);
			atEpAmount = removeSpecialChars(atEpAmount);
			
			unemploymentAmount = removeSpecialChars(unemploymentAmount);			
			unemploymentPercent = removeSpecialChars(unemploymentPercent);			
			
			fpPercent = removeSpecialChars(fpPercent);
			fpAmount = removeSpecialChars(fpAmount);
			
			fogasaPercent = removeSpecialChars(fogasaPercent);
			fogasaAmount = removeSpecialChars(fogasaAmount);
			
			extrBase = removeSpecialChars(extrBase);
			extrPercent = removeSpecialChars(extrPercent);
			extrAmount = removeSpecialChars(extrAmount);
			
			nextrBase = removeSpecialChars(nextrBase);
			nextrPercent = removeSpecialChars(nextrPercent);
			nextrAmount = removeSpecialChars(nextrAmount);
			
			irpfTotal = removeSpecialChars(irpfTotal);
			totalCosts = removeSpecialChars(totalCosts);
			
			console.jump();
			console.log(RUN,"COSTS");
			console.line();
			
			console.log(GET, "REMUNERATION", remuneration, ARROW_REVERSE);
			console.log(GET, "PRORRATION", prorration, ARROW_REVERSE);
			
			console.log(GET, "CGC BASE", cgcBase, ARROW_REVERSE);
			console.log(GET, "CGC PERCENT", cgcPercent, ARROW_REVERSE);
			console.log(GET, "CGC AMOUNT", cgcAmount, ARROW_REVERSE);
			
			console.log(GET, "CGP BASE", cgpBase, ARROW_REVERSE);

			console.log(GET, "AT/EP AMOUNT", atEpAmount, ARROW_REVERSE);
			console.log(GET, "AT/EP PERCENT", atEpPercent, ARROW_REVERSE);
			
			console.log(GET, "UNEMPLOYMENT AMOUNT", unemploymentAmount, ARROW_REVERSE);
			console.log(GET, "UNEMPLOYMENT PERCENT", unemploymentPercent, ARROW_REVERSE);
			
			console.log(GET, "FP PERCENT", fpPercent, ARROW_REVERSE);
			console.log(GET, "FP AMOUNT", fpAmount, ARROW_REVERSE);

			console.log(GET, "FOGASA PERCENT", fogasaPercent, ARROW_REVERSE);
			console.log(GET, "FOGASA AMOUNT", fogasaAmount, ARROW_REVERSE);
			
			console.log(GET, "EXTR BASE", extrBase, ARROW_REVERSE);
			console.log(GET, "EXTR PERCENT", extrPercent, ARROW_REVERSE);
			console.log(GET, "EXTR AMOUNT", extrAmount, ARROW_REVERSE);
			
			console.log(GET, "NEXTR BASE", nextrBase, ARROW_REVERSE);
			console.log(GET, "NEXTR PERCENT", nextrPercent, ARROW_REVERSE);
			console.log(GET, "NEXTR AMOUNT", nextrAmount, ARROW_REVERSE);
			
			console.log(GET, "IRPF TOTAL", irpfTotal, ARROW_REVERSE);
			console.log(GET, "TOTAL COSTS", totalCosts, ARROW_REVERSE);
			
			/** [DEBUG] PRINTING PDF TO HAVE VISUAL APPROACH **/			
				FileOutputStream fos = new FileOutputStream("PDF_TRYING.pdf");
				fos.write(pdf.getBytes());
				fos.close();
			/** ############################################ **/
			
				
			console.jump();
			console.start("COMPARING DATA");
			
			/** Assert employee data **/
			assertData("Employee name", empName, croppedString(draft.getEmployeeName(), 255, HELVETICA, 9f));
			assertData("Employee document", empDocument, draft.getEmployeeDocument());
			assertData("Employee category", 
					empCategory, 
					croppedString("G.Profesional: " + draft.getEmployeeAgreementCategory(), 255, HELVETICA, 9f)
					.replace("G.Profesional: ", "")
			);
			assertData("Employee group", empGroup, draft.getEmployeeQuoteGroup());
			assertData("Employee SS", empNSS, draft.getEmployeeSS());
			assertData("Employee Seniority", empSeniority, formatDate(draft.getEmployeeSeniorityDate(),"dd/MM/yyyy").orElse(""));

			
			/** Assert enterprise data **/
			assertData("Enterprise name", entName, draft.getEnterpriseName());
			assertData("Enterprise CCC", entCCC, draft.getEnterpriseCCC());
			assertData("Enterprise Address", entAddress.trim(), getLines(draft.getEnterpriseAddress(), 390, HELVETICA, 15).get(0).trim());
			assertData("Enterprise Document", entDocument, draft.getEnterpriseDocument());
			
			/** Footer data **/
			assertData("REMUNERATION", remuneration.trim() , toLatinNumber(draft.getRemuneration()).trim());
			assertData("PRORRATION", prorration.trim(), toLatinNumber(draft.getProrationBase()));
			
			assertData("GCC BASE", cgcBase.trim(), toLatinNumber(draft.getCgcBase()));
			assertData("GCC PERCENT", cgcPercent.trim(), toLatinNumber(varPorcentajeCgcE));
			assertData("GCC AMOUNT", cgcAmount.trim(), toLatinNumber(cgcCost.getAmount()));
			
			assertData("GCP BASE", cgpBase.trim(), toLatinNumber(draft.getCgpBase()));

			assertData("AT/EP PERCENT", atEpPercent.trim(), toLatinNumber(varTarifaIt + varTarifaIms));
			assertData("AT/EP AMOUNT", atEpAmount.trim(), toLatinNumber(itCost.getAmount() + imsCost.getAmount()));
			
			assertData("UNEMPLOYMENT PERCENT", unemploymentPercent.trim(), toLatinNumber(varPorcentajeDesmplE));
			assertData("UNEMPLOYMENT AMOUNT", unemploymentAmount.trim(), toLatinNumber(unemploymentCost.getAmount()));
			
			assertData("FP PERCENT", fpPercent.trim(), toLatinNumber(varPorcentajeFpE));			
			assertData("FP AMOUNT", fpAmount.trim(), toLatinNumber(fpCost.getAmount()));			
			
			assertData("FOGASA PERCENT", fogasaPercent.trim(), toLatinNumber(varPorcentajeFogasa));			
			assertData("FOGASA AMOUNT", fogasaAmount.trim(), toLatinNumber(fogasaCost.getAmount()));
			
			assertData("EXTR BASE", extrBase.trim(), toLatinNumber(draft.gethExtraBase()));			
			assertData("EXTR PERCENT", extrPercent.trim(), toLatinNumber(varPorcentajeExtrE));			
			assertData("EXTR AMOUNT", extrAmount.trim(), toLatinNumber(extrCost.getAmount()));
			
			assertData("NEXTR BASE", nextrBase.trim(), toLatinNumber(draft.getNonHExtraBase()));			
			assertData("NEXTR PERCENT", nextrPercent.trim(), toLatinNumber(varPorcentajeNextrE));			
			assertData("NEXTR AMOUNT", nextrAmount.trim(), toLatinNumber(nextrCost.getAmount()));
		
			assertData("TOTAL IRPF", irpfTotal.trim(), toLatinNumber(draft.getIrpfBase() + draft.getInkindIrpfBase()));
			assertData("TOTAL COSTS", totalCosts.trim(), toLatinNumber(draft.getTotalEnterprise()));
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
			
	
		
	}
	
	
	private String removeSpecialChars(String text) {
		if(text == null) 
			return null;
			
		return text.replace("\u20ac","").replace("%","");			
	}
	
	
	private <T> void assertData(String name,T payrollObj, T draftObj) {
		console.log(Status.COMPARE,"payroll " + name, payrollObj, EQUAL);
		console.log(Status.COMPARE,"Draft " + name, draftObj, EQUAL);
		
		assertWithLog("Draft -> Payroll",  name + " not matching", draftObj, payrollObj);
		console.success("DONE");
		console.jump();
	}
	
}
