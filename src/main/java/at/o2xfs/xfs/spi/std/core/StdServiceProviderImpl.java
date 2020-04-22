package at.o2xfs.xfs.spi.std.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.WfsVersion;
import at.o2xfs.xfs.api.XfsError;
import at.o2xfs.xfs.api.XfsEventClass;
import at.o2xfs.xfs.api.XfsException;
import at.o2xfs.xfs.api.XfsVersion;
import at.o2xfs.xfs.databind.XfsEnumSet32Wrapper;
import at.o2xfs.xfs.spi.api.ServiceProvider;
import at.o2xfs.xfs.spi.api.SpiMemorySystem;
import at.o2xfs.xfs.spi.std.api.CloseRequest;
import at.o2xfs.xfs.spi.std.api.DeregisterRequest;
import at.o2xfs.xfs.spi.std.api.ExecuteRequest;
import at.o2xfs.xfs.spi.std.api.GetInfoRequest;
import at.o2xfs.xfs.spi.std.api.LockRequest;
import at.o2xfs.xfs.spi.std.api.OpenRequest;
import at.o2xfs.xfs.spi.std.api.RegisterRequest;
import at.o2xfs.xfs.spi.std.api.StdServiceProvider;
import at.o2xfs.xfs.spi.std.api.StdServiceProviderFactory;
import at.o2xfs.xfs.spi.std.api.UnlockRequest;

public class StdServiceProviderImpl implements ServiceProvider {

	private static final Logger LOG = LogManager.getLogger(StdServiceProviderImpl.class);

	private static final XfsVersion LOW_VERSION = XfsVersion.V3_00;
	private static final XfsVersion HIGH_VERSION = XfsVersion.V3_30;

	private final SpiMemorySystem memorySystem;
	private final MessageQueue messageQueue;
	private final RequestQueue requestQueue;

	private final Map<String, StdServiceProvider> serviceProviders;

	public StdServiceProviderImpl(SpiMemorySystem memorySystem, MessageQueue messageQueue) {
		this.memorySystem = Objects.requireNonNull(memorySystem);
		this.messageQueue = Objects.requireNonNull(messageQueue);
		this.requestQueue = new RequestQueue(messageQueue);
		this.serviceProviders = new HashMap<>();
	}

	private int close(ServiceId hService, Address hWnd, RequestId requestId) {
		LOG.debug("WFPClose: hService={}, hWnd={}, requestId={}", hService, hWnd, requestId);
		int errorCode = 0;
		try {
			requestQueue.offer(getServiceProvider(hService), CloseRequest.build(requestId, hService, hWnd));
		} catch (XfsException e) {
			errorCode = e.getErrorCode();
		}
		return errorCode;
	}

	private StdServiceProvider createServiceProvider(String logicalName) {
		ServiceLoader<StdServiceProviderFactory> loader = ServiceLoader.load(StdServiceProviderFactory.class);
		StdServiceProvider serviceProvider = null;
		for (StdServiceProviderFactory factory : loader) {
			serviceProvider = factory
					.createServiceProvider(new StdSpiContext(memorySystem, messageQueue), logicalName)
					.get();
			break;
		}
		return serviceProvider;
	}

	private int deregister(ServiceId hService, Set<XfsEventClass> eventClasses, Optional<Address> hWndReg, Address hWnd,
			RequestId requestId) {
		LOG
				.info("WFPDeregister: hService={}, eventClasses={}, hWndReg={}, hWnd={}, requestId={}", hService,
						eventClasses, hWndReg, hWnd, requestId);
		int errorCode = 0;
		try {
			DeregisterRequest request = DeregisterRequest.of(requestId, hService, hWnd, eventClasses, hWndReg);
			requestQueue.offer(getServiceProvider(hService), request);
		} catch (XfsException e) {
			errorCode = e.getErrorCode();
		}
		return errorCode;
	}

