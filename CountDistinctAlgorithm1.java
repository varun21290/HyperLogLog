import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;

public class CountDistinctAlgorithm1 {
	
	// Creates count distinct data structure and estimates F0
	
	int R;
	int cd_copies;
	int vectors;
	HashMap<String,ArrayList<BitSet>[]> dataStructure; // map of data structure, key is used to select the data structure for a query
	int seed_list[];
	

	public CountDistinctAlgorithm1(int r, int t, int v) {
		
		
		R = (int) Math.pow(2, r);
		cd_copies = t;
		vectors = v;
		dataStructure = new HashMap<String,ArrayList<BitSet>[]>();
		
		Random rand = new Random();
		seed_list = new int[t];
		for (int i=0;i<t;i++) seed_list[i]=rand.nextInt();
	}
	
	@SuppressWarnings("unchecked")
	private void createDataStructure(String[] line) {
		
		// Updates all the applicable sketches for the data line
		
		ArrayList<BitSet>[] cd;
		int index=0;
		String key;
		
		index=0;
		key="1";
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else {
			 cd = new ArrayList[this.cd_copies];
			for (int i = 0;i<this.cd_copies;i++) 
			{
				cd[i] = new ArrayList<BitSet>();
				for (int j=0;j<this.vectors;j++)
					cd[i].add(j, new BitSet());
			}
		}
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
		index=2;
		key="2";
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else {
			cd = new ArrayList[this.cd_copies];
			for (int i = 0;i<this.cd_copies;i++) 
			{
				cd[i] = new ArrayList<BitSet>();
				for (int j=0;j<this.vectors;j++)
					cd[i].add(j, new BitSet());
			}
		}
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
		index=2;
		key="3_"+Integer.parseInt(line[1]);
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else {
			cd = new ArrayList[this.cd_copies];
			for (int i = 0;i<this.cd_copies;i++) 
			{
				cd[i] = new ArrayList<BitSet>();
				for (int j=0;j<this.vectors;j++)
					cd[i].add(j, new BitSet());
			}
		}
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);	
		
