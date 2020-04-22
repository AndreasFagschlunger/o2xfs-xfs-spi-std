package at.o2xfs.xfs.spi.std.memory;

import java.io.IOException;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import at.o2xfs.common.Hex;
import at.o2xfs.common.Library;
import at.o2xfs.memory.core.Address;
import at.o2xfs.memory.databind.MemoryMapper;
import at.o2xfs.xfs.api.XfsError;
import at.o2xfs.xfs.api.XfsException;
import at.o2xfs.xfs.api.XfsExceptionFactory;
import at.o2xfs.xfs.spi.api.SpiMemorySystem;

public class StdSpiMemorySystem implements SpiMemorySystem {

	static {
		Library.loadLibrary("o2xfs-xfs-spi-std");
	}

	private static final Logger LOG = LogManager.getLogger(StdSpiMemorySystem.class);

	static final Address NULL = Address.build(new byte[sizeof()]);

	private final MemoryMapper mapper;

	public StdSpiMemorySystem(MemoryMapper mapper) {
		this.mapper = Objects.requireNonNull(mapper);
	}

	private native int wfmAllocateBuffer0(byte[] data, byte[] lpData);

	private native int wfmAllocateMore0(byte[] data, byte[] original, byte[] lpData);

	private native int wfmFreeBuffer(byte[] lpvData);

	private native void write0(byte[] ptr, int offset, byte[] src);

	private native byte[] read0(byte[] ptr, int offset, int length);

	byte[] read(MemoryOffset offset, int length) {
		return read0(offset.getAddress().getValue(), offset.getValue(), length);
	}

	Address wfmAllocateBuffer(byte[] value) throws XfsException {
		byte[] lpData = NULL.getValue();
		int errorCode = wfmAllocateBuffer0(value, lpData);
		LOG
				.debug("WFMAllocateBuffer: errorCode={},value={},lpData={}", errorCode, Hex.encode(value),
						Hex.encode(lpData));
		if (errorCode != 0) {
			throw XfsExceptionFactory.create(errorCode);
		}
		return Address.build(lpData);
	}

	Address wfmAllocateMore(Address original, byte[] value) throws XfsException {
		byte[] lpData = NULL.getValue();
		int errorCode = wfmAllocateMore0(value, original.getValue(), lpData);
		LOG
				.debug("WFMAllocateMore: errorCode={},value={},original={},lpData={}", errorCode, Hex.encode(value),
						original, Hex.encode(lpData));
		if (errorCode != 0) {
			throw XfsExceptionFactory.create(errorCode);
		}
		return Address.build(lpData);
	}

	void write(MemoryOffset offset, byte[] value) {
		LOG.debug("write: offset={},value={}", offset, Hex.encode(value));
		write0(offset.getAddress().getValue(), offset.getValue(), value);
	}

	@Override
	public Address allocateBuffer(Object value) throws XfsException {
		LOG.debug("allocateBuffer: value={}", value);
		Address result = null;
		try (WfmAllocateGenerator gen = new WfmAllocateGenerator(this, null)) {
			mapper.write(gen, value);
			result = gen.allocate();
		} catch (IOException e) {
			LOG.error(new ParameterizedMessage("Error writing object: value={}", new Object[] { value }, e));
			throw new XfsException(XfsError.INTERNAL_ERROR);
		}
		return result;
	}

	@Override
	public Address allocateMore(Address original, Object value) {
		LOG.debug("allocateMore: original={},value={}", original, value);
		Address result = null;
		try (WfmAllocateGenerator gen = new WfmAllocateGenerator(this, original)) {
			mapper.write(gen, value);
			result = gen.allocate();
		} catch (IOException e) {
			LOG
					.error(new ParameterizedMessage("Error writing object: original={}, value={}",
							new Object[] { original, value }, e));
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public void free(Address lpvData) {
		LOG.debug("WFMFreeBuffer: lpvData={}", lpvData);
		int errorCode = wfmFreeBuffer(lpvData.getValue());
		if (errorCode != 0) {
			LOG
					.error(new ParameterizedMessage("WFMFreeBuffer: lpvData={}", new Object[] { lpvData },
							XfsExceptionFactory.create(errorCode)));
		}
	}

	@Override
	public Address nullValue() {
		return NULL;
	}

	@Override
	public Address write(Object value) {
		return null;
	}

	@Override
	public void write(Address address, Object value) {
		try (WritableMemoryGenerator gen = new WritableMemoryGenerator(this, address)) {
			mapper.write(gen, value);
		} catch (IOException e) {
			LOG
					.error(new ParameterizedMessage("Error writing object: address={}, value={}",
							new Object[] { address, value }, e));
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T read(Address address, Class<T> valueType) {
		LOG.debug("read: address={},valueType={}", address, valueType);
		return mapper.read(new SpiReadableMemory(this, address), valueType);
	}

	private static native int sizeof();

}
