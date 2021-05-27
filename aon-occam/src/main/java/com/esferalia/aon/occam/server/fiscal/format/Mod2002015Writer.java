package com.esferalia.aon.occam.server.fiscal.format;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod2002015Writer {
	
	// ***************************************************
	// **** VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****
	// ***************************************************
	
	private static SimpleDateFormat DATE_FORMAT_6 = new SimpleDateFormat("ddMMyy");
	private static int DS = 17;  // Tamaño de digitos por defecto para los importes 
	private static int DD =  2;  // Decimales por defecto para los importes
	
	// Añade un importe de una casilla del modelo (con signo, longitud y decimales por defecto, relleno con ceros por la izquierda) 
	private static void addSignedKey(Writer line, Mod2002015 mod200, Mod2002015Key key) throws IOException {
		line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key) , DS, DD ));
	}
	
	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso no se pone el importe, sino que se ponen ceros
	private static void addSignedKey(Writer line, Mod2002015 mod200, Mod2002015Key key, boolean isComplementary) throws IOException {
		if (isComplementary) {
			line.append(AonFiscalFileUtils.zeros(DS));
		} else {
			line.append(AonFiscalFileUtils.signedZero(mod200.getDoubleValue(key) , DS, DD ));
		}
	}
	
	// Añade un importe de una casilla del modelo (sin signo y relleno con ceros por la izquierda) 
	private static void addUnSignedKey(Writer line, Mod2002015 mod200, Mod2002015Key key, int size, int dec) throws IOException {
		line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key) , size, dec ));
	}
	
	// Sobrecargado para poder pasarle si es una pagina complementaria, en tal caso no se pone el importe, sino que se ponen ceros
	private static void addUnSignedKey(Writer line, Mod2002015 mod200, Mod2002015Key key, int size, int dec, boolean isComplementary) throws IOException {
		
		if (isComplementary)
			line.append(AonFiscalFileUtils.zeros(size));
		else line.append(AonFiscalFileUtils.unsigned(mod200.getDoubleValue(key) , size, dec ));
		
	}
	
	// Añade la etiqueta inicio de pagina
	private static void addStartLabel(Writer line, String label) throws IOException {
		
		line.append("<"+label+">");
		
	}
	
	// Añade la etiqueta fin de pagina
	private static void addEndLabel(Writer line, String label) throws IOException {
		
		line.append("</"+label+">");
		
	}
	
    // Declaración Representantes Legales de la Entidad
	private static void addLegalRepresentative(Writer line, Mod2002015 mod200, int index) throws IOException {
		String name = "";
		String document = "";
		Date notaryDate = null;
		String notary = "";
		if (index < mod200.getRepresentatives().size()) {			
			name = mod200.getRepresentatives().get(index).getName();
			document = mod200.getRepresentatives().get(index).getDocument();
			notaryDate = mod200.getRepresentatives().get(index).getNotaryDate();
			notary = mod200.getRepresentatives().get(index).getNotary();
		}
		// Añadir los datos al Writer
		line.append( AonFiscalFileUtils.text(name, 36) );
		line.append( AonFiscalFileUtils.text(document, 9) );
		line.append( AonFiscalFileUtils.dateZero(notaryDate) );
		line.append( AonFiscalFileUtils.text(notary,12));
	}
	
	// A. Relación de administradores
	private static void addCompanyAdministrator(Writer line, Mod2002015 mod200, int index) throws IOException {
		String document = "";
		String fj = "";
		String rpte = "0";
		String name = "";
		String residence = "";
		String province = "";
		if (index < mod200.getAdministrators().size()) {
			document = mod200.getAdministrators().get(index).getDocument();			
			fj = mod200.getAdministrators().get(index).getEntity();	
			rpte = mod200.getAdministrators().get(index).getRepresenStr();                             
			name = mod200.getAdministrators().get(index).getName();
			residence = mod200.getAdministrators().get(index).getResidence();                 
            if (mod200.getDoubleValue(Mod2002015Key.C0021)==1)
            	province = mod200.getAdministrators().get(index).getProvinceStr(); // Provincia solo se pone si esta marcado Caracter 021
		}
		// Añadir los datos al Writer
		line.append( AonFiscalFileUtils.text(document,  9) );  // N.I.F.			
		line.append( AonFiscalFileUtils.text(fj,        1) );  // F/J	
		line.append( AonFiscalFileUtils.text(rpte,      1) );  // RPTE.                            
		line.append( AonFiscalFileUtils.text(name,     40) );  // Apellidos y nombre / Razón social
		line.append( AonFiscalFileUtils.text(residence,17) );  // Domicilio fiscal                 
		line.append( AonFiscalFileUtils.text(province,  2) );  // Código Provincial
		
	}	
	
	// Devuelve el codigo de provincia (por defecto) o el pais, según este cumplimentado 
	// uno u otro campo (province o country) de participaciones
	private static String getProvinceCountry(CompanyParticipation cp) {
		
		int province = cp.getProvince();
		if (province==0)
			return AonStringUtils.trimToEmpty(cp.getCountry()); // Pais
		else return AonStringUtils.leftPad(Integer.toString(province), 2, "0");  // Provincia
		
	}
	
	// B.1. Participaciones declarante en otras entidades	
	private static void addCompanyParticipationOut(Writer line, Mod2002015 mod200, int index) throws IOException {
		String document = "";	
		String name = ""; 
		String province = ""; 
		double percent = 0; 
		double nominalValue = 0;	
		double bookValue = 0; 
		double incomes = 0; 
		double aValue = 0; 
		double bValue = 0; 
		double cValue = 0; 
		double ccValue = 0;
		double dValue = 0; 
		double capital = 0; 
		double reserve = 0; 
		double otherAmounts = 0; 
		double result = 0;
		if (index < mod200.getParticipationsOut().size()) {			
            document     = mod200.getParticipationsOut().get(index).getDocument();	
            name         = mod200.getParticipationsOut().get(index).getName();	    
            province     = getProvinceCountry(mod200.getParticipationsOut().get(index));	
            percent      = mod200.getParticipationsOut().get(index).getPercent();
            nominalValue = mod200.getParticipationsOut().get(index).getNominalValue();  
            bookValue    = mod200.getParticipationsOut().get(index).getBookValue();  	
            incomes 	 = mod200.getParticipationsOut().get(index).getIncomes();       
            aValue       = mod200.getParticipationsOut().get(index).getaValue();        
            bValue       = mod200.getParticipationsOut().get(index).getbValue();        
            cValue       = mod200.getParticipationsOut().get(index).getcValue();        
            dValue       = mod200.getParticipationsOut().get(index).getdValue();        
            ccValue      = mod200.getParticipationsOut().get(index).getccValue();
            capital      = mod200.getParticipationsOut().get(index).getCapital();       
            reserve      = mod200.getParticipationsOut().get(index).getReserve();       
            otherAmounts = mod200.getParticipationsOut().get(index).getOtherAmounts();  
            result       = mod200.getParticipationsOut().get(index).getResult();        
		}
        line.append( AonFiscalFileUtils.text(document, 15 ));
        line.append( AonFiscalFileUtils.text(name,     30 ));
        line.append( AonFiscalFileUtils.text(province,  2 )); 
        line.append( AonFiscalFileUtils.unsigned(percent,5,2)); 
        line.append( AonFiscalFileUtils.signedZero(nominalValue,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(bookValue   ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(incomes 	   ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(aValue      ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(bValue      ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(cValue      ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(ccValue     ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(dValue      ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(capital     ,DS, DD)); 
        line.append( AonFiscalFileUtils.signedZero(reserve     ,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(otherAmounts,DS, DD));
        line.append( AonFiscalFileUtils.signedZero(result      ,DS, DD));
	}
	
	// B.2. Participaciones de personas o entidades en la declarante
	private static void addCompanyParticipationIn(Writer line, Mod2002015 mod200, int index) throws IOException {
		String document = "";
		String rpte = "0";
		String fj = "";
		String name = "";
		String province = "";
		double nominalValue = 0;
		double percent = 0;
		if (index < mod200.getParticipationsIn().size()) {
			document = mod200.getParticipationsIn().get(index).getDocument();   
			rpte = mod200.getParticipationsIn().get(index).getRepresenStr();
			fj = mod200.getParticipationsIn().get(index).getEntity();
			name = mod200.getParticipationsIn().get(index).getName();
			province = getProvinceCountry(mod200.getParticipationsIn().get(index));   
			nominalValue = mod200.getParticipationsIn().get(index).getNominalValue();
			percent = mod200.getParticipationsIn().get(index).getPercent();
		}
		line.append( AonFiscalFileUtils.text(document, 15));
		line.append( AonFiscalFileUtils.text(rpte,      1));
		line.append( AonFiscalFileUtils.text(fj,        1));					                                                      
		line.append( AonFiscalFileUtils.text(name,     37)); 
		line.append( AonFiscalFileUtils.text(province,  2));
		line.append( AonFiscalFileUtils.signedZero(nominalValue, DS, DD) ); 
		line.append( AonFiscalFileUtils.unsigned(percent, 5,2));
	}
	
	// **** FIN VARIABLES Y METODOS ESTATICOS DE UTILIDAD ****
	
	@FunctionalInterface
	private interface IPropertyFiller {		
		public void propertyFill(Writer line, Mod2002015 mod200, String tag) throws IOException;
	}
    
	private enum Pages2015 {
		 PAG00 ("AUX", new IPropertyFiller[] {				 
			 (line,mod200, label) -> addStartLabel(line,label)
 			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces( 70))
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(  4))
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(  4))
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(  9))
			,(line,mod200, label) -> line.append(AonFiscalFileUtils.spaces(213))
			,(line,mod200, label) -> addEndLabel(line,label)
		 })
		 ,PAG01 ("T200010", new IPropertyFiller[] {
				 (line,mod200, label) -> addStartLabel(line,label)
				,(line,mod200, label) -> line.append(" ")
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.dateZero(mod200.getPeriodStart()))					
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.dateZero(mod200.getPeriodEnd()))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getPeriodType(),1,0))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(
					 mod200.getCnae()==null?"":mod200.getCnae().replace(".",""), 4))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterpriseDocument(),9)) 
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterpriseName(),40))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterprisePhone1(),9))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterprisePhone2(),9))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getYear(), 4,0))
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0001, 1, 0 ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0002, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0003, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0004, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0005, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0011, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0013, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0014, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0017, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0018, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0019, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0021, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0023, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0024, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0025, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0031, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0032, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0036, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0048, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0058, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0060, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0066, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0006, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0015, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0022, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0028, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0047, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0035, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0049, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0029, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0033, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0034, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0038, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0046, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0012, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0064, 1, 0 )			
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0057, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0020, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0062, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0056, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0007, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0009, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0010, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0016, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0026, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0027, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0030, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0039, 1, 0 ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0043, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0067, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0068, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0069, 1, 0 ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0045, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0063, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0071, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0059, 1, 0 )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0065, 1, 0 )
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned( 
					mod200.getBalanceType()==null?0:mod200.getBalanceType().ordinal()+1, 1,0))                
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned( 
						mod200.getPygType()==null || mod200.getDoubleValue(Mod2002015Key.C0026)==1
						?0:mod200.getPygType().ordinal()+1, 1,0) )
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0061, 1, 0)
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getFiscalGroup(), 7)) 
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getDominantDocument(), 9) ) 
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getDominantIdentificationNumber(), 9) ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0041, 9, 2 ) 
				,(line,mod200, label) -> addUnSignedKey( line, mod200, Mod2002015Key.C0042, 9, 2 ) 
				,(line,mod200, label) -> line.append( mod200.isComplementary()?"1":"0" )
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(
					AonNumberUtils.todouble(mod200.getComplementaryReceipt()),13,0))
	            ,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getSecretary().getName(),21)) 
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getSecretary().getDocument(),9))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.dateZero(mod200.getSecretary().getIrnr())) 
				,(line,mod200, label) -> addLegalRepresentative(line, mod200, 0)
				,(line,mod200, label) -> addLegalRepresentative(line, mod200, 1)
				,(line,mod200, label) -> addLegalRepresentative(line, mod200, 2)
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(21))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(20))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(50))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.zeros(9))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.zeros(9))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(50))
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.spaces(13))			
				,(line,mod200, label) -> addEndLabel(line,label)
		 })

		,PAG02 ("T200020", new IPropertyFiller[] {
			(line,mod200, label) -> {
				boolean isComplementary = false; // Indicador de pagina complementaria
				int a  = 0;  // Contador para Administradores
				int b1 = 0;  // Contador para Participaciones B1
				int b2 = 0;  // Contador para Participaciones B2 
				while ( a  < mod200.getAdministrators().size() || 
						b1 < mod200.getParticipationsOut().size() ||
						b2 < mod200.getParticipationsIn().size()) {
					addStartLabel(line,label);					 
					line.append(isComplementary?"C":" ");
					addCompanyAdministrator(line, mod200, a++);
					addCompanyAdministrator(line, mod200, a++);
					addCompanyAdministrator(line, mod200, a++);
					addCompanyAdministrator(line, mod200, a++);
					addCompanyAdministrator(line, mod200, a++);
					addCompanyAdministrator(line, mod200, a++);
					addCompanyParticipationOut(line, mod200, b1++);
					addCompanyParticipationOut(line, mod200, b1++);
					addCompanyParticipationOut(line, mod200, b1++);
					addCompanyParticipationOut(line, mod200, b1++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addCompanyParticipationIn(line, mod200, b2++);
					addUnSignedKey(line, mod200, Mod2002015Key.POR51, 5, 2, isComplementary );
					addUnSignedKey(line, mod200, Mod2002015Key.PORES, 5, 2, isComplementary );
					isComplementary = true;
					addEndLabel(line,label);
				}
			}
		})
				
		,PAG03 ("T200030", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA101)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA102)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA103)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA104)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA105)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA106)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA107)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA108)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA700)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA701)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA109)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA110)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA111)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA112)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA113)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA114)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA115)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA116)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA117)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA118)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA119)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA120)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA121)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA122)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA123)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA124)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA125)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA126)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA127)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA128)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA129)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA130)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA131)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA132)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA133)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA134)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA135)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA136)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA137)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA138)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA139)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA140)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA141)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA142)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA143)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA144)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA145)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA146)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA147)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA148)
			,(line,mod200, label) -> addEndLabel(line,label)			
		})
		
		,PAG04 ("T200040", new IPropertyFiller[] {  
             (line,mod200, label) -> addStartLabel(line,label)
            ,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA149)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA150)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA151)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA152)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA153)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA154)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA155)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA156)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA157)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA158)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA159)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA160)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA161)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA162)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA163)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA164)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA165)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA166)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA167)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA168)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA169)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA170)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA171)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA172)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA173)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA174)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA175)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA176)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA177)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA178)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA179)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BA180)				
			,(line,mod200, label) -> addEndLabel(line,label)
		})
		
		,PAG05 ("T200050", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP185)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP186)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP187)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP188)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP189)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP190)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP191)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP192)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP193)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP702)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP1001)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP1002)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP194)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP195)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP196)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP197)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP198)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP199)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP200)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP201)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP202)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP203)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP204)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP205)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP206)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP207)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP208)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP209)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP210)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP211)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP212)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP213)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP214)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP215)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP216)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP217)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP218)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP219)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP220)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP221)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP222)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP223)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP224)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP225)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP226)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP227)
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG06 ("T200060", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
            ,(line,mod200, label) -> line.append(" ")			 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP228)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP229)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP230)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP703)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP704)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP231)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP232)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP233)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP234)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP235)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP236)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP237)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP238)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP239)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP240)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP241)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP242)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP243)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP244)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP245)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP246)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP247)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP248)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP249)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP250)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP251)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BP252)				
			,(line,mod200, label) -> addEndLabel(line,label)
			})
				
		,PAG07 ("T200070", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG255)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG256)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG257)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG705)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG706)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG707)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG708)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG258)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG259)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG260)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG261)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG760)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG761)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG262)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG762)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG763)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG263)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG264)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG265)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG266)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG267)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG268)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG269)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG270)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG271)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG273)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG274)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG275)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG276)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG277)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG278)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG279)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG280)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG281)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG282)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG283)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG709)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG284)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG285)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG286)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG287)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG288)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG289)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG290)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG291)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG292)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG293)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG710)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG294)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG295)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG296)				
			,(line,mod200, label) -> addEndLabel(line,label)
			})

		,PAG08 ("T200080", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
		    ,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG297)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG298)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG299)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG300)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG301)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG302)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG303)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG304)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG305)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG306)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG307)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG308)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG309)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG310)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG311)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG312)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG313)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG314)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG315)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG316)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG317)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG318)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG319)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG320)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG321)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG322)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG323)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG329)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG330)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG331)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG332) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG324)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG325)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG326)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG327)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG328)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.PG500)
			,(line,mod200, label) -> addEndLabel(line,label)
		})
			
		,PAG09 ("T200090", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0500)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0336)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0337)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0338)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0339)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0340)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0341)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0342)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0343)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0344)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0345)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0346)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0347)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0348)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0349)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0350)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0351)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0352)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0353)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0354)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.T0355)
			,(line,mod200, label) -> addEndLabel(line,label)
		})
			
		,PAG10 ("T200100", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ") 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC380)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC381)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC382)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC383)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC384)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC385)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC386)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC394)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC395)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC396)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC397)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC398)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC399)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC400)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC408)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC409)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC410)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC411)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC412)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC413)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC414)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC422)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC423)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC424)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC425)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC426)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC427)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC428)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC436)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC437)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC438)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC439)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC440)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC441)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC442)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC450)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC451)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC452)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC453)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC454)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC455)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC456)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC464)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC465)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC466)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC467)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC468)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC469)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC470)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC478)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC479)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC480)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC481)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC482)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC483)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC484)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC492)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC493)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC494)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC495)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC496)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC497)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC498)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC506)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC507)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC508)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC509)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC510)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC511)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC512)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC520)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC521)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC522)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC523)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC524)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC525)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC526)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC534)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC535)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC536)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC537)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC538)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC539)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC540)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC548)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC549)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC550)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC551)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC552)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC553)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC554)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC562)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC563)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC564)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC565)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC566)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC567)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC568)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC576)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC577)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC578)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC579)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC580)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC581)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC582)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC590)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC591)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC592)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC593)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC594)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC595)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC596)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC604)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC605)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC606)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC607)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC608)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC609)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC610)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC618)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC619)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC620)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC621)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC622)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC623)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC624)  
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC715)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC716)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC717)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC718)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC719)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC720)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC721)  
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC729)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC730)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC731)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC732)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC733)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC734)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC735)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC632)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC633)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC634)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC635)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC636)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC637)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC638)
		    ,(line,mod200, label) -> addEndLabel(line,label)
		})
			
		,PAG11 ("T200110", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC387)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC388)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC389)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC390)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC391)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC392)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC393)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC401)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC402)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC403)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC404)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC405)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC406)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC407)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC415)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC416)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC417)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC418)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC419)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC420)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC421)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC429)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC430)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC431)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC432)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC433)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC434)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC435)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC443)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC444)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC445)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC446)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC448)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC449)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC457)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC458)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC461)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC462)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC463)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC471)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC472)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC475)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC476)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC477)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC485)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC486)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC489)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC490)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC491)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC499)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC502)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC503)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC504)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC505)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC513)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC514)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC515)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC516)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC517)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC518)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC519)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC527)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC528)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC529)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC530)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC531)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC532)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC533)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC541)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC542)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC543)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC544)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC545)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC546)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC547)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC555)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC556)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC557)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC558)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC560)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC561)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC569)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC570)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC571)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC572)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC574)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC575)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC583)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC584)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC585)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC586)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC588)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC589)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC597)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC598)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC599)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC600)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC602)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC603)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC611)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC612)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC613)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC614)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC615)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC616)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC617)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC625)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC626)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC627)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC628)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC629)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC630)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC631)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC722)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC723)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC724)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC725)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC726)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC727)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC728)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC736)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC737)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC738)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC739)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC740)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC741)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC742)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC639)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC640)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC641)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC642)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC643)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC644)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TC645)
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG12 ("T200120", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ500)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ301)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ302)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ501)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1230)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1231)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0355)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0356)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0357)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0358)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0359)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0360)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0225)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0226)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0545)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0272)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0361)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0362)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0303)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0304)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0505)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0305)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0306)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0307)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0308)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I1003)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D1004)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0309)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0310)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I1005)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D1006)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0514)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0509)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0516)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0551)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0321)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0322)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0415)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0211)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0331)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0332)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0325)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0326)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0327)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0328)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0333)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0334)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0416)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0543)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0335)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0336)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0337)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0338)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0368)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0339)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0341)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0342)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0508)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I1009)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D1010)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0343)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0363)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0364)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0345)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0346)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0371)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0347)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0348)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I1011)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D1012)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I1013)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D1014)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I1015)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D1016)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0369)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0370)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0256)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0278)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0372)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0373)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0374)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0340)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0351)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0375)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0376)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I1320)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D1321)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0184)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0544)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I1022)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D1023)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I1018)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D1019)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I1275)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D1276)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0377)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0378)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0379)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0380)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0381)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0382)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0383)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0384)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0387)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0388)
			,(line,mod200, label) -> addEndLabel(line,label)
		})
			
		,PAG13 ("T200130", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0311)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0312)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0313)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0314)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0323)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0324)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0317)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0318)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0385)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0386)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0389)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0390)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0396)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0397) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0398) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0250)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0251)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0391)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0392)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0400)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0403)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0404)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0518)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0519)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0510)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0512)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0329)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0330)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0365)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D1026)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0409)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0410)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0411)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0412)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I1027)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D1028)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0413)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0414)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.I0417)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.D0418)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ578)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ579)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1029)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1030)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1031)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ550)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1032)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ547)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ552)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1033)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1034)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1330)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ553)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ554)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ555)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ556)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ559)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1035)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ520)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ521)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ545)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ593)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.LQ558, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ560)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ210)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ480)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ408)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1037)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ561)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1285)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1286)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1331)
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG14 ("T200140", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ562)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1038)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN567)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN568)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN563)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN566)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN576)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN569)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN570)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1280)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN572)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN571)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN573)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN575)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN577)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN581)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN582)    
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN583) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN585)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN584)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN588)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1039)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN565)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN590)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN399)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN082)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1040)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1041)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN592)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN595)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN596)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN597)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN599)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN600)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN601)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN602)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN603)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN604)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN605)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN606)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN611)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN612)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN615)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN616)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN633)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN642)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN617)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN618)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN619)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN620)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN083)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1332)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1200)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1042)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1333)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN621)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN622)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1020)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1043)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1021)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1044)
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG15 ("T200150", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ640)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ641)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ548)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ643)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ644)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ645)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ646)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ647)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ648)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ649)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ650)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ651)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ652)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ653)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ654)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ655)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ656)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ657)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ658)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ659)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ660)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ661)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ662)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ663)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ664)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ665)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ666)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ667)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ668)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ669)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ743)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ747)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ748)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ275)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ276)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ277)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ608)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ609)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ610)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ704)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ705)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ706)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ013)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ014)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ015)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ725)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ726)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ727)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ534)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ535)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ536)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ607)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ675)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ699)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1045)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1046)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1047)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ670)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ547)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ671)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1048)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1049)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN104)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN105, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN846)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN847)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN106)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN107, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN282)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN283)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN284)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN108)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN109, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN702)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN703)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN707)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN110)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN111, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN071)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN187)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN300)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN112)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN113, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN025)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN026)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN027)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN114)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN115, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN714)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN715)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN716)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN735)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN920, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN736)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN737)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN738)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN116)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN117)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN570)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN118)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN103, 7, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN153)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN728, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN637)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN638)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN154)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN729, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN849)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN894)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN197)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN155)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN730, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN285)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN286)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN287)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN156)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN731, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN825)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN826)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN827)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN157)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN732, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN001)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN002)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN003)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN158)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN733, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN028)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN029)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN030)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN159)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN734, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN717)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN718)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN719)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN720)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN721, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN722)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN723)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN724)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN739)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN921, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN740)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN741)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN742)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN134)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN926, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN135)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN136)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN137)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN160)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN161)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN572)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN162)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN103, 7, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1054)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN1050, 4, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1051)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1052)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1053)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN131)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN132)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN571)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN133)
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.BN103, 7, 2 )
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN163)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN165)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN166)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN167)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN169)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN170)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN171)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN573)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN174)
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
		})

		,PAG16 ("T200160", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN835)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN836)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN837)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN838)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN839)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN840)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN932)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN933)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN934)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN297)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN298)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN299)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN090)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN091)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN092)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN004)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN005)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN006)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN031)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN032)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN033)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN022)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN023)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN024)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN040)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN041)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN042)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN138)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN139)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN140)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN141)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN142)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN143)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN188)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN189)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN190)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN803)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN804)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN805)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1055)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1056)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1057)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN700)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN708)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN709)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN841)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN585)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN843)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN749)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN750)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN752)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN753)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN754)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN755)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN756)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN757)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN758)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN759)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN760)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN761)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN762)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN763)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN744)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN745)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN746)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN779)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN783)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN784)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN764)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN584)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN765)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN854)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN855)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN857)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN858)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN859)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN860)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN861)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN862)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN863)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN864)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN865)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN883)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN884)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN885)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN785)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN789)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN790)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN852)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN853)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN856)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN088)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN564)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN194)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN195)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN196)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN868)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN869)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN834)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN871) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN872)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN873)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN874)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN875)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN876)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN877)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN878)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN879)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN880)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN881)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN882)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN866)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN867)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN870)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN939)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN940)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN941)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN191)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN192)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN193)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN613)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN614)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN701)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN200)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN257)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN011)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN037)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN038)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN039)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN044)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN045)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN046)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN528)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN529)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN530)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN144)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN145)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN146)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN147)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN148)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN149)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN240)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN241)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN242)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1058)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1059)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1060)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN791)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN802)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN806)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN886)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN590)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN887)
			,(line,mod200, label) -> addEndLabel(line,label)
		})

		,PAG17 ("T200170", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)
			,(line,mod200, label) -> line.append(" ")
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1061)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1062)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN768)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN769)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN770)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN774)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN775)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN776)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN780)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN781)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN782)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN786)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN787)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN788)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN766)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN767)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN833)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN198)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN896)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN897)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN288)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN289)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN290)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN466)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN467)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN468)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN061)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN498)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN586)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN472)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN473)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN478)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN180)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN181)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN182)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN531)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN532)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN533)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN945)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN946)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN947)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN960)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN961)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN962)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN183)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN185)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN186)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN966)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN967)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN968)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN457)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN458)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN459)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN460)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN461)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN462)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1063)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1064)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1065)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1066)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1067)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1068)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1069)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1070)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1071)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN813)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN814)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN815)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN986)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN810)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN507)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN557)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN591)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN594)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN795)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN796)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN797)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN798)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN799)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN800)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN096)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN698)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN713)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN549)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN888)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN889)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN807)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN808)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN809)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1075)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1076)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1077)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN963)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN964)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN965)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN931)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN502)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN751)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1078)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1079)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1080)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN070)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN072)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN073)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN078)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN079)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN080)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN085)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN086)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN087)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN093)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN057)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN058)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN207)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN208)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN209)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN216)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN217)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN218)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN204)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN205)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN206)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN219)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN220)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN221)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN228)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN229)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN230)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN237)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN238)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN239)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN007)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN012)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN016)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN199)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN292)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN293)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN419)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN422)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN423)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN424)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN425)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN428)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN429)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN430)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN431)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN432)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN433)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN434)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN435)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN436)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN437)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN438)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN439)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN440)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1081)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1082)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1083)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1084)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1085)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1086)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1087)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1088)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1089)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1090)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1091)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1092)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1093)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1094)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1095)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1096)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1097)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1098)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1099)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1100)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1101)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1102)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1103)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1104)
			,(line,mod200, label) -> addEndLabel(line,label)
		})
		,PAG18 ("T200180", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1105)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1106)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1107)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1108)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1109)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1110)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1111)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1112)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1113)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1114)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1115)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1116)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1117)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1118)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1119)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1120)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1121)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1122)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN828)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN829)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN830)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN634)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN635)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN636)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN831)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN588)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN832)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN918)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN919)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN574)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN580)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN589)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN976)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN977)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN978)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN822)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN823)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN824)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN231)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN232)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN233)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN850)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN851)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1123)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1124)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1125)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1126)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1127)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1128)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1129)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1130)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN517)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN081)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN082)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1234)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN929)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN930)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN942)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN943)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN944)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN294)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN295)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN296)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN066)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN074)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN084)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN008)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN009)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN010)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN034)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN035)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN036)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN201)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN202)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN203)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN904)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN905)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN906)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN990)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN991)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN992)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN997)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN998)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN999)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN246)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN247)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN248)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN993)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN994)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN995)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN598)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN565)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN895)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN974)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1162)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1163)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1164)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1165)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1166)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1167)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1168)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1169)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1170)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1171)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1040)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1173)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1174)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1175)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1176)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1177)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1178)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1179)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1180)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1181)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1182)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1183)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1041)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN1185)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1131)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1132)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1133)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1134)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1135)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1136)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1137)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1032)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1139)
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1140)
// 			------------------------------------------------------------------------			
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
		})
		,PAG19 ("T200190", new IPropertyFiller[] {
				 (line,mod200, label) -> addStartLabel(line,label)
				,(line,mod200, label) -> line.append(" ")
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1141)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1142)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1143)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1144)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1145)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1146)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1147)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1148)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1149)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1150)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1151)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1152)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1153)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1154)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1155)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1156)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1157)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1158)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1159)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1160)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1161)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID650)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID651)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID652)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID653)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID654)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID1270)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID1271)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID655)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID656)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID658)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID659)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID660)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID662)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID664)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID665)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.ID666)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC001)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC002)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC003)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC004)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC005)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC006)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC007)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC008)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC009)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC010)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC011)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC012)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC013)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC014)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC015)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC016)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC017)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC018)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC019)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC020)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC021)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC022)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC023)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC024)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC025)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC026)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC027)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC028)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC029)	
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC030)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC031)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC032)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC033)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC034)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC035)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC036)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC037)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC038)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC039)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC040)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC041)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC042)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC043)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC044)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC045)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC046)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC047)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC048)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC049)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC050)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC051)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC052)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC053)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.DC054)
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getNrsAnexoIII(), 22))
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getJustCanarias(), 22))
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getNrsAnexoIV(), 22))
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.text(mod200.getNrsAnexoV(), 22))
				,(line,mod200, label) -> addEndLabel(line,label)
		})
		
		,PAG20 ("T200200", new IPropertyFiller[] {
				 (line,mod200, label) -> addStartLabel(line,label)
				,(line,mod200, label) -> line.append(" ")
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1240)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1241)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1242)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1243)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1244)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1245)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1246)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1247)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1248)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1249)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1250)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1251)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1252)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1253)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1254)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1255)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1256)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1257)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1258)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1259)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1260)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1188)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1189)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1191)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1193)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1194)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1196)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1198)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1199)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1201)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1202)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1203)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1204)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1205)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1206)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1209)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1210)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1211)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1212)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1213)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1214)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1215)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1216)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM890)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM891)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM892)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM503)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM522)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM523)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM273)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM274)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM537)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM955)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM956)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM957)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1217)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1218)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1219)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM538)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM539)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM546)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM893)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM173)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM958)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM898)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM899)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM227)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM959)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM917)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM948)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM291)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM979)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM949)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM950)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM951)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM980)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM952)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM981)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM982)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM983)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM984)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1220)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1221)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1222)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM1223)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM953)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM344)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM985)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM954)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM393)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM150)
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LM506)
				,(line,mod200, label) -> addEndLabel(line,label)
		})
		
		,PAG21 ("T200210", new IPropertyFiller[] {

				(line,mod200, label) -> {
					    
					    addStartLabel(line,label);  // Etiqueta de inicio de pagina
					    
					    // Esta página puede tener complementarias en los apartados que ahora no están en el modelo
					    // Si se añaden dichos apartados hay que tenerlo en cuenta, para hacerlo de forma similar a la pagina 2
					    boolean isComplementary = false;
						line.append(isComplementary?"C":" ");  // Indicador de pagina complementaria
					
						// [...] NO ESTA EN EL MODELO - Operaciones relacionadas con paises o territorios considerados como paraisos fiscales (hasta 6)
						for (int i=0;i<6;i++) {
							line.append(AonFiscalFileUtils.spaces(20));    // Descripción de la operación
							line.append(AonFiscalFileUtils.spaces(20));    // Persona o entidad
							line.append(AonFiscalFileUtils.spaces(1));     // F/J 
							line.append(AonFiscalFileUtils.spaces(2));     // Clave país/territorio
							line.append(AonFiscalFileUtils.zeros(DS));  // Importe
						}
						
						// [...] NO ESTA EN EL MODELO - Tenencia de valores con paraisos fiscales (hasta 6)
						for (int i=0;i<6;i++) {
							line.append(AonFiscalFileUtils.spaces( 1)); // Tipo A - B - C
							line.append(AonFiscalFileUtils.spaces(23)); // Entidad participada
							line.append(AonFiscalFileUtils.spaces( 2)); // Clave país/territorio
							line.append(AonFiscalFileUtils.zeros(DS)); // Valor adquisición
							line.append(AonFiscalFileUtils.zeros(5));            // % participación
						}
						
						// [...] NO ESTA EN EL MODELO - Comunicación del importe neto de la cifra de negocios
						line.append(AonFiscalFileUtils.zeros(DS)); // Grupos de sociedades. Importe neto cifra negocios [987] (Esta casilla solo va en la primera pagina, en las complemetarias va a cero)
						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [1]
						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [2]
						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [3]
						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [4]
						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [5]
						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [6]
						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [7]
						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [8]
						line.append(AonFiscalFileUtils.spaces(9)); // Grupos de sociedades. NIF de las entidades del grupo [9]
						line.append(AonFiscalFileUtils.zeros(DS)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. Importe neto [988] (Esta casilla solo va en la primera pagina, en las complemetarias va a cero)
						line.append(AonFiscalFileUtils.zeros(3)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. Nº establecimientos (Esta casilla solo va en la primera pagina, en las complemetarias va a cero)
						line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [1]
						line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [2]
						line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [3]
						line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [4]
						line.append(AonFiscalFileUtils.spaces(9)); // Comunicación importe neto cifra negocios - No residentes más de un establecimiento. NIF de los establecimientos permanentes [5]
						line.append(AonFiscalFileUtils.zeros(DS)); // Comunicación importe neto cifra negocios - Entidades de crédito, aseguradoras, I.I.C. y sociedades de garantíarecíproca - Importe neto [989] (Esta casilla solo va en la primera pagina, en las complemetarias va a cero)
						
						// Las siguientes 5 casillas solo van con importes en la primera pagina, en las complementarias van a cero
						addUnSignedKey(line, mod200, Mod2002015Key.LQ0N1, 4, 0, isComplementary); // Rég. Entidades navieras en función del tonelaje. Nº de buques  [N1]
						addSignedKey(line, mod200, Mod2002015Key.LQ630,isComplementary); // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de  aplicar la escala [630]
						addSignedKey(line, mod200, Mod2002015Key.LQ631,isComplementary); // Rég. Entidades navieras en función del tonelaje. Importe rentas generadas en trasmisiones de buques [631]
						addSignedKey(line, mod200, Mod2002015Key.LQ632,isComplementary); // Rég. Entidades navieras en función del tonelaje. Compensación bases imponibles negativas períodos anteriores [632]
						addSignedKey(line, mod200, Mod2002015Key.LQ579,isComplementary); // Rég. Entidades navieras en función del tonelaje. Base imponible resultante de la aplicación del régimen [579]
						
						addEndLabel(line,label); // Etiqueta fin de pagina
					}
		})
		
		,PAG22 ("T200220", new IPropertyFiller[] {
				
				(line,mod200, label) -> {
					
					addStartLabel(line,label);  // Etiqueta de inicio de pagina
					
				    // Esta página puede tener complementarias en algunos apartados que ahora no están en el modelo
				    // Si se añaden dichos apartados hay que tenerlo en cuenta, para hacerlo de forma similar a la pagina 2
				    boolean isComplementary = false;			        
				    line.append(isComplementary?"C":" ");  // Indicador de pagina complementaria
					
					// [...] NO ESTA EN EL MODELO - Regimen Especial de la reserva para inversiones en Canarias (21 campos, estos van a cero en las complementarias)
					line.append(AonFiscalFileUtils.zeros(21*DS));
					
					line.append(AonFiscalFileUtils.spaces(15)); // Operaciones con personas o entidades vinculadas - Nº identificación matriz
					line.append(AonFiscalFileUtils.spaces(40)); // Operaciones con personas o entidades vinculadas - Razón social (matriz)
					
					// [...] NO ESTA EN EL MODELO - Operaciones con personas o entidades vinculadas (hasta 3)
					for (int i=0;i<3;i++) {
						line.append(AonFiscalFileUtils.spaces(15)); // NIF
						line.append(AonFiscalFileUtils.spaces( 1)); // F/J
						line.append(AonFiscalFileUtils.spaces(40)); // Apellidos y nombre
						line.append(AonFiscalFileUtils.spaces( 2)); // Código provincia/país
						line.append(AonFiscalFileUtils.spaces( 1)); // Tipo vinculación A a L
						line.append(AonFiscalFileUtils.zeros (DS)); // Importe operación
					}

					// [...] NO ESTA EN EL MODELO - Operaciones con personas o entidades vinculadas (hasta 4)
					for (int i=0;i<4;i++) {
						line.append(AonFiscalFileUtils.spaces(15)); // NIF
						line.append(AonFiscalFileUtils.spaces( 1)); // F/J
						line.append(AonFiscalFileUtils.spaces(40)); // Apellidos y nombre
						line.append(AonFiscalFileUtils.spaces( 1)); // Tipo vinculación A a L
						line.append(AonFiscalFileUtils.spaces( 2)); // Código provincia/país
						line.append(AonFiscalFileUtils.zeros ( 2)); // Tipo operación 1 a 11
						line.append(AonFiscalFileUtils.spaces( 1)); // Ingreso/Pago "I" "P"
						line.append(AonFiscalFileUtils.spaces( 2)); // Método valoración 1a 1b 1c 2a 2b
						line.append(AonFiscalFileUtils.zeros (DS)); // Importe operación
					}
					
					addSignedKey(line, mod200, Mod2002015Key.CP0C1, isComplementary); // Rég. cooperativas - Determ. base imponible - Ingresos computables - Resultados cooperativos [C1]
					addSignedKey(line, mod200, Mod2002015Key.CP0E1, isComplementary); // Rég. cooperativas - Determ. base imponible - Ingresos computables - Resultados extracooperativos [E1]
					addSignedKey(line, mod200, Mod2002015Key.CP0C2, isComplementary); // Rég. cooperativas - Determ. base imponible - Gastos específicos - Resultados cooperativos [C2]
					addSignedKey(line, mod200, Mod2002015Key.CP0E2, isComplementary); // Rég. cooperativas - Determ. base imponible - Gastos específicos - Resultados extracooperativos [E2]
					addSignedKey(line, mod200, Mod2002015Key.CP0C3, isComplementary); // Rég. cooperativas - Determ. base imponible - Gastos generales - Resultados cooperativos [C3]
					addSignedKey(line, mod200, Mod2002015Key.CP0E3, isComplementary); // Rég. cooperativas - Determ. base imponible - Gastos generales - Resultados extracooperativos [E3]
					addSignedKey(line, mod200, Mod2002015Key.CP0C4, isComplementary); // Rég. cooperativas - Determ. base imponible - Gastos Fondo de Educación y Promoción - Resultados cooperativos [C4]
					addSignedKey(line, mod200, Mod2002015Key.CP0E4, isComplementary); // Rég. cooperativas - Determ. base imponible - Gastos Fondo de Educación y Promoción - Resultados extracooperativos [E4]
					addSignedKey(line, mod200, Mod2002015Key.CP0E5, isComplementary); // Rég. cooperativas - Determ. base imponible - Incrementos y disminuciones patrimoniales - Resultados extracooperativos [E5]
					addSignedKey(line, mod200, Mod2002015Key.CP0C6, isComplementary); // Rég. cooperativas - Determ. base imponible - resultado - Resultados cooperativos [C6]
					addSignedKey(line, mod200, Mod2002015Key.CP0E6, isComplementary); // Rég. cooperativas - Determ. base imponible - resultado - Resultados extracooperativos [E6]
					addSignedKey(line, mod200, Mod2002015Key.CP0C7, isComplementary); // Rég. cooperativas - Determ. base imponible - aumentos - Resultados cooperativos [C7]
					addSignedKey(line, mod200, Mod2002015Key.CP0E7, isComplementary); // Rég. cooperativas - Determ. base imponible - aumentos - Resultados extracooperativos [E7]
					addSignedKey(line, mod200, Mod2002015Key.CP0C8, isComplementary); // Rég. cooperativas - Determ. base imponible - disminuciones - Resultados cooperativos [C8]
					addSignedKey(line, mod200, Mod2002015Key.CP0E8, isComplementary); // Rég. cooperativas - Determ. base imponible - disminuciones - Resultados extracooperativos [E8]
					addSignedKey(line, mod200, Mod2002015Key.CP0C9, isComplementary); // Rég. cooperativas - Determ. base imponible - 50% Dotación obligatoria - Resultados cooperativos [C9]
					addSignedKey(line, mod200, Mod2002015Key.CP0E9, isComplementary); // Rég. cooperativas - Determ. base imponible - 50% Dotación obligatoria - Resultados extracooperativos [E9]
					addSignedKey(line, mod200, Mod2002015Key.CPC10, isComplementary); // Rég. cooperativas - Determ. base imponible - Reserva inversiones Canarias - Resultados cooperativos [C10]
					addSignedKey(line, mod200, Mod2002015Key.CPC11, isComplementary); // Rég. cooperativas - Determ. base imponible - Factor de agotamiento - Resultados cooperativos [C11]
					addSignedKey(line, mod200, Mod2002015Key.CPE11, isComplementary); // Rég. cooperativas - Determ. base imponible - Factor de agotamiento - Resultados extracooperativos [E11]
					addSignedKey(line, mod200, Mod2002015Key.CPC12, isComplementary); // Rég. cooperativas - Determ. base imponible - Base imponible - Resultados cooperativos [553]
					addSignedKey(line, mod200, Mod2002015Key.CPE12, isComplementary); // Rég. cooperativas - Determ. base imponible - Base imponible - Resultados extracooperativos [554]
					addEndLabel(line,label); // Etiqueta fin de pagina
				}
			})
		
		,PAG23 ("T200230", new IPropertyFiller[] {
				 (line,mod200, label) -> addStartLabel(line,label)
				,(line,mod200, label) -> line.append(" ")
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ673, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ674, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1224, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ676, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ677, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ678, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ679, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ680, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ681, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ682, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ683, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ684, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ685, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ686, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ687, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ688, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ689, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ690, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ691, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ692, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ693, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ623, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ624, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ672, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ279, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ280, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ281, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ587, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ515, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ900, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ059, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ099, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ100, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ017, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ018, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ019, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ772, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ773, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ777, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ907, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ908, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ909, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ910, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ911, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ912, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ935, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ936, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ937, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ694, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ561, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ695, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1225, mod200.isComplementary())
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ1226, mod200.isComplementary())
				,(line,mod200, label) -> {
					// [...] NO ESTA EN EL MODELO - Operaciones fusión, escisión, canje de valores.
					for (int i=0;i<3;i++) {
						line.append(AonFiscalFileUtils.spaces( 1)); 
						line.append(AonFiscalFileUtils.spaces( 9)); 
						line.append(AonFiscalFileUtils.spaces(40)); 
						line.append(AonFiscalFileUtils.spaces( 9)); 
						line.append(AonFiscalFileUtils.spaces(40)); 
						line.append(AonFiscalFileUtils.zeros ( 8));
						line.append(AonFiscalFileUtils.zeros (DS));
						line.append(AonFiscalFileUtils.zeros (DS));
						line.append(AonFiscalFileUtils.zeros (DS));
					}
				}
				
				,(line,mod200, label) -> addEndLabel(line,label)
		})
		
		// [...] NO ESTA EN EL MODELO - Página 24: Agrup. interés económico y UTES
		// [...] NO ESTA EN EL MODELO - Página 25: Régimen especial de transparencia fiscal internacional
		
		,PAG26 ("T200260", new IPropertyFiller[] {
			 (line,mod200, label) -> addStartLabel(line,label)  // Etiqueta de inicio de pagina
			,(line,mod200, label) -> line.append(" ")  // Indicador de pagina complementaria
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR050) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR051) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR052) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR053) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR054) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR055) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR056) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.TR626, 5, 2) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.TR627, 5, 2) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.TR628, 5, 2) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.TR629, 5, 2) 
			,(line,mod200, label) -> addUnSignedKey(line, mod200, Mod2002015Key.TR625, 5, 2) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR420) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR421) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR426) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR427) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR600) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR402) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR442) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR443) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR444) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR602) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR445) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR446) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR447) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR448) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR604) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR449) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR450) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR451) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR465) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR606) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR474) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR475) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR476) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR477) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR612) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR482) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR483) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR484) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR485) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR616) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR913) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR914) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR915) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR916) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR642) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR486) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR487) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR488) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR489) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR618) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR490) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR491) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR492) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR493) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR620) 
