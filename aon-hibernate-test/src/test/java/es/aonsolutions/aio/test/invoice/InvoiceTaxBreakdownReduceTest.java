package es.aonsolutions.aio.test.invoice;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.code.aon.config.enumeration.TaxType;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.product.strategy.TaxKey;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

import es.aonsolutions.aio.test.AonHibernateTestBasic;
import es.aonsolutions.aio.test.AonHibernateTestRandom;

class InvoiceTaxBreakdownReduceTest extends AonHibernateTestBasic {
	
	private static DecimalFormat PT = new DecimalFormat("#,##0");
	private static DecimalFormat BS = new DecimalFormat("#,##0.0000");
	private static DecimalFormat QT = new DecimalFormat("#,##0.00");
	
	
	public static void main(String[] args) {
		List<TaxBreakDown> list = new LinkedList<>();
		list.add(getTaxBreakDown(TaxType.VAT,4.0));
		list.add(getTaxBreakDown(TaxType.VAT,4.0));
		list.add(getTaxBreakDown(TaxType.VAT,4.0));
		list.add(getTaxBreakDown(TaxType.VAT,5.0));
		list.add(getTaxBreakDown(TaxType.VAT,5.0));
		list.add(getTaxBreakDown(TaxType.VAT,10.0));
		list.add(getTaxBreakDown(TaxType.VAT,10.0));
		list.add(getTaxBreakDown(TaxType.VAT,10.0));
		list.add(getTaxBreakDown(TaxType.VAT,10.0));
		list.add(getTaxBreakDown(TaxType.VAT,10.0));
		list.add(getTaxBreakDown(TaxType.VAT,10.0));
		list.add(getTaxBreakDown(TaxType.VAT,10.0));
		list.add(getTaxBreakDown(TaxType.VAT,10.0));
		list.add(getTaxBreakDown(TaxType.VAT,10.0));
		list.add(getTaxBreakDown(TaxType.VAT,21.0));
		list.add(getTaxBreakDown(TaxType.VAT,21.0));
		list.add(getTaxBreakDown(TaxType.VAT,21.0));
		list.add(getTaxBreakDown(TaxType.VAT,21.0));
		list.add(getTaxBreakDown(TaxType.VAT,21.0));
		list.add(getTaxBreakDown(TaxType.VAT,21.0));
		list.add(getTaxBreakDown(TaxType.VAT,21.0));
		print(list);
		Map<TaxKey,TaxBreakDown> taxs = new HashMap<>();
		list.stream()
			.map( InvoiceTaxBreakdownReduceTest::getPair )
			.forEach(pair -> {
				taxs.merge(pair.getLeft(), pair.getRight(), (o , n) -> {
					n.setBase(AonMathUtils.round( n.getBase() + o.getBase(),4));
					return n;
				});
			});
			;
		taxs.values().stream().forEach(tb -> tb.setTaxQuota( AonMathUtils.round( tb.getBase() * tb.getTaxPercent() / 100, 2 )));
		print(taxs.values());
		round(taxs.values(), list);
		print(list);
	}
	
	private static void round(Collection<TaxBreakDown> taxs, Collection<TaxBreakDown> list) {
		for ( TaxBreakDown tax : taxs) {
			TaxKey key = getKey(tax);
			long count = list.stream()
				.map(tb -> getKey(tb))
				.filter( k -> k.equals(key))
				.count();
			if ( count > 0) {
				long i = 0;
				double quota = tax.getTaxQuota();
				for ( TaxBreakDown tb : list ) {
					if (getKey(tb).equals(key)) {
						i++;
						if ( i == count) {
							tb.setTaxQuota(quota);
						} else {
							quota = AonMathUtils.round( quota - tb.getTaxQuota(), 2);
						}
					}
				}
			}
		}
	}

	private static Pair<TaxKey,TaxBreakDown> getPair(TaxBreakDown tb) {
		return new Pair<TaxKey, TaxBreakDown>(getKey(tb), tb);
	}

	private static TaxKey getKey(TaxBreakDown tb) {
		TaxKey key = new TaxKey();
		key.setType(tb.getTaxType());
		key.setPercent(tb.getTaxPercent());
		return key;
	}

	private static void print(Collection<TaxBreakDown> collection) {
		System.out.println("----------------------");
		MutableDouble sumBase = new MutableDouble();
		MutableDouble sumQuota = new MutableDouble();
		collection.stream()
			.map( tb ->  {
				sumBase.add( tb.getBase());
				sumQuota.add( tb.getTaxQuota());
				return tb;
			})
			.forEach( tb -> print(tb) );
		System.out.println( AonStringUtils.spaces( 6)
				+ " " + AonStringUtils.spaces(6) + "  "
				+ " " + AonStringUtils.leftPad( BS.format(sumBase), 15)
				+ " " + AonStringUtils.leftPad( QT.format(sumQuota), 15));
	}

	private static void print(TaxBreakDown tb) {
		System.out.println( AonStringUtils.rightPad( tb.getTaxType().toString(), 6)
			+ " " + AonStringUtils.leftPad( PT.format( tb.getTaxPercent()), 6) + "% "
			+ " " + AonStringUtils.leftPad( BS.format(tb.getBase()), 15)
			+ " " + AonStringUtils.leftPad( QT.format(tb.getTaxQuota()), 15));
	}

	private static TaxBreakDown getTaxBreakDown(TaxType type,  double percent ) {
		TaxBreakDown tb = new TaxBreakDown();
		tb.setTaxType(type);
		tb.setTaxPercent(percent);
		tb.setBase(AonHibernateTestRandom.getDouble(0, 1000,  AonHibernateTestRandom.number(0, 4) ) );
		tb.setTaxQuota( AonMathUtils.round( tb.getBase() * tb.getTaxPercent() / 100, 2 ));
		return tb;
	}
	
}

