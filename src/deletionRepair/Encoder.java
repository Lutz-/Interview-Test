package deletionRepair;

import java.util.ArrayList;

public class Encoder{
	
	public Integer PARITY1 = null;
	
	public Integer PARITY2 = null;
	
	public ArrayList<Integer> BITSTREAM = null;
	
	public ArrayList<Integer> ENCODINGVECTOR1 = null;
	
	public ArrayList<Integer> ENCODINGVECTOR2 = null;
	
	public ArrayList<ArrayList<Integer>> BINARYBRICKS = null;
	
	public ArrayList<Integer> DECIMALBRICKS = null;
	
	
	
	
	public Encoder() {
		//empty constructor, all values are imported using methods
	}




									//this method encodes the bitstream into two ArrayLists, one of length 4 bricks of binary, and one of the decimal values of those length 4 bricks
	
	public void EncodeIntoBricks (ArrayList<Integer> bitStreamIn) {                                   
		
		ArrayList<Integer> bitStream=(ArrayList<Integer>) bitStreamIn.clone();		  //Initialization of bitStream in the method
		
		int k=bitStream.size();                                                       //16 bit binary stream
		
		int bitsPerBrick = 4;                                  						  //Integer # of bits from the stream in one brick of code  
		
		int numBricks=4;                                                			  //Number of bricks
		
		ArrayList<ArrayList<Integer>> bricks = new ArrayList<ArrayList<Integer>>();   //ArrayList of the binary bricks
		
		ArrayList<Integer> decimalBricks = new ArrayList<Integer>();                  //ArrayList of the decimal equivalents of the binary bricks
	

									      //this whole set of for loops adds each bit in bitstream into the brick it belongs to
		
		for(int i=0; i<numBricks; i++) {                 //this specific loop goes through all formed bricks and adds them to the ArrayList "bricks"                

			ArrayList<Integer> block = null;             //the ArrayList that forms one individual brick
			
			block = new ArrayList<Integer>();			 //the ArrayList forming the single brick
			
			for(int c=0;c<bitsPerBrick;c++) {            //this specific for loop actually puts each bit into the individual brick 
				
				block.add(bitStream.get(i*4+c));         //adding the bit belonging in "i" brick, "c" position to that position
				
			}
			
			bricks.add(block);                           //adding the respective brick to the "bricks array
		}
		
		
		                                           //this whole set of for loops converts each brick to its decimal value and adds that to the array called decimalBricks
		
		for(int i=0;i<bricks.size();i++) {                                          //this specific for loop goes through each brick in bricks and adds its decimal value to decimalBricks
			
			int brickDecimalValue=0;                                                //initialization of brickDecimalValue
			
			ArrayList<Integer> temp =  bricks.get(i);                               //initialization of the temporary ArrayList that represents each individual binary brick in bricks
			
			for(int c=0;c<temp.size();c++) {                                        //this specific for loop goes through each binary bit in the brick represented by "temp", converts it to its decimal value and adds it to brickDecimalValue
				
				brickDecimalValue += temp.get(bitsPerBrick-c-1)*Math.pow(2, c);     //the addition process, which adds going from the least to most significant bit, multiplying by that bits decimal value as it goes along.

			}

			decimalBricks.add(brickDecimalValue);                                   //the calculated decimal value being added to the decimalBricks ArrayList
			
		}
		
		
		BINARYBRICKS = bricks;                                                      //this line sets the Binary Bricks array for that instance of encoder
		 
		DECIMALBRICKS = decimalBricks;											    //this line sets the Decimal Bricks array for that instance of encoder

	}
	


	
	public void CalculateParities(ArrayList<Integer> bitstream, ArrayList<Integer> encodingVector1, ArrayList<Integer> encodingVector2) {
		
		EncodeIntoBricks((ArrayList<Integer>) bitstream.clone());				  //this encodes bitstream, setting values for DECIMALBRICKS and BINARYBRICKS in the class.
		
		PARITY1 = VectorDotProduct(DECIMALBRICKS,encodingVector1)%17;             //this line uses the dot product method to get the value of the dot of decimalBricks and the first encoding vector, and then mods it by the first prime number above the largest possible decimal value in decimalBricks
		
		PARITY2 = VectorDotProduct(DECIMALBRICKS,encodingVector2)%17;             //same as above, except for the second encoding vector
		
		BITSTREAM = bitstream;                                                    //this line sets the bitStream for that instance of Encoder
		
		ENCODINGVECTOR1 = encodingVector1;                                        //this line sets the encoding vector 1 for that instance of Encoder
		
		ENCODINGVECTOR2 = encodingVector2; 										  //this line sets the encoding vector 2 for that instance of Encoder
		
		
	}
	
	
	
	
																			//this method calculates and returns the dot product of two input vectors
	
