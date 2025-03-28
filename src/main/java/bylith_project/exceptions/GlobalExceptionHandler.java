package bylith_project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	public static class ErrorResponse {
		private String error;
		private String message;

		public ErrorResponse(String error, String message) {
			this.error = error;
			this.message = message;
		}

		public String getError() {
			return error;
		}

		public String getMessage() {
			return message;
		}
	}

	@SuppressWarnings("serial")
	public static class ResourceNotFoundException extends RuntimeException {
		public ResourceNotFoundException(String message) {
			super(message);
		}
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
		return buildErrorResponse(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
		return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String error, String message) {
		return ResponseEntity.status(status).body(new ErrorResponse(error, message));
	}
}