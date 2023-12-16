package es.udc.ws.app.model.match;
import java.time.LocalDateTime;
import java.util.Objects;

public class Match {
    private Long matchId;
    private String visitorName;
    private LocalDateTime matchDate;
    private double matchPrice;
    private int maxAvailable;
    private int numberOfSells;
    private LocalDateTime creationDate;

    public Match(String visitorName, LocalDateTime matchDate, double matchPrice, int maxAvailable, int numberOfSells) {
        this.visitorName = visitorName;
        this.matchDate = matchDate;
        this.matchPrice = matchPrice;
        this.maxAvailable = maxAvailable;
        this.numberOfSells = numberOfSells;
    }
    public Match(Long matchId,String visitorName, LocalDateTime matchDate, double matchPrice, int maxAvailable, int numberOfSells) {
        this(visitorName, matchDate, matchPrice, maxAvailable, numberOfSells);
        this.matchId=matchId;
        }
    public Match(Long matchId,String visitorName, LocalDateTime matchDate, double matchPrice, int maxAvailable, int numberOfSells, LocalDateTime creationDate) {
        this(matchId, visitorName, matchDate, matchPrice, maxAvailable, numberOfSells);
        this.creationDate = ( creationDate != null) ? creationDate.withNano(0) : null;
    }



    public Long getMatchId() {
        return matchId;
    }

    public String getVisitorName() {
        return visitorName;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public double getMatchPrice() {
        return matchPrice;
    }

    public int getMaxAvailable() {
        return maxAvailable;
    }

    public int getNumberOfSells() {
        return numberOfSells;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public void setMatchPrice(double matchPrice) {
        this.matchPrice = matchPrice;
    }

    public void setMaxAvailable(int maxAvailable) {
        this.maxAvailable = maxAvailable;
    }

    public void setNumberOfSells(int numberOfSells) {
        this.numberOfSells = numberOfSells;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Match match = (Match) o;
        return Double.compare(matchPrice, match.matchPrice) == 0 && maxAvailable == match.maxAvailable && numberOfSells == match.numberOfSells && Objects.equals(matchId, match.matchId) && Objects.equals(visitorName, match.visitorName) && Objects.equals(matchDate, match.matchDate) && Objects.equals(creationDate, match.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, visitorName, matchDate, matchPrice, maxAvailable, numberOfSells, creationDate);
    }
}
