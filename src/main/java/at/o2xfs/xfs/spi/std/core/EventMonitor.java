package at.o2xfs.xfs.spi.std.core;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.ServiceId;

public final class EventMonitor {

	private final Address hWnd;
	private final ServiceId serviceId;

	private EventMonitor(Address hWnd, ServiceId serviceId) {
		this.hWnd = Objects.requireNonNull(hWnd);
		this.serviceId = Objects.requireNonNull(serviceId);

	}

	public Address getHWnd() {
		return hWnd;
	}

	public ServiceId getServiceId() {
		return serviceId;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(hWnd).append(serviceId).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EventMonitor) {
			EventMonitor eventMonitor = (EventMonitor) obj;
			return new EqualsBuilder()
					.append(hWnd, eventMonitor.hWnd)
					.append(serviceId, eventMonitor.serviceId)
					.isEquals();
		}
		return false;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("hWnd", hWnd).append("serviceId", serviceId).toString();
	}

	public static EventMonitor build(Address hWnd, ServiceId serviceId) {
		return new EventMonitor(hWnd, serviceId);
	}
}
