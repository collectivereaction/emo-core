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

public class CalculatorThread implements Runnable   {

	static ArrayList<String> pVal = new ArrayList<>();
	static ArrayList<String> aVal = new ArrayList<>();
	static ArrayList<String> playerDamaged = new ArrayList<>();
	static ArrayList<String> playerScored = new ArrayList<>();
	static ArrayList<String> enemySpawned = new ArrayList<>();
	
	
	Socket gameSocket;
	String gameSessionID;
	
	double nextStop = 0;
	
	public CalculatorThread (String str, Socket socket)
	{
		gameSessionID = str;
		
		gameSocket = socket;
		
	}
    public void run() {
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();

        DynamoDB dynamoDB = new DynamoDB(client);

		Table gameTable = dynamoDB.getTable("game-test");
		final BufferedWriter out;
		
		//System.out.println(gameSessionID);
		//System.out.println(testStr);
		System.out.println("Made it here?");
        try {

			out = new BufferedWriter (new OutputStreamWriter(gameSocket.getOutputStream()));
			//String outputLine = "PH-\n\r";
		

			//clientSocket.getOutputStream().write("Change Game".getBytes("UTF-8"));
			
			nextStop = System.currentTimeMillis() + 5000;
            
            while (true) {
				
				
				
				if (System.currentTimeMillis() > nextStop)
				{
					Calculator calculateHealthChange = new Calculator(random(),random(),random(),random(),random());
					Double healthChange = calculateHealthChange.calculate(pVal, aVal, playerDamaged, playerScored, enemySpawned);
					
					Calculator calculateSpeedChange = new Calculator(random(),random(),random(),random(),random());
					Double speedChange = calculateSpeedChange.calculate(pVal, aVal, playerDamaged, playerScored, enemySpawned);
					
					System.out.println("hello calculator");
					pVal.clear();
					aVal.clear();
					playerDamaged.clear();
					playerScored.clear();
					enemySpawned.clear();
					nextStop = System.currentTimeMillis() + 5000;
					out.flush();
					String outputLine2 = "PS " + speedChange + "\n\r";
					String outputLine1 = "PH " + healthChange + "\n\r";
					out.write(outputLine1);
					out.flush();
					out.write(outputLine2);
					out.flush();
					System.out.println("Sending to the socket");
				}

/*
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
	
	public static void appendP(String str)
	{
		pVal.add(str);
	}
	
	public static void appendA(String str)
	{
		aVal.add(str);
	}
	public static void appendPlayerDamaged(String str)
	{
		playerDamaged.add(str);
	}
	public static void appendPlayerScored(String str)
	{
		playerScored.add(str);
	}
	public static void appendEnemySpawned(String str)
	{
		enemySpawned.add(str);
	}
	
	public double random()
	{
		Random rand = new Random();
		return rand.nextFloat() * 4.0 - 2.0;
	}
}