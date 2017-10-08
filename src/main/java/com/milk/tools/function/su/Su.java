package com.milk.tools.function.su;

import android.content.Context;
import android.system.ErrnoException;


import com.milk.tools.utils.Logger;
import com.milk.tools.utils.Util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Administrator on 2017/2/7.
 */

public class Su {

    private static Shell.Interactive rootSession;

    //请求root权限
    //并对相关的文件权限进行修改
    public static void su (String... path) {
        Process process = null;
        try {
            if ( path == null ) return;
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec("su");
            DataOutputStream dos = new DataOutputStream(process.getOutputStream());
            for ( String p : path ) {
                Logger.e("获取[%s]的文件权限", p);
                dos.writeBytes("chmod 777 " + p + "\n");
            }
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            process.waitFor();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if ( process != null ) {
                Logger.e("release success" + process.exitValue());
                process.destroy();
            }
        }
    }


    //请求root权限
    //并对相关的文件权限进行修改
    public static void suCmd (String... command) {
        Process process = null;
        try {
            if ( command == null ) return;
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec("su");
            DataOutputStream dos = new DataOutputStream(process.getOutputStream());
            for ( String p : command ) {
                Logger.e("执行的命令[%s]", p);
                dos.writeBytes( p + "\n");
            }
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            process.waitFor();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if ( process != null ) {
                Logger.e("release success" + process.exitValue());
                process.destroy();
            }
        }
    }

    private void sendRootCommand () {
        rootSession.addCommand(new String[]{"id", "date", "ls -l /"}, 0,
                new Shell.OnCommandResultListener() {
                    public void onCommandResult (int commandCode, int exitCode, List<String> output) {
                    }
                });

        rootSession.addCommand(new String[]{"ls -l /"}, 1, new Shell.OnCommandLineListener() {
            @Override
            public void onCommandResult (int commandCode, int exitCode) {
            }

            @Override
            public void onLine (String line) {
            }
        });

        rootSession.addCommand(new String[]{"ls -l /sdcard"}, 2, new Shell.OnCommandLineListener() {
            @Override
            public void onCommandResult (int commandCode, int exitCode) {
            }

            @Override
            public void onLine (String line) {
            }
        });
    }

    public void openRootShell (Context activity) {
        if ( rootSession != null ) {
            sendRootCommand();
        } else {
            rootSession = new Shell.Builder().
                    useSU().
                    setWantSTDERR(true).
                    setWatchdogTimeout(5).
                    setMinimalLogging(true).
                    open(new Shell.OnCommandResultListener() {
                        // Callback to report whether the shell was successfully started up
                        @Override
                        public void onCommandResult (int commandCode, int exitCode, List<String> output) {
                            // note: this will FC if you rotate the phone while the dialog is up
                            if ( exitCode != Shell.OnCommandResultListener.SHELL_RUNNING ) {
                            } else {
                                // Shell is up: send our first request
                                sendRootCommand();
                            }
                        }
                    });
        }
    }

    /**
     * 通过CP命令copy文件
     *
     * @param from
     * @param to
     */
    public static void copy (String from[], String to[]) {
        Process process = null;
        BufferedReader errorStream = null;
        try {
            if ( (from == null || from.length == 0)
                    || (to == null || to.length == 0) ) return;

            final int minSize = Math.min(from.length, to.length);

            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec("su");
            DataOutputStream dos = new DataOutputStream(process.getOutputStream());

            for ( int i = 0; i < minSize; i++ ) {
                dos.writeBytes("cp -f " + from[i] + "   " + to[i] + "\n");
            }

            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            process.waitFor();
//            errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            String msg = "";
//            String line;
//            // 读取命令的执行结果
//            while ((line = errorStream.readLine()) != null) {
//                msg += line;
//            }
//            Logger.e("msg"+msg);
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if ( errorStream != null ) {
                try {
                    errorStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if ( process != null ) {
                Logger.e("release success" + process.exitValue());
                process.destroy();
            }
        }
    }


    public static void copyFileByNio (Context context, String from, String to) {
        File file = new File(from);
        FileInputStream fis = null;
        RandomAccessFile fos = null;
        FileChannel fisChannel = null;
        FileChannel fosChannel = null;
        try {
            File newFile = new File(to);
            if ( !newFile.exists() ) {
                newFile.getParentFile().mkdirs();
                newFile.createNewFile();
            }
            fis = new FileInputStream(file);
            fos = new RandomAccessFile(newFile, "rw");
            fisChannel = fis.getChannel();
            fosChannel = fos.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(1024);
            while ( fisChannel.read(buf) != -1 ) {
                buf.flip();
                fosChannel.write(buf);
                buf.clear();
            }
        } catch (FileNotFoundException e) {
            Su.suFile777WhenThrow(context, e.getCause(),
                    from,//EnMicroMsg.db
                    file.getParentFile().getPath(),//MicroMsg/md5(mm+uin)
                    file.getParentFile().getParentFile().getPath());//MicroMsg
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if ( fosChannel != null ) {
                    fosChannel.close();
                }
                if ( fisChannel != null ) {
                    fisChannel.close();
                }
                if ( fos != null ) {
                    fos.close();
                }
                if ( fis != null ) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void suFile777WhenThrow (Context context, Throwable t, String... path) {
        Throwable throwable = t;
        if ( throwable instanceof ErrnoException ) {
            ErrnoException errnoException = (ErrnoException) throwable;
            if ( errnoException.errno == 13 ) {
                String funcationName = (String) Util.getPrivateValue(errnoException, "functionName");
                if ( funcationName != null && funcationName.equals("open") ) {
                    Su.su(path);
                }
            }
        }
        t.printStackTrace();
    }


    public static RootFile list (String path) {
        try {
            List<String> response = runShell("ls -l \"" + path + "\"");
            LsParser lsParser = LsParser.parse(response, path, null, false);
            List<RootFile> rootFiles = lsParser.getFiles();
            if ( rootFiles == null || rootFiles.size() == 0 ) return null;
            if ( rootFiles.size() == 1 ) return rootFiles.get(0);
            RootFile resultFile = null;
            for ( RootFile rootFile : rootFiles ) {
                if ( rootFile != null && path.equals(rootFile.getPath()) ) {
                    resultFile = rootFile;
                    break;
                }
            }
            return resultFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> runShell (String... command) throws Exception {
        boolean suAvailable = Shell.SU.available();
        if ( !suAvailable )
            throw new Exception("can not su ");
        return Shell.SU.run(command);
    }
}