// 			------------------------------------------------------------------------			
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1334)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1335)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1336)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1337)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1332)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1338)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1339)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1340)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1341)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1332)
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
// 			------------------------------------------------------------------------			

			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR494) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR495) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR496) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR497) 
			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR622) 
			
// 			------------------------------------------------------------------------			
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1300)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1301)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1302)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1304)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1043)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1305)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1306)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1307)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1308)
//			,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.TR1044)
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
			,(line,mod200, label) -> line.append("00000000000000000")
// 			------------------------------------------------------------------------			
			,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
		})	

		// [...] NO ESTAN EN EL MODELO - Páginas 27 a 54

		,DID ("T200DID", new IPropertyFiller[] {
				 (line,mod200, label) -> addStartLabel(line,label)   // Etiqueta de inicio de pagina
				,(line,mod200, label) -> line.append(" ")  			 // Indicador de pagina complementaria
				,(line,mod200, label) -> line.append("0")            // Cuenta corriente tributaria "0" o "1" (no se usa)
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getYear(), 4, 0) )      // Identificación - Ejercicio
		        ,(line,mod200, label) -> line.append( AonFiscalFileUtils.unsigned(mod200.getPeriodType(),1,0) )  //	Tipo de ejercicio
		        ,(line,mod200, label) -> line.append( "0A" )                                                     // Período Impositivo "0A"
				,(line,mod200, label) -> line.append( mod200.getPeriodStart() == null ? AonStringUtils.repeat('0', 6) : DATE_FORMAT_6.format(mod200.getPeriodStart()) ) // Período Impositivo Inicio (ddmmaa)
				,(line,mod200, label) -> line.append( mod200.getPeriodEnd()   == null ? AonStringUtils.repeat('0', 6) : DATE_FORMAT_6.format(mod200.getPeriodEnd()) )   // Período Impositivo Fin (ddmmaa)
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterpriseDocument(),9) )  // Identificación - NIF 
				,(line,mod200, label) -> line.append( AonFiscalFileUtils.text(mod200.getEnterpriseName(),40) )     // Identificación - Apellidos y nombre o Razón Social
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ552)   // Liquidación - Base imponible [552]                                    
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.LQ562)   // Liquidación - Cuota íntegra [562]                          
				,(line,mod200, label) -> addSignedKey(line, mod200, Mod2002015Key.BN621)   // Liquidación - Líquido a ingresar o a devolver Estado [621]
				
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
				,(line,mod200, label) -> line.append(AonFiscalFileUtils.zeros(17))         // RESERVADO AEAT
				,(line,mod200, label) -> {
						double importe = mod200.getDoubleValue(Mod2002015Key.BN621); // importe a ingresar o a devolver
						
						line.append( AonFiscalFileUtils.text(importe<0 ? mod200.getDevType() : "",1) );                                   // Devolución - Renuncia o por Transferencia "blanco" "R","D"				
						line.append( AonFiscalFileUtils.signedZero(importe<0 ? Math.abs(importe) : 0.0, DS, DD) );     // Devolución - Importe a devolver
						line.append( AonFiscalFileUtils.text(importe<0 && "D".equals(mod200.getDevType()) ? mod200.getIban() : "",34) );  // Devolución - Número de cuenta IBAN (si devolución por transferencia)
						
						line.append( AonFiscalFileUtils.text(mod200.getBic(),11)); // Devolución - Código SWIFT-BIC
					
						line.append( AonFiscalFileUtils.text(importe>0 ? mod200.getPayType() : "",1) );  // Ingreso - Modalidad de ingreso. Uno de los siguientes valores	"blanco", "I" Adeudo en	cuenta, "H" Efectivo, "U" Domiciliación
						line.append(" ");   // RESERVADO AEAT
						line.append(" ");   // RESERVADO AEAT
						line.append( AonFiscalFileUtils.signedZero(importe>0 ? importe : 0.0, DS, DD) );               // Ingreso - Importe a ingresar
						line.append( AonFiscalFileUtils.text(importe>0 && ("I".equals(mod200.getPayType()) || "U".equals(mod200.getPayType())) ? mod200.getIban() : "",34) ); // Ingreso - Número de cuenta IBAN (si cargo en cuenta o domiciliacion bancaria)			
// --------------------------------------------------------------------------------------------------		
						addSignedKey(line, mod200, Mod2002015Key.BN1020);  // Abono/Compensación - Abono por conversión de activos impuesto diferido - A       
						addSignedKey(line, mod200, Mod2002015Key.BN1021);  // Abono/Compensación - Compensación por conversión de activos impuesto diferido - C
// --------------------------------------------------------------------------------------------------		
						
						line.append(importe == 0 ? "1" : "0"); // Cuota Cero "0" o "1"
					}
				,(line,mod200, label) -> addEndLabel(line,label) // Etiqueta fin de pagina
					
				})
		;
		 
		private String tag;
		private IPropertyFiller[] propertyFillers;
		
		private Pages2015(String t, IPropertyFiller[] pf) {
			this.tag = t;
			this.propertyFillers = pf;
		}

		private void fillPage(Mod2002015 mod200, Writer line) throws IOException {
			
			// Controles determinadas páginas que solo se ponen si están marcados ciertos caracteres
			boolean addPage = true;
			
			// Página 9. Estado de Ingresos y Gastos Reconocidos. Solo si Balance Normal o Abreviado

			if (this == Pages2015.PAG09) {  
				addPage = (mod200.getBalanceType() == BalanceType.NORMAL) ||
				          (mod200.getBalanceType() == BalanceType.ABREVIADO);					
			}

			
			// Página 22. Agrupaciones de interes económico y UTES (regimen especial). Caracteres 013 o 014 marcados
			// La página 22 actualmente no está en el Modelo 200, luego por ahora nunca se pone, ni siquiera está definida en el enumerado
			if (this == Pages2015.PAG22) {
				addPage = (mod200.getDoubleValue(Mod2002015Key.C0013)==1) ||
			              (mod200.getDoubleValue(Mod2002015Key.C0014)==1) || 
			              (mod200.getDoubleValue(Mod2002015Key.C0017)==1) ||
			              (mod200.getDoubleValue(Mod2002015Key.C0018)==1) ||
			              (mod200.getDoubleValue(Mod2002015Key.C0019)==1)
			              ;
			}
			
			// Página 24. Tributación Conjunta. Caracter 028 marcado

			if (this == Pages2015.PAG26) {
				addPage = (mod200.getDoubleValue(Mod2002015Key.C0028)==1);
			}
			
			// Añadir el contenido de la página
			if (addPage) {
	 			for (IPropertyFiller propertyFiller : this.propertyFillers) {
	 				propertyFiller.propertyFill(line, mod200, this.tag);
				}     		
			}
		}
	}
	
	public static void fillWriter(Mod2002015 mod200, Writer line) throws IOException {
		
		line.append("<T2000"+mod200.getYear()+"0A0000>");  // Etiqueta inicio de fichero
		for (Pages2015 page : Pages2015.values()) {     			
			page.fillPage(mod200, line);			
		}	     			
		line.append("</T2000"+mod200.getYear()+"0A0000>");  // Etiqueta fin de fichero
		line.append(AonStringUtils.CR_LF); // Fin de registro. Constante CRLF
		line.close(); 
		
	}
	
