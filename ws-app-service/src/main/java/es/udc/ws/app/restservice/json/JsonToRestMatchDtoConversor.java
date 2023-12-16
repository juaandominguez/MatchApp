package es.udc.ws.app.restservice.json;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.udc.ws.app.restservice.dto.RestMatchDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

public class JsonToRestMatchDtoConversor {

    public static ObjectNode toObjectNode(RestMatchDto match) {

        ObjectNode matchObject = JsonNodeFactory.instance.objectNode();

        matchObject.put("matchId", match.getMatchId()).
                put("visitorName", match.getVisitorName()).
                put("matchDate", match.getMatchDate()).
                put("matchPrice", match.getMatchPrice()).
                put("maxAvailable", match.getMaxAvailable()).
                put("numberOfSells", match.getNumberOfSells());

        return matchObject;
    }



    public static ArrayNode toArrayNode(List<RestMatchDto> matches) {

        ArrayNode matchesNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < matches.size(); i++) {
            RestMatchDto matchDto = matches.get(i);
            ObjectNode matchObject = toObjectNode(matchDto);
            matchesNode.add(matchObject);
        }

        return matchesNode;
    }

    public static RestMatchDto toRestMatchDto(InputStream jsonMatch) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonMatch);

            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode matchObject = (ObjectNode) rootNode;

                JsonNode matchIdNode = matchObject.get("matchId");
                Long matchId = (matchIdNode != null) ? matchIdNode.longValue() : null;

                double matchPrice =  matchObject.get("matchPrice").doubleValue();
                int maxAvailable = matchObject.get("maxAvailable").intValue();
                int numberOfSells = matchObject.get("numberOfSells").intValue();
                String visitorName = matchObject.get("visitorName").textValue().trim();
                String matchDate = matchObject.get("matchDate").textValue().trim();

                return new RestMatchDto(matchId, visitorName, matchDate, matchPrice, maxAvailable, numberOfSells);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

}
