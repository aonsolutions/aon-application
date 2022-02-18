package solutions.aon.seg.social.exception;

public class RevokedCertificateException extends SegSocialException{
    public RevokedCertificateException(){}
    
    public RevokedCertificateException(String msg){
    	super(msg);
    }
}
