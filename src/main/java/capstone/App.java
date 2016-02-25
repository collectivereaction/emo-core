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

		Scanner reader = new Scanner(System.in);
		String sessionId;
		
		System.out.print("Enter PAD Session ID: ");
		sessionId = reader.next();
		
		ServerThread serverThreadInstance = new ServerThread(sessionId);
		//serverThreadInstance.setString("Hello");
		Thread thr = new Thread(serverThreadInstance);
		thr.start();
		
		//(new Thread(new ServerThread(sessionId))).start();
		
		try {
			//PAD socket
            Socket echoSocket = new Socket(hostName, portNumber);
            BufferedReader in =
                new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
				

			
            while (true) {
                String read = in.readLine();
				String[] temp = read.split(",");

				String P = temp[1].trim();
				String A = temp[2].trim();
				

				try {

					table.putItem(new Item()
					.withPrimaryKey("session", sessionId, "timestamp", System.currentTimeMillis())
					.withString("P", P)
					.withString("A", A));
					System.out.println("PutItem succeeded: " + System.currentTimeMillis() + ", " + P + ", " +  A);
					

				} catch (Exception e) {
					System.err.println("Unable to add: " + read);
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
