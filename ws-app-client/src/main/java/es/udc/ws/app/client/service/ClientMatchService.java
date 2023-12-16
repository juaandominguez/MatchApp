package es.udc.ws.app.client.service;

import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientPurchaseDto;
import es.udc.ws.app.client.service.exceptions.IncorrectCreditCardException;
import es.udc.ws.app.client.service.exceptions.MatchNotAvailableException;
import es.udc.ws.app.client.service.exceptions.NotEnoughUnitsException;
import es.udc.ws.app.client.service.exceptions.AlreadyRetiredException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface ClientMatchService {

    public Long addMatch (ClientMatchDto match)
            throws InputValidationException;

    public List<ClientMatchDto> findMatches(String date)
            throws InputValidationException;

    public ClientMatchDto findMatch(Long matchId) throws InstanceNotFoundException;

    public Long purchaseMatch (String userEmail, Long matchId, String creditCardNumber, int units) throws InstanceNotFoundException, InputValidationException, MatchNotAvailableException, NotEnoughUnitsException;

    public List<ClientPurchaseDto> findUserReservations(String userEmail)
            throws InstanceNotFoundException, InputValidationException;

    public void retirePurchase(Long purchaseId, String creditCardNumber)
            throws InstanceNotFoundException, InputValidationException, AlreadyRetiredException, IncorrectCreditCardException;

}
