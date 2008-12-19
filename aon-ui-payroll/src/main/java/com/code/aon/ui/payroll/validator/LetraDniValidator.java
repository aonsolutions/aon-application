package com.code.aon.ui.payroll.validator;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class LetraDniValidator implements Validator {

	/**
	 * Validates the letter of DNI
	 * 
	 * @param facesContext
	 * @param uiComponent
	 * @param object
	 * @throws ValidatorException
	 */
	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object object)
			throws ValidatorException {
		FacesContext ctx = FacesContext.getCurrentInstance();
		
		System.out.println("***********************  letraDniValidator *******************");
		/*
		 try {
			String cId = uiComponent.getId();
			cId = cId.substring( 0, cId.indexOf( '_' ) );
			BigDecimal value = (BigDecimal) object;
			ITransferObject to = FormUtil.getController( cId ).getTo();
			
			if(to == null){
		    	FacesMessage fm = AonUtil.getMessage( ctx, "aon_payroll_null_to", null );
				throw new ValidatorException( fm );
		    }
			if ( value == null )
				PropertyUtils.setProperty( to, cId, new BigDecimal( 0d ) );
		    if ( value.doubleValue() < 0.00) {
		    	FacesMessage fm = AonUtil.getMessage( ctx, "aon_payroll_1433", null );
				throw new ValidatorException( fm );
		    }
		    if ( value.doubleValue() > 100) {
		    	FacesMessage fm = AonUtil.getMessage( ctx, "aon_payroll_1433", null );
				throw new ValidatorException( fm );
		    }
		} catch (IllegalAccessException e) {
			throw new ValidatorException( AonUtil.getMessage( ctx, e.getMessage(), null ) );
		} catch (InvocationTargetException e) {
			throw new ValidatorException( AonUtil.getMessage( ctx, e.getMessage(), null ) );
		} catch (NoSuchMethodException e) {
			throw new ValidatorException( AonUtil.getMessage( ctx, e.getMessage(), null ) );
		}
	
	*/
	}
	

	
	
	
	/*
	public function comprobarLetraDni (pChDni as char, pChInd as char) return boolean
	// Funcion que comprueba que un Dni esta correctamente introducido.
	Objects begin
	    lChLetraOriginal        as char
	    lChLetraResultado       as char
	    lChNumeroOriginal       as char
	    lInNumeroOriginal       as integer
	    lInNumeroResultado      as integer
	    lChLetras               as char default "RWAGMYFPDXBNJZSQVHLCKET"
	end
	begin
	    if pChInd == "1" then begin
	        lChLetraOriginal = "";
	        lChLetraResultado = "";
	        lInNumeroOriginal = 0;
	        pChDni = pChDni.Upcase();
	        lChNumeroOriginal = cogerParteValida (pChDni);
	        
	        if lChNumeroOriginal.SubString(lChNumeroOriginal.Length, 1) between 0 and 9 then
	            lInNumeroResultado = 8;
	        else
	            lInNumeroResultado = 9;
	        
	        lChNumeroOriginal = "0".StrRepeat(lInNumeroResultado - lChNumeroOriginal.Length) + lChNumeroOriginal;
	        
	        if (lChNumeroOriginal.Length() == 8) then begin
	            if (comprobarDigitos(lChNumeroOriginal)) then begin
	                // entonces añadiremos la letra de control que le corresponda
	                lInNumeroResultado = calcularResultado (lChNumeroOriginal);
	                if (lInNumeroResultado == 0) then lInNumeroResultado = 23;
	                lChLetraResultado = lChLetras[lInNumeroResultado,1];
	                pChDni = lChNumeroOriginal + lChLetraResultado;
	                return True;
	            end       
	            else begin
	                return False;     
	            end
	        end
	        else if (lChNumeroOriginal.Length() == 9) then begin
	            lChLetraOriginal = lChNumeroOriginal.SubString(9,1);
	            if (comprobarDigitos(lChNumeroOriginal.SubString(1,8))) then begin
	                lChNumeroOriginal = lChNumeroOriginal.SubString(1,8);
	                lInNumeroResultado = calcularResultado (lChNumeroOriginal);            
	                if (lInNumeroResultado == 0) then lInNumeroResultado = 23;    
	                lChLetraResultado = lChLetras[lInNumeroResultado,1];    
	                if (lChLetraResultado == lChLetraOriginal) then
	                    return True;
	                else begin
	                    return False;                
	                end
	            end
	            else begin
	                return False;
	            end
	        end
	        else begin
	            return False;
	        end
	    end
	    else if pChInd == "6" then begin
	        lChLetraOriginal = "";
	        lChLetraResultado = "";
	        lInNumeroOriginal = 0;
	        pChDni = pChDni.Upcase();
	        {
	        if not (pChDni.SubString(1, 1) in ("X", "Y", "Z")) then
	            return false;
	        }
	        switch pChDni.SubString(1, 1) begin
	            case "X": lChNumeroOriginal = "0" + cogerParteValida(pChDni.SubString(2, pChDni.Length - 1));
	            case "Y": lChNumeroOriginal = "1" + cogerParteValida(pChDni.SubString(2, pChDni.Length - 1));
	            case "Z": lChNumeroOriginal = "2" + cogerParteValida(pChDni.SubString(2, pChDni.Length - 1));
	            default: return false;
	        end
	        
	        //lChNumeroOriginal = cogerParteValida (pChDni.SubString(2, pChDni.Length - 1));
	        
	        if lChNumeroOriginal.SubString(lChNumeroOriginal.Length, 1) between 0 and 9 then
	            lInNumeroResultado = 8;
	        else
	            lInNumeroResultado = 9;
	        
	        lChNumeroOriginal = "0".StrRepeat(lInNumeroResultado - lChNumeroOriginal.Length) + lChNumeroOriginal;
	        
	        if (lChNumeroOriginal.Length() == 8) then begin
	            if (comprobarDigitos(lChNumeroOriginal)) then begin
	                // entonces añadiremos la letra de control que le corresponda
	                lInNumeroResultado = calcularResultado (lChNumeroOriginal);
	                if (lInNumeroResultado == 0) then lInNumeroResultado = 23;
	                lChLetraResultado = lChLetras[lInNumeroResultado,1];
	                pChDni += lChLetraResultado;
	                return True;
	            end       
	            else begin
	                return False;     
	            end
	        end
	        else if (lChNumeroOriginal.Length() == 9) then begin
	            lChLetraOriginal = lChNumeroOriginal.SubString(9,1);
	            if (comprobarDigitos(lChNumeroOriginal.SubString(1,8))) then begin
	                lChNumeroOriginal = lChNumeroOriginal.SubString(1,8);
	                lInNumeroResultado = calcularResultado (lChNumeroOriginal);            
	                if (lInNumeroResultado == 0) then lInNumeroResultado = 23;    
	                lChLetraResultado = lChLetras[lInNumeroResultado,1];    
	                if (lChLetraResultado == lChLetraOriginal) then
	                    return True;
	                else begin
	                    return False;                
	                end
	            end
	            else begin
	                return False;
	            end
	        end
	        else begin
	            return False;
	        end
	    end
	    else if pChInd == "CIAS" then begin
	        lChLetraOriginal = "";
	        lChLetraResultado = "";
	        lInNumeroOriginal = 0;
	        pChDni = pChDni.Upcase();
	        lChNumeroOriginal = cogerParteValida (pChDni);
	        
	        if lChNumeroOriginal.SubString(lChNumeroOriginal.Length, 1) between 0 and 9 then
	            lInNumeroResultado = 10;
	        else
	            lInNumeroResultado = 11;
	        
	        lChNumeroOriginal = "0".StrRepeat(lInNumeroResultado - lChNumeroOriginal.Length) + lChNumeroOriginal;
	        
	        if (lChNumeroOriginal.Length() == 10) then begin
	            if (comprobarDigitos(lChNumeroOriginal)) then begin
	                // entonces añadiremos la letra de control que le corresponda
	                lInNumeroResultado = calcularResultado (lChNumeroOriginal);
	                if (lInNumeroResultado == 0) then lInNumeroResultado = 23;
	                lChLetraResultado = lChLetras[lInNumeroResultado,1];
	                pChDni = lChNumeroOriginal + lChLetraResultado;
	                return True;
	            end       
	            else begin
	                return False;     
	            end
	        end
	        else if (lChNumeroOriginal.Length() == 11) then begin
	            lChLetraOriginal = lChNumeroOriginal.SubString(11,1);
	            if (comprobarDigitos(lChNumeroOriginal.SubString(1,10))) then begin
	                lChNumeroOriginal = lChNumeroOriginal.SubString(1,10);
	                lInNumeroResultado = calcularResultado (lChNumeroOriginal);            
	                if (lInNumeroResultado == 0) then lInNumeroResultado = 23;    
	                lChLetraResultado = lChLetras[lInNumeroResultado,1];    
	                if (lChLetraResultado == lChLetraOriginal) then
	                    return True;
	                else begin
	                    return False;                
	                end
	            end
	            else begin
	                return False;
	            end
	        end
	        else begin
	            return False;
	        end
	    end
	end
	*/
}