/*	
	public static void main(String argv[]) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
		
		try {
			String filename = "/AEAT/200/2013";  // directorio por defecto
			
			// Mostrar una ventana de dialogo para seleccionar ficheros
			JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(filename));
            int res = fc.showOpenDialog(new JFrame());
		    
	        if (res == JFileChooser.APPROVE_OPTION) {
	        	filename = fc.getSelectedFile().getAbsolutePath();
        		System.out.println("***** Inicio Fichero : "+filename);
				System.out.println("");
				InputStream input = new FileInputStream(filename);
				
				// Convertir el fichero a mod200
				Mod2002015 Mod2002015 = new Mod2002015();
				Mod2002013 mod2002013 = Mod2002013Reader.getMod2002013(input);
				Mod2002015Import2013.import2013(Mod2002015, mod2002013);
				input.close();

				// prueba - dejar todas las claves a cero
				Mod2002015.setKeysMap(new EnumMap<Mod2002015Key,DoubleVariable2014>(Mod2002015Key.class));
				
				//Writer line = new StringWriter();
				filename = "c:\\tmp\\prueba.txt";
				BufferedWriter line = new BufferedWriter(new FileWriter(filename));
				fillWriter(Mod2002015, line);
				 
				// Mostramos la longitud de cada pagina 
			    input = new FileInputStream(filename);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();							
				Document doc = db.parse(new InputSource(new InputStreamReader(input,"ISO-8859-1")));
				
				for ( Pages2014 page : Pages2014.values()) {
					NodeList nodeList = doc.getElementsByTagName(page.tag);
					for (int i = 0; i < nodeList.getLength(); i++) {
						Node node = nodeList.item(i);
						String lin = "<" + page.tag + ">" + node.getTextContent() + "</" + page.tag + ">";
						System.out.println(node.getNodeName()+" - "+lin.length());
					}
				}
				
				System.out.println("");
				System.out.println("***** Fin Fichero : "+filename);
				System.out.println("");
				
				// Mostramos el archivo creado, con el bloc de notas
				Runtime.getRuntime().exec("notepad.exe "+filename);
				
	        }	        
		} 
//	        catch (Exception e) {
//			//e.printStackTrace();			
//		}
		finally {
			System.exit(0);
		}
	}
*/	
}
