package es.udc.ws.app.model.purchase;
import java.sql.*;

public class Jdbc3CcSqlPurchaseDao extends AbstractSqlPurchaseDao{

    @Override
    public Purchase create(Connection connection, Purchase purchase) {
        String queryString =  "INSERT INTO `Purchase`"+
                " (matchId, creditCardNumber,purchaseDate," +
                " units, userEmail, retired) VALUES (?, ?, ?, ?, ?,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {
            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, purchase.getMatchId());
            preparedStatement.setString(i++, purchase.getCreditCardNumber());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(purchase.getPurchaseDate()));
            preparedStatement.setInt(i++, purchase.getUnits());
            preparedStatement.setString(i++, purchase.getUserEmail());
            preparedStatement.setBoolean(i++, purchase.isRetired());

            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long purchaseId = resultSet.getLong(1);
            connection.commit();
            /* Return purchase. */
            return new Purchase(purchaseId, purchase.getMatchId(), purchase.getCreditCardNumber(),
                    purchase.getPurchaseDate().withNano(0), purchase.getUnits(),
                    purchase.getUserEmail(), purchase.isRetired());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

