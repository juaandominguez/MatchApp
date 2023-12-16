package es.udc.ws.app.model.match;

import java.sql.*;

public class Jdbc3CcSqlMatchDao extends AbstractSqlMatchDao{
    @Override
    public Match create(Connection connection, Match match) {

        /* Create "queryString". */
        String queryString = "INSERT INTO `Match`"
                + " (visitorName, matchDate, matchPrice, maxAvailable, numberOfSells, creationDate)"
                + " VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {

            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setString(i++, match.getVisitorName());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(match.getMatchDate()));
            preparedStatement.setDouble(i++, match.getMatchPrice());
            preparedStatement.setInt(i++, match.getMaxAvailable());
            preparedStatement.setInt(i++, match.getNumberOfSells());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(match.getCreationDate()));

            /* Execute query. */
            preparedStatement.executeUpdate();

            /* Get generated identifier. */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()) {
                throw new SQLException(
                        "JDBC driver did not return generated key.");
            }
            Long matchId = resultSet.getLong(1);

            /* Return movie. */
            return new Match(matchId, match.getVisitorName(),match.getMatchDate(),match.getMatchPrice(),match.getMaxAvailable(),match.getNumberOfSells(),match.getCreationDate());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
