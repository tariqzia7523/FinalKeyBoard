package io.github.otobikb.inputmethod.changes;


import io.github.otobikb.inputmethod.latin.BuildConfig;

public class AddIds {
    public static  String  getBannerID(){
        String id = "";
        if(BuildConfig.DEBUG){
            id = "ca-app-pub-3940256099942544/6300978111"; // test id
        }else{
            id = "ca-app-pub-6247650642874574/6012186183"; // orignal id
        }
        return  id;
    }

    public static  String  getInterstialId(){
        String id = "";
        if(BuildConfig.DEBUG){
            id = "ca-app-pub-3940256099942544/1033173712"; // test id
        }else{
            id = "ca-app-pub-6247650642874574/1341764699"; // orignal id
        }
        return  id;
    }
    public static  String  getNativeId(){
        String id = "";
        if(BuildConfig.DEBUG){
            id = "ca-app-pub-3940256099942544/2247696110"; // test id
        }else{
            id = "ca-app-pub-6247650642874574/3422957922"; // orignal id
        }
        return  id;
    }
    public static  String  getOpenId(){
        String id = "";
        if(BuildConfig.DEBUG){
            id = "ca-app-pub-3940256099942544/3419835294"; // test id
        }else{
            id = "ca-app-pub-6247650642874574/9010777510"; // orignal id
        }
        return  id;
    }
}
