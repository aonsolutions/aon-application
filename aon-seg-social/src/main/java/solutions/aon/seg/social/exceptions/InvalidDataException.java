package solutions.aon.seg.social.exceptions;

public class InvalidDataException extends SegSocialException{
	public static void checkCode(Integer statusCode) throws SegSocialException {
		switch (statusCode) {
		case 221:
		case 2006:
		case 3083:
		case 2148:
		case 3252:
			break;	
		case 4860:
			throw new NotAllFilledException();
		case 3030:
			throw new WrongRegimeException();
		case 3260:
			throw new WrongRegimeException();
		case 3066:
			throw new SyntaxException();
		case 3820:
			throw new WrongAffNumber();
		case 3462:
			throw new NotAllowedContributionAccount();
		case 4879:
			throw new InvalidPrintingMethod();
		case 3001:
			throw new UnfilledMandatory();
		case 3543:
			throw new NoQueryData();
		case 3823:
			throw new invalidCccException();
		case 3037:
			throw new NoMoreDataException();
		case 2147:
			throw new ExistingSecondaryUserException();
		default:
			throw new InvalidDataException();
		}
	}
}
