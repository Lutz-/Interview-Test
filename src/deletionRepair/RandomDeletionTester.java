package deletionRepair;

import java.util.ArrayList;

public class RandomDeletionTester {

																	/**
																	 * This version of the tester generates a random 16BitStream and deletion position and tests to see if the Encoder
																	 * can repair the deletion. It displays the original generated bitstream, the two encoding vectors, the decimal values
																	 * of each of the four sections of the original bitstream, the two derived parities, the corrupted bitstream, and the 
																	 * specific type of success or failure of the repair.
																	 */
	
	
	public static void main(String[] args) {
		
		Encoder encoder = new Encoder();													//The initialization of the encoder
																							
		
																							//This block generates, stores, and displays the random bitstream
		ArrayList<Integer> BitStream = new ArrayList<Integer>();
		for(int i=0;i<16;i++) {
			if(Math.random()>0.5)
				BitStream.add(1);
			else
				BitStream.add(0);
		}
		System.out.println(BitStream.toString() + "<-original bitstream");					
																							//end block
																							
																							//This block generates, stores, and displays the encoding vectors being used
		ArrayList<Integer> eVector1 = new ArrayList<Integer>();
		ArrayList<Integer> eVector2 = new ArrayList<Integer>();
		for(int i=0;i<4;i++) {
			eVector1.add(1);
			eVector2.add(i*2);
		}
		System.out.println(eVector1.toString() + "<- encoding vector 1");
		System.out.println(eVector2.toString() + "<- encoding vector 2");
																							//end block
		
		encoder.CalculateParities(BitStream, eVector1, eVector2);							//This line calculates the parities and decimal values of the original vector
		
																							//This block displays the parities and decimal values calculated
		System.out.println(encoder.getDECIMALBRICKS()+" <-original decimal bricks");
		System.out.println(encoder.getPARITY1() +" <-Parity 1");
		System.out.println(encoder.getPARITY2() +" <-Parity 2");
																							//end block

																							//This block generates the random index to be deleted, deletes it from bitstream, and displays the resulting corrupt bitstream
		Double removalIndex =Math.random()*16;
		BitStream.remove(removalIndex.intValue());
		System.out.println(BitStream.toString() + "<-corrupted bitstream");
																							//end block
		
																							//This block initializes the outputs of our four cases
		ArrayList<Integer> output1 = null;
		ArrayList<Integer> output2 = null;
		ArrayList<Integer> output3 = null;
		ArrayList<Integer> output4 = null;
																							//end block
		
																							//This block iterates through four cases of repair, where each case treats one of the four parts
																							//of bitStream as broken and tries to repair that part using the algorithm in RepairStreamCase
																							//the result of the repair is then put in its respective output variable
		for(int i=0;i<4;i++) {
			ArrayList<Integer> CaseOutput = encoder.RepairStreamCase(i, BitStream, encoder.getENCODINGVECTOR1(), encoder.getENCODINGVECTOR2(), encoder.getPARITY1(), encoder.getPARITY2());
			if(i==0)
				output1=CaseOutput;
			if(i==1)
				output2=CaseOutput;
			if(i==2)
				output3=CaseOutput;
			else
				output4=CaseOutput;
		}
																							//end block
		
																							//This block filters the outputs for success, with null outputs ignored and successful outputs
																							//added to an array to be compared to one another
		ArrayList<ArrayList<Integer>> Comparables = new ArrayList<ArrayList<Integer>>();
		if(output1!=null)
			Comparables.add(output1);
		if(output2!=null)
			Comparables.add(output2);
		if(output3!=null)
			Comparables.add(output3);
		if(output4!=null)
			Comparables.add(output4);
																							//end block
		
		
																							//This block checks the results in comparables and outputs the respective success/failure notification
		if(Comparables.size()==1)
			System.out.println("Success with one correct case: "+Comparables.get(0));
		else if(Comparables.size()==2)
			if(Comparables.get(0).containsAll(Comparables.get(1)))
				System.out.println("Success with two correct cases: "+Comparables.get(0));
			else
				System.out.println("Failed, 2 nonmatching arrays");
		else if(Comparables.size()==3)
			if(Comparables.get(0).containsAll(Comparables.get(1))&&Comparables.get(0).containsAll(Comparables.get(2)))
				System.out.println("Success with three correct cases: "+Comparables.get(0));
			else
				System.out.println("Failed, 3 nonmatching arrays");
		else if(Comparables.size()==4)
			if(Comparables.get(0).containsAll(Comparables.get(1))&&Comparables.get(0).containsAll(Comparables.get(2))&&Comparables.get(0).containsAll(Comparables.get(3)))
				System.out.println("Success with four correct cases: "+Comparables.get(0));
			else
				System.out.println("Failed, 4 nonmatching arrays");
		else 
			System.out.println("Failed, no cases returned a satisfactory array");
																							//end block
		
		
		
	}

}
