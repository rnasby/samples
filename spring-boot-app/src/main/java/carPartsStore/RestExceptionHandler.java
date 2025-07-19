package carPartsStore;

import carPartsStore.auth.BadTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(BadTokenException.class)
    public ResponseEntity<String> handleBadToken(BadTokenException e) {
        return new ResponseEntity<>("Bad token", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ProblemDetail> handleServletRequestBindingException(ServletRequestBindingException e)   {
        return new ResponseEntity<>(e.getBody(), e.getStatusCode());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFound(NoResourceFoundException e) {
        return new ResponseEntity<>(e.getBody(), e.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllOthers(Exception e) {
        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
