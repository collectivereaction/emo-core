package capstone;

import java.io.*;
import java.net.*;
import java.util.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

/**
 * This class receives PAD values with a timestamp from the ADAS PAD through socket 7474,
 * then prompts the the user to enter the session id, which is shared between ServerThread and CalculatorThread.
 * It then starts the serverThread, and pushes the session id, P, A values with a timestamp to the database.
 *
 */
public class App
{
    public static void main( String[] args )
    {
		//intialize the Amazon DynamoDB database instance "clinet"
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("pad-test");
		
		double intervalA = 0;
		double intervalB = 0;
		
		//setup the socket hostName and port number
        String hostName = "localhost";
        int portNumber = 7474;

		//scanner to read the session id from the keyboard
		Scanner reader = new Scanner(System.in);
		String sessionId;
		
		//prompt the user
		System.out.print("Enter PAD Session ID: ");
		sessionId = reader.next();
		
		//Create a new instance of ServerThread to start the "ServerThread.java" and pass the session id to that thread
		ServerThread serverThreadInstance = new ServerThread(sessionId);
		//serverThreadInstance.setString("Hello");   //Debugging the serverThreadInstance
		Thread thr = new Thread(serverThreadInstance);
		
		//start the thread
		thr.start();
		
		//(new Thread(new ServerThread(sessionId))).start();
		
		try {
			//PAD socket
            Socket echoSocket = new Socket(hostName, portNumber);
			
			//BufferReader to read to the PAD values
            BufferedReader in =
                new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

			//an infinite loop to keep reading PAD values and upload it to the database
            while (true) {
				//Parsing the string read from ADAS PAD into P and A
                String read = in.readLine();
				String[] temp = read.split(",");

				String P = temp[1].trim();
				String A = temp[2].trim();
				
				
				CalculatorThread.appendP(P);
				CalculatorThread.appendA(A);
				
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//This section of code pushes the session id, P, A values with a timestamp to the pad-test table in the database
				try {
					table.putItem(new Item()
					.withPrimaryKey("session", sessionId, "timestamp", System.currentTimeMillis())
					.withString("P", P)
					.withString("A", A));
					
					//Output the timestamp, P and A values to the console
					System.out.println("PutItem succeeded: " + System.currentTimeMillis() + ", " + P + ", " +  A);
					
				} catch (Exception e) {
					System.err.println("Unable to add: " + read);
					System.err.println(e.getMessage());
					break;
				}
				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
