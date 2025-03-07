package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IDAOCallback;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryDetail;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountPeriod;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.esferalia.aon.occam.api.model.finance.Finance;
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
			.map( ae -> fillAccountingIncome( ctx , ae))
			.map( ai -> fillFinance( ctx , ai))
			.onClose(() -> {
				if (cbk != null) {
					cbk.onFinish();
				}
			})
			;
	}
	
	private static AccountingIncome fillAccountingIncome(AONContext ctx, AccountEntry ae) {
		AccountingIncome ai = new AccountingIncome()
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
	
	private static AccountingIncome fillFinance(AONContext ctx, AccountingIncome ai) {
		return ai;
	}
	// --------------------------------------------------------------- ESCRITURA
	public static AccountingIncome save(AONContext ctx, AccountingIncome income) {
		ctx.checkWrite();
		AccountingIncomeValidation.validateExpense(ctx,income);
		income.getAccountEntry()
			.filter( ae -> ae.getId() != null )
			.ifPresentOrElse( 
				 id -> update(ctx,income) 
				,() -> insert(ctx,income));
		return income;
	}
	
	private static AccountingIncome update(AONContext ctx, AccountingIncome income) {
		return income;
	}
	
	private static AccountingIncome insert(AONContext ctx, AccountingIncome income) {
		initializeAccountEntry( ctx, income );
		if (income.getCustomer().isPresent()) {
			initializeFinance( ctx, income );
		} else {
			saveBasicAccountEntry( ctx, income );
		}
		return income;
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
	
	private static void saveBasicAccountEntry(AONContext ctx, AccountingIncome income) {
		AccountEntry ae = income.getAccountEntry()
			.orElseThrow( () -> new AonCoreException( "AccountEntry not initialized" ) );
		Account expAccount = income.getExpAccount()
			.orElseThrow( () -> new AonCoreException( "Exp Account not initialized" ) );
		Account bankAccount = obtainBankAccount(ctx,income);
		
		ae.setDetails( new LinkedList<>());
		ae.getDetails().add( 
			new AccountEntryDetail()
				.setAccount( expAccount.getId() )
				.setConcept( income.getConcept() )
				.setDocumentNumber( income.getReferenceCode() )
				.setCredit( income.getAmount())
				.setBalancingAccount( bankAccount.getId() )
		);
		ae.getDetails().add( 
			new AccountEntryDetail()
				.setAccount( bankAccount.getId() )
				.setConcept( income.getConcept() )
				.setDocumentNumber( income.getReferenceCode() )
				.setDebit( income.getAmount())
				.setBalancingAccount( expAccount.getId() )
		);
		AccountEntryDAO.save( ctx, ae);
		System.out.println( "AccountEntry saved ..: " +  ae.getId() );
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
			.setActivity( income.getActivity().orElse(null) );
		income.getCustomer().ifPresentOrElse(
		   c -> ae.setConfidential( c.isConfidential()  )
		 ,() -> ae.setConfidential( false )
		);
		return income.setAccountEntry(ae);
	}
	
	private static Optional<Finance> initializeFinance(AONContext ctx, AccountingIncome income) {
		return income.getCustomer()
			.map(creditor -> {
				Finance finance = new Finance();
				finance.setDomain( income.getDomain() );
				finance.setPayment( true );
				finance.setRegistry( RegistryDAO.get(ctx,creditor.getId() ));
				finance.setScope( creditor.getScope() );
				finance.setSecurityLevel( creditor.getSecurityLevel() );
				finance.setRegistryDocument( creditor.getDocument() );
				finance.setRegistryDocumentType( creditor.getDocumentType() );
				finance.setRegistryDocumentCountry( creditor.getDocumentCountry() );
				finance.setRegistryName( creditor.getName() );
				Account account = AccountDAO.get( ctx, creditor.getAccount() );
				if (account != null) {
					finance.setRegistryAccountId( account.getId() );
					finance.setRegistryAccountCode( account.getCode() );
					finance.setRegistryAccountDescription( account.getDescription() );
				}
				finance.setAmount( income.getAmount() );
				finance.setConcept( income.getConcept() );
				finance.setDueDate( income.getDate() );
				finance.setManual(true);
				finance.setFinanceStatus(FinanceStatus.PENDING);
				finance.setRemarks(income.getComments());
				income.getBank().ifPresent( b -> {
					finance.setBankAccount( b.getBankAccount() );	
					finance.setBankAlias( b.getAlias() );
					finance.setBic( b.getBic() );
				});
				income.setFinance(finance);
				return finance;
			});  
	}


	private static record AccountingIncomeContext(AONContext ctx,AonConfiguration config, AccountingIncome exp){}
	private static class AccountingIncomeValidation {
		
		private AccountingIncomeValidation() {
			
		}
		
		private static final Consumer<AccountingIncomeContext> EMPTY_DOMAIN = aec -> {
			if (aec.exp.getDomain() == 0) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		private static final Consumer<AccountingIncomeContext> EMPTY_DATE = aec -> {
			if (aec.exp.getDate() == null)
				throw new AonCoreException(AonError.EMPTY_DATE.getMessage());
		};
		
		private static final Consumer<AccountingIncomeContext> EMPTY_EXP_ACCOUNT = 
			aec -> aec.exp.getExpAccount()
				.filter( a -> a.getId() != null )
				.orElseThrow( () -> new AonCoreException(AonError.EMPTY_EXP_ACCOUNT.getMessage()))
		;
		
		private static final Consumer<AccountingIncomeContext> EMPTY_CONCEPT = aec -> {
			if (AonStringUtils.isEmpty( aec.exp.getConcept() ))
				throw new AonCoreException(AonError.EMPTY_CONCEPT.getMessage());
		};

		private static final Consumer<AccountingIncomeContext> EMPTY_AMOUNT = aec -> {
			if (AonMathUtils.isZero( aec.exp.getAmount() ))
				throw new AonCoreException(AonError.EMPTY_AMOUNT.getMessage());
		};
		
		private static final Consumer<AccountingIncomeContext> EMPTY_DEFAULT_CASH_ACCOUNT = aec -> {
			if (aec.exp.getBank().isEmpty() && aec.exp.getCashAccount().isEmpty()) {
				throw new AonCoreException(AonError.EMPTY_BANK_ACCOUNT.getMessage());
			}
		};
		
		public static void validateExpense(AONContext ctx, AccountingIncome income) throws AonCoreException {
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
				.accept(new AccountingIncomeContext(ctx,config,income));
		}
	}
}
