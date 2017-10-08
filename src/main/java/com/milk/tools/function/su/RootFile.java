package com.milk.tools.function.su;

import java.io.File;

/**
 * Created by Administrator on 2017/2/15.
 */

public class RootFile extends File{
    public String permissions;
    public String owner;
    public String creator;
    public long size = -1;
    public String date;
    public String time;
    public String originalName;

    public RootFile(File dir, String name) {
        super(dir, name);
    }

    public RootFile(String path) {
        super(path);
    }

    @Override
    public boolean isHidden() {
        return getName().startsWith(".");
    }

}
