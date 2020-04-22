package at.o2xfs.xfs.spi.std.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsEventClass;
import at.o2xfs.xfs.api.XfsMessage;

public final class RegisterRequest extends WfpRequest {

	private final Set<XfsEventClass> eventClasses;
	private final Address hWndReg;

	private RegisterRequest(RequestId requestId, ServiceId serviceId, Address hWnd, Set<XfsEventClass> eventClasses,
			Address hWndReg) {
		super(requestId, serviceId, hWnd);
		this.eventClasses = Collections.unmodifiableSet(new HashSet<>(eventClasses));
		this.hWndReg = hWndReg;
	}

	@Override
	public XfsMessage getCompleteMessage() {
		return XfsMessage.REGISTER_COMPLETE;
	}

	public Set<XfsEventClass> getEventClasses() {
		return eventClasses;
	}

	public Address getHWndReg() {
		return hWndReg;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append("eventClasses", eventClasses)
				.append("hWndReg", hWndReg)
				.toString();
	}

	public static RegisterRequest of(RequestId requestId, ServiceId serviceId, Address hWnd,
			Set<XfsEventClass> eventClasses, Address hWndReg) {
		return new RegisterRequest(requestId, serviceId, hWnd, eventClasses, hWndReg);
	}
}
