package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.Amortization.AMORTIZATION;
import static com.esferalia.aon.jooq.tables.BankConcept.BANK_CONCEPT;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.InvoiceDetailAccount.INVOICE_DETAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.InvoiceDua.INVOICE_DUA;
import static com.esferalia.aon.jooq.tables.InvoiceTaxAccount.INVOICE_TAX_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Loan.LOAN;
import static com.esferalia.aon.jooq.tables.PmTypeDetail.PM_TYPE_DETAIL;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Tax.TAX;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.TableLike;
import org.jooq.UpdateSetFirstStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.AccountRecord;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.occam.api.model.Account;
import net.aonsolutions.occam.api.model.Domain;
import net.aonsolutions.occam.api.model.Filter.AccountFilter;
import net.aonsolutions.occam.api.model.Filter.Property;
import net.aonsolutions.occam.api.model.Properties.AccountProperties;
import net.aonsolutions.occam.api.model.metadata.AccountMetadata.AccountMetadataVisitor;
import net.aonsolutions.occam.impl.AONContext;

class AccountHandler {
	
	private AccountHandler() {
	}
	
	private static final AccountPropertiesDAO ACCOUNT_PROPERTIES = new AccountPropertiesDAO();
	private static class AccountPropertiesDAO implements AccountProperties {
		
		private Condition getCondition(AccountFilter filter) {
			if (filter==null) return DSL.trueCondition();
			FilterImpl filterDAO = (FilterImpl) filter.filter(this);
			if (filterDAO == null) return DSL.trueCondition();
			return filterDAO.getCondition();
		}
		
