package es.udc.ws.app.model.matchservice.exceptions;

public class AlreadyRetiredException extends Exception{

    private Long purchaseId;
    public AlreadyRetiredException(Long purchaseId){
        super("Purchase with id=\"" + purchaseId +
                "\" has already been retired");
        this.purchaseId=purchaseId;
    }
    public Long getPurchaseId() {
        return purchaseId;
    }
    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }


}
