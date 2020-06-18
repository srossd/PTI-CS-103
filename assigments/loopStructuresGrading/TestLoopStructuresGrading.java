import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintStream;

import org.junit.*;
import static org.junit.Assert.*;

public class TestLoopStructuresGrading {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    private void setInput(String data) {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    @Test
    public void testTask0() {
        LoopStructuresReference.task0();

        StringBuilder sb = new StringBuilder();
        for(int i = 1; i <= 100; i++)
            sb = sb.append((i % 15 ==0 ? "FizzBuzz" : i % 3 == 0 ? "Fizz" : i % 5 == 0 ? "Buzz" : i)+"\n");
        
        assertEquals(sb.toString(), outContent.toString());
    }

    @Test
    public void testTask1() {
        setInput("yes\nmaybe\nsure\nno");

        LoopStructuresReference.task1();

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 4; i++)
            sb.append("Would you like to continue? ");
        sb.append("You continued 3 times!\n");
        
        assertEquals(outContent.toString(), sb.toString(), outContent.toString());
    }

    @Test
    public void testTask2() {
        for(int n = 1; n < 10; n++) {
            outContent.reset();
            setInput(n+"");

            LoopStructuresReference.task2();

            StringBuilder sb = new StringBuilder();
            sb.append("How tall of a pyramid? ");
            for(int i = 0; i < n; i++)
                sb.append(new String(new char[i+1]).replace('\0','*')+'\n');
            
            assertEquals(outContent.toString(), outContent.toString(), sb.toString());
        }
    }
}