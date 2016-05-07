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
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/*
* This thread is responsible for handling the creation of the calculator objects
* that determine the values sent to the game associated with different game changes.
*/

public class CalculatorThread implements Runnable   {

	//Arrays to store the P, A, playerDamaged, and PlayerScored data for the last 5 seconds
	static ArrayList<String> pVal = new ArrayList<>();
	static ArrayList<String> aVal = new ArrayList<>();
	static ArrayList<String> playerDamaged = new ArrayList<>();
	static ArrayList<String> playerScored = new ArrayList<>();
	

	Socket gameSocket;
	String sessionID;
	
	double nextStop = 0;

	//Consturctor to set the session id and clinetSocket instance
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
		
		//System.out.println("Made it here?");   //Debugging 
        try {

			out = new BufferedWriter (new OutputStreamWriter(gameSocket.getOutputStream()));
			
			//Set the time interval to 5 seconds
			nextStop = System.currentTimeMillis() + 5000;
            
			//Pull and print the model-test table data from the database
			ScanRequest scanRequest = new ScanRequest().withTableName("model-test");
			ScanResult result = client.scan(scanRequest);
			result.getItems().stream().forEach(System.out::println);
			
			//Randomly generating coeffecient for the calculators
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
			
			Double c_delta_degree_enemySpeed = 4.0*random();
			Double c_delta_length_enemySpeed = 4.0*random();
			Double c_player_damaged_enemySpeed = random();
			Double c_player_scored_enemySpeed = random();
			
			Double c_delta_degree_playerDamage = 2.0*random();
			Double c_delta_length_playerDamage = 2.0*random();
			Double c_player_damaged_playerDamage = random();
			Double c_player_scored_playerDamage = random();
			
			//Creating new instances of the calculators for healthChange, SpeedChange, EnemySpawnRateChange, EnemySpeedChange, and PlayerDamageChange
			Calculator calculateHealthChange = new Calculator(c_delta_degree_health, c_delta_length_health, c_player_damaged_health, c_player_scored_health);
			Calculator calculateSpeedChange = new Calculator(c_delta_degree_speed, c_delta_length_speed, c_player_damaged_speed, c_player_scored_speed);
			Calculator calculateEnemySpawnRateChange = new Calculator (c_delta_degree_enemySpawnRate, c_delta_length_enemySpawnRate, c_player_damaged_enemySpawnRate, c_player_scored_enemySpawnRate);
			Calculator calculateEnemySpeedChange = new Calculator (c_delta_degree_enemySpeed, c_delta_length_enemySpeed, c_player_damaged_enemySpeed, c_player_scored_enemySpeed);
			Calculator calculatePlayerDamageChange = new Calculator (c_delta_degree_playerDamage, c_delta_length_playerDamage, c_player_damaged_playerDamage, c_player_scored_playerDamage);
			
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			//This section of code pushes the session id and the coeffecients to the model-test table in the database
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
				
				//.withNumber("c_delta_degree_enemySpawnRate", c_delta_degree_enemySpawnRate)
				//.withNumber("c_delta_length_enemySpawnRate", c_delta_length_enemySpawnRate)
				//.withNumber("c_player_damaged_enemySpawnRate", c_player_damaged_enemySpawnRate)
				//.withNumber("c_player_scored_enemySpawnRate", c_player_scored_enemySpawnRate) 
				
				.withNumber("c_delta_degree_enemySpeed", c_delta_degree_enemySpeed)
				.withNumber("c_delta_length_enemySpeed", c_delta_length_enemySpeed)
				.withNumber("c_player_damaged_enemySpeed", c_player_damaged_enemySpeed)
				.withNumber("c_player_scored_enemySpeed", c_player_scored_enemySpeed)
				
				.withNumber("c_delta_degree_playerDamage", c_delta_degree_playerDamage)
				.withNumber("c_delta_length_playerDamage", c_delta_length_playerDamage)
				.withNumber("c_player_damaged_playerDamage", c_player_damaged_playerDamage)
				.withNumber("c_player_scored_playerDamage", c_player_scored_playerDamage) );
				
				
				
            } catch (Exception e) {
				System.err.println(e.getMessage());
            }
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			//This loop requests calculations from the calculators based on the collection of P, A, etc... and then sends changes to the game through a socket.
            while (true) {
				

				if (System.currentTimeMillis() > nextStop)
				{
					try {
						//Calculate
						Double healthChange = calculateHealthChange.calculate(pVal, aVal, playerDamaged, playerScored);
					
						Double speedChange = calculateSpeedChange.calculate(pVal, aVal, playerDamaged, playerScored);
						
						//Spawn rate needs further debugging in the game side
						//Double enemySpawnRateChange = calculateEnemySpawnRateChange.calculate(pVal, aVal, playerDamaged, playerScored);
						
						Double enemySpeedChange = calculateEnemySpeedChange.calculate(pVal, aVal, playerDamaged, playerScored);
						
						Double playerDamageChange = calculatePlayerDamageChange.calculate(pVal, aVal, playerDamaged, playerScored);
						
						//Pushing the game and clearing the pVal, aVal, playerDamaged and playerScored arrays
						pVal.clear();
						aVal.clear();
						playerDamaged.clear();
						playerScored.clear();
						nextStop = System.currentTimeMillis() + 5000;
						String outputLine1 = "PH " + healthChange + "\n\r";
						String outputLine2 = "PS " + speedChange + "\n\r";
						//String outputLine3 = "ESR " + enemySpawnRateChange + "\n\r";
						String outputLine4 = "ES " + enemySpeedChange + "\n\r";
						String outputLine5 = "PD " + playerDamageChange + "\n\r";
						out.write(outputLine1);
						out.flush();
						Thread.sleep(10);   //A small delay is needed in between for the socket to send different game commands
						out.write(outputLine2);
						out.flush();
						Thread.sleep(10);
						//out.write(outputLine3);
						//out.flush();
						//Thread.sleep(10);
						out.write(outputLine4);
						out.flush();
						Thread.sleep(10);
						out.write(outputLine5);
						out.flush();
						Thread.sleep(10);
						//System.out.println("Sending to the socket");
					} catch (Exception e) {
						System.out.println(e.toString());
					}
					
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
	//Methods called by the serverThread to append to the arrays
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