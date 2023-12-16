package es.udc.ws.app.client.service.exceptions;

import java.time.LocalDateTime;

public class NotEnoughUnitsException extends Exception{
    private Long matchId;
    private int maxAvailable;

    public NotEnoughUnitsException(Long matchId, int maxAvailable, int numberOfSales) {
        super("Match with id = \"" + matchId
                + "\" has not enough units"
                + (" (AvailableEntries  = " + (maxAvailable - numberOfSales))
                + ", MaxEntries = " + maxAvailable + ")");

        this.matchId = matchId;
        this.maxAvailable = maxAvailable;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMarchId(Long saleId) {
        this.matchId = saleId;
    }

    public int getMaxAvailable() {
        return maxAvailable;
    }

    public void setMaxAvailable(int maxAvailable) {
        this.maxAvailable = maxAvailable;
    }
}
