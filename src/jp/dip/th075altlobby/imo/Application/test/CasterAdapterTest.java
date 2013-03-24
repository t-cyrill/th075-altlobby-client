package jp.dip.th075altlobby.imo.Application.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jp.dip.th075altlobby.imo.ProcessAdapter.CallBackRunnable;
import jp.dip.th075altlobby.imo.ProcessAdapter.ProcessAdapter;
import jp.dip.th075altlobby.imo.ProcessAdapter.ProcessAdapterInterface;

public class CasterAdapterTest {
    public static void main(String[] args) throws IOException {
        ProcessAdapterInterface caster = new ProcessAdapter(
                new CallBackRunnable() {
                    @Override
                    public void run(String s) {
                        System.out.println(s);
                    }
                }, new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("プログラムが閉じられました。");
                    }
                }, "F:\\東方萃夢想関係\\東方萃夢想\\th075Caster.exe", "-w");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.equals("exit"))
                break;
            caster.write(line);
        }
        caster.close();
    }
}
