package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.EnterpriseDataNames;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType.InvoiceCommunicationTypeAccepter;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;

public class InvoiceCommunicationConfiguration implements Serializable{

	private static final long serialVersionUID = 1L;

	private LinkedList<CommunicationData> datas = new LinkedList<>();

	private Integer defaultCertificate;
	private Certificate certificate;
	
	@Deprecated	private String lroeRegistryDate;
	@Deprecated	private String siiRegistryDate;
	@Deprecated	private boolean prepareNewSii;
	
	// -------------------------------------------------------------------- [PUBLIC]
	
	public LinkedList<CommunicationData> getDataList() {
		return datas;
	}
	public InvoiceCommunicationConfiguration setDataList(LinkedList<CommunicationData> datas) {
		if (datas == null) this.datas = new LinkedList<>();
		this.datas = datas;
		return this;
	}
	public Stream<CommunicationData> dataStream(Date atDate) {
		return AonCollectionUtils.stream( this.datas )
			.filter( cc -> cc.inRange(atDate));
	}
	public Stream<CommunicationData> dataStream() {
		return AonCollectionUtils.stream( this.datas );
	}
	public InvoiceCommunicationConfiguration addData(CommunicationData ed) {
		if (ed == null || ed.getDataName() == null) throw new AonCoreException( InvoiceCommunicationError.ICC_5000.getMessage() );
		this.datas.add(ed);
		return this;
	}

	public Integer getDefaultCertificate() {
		return defaultCertificate;
	}
	public InvoiceCommunicationConfiguration setDefaultCertificate(Integer defaultCertificate) {
		this.defaultCertificate = defaultCertificate;
		return this;
	}
	
	public Certificate getCertificate() {
		return certificate;
	}
	public InvoiceCommunicationConfiguration setCertificate(Certificate certificate) {
		this.certificate = certificate;
		return this;
	}
	
	// [ --------------- STREAMS -----------------]
	public Stream<CommunicationData> getTbaiStream() 					{return dataStream().filter( cc -> cc.isTBai() );}
	public Stream<CommunicationData> getTbaiStream(Date atDate) 		{return getTbaiStream().filter(cc -> cc.inRange(atDate));}
	public Stream<CommunicationData> getLroeStream() 					{return dataStream().filter( cc -> cc.isLroe());}
	public Stream<CommunicationData> getLroeStream(Date atDate) 		{return getLroeStream().filter(cc -> cc.inRange(atDate));}
	public Stream<CommunicationData> getSiiStream() 					{return dataStream().filter( cc->cc.isSii());}
	public Stream<CommunicationData> getSiiStream(Date atDate) 			{return getSiiStream().filter(cc -> cc.inRange(atDate));}
	public Stream<CommunicationData> getVerifactuStream() 				{return dataStream().filter(cc -> cc.isVerifactu() );}
	public Stream<CommunicationData> getVerifactuStream(Date atDate) 	{return getVerifactuStream().filter(cc -> cc.inRange(atDate));}
	public Stream<CommunicationData> getNoVerifactuStream() 			{return dataStream().filter( cc -> cc.isNoVerifactu() );}
	public Stream<CommunicationData> getNoVerifactuStream(Date atDate) 	{return getNoVerifactuStream().filter(cc -> cc.inRange(atDate));}
	public Stream<CommunicationData> getSifStream() 					{return dataStream().filter( cc -> cc.isSif());}
	public Stream<CommunicationData> getSifStream(Date atDate) 			{return getSifStream().filter(cc -> cc.inRange(atDate));}
	public Stream<CommunicationData> getNoSifStream() 					{return dataStream().filter(cc -> cc.isNoSif() );}
	public Stream<CommunicationData> getNoSifStream(Date atDate) 		{return getNoSifStream().filter(cc -> cc.inRange(atDate));}
	
	// -------------------------------- [ TICKET BAI ]
	public Optional<CommunicationData> getTbaiData() 					{ return getTbaiData( new Date() );}
	public Optional<CommunicationData> getTbaiData( Date atDate)		{ return getTbaiStream(atDate).findFirst();}
	public boolean isTbai() 											{ return isTbai(new Date());}
	public boolean isTbai(InvoiceType type) 							{ return type.isSales() && isTbai();}
	public boolean isTbai(InvoiceType type, Date atDate)				{ return type.isSales() && isTbai(atDate);}
	public boolean isTbai(Date atDate) 									{ return getTbaiData( atDate).isPresent();}
	
	// ------------------------------------- [ LROE ]
	public Optional<CommunicationData> getLroeData() 					{ return getLroeData( new Date() );}
	public Optional<CommunicationData> getLroeData( Date atDate)		{ return getLroeStream(atDate).findFirst();}
	public boolean isLroe() 											{ return isLroe( new Date() );}
	public boolean isLroe(InvoiceType type, Date date) 					{ return (isLroe(date) && ((type.isSales() && !isNoSif(date)) || !type.isSales()));}
	public boolean isLroe(InvoiceType type) 							{ return isLroe( type, new Date());}
	public boolean isLroe(Date atDate )									{ return getLroeData( atDate ).isPresent(); }
	
