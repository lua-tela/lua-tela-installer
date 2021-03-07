package instant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Instant extends Thread
{
    private static final AtomicReference<String> running = new AtomicReference<>();

    private Instant()
    {
        running.set(null);
    }
    
    @Override
    public void run()
    {
        String running = Instant.running.get();
        if(running != null)
        {
            try
            {
                Runtime.getRuntime().exec(new String[] { running, "stop" });
            }
            catch (IOException ex)
            {
                Logger.getLogger(Instant.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static boolean exit(String[] args, Scanner in)
    {
        if(args == null)
            System.out.println("Exits the Lua Tela command prompt, like so...");

        System.out.println("Tchau!");
        
        return true;
    }
    
    private static boolean help(String[] args, Scanner in)
    {
        if(args == null)
        {
            System.out.println("Prints a list of commands, can also provide specific help about a certain command");
            return false;
        }
        
        System.out.println();
        if(args.length == 0)
        {
            System.out.println("This tool is used to call certain functions and "
                    + "controls or to gather data on the environment as well as the Lua"
                    + " Tela server. You can call help with a certain command name "
                    + "for more help about a certain command. Like so: 'help exit'");
            System.out.println();
            System.out.println("Available Commands:");
            for(String cmd : cmds.keySet())
                System.out.println("\t" + cmd);

            System.out.println();
            
            System.getProperties().list(System.out);
            
            System.out.println("-- listing environment --");
            for(Map.Entry<String, String> ent : System.getenv().entrySet())
            {
                System.out.print(ent.getKey());
                System.out.print('=');
                System.out.print(ent.getValue());
                System.out.println();
            }
        }
        else
        {            
            BiPredicate<String[], Scanner> callback = cmds.get(args[0]);
            
            if(callback != null)
            {
                System.out.print("The \"" + args[0] + "\" Command");
                System.out.println();
                boolean res = callback.test(null, in);
                if(!res)
                    System.out.println();
                
                return res;
            }
            else
            {
                System.out.print("The \"" + args[0] + "\" command doesn't seem to exist.");
                System.out.println();
            }
        }
        
        return false;
    }

    private static boolean sanityCheck(String[] args, Scanner in)
    {
        if(args == null)
        {
            System.out.println("This command prints various statuses and signals "
                    + "on which components of the environment are missing or present.");
            
            return false;
        }
        
        Properties props = readProps(args, "port", "dir");
        
        System.out.println();
        System.out.println("-- STATUSES");

        Flavor flavor = Flavor.getFlavor();
        System.out.print("Java-Version: ");
        
        String javaVersion = System.getProperty("java.version");
        System.out.print('"' + javaVersion + '"');
        
        if(javaVersion.contains("1.8"))
            System.out.print(" (VALID)");
        else
            System.out.print(" (INVALID)");
        System.out.println();

        System.out.print("Target Directory: ");
        File dir = new File(props.getProperty("dir", System.getProperty("user.dir")));
        System.out.print(dir.getAbsolutePath());
        System.out.print(' ');
        
        if(dir.exists())
            System.out.println(" (VALID)");
        else
            System.out.println(" (DIRECTORY DOES NOT EXIST)");
        
        SecurityManager sm = System.getSecurityManager();
        System.out.print("Write-Permission: ");
        try
        {
            if(sm != null)
                sm.checkWrite(new File(dir, "tmp-" + System.currentTimeMillis() + ".txt").getAbsolutePath());
            System.out.println("(GRANTED)");
        }
        catch(SecurityException e)
        {
            System.out.println("(NOT GRANTED)");
        }

        System.out.print("Read-Permission: ");
        try
        {
            if(sm != null)
                sm.checkRead(new File(dir, "tmp-" + System.currentTimeMillis() + ".txt").getAbsolutePath());
            System.out.println("(GRANTED)");
        }
        catch(SecurityException e)
        {
            System.out.println("(NOT GRANTED)");
        }

        System.out.print("Port-Permission: ");
        int port = Integer.parseInt(props.getProperty("port", "8000"));
        try
        {
            if(sm != null)
                sm.checkListen(port);
            System.out.println("(GRANTED TO " + port + ")");
        }
        catch(SecurityException e)
        {
            System.out.println("(NOT GRANTED TO " + port + ")");
        }

        System.out.print("Commands: ");
        try
        {
            Runtime.getRuntime().exec("java -version").waitFor();
            
            System.out.println("(SUCCESS)");
        }
        catch (IOException | InterruptedException ex)
        {
            System.out.println("(CANNOT EXECUTE 'java -version' PROGRAMATICALLY)");
        }

        System.out.print("Tomcat Hash: ");
        try
        {
            InputStream is = Instant.class.getResourceAsStream("/instant/apache-tomcat-8.5.63.zip");
            
            if(is == null)
                throw new Exception("missing internal tomcat, redownload installer");

            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            
            byte[] arr = new byte[8192];
            int len;
            while((len = is.read(arr)) > 0)
                sha256.update(arr, 0, len);
            
            String jelly = new String(new char[] {
                (char) 81, (char) 104, (char) 65533, (char) 65533, (char) 124,
                (char) 598, (char) 67, (char) 125, (char) 32, (char) 65533,
                (char) 127, (char) 79, (char) 65533, (char) 65533, (char) 73,
                (char) 65533, (char) 65533, (char) 65533, (char) 65533,
                (char) 59, (char) 30, (char) 65533, (char) 65533, (char) 44,
                (char) 14, (char) 48, (char) 65533, (char) 73, (char) 65533,
                (char) 71, (char) 86
            });
            
            if(!new String(sha256.digest()).equals(jelly))
                throw new Exception("mismatched internal tomcat hash, redownload installer");

            System.out.println("(SUCCESS)");
        }
        catch (Exception ex)
        {
            System.out.println('(' + ex.getLocalizedMessage().toUpperCase() + ')');
        }
        
        System.out.println();
        System.out.println("-- SIGNALS");
        
        System.out.println("OS-Flavor: " + flavor);
        System.out.println("Tomcat Version: 8.5.63");

        System.out.println();

        return false;
    }

    private static boolean installTomcat(String[] args, Scanner in)
    {
        if(args == null)
        {
            System.out.println("Install Lua Tela onto the current machine.");
            return false;
        }
        
        System.out.println();
        
        Properties props = readProps(args, "port", "dir", "datauser", "datapass", "datahost", "database", "dataext", "dataurl");

        System.out.println("Installing Lua Tela!");
        
        Flavor flavor = Flavor.getFlavor();
        File dir = new File(props.getProperty("dir", System.getProperty("user.dir")));
        System.out.println("Target Directory: " + dir.getAbsolutePath());
        
        InputStream is = Instant.class.getResourceAsStream("/instant/apache-tomcat-8.5.63.zip");
        
        try
        {
            Path path = dir.toPath();
            ZipEntry entry;
            ZipInputStream zis = new ZipInputStream(is);

            while((entry = zis.getNextEntry()) != null)
            {
                Path p = path.resolve(entry.getName()).toAbsolutePath();
                File f = p.toFile();
                
                if(entry.isDirectory())
                {
                    if(!f.exists())
                        f.mkdirs();
                    continue;
                }

                if(f.exists())
                    f.delete();

                Files.copy(zis, p);
            }
            
            zis.close();
            
            System.out.println("EXTRACTED TOMCAT");
            
            int port = Integer.parseInt(props.getProperty("port", "8000"));
            Utils.rewrite(new File(dir, "conf/server.xml"), (String line) -> {
                if(line.contains("[=[PORT]=]"))
                    line = line.replace("[=[PORT]=]", Integer.toString(port));
                
                return line;
            });
            String user = props.getProperty("datauser", "");
            String pass = props.getProperty("datapass", "");
            String host = props.getProperty("datahost", "");
            String base = props.getProperty("database", "");
            int dport = Integer.parseInt(props.getProperty("dataport", "3306"));
            String ext = props.getProperty("dataext", "");
            String url = props.getProperty("dataurl", "");
            Utils.rewrite(new File(dir, "webapps/ROOT/WEB-INF/web.xml"), (String line) -> {
                if(line.contains("[=[USER]=]"))
                    line = line.replace("[=[USER]=]", user);
                else if(line.contains("[=[PASS]=]"))
                    line = line.replace("[=[PASS]=]", pass);
                else if(line.contains("[=[HOST]=]"))
                    line = line.replace("[=[HOST]=]", host);
                else if(line.contains("[=[BASE]=]"))
                    line = line.replace("[=[BASE]=]", base);
                else if(line.contains("[=[PORT]=]"))
                    line = line.replace("[=[PORT]=]", Integer.toString(dport));
                else if(line.contains("[=[EXT]=]"))
                    line = line.replace("[=[EXT]=]", ext.replace("&", "&amp;"));
                else if(line.contains("[=[URL]=]"))
                    line = line.replace("[=[URL]=]", url.replace("&", "&amp;"));
                
                return line;
            });
        }
        catch (IOException ex)
        {
            Logger.getLogger(Instant.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }
    
    private static boolean runTomcat(String[] args, Scanner in)
    {
        if(args == null)
        {
            System.out.println("Run Lua Tela on the current machine.");
            return false;
        }
        
        System.out.println();

        Properties props = readProps(args, "dir");
        
        Flavor flavor = Flavor.getFlavor();
        File dir = new File(props.getProperty("dir", System.getProperty("user.dir")));
        System.out.println("Target Directory: " + dir.getAbsolutePath());
        
        try
        {
            File startFile = new File(dir, "bin" + File.separator + "catalina" + flavor.getExecExtension());
            startFile.setExecutable(true);
            try
            {
                ProcessBuilder pb = new ProcessBuilder(startFile.getAbsolutePath(), "run");
                pb.directory(dir);
                running.set(startFile.getAbsolutePath());
                Process p = pb.start();

                Utils.printStream(p.getErrorStream(), System.out);
                Utils.printStream(p.getInputStream(), System.err);
                p.waitFor();
                running.set(null);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Instant.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        catch(IOException ex)
        {
            Logger.getLogger(Instant.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    private static final Map<String, BiPredicate<String[], Scanner>> cmds = new HashMap<>();
    
    static
    {
        cmds.put("exit", Instant::exit);
        cmds.put("help", Instant::help);
        cmds.put("sanity-check", Instant::sanityCheck);
        cmds.put("install-tomcat", Instant::installTomcat);
        cmds.put("run-tomcat", Instant::runTomcat);
    }
    
    private static boolean readCommand(Scanner in)
    {
        String line = in.nextLine().trim();
        List<String> strs = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        char c;
        for(int i = 0; i < line.length(); i++)
        {
            c = line.charAt(i);

            if(Character.isWhitespace(c))
            {
                if(sb.length() != 0)
                {
                    strs.add(sb.toString());
                    sb.setLength(0);
                }
            }
            else if(c == '"' && sb.length() == 0)
            {
                int j;
                for(j = i + 1; j < line.length(); j++)
                {
                    c = line.charAt(j);
                    
                    if(c == '"')
                        break;
                    else
                        sb.append(c);
                }
                i = j;
                strs.add(sb.toString());
                sb.setLength(0);
            }
            else
                sb.append(c);
        }
        if(sb.length() > 0)
            strs.add(sb.toString());

        if(strs.isEmpty())
            return false;
        
        String cmd = strs.remove(0);
        String[] args = strs.toArray(new String[0]);
        
        BiPredicate<String[], Scanner> callback = cmds.get(cmd);
        
        if(callback == null)
        {
            System.out.println("Unknown command: \"" + cmd + "\"! Try asking for 'help'.\n");
            return false;
        }
        else
        {
            try
            {
                return callback.test(args, in);
            }
            catch(PropertyException ex)
            {
                System.out.println(ex.getLocalizedMessage());
                return false;
            }
        }
    }
    
    private static Properties readProps(String[] args, String... strs)
    {
        Properties props = new Properties();
        Set<String> valid = new HashSet<>(Arrays.asList(strs));
        
        String str;
        for(int i = 0; i < args.length; i++)
        {
            str = args[i];
            
            if(str.startsWith("--"))
            {
                if(i < args.length - 1)
                {
                    str = str.substring(2);

                    if(valid.contains(str))
                        props.put(str, args[i + 1]);
                    else
                        throw new PropertyException("Unknown property: \"--" + str + "\"");
                }
                else
                    throw new PropertyException("Missing value for: \"" + str + "\"");
                
                i++;
            }
            else if(str.startsWith("-"))
            {
                str = str.substring(1);

                if(valid.contains(str))
                    props.put(str, "");
                else
                    throw new PropertyException("Unknown property: \"-" + str + "\"");
            }
        }
        
        return props;
    }
    
    public static void main(String[] args) throws Exception
    {
        // install-tomcat --dir run --datauser root --datapass hkrocks1 --datahost localhost --database testdb --dataext useSSL=false&allowPublicKeyRetrieval=true
        
        System.out.println("--[===========================[ LUA-TELA ]===========================]");
        System.out.println("Lua-Tela Web Framework (the power within)");
        System.out.println("/*");
        System.out.println(" * installer and diagnostic tool for Lua-Tela web framework.");
        System.out.println(" */");
        System.out.println();
        SimpleDateFormat df = new SimpleDateFormat("d MMM yyyy hh:mm aaa");
        df.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        System.out.println("Date: " + df.format(new Date()));
        
        Scanner in = new Scanner(System.in);
        
        Runtime.getRuntime().addShutdownHook(new Instant());
        
        if(args == null || args.length == 0)
        {
            System.out.println("Type help or a command...");
            System.out.println();

            while(true)
            {
                System.out.print("cmd: ");            
                if(readCommand(in))
                    break;
            }
        }
        else
        {
            String cmd = args[0];
            BiPredicate<String[], Scanner> callback = cmds.get(cmd);

            if(callback == null)
            {
                System.out.println("Unknown command: \"" + cmd + "\"! Try asking for 'help'.\n");
            }
            else
            {
                try
                {
                    String[] tmp = new String[args.length - 1];
                    System.arraycopy(args, 1, tmp, 0, tmp.length);
                    callback.test(tmp, in);
                }
                catch(PropertyException ex)
                {
                    System.out.println(ex.getLocalizedMessage());
                }
            }
        }
        
        in.close();
    }
    
    static class PropertyException extends RuntimeException
    {
        PropertyException(String msg)
        {
            super(msg);
        }
    }
}
