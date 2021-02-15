package com.esferalia.aon.in.payroll.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.in.payroll.csv.IEnterprisePayroll;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

@Ignore
public class PdfTest {
	
	
	
	
	
	
	
	@Test
	@Ignore
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
					case "GRUPO_COTIZACION":
						if(!((String)data.getValue(data.getPeriod())).equals("02")) {
							System.out.println((String)data.getValue(data.getPeriod()));
							fail("Wrong employee code");
						}
						break;
					case "DIAS_NOMINA":
						if((int)data.getValue(data.getPeriod())!=30)
							fail("Wrong time units");
						break;
					case "PORCENTAJE_IRPF":
						if((Double)data.getValue(data.getPeriod())!=16d)
							fail("Wrong irpf percent");
						break;
					case "PORCENTAJE_CGC":
						if((Double)data.getValue(data.getPeriod())!=4.7)
							fail("Wrong CGC percent");
						break;
					case "PORCENTAJE_DESMPL":
						if((Double)data.getValue(data.getPeriod())!=1.55)
							fail("Wrong DESMPL percent");
						break;
					case "PORCENTAJE_FP":
						if((Double)data.getValue(data.getPeriod())!=0.1)
							fail("Wrong FP percent");
						break;
					case "PORCENTAJE_CGC_E":
						if((Double)data.getValue(data.getPeriod())!=23.6)
							fail("Wrong CGC_E percent");
						break;
					case "PORCENTAJE_CGP_E":
						if((Double)data.getValue(data.getPeriod())!=1.5)
							fail("Wrong CGP_E percent");
						break;
					case "PORCENTAJE_DESMPL_E":
						if((Double)data.getValue(data.getPeriod())!=5.5)
							fail("Wrong DESMPL_E percent");
						break;
					case "PORCENTAJE_FP_E":
						if((Double)data.getValue(data.getPeriod())!=0.6)
							fail("Wrong DESMPL_E percent");
						break;
					case "PORCENTAJE_FOGASA":
						if((Double)data.getValue(data.getPeriod())!=0.2)
							fail("Wrong DESMPL_E percent");
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
	@Ignore
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
					case "GRUPO_COTIZACION":
						if(!((String)data.getValue(data.getPeriod())).equals("07")) {
							System.out.println((String)data.getValue(data.getPeriod()));
							fail("Wrong tarifa");
						}
						break;
					case "DIAS_NOMINA":
						if((int)data.getValue(data.getPeriod())!=30)
							fail("Wrong time units");
						break;
					case "PORCENTAJE_IRPF":
						if((Double)data.getValue(data.getPeriod())!=2d)
							fail("Wrong irpf percent");
						break;
					case "PORCENTAJE_CGC":
						if((Double)data.getValue(data.getPeriod())!=4.7)
							fail("Wrong CGC percent");
						break;
					case "PORCENTAJE_DESMPL":
						if((Double)data.getValue(data.getPeriod())!=1.55)
							fail("Wrong DESMPL percent");
						break;
					case "PORCENTAJE_FP":
						if((Double)data.getValue(data.getPeriod())!=0.1)
							fail("Wrong FP percent");
						break;
					case "PORCENTAJE_CGC_E":
						if((Double)data.getValue(data.getPeriod())!=23.6)
							fail("Wrong CGC_E percent");
						break;
					case "PORCENTAJE_CGP_E":
						if((Double)data.getValue(data.getPeriod())!=1.5)
							fail("Wrong CGP_E percent");
						break;
					case "PORCENTAJE_DESMPL_E":
						if((Double)data.getValue(data.getPeriod())!=5.5)
							fail("Wrong DESMPL_E percent");
						break;
					case "PORCENTAJE_FP_E":
						if((Double)data.getValue(data.getPeriod())!=0.6)
							fail("Wrong DESMPL_E percent");
						break;
					case "PORCENTAJE_FOGASA":
						if((Double)data.getValue(data.getPeriod())!=0.2)
							fail("Wrong DESMPL_E percent");
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
						case "Salario Base":
							break;
						case "Antigüedad":
							break;
						case "Plus Manutención":
							break;
						case "P.p.extras":
							break;
						case "Bonus octubre":
							break;
						case "Domingos-festiv":
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
					assertEquals((Double)169d, extraPayProration);
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
					case "TC2":
						if(!((String)data.getValue(data.getPeriod())).equals("200")) {
							
							fail("Wrong cod ct");
						}
						break;
					case "GRUPO_COTIZACION":
						if(!((String)data.getValue(data.getPeriod())).equals("7")) {
							System.out.println((String)data.getValue(data.getPeriod()));
							fail("Wrong tarifa");
						}
						break;
					case "DIAS_NOMINA":
						if(!(((int)data.getValue(data.getPeriod()))==30)) {
							System.out.println((Integer)data.getValue(data.getPeriod()));
							fail("Wrong time units");
						}
						break;
					case "PORCENTAJE_CGC":
						if((Double)data.getValue(data.getPeriod())!=4.7) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_FP":
						if((Double)data.getValue(data.getPeriod())!=0.1) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_DESMPL":
						if((Double)data.getValue(data.getPeriod())!=1.55) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_IRPF":
						if((Double)data.getValue(data.getPeriod())!=4.0) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_CGC_E":
						if((Double)data.getValue(data.getPeriod())!=23.6) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_CGP_E":
						if((Double)data.getValue(data.getPeriod())!=1.5) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_DESMPL_E":
						if((Double)data.getValue(data.getPeriod())!=5.5) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_FP_E":
						if((Double)data.getValue(data.getPeriod())!=0.6) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_FOGASA":
						if((Double)data.getValue(data.getPeriod())!=0.2) {
							fail("Not catching the percentage");
						}
						break;
					case "IMS_E":
						break;
					default:
						fail("Unrecognized type: "+ name);
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
						case "IMS_E":
							break;
						default:
							fail("Unrecognized cost: "+description);
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
					assertEquals((Double)80.02/*475.75d/*/, socialSecurityContributions);
				}
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
			});
		}
	}
	
	
	
	@Test
	//@Ignore
	public void testA3New() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("learning2020.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder());
		}
	}
	
	@Test
	//@Ignore
	public void testA3Muchos() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("learning2020.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				public int cont = 1;
				@Override
				public void setTotalDeduction(Double totalDeduction) {
					if(totalDeduction==null) {
						fail("Fallo en la nómina "+cont);
					}
					cont++;
				}
				
			});
		}
	}
	
	@Test
	//@Ignore
	public void testA3Demasiados() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("NOMINAS UN LUGAR 2020.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				public int contIrpf = 1;

				@Override
				public void setProExtBase(Double extraPayProration) {
					System.out.println(extraPayProration);
				}

				
			});
		}
	}
	
	@Test
	@Ignore
	public void testA3Excessive() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("NOMINAS ATSP 2020.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
//				public int contIrpf = 1;
//
//				@Override
//				public void setProExtBase(Double extraPayProration) {
//					System.out.println(extraPayProration);
//				}
				
				
			});
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
	//@Ignore
	public void testAplifisa() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("aplifisa/nomina.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				@Override
				public void setEnterpriseName(String enterpriseName) {
					System.out.println(enterpriseName);
				}
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println(employeeName);
					
				}
				
				
				@Override
				public void setEnterpriseAddress(String enterpriseAddress) {
					System.out.println(enterpriseAddress);
					assertEquals("PZ JESÚS DE MEDINACELLI, 6   22", enterpriseAddress);
				}
				@Override
				public void setEmployeeDocument(String employeeDocument) {
					System.out.println(employeeDocument);
					assertEquals("X7379673P", employeeDocument);
				}
				
				
				@Override
				public void setEnterpriseCity(String enterpriseCity) {
					System.out.println(enterpriseCity);
					assertEquals("VALENCIA", enterpriseCity);
				}
				@Override
				public void setSocialSecurityNumber(String socialSecurityNumber) {
					System.out.println(socialSecurityNumber);
					assertEquals("441004368889", socialSecurityNumber);
				}
				
				
				@Override
				public void setEnterpriseDocument(String enterpriseDocument) {
					System.out.println(enterpriseDocument);
					assertEquals("B40589533", enterpriseDocument);
				}
				@Override
				public void setCategory(String category) {
					System.out.println(category);
					assertEquals("OFICIAL 1ª", category);
				}
				
				
				@Override
				public void setCcc(String ccc) {
					System.out.println(ccc);
					assertEquals("46151517741", ccc);
				}
				@Override
				public void setQuoteGroup(String quoteGroup) {
					System.out.println(quoteGroup);
					assertEquals("8", quoteGroup);
				}
				@Override
				public void setSeniorityDate(Date seniorityDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(seniorityDate);
					System.out.println(seniorityDate);
					assertTrue(calendar.get(Calendar.DAY_OF_MONTH)==14 && calendar.get(Calendar.MONTH)==8 && calendar.get(Calendar.YEAR)==2020);
				}
				
				
				@Override
				public void setStartDate(Date startDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(startDate);
					System.out.println(startDate);
					assertTrue(calendar.get(Calendar.DAY_OF_MONTH)==1 && calendar.get(Calendar.MONTH)==11 && calendar.get(Calendar.YEAR)==2020);
				}
				@Override
				public void setEndDate(Date endDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					System.out.println(endDate);
					assertTrue(calendar.get(Calendar.DAY_OF_MONTH)==2 && calendar.get(Calendar.MONTH)==11 && calendar.get(Calendar.YEAR)==2020);
				}
				
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					System.out.println("PAYMENT:\tAmount: "+amount+", Description: "+description+", Start Date: "+startDate+", End date: "+endDate+"Payment: "+payment.getName());
					switch (description) {
					case "SALARIO BASE":
						break;
					case "PLUS DE TRANSPORTE":
						break;
					case "COMPL. ACTIVIDAD":
						break;
					default:
						System.err.println(description);
						fail("Unrecognized concept");
				}
				HashSet<Double> devengos=new HashSet<Double>(Arrays.asList(57.98, 6.56, 34.66));
				if(!devengos.contains(amount))
					fail("Amount not found");
				}
				
				@Override
				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("DEDUCTION:\tAmount: "+amount+", description: "+description+", start date: "+start+", end date: "+end+", payment type: "+deduction.getType()+", payment name: "+deduction.getName());
					
		
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
					HashSet<Double> deductions = new HashSet<Double>(Arrays.asList(5.43,1.85,.12,1.98));
					if(!deductions.contains(amount))
						fail("Amount not found");
				}
				
				@Override
				public void addData(String name, ITimedVariable<?> data) {
					System.out.println("DATA:\t"+name+", "+data.getValue(data.getPeriod()));
					switch (name) {
					case "BASE_CGC":
						if((Double)data.getValue(data.getPeriod())!=115.5) {
							fail("Not catching the base");
						}
						break;
					case "DESMPL":
						if((Double)data.getValue(data.getPeriod())!=115.5) {
							fail("Not catching the base");
						}
						break;
					case "FP":
						if((Double)data.getValue(data.getPeriod())!=115.5) {
							fail("Not catching the base");
						}
						break;
					case "BASE_IRPF":
						if((Double)data.getValue(data.getPeriod())!=99.2) {
							fail("Not catching the base");
						}
						break;
					case "BASE_DESMPL":
						if((Double)data.getValue(data.getPeriod())!=115.5) {
							fail("Not catching the base");
						}
						break;
					case "BASE_FP":
						if((Double)data.getValue(data.getPeriod())!=115.5) {
							fail("Not catching the base");
						}
						break;
					case "TOTAL_DEVENGADO":
						if((Double)data.getValue(data.getPeriod())!=99.2) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGC_E":
						if((Double)data.getValue(data.getPeriod())!=115.5) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGP_E":
						if((Double)data.getValue(data.getPeriod())!=115.5) {
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
					case "TC2":
						if(!((String)data.getValue(data.getPeriod())).equals("200")) {
							
							fail("Wrong cod ct");
						}
						break;
					case "GRUPO_COTIZACION":
						if(!((String)data.getValue(data.getPeriod())).equals("8")) {
							System.out.println((String)data.getValue(data.getPeriod()));
							fail("Wrong tarifa");
						}
						break;
					case "DIAS_NOMINA":
						if(!(((int)data.getValue(data.getPeriod()))==2)) {
							System.out.println((Integer)data.getValue(data.getPeriod()));
							fail("Wrong time units");
						}
						break;
					case "PORCENTAJE_CGC":
						if((Double)data.getValue(data.getPeriod())!=4.7) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_FP":
						if((Double)data.getValue(data.getPeriod())!=0.1) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_DESMPL":
						if((Double)data.getValue(data.getPeriod())!=1.6) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_IRPF":
						if((Double)data.getValue(data.getPeriod())!=2.0) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_CGC_E":
						if((Double)data.getValue(data.getPeriod())!=23.6) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_IT_E":
						if((Double)data.getValue(data.getPeriod())!=6.7) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_CGP_E":
						if((Double)data.getValue(data.getPeriod())!=1.5) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_DESMPL_E":
						if((Double)data.getValue(data.getPeriod())!=6.7) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_FP_E":
						if((Double)data.getValue(data.getPeriod())!=.6) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_FOGASA_E":
						if((Double)data.getValue(data.getPeriod())!=.2) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_FUERZA_MAYOR":
						if((Double)data.getValue(data.getPeriod())!=2) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_NO_ESTRUCTURALES":
						if((Double)data.getValue(data.getPeriod())!=4.7) {
							fail("Not catching the percentage");
						}
						break;
					case "IMS_E":
						break;
					default:
						fail("Unrecognized type: "+ name);
					}
					
					
					
				}
				
				
				@Override
				public void setTotalDeduction(Double totalDeduction) {
					System.out.println(totalDeduction);
					assertEquals((Double)9.38, totalDeduction);
				}
				@Override
				public void setTotalLiquid(Double totalLiquid) {
					System.out.println(totalLiquid);
					assertEquals((Double)89.82, totalLiquid);
				}
				@Override
				public void setIssueDate(Date issueDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(issueDate);
					System.out.println(issueDate);
					assertTrue(calendar.get(Calendar.DAY_OF_MONTH)==02 && calendar.get(Calendar.MONTH)==11 && calendar.get(Calendar.YEAR)==2020);
				}
				
				@Override
				public void setRemuneration(Double remuneration) {
					System.out.println(remuneration);
					assertEquals((Double)99.2, remuneration);
				}
				@Override
				public void setProExtBase(Double extraPayProration) {
					System.out.println(extraPayProration);
					assertEquals((Double)16.3, extraPayProration);
				}
				
				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					System.out.println(amount+", "+description+", "+start+", "+end+", cost: "+cost.getType());
					switch(description) {
						case "Contingencias comunes":
							if(amount!=27.26d) {
								fail("Wrong cost amount");
							}
							break;
						case "Contingencias profesionales":
							if(amount!=7.74d) {
								fail("Wrong cost amount");
							}
							break;
						case "Desempleo":
							if(amount!=7.74d) {
								fail("Wrong cost amount");
							}
							break;
						case "Formación Profesional":
							if(amount!=.69) {
								fail("Wrong cost amount");
							}
							break;
						case "Fogasa":
							if(amount!=.23) {
								fail("Wrong cost amount");
							}
							break;
						case "Impuesto sobre la renta de las personas físicas":
							if(amount!=99.2) {
								fail("Wrong cost amount");
							}
							break;
						case "IMS_E":
							break;
						default:
							fail("Unrecognized cost: "+description);
					}
					
				}
				@Override
				public void setTotalSS(Double socialSecurityContributions) {
					System.out.println(socialSecurityContributions);
					assertEquals((Double)7.4, socialSecurityContributions);
				}
				@Override
				public void setTotalEnterprise(Double totalEnterprise) {
					System.out.println(totalEnterprise);
					assertEquals((Double)142.86, totalEnterprise);
				}
				
				
				
				
			});
			
			
		}
	}
	@Test
	//@Ignore
	public void testAplifisa2() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("aplifisa/aplifisa_para_probar.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				@Override
				public void setEnterpriseName(String enterpriseName) {
					System.out.println("Ent. name:\t"+enterpriseName);
				}
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println("Emp. name:\t"+employeeName);
					
				}
				
				
				@Override
				public void setEnterpriseAddress(String enterpriseAddress) {
					System.out.println("Ent. address:\t"+enterpriseAddress);
				}
				@Override
				public void setEmployeeDocument(String employeeDocument) {
					System.out.println("Emp. doc:\t"+employeeDocument);
				}
				
				
				@Override
				public void setEnterpriseCity(String enterpriseCity) {
					System.out.println("Ent. city:\t"+enterpriseCity);
				}
				@Override
				public void setSocialSecurityNumber(String socialSecurityNumber) {
					System.out.println("NSS:\t"+socialSecurityNumber);
				}
				
				
				@Override
				public void setEnterpriseDocument(String enterpriseDocument) {
					System.out.println("Ent. cod:\t"+enterpriseDocument);
				}
				@Override
				public void setCategory(String category) {
					System.out.println("Category:\t"+category);
				}
				
				
				@Override
				public void setCcc(String ccc) {
					System.out.println("CCC:\t"+ccc);
				}
				@Override
				public void setQuoteGroup(String quoteGroup) {
					System.out.println("Quote group:\t"+quoteGroup);
				}
				@Override
				public void setSeniorityDate(Date seniorityDate) {
					System.out.println("Seniority date:\t"+seniorityDate);
				}
				
				
				@Override
				public void setStartDate(Date startDate) {
					System.out.println("Start date:\t"+startDate);
				}
				@Override
				public void setEndDate(Date endDate) {
					System.out.println("End. name:\t"+endDate);
				}
				
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					System.out.println("PAYMENT:\tAmount: "+amount+", Description: "+description+", Start Date: "+startDate+", End date: "+endDate+"Payment: "+payment.getName());
				}
				
				@Override
				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("DEDUCTION:\tAmount: "+amount+", description: "+description+", start date: "+start+", end date: "+end+", payment type: "+deduction.getType()+", payment name: "+deduction.getName());
				}
				
				@Override
				public void addData(String name, ITimedVariable<?> data) {
					System.out.println("DATA:\t"+name+", "+data.getValue(data.getPeriod()));
				}
				
				
				@Override
				public void setTotalDeduction(Double totalDeduction) {
					System.out.println("Total deduction:\t"+totalDeduction);
				}
				@Override
				public void setTotalLiquid(Double totalLiquid) {
					System.out.println("Total liquid:\t"+totalLiquid);
				}
				@Override
				public void setIssueDate(Date issueDate) {
					System.out.println("Issue date:\t"+issueDate);
				}
				
				@Override
				public void setRemuneration(Double remuneration) {
					System.out.println("Remuneration:\t"+remuneration);
				}
				@Override
				public void setProExtBase(Double extraPayProration) {
					System.out.println("Extra pro:\t"+extraPayProration);
				}
				
				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("COST:\t"+amount+", "+description+", "+start+", "+end+", cost: "+cost.getType());
					
				}
				@Override
				public void setTotalSS(Double socialSecurityContributions) {
					System.out.println("Total SS:\t"+socialSecurityContributions);
				}
				@Override
				public void setTotalEnterprise(Double totalEnterprise) {
					System.out.println("Total enterprise:\t"+totalEnterprise);
				}
			});
		}
	}
	
	@Test
	//@Ignore
	public void testAplifisa3() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("aplifisa/1-Nominas otra asesoria capital en20.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
	
			});
		}
	}
	
	@Test
	//@Ignore
	public void testAplifisa2000() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("aplifisa/nominas 2020.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
	
			});
		}
	}
	
	
	@Test
	//@Ignore
	public void testAplifisaOmar() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("aplifisa/nominaOmar.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				@Override
				public void setEnterpriseName(String enterpriseName) {
					System.out.println(enterpriseName);
				}
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println(employeeName);
					
				}
				
				
				@Override
				public void setEnterpriseAddress(String enterpriseAddress) {
					System.out.println(enterpriseAddress);
					assertEquals("CL MADERAS, 00", enterpriseAddress);
				}
				@Override
				public void setEmployeeDocument(String employeeDocument) {
					System.out.println(employeeDocument);
					assertEquals("23850806J", employeeDocument);
				}
				
				
				@Override
				public void setEnterpriseCity(String enterpriseCity) {
					System.out.println(enterpriseCity);
					assertEquals("VALENCIA", enterpriseCity);
				}
				@Override
				public void setSocialSecurityNumber(String socialSecurityNumber) {
					System.out.println(socialSecurityNumber);
					assertEquals("461119716005", socialSecurityNumber);
				}
				
				
				@Override
				public void setEnterpriseDocument(String enterpriseDocument) {
					System.out.println(enterpriseDocument);
					assertEquals("B96615521", enterpriseDocument);
				}
				@Override
				public void setCategory(String category) {
					System.out.println(category);
					assertEquals("APRENDIZ", category);
				}
				
				
				@Override
				public void setCcc(String ccc) {
					System.out.println(ccc);
					assertEquals("46109238168", ccc);
				}
				@Override
				public void setQuoteGroup(String quoteGroup) {
					System.out.println(quoteGroup);
					assertEquals("10", quoteGroup);
				}
				@Override
				public void setSeniorityDate(Date seniorityDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(seniorityDate);
					System.out.println(seniorityDate);
					assertTrue(calendar.get(Calendar.DAY_OF_MONTH)==7 && calendar.get(Calendar.MONTH)==8 && calendar.get(Calendar.YEAR)==2020);
				}
				
				
				@Override
				public void setStartDate(Date startDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(startDate);
					System.out.println(startDate);
					assertTrue(calendar.get(Calendar.DAY_OF_MONTH)==1 && calendar.get(Calendar.MONTH)==9 && calendar.get(Calendar.YEAR)==2020);
				}
				@Override
				public void setEndDate(Date endDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					System.out.println(endDate);
					assertTrue(calendar.get(Calendar.DAY_OF_MONTH)==31 && calendar.get(Calendar.MONTH)==9 && calendar.get(Calendar.YEAR)==2020);
				}
				
				
				
				@Override
				public void setTimeUnits(Integer timeUnits) {
					System.out.println(timeUnits);
					assertEquals((Integer)30, timeUnits);
				}
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					System.out.println("PAYMENT:\tAmount: "+amount+", Description: "+description+", Start Date: "+startDate+", End date: "+endDate+"Payment: "+payment.getName());
					switch (description) {
					case "SALARIO BASE":
						break;
					case "P.P. PAGA EXTRA":
						break;
					case "A CUENTA CONVENIO":
						break;
					default:
						System.err.println(description);
						fail("Unrecognized concept");
				}
				HashSet<Double> devengos=new HashSet<Double>(Arrays.asList(529.49,113.25,188.5));
				if(!devengos.contains(amount))
					fail("Amount not found");
				}
				
				@Override
				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("DEDUCTION:\tAmount: "+amount+", description: "+description+", start date: "+start+", end date: "+end+", payment type: "+deduction.getType()+", payment name: "+deduction.getName());
					
		
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
						case "No estructurales":
							break;
						default:
							fail("Unrecognized concept: "+description);
					}
					HashSet<Double> deductions = new HashSet<Double>(Arrays.asList(8.49,16.28,24.77,16.62));
					if(!deductions.contains(amount))
						fail("Amount not found: "+amount);
				}
				
				@Override
				public void addData(String name, ITimedVariable<?> data) {
					System.out.println("DATA:\t"+name+", "+data.getValue(data.getPeriod()));
					switch (name) {
					case "BASE_CGC":
						if((Double)data.getValue(data.getPeriod())!=1050) {
							fail("Not catching the base");
						}
						break;
					case "BASE_IRPF":
						if((Double)data.getValue(data.getPeriod())!=831.24) {
							fail("Not catching the base");
						}
						break;
					case "BASE_DESMPL":
						if((Double)data.getValue(data.getPeriod())!=1050) {
							fail("Not catching the base");
						}
						break;
					case "TOTAL_DEVENGADO":
						if((Double)data.getValue(data.getPeriod())!=831.24) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGC_E":
						if((Double)data.getValue(data.getPeriod())!=1050) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGP_E":
						if((Double)data.getValue(data.getPeriod())!=1050) {
							fail("Not catching the base");
						}
						break;
					case "GRUPO_COTIZACION":
						if(!((String)data.getValue(data.getPeriod())).equals("10")) {
							System.out.println((String)data.getValue(data.getPeriod()));
							fail("Wrong tarifa");
						}
						break;
					case "DIAS_NOMINA":
						if(!(((int)data.getValue(data.getPeriod()))==30)) {
							System.out.println((Integer)data.getValue(data.getPeriod()));
							fail("Wrong time units");
						}
						break;
					case "PORCENTAJE_IRPF":
						if((Double)data.getValue(data.getPeriod())!=2.0) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_IT_E":
						if((Double)data.getValue(data.getPeriod())!=4.45) {
							fail("Not catching the percentage");
						}
						break;
					case "IMS_E":
						break;
					default:
						fail("Unrecognized type: "+ name);
					}
					
					
					
				}
				
				
				@Override
				public void setTotalDeduction(Double totalDeduction) {
					System.out.println(totalDeduction);
					assertEquals((Double)41.39, totalDeduction);
				}
				@Override
				public void setTotalLiquid(Double totalLiquid) {
					System.out.println(totalLiquid);
					assertEquals((Double)789.85, totalLiquid);
				}
				@Override
				public void setIssueDate(Date issueDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(issueDate);
					System.out.println(issueDate);
					assertTrue(calendar.get(Calendar.DAY_OF_MONTH)==31 && calendar.get(Calendar.MONTH)==9 && calendar.get(Calendar.YEAR)==2020);
				}
				
				@Override
				public void setRemuneration(Double remuneration) {
					System.out.println(remuneration);
//					assertEquals((Double)967.37, remuneration);
				}
				@Override
				public void setProExtBase(Double extraPayProration) {
					System.out.println(extraPayProration);
					assertEquals((Double)113.25, extraPayProration);
				}
				
				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					System.out.println(amount+", "+description+", "+start+", "+end+", cost: "+cost.getType());
					switch(description) {
						case "Contingencias comunes":
							if(amount!=42.56d) {
								fail("Wrong cost amount");
							}
							break;
						case "Contingencias profesionales":
							if(amount!=5.85d) {
								fail("Wrong cost amount");
							}
							break;
						case "Desempleo":
							if(amount!=57.75d) {
								fail("Wrong cost amount");
							}
							break;
						case "Fogasa":
							if(amount!=3.23) {
								fail("Wrong cost amount");
							}
							break;
						case "IMS_E":
							break;
						default:
							fail("Unrecognized cost: "+description);
					}
					
				}
				@Override
				public void setTotalSS(Double socialSecurityContributions) {
					System.out.println(socialSecurityContributions);
					assertEquals((Double)24.77, socialSecurityContributions);
				}
				@Override
				public void setTotalEnterprise(Double totalEnterprise) {
					System.out.println(totalEnterprise);
					assertEquals((Double)940.63, totalEnterprise);
				}
			});
		}
	}
	
	
	@Test
	//@Ignore
	public void testNPE() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("aplifisa/nominaNullPointer.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				@Override
				public void setEnterpriseName(String enterpriseName) {
					System.out.println(enterpriseName);
				}
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println(employeeName);
					
				}
				
				
				@Override
				public void setEnterpriseAddress(String enterpriseAddress) {
					System.out.println(enterpriseAddress);
					assertEquals(",", enterpriseAddress);
				}
				@Override
				public void setEmployeeDocument(String employeeDocument) {
					System.out.println(employeeDocument);
					assertEquals("49466959P", employeeDocument);
				}
				
				
				@Override
				public void setEnterpriseCity(String enterpriseCity) {
					System.out.println(enterpriseCity);
					assertEquals("XIRIVELLA", enterpriseCity);
				}
				@Override
				public void setSocialSecurityNumber(String socialSecurityNumber) {
					System.out.println(socialSecurityNumber);
					assertEquals("121019776055", socialSecurityNumber);
				}
				
				
				@Override
				public void setEnterpriseDocument(String enterpriseDocument) {
					System.out.println(enterpriseDocument);
					assertEquals("B55733166", enterpriseDocument);
				}
				@Override
				public void setCategory(String category) {
					System.out.println(category);
					assertEquals("AYUDANTE CAMARERO", category);
				}
				
				
				@Override
				public void setCcc(String ccc) {
					System.out.println(ccc);
					assertEquals("46153399339", ccc);
				}
				@Override
				public void setQuoteGroup(String quoteGroup) {
					System.out.println(quoteGroup);
					assertEquals("10", quoteGroup);
				}
				@Override
				public void setSeniorityDate(Date seniorityDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(seniorityDate);
					System.out.println(seniorityDate);
					assertTrue(calendar.get(Calendar.DAY_OF_MONTH)==28 && calendar.get(Calendar.MONTH)==8 && calendar.get(Calendar.YEAR)==2020);
				}
				
				
				@Override
				public void setStartDate(Date startDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(startDate);
					System.out.println(startDate);
					assertTrue(calendar.get(Calendar.DAY_OF_MONTH)==1 && calendar.get(Calendar.MONTH)==9 && calendar.get(Calendar.YEAR)==2020);
				}
				@Override
				public void setEndDate(Date endDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(endDate);
					System.out.println(endDate);
					assertTrue(calendar.get(Calendar.DAY_OF_MONTH)==31 && calendar.get(Calendar.MONTH)==9 && calendar.get(Calendar.YEAR)==2020);
				}
				
				
				
				@Override
				public void setTimeUnits(Integer timeUnits) {
					System.out.println(timeUnits);
					assertEquals((Integer)30, timeUnits);
				}
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					System.out.println("PAYMENT:\tAmount: "+amount+", Description: "+description+", Start Date: "+startDate+", End date: "+endDate+"Payment: "+payment.getName());
					switch (description) {
					case "SALARIO BASE":
						break;
					case "PLUS DE TRANSPORTE":
						break;
					case "H.EXTRA ORDINARIAS":
						break;
					case "MANUTENCION":
						break;
					case "P.P. PAGA EXTRA":
						break;
					case "ENF. COMUN 23-26 21-":
						break;
					case "COMPLEMENTO I.T.":
						break;
					default:
						System.err.println(description);
						fail("Unrecognized concept");
				}
				HashSet<Double> devengos=new HashSet<Double>(Arrays.asList(899.38,36.11,33.44,31.88,224.85,100.15,23.84));
				if(!devengos.contains(amount))
					fail("Amount not found");
				}
				
				@Override
				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("DEDUCTION:\tAmount: "+amount+", description: "+description+", start date: "+start+", end date: "+end+", payment type: "+deduction.getType()+", payment name: "+deduction.getName());
					
		
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
						case "No estructurales":
							break;
						case "Embargo":
							break;
						default:
							fail("Unrecognized concept: "+description);
					}
					HashSet<Double> deductions = new HashSet<Double>(Arrays.asList(67.24,23.43,1.47,1.57,1.57,26.36,83.87));
					if(!deductions.contains(amount))
						fail("Amount not found: "+amount);
				}
				
				@Override
				public void addData(String name, ITimedVariable<?> data) {
					System.out.println("DATA:\t"+name+", "+data.getValue(data.getPeriod()));
					switch (name) {
					case "BASE_CGC":
						if((Double)data.getValue(data.getPeriod())!=1430.67) {
							fail("Not catching the base");
						}
						break;
					case "DESMPL":
						if((Double)data.getValue(data.getPeriod())!=1464.11) {
							fail("Not catching the base");
						}
						break;
					case "FP":
						if((Double)data.getValue(data.getPeriod())!=1464.11) {
							fail("Not catching the base");
						}
						break;
					case "BASE_NO_ESTRUCTURALES":
						if((Double)data.getValue(data.getPeriod())!=33.44) {
							fail("Not catching the base");
						}
						break;
					case "BASE_IRPF":
						if((Double)data.getValue(data.getPeriod())!=1317.77) {
							fail("Not catching the base");
						}
						break;
					case "BASE_DESMPL":
						if((Double)data.getValue(data.getPeriod())!=1464.11) {
							fail("Not catching the base");
						}
						break;
					case "BASE_FP":
						if((Double)data.getValue(data.getPeriod())!=1464.11) {
							fail("Not catching the base");
						}
						break;
					case "TOTAL_DEVENGADO":
						if((Double)data.getValue(data.getPeriod())!=1349.65) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGC_E":
						if((Double)data.getValue(data.getPeriod())!=1430.67) {
							fail("Not catching the base");
						}
						break;
					case "BASE_CGP_E":
						if((Double)data.getValue(data.getPeriod())!=1464.11) {
							fail("Not catching the base");
						}
						break;
					case "BASE_NESTR":
						if((Double)data.getValue(data.getPeriod())!=33.44) {
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
					case "TC2":
						if(!((String)data.getValue(data.getPeriod())).equals("200")) {
							
							fail("Wrong cod ct");
						}
						break;
					case "GRUPO_COTIZACION":
						if(!((String)data.getValue(data.getPeriod())).equals("10")) {
							System.out.println((String)data.getValue(data.getPeriod()));
							fail("Wrong tarifa");
						}
						break;
					case "DIAS_NOMINA":
						if(!(((int)data.getValue(data.getPeriod()))==30)) {
							System.out.println((Integer)data.getValue(data.getPeriod()));
							fail("Wrong time units");
						}
						break;
					case "PORCENTAJE_CGC":
						if((Double)data.getValue(data.getPeriod())!=4.7) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_FP":
						if((Double)data.getValue(data.getPeriod())!=0.1) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_DESMPL":
						if((Double)data.getValue(data.getPeriod())!=1.6) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_IRPF":
						if((Double)data.getValue(data.getPeriod())!=2.0) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_CGC_E":
						if((Double)data.getValue(data.getPeriod())!=23.6) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_IT_E":
						if((Double)data.getValue(data.getPeriod())!=1.5) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_CGP_E":
						if((Double)data.getValue(data.getPeriod())!=1.5) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_DESMPL_E":
						if((Double)data.getValue(data.getPeriod())!=6.7) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_FP_E":
						if((Double)data.getValue(data.getPeriod())!=.6) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_FOGASA_E":
						if((Double)data.getValue(data.getPeriod())!=.2) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_FUERZA_MAYOR":
						if((Double)data.getValue(data.getPeriod())!=2) {
							fail("Not catching the percentage");
						}
						break;
					case "PORCENTAJE_NO_ESTRUCTURALES":
						if((Double)data.getValue(data.getPeriod())!=4.7) {
							fail("Not catching the percentage");
						}
						break;
					case "HORAS_EXTRAS":
						if((Double)data.getValue(data.getPeriod())!=33.44) {
							fail("Not catching the percentage");
						}
						break;
					case "IMS_E":
						break;
					default:
						fail("Unrecognized type: "+ name);
					}
					
					
					
				}
				
				
				@Override
				public void setTotalDeduction(Double totalDeduction) {
					System.out.println(totalDeduction);
					assertEquals((Double)203.94, totalDeduction);
				}
				@Override
				public void setTotalLiquid(Double totalLiquid) {
					System.out.println(totalLiquid);
					assertEquals((Double)1145.71, totalLiquid);
				}
				@Override
				public void setIssueDate(Date issueDate) {
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(issueDate);
					System.out.println(issueDate);
					assertTrue(calendar.get(Calendar.DAY_OF_MONTH)==31 && calendar.get(Calendar.MONTH)==9 && calendar.get(Calendar.YEAR)==2020);
				}
				
				@Override
				public void setRemuneration(Double remuneration) {
					System.out.println(remuneration);
//					assertEquals((Double)967.37, remuneration);
				}
				@Override
				public void setProExtBase(Double extraPayProration) {
					System.out.println(extraPayProration);
					assertEquals((Double)224.85, extraPayProration);
				}
				
				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					System.out.println(amount+", "+description+", "+start+", "+end+", cost: "+cost.getType());
					switch(description) {
						case "Contingencias comunes":
							if(amount!=337.64d) {
								fail("Wrong cost amount");
							}
							break;
						case "Contingencias profesionales":
							if(amount!=21.95d) {
								fail("Wrong cost amount");
							}
							break;
						case "Desempleo":
							if(amount!=98.09d) {
								fail("Wrong cost amount");
							}
							break;
						case "Formación Profesional":
							if(amount!=8.78) {
								fail("Wrong cost amount");
							}
							break;
						case "Fogasa":
							if(amount!=2.93) {
								fail("Wrong cost amount");
							}
							break;
						case "Fuerza mayor o estructurales":
							if(amount!=7.89) {
								fail("Wrong cost amount");
							}
							break;
						case "IMS_E":
							break;
						default:
							fail("Unrecognized cost: "+description);
					}
					
				}
				@Override
				public void setTotalSS(Double socialSecurityContributions) {
					System.out.println(socialSecurityContributions);
					assertEquals((Double)93.71, socialSecurityContributions);
				}
				@Override
				public void setTotalEnterprise(Double totalEnterprise) {
					System.out.println(totalEnterprise);
					assertEquals((Double)1826.93, totalEnterprise);
				}
			});
		}
	}
	
	@Test
	//@Ignore
	public void testAplifisaSettle() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("aplifisa/nomrec.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				@Override
				public void setEnterpriseName(String enterpriseName) {
					System.out.println("Ent. name:\t"+enterpriseName);
				}
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println("Emp. name:\t"+employeeName);
					
				}
				
				
				@Override
				public void setEnterpriseAddress(String enterpriseAddress) {
					System.out.println("Ent. address:\t"+enterpriseAddress);
				}
				@Override
				public void setEmployeeDocument(String employeeDocument) {
					System.out.println("Emp. doc:\t"+employeeDocument);
				}
				
				
				@Override
				public void setEnterpriseCity(String enterpriseCity) {
					System.out.println("Ent. city:\t"+enterpriseCity);
				}
				@Override
				public void setSocialSecurityNumber(String socialSecurityNumber) {
					System.out.println("NSS:\t"+socialSecurityNumber);
				}
				
				
				@Override
				public void setEnterpriseDocument(String enterpriseDocument) {
					System.out.println("Ent. cod:\t"+enterpriseDocument);
				}
				@Override
				public void setCategory(String category) {
					System.out.println("Category:\t"+category);
				}
				
				
				@Override
				public void setCcc(String ccc) {
					System.out.println("CCC:\t"+ccc);
				}
				@Override
				public void setQuoteGroup(String quoteGroup) {
					System.out.println("Quote group:\t"+quoteGroup);
				}
				@Override
				public void setSeniorityDate(Date seniorityDate) {
					System.out.println("Seniority date:\t"+seniorityDate);
				}
				
				
				@Override
				public void setStartDate(Date startDate) {
					System.out.println("Start date:\t"+startDate);
				}
				@Override
				public void setEndDate(Date endDate) {
					System.out.println("End. name:\t"+endDate);
				}
				
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					System.out.println("PAYMENT:\tAmount: "+amount+", Description: "+description+", Start Date: "+startDate+", End date: "+endDate+"Payment: "+payment.getName());
				}
				
				@Override
				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("DEDUCTION:\tAmount: "+amount+", description: "+description+", start date: "+start+", end date: "+end+", payment type: "+deduction.getType()+", payment name: "+deduction.getName());
				}
				
				@Override
				public void addData(String name, ITimedVariable<?> data) {
					System.out.println("DATA:\t"+name+", "+data.getValue(data.getPeriod()));
				}
				
				
				@Override
				public void setTotalDeduction(Double totalDeduction) {
					System.out.println("Total deduction:\t"+totalDeduction);
				}
				@Override
				public void setTotalLiquid(Double totalLiquid) {
					System.out.println("Total liquid:\t"+totalLiquid);
				}
				@Override
				public void setIssueDate(Date issueDate) {
					System.out.println("Issue date:\t"+issueDate);
				}
				
				@Override
				public void setRemuneration(Double remuneration) {
					System.out.println("Remuneration:\t"+remuneration);
				}
				@Override
				public void setProExtBase(Double extraPayProration) {
					System.out.println("Extra pro:\t"+extraPayProration);
				}
				
				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("COST:\t"+amount+", "+description+", "+start+", "+end+", cost: "+cost.getType());
					
				}
				@Override
				public void setTotalSS(Double socialSecurityContributions) {
					System.out.println("Total SS:\t"+socialSecurityContributions);
				}
				@Override
				public void setTotalEnterprise(Double totalEnterprise) {
					System.out.println("Total enterprise:\t"+totalEnterprise);
				}
			});
		}
	}
	
	
	@Test
	//@Ignore
	public void testIRPF() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("aplifisa/nomina_testeo.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				@Override
				public void setEnterpriseName(String enterpriseName) {
					System.out.println("Ent. name:\t"+enterpriseName);
				}
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println("Emp. name:\t"+employeeName);
					
				}
				
				
				@Override
				public void setEnterpriseAddress(String enterpriseAddress) {
					System.out.println("Ent. address:\t"+enterpriseAddress);
				}
				@Override
				public void setEmployeeDocument(String employeeDocument) {
					System.out.println("Emp. doc:\t"+employeeDocument);
				}
				
				
				@Override
				public void setEnterpriseCity(String enterpriseCity) {
					System.out.println("Ent. city:\t"+enterpriseCity);
				}
				@Override
				public void setSocialSecurityNumber(String socialSecurityNumber) {
					System.out.println("NSS:\t"+socialSecurityNumber);
				}
				
				
				@Override
				public void setEnterpriseDocument(String enterpriseDocument) {
					System.out.println("Ent. cod:\t"+enterpriseDocument);
				}
				@Override
				public void setCategory(String category) {
					System.out.println("Category:\t"+category);
				}
				
				
				@Override
				public void setCcc(String ccc) {
					System.out.println("CCC:\t"+ccc);
				}
				@Override
				public void setQuoteGroup(String quoteGroup) {
					System.out.println("Quote group:\t"+quoteGroup);
				}
				@Override
				public void setSeniorityDate(Date seniorityDate) {
					System.out.println("Seniority date:\t"+seniorityDate);
				}
				
				
				@Override
				public void setStartDate(Date startDate) {
					System.out.println("Start date:\t"+startDate);
				}
				@Override
				public void setEndDate(Date endDate) {
					System.out.println("End. name:\t"+endDate);
				}
				
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					System.out.println("PAYMENT:\tAmount: "+amount+", Description: "+description+", Start Date: "+startDate+", End date: "+endDate+"Payment: "+payment.getName());
				}
				
				@Override
				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("DEDUCTION:\tAmount: "+amount+", description: "+description+", start date: "+start+", end date: "+end+", payment type: "+deduction.getType()+", payment name: "+deduction.getName());
				}
				
				@Override
				public void addData(String name, ITimedVariable<?> data) {
					System.out.println("DATA:\t"+name+", "+data.getValue(data.getPeriod()));
				}
				
				
				@Override
				public void setTotalDeduction(Double totalDeduction) {
					System.out.println("Total deduction:\t"+totalDeduction);
				}
				@Override
				public void setTotalLiquid(Double totalLiquid) {
					System.out.println("Total liquid:\t"+totalLiquid);
				}
				@Override
				public void setIssueDate(Date issueDate) {
					System.out.println("Issue date:\t"+issueDate);
				}
				
				@Override
				public void setRemuneration(Double remuneration) {
					System.out.println("Remuneration:\t"+remuneration);
				}
				@Override
				public void setProExtBase(Double extraPayProration) {
					System.out.println("Extra pro:\t"+extraPayProration);
				}
				
				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("COST:\t"+amount+", "+description+", "+start+", "+end+", cost: "+cost.getType());
					
				}
				@Override
				public void setTotalSS(Double socialSecurityContributions) {
					System.out.println("Total SS:\t"+socialSecurityContributions);
				}
				@Override
				public void setTotalEnterprise(Double totalEnterprise) {
					System.out.println("Total enterprise:\t"+totalEnterprise);
				}
			});
		}
	}
	
	

	@Test
	@Ignore
	public void testAltai() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("aplifisa/EXTRA DIC.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println(employeeName);
					
				}
			});
		}
	}

	@Test
	//@Ignore
	public void test260() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("nomina260.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				@Override
				public void setEnterpriseName(String enterpriseName) {
					System.out.println("Ent. name:\t"+enterpriseName);
				}
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println("Emp. name:\t"+employeeName);
					
				}
				
				
				@Override
				public void setEnterpriseAddress(String enterpriseAddress) {
					System.out.println("Ent. address:\t"+enterpriseAddress);
				}
				@Override
				public void setEmployeeDocument(String employeeDocument) {
					System.out.println("Emp. doc:\t"+employeeDocument);
				}
				
				
				@Override
				public void setEnterpriseCity(String enterpriseCity) {
					System.out.println("Ent. city:\t"+enterpriseCity);
				}
				@Override
				public void setSocialSecurityNumber(String socialSecurityNumber) {
					System.out.println("NSS:\t"+socialSecurityNumber);
				}
				
				
				@Override
				public void setEnterpriseDocument(String enterpriseDocument) {
					System.out.println("Ent. cod:\t"+enterpriseDocument);
				}
				@Override
				public void setCategory(String category) {
					System.out.println("Category:\t"+category);
				}
				
				
				@Override
				public void setCcc(String ccc) {
					System.out.println("CCC:\t"+ccc);
				}
				@Override
				public void setQuoteGroup(String quoteGroup) {
					System.out.println("Quote group:\t"+quoteGroup);
				}
				@Override
				public void setSeniorityDate(Date seniorityDate) {
					System.out.println("Seniority date:\t"+seniorityDate);
				}
				
				
				@Override
				public void setStartDate(Date startDate) {
					System.out.println("Start date:\t"+startDate);
				}
				@Override
				public void setEndDate(Date endDate) {
					System.out.println("End. name:\t"+endDate);
				}
				
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description, Date startDate,
						Date endDate, IPayment payment, Map<String, ITimedVariable<?>> context) {
					System.out.println("PAYMENT:\tAmount: "+amount+", Description: "+description+", Start Date: "+startDate+", End date: "+endDate+"Payment: "+payment.getName());
				}
				
				@Override
				public void addDeduction(Double amount, String description, Date start, Date end, IDeduction deduction,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("DEDUCTION:\tAmount: "+amount+", description: "+description+", start date: "+start+", end date: "+end+", payment type: "+deduction.getType()+", payment name: "+deduction.getName());
				}
				
				@Override
				public void addData(String name, ITimedVariable<?> data) {
					System.out.println("DATA:\t"+name+", "+data.getValue(data.getPeriod()));
				}
				
				
				@Override
				public void setTotalDeduction(Double totalDeduction) {
					System.out.println("Total deduction:\t"+totalDeduction);
				}
				@Override
				public void setTotalLiquid(Double totalLiquid) {
					System.out.println("Total liquid:\t"+totalLiquid);
				}
				@Override
				public void setIssueDate(Date issueDate) {
					System.out.println("Issue date:\t"+issueDate);
				}
				
				@Override
				public void setRemuneration(Double remuneration) {
					System.out.println("Remuneration:\t"+remuneration);
				}
				@Override
				public void setProExtBase(Double extraPayProration) {
					System.out.println("Extra pro:\t"+extraPayProration);
				}
				
				@Override
				public void addCost(Double amount, String description, Date start, Date end, IDeduction cost,
						Map<String, ITimedVariable<?>> context) {
					System.out.println("COST:\t"+amount+", "+description+", "+start+", "+end+", cost: "+cost.getType());
					
				}
				@Override
				public void setTotalSS(Double socialSecurityContributions) {
					System.out.println("Total SS:\t"+socialSecurityContributions);
				}
				@Override
				public void setTotalEnterprise(Double totalEnterprise) {
					System.out.println("Total enterprise:\t"+totalEnterprise);
				}
			});
		}
	}
	
	
	
}