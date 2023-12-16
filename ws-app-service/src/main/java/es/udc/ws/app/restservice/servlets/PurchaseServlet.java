package es.udc.ws.app.restservice.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.udc.ws.util.json.ExceptionToJsonConversor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import es.udc.ws.app.restservice.dto.RestPurchaseDto;
import es.udc.ws.app.model.matchservice.MatchServiceFactory;
import es.udc.ws.app.model.matchservice.exceptions.NotEnoughUnitsException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNotAvailableException;
import es.udc.ws.app.model.matchservice.exceptions.IncorrectCreditCardException;
import es.udc.ws.app.model.matchservice.exceptions.AlreadyRetiredException;
import es.udc.ws.app.model.purchase.Purchase;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JsonToRestPurchaseDtoConversor;
import es.udc.ws.app.restservice.dto.PurchaseToRestPurchaseDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;

@SuppressWarnings("serial")
public class PurchaseServlet extends RestHttpServletTemplate {

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        if(req.getPathInfo() == null || req.getPathInfo().equals("/")) {
            String userEmail = ServletUtils.getMandatoryParameter(req, "userEmail");
            Long matchId = ServletUtils.getMandatoryParameterAsLong(req, "matchId");
            String creditCardNumber = ServletUtils.getMandatoryParameter(req, "creditCardNumber");
            String units = ServletUtils.getMandatoryParameter(req, "units");
            try {
                int unitsAsInt = Integer.parseInt(units);
                Purchase purchase = MatchServiceFactory.getService().purchaseMatch(userEmail, matchId, creditCardNumber, unitsAsInt);
                RestPurchaseDto purchaseDto = PurchaseToRestPurchaseDtoConversor.toRestPurchaseDto(purchase);
                String purchaseURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + purchase.getPurchaseId().toString();
                Map<String, String> headers = new HashMap<>(1);
                headers.put("Location", purchaseURL);
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                        JsonToRestPurchaseDtoConversor.toObjectNode(purchaseDto), headers);
            } catch (MatchNotAvailableException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toMatchNotAvailableException(e), null);
            } catch (NotEnoughUnitsException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toNotEnoughUnitsException(e), null);
            } catch (NumberFormatException e){
                ServletUtils.writeServiceResponse(resp, 400, ExceptionToJsonConversor.toInputValidationException(new InputValidationException("Invalid units: "+ units)), (Map)null);
            }
        }
        else{
            Long purchaseId = ServletUtils.getIdFromPath(req, "purchase");
            String creditCardNumber = ServletUtils.getMandatoryParameter(req, "creditCardNumber");

            try {
                MatchServiceFactory.getService().retirePurchase(purchaseId,creditCardNumber);
            } catch (AlreadyRetiredException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toPurchaseExpirationException(e), null);
                return;
            } catch (IncorrectCreditCardException e) {
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toIncorrectCreditCardException(e), null);
                return;
            }
            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_NO_CONTENT,null,null);
        }
    }
    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        String userEmail = ServletUtils.getMandatoryParameter(req, "userEmail");

        List<Purchase> purchases = MatchServiceFactory.getService().findUserReservations(userEmail);

        List<RestPurchaseDto> purchaseDtos = new ArrayList<>();
        for(Purchase purchase : purchases){
            purchaseDtos.add(PurchaseToRestPurchaseDtoConversor.toRestPurchaseDto(purchase));
        }
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                JsonToRestPurchaseDtoConversor.toArrayNode(purchaseDtos), null);
    }

}
