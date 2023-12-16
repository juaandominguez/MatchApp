package es.udc.ws.app.client.service.exceptions;

public class IncorrectCreditCardException extends Exception{
    private Long purchaseId;
    private String creditCardNumber;

    public IncorrectCreditCardException(Long purchaseId, String creditCardNumber){
        super("Purchase with id = \"" + purchaseId + "\" " +
                "has an incorrect credit card number (incorrect card number = \"" +
                creditCardNumber +"\")");
        this.purchaseId = purchaseId;
        this.creditCardNumber = creditCardNumber;
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
