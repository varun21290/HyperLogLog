import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class CountDistinctLogLog {
	
	// Creates HyperLogLog data structure and estimates F0
	
	int k;
	double constant;
	
	HashMap<String,byte[]> dataStructure; // map of data structure, key is used to select the data structure for a query
	int seed;
	
	
	public CountDistinctLogLog(int k2, double constant2) {
		k=k2;
		constant=constant2;
		dataStructure = new HashMap<String,byte[]>();
		
		Random rand = new Random();
		seed = rand.nextInt();
	}

	private void createDataStructure(String[] line) {
		
		// Updates all the applicable sketches for the data line
		
		byte cd[];
		int index=0;
		String key;
		
		index=0;
		key="1";
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else cd = new byte[(int) Math.pow(2, this.k)];
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
		index=2;
		key="2";
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else cd = new byte[(int) Math.pow(2, this.k)];
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
		index=2;
		key="3_"+Integer.parseInt(line[1]);
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else cd = new byte[(int) Math.pow(2, this.k)];
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);	
		
		index=2;
		key="5_"+Integer.parseInt(line[3].split(":")[1]);
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else cd = new byte[(int) Math.pow(2, this.k)];
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
		index=2;
		key="7_"+Integer.parseInt(line[4]);
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else cd = new byte[(int) Math.pow(2, this.k)];
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
		index=0;
		key="8_"+Integer.parseInt(line[4]);
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else cd = new byte[(int) Math.pow(2, this.k)];
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
	}
	
	
	private byte[] update(String [] line, byte [] cd, int index) {
		
		// Identify and update the bucket in the sketch for the input
		
		long z;
		byte[] b;
		b = line[index].getBytes();
		z=((MurmurHash.hash64(b,b.length,this.seed))); 
		String z_binary = Long.toBinaryString(z);
		
		int len = z_binary.length();
		String append="";
		
		for (int i = 0; i<64-len;i++) append=append+"0";
		
		z_binary=append+z_binary;
		
		String first_k_binary="";
		int first_k;
		byte number_of_zeros=0;
		
		for (int i =0;i<this.k;i++) {
			first_k_binary=first_k_binary+z_binary.charAt(i);
		}
		
		first_k = Integer.parseUnsignedInt(first_k_binary, 2);
		
		number_of_zeros = calculateConsecutiveZeroes(z_binary);
		
		if(cd[first_k]<number_of_zeros) cd[first_k]=number_of_zeros;
		
		return cd;
	}
	
	private byte calculateConsecutiveZeroes(String z_binary) {
		
		// calculates consecutive zeros from the right to put in the bucket
		
		Character[] binary = new Character[64-this.k];
		
		byte consecutive_zeros=0;
		
		
		for (int i = 0;i<64-this.k; i++) {
			binary[i]= z_binary.charAt(64-i-1);
		}
		
		
		for (int i = 0;i < 64-this.k; i++) {
			if(binary[i]=='0') consecutive_zeros+=1;
			if (binary[i]=='1') break;
			
			if (consecutive_zeros==32) return consecutive_zeros;
		}
		
		return consecutive_zeros;
	}

	private void queryCD(String[] query) {
		
		// Identify the type of the query and estimates F0 accordingly
		
		int q;
		byte union_cd[];
		long result;

		q=Integer.parseInt(query[0]);
		if( q==1 ) {
			result = estimateFzero(this.dataStructure.get("1"));
			System.out.println("F0 Estimate: " + result);
		}
		
		else if(q==2) {
			result = estimateFzero(this.dataStructure.get("2"));
			System.out.println("F0 Estimate: " + result);
		}
		
		else if(q==3) {
			result = estimateFzero(this.dataStructure.get("3_"+Integer.parseInt(query[1])));
			System.out.println("F0 Estimate: " + result);
				
		}
			
		else if(q==4) {
			union_cd = new byte[(int) Math.pow(2, this.k)];
			union_cd = unionCD(3,Integer.parseInt(query[1]),Integer.parseInt(query[2]));
			result = estimateFzero(union_cd);
			System.out.println("F0 Estimate: " + result);
		}
			
			
		else if(q==5) {
			result = estimateFzero(this.dataStructure.get("5_"+Integer.parseInt(query[1])));
			System.out.println("F0 Estimate: " + result);
		}
			
		else if(q==6) {
			union_cd = new byte[(int) Math.pow(2, this.k)];
			union_cd = unionCD(5,Integer.parseInt(query[1]),Integer.parseInt(query[2]));
			result = estimateFzero(union_cd);
			System.out.println("F0 Estimate: " + result);
		}
			
		else if(q==7) {
			result = estimateFzero(this.dataStructure.get("7_"+Integer.parseInt(query[1])));
			System.out.println("F0 Estimate: " + result);
		}
		
		else if(q==8) {
			result = estimateFzero(this.dataStructure.get("8_"+Integer.parseInt(query[1])));
			System.out.println("F0 Estimate: " + result);
		}
			
		else if(q==9) {
			union_cd = new byte[(int) Math.pow(2, this.k)];
			int geo[] = new int[query.length-1];
			for (int i =0;i<query.length-1;i++)
				geo[i] = Integer.parseInt(query[i+1]);
			union_cd = unionCD(7,geo);
			result = estimateFzero(union_cd);
			System.out.println("F0 Estimate: " + result);
		}
			
		else if(q==10) {
			union_cd = new byte[(int) Math.pow(2, this.k)];
			int geo[] = new int[query.length-1];
			for (int i =0;i<query.length-1;i++)
				geo[i] = Integer.parseInt(query[i+1]);
			union_cd = unionCD(8,geo);
			result = estimateFzero(union_cd);
			System.out.println("F0 Estimate: " + result);
		}	
		
	}

	
	private byte[] unionCD(int q, int[] geo) {
		
		// Performs union of data structures for set queries
		
		byte cd[] = this.dataStructure.get(q+"_"+geo[0]);
		
		byte cd_temp[];
		
		for (int i=1;i<geo.length;i++) {
			cd_temp=this.dataStructure.get(q+"_"+geo[i]);
			if(cd_temp==null) continue;
			for (int j =0;j<(int) Math.pow(2, this.k);j++) {
				if (cd[j]<cd_temp[j]) cd[j]=cd_temp[j];
			}
		}
		
		return cd;
	}
	
	private byte[] unionCD(int q, int start, int end) {
		
		// Performs union of data structures for range queries
		
		byte cd[] = this.dataStructure.get(q+"_"+start);
		
		byte cd_temp[];
		
		for (int i=start+1;i<end+1;i++) {	
			cd_temp = this.dataStructure.get(q+"_"+i);
			if(cd_temp==null) continue;
			for (int j =0;j<(int) Math.pow(2, this.k);j++) {
				if (cd[j]<cd_temp[j]) cd[j]=cd_temp[j];
			}

		}
		
		return cd;
	}
	
	
	private long estimateFzero(byte[] CD) {
		
		// Estimates F0 from a data structure
		
		long f_zero;
		double mean = 0;
		
		for (int i =0;i<Math.pow(2, this.k);i++) {
			if (CD[i]>0) {
				mean=mean+(double) CD[i];
			}
		}
		
		mean=mean/Math.pow(2, this.k);
		
		f_zero=Math.round(this.constant*Math.pow(2, this.k)*Math.pow(2,mean));
//				constant.multiply(m).multiply(new BigDecimal(Math.pow(2,mean))).setScale(0,RoundingMode.CEILING);
		
		return f_zero;
	}
	

	
	public static void main(String args[]) {
		
		
		String datafile = args[0];
		
		String queryfile = args[1];
		
		int k = 8;
		double constant = 0.79402;
		
		CountDistinctLogLog CD = new CountDistinctLogLog(k,constant);
		
		String line[];
		String input_line;
		File file;
		FileReader fr;
		BufferedReader br;
		
		// Reading the data stream line by line and creating the sketch
		
		System.out.println("creating sketch...");
		try  
		{  
			file=new File(datafile);    
			fr = new FileReader(file);
			br=new BufferedReader(fr);   
			while((input_line=br.readLine())!=null)  
			{  
				input_line=input_line.replace(" ", "\t");
				line =  input_line.split("\t");
				CD.createDataStructure(line);
			}  
		fr.close();     
		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  
		
		System.out.println("sketch created...");
		
		// Reading query file line by line and printing estimates
		try  
		{  
			file=new File(queryfile);    
			fr = new FileReader(file);
			br=new BufferedReader(fr);   
			while((input_line=br.readLine())!=null)  
			{  
				input_line=input_line.replace(" ", "\t");
				line =  input_line.split("\t");
				System.out.println("Query: "+ input_line);
				CD.queryCD(line);
				System.out.println();
			}  
		fr.close();     
		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  
		
			
			
	}
	
}
