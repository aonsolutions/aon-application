package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.Consumer;

import com.esferalia.aon.occam.api.model.invoice.CommunicationData;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.watson.error.AonCoreException;

public class InvoiceCommunicationConfigurationValidation {

	private InvoiceCommunicationConfigurationValidation() {
	}
	
//	private static final Consumer<InvoiceCommunicationConfiguration> NO_SIF_VALIDATION = icc -> {
//		icc.getNoSifStream()
//			.filter(ed -> ed.isNotDeleted() )
//			.filter(ed -> ed.getEndDate() == null )
//			.map(ed -> ed.getStartDate())
//			.forEach(atDate -> {
//				if (icc.isTbai()) throw new AonCoreException(InvoiceCommunicationError.ICC_5030.getMessage() );
//				if (icc.isVerifactu()) throw new AonCoreException(InvoiceCommunicationError.ICC_5031.getMessage() );
//				if (icc.isNoVerifactu()) throw new AonCoreException(InvoiceCommunicationError.ICC_5032.getMessage() );
//				if (icc.isSif()) throw new AonCoreException(InvoiceCommunicationError.ICC_5033.getMessage() );
//			});
//	};
//
//	private static final Consumer<InvoiceCommunicationConfiguration> SIF_VALIDATION = icc -> {
//		icc.getSifStream()
//			.filter(ed -> ed.isNotDeleted() )
//			.filter(sif -> sif.getEndDate() == null )
//			.map(sif -> sif.getStartDate())
//			.forEach(atDate -> {
//				if (icc.isVerifactu(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5034.getMessage() );
//				if (icc.isNoVerifactu(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5035.getMessage() );
//				if (icc.isSii(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5036.getMessage() );
//				if (icc.isLroe(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5037.getMessage() );
//				if (icc.isTbai(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5038.getMessage() );
//			});
//	};	
//
//	private static final Consumer<InvoiceCommunicationConfiguration> SII_VALIDATION = icc -> {
//		icc.getSiiStream()
//			.filter(ed -> ed.isNotDeleted() )
//			.filter(sii -> sii.getEndDate() == null )
//			.map(sii -> sii.getStartDate())
//			.forEach(atDate -> {
//				if (icc.isVerifactu(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5021.getMessage() );
//				if (icc.isNoVerifactu(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5022.getMessage() );
//				if (icc.isSif(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5023.getMessage() );
//				if (icc.isLroe(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5024.getMessage() );
//			});
//	};
//
//	private static final Consumer<InvoiceCommunicationConfiguration> VERIFACTU_VALIDATION = icc -> {
//		icc.getVerifactuStream()
//			.filter(ed -> ed.isNotDeleted() )
//			.filter(ed -> ed.getEndDate() == null )
//			.map(ed -> ed.getStartDate())
//			.forEach(atDate -> {
//				if (icc.isNoVerifactu(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5016.getMessage() ); 
//				if (icc.isSif(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5017.getMessage() );
//				if (icc.isSii(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5018.getMessage() );
//				if (icc.isTbai(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5019.getMessage() );
//				if (icc.isLroe(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5020.getMessage() );
//			});
//	};
//
//	public static final Consumer<InvoiceCommunicationConfiguration> NO_VERIFACTU_VALIDATION = icc -> {
//		icc.getNoVerifactuStream()
//			.filter(ed -> ed.isNotDeleted() )
//			.filter(ed -> ed.getEndDate() == null )
//			.map(ed -> ed.getStartDate())
//			.forEach(atDate -> {
//				if (icc.isVerifactu(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5025.getMessage() );
//				if (icc.isSif(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5026.getMessage() );
//				if (icc.isSii(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5027.getMessage() );
//				if (icc.isTbai(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5028.getMessage() );
//				if (icc.isLroe(atDate)) throw new AonCoreException(InvoiceCommunicationError.ICC_5029.getMessage() );
//			});
//	};
//	
//	public static final Consumer<InvoiceCommunicationConfiguration> TBAI_VALIDATION = icc -> {
//		icc.getTbaiStream()
//			.filter(ed -> ed.isNotDeleted() )
//			.filter(ed -> ed.getEndDate() == null )
//			.map(ed -> ed.getStartDate())
//			.forEach(atDate -> {
//				if (icc.isVerifactu(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5007.getMessage() );
//				if (icc.isNoVerifactu(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5008.getMessage() );
//				if (icc.isSif(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5009.getMessage() );
//				if (icc.isLroe(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5010.getMessage() );
//			});
//	};
//	
//	public static final Consumer<InvoiceCommunicationConfiguration> LROE_VALIDATION = icc -> {
//		icc.getLroeStream()
//			.filter(ed -> ed.isNotDeleted() )
//			.filter(ed -> ed.getEndDate() == null )
//			.map(ed -> ed.getStartDate())
//			.forEach(atDate -> {
//				if (icc.isVerifactu(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5011.getMessage() );
//				if (icc.isNoVerifactu(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5012.getMessage() );
//				if (icc.isSif(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5013.getMessage() );
//				if (icc.isSii(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5014.getMessage() );
//				if (icc.isTbai(atDate)) throw new AonCoreException( InvoiceCommunicationError.ICC_5015.getMessage() );
//			});
//	};
	public static void validate(InvoiceCommunicationConfiguration icc) {
//		TBAI_VALIDATION
//			.andThen(LROE_VALIDATION)
//			.andThen(VERIFACTU_VALIDATION)
//			.andThen(NO_VERIFACTU_VALIDATION)
//			.andThen(SII_VALIDATION)
//			.andThen(SIF_VALIDATION)
//			.andThen(NO_SIF_VALIDATION)
//		.accept(icc);
	}
//	
//	public static void validate(AONContext ctx, InvoiceCommunicationConfiguration icc) throws AonCoreException{
//		validate(icc);
//	}

