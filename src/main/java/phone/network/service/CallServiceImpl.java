package phone.network.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phone.network.model.Call;
import phone.network.model.CallResult;
import phone.network.model.Phone;
import phone.network.model.PhoneStatuses;
import phone.network.repository.PhoneRepository;

@Service
public class CallServiceImpl implements CallService{
    private final PhoneRepository phoneRepository;

    public CallServiceImpl(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    @Override
    @Transactional
    public CallResult initiateCall(String callerNumber, String receiverNumber) {
        Phone caller = phoneRepository.findById(callerNumber).orElse(null);
        Phone receiver = phoneRepository.findById(receiverNumber).orElse(null);

        if (caller == null) {
            return new CallResult(false, "Абонент-инициатор не найден");
        }
        if (receiver == null) {
            return new CallResult(false, "Абонент не найден");
        }
        if (receiver.getStatus() != PhoneStatuses.FREE) {
            return new CallResult(false, "Абонент занят");
        }

        Call call = new Call(callerNumber, receiverNumber);

        caller.setStatus(PhoneStatuses.BUSY);
        caller.setCurrentCall(call);

        receiver.setStatus(PhoneStatuses.RINGING);
        receiver.setCurrentCall(call);

        phoneRepository.save(caller);
        phoneRepository.save(receiver);

        return new CallResult(true, "Успешно");
    }

    @Override
    @Transactional
    public CallResult answerCall(String receiverNumber) {
        Phone phone = phoneRepository.findById(receiverNumber).orElse(null);
        if (phone != null && phone.getStatus() == PhoneStatuses.RINGING) {
            phone.setStatus(PhoneStatuses.BUSY);
            phoneRepository.save(phone);
            return new CallResult(true, "Успешно");
        }
        return new CallResult(false, "Ошибка ответа на звонок");
    }

    @Override
    @Transactional
    public CallResult terminateCall(String phoneNumber) {
        Phone phone1 = phoneRepository.findById(phoneNumber).orElse(null);
        if (phone1 != null && phone1.getCurrentCall() != null) {
            Call currentCall = phone1.getCurrentCall();
            String otherNumber = currentCall.getOtherNumber(phoneNumber);

            phone1.setStatus(PhoneStatuses.FREE);
            phone1.setCurrentCall(null);
            phoneRepository.save(phone1);

            if (otherNumber != null) {
                Phone phone2 = phoneRepository.findById(otherNumber).orElse(null);
                if (phone2 != null) {
                    phone2.setStatus(PhoneStatuses.FREE);
                    phone2.setCurrentCall(null);
                    phoneRepository.save(phone2);
                }
            }
            return new CallResult(true, "Успешно");
        }
        return new CallResult(false, "Ошибка сброса звонка");
    }
}
