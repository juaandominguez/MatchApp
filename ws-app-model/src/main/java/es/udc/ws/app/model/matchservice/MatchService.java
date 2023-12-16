package es.udc.ws.app.model.matchservice;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.matchservice.exceptions.AlreadyRetiredException;
import es.udc.ws.app.model.matchservice.exceptions.IncorrectCreditCardException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNotAvailableException;
import es.udc.ws.app.model.matchservice.exceptions.NotEnoughUnitsException;
import es.udc.ws.app.model.purchase.Purchase;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface MatchService {

    public Match addMatch(Match match) throws InputValidationException;
    public List<Match> findMatches(LocalDate initialDate, LocalDate endDate);
    public Match findMatch(Long matchId) throws InstanceNotFoundException;
    Purchase purchaseMatch(String userEmail, Long matchId, String creditCardNumber, int units) throws InstanceNotFoundException, InputValidationException, MatchNotAvailableException, NotEnoughUnitsException;

    public List<Purchase> findUserReservations(String userEmail) throws InputValidationException, InstanceNotFoundException;
    public void retirePurchase(Long purchaseId, String creditCardNumber) throws InstanceNotFoundException, InputValidationException, AlreadyRetiredException, IncorrectCreditCardException;


}
