package es.udc.ws.app.model.matchservice.exceptions;

import java.time.LocalDateTime;

public class NotEnoughUnitsException extends Exception{

    private Long matchId;
    private int maxAvailable;
    private int numberOfSales;
    public NotEnoughUnitsException(Long matchId, int maxAvailable, int numberOfSales){
        super("Match with id=\"" + matchId +
                "\" only has \"" +
                (maxAvailable-numberOfSales) + "\" units available");
        this.matchId = matchId;
        this.maxAvailable = maxAvailable;
        this.numberOfSales=numberOfSales;
    }

    public Long getMatchId() {return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public int getMaxAvailable() {
        return maxAvailable;
    }

    public void setMaxAvailable(int maxAvailable) {
        this.maxAvailable = maxAvailable;
    }

    public int getNumberOfSales() {
        return numberOfSales;
    }

    public void setNumberOfSales(int numberOfSales) {
        this.numberOfSales = numberOfSales;
    }
}
