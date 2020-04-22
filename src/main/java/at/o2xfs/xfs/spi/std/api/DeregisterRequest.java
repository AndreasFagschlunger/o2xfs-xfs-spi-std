package at.o2xfs.xfs.spi.std.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsEventClass;
import at.o2xfs.xfs.api.XfsMessage;

public final class DeregisterRequest extends WfpRequest {

	private final Set<XfsEventClass> eventClasses;
	private final Optional<Address> hWndReg;

	private DeregisterRequest(RequestId requestId, ServiceId serviceId, Address hWnd, Set<XfsEventClass> eventClasses,
			Optional<Address> hWndReg) {
		super(requestId, serviceId, hWnd);
		Objects.requireNonNull(hWndReg);
		this.eventClasses = Collections.unmodifiableSet(new HashSet<>(eventClasses));
		this.hWndReg = hWndReg;
	}

	@Override
	public XfsMessage getCompleteMessage() {
		return XfsMessage.DEREGISTER_COMPLETE;
	}

	public Set<XfsEventClass> getEventClasses() {
		return eventClasses;
	}

	public Optional<Address> getHWndReg() {
		return hWndReg;
	}

	public static DeregisterRequest of(RequestId requestId, ServiceId serviceId, Address hWnd,
			Set<XfsEventClass> eventClasses, Optional<Address> hWndReg) {
		return new DeregisterRequest(requestId, serviceId, hWnd, eventClasses, hWndReg);
	}
}
