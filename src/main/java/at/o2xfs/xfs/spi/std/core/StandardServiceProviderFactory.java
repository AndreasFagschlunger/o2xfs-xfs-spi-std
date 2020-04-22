package at.o2xfs.xfs.spi.std.core;

import at.o2xfs.memory.databind.MemoryMapper;
import at.o2xfs.xfs.spi.api.ServiceProvider;
import at.o2xfs.xfs.spi.api.ServiceProviderFactory;
import at.o2xfs.xfs.spi.std.memory.StdSpiMemorySystem;

public class StandardServiceProviderFactory extends ServiceProviderFactory {

	@Override
	public ServiceProvider newServiceProvider() {
		StdSpiMemorySystem memorySystem = new StdSpiMemorySystem(new MemoryMapper());
		Win32MessageQueue messageQueue = new Win32MessageQueue(memorySystem);
		return new StdServiceProviderImpl(memorySystem, messageQueue);
	}
}
