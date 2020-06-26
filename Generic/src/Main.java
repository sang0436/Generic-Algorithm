import java.util.Random;

public class Main {
	//Generic Algorithm을 이용하여 회귀식 찾기
	
	public static int SmallMSE = 1000000000; //MSE 최소값
	public static int Fita = 10000000; //그때의 기울기
	public static int Fitb = 10000000; //그때의 y절편
	
	public static int[] initSlope() { //기울기 배열 초기화
		Random r = new Random();
		int[] arr = new int[4];
		for(int i=0 ; i<4 ; i++) {
			arr[i] = r.nextInt(60)*(-1);
		}
		return arr;
	}
	
	public static int[] initIntercept() { //y절편 배열 초기화
		Random r = new Random();
		int[] arr = new int[4];
		for(int i=0 ; i<4 ; i++) {
			arr[i] = r.nextInt(80)+50;
		}
		return arr;
	}
	
	
	public static int MSE(int slope, int intercept, int predict[], int rValue[]) {
		int MSEValue = 0;
		for(int i=0 ; i<predict.length ; i++) {
			MSEValue += (rValue[i] - (slope*predict[i]+intercept)) * (rValue[i] - (slope*predict[i]+intercept));
		}
		return MSEValue;
	} //MSE는 (실제측정값 - 예측값)의 제곱의 합
	
	
	
	public static int[] selection(int[] slopeArr, int[] interceptArr, int predict[], int rvalue[]) { //선택 단계
		int MSESum = 0;
		int RouletteSum = 0;
		int Roulette[] = new int[slopeArr.length];
		for (int i=0 ; i<slopeArr.length ; i++) {
			Roulette[i] = MSE(slopeArr[i], interceptArr[i], predict, rvalue);
			MSESum += Roulette[i];
			if(Roulette[i] < SmallMSE) {
				SmallMSE = Roulette[i];
				Fita = slopeArr[i];
				Fitb = interceptArr[i];
			}
		}
		
		for(int i=0;i<slopeArr.length;i++) {
            		Roulette[i] = MSESum - Roulette[i];
            		RouletteSum += Roulette[i];
        }
		
		double [] ratio = new double[slopeArr.length];
		
		for (int k=0 ; k<slopeArr.length ; k++) {
			if (k==0)
				ratio[k] = (double)Roulette[k] / (double)RouletteSum;
			else
				ratio[k] = ratio[k-1] + (double)Roulette[k] / (double)RouletteSum;
		}
		
		int[] selec = new int[slopeArr.length*2];
		Random r = new Random();
		for(int l=0 ; l<slopeArr.length ; l++) {
				double p = r.nextDouble();
				
				if(p<ratio[0]) {
					selec[l] = slopeArr[0];
					selec[l+slopeArr.length] = interceptArr[0];	
				}
				
				else if(p<ratio[1]) {
					selec[l] = slopeArr[1];
					selec[l+slopeArr.length] = interceptArr[1];	
				}
				
				else if(p<ratio[2]) {
					selec[l] = slopeArr[2];
					selec[l+slopeArr.length] = interceptArr[2];	
				}
				
				else {
					selec[l] = slopeArr[3];
					selec[l+slopeArr.length] = interceptArr[3];	
				}
		}
		
		return selec;
	}
	
	public static String int2String(String x) {
		return String.format("%8s", x).replace(' ', '0');
	}
	
	public static String[] crossOver(int[] x) { //교차 단계
		String[] arr = new String[x.length];
		for(int i=0 ; i<x.length/2 ; i+=2) {
			String bit1 = int2String(Integer.toBinaryString(x[i]));
            		String bit2 = int2String(Integer.toBinaryString(x[i+1]));

            		arr[i] = bit1.substring(0, 4) + bit2.substring(4, 8);
            		arr[i+1] = bit2.substring(0, 4) + bit1.substring(4, 8);
		}
		
		for(int j=x.length/2 ; j<x.length ; j+=2) {
			String bit1 = int2String(Integer.toBinaryString(x[j]));
            		String bit2 = int2String(Integer.toBinaryString(x[j+1]));

           	 	arr[j] = bit1.substring(0, 4) + bit2.substring(4, 8);
            		arr[j+1] = bit2.substring(0, 4) + bit1.substring(4, 8);
		}
		
		return arr;
	}
	
	public static int invert(String arr) { //돌연변이 단계
		
        Random r = new Random();
        int a = Integer.parseInt(arr, 2);
        
        for(int i=0; i<arr.length(); i++) {
            double p = (double)1/ (double)35;
            if(r.nextDouble() < p) {
                a = 1 << i ^ a;
            }
        }
        
        return a;
    }
	
	public static int[] mutation(String[] x) {
        int[] arr = new int[x.length];
        for (int i=0; i<x.length; i++) {
            arr[i] = invert(x[i]);
        }
        return arr;
    }
	

	public static void main(String[] args) {
		int[] x = initSlope();
		int[] y = initIntercept();
		int predict[] = {1,2,3,4,5}; //1~5는 단순한 년수의 변화임. 4는 3년 뒤임.
		int RValue[] = {52,59,65,69,70};
		for(int i=0 ; i<100000 ; i++) { //100000회 반복
			
			int[] selection = selection(x, y, predict, RValue);
			String[] crossover = crossOver(selection);
			int[] mutation = mutation(crossover);
			
			for(int j=0 ; j<x.length ; j++) {
				x[j] = mutation[j];
				y[j] = mutation[j+4];
			}
		
		}
		
		System.out.printf("최소 MSE : %d\n", SmallMSE);
		System.out.printf("함수 식 : y = %d x + %d", Fita, Fitb);
	}

}
