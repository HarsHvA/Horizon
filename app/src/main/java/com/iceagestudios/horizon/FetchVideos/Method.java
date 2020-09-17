package com.iceagestudios.horizon.FetchVideos;

import java.io.File;

public class Method {
    public static void load_Directory_Files(File directory){
        File[] fileList = directory.listFiles();
        if(fileList != null && fileList.length > 0){
            for (int i=0; i<fileList.length; i++){
                if(fileList[i].isDirectory()){
                    if(fileList[i].getName().contains("cache") || fileList[i].getName().contains("Android")
                    || fileList[i].getName().contains("stories")|| fileList[i].getName().contains(".Statuses")
                    || fileList[i].getName().toLowerCase().contains("gifs"))
                    {

                    }else
                    {
                        load_Directory_Files(fileList[i]);
                    }
                }
                else {
                    String name = fileList[i].getName().toLowerCase();
                    for (String extension: Constant.videoExtensions){
                        //check the type of file
                        if(name.endsWith(extension)){
                            Constant.allMediaList.add(fileList[i]);
                            Constant.allMediaFoldersList.add(fileList[i].getParentFile());
                            //when we found file
                            break;
                        }
                    }
                }
            }
        }
    }
}
