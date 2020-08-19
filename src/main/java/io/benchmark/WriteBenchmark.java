package io.benchmark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 *
 * @author 莫那·鲁道
 * @date 2019-05-12-16:09
 */
public class WriteBenchmark {

    static long fileSize = 1024 * 1024 * 1024;

    static int arrLen = 0;

    // 32, 64, 128, 512,
    static int[] lenArr = {1024, 2048, 4096, 8192, 16384, 32768, 524288, 134217728};

    public static void main(String[] args) throws Exception {
        for (int i : lenArr) {
            System.out.println("============================== length : " + i);
            arrLen = i;
            FileChannelBM.write();
//            RandomAccessFileBM.write();
//            FileOutputStreamBM.write();
            MmapBM.write();
        }
    }

    static class MmapBM {

        static void write() throws IOException {

            MappedByteBuffer mb = FileUtil.getMappedByteBuffer();

            byte[] arr = new byte[arrLen];
            Arrays.fill(arr, (byte) 2);
            int length = 0;
            long s = System.currentTimeMillis();
            while (length < mb.capacity()) {
                length += arr.length;
                mb.put(arr);
            }
            // 测试 force, 纯粹写测试时,应该关闭,对性能影响很大
//            long s1 = System.currentTimeMillis();
//            mb.force();
//            long s2 = System.currentTimeMillis();
//            System.out.println("force : " + (s2 - s1));
            long e = System.currentTimeMillis();
            System.out.print("MappedByteBuffer cost : " + (e - s) + "ms");
            mb.force();
            System.out.println(", force time: " + (System.currentTimeMillis() - e) + "ms");
        }
    }

    static class FileChannelBM {

        static void write() throws IOException {
            try (FileChannel fc = FileUtil.getFileChannel(true)) {

                byte[] arr = new byte[arrLen];
                Arrays.fill(arr, (byte) 2);

                int length = 0;
                long s = System.currentTimeMillis();
                ByteBuffer b = ByteBuffer.allocate(arrLen);
                b.put(arr);
                while (length < fileSize) {
                    length += arr.length;
                    fc.write(b);
                    b.flip();
                }
                long e = System.currentTimeMillis();
                System.out.println("FileChannel cost : " + (e - s) + "ms");
            }
        }
    }

    static class RandomAccessFileBM {

        static void write() throws IOException {

            try (RandomAccessFile ra = FileUtil.getRandomAccessFile(true)) {

                byte[] arr = new byte[arrLen];
                Arrays.fill(arr, (byte) 2);
                long s = System.currentTimeMillis();
                int length = 0;
                while (length < fileSize) {
                    length += arr.length;
                    ra.write(arr);
                }

                long e = System.currentTimeMillis();
                System.out.println("RandomAccessFile cost : " + (e - s) + "ms");
            }
        }
    }

    static class FileOutputStreamBM {

        static void write() throws IOException {
            File file = FileUtil.getRandomFile();

            try (FileOutputStream fo = new FileOutputStream(file)) {

                byte[] arr = new byte[arrLen];
                Arrays.fill(arr, (byte) 2);
                long s = System.currentTimeMillis();
                int length = 0;
                while (length < fileSize) {
                    length += arr.length;
                    fo.write(arr);
                }
                long e = System.currentTimeMillis();
                System.out.println("FileOutputStream cost : " + (e - s) + "ms");
            }
        }
    }
}
