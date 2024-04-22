package solutions.aon.circe.exception;

public class RevokedCertificateException extends SegSocialException{
    public RevokedCertificateException(){}
    
    public RevokedCertificateException(String msg){
    	super(msg);
    }
}
