package at.o2xfs.xfs.spi.std.memory;

import java.util.Objects;

import at.o2xfs.memory.core.Address;
import at.o2xfs.memory.core.BaseReadableMemory;
import at.o2xfs.memory.databind.ReadableMemory;

public class SpiReadableMemory extends BaseReadableMemory implements ReadableMemory {

	private final StdSpiMemorySystem memorySystem;
	private final MemoryOffset offset;

	SpiReadableMemory(StdSpiMemorySystem memorySystem, Address address) {
		this.memorySystem = Objects.requireNonNull(memorySystem);
		this.offset = MemoryOffset.of(address);
	}

	@Override
	public byte[] read(int length) {
		byte[] result = memorySystem.read(offset, length);
		offset.setValue(offset.getValue() + length);
		return result;
	}

	@Override
	public Address nextAddress() {
		return Address.build(read(offset.getAddress().getValue().length));
	}

	@Override
	public ReadableMemory nextReference() {
		return new SpiReadableMemory(memorySystem, Address.build(read(offset.getAddress().getValue().length)));
	}
}
