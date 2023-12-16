package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.ClientMatchService;
import es.udc.ws.app.client.service.ClientMatchServiceFactory;
import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientPurchaseDto;
import es.udc.ws.app.client.service.exceptions.IncorrectCreditCardException;
import es.udc.ws.app.client.service.exceptions.MatchNotAvailableException;
import es.udc.ws.app.client.service.exceptions.NotEnoughUnitsException;
import es.udc.ws.app.client.service.exceptions.AlreadyRetiredException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MatchServiceClient {
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsageAndExit();
        }
        ClientMatchService clientMatchService =
                ClientMatchServiceFactory.getService();
        if ("-addMatch".equalsIgnoreCase(args[0])) {
            validateArgs(args, 5, new int[]{4});

            try {
                Long matchId = clientMatchService.addMatch(new ClientMatchDto(null,
                        args[1], args[2], Double.parseDouble(args[3]),
                        Integer.parseInt(args[4]),Integer.parseInt(args[4])));

                System.out.println("Match " + matchId + " created successfully");

            } catch (NumberFormatException | InputValidationException ex) {
                ex.printStackTrace(System.err);
            }
        }

        else if("-findMatches".equalsIgnoreCase(args[0])){
            validateArgs(args, 2, new int[]{});
            try{
                List<ClientMatchDto> matches = clientMatchService.findMatches(args[1]);
                for (int i = 0; i < matches.size(); i++) {
                    ClientMatchDto matchDto = matches.get(i);
                    System.out.println("Id: " + matchDto.getMatchId() +
                            ", Visitor Name: " + matchDto.getVisitorName() +
                            ", Match Date: " + matchDto.getMatchDate() +
                            ", Match Price: " + matchDto.getMatchPrice() +
                            ", Max Tickets: " + matchDto.getMaxAvailable() +
                            ", Available Tickets: " + matchDto.getAvailableEntries());
                }
            }
            catch(InputValidationException e){
                e.printStackTrace(System.err);
            }
        }


        else if("-findMatch".equalsIgnoreCase(args[0])){
            validateArgs(args, 2, new int[]{});
            try{
                ClientMatchDto matchDto = clientMatchService.findMatch(Long.parseLong(args[1]));

                System.out.println("Id: " + matchDto.getMatchId() +
                        ", Visitor Name: " + matchDto.getVisitorName() +
                        ", Match Date: " +matchDto.getMatchDate() +
                        ", Match Price: " + matchDto.getMatchPrice() +
                        ", Max Tickets: " + matchDto.getMaxAvailable() +
                        ", Available Tickets: " + matchDto.getAvailableEntries());
            }
            catch (InstanceNotFoundException | NumberFormatException e){
                e.printStackTrace(System.err);
            }
        }

        else if("-buy".equalsIgnoreCase(args[0])){
            validateArgs(args, 5, new int[]{1, 3});
            try{
                Long purchaseId = clientMatchService.purchaseMatch(args[2], Long.parseLong(args[1]), args[4], Integer.parseInt(args[3]));
                System.out.println("Purchase " + purchaseId + " created successfully");
            }
            catch (InputValidationException | InstanceNotFoundException | MatchNotAvailableException | NotEnoughUnitsException e){
                e.printStackTrace(System.err);
            }

        }

        else if ("-findPurchases".equalsIgnoreCase(args[0])){
            validateArgs(args, 2, new int[]{});
            try{
                 List<ClientPurchaseDto> reservations = clientMatchService.findUserReservations(args[1]);
                for (ClientPurchaseDto reservation : reservations) {
                    System.out.println("Id: " + reservation.getPurchaseId() +
                            ", Match Id: " + reservation.getMatchId() +
                            ", Number of Tickets: " +reservation.getUnits() +
                            ", User Email: " + reservation.getUserEmail() +
                            ", Last 4 digits of credit card: " + reservation.getCreditCardNumber() +
                            ", Purchase Date: " + reservation.getPurchaseDate() +
                            ", Retired: " + (reservation.isRetired() ? "Yes" : "No"));
                }
            } catch (InstanceNotFoundException | InputValidationException e) {
                e.printStackTrace(System.err);
            }
        }
        else if ("-collect".equalsIgnoreCase(args[0])){
            validateArgs(args, 3, new int[]{1});
            try{
                    clientMatchService.retirePurchase(Long.parseLong(args[1]),args[2]);
                    System.out.println("Purchase: " + Long.parseLong(args[1]) + " retired\n");
            } catch (InstanceNotFoundException | InputValidationException | AlreadyRetiredException | IncorrectCreditCardException e) {
                e.printStackTrace(System.err);
            }
        }
    }



    public static void validateArgs(String[] args, int expectedArgs,
                                    int[] numericArguments) {
        if (expectedArgs != args.length) {
            printUsageAndExit();
        }
        for (int i = 0; i < numericArguments.length; i++) {
            int position = numericArguments[i];
            try {
                Double.parseDouble(args[position]);
            } catch (NumberFormatException n) {
                printUsageAndExit();
            }
        }
    }

    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage() {
        System.err.println("Usage:\n" +
                "    [add]    MatchServiceClient -addMatch <visitor> <matchDate> <price> <maxTickets>\n" +
                "    [buy]    MatchServiceClient -buy <matchId> <userEmail> <numTickets> <cardNumber>\n" +
                "    [findMatches]    MatchServiceClient -findMatches <untilDay>\n" +
                "    [findMatch]    MatchServiceClient -findMatch <matchId>\n" +
                "    [retirePurchase] MatchServiceClient -collect <purchaseId> <cardNumber>\n" +
                "    [findPurchases] MatchServiceClient -findPurchases <userEmail>\n");
    }
}
