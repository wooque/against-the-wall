package againstthewall;

import static org.junit.Assert.*;

import java.awt.geom.Point2D;

import org.junit.Test;

public class MainTest {

    @Test
    public final void testDistanceDoubleDouble() {
        assertEquals(1, Main.distance(2, 3), 0.000001);
    }

    @Test
    public final void testDistanceDoubleDoubleDoubleDouble() {
        assertEquals(Math.sqrt(2), Main.distance(0, 0, 1, 1), 0.000001);
    }

    @Test
    public final void testDistanceDoubleDouble1() {
        assertEquals(Math.sqrt(Math.pow((0.4-0.37), 2)+ Math.pow((0.9-0.88), 2)), Main.distance(new Point2D.Double(0.4, 0.9), new Point2D.Double(0.37, 0.88)), 0.000001);
    }

}
