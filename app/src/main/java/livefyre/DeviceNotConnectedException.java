package livefyre;

public class DeviceNotConnectedException extends RuntimeException {

	private static final long serialVersionUID = -743862426460328113L;

	public DeviceNotConnectedException() {
		super("Device Not Connected Exception");
	}

	public DeviceNotConnectedException(Throwable throwable) {
		super(throwable);
	}

	public DeviceNotConnectedException(String msg) {
		super(msg);
	}

	public DeviceNotConnectedException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}
