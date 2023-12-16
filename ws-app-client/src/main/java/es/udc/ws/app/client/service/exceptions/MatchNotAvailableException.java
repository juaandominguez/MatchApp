package es.udc.ws.app.client.service.exceptions;

public class MatchNotAvailableException extends Exception{
    private Long matchId;

    public MatchNotAvailableException(Long matchId){
        super("Match with id = \"" + matchId
                + "\" is not available");
        this.matchId = matchId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
}
