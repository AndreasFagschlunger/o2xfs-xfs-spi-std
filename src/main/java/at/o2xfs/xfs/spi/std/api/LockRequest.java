package at.o2xfs.xfs.spi.std.api;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsMessage;

public final class LockRequest extends WfpRequest {

	private final int timeOut;

	private LockRequest(RequestId requestId, ServiceId serviceId, Address hWnd, int timeOut) {
		super(requestId, serviceId, hWnd);
		this.timeOut = timeOut;
	}

	@Override
	public XfsMessage getCompleteMessage() {
		return XfsMessage.LOCK_COMPLETE;
	}

	public int getTimeOut() {
		return timeOut;
	}

	public static LockRequest build(RequestId requestId, ServiceId serviceId, Address hWnd, int timeOut) {
		return new LockRequest(requestId, serviceId, hWnd, timeOut);
	}
}
