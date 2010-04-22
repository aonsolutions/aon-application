package com.code.aon.ui.payroll.wizard;

import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.payroll.dao.IPayrollAlias;
import com.code.aon.payroll.geograficas.Provincia;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.payroll.controller.Utils;
import com.code.aon.ui.util.AonUtil;

public class NumberValidationUtils {
	
	public static void checkCiasPattern(String cias) {
		if(!Utils.validarMascara(cias, "##########A")){
			String msg = "Numero cias no valido. Debe cumplir con la mascara 9999999999X.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} 
	}
	
	public static void checkNumColPattern(String numCol) {
		if(!Utils.validarMascara(numCol, "########")){
			String msg = "Numero de colegiado no valido. Debe cumplir con la mascara 99999999.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} 
	}
	
	public static boolean checkCias(String cias) {
		String lChLetras = "RWAGMYFPDXBNJZSQVHLCKET";
		String lChLetraOriginal = "";
		String lChLetraResultado = "";
		cias = cias.toUpperCase();
		String lChNumeroOriginal = cogerParteValida(cias);
		String letter = lChNumeroOriginal.substring(
				lChNumeroOriginal.length() - 1, lChNumeroOriginal.length());
		Integer lInNumeroResultado;

		try {
			Integer.parseInt(letter);
			lInNumeroResultado = 10;
		} catch (NumberFormatException e) {
			// La cadena no se puede convertir a entero
			lInNumeroResultado = 11;
		}
		lChNumeroOriginal = repeat('0', (lInNumeroResultado - lChNumeroOriginal
				.length()))
				+ lChNumeroOriginal;

		if (lChNumeroOriginal.length() == 10) {
			if (comprobarDigitos(lChNumeroOriginal)) {
				// entonces añadiremos la letra de control que le corresponda
				lInNumeroResultado = calcularResultado(lChNumeroOriginal);
				if (lInNumeroResultado == 0) {
					lInNumeroResultado = 23;
				}
				lChLetraResultado = lChLetras.substring(lInNumeroResultado, 1);
				cias = lChNumeroOriginal + lChLetraResultado;
				return true;
			} else {
				return false;
			}
		} else if (lChNumeroOriginal.length() == 11) {
			lChLetraOriginal = lChNumeroOriginal.substring(10, 11);
			if (comprobarDigitos(lChNumeroOriginal.substring(0, 10))) {
				lChNumeroOriginal = lChNumeroOriginal.substring(0, 10);
				lInNumeroResultado = calcularResultado(lChNumeroOriginal);
				if (lInNumeroResultado == 0) {
					lInNumeroResultado = 23;
				}
				lChLetraResultado = lChLetras.substring(lInNumeroResultado - 1,
						lInNumeroResultado);
				if (lChLetraResultado.equals(lChLetraOriginal)) {
					cias = lChNumeroOriginal + lChLetraOriginal;
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private static String repeat(char c, int i) {
		String tst = "";
		for (int j = 0; j < i; j++) {
			tst = tst + c;
		}
		return tst;
	}

	private static String cogerParteValida(String pChDni) {
		String lChDniResultado;
		lChDniResultado = "";
		lChDniResultado = pChDni.trim();
		lChDniResultado = lChDniResultado.replace(".", "");
		return lChDniResultado;
	}

	private static Boolean comprobarDigitos(String pChNumero) {
		// Funcion que comprueba que los caracteres del dni (que son char)
		// corresponden a valores numericos
		int pos = 0;
		while (pos < pChNumero.length()) {
			if (pChNumero.charAt(pos) < '0' || pChNumero.charAt(pos) > '9') {
				return false;
			}
			pos++;
		}
		return true;
	}

	private static Integer calcularResultado(String pChDato) {
		Integer lDcNumero = Integer.parseInt(pChDato);
		Integer parteEntera = lDcNumero / 23;
		return (lDcNumero - (parteEntera * 23));
	}
	

	public static String checkNumCol(String pChNumcol) {
	    String lChRet = null;
	    Integer lSmInd;
	    Integer lSmLong;
	    
//	    comprobar que el num de colegiado ni sea null ni este vecio
	    if (!pChNumcol.isEmpty() && pChNumcol.length()>0) {
//	    	if not ((lSmLong = pChNumcol.length()) in (8, 7, 2)) {
	    	
//	    	comprobar la mascara del numero de colegiado. 8 digitos -> mask=99999999
	    	lSmLong = pChNumcol.length();
	    	if (!(lSmLong==8)) {
	            lChRet = "¡¡¡Longitud invalida!!!. El Número de colegiado debe expresarse con 8 dígitos";
	        } else {
	            lSmInd = 1;
	            
	            while (lChRet==null && lSmInd < lSmLong + 1){
	            	Integer numCol = Integer.parseInt(pChNumcol.substring(lSmInd-1, lSmInd));
	            	if (numCol>0 && numCol<=9) {
	                    ++lSmInd;
	                } else {
	                    lChRet = "¡¡¡Número invalido!!!. El Número de colegiado debe expresarse con 8 dígitos numéricos";
	                }
	            }
	            if (lChRet==null) {
//	            	 coger del numero el primer digito y buscarlo en provincia por su codigo 
	            	IManagerBean provinciaBean = (IManagerBean)AonUtil.getManagerBean(Provincia.class);
	            	Criteria criteria = new Criteria();
	            	List<ITransferObject> list=null;
	            	try {
						criteria.addEqualExpression(provinciaBean.getFieldName(IPayrollAlias.PROVINCIA_CDG), pChNumcol.substring(0, 2));
						list = provinciaBean.getList(criteria);
					} catch (ManagerBeanException e) {
						e.printStackTrace();
					}
                	if (list.size()!=0) {
	                    if (lSmLong == 8) {
	                        if (!pChNumcol.substring(7, 8).equals(gFnDCColegiado(pChNumcol.substring(0, 7)))) {
	                            lChRet = "¡¡¡Dígito de control incorrecto!!!. El dígito de control del número de colegiado no es correcto";
	                        }
	                    } else {
	                        if (lSmLong == 2) {
	                            pChNumcol += "99999";
	                        }
	                        pChNumcol += gFnDCColegiado(pChNumcol);
	                    }
	                } else {
	                    lChRet = "¡¡¡Código de provincia invalido!!!. La Provincia indicada no se ha encontrado en la Base de Datos";
	                }
	            }
	         }
	    }
	    return lChRet;
	}

	private static String gFnDCColegiado(String pInNumero) {
		Integer lDcNumero = Integer.parseInt(pInNumero);
		Integer parteEntera = lDcNumero / 9;
		lDcNumero = (lDcNumero - (parteEntera * 9));
		return lDcNumero.toString();
	}

}
