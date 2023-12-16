package es.udc.ws.app.model.matchservice;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.match.SqlMatchDao;
import es.udc.ws.app.model.match.SqlMatchDaoFactory;
import es.udc.ws.app.model.matchservice.exceptions.AlreadyRetiredException;
import es.udc.ws.app.model.matchservice.exceptions.IncorrectCreditCardException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNotAvailableException;
import es.udc.ws.app.model.matchservice.exceptions.NotEnoughUnitsException;
import es.udc.ws.app.model.purchase.Purchase;
import es.udc.ws.app.model.purchase.SqlPurchaseDao;
import es.udc.ws.app.model.purchase.SqlPurchaseDaoFactory;
import es.udc.ws.app.model.util.CustomPropertyValidator;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.*;

public class MatchServiceImpl implements MatchService{
    private final DataSource dataSource;
    private SqlMatchDao matchDao = null;
    private SqlPurchaseDao purchaseDao = null;

    public MatchServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(MATCH_DATA_SOURCE);
        matchDao = SqlMatchDaoFactory.getDao();
        purchaseDao = SqlPurchaseDaoFactory.getDao();
    }

    private void validateMatch(Match match) throws InputValidationException {

        PropertyValidator.validateMandatoryString("visitorName", match.getVisitorName());

        CustomPropertyValidator.validateVisitorName(match.getVisitorName());
        CustomPropertyValidator.validateDate(match.getMatchDate());
        PropertyValidator.validateDouble("matchPrice", match.getMatchPrice(),0,MAX_PRICE);
        PropertyValidator.validateLong("maxAvailable",match.getMaxAvailable(),1,MAX_AVAILABLE);
        PropertyValidator.validateLong("numberOfSells",match.getNumberOfSells(),0,MAX_AVAILABLE);
    }
    @Override
    public Match addMatch(Match match) throws InputValidationException{
        validateMatch(match);
        match.setCreationDate(LocalDateTime.now().withNano(0));

        try (Connection connection = dataSource.getConnection()) {

            try {
                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                Match createdMatch = matchDao.create(connection, match);

                /* Commit. */
                connection.commit();

                return createdMatch;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Match findMatch(Long matchId) throws InstanceNotFoundException {

        try (Connection connection = dataSource.getConnection()) {
            return matchDao.find(connection,matchId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Match> findMatches(LocalDate initialDate, LocalDate endDate) {

        try (Connection connection = dataSource.getConnection()){
            return matchDao.findMatches(connection, initialDate, endDate);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }



    @Override
    public Purchase purchaseMatch(String userEmail, Long matchId, String creditCardNumber, int units) throws InstanceNotFoundException, InputValidationException, MatchNotAvailableException, NotEnoughUnitsException {

        CustomPropertyValidator.validateEmail(userEmail);
        PropertyValidator.validateCreditCard(creditCardNumber);
        PropertyValidator.validateLong("units",units,1,MAX_AVAILABLE);

        try (Connection connection = dataSource.getConnection()) {

            try{
            /* Prepare connection. */
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            connection.setAutoCommit(false);

            /* Do work. */
            Match match = matchDao.find(connection, matchId);
            boolean retired=false;
            if (!(LocalDateTime.now().isBefore(match.getMatchDate()))){
                throw new MatchNotAvailableException(matchId);
            }
            if(match.getMaxAvailable()<match.getNumberOfSells()+units){
                throw new NotEnoughUnitsException(matchId,match.getMaxAvailable(),units);
            }
            Purchase purchase = purchaseDao.create(connection, new Purchase(matchId,creditCardNumber,LocalDateTime.now().withNano(0),units,userEmail, retired));
            match.setNumberOfSells(match.getNumberOfSells()+units);
            matchDao.update(connection,match);
            /* Commit. */
            connection.commit();

            return purchase;

        } catch (InstanceNotFoundException | MatchNotAvailableException | NotEnoughUnitsException e) {
            connection.commit();
            throw e;
        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);
        } catch (RuntimeException | Error e) {
            connection.rollback();
            throw e;
        }

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }}

    @Override
    public List<Purchase> findUserReservations(String userEmail) throws InputValidationException, InstanceNotFoundException {
        CustomPropertyValidator.validateEmail(userEmail);
        try (Connection connection = dataSource.getConnection()){
            return purchaseDao.findByUser(connection, userEmail);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void retirePurchase(Long purchaseId, String creditCardNumber) throws InstanceNotFoundException, InputValidationException, AlreadyRetiredException, IncorrectCreditCardException {
        PropertyValidator.validateCreditCard(creditCardNumber);
        try(Connection connection = dataSource.getConnection()){
           Purchase purchase = purchaseDao.find(connection ,purchaseId);
            if(!creditCardNumber.equals(purchase.getCreditCardNumber())){
                throw new IncorrectCreditCardException(purchase.getPurchaseId(), creditCardNumber);
            }
           if(purchase.isRetired()){
               throw new AlreadyRetiredException(purchase.getPurchaseId());
           }
           else{
               purchase.setRetired(true);
               purchaseDao.update(connection, purchase);
           }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
