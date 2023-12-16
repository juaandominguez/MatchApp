package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientPurchaseDto;
import es.udc.ws.app.thrift.ThriftPurchaseDto;

import java.util.ArrayList;
import java.util.List;

public class ClientPurchaseDtoToThriftPurchaseDtoConversor {
    public static ThriftPurchaseDto toThriftPurchaseDto(
            ClientPurchaseDto clientPurchaseDto) {

        Long purchaseId = clientPurchaseDto.getPurchaseId();

        return new ThriftPurchaseDto(
                purchaseId == null ? -1 : purchaseId,
                clientPurchaseDto.getMatchId(),
                clientPurchaseDto.getCreditCardNumber(),
                clientPurchaseDto.getPurchaseDate(),
                (short) clientPurchaseDto.getUnits(),
                clientPurchaseDto.getUserEmail(),
                clientPurchaseDto.isRetired());

    }

    public static List<ClientPurchaseDto> toClientPurchaseDtos(List<ThriftPurchaseDto> purchases) {

        List<ClientPurchaseDto> clientPurchaseDtos = new ArrayList<>(purchases.size());

        for (ThriftPurchaseDto purchase : purchases) {
            clientPurchaseDtos.add(toClientPurchaseDto(purchase));
        }
        return clientPurchaseDtos;

    }

    public static ClientPurchaseDto toClientPurchaseDto(ThriftPurchaseDto purchase) {

        return new ClientPurchaseDto(
                purchase.getPurchaseId(),
                purchase.getMatchId(),
                purchase.getCreditCardNumber(),
                purchase.getPurchaseDate(),
                purchase.getUnits(),
                purchase.getUserEmail(),
                purchase.isRetired());
    }

}
