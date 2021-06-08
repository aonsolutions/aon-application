package com.esferalia.aon.gwt.payroll.server.pdf;

import static com.esferalia.aon.gwt.payroll.tools.Console.Separator.ARROW_REVERSE;
import static com.esferalia.aon.gwt.payroll.tools.Console.Status.GET;
import static com.esferalia.aon.gwt.payroll.tools.Console.Status.RUN;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.StringToolkit.joinCharacterList;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.AT_EP_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.AT_EP_PERCENT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.CGC_AMOUNT;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.CGC_BASE;
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.CGC_PERCENT;
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
import static com.esferalia.aon.in.payroll.pdf.maker.payroll.PayrollTemplateTags.REMUNERATION;
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
import com.esferalia.aon.in.payroll.pdf.api.toolkit.PDFToolkit;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

public class DefaultPayrollIntegrationTests {

	
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
		
		/** Filling employee data **/
		draft.setEmployeeName("ANTONIO DE LA VEGA AMATISTO RODIGUEZ PEREZ LÓPEZ");
		draft.setEmployeeAddress("Avenida de los cerezos 25 Ático A, escalera B1, Alcántara, Toledo");
		draft.setEmployeeDocument("50392654R");
		draft.setEmployeeSeniorityDate(new Date());
		draft.setEmployeeQuoteGroup("01");
		draft.setEmployeeAgreementCategory("JUNIOR SOFTWARE ENGINEER / ANALISTA");
		draft.setEmployeeSS("182624935460231");
		
		/** Filling etreprise data **/
		draft.setEnterpriseName("MICROSOFT AI LABS LTD WESTERN-EUROPE.S.L.");
		draft.setEnterpriseCCC("16485960263702345");
		draft.setEnterpriseAddress("Plaza de los alfajores azules, 22 Bajo A, Alameda de riotes, Extremadura");
		draft.setEnterpriseDocument("192735471938G");
		
		/** Payments **/
		Payment horasExtrasEstruct = new Payment();
		horasExtrasEstruct.setType(Type.CRA_0002);
		horasExtrasEstruct.setDescription("Horas extras estructurales pro");
		horasExtrasEstruct.setAmount(125.55);
		horasExtrasEstruct.setQuote(125.55);
		
		Payment horasExtrasNoEstruct = new Payment();	
		horasExtrasNoEstruct.setType(Type.CRA_0003);
		horasExtrasNoEstruct.setDescription("Horas extras no estructurales pro");
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
		imsCost.setAmount(12.99d);

