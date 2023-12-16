package es.udc.ws.app.model.match;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

public abstract class AbstractSqlMatchDao implements SqlMatchDao{
    protected AbstractSqlMatchDao(){

    }
    @Override
    public Match find(Connection connection, Long matchId) throws InstanceNotFoundException {
        String queryString = "SELECT visitorName, matchDate, " +
                "matchPrice, maxAvailable, numberOfSells, creationDate " +
                "FROM `Match` WHERE matchId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setLong(i++, matchId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new InstanceNotFoundException(matchId, Match.class.getName());
            }

            i = 1;
            String visitorName = resultSet.getString(i++);
            Timestamp matchDateAsTimeStamp = resultSet.getTimestamp(i++);
            LocalDateTime matchDate = matchDateAsTimeStamp != null ? matchDateAsTimeStamp.toLocalDateTime().withNano(0) : null;
            double matchPrice = resultSet.getDouble(i++);
            int maxAvailable = resultSet.getInt(i++);
            int numberOfSells = resultSet.getInt(i++);
            Timestamp creationDateAsTimeStamp = resultSet.getTimestamp(i++);
            LocalDateTime creationDate = creationDateAsTimeStamp != null ? creationDateAsTimeStamp.toLocalDateTime() : null;

            return new Match(matchId, visitorName, matchDate, matchPrice, maxAvailable, numberOfSells, creationDate);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Connection connection, Match match) throws InstanceNotFoundException {
        String queryString = "UPDATE `Match`"
                + "SET visitorName = ?, matchDate = ?, matchPrice = ?, "
                + "maxAvailable = ?, numberOfSells = ? WHERE matchId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            int i = 1;
            preparedStatement.setString(i++, match.getVisitorName());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(match.getMatchDate()));
            preparedStatement.setDouble(i++, match.getMatchPrice());
            preparedStatement.setInt(i++, match.getMaxAvailable());
            preparedStatement.setInt(i++, match.getNumberOfSells());
            preparedStatement.setLong(i++, match.getMatchId());

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows == 0) {
                throw new InstanceNotFoundException(match.getMatchId(),
                        Match.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Match> findMatches(Connection connection, LocalDate initialDate, LocalDate endDate) {
        String queryString = "SELECT matchId, visitorName, matchDate, " +
                "matchPrice, maxAvailable, numberOfSells, creationDate " +
                "FROM `Match` WHERE matchDate >= ? and matchDate <= ? ";

        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            if(initialDate != null && endDate != null){
                int i = 1;
                preparedStatement.setTimestamp(i++, Timestamp.valueOf((initialDate.atStartOfDay())));
                preparedStatement.setTimestamp(i++, Timestamp.valueOf(endDate.atStartOfDay()));
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Match> matches = new ArrayList<Match>();

            while(resultSet.next()){
                int i = 1;
                Long matchId = Long.valueOf(resultSet.getLong(i++));
                String visitorName = resultSet.getString(i++);
                Timestamp matchDateAsTimeStamp = resultSet.getTimestamp(i++);
                LocalDateTime matchDate = matchDateAsTimeStamp != null ? matchDateAsTimeStamp.toLocalDateTime() : null;
                double matchPrice = resultSet.getDouble(i++);
                int maxAvailable = resultSet.getInt(i++);
                int numberOfSells = resultSet.getInt(i++);
                Timestamp creationDateAsTimeStamp = resultSet.getTimestamp(i++);
                LocalDateTime creationDate = creationDateAsTimeStamp != null ? creationDateAsTimeStamp.toLocalDateTime() : null;


                matches.add(new Match(matchId, visitorName, matchDate.withNano(0), matchPrice, maxAvailable, numberOfSells, creationDate.withNano(0)));

            }
            return matches;
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long matchId) throws InstanceNotFoundException {
        String queryString = "DELETE FROM `Match` WHERE" + " matchId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setLong(i++, matchId);

            /* Execute query. */
            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0) {
                throw new InstanceNotFoundException(matchId, Match.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