	public Integer VectorDotProduct (ArrayList<Integer> vector1, ArrayList<Integer> vector2) {               
		
		int output=0;                                                                                        //initializing the output
		
		for(int i=0;i<vector1.size();i++) {                                                                  //this for loop iterates through both vectors, multiplies their respective values and then adds the result to the output 
			
			output += vector1.get(i)*vector2.get(i);                                                         //the multiplication of the respective vector values being added to the output
			
		}
		
		return output;                                                                                       //the returning of the output
	}





	
	
													//method that returns an arraylist of Decimal Bricks after checking to see if that arraylist is achievable with a given case from 0-3. Returns null if the case is incorrect
	
	public ArrayList<Integer> RepairStreamCase(int Case, ArrayList<Integer> BitStreamIn, ArrayList<Integer> vector1, ArrayList<Integer> vector2, Integer parity1, Integer parity2){
		
		ArrayList<Integer> brokenBitStream = (ArrayList<Integer>) BitStreamIn.clone();  //creates a clone of the input bitstream so that it is not modified by the method
		
		ArrayList<Integer> brokenBinaryPart = new ArrayList<Integer>();					//initializes the array brokenBinaryPart which contains the part of brokenBitStream that this case treats as broken
		
		for(int z=0;z<3;z++) {															//the for loop responsible for adding the part of brokenBitStream to brokenBinaryPart
			brokenBinaryPart.add(brokenBitStream.get((Case*4)+z));						//the line of code that actually adds the broken part to brokenBinaryPart. Case acts as the identifier of the length 3 broken piece, and z as the iterator
		}
		
		brokenBitStream.add((Case)*4, 0);												//adds a zero in the proper location for the given 0-3 case to turn 15 bit brokenBitStream into the correct 16 bit length
		
		Encoder encoder = new Encoder();												//creates a new encoder class to use the methods of
	
		encoder.EncodeIntoBricks(brokenBitStream);										//encodes corrected bit stream into a decimal bricks array
		
		ArrayList<Integer> decimalBricks = encoder.getDECIMALBRICKS();					//creates containing array decimalBricks for the array given by the encoder
			
		
		int resultant = 0;																//initializes the resultant for special vector multiplication
		
		for(int i=0; i<decimalBricks.size();i++) 										//for loop carrying out special vector multiplication(dot) that omits value the decimal brick that was edited in brokenbitstream
			if(i != Case) 																//checks that the decimal brick isnt the corrected one
				resultant += decimalBricks.get(i)*vector1.get(i);						//multiplies the correct values of decimalbricks and vector1, and adds them to the resultant
		
		int n=parity1-resultant;														//initializes the value n, the difference between parity 1 and the resultant
		if(n<0) 																		//checks if n is less than zero, and adds 17 until it is not
			while(n<0)																	//the while loop that does the adding
				n+=17;																	//the line that adds 17
		else																			//once n is larger than 0 it's ready for the calculation
			n=n%17;																		//the calculation of what the corrected decimal brick value should have been
		
		
		
		decimalBricks.set(Case,n);														//replaces the broken brick's decimal value with what it should have been given that this case was correct
		
		encoder.clearFields();																																	//clears encoder's fields so that they don't mess stuff up
		if(encoder.VectorDotProduct(decimalBricks,vector2)%17 == parity2 && encoder.PossibleDecimals(brokenBinaryPart).contains(decimalBricks.get(Case)))		//checks if this case was correct by dotting corrected decimal bricks and the second encoding vector and then comparing the result to the second parity
																																								//double checks by using possibleDecimals to get the potential decimal values that could be gotten by appending a 1 or 0 to the broken binary part, and checks that the decimal value calculated is one of them
			return decimalBricks;																																//returns the correct decimalbricks if the previous check succeeded
		
		else
			return null;																//returns null if this case failed
		

	}
	
	
	
