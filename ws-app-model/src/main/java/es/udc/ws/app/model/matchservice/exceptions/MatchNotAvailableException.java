package es.udc.ws.app.model.matchservice.exceptions;

import static java.time.LocalDateTime.now;

public class MatchNotAvailableException extends Exception{

    private Long matchId;
    public MatchNotAvailableException(Long matchId) {
        super("Match with id=\"" + matchId + "\n cannot be purchased because it already has been played");
        this.matchId=matchId;
    }
    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
}
