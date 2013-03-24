package jp.dip.th075altlobby.imo.CasterAdapter;

public interface CasterEventInterface {
	public final static int TIMEOUT_AWAY = 1,
							TIMEOUT_ACCESS = 2,
							INPUT_TIMEOUT = 3,
							BIND_ERROR = 4,
							TH075FAILED = 8;

	public abstract void alreadyFighting();
	public abstract void bufferMarginPrinted(int margin);
	public abstract void closed();
	public abstract void connected(String s);
	public abstract void defaultCall(String s);
	public abstract void failed(int reason);
	public abstract void inputBufferMarginTiming();
	public abstract void inputedBufferMargin(int margin);
	public abstract void observedDelay(double realDelay);
//	public abstract void phasePrinted(String s);
//	public abstract void portSetting();
//	public abstract void rewindFrames(String s);
//	public abstract void rollCasterWaitingInput();
//	public abstract void roundCount(String s);
//	public abstract void roundCountPrinted(String s);
//	public abstract void rollInputDelayPrinted(String s);
	public abstract void waitingOppositePlayerInput();
}
