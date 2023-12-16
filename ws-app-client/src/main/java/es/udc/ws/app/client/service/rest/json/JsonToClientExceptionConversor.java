package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import es.udc.ws.app.client.service.exceptions.IncorrectCreditCardException;
import es.udc.ws.app.client.service.exceptions.MatchNotAvailableException;
import es.udc.ws.app.client.service.exceptions.NotEnoughUnitsException;
import es.udc.ws.app.client.service.exceptions.AlreadyRetiredException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;

public class JsonToClientExceptionConversor {

    public static Exception fromBadRequestErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InputValidation")) {
                    return toInputValidationException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InputValidationException toInputValidationException(JsonNode rootNode) {
        String message = rootNode.get("message").textValue();
        return new InputValidationException(message);
    }

    public static Exception fromNotFoundErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if (errorType.equals("InstanceNotFound")) {
                    return toInstanceNotFoundException(rootNode);
                } else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static InstanceNotFoundException toInstanceNotFoundException(JsonNode rootNode) {
        String instanceId = rootNode.get("instanceId").textValue();
        String instanceType = rootNode.get("instanceType").textValue();
        return new InstanceNotFoundException(instanceId, instanceType);
    }

    public static Exception fromForbiddenErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                if(errorType.equals("IncorrectCreditCardNumber")) {
                    return toIncorrectCreditCardException(rootNode);
                }
                else if (errorType.equals("AlreadyRetired")) {
                    return toPurchaseExpirationException(rootNode);
                }
                else if(errorType.equals("MatchNotAvailable")){
                    return toMatchNotAvailableException(rootNode);
                }else if(errorType.equals("NotEnoughUnits")){
                    return toNotEnoughUnitsException(rootNode);
                }else {
                    throw new ParsingException("Unrecognized error type: " + errorType);
                }
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }
    private static IncorrectCreditCardException toIncorrectCreditCardException(JsonNode rootNode) {
        Long purchaseId = rootNode.get("purchaseId").longValue();
        String creditCard = rootNode.get("creditCardNumber").textValue();
        return new IncorrectCreditCardException(purchaseId,creditCard);
    }
    private static MatchNotAvailableException toMatchNotAvailableException(JsonNode rootNode) {
        Long purchaseId = rootNode.get("matchId").longValue();
        return new MatchNotAvailableException(purchaseId);
    }
    private static NotEnoughUnitsException toNotEnoughUnitsException(JsonNode rootNode) {
        Long purchaseId = rootNode.get("matchId").longValue();
        int maxAvailable = rootNode.get("maxAvailable").intValue();
        int numberOfSales = rootNode.get("numberOfSales").intValue();
        return new NotEnoughUnitsException(purchaseId,maxAvailable, numberOfSales);
    }
    public static Exception fromGoneErrorCode(InputStream ex) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(ex);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                String errorType = rootNode.get("errorType").textValue();
                throw new ParsingException("Unrecognized error type: " + errorType);
            }
        } catch (ParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static AlreadyRetiredException toPurchaseExpirationException(JsonNode rootNode) {
        Long purchaseId = rootNode.get("purchaseId").longValue();
        return new AlreadyRetiredException(purchaseId);
    }
}
