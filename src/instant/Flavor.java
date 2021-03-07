package instant;

abstract class Flavor
{
    private static final Flavor flavor;
    
    static
    {
        String os = System.getProperty("os.name");
        if(os.contains("Windows"))
        {
            flavor = new WindowsFlavor();
        }
        else if(os.contains("Mac"))
        {
            flavor = new MacFlavor();
        }
        else if(os.contains("Linux"))
        {
            flavor = new LinuxFlavor();
        }
        else
            throw new Error("Unknown OS?");
    }
    
    abstract String getFlavorName();
    
    abstract String getExecExtension();
    
    @Override
    public String toString()
    {
        return getFlavorName();
    }
    
    static Flavor getFlavor()
    {
        return flavor;
    }
}
