package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestMatchDto;
import es.udc.ws.app.restservice.dto.RestPurchaseDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.util.List;

public class JsonToRestPurchaseDtoConversor {

    public static ObjectNode toObjectNode(RestPurchaseDto purchase) {

        ObjectNode purchaseNode = JsonNodeFactory.instance.objectNode();

        if (purchase.getPurchaseId() != null) {
            purchaseNode.put("purchaseId", purchase.getPurchaseId());
        }
       purchaseNode.put("matchId",purchase.getMatchId()).
                put("creditCardNumber", purchase.getCreditCardNumber()).
                put("purchaseDate", purchase.getPurchaseDate()).
                put("units",purchase.getUnits()).
                put("userEmail",purchase.getUserEmail()).
                put("retired",purchase.isRetired());

        return purchaseNode;
    }

    public static ArrayNode toArrayNode(List<RestPurchaseDto> purchases) {

        ArrayNode purchasesNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < purchases.size(); i++) {
            RestPurchaseDto purchaseDto = purchases.get(i);
            ObjectNode purchaseObject = toObjectNode(purchaseDto);
            purchasesNode.add(purchaseObject);
        }

        return purchasesNode;
    }
    public static RestMatchDto toRestPurchaseDto(InputStream jsonMatch) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonMatch);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode movieObject = (ObjectNode) rootNode;

                JsonNode matchIdNode = movieObject.get("matchId");
                Long movieId = (matchIdNode != null) ? matchIdNode.longValue() : null;

                String visitorName = movieObject.get("visitorName").textValue().trim();
                String matchDate = movieObject.get("matchDate").textValue().trim();
                double matchPrice =  movieObject.get("matchPrice").doubleValue();
                int maxAvailable = movieObject.get("maxAvailable").intValue();
                int numberOfSells = movieObject.get("numberOfSells").intValue();

                return new RestMatchDto(movieId, visitorName, matchDate, matchPrice, maxAvailable, numberOfSells);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
}