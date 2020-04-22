package at.o2xfs.xfs.spi.std.api;

import java.util.Optional;
import java.util.Set;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsConstant;
import at.o2xfs.xfs.api.XfsEventClass;

public interface SpiContext {

	void deregister(ServiceId serviceId, Set<XfsEventClass> eventClasses, Optional<Address> hWndReg);

	<E extends Enum<E> & XfsConstant> void fireExecuteEvent(ServiceId serviceId, RequestId requestId, E eventId,
			Optional<Object> outputParam);

	<E extends Enum<E> & XfsConstant> void fireServiceEvent(E eventId, Optional<Object> outputParam);

	<E extends Enum<E> & XfsConstant> void fireSystemEvent(E eventId, Optional<Object> outputParam);

	<E extends Enum<E> & XfsConstant> void fireUserEvent(E eventId, Optional<Object> outputParam);

	<T> T read(Address address, Class<T> valueType);

	void register(ServiceId serviceId, Set<XfsEventClass> eventClasses, Address hWndReg);

}
