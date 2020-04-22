package at.o2xfs.xfs.spi.std.api;

import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;

import at.o2xfs.memory.core.Address;
import at.o2xfs.xfs.api.RequestId;
import at.o2xfs.xfs.api.ServiceId;
import at.o2xfs.xfs.api.XfsMessage;

public final class GetInfoRequest extends WfpRequest {

	private final long category;
	private final Optional<Address> queryDetails;

	private GetInfoRequest(RequestId requestId, ServiceId serviceId, Address hWnd, long category,
			Optional<Address> queryDetails) {
		super(requestId, serviceId, hWnd);
		this.category = category;
		this.queryDetails = queryDetails;
	}

	@Override
	public XfsMessage getCompleteMessage() {
		return XfsMessage.GETINFO_COMPLETE;
	}

	public long getCategory() {
		return category;
	}

	public Optional<Address> getQueryDetails() {
		return queryDetails;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append("category", category)
				.append("queryDetails", queryDetails)
				.toString();
	}

	public static GetInfoRequest build(RequestId requestId, ServiceId serviceId, Address hWnd, long category,
			Optional<Address> queryDetails) {
		return new GetInfoRequest(requestId, serviceId, hWnd, category, queryDetails);
	}
}
