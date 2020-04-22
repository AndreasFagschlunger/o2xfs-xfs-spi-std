package at.o2xfs.xfs.spi.std.core;

import java.util.Collection;

public interface MessageQueue {

	void offer(Collection<? extends WfpEvent> events);

	void stop();

}
