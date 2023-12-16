package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.util.exceptions.InputValidationException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MatchToRestMatchDtoConversor {
    public static List<RestMatchDto> toRestMatchDtos(List<Match> matches) {
        List<RestMatchDto> matchDtos = new ArrayList<>(matches.size());
        for (Match match : matches) {
            matchDtos.add(toRestMatchDto(match));
        }
        return matchDtos;
    }

    public static RestMatchDto toRestMatchDto(Match match) {
        return new RestMatchDto(match.getMatchId(), match.getVisitorName(), match.getMatchDate().toString(),
                match.getMatchPrice(), match.getMaxAvailable(), match.getNumberOfSells());
    }

    public static Match toMatch(RestMatchDto match) throws InputValidationException {
        try{
            return new Match(match.getMatchId(), match.getVisitorName(), LocalDateTime.parse(match.getMatchDate()),
                    match.getMatchPrice(), match.getMaxAvailable(), match.getNumberOfSells());
        }
        catch (DateTimeParseException e){
            throw new InputValidationException("Invalid date: "+ match.getMatchDate());
        }
    }
}
