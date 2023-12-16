package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.matchservice.MatchServiceFactory;
import es.udc.ws.app.model.matchservice.exceptions.AlreadyRetiredException;
import es.udc.ws.app.model.matchservice.exceptions.IncorrectCreditCardException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNotAvailableException;
import es.udc.ws.app.model.matchservice.exceptions.NotEnoughUnitsException;
import es.udc.ws.app.model.purchase.Purchase;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import org.apache.thrift.TException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ThriftMatchServiceImpl implements ThriftMatchService.Iface{

    @Override
    public ThriftMatchDto addMatch(ThriftMatchDto matchDto) throws ThriftInputValidationException {
        try {
            Match match = MatchToThriftMatchDtoConversor.toMatch(matchDto);
            Match addedMatch = MatchServiceFactory.getService().addMatch(match);
            return MatchToThriftMatchDtoConversor.toThriftMatchDto(addedMatch);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }

    }
    @Override
    public List<ThriftMatchDto> findMatches(String date) throws ThriftInputValidationException{
        try {
            List<Match> matches = MatchServiceFactory.getService().findMatches(LocalDate.now(), LocalDate.parse(date));
            return MatchToThriftMatchDtoConversor.toThriftMatchDtos(matches);
        }catch(DateTimeParseException e){
           throw new ThriftInputValidationException(e.getMessage());

        }
    }

        @Override
    public ThriftMatchDto findMatch(long matchId) throws ThriftInstanceNotFoundException {
        try {

            Match match = MatchServiceFactory.getService().findMatch(matchId);
            return MatchToThriftMatchDtoConversor.toThriftMatchDto(match);

        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        }
    }

    @Override
    public long purchaseMatch(String userEmail, long matchId, String creditCardNumber, short units) throws ThriftInstanceNotFoundException, ThriftInputValidationException, ThriftMatchNotAvailableException, ThriftNotEnoughUnitsException {
        try {
            Purchase purchase = MatchServiceFactory.getService().purchaseMatch(userEmail, matchId, creditCardNumber, units);
            return PurchaseToThriftPurchaseDtoConversor.toThriftPurchaseDto(purchase).getPurchaseId();

        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }
        catch (MatchNotAvailableException e){
            throw new ThriftMatchNotAvailableException(e.getMatchId());
        }
        catch (NotEnoughUnitsException e){
            throw new ThriftNotEnoughUnitsException(e.getMatchId(),(short) e.getMaxAvailable(), (short) e.getNumberOfSales());
        }
    }

    @Override
    public List<ThriftPurchaseDto> findUserReservations(String userEmail)
            throws ThriftInputValidationException, ThriftInstanceNotFoundException, TException {
        try {
            List<Purchase> purchases = MatchServiceFactory.getService().findUserReservations(userEmail);
            return PurchaseToThriftPurchaseDtoConversor.toThriftPurchasesDtos(purchases);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));

        }

    }

    @Override
    public void retirePurchase(long purchaseId, String creditCardNumber)
            throws ThriftInstanceNotFoundException, ThriftInputValidationException, ThriftAlreadyRetiredException, ThriftIncorrectCreditCardException, TException {
        try{
            MatchServiceFactory.getService().retirePurchase(purchaseId,creditCardNumber);
        }catch (AlreadyRetiredException e){
            throw new ThriftAlreadyRetiredException(purchaseId);
        }catch (IncorrectCreditCardException e){
            throw new ThriftIncorrectCreditCardException(purchaseId,creditCardNumber);
        }catch (InputValidationException e) {
        throw new ThriftInputValidationException(e.getMessage());
        } catch (InstanceNotFoundException e) {
        throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        }
    }
}
