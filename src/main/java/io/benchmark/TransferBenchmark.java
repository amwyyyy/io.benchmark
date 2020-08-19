package io.benchmark;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

/**
 * @author denis.huang
 * @since 2020/6/22 14:01
 */
public class TransferBenchmark {
  static int[] lenArr = {8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824};

  static int arrLen = 0;

  public static void main(String[] args) throws IOException {
    for (int i : lenArr) {
      System.out.println("============================== length : " + i);
      arrLen = i;

      InputStreamTransfer.transfer();
      FileChannelTransfer.transfer();
      FilesTransfer.transfer();
    }
  }

  static class InputStreamTransfer {
    static void transfer() throws IOException {
      File file = FileUtil.getAlreadyFillFile(arrLen);
      InputStream is = new FileInputStream(file);
      OutputStream os = new FileOutputStream(FileUtil.getRandomFile());
      long s = System.currentTimeMillis();
      byte[] buf = new byte[4096];
      int bytesRead;
      while ((bytesRead = is.read(buf)) > 0) {
        os.write(buf, 0, bytesRead);
      }
      long e = System.currentTimeMillis();
      System.out.println("InputStreamTransfer cost : " + (e - s) + "ms");
      os.close();
      is.close();
    }
  }

  static class FileChannelTransfer {
    static void transfer() throws IOException {
      File file = FileUtil.getAlreadyFillFile(arrLen);
      FileChannel inputFc = new FileInputStream(file).getChannel();
      FileChannel outputFc = new FileOutputStream(FileUtil.getRandomFile()).getChannel();
      long s = System.currentTimeMillis();
      outputFc.transferFrom(inputFc, 0, inputFc.size());
      long e = System.currentTimeMillis();
      System.out.println("FileChannelTransfer cost : " + (e - s) + "ms");
      outputFc.close();
      inputFc.close();
    }
  }

  static class FilesTransfer {
    static void transfer() throws IOException {
      File in = FileUtil.getAlreadyFillFile(arrLen);
      File out = FileUtil.getRandomFile();
      long s = System.currentTimeMillis();
      Files.copy(in.toPath(), out.toPath());
      long e = System.currentTimeMillis();
      System.out.println("FilesTransfer cost : " + (e - s) + "ms");
    }
  }
}
