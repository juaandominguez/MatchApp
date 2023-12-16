package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.purchase.Purchase;

public class PurchaseToRestPurchaseDtoConversor {
    public static RestPurchaseDto toRestPurchaseDto(Purchase purchase) {
        return new RestPurchaseDto(purchase.getPurchaseId(), purchase.getMatchId(), purchase.getCreditCardNumber(), purchase.getPurchaseDate().toString(), purchase.getUnits(), purchase.getUserEmail(), purchase.isRetired());
    }
}
