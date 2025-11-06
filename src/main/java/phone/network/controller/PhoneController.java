package phone.network.controller;

import phone.network.model.Phone;
import phone.network.model.CallResult;
import phone.network.service.CallService;
import phone.network.service.PhoneManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/phones")
@CrossOrigin(origins = "*")
public class PhoneController {
    private final PhoneManagementService phoneManagementService;
    private final CallService callService;

    public PhoneController(PhoneManagementService phoneManagementService, CallService callService) {
        this.phoneManagementService = phoneManagementService;
        this.callService = callService;
    }

    // CRUD операции - через PhoneManagementService
    @GetMapping
    public ResponseEntity<List<Phone>> getAllPhones() {
        return ResponseEntity.ok(phoneManagementService.getAllPhones());
    }

    @PostMapping
    public ResponseEntity<?> addPhone(@RequestParam String phoneNumber) {
        Phone newPhone = phoneManagementService.addPhone(phoneNumber);
        if (newPhone == null) {
            return ResponseEntity.badRequest().body(new CallResult(false, "Телефон уже существует"));
        }
        return ResponseEntity.ok(newPhone);
    }

    @DeleteMapping("/{phoneNumber}")
    public ResponseEntity<CallResult> deletePhone(@PathVariable String phoneNumber) {
        boolean deleted = phoneManagementService.deletePhone(phoneNumber);
        if (deleted) {
            return ResponseEntity.ok(new CallResult(true, "Телефон удален"));
        }
        return ResponseEntity.badRequest().body(new CallResult(false, "Телефон не найден"));
    }

    // Call операции - напрямую через CallService
    @PostMapping("/{phoneNumber}/call")
    public ResponseEntity<CallResult> makeCall(
            @PathVariable String phoneNumber,
            @RequestParam String targetNumber) {
        CallResult result = callService.initiateCall(phoneNumber, targetNumber);
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
}