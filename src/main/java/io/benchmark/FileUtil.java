package io.benchmark;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.UUID;

/**
 *
 * @author 莫那·鲁道
 * @date 2019-05-12-12:42
 */
public class FileUtil {

    static File getRandomFile() {
        String fileName = UUID.randomUUID().toString();

        File file = new File(fileName);
        file.deleteOnExit();
        return file;
    }

    static RandomAccessFile getRandomAccessFile(boolean isWrite) throws IOException {
        File file;
        if (isWrite) {
            file = FileUtil.getRandomFile();
        } else {
            file = FileUtil.getAlreadyFillFile();
        }
        return new RandomAccessFile(file, "rw");
    }

    static FileChannel getFileChannel(boolean isWrite) throws IOException {
        File file;
        if (isWrite) {
            file = FileUtil.getRandomFile();
        } else {
            file = FileUtil.getAlreadyFillFile();
        }
        return new RandomAccessFile(file, "rw").getChannel();
    }

    static MappedByteBuffer getMappedByteBuffer() throws IOException {
        File file = FileUtil.getRandomFile();
        return new RandomAccessFile(file, "rw").getChannel().
            map(FileChannel.MapMode.READ_WRITE, 0, 1024 * 1024 * 1024);
    }

    static File getAlreadyFillFile() throws IOException {
        File file = getRandomFile();
        FileOutputStream fo = new FileOutputStream(file);
        byte[] arr = new byte[1024 * 1024 * 1024];
        Arrays.fill(arr, (byte) 1);
        fo.write(arr);
        return file;
    }

    static File getAlreadyFillFile(int size) throws IOException {
        File file = getRandomFile();
        FileOutputStream fo = new FileOutputStream(file);
        byte[] arr = new byte[size];
        Arrays.fill(arr, (byte) 1);
        fo.write(arr);
        return file;
    }
}
