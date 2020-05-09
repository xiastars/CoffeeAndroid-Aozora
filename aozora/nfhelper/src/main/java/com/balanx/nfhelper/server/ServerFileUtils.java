package com.balanx.nfhelper.server;

import android.content.Context;

import com.balanx.nfhelper.utils.Logs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by xiastars on 2017/8/2.
 */

public class ServerFileUtils {


    public static String readFileByLineOnAsset(String code, String filePath, Context context) {
        if(code == null){
            return null;
        }
        BufferedReader br = null;
        InputStream reader = null;
        try {
            reader = context.getAssets().open(filePath);
            InputStreamReader isr = new InputStreamReader(reader,"UTF-8");
            br = new BufferedReader(isr);
            String str = null;
            while ((str = br.readLine()) != null) {
                Logs.i("str:"+str);
                if (str != null && str.contains(",")) {
                    int index = str.indexOf(",");
                    String first = (String) str.subSequence(0, index);
                    Logs.i("first:"+first+",,,"+code);
                    if (code.equals(first)) {
                        return (String) str.subSequence(index + 1, str.length());
                    }
                }
            }

            br.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null && reader != null) {
                    br.close();
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
