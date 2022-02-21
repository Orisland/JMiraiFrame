package org.orisland.wows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;


public class HttpUtil {
	public static void main(String[] args) {
	}

    public static void download(String urlString, String filename,String savePath){  
        // 构造URL  
        URL url;
		try {
			url = new URL(urlString);
	        URLConnection con = url.openConnection();  
	        con.setConnectTimeout(5*1000);  
	        InputStream is = con.getInputStream();  
	        byte[] bs = new byte[1024];  
	        int len;  
	       File sf=new File(savePath);  
	       if(!sf.exists()){  
	           sf.mkdirs();  
	       }  
	       OutputStream os = new FileOutputStream(sf.getPath()+"/"+filename);  
	        while ((len = is.read(bs)) != -1) {  
	          os.write(bs, 0, len);  
	        }  
	        os.close();  
	        is.close();  
		} catch (Exception e) {
			e.printStackTrace();
		}  
       
    }   
    
    
    public static String Get(String str,String code) {
    	if(code==null | "".equals(code)){
    		code="utf-8";
    	}
		StringBuilder sb = new StringBuilder();
		URL url;
		try {
			 url = new URL(str);
			 URLConnection urlConnection = url.openConnection();
			 urlConnection.setConnectTimeout(20*1000);
			 urlConnection.setReadTimeout(20*1000);
			 urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");
			 urlConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			 urlConnection.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
			 BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),code)); // 获取输入流
			 String line = null;
			 while ((line = br.readLine()) != null) {
			   sb.append(line + "\n");
			 }
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("访问地址失败！ URL="+str);
		}
		return sb.toString();
	}
    
    public static String Post(String url, String param,String code) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();;
        if(code == null){
        	code = "UTF-8";
        }
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),code));
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),code));
            String line;
            while ((line = in.readLine()) != null) {
            	result.append(line);
            }
            
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！ URL="+url+"  param="+param);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally{
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result.toString();
    }    
    
    
    

}
