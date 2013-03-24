package jp.dip.th075altlobby.imo.CasterAdapter;

import java.io.IOException;

import jp.dip.th075altlobby.imo.ProcessAdapter.CallBackRunnable;
import jp.dip.th075altlobby.imo.ProcessAdapter.ProcessAdapterInterface;

/**
 * <h2>管理外の別プロセスを起動する機能を提供する</h2>
 * <p>このクラスはProcessAdapterInterfaceを実装しますがProcessAdapterが持つ有用な機能を有しません。</p>
 * <p>起動されるプロセスはJavaの管理外におかれます。そのため、close, writeは機能を持ちません。</p>
 * <p>isAliveは常にfalseを、getInputStreamThreadExitStatus, getOutputStreamThreadExitStatusは常に0を返します。</p>
 * <p>IdiotCasterAdapterはIOスレッドを開きません。</p>
 *
 * @author Cyrill
 *
 */
public class IdiotCasterAdapter implements ProcessAdapterInterface {
	/**
	 * インスタンスを生成する。
	 * コンストラクタはインスタンスを生成しますが、
	 * runnable, closedRunnableが呼び出されることはありません。
	 *
	 * @param runnable コールバックRunnable
	 * @param closedRunnable コールバックRunnable
	 * @param command 実行されるコマンド
	 * @throws IOException 起動に失敗した場合
	 */
	public IdiotCasterAdapter(CallBackRunnable runnable,
			Runnable closedRunnable, String... command) throws IOException {
		String[] cmd = new String[command.length + 3];
		cmd[0] = "CMD";
		cmd[1] = "/C";
		cmd[2] = "START";
		System.arraycopy(command, 0, cmd, 3, command.length);
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.start();
	}

	@Override
	public void write(String s) {}

	@Override
	public void close() {}

	@Override
	public int getInputStreamThreadExitStatus() {
		return 0;
	}

	@Override
	public int getOutputStreamThreadExitStatus() {
		return 0;
	}

	@Override
	public boolean isAlive() {
		return false;
	}
}
