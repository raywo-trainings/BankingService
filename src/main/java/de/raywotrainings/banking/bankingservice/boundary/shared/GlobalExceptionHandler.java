package de.raywotrainings.banking.bankingservice.boundary.shared;


import de.raywotrainings.banking.bankingservice.control.account.InsufficientFundsException;
import de.raywotrainings.banking.bankingservice.control.shared.ClientDoesntExistException;
import de.raywotrainings.banking.bankingservice.control.shared.NotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ProblemDetail handleNotFoundException(NotFoundException exception) {
    var result = ProblemDetail.forStatus(NOT_FOUND);
    result.setTitle("Resource not found");
    result.setDetail(exception.getMessage());

    return result;
  }


  @ExceptionHandler({
      ClientDoesntExistException.class,
      WrongBookingTypeException.class,
      InsufficientFundsException.class,
      IllegalStateException.class
  })
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ProblemDetail handleClientDoesntException(Exception exception) {
    var result = ProblemDetail.forStatus(UNPROCESSABLE_ENTITY);

    result.setTitle("Unprocessable Entity");
    result.setDetail(exception.getMessage());

    return result;
  }


  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(UNPROCESSABLE_ENTITY)
  public ProblemDetail handleConstraintViolationException(ConstraintViolationException exception) {
    var result = ProblemDetail.forStatus(UNPROCESSABLE_ENTITY);
    result.setTitle("Unprocessable Entity");
    result.setDetail("Unable to process request due to constraint violations");
    result.setProperty(
        "violations",
        exception.getConstraintViolations()
            .stream()
            .map(v -> ConstraintViolationDTO.builder()
                .field(v.getPropertyPath().toString())
                .message(v.getMessage())
                .build()
            )
            .toList());

    return result;
  }


  /**
   * If @Valid fails on a method argument of the controller, Spring will throw
   * a MethodArgumentNotValidException. In the super class, this Exception
   * already has a handler method, so it is not possible to write a
   * custom method annotated with @ExceptionHandler. Instead, we need to
   * overwrite the existing one.
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
      @NonNull WebRequest request
  ) {
    ex.getBody().setStatus(UNPROCESSABLE_ENTITY);
    ex.getBody().setProperty(
        "violations",
        ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(
                v -> ConstraintViolationDTO.builder()
                    .field(v.getField())
                    .constraint(v.getCode())
                    .message(v.getDefaultMessage())
                    .build()
            )
            .collect(Collectors.toList())
    );

    return super.handleMethodArgumentNotValid(ex, headers, UNPROCESSABLE_ENTITY, request);
  }

}