	// ------------------------------------- [ SII ]
	public Optional<CommunicationData> getSiiData() 					{ return getSiiData( new Date() );}
	public Optional<CommunicationData> getSiiData( Date atDate) 		{ return getSiiStream(atDate).findFirst(); }
	public boolean isSii() 												{ return isSii(InvoiceType.SALES,new Date()); }
	public boolean isSii(InvoiceType type) 								{ return isSii(type, new Date()); }
	public boolean isSii(InvoiceType type, Date atDate) 				{ 
		return isSii(atDate) 
			&& ((type.isSales() && !isNoSif(atDate) && !isTbai(atDate)) || !type.isSales());
	}
	public boolean isSii(Date atDate) 									{ return getSiiData( atDate ).isPresent(); }
	
	// ------------------------------- [ VERIFACTU ]
	public Optional<CommunicationData> getVerifactuData() 				{ return getVerifactuData( new Date() ); }
	public Optional<CommunicationData> getVerifactuData( Date atDate) 	{ return getVerifactuStream(atDate).findFirst(); }
	public boolean isVerifactu() 										{ return isVerifactu(new Date()); }
	public boolean isVerifactu(InvoiceType type) 						{ return isVerifactu( type, new Date() ); }
	public boolean isVerifactu(InvoiceType type,Date date) 				{ return type.isSales() && isVerifactu(date); }
	public boolean isVerifactu(Date atDate) 							{ return getVerifactuData( atDate ).isPresent(); }

	// ----------------------------- [ NO VERIFACTU ]
	public Optional<CommunicationData> getNoVerifactuData() 			{ return getNoVerifactuData( new Date() );}
	public Optional<CommunicationData> getNoVerifactuData( Date atDate) { return getNoVerifactuStream(atDate).findFirst(); }
	public boolean isNoVerifactu() 										{ return isNoVerifactu( new Date()); }
	public boolean isNoVerifactu(InvoiceType type) 						{ return isNoVerifactu( type, new Date() ); }
	public boolean isNoVerifactu(InvoiceType type, Date date) 			{ return type.isSales() && isNoVerifactu(date); }
	public boolean isNoVerifactu(Date atDate) 							{ return getNoVerifactuData( atDate ).isPresent(); }

	// ----------------------------------- [ SIF ]
	public Optional<CommunicationData> getSifData() 					{ return getSifData( new Date() ); }
	public Optional<CommunicationData> getSifData(Date atDate) 			{ return getSifStream(atDate).findFirst(); }
	public boolean isSif() 												{ return isSif(new Date()); }
	public boolean isSif(InvoiceType type) 								{ return isSif(type, new Date()); }
	public boolean isSif(InvoiceType type, Date atDate)					{ return type.isSales() && isSif(atDate); }
	public boolean isSif(Date atDate) 									{ return getSifData( atDate ).isPresent(); }
	
	// ----------------------------------- [ NO SIF ]
	public Optional<CommunicationData> getNoSifData() 					{ return getNoSifData( new Date() ); }
	public Optional<CommunicationData> getNoSifData( Date atDate) 		{ return getNoSifStream(atDate).findFirst(); }
	public boolean isNoSif() 											{ return isNoSif( new Date());}
	public boolean isNoSif(Date atDate)									{ return getNoSifData( atDate ).isPresent(); }

	// ----------------------------------- [ COMMUNICATION ]
	public boolean hasCommunication() 									{ return hasCommunication(InvoiceType.SALES); }
	public boolean hasCommunication(Date expDate) 						{ return hasCommunication(InvoiceType.SALES, expDate); }
	public boolean hasCommunication(InvoiceType type) 					{ return hasCommunication(type, new Date()); }
	public boolean hasCommunication(InvoiceType type, Date expDate)  	{ return typesStream(type, expDate).anyMatch( cc -> !cc.isNoSif() ); }
	
	// ------------------------- [ COMMUNICATION DATA TYPES]
	public List<CommunicationData> getTypes() 									{ return getTypes(InvoiceType.SALES, new Date());}
	public List<CommunicationData> getTypes(InvoiceType type ) 					{ return getTypes(type , new Date());}
	public List<CommunicationData> getTypes(Date atDate) 						{ return getTypes(InvoiceType.SALES, atDate);}
	public Stream<CommunicationData> typesStream() 								{ return typesStream(InvoiceType.SALES, new Date());}
	public Stream<CommunicationData> typesStream(InvoiceType type) 				{ return AonCollectionUtils.stream(getTypes(type));}
	public Stream<CommunicationData> typesStream(Date atDate) 					{ return AonCollectionUtils.stream(getTypes(atDate));}
	public Stream<CommunicationData> typesStream(InvoiceType type ,Date atDate) { return AonCollectionUtils.stream(getTypes(type, atDate));}
	
	public List<CommunicationData> getTypes(InvoiceType type ,Date atDate) {
		InvoiceCommunicationTypeEngine engine = new InvoiceCommunicationTypeEngine();
		return engine.getTypes(this, type, atDate);
	}
	
