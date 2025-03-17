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
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
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

public class AccountingIncomeDAO {
	
	private AccountingIncomeDAO() {
	
	}
	
	// --------------------------------------------------------------- LECTURA
	public static Optional<AccountingIncome> get(CloseableAONContext ctx, int domain, Integer accountEntryId, IDAOCallback cbk) {
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
	public static Stream<AccountingIncome> stream(CloseableAONContext ctx
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
	
	private static Stream<AccountingIncome> stream(CloseableAONContext ctx
			, AccountEntryParams params
			, int offset
			, int limit
			, AccountEntryOrder orderBy
			, IDAOCallback cbk) {
		ctx.checkRead();
		return (
			params.setType( AccountEntryType.OTHER_INCOMES ).hasDetailProperties()
				? AccountEntryDAO.fetchByLines(ctx,p -> AccountEntryUtils.getFilterByLines(ctx,p, params),offset,limit,orderBy)
				: AccountEntryDAO.fetch(ctx,p -> AccountEntryUtils.getFilterByHeader(ctx,p, params),offset,limit,orderBy)
			)
			.map( ae -> createAccountingIncome( ctx , ae))
			.map( ai -> fillFinance( ctx , ai))
			.map( ai -> fillAccountingIncome( ctx , ai))
			.onClose(() -> {
				if (cbk != null) {
					cbk.onFinish();
				}
			})
			;
	}
	private static AccountingIncome createAccountingIncome(AONContext ctx, AccountEntry ae) {
		return new AccountingIncome()
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
			AccountingIncome visit( AONContext ctx, AccountingIncome inc, AccountEntryDetail aed ) {
				return inc.setExpAccount( AccountDAO.get( ctx, aed.getAccount() ) )
					.setConcept( aed.getConcept())
					.setReferenceCode( aed.getDocumentNumber())
					.setAmount( AonMathUtils.round(aed.getDebit() - aed.getCredit()));
			}
			
			@Override
			boolean accept(String code) {
				return (AonStringUtils.startsWith( code, "7"));
			}
		},
		CASH_ACCOUNT {
			@Override
			AccountingIncome visit( AONContext ctx, AccountingIncome inc, AccountEntryDetail aed  ) {
				return inc.setCashAccount( AccountDAO.get( ctx, aed.getAccount() ) )
					.setConcept( aed.getConcept())
					.setReferenceCode( aed.getDocumentNumber())
					.setAmount( AonMathUtils.round(aed.getDebit() - aed.getCredit()));
			}
			
			@Override
			boolean accept(String code) {
				return (AonStringUtils.startsWith( code, "5"));
			}
		},
		CUSTOMER_ACCOUNT {
			@Override
			AccountingIncome visit( AONContext ctx, AccountingIncome inc, AccountEntryDetail aed  ) {
				return inc.setCashAccount( AccountDAO.get( ctx, aed.getAccount() ) )
					.setConcept( aed.getConcept())
					.setReferenceCode( aed.getDocumentNumber())
					.setAmount( AonMathUtils.round(aed.getDebit() - aed.getCredit()));
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
		abstract AccountingIncome visit( AONContext ctx, AccountingIncome inc, AccountEntryDetail aed  );
	}
	
	private static AccountingIncome fillAccountingIncome(AONContext ctx, AccountingIncome ai) {
		ai.getAccountEntry()
			.ifPresent(	ae-> AonCollectionUtils.stream( ae.getDetails() )
				.forEach( aed -> AccountType.get( aed.getAccountCode() ).ifPresent( at -> at.visit( ctx, ai, aed)))
		);
		return ai;
	}
	
	private static AccountingIncome fillFinance(AONContext ctx, AccountingIncome ai) {
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
				.map( ft -> ft.getFinance())
				.orElse(null)
		)
		.setCustomer( 
			ai.getFinance()
				.map(Finance::getRegistry)
				.filter( r -> r != null )
				.map(r -> r.getId() )
				.map(id -> CustomerDAO.get( ctx, p -> p.getIdProperty().eq(id).and(p.getDomainProperty().eq(ai.getDomain()))))
				.orElse(null)
		);
		return ai;
	}
	
	// --------------------------------------------------------------- ESCRITURA
	public static AccountingIncome save(AONContext ctx, AccountingIncome income) {
		ctx.checkWrite();
		AccountingIncomeValidation.validateIncome(ctx,income);
		income.getAccountEntry()
			.filter( ae -> ae.getId() != null )
			.ifPresentOrElse( 
				 id -> update(ctx,income) 
				,() -> insert(ctx,income));
		return income;
	}
	
	private static AccountingIncome update(AONContext ctx, AccountingIncome income) {
		return income.getAccountEntry()
			.map( ae -> {
				delete(ctx, ae);
				income.setAccountEntry( null );
				income.setFinance( null );
				return insert( ctx, income );
			})
			.orElseThrow( () -> new AonCoreException("No se pudo modificar el ingreso."));
	}
	
	private static AccountingIncome insert(AONContext ctx, AccountingIncome income) {
		initializeAccountEntry( ctx, income );
		saveAccountEntry( ctx, income );
		income.getCustomer()
			.ifPresent( c -> saveAndRecordFinanace( ctx, c, income ));
		return income;
	}
	
	private static Optional<Account> obtainCustomerAccount(AONContext ctx, AccountingIncome income) {
		return income.getCustomer()
			.map( c -> CustomerDAO.ensureAccount( ctx, c.getId()))
		;
	}
	
	private static Account obtainBankAccount(AONContext ctx, AccountingIncome income) {
		return income.getCashAccount()
			.or( () -> income.getBank()
				.map( RegistryBank::getAccount ))
				.filter( a -> a != null && a.getId() != null )
				.or(() -> income.getBank()
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
								.setDomain(income.getDomain())
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
	
	private static AccountingIncome saveAccountEntry(AONContext ctx, AccountingIncome income) {
		AccountEntry ae = income.getAccountEntry()
			.orElseThrow( () -> new AonCoreException( "AccountEntry not initialized" ) );
		Account expAccount = income.getExpAccount()
			.orElseThrow( () -> new AonCoreException( "Exp Account not initialized" ) );
		Account bankAccount = obtainBankAccount(ctx,income);
		Optional<Account> customerAccount = obtainCustomerAccount(ctx,income);
		
		ae.setDetails( new LinkedList<>());
		
		ae.getDetails().add( 
			new AccountEntryDetail()
				.setAccount( expAccount.getId() )
				.setConcept( income.getConcept() )
				.setDocumentNumber( income.getReferenceCode() )
				.setCredit( income.getAmount())
				.setBalancingAccount(customerAccount.map( Account::getId ).orElse(bankAccount.getId()))
		);
		customerAccount.ifPresent( ca -> {
			ae.getDetails().add( 
				new AccountEntryDetail()
					.setAccount( ca.getId() )
					.setConcept( income.getConcept() )
					.setDocumentNumber( income.getReferenceCode() )
					.setDebit( income.getAmount())
					.setBalancingAccount(expAccount.getId())
			);
			ae.getDetails().add( 
				new AccountEntryDetail()
					.setAccount( ca.getId() )
					.setConcept( income.getConcept() )
					.setDocumentNumber( income.getReferenceCode() )
					.setCredit( income.getAmount())
					.setBalancingAccount(bankAccount.getId())
			);
		});
		ae.getDetails().add( 
			new AccountEntryDetail()
				.setAccount( bankAccount.getId() )
				.setConcept( income.getConcept() )
				.setDocumentNumber( income.getReferenceCode() )
				.setDebit( income.getAmount())
				.setBalancingAccount(customerAccount.map( Account::getId ).orElse(expAccount.getId()))
		);
		AccountEntryDAO.save( ctx, ae);
		return income;
	}

	private static AccountingIncome initializeAccountEntry(AONContext ctx, AccountingIncome income) {
		AccountPeriod period = AccountPeriodDAO.getPeriod(ctx, income.getDate() );
		if (period == null) {
			throw new AonCoreException( AonError.WRONG_PERIOD.format( income.getDate() ) );
		}
		AccountEntry ae = new AccountEntry()
			.setDomain( income.getDomain() )
			.setPeriod(period.getId())
			.setEntryDate( income.getDate() )
			.setEntryType( AccountEntryType.OTHER_INCOMES )
			.setActivity( income.getActivity().orElse(null) )
			.setComments( income.getComments() );
		income.getCustomer().ifPresentOrElse(
		   c -> ae.setConfidential( c.isConfidential()  )
		 ,() -> ae.setConfidential( false )
		);
		return income.setAccountEntry(ae);
	}
	
	private static AccountingIncome saveAndRecordFinanace(AONContext ctx, Customer cust, AccountingIncome income) {
		Finance finance = new Finance();
		finance.setDomain( income.getDomain() );
		finance.setPayment( false );
		finance.setRegistry( RegistryDAO.get(ctx,cust.getId() ));
		finance.setScope( cust.getScope() );
		finance.setSecurityLevel( cust.getSecurityLevel() );
		finance.setRegistryDocument( cust.getDocument() );
		finance.setRegistryDocumentType( cust.getDocumentType() );
		finance.setRegistryDocumentCountry( cust.getDocumentCountry() );
		finance.setRegistryName( cust.getName() );
		Account account = AccountDAO.get( ctx, cust.getAccount() );
		if (account != null) {
			finance.setRegistryAccountId( account.getId() );
			finance.setRegistryAccountCode( account.getCode() );
			finance.setRegistryAccountDescription( account.getDescription() );
		}
		finance.setAmount( income.getAmount() );
		finance.setConcept( AonStringUtils.abbreviate( income.getConcept() , 32) );
		finance.setDueDate( income.getDate() );
		finance.setManual(true);
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setRemarks(income.getComments());
		income.getBank().ifPresent( b -> {
			finance.setBankAccount( b.getBankAccount() );	
			finance.setBankAlias( b.getAlias() );
			finance.setBic( b.getBic() );
		});
		Integer financeId = FinanceDAO.save( ctx, finance);
		finance.setId( financeId );
		income.getAccountEntry().ifPresent( ae -> {
			FinanceTracking tracking = new FinanceTracking()
				.setDomain(finance.getDomain())
				.setFinance(finance)
				.setTrackingDate( finance.getDueDate() )
				.setAmount(finance.getAmount() )
				.setRegistryBank( income.getBank().orElse( null) )
				.setDescription( income.getConcept() )
				.setAmount( finance.getAmount() )
			;
			FinanceTrackingDAO.pay( ctx, tracking, ae );
		});
		Finance savedFinance = FinanceDAO.getFinance( ctx, financeId);
		income.setFinance(savedFinance);
		return income;
	}


	private static record AccountingIncomeContext(AONContext ctx,AonConfiguration config, AccountingIncome inc){}
	private static class AccountingIncomeValidation {
		
		private AccountingIncomeValidation() {
			
		}
		
		private static final Consumer<AccountingIncomeContext> EMPTY_DOMAIN = aec -> {
			if (aec.inc.getDomain() == 0) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		private static final Consumer<AccountingIncomeContext> EMPTY_DATE = aec -> {
			if (aec.inc.getDate() == null)
				throw new AonCoreException(AonError.EMPTY_DATE.getMessage());
		};
		
		private static final Consumer<AccountingIncomeContext> EMPTY_EXP_ACCOUNT = 
			aec -> aec.inc.getExpAccount()
				.filter( a -> a.getId() != null )
				.orElseThrow( () -> new AonCoreException(AonError.EMPTY_EXP_ACCOUNT.getMessage()))
		;
		
		private static final Consumer<AccountingIncomeContext> EMPTY_CONCEPT = aec -> {
			if (AonStringUtils.isEmpty( aec.inc.getConcept() ))
				throw new AonCoreException(AonError.EMPTY_CONCEPT.getMessage());
		};

		private static final Consumer<AccountingIncomeContext> EMPTY_AMOUNT = aec -> {
			if (AonMathUtils.isZero( aec.inc.getAmount() ))
				throw new AonCoreException(AonError.EMPTY_AMOUNT.getMessage());
		};
		
		private static final Consumer<AccountingIncomeContext> EMPTY_DEFAULT_CASH_ACCOUNT = aec -> {
			if (aec.inc.getBank().isEmpty() && aec.inc.getCashAccount().isEmpty()) {
				throw new AonCoreException(AonError.EMPTY_BANK_ACCOUNT.getMessage());
			}
		};
		
		private static final Consumer<AccountingIncomeContext> VALID_CUSTOMER = aec -> {
			if (aec.inc.getCustomer().isPresent()) {
				aec.inc.getCustomer()
					.filter( c -> c.getId() != null)
					.map( c -> CustomerDAO.get( aec.ctx, p -> p.getIdProperty().eq(c.getId()).and(p.getDomainProperty().eq(aec.inc.getDomain()))))
					.filter( c -> c != null && c.getId() != null)
					.orElseThrow(() -> new AonCoreException(AonError.EMPTY_CUSTOMER.getMessage() ) );
			}
		}
		;
			

		public static void validateIncome(AONContext ctx, AccountingIncome income) throws AonCoreException {
			AonConfiguration config = ConfigurationDAO.getConfiguration(ctx, income.getDate());
			validateExpense(ctx, config, income);
		}
		public static void validateExpense(AONContext ctx,AonConfiguration config, AccountingIncome income) throws AonCoreException {
			EMPTY_DOMAIN
				.andThen(EMPTY_DATE)
				.andThen(EMPTY_EXP_ACCOUNT)
				.andThen(EMPTY_CONCEPT)
				.andThen(EMPTY_AMOUNT)
				.andThen(EMPTY_DEFAULT_CASH_ACCOUNT)
				.andThen(VALID_CUSTOMER)
				.accept(new AccountingIncomeContext(ctx,config,income));
		}
	}
	
	public static void delete(AONContext ctx, AccountEntry ae) {
		if (ae == null) throw new AonCoreException("AccountEntry is mandatory"); 
		if (ae.getEntryType() != AccountEntryType.OTHER_INCOMES) 
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
