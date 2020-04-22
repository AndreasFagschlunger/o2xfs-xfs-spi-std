package at.o2xfs.xfs.spi.std.api;

import java.util.Objects;
import java.util.Optional;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.WfsVersion;
import at.o2xfs.xfs.api.XfsMessage;

public final class OpenRequest extends WfpRequest {

	public static class Builder {

		private final RequestId requestId;
		private final ServiceId serviceId;
		private final Address hWnd;
		private String logicalName;
		private Address hApp;
		private Optional<String> appId;
		private long traceLevel;
		private long dwSPIVersionsRequired;
		private WfsVersion spiVersion;
		private long dwSrvcVersionsRequired;
		private WfsVersion srvcVersion;

		public Builder(RequestId requestId, ServiceId serviceId, Address hWnd) {
			this.requestId = Objects.requireNonNull(requestId);
			this.serviceId = Objects.requireNonNull(serviceId);
			this.hWnd = Objects.requireNonNull(hWnd);
			this.appId = Optional.empty();
		}

		public Builder logicalName(String logicalName) {
			this.logicalName = logicalName;
			return this;
		}

		public Builder hApp(Address hApp) {
			this.hApp = hApp;
			return this;
		}

		public Builder appId(Optional<String> appId) {
			this.appId = Objects.requireNonNull(appId);
			return this;
		}

		public Builder traceLevel(long traceLevel) {
			this.traceLevel = traceLevel;
			return this;
		}

		public Builder dwSPIVersionsRequired(long dwSPIVersionsRequired) {
			this.dwSPIVersionsRequired = dwSPIVersionsRequired;
			return this;
		}

		public Builder spiVersion(WfsVersion spiVersion) {
			this.spiVersion = spiVersion;
			return this;
		}

		public Builder dwSrvcVersionsRequired(long dwSrvcVersionsRequired) {
			this.dwSrvcVersionsRequired = dwSrvcVersionsRequired;
			return this;
		}

		public Builder srvcVersion(WfsVersion srvcVersion) {
			this.srvcVersion = srvcVersion;
			return this;
		}

		public OpenRequest build() {
			return new OpenRequest(this);
		}
	}

	private final String logicalName;
	private final Address hApp;
	private final Optional<String> appId;
	private final long traceLevel;
	private final long dwSPIVersionsRequired;
	private final WfsVersion spiVersion;
	private final long dwSrvcVersionsRequired;
	private final WfsVersion srvcVersion;

	private OpenRequest(Builder builder) {
		super(builder.requestId, builder.serviceId, builder.hWnd);
		logicalName = builder.logicalName;
		hApp = builder.hApp;
		appId = builder.appId;
		traceLevel = builder.traceLevel;
		dwSPIVersionsRequired = builder.dwSPIVersionsRequired;
		spiVersion = builder.spiVersion;
		dwSrvcVersionsRequired = builder.dwSrvcVersionsRequired;
		srvcVersion = builder.srvcVersion;
	}

	@Override
	public XfsMessage getCompleteMessage() {
		return XfsMessage.OPEN_COMPLETE;
	}

	public String getLogicalName() {
		return logicalName;
	}

	public Address gethApp() {
		return hApp;
	}

	public Optional<String> getAppId() {
		return appId;
	}

	public long getTraceLevel() {
		return traceLevel;
	}

	public long getDwSPIVersionsRequired() {
		return dwSPIVersionsRequired;
	}

	public WfsVersion getSpiVersion() {
		return spiVersion;
	}

	public long getDwSrvcVersionsRequired() {
		return dwSrvcVersionsRequired;
	}

	public WfsVersion getSrvcVersion() {
		return srvcVersion;
	}

}
