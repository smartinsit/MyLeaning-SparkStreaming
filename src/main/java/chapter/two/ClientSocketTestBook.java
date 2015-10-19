package chapter.two;

import java.io.*;
import java.net.*;

/**
 * Created by smartins on 10/18/15.
 * Test client from the book
 */

public class ClientSocketTestBook {

    public static void main(String[] args) {
        try{
            //Creating
            Socket soc = new Socket("127.0.0.1",9087);
            InputStream inputStream = soc.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(inputStream));

            while(true){
                System.out.println("Waiting for the data from server");
                String data = read.readLine();
                System.out.println("Socket workign fine - Here is data recived - "+data);
            }

        }catch(Exception e ){
            e.printStackTrace();
        }


    }

}

