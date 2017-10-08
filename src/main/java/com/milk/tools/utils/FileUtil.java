package com.milk.tools.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import org.w3c.dom.NameList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2016/3/20.
 */
public class FileUtil {

    private static final double KB = 1024.0;
    private static final double MB = KB * KB;
    private static final double GB = MB * MB;

    public static final String ROOT_FILE = Environment.getExternalStorageDirectory() + File.separator;


    private FileUtil(){
        //method in this class must be static
        throw new AssertionError();
    }

    public static void mkDirsForRootFile(Context context){
        File file = new File(ExternalStorage.getAllStorageLocations().get(ExternalStorage.SD_CARD), PackageUtil.getPackageName(context));
        boolean mkdirs = file.mkdirs();
        Logger.v("mkdirs [%s]",mkdirs+"");
    }


    public static boolean createFile(String path){
        if(path == null || path.length()==0){
            Logger.v("path can not be null or length is 0");
            return false;
        }
        File file = new File(path);

        if(file.exists()){
            Logger.v("the file of path is exists");
            return true;
        }

        try {
            file.getParentFile().mkdirs();
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String getFileName(File file){
        if(file == null){
            Logger.v("File is null");
            return  null;
        }

        if(!file.exists()){
            Logger.v("File is not exists");
            return null;
        }

        return file.getName();
    }

    public static String getFileName(String fileString){
        if(fileString == null){
            Logger.v("File is null");
            return  null;
        }

        File file = new File(fileString);

        if(!file.exists()){
            Logger.v("File is not exists");
            return null;
        }

        return file.getName();
    }

    public static void delete(File file) {
        if (!file.exists()) {
            Log.i("删除文件", "文件不存在！\n");
        } else if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File a : listFiles) {
                delete(a);
            }
        }
    }

