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
import com.esferalia.aon.payroll.util.PayrollUtils;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.COTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CUENTACOTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CertificadoEmpresa;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.PERIODODISTRIBUCIONJORNADASTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE;
import com.lowagie.text.pdf.PdfReader;

public class EnterpriseCertificate extends AbstractEnterpriseCertificate {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String ENTERPRISE_CERTIFICATE_NAME = "certiEmpresa";
	
	public EnterpriseCertificate(){
		super.documentName = ENTERPRISE_CERTIFICATE_NAME;
	}
	
	public void loadPdfFieldValues(ContractCode code, Contract contract, List<CertificadoEmpresa> certificadoList) throws UnsupportedContractDocumentException{
		
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
					setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_DIR_STAFF_NAME.getValue(),dirStaffName);
					setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_DIR_STAFF_CHARGE.getValue(),ccc.getDatosRepresentante().getCargo());
				}
				
				
				// ENTERPRISE
				setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
				if(contract.getEnterpriseCCC()!=null && PayrollUtils.getInstance().getRegimeCode(contract.getEnterpriseCCC())!=null){
					setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_REGIME_CODE.getValue(),PayrollUtils.getInstance().getRegimeCode(contract.getEnterpriseCCC()));
				}
				setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_REGIME_NAME.getValue(),contract.getEnterpriseCCC().getActivity().getType().getName(getLocale()));
				setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_CCC.getValue(),contract.getEnterpriseCCC().getCcc());
				setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_SOCIAL_ADDRESS.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
				setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_CITY.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getCity());
				try {	
					setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_ZIP.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip());
				} catch (StringIndexOutOfBoundsException aie) {
					// do nothing
				} catch (NullPointerException npe) {
					// do nothing
				}
				setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_PROVINCE.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getProvince());
				setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_CNAE_CODE.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode());
				setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_CNAE_NAME.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getTitle());
				setPdfFieldValue(EnterpriseCertificateField.ENTERPRISE_WORKPLACE_ADDRESS.getValue(),contract.getWorkPlace().getAddress().getFullAddress());

				TRABAJADORTYPE trabajador = ccc.getDatosTrabajador().get(0);

				
				
				// EMPLOYEE
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_FULLNAME.getValue(),contract.getPerson().getFullName());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_DOCUMENT.getValue(),trabajador.getDNINIE());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_SS_NUMBER.getValue(),trabajador.getNumSS());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_QUOTE_GROUP.getValue(),trabajador.getGrupoCotizacion());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_CONTRACT_TIPE.getValue(),trabajador.getTipoContrato());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_DURATION.getValue(),getIntegerFormat(trabajador.getDuracionContrato()));
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_PROFESSION_CODE.getValue(),trabajador.getCodProfesion());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_PROFESSION_NAME.getValue(),"");
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_PROFESSION_NAME_MORE.getValue(),"");
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_PUBLIC_CHARGE.getValue(),trabajador.getCargoPublicoSindical());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_PUBLIC_CHARGE_DURATION.getValue(),trabajador.getDedicacionCompleta());
				if(trabajador.getFechaAltaEmpresa()!=null){
					setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_START_DATE.getValue(),
							dateFormatter.format(sepeDateFormatter.parse(trabajador.getFechaAltaEmpresa())));
				}
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_SUSPENSION_CODE.getValue(),trabajador.getCodCausaSuspension());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_SUSPENSION_NAME.getValue(),"");
				if(trabajador.getCodCausaSuspension()!=null){
					for(SuspensionCause cause: SuspensionCause.values()){
						if(Integer.parseInt(cause.getValue()) == Integer.parseInt(trabajador.getCodCausaSuspension())){
							setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_SUSPENSION_NAME_MORE.getValue(),cause.getName(getLocale()));
							break;
						}
					}
				}
				
				if(trabajador.getFechaSuspensionExtincion()!=null){
					setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_END_DATE.getValue(),
							dateFormatter.format(sepeDateFormatter.parse(trabajador.getFechaSuspensionExtincion())));
				}
				if(trabajador.getFechaFinSuspension()!=null){
					setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_SUSPENDION_END_DATE.getValue(),
							dateFormatter.format(sepeDateFormatter.parse(trabajador.getFechaFinSuspension())));
				}
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_ERE_NUMBER.getValue(),trabajador.getERE());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TIME_REDUCTION_PERCENT.getValue(),trabajador.getPorcentualReduccionERE());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_CARE_PERCENT.getValue(),trabajador.getPorcentualDedicacion());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_SALARY_TRAMITATION_DAY_NUMBER.getValue(),trabajador.getDiasSalarioTramitacion());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_SALARY_TRAMITATION_FROM.getValue(),trabajador.getFechaDesdePeriodoSalarios());
				setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_SALARY_TRAMITATION_TO.getValue(),trabajador.getFechaHastaPeriodoSalarios());
			
				if(trabajador.getDistribucionJornadas()!=null && trabajador.getDistribucionJornadas().getPeriodo()!=null){
					PERIODODISTRIBUCIONJORNADASTYPE periodo = null;
					
					if(trabajador.getDistribucionJornadas().getPeriodo().size()>0){
						periodo = trabajador.getDistribucionJornadas().getPeriodo().get(0);
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD1_TYPE.getValue(),periodo.getTipoDistribucion().equals("1")?"R":"I");
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD1_DAYS.getValue(),
								getIntegerFormat(periodo.getNumeroDiasTrabajadosPorSemanaOPeriodo()));
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD1_FROM.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaInicioPeriodo())));
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD1_TO.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaFinPeriodo())));
					}
					
					if(trabajador.getDistribucionJornadas().getPeriodo().size()>1){
						periodo = trabajador.getDistribucionJornadas().getPeriodo().get(1);
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD2_TYPE.getValue(),periodo.getTipoDistribucion().equals("1")?"R":"I");
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD2_DAYS.getValue(),
								getIntegerFormat(periodo.getNumeroDiasTrabajadosPorSemanaOPeriodo()));
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD2_FROM.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaInicioPeriodo())));
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD2_TO.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaFinPeriodo())));
					}
					
					if(trabajador.getDistribucionJornadas().getPeriodo().size()>2){
						periodo = trabajador.getDistribucionJornadas().getPeriodo().get(2);
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD3_TYPE.getValue(),periodo.getTipoDistribucion().equals("1")?"R":"I");
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD3_DAYS.getValue(),
								getIntegerFormat(periodo.getNumeroDiasTrabajadosPorSemanaOPeriodo()));
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD3_FROM.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaInicioPeriodo())));
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD3_TO.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaFinPeriodo())));
					}
					
					if(trabajador.getDistribucionJornadas().getPeriodo().size()>3){
						periodo = trabajador.getDistribucionJornadas().getPeriodo().get(3);
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD4_TYPE.getValue(),periodo.getTipoDistribucion().equals("1")?"R":"I");
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD4_DAYS.getValue(),
								getIntegerFormat(periodo.getNumeroDiasTrabajadosPorSemanaOPeriodo()));
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD4_FROM.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaInicioPeriodo())));
						setPdfFieldValue(EnterpriseCertificateField.EMPLOYEE_TP_PERIOD4_TO.getValue(),
								dateFormatter.format(sepeDateFormatter.parse(periodo.getFechaFinPeriodo())));
					}
				}
				
				if(trabajador.getDatosCotizacion()!=null){
					COTIZACIONTYPE cotizacion = null;
					
					// COTIZACIONES - CONT. COMUNES Y DESEMPLEO
					
					if(trabajador.getDatosCotizacion().size()>0){
						cotizacion = trabajador.getDatosCotizacion().get(0);
						setPdfFieldValue(EnterpriseCertificateField.ROW1_YEAR.getValue(),cotizacion.getAno());
						setPdfFieldValue(EnterpriseCertificateField.ROW1_MONTH.getValue(),cotizacion.getMes());
						setPdfFieldValue(EnterpriseCertificateField.ROW1_DAYS.getValue(),getIntegerFormat(cotizacion.getNumDiasCotizados()));
						setPdfFieldValue(EnterpriseCertificateField.ROW1_BASE_COMMON_CONTINGENCIES.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionContingenciasComunes()));
						setPdfFieldValue(EnterpriseCertificateField.ROW1_BASE_UNEMPLOYMENT.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionDesempleo()));
						setPdfFieldValue(EnterpriseCertificateField.ROW1_REMARKS.getValue(),cotizacion.getObservaciones());
					}
					
					if(trabajador.getDatosCotizacion().size()>1){
						cotizacion = trabajador.getDatosCotizacion().get(1);
						setPdfFieldValue(EnterpriseCertificateField.ROW2_YEAR.getValue(),cotizacion.getAno());
						setPdfFieldValue(EnterpriseCertificateField.ROW2_MONTH.getValue(),cotizacion.getMes());
						setPdfFieldValue(EnterpriseCertificateField.ROW2_DAYS.getValue(),getIntegerFormat(cotizacion.getNumDiasCotizados()));
						setPdfFieldValue(EnterpriseCertificateField.ROW2_BASE_COMMON_CONTINGENCIES.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionContingenciasComunes()));
						setPdfFieldValue(EnterpriseCertificateField.ROW2_BASE_UNEMPLOYMENT.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionDesempleo()));
						setPdfFieldValue(EnterpriseCertificateField.ROW2_REMARKS.getValue(),cotizacion.getObservaciones());
					}
					
					if(trabajador.getDatosCotizacion().size()>2){							
						cotizacion = trabajador.getDatosCotizacion().get(2);
						setPdfFieldValue(EnterpriseCertificateField.ROW3_YEAR.getValue(),cotizacion.getAno());
						setPdfFieldValue(EnterpriseCertificateField.ROW3_MONTH.getValue(),cotizacion.getMes());
						setPdfFieldValue(EnterpriseCertificateField.ROW3_DAYS.getValue(),getIntegerFormat(cotizacion.getNumDiasCotizados()));
						setPdfFieldValue(EnterpriseCertificateField.ROW3_BASE_COMMON_CONTINGENCIES.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionContingenciasComunes()));
						setPdfFieldValue(EnterpriseCertificateField.ROW3_BASE_UNEMPLOYMENT.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionDesempleo()));
						setPdfFieldValue(EnterpriseCertificateField.ROW3_REMARKS.getValue(),cotizacion.getObservaciones());
					}
					
					if(trabajador.getDatosCotizacion().size()>3){
						cotizacion = trabajador.getDatosCotizacion().get(3);
						setPdfFieldValue(EnterpriseCertificateField.ROW4_YEAR.getValue(),cotizacion.getAno());
						setPdfFieldValue(EnterpriseCertificateField.ROW4_MONTH.getValue(),cotizacion.getMes());
						setPdfFieldValue(EnterpriseCertificateField.ROW4_DAYS.getValue(),getIntegerFormat(cotizacion.getNumDiasCotizados()));
						setPdfFieldValue(EnterpriseCertificateField.ROW4_BASE_COMMON_CONTINGENCIES.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionContingenciasComunes()));
						setPdfFieldValue(EnterpriseCertificateField.ROW4_BASE_UNEMPLOYMENT.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionDesempleo()));
						setPdfFieldValue(EnterpriseCertificateField.ROW4_REMARKS.getValue(),cotizacion.getObservaciones());
					}
					
					if(trabajador.getDatosCotizacion().size()>4){
						cotizacion = trabajador.getDatosCotizacion().get(4);
						setPdfFieldValue(EnterpriseCertificateField.ROW5_YEAR.getValue(),cotizacion.getAno());
						setPdfFieldValue(EnterpriseCertificateField.ROW5_MONTH.getValue(),cotizacion.getMes());
						setPdfFieldValue(EnterpriseCertificateField.ROW5_DAYS.getValue(),getIntegerFormat(cotizacion.getNumDiasCotizados()));
						setPdfFieldValue(EnterpriseCertificateField.ROW5_BASE_COMMON_CONTINGENCIES.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionContingenciasComunes()));
						setPdfFieldValue(EnterpriseCertificateField.ROW5_BASE_UNEMPLOYMENT.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionDesempleo()));
						setPdfFieldValue(EnterpriseCertificateField.ROW5_REMARKS.getValue(),cotizacion.getObservaciones());
					}
					
					if(trabajador.getDatosCotizacion().size()>5){
						cotizacion = trabajador.getDatosCotizacion().get(5);
						setPdfFieldValue(EnterpriseCertificateField.ROW6_YEAR.getValue(),cotizacion.getAno());
						setPdfFieldValue(EnterpriseCertificateField.ROW6_MONTH.getValue(),cotizacion.getMes());
						setPdfFieldValue(EnterpriseCertificateField.ROW6_DAYS.getValue(),getIntegerFormat(cotizacion.getNumDiasCotizados()));
						setPdfFieldValue(EnterpriseCertificateField.ROW6_BASE_COMMON_CONTINGENCIES.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionContingenciasComunes()));
						setPdfFieldValue(EnterpriseCertificateField.ROW6_BASE_UNEMPLOYMENT.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionDesempleo()));
						setPdfFieldValue(EnterpriseCertificateField.ROW6_REMARKS.getValue(),cotizacion.getObservaciones());
					}
					
					if(trabajador.getDatosCotizacion().size()>6){
						cotizacion = trabajador.getDatosCotizacion().get(6);
						setPdfFieldValue(EnterpriseCertificateField.ROW7_YEAR.getValue(),cotizacion.getAno());
						setPdfFieldValue(EnterpriseCertificateField.ROW7_MONTH.getValue(),cotizacion.getMes());
						setPdfFieldValue(EnterpriseCertificateField.ROW7_DAYS.getValue(),getIntegerFormat(cotizacion.getNumDiasCotizados()));
						setPdfFieldValue(EnterpriseCertificateField.ROW7_BASE_COMMON_CONTINGENCIES.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionContingenciasComunes()));
						setPdfFieldValue(EnterpriseCertificateField.ROW7_BASE_UNEMPLOYMENT.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionDesempleo()));
						setPdfFieldValue(EnterpriseCertificateField.ROW7_REMARKS.getValue(),cotizacion.getObservaciones());
					}
					
					if(trabajador.getDatosCotizacion().size()>7){
						cotizacion = trabajador.getDatosCotizacion().get(7);
						setPdfFieldValue(EnterpriseCertificateField.ROW8_YEAR.getValue(),cotizacion.getAno());
						setPdfFieldValue(EnterpriseCertificateField.ROW8_MONTH.getValue(),cotizacion.getMes());
						setPdfFieldValue(EnterpriseCertificateField.ROW8_DAYS.getValue(),getIntegerFormat(cotizacion.getNumDiasCotizados()));
						setPdfFieldValue(EnterpriseCertificateField.ROW8_BASE_COMMON_CONTINGENCIES.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionContingenciasComunes()));
						setPdfFieldValue(EnterpriseCertificateField.ROW8_BASE_UNEMPLOYMENT.getValue(),getDoubleFormat(cotizacion.getBaseCotizacionDesempleo()));
						setPdfFieldValue(EnterpriseCertificateField.ROW8_REMARKS.getValue(),cotizacion.getObservaciones());
					}
				}

				if(trabajador.getDatosVacacionesCotizadas()!=null){
					setPdfFieldValue(EnterpriseCertificateField.HOLIDAY_DAYS.getValue(),
							getIntegerFormat(trabajador.getDatosVacacionesCotizadas().getNumDiasCotizados()));
					setPdfFieldValue(EnterpriseCertificateField.HOLIDAY_BASE_COMMON_CONTINGENCIES.getValue(),
							getDoubleFormat(trabajador.getDatosVacacionesCotizadas().getBaseCotizacionContingenciasComunes()));
					setPdfFieldValue(EnterpriseCertificateField.HOLIDAY_BASE_UNEMPLOYMENT.getValue(),
							getDoubleFormat(trabajador.getDatosVacacionesCotizadas().getBaseCotizacionDesempleo()));
					setPdfFieldValue(EnterpriseCertificateField.HOLIDAY_REMARKS.getValue(),trabajador.getDatosVacacionesCotizadas().getObservaciones());
				}
				
				
				if(trabajador.getDatosCotizacion()==null || trabajador.getDatosCotizacion().isEmpty()){
					setPdfFieldValue(EnterpriseCertificateField.TOTAL_DAYS.getValue(),"N/D");
					setPdfFieldValue(EnterpriseCertificateField.TOTAL_BASE_COMMON_CONTINGENCIES.getValue(),"N/D");
					setPdfFieldValue(EnterpriseCertificateField.TOTAL_BASE_UNEMPLOYMENT.getValue(),"N/D");
				} else {
					Integer totalDays = trabajador.getDatosCotizacion()
							.stream().map(COTIZACIONTYPE::getNumDiasCotizados)
							.mapToInt(Integer::parseInt).sum();
					Double totalCommonCont = trabajador.getDatosCotizacion()
							.stream().map(COTIZACIONTYPE::getBaseCotizacionContingenciasComunes)
							.mapToDouble(Double::parseDouble)
							.map(value -> CommonUtil.round(value/100))
							.sum();
					Double totalUnemployment = trabajador.getDatosCotizacion()
							.stream().map(COTIZACIONTYPE::getBaseCotizacionDesempleo)
							.mapToDouble(Double::parseDouble)
							.map(value -> CommonUtil.round(value/100))
							.sum();
					
					totalDays += Integer.parseInt(trabajador.getDatosVacacionesCotizadas().getNumDiasCotizados());
					totalCommonCont += Double.parseDouble(trabajador.getDatosVacacionesCotizadas().getBaseCotizacionContingenciasComunes());
					totalUnemployment +=Double.parseDouble(trabajador.getDatosVacacionesCotizadas().getBaseCotizacionDesempleo());
					
					setPdfFieldValue(EnterpriseCertificateField.TOTAL_DAYS.getValue(),
							String.valueOf(totalDays));
					setPdfFieldValue(EnterpriseCertificateField.TOTAL_BASE_COMMON_CONTINGENCIES.getValue(),
							String.valueOf(totalCommonCont));
					setPdfFieldValue(EnterpriseCertificateField.TOTAL_BASE_UNEMPLOYMENT.getValue(),
							String.valueOf(totalUnemployment));
				}
			}
			
			// FOOTER
			setPdfFieldValue(EnterpriseCertificateField.SIGN_CITY.getValue(),contract.getWorkPlace().getAddress().getCity());
			dateFormatter.applyPattern("dd");
			setPdfFieldValue(EnterpriseCertificateField.SIGN_DAY.getValue(),dateFormatter.format(new Date()));
			dateFormatter.applyPattern("MMMM");
			setPdfFieldValue(EnterpriseCertificateField.SIGN_MONTH.getValue(),dateFormatter.format(new Date()));
			dateFormatter.applyPattern("yy");
			setPdfFieldValue(EnterpriseCertificateField.SIGN_YEAR.getValue(),dateFormatter.format(new Date()));
			setPdfFieldValue(EnterpriseCertificateField.SIGNATURE.getValue(),"");
			
			
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
		} catch (ParseException e) {
			// do nothing
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
	
	