package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AccountEntryFinanceTracking.ACCOUNT_ENTRY_FINANCE_TRACKING;

import java.util.LinkedList;
import java.util.Objects;
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
			.map( ae -> createAccountingExpense( ctx , ae))
			.map( ai -> fillFinance( ctx , ai))
			.map( ai -> fillAccountingExpense( ctx , ai))
			.onClose(() -> {
				if (cbk != null) {
					cbk.onFinish();
				}
			})
			;
	}
	private static AccountingExpense createAccountingExpense(AONContext ctx, AccountEntry ae) {
		return new AccountingExpense()
			.setDomain( ae.getDomain() )
			.setAccountEntry( ae )
			.setActivity( ae.getActivity( ) )
			.setDate( ae.getEntryDate() )
			.setComments( ae.getComments() )
		;
	}

	private enum AccountType {
		EXP_ACCOUNT {
			@Override
			AccountingExpense visit( AONContext ctx, AccountingExpense inc, AccountEntryDetail aed ) {
				return inc.setExpAccount( AccountDAO.get( ctx, aed.getAccount() ) )
					.setConcept( aed.getConcept())
					.setReferenceCode( aed.getDocumentNumber())
					.setAmount( AonMathUtils.round(aed.getDebit() - aed.getCredit()));
			}
			
			@Override
			boolean accept(String code) {
				return (AonStringUtils.startsWith( code, "6"));
			}
		},
		CASH_ACCOUNT {
			@Override
			AccountingExpense visit( AONContext ctx, AccountingExpense inc, AccountEntryDetail aed  ) {
				return inc.setCashAccount( AccountDAO.get( ctx, aed.getAccount() ) )
					.setConcept( aed.getConcept())
					.setReferenceCode( aed.getDocumentNumber());
			}
			
			@Override
			boolean accept(String code) {
				return (AonStringUtils.startsWith( code, "5"));
			}
		},
		CREDITOR_ACCOUNT {
			@Override
			AccountingExpense visit( AONContext ctx, AccountingExpense inc, AccountEntryDetail aed  ) {
				return inc.setCashAccount( AccountDAO.get( ctx, aed.getAccount() ) )
					.setConcept( aed.getConcept())
					.setReferenceCode( aed.getDocumentNumber());
			}

			@Override
			boolean accept(String code) {
				return (AonStringUtils.startsWith( code, "4"));
			}
		},
		;
		static Optional<AccountType> get( String code ) {
			return AonCollectionUtils.stream( AccountType.values() )
				.filter( at -> at.accept( code ) )
				.findFirst();
		}
		
		abstract boolean accept( String code);
		abstract AccountingExpense visit( AONContext ctx, AccountingExpense inc, AccountEntryDetail aed  );
	}
	
	private static AccountingExpense fillAccountingExpense(AONContext ctx, AccountingExpense ai) {
		ai.getAccountEntry()
			.ifPresent(	ae-> AonCollectionUtils.stream( ae.getDetails() )
				.forEach( aed -> AccountType.get( aed.getAccountCode() ).ifPresent( at -> at.visit( ctx, ai, aed)))
		);
		return ai;
	}
	
	private static AccountingExpense fillFinance(AONContext ctx, AccountingExpense ai) {
		ai.setFinance(
			ai.getAccountEntry()
				.map( ae -> FinanceEntryDAO.getFinanceEntry(ctx, ae.getId()) )
				.filter( fe -> fe.getTrackings() != null )
				.filter( fe -> fe.getTrackings().size() == 1 )
				.map( fe -> AonCollectionUtils.stream(fe.getTrackings().values()).findFirst().orElse(null) )
				.filter( Objects::nonNull )
				.map( ft -> FinanceTrackingDAO.getFinanceTracking( ctx, ft.getId() ))
				.filter( Objects::nonNull )
				.map( ft -> {
					ai.setBank( ft.getRegistryBank() );
					return ft;
				})
				.map( ft -> ft.getFinance() )
				.filter( Objects::nonNull )
				.map( f -> FinanceDAO.getFinance(ctx, f.getId()) )
				.orElse(null)
		)
		.setCreditor( 
			ai.getFinance()
				.map(Finance::getRegistry)
				.filter( r -> r != null )
				.map(r -> r.getId() )
				.map(id -> CreditorDAO.get( ctx, p -> p.getIdProperty().eq(id).and(p.getDomainProperty().eq(ai.getDomain()))))
				.orElse(null)
		);
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
			.orElseThrow( () -> new AonCoreException("No se pudo modificar el ingreso."));
	}
	
	private static AccountingExpense insert(AONContext ctx, AccountingExpense expense) {
		initializeAccountEntry( ctx, expense );
		saveAccountEntry( ctx, expense );
		expense.getCreditor()
			.ifPresent( c -> saveAndRecordFinanace( ctx, c, expense ));
		return expense;
	}
	
	private static Optional<Account> obtainCreditorAccount(AONContext ctx, AccountingExpense expense) {
		return expense.getCreditor()
			.map( c -> CreditorDAO.ensureAccount( ctx, c.getId()))
		;
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
	
	private static AccountingExpense saveAccountEntry(AONContext ctx, AccountingExpense expense) {
		AccountEntry ae = expense.getAccountEntry()
			.orElseThrow( () -> new AonCoreException( "AccountEntry not initialized" ) );
		Account expAccount = expense.getExpAccount()
			.orElseThrow( () -> new AonCoreException( "Exp Account not initialized" ) );
		Account bankAccount = obtainBankAccount(ctx,expense);
		Optional<Account> creditorAccount = obtainCreditorAccount(ctx,expense);
		
		ae.setDetails( new LinkedList<>());
		
		ae.getDetails().add( 
			new AccountEntryDetail()
				.setAccount( expAccount.getId() )
				.setConcept( expense.getConcept() )
				.setDocumentNumber( expense.getReferenceCode() )
				.setDebit( expense.getAmount())
				.setBalancingAccount(creditorAccount.map( Account::getId ).orElse(bankAccount.getId()))
		);
		creditorAccount.ifPresent( ca -> {
			ae.getDetails().add( 
				new AccountEntryDetail()
					.setAccount( ca.getId() )
					.setConcept( expense.getConcept() )
					.setDocumentNumber( expense.getReferenceCode() )
					.setCredit( expense.getAmount())
					.setBalancingAccount(expAccount.getId())
			);
			ae.getDetails().add( 
				new AccountEntryDetail()
					.setAccount( ca.getId() )
					.setConcept( expense.getConcept() )
					.setDocumentNumber( expense.getReferenceCode() )
					.setDebit( expense.getAmount())
					.setBalancingAccount(bankAccount.getId())
			);
		});
		ae.getDetails().add( 
			new AccountEntryDetail()
				.setAccount( bankAccount.getId() )
				.setConcept( expense.getConcept() )
				.setDocumentNumber( expense.getReferenceCode() )
				.setCredit( expense.getAmount())
				.setBalancingAccount(creditorAccount.map( Account::getId ).orElse(expAccount.getId()))
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
			.setActivity( expense.getActivity().orElse(null) )
			.setComments( expense.getComments() );
		expense.getCreditor().ifPresentOrElse(
		   c -> ae.setConfidential( c.isConfidential()  )
		 ,() -> ae.setConfidential( false )
		);
		return expense.setAccountEntry(ae);
	}
	
	private static AccountingExpense saveAndRecordFinanace(AONContext ctx, Creditor cred, AccountingExpense expense) {
		Finance finance = new Finance();
		finance.setDomain( expense.getDomain() );
		finance.setPayment( false );
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


	private static record AccountingExpenseContext(AONContext ctx,AonConfiguration config, AccountingExpense inc){}
	private static class AccountingExpenseValidation {
		
		private AccountingExpenseValidation() {
			
		}
		
		private static final Consumer<AccountingExpenseContext> EMPTY_DOMAIN = aec -> {
			if (aec.inc.getDomain() == 0) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		private static final Consumer<AccountingExpenseContext> EMPTY_DATE = aec -> {
			if (aec.inc.getDate() == null)
				throw new AonCoreException(AonError.EMPTY_DATE.getMessage());
		};
		
		private static final Consumer<AccountingExpenseContext> EMPTY_EXP_ACCOUNT = 
			aec -> aec.inc.getExpAccount()
				.filter( a -> a.getId() != null )
				.orElseThrow( () -> new AonCoreException(AonError.EMPTY_EXP_ACCOUNT.getMessage()))
		;
		
		private static final Consumer<AccountingExpenseContext> EMPTY_CONCEPT = aec -> {
			if (AonStringUtils.isEmpty( aec.inc.getConcept() ))
				throw new AonCoreException(AonError.EMPTY_CONCEPT.getMessage());
		};

		private static final Consumer<AccountingExpenseContext> EMPTY_AMOUNT = aec -> {
			if (AonMathUtils.isZero( aec.inc.getAmount() ))
				throw new AonCoreException(AonError.EMPTY_AMOUNT.getMessage());
		};
		
		private static final Consumer<AccountingExpenseContext> EMPTY_DEFAULT_CASH_ACCOUNT = aec -> {
			if (aec.inc.getBank().isEmpty() && aec.inc.getCashAccount().isEmpty()) {
				throw new AonCoreException(AonError.EMPTY_BANK_ACCOUNT.getMessage());
			}
		};
		
		private static final Consumer<AccountingExpenseContext> VALID_CREDITOR = aec -> {
			if (aec.inc.getCreditor().isPresent()) {
				aec.inc.getCreditor()
					.filter( c -> c.getId() != null)
					.map( c -> CreditorDAO.get( aec.ctx, p -> p.getIdProperty().eq(c.getId()).and(p.getDomainProperty().eq(aec.inc.getDomain()))))
					.filter( c -> c != null && c.getId() != null)
					.orElseThrow(() -> new AonCoreException(AonError.EMPTY_CUSTOMER.getMessage() ) );
			}
		}
		;
			
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
				.andThen(VALID_CREDITOR)
				.accept(new AccountingExpenseContext(ctx,config,expense));
		}
	}
	
	public static void delete(AONContext ctx, AccountEntry ae) {
		if (ae == null) throw new AonCoreException("AccountEntry is mandatory"); 
		if (ae.getEntryType() != AccountEntryType.OTHER_EXPENSES) 
			throw new AonCoreException("El apunte que se quiere borrar no es \"Otros Gastos\"");
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