	public static final Consumer<CommunicationData> EMPTY = cc -> {
		if (cc == null) throw new AonCoreException (InvoiceCommunicationError.ICC_5000.getMessage());
	};
	public static final Consumer<CommunicationData> EMPTY_NAME = cc -> {
		if (cc.getDataName()  == null) throw new AonCoreException (InvoiceCommunicationError.ICC_5001.getMessage());
	};
	public static final Consumer<CommunicationData> EMPTY_ADMINISTRATION = cc -> {
		if (cc.getAdministration().isEmpty()) throw new AonCoreException (InvoiceCommunicationError.ICC_5002.getMessage());
	};
	public static final Consumer<CommunicationData> EMPTY_START_DATE = cc -> {
		if (cc.getStartDate() == null) throw new AonCoreException (InvoiceCommunicationError.ICC_5003.getMessage());
	};
	public static final Consumer<CommunicationData> START_DATE_END_DATE = cc -> {
		if (cc.getEndDate() != null && cc.getStartDate().after(cc.getEndDate()) ) 
			throw new AonCoreException (InvoiceCommunicationError.ICC_5004.getMessage());
	};
	public static final Consumer<CommunicationData> LROE_ADMINISTRATION = cc -> {
		if (cc.isLroe() && !cc.isBizkaia()) 
			throw new AonCoreException (InvoiceCommunicationError.ICC_5020.getMessage());
	};
	public static final Consumer<CommunicationData> TBAI_VALIDATION = cc -> {
		if (cc.isTBai() && !cc.isAraba ()&& !cc.isGipuzkoa()) 
			throw new AonCoreException (InvoiceCommunicationError.ICC_5021.getMessage());
	};
	public static final Consumer<CommunicationData> VERIFACTU_VALIDATION = cc -> {
		if (cc.isVerifactu() && !cc.isAEAT() && !cc.isCanarias()) 
			throw new AonCoreException (InvoiceCommunicationError.ICC_5022.getMessage());
	};
	public static final Consumer<CommunicationData> NO_VERIFACTU_VALIDATION = cc -> {
		if (cc.isNoVerifactu() && !cc.isAEAT() && !cc.isCanarias()) 
			throw new AonCoreException (InvoiceCommunicationError.ICC_5023.getMessage());
	};
	public static final Consumer<CommunicationData> SII_VALIDATION = cc -> {
		if (cc.isSii() && !cc.isAEAT() && !cc.isCanarias() && !cc.isNavarra() && !cc.isAraba() && !cc.isGipuzkoa())	
			throw new AonCoreException (InvoiceCommunicationError.ICC_5024.getMessage());
	};
	public static final Consumer<CommunicationData> SIF_VALIDATION = cc -> {
		if (cc.isSif() && !cc.isNavarra() )	
			throw new AonCoreException (InvoiceCommunicationError.ICC_5025.getMessage());
	};
	public static void validate(CommunicationData toEnable) {
		EMPTY
		.andThen(EMPTY_NAME)
		.andThen(EMPTY_ADMINISTRATION)
		.andThen(EMPTY_START_DATE)
		.andThen(START_DATE_END_DATE)
		.andThen(LROE_ADMINISTRATION)
		.andThen(TBAI_VALIDATION)
		.andThen(VERIFACTU_VALIDATION)
		.andThen(NO_VERIFACTU_VALIDATION)
		.andThen(SII_VALIDATION)
		.andThen(SIF_VALIDATION)
		.accept(toEnable);
		
	}

}
