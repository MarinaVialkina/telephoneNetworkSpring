package phone.network.controller;

import phone.network.dto.CallRequest;
import phone.network.model.CallResult;
import phone.network.service.CallService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/calls")
@CrossOrigin(origins = "*")
public class CallController {
    private final CallService callService;

    public CallController(CallService callService) {
        this.callService = callService;
    }

    // ТОЛЬКО Call операции
    @PostMapping("/{callerNumber}/to/{receiverNumber}")
    public ResponseEntity<CallResult> makeCall(
            @PathVariable String callerNumber,
            @PathVariable String receiverNumber) {

        CallResult result = callService.initiateCall(callerNumber, receiverNumber);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{phoneNumber}/answer")
    public ResponseEntity<CallResult> answerCall(@PathVariable String phoneNumber) {
        CallResult result = callService.answerCall(phoneNumber);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{phoneNumber}/terminate")
    public ResponseEntity<CallResult> terminateCall(@PathVariable String phoneNumber) {
        CallResult result = callService.terminateCall(phoneNumber);
        return ResponseEntity.ok(result);
    }

    // Альтернативный вариант с RequestBody
    @PostMapping("/initiate")
    public ResponseEntity<CallResult> initiateCall(@Valid @RequestBody CallRequest request) {
        // В CallRequest добавить поле callerNumber
        CallResult result = callService.initiateCall(request.getCallerNumber(), request.getTargetNumber());
        return ResponseEntity.ok(result);
    }
}
