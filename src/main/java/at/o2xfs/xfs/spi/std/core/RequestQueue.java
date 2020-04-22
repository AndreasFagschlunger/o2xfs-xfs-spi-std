package at.o2xfs.xfs.spi.std.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsError;
import at.o2xfs.xfs.api.XfsException;
import at.o2xfs.xfs.spi.std.api.ExecuteRequest;
import at.o2xfs.xfs.spi.std.api.GetInfoRequest;
import at.o2xfs.xfs.spi.std.api.StdServiceProvider;
import at.o2xfs.xfs.spi.std.api.WfpRequest;

public class RequestQueue {

	private static final Logger LOG = LogManager.getLogger(RequestQueue.class);

	private final MessageQueue messageQueue;
	private final ExecutorService executorService;

	private final Map<RequestId, RequestHandler> requests;

	public RequestQueue(MessageQueue messageQueue) {
		this.messageQueue = Objects.requireNonNull(messageQueue);
		executorService = Executors.newCachedThreadPool();
		requests = new HashMap<>();
	}

	private void process(RequestHandler handler) {
		LOG.debug("Execute: {}", handler.getRequest());
		Optional<Object> outputParam = Optional.empty();
		int errorCode = (int) XfsError.INTERNAL_ERROR.getValue();
		try {
			outputParam = handler.execute();
			errorCode = 0;
		} catch (XfsException e) {
			errorCode = e.getErrorCode();
		} finally {
			fireOperationCompleteEvent(handler.getRequest(), errorCode, outputParam);
		}
	}

	void fireOperationCompleteEvent(WfpRequest request, int errorCode, Optional<Object> outputParam) {
		long eventId = 0;
		if (request instanceof GetInfoRequest) {
			eventId = ((GetInfoRequest) request).getCategory();
		} else if (request instanceof ExecuteRequest) {
			eventId = ((ExecuteRequest) request).getCommand();
		}
		WfpEvent event = new WfpEvent.Builder(request.getHWnd(), request.getCompleteMessage())
				.requestId(request.getRequestId())
				.serviceId(request.getServiceId())
				.eventId(eventId)
				.errorCode(errorCode)
				.outputParam(outputParam)
				.build();
		messageQueue.offer(Collections.singleton(event));
		synchronized (requests) {
			requests.remove(request.getRequestId());
		}
	}

	public void cancel(ServiceId serviceId, RequestId requestId) throws XfsException {
		synchronized (requests) {
			RequestHandler handler = requests.get(requestId);
			if (handler == null) {
				throw new XfsException(XfsError.INVALID_REQ_ID);
			}
			handler.cancel();
		}
	}

	public void offer(StdServiceProvider serviceProvider, WfpRequest request) {
		synchronized (requests) {
			RequestHandler command = new RequestHandler(serviceProvider, request);
			executorService.execute(() -> process(command));
			requests.put(request.getRequestId(), command);
		}
	}

	public void stop() {
		executorService.shutdown();
	}
}
