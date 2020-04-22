package at.o2xfs.xfs.spi.std.api;

import java.util.Optional;

public interface StdServiceProviderFactory {

	Optional<StdServiceProvider> createServiceProvider(SpiContext context, String logicalName);
}
