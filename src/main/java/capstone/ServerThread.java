package capstone;

import java.io.*;
import java.net.*;
import java.util.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class ServerThread implements Runnable {
    public void run() {
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();

        DynamoDB dynamoDB = new DynamoDB(client);

        //Table table = dynamoDB.getTable("pad-test");
		Table gameTable = dynamoDB.getTable("game-test");

        try {
            ServerSocket serverSocket = new ServerSocket(7777);
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
			System.out.println("Made it here 1");
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
				System.out.println("Made it here 2");
				String[] gameTemp = inputLine.split(",");

				String gameTimeStamp = gameTemp[0].trim();
				String gameEvent = gameTemp[1].trim();
				System.out.println("PutItem succeeded: " + inputLine);
                //try {
				//	gameTable.putItem(new Item()
				//	.withPrimaryKey("session", "jake-pruitt", "timestamp", gameTimeStamp)
				//	.withString("gameCode", gameEvent));
				//	System.out.println("PutItem succeeded: " + inputLine);
                //} catch (Exception e) {
				//	System.err.println("Unable to add: " + inputLine);
				//	System.err.println(e.getMessage());
                //    break;
                //}
            }
        } catch (UnknownHostException e) {
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
        }
    }
}
