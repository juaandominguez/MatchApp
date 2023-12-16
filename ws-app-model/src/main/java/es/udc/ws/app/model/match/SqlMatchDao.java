package es.udc.ws.app.model.match;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.util.List;
import java.time.LocalDate;
public interface SqlMatchDao
{
    public Match create(Connection connection, Match match);
    public void update(Connection connection, Match match) throws InstanceNotFoundException;
    public Match find(Connection connection, Long matchId) throws InstanceNotFoundException;
    public List<Match> findMatches(Connection connection, LocalDate initialDate, LocalDate endDate);
    //necesario para tests
    public void remove(Connection connection,Long matchId) throws InstanceNotFoundException;
}
