package com.threshold.weightedShamir;
/* An implementation of the weighted Shamir secret sharing scheme from paper :http://www.ijtpc.org/volume13/JTPC2428.pdf*/
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;


public class WeightedShamir{

private  int total;
private  int threshold;
private  BigInteger secret;
private  HashMap<Integer, BigInteger> partyShares;
private  BigInteger prime;
private ArrayList<HashMap<Integer,BigInteger>>  indiviualShares = new ArrayList<HashMap<Integer,BigInteger>>();

public void setShamirParmaters(int total,int threshold,BigInteger secret){
	
	this.total = total;
	this.threshold = threshold;
	this.secret = secret;
	
	Random r = new Random();
	this.prime = new BigInteger(secret.bitLength()+1,60,r);
	
}

public void setPartyShares(){
	
	
	final BigInteger[] coefficentValues = new BigInteger[threshold-1];
	
	
	for (int i = 0; i < threshold - 1; i++) {
		    coefficentValues[i] = getRandom(prime);
            
    }
	
	partyShares = new HashMap<Integer, BigInteger>();
	
	 for (int i = 1; i <= total; i++) {
            BigInteger accum = secret;

            for (int j = 1; j < threshold; j++) {
                final BigInteger t1 = BigInteger.valueOf(i).modPow(BigInteger.valueOf(j), prime);
                final BigInteger t2 = coefficentValues[j - 1].multiply(t1).mod(prime);

                accum = accum.add(t2).mod(prime);
            }
            partyShares.put(i,accum);
            
        }
	
}

public void setIndiviualShares(ArrayList<Integer> shares){
	int count = 0;
		
	  for(int i=0;i<shares.size();i++){
		HashMap<Integer,BigInteger> temp = new HashMap<Integer,BigInteger>();
		int tShares = shares.get(i);
		while(tShares > 0){
			int keyValue = (int) partyShares.keySet().toArray()[count];
			BigInteger value = partyShares.get(count+1);
			temp.put(keyValue,value);
			count=count+1;
			tShares =tShares-1;
		}
		indiviualShares.add(temp);
		
	}
	
}

public void printIndiviualShares(){
	 
	for(int y=0;y<indiviualShares.size();y++){
			System.out.println("Person "+(y+1)+" has shares(shareNumber:Value)"+indiviualShares.get(y));   
	 }
	  
	
}

public HashMap<Integer, BigInteger>  getPartyShares(){
	
	return partyShares;
}

public void printPartyShares(){
	
	for (Map.Entry<Integer, BigInteger> entry : partyShares.entrySet()) {
	    System.out.println("Share number "+entry.getKey()+" : Value = "+entry.getValue());
	}
}

public BigInteger mergeShares(HashMap<Integer,BigInteger> partyCombine) {
	
	
		 BigInteger sec = BigInteger.ZERO;
		 
		 
		 for (Map.Entry<Integer, BigInteger> entry : partyCombine.entrySet()) {
			    BigInteger numerator = BigInteger.ONE;
		        BigInteger denominator = BigInteger.ONE;
		        for (Map.Entry<Integer, BigInteger> entry2 : partyCombine.entrySet()){
		        	if(entry.getKey() != entry2.getKey()){
		        		numerator = numerator.multiply(BigInteger.valueOf(0-entry2.getKey())).mod(prime);
		                denominator = denominator.multiply(BigInteger.valueOf(entry.getKey() - entry2.getKey())).mod(prime);
		        		
		        	}
		        }
		        final BigInteger value = entry.getValue();

		        final BigInteger tmp = value.multiply(numerator).multiply(denominator.modInverse(prime)).mod(prime);
		        sec = sec.add(prime).add(tmp).mod(prime);
		        
			}
		
		 
		   /* for (int i = 0; i < threshold; i++) {
		        BigInteger numerator = BigInteger.ONE;
		        BigInteger denominator = BigInteger.ONE;

		        for (int j = 0; j < threshold; j++) {
		            if (i != j) {
		                numerator = numerator.multiply(BigInteger.valueOf(-j - 1)).mod(prime);
		                denominator = denominator.multiply(BigInteger.valueOf(i - j)).mod(prime);
		            }
		        }

		        
		        final BigInteger value = partyShares.get(i+1);

		        final BigInteger tmp = value.multiply(numerator).multiply(denominator.modInverse(prime)).mod(prime);
		        sec = sec.add(prime).add(tmp).mod(prime);

		    }
            System.out.println("Secret is "+sec);*/
		    return sec;
	
   
}

private BigInteger getRandom(final BigInteger p) {
    while (true) {
    	SecureRandom sr = new SecureRandom();
        final BigInteger temp = new BigInteger(p.bitLength(), sr);
        if (temp.compareTo(BigInteger.ZERO) > 0 && temp.compareTo(p) < 0) {
            return temp;
        }
    }
}

public static void main(String args[]){
	
	Scanner sc = new Scanner(System.in);
	
	System.out.println("Enter the total number of people");
	int numOfPeople = sc.nextInt();
	while(numOfPeople <=0){
		System.out.println("Cannot have 0 or negative number.Try again!");
		numOfPeople = sc.nextInt();
	}
	
	System.out.println("Enter the number of shares that each person owns in order");
	ArrayList<Integer> shares = new ArrayList<Integer>(numOfPeople);
	

	for(int i=0;i< numOfPeople;i++){
		 int share = sc.nextInt();
		 while(share <=0){
			 System.out.println("Cannot have 0 or negative number.Try again!"); 
			 share = sc.nextInt();
		 }
		 shares.add(share);
	}
	
	int totalShares = 0;
	System.out.println("The number of shares each person has is");
	System.out.println("----------------------------------------------------------------------");
	for(int i=0;i< numOfPeople;i++){
		System.out.println("Person "+(i+1)+ "- Number of shares: "+shares.get(i));
		totalShares += shares.get(i);
	}
	System.out.println("The total number of shares is "+totalShares);
	System.out.println("----------------------------------------------------------------------");
	
	System.out.println("Enter a big integer value for the secret(positive value)");
	BigInteger secret = sc.nextBigInteger();
	
	System.out.println("Enter the threshold numer of shares to recover the secret");
	int threshold = sc.nextInt();
	while(threshold <=0 || threshold > totalShares){
		System.out.println("Invalid input:Threshold cannot be negative or greater than the total");
		threshold = sc.nextInt();
	}
	
	System.out.println("..........................Generating shares .............................");
	WeightedShamir ws = new WeightedShamir();
	ws.setShamirParmaters(totalShares,threshold,secret);	
	ws.setPartyShares();
	ws.printPartyShares();	
	System.out.println("...................Setting Indiviual Shares..............................");
	ws.setIndiviualShares(shares);
	
	System.out.println("");
	System.out.println("...................Printing Indiviual Shares.............................");
	ws.printIndiviualShares();
	
	System.out.println("");
	System.out.println("Enter the number of shares you want to combine(minimum must be "+threshold+")");
	HashMap<Integer,BigInteger> getShares = ws.getPartyShares();
	HashMap<Integer,BigInteger> sharesCombine = new HashMap<Integer,BigInteger>();
	int sharesToRecover = sc.nextInt();
	while(sharesToRecover <=0 || sharesToRecover > totalShares || sharesToRecover < threshold){
		System.out.println("Invalid number of,Enter again!");
		sharesToRecover = sc.nextInt();
	}
	System.out.println("Enter the share numbers");
	for(int i=0;i<sharesToRecover;i++){
		int shareNum = sc.nextInt();
		while(shareNum <=0 || shareNum > totalShares){
			System.out.println("Invalid share number,Enter again!");
			shareNum = sc.nextInt();
		}
		BigInteger share2 = getShares.get(shareNum);
		sharesCombine.put(shareNum, share2);
	}
	
	System.out.println("The share number and shares you have chosen to merge is");
	for (Map.Entry<Integer, BigInteger> entry : sharesCombine.entrySet()) {
	    System.out.println("Share number "+entry.getKey()+" : Value = "+entry.getValue());
	}
	BigInteger recover = ws.mergeShares(sharesCombine);
	System.out.println("Recovered secret is "+recover);
	
	sc.close();
   }

}
