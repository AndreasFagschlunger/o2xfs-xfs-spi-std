package at.o2xfs.xfs.spi.std.core;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsMessage;

public final class WfpEvent {

	public static class Builder {

		private final Address hWnd;
		private final XfsMessage msg;
		private RequestId requestId;
		private ServiceId serviceId;
		private int errorCode;
		private long eventId;
		private Optional<Object> outputParam;

		public Builder(Address hWnd, XfsMessage msg) {
			this.hWnd = Objects.requireNonNull(hWnd);
			this.msg = Objects.requireNonNull(msg);
			requestId = RequestId.ZERO;
			serviceId = ServiceId.ZERO;
			errorCode = 0;
			eventId = 0;
			outputParam = Optional.empty();
		}

		public Builder requestId(RequestId requestId) {
			this.requestId = Objects.requireNonNull(requestId);
			return this;
		}

		public Builder serviceId(ServiceId serviceId) {
			this.serviceId = Objects.requireNonNull(serviceId);
			return this;
		}

		public Builder errorCode(int errorCode) {
			this.errorCode = errorCode;
			return this;
		}

		public Builder eventId(long eventId) {
			this.eventId = eventId;
			return this;
		}

		public Builder outputParam(Optional<Object> outputParam) {
			this.outputParam = Objects.requireNonNull(outputParam);
			return this;
		}

		public WfpEvent build() {
			return new WfpEvent(this);
		}
	}

	private final Address hWnd;
	private final XfsMessage msg;
	private final RequestId requestId;
	private final ServiceId serviceId;
	private final int errorCode;
	private final long eventId;
	private final Optional<Object> outputParam;

	public WfpEvent(Builder builder) {
		hWnd = builder.hWnd;
		msg = builder.msg;
		requestId = builder.requestId;
		serviceId = builder.serviceId;
		errorCode = builder.errorCode;
		eventId = builder.eventId;
		outputParam = builder.outputParam;
	}

	public Address getHWnd() {
		return hWnd;
	}

	public XfsMessage getMsg() {
		return msg;
	}

	public RequestId getRequestId() {
		return requestId;
	}

	public ServiceId getServiceId() {
		return serviceId;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public long getEventId() {
		return eventId;
	}

	public Optional<Object> getOutputParam() {
		return outputParam;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(hWnd)
				.append(msg)
				.append(requestId)
				.append(serviceId)
				.append(errorCode)
				.append(eventId)
				.append(outputParam)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WfpEvent) {
			WfpEvent event = (WfpEvent) obj;
			return new EqualsBuilder()
					.append(hWnd, event.hWnd)
					.append(msg, event.msg)
					.append(requestId, event.requestId)
					.append(serviceId, event.serviceId)
					.append(errorCode, event.errorCode)
					.append(eventId, event.eventId)
					.append(outputParam, event.outputParam)
					.isEquals();
		}
		return false;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("hWnd", hWnd)
				.append("msg", msg)
				.append("requestId", requestId)
				.append("serviceId", serviceId)
				.append("errorCode", errorCode)
				.append("eventId", eventId)
				.append("outputParam", outputParam)
				.toString();
	}

}
