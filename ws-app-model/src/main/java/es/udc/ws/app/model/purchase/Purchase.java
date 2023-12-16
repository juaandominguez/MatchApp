package es.udc.ws.app.model.purchase;

import es.udc.ws.app.model.match.Match;

import java.time.LocalDateTime;
import java.util.Objects;

public class Purchase {

    private Long purchaseId;
    private Long matchId;
    private String creditCardNumber;
    private LocalDateTime purchaseDate;
    private int units;
    private String userEmail;
    private boolean retired;

    public Purchase(Long matchId, String creditCardNumber,
                    LocalDateTime purchaseDate, int units, String userEmail,
                    boolean retired) {
        this.matchId = matchId;
        this.creditCardNumber = creditCardNumber;
        this.purchaseDate = (purchaseDate != null) ? purchaseDate.withNano(0) : null;
        this.units = units;
        this.userEmail = userEmail;
        this.retired = retired;
    }
    public Purchase(Long purchaseId, Long matchId, String creditCardNumber,
                    LocalDateTime purchaseDate, int units, String userEmail,
                    boolean retired) {
        this(matchId, creditCardNumber, purchaseDate, units, userEmail, retired);
        this.purchaseId = purchaseId;
    }

    ///// getters and setter /////
    public Long getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(Long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }


    public boolean isRetired() {
        return retired;
    }

    public void setRetired(boolean retired) {
        this.retired = retired;
    }

    //////Rewrite equals purchase, using all parameters//////
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Purchase purchase = (Purchase) o;
        return getPurchaseId() == purchase.getPurchaseId() && getMatchId() == purchase.getMatchId() && getUnits() == purchase.getUnits() && isRetired() == purchase.isRetired() && Objects.equals(getCreditCardNumber(), purchase.getCreditCardNumber()) && Objects.equals(getPurchaseDate(), purchase.getPurchaseDate()) && Objects.equals(getUserEmail(), purchase.getUserEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPurchaseId(), getMatchId(), getCreditCardNumber(), getPurchaseDate(), getUnits(), getUserEmail(), isRetired());
    }


}