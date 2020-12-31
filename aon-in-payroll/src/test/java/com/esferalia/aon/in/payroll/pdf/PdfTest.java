package com.esferalia.aon.in.payroll.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public class PdfTest {
	
	
	
	
	
	
	
	@Test
	//@Ignore
	public void testDsi() throws IOException, UnknownPDFException{
		try (InputStream is = PdfTest.class.getResourceAsStream("dsi_nomina_1pag.pdf")){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				//1st line
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println(employeeName);
					assertEquals("ANDREA ANDREA, MARIA", employeeName);
				}
				@Override
				public void setEnterpriseName(String enterpriseName) {
					System.out.println(enterpriseName);
					assertEquals("EMPRESA S.L.", enterpriseName);
				}
				//2nd line
				@Override
				public void setEnterpriseAddress(String enterpriseAddress) {
					System.out.println(enterpriseAddress);
					assertEquals("CL VIA, 12", enterpriseAddress);
				}
				@Override
				public void setEmployeeDocument(String employeeDocument) {
					System.out.println(employeeDocument);
					assertEquals("37723953C", employeeDocument);
				}
				//3rd line
				@Override
				public void setEnterpriseDocument(String enterpriseDocument) {
					System.out.println(enterpriseDocument);
					assertEquals("B50671908", enterpriseDocument);
				}
				@Override
				public void setSocialSecurityNumber(String socialSecurityNumber) {
					System.out.println(socialSecurityNumber);
					assertEquals("080298386069", socialSecurityNumber);
				}
				//4th line
				@Override
				public void setCategory(String category) {
					assertNull(category);
				}
				//5th line
				@Override
				public void setCcc(String ccc) {
					System.out.println(ccc);
					assertEquals("50874511193", ccc);
				}
				@Override
				public void setQuoteGroup(String quoteGroup) {
					System.out.println(quoteGroup);
					assertEquals("02", quoteGroup);
				}
				@Override
				public void setSeniorityDate(Date seniorityDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(seniorityDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(seniorityDate);
					assertEquals("14-09-1972", actual);
				}
				//6th line
				@Override
				public void setStartDate(Date startDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(startDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(startDate);
					assertEquals("01-02-2020", actual);
				}
				@Override
				public void setEndDate(Date endDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(endDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(endDate);
					assertEquals("29-02-2020", actual);
				}
				@Override
				public void setChargeDate(Date chargeDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(chargeDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(chargeDate);
					assertEquals("29-02-2020", actual);
				}
				@Override
				public void setTimeUnits(Integer timeUnits) {
					System.out.println(timeUnits);
					assertEquals((Integer)30, timeUnits);
				}
				//PAYMENTS
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					System.out.println("Amount: "+amount+", Description: "+description+", Start Date: "+startDate+", End date: "+endDate+"Payment: "+payment.getName());
					switch (description) {
					case "SALARIO BASE":
						break;
					case "ANTIGUEDAD":
						break;
					case "COMPL":
						break;
					case "NOCTURNO":
						break;
					case "ESTUDIOS":
						break;
					default:
						fail("Unrecognized concept");
				}
				HashSet<Double> devengos=new HashSet<Double>(Arrays.asList(389.35, 118.16, 8.99, 95.03, 147.31));
				if(!devengos.contains(amount))
					fail("Amount not found");
				}
				@Override
				public void setTotalPayment(Double totalPayment) {
					System.out.println(totalPayment);
					assertEquals((Double)758.84, totalPayment);
				}
				//DEDUCTIONS
				@Override
				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("Amount: "+amount+", description: "+description+", start date: "+start+", end date: "+end+", payment type: "+deduction.getType()+", payment name: "+deduction.getName());
					
		
					switch (description) {
						case "Dcto.Conceptos en Especie":
							break;
						case "Contingencias comunes":
							break;
						case "Formación Profesional":
							break;
						case "Desempleo":
							break;
						case "Impuesto sobre la renta de las personas físicas":
							break;
						case "DESCUENTO 1":
							break;
						default:
							fail("Unrecognized concept");
					}
					HashSet<Double> deductions = new HashSet<Double>(Arrays.asList(57.15,16.28,1.05,121.41,25.0));
					if(!deductions.contains(amount))
						fail("Amount not found");
				}
				
				@Override
				public void setTotalLiquid(Double totalLiquid) {
					System.out.println(totalLiquid);
					assertEquals((Double)537.95, totalLiquid);
				}
				@Override
				public void setRawCgcBase(Double rawCgcBase) {
					System.out.println(rawCgcBase);
					assertEquals((Double)1215.90, rawCgcBase);
				}
				@Override
				public void setCgcBase(Double commonBase) {
					System.out.println(commonBase);
					assertEquals((Double)1215.90, commonBase);
				}
				@Override
				public void setCgpBase(Double professionalBase) {
					System.out.println(professionalBase);
					assertEquals((Double)1050.00, professionalBase);
				}
				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					System.out.println(amount+", "+description+", "+start+", "+end+", cost: "+cost.getType());
					switch(description) {
						case "Contingencias comunes":
							if(amount!=286.95d) {
								fail("Wrong cost amount");
							}
							break;
						case "Contingencias profesionales":
							if(amount!=15.75d) {
								fail("Wrong cost amount");
							}
							break;
						case "Desempleo":
							if(amount!=57.75d) {
								fail("Wrong cost amount");
							}
							break;
						case "Formación Profesional":
							if(amount!=6.3d) {
								fail("Wrong cost amount");
							}
							break;
						case "Fogasa":
							if(amount!=2.1d) {
								fail("Wrong cost amount");
							}
							break;
						default:
							fail("Unrecognized cost");
					}
					
				}
				@Override
				public void setRemuneration(Double remuneration) {
					System.out.println(remuneration);
					assertEquals((Double)758.84, remuneration);
				}
				@Override
				public void setProExtBase(Double extraPayProration) {
					System.out.println(extraPayProration);
					assertEquals((Double)126.47, extraPayProration);
				}
				
				@Override
				public void setIrpfBase(Double irpfBase) {
					System.out.println(irpfBase);
					assertEquals((Double)758.84, irpfBase);
				}
//				@Override
//				public void setHExtraBase(Double hExtraBase) {
//					System.out.println(hExtraBase);
//					assertNull(hExtraBase);
//				}
				
				
				@Override
				public void addData(String name, ITimedVariable<?> data) {
					System.out.println(name+", "+data.getValue(data.getPeriod()));
					switch (name) {
					case "BASE_CGC":
						if((Double)data.getValue(data.getPeriod())!=1215.9) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGP":
						if((Double)data.getValue(data.getPeriod())!=1050.0) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGC_BRUTA":
						if((Double)data.getValue(data.getPeriod())!=885.31) {
							fail("Not catching the base");
						}
						break;
					case "BASE_IRPF":
						if((Double)data.getValue(data.getPeriod())!=758.84) {
							fail("Not catching the base");
						}
						break;
					case "CGC_E":
						if((Double)data.getValue(data.getPeriod())!=1215.9) {
							fail("Not catching the base");
						}
						break;
					case "CGC":
						if((Double)data.getValue(data.getPeriod())!=1215.9) {
							fail("Not catching the base");
						}
						break;
					case "IT_E":
						if((Double)data.getValue(data.getPeriod())!=1050) {
							fail("Not catching the base");
						}
						break;
					case "DESMPL_E":
						if((Double)data.getValue(data.getPeriod())!=1050) {
							fail("Not catching the base");
						}
						break;
					case "DESMPL":
						if((Double)data.getValue(data.getPeriod())!=1050) {
							fail("Not catching the base");
						}
						break;
					case "FP_E":
						if((Double)data.getValue(data.getPeriod())!=1050) {
							fail("Not catching the base");
						}
						break;
					case "FP":
						if((Double)data.getValue(data.getPeriod())!=1050) {
							fail("Not catching the base");
						}
						break;
					case "FOGASA_E":
						if((Double)data.getValue(data.getPeriod())!=1050) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGP_BRUTA":
						if((Double)data.getValue(data.getPeriod())!=885.31) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGP_E":
						if((Double)data.getValue(data.getPeriod())!=1050) {
							//fail("Not catching the base");
						}
						break;
					case "__EMPLOYEE_CODE":
						if(!((String)data.getValue(data.getPeriod())).equals("02")) {
							System.out.println((String)data.getValue(data.getPeriod()));
							fail("Wrong employee code");
						}
						break;
					default:
						System.err.println(name);
						fail("Unrecognized type");
					}
					
					
					
				}
				@Override
				public void setTotalSS(Double socialSecurityContributions) {
					System.out.println(socialSecurityContributions);
					assertEquals((Double)443.33, socialSecurityContributions);
				}
				
			});
		}
	}
	
	
	@Test
	//@Ignore
	public void testDsiAt() throws IOException, UnknownPDFException{
		try (InputStream is = PdfTest.class.getResourceAsStream("dsi_at.pdf")){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				//1st line
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println(employeeName);
					assertEquals("MINGUEZ MINGUEZ, LUIS", employeeName);
				}
				@Override
				public void setEnterpriseName(String enterpriseName) {
					System.out.println(enterpriseName);
					assertEquals("EMPRESA S.L.", enterpriseName);
				}
				//2nd line
				@Override
				public void setEnterpriseAddress(String enterpriseAddress) {
					System.out.println(enterpriseAddress);
					assertEquals("CL VIA, 12", enterpriseAddress);
				}
				@Override
				public void setEmployeeDocument(String employeeDocument) {
					System.out.println(employeeDocument);
					assertEquals("17445661T", employeeDocument);
				}
				//3rd line
				@Override
				public void setEnterpriseDocument(String enterpriseDocument) {
					System.out.println(enterpriseDocument);
					assertEquals("B50671908", enterpriseDocument);
				}
				@Override
				public void setSocialSecurityNumber(String socialSecurityNumber) {
					System.out.println(socialSecurityNumber);
					assertEquals("507871144491", socialSecurityNumber);
				}
				//4th line
				@Override
				public void setCategory(String category) {
					System.out.println(category);
					assertEquals("DEPTA. 1ª", category);
				}
				//5th line
				@Override
				public void setCcc(String ccc) {
					System.out.println(ccc);
					assertEquals("50874511193", ccc);
				}
				@Override
				public void setQuoteGroup(String quoteGroup) {
					System.out.println(quoteGroup);
					assertEquals("07", quoteGroup);
				}
				@Override
				public void setSeniorityDate(Date seniorityDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(seniorityDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(seniorityDate);
					assertEquals("01-01-2019", actual);
				}
				//6th line
				@Override
				public void setStartDate(Date startDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(startDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(startDate);
					assertEquals("01-06-2020", actual);
				}
				@Override
				public void setEndDate(Date endDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(endDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(endDate);
					assertEquals("30-06-2020", actual);
				}
				@Override
				public void setChargeDate(Date chargeDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(chargeDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(chargeDate);
					assertEquals("30-06-2020", actual);
				}
				@Override
				public void setTimeUnits(Integer timeUnits) {
					System.out.println(timeUnits);
					assertEquals((Integer)30, timeUnits);
				}
				//PAYMENTS
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					System.out.println("Amount: "+amount+", Description: "+description+", Start Date: "+startDate+", End date: "+endDate+"Payment: "+payment.getName());
					switch (description) {
					case "SALARIO BASE":
						break;
					case "ANTIGUEDAD":
						break;
					case "COMPL":
						break;
					case "NOCTURNO":
						break;
					case "ESTUDIOS":
						break;
					case "INCENTIVOS":
						break;
					case "ACCID./ENF.PROF del 16 al 29":
						break;
					default:
						fail("Unrecognized concept");
				}
				HashSet<Double> devengos=new HashSet<Double>(Arrays.asList(496.40, 113.33, 341.25));
				if(!devengos.contains(amount))
					fail("Amount not found");
				}
				@Override
				public void setTotalPayment(Double totalPayment) {
					System.out.println(totalPayment);
					assertEquals((Double)950.98, totalPayment);
				}
				//DEDUCTIONS
				@Override
				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("Amount: "+amount+", description: "+description+", start date: "+start+", end date: "+end+", payment type: "+deduction.getType()+", payment name: "+deduction.getName());
					
		
					switch (description) {
						case "Dcto.Conceptos en Especie":
							break;
						case "Contingencias comunes":
							break;
						case "Formación Profesional":
							break;
						case "Desempleo":
							break;
						case "Impuesto sobre la renta de las personas físicas":
							break;
						case "DESCUENTO 1":
							break;
						default:
							fail("Unrecognized concept");
					}
					HashSet<Double> deductions = new HashSet<Double>(Arrays.asList(56.7,18.7,1.21,19.02));
					if(!deductions.contains(amount))
						fail("Amount not found");
				}
				@Override
				public void setTotalLiquid(Double totalLiquid) {
					System.out.println(totalLiquid);
					assertEquals((Double)855.35, totalLiquid);
				}
				@Override
				public void setRawCgcBase(Double rawCgcBase) {
					System.out.println(rawCgcBase);
					assertEquals((Double)1206.4, rawCgcBase);
				}
				@Override
				public void setCgcBase(Double commonBase) {
					System.out.println(commonBase);
					assertEquals((Double)1206.4, commonBase);
				}
				@Override
				public void setCgpBase(Double professionalBase) {
					System.out.println(professionalBase);
					assertEquals((Double)1206.40, professionalBase);
				}
				//COSTS
				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					System.out.println(amount+", "+description+", "+start+", "+end+", cost: "+cost.getType());
					switch(description) {
						case "Contingencias comunes":
							if(amount!=284.71d) {
								fail("Wrong cost amount");
							}
							break;
						case "Contingencias profesionales":
							if(amount!=18.1d) {
								fail("Wrong cost amount");
							}
							break;
						case "Desempleo":
							if(amount!=66.35d) {
								fail("Wrong cost amount");
							}
							break;
						case "Formación Profesional":
							if(amount!=7.24d) {
								fail("Wrong cost amount");
							}
							break;
						case "Fogasa":
							if(amount!=2.41d) {
								fail("Wrong cost amount");
							}
							break;
						default:
							fail("Unrecognized cost");
					}
					
				}
				
				@Override
				public void setRemuneration(Double remuneration) {
					System.out.println(remuneration);
					assertEquals((Double)609.73, remuneration);
				}
				@Override
				public void setProExtBase(Double extraPayProration) {
					System.out.println(extraPayProration);
					assertEquals((Double)141.67, extraPayProration);
				}
				@Override
				public void setIrpfBase(Double irpfBase) {
					System.out.println(irpfBase);
					assertEquals((Double)950.98, irpfBase);
				}
				@Override
				public void setHExtraBase(Double hExtraBase) {
					System.out.println(hExtraBase);
					assertNull(hExtraBase);
				}
				
				
				@Override
				public void addData(String name, ITimedVariable<?> data) {
					System.out.println(name+", "+data.getValue(data.getPeriod()));
					switch (name) {
					case "BASE_CGC":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGP":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGC_BRUTA":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "BASE_IRPF":
						if((Double)data.getValue(data.getPeriod())!=950.98) {
							fail("Not catching the base");
						}
						break;
					case "CGC_E":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "CGC":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "IT_E":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "DESMPL_E":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "DESMPL":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "FP_E":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "FP":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "FOGASA_E":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGP_BRUTA":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGP_E":
						if((Double)data.getValue(data.getPeriod())!=1206.4) {
							fail("Not catching the base");
						}
						break;
					case "__EMPLOYEE_CODE":
						if(!((String)data.getValue(data.getPeriod())).equals("07")) {
							System.out.println((String)data.getValue(data.getPeriod()));
							fail("Wrong tarifa");
						}
						break;
					default:
						System.err.println(name);
						fail("Unrecognized type");
					}
					
					
					
				}
				@Override
				public void setTotalSS(Double socialSecurityContributions) {
					System.out.println(socialSecurityContributions);
					assertEquals((Double)455.42, socialSecurityContributions);
				}
				
				
				
			});
		}
	}
	
	@Test
	//@Ignore
	public void testA3() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("a3.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				//top right
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println(employeeName);
					assertEquals("IVANOV , PETAR GEORGIEV", employeeName);
				}
				
				
				@Override
				public void setEmployeeAddress(String employeeAddress) {
					System.out.println(employeeAddress);
					assertEquals("CL ALFONSO VI 30 3 DC", employeeAddress);
				}
				
						
				@Override
				public void setEmployeeCity(String employeeCity) {
					System.out.println(employeeCity);
					assertEquals("MIRANDA DE EBRO", employeeCity);
				}
				
				
				//top left
				@Override
				public void setEnterpriseDocument(String enterpriseDocument) {
					System.out.println(enterpriseDocument);
					assertEquals("J01409838", enterpriseDocument);
				}
				
				
				//main
				@Override
				public void setEnterpriseName(String enterpriseName) {
					System.out.println(enterpriseName);
					assertEquals("RESTAURANTE EL VISO, S.C", enterpriseName);
				}

				@Override
				public void setEnterpriseAddress(String enterpriseAddress) {
					System.out.println(enterpriseAddress);
					assertEquals("CL REAL 32 BJ", enterpriseAddress);
				}

				@Override
				public void setCcc(String ccc) {
					System.out.println(ccc);
					assertEquals("01103481696", ccc);
				}


				
				
				@Override
				public void setCategory(String category) {
					System.out.println(category);
					assertEquals("FREGADOR", category);
				}		

				@Override
				public void setSeniorityDate(Date seniorityDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(seniorityDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(seniorityDate);
					assertEquals("01-10-2008", actual);
				}

				@Override
				public void setEmployeeDocument(String employeeDocument) {
					System.out.println(employeeDocument);
					assertEquals("X8865220P", employeeDocument);
				}
				


				@Override
				public void setSocialSecurityNumber(String socialSecurityNumber) {
					System.out.println(socialSecurityNumber);
					assertEquals("481045498340", socialSecurityNumber);
				}
				
				@Override
				public void setQuoteGroup(String quoteGroup) {
					System.out.println(quoteGroup);
					assertEquals("7", quoteGroup);
				}
				

				
				@Override
				public void setStartDate(Date startDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(startDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(startDate);
					assertEquals("01-01-2020", actual);
				}
				
				@Override
				public void setEndDate(Date endDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(endDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(endDate);
					assertEquals("31-01-2020", actual);
				}
				
				@Override
				public void setChargeDate(Date chargeDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(chargeDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(chargeDate);
					assertEquals("31-01-2020", actual);
				}

				@Override
				public void setTimeUnits(Integer timeUnits) {
					System.out.println(timeUnits);
					assertEquals((Integer)30, timeUnits);
				}
				
				
				//SEVERAL PAYMENTS
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					System.out.println("Amount: "+amount+", quote: "+quote+", tax: "+tax+", description: "+description+", start date: "+startDate+", end date: "+endDate+", payment type: "+payment.getType()+", payment name: "+payment.getName());
					switch (description) {
						case "*Salario Base":
							break;
						case "*Antigüedad":
							break;
						case "*Plus Manutención":
							break;
						case "*P.p.extras":
							break;
						case "*Bonus octubre":
							break;
						case "*Domingos-festiv":
							break;
						default:
							fail("Unrecognized payment concept: "+description);
					}
					HashSet<Double> devengos=new HashSet<Double>(Arrays.asList(802.24, 128.36, 31.17, 169.0, 78.2, 51.34));
					if(!devengos.contains(amount))
						fail("Amount not found");
					
				}
				
				@Override
				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("Amount: "+amount+", description: "+description+", start date: "+start+", end date: "+end+", payment type: "+deduction.getType()+", payment name: "+deduction.getName());
					
		
					switch (description) {
						case "Dcto.Conceptos en Especie":
							break;
						case "Contingencias comunes":
							break;
						case "Formación Profesional":
							break;
						case "Desempleo":
							break;
						case "Impuesto sobre la renta de las personas físicas":
							break;
						default:
							fail("Unrecognized deduction concept: "+description+", length: "+description.length());
					}
					HashSet<Double> deductions = new HashSet<Double>(Arrays.asList(31.17, 59.23, 1.26, 19.53, 50.42));
					if(!deductions.contains(amount))
						fail("Amount not found");
				}
				
				
				
				//BASES
				@Override
				public void setRemuneration(Double remuneration) {
					System.out.println(remuneration);
					assertEquals((Double)1260.31, remuneration);
				}
				@Override
				public void setProExtBase(Double extraPayProration) {
					assertNull(extraPayProration);
				}
				@Override
				public void setCgcBase(Double commonBase) {
					System.out.println(commonBase);
					assertEquals((Double)1260.31, commonBase);
				}				
				@Override
				public void setCgpBase(Double professionalBase) {
					System.out.println(professionalBase);
					assertEquals((Double)1260.31, professionalBase);
				}
				@Override
				public void setIrpfBase(Double irpfBase) {
					System.out.println(irpfBase);
					assertEquals((Double)1260.31, irpfBase);
				}
				@Override
				public void setTotalPayment(Double totalPayment) {
					System.out.println(""+totalPayment);
					assertEquals((Double)1260.31, totalPayment);
				}
				@Override
				public void setTotalDeduction(Double totalDeduction) {
					System.out.println(""+totalDeduction);
					assertEquals((Double)161.61, totalDeduction);
				}


				@Override
				public void setIssueDate(Date issueDate) {
					DateFormat df = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy");
					System.out.println(df.format(issueDate));
					String actual = new SimpleDateFormat("dd-MM-yyyy").format(issueDate);
					assertEquals("31-01-2020", actual);	
				}


				@Override
				public void setTotalLiquid(Double totalLiquid) {
					System.out.println(totalLiquid);
					assertEquals((Double)1098.7, totalLiquid);
				}

				
				@Override
				public void addData(String name, ITimedVariable<?> data) {
					System.out.println(name+", "+data.getValue(data.getPeriod()));
					switch (name) {
					case "BASE_CGC":
						if((Double)data.getValue(data.getPeriod())!=1260.31) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGP":
						if((Double)data.getValue(data.getPeriod())!=1260.31) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGC_BRUTA":
						if((Double)data.getValue(data.getPeriod())!=1260.31) {
							fail("Not catching the base");
						}
						break;
					case "BASE_IRPF":
						if((Double)data.getValue(data.getPeriod())!=1260.31) {
							fail("Not catching the base");
						}
						break;
					case "TOTAL_DEVENGADO":
						if((Double)data.getValue(data.getPeriod())!=1260.31) {
							fail("Not catching the base");
						}
						break;
					case "CGC_E":
						if((Double)data.getValue(data.getPeriod())!=1260.31) {
							fail("Not catching the base");
						}
						break;
					
					case "IT_E":
						if((Double)data.getValue(data.getPeriod())!=1260.31) {
							fail("Not catching the base");
						}
						break;
					case "DESMPL_E":
						if((Double)data.getValue(data.getPeriod())!=1260.31) {
							fail("Not catching the base");
						}
						break;
					case "FP_E":
						if((Double)data.getValue(data.getPeriod())!=1260.31) {
							fail("Not catching the base");
						}
						break;
					case "FOGASA_E":
						if((Double)data.getValue(data.getPeriod())!=1260.31) {
							fail("Not catching the base");
						}
						break;
					case "__ENTERPRISE_CODE":
						if(!((String)data.getValue(data.getPeriod())).equals("200")) {
							
							fail("Wrong cod ct");
						}
						break;
					case "__EMPLOYEE_CODE":
						if(!((String)data.getValue(data.getPeriod())).equals("7")) {
							System.out.println((String)data.getValue(data.getPeriod()));
							fail("Wrong tarifa");
						}
						break;
					default:
						fail("Unrecognized type");
					}
					
					
					
				}


				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					System.out.println(amount+", "+description+", "+start+", "+end+", cost: "+cost.getType());
					switch(description) {
						case "Contingencias comunes":
							if(amount!=297.43d) {
								fail("Wrong cost amount");
							}
							break;
						case "Contingencias profesionales":
							if(amount!=18.9d) {
								fail("Wrong cost amount");
							}
							break;
						case "Desempleo":
							if(amount!=69.32d) {
								fail("Wrong cost amount");
							}
							break;
						case "Formación Profesional":
							if(amount!=7.56d) {
								fail("Wrong cost amount");
							}
							break;
						case "Fogasa":
							if(amount!=2.52d) {
								fail("Wrong cost amount");
							}
							break;
						default:
							fail("Unrecognized cost");
					}
					
				}


				@Override
				public void setTotalEnterprise(Double totalEnterprise) {
					System.out.println(totalEnterprise);
					if(totalEnterprise!=395.73)
						fail("Wrong total enterprise");
				}


				@Override
				public void setTotalSS(Double socialSecurityContributions) {
					System.out.println(socialSecurityContributions);
					if(socialSecurityContributions!=475.75)
						fail("Wrong total SS");
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
			});
		}
	}
	
	
	
	@Test
	//@Ignore
	public void testA3New() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("year_payrolls_lorena.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder());
		}
	}
	
//	@Test
//	@Ignore
//	public void hfhffh() throws IOException, UnknownPDFException {
//		try ( InputStream is = PdfTest.class.getResourceAsStream("dsi_paga_extra.pdf") ){
//			SalaryPDFParser.parse(is, new SalaryBuilder());
//		}
//	}

	@Test
	@Ignore
	public void testAltai() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("altai.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println(employeeName);
				}
			});
		}
	}
	
	
}