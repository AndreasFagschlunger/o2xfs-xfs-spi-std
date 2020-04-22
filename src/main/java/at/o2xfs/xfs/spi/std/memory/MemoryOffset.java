package at.o2xfs.xfs.spi.std.memory;

import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import at.o2xfs.memory.core.Address;

public final class MemoryOffset {

	private final Address address;
	private int value;

	private MemoryOffset(Address address) {
		this.address = Objects.requireNonNull(address);
	}

	public Address getAddress() {
		return address;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(address).append(value).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MemoryOffset) {
			MemoryOffset memoryOffset = (MemoryOffset) obj;
			return new EqualsBuilder()
					.append(address, memoryOffset.address)
					.append(value, memoryOffset.value)
					.isEquals();
		}
		return false;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("address", address).append("value", value).toString();
	}

	public static MemoryOffset of(Address address) {
		return new MemoryOffset(address);
	}
}