	// ------------ [ IS COMMUNICATION CERTIFICATE NEEDED ]
	public boolean isCertificateNeeded( ) { return isCertificateNeeded( InvoiceType.SALES );}
	public boolean isCertificateNeeded( InvoiceType invoiceType ) {
		return AonCollectionUtils.stream(getTypes(invoiceType))
			.anyMatch(t -> t.isCertificateNeeded()
			)
		;
	}
	public Optional<CommunicationData> getData(InvoiceCommunicationType t, Date atDate) {
		if ( t == null ) return Optional.empty();
		return t.accept( new InvoiceCommunicationTypeAccepter<Optional<CommunicationData>>() {
			@Override public Optional<CommunicationData> visitVERIFACTU() { return getVerifactuData(atDate); }
			@Override public Optional<CommunicationData> visitSII() { return getSiiData(atDate); }
			@Override public Optional<CommunicationData> visitTBAI() { return getTbaiData(atDate); }
			@Override public Optional<CommunicationData> visitLROE() { return getLroeData(atDate); }
			@Override public Optional<CommunicationData> visitNO_VERIFACTU() { return getNoVerifactuData(atDate); }
			@Override public Optional<CommunicationData> visitSIF() { return getSifData(atDate); }
			@Override public Optional<CommunicationData> visitSERES() { return Optional.empty(); }
			@Override public Optional<CommunicationData> visitEMAIL() { return Optional.empty(); }
			@Override public Optional<CommunicationData> visitCLOSING() { return Optional.empty(); }
			@Override public Optional<CommunicationData> visitFACTURAE() { return Optional.empty(); }
		});
	}	
	public Optional<CommunicationData> getData(EnterpriseDataNames name, Date atDate) {
		if (name == EnterpriseDataNames.ICC_NO_SIF) return getNoSifData(atDate);
		return InvoiceCommunicationType.get( name).flatMap( t -> getData(t, atDate) );
	}
	
	public boolean isAraba() { return isAraba(new Date()); }
	public boolean isAraba(Date atDate) { return dataStream(atDate).anyMatch(cc -> cc.isAraba()); }
	public boolean isGipuzkoa() { return isGipuzkoa(new Date()); }
	public boolean isGipuzkoa(Date atDate) { return dataStream(atDate).anyMatch( cc -> cc.isGipuzkoa()); }
	public boolean isBizkaia() { return isBizkaia(new Date()); }
	public boolean isBizkaia(Date atDate) { return dataStream(atDate).anyMatch( cc -> cc.isBizkaia()); }
	public boolean isNavarra() { return isNavarra(new Date()); }
	public boolean isNavarra(Date atDate) { return dataStream(atDate).anyMatch( cc -> cc.isNavarra()); }
	public boolean isCanarias() { return isCanarias(new Date()); }
	public boolean isCanarias(Date atDate) { return dataStream(atDate).anyMatch( cc -> cc.isCanarias()); }
	public boolean isAEAT() { return isAEAT(new Date()); }
	public boolean isAEAT(Date atDate) { return dataStream(atDate).anyMatch( cc -> cc.isAEAT()); }
	
	public Optional<Administration> getAdministration() {
		return getAdministration(new Date());
	}
	public Optional<Administration> getAdministration(Date atDate) {
		if (isAEAT(atDate)) return Optional.of(Administration.COMMON_TERRITORY);
		if (isCanarias(atDate)) return Optional.of(Administration.CANARIAS);
		if (isBizkaia(atDate)) return Optional.of(Administration.BIZKAIA);
		if (isGipuzkoa(atDate)) return Optional.of(Administration.GIPUZKOA);
		if (isAraba(atDate)) return Optional.of(Administration.ALAVA);
		if (isNavarra(atDate)) return Optional.of(Administration.NAVARRA);
		return Optional.empty();
	}
	
	// ************************************************************************ 
	// ****************************************************************** [OLD]
	// ************************************************************************ 
	
	@Deprecated
	public String getLroeRegistryDate() {
		return lroeRegistryDate;
	}
	
	@Deprecated
	public InvoiceCommunicationConfiguration setLroeRegistryDate(String lroeRegistryDate) {
		this.lroeRegistryDate = lroeRegistryDate;
		return this;
	}
	
	@Deprecated
	public String getSiiRegistryDate() {
		return siiRegistryDate;
	}
	
	@Deprecated
	public InvoiceCommunicationConfiguration setSiiRegistryDate(String siiRegistryDate) {
		this.siiRegistryDate = siiRegistryDate;
		return this;
	}
	
	@Deprecated
	public boolean isRegistryTaxDate() {
		return "tax".equalsIgnoreCase(getSiiRegistryDate());
	}
	
	@Deprecated
	public boolean isPrepareNewSii() {
		return prepareNewSii;
	}
	
	@Deprecated
	public InvoiceCommunicationConfiguration setPrepareNewSii(boolean prepareNewSii) {
		this.prepareNewSii = prepareNewSii;
		return this;
	}
}
