package es.udc.ws.app.client.service.rest.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientPurchaseDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToClientPurchaseDtoConversor {

    public static ClientPurchaseDto toClientPurchaseDto(InputStream jsonSale) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonSale);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode purchaseObject = (ObjectNode) rootNode;

                JsonNode purchaseIdNode = purchaseObject.get("purchaseId");
                Long purchaseId = (purchaseIdNode != null) ? purchaseIdNode.longValue() : null;

                Long matchId = purchaseObject.get("matchId").longValue();
                String creditCardNumber = purchaseObject.get("creditCardNumber").textValue().trim();
                String purchaseDate = purchaseObject.get("purchaseDate").textValue().trim();
                int units = purchaseObject.get("units").intValue();
                String userEmail = purchaseObject.get("userEmail").textValue().trim();
                boolean retired = purchaseObject.get("retired").booleanValue();

                return new ClientPurchaseDto(purchaseId, matchId, creditCardNumber,purchaseDate, units, userEmail, retired);

            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
    public static List<ClientPurchaseDto> toClientPurchaseDtos(InputStream jsonPurchases) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonPurchases);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode purchasesArray = (ArrayNode) rootNode;
                List<ClientPurchaseDto> purchaseDtos = new ArrayList<>(purchasesArray.size());
                for (JsonNode purchaseNode : purchasesArray) {
                    purchaseDtos.add(toClientPurchaseDto(purchaseNode));
                }

                return purchaseDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
    private static ClientPurchaseDto toClientPurchaseDto(JsonNode purchaseNode) throws ParsingException {
        if (purchaseNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            ObjectNode matchObject = (ObjectNode) purchaseNode;

            JsonNode purchaseIdNode = matchObject.get("purchaseId");
            Long purchaseId = (purchaseIdNode != null) ? purchaseIdNode.longValue() : null;

            Long matchId = matchObject.get("matchId").longValue();
            String creditCardNumber = matchObject.get("creditCardNumber").textValue().trim();
            String purchaseDate = matchObject.get("purchaseDate").textValue().trim();
            int units =  matchObject.get("units").intValue();
            String userEmail = matchObject.get("userEmail").textValue().trim();
            boolean retired = matchObject.get("retired").booleanValue();

            return new ClientPurchaseDto(purchaseId,matchId,creditCardNumber,purchaseDate,units,userEmail,retired);

        }
    }

}
