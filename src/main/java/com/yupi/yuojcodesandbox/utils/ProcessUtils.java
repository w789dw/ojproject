package com.yupi.yuojcodesandbox.utils;

import cn.hutool.core.util.StrUtil;
import com.yupi.yuojcodesandbox.model.ExecuteCodeResponse;
import com.yupi.yuojcodesandbox.model.ExecuteMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.io.*;
import java.lang.management.MemoryUsage;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;

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
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            // 记录初始内存
            long initialMemory = memoryMXBean.getHeapMemoryUsage().getUsed();
            final long[] maxMemory = {0};

            // 启动一个线程定期检查内存
            Thread memoryMonitor = new Thread(() -> {
                while (runProcess.isAlive()) {
                    MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();
                    maxMemory[0] = Math.max(maxMemory[0], heapUsage.getUsed());
                    try {
                        Thread.sleep(100); // 每 100ms 检查一次
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            memoryMonitor.start();

            //等待程序执行获取错误码
            int exitValue = runProcess.waitFor();
            System.out.println(runProcess.getInputStream().toString());
            executeMessage.setExitValue(exitValue);
            memoryMonitor.join(); // 等待内存监控线程结束
            executeMessage.setMemory((maxMemory[0] - initialMemory)/1000); // 记录最大内存增量

           //正常退出
           if (exitValue == 0){
               System.out.println(opName + "成功");
               //分批获取进程的正常输出
               BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
               List<String> outputList = new ArrayList<>();
               //逐行读取
               String compileOutputLine;
               while ((compileOutputLine = bufferedReader.readLine()) != null){
                   outputList.add(compileOutputLine);
               }
               executeMessage.setMessage(StringUtils.join(outputList, "\n"));
           }else {
               //异常退出
               System.out.println(opName + "失败，错误码：" + exitValue);
               //分批获取进程的输出
//               BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
//               List<String> outputList = new ArrayList<>();
//               //逐行读取
//               String compileOutputLine;
//               while ((compileOutputLine = bufferedReader.readLine()) != null){
//                   outputList.add(compileOutputLine);
//               }
               //分批获取进程的输出
               BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
               List<String> errorOutputList = new ArrayList<>();
               //逐行读取
               String errorCompileOutputLine;
               while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null){
                   errorOutputList.add(errorCompileOutputLine);
               }
               executeMessage.setErrorMessage(StringUtils.join(errorOutputList, "\n"));
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
