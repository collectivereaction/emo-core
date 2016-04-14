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
	

	Socket gameSocket;
	String sessionID;
	
	double nextStop = 0;
	
	public CalculatorThread (String str, Socket socket)
	{
		sessionID = str;
		
		gameSocket = socket;
		
	}
    public void run() {
		AmazonDynamoDBClient client = new AmazonDynamoDBClient();

        DynamoDB dynamoDB = new DynamoDB(client);

		Table modelTable = dynamoDB.getTable("model-test");
		final BufferedWriter out;
		
		//System.out.println(sessionID);
		//System.out.println(testStr);
		System.out.println("Made it here?");
        try {

			out = new BufferedWriter (new OutputStreamWriter(gameSocket.getOutputStream()));
			//String outputLine = "PH-\n\r";
		

			//clientSocket.getOutputStream().write("Change Game".getBytes("UTF-8"));
			
			nextStop = System.currentTimeMillis() + 5000;
            

			
			Double c_delta_degree_health = 2.0*random();
			Double c_delta_length_health = 2.0*random();
			Double c_player_damaged_health = random();
			Double c_player_scored_health = random();
			
			Double c_delta_degree_speed = 2.0*random();
			Double c_delta_length_speed = 2.0*random();
			Double c_player_damaged_speed = random();
			Double c_player_scored_speed = random();
			
			Double c_delta_degree_enemySpawnRate = 2.0*random();
			Double c_delta_length_enemySpawnRate = 2.0*random();
			Double c_player_damaged_enemySpawnRate = random();
			Double c_player_scored_enemySpawnRate = random();
			
			Calculator calculateHealthChange = new Calculator(c_delta_degree_health, c_delta_length_health, c_player_damaged_health, c_player_scored_health);
			Calculator calculateSpeedChange = new Calculator(c_delta_degree_speed, c_delta_length_speed, c_player_damaged_speed, c_player_scored_speed);
			Calculator calculateESRChange = new Calculator (c_delta_degree_enemySpawnRate, c_delta_length_enemySpawnRate, c_player_damaged_enemySpawnRate, c_player_scored_enemySpawnRate);
			try {
				modelTable.putItem(new Item()
				.withPrimaryKey("id", sessionID)
				.withNumber("c_delta_degree_health", c_delta_degree_health)
				.withNumber("c_delta_length_health", c_delta_length_health)
				.withNumber("c_player_damaged_health", c_player_damaged_health)
				.withNumber("c_player_scored_health", c_player_scored_health)
				
				.withNumber("c_delta_degree_speed", c_delta_degree_speed)
				.withNumber("c_delta_length_speed", c_delta_length_speed)
				.withNumber("c_player_damaged_speed", c_player_damaged_speed)
				.withNumber("c_player_scored_speed", c_player_scored_speed)
				
				.withNumber("c_delta_degree_enemySpawnRate", c_delta_degree_enemySpawnRate)
				.withNumber("c_delta_length_enemySpawnRate", c_delta_length_enemySpawnRate)
				.withNumber("c_player_damaged_enemySpawnRate", c_player_damaged_enemySpawnRate)
				.withNumber("c_player_scored_enemySpawnRate", c_player_scored_enemySpawnRate) );
				
				
				
            } catch (Exception e) {
				System.err.println(e.getMessage());
            }
			
            while (true) {
				

				if (System.currentTimeMillis() > nextStop)
				{
					
					Double healthChange = calculateHealthChange.calculate(pVal, aVal, playerDamaged, playerScored);
					
					
					Double speedChange = calculateSpeedChange.calculate(pVal, aVal, playerDamaged, playerScored);
					
					Double enemySpawnRateChange = calculateESRChange.calculate(pVal, aVal, playerDamaged, playerScored);
					
					System.out.println("hello calculator");
					pVal.clear();
					aVal.clear();
					playerDamaged.clear();
					playerScored.clear();
					nextStop = System.currentTimeMillis() + 5000;
					out.flush();
					String outputLine2 = "PS " + speedChange + "\n\r";
					String outputLine1 = "PH " + healthChange + "\n\r";
					String outputLine3 = "ESR " + enemySpawnRateChange + "\n\r";
					out.write(outputLine1);
					out.newLine();
					out.flush();
					out.write(outputLine2);
					out.newLine();
					out.flush();
					out.write(outputLine3);
					out.newLine();
					out.flush();
					System.out.println("\nenemySpawnRateChange :" + enemySpawnRateChange);
					System.out.println("Sending to the socket");
				}

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
	
	public double random()
	{
		Random rand = new Random();
		return rand.nextFloat() * 4.0 - 2.0;
	}
}