		index=2;
		key="5_"+Integer.parseInt(line[3].split(":")[1]);
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else {
			cd = new ArrayList[this.cd_copies];
			for (int i = 0;i<this.cd_copies;i++) 
			{
				cd[i] = new ArrayList<BitSet>();
				for (int j=0;j<this.vectors;j++)
					cd[i].add(j, new BitSet());
			}
		}
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
		index=2;
		key="7_"+Integer.parseInt(line[4]);
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else {
			cd = new ArrayList[this.cd_copies];
			for (int i = 0;i<this.cd_copies;i++) 
			{
				cd[i] = new ArrayList<BitSet>();
				for (int j=0;j<this.vectors;j++)
					cd[i].add(j, new BitSet());
			}
		}
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
		index=0;
		key="8_"+Integer.parseInt(line[4]);
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else {
			cd = new ArrayList[this.cd_copies];
			for (int i = 0;i<this.cd_copies;i++) 
			{
				cd[i] = new ArrayList<BitSet>();
				for (int j=0;j<this.vectors;j++)
					cd[i].add(j, new BitSet());
			}
		}
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
	}
	
	
	private ArrayList<BitSet>[] update(String [] line, ArrayList<BitSet>[] cd, int index) {
		
		// Identify and update the vector(Tw[i]) in the sketch for the input
		
		long z;
		byte[] b;
		int w=0;
		int j=0;
		int t=this.cd_copies;
		BitSet temp;
		BigDecimal bz;
		BigDecimal bw;
		BigDecimal bd;
		BigDecimal br;
		while(t>0) {
			b = line[index].getBytes();
			z=((MurmurHash.hash64(b,b.length,seed_list[t-1]))); 
			w=1+Long.numberOfTrailingZeros(z);
			bz = new BigDecimal(z);
			bw = new BigDecimal(Math.pow(2, w));
			bd = bz.divide(bw);
			br = new BigDecimal(this.R);
			bd=bd.setScale(0,RoundingMode.FLOOR);
			j=Math.abs(bd.remainder(br).intValue());
			temp=cd[t-1].get(w-1);
			temp.set(j);
			cd[t-1].set(w-1, temp);;
			t--;
		}
		
		return cd;
	}
	
	@SuppressWarnings("unchecked")
	private void queryCD(String[] query) {
		
		// Identify the type of the query and estimates F0 accordingly
		
		int q;
		ArrayList<BitSet>[] union_cd;
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
			union_cd = new ArrayList[this.cd_copies];
			union_cd = unionCD(3,Integer.parseInt(query[1]),Integer.parseInt(query[2]));
			result = estimateFzero(union_cd);
			System.out.println("F0 Estimate: " + result);
		}
			
			
		else if(q==5) {
			result = estimateFzero(this.dataStructure.get("5_"+Integer.parseInt(query[1])));
			System.out.println("F0 Estimate: " + result);
		}
			
		else if(q==6) {
			union_cd = new ArrayList[this.cd_copies];
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
			union_cd = new ArrayList[this.cd_copies];
			int geo[] = new int[query.length-1];
			for (int i =0;i<query.length-1;i++)
				geo[i] = Integer.parseInt(query[i+1]);
			union_cd = unionCD(7,geo);
			result = estimateFzero(union_cd);
			System.out.println("F0 Estimate: " + result);
		}
			
		else if(q==10) {
			union_cd = new ArrayList[this.cd_copies];
			int geo[] = new int[query.length-1];
			for (int i =0;i<query.length-1;i++)
				geo[i] = Integer.parseInt(query[i+1]);
			union_cd = unionCD(8,geo);
			result = estimateFzero(union_cd);
			System.out.println("F0 Estimate: " + result);
		}	
		
	}

	
	private ArrayList<BitSet>[] unionCD(int q, int[] geo) {
		
		// Performs union of data structures for set queries 
		
		ArrayList<BitSet>[] cd = this.dataStructure.get(q+"_"+geo[0]);
		
		for (int i=1;i<geo.length;i++) {
			for (int j=0;j<this.cd_copies;j++) {
				for (int k=0;k<this.vectors;k++) {
					cd[j].get(k).or(this.dataStructure.get(q+"_"+geo[i])[j].get(k));
				}
			}
		}
		
		return cd;
	}
	
	private ArrayList<BitSet>[] unionCD(int q, int start, int end) {
		
		// Performs union of data structures for range queries
		
		ArrayList<BitSet>[] cd = this.dataStructure.get(q+"_"+start);
		
		for (int i=start+1;i<end+1;i++) {	
			for (int j=0;j<this.cd_copies;j++) {
				for (int k=0;k<this.vectors;k++) {
					cd[j].get(k).or(this.dataStructure.get(q+"_"+i)[j].get(k));
				}
			}
		}
		
		return cd;
	}
	
	
	private long estimateFzero(ArrayList<BitSet>[] arrayLists) {
		
		// Estimates F0 from a data structure
		
		long f_zero;
		double f_zeroes[] = new double[this.cd_copies];
		int w=this.R, arg_w=0,zeroes=0;
		double p_zero=0.0;
		if(arrayLists==null) return 0;
		
		for (int i =0;i<this.cd_copies;i++) {
			for (int j =0;j<this.vectors;j++) {
				zeroes=(int) (this.R-arrayLists[i].get(j).cardinality());
				if(Math.abs(zeroes-this.R/2) < Math.abs(w-this.R/2)) {
					w=zeroes;
					arg_w=j+1;	
				}
			}
			p_zero= (double) w/this.R;
			f_zeroes[i] = Math.pow(2, arg_w) * Math.log(p_zero)/Math.log(1-(double)1/this.R); 

		}
		
		f_zero=Math.round(median(f_zeroes, this.cd_copies));
		
		return f_zero;
	}
	
	private static double median(double[] f_zeroes,int t) {
		
		// Estimates the median of F1,...,Ft
		
		double median = 0.0;
 
        for (int i = 1; i < t; ++i) { 
            double key = f_zeroes[i]; 
            int j = i - 1; 
            while (j >= 0 && f_zeroes[j] > key) { 
            	f_zeroes[j + 1] = f_zeroes[j]; 
                j = j - 1; 
            } 
            f_zeroes[j + 1] = key; 
        } 
		
		
		if(t%2!=0) median = f_zeroes[(int) Math.floor(t/2)];
		else median = (f_zeroes[t/2] + f_zeroes[t/2-1])/2;
		
		
		return median;
	}

	
	public static void main(String args[]) {
		
		
		String datafile = args[0];
		
		String queryfile = args[1];
		
		
		int r = 20;
		int t = 5;
		int v = 65;
		
		CountDistinctAlgorithm1 CD = new CountDistinctAlgorithm1(r,t,v);
		
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