	private int execute(ServiceId hService, long dwCommand, Optional<Address> lpCmdData, long dwTimeOut, Address hWnd,
			RequestId requestId) {
		LOG
				.info("WFPExecute: hService={}, dwCommand={}, lpCmdData={}, dwTimeOut={}, hWnd={}, requestId={}",
						hService, dwCommand, lpCmdData, dwTimeOut, hWnd, requestId);
		int errorCode = 0;
		try {
			requestQueue
					.offer(getServiceProvider(hService),
							ExecuteRequest.build(requestId, hService, hWnd, dwCommand, lpCmdData));
		} catch (XfsException e) {
			errorCode = e.getErrorCode();
		}
		return errorCode;
	}

	private int getInfo(ServiceId hService, long category, Optional<Address> queryDetails, long dwTimeOut, Address hWnd,
			RequestId requestId) {
		LOG
				.info("WFPGetInfo: hService={}, dwCategory={}, queryDetails={}, dwTimeOut={}, hWnd={}, requestId={}",
						hService, category, queryDetails, dwTimeOut, hWnd, requestId);
		int errorCode = 0;
		try {
			requestQueue
					.offer(getServiceProvider(hService),
							GetInfoRequest.build(requestId, hService, hWnd, category, queryDetails));
		} catch (XfsException e) {
			errorCode = e.getErrorCode();
		}
		return errorCode;
	}

	private StdServiceProvider getServiceProvider(ServiceId hService) throws XfsException {
		return serviceProviders
				.values()
				.stream()
				.filter(e -> e.getServiceIds().contains(hService))
				.findFirst()
				.orElseThrow(() -> new XfsException(XfsError.INVALID_HSERVICE));
	}

	private int lock(ServiceId hService, int timeOut, Address hWnd, RequestId requestId) {
		LOG.info("WFPLock: hService={}, timeOut={}, hWnd={}, requestId={}", hService, timeOut, hWnd, requestId);
		int errorCode = 0;
		try {
			requestQueue.offer(getServiceProvider(hService), LockRequest.build(requestId, hService, hWnd, timeOut));
		} catch (XfsException e) {
			errorCode = e.getErrorCode();
		}
		return errorCode;
	}

	private int open(ServiceId hService, String logicalName, Address hApp, Optional<String> appId, long dwTraceLevel,
			long dwTimeOut, Address hWnd, RequestId requestId, Address hProvider, long dwSPIVersionsRequired,
			Address lpSPIVersion, long dwSrvcVersionsRequired, Address lpSrvcVersion) {
		LOG
				.info("WFPOpen: hService={}, lpszLogicalName={}, hApp={}, lpszAppId={}, dwTraceLevel={}, dwTimeOut={}, hWnd={}, requestId={}, hProvider={}, dwSPIVersionsRequired={}, lpSPIVersion={}, dwSrvcVersionsRequired={}, lpSrvcVersion={}",
						hService, logicalName, hApp, appId, dwTraceLevel, dwTimeOut, hWnd, requestId, hProvider,
						dwSPIVersionsRequired, lpSPIVersion, dwSrvcVersionsRequired, lpSrvcVersion);
		XfsError result = XfsError.INTERNAL_ERROR;

		StdServiceProvider serviceProvider;
		synchronized (serviceProviders) {
			serviceProvider = serviceProviders.computeIfAbsent(logicalName, e -> createServiceProvider(logicalName));
		}
		if (serviceProvider == null) {
			return (int) result.getValue();
		}
		WfsVersion.Builder spiVersionBuilder = new WfsVersion.Builder()
				.lowVersion(LOW_VERSION)
				.highVersion(HIGH_VERSION);
		WfsVersion.Builder srvcVersionBuilder = new WfsVersion.Builder()
				.lowVersion(LOW_VERSION)
				.highVersion(HIGH_VERSION);
		VersionNegotiation spiVersionNegotiation = VersionNegotiation
				.build(LOW_VERSION, HIGH_VERSION, (int) dwSPIVersionsRequired);
		VersionNegotiation srvcVersionNegotiation = VersionNegotiation
				.build(LOW_VERSION, HIGH_VERSION, (int) dwSrvcVersionsRequired);
		if (spiVersionNegotiation.isVersionTooLow()) {
			result = XfsError.SPI_VER_TOO_LOW;
		} else if (spiVersionNegotiation.isVersionTooHigh()) {
			result = XfsError.SPI_VER_TOO_HIGH;
		} else if (srvcVersionNegotiation.isVersionTooLow()) {
			result = XfsError.SRVC_VER_TOO_LOW;
		} else if (srvcVersionNegotiation.isVersionTooHigh()) {
			result = XfsError.SRVC_VER_TOO_HIGH;
		} else {
			spiVersionBuilder.version(spiVersionNegotiation.getVersion().get());
			srvcVersionBuilder.version(srvcVersionNegotiation.getVersion().get());
			result = XfsError.SUCCESS;
		}

		WfsVersion spiVersion = spiVersionBuilder.description(System.getProperty("java.version")).build();
		memorySystem.write(lpSPIVersion, spiVersion);

		WfsVersion srvcVersion = srvcVersionBuilder.description(System.getProperty("java.version")).build();
		memorySystem.write(lpSrvcVersion, srvcVersion);

		if (XfsError.SUCCESS.equals(result)) {
			requestQueue
					.offer(serviceProvider,
							new OpenRequest.Builder(requestId, hService, hWnd)
									.logicalName(logicalName)
									.hApp(hApp)
									.appId(appId)
									.traceLevel(dwTraceLevel)
									.dwSPIVersionsRequired(dwSPIVersionsRequired)
									.spiVersion(spiVersion)
									.dwSrvcVersionsRequired(dwSrvcVersionsRequired)
									.srvcVersion(srvcVersion)
									.build());
		}
		LOG.info("result={}", result);
		return (int) result.getValue();
	}

