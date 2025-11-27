package phone.network.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import phone.network.events.PhonesUpdatedEvent;
import phone.network.model.Call;
import phone.network.model.CallResult;
import phone.network.model.Phone;
import phone.network.model.PhoneStatuses;
import phone.network.repository.PhoneRepository;
import org.springframework.context.ApplicationEventPublisher;

@Service
public class CallServiceImpl implements CallService {
    private final PhoneRepository phoneRepository;
    private final ApplicationEventPublisher eventPublisher; // ← НОВОЕ ПОЛЕ

    public CallServiceImpl(PhoneRepository phoneRepository, ApplicationEventPublisher eventPublisher) { // ← НОВЫЙ ПАРАМЕТР
        this.phoneRepository = phoneRepository;
        this.eventPublisher = eventPublisher;
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

        eventPublisher.publishEvent(new PhonesUpdatedEvent(this)); // ← ОДНА СТРОКА

        return new CallResult(true, "Успешно");
    }

    @Override
    @Transactional
    public CallResult answerCall(String receiverNumber) {
        Phone phone = phoneRepository.findById(receiverNumber).orElse(null);
        if (phone != null && phone.getStatus() == PhoneStatuses.RINGING) {
            phone.setStatus(PhoneStatuses.BUSY);
            phoneRepository.save(phone);
            eventPublisher.publishEvent(new PhonesUpdatedEvent(this)); // ← ОДНА СТРОКА
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
            Phone phone2 = phoneRepository.findById(otherNumber).orElse(null);

            // СБРАСЫВАЕМ ПЕРВЫЙ ТЕЛЕФОН
            phone1.setStatus(PhoneStatuses.FREE);
            phone1.setCurrentCall(null);
            phoneRepository.save(phone1);

            // СБРАСЫВАЕМ ВТОРОЙ ТЕЛЕФОН (если существует)
            if (phone2 != null) {
                phone2.setStatus(PhoneStatuses.FREE); // ← ДОБАВИТЬ ЭТУ СТРОКУ!
                phone2.setCurrentCall(null);
                phoneRepository.save(phone2);
            }

            eventPublisher.publishEvent(new PhonesUpdatedEvent(this));

            return new CallResult(true, "Успешно");
        }
        return new CallResult(false, "Ошибка сброса звонка");
    }
}