		Deduction itCost = new Deduction();
		itCost.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.OTHER);
		itCost.setName("IT_E");
		itCost.setAmount(12.99d);

		Deduction unemploymentCost = new Deduction();
		unemploymentCost.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.UNEMPLOYMENT);
		unemploymentCost.setName("DESMPL_E");
		unemploymentCost.setAmount(7.12d);
		
		Deduction fpCost = new Deduction();
		fpCost.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.JOB_TRAINING);
		fpCost.setName("FP_E");
		fpCost.setAmount(6.8d);
		
		Deduction fogasaCost = new Deduction();
		fogasaCost.setType(com.esferalia.aon.gwt.payroll.shared.Deduction.Type.FOGASA);
		fogasaCost.setName("FOGASA");
		fogasaCost.setAmount(3.8d);
		
		draft.addCost(cgcCost);
		draft.addCost(imsCost);
		draft.addCost(itCost);
		draft.addCost(unemploymentCost);
		draft.addCost(fpCost);
		draft.addCost(fogasaCost);
		
		/** Context data **/
		draft.addVariable("PORCENTAJE_DESMPL", 5, new Date(), new Date());
		draft.addVariable("PORCENTAJE_CGC", 17, new Date(), new Date());
		
		/** Cost context data **/
		draft.addVariable("PORCENTAJE_CGC_E", 17, new Date(), new Date());
		draft.addVariable("TARIFA_IMS", 17, new Date(), new Date());
		draft.addVariable("TARIFA_IT", 4.56, new Date(), new Date());
		draft.addVariable("PORCENTAJE_DESMPL_E", 4.56, new Date(), new Date());
	
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
			
			String irpfmount = "";
			String inkindIrpfAmount = "";
			
			String totalCosts = "";
			
			/** Getting data markers **/
			Optional<PDMarkedContent> empNameMark = PDFToolkit.getContent(document, EMPLOYEE_NAME);
			Optional<PDMarkedContent> empDocumentMark = PDFToolkit.getContent(document, EMPLOYEE_DOCUMENT);
			Optional<PDMarkedContent> empGroupMark = PDFToolkit.getContent(document, EMPLOYEE_GROUP);
			Optional<PDMarkedContent> empCategoryMark = PDFToolkit.getContent(document, EMPLOYEE_CATEGORY);
			Optional<PDMarkedContent> empSsMark = PDFToolkit.getContent(document, EMPLOYEE_SS_NUMBER);
			Optional<PDMarkedContent> empSeniorityMark = PDFToolkit.getContent(document, EMPLOYEE_SENIORITY);				
			
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
			
			Optional<PDMarkedContent> entNameMark = PDFToolkit.getContent(document, ENTERPRISE_NAME);
			Optional<PDMarkedContent> entCccMark = PDFToolkit.getContent(document, ENTERPRISE_CCC);
			Optional<PDMarkedContent> entDocumentMark = PDFToolkit.getContent(document, ENTERPRISE_DOCUMENT);
			Optional<PDMarkedContent> entAddressMark = PDFToolkit.getContent(document, ENTERPRISE_ADDRESS);
			
			if(entNameMark.isPresent())
				entName = joinCharacterList(entNameMark.get().getContents());	
			
			if(entDocumentMark.isPresent())
				entDocument = joinCharacterList(entDocumentMark.get().getContents());	
			
			if(entCccMark.isPresent())
				entCCC = joinCharacterList(entCccMark.get().getContents());
			
			entDocument = entDocument.replace("NIF:", "").trim();	
			entCCC = entCCC.replace("CCC:", "").trim();
			
			console.jump();
			console.log(RUN,"ENTERPRISE DATA");
			console.line();
			
			console.log(GET, "NAME", entName, ARROW_REVERSE);
			console.log(GET, "DOCUMENT", entDocument, ARROW_REVERSE);
			console.log(GET, "CCC", entCCC, ARROW_REVERSE);
			
			Optional<PDMarkedContent> remunerationMark = PDFToolkit.getContent(document, REMUNERATION);			
			Optional<PDMarkedContent> prorrationMark = PDFToolkit.getContent(document, EXTRA_PRORRATION);		
			
			Optional<PDMarkedContent> cgcBaseMark = PDFToolkit.getContent(document, CGC_BASE);			
			Optional<PDMarkedContent> cgcPercentMark = PDFToolkit.getContent(document, CGC_PERCENT);			
			Optional<PDMarkedContent> cgcAmountMark = PDFToolkit.getContent(document, CGC_AMOUNT);	
			
			Optional<PDMarkedContent> cgpBaseMark = PDFToolkit.getContent(document, REMUNERATION);
			
			Optional<PDMarkedContent> atEpPercentMark = PDFToolkit.getContent(document, AT_EP_PERCENT);			
			Optional<PDMarkedContent> atEpAmountMark = PDFToolkit.getContent(document, AT_EP_AMOUNT);			

			Optional<PDMarkedContent> unemploymentPercentMark = PDFToolkit.getContent(document, UNEMPLOYMENT_PERCENT);			
			Optional<PDMarkedContent> unemploymentAmountMark = PDFToolkit.getContent(document, UNEMPLOYMENT_AMOUNT);			

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
			
			remuneration = remuneration.replace("\u20ac","").replace("%","");			
			prorration = prorration.replace("\u20ac","").replace("%","");			
			
			cgcBase = cgcBase.replace("\u20ac","").replace("%","");			
			cgcPercent = cgcPercent.replace("\u20ac","").replace("%","");			
			cgcAmount = cgcAmount.replace("\u20ac","").replace("%","");			
			
			unemploymentAmount = unemploymentAmount.replace("\u20ac","").replace("%","");			
			unemploymentPercent = unemploymentPercent.replace("\u20ac","").replace("%","");			
			
			console.jump();
			console.log(RUN,"COSTS");
			console.line();
			
			console.log(GET, "REMUNERATION", remuneration, ARROW_REVERSE);
			console.log(GET, "PRORRATION", prorration, ARROW_REVERSE);
			
			console.log(GET, "CGC BASE", cgcBase, ARROW_REVERSE);
			console.log(GET, "CGC PERCENT", cgcPercent, ARROW_REVERSE);
			console.log(GET, "CGC AMOUNT", cgcAmount, ARROW_REVERSE);
			
			console.log(GET, "UNEMPLOYMENT AMOUNT", unemploymentAmount, ARROW_REVERSE);
			console.log(GET, "UNEMPLOYMENT PERCENT", unemploymentPercent, ARROW_REVERSE);
			
			FileOutputStream fos = new FileOutputStream("PDF_TRYING.pdf");
			fos.write(pdf.getBytes());
			fos.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
			
	
		
	}
	
		
	
}
