package es.udc.ws.app.model.purchase;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlPurchaseDao implements SqlPurchaseDao{

    protected AbstractSqlPurchaseDao(){
    }

    @Override
    public Purchase find(Connection connection, Long purchaseId) throws InstanceNotFoundException {
        /*create queryString*/
        String queryString = "SELECT matchId, creditCardNumber, " +
                "purchaseDate, units, userEmail, retired " +
                "FROM Purchase WHERE purchaseId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            int i = 1;
            preparedStatement.setLong(i++, purchaseId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()){
                throw new InstanceNotFoundException(purchaseId,Purchase.class.getName());
            }

            i = 1;
            int matchId = resultSet.getInt(i++);
            String creditCardNumber = resultSet.getString(i++);
            Timestamp purchaseDateAsTimeStamp = resultSet.getTimestamp(i++);
            LocalDateTime purchaseDate = purchaseDateAsTimeStamp != null ?
                    purchaseDateAsTimeStamp.toLocalDateTime() : null;
            int units = resultSet.getInt(i++);
            String userEmail = resultSet.getString(i++);
            boolean retired = resultSet.getBoolean(i++);

            return new Purchase(purchaseId,Long.valueOf(matchId), creditCardNumber,
                    purchaseDate,units,userEmail,retired);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Connection connection, Purchase purchase)
            throws InstanceNotFoundException{

        /* Create "queryString". */
        String queryString = "UPDATE Purchase"
                + " SET matchId = ?, creditCardNumber = ?, "
                + " purchaseDate = ?, units = ?, userEmail = ?, " +
                " retired = ? WHERE purchaseId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, purchase.getMatchId());
            preparedStatement.setString(i++, purchase.getCreditCardNumber());
            Timestamp date = purchase.getPurchaseDate() != null ? Timestamp.valueOf(purchase.getPurchaseDate()) : null;
            preparedStatement.setTimestamp(i++, date);
            preparedStatement.setInt(i++, purchase.getUnits());
            preparedStatement.setString(i++, purchase.getUserEmail());
            preparedStatement.setBoolean(i++, purchase.isRetired());
            preparedStatement.setLong(i++, purchase.getPurchaseId());

            /* Execute query. */
            int updatedRows = preparedStatement.executeUpdate();
            if (updatedRows == 0) {
                throw new InstanceNotFoundException(purchase.getPurchaseId(),Purchase.class.getName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void remove(Connection connection, Long purchaseId)
            throws InstanceNotFoundException{
        /* Create "queryString". */
        String queryString = "DELETE FROM Purchase WHERE" + " purchaseId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, purchaseId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(purchaseId,Purchase.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<Purchase> findByUser(Connection connection, String userEmail) throws InstanceNotFoundException {
        String queryString = "SELECT purchaseId, matchId, creditCardNumber, " +
                "purchaseDate, units, retired " +
                "FROM Purchase WHERE userEmail = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {

            preparedStatement.setString(1, userEmail);
            /* Execute query. */
            ResultSet resultSet = preparedStatement.executeQuery();

            /* Read movies. */
            List<Purchase> purchase = new ArrayList<Purchase>();

            while (resultSet.next()) {

                int i = 1;
                Long purchaseId = Long.valueOf(resultSet.getInt(i++));
                Long matchId = Long.valueOf(resultSet.getInt(i++));
                String creditCardNumber = resultSet.getString(i++);
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime purchaseDate = creationDateAsTimestamp.toLocalDateTime();
                int units = resultSet.getInt(i++);
                boolean retired = resultSet.getBoolean(i++);


                purchase.add(new Purchase(purchaseId, matchId, creditCardNumber, purchaseDate,
                        units,userEmail,retired));

            }

            /* Return movies. */
            return purchase;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
