package at.o2xfs.xfs.spi.std.api;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsMessage;

public final class CloseRequest extends WfpRequest {

	private CloseRequest(RequestId requestId, ServiceId serviceId, Address hWnd) {
		super(requestId, serviceId, hWnd);
	}

	@Override
	public XfsMessage getCompleteMessage() {
		return XfsMessage.CLOSE_COMPLETE;
	}

	public static final CloseRequest build(RequestId requestId, ServiceId serviceId, Address hWnd) {
		return new CloseRequest(requestId, serviceId, hWnd);
	}
}
