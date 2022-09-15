package com.magiri.animalcare.Model;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatMemory {
    public static void saveLastChat(String chatData,String ChatID, Context context){
        try{
            FileOutputStream fileOutputStream=context.openFileOutput(ChatID+".txt",Context.MODE_PRIVATE);
            fileOutputStream.write(chatData.getBytes());
            fileOutputStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public static String getLastChat(Context context,String ChatID){
        String ChatData="0";
        try{
            FileInputStream fileInputStream=context.openFileInput(ChatID+".txt");
            InputStreamReader inputStreamReader=new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder=new StringBuilder();
            String line;
            while((line=bufferedReader.readLine())!=null){
                stringBuilder.append(line);
            }
            ChatData=stringBuilder.toString();
        }catch (IOException e){
            e.printStackTrace();
        }
        return ChatData;
    }


}
