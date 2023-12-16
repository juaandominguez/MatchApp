package es.udc.ws.app.restservice.dto;

public class RestMatchDto {

    private Long matchId;
    private String visitorName;
    private String matchDate;
    private double matchPrice;
    private int maxAvailable;
    private int numberOfSells;

    public RestMatchDto() {
    }

    public RestMatchDto(Long matchId, String visitorName, String matchDate, double matchPrice, int maxAvailable, int numberOfSells) {
        this.matchId = matchId;
        this.visitorName = visitorName;
        this.matchDate = matchDate;
        this.matchPrice = matchPrice;
        this.maxAvailable = maxAvailable;
        this.numberOfSells = numberOfSells;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public double getMatchPrice() {
        return matchPrice;
    }

    public void setMatchPrice(double matchPrice) {
        this.matchPrice = matchPrice;
    }

    public int getMaxAvailable() {
        return maxAvailable;
    }

    public void setMaxAvailable(int maxAvailable) {
        this.maxAvailable = maxAvailable;
    }

    public int getNumberOfSells() {
        return numberOfSells;
    }

    public void setNumberOfSells(int numberOfSells) {
        this.numberOfSells = numberOfSells;
    }

    @Override
    public String toString() {
        return "MatchDto [matchId=" + matchId + ", visitorName=" + visitorName
                + ", matchDate=" + matchDate
                + ", matchPrice=" + matchPrice + ", maxAvailable=" + maxAvailable + ", numberOfSells=" + numberOfSells + "]";
    }
}
