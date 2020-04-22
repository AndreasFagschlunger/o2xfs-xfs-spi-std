package at.o2xfs.xfs.spi.std.core;

import java.util.Optional;

import at.o2xfs.xfs.api.XfsVersion;

public final class VersionNegotiation {

	private final XfsVersion lowVersion;
	private final XfsVersion highVersion;
	private final XfsVersion requiredLowVersion;
	private final XfsVersion requiredHighVersion;

	private VersionNegotiation(XfsVersion lowVersion, XfsVersion highVersion, XfsVersion requiredLowVersion,
			XfsVersion requiredHighVersion) {
		this.lowVersion = lowVersion;
		this.highVersion = highVersion;
		this.requiredLowVersion = requiredLowVersion;
		this.requiredHighVersion = requiredHighVersion;
	}

	public boolean isVersionTooLow() {
		return requiredHighVersion.compareTo(lowVersion) < 0;
	}

	public boolean isVersionTooHigh() {
		return requiredLowVersion.compareTo(highVersion) > 0;
	}

	public Optional<XfsVersion> getVersion() {
		Optional<XfsVersion> result = Optional.empty();
		if (!isVersionTooLow() && !isVersionTooHigh()) {
			if (requiredHighVersion.compareTo(highVersion) > 0) {
				result = Optional.of(highVersion);
			} else {
				result = Optional.of(requiredHighVersion);
			}
		}
		return result;
	}

	public static VersionNegotiation build(XfsVersion lowVersion, XfsVersion highVersion, int versionsRequired) {
		XfsVersion requiredLowVersion = XfsVersion.of(versionsRequired >> 16 & 0xFFFF);
		XfsVersion requiredHighVersion = XfsVersion.of(versionsRequired & 0xFFFF);
		return new VersionNegotiation(lowVersion, highVersion, requiredLowVersion, requiredHighVersion);
	}
}