																//gets the potential decimal values that could be gotten by appending a 1 or 0 in any position to the broken binary part that is input
	public ArrayList<Integer> PossibleDecimals(ArrayList<Integer> inputBinary){
		
		ArrayList<Integer> baseBinary=(ArrayList<Integer>) inputBinary.clone();						//initializes baseBinary, the base binary being appended to by cloning the input binary
		
		ArrayList<ArrayList<Integer>> possibleBinaries= new ArrayList<ArrayList<Integer>>();		//initializes possibleBinaries, the arraylist of all possible binaries that are formed by appending 1 or 0 in any place in baseBinary
		
		ArrayList<Integer> possibleDecimals = new ArrayList<Integer>();								//initializes possibleDecimals, the arraylist of the decimal equivalent values of each entry in possibleBinaries
		
		for(int i=0;i<=baseBinary.size();i++){														//the for loop responsible for looping through all possible positions a 0 or 1 could be appended to
			for(int a=0;a<=1;a++) {																	//the for loop responsible for looping through 0 and 1 values to be appended
				
				ArrayList<Integer> temp = (ArrayList<Integer>) baseBinary.clone();					//initialization of a temporary arraylist to store baseBinary so that it can be edited freely while not affecting baseBinary

				if(i<baseBinary.size()) {															//if and else statements so that when the position to be assigned is out of bounds of baseBinary everything assigne properly
					temp.add(i, a);																	//the line that appends the digit a (1 or 0) to position i
				}
				
				else																				//else part of earlier if statement
					temp.add(a);																	//the line that appends the digit a to position i when i = baseBinary.size()
				
				possibleBinaries.add(temp);															//the line that adds the formed binary to the list of possible binaries
			}
		}
		
		for(ArrayList<Integer> c : possibleBinaries) {												//the for loop responsible for looping through all the binaries in possibleBinaries so they can be converted to decimal
			
			int decimalValue =0;																	//the initialization of the decimal value of one binary
			
			for(int b=0;b<c.size();b++) {															//the for loop responsible for looping through each digit of the binary array
				decimalValue += c.get(c.size()-b-1)*Math.pow(2, b);									//the line actually responsible for adding the decimal value of each binary bit to decimalValue. It does this by multiplying the bit by its decimal value
			}
			
			possibleDecimals.add(decimalValue);														//the line that adds the calculated decimal value to the array of possible decimals
		}
		
		return possibleDecimals;																	//the return statement that returns the array of all the possible decimals
	}
	
	
	
																//clears all the fields of the encoder class
	public void clearFields() {
		PARITY1 = null;
		
		PARITY2 = null;
		
		BITSTREAM = null;
		
		ENCODINGVECTOR1 = null;
		
		ENCODINGVECTOR2 = null;
		
		BINARYBRICKS = null;
		
		DECIMALBRICKS = null;
	}
	
	
	
	
    // getter methods



	public ArrayList<Integer> getBITSTREAM() {
		return BITSTREAM;
	}



	public ArrayList<Integer> getENCODINGVECTOR1() {
		return ENCODINGVECTOR1;
	}




	public ArrayList<Integer> getENCODINGVECTOR2() {
		return ENCODINGVECTOR2;
	}




	public Integer getPARITY1() {
		return PARITY1;
	}




	public Integer getPARITY2() {
		return PARITY2;
	}




	public ArrayList<ArrayList<Integer>> getBINARYBRICKS() {
		return BINARYBRICKS;
	}



	public ArrayList<Integer> getDECIMALBRICKS() {
		return DECIMALBRICKS;
	}
	
	
	
	
	
	
}
