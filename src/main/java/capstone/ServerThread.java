package capstone;

import java.io.*;
import java.net.*;
import java.util.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class ServerThread implements Runnable {
	
	String gameSessionID;
	String testStr = "---------------";
	
	public ServerThread (String str)
	{
		gameSessionID = str;
	}
    public void run() {
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();

        DynamoDB dynamoDB = new DynamoDB(client);

		Table gameTable = dynamoDB.getTable("game-test");
		
		
		//System.out.println(gameSessionID);
		//System.out.println(testStr);
		
        try {
            ServerSocket serverSocket = new ServerSocket(7777);
            Socket clientSocket = serverSocket.accept();
			System.out.println("Made it here?");
			
			//clientSocket.getOutputStream().write("Change Game".getBytes("UTF-8"));
			
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
			
			BufferedWriter out = new BufferedWriter (new OutputStreamWriter(clientSocket.getOutputStream()));
			String outputLine = "Hello World\n\r";
			
            while (true) {
				
				out.write(outputLine);
				System.out.println("Sending to the socket");
				
				/*inputLine = in.readLine();
				

				String[] gameTemp = inputLine.split(",");
				
				String gameTimeStamp = gameTemp[0].trim();
				long l = Long.parseLong(gameTimeStamp);
				String gameEvent = gameTemp[1].trim();
				System.out.println("PutItem succeeded: " + inputLine);
				
                try {
					gameTable.putItem(new Item()
					.withPrimaryKey("id", gameSessionID, "time", l)
					.withString("event", gameEvent));
					System.out.println("PutItem succeeded: " + inputLine);
                } catch (Exception e) {
					System.err.println("Unable to add: " + inputLine);
					System.err.println(e.getMessage());
                    break;
                }
				*/
            }
        } catch (UnknownHostException e) {
            System.exit(1);
        } catch (IOException e) {
			System.err.println(e.toString());
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
        }
    }
	
	public void setString(String str)
	{
		testStr = str;
	}
}
