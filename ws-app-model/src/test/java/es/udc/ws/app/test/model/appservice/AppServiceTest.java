package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.match.SqlMatchDao;
import es.udc.ws.app.model.match.SqlMatchDaoFactory;
import es.udc.ws.app.model.matchservice.MatchService;
import es.udc.ws.app.model.matchservice.MatchServiceFactory;
import es.udc.ws.app.model.matchservice.exceptions.AlreadyRetiredException;
import es.udc.ws.app.model.matchservice.exceptions.IncorrectCreditCardException;
import es.udc.ws.app.model.matchservice.exceptions.MatchNotAvailableException;
import es.udc.ws.app.model.matchservice.exceptions.NotEnoughUnitsException;
import es.udc.ws.app.model.purchase.Purchase;
import es.udc.ws.app.model.purchase.SqlPurchaseDao;
import es.udc.ws.app.model.purchase.SqlPurchaseDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppServiceTest {
    private final long NON_EXISTENT_MATCH_ID = -1;
    private final long NON_EXISTENT_PURCHASE_ID = -1;
    private final String VALID_EMAIL = "ws-user@gmail.com";
    private final String INVALID_EMAIL = "ws-user#gmail.com";

    private final String VALID_CREDIT_CARD_NUMBER = "1234567890123456";
    private final String INCORRECT_CREDIT_CARD_NUMBER = "1234567890123457";
    private final String INVALID_CREDIT_CARD_NUMBER = "";
    private final int VALID_PURCHASE_UNITS = 15;

    private final int EXCESSIVE_PURCHASE_UNITS = 999;
    private final int INVALID_PURCHASE_UNITS = -1;

    private static MatchService matchService = null;

    private static SqlPurchaseDao purchaseDao = null;

    private static SqlMatchDao matchDao = null;

    @BeforeAll
    public static void init() {

        /*
         * Create a simple data source and add it to "DataSourceLocator" (this
         * is needed to test "es.udc.ws.movies.model.movieservice.MovieService"
         */
        DataSource dataSource = new SimpleDataSource();

        /* Add "dataSource" to "DataSourceLocator". */
        DataSourceLocator.addDataSource(MATCH_DATA_SOURCE, dataSource);

        matchService = MatchServiceFactory.getService();

        matchDao = SqlMatchDaoFactory.getDao();

        purchaseDao = SqlPurchaseDaoFactory.getDao();
    }

    private Match getValidMatch(String visitorName) {
        return new Match(visitorName, LocalDateTime.of (2024, Month.AUGUST, 3, 0, 0), 30, 50,5);
    }

    private Match getValidMatch() {
        return getValidMatch("Visitor Name");
    }
    private Purchase findPurchase(Purchase purchase) throws InstanceNotFoundException{

        DataSource dataSource = DataSourceLocator.getDataSource(MATCH_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                Purchase purchaseFound = purchaseDao.find(connection, purchase.getPurchaseId());

                /* Commit. */
                connection.commit();
                return purchaseFound;
            }
            catch (InstanceNotFoundException e){
                connection.commit();
                throw new InstanceNotFoundException(purchase.getMatchId(), Match.class.getName());
            }
            catch (SQLException e) {
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

    private Match createMatch(Match match) {

        Match addedMatch = null;
        try {
            addedMatch = matchService.addMatch(match);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
        return addedMatch;

    }

    private void removeMatch(Long matchId) throws InstanceNotFoundException{

        DataSource dataSource = DataSourceLocator.getDataSource(MATCH_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                matchDao.remove(connection, matchId);

                /* Commit. */
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new InstanceNotFoundException(matchId, Match.class.getName());
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

    private void updateMatch(Match match) throws InstanceNotFoundException{

        DataSource dataSource = DataSourceLocator.getDataSource(MATCH_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                matchDao.update(connection, match);

                /* Commit. */
                connection.commit();
            }
            catch (InstanceNotFoundException e){
                connection.commit();
                throw new InstanceNotFoundException(match.getMatchId(), Match.class.getName());
            }
            catch (SQLException e) {
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

    private void removePurchase(Long purchaseId) throws InstanceNotFoundException{
        DataSource dataSource = DataSourceLocator.getDataSource(MATCH_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {

                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                /* Do work. */
                purchaseDao.remove(connection, purchaseId);

                /* Commit. */
                connection.commit();

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

    @Test
    public void testAddMatchAndFindMatch() throws  InstanceNotFoundException {

        Match match = getValidMatch();
        Match addedMatch = null;

        try {

            // Create Match
            LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);

            addedMatch = createMatch(match);

            LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);

            // Find Match
            Match foundMatch = matchService.findMatch(addedMatch.getMatchId());

            //bad insert on BD
            assertNotEquals(1000, foundMatch.getMatchId());
            assertNotEquals(" ", match.getVisitorName());
            assertNotEquals(LocalDateTime.of(1901,
                    Month.JANUARY, 1, 0, 0, 0), match.getMatchDate());
            assertNotEquals(1, match.getMatchPrice());
            assertNotEquals(1, match.getMaxAvailable());
            assertNotEquals(1, match.getNumberOfSells());

            assertEquals(addedMatch, foundMatch);
            assertTrue((foundMatch.getCreationDate().compareTo(beforeCreationDate) >= 0)
                    && (foundMatch.getCreationDate().compareTo(afterCreationDate) <= 0));

        } finally {
            if(addedMatch != null){
                removeMatch(addedMatch.getMatchId());
            }
        }
    }

    @Test
    public void testPurchase() throws InstanceNotFoundException {
        Match match = createMatch(getValidMatch());
        Purchase purchase = null;
        try {
            purchase = matchService.purchaseMatch(VALID_EMAIL,match.getMatchId(),VALID_CREDIT_CARD_NUMBER,VALID_PURCHASE_UNITS);
            Purchase foundPurchase = findPurchase(purchase);
            Match foundMatch = matchService.findMatch(match.getMatchId());

            assertEquals(match.getNumberOfSells() + VALID_PURCHASE_UNITS, foundMatch.getNumberOfSells());
            assertEquals(purchase.getPurchaseId(),foundPurchase.getPurchaseId());
            assertEquals(VALID_EMAIL,foundPurchase.getUserEmail());
            assertEquals(VALID_PURCHASE_UNITS,foundPurchase.getUnits());
            assertEquals(VALID_CREDIT_CARD_NUMBER,foundPurchase.getCreditCardNumber());

            assertNotEquals(purchase.getPurchaseId()+1,foundPurchase.getPurchaseId());
            assertNotEquals(INVALID_EMAIL,foundPurchase.getUserEmail());
            assertNotEquals(INVALID_PURCHASE_UNITS,foundPurchase.getUnits());
            assertNotEquals(INVALID_CREDIT_CARD_NUMBER,foundPurchase.getCreditCardNumber());

            assertFalse(foundPurchase.isRetired());
            matchService.retirePurchase(purchase.getPurchaseId(),VALID_CREDIT_CARD_NUMBER);
            foundPurchase = findPurchase(purchase);
            assertTrue(foundPurchase.isRetired());



        } catch (InputValidationException | MatchNotAvailableException | NotEnoughUnitsException |
                 AlreadyRetiredException | IncorrectCreditCardException e) {
            throw new RuntimeException(e);
        } finally {
            if(purchase != null){
                removePurchase(purchase.getPurchaseId());
            }
            removeMatch(match.getMatchId());
        }
    }
    @Test
    public void testAddInvalidMatch() {

        // Check match visitor name not null
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setVisitorName(null);
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        // Check match visitor name not empty
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setVisitorName("");
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        // Check maxAvailable >= 0
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setMaxAvailable((short) -1);
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        // Check maxAvailable <= MAX_AVAILABLE
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setMaxAvailable((short) (MAX_AVAILABLE + 1));
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        // Check match price >= 0
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setMatchPrice((short) -1);
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        // Check match price <= MAX_PRICE
        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setMatchPrice((short) (MAX_PRICE + 1));
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

        assertThrows(InputValidationException.class, () -> {
            Match match = getValidMatch();
            match.setMatchDate(LocalDateTime.of (2020, Month.AUGUST, 3, 12, 30));
            Match addedMatch = matchService.addMatch(match);
            removeMatch(addedMatch.getMatchId());
        });

    }
    @Test
    public void testFindNonExistentMatch(){
        assertThrows(InstanceNotFoundException.class, () -> matchService.findMatch(NON_EXISTENT_MATCH_ID));
    }

    @Test
    public void testUpdateMatch() throws InstanceNotFoundException {
        Match match = createMatch(getValidMatch());
        try{
            Match matchToUpdate = new Match(match.getMatchId(), "Visitor 2", LocalDateTime.of (2025, Month.AUGUST, 3, 0, 0).withNano(0), 200, 999, 20);
            updateMatch(matchToUpdate);
            Match updatedMatch = matchService.findMatch(match.getMatchId());
            matchToUpdate.setCreationDate(match.getCreationDate());
            assertEquals(matchToUpdate, updatedMatch);
        }
        finally {
            removeMatch(match.getMatchId());
        }
    }

    @Test
    public void testUpdateInvalidMatch() throws InstanceNotFoundException {

        Long matchId = createMatch(getValidMatch()).getMatchId();
        try {
            // Check match visitor name not null
            Match match = matchService.findMatch(matchId);
            match.setVisitorName(null);
            assertThrows(RuntimeException.class, () -> updateMatch(match));
        } finally {
            // Clear Database
            removeMatch(matchId);
        }
    }
    @Test
    public void testUpdateNonExistentMatch() {

        Match match = getValidMatch();
        match.setMatchId(NON_EXISTENT_MATCH_ID);
        match.setCreationDate(LocalDateTime.now());

        assertThrows(InstanceNotFoundException.class, () -> updateMatch(match));

    }
    @Test
    public void testRemoveNonExistentMatch() {
        assertThrows(InstanceNotFoundException.class, () -> removeMatch(NON_EXISTENT_MATCH_ID));
    }

    @Test
    public void testFindMatchBetweenTwoDates(){
        Match match = createMatch(getValidMatch());
        List<Match> arr = new ArrayList<>();
        arr.add(match);
        LocalDate startDate = LocalDate.of (2024, Month.AUGUST, 2);
        LocalDate endDate = LocalDate.of (2024, Month.AUGUST, 12);
        LocalDate startDate2 = LocalDate.of (2023, Month.AUGUST, 3);
        LocalDate endDate2 = LocalDate.of (2023, Month.AUGUST, 12);
        assertEquals(arr.get(0).getMatchId(), matchService.findMatches(startDate,endDate).get(0).getMatchId());
        assertTrue(matchService.findMatches(startDate2,endDate2).isEmpty());
        arr.remove(match);
        try{
            removeMatch(match.getMatchId());
        }
        catch (InstanceNotFoundException e){
            throw new RuntimeException(e);
        }
    }

     @Test
    public void testFindMatchById() throws InstanceNotFoundException {
        Match match = createMatch(getValidMatch());
        try{
            assertEquals(match.getMatchId(), matchService.findMatch(match.getMatchId()).getMatchId());
        }finally {
            removeMatch(match.getMatchId());
        }
     }

     @Test
    public void testBuyPurchaseMatchTickets() throws MatchNotAvailableException, NotEnoughUnitsException, InstanceNotFoundException, InputValidationException {
        Match match = createMatch(getValidMatch());
        Purchase purchase = null;
        try {
            LocalDateTime beforePurchasing = LocalDateTime.now().withNano(0);
            purchase = matchService.purchaseMatch(VALID_EMAIL, match.getMatchId(), VALID_CREDIT_CARD_NUMBER, VALID_PURCHASE_UNITS);
            LocalDateTime afterPurchasing = LocalDateTime.now().withNano(0);
            assertEquals(match.getMatchId(), purchase.getMatchId());
            assertEquals(VALID_EMAIL, purchase.getUserEmail());
            assertEquals(match.getMatchId(), purchase.getMatchId());
            assertEquals(VALID_CREDIT_CARD_NUMBER, purchase.getCreditCardNumber());
            assertEquals(VALID_PURCHASE_UNITS, purchase.getUnits());
            assertTrue(purchase.getPurchaseDate().compareTo(beforePurchasing) >= 0 && purchase.getPurchaseDate().compareTo(afterPurchasing) <= 0);

        } finally {
            if(purchase != null){
                removePurchase(purchase.getPurchaseId());
            }
            removeMatch(match.getMatchId());
        }
     }
    @Test
    public void testBuyNotEnoughPurchaseMatchTickets() throws InstanceNotFoundException {
        Match match = createMatch(getValidMatch());
        Purchase purchase = null;
        try {
            assertThrows(NotEnoughUnitsException.class, () -> {
            matchService.purchaseMatch(VALID_EMAIL, match.getMatchId(), VALID_CREDIT_CARD_NUMBER, EXCESSIVE_PURCHASE_UNITS);
        });
        } finally {
            if(purchase != null){
                removePurchase(purchase.getPurchaseId());
            }
            removeMatch(match.getMatchId());
        }
    }

    @Test
    public void testBuyNotFoundPurchaseMatchTickets() throws InstanceNotFoundException {
        Match match = createMatch(getValidMatch());
        Purchase purchase = null;
        try{
            assertThrows(InstanceNotFoundException.class, () -> {
            matchService.purchaseMatch(VALID_EMAIL, NON_EXISTENT_MATCH_ID, VALID_CREDIT_CARD_NUMBER, VALID_PURCHASE_UNITS);
        });
        } finally {
            if(purchase != null){
                removePurchase(purchase.getPurchaseId());
            }
            removeMatch(match.getMatchId());
        }
    }
    @Test
    public void testInvalidInputPurchaseMatchTickets() throws InstanceNotFoundException {
        Match match = createMatch(getValidMatch());
        Purchase purchase = null;
        try {
            assertThrows(InputValidationException.class, () -> {
            matchService.purchaseMatch(INVALID_EMAIL, match.getMatchId(), VALID_CREDIT_CARD_NUMBER, VALID_PURCHASE_UNITS);});
            assertThrows(InputValidationException.class, () -> {
                matchService.purchaseMatch(VALID_EMAIL, match.getMatchId(), INVALID_CREDIT_CARD_NUMBER, VALID_PURCHASE_UNITS);
            });
            assertThrows(InputValidationException.class, () -> {
                matchService.purchaseMatch(VALID_EMAIL, match.getMatchId(), INVALID_CREDIT_CARD_NUMBER, INVALID_PURCHASE_UNITS);
            });
        } finally {
            if(purchase != null){
                removePurchase(purchase.getPurchaseId());
            }
            removeMatch(match.getMatchId());
        }
    }
     @Test
     public void testFindAllUserPurchases() throws MatchNotAvailableException, NotEnoughUnitsException, InstanceNotFoundException, InputValidationException {
        Match match = createMatch(getValidMatch());
        List<Purchase> purchases = new ArrayList<>();
        try{
            for(int i =0; i<5; i++){
                purchases.add(matchService.purchaseMatch(VALID_EMAIL, match.getMatchId(), VALID_CREDIT_CARD_NUMBER, 1));
            }
            List<Purchase> foundPurchases = matchService.findUserReservations(VALID_EMAIL);
            for(int i = 0; i<5; i++){
                assertEquals(purchases, foundPurchases);
            }
        }
        finally {
            purchases.forEach((purchase) ->{
                if(purchase != null){
                    try{
                        removePurchase(purchase.getPurchaseId());
                    }
                    catch(InstanceNotFoundException e){
                        throw new RuntimeException(e);
                    }
                }
            }
            );
            removeMatch(match.getMatchId());
        }
     }

    @Test
    public void testRetirePurchasesWithInvalidCard() throws MatchNotAvailableException, NotEnoughUnitsException, InstanceNotFoundException, InputValidationException {
        Match match = createMatch(getValidMatch());
        Purchase purchase = null;
        try{
            purchase = matchService.purchaseMatch(VALID_EMAIL, match.getMatchId(), VALID_CREDIT_CARD_NUMBER, VALID_PURCHASE_UNITS);
            Purchase finalPurchase = purchase;
            assertThrows(InputValidationException.class, () -> {
                matchService.retirePurchase(finalPurchase.getPurchaseId(), INVALID_CREDIT_CARD_NUMBER);
            });
        }
        finally {
            if(purchase != null){
                removePurchase(purchase.getPurchaseId());
            }
            removeMatch(match.getMatchId());
        }

    }
@Test
public void testRetirePurchasesWithIncorrectCard() throws MatchNotAvailableException, NotEnoughUnitsException, InstanceNotFoundException, InputValidationException {
    Match match = createMatch(getValidMatch());
    Purchase purchase = null;
    try{
        purchase = matchService.purchaseMatch(VALID_EMAIL, match.getMatchId(), VALID_CREDIT_CARD_NUMBER, VALID_PURCHASE_UNITS);
        Purchase finalPurchase = purchase;
        assertThrows(IncorrectCreditCardException.class, () -> {
            matchService.retirePurchase(finalPurchase.getPurchaseId(), INCORRECT_CREDIT_CARD_NUMBER);
        });
    }
    finally {
        if(purchase != null){
            removePurchase(purchase.getPurchaseId());
        }
        removeMatch(match.getMatchId());
    }

}

    @Test
    public void testAlreadyRetirePurchase() throws MatchNotAvailableException, NotEnoughUnitsException, InstanceNotFoundException, InputValidationException, AlreadyRetiredException, IncorrectCreditCardException {
        Match match = createMatch(getValidMatch());
        Purchase purchase = null;
        try{
            purchase = matchService.purchaseMatch(VALID_EMAIL, match.getMatchId(), VALID_CREDIT_CARD_NUMBER, VALID_PURCHASE_UNITS);
            Purchase finalPurchase = purchase;
            matchService.retirePurchase(purchase.getPurchaseId(), VALID_CREDIT_CARD_NUMBER);
            assertThrows(AlreadyRetiredException.class, () -> {
                matchService.retirePurchase(finalPurchase.getPurchaseId(), VALID_CREDIT_CARD_NUMBER);
            });
        }
        finally {
            if(purchase != null){
                removePurchase(purchase.getPurchaseId());
            }
            removeMatch(match.getMatchId());
        }

    }

}