	private int register(ServiceId hService, Set<XfsEventClass> eventClasses, Address hWndReg, Address hWnd,
			RequestId requestId) {
		LOG
				.info("WFPRegister: hService={}, eventClasses={}, hWndReg={}, hWnd={}, requestId={}", hService,
						eventClasses, hWndReg, hWnd, requestId);
		int errorCode = 0;
		try {
			requestQueue
					.offer(getServiceProvider(hService),
							RegisterRequest.of(requestId, hService, hWnd, eventClasses, hWndReg));
		} catch (XfsException e) {
			errorCode = e.getErrorCode();
		}
		return errorCode;
	}

	private int unlock(ServiceId hService, Address hWnd, RequestId requestId) {
		LOG.info("WFPUnlock: hService={}, hWnd={}, requestId={}", hService, hWnd, requestId);
		int errorCode = 0;
		try {
			requestQueue.offer(getServiceProvider(hService), UnlockRequest.build(requestId, hService, hWnd));
		} catch (XfsException e) {
			errorCode = e.getErrorCode();
		}
		return errorCode;
	}

	@Override
	public int cancelAsyncRequest(int hService, long requestId) {
		LOG.info("cancelAsyncRequest: hService={}, requestId={}", hService, requestId);
		int result = 0;
		try {
			requestQueue.cancel(ServiceId.of(hService), RequestId.of(requestId));
		} catch (XfsException e) {
			result = e.getErrorCode();
		}
		return result;
	}

	@Override
	public int close(int hService, byte[] hWnd, long requestId) {
		LOG.debug("WFPClose: hService={}, hWnd={}, requestId={}", hService, hWnd, requestId);
		return close(ServiceId.of(hService), Address.build(hWnd), RequestId.of(requestId));
	}

	@Override
	public int deregister(int hService, long dwEventClass, byte[] hWndReg, byte[] hWnd, long requestId) {
		LOG
				.info("deregister: hService={}, dwEventClass={}, hWndReg={}, hWnd={}, requestId={}", hService,
						dwEventClass, hWndReg, hWnd, requestId);
		return deregister(ServiceId.of(hService), XfsEnumSet32Wrapper.of(dwEventClass, XfsEventClass.class),
				hWndReg == null ? Optional.empty() : Optional.of(Address.build(hWndReg)), Address.build(hWnd),
				RequestId.of(requestId));
	}

