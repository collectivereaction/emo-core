package capstone;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.io.*;
import java.net.*;
import java.util.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

/**
 * This thread is started by the "App.java" class.
 * This class takes in the game states sent by the game, starts the CalculatorThread and passes the session id and client socket instance to it 
	to maintain one instance of the client socket.
 * It then pushes the game states to the database with a timestamp.
 *
 */

public class ServerThread implements Runnable   {
	
	//session id
	String gameSessionID;
	
	//constructor to set the session id
	public ServerThread (String str)
	{
		gameSessionID = str;
	}
    public void run() {
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();

        DynamoDB dynamoDB = new DynamoDB(client);

		Table gameTable = dynamoDB.getTable("game-test");
		final BufferedWriter out;
		
		//System.out.println("Made it here?");    //Debugging
		
        try {
			//create a new instance of the ServerSocket with port number 7777
            ServerSocket serverSocket = new ServerSocket(7777);
			
			//Wait for the severSocket to accept the connection
            Socket clientSocket = serverSocket.accept();
			
			//System.out.println("Made it here?");    //Debugging
			
			//initialize the calculator thread and then start it
			CalculatorThread calculatorThreadInstance = new CalculatorThread(gameSessionID, clientSocket);
			Thread thr = new Thread(calculatorThreadInstance);
			thr.start();
			
			
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
			
			out = new BufferedWriter (new OutputStreamWriter(clientSocket.getOutputStream()));

            while (true) {
				//Parse the string sent by the game in to game state and timestamp
				inputLine = in.readLine();
		
				String[] gameTemp = inputLine.split(",");
				
				String gameTimeStamp = gameTemp[0].trim();
				long l = Long.parseLong(gameTimeStamp);
				String gameEvent = gameTemp[1].trim();
				System.out.println("PutItem succeeded: " + inputLine);
				
				//Append the Player damaged array in the Calculator thread
				if (gameEvent.equals("Player Damaged"))
				{
					CalculatorThread.appendPlayerDamaged(gameEvent);
				}
				//Append the player scored array in the Calculator thread
				else if(gameEvent.equals("Player Scored"))
				{
					CalculatorThread.appendPlayerScored(gameEvent);
				}

				///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
				//This section of code pushes the session id and game states with a timestamp to the game-test table in the database
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
				/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            }
        } catch (UnknownHostException e) {
            System.exit(1);
        } catch (IOException e) {
			System.err.println(e.toString());
            System.err.println("Couldn't get I/O for the connection");
            System.exit(1);
        }
    }
	

	
}
