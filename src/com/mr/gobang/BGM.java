package com.mr.gobang;

import sun.net.ResourceManager;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.net.URI;
import java.net.URL;

public class BGM{
    static public BGM bgm;
    private File f;
    private URI uri;
    private URL url;
    private AudioClip audioClip;
    private boolean isPlaying;
    public static BGM getInstance(){
        if(bgm==null)
            bgm=new BGM();
        return bgm;
    }
    private BGM(){
        try{
            isPlaying=true;
            f=new File("src\\res\\music\\bgm.wav");
            uri=f.toURI();
            url=uri.toURL();
            audioClip= Applet.newAudioClip(url);
            //audioClip.loop();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void loop(){
        audioClip.loop();
    }
    public void play(){
        audioClip.play();
    }
    public void stop(){
        audioClip.stop();
    }


}
