package omare.com.mx.com.demo.reactive.validators;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RequestValidator {

    private Validator validator;

    public <T> Mono<T> validate(T t){
        if(t == null){
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        }

        Set<ConstraintViolation<T>> violations = validator.validate(t);

        String message = violations.stream()
                .map(constraintViolation -> String.format("%s value '%s' %s",
                        constraintViolation.getPropertyPath(),
                        constraintViolation.getInvalidValue(),
                        constraintViolation.getMessage()))
                .collect(Collectors.joining());

        if(violations.isEmpty()){
            return Mono.just(t);
        }

        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, message));
    }

}
