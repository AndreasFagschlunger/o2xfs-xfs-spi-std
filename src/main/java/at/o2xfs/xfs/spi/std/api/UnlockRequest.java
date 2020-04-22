package at.o2xfs.xfs.spi.std.api;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsMessage;

public final class UnlockRequest extends WfpRequest {

	private UnlockRequest(RequestId requestId, ServiceId serviceId, Address hWnd) {
		super(requestId, serviceId, hWnd);
	}

	@Override
	public XfsMessage getCompleteMessage() {
		return XfsMessage.UNLOCK_COMPLETE;
	}

	public static UnlockRequest build(RequestId requestId, ServiceId serviceId, Address hWnd) {
		return new UnlockRequest(requestId, serviceId, hWnd);
	}
}
