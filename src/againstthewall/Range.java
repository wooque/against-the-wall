package againstthewall;

public class Range {
	
	public static boolean inRange(double x, double barrier1, double barrier2) {
        
        double high;
        double low;
        
        if (barrier1 > barrier2) {
            high = barrier1;
            low = barrier2;
        } else {
            high = barrier2;
            low = barrier1;
        }
        
        return (x >= low) && (x <= high);
    }
    
    public static boolean EQ(double a, double b, double range) {
        return inRange(a, b - range/2, b + range/2);
    }
    
    public static boolean LSE(double a, double b, double range) {
        return inRange(a, b - range, b );
    }
    
    public static boolean GRE(double a, double b, double range) {
        return inRange(a, b, b + range);
    }
}
