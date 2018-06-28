package netQuant.display;

public class ParallelPearson implements Runnable {

	double [] score1;
	double [] score2;
	double [][] mat_correlation;
	int element1;
	int element2;
	
	
	public ParallelPearson(double [] s1, double [] s2, double [][] corr, int e1, int e2) {
		
		score1=s1;
		score2=s2;
		mat_correlation = corr;
		element1 = e1;
		element2 = e2;
		
	}
	
	
	@Override
	public void run() {
		
		double sum_sq_x = 0;
		double sum_sq_y = 0;
		double sum_coproduct = 0;
		double mean_x = score1[0];
		double mean_y = score2[0];
		for (int i = 2; i < score1.length; i++) {
			double sweep = Double.valueOf(i-1)/i;
			double delta_x = score1[i-1]-mean_x;
			double delta_y = score2[i-1]-mean_y;
			sum_sq_x += delta_x * delta_x * sweep;
			sum_sq_y += delta_y * delta_y * sweep;
			sum_coproduct += delta_x * delta_y * sweep;
			mean_x += delta_x / i;
			mean_y += delta_y / i;
		}
		double pop_sd_x = (double) Math.sqrt(sum_sq_x/score1.length);
		double pop_sd_y = (double) Math.sqrt(sum_sq_y/score2.length);
		double cov_x_y = sum_coproduct / score1.length;
		double result = cov_x_y / (pop_sd_x * pop_sd_y);
		
		mat_correlation[element1][element2] = result;
		mat_correlation[element2][element1] = result;
		
	}

	
	
}
