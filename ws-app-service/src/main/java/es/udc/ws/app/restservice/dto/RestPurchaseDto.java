package es.udc.ws.app.restservice.dto;

public class RestPurchaseDto {

    private Long purchaseId;
    private Long matchId;
    private String creditCardNumber;
    private String purchaseDate;
    private int units;
    private String userEmail;
    private boolean retired;

    public RestPurchaseDto() {
    }

    public RestPurchaseDto(Long purchaseId, Long matchId, String creditCardNumber, String purchaseDate, int units, String userEmail, boolean retired) {
        this.purchaseId = purchaseId;
        this.matchId = matchId;
        this.creditCardNumber = creditCardNumber;
        this.purchaseDate = purchaseDate;
        this.units = units;
        this.userEmail = userEmail;
        this.retired = retired;
    }


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
        return creditCardNumber.substring(creditCardNumber.length()-4);
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
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
}
