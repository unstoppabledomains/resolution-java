package unstoppableResolution;

public class NamingServiceException extends Exception {

  private NSExceptionCode code;
  private String detailMessage;
  
  NamingServiceException(NSExceptionCode code, String domain) {
    super(messageFromCode(code, domain));
    this.code = code;
    this.detailMessage = messageFromCode(code, domain);
  }

  public NSExceptionCode getCode() { return this.code; }

  public String getMessage() { return this.detailMessage; }

  private static String messageFromCode(NSExceptionCode code, String domain) {
    if (domain == null) domain = "Domain";
    switch(code) {
      case UnsupportedDomain: {
        return domain + " is unsupported";
      }
      case UnregisteredDomain: {
        return domain + " is not registered";
      }
      case UnknownCurrency: {
        return domain + " doesn't have such currency configured";
      }
      case RecordNotFound: {
        return domain + " doesn't have such record";
      }
      default: 
        return "";
    }
  }
}