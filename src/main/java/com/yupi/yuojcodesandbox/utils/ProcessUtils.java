package com.yupi.yuojcodesandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.yupi.yuojcodesandbox.model.ExecuteCodeResponse;
import com.yupi.yuojcodesandbox.model.ExecuteMessage;
import org.springframework.util.StopWatch;

import java.io.*;

/**
 * 进程工具类
 */
public class ProcessUtils {

    /**
     * 执行进程并获取信息
     * @param runProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName){
        ExecuteMessage executeMessage = new ExecuteMessage();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            //等待程序执行获取错误码
           int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
           //正常退出
           if (exitValue == 0){
               System.out.println(opName + "成功");
               //分批获取进程的正常输出
               BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
               StringBuilder compileOutputStringBuilder = new StringBuilder();
               //逐行读取
               String compileOutputLine;
               while ((compileOutputLine = bufferedReader.readLine()) != null){
//                    System.out.println(compileOutputLine);
                   compileOutputStringBuilder.append(compileOutputLine);
               }
//               System.out.println(compileOutputStringBuilder);
               executeMessage.setMessage(compileOutputStringBuilder.toString());
           }else {
               //异常退出
               System.out.println(opName + "失败，错误码：" + exitValue);
               //分批获取进程的输出
               BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
               StringBuilder compileOutputStringBuilder = new StringBuilder();
               //逐行读取
               String compileOutputLine;
               while ((compileOutputLine = bufferedReader.readLine()) != null){
                   compileOutputStringBuilder.append(compileOutputLine);
               }
               //分批获取进程的输出
               BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
               StringBuilder errorCompileOutputStringBuilder = new StringBuilder();
               //逐行读取
               String errorCompileOutputLine;
               while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null){
                   errorCompileOutputStringBuilder.append(errorCompileOutputLine);
               }
               executeMessage.setErrorMessage(compileOutputStringBuilder.toString());
           }
           stopWatch.stop();
           executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
       }catch (Exception e){
            e.printStackTrace();
       }
        return executeMessage;
    }


    /**
     * 执行交互式进程并获取信息
     * @param runProcess
     * @param args
     * @return
     */
    public static ExecuteMessage runInteractProcessAndGetMessage(Process runProcess, String args){
        ExecuteMessage executeMessage = new ExecuteMessage();


        try {
            OutputStream outputStream = runProcess.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

            String[] s = args.split(" ");
            String join = StrUtil.join("\n", s) + "\n";
            outputStreamWriter.write(join);
            outputStreamWriter.flush();

            //等待程序执行获取错误码
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);

            //分批获取进程的正常输出
            InputStream inputStream = runProcess.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder compileOutputStringBuilder = new StringBuilder();
            //逐行读取
            String compileOutputLine;
            while ((compileOutputLine = bufferedReader.readLine()) != null){
                compileOutputStringBuilder.append(compileOutputLine);
            }
            executeMessage.setMessage(compileOutputStringBuilder.toString());
            outputStreamWriter.close();
            outputStream.close();
            inputStream.close();
            runProcess.destroy();
        }catch (Exception e){
            e.printStackTrace();
        }
        return executeMessage;
    }
}
