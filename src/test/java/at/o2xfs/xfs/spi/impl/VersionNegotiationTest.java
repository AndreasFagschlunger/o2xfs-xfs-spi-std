package at.o2xfs.xfs.spi.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import at.o2xfs.xfs.api.XfsVersion;
import at.o2xfs.xfs.spi.std.core.VersionNegotiation;

public class VersionNegotiationTest {

	private static final XfsVersion V1_00 = XfsVersion.of(0x0001);
	private static final XfsVersion V2_20 = XfsVersion.of(0x1402);

	@Test
	public final void testMatchEqual() {
		VersionNegotiation negotiation = VersionNegotiation.build(V1_00, V1_00, 0x00010001);
		assertFalse(negotiation.isVersionTooLow());
		assertFalse(negotiation.isVersionTooHigh());
		assertEquals(V1_00, negotiation.getVersion().get());
	}

	@Test
	public final void testMatchLowest() {
		VersionNegotiation negotiation = VersionNegotiation.build(V1_00, V1_00, 0x00010A02);
		assertFalse(negotiation.isVersionTooLow());
		assertFalse(negotiation.isVersionTooHigh());
		assertEquals(V1_00, negotiation.getVersion().get());
	}

	@Test
	public final void testMatchHighest() {
		VersionNegotiation negotiation = VersionNegotiation.build(V1_00, V2_20, 0x0B020003);
		assertFalse(negotiation.isVersionTooLow());
		assertFalse(negotiation.isVersionTooHigh());
		assertEquals(V2_20, negotiation.getVersion().get());
	}

	@Test
	public final void testVerTooLow() {
		VersionNegotiation negotiation = VersionNegotiation.build(V2_20, XfsVersion.V3_00, 0x00010001);
		assertTrue(negotiation.isVersionTooLow());
		assertFalse(negotiation.isVersionTooHigh());
		assertFalse(negotiation.getVersion().isPresent());
	}

	@Test
	public final void testVerTooHigh() {
		VersionNegotiation negotiation = VersionNegotiation.build(V1_00, V1_00, 0x0B010003);
		assertFalse(negotiation.isVersionTooLow());
		assertTrue(negotiation.isVersionTooHigh());
		assertFalse(negotiation.getVersion().isPresent());
	}
}
