package solutions.aon.circe.exception;

public class ReportTooLongException extends SegSocialException {
	public ReportTooLongException () {
	}
	
	public ReportTooLongException (String msg) {
		super(msg);
	}
}
