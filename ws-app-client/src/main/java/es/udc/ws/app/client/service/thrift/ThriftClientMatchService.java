package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.ClientMatchService;
import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientPurchaseDto;
import es.udc.ws.app.client.service.exceptions.AlreadyRetiredException;
import es.udc.ws.app.client.service.exceptions.IncorrectCreditCardException;
import es.udc.ws.app.client.service.exceptions.MatchNotAvailableException;
import es.udc.ws.app.client.service.exceptions.NotEnoughUnitsException;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.time.LocalDate;
import java.util.List;

public class ThriftClientMatchService implements ClientMatchService {

    private final static String ENDPOINT_ADDRESS_PARAMETER =
            "ThriftClientMatchService.endpointAddress";

    private final static String endpointAddress =
            ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);
    @Override
    public Long addMatch(ClientMatchDto match) throws InputValidationException {

        ThriftMatchService.Client client = getClient();


        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return client.addMatch(ClientMatchDtoToThriftMatchDtoConversor.toThriftMatchDto(match)).getMatchId();

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<ClientMatchDto> findMatches(String date) throws InputValidationException{

        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return ClientMatchDtoToThriftMatchDtoConversor.toClientMatchDtos(client.findMatches(date));

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ClientMatchDto findMatch(Long matchId) throws InstanceNotFoundException {
        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return ClientMatchDtoToThriftMatchDtoConversor.toClientMatchDto(client.findMatch(matchId));
        }catch (ThriftInstanceNotFoundException e){
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long purchaseMatch(String userEmail, Long matchId, String creditCardNumber, int units) throws InstanceNotFoundException, InputValidationException, MatchNotAvailableException, NotEnoughUnitsException {
        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return client.purchaseMatch(userEmail, matchId, creditCardNumber, (short) units);

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftMatchNotAvailableException e){
            throw new MatchNotAvailableException(e.getMatchId());
        } catch (ThriftNotEnoughUnitsException e){
            throw new NotEnoughUnitsException(e.getMatchId(), e.getMaxAvailable(), e.getNumberOfSales());
        }

        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientPurchaseDto> findUserReservations(String userEmail)
            throws InstanceNotFoundException, InputValidationException {
        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            return ClientPurchaseDtoToThriftPurchaseDtoConversor.toClientPurchaseDtos(client.findUserReservations(userEmail));

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void retirePurchase(Long purchaseId, String creditCardNumber)
            throws InstanceNotFoundException, InputValidationException, AlreadyRetiredException, IncorrectCreditCardException {
        ThriftMatchService.Client client = getClient();

        try (TTransport transport = client.getInputProtocol().getTransport()) {

            transport.open();

            client.retirePurchase(purchaseId,creditCardNumber);

        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftAlreadyRetiredException e) {
            throw new AlreadyRetiredException(purchaseId);
        } catch ( ThriftIncorrectCreditCardException e){
            throw new IncorrectCreditCardException(purchaseId,creditCardNumber);
        } catch ( Exception e){
            throw new RuntimeException(e);
        }
    }

    private ThriftMatchService.Client getClient() {

        try {

            TTransport transport = new THttpClient(endpointAddress);
            TProtocol protocol = new TBinaryProtocol(transport);

            return new ThriftMatchService.Client(protocol);

        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }

    }
}
