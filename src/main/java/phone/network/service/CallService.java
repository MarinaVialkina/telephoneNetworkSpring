package phone.network.service;

import phone.network.model.CallResult;

public interface CallService {
    CallResult initiateCall(String callerNumber, String receiverNumber);
    CallResult answerCall(String receiverNumber);
    CallResult terminateCall(String phoneNumber);
}