    /**
     * 删除去掉a中的文件
     * @param file
     * @param unDeleteFils
     */
    public static void deleteExceptFiles(File file,List<String> unDeleteFils){
        if (!file.exists()) {
            Log.i("删除文件", "文件不存在！\n");
        } else if (file.isFile()) {
            if (!unDeleteFils.contains(file.getName())){
                file.delete();
            }
        } else if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            for (File a : listFiles) {
                delete(a);
            }
        }
    }

    public static void deleteFiles(File file){
        if (file == null){
            Logger.d("file param is null");
            return;
        }

        if (!file.exists()){
            Logger.d("file[%s] is not exists",file.getAbsolutePath());
            return;
        }

        if (!file.isDirectory()){
            Logger.d("file[%s] is not a directory",file.getAbsolutePath());
            return;
        }

        File[] f = file.listFiles();

        if (f.length < 5){
            Logger.d("No need to process file when the number is less than 5 ");
            return;
        }

        List<File> files = new ArrayList<>();
        files.addAll(Arrays.asList(f));

        Collections.sort(files,new FileComparator());

        Logger.d("When the number of files exceeds 5,delete 5");

        for (int i = 0;i<5;i++){
            files.get(i).delete();
        }
    }

    public static void close (Writer pw) {
        if ( pw  != null){
            try {
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pw = null;
        }
    }

    public static void close (Reader br) {
        if ( br  != null){
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            br = null;
        }
    }


    public static class FileComparator implements Comparator<File> {
        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() < rhs.lastModified()){
                return -1;
            }else if (lhs.lastModified() == rhs.lastModified()){
                return 0;
            }else {
                return 1;
            }
        }
    }


    public static void saveStringToConfig(String path, String info) throws Exception {
        File destFile = new File(path);
        if (!(destFile.exists() || destFile.getParentFile().exists())) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        BufferedWriter output = new BufferedWriter(new FileWriter(destFile));
        output.write(info);
        output.close();
    }

    public static String getStringFromConfig(String path) throws IOException {
        File destFile = new File(path);
        if (!(destFile.exists() || destFile.getParentFile().exists())) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            return null;
        }
        BufferedReader input = new BufferedReader(new FileReader(destFile));
        String result = input.readLine();
        input.close();
        return result;
    }

    public static List<String> getStringsFromConfig(String path) throws IOException {
        File destFile = new File(path);
        if (!(destFile.exists() || destFile.getParentFile().exists())) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            return null;
        }
        BufferedReader input = new BufferedReader(new FileReader(destFile));
        List<String> strings = new ArrayList<>();
        String line = null;
        while ( (line = input.readLine())!=null ){
            strings.add(line);
        }
        input.close();
        return strings;
    }

    public static void saveStringToConfig(String path, String info, boolean isAppend) throws Exception {
        File destFile = new File(path);
        if (!(destFile.exists() || destFile.getParentFile().exists())) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        BufferedWriter output = new BufferedWriter(new FileWriter(destFile, isAppend));
        output.write(info);
        output.write("\n");
        output.close();
    }


    /**
     * 复制assert中的文件到指定的destPath
     * @param context
     * @param assertFileName
     * @param destPath
     */
    public static void copyAssertToDest(Context context,String assertFileName,String destPath){
        AssetManager assetManager = null;
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            assetManager = context.getAssets();
            inputStream = assetManager.open(assertFileName);
            fileOutputStream = new FileOutputStream(destPath);
            byte[] buffer = new byte[2048];
            int read = 0;
            while ( (read = inputStream.read(buffer)) != -1 ){
                fileOutputStream.write(buffer,0,read);
            }

            fileOutputStream.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if ( fileOutputStream != null ){
                    fileOutputStream.close();
                }

                if ( inputStream!=null )
                     inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制文件目录
     * @param srcDir 要复制的源目录 eg:/mnt/sdcard/DB
     * @param destDir 复制到的目标目录 eg:/mnt/sdcard/db/
     * @return
     */
    public static boolean copyDir(String srcDir, String destDir){
        File sourceDir = new File(srcDir);
        //判断文件目录是否存在
        if(!sourceDir.exists()){
            return false;
        }
        //判断是否是目录
        if (sourceDir.isDirectory()) {
            File[] fileList = sourceDir.listFiles();
            File targetDir = new File(destDir);
            //创建目标目录
            if(!targetDir.exists()){
                targetDir.mkdirs();
            }
            //遍历要复制该目录下的全部文件
            for(int i= 0;i<fileList.length;i++){
                if(fileList[i].isDirectory()){//如果如果是子目录进行递归
                    copyDir(fileList[i].getPath()+ "/",
                            destDir + fileList[i].getName() + "/");
                }else{//如果是文件则进行文件拷贝
                    copyFile(fileList[i].getPath(), destDir +fileList[i].getName());
                }
            }
            return true;
        }else {
            copyFileToDir(srcDir,destDir);
            return true;
        }
    }


    /**
     * 复制文件（非目录）
     * @param srcFile 要复制的源文件
     * @param destFile 复制到的目标文件
     * @return
     */
    private static boolean copyFile(String srcFile, String destFile){
        try{
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[]=new byte[1024];
            int len;
            while ((len= streamFrom.read(buffer)) > 0){
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch(Exception ex){
            return false;
        }
    }


    /**
     * 把文件拷贝到某一目录下
     * @param srcFile
     * @param destDir
     * @return
     */
    public static boolean copyFileToDir(String srcFile, String destDir){
        if ( srcFile == null || destDir == null ) return false;
        File fileDir = new File(destDir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        String destFile = destDir +"/" + new File(srcFile).getName();
        try{
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[]=new byte[1024];
            int len;
            while ((len= streamFrom.read(buffer)) > 0){
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch(Exception ex){
            return false;
        }
    }


    /**
     * 移动文件目录到某一路径下
     * @param srcFile
     * @param destDir
     * @return
     */
    public static boolean moveFile(String srcFile, String destDir) {
        //复制后删除原目录
        if (copyDir(srcFile, destDir)) {
            deleteFile(new File(srcFile));
            return true;
        }
        return false;
    }

    /**
     * 删除文件（包括目录）
     * @param delFile
     */
    public static void deleteFile(File delFile) {
        //如果是目录递归删除
        if (delFile.isDirectory()) {
            File[] files = delFile.listFiles();
            for (File file : files) {
                deleteFile(file);
            }
        }else{
            delFile.delete();
        }
        //如果不执行下面这句，目录下所有文件都删除了，但是还剩下子目录空文件夹
        delFile.delete();
    }

    public static String getFileSuffix(String fileName) {
        if (fileName == null) return null;
        String[] splitString = fileName.split("\\.");
        if (splitString.length < 2) return null;
        return splitString[splitString.length - 1];
    }
}
