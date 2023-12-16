package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.purchase.Purchase;
import es.udc.ws.app.thrift.ThriftMatchDto;
import es.udc.ws.app.thrift.ThriftPurchaseDto;

import java.util.ArrayList;
import java.util.List;


public class PurchaseToThriftPurchaseDtoConversor {

    public static List<ThriftPurchaseDto> toThriftPurchasesDtos(List<Purchase> purchases) {

        List<ThriftPurchaseDto> dtos = new ArrayList<>(purchases.size());

        for (Purchase purchase : purchases) {
            dtos.add(toThriftPurchaseDto(purchase));
        }
        return dtos;

    }
    public static ThriftPurchaseDto toThriftPurchaseDto(Purchase purchase) {

        return new ThriftPurchaseDto(purchase.getPurchaseId(), purchase.getMatchId(),
                purchase.getCreditCardNumber(),
                purchase.getPurchaseDate().toString(),
                (short) purchase.getUnits(), purchase.getUserEmail(), purchase.isRetired());

    }
}
