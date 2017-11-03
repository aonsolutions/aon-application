package com.esferalia.aon.file.payroll.contract.pdf.enterpriseCertificate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.SuspensionCause;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.COTIZACIONREATYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CUENTACOTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CertificadoEmpresa;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.PERIODODISTRIBUCIONJORNADASTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE;
import com.lowagie.text.pdf.PdfReader;

public class EnterpriseCertificateSea extends EnterpriseCertificate {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String ENTERPRISE_CERTIFICATE_NAME = "certiEmpresaSea";
	
	public EnterpriseCertificateSea(){
		super.documentName = ENTERPRISE_CERTIFICATE_NAME;
	}
	
	@Override
	public void loadPdfFieldValues(ContractCode code, Contract contract, List<CertificadoEmpresa> certificadoList) throws UnsupportedContractDocumentException{
		
		
		super.loadPdfFieldValues(code, contract, certificadoList);
		
		try {
			PdfReader reader = new PdfReader(getEnterpriseCertificateUrl(documentName+".pdf"));
			readPdfFields(reader);
			super.loadPdfCommonFields(contract);

			SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", getLocale());
			SimpleDateFormat sepeDateFormatter = new SimpleDateFormat("yyyyMMdd", getLocale());
				
			if (certificadoList.size()>0 && certificadoList.get(0)!=null && certificadoList.get(0).getCuentaCotizacion().size()>0 ){
				CUENTACOTIZACIONTYPE ccc = certificadoList.get(0).getCuentaCotizacion().get(0);
				
				if(ccc.getDatosRepresentante()!=null){
					String dirStaffName = ccc.getDatosRepresentante().getApellido1() + " ";
					if(ccc.getDatosRepresentante().getApellido2()!=null){
						dirStaffName += ccc.getDatosRepresentante().getApellido2() + ", ";
					}
					dirStaffName += ccc.getDatosRepresentante().getNombre();
					setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_DIR_STAFF_NAME.getValue(),dirStaffName);
					setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_DIR_STAFF_CHARGE.getValue(),ccc.getDatosRepresentante().getCargo());
				}
				
				
				// ENTERPRISE
				setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
//				if(contract.getEnterpriseCCC()!=null && PayrollUtils.getInstance().getRegimeCode(contract.getEnterpriseCCC())!=null){
//					setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_REGIME_CODE.getValue(),PayrollUtils.getInstance().getRegimeCode(contract.getEnterpriseCCC()));
//				}
//				setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_REGIME_NAME.getValue(),contract.getEnterpriseCCC().getActivity().getType().getName(getLocale()));
				setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_CCC.getValue(),contract.getEnterpriseCCC().getCcc());
				setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_SOCIAL_ADDRESS.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
				setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_CITY.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getCity());
				try {	
					setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_ZIP.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip());
				} catch (StringIndexOutOfBoundsException aie) {
					// do nothing
				} catch (NullPointerException npe) {
					// do nothing
				}
				setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_PROVINCE.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getProvince());
				setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_CNAE_CODE.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode());
				setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_CNAE_NAME.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getTitle());
				setPdfFieldValue(EnterpriseCertificateSeaField.ENTERPRISE_WORKPLACE_ADDRESS.getValue(),contract.getWorkPlace().getAddress().getFullAddress());

				TRABAJADORTYPE trabajador = ccc.getDatosTrabajador().get(0);

				
				
				// EMPLOYEE
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_FULLNAME.getValue(),contract.getPerson().getFullName());
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_DOCUMENT.getValue(),trabajador.getDNINIE());
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_SS_NUMBER.getValue(),trabajador.getNumSS());
//				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_QUOTE_GROUP.getValue(),trabajador.getGrupoCotizacion());
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_CONTRACT_TIPE.getValue(),trabajador.getTipoContrato());
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_DURATION.getValue(),getIntegerFormat(trabajador.getDuracionContrato()));
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_PROFESSION_CODE.getValue(),trabajador.getCodProfesion());
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_PROFESSION_NAME.getValue(),"");
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_PROFESSION_NAME_MORE.getValue(),"");
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_PUBLIC_CHARGE.getValue(),trabajador.getCargoPublicoSindical());
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_PUBLIC_CHARGE_DURATION.getValue(),trabajador.getDedicacionCompleta());
				if(trabajador.getFechaAltaEmpresa()!=null){
					setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_START_DATE.getValue(),
							dateFormatter.format(sepeDateFormatter.parse(trabajador.getFechaAltaEmpresa())));
				}
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_SUSPENSION_CODE.getValue(),trabajador.getCodCausaSuspension());
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_SUSPENSION_NAME.getValue(),"");
				if(trabajador.getCodCausaSuspension()!=null){
					for(SuspensionCause cause: SuspensionCause.values()){
						if(Integer.parseInt(cause.getValue()) == Integer.parseInt(trabajador.getCodCausaSuspension())){
							setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_SUSPENSION_NAME_MORE.getValue(),cause.getName(getLocale()));
							break;
						}
					}
				}
				
				if(trabajador.getFechaSuspensionExtincion()!=null){
					setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_END_DATE.getValue(),
							dateFormatter.format(sepeDateFormatter.parse(trabajador.getFechaSuspensionExtincion())));
				}
				if(trabajador.getFechaFinSuspension()!=null){
					setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_SUSPENDION_END_DATE.getValue(),
							dateFormatter.format(sepeDateFormatter.parse(trabajador.getFechaFinSuspension())));
				}
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_ERE_NUMBER.getValue(),trabajador.getERE());
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TIME_REDUCTION_PERCENT.getValue(),trabajador.getPorcentualReduccionERE());
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_CARE_PERCENT.getValue(),trabajador.getPorcentualDedicacion());
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_SALARY_TRAMITATION_DAY_NUMBER.getValue(),trabajador.getDiasSalarioTramitacion());
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_SALARY_TRAMITATION_FROM.getValue(),trabajador.getFechaDesdePeriodoSalarios());
				setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_SALARY_TRAMITATION_TO.getValue(),trabajador.getFechaHastaPeriodoSalarios());
			
				if(trabajador.getDistribucionJornadas()!=null && trabajador.getDistribucionJornadas().getPeriodo()!=null){
					PERIODODISTRIBUCIONJORNADASTYPE periodo = null;
					
					if(trabajador.getDistribucionJornadas().getPeriodo().size()>0){
						periodo = trabajador.getDistribucionJornadas().getPeriodo().get(0);
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD1_TYPE.getValue(),periodo.getTipoDistribucion().equals("1")?"R":"I");
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD1_DAYS.getValue(),
								getIntegerFormat(periodo.getNumeroDiasTrabajadosPorSemanaOPeriodo()));
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD1_FROM.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaInicioPeriodo())));
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD1_TO.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaFinPeriodo())));
					}
					
					if(trabajador.getDistribucionJornadas().getPeriodo().size()>1){
						periodo = trabajador.getDistribucionJornadas().getPeriodo().get(1);
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD2_TYPE.getValue(),periodo.getTipoDistribucion().equals("1")?"R":"I");
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD2_DAYS.getValue(),
								getIntegerFormat(periodo.getNumeroDiasTrabajadosPorSemanaOPeriodo()));
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD2_FROM.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaInicioPeriodo())));
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD2_TO.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaFinPeriodo())));
					}
					
					if(trabajador.getDistribucionJornadas().getPeriodo().size()>2){
						periodo = trabajador.getDistribucionJornadas().getPeriodo().get(2);
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD3_TYPE.getValue(),periodo.getTipoDistribucion().equals("1")?"R":"I");
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD3_DAYS.getValue(),
								getIntegerFormat(periodo.getNumeroDiasTrabajadosPorSemanaOPeriodo()));
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD3_FROM.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaInicioPeriodo())));
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD3_TO.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaFinPeriodo())));
					}
					
					if(trabajador.getDistribucionJornadas().getPeriodo().size()>3){
						periodo = trabajador.getDistribucionJornadas().getPeriodo().get(3);
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD4_TYPE.getValue(),periodo.getTipoDistribucion().equals("1")?"R":"I");
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD4_DAYS.getValue(),
								getIntegerFormat(periodo.getNumeroDiasTrabajadosPorSemanaOPeriodo()));
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD4_FROM.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaInicioPeriodo())));
						setPdfFieldValue(EnterpriseCertificateSeaField.EMPLOYEE_TP_PERIOD4_TO.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaFinPeriodo())));
					}
				}
				
				if(trabajador.getDatosCotizacion()!=null){
					COTIZACIONREATYPE cotizacion = null;
					
					// COTIZACIONES
					
					fillDatosCotizacion(cotizacion, trabajador, 0,
							EnterpriseCertificateSeaField.ROW1_YEAR,
							EnterpriseCertificateSeaField.ROW1_MONTH,
							EnterpriseCertificateSeaField.ROW1_QUOTE_GROUP,
							EnterpriseCertificateSeaField.ROW1_QUOTE_DAYS,
							EnterpriseCertificateSeaField.ROW1_JOURNAL_DAYS,
							EnterpriseCertificateSeaField.ROW1_BASE_UNEMPLOYMENT,
							EnterpriseCertificateSeaField.ROW1_REMARKS);
					
					fillDatosCotizacion(cotizacion, trabajador, 1,
							EnterpriseCertificateSeaField.ROW2_YEAR,
							EnterpriseCertificateSeaField.ROW2_MONTH,
							EnterpriseCertificateSeaField.ROW2_QUOTE_GROUP,
							EnterpriseCertificateSeaField.ROW2_QUOTE_DAYS,
							EnterpriseCertificateSeaField.ROW2_JOURNAL_DAYS,
							EnterpriseCertificateSeaField.ROW2_BASE_UNEMPLOYMENT,
							EnterpriseCertificateSeaField.ROW2_REMARKS);
					
					fillDatosCotizacion(cotizacion, trabajador, 2,
							EnterpriseCertificateSeaField.ROW3_YEAR,
							EnterpriseCertificateSeaField.ROW3_MONTH,
							EnterpriseCertificateSeaField.ROW3_QUOTE_GROUP,
							EnterpriseCertificateSeaField.ROW3_QUOTE_DAYS,
							EnterpriseCertificateSeaField.ROW3_JOURNAL_DAYS,
							EnterpriseCertificateSeaField.ROW3_BASE_UNEMPLOYMENT,
							EnterpriseCertificateSeaField.ROW3_REMARKS);
					
					fillDatosCotizacion(cotizacion, trabajador, 3,
							EnterpriseCertificateSeaField.ROW4_YEAR,
							EnterpriseCertificateSeaField.ROW4_MONTH,
							EnterpriseCertificateSeaField.ROW4_QUOTE_GROUP,
							EnterpriseCertificateSeaField.ROW4_QUOTE_DAYS,
							EnterpriseCertificateSeaField.ROW4_JOURNAL_DAYS,
							EnterpriseCertificateSeaField.ROW4_BASE_UNEMPLOYMENT,
							EnterpriseCertificateSeaField.ROW4_REMARKS);
					
					fillDatosCotizacion(cotizacion, trabajador, 4,
							EnterpriseCertificateSeaField.ROW5_YEAR,
							EnterpriseCertificateSeaField.ROW5_MONTH,
							EnterpriseCertificateSeaField.ROW5_QUOTE_GROUP,
							EnterpriseCertificateSeaField.ROW5_QUOTE_DAYS,
							EnterpriseCertificateSeaField.ROW5_JOURNAL_DAYS,
							EnterpriseCertificateSeaField.ROW5_BASE_UNEMPLOYMENT,
							EnterpriseCertificateSeaField.ROW5_REMARKS);
					
					fillDatosCotizacion(cotizacion, trabajador, 5,
							EnterpriseCertificateSeaField.ROW6_YEAR,
							EnterpriseCertificateSeaField.ROW6_MONTH,
							EnterpriseCertificateSeaField.ROW6_QUOTE_GROUP,
							EnterpriseCertificateSeaField.ROW6_QUOTE_DAYS,
							EnterpriseCertificateSeaField.ROW6_JOURNAL_DAYS,
							EnterpriseCertificateSeaField.ROW6_BASE_UNEMPLOYMENT,
							EnterpriseCertificateSeaField.ROW6_REMARKS);
					
					fillDatosCotizacion(cotizacion, trabajador, 6,
							EnterpriseCertificateSeaField.ROW7_YEAR,
							EnterpriseCertificateSeaField.ROW7_MONTH,
							EnterpriseCertificateSeaField.ROW7_QUOTE_GROUP,
							EnterpriseCertificateSeaField.ROW7_QUOTE_DAYS,
							EnterpriseCertificateSeaField.ROW7_JOURNAL_DAYS,
							EnterpriseCertificateSeaField.ROW7_BASE_UNEMPLOYMENT,
							EnterpriseCertificateSeaField.ROW7_REMARKS);
					
					fillDatosCotizacion(cotizacion, trabajador, 7,
							EnterpriseCertificateSeaField.ROW8_YEAR,
							EnterpriseCertificateSeaField.ROW8_MONTH,
							EnterpriseCertificateSeaField.ROW8_QUOTE_GROUP,
							EnterpriseCertificateSeaField.ROW8_QUOTE_DAYS,
							EnterpriseCertificateSeaField.ROW8_JOURNAL_DAYS,
							EnterpriseCertificateSeaField.ROW8_BASE_UNEMPLOYMENT,
							EnterpriseCertificateSeaField.ROW8_REMARKS);
					
				}

				if(trabajador.getDatosVacacionesCotizadasREA()!=null){
					setPdfFieldValue(EnterpriseCertificateSeaField.HOLIDAY_QUOTE_DAYS.getValue(),
							getIntegerFormat(trabajador.getDatosVacacionesCotizadasREA().getNumDiasCotizados()));
					setPdfFieldValue(EnterpriseCertificateSeaField.HOLIDAY_JOURNAL_DAYS.getValue(),
							getIntegerFormat(trabajador.getDatosVacacionesCotizadasREA().getNumJornadasCotizadas()));
					setPdfFieldValue(EnterpriseCertificateSeaField.HOLIDAY_BASE_UNEMPLOYMENT.getValue(),
							getDoubleFormat(trabajador.getDatosVacacionesCotizadasREA().getBaseCotizacionDesempleo()));
					setPdfFieldValue(EnterpriseCertificateSeaField.HOLIDAY_REMARKS.getValue(),trabajador.getDatosVacacionesCotizadasREA().getObservaciones());
				}
				
				
				if(trabajador.getDatosCotizacionREA()==null || trabajador.getDatosCotizacionREA().isEmpty()){
					setPdfFieldValue(EnterpriseCertificateSeaField.TOTAL_QUOTE_DAYS.getValue(),"N/D");
					setPdfFieldValue(EnterpriseCertificateSeaField.TOTAL_JOURNAL_DAYS.getValue(),"N/D");
					setPdfFieldValue(EnterpriseCertificateSeaField.TOTAL_BASE_UNEMPLOYMENT.getValue(),"N/D");
					setPdfFieldValue(EnterpriseCertificateSeaField.TOTAL_REMARKS.getValue(),"N/D");
				} else {
					Integer totalQuoteDays = trabajador.getDatosCotizacionREA()
							.stream().map(COTIZACIONREATYPE::getNumDiasCotizados)
							.filter(o -> o!=null)
							.mapToInt(Integer::parseInt).sum();
					Integer totalJournalDays = trabajador.getDatosCotizacionREA()
							.stream().map(COTIZACIONREATYPE::getNumJornadasCotizadas)
							.filter(o -> o!=null)
							.mapToInt(Integer::parseInt).sum();
					Double totalUnemployment = trabajador.getDatosCotizacionREA()
							.stream().map(COTIZACIONREATYPE::getBaseCotizacionDesempleo)
							.filter(o -> o!=null)
							.mapToDouble(Double::parseDouble)
							.map(value -> CommonUtil.round(value/100))
							.sum();
					
					if(trabajador.getDatosVacacionesCotizadasREA()!=null){
						totalQuoteDays += Integer.parseInt(trabajador.getDatosVacacionesCotizadasREA().getNumDiasCotizados());
						totalJournalDays += Integer.parseInt(trabajador.getDatosVacacionesCotizadasREA().getNumDiasCotizados());
						totalUnemployment += Double.parseDouble(trabajador.getDatosVacacionesCotizadasREA().getBaseCotizacionDesempleo())/100;
					}
					
					setPdfFieldValue(EnterpriseCertificateSeaField.TOTAL_QUOTE_DAYS.getValue(),
							String.valueOf(totalQuoteDays));
					setPdfFieldValue(EnterpriseCertificateSeaField.TOTAL_JOURNAL_DAYS.getValue(),
							String.valueOf(totalJournalDays));
					setPdfFieldValue(EnterpriseCertificateSeaField.TOTAL_BASE_UNEMPLOYMENT.getValue(),
							String.valueOf(totalUnemployment));
				}
			}
			
			// FOOTER
			setPdfFieldValue(EnterpriseCertificateSeaField.SIGN_CITY.getValue(),contract.getWorkPlace().getAddress().getCity());
			dateFormatter.applyPattern("dd");
			setPdfFieldValue(EnterpriseCertificateSeaField.SIGN_DAY.getValue(),dateFormatter.format(new Date()));
			dateFormatter.applyPattern("MMMM");
			setPdfFieldValue(EnterpriseCertificateSeaField.SIGN_MONTH.getValue(),dateFormatter.format(new Date()));
			dateFormatter.applyPattern("yy");
			setPdfFieldValue(EnterpriseCertificateSeaField.SIGN_YEAR.getValue(),dateFormatter.format(new Date()));
			setPdfFieldValue(EnterpriseCertificateSeaField.SIGNATURE.getValue(),"");
			
			
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
		} catch (ParseException e) {
			// do nothing
		}
	}
	
	private void fillDatosCotizacion(COTIZACIONREATYPE cotizacion, TRABAJADORTYPE trabajador, int row,
			EnterpriseCertificateSeaField rowYear, EnterpriseCertificateSeaField rowMonth,
			EnterpriseCertificateSeaField rowQuote,
			EnterpriseCertificateSeaField rowQuoteDays, EnterpriseCertificateSeaField rowJournalDays,
			EnterpriseCertificateSeaField rowBaseUnemployment, EnterpriseCertificateSeaField rowRemarks) {
		if (trabajador.getDatosCotizacionREA().size() > row) {
			cotizacion = trabajador.getDatosCotizacionREA().get(row);
			setPdfFieldValue(rowYear.getValue(), cotizacion.getAno());
			setPdfFieldValue(rowMonth.getValue(), cotizacion.getMes());
			setPdfFieldValue(rowQuote.getValue(), cotizacion.getGrupoCotizacion());
			setPdfFieldValue(rowQuoteDays.getValue(), cotizacion.getNumDiasCotizados()!=null?
					getIntegerFormat(cotizacion.getNumDiasCotizados()):null);
			setPdfFieldValue(rowJournalDays.getValue(), cotizacion.getNumJornadasCotizadas()!=null?
					getIntegerFormat(cotizacion.getNumJornadasCotizadas()):null);
			setPdfFieldValue(rowBaseUnemployment.getValue(),
					getDoubleFormat(cotizacion.getBaseCotizacionDesempleo()));
			setPdfFieldValue(rowRemarks.getValue(), cotizacion.getObservaciones());
		}
	}
	
	private String getDoubleFormat(String _value){
		Double value = Double.valueOf(_value);
		value = value/100;
		return value.toString();
	}
	
	private String getIntegerFormat(String _value){
		return Integer.valueOf(_value).toString();
	}
	

}
	
	