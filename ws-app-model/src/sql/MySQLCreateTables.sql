DROP TABLE Purchase;
DROP TABLE `Match`;
-- -----------------------------------------------------
-- Table `Match`
-- -----------------------------------------------------
CREATE TABLE `Match` (
  matchId INT NOT NULL AUTO_INCREMENT,
  visitorName VARCHAR(45) COLLATE latin1_bin NOT NULL,
  matchDate DATETIME NOT NULL,
  matchPrice DOUBLE NOT NULL,
  maxAvailable INT NOT NULL,
  numberOfSells INT NOT NULL,
  creationDate DATETIME NOT NULL,
  CONSTRAINT MatchPK PRIMARY KEY (matchId),
  CONSTRAINT validPrice CHECK (matchPrice >= 0 and matchPrice <= 1000),
  CONSTRAINT validAvailable CHECK (maxAvailable >= 0 and maxAvailable <= 1000)
)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `Purchase`
-- -----------------------------------------------------
CREATE TABLE Purchase (
  purchaseId INT NOT NULL AUTO_INCREMENT,
  matchId INT NOT NULL,
  creditCardNumber VARCHAR(16) NOT NULL,
  purchaseDate DATETIME NOT NULL,
  units INT NOT NULL,
  userEmail VARCHAR(45) COLLATE latin1_bin NOT NULL,
  retired TINYINT DEFAULT 0,
  CONSTRAINT PurchasePK PRIMARY KEY (purchaseId),
  CONSTRAINT PurchaseMatchIdFK FOREIGN KEY(matchId) REFERENCES `Match` (matchId) ON DELETE CASCADE )
ENGINE = InnoDB;
