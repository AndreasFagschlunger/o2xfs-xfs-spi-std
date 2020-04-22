package at.o2xfs.xfs.spi.std.core;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsConstant;
import at.o2xfs.xfs.api.XfsEventClass;
import at.o2xfs.xfs.api.XfsMessage;
import at.o2xfs.xfs.spi.api.SpiMemorySystem;
import at.o2xfs.xfs.spi.std.api.SpiContext;

public class StdSpiContext implements SpiContext {

	private final SpiMemorySystem memorySystem;
	private final MessageQueue messageQueue;
	private final Map<EventMonitor, Set<XfsEventClass>> registrations;

	public StdSpiContext(SpiMemorySystem memorySystem, MessageQueue messageQueue) {
		this.memorySystem = Objects.requireNonNull(memorySystem);
		this.messageQueue = Objects.requireNonNull(messageQueue);
		registrations = new HashMap<>();
	}

	private void deregister(EventMonitor key, Set<XfsEventClass> eventClasses) {
		synchronized (registrations) {
			Set<XfsEventClass> classes = registrations.get(key);
			if (classes == null) {
				return;
			}
			classes.removeAll(eventClasses);
			if (classes.isEmpty() || eventClasses.isEmpty()) {
				registrations.remove(key);
			}
		}
	}

	private Stream<EventMonitor> findEventMonitors(XfsEventClass eventClass) {
		return registrations.entrySet().stream().filter(e -> e.getValue().contains(eventClass)).map(e -> e.getKey());
	}

	@Override
	public void deregister(ServiceId serviceId, Set<XfsEventClass> eventClasses, Optional<Address> hWndReg) {
		if (hWndReg.isPresent()) {
			deregister(EventMonitor.build(hWndReg.get(), serviceId), eventClasses);
		} else {
			synchronized (registrations) {
				for (EventMonitor key : registrations.keySet()) {
					if (serviceId.equals(key.getServiceId())) {
						deregister(key, eventClasses);
					}
				}
			}
		}
	}

	@Override
	public <E extends Enum<E> & XfsConstant> void fireExecuteEvent(ServiceId serviceId, RequestId requestId, E eventId,
			Optional<Object> outputParam) {
		Set<WfpEvent> events = findEventMonitors(XfsEventClass.EXECUTE_EVENTS)
				.filter(e -> e.getServiceId().equals(serviceId))
				.map(e -> new WfpEvent.Builder(e.getHWnd(), XfsMessage.EXECUTE_EVENT)
						.serviceId(e.getServiceId())
						.requestId(requestId)
						.eventId(eventId.getValue())
						.build())
				.collect(Collectors.toSet());
		messageQueue.offer(events);
	}

	@Override
	public <E extends Enum<E> & XfsConstant> void fireServiceEvent(E eventId, Optional<Object> outputParam) {
		Set<WfpEvent> events = findEventMonitors(XfsEventClass.SERVICE_EVENTS)
				.map(e -> new WfpEvent.Builder(e.getHWnd(), XfsMessage.SERVICE_EVENT)
						.serviceId(e.getServiceId())
						.eventId(eventId.getValue())
						.outputParam(outputParam)
						.build())
				.collect(Collectors.toSet());
		messageQueue.offer(events);
	}

	@Override
	public <E extends Enum<E> & XfsConstant> void fireSystemEvent(E eventId, Optional<Object> outputParam) {
		// TODO Auto-generated method stub

	}

	@Override
	public <E extends Enum<E> & XfsConstant> void fireUserEvent(E eventId, Optional<Object> outputParam) {
		Set<WfpEvent> events = findEventMonitors(XfsEventClass.USER_EVENTS)
				.map(e -> new WfpEvent.Builder(e.getHWnd(), XfsMessage.USER_EVENT)
						.serviceId(e.getServiceId())
						.eventId(eventId.getValue())
						.outputParam(outputParam)
						.build())
				.collect(Collectors.toSet());
		messageQueue.offer(events);
	}

	@Override
	public <T> T read(Address address, Class<T> valueType) {
		return memorySystem.read(address, valueType);
	}

	@Override
	public void register(ServiceId serviceId, Set<XfsEventClass> eventClasses, Address hWndReg) {
		EventMonitor key = EventMonitor.build(hWndReg, serviceId);
		synchronized (registrations) {
			Set<XfsEventClass> classes = registrations.computeIfAbsent(key, e -> EnumSet.noneOf(XfsEventClass.class));
			classes.addAll(eventClasses);
		}
	}

}
