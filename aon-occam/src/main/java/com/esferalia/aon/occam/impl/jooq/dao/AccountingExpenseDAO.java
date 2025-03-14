package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.accounting.AccountingExpense;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.esferalia.aon.occam.impl.jooq.dao.AccountEntryDAO.AccountEntryOrder;
import com.esferalia.aon.occam.server.accounting.AccountEntryUtils;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountingExpenseDAO {
	
	private AccountingExpenseDAO() {
	
	}
	
	// --------------------------------------------------------------- LECTURA
	public static Optional<AccountingExpense> get(CloseableAONContext ctx, int domain, Integer accountEntryId, IDAOCallback cbk) {
		return stream(ctx
			,new AccountEntryParams()
				.setDomain( domain )
				.setAccountEntryId( accountEntryId )
			, 0
			, 1
			, AccountEntryOrder.ORDER_CREATION_DATE_DESC
			, cbk)
			.findFirst();
	}
	public static Stream<AccountingExpense> stream(CloseableAONContext ctx
			, int domain
			, String query
			, int offset
			, int limit
			, IDAOCallback cbk) {
		return stream(ctx
			,new AccountEntryParams()
				.setDomain( domain )
				.setQuery( query)
			, offset
			, limit
			,AccountEntryOrder.ORDER_CREATION_DATE_DESC
			, cbk);
	}
	
	private static Stream<AccountingExpense> stream(CloseableAONContext ctx
			, AccountEntryParams params
			, int offset
			, int limit
			, AccountEntryOrder orderBy
			, IDAOCallback cbk) {
		ctx.checkRead();
		return (
			params.setType( AccountEntryType.OTHER_EXPENSES ).hasDetailProperties()
				? AccountEntryDAO.fetchByLines(ctx,p -> AccountEntryUtils.getFilterByLines(ctx,p, params),offset,limit,orderBy)
				: AccountEntryDAO.fetch(ctx,p -> AccountEntryUtils.getFilterByHeader(ctx,p, params),offset,limit,orderBy)
			)
			.map( ae -> fillAccountingExpense( ctx , ae))
			.map( ai -> fillFinance( ctx , ai))
			.onClose(() -> {
				if (cbk != null) {
					cbk.onFinish();
				}
			})
			;
	}
	
	private static AccountingExpense fillAccountingExpense(AONContext ctx, AccountEntry ae) {
		AccountingExpense ai = new AccountingExpense()
			.setDomain( ae.getDomain() )
			.setAccountEntry( ae )
			.setActivity( ae.getActivity( ) )
			.setDate( ae.getEntryDate() )
			.setComments( ae.getComments() )
		;
		AonCollectionUtils.stream( ae.getDetails() )
			.filter( aed -> AonStringUtils.startsWith( aed.getAccountCode(), "57"))
			.findFirst()
			.ifPresent( aed -> 
				ai.setCashAccount( AccountDAO.get( ctx, aed.getAccount() ) )
				  .setExpAccount( AccountDAO.get( ctx, aed.getBalancingAccount() ) )
				  .setConcept( aed.getConcept())
				  .setReferenceCode( aed.getDocumentNumber())
				  .setAmount( AonMathUtils.round(aed.getDebit() - aed.getCredit())))
			;
		return ai;
	}
	
	private static AccountingExpense fillFinance(AONContext ctx, AccountingExpense ai) {
		return ai;
	}
	// --------------------------------------------------------------- ESCRITURA
	public static AccountingExpense save(AONContext ctx, AccountingExpense expense) {
		ctx.checkWrite();
		AccountingExpenseValidation.validateExpense(ctx,expense);
		expense.getAccountEntry()
			.filter( ae -> ae.getId() != null )
			.ifPresentOrElse( 
				 id -> update(ctx,expense) 
				,() -> insert(ctx,expense));
		return expense;
	}
	
	private static AccountingExpense update(AONContext ctx, AccountingExpense expense) {
		return expense.getAccountEntry()
			.map( ae -> {
				delete(ctx, ae);
				expense.setAccountEntry( null );
				expense.setFinance( null );
				return insert( ctx, expense );
			})
			.orElseThrow( () -> new AonCoreException("No se pudo modificar el gasto."));
	}
	
	private static AccountingExpense insert(AONContext ctx, AccountingExpense expense) {
		initializeAccountEntry( ctx, expense );
		saveBasicAccountEntry( ctx, expense );
		expense.getCreditor()
			.ifPresent( c -> saveAndRecordFinanace( ctx, c, expense ));
		return expense;
	}
	
	private static Account obtainBankAccount(AONContext ctx, AccountingExpense expense) {
		return expense.getCashAccount()
			.or( () -> expense.getBank()
				.map( RegistryBank::getAccount ))
				.filter( a -> a != null && a.getId() != null )
				.or(() -> expense.getBank()
					.map(rbank -> {
						String accountCode = AccountDAO.getNextAccountCode(ctx,"5720" );
						String accountDescription = rbank.getAlias();
						if(AonStringUtils.isEmpty(accountDescription) ) {
							accountDescription = rbank.getFullName();
						}
						if(AonStringUtils.isEmpty(accountDescription) ) {
							accountDescription = accountCode;
						}
						Account newAccount = AccountDAO.save(ctx, 
							new Account()
								.setDomain(expense.getDomain())
								.setCode(accountCode)
								.setDescription( accountDescription )
								.setAlias( rbank.getAlias() )
								.setActive( true));
						RegistryBankDAO.updateAccount(ctx, rbank.getId(), newAccount.getId());
						return newAccount;
					})
				)
				.orElseThrow( () -> new AonCoreException("Bank Account not initialized")
			);
	}
	
	private static AccountingExpense saveBasicAccountEntry(AONContext ctx, AccountingExpense expense) {
		AccountEntry ae = expense.getAccountEntry()
			.orElseThrow( () -> new AonCoreException( "AccountEntry not initialized" ) );
		Account expAccount = expense.getExpAccount()
			.orElseThrow( () -> new AonCoreException( "Exp Account not initialized" ) );
		Account bankAccount = obtainBankAccount(ctx,expense);
		
		ae.setDetails( new LinkedList<>());
		ae.getDetails().add( 
			new AccountEntryDetail()
				.setAccount( expAccount.getId() )
				.setConcept( expense.getConcept() )
				.setDocumentNumber( expense.getReferenceCode() )
				.setCredit( expense.getAmount())
				.setBalancingAccount( bankAccount.getId() )
		);
		ae.getDetails().add( 
			new AccountEntryDetail()
				.setAccount( bankAccount.getId() )
				.setConcept( expense.getConcept() )
				.setDocumentNumber( expense.getReferenceCode() )
				.setDebit( expense.getAmount())
				.setBalancingAccount( expAccount.getId() )
		);
		AccountEntryDAO.save( ctx, ae);
		return expense;
	}

	private static AccountingExpense initializeAccountEntry(AONContext ctx, AccountingExpense expense) {
		AccountPeriod period = AccountPeriodDAO.getPeriod(ctx, expense.getDate() );
		if (period == null) {
			throw new AonCoreException( AonError.WRONG_PERIOD.format( expense.getDate() ) );
		}
		AccountEntry ae = new AccountEntry()
			.setDomain( expense.getDomain() )
			.setPeriod(period.getId())
			.setEntryDate( expense.getDate() )
			.setEntryType( AccountEntryType.OTHER_EXPENSES )
			.setActivity( expense.getActivity().orElse(null) );
		expense.getCreditor().ifPresentOrElse(
		   c -> ae.setConfidential( c.isConfidential()  )
		 ,() -> ae.setConfidential( false )
		);
		return expense.setAccountEntry(ae);
	}
	
	private static AccountingExpense saveAndRecordFinanace(AONContext ctx, Creditor cred, AccountingExpense expense) {
		Finance finance = new Finance();
		finance.setDomain( expense.getDomain() );
		finance.setPayment( true );
		finance.setRegistry( RegistryDAO.get(ctx,cred.getId() ));
		finance.setScope( cred.getScope() );
		finance.setSecurityLevel( cred.getSecurityLevel() );
		finance.setRegistryDocument( cred.getDocument() );
		finance.setRegistryDocumentType( cred.getDocumentType() );
		finance.setRegistryDocumentCountry( cred.getDocumentCountry() );
		finance.setRegistryName( cred.getName() );
		Account account = AccountDAO.get( ctx, cred.getAccount() );
		if (account != null) {
			finance.setRegistryAccountId( account.getId() );
			finance.setRegistryAccountCode( account.getCode() );
			finance.setRegistryAccountDescription( account.getDescription() );
		}
		finance.setAmount( expense.getAmount() );
		finance.setConcept( AonStringUtils.abbreviate( expense.getConcept() , 32) );
		finance.setDueDate( expense.getDate() );
		finance.setManual(true);
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setRemarks(expense.getComments());
		expense.getBank().ifPresent( b -> {
			finance.setBankAccount( b.getBankAccount() );	
			finance.setBankAlias( b.getAlias() );
			finance.setBic( b.getBic() );
		});
		Integer financeId = FinanceDAO.save( ctx, finance);
		finance.setId( financeId );
		expense.getAccountEntry().ifPresent( ae -> {
			FinanceTracking tracking = new FinanceTracking()
				.setDomain(finance.getDomain())
				.setFinance(finance)
				.setTrackingDate( finance.getDueDate() )
				.setAmount(finance.getAmount() )
				.setRegistryBank( expense.getBank().orElse( null) )
				.setDescription( expense.getConcept() )
				.setAmount( finance.getAmount() )
			;
			FinanceTrackingDAO.pay( ctx, tracking, ae );
		});
		Finance savedFinance = FinanceDAO.getFinance( ctx, financeId);
		expense.setFinance(savedFinance);
		return expense;
	}


	private static record AccountingExpenseContext(AONContext ctx,AonConfiguration config, AccountingExpense exp){}
	private static class AccountingExpenseValidation {
		
		private AccountingExpenseValidation() {
			
		}
		
		private static final Consumer<AccountingExpenseContext> EMPTY_DOMAIN = aec -> {
			if (aec.exp.getDomain() == 0) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		private static final Consumer<AccountingExpenseContext> EMPTY_DATE = aec -> {
			if (aec.exp.getDate() == null)
				throw new AonCoreException(AonError.EMPTY_DATE.getMessage());
		};
		
		private static final Consumer<AccountingExpenseContext> EMPTY_EXP_ACCOUNT = 
			aec -> aec.exp.getExpAccount()
				.filter( a -> a.getId() != null )
				.orElseThrow( () -> new AonCoreException(AonError.EMPTY_EXP_ACCOUNT.getMessage()))
		;
		
		private static final Consumer<AccountingExpenseContext> EMPTY_CONCEPT = aec -> {
			if (AonStringUtils.isEmpty( aec.exp.getConcept() ))
				throw new AonCoreException(AonError.EMPTY_CONCEPT.getMessage());
		};

		private static final Consumer<AccountingExpenseContext> EMPTY_AMOUNT = aec -> {
			if (AonMathUtils.isZero( aec.exp.getAmount() ))
				throw new AonCoreException(AonError.EMPTY_AMOUNT.getMessage());
		};
		
		private static final Consumer<AccountingExpenseContext> EMPTY_DEFAULT_CASH_ACCOUNT = aec -> {
			if (aec.exp.getBank().isEmpty() && aec.exp.getCashAccount().isEmpty()) {
				throw new AonCoreException(AonError.EMPTY_BANK_ACCOUNT.getMessage());
			}
		};
		
		public static void validateExpense(AONContext ctx, AccountingExpense expense) throws AonCoreException {
			AonConfiguration config = ConfigurationDAO.getConfiguration(ctx, expense.getDate());
			validateExpense(ctx, config, expense);
		}
		public static void validateExpense(AONContext ctx,AonConfiguration config, AccountingExpense expense) throws AonCoreException {
			EMPTY_DOMAIN
				.andThen(EMPTY_DATE)
				.andThen(EMPTY_EXP_ACCOUNT)
				.andThen(EMPTY_CONCEPT)
				.andThen(EMPTY_AMOUNT)
				.andThen(EMPTY_DEFAULT_CASH_ACCOUNT)
				.accept(new AccountingExpenseContext(ctx,config,expense));
		}
	}
	
	public static void delete(AONContext ctx, AccountEntry ae) {
		if (ae == null) throw new AonCoreException("AccountEntry is mandatory"); 
		if (ae.getEntryType() != AccountEntryType.OTHER_EXPENSES) 
			throw new AonCoreException("El apunte que se quiere borrar no es \"Otros Ingresos\"");
		AccountEntryDAO.delete( ctx, ae.getId() );
	}
	
	public static void unrecord(AONContext ctx, int domain, int accountEntryId) {
		ctx.getDslContext()
			.select(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING)
			.from( ACCOUNT_ENTRY_FINANCE_TRACKING )
			.where(ACCOUNT_ENTRY_FINANCE_TRACKING.ACCOUNT_ENTRY.eq(accountEntryId))
			.and(ACCOUNT_ENTRY_FINANCE_TRACKING.DOMAIN.eq(domain))
			.fetch()
			.stream()
			.forEach( rec -> {
				Integer ftId = rec.getValue(ACCOUNT_ENTRY_FINANCE_TRACKING.FINANCE_TRACKING);
				FinanceTracking ft = FinanceTrackingDAO.getFinanceTracking( ctx, ftId );
				FinanceTrackingDAO.delete( ctx, ft);
				if (ft.getFinance() != null) {
					FinanceDAO.delete( ctx, ft.getFinance().getId() );
				}
			});
		
	}

}
