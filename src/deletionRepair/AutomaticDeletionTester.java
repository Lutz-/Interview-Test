package deletionRepair;

import java.util.ArrayList;

public class AutomaticDeletionTester {
	
	
																	/**
																	 * This version of the tester systematically creates every possible combination of 16 bits and tests each of them with 
																	 * every possible deletion that could occur inside of them. For each combination/deletion combo it determines whether
																	 * the corrupted 16 bit code was either:
																	 * A) restored properly ---------------------------------------------- AKA "Successful Case"
																	 * B) restored improperly -------------------------------------------- AKA "False Success"
																	 * C) unable to be restored due to multiple conflicting solutions ---- AKA "Failed Case"
																	 * D) unable to be restored due to all cases not returning solutions - AKA "Critical Failure"
																	 * It then counts up the number of combinations/deletions that arrive at each case and displays them 
																	 */
	
	
	public static void main(String[] args) {
		
																													//This block is the initialization of the BitStream array and the counters for each case
		ArrayList<Integer> BitStream = new ArrayList<Integer>();
		int numFailedCases=0;
		int numFalseSuccesses=0;
		int numSuccessfulCases=0;
		int numCriticalFailures=0;
																													//end of block

		
																													//This block creates every possible 16 bit combination as an array and then adds that array
																													//to a containing array called binaries
		int nBits = 16;
		
		int maxNumber = 1 << nBits; //this equals 2^nBits or in java: Math.pow(2,nbits)
		
		ArrayList<ArrayList<Integer>> binaries = new ArrayList<>();
		
		for (int i = 0; i < maxNumber; i++) {
			
			ArrayList<Integer> binary = new ArrayList<Integer>();
			
			for(int c=0;c<Integer.toBinaryString(i).length();c++) {
				binary.add(Integer.parseInt(Integer.toBinaryString(i).substring(c,c+1)));
			}
			
			while (binary.size() != nBits) {
				binary.add(0, 0);
			}
			
			binaries.add(binary);
		}
																													//end of block
		
		
		for(int removalIndex=0;removalIndex<16;removalIndex++) {													//This overarching for loop iterates over every possible deletion that can occur in a 16 bit binary string
																													
			for(ArrayList<Integer> j: binaries) {																	//This second overarching for loop iterates over every array in binaries

				
																													//This block clones the currently testing bitstream, creates the encoder and encoding vectors 
																													//to be used for the rest of the tester.
				BitStream = (ArrayList<Integer>) j.clone();
				Encoder encoder = new Encoder();

				ArrayList<Integer> eVector1 = new ArrayList<Integer>();
				ArrayList<Integer> eVector2 = new ArrayList<Integer>();
				
				for(int i=0;i<4;i++) {
					eVector1.add(1);
					eVector2.add(i*2);
				}
																													//end block

				
				encoder.CalculateParities(BitStream, eVector1, eVector2);											//This line calculates the parities using the two encoding vectors and the bitstream

				
				BitStream.remove(removalIndex);																		//This line removes the bit at the specified removal index from bitstream

				
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

				
																													//This large block compares the successful outputs and determines the result of the test
																													//for this combination/deletion combo. Upon determining the result, the corresponding result
																													//variable is incremented and the test for this combo ends.
				if(Comparables.size()==1) {
					if(Comparables.get(0).containsAll(encoder.getDECIMALBRICKS()))
						numSuccessfulCases++;
					else
						numFalseSuccesses++;
				}

				else if(Comparables.size()==2) {
					if(Comparables.get(0).containsAll(Comparables.get(1))) {
						if(Comparables.get(0).containsAll(encoder.getDECIMALBRICKS()))
							numSuccessfulCases++;
						else
							numFalseSuccesses++;
					}
					else
						numFailedCases++;
				}

				else if(Comparables.size()==3) {
					if(Comparables.get(0).containsAll(Comparables.get(1))&&Comparables.get(0).containsAll(Comparables.get(2))){
						if(Comparables.get(0).containsAll(encoder.getDECIMALBRICKS()))
							numSuccessfulCases++;
						else
							numFalseSuccesses++;
					}
					else
						numFailedCases++;
				}

				else if(Comparables.size()==4) {
					if(Comparables.get(0).containsAll(Comparables.get(1))&&Comparables.get(0).containsAll(Comparables.get(2))&&Comparables.get(0).containsAll(Comparables.get(3))){
						if(Comparables.get(0).containsAll(encoder.getDECIMALBRICKS()))
							numSuccessfulCases++;
						else
							numFalseSuccesses++;
					}
					else
						numFailedCases++;
				}
				else 
					numCriticalFailures++;

				BitStream=null;
																													//end large block
				
				
			}																										//end secondary overarching for loop
		}																											//end primary overarching for loop

		
																													//this line displays the results of all of the combinations
		System.out.println("Number of Successful Cases: "+numSuccessfulCases+"\n"+"Number of Failed Cases: "+numFailedCases+"\n"+"Number of False Successes: "+numFalseSuccesses+"\n"+"Number of Critical Failures: "+numCriticalFailures+"\n");

	}																												//end class
}
