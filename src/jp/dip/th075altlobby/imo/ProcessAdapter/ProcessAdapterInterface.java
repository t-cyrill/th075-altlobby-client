package jp.dip.th075altlobby.imo.ProcessAdapter;

public interface ProcessAdapterInterface {

    public abstract void write(String s);

    public abstract int getInputStreamThreadExitStatus();

    public abstract void close();

    public abstract int getOutputStreamThreadExitStatus();

    public abstract boolean isAlive();

}