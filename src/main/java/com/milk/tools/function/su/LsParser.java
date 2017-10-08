package com.milk.tools.function.su;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/15.
 */

public class LsParser {

    private FileFilter mFilter;
    private List<RootFile> mFiles;
    private boolean includeHidden;
    private String mPath = null;

    private LsParser() {
        mFiles = new ArrayList<RootFile>();
    }

    public static LsParser parse(List<String> response, String path, FileFilter filter, boolean includeHidden) {
        LsParser parser = new LsParser();
        parser.mFilter = filter;
        parser.includeHidden = includeHidden;
        parser.mPath = path;
        for (String line : response) {
            parser.processLine(line);
        }
        return parser;
    }

    protected void processLine(String line) {
        RootFile file = new RootFile(mPath);
        LsTokenizer tokenizer = new LsTokenizer(line);
        int index = 0;
        String token;
        while ((token = tokenizer.nextToken()) != null) {
            if (index == 0) file.permissions = token.startsWith("-")?token.substring(1):token;
            else if (index == 1) file.owner = token;
            else if (index == 2) file.creator = token;
            else if (index == 3) {
                if (token.contains("-")) {
                    file.date = token;
                    index++; // since there's no size, skip to next token
                } else {
                    file.size = Long.parseLong(token);
                }
            } else if (index == 4) {
                file.date = token;
            } else if (index == 5) {
                file.time = token;
            } else if (index == 6) {
                // Store the original name for the case that this is a link (name is displayed but path is different)
                file.originalName = token;
                //file.setPath(mPath + "/" + token);
            } else {
                //file.setPath(token); // this is a link to another file/folder
            }
            index++;
        }
        boolean skip = includeHidden && file.getName().startsWith(".");
        if ((mFilter == null || mFilter.accept(file)) && !skip)
            mFiles.add(file);
    }

    public List<RootFile> getFiles() {
        return mFiles;
    }
}
