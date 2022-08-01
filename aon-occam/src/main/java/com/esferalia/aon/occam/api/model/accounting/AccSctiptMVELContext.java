package com.esferalia.aon.occam.api.model.accounting;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Objects;

import org.mvel2.MVEL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.accounting.AccountEntryDetailExpressionScript.AccountEntryDetailExpression;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod303.Mod303Declaration;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public abstract class AccSctiptMVELContext<T> extends HashMap<String, Object> {

	private static final long serialVersionUID = 2589312117223760204L;
	
	public static final String MODEL_KEY = "modelo";
	public static final String MODEL_DECLARATION_KEY = "declaracion";
	
	private AccountEntry accountEntry;
	
	private static class ScriptContext {
		private AccountEntryDetailExpression aede;
		private Account account;
		private Account balancingAccount;
		private AccountEntryDetail aed;
		
		private ScriptContext( AccountEntryDetailExpression aede ) {
			this.aede = aede;
			this.aed = new AccountEntryDetail();
		}

		private AccountEntryDetailExpression getAede() {
			return aede;
		}

		private Account getAccount() {
			return account;
		}

		private ScriptContext setAccount(Account account) {
			this.account = account;
			if ( account == null) {
				this.aed.setAccount(null);
				this.aed.setAccountCode(null);
				this.aed.setAccountDescription(null);
			} else  {
				this.aed.setAccount(getAccount().getId());
				this.aed.setAccountCode(getAccount().getCode());
				this.aed.setAccountDescription(getAccount().getDescription());
			}
			return this;
		}

		private Account getBalancingAccount() {
			return balancingAccount;
		}

		private ScriptContext setBalancingAccount(Account balancingAccount) {
			this.balancingAccount = balancingAccount;
			if ( this.balancingAccount == null) {
				this.aed.setBalancingAccount(null);
				this.aed.setBalancingAccountCode(null);
				this.aed.setBalancingAccountDescription(null);
			} else  {
				this.aed.setBalancingAccount(getBalancingAccount().getId());
				this.aed.setBalancingAccountCode(getBalancingAccount().getCode());
				this.aed.setBalancingAccountDescription(getBalancingAccount().getDescription());
			}
			return this;
		}

		public AccountEntryDetail getAed() {
			return aed;
		}

		public ScriptContext setConcept(String concept) {
			this.aed.setConcept(concept);
			return this;
		}
		
		public ScriptContext setDebit(Double debit) {
			this.aed.setDebit(debit);
			return this;
		}
		
		public ScriptContext setCredit(Double credit) {
			this.aed.setCredit(credit);
			return this;
		}

		public boolean hasAmount() {
			return AonMathUtils.isNotZero(this.aed.getDebit()) || AonMathUtils.isNotZero(this.aed.getCredit()); 
		}
	}
	
	private ScriptContext fillConcept(ScriptContext sc) {
		Object ret = MVEL.eval( sc.getAede().getConceptExpression() , this , this);
		return sc.setConcept(ret == null? null : ret.toString());
	}
	
	private ScriptContext fillAmount(ScriptContext sc) {
		Object ret = MVEL.eval( sc.getAede().getExpression() , this , this);
		Double amount = (ret != null && AonNumberUtils.isNumber(ret.toString())) 
			?AonNumberUtils.toDouble(ret.toString())
			:0.0;
		return  (sc.getAede().isCreditNature())
			?sc.setCredit(amount)
			:sc.setDebit(amount);
	}

	public AccountEntry fillDetails(AONContext ctx, T t, AccountEntryDetailExpressionScript<T> script) {
		if (isEmpty()) fillContext();
		if (this.accountEntry == null) this.accountEntry = fillAccountEntry( t );
		
		if (this.accountEntry == null)
			throw new IllegalStateException("No se ha rellenado una cabecera de apunte");
		script
			.getDetails()
			.stream()
			.map( ScriptContext::new )
			.map( sc -> sc.setAccount( AccountDAO.get(ctx, sc.getAede().getAccount())) )
			.map( sc -> sc.setBalancingAccount( AccountDAO.get(ctx, sc.getAede().getBalancingAccount())) )
			.map( this::fillConcept )
			.map( this::fillAmount )
			.filter( sc -> sc.hasAmount() )
			.forEach(sc -> addDetail(this.accountEntry, sc.getAed()));
			;
		return this.accountEntry;
	}
	
	private AccountEntry addDetail(AccountEntry ae, AccountEntryDetail aed) {
		AccountEntryDetail added =  ae.getDetails()
			.stream()
			.filter(Objects::nonNull)
			.filter(det -> AonNumberUtils.equals(det.getAccount(), aed.getAccount()))
			.map(det -> det.addDebit(aed.getDebit()))
			.map(det -> det.addCredit(aed.getCredit()))
			.findFirst()
			.orElse(null);
		return (added == null) ? ae.addDetail(aed) : ae;
	}

	public Object getOrThrow( String key ) {
		if ( containsKey(key)) return get(key);
		throw new IllegalArgumentException(MessageFormat.format("No existe una valor válido para la variable {0}", key));
	}
	
	public abstract AccountEntry fillAccountEntry(T t);
	public abstract void fillContext();
	

	// ******************************************
	// **********************  EXPRESSION METHODS
	// ******************************************
	
		// ***********************************
		// **********************  MATEMÁTICAS
		// ***********************************
	
	// Devuelve el valor absoluto redondeado a 2 dígitos.
	public double abs(double value) {
		return AonMathUtils.absRounded(value);
	}

		// ********************************
		// **********************  FISCALES
		// ********************************
	public FiscalModel model() {
		Object obj = getOrThrow( MODEL_KEY );
		if (obj instanceof FiscalModel) return (FiscalModel) obj;
		throw new IllegalArgumentException(MessageFormat.format("La variable {0} no es una declaración fiscal válida.", MODEL_KEY));	
	}
	public String nombreModelo() {
		return MessageFormat.format("Mod. {0}",model().getModelFullName());	
	}
	
	public boolean esComplementaria() {
		return model().isComplementary();
	}
	public boolean esSustitutiva() {
		return model().isReplacement();
	}
	
	public boolean aIngresar() {
		return FiscalModelDeclarationType.isToDeposit(model().getDeclarationResultType());
	}
		// *******************************************
		// **********************  FISCALES MODELO 303
		// *******************************************
	public Mod303 mod303() {
		Object obj = get( MODEL_KEY );
		if (obj instanceof Mod303) return (Mod303) obj;
		throw new IllegalArgumentException(MessageFormat.format("La variable {0} no es una declaración del modelo 303 válido.", MODEL_KEY));	
	}
	public Mod303Declaration declaracion() {
		Object obj = get( MODEL_DECLARATION_KEY );
		if (obj instanceof Mod303Declaration ) return (Mod303Declaration) obj;
		throw new IllegalArgumentException(MessageFormat.format("La variable {0} no es una declaración válida.", MODEL_DECLARATION_KEY));	
	}
	
	public double prorrataIVA() {
		return 0.0;
	}
}

