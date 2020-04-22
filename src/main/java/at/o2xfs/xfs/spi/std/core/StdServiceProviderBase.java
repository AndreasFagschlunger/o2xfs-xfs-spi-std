package at.o2xfs.xfs.spi.std.core;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsException;
import at.o2xfs.xfs.spi.std.api.DeregisterRequest;
import at.o2xfs.xfs.spi.std.api.RegisterRequest;
import at.o2xfs.xfs.spi.std.api.SpiContext;
import at.o2xfs.xfs.spi.std.api.StdServiceProvider;

public abstract class StdServiceProviderBase implements StdServiceProvider {

	protected final SpiContext context;

	private final String logicalName;

	protected final Set<ServiceId> serviceIds;

	public StdServiceProviderBase(SpiContext context, String logicalName) {
		this.context = Objects.requireNonNull(context);
		this.logicalName = Objects.requireNonNull(logicalName);
		serviceIds = new HashSet<>();
	}

	@Override
	public void deregister(DeregisterRequest request) throws XfsException {
		context.deregister(request.getServiceId(), request.getEventClasses(), request.getHWndReg());
	}

	@Override
	public String getLogicalName() {
		return logicalName;
	}

	@Override
	public Set<ServiceId> getServiceIds() {
		return serviceIds;
	}

	@Override
	public void register(RegisterRequest request) throws XfsException {
		context.register(request.getServiceId(), request.getEventClasses(), request.getHWndReg());
	}

}
