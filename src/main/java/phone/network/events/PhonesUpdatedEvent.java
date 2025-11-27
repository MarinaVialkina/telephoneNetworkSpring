package phone.network.events;

import org.springframework.context.ApplicationEvent;

public class PhonesUpdatedEvent extends ApplicationEvent {
    public PhonesUpdatedEvent(Object source) {
        super(source);
    }
}