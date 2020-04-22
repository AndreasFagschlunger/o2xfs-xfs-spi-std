package at.o2xfs.xfs.spi.std.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import at.o2xfs.common.Library;
import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.WfsResult;
import at.o2xfs.xfs.api.XfsException;
import at.o2xfs.xfs.spi.api.SpiMemorySystem;

public class Win32MessageQueue implements MessageQueue {

	static {
		Library.loadLibrary("o2xfs-xfs-spi-std");
	}

	private static final Logger LOG = LogManager.getLogger(Win32MessageQueue.class);

	private final SpiMemorySystem memorySystem;

	private final List<WfpEvent> queue;

	private Thread thread = null;

	public Win32MessageQueue(SpiMemorySystem memorySystem) {
		this.memorySystem = Objects.requireNonNull(memorySystem);
		queue = new ArrayList<>();
	}

	private void doRun() {
		try {
			while (!thread.isInterrupted()) {
				WfpEvent event;
				synchronized (queue) {
					if (queue.isEmpty()) {
						LOG.debug("Waiting for events...");
						queue.wait();
					}
					event = queue.remove(0);
				}
				fireEvent(event);
			}
		} catch (InterruptedException e) {
			LOG.info("Stopped.");
		}
	}

	private native void postMessage0(byte[] hWnd, long msg, byte[] param);

	private void start() {
		if (thread == null || !thread.isAlive()) {
			thread = new Thread(() -> doRun());
			thread.start();
		}
	}

	private void fireEvent(WfpEvent event) {
		try {
			Address resultBuffer = memorySystem.allocateBuffer(WfsResult.empty());
			WfsResult.Builder builder = new WfsResult.Builder()
					.requestId(event.getRequestId())
					.serviceId(event.getServiceId())
					.eventId(event.getEventId());
			if (event.getOutputParam().isPresent()) {
				builder.buffer(memorySystem.allocateMore(resultBuffer, event.getOutputParam().get()));
			}
			WfsResult wfsResult = builder.build();
			LOG.info("WfsResult: {},outputParam={}", wfsResult, event.getOutputParam());
			memorySystem.write(resultBuffer, wfsResult);
			postMessage0(event.getHWnd().getValue(), event.getMsg().getValue(), resultBuffer.getValue());
		} catch (XfsException e) {
			LOG.error(new ParameterizedMessage("fireEvent: {}", new Object[] { event }, e));
		}
	}

	@Override
	public void offer(Collection<? extends WfpEvent> events) {
		synchronized (queue) {
			queue.addAll(events);
			queue.notifyAll();
			start();
		}
	}

	@Override
	public void stop() {
		synchronized (queue) {
			if (thread != null && thread.isAlive()) {
				thread.interrupt();
			}
		}
		if (thread != null) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
