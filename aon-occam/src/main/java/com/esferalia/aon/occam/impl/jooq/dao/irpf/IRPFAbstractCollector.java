package com.esferalia.aon.occam.impl.jooq.dao.irpf;

import java.util.Date;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

abstract class IRPFAbstractCollector implements Collector<IrpfBreakdown, TreeMap<String,IrpfBreakdown>, TreeMap<String,IrpfBreakdown>> {

	@Override
	public Supplier<TreeMap<String, IrpfBreakdown>> supplier() {
		return TreeMap::new;
	}

	private Integer nullIfNotEquals( Integer l, Integer r) {
		return AonNumberUtils.equals(l,r) ? l : null;
	}
	private String nullIfNotEquals( String l, String r) {
		return AonStringUtils.equals(l,r) ? l : null;
	}
	private Double negativeIfNotEquals( Double l, Double r) {
		return AonNumberUtils.equals(l,r) ? l : -1;
	}
	
	private Date nullIfNotEquals( Date l, Date r) {
		return AonDateUtils.isSameDay(l, r) ? l : null;
	}
	private <T> T nullIfNotEquals( T l, T r) {
		return l == r ? l : null;
	}
	
	protected IrpfBreakdown initialize( IrpfBreakdown js) {
		return new IrpfBreakdown()
			.setActivity( js.getActivity() )
			.setActivityDescription( js.getActivityDescription() )
			.setEpigraph( js.getEpigraph() )
			.setRegistryDocument( js.getRegistryDocument() )
			.setRegistryDocumentType( js.getRegistryDocumentType() )
			.setRegistryDocumentCountry( js.getRegistryDocumentCountry() )
			.setName( js.getName() )
			.setIssueDate( js.getIssueDate() )
			.setFromSalary( js.isFromSalary() )
			.setInsidePeriod( js.isInsidePeriod() )
			.setSalary( js.getSalary() )
			.setInvoiceType( js.getInvoiceType() )
			.setInvoice( js.getInvoice() )
			.setSeries( js.getSeries() )
			.setNumber( js.getNumber() )
			.setReferenceCode( js.getReferenceCode() )
			.setTaxDate( js.getTaxDate() )
			.setWithholdingType( js.getWithholdingType() )
			.setIRPFRegime( js.getIRPFRegime() )
			.setInKind( js.isInKind() )
			.setPercent( js.getPercent() )
			.setDeductiblePercent( js.getDeductiblePercent() )
			.setGroupedBy( js.getGroupedBy() )
			.setZip( js.getZip() )
			.setCity( js.getCity() )
		;
	}

	protected void merge(IrpfBreakdown mapped, IrpfBreakdown js) {
		mapped
			.setWithholdingType( nullIfNotEquals(mapped.getWithholdingType(),js.getWithholdingType()))
			.setActivity( nullIfNotEquals(mapped.getActivity(), js.getActivity()))
			.setActivityDescription( nullIfNotEquals(mapped.getActivityDescription(), js.getActivityDescription()) ) 
			.setEpigraph( nullIfNotEquals(mapped.getEpigraph(), js.getEpigraph()) )
			.setRegistryDocumentType( nullIfNotEquals(mapped.getRegistryDocumentType(), js.getRegistryDocumentType()))
			.setRegistryDocumentCountry( nullIfNotEquals(mapped.getRegistryDocumentCountry(), js.getRegistryDocumentCountry()))
			.setName( nullIfNotEquals(mapped.getName(), js.getName())  )
			.setIssueDate( nullIfNotEquals( mapped.getIssueDate(), js.getIssueDate()) )
			.setTaxDate( nullIfNotEquals( mapped.getTaxDate(), js.getTaxDate()) )
			.setSalary( nullIfNotEquals( mapped.getSalary(), js.getSalary()) )
			.setInvoice( nullIfNotEquals( mapped.getInvoice(), js.getInvoice()) )
			.setInvoiceType( nullIfNotEquals( mapped.getInvoiceType(), js.getInvoiceType()) )
			.setSeries( nullIfNotEquals( mapped.getSeries(), js.getSeries()) )
			.setNumber( nullIfNotEquals( mapped.getNumber(), js.getNumber()) )
			.setReferenceCode( nullIfNotEquals( mapped.getReferenceCode(), js.getReferenceCode()) )
			.setBase( AonMathUtils.round(mapped.getBase() + js.getBase()))
			.setPercent( negativeIfNotEquals(mapped.getPercent(),js.getPercent()) )
			.setQuota( AonMathUtils.round(mapped.getQuota() + js.getQuota()))
			.setDeductiblePercent( negativeIfNotEquals(mapped.getDeductiblePercent(),js.getDeductiblePercent()) )
			.setDeductibleQuota(AonMathUtils.round(mapped.getDeductibleQuota() + js.getDeductibleQuota()));
	}

	@Override
	public BinaryOperator<TreeMap<String, IrpfBreakdown>> combiner() {
		return ((map1, map2) -> map1);
	}

	@Override
	public Function<TreeMap<String, IrpfBreakdown>, TreeMap<String, IrpfBreakdown>> finisher() {
		return (map -> map);
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Set.of(Characteristics.UNORDERED);
	}
}
