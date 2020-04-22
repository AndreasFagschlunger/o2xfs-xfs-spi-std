package at.o2xfs.xfs.spi.std.api;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsMessage;

abstract public class WfpRequest {

	private final RequestId requestId;
	private final ServiceId serviceId;
	private final Address hWnd;

	public WfpRequest(RequestId requestId, ServiceId serviceId, Address hWnd) {
		this.requestId = Objects.requireNonNull(requestId);
		this.serviceId = Objects.requireNonNull(serviceId);
		this.hWnd = Objects.requireNonNull(hWnd);
	}

	public abstract XfsMessage getCompleteMessage();

	public RequestId getRequestId() {
		return requestId;
	}

	public Address getHWnd() {
		return hWnd;
	}

	public ServiceId getServiceId() {
		return serviceId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WfpRequest) {
			WfpRequest wfpRequest = (WfpRequest) obj;
			return new EqualsBuilder()
					.append(requestId, wfpRequest.requestId)
					.append(serviceId, wfpRequest.serviceId)
					.append(hWnd, wfpRequest.hWnd)
					.isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(requestId).append(serviceId).append(hWnd).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("requestId", requestId)
				.append("serviceId", serviceId)
				.append("hWnd", hWnd)
				.toString();
	}
}
