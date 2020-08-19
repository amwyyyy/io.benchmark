package io.benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 *
 * @author 莫那·鲁道
 * @date 2019-05-12-12:41
 */
public class ReadBenchmark {

    static int arrLen = 0;

    static int[] lenArr = {32, 64, 128, 512, 1024, 2048, 4096, 8192, 16384, 32768, 524288, 134217728};

    public static void main(String[] args) throws Exception {

        for (int i : lenArr) {
            System.out.println("============================== length : " + i);
            arrLen = i;
            FileChannelBM.read();
            RandomAccessFileBM.read();
            FileInput.read();
            MmapBM.read();
        }
    }

    static class FileInput {

        static void read() throws IOException {
            File file = FileUtil.getAlreadyFillFile();

            try (FileInputStream fs = new FileInputStream(file)) {

                long s = System.currentTimeMillis();
                while (true) {
                    byte[] arr = new byte[arrLen];
                    int len = fs.read(arr);
                    if (len == -1) {
                        break;
                    }
                }
                long e = System.currentTimeMillis();
                System.out.println("FileInputStream cost : " + (e - s) + "ms");
            }
        }
    }

    static class MmapBM {

        static void read() throws IOException {
            final MappedByteBuffer mb = FileUtil.getMappedByteBuffer();

            byte[] arr = new byte[arrLen];
            long s = System.currentTimeMillis();
            while (mb.hasRemaining()) {
                mb.get(arr);
                arr = new byte[arrLen];
            }
            long e = System.currentTimeMillis();
            System.out.println("mmp cost : " + (e - s) + "ms");
        }
    }

    static class RandomAccessFileBM {

        static void read() throws IOException {
            try (RandomAccessFile ra = FileUtil.getRandomAccessFile(false)) {
                long s = System.currentTimeMillis();
                while (true) {
                    byte[] arr = new byte[arrLen];
                    int len = ra.read(arr);
                    if (len == -1) {
                        break;
                    }
                }
                long e = System.currentTimeMillis();
                System.out.println("RandomAccessFile cost : " + (e - s) + "ms");
            }
        }
    }

    static class FileChannelBM {

        static void read() throws Exception {

            try (FileChannel fc = FileUtil.getFileChannel(false)) {

                ByteBuffer byteBuffer = ByteBuffer.allocate(arrLen);

                long s = System.currentTimeMillis();
                while (true) {
                    int len = fc.read(byteBuffer);
                    byteBuffer.clear();
                    if (len == -1) {
                        break;
                    }
                }
                long e = System.currentTimeMillis();
                System.out.println("fileChannel cost : " + (e - s) + "ms");
            }
        }
    }
}
