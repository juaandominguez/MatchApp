package es.udc.ws.app.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.app.client.service.ClientMatchService;
import es.udc.ws.app.client.service.dto.ClientMatchDto;
import es.udc.ws.app.client.service.dto.ClientPurchaseDto;
import es.udc.ws.app.client.service.exceptions.IncorrectCreditCardException;
import es.udc.ws.app.client.service.exceptions.AlreadyRetiredException;
import es.udc.ws.app.client.service.exceptions.MatchNotAvailableException;
import es.udc.ws.app.client.service.exceptions.NotEnoughUnitsException;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientMatchDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientPurchaseDtoConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.List;

public class RestClientMatchService implements ClientMatchService {
    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientMatchService.endpointAddress";
    private String endpointAddress;

    @Override
    public Long addMatch(ClientMatchDto match) throws InputValidationException {

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "matches").
                    bodyStream(toInputStream(match), ContentType.create("application/json")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientMatchDtoConversor.toClientMatchDto(response.getEntity().getContent()).getMatchId();

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public List<ClientMatchDto> findMatches(String date) throws InputValidationException{

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "matches?date="
                            + URLEncoder.encode(date, "UTF-8")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientMatchDtoConversor.toClientMatchDtos(response.getEntity()
                    .getContent());

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ClientMatchDto findMatch(Long matchId) throws InstanceNotFoundException {

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() + "matches/" + matchId).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientMatchDtoConversor.toClientMatchDto(
                    response.getEntity().getContent());

        } catch (InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Long purchaseMatch(String userEmail, Long matchId, String creditCardNumber, int units)
            throws InstanceNotFoundException, InputValidationException, NotEnoughUnitsException, MatchNotAvailableException {

        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() + "purchases").
                    bodyForm(
                            Form.form().
                                    add("userEmail", userEmail).
                                    add("matchId", Long.toString(matchId)).
                                    add("creditCardNumber", creditCardNumber).
                                    add("units", Integer.toString(units)).
                                    build()).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientPurchaseDtoConversor.toClientPurchaseDto(
                    response.getEntity().getContent()).getPurchaseId();

        } catch (InputValidationException | InstanceNotFoundException | NotEnoughUnitsException | MatchNotAvailableException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<ClientPurchaseDto> findUserReservations(String userEmail) throws InstanceNotFoundException, InputValidationException {
        try {
            ClassicHttpResponse response = (ClassicHttpResponse) Request.get(getEndpointAddress() +
                            "purchases?userEmail=" + userEmail).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientPurchaseDtoConversor.toClientPurchaseDtos(response.getEntity().getContent());

        } catch (InstanceNotFoundException | InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void retirePurchase(Long purchaseId, String creditCardNumber) throws InstanceNotFoundException, InputValidationException, AlreadyRetiredException, IncorrectCreditCardException {
        try {

            ClassicHttpResponse response = (ClassicHttpResponse) Request.post(getEndpointAddress() +
                    "purchases/" + purchaseId).bodyForm(Form.form().add("creditCardNumber", creditCardNumber).build())
                    .execute().returnResponse();
                    validateStatusCode(HttpStatus.SC_NO_CONTENT, response);

        } catch (InputValidationException | InstanceNotFoundException | AlreadyRetiredException | IncorrectCreditCardException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager
                    .getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }

    private InputStream toInputStream(ClientMatchDto match) {

        try {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientMatchDtoConversor.toObjectNode(match));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void validateStatusCode(int successCode, ClassicHttpResponse response) throws Exception {

        try {

            int statusCode = response.getCode();

            /* Success? */
            if (statusCode == successCode) {
                return;
            }

            /* Handler error. */
            switch (statusCode) {
                case HttpStatus.SC_NOT_FOUND -> throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_BAD_REQUEST -> throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_FORBIDDEN -> throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_GONE -> throw JsonToClientExceptionConversor.fromGoneErrorCode(
                        response.getEntity().getContent());
                default -> throw new RuntimeException("HTTP error; status code = "
                        + statusCode);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
