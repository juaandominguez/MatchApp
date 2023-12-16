package es.udc.ws.app.restservice.json;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.model.matchservice.exceptions.AlreadyRetiredException;
import es.udc.ws.app.model.matchservice.exceptions.IncorrectCreditCardException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNotAvailableException;
import es.udc.ws.app.model.matchservice.exceptions.NotEnoughUnitsException;

public class AppExceptionToJsonConversor {
    public static ObjectNode toPurchaseExpirationException(AlreadyRetiredException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "AlreadyRetired");
        exceptionObject.put("purchaseId", (ex.getPurchaseId() != null) ? ex.getPurchaseId() : null);

        return exceptionObject;
    }

    public static ObjectNode toIncorrectCreditCardException(IncorrectCreditCardException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "IncorrectCreditCardNumber");
        exceptionObject.put("purchaseId", (ex.getPurchaseId() != null) ? ex.getPurchaseId() : null);
        if (ex.getCreditCardNumber() != null)
            exceptionObject.put("creditCardNumber", ex.getCreditCardNumber());
        else
            exceptionObject.set("creditCardNumber", null);
        return exceptionObject;
    }
    public static ObjectNode toMatchNotAvailableException(MatchNotAvailableException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "MatchNotAvailable");
        exceptionObject.put("matchId", (ex.getMatchId() != null) ? ex.getMatchId() : null);
        return exceptionObject;
    }

    public static ObjectNode toNotEnoughUnitsException(NotEnoughUnitsException ex) {

        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errorType", "NotEnoughUnits");
        exceptionObject.put("matchId", (ex.getMatchId() != null) ? ex.getMatchId() : null);
        if (ex.getMaxAvailable() != 0)
            exceptionObject.put("maxAvailable", ex.getMaxAvailable());
        else
            exceptionObject.set("maxAvailable",null);
        if (ex.getNumberOfSales() != 0)
            exceptionObject.put("numberOfSales", ex.getNumberOfSales());
        else
            exceptionObject.set("numberOfSales", null);
        return exceptionObject;
    }
}
