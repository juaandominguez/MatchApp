package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.match.Match;
import es.udc.ws.app.thrift.ThriftMatchDto;
import es.udc.ws.util.exceptions.InputValidationException;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class MatchToThriftMatchDtoConversor {

    public static Match toMatch(ThriftMatchDto match) throws InputValidationException{
        try{
            return new Match(match.getMatchId(), match.getVisitorName(), LocalDateTime.parse(match.getMatchDate()),
                    Double.valueOf(match.getMatchPrice()).floatValue(),match.getMaxAvailable(),match.getNumberOfSells());
        }catch (DateTimeParseException e){
            throw new InputValidationException("Invalid date: "+ match.getMatchDate());
        }
    }

    public static List<ThriftMatchDto> toThriftMatchDtos(List<Match> matches) {

        List<ThriftMatchDto> dtos = new ArrayList<>(matches.size());

        for (Match match : matches) {
            dtos.add(toThriftMatchDto(match));
        }
        return dtos;

    }

    public static ThriftMatchDto toThriftMatchDto(Match match) {

        return new ThriftMatchDto(match.getMatchId(), match.getVisitorName(),
                match.getMatchDate().toString(),
                match.getMatchPrice(),(short) match.getMaxAvailable(),(short) match.getNumberOfSells());

    }
}
