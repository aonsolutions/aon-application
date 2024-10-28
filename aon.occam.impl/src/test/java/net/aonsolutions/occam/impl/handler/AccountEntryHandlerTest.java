package net.aonsolutions.occam.impl.handler;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;
import static com.esferalia.aon.jooq.tables.AccountPeriod.ACCOUNT_PERIOD;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.jooq.Record;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.Account;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.Pair;

import net.aonsolutions.occam.api.model.AccountEntry;
import net.aonsolutions.occam.impl.AbstractOccamImplTest;
import net.aonsolutions.occam.impl.handler.AccountEntryHandler.AccountEntryDetailFiller;
import net.aonsolutions.occam.impl.handler.AccountEntryHandler.AccountEntryFiller;

class AccountEntryHandlerTest extends AbstractOccamImplTest {
	
	private static final Account DET_ACCOUNT = ACCOUNT.as("detAcc");
	private static final Account BAL_ACCOUNT = ACCOUNT.as("balAcc");

	@Test
	void testSelect() {
		List<AccountEntry> result =
			ctx.getDslContext().select()
				.from(ACCOUNT_ENTRY)
				.innerJoin(ACCOUNT_ENTRY_DETAIL).on(ACCOUNT_ENTRY.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT_ENTRY))
				.leftOuterJoin(ACCOUNT_PERIOD).on(ACCOUNT_PERIOD.ID.eq(ACCOUNT_ENTRY.ACCOUNT_PERIOD))
				.leftOuterJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_ACTIVITY.ID.eq(ACCOUNT_ENTRY.ACTIVITY))
				.leftOuterJoin(DET_ACCOUNT).on(DET_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.ACCOUNT))
				.leftOuterJoin(BAL_ACCOUNT).on(BAL_ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
				.where( ACCOUNT_ENTRY.ENTRY_DATE.gt( AonDateUtils.toSql( AonDateUtils.getYearLastDay(2023))) )
				.orderBy(ACCOUNT_ENTRY.ACCOUNT_PERIOD,
					ACCOUNT_ENTRY.JOURNAL,
					ACCOUNT_ENTRY.ID,
					ACCOUNT_ENTRY_DETAIL.LINE,
					ACCOUNT_ENTRY_DETAIL.ID
				)
				.limit(300)
				//.collect(Collectors.groupingBy( r -> r.getValue( ACCOUNT_ENTRY.ID), Collectors.toCollection( LinkedList::new )))
				
				
		        .collect(Collectors.groupingBy(
		        		r -> r.getValue( ACCOUNT_ENTRY.ID)
		        		, () -> new TreeMap<>()
		        		, Collectors.toCollection( LinkedList::new )))
	    				
				
				
				.entrySet()
				.stream()
				.filter( e -> AonCollectionUtils.isNotEmpty(e.getValue()))
				.map( e -> new Pair<AccountEntry, List<Record>>(AccountEntryFiller.build( e.getValue().get(0)) ,e.getValue()))
				.map( p -> {
					p.getRight().stream()
						.map(r -> AccountEntryDetailFiller.build( r ) )
						.forEach( d -> p.getLeft().addDetail(d) )
					;
					return p.getLeft();
				})
				.peek(a -> AccountEntryPrinter.print( System.out, a) )
				.toList()
			;
		
		
		System.out.println( result.size());
		
		
	}

}
