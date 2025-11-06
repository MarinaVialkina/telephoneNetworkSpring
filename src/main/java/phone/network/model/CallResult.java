package phone.network.model;

public class CallResult {
    private final boolean success;
    private final String message;

    public CallResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
