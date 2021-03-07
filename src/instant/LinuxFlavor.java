package instant;

class LinuxFlavor extends Flavor
{
    LinuxFlavor()
    {
        // Linux Mint
        // Debian
        // Fedora
        // CentOS/Redhat
        // openSUSE
        // Mageia
        // Arch Linux
        // Slackware Linux
        // Puppy Linux
        // Ubuntu
    }

    @Override
    String getFlavorName()
    {
        return "Linux";
    }

    @Override
    String getExecExtension()
    {
        return ".sh";
    }
}
