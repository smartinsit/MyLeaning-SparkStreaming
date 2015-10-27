package chapter.four;

import tachyon.client.FileOutStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

/**
 * Created by smartins on 10/26/15.
 */
public class SampleLogGenerator {
    public static void main (String[] args) {
        try {
            if (args.length != 2) {
                System.out.println("Usage - java SampleLogGenerator <logcation of the log file to be read> <Location of the files in which the logs need to be updated>");
                System.exit(0);
            }
            String location = args[0];

            File f = new File(location);
            FileOutputStream writer = new FileOutputStream(f);

            File read = new File(args[1]);
            BufferedReader reader = new BufferedReader(new FileReader(read));

            for (;;) {
                writer.write((reader.readLine() + "\n").getBytes());
                writer.flush();
                Thread.sleep(500);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
