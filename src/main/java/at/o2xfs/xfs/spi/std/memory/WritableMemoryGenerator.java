package at.o2xfs.xfs.spi.std.memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.o2xfs.common.Hex;
import at.o2xfs.memory.core.Address;
import at.o2xfs.memory.core.MemoryGenerator;
import at.o2xfs.memory.core.util.BaseMemoryGenerator;

public class WritableMemoryGenerator extends BaseMemoryGenerator implements MemoryGenerator {

	private static final Logger LOG = LogManager.getLogger(WritableMemoryGenerator.class);

	private final StdSpiMemorySystem memorySystem;
	private final List<MemoryOffset> offsets;
	private final int addressLength;

	public WritableMemoryGenerator(StdSpiMemorySystem memorySystem, Address address) {
		this.memorySystem = Objects.requireNonNull(memorySystem);
		offsets = new ArrayList<>();
		offsets.add(MemoryOffset.of(address));
		this.addressLength = address.getValue().length;
	}

	private MemoryOffset currentOffset() {
		return offsets.get(offsets.size() - 1);
	}

	@Override
	public void close() throws IOException {
		if (isClosed()) {
			return;
		}
		offsets.clear();
	}

	@Override
	public boolean isClosed() {
		return offsets.isEmpty();
	}

	@Override
	public void write(byte[] src) {
		LOG.debug("src={}", Hex.encode(src));
		MemoryOffset offset = currentOffset();
		memorySystem.write(offset, src);
		offset.setValue(offset.getValue() + src.length);
	}

	@Override
	public void writeNull() {
		write(new byte[addressLength]);
	}

	@Override
	public void startPointer() {
		MemoryOffset offset = currentOffset();
		Address address = Address.build(memorySystem.read(offset, addressLength));
		offset.setValue(offset.getValue() + addressLength);
		offsets.add(MemoryOffset.of(address));
	}

	@Override
	public void endPointer() {
		offsets.remove(offsets.size() - 1);
	}
}
