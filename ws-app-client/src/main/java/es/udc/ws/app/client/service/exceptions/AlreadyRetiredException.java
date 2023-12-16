package es.udc.ws.app.client.service.exceptions;

public class AlreadyRetiredException extends Exception{
    private Long purchaseId;

    public AlreadyRetiredException(Long purchaseId){
        super("Purchase with id = \"" + purchaseId + "\" has been " +
                "already retired");
        this.purchaseId = purchaseId;
    }

    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }
}
