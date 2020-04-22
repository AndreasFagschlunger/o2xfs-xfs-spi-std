package at.o2xfs.xfs.spi.std.memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import at.o2xfs.common.ByteArrayBuffer;
import at.o2xfs.memory.core.Address;
import at.o2xfs.memory.core.util.BaseMemoryGenerator;
import at.o2xfs.xfs.api.XfsException;

public class WfmAllocateGenerator extends BaseMemoryGenerator {

	private static final int INITIAL_CAPACITY = 128;

	private final StdSpiMemorySystem memorySystem;
	private final Optional<Address> original;

	private final List<ByteArrayBuffer> buffers;

	private Address address;

	public WfmAllocateGenerator(StdSpiMemorySystem memorySystem, Address original) {
		this.memorySystem = Objects.requireNonNull(memorySystem);
		this.original = Optional.ofNullable(original);
		buffers = new ArrayList<>();
		buffers.add(new ByteArrayBuffer(INITIAL_CAPACITY));
	}

	private ByteArrayBuffer buffer() {
		return buffers.get(buffers.size() - 1);
	}

	@Override
	public boolean isClosed() {
		return buffers.isEmpty();
	}

	@Override
	public void write(byte[] src) {
		buffer().append(src);
	}

	@Override
	public void writeNull() {
		buffer().append(StdSpiMemorySystem.NULL.getValue());
	}

	@Override
	public void startPointer() {
		buffers.add(new ByteArrayBuffer(INITIAL_CAPACITY));
	}

	@Override
	public void endPointer() {
		ByteArrayBuffer buffer = buffers.remove(buffers.size() - 1);
		if (buffer.length() == 0) {
			writeNull();
		} else {
			try {
				Address reference;
				if (original.isPresent()) {
					reference = memorySystem.wfmAllocateMore(original.get(), buffer.toByteArray());
				} else {
					reference = memorySystem.wfmAllocateBuffer(buffer.toByteArray());
				}
				write(reference.getValue());
			} catch (XfsException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void close() throws IOException {
		if (isClosed()) {
			return;
		} else if (buffers.size() != 1) {
			throw new IOException("");
		}
		ByteArrayBuffer buffer = buffers.remove(buffers.size() - 1);
		try {
			if (original.isPresent()) {
				address = memorySystem.wfmAllocateMore(original.get(), buffer.toByteArray());
			} else {
				address = memorySystem.wfmAllocateBuffer(buffer.toByteArray());
			}
		} catch (XfsException e) {
			throw new IOException(e);
		}
	}

	public Address allocate() throws IOException {
		close();
		return address;
	}
}
