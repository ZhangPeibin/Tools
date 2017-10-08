package com.milk.tools.function.su;

import com.milk.tools.utils.Logger;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by Administrator on 2017/4/19.
 */

public class SuHelper {

    private OutputStreamWriter outputStreamWriter;

    private Process mProcess;

    private static SuHelper suHelper = null;


    public static SuHelper getSuHelper() {
        if (suHelper == null) {
            synchronized (SuHelper.class) {
                if (suHelper == null)
                    suHelper = new SuHelper();
            }
        }
        return suHelper;
    }

    public SuHelper() {
        try {
            ProcessBuilder mProcessBuilder = new ProcessBuilder("su");
            mProcess = mProcessBuilder.redirectErrorStream(true).start();
            outputStreamWriter = new OutputStreamWriter(mProcess.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exec(String command) {
        try {
            outputStreamWriter.write(command, 0, command.length());
            outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exec(String... command) {
        try {
            for (String s : command) {
                outputStreamWriter.write(s, 0, s.length());
            }
            outputStreamWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 通过CP命令copy文件
     *
     * @param from
     * @param to
     */
    public void copy(String from[], String to[]) {
        if ((from == null || from.length == 0)
                || (to == null || to.length == 0)) return;

        final int minSize = Math.min(from.length, to.length);

        String[] commands = new String[minSize];
        for (int i = 0; i < minSize; i++) {
            commands[i] = ("cp -f " + from[i] + "   " + to[i] + "\n");
        }

        exec(commands);
    }

    /**
     * 通过CP命令copy文件
     *
     * @param from
     * @param to
     */
    public void copyWithIOption(String from[], String to[]) {
        if ((from == null || from.length == 0)
                || (to == null || to.length == 0)) return;

        final int minSize = Math.min(from.length, to.length);

        String[] commands = new String[minSize];
        for (int i = 0; i < minSize; i++) {
            commands[i] = ("cp -f " + from[i] + "   " + to[i] + "\n");
        }

        exec(commands);
    }

    public void free() {
        if (outputStreamWriter != null) {
            try {
                outputStreamWriter.close();
                outputStreamWriter = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mProcess != null) {
            mProcess.destroy();
        }
        if (suHelper == null)
            suHelper = null;

        Logger.e("free success ");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        free();
    }
}
