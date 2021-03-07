package instant;

class WindowsFlavor extends Flavor
{
    WindowsFlavor()
    {
    }

    @Override
    String getFlavorName()
    {
        return "Windows";
    }

    @Override
    String getExecExtension()
    {
        return ".bat";
    }
}
