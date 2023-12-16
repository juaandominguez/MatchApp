namespace java es.udc.ws.app.thrift



struct ThriftMatchDto {

    1: i64 matchId

    2: string visitorName

    3: string matchDate

    4: double matchPrice

    5: i16 maxAvailable

    6: i16 numberOfSells

}



struct ThriftPurchaseDto {

    1: i64 purchaseId

    2: i64 matchId

    3: string creditCardNumber

    4: string purchaseDate

    5: i16 units

    6: string userEmail

    7: bool retired

}



exception ThriftInputValidationException {

    1: string message

}



exception ThriftInstanceNotFoundException {

    1: string instanceId

    2: string instanceType

}



exception ThriftAlreadyRetiredException {

    1: i64 purchaseId

}



exception ThriftIncorrectCreditCardException {

    1: i64 purchaseId

    2: string creditCardNumber

}

exception ThriftMatchNotAvailableException {

    1: i64 matchId

}

exception ThriftNotEnoughUnitsException {

    1: i64 matchId

    2: i16 maxAvailable

    3: i16 numberOfSales

}

service ThriftMatchService {



   ThriftMatchDto addMatch(1: ThriftMatchDto matchDto) throws (1: ThriftInputValidationException e)



   list<ThriftMatchDto> findMatches(1: string date) throws (1: ThriftInputValidationException e)


   ThriftMatchDto findMatch(1: i64 matchId) throws (1: ThriftInstanceNotFoundException e)

   i64 purchaseMatch(1: string userEmail, 2: i64 matchId, 3: string creditCardNumber, 4: i16 units) throws(1: ThriftInstanceNotFoundException e, 2: ThriftInputValidationException ee, 3: ThriftMatchNotAvailableException eee, 4: ThriftNotEnoughUnitsException eeee)

   list<ThriftPurchaseDto> findUserReservations(1: string userEmail) throws (1: ThriftInputValidationException e, 2: ThriftInstanceNotFoundException ee)

   void retirePurchase(1: i64 purchaseId, 2: string creditCardNumber) throws (1: ThriftInstanceNotFoundException e, 2: ThriftInputValidationException ee, 3: ThriftAlreadyRetiredException eee, 4: ThriftIncorrectCreditCardException eeee)

}
