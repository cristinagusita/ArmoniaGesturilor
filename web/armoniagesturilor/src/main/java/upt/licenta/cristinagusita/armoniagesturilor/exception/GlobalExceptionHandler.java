package upt.licenta.cristinagusita.armoniagesturilor.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;



@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        APIResponse apiResponse = new APIResponse(false, ex.getMessage());

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(SuccessMessage.class)
    public ResponseEntity<APIResponse> handleSuccessMessage(SuccessMessage ex, WebRequest request) {
        APIResponse apiResponse = new APIResponse(true, ex.getMessage());
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}