	@Override
	public int execute(int hService, long dwCommand, byte[] lpCmdData, long dwTimeOut, byte[] hWnd, long requestId) {
		LOG
				.debug("WFPExecute: hService={}, dwCommand={}, lpCmdData={}, dwTimeOut={}, hWnd={}, requestId={}",
						hService, dwCommand, lpCmdData, dwTimeOut, hWnd, requestId);
		return execute(ServiceId.of(hService), dwCommand,
				lpCmdData == null ? Optional.empty() : Optional.of(Address.build(lpCmdData)), dwTimeOut,
				Address.build(hWnd), RequestId.of(requestId));
	}

	@Override
	public int getInfo(int hService, long dwCategory, byte[] lpQueryDetails, long dwTimeOut, byte[] hWnd,
			long requestId) {
		LOG
				.debug("WFPGetInfo: hService={}, dwCategory={}, lpQueryDetails={}, dwTimeOut={}, hWnd={}, requestId={}",
						hService, dwCategory, lpQueryDetails, dwTimeOut, hWnd, requestId);
		return getInfo(ServiceId.of(hService), dwCategory,
				lpQueryDetails == null ? Optional.empty() : Optional.of(Address.build(lpQueryDetails)), dwTimeOut,
				Address.build(hWnd), RequestId.of(requestId));
	}

	@Override
	public int lock(int hService, long dwTimeOut, byte[] hWnd, long requestId) {
		LOG.debug("WFPLock: hService={}, dwTimeOut={}, hWnd={}, requestId={}", hService, dwTimeOut, hWnd, requestId);
		return lock(ServiceId.of(hService), (int) dwTimeOut, Address.build(hWnd), RequestId.of(requestId));
	}

	@Override
	public int open(int hService, String logicalName, byte[] hApp, String appId, long dwTraceLevel, long dwTimeOut,
			byte[] hWnd, long requestId, byte[] hProvider, long dwSPIVersionsRequired, byte[] lpSPIVersion,
			long dwSrvcVersionsRequired, byte[] lpSrvcVersion) {
		LOG
				.debug("WFPOpen: hService={}, lpszLogicalName={}, hApp={}, lpszAppId={}, dwTraceLevel={}, dwTimeOut={}, hWnd={}, requestId={}, hProvider={}, dwSPIVersionsRequired={}, lpSPIVersion={}, dwSrvcVersionsRequired={}, lpSrvcVersion={}",
						hService, logicalName, hApp, appId, dwTraceLevel, dwTimeOut, hWnd, requestId, hProvider,
						dwSPIVersionsRequired, lpSPIVersion, dwSrvcVersionsRequired, lpSrvcVersion);
		return open(ServiceId.of(hService), logicalName, Address.build(hApp), Optional.ofNullable(appId), dwTraceLevel,
				dwTimeOut, Address.build(hWnd), RequestId.of(requestId), Address.build(hProvider),
				dwSPIVersionsRequired, Address.build(lpSPIVersion), dwSrvcVersionsRequired,
				Address.build(lpSrvcVersion));
	}

	@Override
	public int register(int hService, long dwEventClass, byte[] hWndReg, byte[] hWnd, long requestId) {
		LOG
				.debug("WFPRegister: hService={},dwEventClass={},hWndReg={},hWnd={},requestId={}", hService,
						dwEventClass, hWndReg, hWnd, requestId);
		return register(ServiceId.of(hService), XfsEnumSet32Wrapper.of(dwEventClass, XfsEventClass.class),
				Address.build(hWndReg), Address.build(hWnd), RequestId.of(requestId));
	}

	@Override
	public int setTraceLevel(int hService, long dwTraceLevel) {
		LOG.info("WFPSetTraceLevel: hService={}, dwTraceLevel={}", hService, dwTraceLevel);
		return 0;
	}

	@Override
	public int unloadService() {
		LOG.info("WFPUnloadService");
		requestQueue.stop();
		messageQueue.stop();
		return 0;
	}

	@Override
	public int unlock(int hService, byte[] hWnd, long requestId) {
		LOG.debug("WFPUnlock: hService={}, hWnd={}, requestId={}", hService, hWnd, requestId);
		return unlock(ServiceId.of(hService), Address.build(hWnd), RequestId.of(requestId));
	}
}
