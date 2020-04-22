package at.o2xfs.xfs.spi.std.api;

import java.util.Optional;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsMessage;

public final class ExecuteRequest extends WfpRequest {

	private final long command;
	private final Optional<Address> data;

	private ExecuteRequest(RequestId requestId, ServiceId serviceId, Address hWnd, long command,
			Optional<Address> data) {
		super(requestId, serviceId, hWnd);
		this.command = command;
		this.data = data;
	}

	@Override
	public XfsMessage getCompleteMessage() {
		return XfsMessage.EXECUTE_COMPLETE;
	}

	public long getCommand() {
		return command;
	}

	public Optional<Address> getData() {
		return data;
	}

	public static ExecuteRequest build(RequestId requestId, ServiceId serviceId, Address hWnd, long command,
			Optional<Address> data) {
		return new ExecuteRequest(requestId, serviceId, hWnd, command, data);
	}
}
