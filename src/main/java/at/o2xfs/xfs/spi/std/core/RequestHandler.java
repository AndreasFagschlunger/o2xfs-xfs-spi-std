package at.o2xfs.xfs.spi.std.core;

import java.util.Objects;
import java.util.Optional;

import at.o2xfs.xfs.api.XfsException;
import at.o2xfs.xfs.spi.std.api.CloseRequest;
import at.o2xfs.xfs.spi.std.api.DeregisterRequest;
import at.o2xfs.xfs.spi.std.api.ExecuteRequest;
import at.o2xfs.xfs.spi.std.api.GetInfoRequest;
import at.o2xfs.xfs.spi.std.api.LockRequest;
import at.o2xfs.xfs.spi.std.api.OpenRequest;
import at.o2xfs.xfs.spi.std.api.RegisterRequest;
import at.o2xfs.xfs.spi.std.api.StdServiceProvider;
import at.o2xfs.xfs.spi.std.api.UnlockRequest;
import at.o2xfs.xfs.spi.std.api.WfpRequest;

public class RequestHandler {

	private final StdServiceProvider serviceProvider;

	private final WfpRequest request;

	public RequestHandler(StdServiceProvider serviceProvider, WfpRequest request) {
		this.serviceProvider = Objects.requireNonNull(serviceProvider);
		this.request = Objects.requireNonNull(request);
	}

	public void cancel() throws XfsException {
		serviceProvider.cancel(request.getServiceId(), request.getRequestId());
	}

	public Optional<Object> execute() throws XfsException {
		Optional<Object> result = Optional.empty();
		switch (request.getCompleteMessage()) {
		case OPEN_COMPLETE:
			serviceProvider.open((OpenRequest) request);
			break;
		case CLOSE_COMPLETE:
			serviceProvider.close((CloseRequest) request);
			break;
		case LOCK_COMPLETE:
			serviceProvider.lock((LockRequest) request);
			break;
		case UNLOCK_COMPLETE:
			serviceProvider.unlock((UnlockRequest) request);
			break;
		case REGISTER_COMPLETE:
			serviceProvider.register((RegisterRequest) request);
			break;
		case DEREGISTER_COMPLETE:
			serviceProvider.deregister((DeregisterRequest) request);
			break;
		case GETINFO_COMPLETE:
			result = Optional.of(serviceProvider.getInfo((GetInfoRequest) request));
			break;
		case EXECUTE_COMPLETE:
			result = serviceProvider.execute((ExecuteRequest) request);
			break;
		default:
			throw new IllegalArgumentException(request.getCompleteMessage().name());
		}
		return result;
	}

	public WfpRequest getRequest() {
		return request;
	}
}
