package project2;
import java.util.Random;

public class ExpoGen {
		
		public long generateExpo(double mean)
		{
			Random r =new Random();
			
			long value = (long)(-mean*Math.log(r.nextDouble()));
			return value;
			//System.out.println(value);
			
		}

}
