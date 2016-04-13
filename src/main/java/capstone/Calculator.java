package capstone;

import java.util.*;

public class Calculator
{	
	Double deltaDegree = 0.0;
	Double deltaLength = 0.0;
	Double playerDamagedNum = 0.0;
	Double playerScoredNum = 0.0;
	Double enemySpawnedNum = 0.0;
	
	Double c_delta_degree;
	Double c_delta_length;
	Double c_player_damaged;
	Double c_player_scored;
	
	public Calculator (Double c_delta_degree, Double c_delta_length, Double c_player_damaged, Double c_player_scored)
	{
		this.c_delta_degree = c_delta_degree;
		this.c_delta_length = c_delta_length;
		this.c_player_damaged = c_player_damaged;
		this.c_player_scored = c_player_scored;
	}
	
	public Double calculate(ArrayList<String> pVal, ArrayList<String> aVal, ArrayList<String> playerDamaged, ArrayList<String> playerScored)
	{
		Double pInitial = Double.parseDouble(pVal.get(0));
		Double pFinal = Double.parseDouble(pVal.get(pVal.size()-1));
		
		Double aInitial = Double.parseDouble(aVal.get(0));
		Double aFinal = Double.parseDouble(aVal.get(aVal.size()-1));
		
		Double thetaInitial = Math.atan2(aInitial,pInitial);
		Double thetaFinal = Math.atan2(aFinal,pFinal);
		
		deltaLength = Math.pow( (Math.pow(pFinal,2.0) + Math.pow(aFinal,2.0)) , .5 ) - Math.pow( (Math.pow(pInitial,2.0) + Math.pow(aInitial,2.0)) , .5 );
		
		deltaDegree = thetaFinal - thetaInitial;
		
		if(deltaDegree < -3.1415926)
		{
			deltaDegree += 6.2831;
		} 
		else if(deltaDegree > 3.1415926)
		{
			deltaDegree -= 6.2831;
		}
		
		playerDamagedNum = new Double(playerDamaged.size());
		playerScoredNum = new Double(playerScored.size());
		
		return ( c_delta_degree * deltaDegree +  c_delta_length * deltaLength +  c_player_damaged * playerDamagedNum + c_player_scored * playerScoredNum );
	}
}