		@Override public Property<Integer> getIdProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT.DOMAIN);}
		@Override public Property<String> getCodeProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT.CODE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT.DESCRIPTION);}
		@Override public Property<String> getAliasProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT.ALIAS);}
		@Override public Property<Byte> getEntryEnabledProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT.ENTRYENABLED);}
		@Override public Property<Byte> getActiveProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT.ACTIVE);}
		@Override public Property<Byte> getLevelProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT.LEVEL);}
		@Override public Property<String> getCostCenterProperty() {return new FilterImpl.PropertyDAO<>(ACCOUNT.COST_CENTER);}
	}

	static class AccountFiller extends Filler<Account> {
		@Override
		public Account apply(Record r) {
			return build(r);
		}
		
		static Account build(Record r) {
			return build(r, ACCOUNT);
		}

		static Account build(Record r, com.esferalia.aon.jooq.tables.Account alias) {
			if (isNull(r, alias.ID)) return null;
			return new Account()
				.setId(getValue(r, alias.ID))
				.setDomain(getValue(r, alias.DOMAIN))
				.setCode(getValue(r, alias.CODE))
				.setDescription(getValue(r, alias.DESCRIPTION))
				.setAlias(getValue(r, alias.ALIAS))
				.setEntryEnabled(getBoolean(r, alias.ENTRYENABLED))
				.setLevel(getByte(r, alias.LEVEL))
				.setActive(getBoolean(r, alias.ACTIVE))
				.setCostCenter(getValue(r, alias.COST_CENTER))
				.markAsClean()
			;				
		}
	}

	private static SelectConditionStep<Record> select(AONContext ctx, int domain) {
		ctx.checkRead();
		return ctx.getDslContext()
			.select()
			.from(ACCOUNT)
			.where(ACCOUNT.DOMAIN.in(ctx.getInheritanceDomainIds(domain)))
		;
	}
	
	static Optional<Account> get(AONContext ctx, int domain, Integer accountId) {
		return select(ctx, domain)
			.and(ACCOUNT.ID.equal(accountId))
			.fetch()
			.stream()
			.map(new AccountFiller())
			.findFirst();			
	}
	
	static Optional<Account> get(AONContext ctx, int domain, String accountCode) {
		return select(ctx, domain)
			.and(ACCOUNT.CODE.equal(accountCode))
			.fetch()
			.stream()
			.map(new AccountFiller())
			.findFirst();			
	}
	
	static Stream<Account> stream(AONContext ctx, int domain, AccountFilter filter) {
		return stream(ctx, domain, filter, 0, Integer.MAX_VALUE);
	}

	static Stream<Account> stream(AONContext ctx, int domain, AccountFilter filter, int offset, int numberOfRows) {
		return select(ctx, domain)
			.and(ACCOUNT_PROPERTIES.getCondition(filter))
			.limit(offset, numberOfRows)
			.fetch()
			.stream()
			.map(new AccountFiller());			
	}
	
	
	static Account save(AONContext ctx, int domain, Account account) {
		ctx.checkWrite();
		if (account == null) throw new AonCoreException(AonError.EMPTY_SAVE.getMessage());
		if (account.isDirty()) { 
			Insurer.ensure(ctx, domain, account);
			SaveValidation.validate(ctx, domain, account);
			return (account.getId() == null)
				?insert(ctx, account)
				:update(ctx, account);
		} else {
			ctx.log().debug(AonError.NOT_DIRTY.format("Account",account.getId()));
		}
		return account;  
	}

	private static Account insert(AONContext ctx, Account account) {
		Integer id = ctx.getDslContext()
			.insertInto(ACCOUNT)
			.set(ACCOUNT.DOMAIN,account.getDomain())
			.set(ACCOUNT.CODE,account.getCode())
			.set(ACCOUNT.DESCRIPTION,account.getDescription())
			.set(ACCOUNT.ALIAS,account.getAlias())
			.set(ACCOUNT.ENTRYENABLED, AonEnumUtils.getByte(account.isEntryEnabled()))
			.set(ACCOUNT.LEVEL,account.getLevel())
			.set(ACCOUNT.ACTIVE, AonEnumUtils.getByte(account.isActive()))
			.set(ACCOUNT.COST_CENTER,account.getCostCenter())
			.returning(ACCOUNT.ID)
			.fetchOne()
			.getValue(ACCOUNT.ID);
		ctx.log().debug("INSERT ACCOUNT id: {0} code: {1}",account.getId(),account.getCode());
		return account.setId(id).markAsClean();
	}
	
	
	private static class UpdateBuilder implements AccountMetadataVisitor<Void> {
			
		private	final UpdateSetFirstStep<AccountRecord> updateSetFirstStep;
		private	final Account account;
		private	UpdateSetMoreStep<AccountRecord> updateSetMoreStep;
		
		private UpdateBuilder(Account account, UpdateSetFirstStep<AccountRecord> updateSetFirstStep) {
			this.updateSetFirstStep = updateSetFirstStep;
			this.account = account;
		}
			
		private UpdateSetMoreStep<AccountRecord> getStatement() {
			account.dirtySet()
				.stream()
				.forEach(dm -> dm.visit(this));
			return updateSetMoreStep; 
		}
		
		private <T> Void set(Field<T> field,T value) {
			updateSetMoreStep = (updateSetMoreStep == null)  
				?updateSetFirstStep.set(field,value)
				:updateSetMoreStep.set(field,value)
			;
			return null;
		}
		
		@Override public Void visitId() { return null; }
		@Override public Void visitDomain() { return set(ACCOUNT.DOMAIN, account.getDomain()); }
		@Override public Void visitCode() { return set(ACCOUNT.CODE, account.getCode()); }
		@Override public Void visitDescription() { return set(ACCOUNT.DESCRIPTION, account.getDescription());}
		@Override public Void visitAlias() { return set(ACCOUNT.ALIAS, account.getAlias());}
		@Override public Void visitActive() { return set(ACCOUNT.ACTIVE, AonEnumUtils.getByte(account.isActive()));}
		@Override public Void visitEntryEnabled() {return set(ACCOUNT.ENTRYENABLED, AonEnumUtils.getByte(account.isEntryEnabled()));}
		@Override public Void visitLevel() {return set(ACCOUNT.LEVEL, account.getLevel());}
		@Override public Void visitCostCenter() {return set(ACCOUNT.COST_CENTER, account.getCostCenter());}

	}
	
	private static Account update(AONContext ctx, final Account account) {
		UpdateBuilder builder = new UpdateBuilder(account,ctx.getDslContext().update(ACCOUNT));
		int count = builder.getStatement()
			.where(ACCOUNT.ID.eq(account.getId()))
			.execute();
		ctx.log().debug("UPDATE ACCOUNT id: {0}. ({1} rows)", account.getId(),count);
		return account;
	}	
	
	static Account delete(AONContext ctx, int domain, Account account) {
		ctx.checkWrite();
		DeleteValidation.validate(ctx, domain, account);
		ctx.getDslContext()
			.delete(ACCOUNT)
			.where(ACCOUNT.ID.eq(account.getId()))
			.execute();
		ctx.log().debug("DELETE ACCOUNT id: {0} code: {1}",account.getId(),account.getCode());
		return account;
	}

	private class Insurer{
		
		/**
		 * Se rellena el nivel de la cuenta.
		 */
		private static final Consumer<Context> COMPLETE_LEVEL = 
			c -> c.account.setLevel( (byte) ((c.account.getCode().length() > 4)? 5: c.account.getCode().length()));

		/**
		 * Se rellena si permite apuntes o no.
		 */
		private static final Consumer<Context> COMPLETE_ENTRY_ENABLED  = 
			c -> c.account.setEntryEnabled(c.account.getLevel()==5);

		/**
		 * Se rellena si permite apuntes o no.
		 */
		private static final Consumer<Context> ENSURE_COST_CENTER = c -> {
			if (AonStringUtils.isBlank( c.account.getCostCenter())) 
				c.account.setCostCenter(null);
		};
		
		private record Context(AONContext ctx,int domain, Account account) { }
		static void ensure(AONContext ctx,int domain,Account account) throws AonCoreException {
			COMPLETE_LEVEL
				.andThen(COMPLETE_ENTRY_ENABLED)
				.andThen(ENSURE_COST_CENTER)
				.accept( new Context( ctx, domain, account) );
		}

	}
	
	private class SaveValidation {
		
		private SaveValidation() {
			
		}

		/**
		 * El dominio de la cuenta no puede estar vacio.
		 */
		private static final Consumer<Context> EMPTY_DOMAIN = c -> {
			if (c.account.getDomain() == null) 
				throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		};
		
		/**
		 * El código de cuenta contable es un dato obligatorio.
		 */
		private static final Consumer<Context> EMPTY_CODE = c -> {
			if (AonStringUtils.isBlank(c.account.getCode()))
				throw new AonCoreException(AonError.ACCOUNT_EMPTY_CODE.getMessage());
		};
		
		/**
		 * La descripcion de cuenta contable es un dato obligatorio.
		 */
		private static final Consumer<Context> EMPTY_DESCRIPTION = c -> {
			if (AonStringUtils.isBlank(c.account.getDescription()))
				throw new AonCoreException(AonError.ACCOUNT_EMPTY_DESCRIPTION.getMessage());
		};

		/**
		 * La longitd de la cuenta debe ser 1,2,3,4, ó 9
		 */
		private static final Consumer<Context> VALID_LENGTH = c -> {
			int l = AonStringUtils.length(c.account.getCode());
			if (l != 1 && l != 2 && l != 3 && l != 4 && l != 9)
				throw new AonCoreException(AonError.ACCOUNT_INVALID_LENGTH.format(c.account.getCode()));
		};
		/**
		 * La cuenta contable debe ser numerica
		 */
		private static final Consumer<Context> NUMERIC_CODE = c -> {
			if (!AonStringUtils.isNumeric(c.account.getCode()))
				throw new AonCoreException(AonError.ACCOUNT_NO_NUMERIC.getMessage());
		};
		
		/**
		 * Los niveles inferiores de la cuenta deben existir.
		 */
		private static final Consumer<Context> LOW_LEVEL_EXISTS = c -> {
			int level = (byte) ((c.account.getCode().length() > 4)? 5: c.account.getCode().length());
			if (level>1) {
				int parentLevel = level - 1;
				String parentCode = AonStringUtils.substring(c.account.getCode(),0, parentLevel);
				if (AccountHandler.stream(c.ctx, c.domain, f -> f.getCodeProperty().eq(parentCode))
						.findFirst()
						.isEmpty()) {
					throw new AonCoreException(AonError.ACCOUNT_LOW_LEVEL_NOT_PRESENT.format(c.account.getCode()));
				}
			}
		};
		
		/**
		 * La cuenta contable no puede estar duplicada.
		 */
		private static final Consumer<Context> DUPLICATED_CODE = c -> {
			AccountHandler.stream(c.ctx,c.domain
					,f -> f.getCodeProperty().eq(c.account.getCode()).and( f.getIdProperty().ne(c.account.getId()) ))
				.findFirst()
				.ifPresent( duplicated -> {
					throw new AonCoreException(AonError.ACCOUNT_DUPLICATED_CODE.format(c.account.getFullName(),duplicated.getFullName()));
				});
		};
		
		private record Context(AONContext ctx,int domain, Account account) { }
		static void validate(AONContext ctx,int domain, Account account) throws AonCoreException {
			EMPTY_DOMAIN
				.andThen(EMPTY_CODE)
				.andThen(EMPTY_DESCRIPTION)
				.andThen(VALID_LENGTH)
				.andThen(NUMERIC_CODE)
				.andThen(LOW_LEVEL_EXISTS)
				.andThen(DUPLICATED_CODE)
				.accept(new Context(ctx, domain, account));

		}
	}

	private class DeleteValidation {
			
		private DeleteValidation() {
		}

		/**
		 * No se puede borrar una cuenta de diferente dominio.
		 */
		private static final Consumer<Context> FROM_PARENT_DOMAIN_CHECK = c -> {
			if (AonNumberUtils.notEquals(c.account.getDomain(), c.domain)) {
				Domain domain = DomainHandler.get(c.ctx, c.account.getDomain()).orElse(null);
				// Estamos en el dominio padre y la cuenta es de un hijo. Usado desde utilidades contables.
				if (domain == null || AonNumberUtils.notEquals(c.domain,domain.getParentId()) ) {
					throw new AonCoreException(AonError.ACCOUNT_PARENT_ACCOUNT.getMessage());
				}
			}
		};
		
		/**
		 * No se puede borrar una cuenta de diferente dominio.
		 */
		private static final Consumer<Context> HIGH_LEVEL_EXISTS = c -> {
			int level = (byte) ((c.account.getCode().length() > 4)? 5: c.account.getCode().length());
			if (level< 5) {
				AccountHandler.stream(c.ctx, c.domain
					, f -> f.getCodeProperty().like(c.account.getCode() + "%").and( f.getIdProperty().ne(c.account.getId()))
				)
				.findAny()
				.orElseThrow( () -> new AonCoreException(AonError.ACCOUNT_HIGH_LEVEL_PRESENT.getMessage()))
				;
			}
		};

		private static final Consumer<Context> CHECK_IF_PRESENT_ACCOUNT_ENTRY_DETAIL_ACCOUNT = c -> {
			if ( exists(c,ACCOUNT_ENTRY_DETAIL,ACCOUNT_ENTRY_DETAIL.ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_ACCOUNT_ENTRY_DETAIL_ACCOUNT.getMessage());
		};

		private static final Consumer<Context> CHECK_IF_PRESENT_ACCOUNT_ENTRY_DETAIL_BAL_ACCOUNT = c -> {
			if ( exists(c,ACCOUNT_ENTRY_DETAIL,ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_ACCOUNT_ENTRY_DETAIL_BAL_ACCOUNT.getMessage());
		};
		// ----
		private static final Consumer<Context> CHECK_IF_PRESENT_AMORTIZATION_ACC_ACCOUNT = c -> {
			if ( exists(c,AMORTIZATION,AMORTIZATION.ACCUMULATED_ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_AMORTIZATION_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_AMORTIZATION_ALL_ACCOUNT = c -> {
			if ( exists(c,AMORTIZATION,AMORTIZATION.ALLOCATION_ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_AMORTIZATION_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_AMORTIZATION_FIX_ACCOUNT = c -> {
			if ( exists(c,AMORTIZATION,AMORTIZATION.FIXED_ASSET_ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_AMORTIZATION_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_BANK_CONCEPT_ACCOUNT = c -> {
			if ( exists(c,BANK_CONCEPT,BANK_CONCEPT.ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_BANK_CONCEPT_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_CREDITOR_ACCOUNT = c -> {
			if ( exists(c,CREDITOR,CREDITOR.ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_CREDITOR_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_CUSTOMER_ACCOUNT = c -> {
			if ( exists(c,CUSTOMER,CUSTOMER.ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_CUSTOMER_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_SUPPLIER_ACCOUNT = c -> {
			if ( exists(c,SUPPLIER,SUPPLIER.ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_SUPPLIER_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_PRODUCT_PUR_ACCOUNT = c -> {
			if ( exists(c,PRODUCT,PRODUCT.PURCHASE_ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_PRODUCT_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_PRODUCT_SAL_ACCOUNT = c -> {
			if ( exists(c,PRODUCT,PRODUCT.SALES_ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_PRODUCT_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_TAX_PUR_ACCOUNT = c -> {
			if ( exists(c,TAX,TAX.PURCHASE_ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_TAX_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_TAX_SAL_ACCOUNT = c -> {
			if ( exists(c,TAX,TAX.SALES_ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_TAX_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_INV_DET_ACCOUNT = c -> {
			if ( exists(c,INVOICE_DETAIL_ACCOUNT,INVOICE_DETAIL_ACCOUNT.ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_INVOICE_DETAIL_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_INV_DUA_DUTY_ACCOUNT = c -> {
			if ( exists(c,INVOICE_DUA,INVOICE_DUA.DUTY_ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_INVOICE_DUA_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_INV_DUA_VAT_ACCOUNT = c -> {
			if ( exists(c,INVOICE_DUA,INVOICE_DUA.VAT_ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_INVOICE_DUA_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_INV_TAX_ACCOUNT = c -> {
			if ( exists(c,INVOICE_TAX_ACCOUNT,INVOICE_TAX_ACCOUNT.ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_INVOICE_TAX_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_LOAN_ACCOUNT = c -> {
			if ( exists(c,LOAN,LOAN.ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_LOAN_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_PM_TYPE_ACCOUNT = c -> {
			if ( exists(c,PM_TYPE_DETAIL,PM_TYPE_DETAIL.ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_PM_TYPE_ACCOUNT.getMessage());
		};
		private static final Consumer<Context> CHECK_IF_PRESENT_RBANK_ACCOUNT = c -> {
			if ( exists(c,RBANK,RBANK.ACCOUNT) )
				throw new AonCoreException(AonError.ACCOUNT_PRESENT_IN_RBANK_ACCOUNT.getMessage());
		};

		private static boolean exists(Context c,TableLike<?> table, Field<Integer> column ) {
			return c.ctx.getDslContext().select( column )
				.from(table)
				.where(column.eq(c.account.getId()))
				.stream()
				.map( rec -> rec.getValue(column))
				.findFirst()
				.orElse(null) != null;
		}

		private record Context(AONContext ctx,int domain, Account account) { }
		static void validate(AONContext ctx,int domain , Account account) {
			FROM_PARENT_DOMAIN_CHECK
				.andThen(HIGH_LEVEL_EXISTS)
				.andThen(CHECK_IF_PRESENT_ACCOUNT_ENTRY_DETAIL_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_ACCOUNT_ENTRY_DETAIL_BAL_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_CREDITOR_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_CUSTOMER_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_SUPPLIER_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_PRODUCT_PUR_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_PRODUCT_SAL_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_AMORTIZATION_ACC_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_AMORTIZATION_ALL_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_AMORTIZATION_FIX_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_TAX_PUR_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_TAX_SAL_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_INV_DET_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_INV_TAX_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_PM_TYPE_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_BANK_CONCEPT_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_RBANK_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_LOAN_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_INV_DUA_DUTY_ACCOUNT)
				.andThen(CHECK_IF_PRESENT_INV_DUA_VAT_ACCOUNT)
			.accept(new Context(ctx, domain, account));
		}
	}
	
	// *************************************************
	// ********** TEST PURPOSE METHODS *****************
	// *************************************************
	static Account getRandom(AONContext ctx, int domain, AccountFilter filter) {
		return select(ctx,domain)
			.and(ACCOUNT_PROPERTIES.getCondition(filter))
			.orderBy( DSL.rand() )
			.fetch()
			.stream()
			.map(new AccountFiller())
			.findFirst()
			.orElse(null);
	}
}




