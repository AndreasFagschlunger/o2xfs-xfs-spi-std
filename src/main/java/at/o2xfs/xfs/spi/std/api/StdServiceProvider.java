package at.o2xfs.xfs.spi.std.api;

import java.util.Optional;
import java.util.Set;

import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsException;

public interface StdServiceProvider {

	void cancel(ServiceId serviceId, RequestId requestId) throws XfsException;

	void close(CloseRequest request) throws XfsException;

	void deregister(DeregisterRequest request) throws XfsException;

	Optional<Object> execute(ExecuteRequest request) throws XfsException;

	Object getInfo(GetInfoRequest request) throws XfsException;

	String getLogicalName();

	Set<ServiceId> getServiceIds();

	void lock(LockRequest request) throws XfsException;

	void open(OpenRequest request) throws XfsException;

	void register(RegisterRequest request) throws XfsException;

	void unlock(UnlockRequest request) throws XfsException;

}
