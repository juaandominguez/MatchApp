package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.model.matchservice.MatchServiceFactory;
import es.udc.ws.app.restservice.dto.MatchToRestMatchDtoConversor;
import es.udc.ws.app.restservice.dto.RestMatchDto;
import es.udc.ws.app.restservice.json.JsonToRestMatchDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ExceptionToJsonConversor;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MatchServlet extends RestHttpServletTemplate {

    @Override
    protected void processPost(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException {
        ServletUtils.checkEmptyPath(req);

        RestMatchDto matchDto = JsonToRestMatchDtoConversor.toRestMatchDto(req.getInputStream());
        Match match = MatchToRestMatchDtoConversor.toMatch(matchDto);

        match = MatchServiceFactory.getService().addMatch(match);

        matchDto = MatchToRestMatchDtoConversor.toRestMatchDto(match);
        String matchURL = ServletUtils.normalizePath(req.getRequestURL().toString()) + "/" + match.getMatchId();
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", matchURL);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_CREATED,
                JsonToRestMatchDtoConversor.toObjectNode(matchDto), headers);
    }

    @Override
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException, InstanceNotFoundException {
        if (req.getPathInfo() == null || req.getPathInfo().equals("/")) {
            String specifiedDate = ServletUtils.getMandatoryParameter(req, "date");
            try {
                List<Match> matches = MatchServiceFactory.getService().findMatches(LocalDate.now(), LocalDate.parse(specifiedDate));

                List<RestMatchDto> matchDtos = MatchToRestMatchDtoConversor.toRestMatchDtos(matches);
                ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                        JsonToRestMatchDtoConversor.toArrayNode(matchDtos), null);
            }catch(DateTimeParseException e){
                ServletUtils.writeServiceResponse(resp, 400, ExceptionToJsonConversor.toInputValidationException(new InputValidationException("Invalid date: "+ specifiedDate)), (Map)null);
            }
        } else {
            Long matchId = ServletUtils.getIdFromPath(req, "match");

            Match foundMatch = MatchServiceFactory.getService().findMatch(matchId);
            RestMatchDto matchDto = MatchToRestMatchDtoConversor.toRestMatchDto(foundMatch);

            ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                    JsonToRestMatchDtoConversor.toObjectNode(matchDto), null);

        }
    }

}
