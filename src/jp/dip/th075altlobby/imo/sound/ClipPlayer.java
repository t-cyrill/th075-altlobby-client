package jp.dip.th075altlobby.imo.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ClipPlayer implements LineListener {
    private Clip clip;

    /**
     * ファイルを指定してプレイヤーのインスタンスを生成します。
     * 
     * @param filename
     *            ロードする音楽ファイル
     * @throws LineUnavailableException
     *             ラインが利用できないとき
     * @throws UnsupportedAudioFileException
     *             サポートされないファイル形式の場合
     * @throws IOException
     *             その他の入出力例外が発生した場合
     */
    public ClipPlayer(String file_path) throws LineUnavailableException,
            UnsupportedAudioFileException, IOException {
        load(file_path);
    }

    /**
     * 音楽ファイルをロードします。
     * 
     * @param filename
     *            ロードする音楽ファイル
     * @throws LineUnavailableException
     *             ラインが利用できないとき
     * @throws UnsupportedAudioFileException
     *             サポートされないファイル形式の場合
     * @throws IOException
     *             その他の入出力例外が発生した場合
     */
    public void load(String filename) throws LineUnavailableException,
            UnsupportedAudioFileException, IOException {
        // オーディオストリームを開く
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(
                filename));

        AudioFormat format = stream.getFormat();
        // ライン情報を取得
        DataLine.Info info = new DataLine.Info(Clip.class, format);

        // 空のクリップを作成
        clip = (Clip) AudioSystem.getLine(info);

        // クリップのイベントを監視
        clip.addLineListener(this);

        // オーディオストリームをクリップとして開く
        clip.open(stream);

        // ストリームを閉じる
        stream.close();
    }

    /**
     * このプレイヤーの音を再生します。
     */
    public void play() {
        clip.start();
    }

    @Override
    public void update(LineEvent event) {
        // ストップか最後まで再生された場合リソースを開放する
        if (event.getType() == LineEvent.Type.STOP) {
            Clip clip = (Clip) event.getSource();
            clip.stop();
            clip.close();
        }
    }

    /**
     * このプレイヤーを閉じます。 インスタンスを生成した場合、参照が解除されるためにも、不要になる前に必ず呼び出す必要があります。
     */
    public void close() {
        clip.close();
    }
}