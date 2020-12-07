package com.esferalia.aon.in.payroll.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public class PdfTest {
	
	
	@Test
	@Ignore
	public void testDsi() throws IOException, UnknownPDFException{
		try (InputStream is = PdfTest.class.getResourceAsStream("dsi_nomina.pdf")){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				
			});
		}
	}
	
	
	
	
	
	
	@Test
	@Ignore
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
					System.out.println(seniorityDate);
					try {
						assertEquals(new SimpleDateFormat("dd-MM-yyyy").parse("01-10-2008"), seniorityDate);
					} catch (ParseException e) {
						fail();
					}
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
					System.out.println(startDate);
					try {
						Date expected=new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2020");
						assertEquals(expected, startDate);
					} catch (ParseException e) {
						fail();
					}
				}
				
				@Override
				public void setEndDate(Date endDate) {
					System.out.println(endDate);
					try {
						Date expected=new SimpleDateFormat("dd-MM-yyyy").parse("31-01-2020");
						assertEquals(expected, endDate);
					} catch (ParseException e) {
						fail();
					}
				}
				
				@Override
				public void setChargeDate(Date chargeDate) {
					System.out.println(chargeDate);
					try {
						Date expected=new SimpleDateFormat("dd-MM-yyyy").parse("31-01-2020");
						assertEquals(expected, chargeDate);
					} catch (ParseException e) {
						fail();
					}
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
							fail("Unrecognized concept");
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
							fail("Unrecognized concept");
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
					System.out.println(issueDate);
					try {
						Date expected=new SimpleDateFormat("dd-MM-yyyy").parse("31-01-2020");
						assertEquals(expected, issueDate);
					} catch (ParseException e) {
						fail();
					}		
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