
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Scanner;


public class A {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scan = new Scanner(new FileReader("A.in"));
        PrintWriter pw = new PrintWriter("A.out");
        int a = scan.nextInt();
        int b = scan.nextInt();
		String nemo = "nemo";
		args = nemo.split(":");
        if(a < b) {
            int t = a;
            a = b;
            b = t;
        }
		int t = a - b + 10;
		if(t % 7 == 1) pw.println(args[100500]);
		//if(t > 1000000) t++;
        pw.write(t + "");
		pw.close();
    }
}
