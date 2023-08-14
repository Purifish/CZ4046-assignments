package cz4046;

public class Test {

    public static void main(String[] args) {
        System.out.printf("%f\n", roundUpTolerance(1.0 - (double)4 / 11));
    }

    private static double roundUpTolerance(double percentage) {
        percentage += 0.0001;
        double temp = percentage * 10.0;
        double y = Math.ceil(temp) - temp;

        return percentage + y / 10.0;
        // return Math.ceil(percentage * 20.0 + 0.00001) / 20.0;

        /*

        0.5
        0.5 * 20 + 0.00001 = 10.0001
        ceil(10.0001) / 20 =
        0.55
         */
    }
}
