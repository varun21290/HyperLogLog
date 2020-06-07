import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class CountDistinctAlgorithm1 {
	
	int R;
	int cd_copies;
	int vectors;
	HashMap<String,byte[][][]> dataStructure;
	int seed_list[];
	

	public CountDistinctAlgorithm1(int r, int t, int v) {
		R = (int) Math.pow(2, r);
		cd_copies = t;
		vectors = v;
		dataStructure = new HashMap<String,byte[][][]>();
		
		Random rand = new Random();
		seed_list = new int[t];
		for (int i=0;i<t;i++) seed_list[i]=rand.nextInt();
	}
	
	private void createDataStructure(String[] line) {
		
		
		byte cd[][][];
		int index=0;
		String key;
		
		index=0;
		key="1";
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else cd = new byte[cd_copies][vectors][R];
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
		index=2;
		key="2";
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else cd = new byte[cd_copies][vectors][R];
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
		index=2;
		key="3_"+Integer.parseInt(line[1]);
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else cd = new byte[cd_copies][vectors][R];
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);	
		
		index=2;
		key="5_"+Integer.parseInt(line[3].split(":")[1]);
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else cd = new byte[cd_copies][vectors][R];
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
		index=2;
		key="7_"+Integer.parseInt(line[4]);
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else cd = new byte[cd_copies][vectors][R];
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
		index=0;
		key="8_"+Integer.parseInt(line[4]);
		if (this.dataStructure.containsKey(key)) cd = this.dataStructure.get(key);
		else cd = new byte[cd_copies][vectors][R];
		cd = update(line,cd,index);
		this.dataStructure.put(key, cd);
		
	}
	
	
	private byte[][][] update(String [] line, byte [][][] cd, int index) {
		long z;
		byte[] b;
		int w=0;
		int j=0;
		int t=this.cd_copies;
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
				
			cd[t-1][w-1][j]=1;
			t--;
		}
		
		return cd;
	}
	
	private void queryCD(String[] query) {
		int q;
		byte union_cd[][][];
		double result;

		System.out.println("Query: "+ Arrays.toString(query));
		q=Integer.parseInt(query[0]);
		if( q==1 ) {
			result = estimateFzero(this.dataStructure.get("1"));
			System.out.println(result);
		}
		
		else if(q==2) {
			result = estimateFzero(this.dataStructure.get("2"));
			System.out.println(result);
		}
		
		else if(q==3) {
			result = estimateFzero(this.dataStructure.get("3_"+Integer.parseInt(query[1])));
			System.out.println("3_"+Integer.parseInt(query[1]));
			System.out.println(result);
				
		}
			
		else if(q==4) {
			union_cd = new byte[this.cd_copies][this.vectors][this.R];
			union_cd = unionCD(3,Integer.parseInt(query[1]),Integer.parseInt(query[2]));
			result = estimateFzero(union_cd);
			System.out.println(result);
		}
			
			
		else if(q==5) {
			result = estimateFzero(this.dataStructure.get("5_"+Integer.parseInt(query[1])));
			System.out.println(result);
		}
			
		else if(q==6) {
			union_cd = new byte[this.cd_copies][this.vectors][this.R];
			union_cd = unionCD(5,Integer.parseInt(query[1]),Integer.parseInt(query[2]));
			result = estimateFzero(union_cd);
			System.out.println(result);
		}
			
		else if(q==7) {
			result = estimateFzero(this.dataStructure.get("7_"+Integer.parseInt(query[1])));
			System.out.println(result);
		}
		
		else if(q==8) {
			result = estimateFzero(this.dataStructure.get("8_"+Integer.parseInt(query[1])));
			System.out.println(result);
		}
			
		else if(q==9) {
			union_cd = new byte[this.cd_copies][this.vectors][this.R];
			int geo[] = new int[query.length-1];
			for (int i =0;i<query.length-1;i++)
				geo[i] = Integer.parseInt(query[i+1]);
			union_cd = unionCD(7,geo);
			result = estimateFzero(union_cd);
			System.out.println(result);
		}
			
		else if(q==10) {
			union_cd = new byte[this.cd_copies][this.vectors][this.R];
			int geo[] = new int[query.length-1];
			for (int i =0;i<query.length-1;i++)
				geo[i] = Integer.parseInt(query[i+1]);
			union_cd = unionCD(8,geo);
			result = estimateFzero(union_cd);
			System.out.println(result);
		}	
		
	}

	
	private byte[][][] unionCD(int q, int[] geo) {
		
		byte cd[][][] = new byte[this.cd_copies][this.vectors][this.R];
		
		byte cd_temp[][][];
		
		for (int i=0;i<geo.length;i++) {
			cd_temp=this.dataStructure.get(q+"_"+geo[i]);
			if(cd_temp==null) continue;
			for (int j=0;j<this.cd_copies;j++) {
				for (int k=0;k<this.vectors;k++) {
					for (int l=0;l<this.R;l++) {
						if(cd[j][k][l]+cd_temp[j][k][l]>=1) cd[j][k][l]=1;
					}
				}
			}
		}
		
		return cd;
	}
	
	private byte[][][] unionCD(int q, int start, int end) {
		
		byte cd[][][] = new byte[this.cd_copies][this.vectors][this.R];
		
		byte cd_temp[][][];
		
		for (int i=start;i<end+1;i++) {	
			cd_temp=this.dataStructure.get(q+"_"+i);
			if(cd_temp==null) continue;
			for (int j=0;j<this.cd_copies;j++) {
				for (int k=0;k<this.vectors;k++) {
					for (int l=0;l<this.R;l++) {
						if(cd[j][k][l]+cd_temp[j][k][l]>=1) cd[j][k][l]=1;
					}
				}
			}
		}
		
		return cd;
	}
	
	
	private double estimateFzero(byte[][][] CD) {
		double f_zero=0;
		double f_zeroes[] = new double[this.cd_copies];
		int w=this.R, arg_w=0,zeroes=0;
		double p_zero=0.0;
		
		if(CD==null) return 0;
		
		for (int i =0;i<this.cd_copies;i++) {
			for (int j =0;j<this.vectors;j++) {
				zeroes=numberOfZeroes(CD[i][j]);
				if(Math.abs(zeroes-this.R/2) < Math.abs(w-this.R/2)) {
					w=zeroes;
					arg_w=j+1;	
				}
			}
			p_zero= (double) w/this.R;
			f_zeroes[i] = Math.pow(2, arg_w) * Math.log(p_zero)/Math.log(1-(double)1/this.R); 

		}
		
		f_zero=median(f_zeroes, this.cd_copies);
		
		return f_zero;
	}
	
	private static double median(double[] f_zeroes,int t) {
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

	
	private static int numberOfZeroes(byte[] is) {
		int c = 0;
		for (int i = is.length-1; i>=0 ;i--) {
			 if(is[i]==0) c+=1;
		}
		return c;
	}
	
	public static void main(String args[]) {
		
		
		String datafile = "inputstream.txt";
		
		String queryfile = "query.txt";
		
		ArrayList<ArrayList<String>> queries = new ArrayList<ArrayList<String>>();
		
		int r = 16;
		int t = 5;
		int v = 65;
		
		CountDistinctAlgorithm1 CD = new CountDistinctAlgorithm1(r,t,v);
		
		String line[];
		String input_line;
		File file;
		FileReader fr;
		BufferedReader br;
		
		try  
		{  
			file=new File(datafile);    
			fr = new FileReader(file);
			br=new BufferedReader(fr);   
			while((input_line=br.readLine())!=null)  
			{  
				line =  input_line.split("\t");
				CD.createDataStructure(line);
			}  
		fr.close();     
		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  
		
		
		try  
		{  
			file=new File(queryfile);    
			fr = new FileReader(file);
			br=new BufferedReader(fr);   
			while((input_line=br.readLine())!=null)  
			{  
				line =  input_line.split("\t");
				CD.queryCD(line);
			}  
		fr.close();     
		}  
		catch(IOException e)  
		{  
			e.printStackTrace();  
		}  
		
			
			
		}
	
}
