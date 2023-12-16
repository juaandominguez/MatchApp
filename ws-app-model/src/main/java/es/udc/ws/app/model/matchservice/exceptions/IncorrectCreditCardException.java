package es.udc.ws.app.model.matchservice.exceptions;

public class IncorrectCreditCardException extends Exception{
    private Long purchaseId;
    private String creditCardNumber;

    public IncorrectCreditCardException(Long purchaseId,String creditCardNumber){
        super("Credit card number=\"" + creditCardNumber +
                "\" does not correspond with the card used in the purchase with id=\"" +
                purchaseId + "\")");
        this.purchaseId=purchaseId;
        this.creditCardNumber=creditCardNumber;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }
}
