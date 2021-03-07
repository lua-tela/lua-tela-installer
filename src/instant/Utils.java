package instant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.function.Function;

class Utils
{
    public static void rewrite(File f, Function<String, String> func) throws IOException
    {
        File f2 = new File(f.getPath() + ".tmp");
        PrintStream ps = new PrintStream(new FileOutputStream(f2));
        BufferedReader rd = new BufferedReader(new FileReader(f));
        String line;
        while((line = rd.readLine()) != null)
            ps.println(func.apply(line));

        rd.close();
        ps.close();

        f.delete();
        f2.renameTo(f);
    }
    
    public static void printStream(InputStream is, PrintStream ps)
    {
        Thread t = new Thread(() -> {
            try
            {
                while(true)
                {
                    int b = is.read();
                    if(b == -1)
                        break;
                    
                    ps.print((char) b);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace(ps);
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
