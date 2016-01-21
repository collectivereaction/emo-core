package capstone;

import java.io.*;
import java.net.*;
import java.util.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("pad-test");
 
        String hostName = "localhost";
        int portNumber = 7474;
 
		try {
            Socket echoSocket = new Socket(hostName, portNumber);
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(echoSocket.getInputStream()));
            while (true) {
                String read = in.readLine();
				String[] temp = read.split(",");
				
				String P = temp[1].trim();
				String A = temp[2].trim();
				
				try {
					table.putItem(new Item()
					.withPrimaryKey("session", "test", "timestamp", (new Date()).toString())
					.withString("P", P)
					.withString("A", A));
					System.out.println("PutItem succeeded: " + read);

				} catch (Exception e) {
					System.err.println("Unable to add movie: " + read);
					System.err.println(e.getMessage());
					break;
				}
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } 
    }
}
